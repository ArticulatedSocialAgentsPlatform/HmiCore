package nl.utwente.hmi.middleware.HTTPRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.JsonArray;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPRequest implements Middleware, Callback {
	private static Logger logger = LoggerFactory.getLogger(HTTPRequest.class.getName());

	private String url;
	private Map<String, String> globalHeaders;
	private OkHttpClient client;
	
	private ObjectMapper om;

	private Set<MiddlewareListener> listeners;

	private String defaultMethod;

	public HTTPRequest(String url) {
		this(url, new HashMap<String,String>());
	}

	public HTTPRequest(String url, Map<String, String> headers) {
		this(url, headers, "");
	}
	
	public HTTPRequest(String url, Map<String, String> headers, String defaultMethod) {
		this.url = url;
		this.globalHeaders = headers;
		this.defaultMethod = defaultMethod;
		listeners = Collections.synchronizedSet(new HashSet<MiddlewareListener>());
		
		client = new OkHttpClient();
		
		om = new ObjectMapper();
	}
	
	/**
	 * POST data or do a GET request depending on how your specify your JSON.
	 * The JSON node is constructed as follows:
	 * - optional top-level field "method" with value "GET" or "POST"
	 * - optional top-level field "headers" with key-value pairs of headers to be added to this request
	 * - optional top-level field "data" with key-value pairs of data to be sent
	 * 
	 * Example: {"headers":{"auth_key":"xxxxx"},"method":"POST","data":{"key":"value"}}
	 * 
	 * Note: to keep this HTTPRequest somewhat transparent as a middleware, none of these parameters is mandatory... if you want, you can just ignore all of them, and dump your JSON data as-is
	 * If you do, sendData will attempt to make something of it following these rules:
	 * - if there is no data field, but there are other top-level fields, these will be treated as data instead: e.g. {"method":"GET","key1":"val1","key2":"val2"} will just send a GET request with key1=val1&key2=val2 parameters
	 * - if you have specified a method field, it will use that method. If not, it will use the optional default method specified through the loader. Otherwise, it will use GET if there is no data and POST if there is data
	 * - if there are no fields whatsoever (i.e. the JSON is "{}") then we will do a GET without parameters
	 * 
	 */
	@Override
	public void sendData(JsonNode jn) {
		logger.debug("Preparing HTTP request message: {}", jn.toString());
		
		//start building the request
		Request.Builder rb = new Request.Builder();
		
		//add headers, these may be configured from the init params, or be set in the sending data
		logger.trace("Adding headers to request {}", url);
		for(Entry<String,String> header : globalHeaders.entrySet()) {
			String key = header.getKey();
			String val = header.getValue();
			logger.trace("Adding global header {}: {}", key, val);
			rb.addHeader(header.getKey(), header.getValue());
		}
		
		if(jn != null && jn.isObject() && jn.has("headers")) {
			//find any optional headers specified in this data structure
			JsonNode inlineHeaders = jn.findPath("headers");
			if(inlineHeaders.isObject()) {
				Iterator<Entry<String,JsonNode>> it = inlineHeaders.fields();
				while(it.hasNext()) {
					Entry<String,JsonNode> header = it.next();
					String key = header.getKey();
					JsonNode val = header.getValue();
					if(val.isTextual()) {
						logger.trace("Adding inline header {}: {}", key, val.asText());
						rb.addHeader(key, val.asText());
					} else {
						logger.trace("Adding inline header {}: {}", key, val.toString());
						rb.addHeader(key,  val.toString());
					}
				}
			}
		}
				
		//get any attached params
		JsonNode data = getDataFields(jn);
		Map<String,JsonNode> params = makeParams(data);
		String method = getMethod(jn);
		
		if(method.equals("POST")) {
			rb.url(url);
			
			//depending on optional content-type header we may wish to send JSON or a collection of key-value pairs (default)
			if(params.size() == 0) {
				logger.warn("Method is POST but there is no data to send {} {}", url, jn.toString());
			} else if("application/json".equalsIgnoreCase(rb.getHeaders$okhttp().get("Content-Type"))) {
				logger.trace("Creating POST JSON for request to {}: {}", url, data.toString());
				rb.post(RequestBody.create(data.toString(), MediaType.parse("application/json; charset=utf-8")));
			} else {
				logger.trace("Creating POST form body for request to {}", url);
				FormBody.Builder dataBuilder = new FormBody.Builder();

				for(Entry<String,JsonNode> param : params.entrySet()) {
					String key = param.getKey();
					JsonNode val = param.getValue();
					if(val.isTextual()) {
						logger.trace("Adding data {}: {}", key, val.asText());
						dataBuilder.add(key, val.asText());
					} else {
						logger.trace("Adding data {}: {}", key, val.toString());
						dataBuilder.add(key, val.toString());
					}
				}
				FormBody body = dataBuilder.build();
				rb.post(body);
			}

			//finally, set the url
		} else {
			String urlAppend = "";
			if(params.size() > 0) {
				logger.trace("Creating GET URL query for request to {}", url);
				List<String> urlParams = new ArrayList<String>();
				for(Entry<String,JsonNode> param : params.entrySet()) {
					String key = param.getKey();
					JsonNode val = param.getValue();
					if(val.isTextual()) {
						String query = urlEncode(key) + "=" + urlEncode(val.asText());
						logger.trace("Adding query to url {}: {}", key, query);
						urlParams.add(query);
					} else {
						String query = urlEncode(key) + "=" + urlEncode(val.toString());
						logger.trace("Adding query to url {}: {}", key, query);
						urlParams.add(query);
					}
				}
				urlAppend = "?";
				urlAppend += String.join("&", urlParams);
			}
			
			//finally, set the url
			rb.url(url+urlAppend);
		}
		
		//finalize the request and send it to server
        Request req = rb.build();
		Call call = client.newCall(req);
		
		logger.debug("Sending HTTP request to {}: {}", url, req.toString());
		call.enqueue(this);
	}
	
	private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
	
	/**
	 * Builds set of key value pairs for the parameters to include in POST or GET
	 * @param jn
	 * @return a map of key, value parameters
	 */
	private Map<String,JsonNode> makeParams(JsonNode jn) {
		Map<String,JsonNode> params = new HashMap<String,JsonNode>();
		
		Iterator<Entry<String,JsonNode>> it = jn.fields();
		while(it.hasNext()) {
			Entry<String,JsonNode> d = it.next();
			String key = d.getKey();
			JsonNode val = d.getValue();
			params.put(key, val);
		}
		
		return params;
	}
	
	/**
	 * Extracts all data from a JsonNode
	 * Uses the "data" field, if available, or defaults to using all top-level fields (excluding method & headers)
	 * @param jn
	 * @return a JsonNode with all data
	 */
	private JsonNode getDataFields(JsonNode jn) {
		if(jn.has("data")) {
			return jn.findPath("data");
		}  
		
		ObjectNode data = om.createObjectNode();
		
		//TODO: this selecting and filtering of data fields could probably be made more efficient.. but it works :)
		Iterator<Entry<String,JsonNode>> it = jn.fields();
		while(it.hasNext()) {
			Entry<String,JsonNode> d = it.next();
			String key = d.getKey();
			JsonNode val = d.getValue();
			
			//filter out all "special" fields
			if(!key.equals("method") && !key.equals("headers")) {
				data.set(key, val);
			}
		}
		
		return data;
	}
	
	/**
	 * Determines the method (GET or POST) for this JSON message:
	 * - if it's set explicitly in the "method" field of the message, use that
	 * - otherwise, if it's specified as a loader param, use that
	 * - otherwise, use POST if there is data, or GET if the message is empty
	 * @param jn
	 * @return POST or GET
	 */
	private String getMethod(JsonNode jn) {
		if(jn.has("method") && ("POST".equals(jn.findPath("method").asText()) || "GET".equals(jn.findPath("method").asText()))) {
			return jn.findPath("method").asText();
		} else if(!defaultMethod.equals("")) {
			return defaultMethod;
		} else {
			if(jn.size() == 0 || (jn.size() == 1 && jn.has("headers"))) {
				return "GET";
			} else {
				return "POST";
			}
		}
		
		
	}
	
	@Override
	public void addListener(MiddlewareListener ml) {
		this.listeners.add(ml);
	}

	@Override
	public void sendDataRaw(String data) {
		//nothing for now..
	}

	@Override
	public void onFailure(Call arg0, IOException arg1) {
		logger.error("HTTPRequest failed {} {} {}", new Object[] {url, arg0.toString(), arg1.getStackTrace()});
	}

	@Override
	public void onResponse(Call arg0, Response arg1) throws IOException {
		String response = arg1.body().string();
		logger.debug("Code: {} - Got HTTP response from {}: {}", new Object[] {arg1.code(), url, response});
		
		JsonNode responseJN = om.createObjectNode();
		
		//try to parse response as JSON first, or just treat it as regular string and encase in an otherwise empty JSON object {"response":"....."}
		try {
			JsonNode jn = om.readTree(response);
			if(jn != null) {
				logger.debug("Parsed response from {} as JSON: {}", url, jn.toString());
				((ObjectNode)responseJN).set("response", jn);
			} else {
				//TODO: might want to pass this on to listeners as well..?
				logger.warn("Got empty response from {}", url);
			}
		} catch (JsonProcessingException e) {
			logger.info("Unable to parse response from {} as JSON string, just treating it as text instead \"{}\": {}", new Object[] {url, response, e.getMessage()});
			//e.printStackTrace();
			
			//just encase it in JSON manually...
			((ObjectNode)responseJN).put("response", response);
		}
	
		//TODO: right now, we ignore the HTTP response code but we may need to pass it on in the future: {... ,"response-code":"200", ...}
		//TODO: check for response code 500 internal server error and 404 not found etc.
		
		//pass it on to all registered listeners
		for(MiddlewareListener ml : listeners) {
			ml.receiveData(responseJN);
		}
	}
	
	public static void main(String[] args) {
		//a very basic test to see if we can first authenticate and then post a request
		
		ObjectMapper omp = new ObjectMapper();
		Middleware auth = new HTTPRequest("https://servletstest.rrdweb.nl/r2d2/v5.1.1/auth/login", new HashMap<String,String>());
		auth.addListener(new MiddlewareListener() {

			@Override
			public void receiveData(JsonNode jn) {
				System.out.println("Auth response: "+jn.toString());
				
				String authToken = jn.findPath("response").asText();
				
				Middleware set = new HTTPRequest("https://servletstest.rrdweb.nl/r2d2project/couch/v5.1.1/variables", new HashMap<String,String>());
				set.addListener(new MiddlewareListener() {

					@Override
					public void receiveData(JsonNode jn) {
						System.out.println("Set response: "+jn.toString());
						
						Middleware get = new HTTPRequest("https://servletstest.rrdweb.nl/r2d2project/couch/v5.1.1/variables", new HashMap<String,String>());
						get.addListener(new MiddlewareListener() {

							@Override
							public void receiveData(JsonNode jn) {
								System.out.println("Get response: "+jn.toString());
							}
							
						});
						
						try {
							get.sendData(omp.readTree("{\"method\":\"GET\",\"headers\":{\"X-Auth-Token\":\""+authToken+"\"},\"names\":\"var1 var2\"}"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
				
				try {
					set.sendData(omp.readTree("{\"headers\":{\"Content-Type\":\"application/json\",\"X-Auth-Token\":\""+authToken+"\"},\"var1\":\"valtest1\",\"var2\":\"valt2\"}"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		});

		
		try {
			//create a login for email and password at: https://servletstest.rrdweb.nl/r2d2/swagger-ui.html#/auth-controller/signupUsingPOST
			auth.sendData(omp.readTree("{\"method\":\"POST\",\"data\":{\"email\":\"placeholder\",\"password\":\"placeholder\"}}"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

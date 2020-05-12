package nl.utwente.hmi.middleware.HTTPRequest;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;
import nl.utwente.hmi.middleware.stomp.STOMPMiddlewareLoader;

/**
 * A wrapper for sending GET and POST requests through our Middleware structure
 * Loader requires a url through the {@value URL_PARAM} parameter
 * Also takes any amount of optional headers that must be prefixed with {@value HEADER_PARAM}
 * @author Daniel
 *
 */
public class HTTPRequestLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(HTTPRequestLoader.class.getName());

	public static final String URL_PARAM = "HTTPRequestURL";
	public static final String HEADER_PARAM = "HTTPRequestHeader_";
	public static final String METHOD_PARAM = "HTTPRequestMethod";
	
	@Override
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		String url = "";
		String defaultMethod = "";
		
		Map<String,String> headers = new HashMap<String,String>();
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.debug("propkey: {}",(String)entry.getKey());
			logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals(URL_PARAM)){
				url = (String)entry.getValue();
			} else if(((String)entry.getKey()).startsWith(HEADER_PARAM)) {
				headers.put(((String)entry.getKey()).replaceFirst(HEADER_PARAM, ""), (String)entry.getValue());
			} else if(((String)entry.getKey()).equals(METHOD_PARAM)) {
				defaultMethod = (String)entry.getValue();
			}
		}
		
		if("".equals(url)) {
			logger.error("HTTPRequest middleware requires an \"{}\" property",URL_PARAM);
		} else {
			m = new HTTPRequest(url, headers, defaultMethod);
		}
		
		return m;
	}

	
	
}

package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.loader.GenericMiddlewareLoader;
import nl.utwente.hmi.middleware.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The MiddlewareWrapper is designed to have a simple implementation for creating a middleware with a listener
 * (MiddlewareListener) for receiving data and a worker (Worker) for processing data. If more flexibility is required,
 * the author can utilize the Worker and MiddlewareListener interfaces. Additionally, this class contains methods
 * for sending data as well, next to methods for retrieving data.
 * @author WaterschootJB
 */

public abstract class MiddlewareWrapper implements Worker, MiddlewareListener {

    protected ObjectMapper mapper;
    protected boolean running = true;
    protected static Logger logger = LoggerFactory.getLogger(MiddlewareWrapper.class.getName());
    protected BlockingQueue<JsonNode> queue;
    protected Middleware middleware;

    /**
     * Constructor for instantiating a thread with an abstracted middleware wrapper using a
     * LinkedBlockingQueue. This wrapper has two separate threads for retrieving data (itself) from
     * middleware and processing the received data (a worker thread).
     *
     * @param middleware, the middleware being used for creating the wrapper
     */
    public MiddlewareWrapper(Middleware middleware) {
        this.middleware = middleware;
        initializeWrapper();
    }

    /**
     * Constructor for the middleware wrapper that requires properties, convenient if you don't want to
     * use files for configuration.
     *
     * @param ps, properties that require the following properties
     *            "middleware", e.g. middleware: nl.utwente.hmi.middleware.activemq.ActiveMQMiddleWare
     *            "address of middleware", e.g. amqBrokerURI: tcp://localhost:61616
     *            "other required properties of the specific middleware", e.g. iTopic:in and oTopic:out
     */
    public MiddlewareWrapper(Properties ps) {
        GenericMiddlewareLoader.setGlobalProperties(ps);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(ps.getProperty("loader"), ps);
        this.middleware = gml.load();
        initializeWrapper();
    }

    /**
     * Constructor for the middleware wrapper that requires two property file names. The wrapper has two
     * separate threads for retrieving data (itself) and processing data (a worker thread).
     *
     * @param generalProps,  property file containing the connecting link for the middleware
     * @param specificProps, property file containing the middleware instantiation and its required properties
     */
    public MiddlewareWrapper(String generalProps, String specificProps) {
        Properties ps = new Properties();
        InputStream mwProps = MiddlewareWrapper.class.getClassLoader().getResourceAsStream(specificProps);
        try {
            ps.load(mwProps);
        } catch (IOException e) {
            logger.warn("Could not load flipper middleware props file {}", mwProps);
            e.printStackTrace();
        }
        GenericMiddlewareLoader.setGlobalPropertiesFile(generalProps);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(ps.getProperty("loader"), ps);
        this.middleware = gml.load();
        initializeWrapper();
    }

    /**
     * Method for creating a MiddlewareWrapper with a configuration file
     * @param jn, a JsonNode that represents properties of the middleware in the format of
     *        {
     *          "loader" : "nl.utwente.hmi.*",
     *          "properties" : {}
     *        }
     * @return, the MiddlewareWrapper
     **/
    public MiddlewareWrapper(JsonNode jsonNode){
        Properties ps = loadMWProps(jsonNode);
        GenericMiddlewareLoader.setGlobalProperties(ps);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(ps.getProperty("loader"), ps);
        this.middleware = gml.load();
        initializeWrapper();
    }

    private Properties loadMWProps(JsonNode jn){
        if(jn.has("loader")) {
            Properties mwProps = new Properties();
            String mw = jn.get("loader").textValue();
            mwProps.put("loader",mw);
            JsonNode props = jn.get("properties");
            Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while(it.hasNext()) {
                Map.Entry<String, JsonNode> prop = it.next();
                mwProps.setProperty(prop.getKey(),prop.getValue().textValue());
            }
            return mwProps;
        }
        else{
            return null;
        }
    }

    private void initializeWrapper(){
        this.mapper = new ObjectMapper();
        middleware.addListener(this);
        logger.debug("Listener created");
        this.queue = new LinkedBlockingQueue<>();
        new Thread(this).start();
    }

     /**
     * Does the required processing on the data
     * Each worker is responsible for implementing this method as required. Use this method
     * for processing data
     *
     * @param jn the incoming data in JsonNode format
     */
    public abstract void processData(JsonNode jn);

    @Override
    public void addDataToQueue(JsonNode jn) {
        queue.add(jn);
    }

    @Override
    public void run() {
        logger.debug("Worker started.");
        while (running) {
            try {
                JsonNode jn = queue.take();
                processData(jn);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

     /**
     * Returns if the middleware is created and connected     *
     * @return true if connected, false if not
     */
    public boolean isConnected() {
        return middleware != null;
    }


    public void receiveData(JsonNode jn) {
        logger.debug("Received data: {}", jn.toString());
        queue.clear();
        queue.add(jn);
    }

    /**
     * Sends data in UTF-8 format over the middleware, which is of form { "content" : $data}     *
     * @param data, a String representation of the data to send
     */
    public void sendData(JsonNode data) {
        middleware.sendData(data);
    }


}


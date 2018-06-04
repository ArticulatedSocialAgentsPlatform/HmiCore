package nl.utwente.hmi.middleware.ros;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;
import java.util.Properties;

/** Loading a ROS Middleware **/

public class ROSMiddlewareLoader implements MiddlewareLoader {
    private static Logger logger = LoggerFactory.getLogger(ROSMiddlewareLoader.class.getName());

    public Middleware loadMiddleware(Properties ps) {
        Middleware m = null;
        String websocketURI = "ws://localhost:9090";
        String publisher = "";
        String subscriber = "";

        for (Entry<Object, Object> entry : ps.entrySet()) {
            try {
                if (((String) entry.getKey()).equals("websocketURI")) {
                    websocketURI = (String)entry.getValue();
                }
                if(((String)entry.getKey()).equals("publisher")){
                    publisher = (String)entry.getValue();
                }
                if(((String)entry.getKey()).equals("subscriber")){
                    subscriber = (String)entry.getValue();
                }
            } catch (NumberFormatException nfe) {
                logger.error("Error loading the port specifications. Please ensure these are numeric.");
            }
        }

        if(websocketURI.equals("") || publisher.equals("") || subscriber.equals("")){
            logger.error("Could not load the ROSMiddleware, need at least properties: the WebSocket URI with port (default : ws://localhost:9090), publisher and subscriber. The port value is '9090' by default.");
        } else {
            m = new ROSMiddleware(websocketURI, publisher, subscriber);
        }

        return m;
    }

}

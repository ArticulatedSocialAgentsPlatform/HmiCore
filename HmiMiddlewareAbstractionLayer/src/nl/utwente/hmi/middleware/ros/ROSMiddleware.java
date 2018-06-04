package nl.utwente.hmi.middleware.ros;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.std_msgs.PrimitiveMsg;
import ros.tools.MessageUnpacker;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for the ROS middleware.
 * @author pchevalier
 *
 */

public class ROSMiddleware  implements Middleware {
    private static Logger logger;

    static {
        logger = LoggerFactory.getLogger(ROSMiddleware.class.getName());
    }

    String websocketURI;
    String publisher;
    String subscriber;
    RosBridge bridge;
    Publisher pub;

    private Set<MiddlewareListener> listeners;

    public ROSMiddleware(String websocketURI, String publisher, String subscriber) {
        this.websocketURI = websocketURI;
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.bridge = new RosBridge();
        this.listeners = Collections.synchronizedSet(new HashSet<MiddlewareListener>());

        setupMiddleware();


    }

    public void sendData(JsonNode jn){
        if(jn != null){
            pub.publish(new PrimitiveMsg<>(jn.toString()));
            logger.debug("Sending data: {}", jn.toString());
        }
    }

    public void addListener(MiddlewareListener ml){
        this.listeners.add(ml);
    }

    public void convertMsg(String jsonString) {
        //parse json string and create JsonObject
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jn = mapper.readTree(jsonString);
            logger.debug("Transformed to json object: {}", jn.toString());

            if(jn != null){
                for(MiddlewareListener ml : listeners){
                    ml.receiveData(jn);
                }
            }
        } catch (JsonProcessingException e) {
            logger.warn("Error while parsing JSON string \"{}\": {}", jsonString, e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setupMiddleware(){
        this.bridge.connect(this.websocketURI, true);

        this.bridge.subscribe(SubscriptionRequestMsg.generate(this.subscriber)
                        .setType("std_msgs/String")
                        .setThrottleRate(1)
                        .setQueueLength(1),
                new RosListenDelegate() {

                    public void receive(JsonNode data, String stringRep) {
                        MessageUnpacker<PrimitiveMsg<String>> unpacker = new MessageUnpacker<PrimitiveMsg<String>>(PrimitiveMsg.class);
                        PrimitiveMsg<String> msg = unpacker.unpackRosMessage(data);

                        convertMsg(msg.data.toString());

                        /* System.out.println("jason msg");
                        System.out.println(data);
                        System.out.println(msg.data);

                        ((ObjectNode)data).replace("msg", data.get("msg").get("data"));
                        System.out.println(data);

                        for(MiddlewareListener ml : listeners){
                            ml.receiveData((ObjectNode)msg.data);
                        } */

                        //System.out.println(data);
                    }
                }
        );
        this.pub = new Publisher(this.publisher, "std_msgs/String", this.bridge);

    }
    
}



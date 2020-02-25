/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
    
    private static final String KEEPALIVE = "{\"keepalive\":true}";

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
        this.bridge = new NoTimeoutRosBridge();
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
                        
                        logger.debug("Got ROS msg from topic {}: {}", subscriber, msg.data.toString());
                        
                        if(KEEPALIVE.equals(msg.data.toString())){
                        	logger.debug("Got keepalive message on topic {} - Not forwarding to listeners.", subscriber);
                        } else {
                        	convertMsg(msg.data.toString());
                        }

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
        
        //start sending the keepalive messages
        Thread keepAlive = new Thread(new KeepAlive());
        keepAlive.start();
    }
    
    /**
     * A small class to send periodic keepalive messages. This should prevent the ROS server from killing our publisher websocket *GRRR*
     * TODO: in future, we could try to detect when the websocket connection has closed and do a reconnect... but this might be difficult because the rosbridge implementation seems to catch all exceptions and hide them... it might require quite a deep hack
     * @author davisond
     *
     */
    class KeepAlive implements Runnable {
    	
		@Override
		public void run() {
			while(true) {
				if(bridge.hasConnected()) {
					//send keepalive messages to prevent timeout
					logger.debug("Sending keepalive message on topic {}", publisher);
		            pub.publish(new PrimitiveMsg<>(KEEPALIVE));
		            
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
				        Thread.currentThread().interrupt();
					}
				}
			}
		}
    	
    }

	@Override
	public void sendDataRaw(String data) {
        if (data != null){
            pub.publish(new PrimitiveMsg<>(data));
            logger.debug("Sending data: {}", data);
        }
	}
    
}



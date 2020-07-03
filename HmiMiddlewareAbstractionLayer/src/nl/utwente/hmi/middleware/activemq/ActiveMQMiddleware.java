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
package nl.utwente.hmi.middleware.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for the ActiveMQ middleware.
 * Parameters:
 * 
 * @author Dennis Reidsma
 *
 */
public class ActiveMQMiddleware implements Middleware,  MessageListener {
	private static Logger logger = LoggerFactory.getLogger(ActiveMQMiddleware.class.getName());

	String amqBrokerURI;
	String iTopic;
	String oTopic;
    private Session session;
    private MessageProducer oTopicMessageProducer;

	private Set<MiddlewareListener> listeners;

	//private StompHandler stompHandler;

	/**
	 * Create this Middleware using the specified connection info
	 * @param amqBrokerURI the broker URI for the activemq server see http://activemq.apache.org/tcp-transport-reference.html
	 * @param iTopic the topic name on which we are listening for input  (path.to.topic)
	 * @param oTopic the topic name on which we are writing output   (path.to.topic)
	 */
	public ActiveMQMiddleware(String amqBrokerURI, String iTopic, String oTopic){
		this.amqBrokerURI = amqBrokerURI;
		this.iTopic = iTopic;
		this.oTopic = oTopic;
		
		this.listeners = Collections.synchronizedSet(new HashSet<MiddlewareListener>());
		
        try
        {
            
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(amqBrokerURI);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /* Init Receiver */
            Topic t = session.createTopic(iTopic);
            MessageConsumer consumer = session.createConsumer(t);
            consumer.setMessageListener(this);

            t = session.createTopic(oTopic);
            oTopicMessageProducer = session.createProducer(t);
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
        
		//this.stompHandler = new StompHandler(apolloIP, apolloPort);
		
		//stompHandler.registerCallback(iTopic, this);
	
	}
	
	@Override
	public void sendData(JsonNode jn) {
	    if(jn == null){
	        return;
        }
        logger.debug("Sending data: {}", jn.toString());
        try {
            if(jn != null){
                TextMessage message = session.createTextMessage();
                message.setText(jn.toString());
                oTopicMessageProducer.send(message);
            }
        }
        catch (JMSException e)
        {
            logger.debug("Error sending data: {}", e.toString());
        }
    }
	
	@Override
	public void addListener(MiddlewareListener ml) {
		this.listeners.add(ml);
	}

	@Override
	public void onMessage(Message msg) {
		String stringMessage = getStringFromMessage(msg);
		
        //parse json string and create JsonObject
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            JsonNode jn = mapper.readTree(stringMessage);
            logger.debug("Transformed to json object: {}", jn.toString());
            
            if(jn != null){
                for(MiddlewareListener ml : listeners){
                    ml.receiveData(jn);
                }
            }
        } catch (JsonProcessingException e) {
            logger.warn("Error while parsing JSON string \"{}\": {}", stringMessage, e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
            
	}
	
	/**
	 * Attempts to extract the message as a string. Currently supports TextMessage and BytesMessage
	 * For a TextMessage this is obviously not an issue, but a BytesMessage will be converted to new String(byteData) (with potential errors down the line if it turns out to be non-String data)
	 * @param msg of type TextMessage or BytesMessage
	 * @return the data of the message represented as a string
	 */
	private String getStringFromMessage(Message msg) {
		String stringMessage = "";
		try {
            if (msg instanceof TextMessage)
            {
                TextMessage textMessage = (TextMessage) msg;
                stringMessage = textMessage.getText();
                logger.debug("Got text message on topic {}: {}", iTopic, stringMessage);	
            }
            else if(msg instanceof BytesMessage){
            	BytesMessage byteMessage = (BytesMessage) msg;
				byte[] byteData = new byte[(int) byteMessage.getBodyLength()];
				byteMessage.readBytes(byteData);
				//TODO: is there a way to figure out whether the binary data is actually a string..?
				stringMessage =  new String(byteData);
				logger.debug("Got bytes message on topic {}: {}", iTopic, stringMessage);
            } else
            {
                logger.error("Received message of unsupported type {} on ActiveMQConnection: not a text message!", msg.getClass().getName());
            }
        }
        catch (JMSException e)
        {
            logger.debug("Error receiving data: {}", e.toString());
        }
		
		return stringMessage;
	}

	@Override
	public void sendDataRaw(String data) {
        try {
            if(data != null){
                TextMessage message = session.createTextMessage();
                message.setText(data);
                oTopicMessageProducer.send(message);
            }
        }
        catch (JMSException e)
        {
            logger.debug("Error sending raw data: {}", e.toString());
        }
	}
	
}

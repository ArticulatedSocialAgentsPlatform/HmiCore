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
        try {
            if (msg instanceof TextMessage)
            {
                TextMessage textMessage = (TextMessage) msg;
                logger.debug("Got message on topic {}: {}", iTopic, textMessage.getText());	
                String jsonString = textMessage.getText();
                
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
            else
            {
                logger.error("Received message of type {} on ActiveMQConnection: not a text message!", msg.getClass().getName());
            }
        }
        catch (JMSException e)
        {
            logger.debug("Error receiving data: {}", e.toString());
        }
            
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

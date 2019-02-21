package nl.utwente.hmi.middleware.activemq;

import java.util.Map.Entry;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(ActiveMQMiddlewareLoader.class.getName());

	/**
	 * This loads the ActiveMQMiddleware instance
	 */
	@Override
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		String amqBrokerURI = "";
		String iTopic = "";
		String oTopic = "";
		
		for(Entry<Object, Object> entry : ps.entrySet()){
            logger.debug("propkey: {}",(String)entry.getKey());
            logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals("amqBrokerURI")){
				amqBrokerURI = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("iTopic")){
				iTopic = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("oTopic")){
				oTopic = (String)entry.getValue();
			}
		}
		
		if(amqBrokerURI.equals("") || iTopic.equals("") || oTopic.equals("")){
			logger.error("Could not load the ActiveMQMiddleware, need at least properties: amqBrokerURI, iTopic, oTopic. Can be set in the global middleware props or in the load call.");
		} else {
			m = new ActiveMQMiddleware(amqBrokerURI, iTopic, oTopic);
		}
		
		return m;
	}

}

package nl.utwente.hmi.middleware.stomp;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STOMPMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(STOMPMiddlewareLoader.class.getName());

	/**
	 * This loads the STOMPMiddleware instance
	 */
	@Override
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		String apolloIP = "";
		int apolloPort = -1;
		String iTopic = "";
		String oTopic = "";
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.debug("propkey: {}",(String)entry.getKey());
			logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals("apolloIP")){
				apolloIP = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("apolloPort")){
				apolloPort = Integer.parseInt((String)entry.getValue());
			}
			if(((String)entry.getKey()).equals("iTopic")){
				iTopic = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("oTopic")){
				oTopic = (String)entry.getValue();
			}
		}
		
		if(apolloIP.equals("") || apolloPort < 1 || iTopic.equals("") || oTopic.equals("")){
			logger.error("Could not load the STOMPMiddleware, need at least properties: apolloIP, apolloPort, iTopic, oTopic. Can be set in the global middleware props or in the load call.");
		} else {
			m = new STOMPMiddleware(apolloIP, apolloPort, iTopic, oTopic);
		}
		
		return m;
	}

}

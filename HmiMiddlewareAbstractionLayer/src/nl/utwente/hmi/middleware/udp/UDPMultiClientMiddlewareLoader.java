package nl.utwente.hmi.middleware.udp;

import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;


/**
 * UDPMultiClientMiddlewareLoader class for one-to-many communication over UDP.
 * @param ps contains the required properties port and optionally timeout (in ms, default: 10000).
 * @see UDPMiddleware
 * @author jankolkmeier
 */
public class UDPMultiClientMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(UDPMultiClientMiddlewareLoader.class.getName());

	@Override
	/**
	 * This loads the UDPMiddleware instance with multiple client support 
	 */
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		int listenPort = -1;
		int timeout = 10000;
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.info(" "+(String)entry.getKey()+": "+(String)entry.getValue());
			if(((String)entry.getKey()).equals("port")){
				listenPort = Integer.parseInt((String)entry.getValue());
			}

			if(((String)entry.getKey()).equals("timeout")){
				timeout = Integer.parseInt((String)entry.getValue());
			}
		}
		
		if (listenPort < 1) {
			logger.error("Could not load the UDPMiddleware, need at least properties: port. Can be set in the global middleware props or in the load call.");
		} else {
			m = new UDPMiddleware(listenPort, timeout);
		}
		
		return m;
	}

}
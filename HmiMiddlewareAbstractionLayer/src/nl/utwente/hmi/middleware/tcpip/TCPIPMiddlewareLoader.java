package nl.utwente.hmi.middleware.tcpip;

import java.util.Map.Entry;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPIPMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(TCPIPMiddlewareLoader.class.getName());

	/**
	 * This loads the TCPIPMiddleware instance given the supplied properties
	 */
	@Override
	public Middleware loadMiddleware(Properties ps) {

		Middleware m = null;
		int readPort = -1;
		int sendPort = -1;
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			try{
				if(((String)entry.getKey()).equals("readPort")){
					readPort = Integer.parseInt(((String)entry.getValue()));
				}
				if(((String)entry.getKey()).equals("sendPort")){
					sendPort = Integer.parseInt(((String)entry.getValue()));
				}
			} catch(NumberFormatException nfe){
				logger.error("Error loading the port specifications. Please ensure these are numeric.");
			}
		}
		
		if(		(readPort >= 1 && readPort <= 65535) && 
				(sendPort >= 1 && sendPort <= 65535))
		{
			m = new TCPIPMiddleware(readPort, sendPort);
		} else {
			logger.error("Could not load the TCPIPMiddleware, need at least properties: readPort, sendPort. Please ensure these are numeric and within acceptable range [1 .. 65535]. . Can be set in the global middleware props or in the load call.");
		}
		
		return m;
	}

}

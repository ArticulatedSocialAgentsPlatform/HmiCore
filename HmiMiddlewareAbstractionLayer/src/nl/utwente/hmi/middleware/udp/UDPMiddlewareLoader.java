package nl.utwente.hmi.middleware.udp;

import java.net.InetAddress;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;

/**
 * UDPMiddlewareLoader class for single client end-to-end communication over UDP.
 * @see UDPMiddleware
 * @author jankolkmeier
 */
public class UDPMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(UDPMiddlewareLoader.class.getName());

	/**
	 * This loads the UDPMiddleware instance
	 * @param ps contains the required properties remoteIP, remotePort and localPort
	 * @see UDPMiddleware
	 * @author jankolkmeier
	 */
	@Override
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		String remoteIP = "";
		int localPort = 0;
		int remotePort = -1;
		int heartbeat = -1;
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.debug("propkey: {}",(String)entry.getKey());
			logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals("remoteIP")){
				remoteIP = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("localPort")){
				localPort = Integer.parseInt((String)entry.getValue());
			}

			if(((String)entry.getKey()).equals("remotePort")){
				remotePort = Integer.parseInt((String)entry.getValue());
			}

			if(((String)entry.getKey()).equals("heartbeat")){
				heartbeat = Integer.parseInt((String)entry.getValue());
			}
		}
		
		if (remoteIP.equals("") || remotePort < 1) {
			logger.error("Could not load the UDPMiddleware, need at least properties: remoteIP, remotePort. Can be set in the global middleware props or in the load call.");
		} else {
			InetAddress addr;
			try {
				addr = InetAddress.getByName(remoteIP);
			} catch (UnknownHostException e) {
				logger.error("Could not parse remoteIP: "+remoteIP);
				e.printStackTrace();
				return null;
			}
			
			m = new UDPMiddleware(localPort, new InetSocketAddress(addr, remotePort), heartbeat);
		}
		
		return m;
	}

}
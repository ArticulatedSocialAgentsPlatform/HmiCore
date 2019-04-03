package nl.utwente.hmi.middleware.loader;

import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;

/**
 * An interface for loading and setting up a specific Middleware (such as YARP or Apollo/STOMP)
 * Each specific implementing class is responsible for validating the properties.
 * @author davisond
 *
 */
public interface MiddlewareLoader {

	/**
	 * Loads the middleware with given properties
	 * @param ps, properties for the middleware
	 * @return the middleware that has been loaded
	 */
	public Middleware loadMiddleware(Properties ps);
	
}

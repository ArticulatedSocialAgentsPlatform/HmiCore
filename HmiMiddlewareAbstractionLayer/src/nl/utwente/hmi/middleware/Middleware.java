package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Very basic interface for sending and recieving "a data package" to and from "a middleware"
 * Implementing classes are responsible for converting the Data structure to and from a suitable middleware-specific message format.
 * @author davisond
 *
 */
public interface Middleware {

	/**
	 * Send the data package accross the middleware channel
	 * @param jn the data package in JSON format (this is translated by the specific middleware before being sent)
	 */
	public void sendData(JsonNode jn);
	
	/**
	 * Add a listener to this middleware, which is notified via the MiddlewareListener.recieveData() when a data package is recieved from the middleware
	 * @param ml the listener instance
	 */
	public void addListener(MiddlewareListener ml);
	
}

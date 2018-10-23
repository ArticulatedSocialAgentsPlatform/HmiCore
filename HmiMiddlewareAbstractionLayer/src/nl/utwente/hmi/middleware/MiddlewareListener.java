package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A middleware listener class will be notified of any incoming data packets from the middleware by the receiveData() function
 * @author davisond
 *
 */
public interface MiddlewareListener {

	/**
	 * Callback method which is called by the Middleware when a new data package arrives
	 * @param jn the received data, formatted as JSON (the specific middleware implementation should process this and transform to a specific format)
	 */
	public void receiveData(JsonNode jn);
	
}

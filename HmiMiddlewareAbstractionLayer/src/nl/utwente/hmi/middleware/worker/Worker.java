package nl.utwente.hmi.middleware.worker;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * A Worker class can take care of a middleware data message
 * @author davisond
 *
 */
public interface Worker extends Runnable {

	/**
	 * Place data in processingQueue of the worker
	 * @param d
	 */
	public void addDataToQueue(JsonNode jn);
	
}

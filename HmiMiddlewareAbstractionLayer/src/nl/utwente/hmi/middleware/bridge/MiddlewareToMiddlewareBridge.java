package nl.utwente.hmi.middleware.bridge;

import com.fasterxml.jackson.databind.JsonNode;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;

/**
 * Creates a bridge between two middlewares, forwarding data packets from one to the other
 * @author davisond
 *
 */
public class MiddlewareToMiddlewareBridge {

	private Middleware m1;
	private Middleware m2;

	public MiddlewareToMiddlewareBridge(Middleware m1, Middleware m2){
		this.m1 = m1;
		this.m2 = m2;
	}
	
	/**
	 * Link both middlewares together using DataForwarders which listen to each other's data
	 */
	public void initBridge(){
		m1.addListener(new DataForwarder(m2));
		m2.addListener(new DataForwarder(m1));
	}
	
	/**
	 * A simple implementation of a MiddlewareListener that can send data packets on to a different middleware
	 * @author davisond
	 *
	 */
	private class DataForwarder implements MiddlewareListener {
		private Middleware otherMiddleware;

		public DataForwarder(Middleware otherMiddleware){
			this.otherMiddleware = otherMiddleware;
		}

		@Override
		public void receiveData(JsonNode jn) {
			otherMiddleware.sendData(jn);
		}
	}
	
}

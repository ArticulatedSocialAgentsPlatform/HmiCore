/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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

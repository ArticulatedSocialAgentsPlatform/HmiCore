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
package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * Very basic interface for sending and receiving "a data package" to and from "a middleware"
 * Implementing classes are responsible for converting the data structure to and from a suitable middleware-specific message format.
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
	 * Add a listener to this middleware, which is notified via the MiddlewareListener.receiveData() when a data package is received from the middleware
	 * @param ml the listener instance
	 */
	public void addListener(MiddlewareListener ml);

	public void sendDataRaw(String data);
	
}

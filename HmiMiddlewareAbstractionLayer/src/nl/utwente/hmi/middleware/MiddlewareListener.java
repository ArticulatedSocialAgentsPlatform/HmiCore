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

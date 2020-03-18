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
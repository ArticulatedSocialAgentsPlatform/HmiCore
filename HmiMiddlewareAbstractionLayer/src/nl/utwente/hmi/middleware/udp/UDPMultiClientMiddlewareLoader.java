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
package nl.utwente.hmi.middleware.udp;

import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;


/**
 * UDPMultiClientMiddlewareLoader class for one-to-many communication over UDP.
 * @see UDPMiddleware
 * @author jankolkmeier
 */
public class UDPMultiClientMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(UDPMultiClientMiddlewareLoader.class.getName());

	@Override
	/**
	 * This loads the UDPMiddleware instance with multiple client support
	 *  @param ps contains the required properties port and optionally timeout (in ms, default: 10000).
	 */
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		int listenPort = -1;
		int timeout = 10000;
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.debug("propkey: {}",(String)entry.getKey());
			logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals("port")){
				listenPort = Integer.parseInt((String)entry.getValue());
			}

			if(((String)entry.getKey()).equals("timeout")){
				timeout = Integer.parseInt((String)entry.getValue());
			}
		}
		
		if (listenPort < 1) {
			logger.error("Could not load the UDPMiddleware, need at least properties: port. Can be set in the global middleware props or in the load call.");
		} else {
			m = new UDPMiddleware(listenPort, timeout);
		}
		
		return m;
	}

}
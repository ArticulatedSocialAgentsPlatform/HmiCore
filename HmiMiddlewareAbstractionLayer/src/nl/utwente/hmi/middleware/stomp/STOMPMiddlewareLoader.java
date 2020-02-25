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
package nl.utwente.hmi.middleware.stomp;

import java.util.Map.Entry;
import java.util.Properties;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STOMPMiddlewareLoader implements MiddlewareLoader {
	private static Logger logger = LoggerFactory.getLogger(STOMPMiddlewareLoader.class.getName());

	/**
	 * This loads the STOMPMiddleware instance
	 */
	@Override
	public Middleware loadMiddleware(Properties ps) {
		Middleware m = null;
		String apolloIP = "";
		int apolloPort = -1;
		String iTopic = "";
		String oTopic = "";
		
		for(Entry<Object, Object> entry : ps.entrySet()){
			logger.debug("propkey: {}",(String)entry.getKey());
			logger.debug("propval: {}",(String)entry.getValue());
			if(((String)entry.getKey()).equals("apolloIP")){
				apolloIP = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("apolloPort")){
				apolloPort = Integer.parseInt((String)entry.getValue());
			}
			if(((String)entry.getKey()).equals("iTopic")){
				iTopic = (String)entry.getValue();
			}
			if(((String)entry.getKey()).equals("oTopic")){
				oTopic = (String)entry.getValue();
			}
		}
		
		if(apolloIP.equals("") || apolloPort < 1 || iTopic.equals("") || oTopic.equals("")){
			logger.error("Could not load the STOMPMiddleware, need at least properties: apolloIP, apolloPort, iTopic, oTopic. Can be set in the global middleware props or in the load call.");
		} else {
			m = new STOMPMiddleware(apolloIP, apolloPort, iTopic, oTopic);
		}
		
		return m;
	}

}

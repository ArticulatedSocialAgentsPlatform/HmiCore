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
package nl.utwente.hmi.middleware.ros;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.loader.MiddlewareLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map.Entry;
import java.util.Properties;

/** Loading a ROS Middleware **/

public class ROSMiddlewareLoader implements MiddlewareLoader {
    private static Logger logger = LoggerFactory.getLogger(ROSMiddlewareLoader.class.getName());

    /**
     * Loading the middleware specifically for ROS
     * @param ps, properties for the middleware, for ROS you need:
     *            "webscocketURI (default: ws://localhost:9090)"
     *            "publisher"
     *            "subscriber"
     * @return the middleware that is created
     */
    public Middleware loadMiddleware(Properties ps) {
        Middleware m = null;
        String websocketURI = "ws://localhost:9090";
        String publisher = "";
        String subscriber = "";
        for (Entry<Object, Object> entry : ps.entrySet()) {
            logger.debug("propkey: {}",(String)entry.getKey());
            logger.debug("propval: {}",(String)entry.getValue());
            try {
                if (((String) entry.getKey()).equals("websocketURI")) {
                    websocketURI = (String)entry.getValue();
                }
                if(((String)entry.getKey()).equals("publisher")){
                    publisher = (String)entry.getValue();
                }
                if(((String)entry.getKey()).equals("subscriber")){
                    subscriber = (String)entry.getValue();
                }
            } catch (NumberFormatException nfe) {
                logger.error("Error loading the port specifications. Please ensure these are numeric. {}",nfe);
            }
        }

        if(websocketURI.equals("") || publisher.equals("") || subscriber.equals("")){
            logger.error("Could not load the ROSMiddleware, need at least properties: the WebSocket URI with port (default : ws://localhost:9090), publisher and subscriber. The port value is '9090' by default.");
        } else {
            m = new ROSMiddleware(websocketURI, publisher, subscriber);
        }

        return m;
    }

}

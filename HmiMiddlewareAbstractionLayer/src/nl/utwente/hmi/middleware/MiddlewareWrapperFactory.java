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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class MiddlewareWrapperFactory {

    private static Logger logger = LoggerFactory.getLogger(MiddlewareWrapperFactory.class.getName());


    /**
     * Method for creating a MiddlewareWrapper with a configuration file
     * @param jn, a JsonNode that represents properties of the middleware in the format of
     *            {
     *              "loader" : "nl.utwente.hmi.*",
     *              "properties" : {
     *
     *              }
     *            }
     * @return, the MiddlewareWrapper
     */
    public static MiddlewareWrapper createMiddlewareWrapper(JsonNode jn){
        if(jn.has("loader")) {
            Properties mwProps = new Properties();
            String mw = jn.get("loader").textValue();
            mwProps.put("loader",mw);
            JsonNode props = jn.get("properties");
            Iterator<Map.Entry<String, JsonNode>> it = props.fields();
            while(it.hasNext()) {
                Map.Entry<String, JsonNode> prop = it.next();
                mwProps.setProperty(prop.getKey(),prop.getValue().textValue());
            }
            return new MiddlewareWrapper(mwProps) {
                @Override
                public void processData(JsonNode jn) {
                    logger.debug("Received Data: {}",jn.toString());
                }
            };
        }
        else{
            logger.error("Invalid format for instantiating middleware");
            return null;
        }
    }

    /**
     * Method for creating a MiddlewareWrapper with a configuration file
     * @param node, a String representation in JSON that represents properties of the middleware in the format of
     *            {
     *              "loader" : "nl.utwente.hmi.*",
     *              "properties" : {
     *
     *              }
     *            }
     * @return, the MiddlewareWrapper
     */
    public static MiddlewareWrapper createMiddlewareWrapper(String node){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return MiddlewareWrapperFactory.createMiddlewareWrapper(mapper.readTree(node));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.error("Invalid format for instantiating middleware");
        return null;
    }
}

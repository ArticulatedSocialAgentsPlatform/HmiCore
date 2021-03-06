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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pk.aamir.stompj.Message;
import pk.aamir.stompj.MessageHandler;
import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;

/**
 * Implementation for the STOMP middleware.
 * This class uses a STOMPHandler to pass around XML messages on the defined input and output topics
 * Data objects are converted to XML Strings using the DataHelper classes
 * @author davisond
 *
 */
public class STOMPMiddleware implements Middleware, MessageHandler {
	private static Logger logger = LoggerFactory.getLogger(STOMPMiddleware.class.getName());

	String apolloIP;
	int apolloPort;
	String iTopic;
	String oTopic;

	private Set<MiddlewareListener> listeners;

	private StompHandler stompHandler;

	/**
	 * Create this Middleware using the specified ports
	 * @param apolloIP the IP of the appollo server we connect to
	 * @param apolloPort the port number of the apollo server
	 * @param iTopic the topic name on which we are listening for input
	 * @param oTopic the topic name on which we are writing output
	 */
	public STOMPMiddleware(String apolloIP, int apolloPort, String iTopic, String oTopic){
		this.apolloIP = apolloIP;
		this.apolloPort = apolloPort;
		this.iTopic = iTopic;
		this.oTopic = oTopic;
		
		this.listeners = Collections.synchronizedSet(new HashSet<MiddlewareListener>());
		
		this.stompHandler = new StompHandler(apolloIP, apolloPort);
		
		stompHandler.registerCallback(iTopic, this);
	
	}
	
	public void sendDataRaw(String data) {
		if(data != null){
			stompHandler.sendMessage(data, oTopic);
			logger.debug("Sending data: {}", data);
		}
	}
	
	@Override
	public void sendData(JsonNode jn) {
		if(jn != null){
			stompHandler.sendMessage(jn.toString(), oTopic);
			logger.debug("Sending data: {}", jn.toString());
		}
	}
	
	@Override
	public void addListener(MiddlewareListener ml) {
		this.listeners.add(ml);
	}

	@Override
	public void onMessage(Message msg) {
		logger.debug("Got message on topic {}: {}", iTopic, msg.getContentAsString());	
		String jsonString = msg.getContentAsString();
		
		//parse json string and create JsonObject
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode jn = mapper.readTree(jsonString);
			logger.debug("Transformed to json object: {}", jn.toString());
			
			if(jn != null){
				for(MiddlewareListener ml : listeners){
					ml.receiveData(jn);
				}
			}
		} catch (JsonProcessingException e) {
			logger.warn("Error while parsing JSON string \"{}\": {}", jsonString, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

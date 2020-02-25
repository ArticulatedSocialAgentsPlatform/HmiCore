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

import pk.aamir.stompj.Connection;
import pk.aamir.stompj.MessageHandler;
import pk.aamir.stompj.ErrorHandler;
import pk.aamir.stompj.ErrorMessage;
import pk.aamir.stompj.StompJException;

/**
 * Simple class for handling the STOMP connection and sending/receiving messages
 * @author davisond
 *
 */
public class StompHandler implements ErrorHandler {

	private Connection con;

	public StompHandler(String apolloIP, int apolloPort){
		init(apolloIP, apolloPort);
	}

	/**
	 * Initialise the connection con
	 * @param apolloPort 
	 * @param apolloIP 
	 */
	private void init(String apolloIP, int apolloPort) {

        //Connect to the stomp server so we can send and receive messages to ASAP
        try {
            con = new Connection(apolloIP, apolloPort, "admin", "password");
            con.setErrorHandler(this);
			con.connect();
		} catch (StompJException e) {
			System.out.println("Error while initialising STOMP connection: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Register a message handler for this Stomp connection
	 * The msgHandler will be invoked every time a message arrives on the specified topic
	 * @param topic the topic to register on
	 * @param msgHandler the message handler
	 */
	public void registerCallback(String topic, MessageHandler msgHandler){
    	con.subscribe(topic, true);
		con.addMessageHandler(topic, msgHandler);
	}
	
	public void sendMessage(String msg, String topic){
    	con.send(msg, topic);
	}

    @Override
    public void onError(ErrorMessage errormessage)
    {
        System.out.println(errormessage.getMessage());
    }
}

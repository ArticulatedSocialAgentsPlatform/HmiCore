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

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;

/**
 * UDPMiddleware class for one-to-one and one-to-many communication over UDP.
 * Multiple clients can send directly to the local udp listen port.
 * In one-to-one mode, outgoing data will be sent to a preconfigured endpoint.
 *   - The preconfigured endpoint will never time out
 * In one-to-many mode, outgoing data will be sent to all (active, see timeout) client endpoints.
 *   - A client will be added to the list of senders (endpoints) once data from a new client was received.
 *   - A client must listen on the same port they use to send data to this middleware receive outgoing data.
 *   - If timeout is enabled, a client endpoint will be removed if no heartbeats (emtpy msg) were received recently.
 * @author jankolkmeier
 */
public class UDPMiddleware implements Middleware, Runnable {
	public static Logger logger = LoggerFactory.getLogger(UDPMiddleware.class.getName());
	
	private boolean running; // Worker thread state
	
	private final int listenPort; // upd listen port.
	private UDPListener listener; // Listener for incoming data on listenPort
	private Set<MiddlewareListener> mwListeners; // Local middleware listeners (in ASAP env)
	private Set<UDPEndpoint> endpoints; // Represents endpoints of remote udp clients
	private boolean singleMode; // See constructors
	private int timeout;    // Timeout interval length before inactive clients are removed (for multi remote).
	private int heartbeat; // Heartbeat interval sent to remote (for single remote).
	private long lastHeartbeatSent; // ...
	private ManualResetEvent main_MRE = new ManualResetEvent(false);
	
	
	/**
	 * Creates a multi-client UDPMiddleware using the specified local port.
	 * All UDP clients that send data to this port will receive outgoing data.
	 * If timeout is set to a positive value, clients from which the listener
	 * hasn't received any packets since $timeout$ ms will be removed from the list.
	 * Clients can send empty ("" or "{}") messages as "heartbeat" to keep receiving updates.
	 * @param listenPort  the port to listen on incoming data
	 * @param timeout the timeout in ms 
	 */
	public UDPMiddleware(int listenPort, int timeout) {
		this.listenPort = listenPort;
		this.timeout = timeout;
		this.mwListeners = Collections.synchronizedSet(new HashSet<MiddlewareListener>());
		this.endpoints = Collections.synchronizedSet(new HashSet<UDPEndpoint>());
		
		this.listener = new UDPListener(this.listenPort, this);
		new Thread(this.listener).start();
		new Thread(this).start();
		singleMode = false;
	}
	
	/**
	 * Creates a single-client UDPMiddleware using the specified local port & remote.
	 * This constructor sets the default endpoint and enables single mode.
	 * The default sender does not time out.
	 * @param listenPort the port to listen on incoming data
	 * @param defaultRemote used to set the default sender.
	 * @param heartbeat if > 0, sends a heartbeat to defaultRemote every $heartbeat$ms.
	 */
	public UDPMiddleware(int listenPort, InetSocketAddress defaultRemote, int heartbeat) {
		this(listenPort, -1); // UDPListener is initialized and running
		this.heartbeat = heartbeat;
		// Default endpoint sends from the same socket we're also listening on!
		UDPEndpoint defaultEndpoint = new UDPEndpoint(defaultRemote, listener.listenSocket); 
		endpoints.add(defaultEndpoint);
		new Thread(defaultEndpoint).start();
		singleMode = true;
	}
	
	public void close() {
		running = false;
		main_MRE.set();
		main_MRE.reset();
		for (UDPEndpoint sender : endpoints) {
			sender.close();
		}
		
		listener.close();
	}
	
    public void run() {
    	running = true;
    	while (running) { // look at data from listen thread
    		
    		try {
				main_MRE.waitOne(100);
			} catch (InterruptedException e1) {
    			running = false;
    			UDPMiddleware.logger.info("Send thread interrupted.");
				e1.printStackTrace();
			}
    		
    		long now = System.currentTimeMillis();
    		
    		// Send heartbeat
    		if (heartbeat > 0 && lastHeartbeatSent+heartbeat < now) {
    			lastHeartbeatSent = now;

        		logger.debug("Queueing Heartbeat!");
    			Iterator<UDPEndpoint> i = endpoints.iterator();
    			while (i.hasNext()) {
    				UDPEndpoint s = i.next();
    				s.enqueue("");
    			}
    		}
    		
    		boolean haveData = true;
    		while (haveData) {
	
	    		Map.Entry<InetSocketAddress,String> data = listener.poll();
	    		if (data == null) {
	    			haveData = false;
	    			continue;
	    		}
	    		
	    		// TODO: in single client mode, we might want to check if data is really
	    		// coming from default client.
	
	    		if (data.getValue().length() > 0 && !data.getValue().equals("{}")) {
	        		logger.debug("Got message on port {}: <{}>", listenPort, data.getValue());
	        		receiveCallback(data.getValue());
	    		} else {
	        		logger.debug("Got heartbeat on port {}: <{}>", listenPort, data.getValue());
	    		}
	    		
	    		if (singleMode) continue;
	    		
	    		boolean exists = false;
	    		for (UDPEndpoint sender : endpoints) {// Don't start a new sender if client already exists
	    			if (sender.remoteClient.equals(data.getKey())) {
	    				sender.lastHeartbeat = now;
	    				exists = true; break;
					}
	    		}
	    		if (exists) continue;
	    		
	    		// Add client to list of clients that receives data.
	    		UDPEndpoint newClient = new UDPEndpoint(data.getKey(), null);
	    		endpoints.add(newClient);
	    		new Thread(newClient).start();
	
				logger.info("Added remote client at: {}:{}",
						newClient.remoteClient.getAddress().toString(), newClient.remoteClient.getPort());
	    	}
    	}
    }

	@Override
	public void sendData(JsonNode jn) {
		this.sendDataRaw(jn.toString());
	}

	@Override
	public void addListener(MiddlewareListener ml) {
		this.mwListeners.add(ml);
	}
	
	public void notifyListener() {
		main_MRE.set();
		main_MRE.reset();
	}
	
	public void receiveCallback(String data) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jn = mapper.readTree(data);
			logger.debug("Transformed to json object: {}", jn.toString());
			if(jn != null){
				for(MiddlewareListener ml : mwListeners){
					ml.receiveData(jn);
				}
			}
		} catch (Exception e) { // JsonProcessingException, IOException
			logger.warn("Error while parsing JSON string \"{}\": {}", data, e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void sendDataRaw(String data) {
		// TODO Auto-generated method stub
		if (data == null) return;
		long now = System.currentTimeMillis();

		logger.debug("Sending data: {}", data);
		
		Iterator<UDPEndpoint> i = endpoints.iterator();
		while (i.hasNext()) {
			UDPEndpoint s = i.next();
			boolean timedOut = (s.lastHeartbeat+timeout) < now;
			boolean checkTimeout = !singleMode && timeout > 0;
			if (s.isRunning() && (!checkTimeout || !timedOut)) {
				s.enqueue(data);
				logger.debug("\t to "+s.remoteClient.getHostString()+":"+s.remoteClient.getPort());
			} else {
				logger.info("Removing client "+s.remoteClient.getHostString()+":"+s.remoteClient.getPort()+
						". Timed out: {} running: {}", timedOut, s.isRunning());
				i.remove();
			}
		}
	}
}
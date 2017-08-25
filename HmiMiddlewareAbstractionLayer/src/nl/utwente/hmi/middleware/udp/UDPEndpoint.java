package nl.utwente.hmi.middleware.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread that sends queued data to a remote endpoint.
 * @author jankolkmeier
 */
public class UDPEndpoint implements Runnable {
	private boolean running = true;
	
	public long lastHeartbeat;
	public final InetSocketAddress remoteClient;
	private ConcurrentLinkedQueue<String> sendQueue;
	DatagramSocket sendSocket;
	
	private ManualResetEvent send_MRE = new ManualResetEvent(false);
	
	/** 
     * Create sender thread object. This does not start the thread.
     * @param remoteClient representing the remote endpoint to send to.
     */
	public UDPEndpoint(InetSocketAddress remoteClient, DatagramSocket socket) {
		if (socket != null) sendSocket = socket;
		else {
			sendSocket = null;
			try { // Open the socket
				sendSocket = new DatagramSocket();
				running = true;
			} catch (SocketException e1) {
				running = false;
				UDPMiddleware.logger.error("Failed to open send socket.");
				e1.printStackTrace();
			}
		}
		
    	this.remoteClient = remoteClient;
		this.sendQueue = new ConcurrentLinkedQueue<String>();
		lastHeartbeat = System.currentTimeMillis();
    }
	
	/** 
     * Create sender thread object. This does not start the thread.
     * @param remoteAddr address of remote endpoint
     * @param remotePort port on remote endpoint
     */
    public UDPEndpoint(InetAddress remoteAddr, int remotePort, DatagramSocket socket) {
    	this(new InetSocketAddress(remoteAddr, remotePort), socket);
    }

	/** 
     * Queues a message for sending.
     * @param msg message to be queued
     */
    public void enqueue(String msg) {
    	if (running) {
    		sendQueue.add(msg);
    		send_MRE.set();
    		send_MRE.reset();
    	}
    }
    
    public void run() {
    	
    	while (running) { // See if there is a new message to be sent

    		try {
				send_MRE.waitOne(100);
			} catch (InterruptedException e1) {
    			running = false;
    			UDPMiddleware.logger.info("Send thread interrupted.");
				e1.printStackTrace();
			}
    		
    		boolean haveData = true;
    		while (haveData) {
	    		String data = sendQueue.poll();
	    		if (data == null) {
	    			haveData = false;
	    			continue;
	    		}
	    		
	    		DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), remoteClient);
	    		try {
	    			sendSocket.send(sendPacket);
	    		} catch (IOException e) {
	    			running = false;
	    			UDPMiddleware.logger.info("Failed to send data. Closing sender.");
	    			e.printStackTrace();
	    		}
    		}
    	}
    	
    	if (sendSocket!=null) sendSocket.close();
    }
    
    public boolean isRunning() {
    	return running;
    }
    
	public void close() {
		running = false;
		send_MRE.set();
		send_MRE.reset();
	}
}
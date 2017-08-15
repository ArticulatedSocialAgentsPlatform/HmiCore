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
	
	/** 
     * Create sender thread object. This does not start the thread.
     * @param remoteClient representing the remote endpoint to send to.
     */
	public UDPEndpoint(InetSocketAddress remoteClient) {
    	this.remoteClient = remoteClient;
		this.sendQueue = new ConcurrentLinkedQueue<String>();
		lastHeartbeat = System.currentTimeMillis();
    }
	
	/** 
     * Create sender thread object. This does not start the thread.
     * @param remoteAddr address of remote endpoint
     * @param remotePort port on remote endpoint
     */
    public UDPEndpoint(InetAddress remoteAddr, int remotePort) {
    	this(new InetSocketAddress(remoteAddr, remotePort));
    }

	/** 
     * Queues a message for sending.
     * @param msg message to be queued
     */
    public void enqueue(String msg) {
    	if (running) sendQueue.add(msg);
    }
    
    public void run() {
    	running = true;
    	DatagramSocket sendSocket = null;
		try { // Open the socket
			sendSocket = new DatagramSocket();
		} catch (SocketException e1) {
			UDPMiddleware.logger.error("Failed to open send socket.");
			e1.printStackTrace();
		}
		
    	while (running) { // See if there is a new message to be sent
    		String data = sendQueue.poll();
    		if (data == null) continue;
    		DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), remoteClient);
    		try {
    			sendSocket.send(sendPacket);
    		} catch (IOException e) {
    			running = false;
    			UDPMiddleware.logger.info("Failed to send data. Closing sender.");
    			e.printStackTrace();
    		}
    	}
    	
    	if (sendSocket!=null) sendSocket.close();
    }
    
    public boolean isRunning() {
    	return running;
    }
    
	public void close() {
		running = false;
	}
}
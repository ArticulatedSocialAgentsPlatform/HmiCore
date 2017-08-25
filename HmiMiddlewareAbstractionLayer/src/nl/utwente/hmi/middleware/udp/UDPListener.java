package nl.utwente.hmi.middleware.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Listens for incomming data on a UDP port.
 * @author jankolkmeier
 */
public class UDPListener implements Runnable {
	private boolean running;
	private int iPort;
	private ConcurrentLinkedQueue<Map.Entry<InetSocketAddress,String>> listenQueue;
	public DatagramSocket listenSocket;
	
	private UDPMiddleware mw;
	
    /** 
     * Create a listener thread object. This does not start the thread.
     * @param iPort the port to listen on.
     */
    public UDPListener(int iPort, UDPMiddleware mw) {
    	this.iPort = iPort;
		this.listenQueue = new ConcurrentLinkedQueue<Map.Entry<InetSocketAddress,String>>();
		this.mw = mw;
		try { // Open the socket
			listenSocket = new DatagramSocket(this.iPort);
	    	running = true;
		} catch (SocketException e1) {
			UDPMiddleware.logger.error("Failed to open listen socket.");
			e1.printStackTrace();
			running = false;
		}
    }

    public void run() {
    	byte[] receiveBuffer = new byte[65507]; // Max UDP packet size.
		
    	while (running) {
    		DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
    		
    		try { // Read data
    			listenSocket.receive(receivePacket);
    		} catch (IOException e) {
    			running = false;
    			UDPMiddleware.logger.error("Failed to receive packet.");
    			e.printStackTrace();
    			continue;
    		}
    		
    		byte[] data = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
            
            // Add message to queue.
    		listenQueue.add(new AbstractMap.SimpleEntry<InetSocketAddress, String>
    				((InetSocketAddress) receivePacket.getSocketAddress(), new String(data)));
    		mw.notifyListener();
    	}
		if (listenSocket != null) listenSocket.close();
    }
    
    /** 
     * Packets received on socket are queued as a tuple together with an object describing their source.
     * @return the oldest unread message on the queue, or null if empty
     */
    public Map.Entry<InetSocketAddress,String> poll() {
    	return listenQueue.poll();
    }

	public void close() {
		running = false;
	}

}
package nl.utwente.hmi.middleware.tcpip;

import static nl.utwente.hmi.middleware.helpers.JsonNodeBuilders.array;
import static nl.utwente.hmi.middleware.helpers.JsonNodeBuilders.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;

/**
 * Implements a Middleware interface, for converting between JSON objects and a TCP/IP socket.
 * This class implements a listening server socket construction for making the initial connection. 
 * When a client connects, the server automatically creates a reverse connection back to the client to ensure a two-way communication.
 * The connection is self-healing, when either side (send or receive) encounters a problem the connection is closed and the server starts waiting again.
 * 
 * @author Dennis Reidsma
 * @author davisond
 */
public final class TCPIPMiddleware implements Runnable, Middleware
{
	private static Logger logger = LoggerFactory.getLogger(TCPIPMiddleware.class.getName());

	private ArrayList<MiddlewareListener> listeners;
	
    /*
     * ========================================================================================= A
     * FEW GLOBAL STATIC PARAMETERS THAT YOU MIGHT WANT TO PLAY AROUND WITH
     * =========================================================================================
     */

    /** Sleeping time of the RequestRedirector if the readQueue is empty */
    private static final long REDIRECT_WAIT_MILLI = 100;

    private static final int SOCKET_TIMEOUT = 5000; // wait 5 secs for client, then timeout to allow
                                                    // reading process to terminate if shutdown

    private static final int CONNECT_RETRY_WAIT = 1000;

    // private static final int WAIT_IF_NO_BML = 100;
    private static final int WAIT_AFTER_DROPPED_CLIENT = 1000;

    /*
     * =========================================================================================
     * EXTERNAL ACCESS: CONTROLLING THE NETWORK CONNECTION
     * =========================================================================================
     */

    /**
     * Instigates total shutdown. May return from this method before shutdown is completed. Shutdown
     * process will terminate all connections and threads for this server.
     */
    public void shutdown()
    {
        synchronized (connectionLock)
        {
            mustshutdown = true;
        }
    }

    /** Returns true iff there is a connection to a client active (and has not been lost). */
    public boolean isConnectedToClient()
    {
        synchronized (connectionLock)
        {
            return isconnected;
        }
    }

    public ServerState getStatus()
    {
        return serverState;
    }

    /**
     * Serverstate
     */
    public enum ServerState
    {
        WAITING, CONNECTING, CONNECTED, DISCONNECTING, NOT_RUNNING
    };

    private ServerState serverState = ServerState.NOT_RUNNING;


    private void setServerState(ServerState state)
    {
        
            if (state == serverState) return;
            if (serverState == ServerState.WAITING && state == ServerState.DISCONNECTING) return; // drop
                                                                                                  // client
                                                                                                  // due
                                                                                            // mentioning
            serverState = state;
            
    }

    /*
     * ========================================================================================= THE
     * MAIN NETWORKING PART: FEEDBACK QUEUE AND STATE VARIABLES
     * =========================================================================================
     */

    /**
     * Incoming messages from YARP are stored here. The main networking loop will get
     * the message and send it to the client if a connection is available.
     */
    private ConcurrentLinkedQueue<String> sendQueue = new ConcurrentLinkedQueue<String>();

    /** Locking object for the states of the main networking loop. */
    private Object connectionLock = new Object();

    /** Internal state var: true if a conenction to a server is active */
    private boolean isconnected = false;

    /** Internal state var: true if a shutdown request has been given */
    private boolean mustshutdown = false;

    /**
     * Internal state var: true if a connection failed and the feedback connection needs to be
     * cleaned up
     */
    private boolean mustdisconnect = false;

    /**
     * Internal state var: true if someone connected on the reading port, and a sending
     * connection needs to be established
     */
    private boolean mustconnect = false;

    /** Waiting time for next run() loop. */
    private long nextMainLoopWait = 100;

    /*
     * ========================================================================================= THE
     * MAIN NETWORKING PART: NETWORKING CONNECTIONS
     * =========================================================================================
     */
    private InetSocketAddress sendSocketAddress = null;

    private Socket sendSocket = null;

    private PrintWriter sendWriter = null;

    private ServerSocket readServerSocket = null;

    private Socket readSocket = null;

    private BufferedReader readReader = null;

    private Reader reader = null;

    private Thread readerThread = null;

    private int readPort = 7500;

    private int sendPort = 7501;

    public int getReadPort()
    {
        return readPort;
    }

    public int getSendPort()
    {
        return sendPort;
    }

    /**
     * Stop reading sockets preparatory to completely shutting down, or preparatory to
     * connecting new client
     */
    public void stopReadSockets()
    {
        try
        {
            logger.debug("Stop Read sockets: Close read socket");
            if (readSocket != null) readSocket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try
        {
            logger.debug("Stop Read sockets: Close server socket");
            if (readServerSocket != null) readServerSocket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /** Stop feedback sending socket when to dropping client */
    public void stopSendSockets()
    {
        try
        {
            if (sendSocket != null) sendSocket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void queueSend(String msg)
    {
        boolean send = true;
        // if there is no connection, and no connection is being prepared, drop the request.
        synchronized (connectionLock)
        {
            if (!isconnected && !mustconnect)
            {
                send = false;
            }
        }
        if (send)
        {
            logger.debug("Putting feedback on queue: "+ msg);
            sendQueue.add(msg);
        }
        else
        {
            // log failure; drop feedback
            logger.warn("Dropped feedback, as no client is connected!");
        }
    }
    
    /*
     * ========================================================================================= THE
     * MAIN NETWORKING PART: THE READING PROCESS
     * =========================================================================================
     */

    /**
     * The process that reads messages from the network connection, and puts it in the message readQueue. If
     * connecting this reader leads to serious exceptions, a disconnect is requested. The reader
     * continues listening for new connections until the server is shut down.
     */
    private class Reader implements Runnable
    {
        public Reader()
        {
        }

        private boolean stop = false;

        boolean connected = false;

        public void stopReading()
        {
            stop = true;
        }

        public void run()
        {
            logger.info("Starting Reader");
            while (!stop)
            { // keep reading for new clients until stop (shutdown?)
                while (!connected && !stop)
                {
                    logger.info("Opening server socket");
                    // start listening
                    try
                    {
                        readServerSocket = new ServerSocket(readPort);
                    }
                    catch (IOException e)
                    {
                        failConnect(e.getMessage());
                        return;
                    }
                    logger.info("Server socket opened");
                    try
                    {
                        readServerSocket.setSoTimeout(0);// don't time out
                    }
                    catch (SocketException e)
                    {
                        failConnect(e.getMessage());
                        return;
                    }

                    try
                    {
                        readServerSocket.setSoTimeout(SOCKET_TIMEOUT);
                    }
                    catch (SocketException e)
                    {
                        failConnect(e.getMessage());
                        return;
                    }

                    try
                    {
                        setServerState(ServerState.WAITING);
                        logger.info("Waiting for client to connect");
                        readSocket = readServerSocket.accept();
                        connected = true;
                        setServerState(ServerState.CONNECTING);
                        logger.info("Incoming client; preparing reader");
                        readReader = new BufferedReader(new InputStreamReader(readSocket.getInputStream(),"UTF-8"));
                        logger.info("Client connected, starting lo listen for messages at port " + readPort);
                    }
                    catch (SocketTimeoutException e)
                    {
                        dropClient("Timeout while accepting incoming client. going back to listen.");
                        continue;
                    }
                    catch (IllegalBlockingModeException e)
                    {
                        failConnect(e.getMessage());
                        return;
                    }
                    catch (IllegalArgumentException e)
                    {
                        failConnect(e.getMessage());
                        return;
                    }
                    catch (IOException e)
                    {
                        dropClient(e.getMessage());
                        continue;
                    }
                    // reading channel is connected. Keep reading from it, and processing what comes in.
                    // also, try to open sending channel!
                    mustconnect = true;
                    //XMLTokenizer tok = new XMLTokenizer(readReader);

                    while (connected && !stop)
                    {
                        //System.out.println("Connected -- keep trying to read");
                        try
                        {
                            String msg = readReader.readLine();
                            if(msg != null){
                            	logger.debug("Got message: "+ msg);
                            	readQueue.add(msg);
                            } else {
                            	throw new IOException("Received TCP msg was null.. socket is broken..?");
                            }
                        }
                        catch (IOException e)
                        {
                            dropClient("Error while reading message from client. " + e.getMessage());
                        }
                    } // while connected
                } // while ! connected
            } // while ! stop
            logger.info("Closing sockets and readers in reader");
            stopReadSockets();
            logger.info("Leaving Reader");
        }

        private void failConnect(String msg)
        {
            logger.error("Failed to start listening to clients: {}.  Shutting server down.", msg);
            mustshutdown = true;
            nextMainLoopWait = 1;
        }

        private void dropClient(String msg)
        {
        	logger.error("Dropping client: {}", msg);
            stopReadSockets();
            connected = false;
            mustdisconnect = true;
            // note, sometimes the socket is slow in being released. After a drop client, it's best
            // to not accept connections for a bit.
            try
            {
                Thread.sleep(WAIT_AFTER_DROPPED_CLIENT);
            }
            catch (InterruptedException ex)
            {
            }
        }
    }

    /*
     * ========================================================================================= THE
     * MAIN NETWORKING PART: THE MAIN LOOP
     * =========================================================================================
     */

    /** The main loop! */
    public void run()
    {
        setServerState(ServerState.WAITING);
        while (true)
        {
            synchronized (connectionLock)
            {
                // if nothing to do, wait a bit before next loop run.
                nextMainLoopWait = 100;
                if (mustdisconnect)
                {
                    dodisconnect(); // disconnect before connect -- sometimes connect requires
                                    // disconnect of old connection
                }
                if (mustconnect)
                {
                    doconnect();
                }
                if (mustshutdown)
                {
                    doshutdown();
                }
                if (isconnected)
                {
                    String nextMsg = sendQueue.poll();
                    if (nextMsg != null)
                    {
                        dosend(nextMsg);
                    }
                }
            }
            if (mustshutdown) break;
            try
            {
                Thread.sleep(nextMainLoopWait);
            }
            catch (InterruptedException ex)
            {
                // no matter -- just continue with next round :)
            }
        }
        logger.info("Server shutdown finished");
    }

    /** Disconnect feedback connection to client. Called from the run() loop. */
    private void dodisconnect()
    {
        setServerState(ServerState.DISCONNECTING);
        // reader.dropClient("Cleaning up client connection"); no! dropclient is done elsewhere
        // beore setting mjustdisconnect to true
        // close the sockets etc
        logger.info("Closing feedback sender");
        stopSendSockets();
        logger.info("Feedback sender closed");
        mustdisconnect = false;
        isconnected = false;
        sendQueue.clear();
        nextMainLoopWait = 1;
        if (!mustshutdown) setServerState(ServerState.WAITING);
    }

    /**
     * Attempt to connect to client feedback channel. If successful, isconnected is true; else, try
     * again next round with wait = 1000. Called from the run() loop.
     */
    private void doconnect()
    {
        setServerState(ServerState.CONNECTING);
        logger.info("Connecting feedback sender");
        // first, prepare the writing socket. Note that the reading socket has been
        // established, otherwise we would not be connecting here!
        sendSocketAddress = new InetSocketAddress(((InetSocketAddress) readSocket.getRemoteSocketAddress()).getAddress(), sendPort);
        logger.info("Connecting to IP: {} on port: {}", sendSocketAddress.getAddress(), sendSocketAddress.getPort());
        
        sendSocket = new Socket();
        try
        {
            sendSocket.connect(sendSocketAddress, SOCKET_TIMEOUT);            
            sendWriter = new PrintWriter(new OutputStreamWriter(sendSocket.getOutputStream(),"UTF-8"), true);
        }
        catch (SocketTimeoutException e)
        {
            retryConnect("Timeout while attempting to connect.");
            return;
        }
        catch (IllegalBlockingModeException e)
        {
            reader.dropClient("IllegalBlockingModeException; "+e.getMessage()+"\n"+ Arrays.toString(e.getStackTrace()));
            return;
        }
        catch (IllegalArgumentException e)
        {
            reader.dropClient("IllegalArgumentException: "+e.getMessage()+"\n"+ Arrays.toString(e.getStackTrace()));
            return;
        }
        catch (IOException e)
        {
            reader.dropClient("IOException: "+e.getMessage()+"\n"+ Arrays.toString(e.getStackTrace()));
            return;
        }
        logger.info("Feedback sender connected");
        mustconnect = false; // success!
        isconnected = true;
        setServerState(ServerState.CONNECTED);
    }

    /** Error connecting, prepare to retry */
    private void retryConnect(String msg)
    {
        logger.error("Error connecting to client feedback channel: [{}] Will try again in msec...", msg);
        nextMainLoopWait = CONNECT_RETRY_WAIT;
    }

    /** Disconnect. Clean up redirectionloop. Called from the run() loop. */
    private void doshutdown()
    {
        setServerState(ServerState.DISCONNECTING);
        logger.info("Enter shutdown...");
        reader.stopReading();
        stopReadSockets();// deze blokkeert, want De XML toknizer heeft de reader op slot gezet
                             // en dan gaat de close() staan wachten. Dus ik ben bang dat ik hier de
                             // reader met meer geweld om zeep moet helpen. Thread kill?
        dodisconnect();
        logger.info("Stopping readerThread...");
        try
        { // wait till redirector stopped
            readerThread.join();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        logger.info("Stopping redirectorThread...");
        try
        { // wait till redirector stopped
            redirectorThread.join();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        logger.info("Shutdown almost done.");
        setServerState(ServerState.NOT_RUNNING);
    }

    /** Send given feedbacki. If fail: drop request, drop client. */
    private void dosend(String msg)
    {
        // if any next feedback, send it and set sleeptime to 1; upon error, dodisconnect
        try
        {
        	logger.debug("Sending msg: {}", msg);
            sendWriter.println(msg);
            
            //try to detect a broken socket
            if(sendWriter.checkError()){
            	throw new IOException("Error while flushing to socket");
            }
        }
        catch (Exception e)
        {
        	logger.error("Error sending feedback; dropping client");
            mustdisconnect = true;
            reader.dropClient("sending channel broken");
        }
        nextMainLoopWait = 1;
    }

    /*
     * ========================================================================================= THE
     * MESSAGE REDIRECTION PROCESS
     * =========================================================================================
     */

    /** redirects message that has come in over the socket to the clients listening on the middleware interface */
    private Redirector redirector = null;

    /** the thread under which the redirector runs. */
    private Thread redirectorThread = null;

    /**
     * Incoming messages from the client are stored here. The redirector loop will get them and send
     * them to the middleware interface listeners.
     */
    private ConcurrentLinkedQueue<String> readQueue = new ConcurrentLinkedQueue<String>();

    /** The process that reads message from the readQueue, and sends it to the listeners on the middleware. */
    private class Redirector implements Runnable
    {
        public void run()
        {
            while (!mustshutdown) // this thread should also stop when the server shuts down, and
                                  // not before.
            {
                String jsonString = readQueue.poll();
                if (jsonString != null)
                {
                    try
                    {
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
                    catch (Exception ex)
                    { // failing realizer means the application is down. shutdown server.
                    	logger.error("Error sending msg to realizer -- shutting down server! {}", ex);
                        mustshutdown = true;
                        nextMainLoopWait = 1;
                    }
                }
                else
                {
                    try
                    {
                        // nothing to send, let's wait a bit :)
                        Thread.sleep(REDIRECT_WAIT_MILLI);
                    }
                    catch (InterruptedException ex)
                    {
                        Thread.interrupted();
                        // no matter -- just continue with next round :) Maybe we were woken up
                        // because new message is available?
                    }
                }
            }
            logger.info("Shutdown Redirection readQueue");
        }

    }

    /*
     * =========================================================================================
     * CONSTRUCTION
     * =========================================================================================
     */

    /**
     * Set the state variables to appropriate values, start the main processing loop, and start the
     * processing loop that will deliver messages to the middleware listeners. Also, start the loop that
     * waits for new clients to connect.
     */
    public TCPIPMiddleware(int readPort, int sendPort)
    {
    	this.listeners = new ArrayList<MiddlewareListener>();
    	
        this.readPort = readPort;
        this.sendPort = sendPort;
        
        redirector = new Redirector();
        redirectorThread = new Thread(redirector);
        redirectorThread.start();
        // start main loop
        new Thread(this).start();
        // start waiting-for-client-Thread
        reader = new Reader();
        readerThread = new Thread(reader);
        readerThread.start();
    }

	@Override
	public void sendData(JsonNode jn) {
		queueSend(jn.toString());
	}

	@Override
	public void addListener(MiddlewareListener ml) {
		this.listeners.add(ml);
	}

    public static void main(String[] args){
    	Middleware m = new TCPIPMiddleware(7500, 7501);
		ObjectMapper om = new ObjectMapper();

    	ObjectNode root = om.createObjectNode().put("keepalive", "ping");
    	
    	while(true){
    		System.out.println("Sending data: "+ root.toString());
    		m.sendData(root);
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}


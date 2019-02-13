package nl.utwente.hmi.middleware.ros;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import ros.RosBridge;

/**
 * This subclass exists only for the purpose of setting the maxIdleTimeout property... 
 * If we don't do this, our connection with the server will be dropped after a while
 * @author davisond
 *
 */
@WebSocket(maxIdleTime=1000000000)
public class NoTimeoutRosBridge extends RosBridge {

}

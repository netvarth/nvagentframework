package com.nv.platform.agentfw.api.impl;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.nv.platform.agentfw.api.exception.AgentFrameworkException;
import com.nv.platform.agentfw.api.worker.EventClientHeartBeat;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

@ClientEndpoint
public class EventClient {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(EventClient.class);
	
	CountDownLatch latch = new CountDownLatch(1);	
	private Session session;

	@OnOpen
	public void onOpen(Session session) {
		logger.info(new NVLogFormatter("Connected to event listener", "session", session.getId()));
	    this.session = session;
	    latch.countDown();
	}

	@OnMessage
	public void onText(String message, Session session) {
		logger.info(new NVLogFormatter("Message received from event listener", "message", message));
		if(message.equalsIgnoreCase("heartbeat")){
			EventClientHeartBeat.heartbeat();
		}
	}

	@OnClose
	public void onClose(CloseReason reason, Session session) {
		logger.info(new NVLogFormatter("Closing a WebSocket", "reason", reason.getReasonPhrase()));
	}

	public CountDownLatch getLatch() {
	    return latch;
	}

	public void sendMessage(String str) throws AgentFrameworkException{
		if (str == null) {
			throw new AgentFrameworkException("NULL message cannot be sent to event listener");
		}
	    try {
	        session.getBasicRemote().sendText(str);
	    } catch (IOException e) {
	    	logger.error(new NVLogFormatter("IO Exception while sending message to event listener", e));
	    	throw new AgentFrameworkException("IO Exception while sending message to event listener", e);
	    } catch (Exception e) {
	    	logger.error(new NVLogFormatter("Exception while sending message to event listener", e));
	    	throw new AgentFrameworkException("Exception while sending message to event listener", e);
	    }
	}
	
	/**
	 * return true if session is open
	 * @return true or false
	 */
	public boolean isConnected() {
		if(session != null) {
			return session.isOpen();
		}
		return false;
	}
	
	
	/**
	 * close session
	 */
	public void close(){
		if (this.session != null){
			try {
				session.close();
				session = null;
			} catch (Exception e) {
				logger.warn(new NVLogFormatter("Exception while closing session", e));
			}
		}
	}
	
}

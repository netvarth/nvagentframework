package com.nv.ynw.agent.rs;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.hibernate.service.spi.InjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nv.agent.service.event.impl.AgentEventHandler;
import com.nv.ynw.agent.rs.configuration.AgentConfig;


@ServerEndpoint(value="/server",configurator=AgentConfig.class)
public class AgentEndpoint {
	private static final Logger log = LoggerFactory.getLogger(AgentEndpoint.class);

	private static final String GUEST_PREFIX = "Guest";
	private static final AtomicInteger connectionIds = new AtomicInteger(0);
	private static final Set<AgentEndpoint> connections =
			new CopyOnWriteArraySet<AgentEndpoint>();

	private final String nickname;
	private Session session;
	private AgentEventHandler agentEventHandler;




	public AgentEndpoint() {
		System.out.println("Agent endpoint started");
		nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
	}


	@OnOpen
	public void start(Session session) {
		this.session= session;
		connections.add(this);
		String message = String.format("* %s %s", nickname, "has joined.");
		broadcast(message);
	}


	@OnClose
	public void end() {
		connections.remove(this);
		String message = String.format("* %s %s",
				nickname, "has disconnected.");
		broadcast(message);
	}


	@OnMessage
	public void incoming(String message) {
		// Never trust the client
		String filteredMessage = String.format("%s: %s",
				nickname, message.toString());
		
		
		try {
			agentEventHandler.handle(message);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		broadcast(filteredMessage);
	}




	@OnError
	public void onError(Throwable t) throws Throwable {
		log.error("Chat Error: " + t.toString(), t);
	}


	private static void broadcast(String msg) {
		for (AgentEndpoint client : connections) {
			try {
				synchronized (client) {
					client.session.getBasicRemote().sendText(msg);
				}
			} catch (IOException e) {
				log.debug("Chat Error: Failed to send message to client", e);
				connections.remove(client);
				try {
					client.session.close();
				} catch (IOException e1) {
					// Ignore
				}
				String message = String.format("* %s %s",
						client.nickname, "has been disconnected.");
				broadcast(message);
			}
		}
	}


	public AgentEventHandler getAgentEventHandler() {
		return agentEventHandler;
	}


	public void setAgentEventHandler(AgentEventHandler agentEventHandler) {
		this.agentEventHandler = agentEventHandler;
	}
	
}

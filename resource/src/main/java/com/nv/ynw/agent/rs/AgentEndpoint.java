/**
 * AgentEndpoint.java
 * 
 * @author Asha Chandran
 */
package com.nv.ynw.agent.rs;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.nv.agent.service.event.impl.AgentEventHandler;
import com.nv.agent.service.event.impl.EventDetails;
import com.nv.platform.event.EventException;
import com.nv.platform.event.EventStatus;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.threadpool.impl.NVThreadPoolManager;
import com.nv.platform.threadpool.impl.TaskExecutionResult;

import java.util.concurrent.Callable;

public class AgentEndpoint extends Endpoint {
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(AgentEndpoint.class);
	private NVThreadPoolManager threadpoolService;
	private AgentEventHandler agentEventHandler;
	EndpointConfig config;

	/**
	 * Constructor to set {@link AgentEventHandler}
	 * @param agentEventHandler {@link AgentEventHandler}
	 */
	public AgentEndpoint(AgentEventHandler agentEventHandler) {
		super();
		this.agentEventHandler = agentEventHandler;
		this.threadpoolService = NVThreadPoolManager.getInstance();
		System.out.println("Agent endpoint started");
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		
	}
	
	/**
	 * Open session
	 * @param session {@link  Session}
	 * @param config {@link  EndpointConfig}
	 */
	@Override
	public void onOpen(final Session session, EndpointConfig config) {
		this.config=config;
		session.addMessageHandler(new MessageHandler.Whole<String>() {

			public void onMessage(String text) {
				try {
					/*EventDetails eventdetails1 = agentEventHandler.getEvent(1);
					EventDetails eventdetails2 = agentEventHandler.getEvent(1);
					EventDetails eventdetails3 = agentEventHandler.getEvent(1);*/
					//get event details from event tbl by event id given by client
					final EventDetails eventdetails = agentEventHandler.getEvent(text);
					/*EventDetails eventdetails4 = agentEventHandler.getEvent(1);
					EventDetails eventdetails5 = agentEventHandler.getEvent(text);
					EventDetails eventdetails6 = agentEventHandler.getEvent(text);
					EventDetails eventdetails7 = agentEventHandler.getEvent(1);*/
					//Submit event processor task to thread 
					threadpoolService.submitTask(new Callable<TaskExecutionResult>() {
						public TaskExecutionResult call() throws Exception {
							eventdetails.getEventProcessor().process(eventdetails.getEvent(),eventdetails.getId());
							return new TaskExecutionResult(true, "SUCCESS");
						}
					});

					try {
						//Send Received message to client
						session.getBasicRemote().sendText(EventStatus.RECEIVED.name());
					} catch (IOException e) {
						logger.error(new NVLogFormatter("Error while sending message to client",e));
					}
				} catch (EventException e) {
					try {
						//Send message to client if client gives invalid data or if there is no entry in event table
						session.getBasicRemote().sendText(EventStatus.INVALID_DATA.name());
					} catch (IOException e1) {
						logger.error(new NVLogFormatter("Error while sending message to client",e1));
					}
				}
			}
		});
	}
}

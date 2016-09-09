/**
 * AgentEndpoint.java
 * 
 * @author Asha Chandran
 */
package com.nv.ynw.agent.rs;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.nv.agent.service.event.AgentEventHandler;
import com.nv.agent.service.event.EventDetails;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.event.ActionStatus;
import com.nv.platform.event.EventException;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.threadpool.impl.NVThreadPoolManager;
import com.nv.platform.threadpool.impl.TaskExecutionResult;

import java.util.List;
import java.util.concurrent.Callable;
/*
 * This is websocket server endpoint
 */
public class AgentEndpoint extends Endpoint {
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(AgentEndpoint.class);
	private NVThreadPoolManager threadpoolService;
	private AgentEventHandler agentEventHandler;
	EndpointConfig config;

	/**
	 * Constructor to set {@link AgentEventHandler}
	 * @param agentEventHandler {@link AgentEventHandler}
	 * @throws UnknownHostException {@link UnknownHostException}
	 * @throws PersistenceException {@link PersistenceException}
	 */
	public AgentEndpoint(AgentEventHandler agentEventHandler) throws PersistenceException, UnknownHostException {
		super();
		this.agentEventHandler = agentEventHandler;
		this.threadpoolService = NVThreadPoolManager.getInstance();
		this.agentEventHandler.updateListenerParams();
		logger.info(new NVLogFormatter("Websocket server endpoint initialization done"));
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		List<Integer> ids = agentEventHandler.getEventIds(session.getId());
		for (Integer id : ids) {
			final EventDetails eventdetails;
			try {
				eventdetails = agentEventHandler.getEvent(id);
				threadpoolService.submitTask(new Callable<TaskExecutionResult>() {
					public TaskExecutionResult call() throws Exception {
						eventdetails.getEventProcessor().process(eventdetails.getEvent(),eventdetails.getId());
						return new TaskExecutionResult(true, "SUCCESS");
					}
				});
			} catch (EventException e) {
				logger.error(new NVLogFormatter("Error while retrieving unprocessed event details at the time of session closed by client",e));
			}

		}
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
					//get event details from event tbl by event id given by client
					final EventDetails eventdetails = agentEventHandler.getEvent(text);
					//Submit event processor task to thread 
					threadpoolService.submitTask(new Callable<TaskExecutionResult>() {
						public TaskExecutionResult call() throws Exception {
							eventdetails.getEventProcessor().process(eventdetails.getEvent(),eventdetails.getId());
							return new TaskExecutionResult(true, "SUCCESS");
						}
					});

					try {
						//Send success message to client
						session.getBasicRemote().sendText(ActionStatus.IN_PROGRESS.name());
					} catch (IOException e) {
						logger.error(new NVLogFormatter("Error while sending message to client",e));
					}
				} catch (EventException e) {
					try {
						//Send message to client if client gives invalid data or if there is no entry in event table for given event id
						session.getBasicRemote().sendText("INVALID DATA");
					} catch (IOException e1) {
						logger.error(new NVLogFormatter("Error while sending message to client",e1));
					}
				}
			}
		});
	}
}

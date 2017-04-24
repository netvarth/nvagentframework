package com.nv.platform.agentfw.api.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import com.nv.platform.agentfw.api.EventPublisherAPI;
import com.nv.platform.agentfw.api.dao.AgentFWEventDB;
import com.nv.platform.agentfw.api.dao.AgentFWListenerParameters;
import com.nv.platform.agentfw.api.dao.AgentFWListenerParametersDB;
import com.nv.platform.agentfw.api.dao.Datasource;
import com.nv.platform.agentfw.api.exception.AgentFrameworkException;
import com.nv.platform.agentfw.api.worker.EventClientHeartBeat;
import com.nv.platform.agentfw.api.worker.EventDeliveryWorker;
import com.nv.platform.event.NvActionLitstener;
import com.nv.platform.event.NvEvent;
import com.nv.platform.event.NvEventActionEntity;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.threadpool.NVThreadpoolFWService;
import com.nv.platform.threadpool.impl.NVThreadPoolManager;
import com.nv.platform.threadpool.impl.TaskExecutionResult;

public class EventPublisherImpl implements EventPublisherAPI, NvActionLitstener{
	
	private static NVLogger logger = NVLoggerAPIFactory.getLogger(EventPublisherImpl.class);
	private EventPublisherImpl() {
		
	}
	
	private static EventPublisherAPI instance = new EventPublisherImpl();
	private static NVThreadpoolFWService threadpoolService;
	
	static {
		threadpoolService = NVThreadPoolManager.getInstance();
	}
	
	public static EventPublisherAPI getInstance() {
		return instance;
	}
	
	private static EventClient socketClient = null;
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	
	public static EventPublisherImpl getEventPublisherImpl(){
		return (EventPublisherImpl)instance;
	}
	
	//--------------------------------------------------------------------------------------//
	
	@Override
	public void init() throws Exception {
		
		// initialize datasource
		//Datasource.initialize();

		// initialize web socket connection
		initWebSocketConnection();
	    
	    this.startWorkers();
	}
	
	//--------------------------------------------------------------------------------------//
	
	
	public void initWebSocketConnection() throws DeploymentException, IOException, URISyntaxException, InterruptedException {
		if(socketClient != null) {
			socketClient.close();
		}
		
		String destination = socketURI(false);
		
		// create the websocket client
		socketClient = new EventClient();
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		
		// connect to websocket server
		try{
			container.connectToServer(socketClient, new URI(destination));
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error creating websocket connection", e));
		}
	    
		Thread.sleep(2000); 
		
	    // wait for the websocket connection to open
		if(socketClient.isConnected()) {
			socketClient.getLatch().await();
		}
	    
	    // start heartbeat
	    EventClientHeartBeat.heartbeat();
	}
	
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 */
	@Override
	public boolean sendEvent(AgentEvent event) throws Exception{
		int eventId = 0;
//		try {
//			saveEvent(event);
//		} catch (Exception e) {
//			logger.error(new NVLogFormatter("Exception saving event. Cannot process further.", e));
//			throw e;
//		}
		try {
			event.setEventId(eventId);
 			deliverEvent(event);
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Exception sending event", e));
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 */
	@Override
	public boolean sendEventAsync(AgentEvent event) throws Exception {
		
		if(event == null) { throw new AgentFrameworkException("Event cannot be NULL"); }
		
		class AsyncClient implements Callable<TaskExecutionResult> {
			private AgentEvent event;
			AsyncClient(AgentEvent newEvent) { this.event = newEvent; }
			@Override
			public TaskExecutionResult call() throws Exception{
				int eventId = 0;
//				try {
//					saveEvent(event);
//				} catch (Exception e) {
//					logger.error(new NVLogFormatter("AsyncClient: Exception saving event. Cannot process further.", e));
//					EventFailureListHolder.getEventsToBeSavedList().add(event);
//					EventFailureListHolder.releaseEventsToBeSavedList();
//					return new TaskExecutionResult(false, "event save failed: "+e.getMessage()); 
//				}
				try {
					event.setEventId(eventId);
		 			deliverEvent(event);
				} catch (Exception e) {
					logger.error(new NVLogFormatter("AsyncClient: Exception sending event", e));
					EventFailureListHolder.getEventsToDeliveredList().add(event);
					EventFailureListHolder.releaseEventsToBeDeliveredList();
					return new TaskExecutionResult(false, "unable to send event: "+e.getMessage()); 
				}
				return new TaskExecutionResult(true, "event delivered successfully"); 
			}	
		}
		
		AsyncClient client = new AsyncClient(event);
		try {
			threadpoolService.submitTask(client);
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Unable to process event", e));
			throw e;
		}
		
		return true;
	}
	
	//--------------------------------------------------------------------------------------//

//	public static void main(String[] args) {
//		(new EventAPIImpl()).sendEvent(new AgentEvent("UPDATE_SCHEDULE", null));
//	}
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 * @param secure
	 * @return
	 */
	private String socketURI(boolean secure) {
		AgentFWListenerParameters params = AgentFWListenerParametersDB.dbSelect();
		if (params == null) {
			params = AgentFWListenerParameters.mockParams();
		}
		if(secure) {
			return "wss://"+ params.getHost() +":" +params.getPort()+ "/agent/server";
		} else {
			return "ws://"+ params.getHost() +":" +params.getPort()+ "/agent/server";
		}
	}
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 * @param event {@link AgentEvent} 
	 * @return event id 
	 * @throws SQLException {@link SQLException}
	 */
	public static int saveEvent(AgentEvent event) throws SQLException {
		return AgentFWEventDB.dbInsert(event.getEventType().name(), event.getEventData()); 
	}
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 * @param event {@link AgentEvent} 
	 * @throws Exception {@link Exception}
	 */
	public static void deliverEvent(AgentEvent event) throws Exception{
	    if (socketClient != null) {
	    	socketClient.sendMessage("{\"eventID\":"+event.getEventId()+"}");
	    } else {
	    	throw new AgentFrameworkException("Web socket connection not extablished in client");
	    }
	}
	
	//--------------------------------------------------------------------------------------//
	
	/**
	 * 
	 * @throws Exception {@link Exception}
	 */
	public void checkConnection() throws Exception{
	    if (socketClient != null) {
	    	socketClient.sendMessage("heartbeat");
	    } else {
	    	throw new AgentFrameworkException("Web socket connection not established in client");
	    }
	}

	//--------------------------------------------------------------------------------------//
	
	private void startWorkers() {
		try {
			Thread heartbeatThread = new Thread(new EventClientHeartBeat());
			heartbeatThread.start();
			
//			Thread dbSaveThread = new Thread(new EventDBSaveWorker());
//			dbSaveThread.start();
			
			Thread deliveryThread = new Thread(new EventDeliveryWorker());
			deliveryThread.start();
			
			logger.info(new NVLogFormatter("Successfully started worker threads")); 
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Exception starting worker threads", e)); 
		}
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.event.NvEventListener#OnEvent(com.nv.platform.event.NvEventTaskEntity)
	 */
	@Override
	public void performAction(NvEvent eventAction) {
		
		
	}


	/* (non-Javadoc)
	 * @see com.nv.platform.event.NvActionLitstener#transactional(java.lang.Object)
	 */
	@Override
	public void transactional(Object ...obj) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see com.nv.platform.event.NvActionLitstener#notify(com.nv.platform.event.NvEventActionEntity)
	 */
	@Override
	public void notify(NvEventActionEntity eventAction) {
		// TODO Auto-generated method stub
		
	}
	
}

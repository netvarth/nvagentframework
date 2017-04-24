package com.nv.platform.agentfw.api;

import com.nv.platform.agentfw.api.impl.AgentEvent;

public interface EventPublisherAPI {
	
	/**
	 * Initialize the web socket
	 * @throws Exception {@link Exception}
	 */
	public void init() throws Exception;

	/**
	 * This API sends an Event to AgentEventListener synchronously.
	 * It first persists the data in AgentEvent and then makes a websocket call to AgentEventListener to deliver the event.
	 * It responds with TRUE if it is able to successfully deliver the event.
	 * @param event AgentEvent
	 * @return TRUE if event is successfully delivered
	 * @throws Exception if unable to save or send event
	 */
	public boolean sendEvent(AgentEvent event) throws Exception;
	
	
	/**
	 * This API sends an Event to AgentEventListener asynchronously.
	 * This API starts a thread to persist the event and send the event to the AgentEventListener.
	 * The thread persists the data in AgentEvent and then makes a websocket call to AgentEventListener to deliver the event.
	 * @param event AgentEvent
	 * @return TRUE
	 * @throws Exception if unable to save or send event
	 */
	public boolean sendEventAsync(AgentEvent event) throws Exception;
}

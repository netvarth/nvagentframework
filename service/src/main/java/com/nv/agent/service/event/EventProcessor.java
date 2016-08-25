/**
 *  EventProcessor.java
 *  @author Asha Chandran
 */
package com.nv.agent.service.event;

import com.nv.platform.event.EventException;
import com.nv.platform.event.NvEvent;

public interface EventProcessor {
	
	/**
	 * Process an event
	 * @param event {@link NvEvent}
	 * @param eventId event id
	 * @throws EventException {@link EventException}
	 */
	public void process(NvEvent event,int eventId) throws EventException;
}

/**
 * 
 */
package com.nv.agent.service.event;

import com.nv.platform.event.ActionType;
import com.nv.platform.event.NvEvent;

/**
 * Event details 
 * @author NetvarthPC
 *
 */
public class EventDetails {
	/**
	 * Event id
	 */
	private int id;
	/**
	 * Event 
	 */
	private NvEvent event;
	/**
	 * Event processor
	 */
	private EventProcessor eventProcessor;
	
	/**
	 * Constructor to set event id,event,event processor
	 * @param id event id
	 * @param event {@link NvEvent}
	 * @param eventProcessor {@link EventProcessor}
	 */
	public EventDetails(int id,NvEvent event, EventProcessor eventProcessor) {
		super();
		this.id = id;
		this.event = event;
		this.eventProcessor =eventProcessor;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the event
	 */
	public NvEvent getEvent() {
		return event;
	}

	/**
	 * @return the eventProcessor
	 */
	public EventProcessor getEventProcessor() {
		return eventProcessor;
	}


}

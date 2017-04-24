package com.nv.platform.agentfw.api.worker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nv.platform.agentfw.api.impl.AgentEvent;
import com.nv.platform.agentfw.api.impl.EventFailureListHolder;
import com.nv.platform.agentfw.api.impl.EventPublisherImpl;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class EventDBSaveWorker implements Runnable {

	NVLogger logger = NVLoggerAPIFactory.getLogger(EventDBSaveWorker.class);
	
	@Override
	public void run() {
		while (true) {
			try{
				// get the events to be saved
				List<AgentEvent> eventsToBeSaved = EventFailureListHolder.getEventsToBeSavedList();
				if (!eventsToBeSaved.isEmpty()) {
					AgentEvent[] events = new AgentEvent[eventsToBeSaved.size()];
					Object[] arr = eventsToBeSaved.toArray();
					eventsToBeSaved.clear();
					EventFailureListHolder.releaseEventsToBeSavedList();
					
					// save the events
					List<AgentEvent> failedEvents = new ArrayList<AgentEvent>();
					events = Arrays.copyOf(arr, arr.length, AgentEvent[].class);
					
					for(AgentEvent event : events) {
						try {
							EventPublisherImpl.saveEvent(event);
						} catch (SQLException e) {
							logger.error(new NVLogFormatter("Error saving event", e));
							failedEvents.add(event);
						}
					}
					
					if (!failedEvents.isEmpty()) {
						logger.warn(new NVLogFormatter("Events failed while adding to DB", "size", failedEvents.size()));
						List<AgentEvent> failedEventsToBeSaved = EventFailureListHolder.getEventsToBeSavedList();
						for (AgentEvent event: failedEvents) {
							failedEventsToBeSaved.add(event);
						}
						EventFailureListHolder.releaseEventsToBeSavedList();
					}
				} else {
					EventFailureListHolder.releaseEventsToBeSavedList();
				}
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error while processing DB failed events", e));
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {} 
		}
	}

}

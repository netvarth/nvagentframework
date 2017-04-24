package com.nv.platform.agentfw.api.worker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nv.platform.agentfw.api.impl.AgentEvent;

import com.nv.platform.agentfw.api.impl.EventFailureListHolder;
import com.nv.platform.agentfw.api.impl.EventPublisherImpl;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class EventDeliveryWorker implements Runnable {
	
	NVLogger logger = NVLoggerAPIFactory.getLogger(EventDeliveryWorker.class);

	@Override
	public void run() {
		while (true) {
			try{
				// get the events to be saved
				List<AgentEvent> eventsToBeDelivered = EventFailureListHolder.getEventsToDeliveredList();
				if (!eventsToBeDelivered.isEmpty()) {
					AgentEvent[] events = new AgentEvent[eventsToBeDelivered.size()];
					Object[] arr = eventsToBeDelivered.toArray();
					eventsToBeDelivered.clear();
					EventFailureListHolder.releaseEventsToBeDeliveredList();
					
					// save the events
					List<AgentEvent> failedEvents = new ArrayList<AgentEvent>();
					events = Arrays.copyOf(arr, arr.length, AgentEvent[].class);
					
					for(AgentEvent event : events) {
						try {
							EventPublisherImpl.deliverEvent(event);
						} catch (Exception e) {
							logger.error(new NVLogFormatter("Error delivering event", e));
							failedEvents.add(event);
						}
					}
					
					if (!failedEvents.isEmpty()) {
						logger.warn(new NVLogFormatter("Events failed while delivering to listener", "size", failedEvents.size()));
						List<AgentEvent> failedEventsToBeSaved = EventFailureListHolder.getEventsToDeliveredList();
						for (AgentEvent event: failedEvents) {
							failedEventsToBeSaved.add(event);
						}
						EventFailureListHolder.releaseEventsToBeDeliveredList();
					}
				} else {
					EventFailureListHolder.releaseEventsToBeDeliveredList();
				}
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error while processing Delivery failed events", e));
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {} 
		}
	}

}

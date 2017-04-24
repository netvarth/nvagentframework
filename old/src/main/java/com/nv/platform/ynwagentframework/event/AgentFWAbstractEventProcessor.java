package com.nv.platform.ynwagentframework.event;

import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.event.process.CreateAlertEventProcessor;
import com.nv.platform.ynwagentframework.event.process.UpdateScheduleEventProcessor;

public abstract class AgentFWAbstractEventProcessor implements AgentFWEventProcessor{

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWAbstractEventProcessor.class);
	
	//----------------------------------------------------------------------------------------------//
	
	/**
	 * Get Processor for an event
	 * @param event
	 * @return
	 */
	public static AgentFWEventProcessor getProcessor(final AgentEvent event) {
		if (event == null) {
			logger.error("NULL event. Cannot process.");
			return null;
		}
		
		switch (event.getEventType()) {
			case UPDATE_SCHEDULE:
				return new UpdateScheduleEventProcessor(event);
			case CREATE_ALERT:
				return new CreateAlertEventProcessor(event);
			default:
				logger.error("Unknown event type["+event.getEventType()+"]");
				return null;
		}
	}
	
	//----------------------------------------------------------------------------------------------//
	
}

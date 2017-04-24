package com.nv.platform.ynwagentframework.event.process;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.dao.AgentFWEventDB;
import com.nv.platform.ynwagentframework.event.AgentEvent;
import com.nv.platform.ynwagentframework.event.AgentFWAbstractEventProcessor;

public class UpdateScheduleEventProcessor extends AgentFWAbstractEventProcessor {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(UpdateScheduleEventProcessor.class);

	private AgentEvent event;
	
	public UpdateScheduleEventProcessor(final AgentEvent event) {
		this.event = event;
	}
	
	public void process() {
		logger.info("received event"); 
		event = AgentFWEventDB.dbSelect(event.getEventId(), event.getEventType().name());
		
		logger.info(new NVLogFormatter("event retrieved from DB", "event", event));

		// Process the event
		
		logger.info("Succesfully processed event"); 
	}
}

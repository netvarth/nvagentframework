package com.nv.platform.ynwagentframework.event.process;

import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.event.AgentEvent;
import com.nv.platform.ynwagentframework.event.AgentFWAbstractEventProcessor;

public class CreateAlertEventProcessor extends AgentFWAbstractEventProcessor {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(CreateAlertEventProcessor.class);

	private AgentEvent event;
	
	public CreateAlertEventProcessor(final AgentEvent event) {
		this.event = event;
	}
	
	public void process() {
		logger.info("Successfully processed"); 
	}
}
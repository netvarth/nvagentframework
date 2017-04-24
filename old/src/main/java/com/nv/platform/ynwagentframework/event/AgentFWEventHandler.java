package com.nv.platform.ynwagentframework.event;

import java.util.concurrent.Callable;

import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.threadpool.impl.TaskExecutionResult;


public class AgentFWEventHandler implements Callable<TaskExecutionResult> {
	
	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWEventHandler.class);
	private AgentEvent event;

	public AgentFWEventHandler(AgentEvent event) {
		System.out.println("Handler created. Event: " +event); 
		this.event = event;
	}
	
	@Override
	public TaskExecutionResult call() throws Exception {
		System.out.println("Handler called"); 
		AgentFWEventProcessor processor = AgentFWAbstractEventProcessor.getProcessor(event);
		processor.process();
		return new TaskExecutionResult(true, "success"); 
	}
	

}

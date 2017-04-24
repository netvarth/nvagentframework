package com.nv.platform.ynwagentframework;



import org.quartz.Job;

import com.nv.platform.ynwagentframework.exception.AgentException;

public interface BaseAgent extends Job{

	public void init() throws AgentException;
	
	public void reloadConfig() throws AgentException;
	
	public void shutdown() throws AgentException;
	
	//public void processResult(AgentExecutionResult result) throws AgentException;
	
}

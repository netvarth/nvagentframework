package com.nv.platform.ynwagentframework.impl;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.BaseAgent;
import com.nv.platform.ynwagentframework.exception.AgentException;

public abstract class AbstractBaseAgent implements BaseAgent {

	NVLogger logger = NVLoggerAPIFactory.getLogger(AbstractBaseAgent.class);
	
	public void init() throws AgentException {
		logger.info(new NVLogFormatter("Nothing to intialize"));
	}

	public void reloadConfig() throws AgentException {
		logger.info(new NVLogFormatter("Nothing to reload"));
	}

	public void shutdown() throws AgentException {
		logger.info(new NVLogFormatter("Shutting down. No cleanUp required"));
	}

//	public void processResult(AgentExecutionResult result) throws AgentException {
//		logger.info(new NVLogFormatter("Agent Execution Result", "result", result.toString()));
//	}

}

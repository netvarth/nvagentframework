package com.nv.platform.ynwagentframework.impl;

import java.util.List;

import org.quartz.SchedulerException;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.exception.AgentFrameworkException;

public class AgentManager {
	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentManager.class);

	private static AgentScheduler scheduler;
	private static AgentManager agentMgr;

	private AgentManager() throws AgentFrameworkException{
		scheduler = new AgentScheduler();
	}

	public static synchronized AgentManager getManager() {
		if(agentMgr == null) {
			try {
				agentMgr = new AgentManager();
			} catch (AgentFrameworkException e) {
				logger.error(new NVLogFormatter("Error initializing AgentManager", e));
			}
		}
		return agentMgr;
	}

	public Object clone() {
		logger.error("Clone not allowed for AgentManager");
		return null;
	}

	public void scheduleAgent(String key, String jobClazz, String schedule) throws AgentFrameworkException {
		Class clazz;
		try {
			clazz = Class.forName(jobClazz);
		} catch (ClassNotFoundException e) {
			logger.error(new NVLogFormatter("Unable to find Agent class", "agent", jobClazz));
			throw new AgentFrameworkException("Unable to find Agent class: " +jobClazz, e);
		}
		scheduler.scheduleJob(key, clazz, schedule); 
	}

	public void shutdownScheduler() throws AgentFrameworkException {
		scheduler.shutdown();
	}

	public boolean deleteJob(String key) throws AgentFrameworkException {
		boolean returnValue = false;
		try {
			returnValue = scheduler.deleteJob(key);
		} catch(SchedulerException se) {
			throw new AgentFrameworkException("Error deleting a job", se);
		}
		return returnValue;
	}

	public void deleteAllJobs() throws AgentFrameworkException {
		try {
			scheduler.deleteAllJobs();
		} catch(SchedulerException se) {
			throw new AgentFrameworkException("Error deleting all jobs", se);
		}
	}


	public List<ScheduledAgentJob> getRunningJobs() {
		return scheduler.getRunningJobs();
	}
}

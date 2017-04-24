package com.nv.platform.ynwagentframework.service;


import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.dao.AgentFrameworkDB;
import com.nv.platform.ynwagentframework.dao.Datasource;
import com.nv.platform.ynwagentframework.impl.AgentConfig;
import com.nv.platform.ynwagentframework.impl.AgentManager;

public class YNWAgentFrameworkService {
	NVLogger logger = NVLoggerAPIFactory.getLogger(YNWAgentFrameworkService.class);
	private static AgentManager manager = AgentManager.getManager();
	
	private static YNWAgentFrameworkService service = null;
	
	public static synchronized YNWAgentFrameworkService getAgentFW() {
		if(service == null) {
			service = new YNWAgentFrameworkService();
		}
		return service;
	}
	
	public Object clone() {
		logger.error("Cannot clone YNWAgentFrameworkService");
		return null; 
	}
	
	/**
	 * initialize YNWAgentFrameworkService
	 */
	public void initialize() {
		
		try {
			Datasource.initialize();
			AgentConfig[] configs = AgentFrameworkDB.dbSelect();
			for (AgentConfig config : configs) {
				manager.scheduleAgent(config.getJobKey(), config.getJobClass(), config.getJobSchedule());
				logger.info(new NVLogFormatter("Agent successfully scheduled", "agentKey", config.getJobKey()));
			}
			logger.info(new NVLogFormatter("YNWAgentFrameworkService started and all agents scheduled successfully"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(new NVLogFormatter("Error initializing AgentFramework","","", e)); 
			
		}
	}
	
	/**
	 * shutdown
	 */
	public void shutdown() {
		try {
			manager.deleteAllJobs();
			manager.shutdownScheduler();
			Datasource.shutdown();
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error during shutdown of AgentFramework", e)); 
		}
	}
	
	
	/**
	 * start YNWAgentFrameworkService
	 * @param args
	 */
	public static void main(String[] args) {
		YNWAgentFrameworkService.getAgentFW().initialize();
	}
}

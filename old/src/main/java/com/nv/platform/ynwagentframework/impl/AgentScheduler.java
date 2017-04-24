package com.nv.platform.ynwagentframework.impl;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.exception.AgentFrameworkException;

public class AgentScheduler {

	NVLogger logger = NVLoggerAPIFactory.getLogger(AgentScheduler.class);
	
	private static class SchedulerHolder {
		private static final SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
		private static Scheduler scheduler = null;
		
		protected static void init() throws AgentFrameworkException {
			try {
				if (scheduler == null) {
					scheduler = schedulerFactory.getScheduler();
					scheduler.start();
				}
			} catch (SchedulerException e) {
				throw new AgentFrameworkException("Error initializing Scheduler", e);
			}
		}
		
		protected static void shutdown() throws AgentFrameworkException {
			try{
				if (scheduler != null) {
					scheduler.shutdown();
				}
			} catch (SchedulerException e) {
				throw new AgentFrameworkException("Error during Scheduler shutdown", e);
			}
		}
	}

	
	/**
	 * Initializes YNW Scheduler
	 * @throws AgentFrameworkException
	 */
	protected AgentScheduler() throws AgentFrameworkException {
		SchedulerHolder.init();
		logger.info(new NVLogFormatter("YNW Scheduler initialized"));
	}
	
	
	/**
	 * Shutdown YNW Scheduler
	 * @throws AgentFrameworkException
	 */
	protected void shutdown() throws AgentFrameworkException {
		SchedulerHolder.shutdown();
		logger.info(new NVLogFormatter("YNW Scheduler shutdown complete"));
	}
	
	/**
	 * 
	 * @param key
	 * @param clazz
	 * @param schedule
	 * @throws AgentFrameworkException
	 * @throws SchedulerException
	 */
	protected void scheduleJob(String key, Class<? extends Job> clazz, String schedule) throws AgentFrameworkException {
		JobDetail job = JobBuilder.newJob(clazz)
						.withIdentity(JobKey.jobKey(key))
						.build();

		Trigger trigger = TriggerBuilder.newTrigger()
						.startNow()
						.withSchedule(CronScheduleBuilder.cronSchedule(schedule)).build();
		try {
			SchedulerHolder.scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException se) {
			se.printStackTrace();
			logger.error(new NVLogFormatter("Exception while scheduling Job", "job", clazz.getName(), se));
			throw new AgentFrameworkException("Exception while scheduling Job", se);
		}
	}
	
	/*
	private String getJobKey(String group) {
		long jobCreateTime = System.currentTimeMillis();
		String jobKey = group + "-" + jobCreateTime;
		return jobKey;
	}*/
	
	
	/**
	 * 
	 * @return
	 */
	protected List<ScheduledAgentJob> getRunningJobs() {
		List<ScheduledAgentJob> agentJobs = new ArrayList<ScheduledAgentJob>();
		try {
			List<JobExecutionContext> jecList = SchedulerHolder.scheduler.getCurrentlyExecutingJobs();
			for(JobExecutionContext jec : jecList) {
				new ScheduledAgentJob(jec.getJobDetail().getKey().getName(), 
							 jec.getFireTime(),
							 jec.getFireInstanceId());
			}
			
		} catch (SchedulerException se) {
			logger.error(new NVLogFormatter("Error getting list of Running Jobs",se));
		}
		
		return agentJobs;
	}
	
	
	/**
	 * Delete a single job. Once deleted, the job will not get scheduled again.
	 * @param key
	 * @return
	 * @throws SchedulerException
	 */
	protected boolean deleteJob(String key) throws SchedulerException {
		boolean returnVal = false;
		try { 
			returnVal = SchedulerHolder.scheduler.deleteJob(JobKey.jobKey(key));
		} catch (SchedulerException se) {
			logger.error(new NVLogFormatter("Error deleting job \"" +key+ "\"",se)); 
			throw se;
		}
		return returnVal;
	}
	
	
	/**
	 * Deletes all currently executing Jobs
	 * @throws SchedulerException
	 */
	protected void deleteAllJobs() throws SchedulerException {
		List<JobKey> jobKeyList = new ArrayList<JobKey>();
		try {
			List<JobExecutionContext> jecList = SchedulerHolder.scheduler.getCurrentlyExecutingJobs();
			for(JobExecutionContext jec : jecList) {
				jobKeyList.add(jec.getJobDetail().getKey());
			}
			SchedulerHolder.scheduler.deleteJobs(jobKeyList);
		} catch (SchedulerException se) {
			logger.error(new NVLogFormatter("Error while deleting job",se));
			throw se;
		}
	}
	
}

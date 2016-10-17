/**
 * WaitlistUserAgent.java
 * 
 * @author Asha Chandran
 */
package com.nv.agent.service.useragents;


import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This agent will do the task of removing current waitlist,delay,adjusting token and waiting time
 */
public class WaitlistUserAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(WaitlistUserAgent.class);

	private WriteDao writeDao;
	private static final String truncate_waitlist_cache= "truncate table wl_cache_tbl";
	private static final String update_waitlist_time= "update acct_wl_stats_tbl set acct_waiting_time=0,queue_size=0,waiting_updated_time='00:00:00',wl_recalculated_time='00:00:00',delay_time=0,delay_updated_time='00:00:00'";
	private static final String update_token= "update sequence_generator_tbl set current_val=0 where table_name='WaitlistCacheEntity'";
	
	/**
	 * Execute job
	 * @param arg0 {@link JobExecutionContext}
	 */
	@Override
	@Transactional(value="write",readOnly=false)
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("WaitlistManagerAgent executing at "+new Date());
		try {
			writeDao.executeUpdate(truncate_waitlist_cache);
			logger.info("WaitlistManagerAgent waitlist_cache executing at "+new Date());
			writeDao.executeUpdate(update_waitlist_time);
			logger.info("WaitlistManagerAgent waitlist_time executing at "+new Date());
			writeDao.executeUpdate(update_token);
			logger.info("WaitlistManagerAgent update_token executing at "+new Date());
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while removing waitlist,delay,adjusting token and waiting time", e));
		}
	}


	/**
	 * @param writeDao the writeDao to set
	 */
	public void setWriteDao(WriteDao writeDao) {
		this.writeDao = writeDao;
	}
}
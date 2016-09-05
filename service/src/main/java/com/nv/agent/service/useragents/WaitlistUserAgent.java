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
	private static final String update_waitlist_time= "update acct_wl_stats_tbl set last_recalcuted_time='00:00:00',updated_time='00:00:00',waiting_time=0";
	private static final String update_token= "update sequence_generator_tbl set current_value=0 where table_name='WaitlistCacheEntity'";
	
	/**
	 * Execute job
	 * @param arg0 {@link JobExecutionContext}
	 */
	@Override
	@Transactional(value="write")
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("WaitlistManagerAgent executing at "+new Date());
		try {
			writeDao.executeUpdate(truncate_waitlist_cache);
			writeDao.executeUpdate(update_waitlist_time);
			writeDao.executeUpdate(update_token);
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while removing waitlist,delay,adjusting token and waiting time   ", e));
		}
	}


	/**
	 * @param writeDao the writeDao to set
	 */
	public void setWriteDao(WriteDao writeDao) {
		this.writeDao = writeDao;
	}
}
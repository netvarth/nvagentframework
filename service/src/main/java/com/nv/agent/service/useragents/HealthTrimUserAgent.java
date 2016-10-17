/**
 * WaitlistUserAgent.java
 * 
 * @author Asha Chandran
 */
package com.nv.agent.service.useragents;


import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.health.HealthMonitorEntity;
import com.nv.platform.base.util.DateUtil;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This agent will do the task of removing health records those are older than 5 days
 */
public class HealthTrimUserAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(HealthTrimUserAgent.class);

	private WriteDao writeDao;
	private ReadDao readDao;
	private static final String get_health= "select health.id from HealthMonitorEntity as health where health.createdDate<:param1";
		
	/**
	 * Execute job
	 * @param arg0 {@link JobExecutionContext}
	 */
	@Override
	@Transactional(value="write",readOnly=false)
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("HealthTrimUserAgent executing at "+new Date());
		try {
			//keep records of health only for 5 days, remove older records 
			List<Integer> healthIds = readDao.executeQuery(Integer.class, get_health,DateUtil.subtractDays(new Date(), 4));
			for (Integer id : healthIds) {
				writeDao.delete(HealthMonitorEntity.class, id);
			}
			
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while removing 5 days older health", e));
		}
	}


	/**
	 * @param writeDao the writeDao to set
	 */
	public void setWriteDao(WriteDao writeDao) {
		this.writeDao = writeDao;
	}


	/**
	 * @param readDao the readDao to set
	 */
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}
}
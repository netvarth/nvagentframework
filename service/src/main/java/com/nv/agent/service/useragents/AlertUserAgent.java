/**
 * AlertPurgeAgent.java
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

import com.nv.platform.alert.AlertEntity;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.util.DateUtil;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This agent will do the task of purging old entries of alerts
 */
public class AlertUserAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(AlertUserAgent.class);

	private ReadDao readDao;
	private WriteDao writeDao;
	
	private static final String get_old_alerts= "select alert.id from AlertEntity as alert where alert.endDate<:param1";
	
	/**
	 * Execute job
	 * @param arg0 {@link JobExecutionContext}
	 */
	@Override
	@Transactional(value="write")
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Alert purging agent executing at "+new Date());
		try {
			List<Integer> ids = readDao.executeQuery(Integer.class, get_old_alerts, DateUtil.getCurrentDateWithoutTime());
			for (Integer id : ids) {
				writeDao.delete(AlertEntity.class, id);
			}
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while purging old entries of alerts", e));
		}
	}

	/**
	 * @param readDao the readDao to set
	 */
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}

	/**
	 * @param writeDao the writeDao to set
	 */
	public void setWriteDao(WriteDao writeDao) {
		this.writeDao = writeDao;
	}
}
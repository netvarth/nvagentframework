package com.nv.agent.service.impl;


import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nv.platform.alert.AlertEntity;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.util.DateUtil;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.agent.repository.BaseDao;

/**
 * This agent will do the task of purging old entries of alerts
 * @author Asha
 */
public class AlertPurgeAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(AlertPurgeAgent.class);

	private ReadDao readDao;
	private BaseDao baseDao;
	
	private static final String get_old_alerts= "select alert.id from AlertEntity as alert where alert.endDate<:param1";
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("AlertPurgeAgent-"+new Date());
		try {
			List<Integer> ids = readDao.executeQuery(Integer.class, get_old_alerts, DateUtil.getCurrentDateWithoutTime());
			for (Integer id : ids) {
				baseDao.deleteWithId(AlertEntity.class, id);
			}
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while purging old entries of alerts", e));
		}
		

	}
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

}
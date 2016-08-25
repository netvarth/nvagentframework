package com.nv.agent.service.impl;


import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nv.platform.aaa.login.LoginHistoryEntity;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.util.DateUtil;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.agent.repository.BaseDao;

/**
 * This agent will do the task of purging old entries of login history
 */
public class LoginHistoryPurgeAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(LoginHistoryPurgeAgent.class);

	private ReadDao readDao;
	private BaseDao baseDao;
	
	private static final String get_old_login_history= "select history.id from LoginHistoryEntity as history where history.loggedIn<=:param1";
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Login history purging agent executing at "+new Date());
		try {
			List<Integer> loginHistoryIds = readDao.executeQuery(Integer.class, get_old_login_history, DateUtil.getStartDateOfThisMonth());
			for (Integer historyId : loginHistoryIds) {
				baseDao.deleteWithId(LoginHistoryEntity.class, historyId);
			}
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while purging old entries of login history", e));
		}
		

	}
	/**
	 * @param readDao the readDao to set
	 */
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}
	/**
	 * @param baseDao the baseDao to set
	 */
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
}
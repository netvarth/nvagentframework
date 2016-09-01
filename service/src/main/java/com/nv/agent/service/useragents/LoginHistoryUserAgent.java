package com.nv.agent.service.useragents;


import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.aaa.login.LoginHistoryEntity;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.util.DateUtil;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This agent will do the task of purging old entries of login history
 */
public class LoginHistoryUserAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(LoginHistoryUserAgent.class);

	private ReadDao readDao;
	private WriteDao writeDao;
	
	private static final String get_old_login_history= "select history.id from LoginHistoryEntity as history where history.loggedIn<=:param1";
	
	@Override
	@Transactional(value="write")
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Login history purging agent executing at "+new Date());
		try {
			List<Integer> loginHistoryIds = readDao.executeQuery(Integer.class, get_old_login_history, DateUtil.getStartDateOfThisMonth());
			System.out.println(DateUtil.getStartDateOfThisMonth());
			for (Integer historyId : loginHistoryIds) {
				System.out.println("historyId:"+historyId);
				writeDao.delete(LoginHistoryEntity.class, historyId);
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
	 * @param writeDao the writeDao to set
	 */
	public void setWriteDao(WriteDao writeDao) {
		this.writeDao = writeDao;
	}
	
	
}
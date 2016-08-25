/**
 * CacheManagerAgent.java
 * 
 * @author Asha
 */
package com.nv.agent.service.impl;


import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.cache.CacheException;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.ynw.cache.availability.AvailabilityCacheManager;

/**
 * Cache Manager agent to trim and expand provider schedule cache
 *
 */
public class CacheManagerAgent extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(CacheManagerAgent.class);
	
	private AvailabilityCacheManager availabilityCache;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("Cache manager agent executing at "+new Date());
		try {
			availabilityCache.trimAndExpand();
		} catch (CacheException | PersistenceException e) {
			logger.error(new NVLogFormatter("Error while cache manager trim and expand provider schedule cache", e));
		}
	}

	/**
	 * @param availabilityCache the availabilityCache to set
	 */
	public void setAvailabilityCache(AvailabilityCacheManager availabilityCache) {
		this.availabilityCache = availabilityCache;
	}
}
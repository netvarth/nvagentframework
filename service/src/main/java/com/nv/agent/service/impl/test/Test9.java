
package com.nv.agent.service.impl.test;


import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This is test agent
 */
public class Test9 extends QuartzJobBean{
	NVLogger logger = NVLoggerAPIFactory.getLogger(Test9.class);
	
	
	/**
	 * Execute job
	 * @param arg0 {@link JobExecutionContext}
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Executing  "+this.getClass().getName()+" at "+new Date());
	}

}
package com.nv.agent.service.impl;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class UserAgent2 extends QuartzJobBean{
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("Executing user agent 2 "+new Date());
	}
}

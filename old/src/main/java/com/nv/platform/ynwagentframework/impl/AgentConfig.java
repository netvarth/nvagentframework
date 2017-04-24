package com.nv.platform.ynwagentframework.impl;

import java.sql.Date;

public class AgentConfig {

	private final int jobId;
	private final String jobKey;
	private final String jobClass;
	private final String jobSchedule;
	private final Date createDate;
	private final Date modifyDate;
	
	public AgentConfig(int jobId, String jobKey, String jobClass, String jobSchedule, Date createDate, Date modifyDate){
		this.jobId = jobId;
		this.jobKey = jobKey;
		this.jobClass = jobClass;
		this.jobSchedule = jobSchedule;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public int getJobId() {
		return jobId;
	}

	public String getJobKey() {
		return jobKey;
	}

	public String getJobClass() {
		return jobClass;
	}

	public String getJobSchedule() {
		return jobSchedule;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	@Override
	public String toString() {
		return "AgentJobConfig [jobId=" + jobId + ", jobKey=" + jobKey
				+ ", jobClass=" + jobClass + ", jobSchedule=" + jobSchedule
				+ ", createDate=" + createDate + ", modifyDate=" + modifyDate
				+ "]";
	}
	
	
}

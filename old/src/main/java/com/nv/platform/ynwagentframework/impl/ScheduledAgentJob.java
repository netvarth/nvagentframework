package com.nv.platform.ynwagentframework.impl;

import java.util.Date;

public class ScheduledAgentJob {

	private final String name;
	private final Date fireTime;
	private final String fireInstanceId;
	
	public ScheduledAgentJob(String key, Date fireTime, String fireInstanceId) {
		this.name = key;
		this.fireTime = fireTime;
		this.fireInstanceId = fireInstanceId;
	}
	
	public String getKey() {
		return this.name;
	}
	
	public Date getCreateDate() {
		return this.fireTime;
	}
	
	public String getFireInstanceId() {
		return this.fireInstanceId;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{AgentJob = ");
		sb.append("[name : " +this.name+"]");
		sb.append(", [fireTime : " +this.fireTime+"]");
		sb.append(", [fireInstanceId : " +this.fireInstanceId+"]");
		return sb.toString();
	}
}

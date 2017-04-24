package com.nv.platform.ynwagentframework.exception;

public class AgentFrameworkException extends Exception{
	
	private static final long serialVersionUID = 1L; 

	public AgentFrameworkException(String message, Exception e) {
		super(message, e);
	}
	
	public AgentFrameworkException(String message) {
		super(message);
	}
}
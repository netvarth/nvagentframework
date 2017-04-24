package com.nv.platform.ynwagentframework.exception;

public class AgentException extends Exception{
	
	private static final long serialVersionUID = 1L; 

	public AgentException(String message, Exception e) {
		super(message, e);
	}
	
	public AgentException(String message) {
		super(message);
	}
}

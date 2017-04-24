package com.nv.platform.ynwagentframework.impl;

public class AgentFWListenerParameters {

	private ListernerType type;
	private String protocol;
	private boolean secure;
	private String host;
	private int port;
	
	
	public AgentFWListenerParameters(String type, String protocol, String secure, String host, int port) {
		this.type = ListernerType.getType(type);
		this.protocol = protocol;
		this.secure = secure.equalsIgnoreCase("true") ? true : false;
		this.host = host;
		this.port = port;
	}
	
	
	public ListernerType getType() {
		return type;
	}

	public String getProtocol() {
		return protocol;
	}

	public boolean isSecure() {
		return secure;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	
	public enum ListernerType {
		EVENT;
		
		/**
		 * 
		 * @param type
		 * @return
		 */
		public static ListernerType getType(String type) {
			if (type == null) { return null; }
			for (ListernerType lType : ListernerType.values()) {
				if (type.equalsIgnoreCase(lType.name())) {
					return lType;
				}
			}
			return null;
		}
	}
}

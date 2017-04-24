package com.nv.platform.agentfw.api.dao;

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

	public static AgentFWListenerParameters mockParams(){
		return new AgentFWListenerParameters("event", "websocket", "false", "localhost", 8080);
	}
	
	public enum ListernerType {
		EVENT;
		
		/**
		 * 
		 * @param type type
		 * @return ListernerType
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

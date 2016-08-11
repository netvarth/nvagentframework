package com.nv.ynw.agent.rs.configuration;


import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import com.nv.ynw.agent.rs.AgentEndpoint;


public class AgentConfig  implements ServerApplicationConfig {

	
	
	 public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned){
      Set<Class<?>> s = new HashSet<Class<?>>();
       s.add(AgentEndpoint.class);
       return s;
	 }  
   
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
	


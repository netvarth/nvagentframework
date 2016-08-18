package com.nv.ynw.agent.rs.configuration;

import com.nv.agent.service.event.impl.AgentEventHandler;
import com.nv.ynw.agent.rs.AgentEndpoint;


import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.websocket.server.ServerEndpointConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;



public class AgentConfig  extends ServerEndpointConfig.Configurator implements ServletRequestListener  {
	public static final AgentEndpoint agentEndpoint = new AgentEndpoint();
	@Autowired
	private AgentEventHandler agentEventHandler;

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
		agentEndpoint.setAgentEventHandler(agentEventHandler);
		System.out.println("IM IN getEndpointInstance"+agentEndpoint.getAgentEventHandler());
		return (T) agentEndpoint;
	}

	public void requestInitialized(ServletRequestEvent sre) {
		ServletContext servletContext = sre.getServletContext();
		WebApplicationContextUtils
		.getRequiredWebApplicationContext(servletContext)
		.getAutowireCapableBeanFactory()
		.autowireBean(this); 
		System.out.println("IM IN requestInitialized");
	}

	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub

	}

}





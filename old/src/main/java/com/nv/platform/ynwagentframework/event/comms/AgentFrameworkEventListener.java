package com.nv.platform.ynwagentframework.event.comms;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.server.Server;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.threadpool.NVThreadpoolFWService;
import com.nv.platform.ynwagentframework.dao.AgentFWListenerParametersDB;
import com.nv.platform.ynwagentframework.dao.Datasource;
import com.nv.platform.ynwagentframework.event.AgentEvent;
import com.nv.platform.ynwagentframework.event.AgentFWEventHandler;
import com.nv.platform.ynwagentframework.exception.AgentFrameworkException;
import com.nv.platorm.threadpool.impl.NVThreadPoolManager;

@ServerEndpoint("/sendEvent")
public class AgentFrameworkEventListener {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFrameworkEventListener.class);
	private static NVThreadpoolFWService threadpoolService;
	
	public AgentFrameworkEventListener() {
		threadpoolService = NVThreadPoolManager.getInstance();
	}
	
	//----------------------------------------------------------------------------------------------//
	
	/**
     * Accept new connection
     */
    @OnOpen
    public void onOpen(Session session){
    	logger.info(session.getId() + " : has opened a connection"); 
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 
    //----------------------------------------------------------------------------------------------//
    
    /**
     * Receive the message and process
     */
    @OnMessage
    public void onMessage(String message, Session session){
    	logger.info("Message from " + session.getId() + ": " + message);
    	JsonReader jsonReader = null;
        try {
        	if(message == null || message.length() == 0) {
        		logger.error("Message NULL");
        		session.getBasicRemote().sendText("failure"); 
        	}
        	jsonReader = Json.createReader(new StringReader(message));
        	JsonObject object = jsonReader.readObject();
        	AgentEvent event = new AgentEvent(object.getInt("eventID"), object.getString("eventType"), null);
        	AgentFWEventHandler handler = new AgentFWEventHandler(event);
        	threadpoolService.submitTask(handler);
            session.getBasicRemote().sendText("success");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        	if(jsonReader != null) {
        		jsonReader.close();
        	}
        }
    }
    
    //----------------------------------------------------------------------------------------------//
 
    /**
     * Close connection
     */
    @OnClose
    public void onClose(Session session){
    	logger.info("Session " +session.getId()+" has ended");
    }
	
    //----------------------------------------------------------------------------------------------//
    
    /**
     * Close connection
     */
    @OnError
    public void onError(Session session, Throwable t){
    	t.printStackTrace(); 
    	logger.info("Session " +session.getId()+" has ended");
    }
    
    //----------------------------------------------------------------------------------------------//
    
    
    public static void init() throws AgentFrameworkException, DeploymentException {
    	final Map<String, Object> serverProps = new HashMap<String, Object>();
        serverProps.put(Server.STATIC_CONTENT_ROOT, "./src/main/webapp");
        
        try {
        	Datasource.initialize();
        	Server server = new Server("localhost", 8080, "/agentfw", serverProps, AgentFrameworkEventListener.class);
			server.start();
			AgentFWListenerParametersDB.dbInsertUpdate();
		} catch (DeploymentException e) {
			logger.error(new NVLogFormatter("Deployment exception initializing Event listener", "cause", e.getMessage(), e));
			throw e;
		} catch (AgentFrameworkException e) {
			logger.error(new NVLogFormatter("AgentFrameworkException initializing Event listener", "cause", e.getMessage(), e));
			throw e;
		}
    }

}

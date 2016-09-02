/**
 * AgentEventHandler.java
 * 
 * @author Asha Chandran
 */
package com.nv.agent.service.event;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.nv.agent.service.event.processors.EmailEventProcessor;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.event.ActionType;
import com.nv.platform.event.AgentFwListenerEntity;
import com.nv.platform.event.EventException;
import com.nv.platform.event.EventStatus;
import com.nv.platform.event.NvEvent;
import com.nv.platform.event.NvEventTaskEntity;
import com.nv.platform.json.JSONMapper;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

/**
 * This class used to get event details and its event processor
 */
public class AgentEventHandler {
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(AgentEventHandler.class);
	Map<ActionType,EventProcessor> eventProcessors ;
	private ReadDao readDao;
	private WriteDao writeDao;
	private JSONMapper objectMapper;
	
	private final String get_agentfw_listener = "from AgentFwListenerEntity as listener where listener.type='event'";
	private final String get_event_ids_of_session = "select event.id from NvEventTaskEntity as event where event.sessionId=:param1 and event.eventStatus=:param2";
	/**
	 * 
	 * @param eventProcessors map with key {@link ActionType} and value {@link EventProcessor}
	 * @param readDao {@link ReadDao}
	 * @param objectMapper {@link JSONMapper}
	 * @param writeDao  {@link WriteDao}
	 */
	public AgentEventHandler( Map<ActionType, EventProcessor> eventProcessors,
			ReadDao readDao,JSONMapper objectMapper,WriteDao writeDao) {
		super();
		this.eventProcessors = eventProcessors;
		this.writeDao= writeDao;
		this.readDao = readDao;
		this.objectMapper = objectMapper;
	}
	/**
	 * Get event details by given event id
	 * @param eventId event id
	 * @return {@link EventDetails}
	 * @throws EventException  {@link EventException}
	 */
	@Transactional(value="write",readOnly=false)
	public EventDetails getEvent(String eventId)throws EventException {
		EventDetails eventDetails;
		try {
			eventDetails = getEvent(Integer.parseInt(eventId));
		} catch (NumberFormatException e) {
			throw new EventException(ErrorStatusType.UNPROCESSABLENTITY.toString());
		}
		return eventDetails;
	}
	
	/**
	 * Get event details by given event id
	 * @param eventId event id
	 * @return {@link EventDetails}
	 * @throws EventException  {@link EventException}
	 */
	@Transactional(value="write",readOnly=false)
	public EventDetails getEvent(int eventId) throws EventException{
		NvEventTaskEntity nvEventTaskEntity;
		EventDetails eventDetails = null;
		try {
			nvEventTaskEntity = readDao.getById(NvEventTaskEntity.class, eventId);
			if(nvEventTaskEntity==null){
				throw new EventException(ErrorStatusType.UNPROCESSABLENTITY.toString());
			}
			JsonNode node = objectMapper.readValue(nvEventTaskEntity.getEventDescription(), JsonNode.class);
			NvEvent event = (NvEvent) objectMapper.readValue(node.toString(),nvEventTaskEntity.getEventClass());
			eventDetails = new EventDetails(nvEventTaskEntity.getId(),event,eventProcessors.get(nvEventTaskEntity.getAction()));
			nvEventTaskEntity.setModifiedDate(new Date());
			nvEventTaskEntity.setEventStatus(EventStatus.RECEIVED);
			writeDao.update(nvEventTaskEntity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventException(ErrorStatusType.UNPROCESSABLENTITY.toString());
		}
		return eventDetails;
	}
	
	@Transactional(value="write",readOnly=false)
	public void updateListenerParams() throws PersistenceException, UnknownHostException{
		/*try {*/
			
			 AgentFwListenerEntity agentFwListenerEntity = (AgentFwListenerEntity) readDao.executeUniqueQuery(AgentFwListenerEntity.class, get_agentfw_listener);
			 if(agentFwListenerEntity==null){
				 agentFwListenerEntity=new AgentFwListenerEntity() ;
				 agentFwListenerEntity.setCreated_date(new Date());
				 agentFwListenerEntity.setHost(InetAddress.getLocalHost().getHostAddress());
				 agentFwListenerEntity.setPort(8080);
				 agentFwListenerEntity.setProtocol("ws");
				 agentFwListenerEntity.setSecure("");
				 agentFwListenerEntity.setType("event");
				 agentFwListenerEntity.setUpdated_date(new Date());
				 writeDao.save(agentFwListenerEntity);
			 }else{
				 agentFwListenerEntity.setHost(InetAddress.getLocalHost().getHostAddress());
				 writeDao.update(agentFwListenerEntity);
			 }
		/*} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	/**
	 * Get event ids by given session id
	 * @param sessionId session id
	 * @return list of event ids
	 */
	@Transactional(value="read",readOnly=true)
	public List<Integer> getEventIds(String sessionId){
		List<Integer> eventIds = new ArrayList<Integer>();
		try {
			eventIds = readDao.executeQuery(Integer.class,get_event_ids_of_session,sessionId,EventStatus.SUCCESS);
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while retrieving event ids of session id",e));
		}
		return eventIds;
	}
}

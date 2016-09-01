/**
 * AgentEventHandler.java
 * 
 * @author Asha Chandran
 */
package com.nv.agent.service.event.impl;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.nv.agent.service.event.EventProcessor;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.event.ActionType;
import com.nv.platform.event.EventException;
import com.nv.platform.event.EventStatus;
import com.nv.platform.event.NvEvent;
import com.nv.platform.event.NvEventTaskEntity;
import com.nv.platform.json.JSONMapper;

/**
 * This class used to get event details and its event processor
 */
public class AgentEventHandler {

	Map<ActionType,EventProcessor> eventProcessors ;
	private ReadDao readDao;
	private WriteDao writeDao;
	private JSONMapper objectMapper;
	
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
	public EventDetails getEvent(String eventId) throws EventException{
		NvEventTaskEntity nvEventTaskEntity;
		EventDetails eventDetails = null;
		try {
			nvEventTaskEntity = readDao.getById(NvEventTaskEntity.class, Integer.parseInt(eventId));
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
	
	/*@Transactional(value="read",readOnly=true)
	public EventDetails getEvent(int eventId) throws EventException{
		NvEventTaskEntity nvEventTaskEntity;
		EventDetails eventDetails = null;
		try {
			nvEventTaskEntity = readDao.getById(NvEventTaskEntity.class, eventId);
			if(nvEventTaskEntity==null){
				throw new EventException(ErrorStatusType.UNPROCESSABLENTITY.toString());
			}
			System.out.println("nvEventTaskEntity getEventDescription"+nvEventTaskEntity.getEventDescription());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EventException(ErrorStatusType.UNPROCESSABLENTITY.toString());
		}
		return eventDetails;
	}*/
}

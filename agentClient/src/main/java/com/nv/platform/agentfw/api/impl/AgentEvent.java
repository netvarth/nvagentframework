package com.nv.platform.agentfw.api.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class AgentEvent {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentEvent.class);

	private int eventID;
	private EventType eventType;
	private String eventData;
	
	public AgentEvent(final String eventType, final String eventData) {
		this.eventType = EventType.getEventType(eventType);
		this.eventData = eventData;
		//System.out.println("event created: "+this); 
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public String getEventData() {
		return eventData;
	}
	
	public int getEventId() {
		return eventID;
	}
	
	public void setEventId(int id) {
		this.eventID = id;
	}
	
	/**
	 * Event Type
	 * 
	 */
	public enum EventType {
		UPDATE_SCHEDULE,
		CREATE_ALERT;

		private static Map<String, EventType> enumTypeMap;
		
		static {
			enumTypeMap = new ConcurrentHashMap<String, EventType>();
			for (EventType e : EventType.values()) {
				enumTypeMap.put(e.name(), e);
			}
		}
		
		public static EventType getEventType(String eventStr) {
			if (eventStr == null) { return null; }
			return enumTypeMap.get(eventStr);
		}
	}

	@Override
	public String toString() {
		return "AgentEvent [eventType=" + eventType + ", eventData=" + this.getEventData() + "]";
	}
	
	/**
	 * Event Status
	 * @author pacharya
	 */
	public enum EventStatus {
		CREATED, DELIVERED, IN_PROGRESS, COMPLETED;
	}
	
}

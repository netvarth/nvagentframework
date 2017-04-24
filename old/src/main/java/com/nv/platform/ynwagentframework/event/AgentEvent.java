package com.nv.platform.ynwagentframework.event;

import java.sql.Blob;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.dao.AgentFWEventDB;

public class AgentEvent {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentEvent.class);
	private int eventId;
	private EventType eventType;
	private Blob eventData;
	
	public AgentEvent(final int eventId, final String eventType, final Blob eventData) {
		this.eventId = eventId;
		this.eventType = EventType.getEventType(eventType);
		this.eventData = eventData;
		System.out.println("event created: "+this); 
	}
	
	public int getEventId() {
		return eventId;
	}

	public EventType getEventType() {
		return eventType;
	}

	public String getEventData() {
		if (this.eventData != null) {
			try {
				return new String(this.eventData.getBytes(1L, (int)this.eventData.length()));
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error reading event data", e));
			}
		}
		return null;
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
		return "AgentEvent [eventId=" + eventId + ", eventType=" + eventType + ", eventData=" + this.getEventData() + "]";
	}
	
	
}

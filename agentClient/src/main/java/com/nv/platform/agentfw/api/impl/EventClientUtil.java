package com.nv.platform.agentfw.api.impl;

import java.io.StringReader;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.nv.platform.agentfw.api.dao.AgentFWEventDB;
import com.nv.platform.agentfw.api.impl.AgentEvent.EventStatus;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class EventClientUtil {
	private static NVLogger logger = NVLoggerAPIFactory.getLogger(EventClientUtil.class);
	
	public static void updateEvent(String eventDetails) {
		int eventID = 0;
		JsonReader jsonReader = null;
		try{
			jsonReader = Json.createReader(new StringReader(eventDetails));
        	JsonObject object = jsonReader.readObject();
        	eventID = object.getInt("eventID");
        	int affectedRows = AgentFWEventDB.dbUpdate(eventID, EventStatus.DELIVERED, EventStatus.CREATED);
        	if(affectedRows < 1) {
        		logger.error(new NVLogFormatter("No event record updated. Its possible that the record was already picked up for processing by the server.", "eventID", eventID));
        	}
        	logger.info(new NVLogFormatter("Event record successfully updated", "eventID", eventID, "eventStatus", EventStatus.DELIVERED.name()));
		} catch (SQLException e) {
			logger.error(new NVLogFormatter("Exception updating event record in DB", "eventID", eventID, e));
		} finally {
        	if(jsonReader != null) {
        		jsonReader.close();
        	}
        }
	}
}

package com.nv.platform.ynwagentframework.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.event.AgentEvent;

public class AgentFWEventDB {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWEventDB.class);

	public static String AGENTFW_EVENT_TABLE_NAME = "ynw_event";
	
	public static AgentEvent dbSelect(int eventId, String eventType) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		AgentEvent event = null;
		try {
			conn = Datasource.getConnection();
			String query = "SELECT event_data FROM " +AGENTFW_EVENT_TABLE_NAME+ " WHERE event_id=? and event_type=?";
			System.out.println("Query: " +query); 
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, eventId);
			stmt.setString(2, eventType);
			rs = stmt.executeQuery();
			if(rs.next()) {
				event = new AgentEvent(
						eventId,
						eventType,
						rs.getBlob("event_data"));
			}
			logger.info(new NVLogFormatter("Event successfully loaded"));
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error loading event", e));
		} finally {
			try {
				if (rs != null) rs.close();
			} catch (SQLException e) {
				logger.error(new NVLogFormatter("Error closing resultset", e));
			}
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				logger.error(new NVLogFormatter("Error closing statement", e));
			}
			Datasource.closeConnection(conn);
		}
		return event;
	}
	
}

package com.nv.platform.agentfw.api.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.serial.SerialBlob;

import com.nv.platform.agentfw.api.impl.AgentEvent.EventStatus;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class AgentFWEventDB {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWEventDB.class);

	public static String AGENTFW_EVENT_TABLE_NAME = "ynw_event";
	
	
	/**
	 * Event DB insert
	 * @param eventType eventType
	 * @param eventData eventData
	 * @return event id
	 * @throws SQLException {@link SQLException}
	 */
	public static int dbInsert(String eventType, String eventData ) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int eventId = 0;
		try {
			conn = Datasource.getConnection();
			String query = "INSERT into " +AGENTFW_EVENT_TABLE_NAME+ " (event_type, status, create_date, modify_date, event_data) values (?, ?, now(), now(), ?)";

			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, eventType);
			stmt.setString(2, "CREATED");
			stmt.setBlob(3, new SerialBlob(eventData.getBytes()));
			
			int affectedRows = stmt.executeUpdate();
			if(affectedRows == 0) {
				logger.error(new NVLogFormatter("Creating Event record in DB failed. Not Rows affected.", "eventType", eventType, "eventData", eventData));
				throw new SQLException("Creating Event record in DB failed. Not Rows affected."); 
			}
			
			// get the auto incremented eventid
			stmt.close();
			stmt = conn.prepareStatement("SELECT LAST_INSERT_ID()");
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				eventId = rs.getInt(1);
			} else {
				logger.error(new NVLogFormatter("Creating event record failed, no ID obtained.", "eventType", eventType, "eventData", eventData));
                throw new SQLException("Creating event record failed, no ID obtained.");
            }
			logger.info(new NVLogFormatter("Event successfully inserted"));
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error inserting new event", "eventType", eventType, "eventData", eventData, e));
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
		return eventId;
	}
	
	
	/**
	 * 
	 * @param eventId eventId
	 * @param  newStatus EventStatus
	 * @param oldStatus EventStatus
	 * @return event id
	 * @throws SQLException {@link SQLException}
	 */
	public static int dbUpdate(int eventId, EventStatus newStatus, EventStatus oldStatus) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Datasource.getConnection();
			String query = "UPDATE " +AGENTFW_EVENT_TABLE_NAME+ " SET status=? WHERE event_id=? AND status=?";

			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, newStatus.name());
			stmt.setInt(2, eventId);
			stmt.setString(3, oldStatus.name());
			
			int affectedRows = stmt.executeUpdate();
			if(affectedRows != 1) {
				logger.error(new NVLogFormatter("Event update failed", "eventId", eventId, "status", newStatus.name()));
				throw new SQLException("Updating Event record in DB failed. Not Rows affected."); 
			}
			
			logger.info(new NVLogFormatter("Event successfully updated", "eventId", eventId, "status", newStatus.name()));
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
		return eventId;
	}
	
}

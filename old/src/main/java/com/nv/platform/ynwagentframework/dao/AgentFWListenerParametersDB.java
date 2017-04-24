package com.nv.platform.ynwagentframework.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.impl.AgentFWListenerParameters;

public class AgentFWListenerParametersDB {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWListenerParametersDB.class);

	public static String AGENTFW_LISTENER_PARAMETERS_TABLE_NAME = "ynw_agentfw_listener_parameters";
	
	/**
	 * selects the event listener host details from the DB
	 * @return AgentFWListenerParameters
	 */
	public static AgentFWListenerParameters dbSelect() {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		AgentFWListenerParameters listernerparams = null;
		try {
			conn = Datasource.getConnection();
			String query = "SELECT * FROM " +AGENTFW_LISTENER_PARAMETERS_TABLE_NAME+ "WHERE type='event'";
			stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery();
			if(rs.next()) {
				listernerparams = new AgentFWListenerParameters(rs.getString("type"), 
						rs.getString("protocol"), 
						rs.getString("secure"),
						rs.getString("host"),
						rs.getInt("port"));
			}
			logger.info(new NVLogFormatter("Listerner params successfully loaded"));
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error loading listener Parameters", e));
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
		return listernerparams;
	}
	
	
	/**
	 * Insert OR Update DB row for the event listener
	 * @return boolean
	 */
	public static boolean dbInsertUpdate() {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			AgentFWListenerParameters listernerparams = dbSelect();
			String[] hostDetails = getListerHostDetails();
			conn = Datasource.getConnection();
			
			int rowsUpdated = 0;
			if (listernerparams == null) {
				String query = "INSERT INTO " +AGENTFW_LISTENER_PARAMETERS_TABLE_NAME+ " (type, protocol, secure, host, port, create_date, update_date) "
						+ "VALUES ('event', 'websocket', 'true', ?, ?, now(), now()";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, hostDetails[0]);
				stmt.setString(2, hostDetails[1]);
				rowsUpdated = stmt.executeUpdate(query);
				if (rowsUpdated == 1) {
					logger.info(new NVLogFormatter("DB row successfully inserted"));
				}
			} else {
				String query = "UPDATE " +AGENTFW_LISTENER_PARAMETERS_TABLE_NAME+ " SET host=?, port=?, update_date=now() WHERE type='event'";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, hostDetails[0]);
				stmt.setString(2, hostDetails[1]);
				rowsUpdated = stmt.executeUpdate(query);
				if (rowsUpdated == 1) {
					logger.info(new NVLogFormatter("DB row successfully updated"));
				}
			}
			
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error loading Agent config", e));
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				logger.error(new NVLogFormatter("Error closing statement", e));
			}
			Datasource.closeConnection(conn);
		}
		return false;
	}
	
	
	/**
	 * get host details
	 * @return
	 */
	private static String[] getListerHostDetails() {
        InetAddress ip;
        String hostname;
        String port;
        String hostDetails[] = new String[2];
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            port = "8025";
            hostDetails[0] = hostname;
            hostDetails[1] = port;
        } catch (UnknownHostException e) {
        	logger.error(new NVLogFormatter("Error getting host details", "message", e.getMessage(), e));
        }
        return hostDetails;
    }
	
}

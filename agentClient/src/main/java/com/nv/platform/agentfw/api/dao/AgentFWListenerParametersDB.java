package com.nv.platform.agentfw.api.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class AgentFWListenerParametersDB {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFWListenerParametersDB.class);

	public static String AGENTFW_LISTENER_PARAMETERS_TABLE_NAME = "agentfw_listener_param_tbl";
	
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
			String query = "SELECT * FROM " +AGENTFW_LISTENER_PARAMETERS_TABLE_NAME+ " WHERE type='event'";
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
	
	

	
}

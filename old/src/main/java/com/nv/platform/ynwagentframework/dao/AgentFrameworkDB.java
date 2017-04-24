package com.nv.platform.ynwagentframework.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.impl.AgentConfig;

public class AgentFrameworkDB {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(AgentFrameworkDB.class);
	public static String AGENTFW_CONFIG_TABLE_NAME = "ynw_agentfw_config";
	private static Collection<AgentConfig> configs = null;

	public static AgentConfig[] dbSelect() {
		List<AgentConfig> config = new ArrayList<AgentConfig>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = Datasource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " +AGENTFW_CONFIG_TABLE_NAME);
			while(rs.next()) {
				config.add(new AgentConfig(rs.getInt("id"), 
						rs.getString("job_key"), 
						rs.getString("job_class"),
						rs.getString("job_schedule"),
						rs.getDate("create_date"),
						rs.getDate("modify_date")));
			}
			configs = Collections.unmodifiableList(config);
			logger.info(new NVLogFormatter("Agent config successfully loaded")); 
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Error loading Agent config", e));
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
		Object[] agentJobConfig = config.toArray();
		return Arrays.copyOf(agentJobConfig, agentJobConfig.length, AgentConfig[].class);
	}

	
	
	
	
	/**
	 * Helps in Health monitor
	 * @return
	 */
	public static boolean isConfigLoaded() {
		return configs != null;
	}
	

}

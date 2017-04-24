package com.nv.platform.ynwagentframework.dao;

import java.sql.DriverManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.ynwagentframework.exception.AgentFrameworkException;

public class Datasource {

	private static NVLogger logger = NVLoggerAPIFactory.getLogger(Datasource.class);
	private static int MIN_CONNECTION = 2;
	private static int MAX_CONNECTION = 10;
	private static int CONNECTIONS_CREATED = 0;

	private static Queue<Connection> connectionPool = new ArrayBlockingQueue<Connection>(MAX_CONNECTION, true);
	private static Map<Integer, Connection> connectionsInUse = new HashMap<Integer, Connection>();
	private static boolean allowConnectionToDB = false;
	//cache db
	private static int CONNECTIONS_CREATED_CACHE = 0;
	private static Queue<Connection> connectionCacheDBPool = new ArrayBlockingQueue<Connection>(MAX_CONNECTION, true);
	private static Map<Integer, Connection> connectionsCacheInUse = new HashMap<Integer, Connection>();
	private static boolean allowConnectionToCacheDB = false;

	//----------------------------------------------------------------------------------------------//
	
	/**
	 * Initialize datasource
	 * @throws AgentFrameworkException
	 */
	public static void initialize() throws AgentFrameworkException {
		for (int i=0; i<MIN_CONNECTION; i++){
			try{
				connectionPool.add(createConnection());
				CONNECTIONS_CREATED ++;
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error opening DB Connection", e));
				throw new AgentFrameworkException("Error opening DB Connection", e);
			}
			logger.info(new NVLogFormatter("Successfully Created "+MIN_CONNECTION+" DB connections")); 
		}
		allowConnectionToDB = true;

		//cache
		for (int i=0; i<MIN_CONNECTION; i++){
			try{
				connectionCacheDBPool.add(createCacheDBConnection());
				CONNECTIONS_CREATED_CACHE ++;
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error opening DB Connection", e));
				throw new AgentFrameworkException("Error opening DB Connection", e);
			}
			logger.info(new NVLogFormatter("Successfully Created "+MIN_CONNECTION+" DB connections")); 
		}
		allowConnectionToCacheDB = true;
	}
	
	//----------------------------------------------------------------------------------------------//

	/**
	 * Create a MySQL DB connection
	 * @return
	 */
	private  static Connection createConnection() {

		String url="";
		String username="";
		String password="";
		try {
			InputStream in = new FileInputStream("/Users/pacharya/Documents/DBConfiguration.properties");
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			url = prop.getProperty("dburl");
			username = prop.getProperty("dbusername");
			password = prop.getProperty("dbpassword");
		} catch (FileNotFoundException e) {
			logger.error(new NVLogFormatter("DB Configuration file not found", e));
		} catch (Exception e) {
			logger.error(new NVLogFormatter("Cannot load DB Configuration file", e));
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect the database!", e);
		} 
		return connection;
	}

	//----------------------------------------------------------------------------------------------//

	/**
	 * get connection from the pool
	 * @return
	 */
	public static Connection getConnection() {
		if(!allowConnectionToDB) throw new IllegalStateException("Database connection is not allowed at this point!");;
		Connection conn = connectionPool.poll();
		if (conn != null) {
			connectionsInUse.put(conn.hashCode(), conn);
		} else if (conn == null && CONNECTIONS_CREATED < MAX_CONNECTION) {
			logger.warn(new NVLogFormatter("Created new DB conneciton", "createdConnections", CONNECTIONS_CREATED));
			conn = createConnection();
			connectionsInUse.put(conn.hashCode(), conn);
		} else {
			logger.error(new NVLogFormatter("Cannot create anymore DB connections", 
					"createdConnections", CONNECTIONS_CREATED,
					"maxConnections", MAX_CONNECTION));
		}
		return conn;
	}

	//----------------------------------------------------------------------------------------------//
	
	/**
	 * return connection back to the pool
	 * @param conn
	 */
	public static void closeConnection(Connection conn) {
		if (connectionsInUse.containsKey(conn.hashCode())) {
			connectionsInUse.remove(conn.hashCode());
		} else {
			logger.warn(new NVLogFormatter("Returned connection is not a saved connection in use"));
		}
		connectionPool.add(conn);
	}


	//----------------------------------------------------------------------------------------------//

	/**
	 * Create a MySQL CACHE DB connection
	 * @return
	 */
	private  static Connection createCacheDBConnection() {

		String url="";
		String username="";
		String password="";
		try {
			InputStream in = new FileInputStream("/Users/pacharya/Documents/DBConfiguration.properties");
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			url = prop.getProperty("cachedburl");
			username = prop.getProperty("dbusername");
			password = prop.getProperty("dbpassword");
		} catch (FileNotFoundException e) {
			logger.error(new NVLogFormatter("DB Configuration file not found", e));
		} catch (IOException e) {
			logger.error(new NVLogFormatter("Cannot load DB Configuration file", e));
		}

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect the database!", e);
		} 
		return connection;
	}

	//----------------------------------------------------------------------------------------------//

	/**
	 * get connection from the pool
	 * @return
	 */
	public static Connection getCacheDBConnection() {
		if(!allowConnectionToCacheDB) throw new IllegalStateException("Database connection is not allowed at this point!");;
		Connection conn = connectionCacheDBPool.poll();
		if (conn != null) {
			connectionsCacheInUse.put(conn.hashCode(), conn);
		} else if (conn == null && CONNECTIONS_CREATED < MAX_CONNECTION) {
			logger.warn(new NVLogFormatter("Created new DB conneciton", "createdConnections", CONNECTIONS_CREATED));
			conn = createCacheDBConnection();
			connectionsCacheInUse.put(conn.hashCode(), conn);
		} else {
			logger.error(new NVLogFormatter("Cannot create anymore cache DB connections", 
					"createdConnections", CONNECTIONS_CREATED,
					"maxConnections", MAX_CONNECTION));
		}
		return conn;
	}
	
	//----------------------------------------------------------------------------------------------//
	
	
	/**
	 * return connection back to the pool
	 * @param conn
	 */
	public static void closeCacheDBConnection(Connection conn) {
		if (connectionsCacheInUse.containsKey(conn.hashCode())) {
			connectionsCacheInUse.remove(conn.hashCode());
		} else {
			logger.warn(new NVLogFormatter("Returned connection is not a saved connection in use"));
		}
		connectionCacheDBPool.add(conn);
	}
	
	//----------------------------------------------------------------------------------------------//
	
	/**
	 * shutdown ConnectionPool
	 */
	public static void shutdown() {
		allowConnectionToDB = false;
		for (Entry<Integer, Connection> connEntry : connectionsInUse.entrySet()) {
			try {
				connEntry.getValue().close();
			} catch (SQLException e) {
				// Do nothing
			}
		}
		connectionsInUse.clear();
		for (Connection conn : connectionPool) {
			try {
				conn.close();
			} catch (SQLException e) {
				// Do nothing
			}
		}
		connectionPool.clear();

		//cache db
		allowConnectionToCacheDB = false;
		for (Entry<Integer, Connection> connEntry : connectionsCacheInUse.entrySet()) {
			try {
				connEntry.getValue().close();
			} catch (SQLException e) {
				// Do nothing
			}
		}
		connectionsCacheInUse.clear();
		for (Connection conn : connectionCacheDBPool) {
			try {
				conn.close();
			} catch (SQLException e) {
				// Do nothing
			}
		}
		connectionCacheDBPool.clear();
	}
}

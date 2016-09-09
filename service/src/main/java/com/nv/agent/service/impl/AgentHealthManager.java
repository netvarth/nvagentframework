
package com.nv.agent.service.impl;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.health.HealthCheck;
import com.nv.platform.health.HealthDao;
import com.nv.platform.health.HealthMonException;
import com.nv.platform.health.configuration.Component;
import com.nv.platform.health.configuration.Configuration;
import com.nv.platform.health.configuration.HealthConfig;
import com.nv.platform.health.impl.HealthMonCollector;
import com.nv.platform.json.JSONMapper;

/**
 * This class extends @HealthMonCollector
 * @author Asha Chandran
 *
 */
public class AgentHealthManager extends HealthMonCollector{

	private List<HealthCheck> components ;
	private HealthDao healthDao;
	private JSONMapper objectMapper;
	
	/**
	 * Constructor
	 * @param healthDao {@link HealthDao}
	 * @param components list of {@link Component}
	 * @param objectMapper {@link JSONMapper}
	 */
	public AgentHealthManager(HealthDao healthDao,List<HealthCheck> components,JSONMapper objectMapper) {
	
		super(healthDao);
		this.components = components;
		this.healthDao = healthDao;
		this.objectMapper=objectMapper;
	}
	
	/**
	 * This method will retrieve health configuration json data from db and convert it into {@link HealthConfig} pojo 
	 * @throws HealthMonException HealthMonException
	 * @throws IOException  when IO operation fails
	 * @throws PersistenceException when database operation fails 
	 * @throws JsonMappingException when json mapping fails
	 * @throws JsonParseException when json parsing fails
	 */
	public void init() throws HealthMonException, JsonParseException, JsonMappingException, PersistenceException, IOException{
		String config = healthDao.getHealthConfiguration();
		JsonNode node = objectMapper.readValue(config, JsonNode.class);
		HealthConfig healthConfig = objectMapper.readValue(node.toString(),HealthConfig.class);
		super.init(healthConfig);
	}
	
	/**
	 * Get configuration which contains all components those who implemented HealthCheck 
	 */
	@Override
	public Configuration getConfiguration() {
		return new Configuration(components);
	}

	/**
	 * @return the components
	 */
	public List<HealthCheck> getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(List<HealthCheck> components) {
		this.components = components;
	}
}

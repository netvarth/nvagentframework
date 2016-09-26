/**
 * 
 */
package com.nv.agent.repository.health;

import java.util.Date;
import java.util.List;

import com.nv.platform.alert.AlertDao;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.health.HealthMonitorEntity;
import com.nv.platform.health.HealthDao;
import com.nv.ynw.configuration.SystemConfiguration;
import com.nv.agent.repository.GenericDaoImpl;

/**
 * 
 * HealthDaoImpl contains all the method implementations that are defined in {@link AlertDao}
 * @author Asha Chandran
 *
 */
public class HealthDaoImpl extends GenericDaoImpl<HealthMonitorEntity> implements HealthDao{
	
	/**
	 * Listing out all the queries used in this class as 
	 * private static final variables
	 */
	private static final String get_health_monitor = "from HealthMonitorEntity health where health.createdDate>=:param1 and health.createdDate<=:param2 order by health.compId,health.createdDate asc";
	private static final String get_system_configuration = "from SystemConfiguration system where system.name=:param1";
	private static final String get_health_monitor_by_id_str= "from HealthMonitorEntity health where health.createdDate>=:param1 and health.createdDate<=:param2 and ";
	
	@Override
	public List<HealthMonitorEntity> getHealthMonitors(Date from, Date to) throws PersistenceException {
		//Query query = baseDao.buildQuery(get_health_monitor).setParameter("param1", from);	
		//query.setParameter("param2", to);
		return executeQuery(HealthMonitorEntity.class, get_health_monitor,from,to);
	}

	@Override
    
	public List<HealthMonitorEntity> getHealthMonitors(List<Integer> compIds, Date from, Date to)throws PersistenceException {
		
		String queryStr = get_health_monitor_by_id_str;
		queryStr+="(";
		String orStr="";
		for (Integer id : compIds) {
			queryStr+=orStr+"health.compId="+id;
			orStr=" or ";
		}
		queryStr+=")  order by health.compId,health.createdDate asc";
		//query.setParameter("param1",compId );	
		/*Query query = baseDao.buildQuery(queryStr);
		System.out.println(queryStr);
		query.setParameter("param1", from);
		query.setParameter("param2", to);*/
		return executeQuery(HealthMonitorEntity.class, queryStr,from,to);
	}
	
	@Override
	
	public String getHealthConfiguration() throws PersistenceException {
		//Query query = baseDao.buildQuery(get_system_configuration).setParameter("param1", "health");		
		SystemConfiguration system = readDao.executeUniqueQuery(SystemConfiguration.class, get_system_configuration,"health");
		if(system!=null){
			return system.getConfiguration();
		}else{
			return null;
		}

	}

	@Override
	public HealthMonitorEntity getHealthById(int id) throws PersistenceException {
		return null;
	}
}

/**
 * @author Asha Chandran
 *
 */
package com.nv.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.health.HealthDao;
import com.nv.ynw.common.configuration.SystemConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:META-INF/testDataSource.xml","classpath:META-INF/persistence-context.xml", })
public class HealthDaoTest {
	@Autowired
	private ApplicationContext applicationContext;
	private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
	HealthDao healthDao;

	@Before
	public void init() throws PersistenceException {
		healthDao = (HealthDao) applicationContext.getBean("health.dao");
		WriteDao baseDao = (WriteDao)applicationContext.getBean("write.dao");
		SystemConfiguration sysConfig = baseDao.getById(SystemConfiguration.class,"health");
		if(sysConfig==null){
			SystemConfiguration systemconfig = new SystemConfiguration();
			systemconfig.setName("health");
			systemconfig.setConfiguration("{\"components\":[{\"id\":1,\"name\":\"System health\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":2,\"name\":\"Alert\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":3,\"name\":\"Login\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":4,\"name\":\"Email\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":5,\"name\":\"Push notification\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"}]}");
			systemconfig.setVersion("1.0");
			baseDao.save(systemconfig);
		}else{
			sysConfig.setConfiguration("{\"components\":[{\"id\":1,\"name\":\"System health\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":2,\"name\":\"Alert\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":3,\"name\":\"Login\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":4,\"name\":\"Email\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"},{\"id\":5,\"name\":\"Push notification\",\"sourceId\":\"PS\",\"sourceName\":\"Platform Service\"}]}");
			baseDao.update(sysConfig);
		}
	}
	
	@Test
	public void getHealthMonConfigData() throws PersistenceException{
		String configData = healthDao.getHealthConfiguration();
		System.out.println(configData);
	}

}

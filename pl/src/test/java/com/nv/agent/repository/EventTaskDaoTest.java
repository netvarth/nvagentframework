package com.nv.agent.repository;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.event.ActionType;
import com.nv.platform.event.EventStatus;
import com.nv.platform.event.NvEventTaskEntity;
import com.nv.ynw.account.SignupEvent;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:META-INF/testDataSource.xml","classpath:META-INF/persistence-context.xml", })
public class EventTaskDaoTest {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	public void createEventTask() throws PersistenceException {
		WriteDao baseDao = (WriteDao)applicationContext.getBean("write.dao");
		
		NvEventTaskEntity nvEventTaskEntity = baseDao.getById(NvEventTaskEntity.class, 1);
		if(nvEventTaskEntity==null){
			nvEventTaskEntity = new NvEventTaskEntity();
			nvEventTaskEntity.setAction(ActionType.SEND_MAIL);
			nvEventTaskEntity.setCreatedDate(new Date());
			nvEventTaskEntity.setEventClass(SignupEvent.class);
			nvEventTaskEntity.setEventStatus(EventStatus.PUBLISHED);
			nvEventTaskEntity.setSessionId("");
			nvEventTaskEntity.setEventDescription("{\"emailTo\":\"asha.chandran@netvarth.com\"}");
			baseDao.save(nvEventTaskEntity);
		}
	}
}


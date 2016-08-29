/**
 * EmailEventProcessor.java
 * 
 * @author NetvarthPC
 */
package com.nv.agent.service.event.processors;

import java.io.IOException;
import java.util.Date;

import com.nv.agent.service.event.EventProcessor;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.event.EventStatus;
import com.nv.platform.event.NvEvent;
import com.nv.platform.event.NvEventTaskEntity;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.sendmsg.SendMsg;
import com.nv.platform.sendmsg.SendMsg.MailType;
import com.nv.platform.sendmsg.common.MessagingException;
import com.nv.ynw.account.SignupEvent;

/**
 * This class will handle email sending process
 */
public class EmailEventProcessor implements EventProcessor{
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(EmailEventProcessor.class);
	private SendMsg sendMsg;
	private WriteDao writeDao;
	/**
	 * Constructor 
	 * @param sendMsg {@link SendMsg}
	 * @param writeDao {@link WriteDao}
	 */
	public EmailEventProcessor(SendMsg sendMsg,WriteDao writeDao) {
		super();
		this.sendMsg = sendMsg;
		this.writeDao = writeDao;
	}
	/**
	 * Process event for email sending
	 * @param event {@link NvEvent}
	 * @param  eventId event id
	 */
	public void process(NvEvent event,int eventId){
		if(event.getClass().isAssignableFrom(SignupEvent.class)){
			SignupEvent signupEvent = (SignupEvent)event;
			try {
				sendMsg.send(MailType.reportHealth,signupEvent.getEmailTo());
				updateSuccessEvent(eventId);
			} catch (MessagingException | IOException | PersistenceException e){
				logger.error(new NVLogFormatter("Error while sending email from EmailEventProcessor by websocket server",e));
				updateFailureEvent(eventId);
			}
		}
	}
	
	/**
	 * Update event with status SUCCESS
	 * @param eventId event id
	 */
	private void updateSuccessEvent(int eventId){
		try {
			NvEventTaskEntity nvEventTaskEntity = writeDao.getById(NvEventTaskEntity.class, eventId);
			nvEventTaskEntity.setModifiedDate(new Date());
			nvEventTaskEntity.setEventStatus(EventStatus.SUCCESS);
			writeDao.update(nvEventTaskEntity);
		} catch (PersistenceException e) {
			e.printStackTrace();
			logger.error(new NVLogFormatter("Error while saving SUCCESS status in email event processor  ",e));
		}
	}
	
	/**
	 * Update event with status FAILURE
	 * @param eventId event id
	 */
	private void updateFailureEvent(int eventId){
		try {
			NvEventTaskEntity nvEventTaskEntity = writeDao.getById(NvEventTaskEntity.class, eventId);
			nvEventTaskEntity.setModifiedDate(new Date());
			nvEventTaskEntity.setEventStatus(EventStatus.FAILURE);
			writeDao.update(nvEventTaskEntity);
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while saving FAILURE status in email event processor",e));
		}
	}
}

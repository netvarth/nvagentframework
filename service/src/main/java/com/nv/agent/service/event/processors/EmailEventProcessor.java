/**
 * EmailEventProcessor.java
 * 
 * @author NetvarthPC
 */
package com.nv.agent.service.event.processors;

import java.io.IOException;
import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import com.nv.agent.service.event.EventProcessor;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.event.ActionStatus;
import com.nv.platform.event.NvEvent;
import com.nv.platform.event.NvEventActionEntity;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;
import com.nv.platform.sendmsg.common.MessagingException;
import com.nv.platform.sendmsg.email.SendEmailMsg;
import com.nv.platform.sendmsg.email.SendEmailMsg.MailType;
import com.nv.ynw.account.SignUpEvent;

/**
 * This class will handle email sending process
 */
public class EmailEventProcessor implements EventProcessor{
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(EmailEventProcessor.class);
	private SendEmailMsg sendMsg;
	private WriteDao writeDao;
	/**
	 * Constructor 
	 * @param sendMsg {@link SendEmailMsg}
	 * @param writeDao {@link WriteDao}
	 */
	public EmailEventProcessor(SendEmailMsg sendMsg,WriteDao writeDao) {
		super();
		this.sendMsg = sendMsg;
		this.writeDao = writeDao;
	}
	/**
	 * Process event for email sending
	 * @param event {@link NvEvent}
	 * @param  eventId event id
	 */
	@Transactional(value="write",readOnly=false)
	public void process(NvEvent event,int eventId){
		if(event.getClass().isAssignableFrom(SignUpEvent.class)){
			SignUpEvent signupEvent = (SignUpEvent)event;
			try {
				sendMsg.send(MailType.providerSignUp,signupEvent.getCredential());
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
			NvEventActionEntity nvEventTaskEntity = writeDao.getById(NvEventActionEntity.class, eventId);
			nvEventTaskEntity.setModifiedDate(new Date());
			nvEventTaskEntity.setActionStatus(ActionStatus.COMPLETED);
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
			NvEventActionEntity nvEventTaskEntity = writeDao.getById(NvEventActionEntity.class, eventId);
			nvEventTaskEntity.setModifiedDate(new Date());
			nvEventTaskEntity.setActionStatus(ActionStatus.FAILD_TO_EXECUTE);
			writeDao.update(nvEventTaskEntity);
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while saving FAILURE status in email event processor",e));
		}
	}
}

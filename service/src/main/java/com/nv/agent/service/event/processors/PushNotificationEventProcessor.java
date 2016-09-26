/**
 * PushNotificationEventProcessor.java
 * 
 * @author Asha Chandran
 */
package com.nv.agent.service.event.processors;

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
import com.nv.platform.sendmsg.pushnotification.SendPushMsg;
import com.nv.ynw.account.SignUpEvent;

/**
 * This class will handle push notification sending process
 */
public class PushNotificationEventProcessor implements EventProcessor{
	private static final NVLogger logger = NVLoggerAPIFactory.getLogger(PushNotificationEventProcessor.class);
	private SendPushMsg sendMsg;
	private WriteDao writeDao;
	
	/**
	 * Constructor 
	 * @param sendMsg {@link SendPushMsg}
	 * @param writeDao {@link WriteDao}
	 */
	public PushNotificationEventProcessor(SendPushMsg sendMsg,WriteDao writeDao) {
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
		/*if(event.getClass().isAssignableFrom(SignUpEvent.class)){
			SignUpEvent signupEvent = (SignUpEvent)event;
			try {
				sendMsg.sendPushMsg(signupEvent.getCredential(),signupEvent.getMessageTitle(), signupEvent.getMessageData());
				updateSuccessEvent(eventId);
			} catch (MessagingException  e){
				logger.error(new NVLogFormatter("Error while sending push notification from PushNotificationEventProcessor by websocket server",e));
				updateFailureEvent(eventId);
			}
		}*/
	}
	
	/**
	 * Update event with status SUCCESS
	 * @param eventId event id
	 */
	private void updateSuccessEvent(int eventId){
		try {
			NvEventActionEntity NvEventActionEntity = writeDao.getById(NvEventActionEntity.class, eventId);
			NvEventActionEntity.setModifiedDate(new Date());
			NvEventActionEntity.setActionStatus(ActionStatus.COMPLETED);
			writeDao.update(NvEventActionEntity);
		} catch (PersistenceException e) {
			e.printStackTrace();
			logger.error(new NVLogFormatter("Error while saving SUCCESS status in Push Notification event processor  ",e));
		}
	}
	
	/**
	 * Update event with status FAILURE
	 * @param eventId event id
	 */
	private void updateFailureEvent(int eventId){
		try {
			NvEventActionEntity nvEventActionEntity = writeDao.getById(NvEventActionEntity.class, eventId);
			nvEventActionEntity.setModifiedDate(new Date());
			nvEventActionEntity.setActionStatus(ActionStatus.FAILD_TO_EXECUTE);
			writeDao.update(nvEventActionEntity);
		} catch (PersistenceException e) {
			logger.error(new NVLogFormatter("Error while saving FAILURE status in Push Notification event processor",e));
		}
	}
}

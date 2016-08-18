package com.nv.agent.service.event.impl;

import java.util.Map;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nv.agent.repository.BaseDao;
import com.nv.agent.service.event.ActionType;
import com.nv.agent.service.event.AgentEvent;
import com.nv.agent.service.event.EventProcessor;
import com.nv.agent.service.event.processors.EmailDTO;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.json.JSONMapper;
import com.nv.platform.sendmsg.SendMsg.MailType;
import com.nv.platorm.threadpool.impl.NVThreadPoolManager;
import com.nv.platorm.threadpool.impl.TaskExecutionResult;

public class AgentEventHandler {
	private NVThreadPoolManager threadpoolService;
	Map<ActionType,EventProcessor> eventProcessors ;
	private BaseDao baseDao;
	private ReadDao readDao;
	
	public AgentEventHandler( Map<ActionType, EventProcessor> eventProcessors,
			BaseDao baseDao, ReadDao readDao) {
		super();
		this.threadpoolService = NVThreadPoolManager.getInstance();
		this.eventProcessors = eventProcessors;
		this.baseDao = baseDao;
		this.readDao = readDao;
	}

	public void handle(String eventId) throws JsonProcessingException{
		JSONMapper objectMapper  =new JSONMapper();
		
		//really take from db
		EmailDTO emailDTO = new EmailDTO(MailType.reportHealth, "asha.chandran@netvarth.com");
		String emailDTOstr  = objectMapper.writeValueAsString(emailDTO);
		AgentEvent agentEvent = new AgentEvent(Integer.parseInt(eventId), emailDTOstr);
		ActionType actionType = ActionType.sendEmail;
		
		threadpoolService.submitTask(new Callable<TaskExecutionResult>() {
			public TaskExecutionResult call() throws Exception {
				eventProcessors.get(actionType).process(agentEvent);
				return null;

			}
		});
	}
	
}

/**
 * 
 */
package com.nv.agent.service.event.processors;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.JsonNode;
import com.nv.agent.service.event.AgentEvent;
import com.nv.agent.service.event.EventProcessor;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.json.JSONMapper;
import com.nv.platform.sendmsg.SendMsg;
import com.nv.platform.sendmsg.SendMsg.MailType;
import com.nv.platform.sendmsg.common.MessagingException;

/**
 * @author NetvarthPC
 *
 */
public class EmailProcessor implements EventProcessor{
	private SendMsg sendMsg;
	private JSONMapper objectMapper;

	public EmailProcessor(SendMsg sendMsg,JSONMapper objectMapper) {
		super();
		this.sendMsg = sendMsg;
		this.objectMapper = objectMapper;
	}

	public void process(AgentEvent agentEvent){
		JsonNode node;
		EmailDTO emailDTO;
		try {
			node = objectMapper.readValue(agentEvent.getEventDesc(), JsonNode.class);
			emailDTO = objectMapper.readValue(node.toString(),EmailDTO.class);
			sendMsg.send(emailDTO.getMailType(), emailDTO.getEmailTo());
		} catch (MessagingException | PersistenceException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

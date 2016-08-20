package com.nv.agent.service.event.processors;

import java.io.Serializable;

import com.nv.platform.sendmsg.SendMsg.MailType;

public class EmailDTO implements Serializable{
	private MailType  mailType;
	private String emailTo;

	public EmailDTO() {
		super();
	}
	public EmailDTO(MailType mailType, String emailTo) {
		super();
		this.mailType = mailType;
		this.emailTo = emailTo;
	}
	public MailType getMailType() {
		return mailType;
	}
	public String getEmailTo() {
		return emailTo;
	}

}

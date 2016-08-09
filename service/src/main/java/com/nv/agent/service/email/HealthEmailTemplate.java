/**
 * HealthEmailTemplate.java
 * @author Asha chandran
 *
 * Version 1.0 Jan 15, 2016
 *
 * Copyright (c) 2016 Netvarth Technologies, Inc.
 * All rights reserved.
 *
 */
package com.nv.agent.service.email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.sendmsg.SendMsg.MailType;
import com.nv.platform.sendmsg.SendMsgCallbackEnum;
import com.nv.platform.sendmsg.email.EmailTemplate;

/**
 *
 *
 * @author Nithesh
 */
public class HealthEmailTemplate implements EmailTemplate{

	String serverIpAddress ;

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.email.EmailTemplate#uniqueId()
	 */
	@Override
	public MailType uniqueId() {
		return MailType.reportHealth;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.email.EmailTemplate#getHTMLTemplate()
	 */
	@Override
	public URL getHTMLTemplate() throws MalformedURLException {
		URL url;
		url = new URL("http://" + serverIpAddress
				+ "/agent/email/templates/report-health.html");
		return url;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.email.EmailTemplate#createDefaultEmailBody(int)
	 */

	@Override
	public String createDefaultEmailBody(String emailId)
			throws IOException, PersistenceException {
		URL url = getHTMLTemplate();

		StringBuffer msgBodyBfr = new StringBuffer();
		String fullMsgBody = "";
		java.net.URLConnection openConnection = url.openConnection();
		InputStream inputStream = openConnection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		String readLine = "";
		while ((readLine = in.readLine()) != null) {
			msgBodyBfr.append(readLine).append("\n");
		}
		in.close();
		fullMsgBody = msgBodyBfr.toString();
		return fullMsgBody;
	}
	@Override
	public String createEmailBody(String id,String message) throws IOException {

		URL url = getHTMLTemplate();

		StringBuffer msgBodyBfr = new StringBuffer();
		String fullMsgBody = "";
		java.net.URLConnection openConnection = url.openConnection();
		InputStream inputStream = openConnection.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		String readLine = "";
		while ((readLine = in.readLine()) != null) {
			msgBodyBfr.append(readLine).append("\n");
		}
		in.close();
		fullMsgBody = msgBodyBfr.toString();
		fullMsgBody = fullMsgBody.replace("{errorMessage}",message);
		return fullMsgBody;
	}

	@Override
	public SendMsgCallbackEnum getMsgCallBack() {
		return SendMsgCallbackEnum.REPORT_HEALTH_MAIL;
	}

	@Override
	public String getSubject() {
		String sub ="YNW Health monitor notification";
		return sub;
	}

	public String getServerIpAddress() {
		return serverIpAddress;
	}

	public void setServerIpAddress(String serverIpAddress) {
		this.serverIpAddress = serverIpAddress;
	}



}

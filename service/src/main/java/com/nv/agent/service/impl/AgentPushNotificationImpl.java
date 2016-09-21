/**
 * AgentPushNotificationImpl.java
 *
 * @author Asha Chandran
 */
package com.nv.agent.service.impl;

import com.nv.platform.sendmsg.pushnotification.PushNotificationImpl;
import com.nv.platform.sendmsg.pushnotification.SendPushMsg;

public class AgentPushNotificationImpl extends PushNotificationImpl implements SendPushMsg {

	public AgentPushNotificationImpl(String googleAuthKey) {
		super(googleAuthKey);
	}

	@Override
	public int getComponentId() {
		return 5;
	}
}

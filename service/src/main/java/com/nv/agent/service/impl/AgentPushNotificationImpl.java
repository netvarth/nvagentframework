/**
 * AgentPushNotificationImpl.java
 *
 * @author Asha Chandran
 */
package com.nv.agent.service.impl;

import com.nv.platform.sendmsg.SendMsg;
import com.nv.platform.sendmsg.pushnotification.PushNotificationImpl;

public class AgentPushNotificationImpl extends PushNotificationImpl implements SendMsg {

	public AgentPushNotificationImpl(String googleAuthKey) {
		super(googleAuthKey);
	}

	@Override
	public int getComponentId() {
		return 5;
	}
}

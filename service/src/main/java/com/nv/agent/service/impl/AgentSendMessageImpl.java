/**
 * AgentSendMessageImpl.java
 *
 * @author Asha Chandran
 */
package com.nv.agent.service.impl;

import com.nv.platform.sendmsg.SendMsg;
import com.nv.platform.sendmsg.email.SendMessageImpl;

public class AgentSendMessageImpl extends SendMessageImpl implements SendMsg {

	@Override
	public int getComponentId() {
		return 4;
	}
}

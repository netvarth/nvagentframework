/**
 * 
 */
package com.nv.agent.service.event;

import java.io.Serializable;

/**
 * @author NetvarthPC
 *
 */
public class AgentEvent implements Serializable{
  private int eventId;
  private String eventDesc;
  
public AgentEvent(int eventId, String eventDesc) {
	super();
	this.eventId = eventId;
	this.eventDesc = eventDesc;
}
public int getEventId() {
	return eventId;
}
public String getEventDesc() {
	return eventDesc;
}
  
}

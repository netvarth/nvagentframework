package com.nv.platform.agentfw.api.impl;

import java.util.ArrayList;
import java.util.List;

public class EventFailureListHolder {
	
	private static List<AgentEvent> eventsToBeSaved = new ArrayList<AgentEvent>();
	private static List<AgentEvent> eventsToBeDelivered = new ArrayList<AgentEvent>();
	
	private static boolean isEventToBeSavedListBeingUsed = false;
	private static boolean isEventToBeDeliveredListBeingUsed = false;
	
	/**
	 * If you use this, be sure to call releaseEventsToBeSavedList() right after you are done getting the value of the list
	 * If you do not call releaseEventsToBeSavedList(), the control on the list will be released after 1 sec. 
	 * @return {@link AgentEvent}
	 */
	public static synchronized List<AgentEvent> getEventsToBeSavedList() {
		long currentTime = System.currentTimeMillis();
		while (isEventToBeSavedListBeingUsed) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			if ((currentTime - System.currentTimeMillis()) > 1000) {
				releaseEventsToBeSavedList();
			}
		}
		isEventToBeSavedListBeingUsed = true;
		return eventsToBeSaved;
	}
	
	public static synchronized void releaseEventsToBeSavedList() {
		isEventToBeSavedListBeingUsed = false;
	}
	
	/**
	 * If you use this, be sure to call releaseEventsToBeDeliveredList() right after you are done getting the value of the list
	 * If you do not call releaseEventsToBeDeliveredList(), the control on the list will be released after 1 sec. 
	 * @return list of {@link AgentEvent}
	 */
	public static synchronized List<AgentEvent> getEventsToDeliveredList() {
		long currentTime = System.currentTimeMillis();
		while (isEventToBeDeliveredListBeingUsed) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			if ((currentTime - System.currentTimeMillis()) > 1000) {
				releaseEventsToBeDeliveredList();
			}
		}
		isEventToBeDeliveredListBeingUsed = true;
		return eventsToBeDelivered;
	}
	
	
	public static synchronized void releaseEventsToBeDeliveredList() {
		isEventToBeDeliveredListBeingUsed = false;
	}
	
}

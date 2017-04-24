package com.nv.platform.agentfw.api.worker;

import java.util.concurrent.atomic.AtomicInteger;

import com.nv.platform.agentfw.api.impl.EventPublisherImpl;
import com.nv.platform.log.api.NVLogFormatter;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;

public class EventClientHeartBeat implements Runnable {

	NVLogger logger = NVLoggerAPIFactory.getLogger(EventClientHeartBeat.class);
	
	private static boolean healthCheckSuccessful = false; 
	
	private static EventPublisherImpl eventPublisher = null;
	
	private static final int secondsToWaitBeforeConnEstablish = 10;
	
	private volatile static AtomicInteger secondsSinceLastSuccessfulHeartbeat = new AtomicInteger(0);
	
	static {
		eventPublisher = EventPublisherImpl.getEventPublisherImpl();
	}
	
	@Override
	public void run() {
		while (true) {
			try{
				if(healthCheckSuccessful) {
					eventPublisher.checkConnection();
				} else {
					logger.error(new NVLogFormatter("Web socket health check unsuccessful. Waiting for connection reestablishment."));
				}
			} catch (Exception e) {
				logger.error(new NVLogFormatter("Error while processing DB failed events", e));
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			// increase the number 
			secondsSinceLastSuccessfulHeartbeat.set(secondsSinceLastSuccessfulHeartbeat.incrementAndGet());
			try {
				if(secondsSinceLastSuccessfulHeartbeat.get() > secondsToWaitBeforeConnEstablish) {
					eventPublisher.initWebSocketConnection();
				}
			} catch (Exception e) {
				healthCheckSuccessful = false;
				logger.error(new NVLogFormatter("Error creating Web Socket connection", e));
			}
		}
	}
	
	public static void heartbeat() {
		healthCheckSuccessful = true;
		secondsSinceLastSuccessfulHeartbeat.set(0);
	}
	
}

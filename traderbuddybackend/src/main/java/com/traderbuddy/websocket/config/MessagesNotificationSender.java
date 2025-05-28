package com.traderbuddy.websocket.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MessagesNotificationSender {
	private final SimpMessagingTemplate messagingTemplate;
	
	public void send(Notification notification,String destination) {
//		Notification notification
		messagingTemplate.convertAndSend(destination, notification);
	}

}

package com.traderbuddy.websocket.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.traderbuddy.models.Messages;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MessagesNotificationSender {
	private final SimpMessagingTemplate messagingTemplate;
	
	public void send(Messages message) {
		String destination="/topic/"+message.getWorkspaceId();
		messagingTemplate.convertAndSend(destination, message);
	}

}

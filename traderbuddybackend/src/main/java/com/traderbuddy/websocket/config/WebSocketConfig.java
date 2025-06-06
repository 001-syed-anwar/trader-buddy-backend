package com.traderbuddy.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic"); // Enables a simple in-memory broker
		config.setApplicationDestinationPrefixes("/app"); // Prefix for messages bound for methods annotated with
															// @MessageMapping
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000") // React app origin
				.withSockJS(); // Enables SockJS fallback options
	}

//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//    	return WebSocketMessageBrokerConfigurer.super.configureMessageConverters(messageConverters);
//    }

}

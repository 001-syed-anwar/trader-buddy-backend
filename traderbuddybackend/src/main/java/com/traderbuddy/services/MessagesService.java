package com.traderbuddy.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.SendMessageRequest;
import com.traderbuddy.models.Channel;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Messages;
import com.traderbuddy.repositories.ChannelRepository;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.repositories.MessagesRepository;
import com.traderbuddy.websocket.config.MessagesNotificationSender;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MessagesService {
	
	private final MessagesRepository messagesRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final ChannelRepository channelRepository;
	private final MemberRepository memberRepository;
	private final MessagesNotificationSender notificationSender;
	
	public void sendMessage(SendMessageRequest request, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Channel saved = channelRepository.getReferenceById(request.getChannelId());
		Member member=memberRepository.findByUserIdAndWorkspaceId(userId, saved.getWorkspaceId())
				.orElseThrow(() -> new IllegalAccessException("User is not the member of the workspace"));
		Messages message= Messages
				.builder()
				.body(request.getBody())
				.imagePath(request.getImage())
				.channelId(request.getChannelId())
				.workspaceId(request.getWorkspaceId())
				.memberId(member.getId())
				.parentMessageId(request.getParentMessageId())
				.build();
		messagesRepository.save(message);
		// Do it via Convert and Send to users 
		// Send notification to users specific to receiver /topic/workspace/channel
		notificationSender.send(message);
		
	}

}

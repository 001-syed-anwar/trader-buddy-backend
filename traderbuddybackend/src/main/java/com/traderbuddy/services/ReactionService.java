package com.traderbuddy.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.AddReactionRequest;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Message;
import com.traderbuddy.models.Reaction;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.repositories.MessageRepository;
import com.traderbuddy.repositories.ReactionRepository;
//import com.traderbuddy.repositories.WorkspaceRepository;
import com.traderbuddy.websocket.config.MessagesNotificationSender;
import com.traderbuddy.websocket.config.Notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionService {
	private final MemberRepository memberRepository;
	private final UserRepository userRepository;
	private final ReactionRepository reactionRepository;
	private final MessageRepository messageRepository;
	private final JwtService jwtService;
	private final MessagesNotificationSender notificationSender;

	@Transactional
	public void addOrRemove(AddReactionRequest request, String token) {
		Message message = messageRepository.getReferenceById(request.getMessageId());
		String email = jwtService.getUsername(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user is invalid"));
		Member member=memberRepository.findByUserIdAndWorkspaceId(user.getId(), message.getWorkspaceId())
				.orElseThrow(() -> new UsernameNotFoundException("member is not part of the workspace"));

		Reaction reaction = reactionRepository.findByMessageIdAndMemberId(request.getMessageId(),member.getId()).orElseGet(()->null);
		if (reaction == null) {
			reaction = Reaction.builder().workspaceId(message.getWorkspaceId()).messageId(request.getMessageId())
					.memberId(member.getId())
					.value(request.getValue()).build();
			reactionRepository.save(reaction);
		} else if (reaction.getValue().equals(request.getValue())) {
			reactionRepository.deleteById(reaction.getId());
		} else {
			reaction.setValue(request.getValue());
			reactionRepository.save(reaction);
		}
		Notification notification = Notification.builder().content("Message Update").build();
		String destination = "/topic/" + message.getWorkspaceId();
		notificationSender.send(notification, destination);
	}
}

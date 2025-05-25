package com.traderbuddy.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.SendMessageRequest;
import com.traderbuddy.dto.response.MessageResponse;
import com.traderbuddy.models.Channel;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Messages;
import com.traderbuddy.repositories.ChannelRepository;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.repositories.MessagesRepository;
import com.traderbuddy.services.MessagesService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class MessagesController {
	private final MessagesService service;
	private final JwtService jwtService;
	private final MessagesRepository messageRepository;
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final ChannelRepository channelRepository;

	@PostMapping("/send")
	public ResponseEntity<Void> sendMessage(@RequestBody SendMessageRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.sendMessage(request, authToken);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/getMessages")
	public ResponseEntity<List<MessageResponse>> getPaginatedPosts(@RequestParam(required = false) String cursor,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(required = true) long channelId,
			@CookieValue(name = "access_token", defaultValue = "") String token) {
		Channel channel = channelRepository.findById(channelId)
				.orElseThrow(() -> new EntityNotFoundException("Channel is not found"));
		// validation to authorize user
		String email = jwtService.getUsername(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user is invalid"));
		memberRepository.findByUserIdAndWorkspaceId(user.getId(), channel.getWorkspaceId())
				.orElseThrow(() -> new UsernameNotFoundException("member is not part of the workspace"));

		// List of messages in the channel
		LocalDateTime cursorDate = (cursor != null) ? LocalDateTime.parse(cursor) : null;
		Pageable pageable = PageRequest.of(0, limit);
		List<Messages> messages = messageRepository.findNextPageByChannelId(cursorDate, channelId, pageable);
		String nextCursor = messages.size() < limit ? null
				: messages.get(messages.size() - 1).getCreatedAt().toString();

		// Response with User and Member details for all Messages List
		List<MessageResponse> response = new ArrayList<>();
		for (Messages message : messages) {
			Member messageMember = memberRepository.findById(message.getMemberId()).orElseGet(null);
			User messageUser = null;
			if (messageMember != null) {
				messageUser = userRepository.findById(messageMember.getUserId()).orElseGet(null);
			}
			MessageResponse entry = MessageResponse.builder().data(message).user(messageUser).member(messageMember)
					.nextCursor(nextCursor).build();
			response.add(entry);
		}

		return ResponseEntity.ok(response);
	}

	/*
	 * get messages
	 * 
	 * createdTime, id, body, imgurl, channelId, member :
	 * {id,role,userId,workspaceId} user : {email, name, profile}
	 * 
	 */
}

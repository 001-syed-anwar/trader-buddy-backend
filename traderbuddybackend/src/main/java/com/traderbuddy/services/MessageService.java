package com.traderbuddy.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.EditMessageRequest;
import com.traderbuddy.dto.request.SendMessageRequest;
import com.traderbuddy.dto.response.MemberDto;
import com.traderbuddy.dto.response.MessageDto;
import com.traderbuddy.dto.response.MessageResponse;
import com.traderbuddy.dto.response.MessageResponseDto;
import com.traderbuddy.dto.response.ReactionResponse;
import com.traderbuddy.dto.response.UserDto;
import com.traderbuddy.models.Channel;
import com.traderbuddy.models.DirectMessage;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Message;
import com.traderbuddy.models.Reaction;
import com.traderbuddy.repositories.ChannelRepository;
import com.traderbuddy.repositories.DirectMessageRepository;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.repositories.MessageRepository;
import com.traderbuddy.repositories.ReactionRepository;
import com.traderbuddy.websocket.config.MessagesNotificationSender;
import com.traderbuddy.websocket.config.Notification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageService {

	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final MemberRepository memberRepository;
	private final DirectMessageRepository directMessageRepository;
	private final ReactionRepository reactionRepository;
	private final JwtService jwtService;
	private final MessagesNotificationSender notificationSender;
 
	public List<MessageResponse> getMessages(String cursor, int limit, Long channelId, Long parentMessageId, Long dmId,
			String token) {
		String email = jwtService.getUsername(token);
		Long userId = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user is invalid")).getId();
		if (channelId != null) {
			Channel channel = channelRepository.getReferenceById(channelId);
			memberRepository.findByUserIdAndWorkspaceId(userId, channel.getWorkspaceId())
					.orElseThrow(() -> new EntityNotFoundException("User is not the member of the workspace"));
		} else if (dmId != null) {
			DirectMessage dm = directMessageRepository.getReferenceById(dmId);
			memberRepository.findByUserIdAndWorkspaceId(userId, dm.getWorkspaceId())
					.orElseThrow(() -> new EntityNotFoundException("User is not the member of the workspace"));
		} else {
			Message parentMessage = messageRepository.getReferenceById(parentMessageId);
			dmId = directMessageRepository.getReferenceById(parentMessage.getDirectMessageId()).getId();
		}

		// List of messages
		LocalDateTime cursorDate = (cursor != null) ? LocalDateTime.parse(cursor) : null;
		Pageable pageable = PageRequest.of(0, limit);
		List<Message> messages = messageRepository.findNextPage(cursorDate, channelId, parentMessageId, dmId, pageable);
		String nextCursor = messages.size() < limit ? null
				: messages.get(messages.size() - 1).getCreatedAt().toString();

		// Response with User and Member details for all Messages List
		List<MessageResponse> response = new ArrayList<>();
		for (Message message : messages) {
//			if(message==null) continue;
//			if(message.getMemberId()==null) continue;
			log.info("The messafe member id is : {} ",message.getMemberId());
			Member messageMember = memberRepository.findById(message.getMemberId()).orElseGet(()->null);
			log.info("The messafe meber id is : {} and the member is {} ",message.getMemberId(),messageMember);
			User messageUser = null;
			if (messageMember != null) {
				log.info("Inside the check but the memberMessage is {}",messageMember);
				messageUser = userRepository.findById(messageMember.getUserId()).orElseGet(()->null);
			}
//			if (messageUser != null) {
				List<ReactionResponse> reactions = reactionRepository.findAllByMessageId(message.getId()).stream()
						.collect(Collectors.groupingBy(Reaction::getValue,
								Collectors.mapping(Reaction::getMemberId, Collectors.toList())))
						.entrySet().stream().map(entry -> ReactionResponse.builder().value(entry.getKey())
								.count(entry.getValue().size()).memberIds(entry.getValue()).build())
						.collect(Collectors.toList());
				MessageDto messageDto = MessageDto.builder().id(message.getId()).body(message.getBody())
						.channelId(message.getChannelId()).directMessageId(message.getDirectMessageId())
						.imagePath(message.getImagePath()).memberId(message.getMemberId())
						.parentMessageId(message.getParentMessageId()).workspaceId(message.getWorkspaceId())
						.createdAt(message.getCreatedAt()).updatedAt(message.getUpdatedAt()).build();
				
				UserDto userDto =UserDto.builder().firstname("Deleted").lastname("User").build();
				if(messageUser!=null) userDto=UserDto.builder().email(messageUser.getEmail()).firstname(messageUser.getFirstname())
						.id(messageUser.getId()).lastname(messageUser.getLastname()).lastSeen(messageUser.getLastSeen())
						.password(messageUser.getPassword()).profileImg(messageUser.getProfileImg())
						.role(messageUser.getRole()).build();
				
				MemberDto memberDto = MemberDto.builder().firstname("Deleted").lastname("User").build();
				if(messageMember!=null) memberDto=MemberDto.builder().firstname(messageMember.getFirstname())
						.id(messageMember.getId()).lastname(messageMember.getLastname())
						.profileImg(messageMember.getProfileImg()).role(messageMember.getRole())
						.userId(messageMember.getUserId()).workspaceId(messageMember.getWorkspaceId()).build();
				MessageResponse entry = MessageResponse.builder().data(messageDto).user(userDto).member(memberDto)
						.nextCursor(nextCursor).reactions(reactions).build();

				List<Message> threadMessages = messageRepository.findAllByParentMessageId(message.getId());
				Integer threadSize = threadMessages.size();
				if (threadSize > 0) {
					entry.setThreadCount(threadSize);
					Message lastThreadMessage = threadMessages.get(threadSize - 1);
					if (lastThreadMessage != null) {
						entry.setThreadTimestamp(lastThreadMessage.getCreatedAt());
						entry.setThreadImage(lastThreadMessage.getImagePath());
						Member lastThreadMember = memberRepository.findById(lastThreadMessage.getMemberId()).orElseGet(()->null);
						if (lastThreadMember != null)
							entry.setThreadName(lastThreadMember.getFirstname() + " " + lastThreadMember.getLastname());
					}
				}
				response.add(entry);
//			}
		}
		return response;
	}

	public MessageResponseDto getMessage(long id, String token) {
		Message message = messageRepository.getReferenceById(id);
		String email = jwtService.getUsername(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user is invalid"));
		memberRepository.findByUserIdAndWorkspaceId(user.getId(), message.getWorkspaceId())
				.orElseThrow(() -> new UsernameNotFoundException("member is not part of the workspace"));

		Member messageMember = memberRepository.findById(message.getMemberId()).orElseGet(null);
		User messageUser = null;
		if (messageMember != null) {
			messageUser = userRepository.findById(messageMember.getUserId()).orElseGet(null);
		}

		List<ReactionResponse> reactions = reactionRepository.findAllByMessageId(message.getId()).stream()
				.collect(Collectors.groupingBy(Reaction::getValue,
						Collectors.mapping(Reaction::getMemberId, Collectors.toList())))
				.entrySet().stream().map(entry -> ReactionResponse.builder().value(entry.getKey())
						.count(entry.getValue().size()).memberIds(entry.getValue()).build())
				.collect(Collectors.toList());
		MessageDto messageDto = MessageDto.builder().id(message.getId()).body(message.getBody())
				.channelId(message.getChannelId()).directMessageId(message.getDirectMessageId())
				.imagePath(message.getImagePath()).memberId(message.getMemberId())
				.parentMessageId(message.getParentMessageId()).workspaceId(message.getWorkspaceId())
				.createdAt(message.getCreatedAt()).updatedAt(message.getUpdatedAt()).build();
		UserDto userDto = UserDto.builder().email(messageUser.getEmail()).firstname(messageUser.getFirstname())
				.id(messageUser.getId()).lastname(messageUser.getLastname()).lastSeen(messageUser.getLastSeen())
				.password(messageUser.getPassword()).profileImg(messageUser.getProfileImg()).role(messageUser.getRole())
				.build();
		MemberDto memberDto = MemberDto.builder().firstname(messageMember.getFirstname()).id(messageMember.getId())
				.lastname(messageMember.getLastname()).profileImg(messageMember.getProfileImg())
				.role(messageMember.getRole()).userId(messageMember.getUserId())
				.workspaceId(messageMember.getWorkspaceId()).build();
		MessageResponseDto response = MessageResponseDto.builder().data(messageDto).user(userDto).member(memberDto)
				.reactions(reactions).build();
		return response;
	}
	
	
	// multiple repository save calls
	@Transactional
	public void sendMessage(SendMessageRequest request, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = null;
		Long dmId = request.getDmId();
		if (request.getChannelId() != null) {
			Channel channel = channelRepository.getReferenceById(request.getChannelId());
			member = memberRepository.findByUserIdAndWorkspaceId(userId, channel.getWorkspaceId())
					.orElseThrow(() -> new IllegalAccessException("User is not the member of the workspace"));
		} else if (request.getDmId() != null) {
			DirectMessage dm = directMessageRepository.getReferenceById(request.getDmId());
			member = memberRepository.findByUserIdAndWorkspaceId(userId, dm.getWorkspaceId())
					.orElseThrow(() -> new IllegalAccessException("User is not the member of the workspace"));
		} else {
			Message parentMessage = messageRepository.getReferenceById(request.getParentMessageId());
			member = memberRepository.getReferenceById(parentMessage.getMemberId());
			dmId = directMessageRepository.getReferenceById(parentMessage.getDirectMessageId()).getId();
		}

		Message message = Message.builder().body(request.getBody()).imagePath(request.getImage())
				.channelId(request.getChannelId()).workspaceId(request.getWorkspaceId()).memberId(member.getId())
				.parentMessageId(request.getParentMessageId()).directMessageId(dmId).build();
		messageRepository.save(message);
		Notification notification = Notification.builder().content("Message Update").build();
		String destination = "/topic/" + message.getWorkspaceId();
		notificationSender.send(notification, destination);
	}

	@Transactional
	public void deleteMessage(Long id, String token) throws IllegalAccessException {
		Message message = messageRepository.getReferenceById(id);
		if (message != null) {
			Member member = memberRepository.getReferenceById(message.getMemberId());
			User user = userRepository.findByEmail(jwtService.getUsername(token))
					.orElseThrow(() -> new EntityNotFoundException("User is not in the db"));
			if (member != null) {
				if (user.getId() != member.getUserId())
					throw new IllegalAccessException("User is not the author of the message");
				messageRepository.deleteById(id);
			}
		}

		Notification notification = Notification.builder().content("Message Update").build();
		String destination = "/topic/" + message.getWorkspaceId();
		notificationSender.send(notification, destination);
	}

	@Transactional
	public void editMessage(Long id, String token, EditMessageRequest request) throws IllegalAccessException {
		Message message = messageRepository.getReferenceById(id);
		Member member = memberRepository.getReferenceById(message.getMemberId());
		User user = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new EntityNotFoundException("User is not in the db"));
		if (user.getId() != member.getUserId())
			throw new IllegalAccessException("User is not the author of the message");
		message.setBody(request.getBody());
		message.setImagePath(request.getImage());
		messageRepository.save(message);

		Notification notification = Notification.builder().content("Message Update").build();
		String destination = "/topic/" + message.getWorkspaceId();
		notificationSender.send(notification, destination);
	}

}

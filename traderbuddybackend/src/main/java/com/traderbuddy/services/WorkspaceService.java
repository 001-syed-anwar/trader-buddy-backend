package com.traderbuddy.services;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.Role;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.CreateWorkpaceRequest;
import com.traderbuddy.dto.request.GetWorkspaceRequest;
import com.traderbuddy.dto.request.UpdateWorkspaceRequest;
import com.traderbuddy.dto.response.CreateWorkpaceResponse;
import com.traderbuddy.dto.response.GetAllWorkspacesResponse;
import com.traderbuddy.dto.response.GetWorkspaceResponse;
import com.traderbuddy.dto.response.UpdateWorkspaceResponse;
import com.traderbuddy.models.Channel;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Workspace;
import com.traderbuddy.repositories.ChannelRepository;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.repositories.WorkspaceRepository;
import com.traderbuddy.utils.HashGenerator;
import com.traderbuddy.websocket.config.MessagesNotificationSender;
import com.traderbuddy.websocket.config.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
	private final WorkspaceRepository workspaceRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final MessagesNotificationSender notificationSender;

	@Transactional
	public CreateWorkpaceResponse create(CreateWorkpaceRequest request, String token) {
		User user = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db"));
		Long userId = user.getId();
		String firstname = user.getFirstname();
		String lastname = user.getLastname();
		String profileImg = user.getProfileImg();
		String joinCode = HashGenerator.generateJoinCode( userId,Instant.now().toString());
		Workspace workpace = Workspace.builder().name(request.getName()).joinCode(joinCode).build();
		Workspace saved = workspaceRepository.save(workpace);
		Member member = Member.builder().firstname(firstname).lastname(lastname).profileImg(profileImg).userId(userId)
				.email(user.getEmail()).workspaceId(saved.getId()).role(Role.ADMIN).build();
		memberRepository.save(member);
		Channel generalChannel = Channel.builder().name("General").workspaceId(saved.getId()).build();
		channelRepository.save(generalChannel);
		CreateWorkpaceResponse response = CreateWorkpaceResponse.builder().id(saved.getId()).build();

		Notification notification = Notification.builder().content("Workspace Update").build();
		String destination = "/topic/user/"+userId;
		notificationSender.send(notification, destination);
		return response;
	}

	public void regenerateJoinCode(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id).orElse(null);
		if (member == null || member.getRole() == Role.USER)
			throw new IllegalAccessException("no workspace with this user as admin is found in the db");
		Workspace workspace = workspaceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Workspace is not there in the db"));
		String newJoinCode = HashGenerator.generateJoinCode(id, userId, workspace.getName(), Instant.now().toString());
		workspace.setJoinCode(newJoinCode);
		workspaceRepository.save(workspace);
		
		Notification notification = Notification.builder().content("Code Update").build();
		String destination = "/topic/"+workspace.getId();
		notificationSender.send(notification, destination);
	}

	public void addPeople(Long id, String joinCode, String token) {
		User user = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db"));
		Long userId = user.getId();
		Workspace workspace = workspaceRepository.findById(id).orElse(null);
		if (workspace == null || !workspace.getJoinCode().equals(joinCode))
			throw new IllegalArgumentException("no such workspace or the joincode has expired");

		if (memberRepository.findByUserIdAndWorkspaceId(userId, id).isPresent())
			throw new IllegalArgumentException("Already an active member of the workspace!");

		String firstname = user.getFirstname();
		String lastname = user.getLastname();
		String profileImg = user.getProfileImg();
		Member member = Member.builder().userId(userId).workspaceId(id).firstname(firstname).lastname(lastname)
				.profileImg(profileImg).email(user.getEmail()).role(Role.USER).build();
		memberRepository.save(member);
		
		Notification notification1 = Notification.builder().content("Member Joined |"+member.getFirstname()+" "+member.getLastname()).build();
		String destination1 = "/topic/"+workspace.getId();
		notificationSender.send(notification1, destination1);
		Notification notification2 = Notification.builder().content("Workspace Update").build();
		String destination2 = "/topic/user/"+userId;
		notificationSender.send(notification2, destination2);
	}

	public GetAllWorkspacesResponse getAllWorkspace(String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		List<Long> allByUserId = memberRepository.findWorkspaceIdsByUserId(userId);
		List<Workspace> all = workspaceRepository.findAllById(allByUserId);
		return GetAllWorkspacesResponse.builder().workspaces(all).build();
	}

	public GetWorkspaceResponse getWorkspace(GetWorkspaceRequest request, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Workspace workspace = memberRepository.findWorkspaceIfUserIsMember(userId, request.getId())
				.orElseThrow(() -> new IllegalArgumentException(
						"The user is not associated with the workspace or the workspace does not exist anymore."));
		return GetWorkspaceResponse.builder().id(workspace.getId()).name(workspace.getName())
				.joinCode(workspace.getJoinCode()).build();
	}

	@Transactional
	public void delete(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id).orElse(null);
		if (member == null || member.getRole() == Role.USER)
			throw new IllegalAccessException("no workspace with this user as admin is found in the db");
		memberRepository.deleteByWorkspaceId(id);
		channelRepository.deleteAllByWorkspaceId(id);
		workspaceRepository.deleteById(id);
		
		// All members of the workspace
		Notification notification = Notification.builder().content("Workspace Delete").build();
		String destination = "/topic/"+id;
		notificationSender.send(notification, destination);
	}

	@Transactional
	public UpdateWorkspaceResponse update(Long id, UpdateWorkspaceRequest request, String token)
			throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, id).orElse(null);
		if (member == null || member.getRole() == Role.USER)
			throw new IllegalAccessException("no workspace with this user as admin is found in the db");
		Workspace workspace = workspaceRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("workspace id doesn't exist"));
		workspace.setName(request.getName());
		workspaceRepository.save(workspace);
		
		Notification notification = Notification.builder().content("Workspace Update").build();
		String destination = "/topic/"+id;
		notificationSender.send(notification, destination);
		
		return UpdateWorkspaceResponse.builder().name(workspace.getName()).id(id).build();
	}

}

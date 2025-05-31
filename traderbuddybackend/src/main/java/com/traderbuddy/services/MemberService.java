package com.traderbuddy.services;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.Role;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.UpdateMemberRequest;
import com.traderbuddy.dto.response.GetMemberResponse;
import com.traderbuddy.dto.response.GetMembersResponse;
import com.traderbuddy.models.Member;
import com.traderbuddy.repositories.MemberRepository;
import com.traderbuddy.websocket.config.MessagesNotificationSender;
import com.traderbuddy.websocket.config.Notification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final JwtService jwtService;
	private final MessagesNotificationSender notificationSender;
	
	public GetMemberResponse getMemberByWorkspaceId(Long workspaceId, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("invalid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
				.orElseThrow(() -> new IllegalArgumentException(
						"The member does not exist with combination of userId and workspaceId"));
		GetMemberResponse response = GetMemberResponse.builder().id(member.getId()).role(member.getRole()).build();
		return response;
	}

	public GetMembersResponse getMembers(Long workspaceId, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("invalid token, username is not found in db")).getId();
		memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
				.orElseThrow(() -> new IllegalArgumentException(
						"The member does not exist with combination of userId and workspaceId"));
		List<Member> members=memberRepository.findAllByWorkspaceId(workspaceId);
		GetMembersResponse response=GetMembersResponse.builder().members(members).build();
		return response;
	}

	public GetMemberResponse getMember(Long id, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("invalid token, username is not found in db")).getId();
		Member member=memberRepository.getReferenceById(id);
		memberRepository.findByUserIdAndWorkspaceId(userId, member.getWorkspaceId()).orElseThrow(()-> new EntityNotFoundException("User is not part of the workspace"));
		GetMemberResponse response = GetMemberResponse.builder()
				.id(member.getId())
				.firstname(member.getFirstname())
				.lastname(member.getLastname())
				.profileImg(member.getProfileImg())
				.userId(member.getUserId())
				.workspaceId(member.getWorkspaceId())
				.role(member.getRole())
				.email(member.getEmail())
				.build();
		return response;
	}

	@Transactional
	public void updateMemberRole(UpdateMemberRequest request, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("invalid token, username is not found in db")).getId();
		Member currentMember=memberRepository.findByUserIdAndWorkspaceId(userId, request.getWorkspaceId()).orElseThrow(()-> new EntityNotFoundException("User is not part of the workspace"));
		if(!currentMember.getRole().equals(Role.ADMIN))
			throw new IllegalAccessException("User don't have admin access");
		Member member=memberRepository.getReferenceById(request.getId());
		member.setRole(request.getRole()==Role.ADMIN?Role.ADMIN:Role.USER);
		memberRepository.save(member);
		
		Notification notification = Notification.builder().content("Member Role |"+member.getFirstname()+" "+member.getLastname()).build();
		String destination = "/topic/"+request.getWorkspaceId();
		notificationSender.send(notification, destination);
	}
	
	@Transactional
	public void leave(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("invalid token, username is not found in db")).getId();
		Member member=memberRepository.getReferenceById(id);
		if(member.getUserId()!=userId)
			throw new IllegalAccessException("Request user are not the actual user!");
		memberRepository.deleteById(id);
		
		Notification notification1 = Notification.builder().content("Member Left |"+member.getFirstname()+" "+member.getLastname()).build();
		String destination1 = "/topic/"+member.getWorkspaceId();
		notificationSender.send(notification1, destination1);
		Notification notification2 = Notification.builder().content("Workspace Update").build();
		String destination2 = "/topic/user/"+member.getUserId();
		notificationSender.send(notification2, destination2);
	}

	@Transactional
	public void remove(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member=memberRepository.getReferenceById(id);
		Role currentMemberRole=memberRepository.findByUserIdAndWorkspaceId(userId, member.getWorkspaceId()).orElseThrow(()->new EntityNotFoundException("User is not part of the workspace!")).getRole();
		if(!currentMemberRole.equals(Role.ADMIN))
			throw new IllegalAccessException("You don't have admin access");
		memberRepository.deleteById(id);
		
		Notification notification = Notification.builder().content("Member Removed |"+member.getFirstname()+" "+member.getLastname()).build();
		String destination1 = "/topic/"+member.getWorkspaceId();
		notificationSender.send(notification, destination1);
		Notification notification2 = Notification.builder().content("Workspace Update").build();
		String destination2 = "/topic/user/"+member.getUserId();
		notificationSender.send(notification2, destination2);
	}
	
}

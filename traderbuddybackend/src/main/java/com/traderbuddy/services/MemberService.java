package com.traderbuddy.services;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.response.GetMemberResponse;
import com.traderbuddy.dto.response.GetMembersResponse;
import com.traderbuddy.models.Member;
import com.traderbuddy.repositories.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final JwtService jwtService;

	public GetMemberResponse getMember(Long workspaceId, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
				.orElseThrow(() -> new IllegalArgumentException(
						"The member does not exist with combination of userId and workspaceId"));
		GetMemberResponse response = GetMemberResponse.builder().id(member.getId()).role(member.getRole()).build();
		return response;
	}

	public GetMembersResponse getMembers(Long workspaceId, String token) {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId)
				.orElseThrow(() -> new IllegalArgumentException(
						"The member does not exist with combination of userId and workspaceId"));
		List<Member> members=memberRepository.findAllByWorkspaceId(workspaceId);
		GetMembersResponse response=GetMembersResponse.builder().members(members).build();
		return response;
	}
}

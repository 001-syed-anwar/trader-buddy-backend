package com.traderbuddy.services;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.Role;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.CreateChannelRequest;
import com.traderbuddy.dto.request.UpdateChannelRequest;
import com.traderbuddy.dto.response.CreateChannelResponse;
import com.traderbuddy.dto.response.GetChannelResponse;
import com.traderbuddy.dto.response.GetChannelsResponse;
import com.traderbuddy.dto.response.UpdateChannelResponse;
import com.traderbuddy.models.Channel;
import com.traderbuddy.models.Member;
import com.traderbuddy.repositories.ChannelRepository;
import com.traderbuddy.repositories.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {
	private final ChannelRepository channelRepository;
	private final MemberRepository memberRepository;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public UpdateChannelResponse update(UpdateChannelRequest request, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Channel saved = channelRepository.getReferenceById(request.getId());
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, saved.getWorkspaceId())
				.orElseThrow(() -> new IllegalAccessException("User is not the admin of the workspace"));
		if (member.getRole() != Role.ADMIN)
			throw new IllegalAccessException("User is not the admin of the workspace");
		String channelName = request.getName().replace(" ", "-").toLowerCase();
		saved.setName(channelName);
		channelRepository.save(saved);
		UpdateChannelResponse response = UpdateChannelResponse.builder().name(saved.getName()).id(saved.getId())
				.workspaceId(saved.getWorkspaceId()).build();
		return response;
	}

	public CreateChannelResponse create(CreateChannelRequest request, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, request.getWorkspaceId())
				.orElseThrow(() -> new IllegalAccessException("User is not the admin of the workspace"));
		if (member.getRole() != Role.ADMIN)
			throw new IllegalAccessException("User is not the admin of the workspace");
		String channelName = request.getName().replace(" ", "-").toLowerCase();
		Channel channel = Channel.builder().workspaceId(request.getWorkspaceId()).name(channelName).build();
		Channel saved = channelRepository.save(channel);
		CreateChannelResponse response = CreateChannelResponse.builder().name(saved.getName()).id(saved.getId())
				.workspaceId(saved.getWorkspaceId()).build();
		return response;
	}

	public GetChannelsResponse get(Long workspaceId, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		memberRepository.findByUserIdAndWorkspaceId(userId, workspaceId).orElseThrow(() -> new IllegalAccessException(
				"Channel doesn't exist or the User is not associated with the channel"));
		List<Channel> allByWorkspaceId = channelRepository.findAllByWorkspaceId(workspaceId);
		GetChannelsResponse response = GetChannelsResponse.builder().channels(allByWorkspaceId).build();
		return response;
	}

	public GetChannelResponse getById(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Channel channel = channelRepository.getReferenceById(id);
		// ADMIN required?
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, channel.getWorkspaceId())
				.orElseThrow(() -> new IllegalAccessException("User is not the admin of the workspace"));
		GetChannelResponse response = GetChannelResponse.builder().workspaceId(channel.getWorkspaceId())
				.name(channel.getName()).build();
		return response;
	}

	public void delete(Long id, String token) throws IllegalAccessException {
		Long userId = userRepository.findByEmail(jwtService.getUsername(token))
				.orElseThrow(() -> new UsernameNotFoundException("inavlid token, username is not found in db")).getId();
		Channel channel = channelRepository.getReferenceById(id);
		// ADMIN required?
		Member member = memberRepository.findByUserIdAndWorkspaceId(userId, channel.getWorkspaceId())
				.orElseThrow(() -> new IllegalAccessException("User is not the admin of the workspace"));
		if(member.getRole()!=Role.ADMIN)
			throw new IllegalAccessException("User is not the admin of the workspace");
		channelRepository.deleteById(id);
	}

}

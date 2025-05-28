package com.traderbuddy.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.models.DirectMessage;
import com.traderbuddy.models.Member;
import com.traderbuddy.repositories.DirectMessageRepository;
import com.traderbuddy.repositories.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectMessageService {
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final DirectMessageRepository directMessageRepository;

	public Long getOrCreate(Long memberTwo, Long workspaceId, String token) {
		String email = jwtService.getUsername(token);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user is invalid"));
		Member member1 = memberRepository.findByUserIdAndWorkspaceId(user.getId(), workspaceId)
				.orElseThrow(() -> new UsernameNotFoundException("sender is not part of the workspace"));
		Member member2 = memberRepository.getReferenceById(memberTwo);
		if (member2.getWorkspaceId() != workspaceId)
			throw new UsernameNotFoundException("receiver is not part of the workspace");
		DirectMessage dm = directMessageRepository.findByMemberOneAndMemberTwo(member1.getId(), member2.getId())
				.orElseGet(() -> {
					DirectMessage create = DirectMessage.builder().memberOne(member1.getId()).memberTwo(member2.getId())
							.workspaceId(workspaceId).build();
					return directMessageRepository.save(create);
				});
		return dm.getId();
	}

}

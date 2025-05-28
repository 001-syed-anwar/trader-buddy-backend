package com.traderbuddy.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
	private MessageDto data;
	private String nextCursor;
	private UserDto user;
	private MemberDto member;
	private List<ReactionResponse> reactions;
}

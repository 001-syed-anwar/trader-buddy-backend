package com.traderbuddy.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
	private MessageDto data;
	private String nextCursor;
	private UserDto user;
	private MemberDto member;
	private List<ReactionResponse> reactions;
	private Integer threadCount;
	private String threadName;
	private LocalDateTime threadTimestamp;
	private String threadImage;
}

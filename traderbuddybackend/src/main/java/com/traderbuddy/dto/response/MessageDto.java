package com.traderbuddy.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
	private Long id;
	private String body;
	private String imagePath;
	private Long memberId;
	private Long channelId;
	private Long workspaceId;
	private Long parentMessageId;
	private Long directMessageId;
	private LocalDateTime createdAt,updatedAt;
}

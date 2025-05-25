package com.traderbuddy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendMessageRequest {
	private String body;
	private String image;
	private Long channelId;
	private Long workspaceId;
	private Long parentMessageId;
}

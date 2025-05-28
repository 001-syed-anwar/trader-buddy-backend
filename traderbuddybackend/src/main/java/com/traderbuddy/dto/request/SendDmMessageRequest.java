package com.traderbuddy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class SendDmMessageRequest {
	private String body;
	private String image;
	private Long memberOne;
	private Long memberTwo;
	private Long workspaceId;
	private Long parentMessageId;
}

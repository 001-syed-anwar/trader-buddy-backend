package com.traderbuddy.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetChannelResponse {
	private Long id;
	private String name;
	private Long workspaceId;
	private LocalDateTime createdAt;
}

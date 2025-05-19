package com.traderbuddy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetWorkspaceResponse {
	private Long id;
	private Long userId;
	private String name;
	private String joinCode;
}

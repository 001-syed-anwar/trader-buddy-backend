package com.traderbuddy.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JoinWorkpaceRequest {
	private Long id;
	private String joinCode;
}

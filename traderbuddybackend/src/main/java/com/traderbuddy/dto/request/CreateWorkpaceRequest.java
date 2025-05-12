package com.traderbuddy.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateWorkpaceRequest {
	private String name;
	private String email;
//	private String inviteCode;
}

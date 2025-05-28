package com.traderbuddy.dto.request;

import com.traderbuddy.auth.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {
	private Long id;
	private Long workspaceId;
	private Role role;
}

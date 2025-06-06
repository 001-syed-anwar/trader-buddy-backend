package com.traderbuddy.dto.response;

import com.traderbuddy.auth.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetMemberResponse {
	private Long id;
	private Role role;
	private Long userId;
	private Long workspaceId;
	private String firstname;
	private String lastname;
	private String profileImg;
	private String email;
}

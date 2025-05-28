package com.traderbuddy.dto.response;

import com.traderbuddy.auth.user.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {
	private Long id;
	// foreign key user id
	private Long userId;
	// foreign key user id
	private Long workspaceId;
	@Enumerated(EnumType.STRING)
	private Role role;
	private String firstname;
	private String lastname;
	private String profileImg;
}

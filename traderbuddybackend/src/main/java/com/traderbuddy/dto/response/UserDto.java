package com.traderbuddy.dto.response;

import java.time.LocalDateTime;

import com.traderbuddy.auth.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String password;
	private String profileImg;
	private LocalDateTime lastSeen;
	private Role role;
}

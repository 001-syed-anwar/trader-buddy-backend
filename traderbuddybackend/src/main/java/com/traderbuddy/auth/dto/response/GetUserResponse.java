package com.traderbuddy.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserResponse {
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String profileImg;
}

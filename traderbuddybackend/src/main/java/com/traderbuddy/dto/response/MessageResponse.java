package com.traderbuddy.dto.response;

import com.traderbuddy.auth.user.User;
import com.traderbuddy.models.Member;
import com.traderbuddy.models.Messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
	private Messages data;
    private String nextCursor;
    private User user;
    private Member member;
}

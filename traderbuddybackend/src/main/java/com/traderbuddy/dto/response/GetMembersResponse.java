package com.traderbuddy.dto.response;

import java.util.List;

import com.traderbuddy.models.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMembersResponse {
	List<Member> members;
}

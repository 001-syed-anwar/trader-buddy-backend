package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.response.GetMemberResponse;
import com.traderbuddy.dto.response.GetMembersResponse;
import com.traderbuddy.services.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService service;

	@GetMapping("/getMember/{workspaceId}")
	public ResponseEntity<GetMemberResponse> getMember(@PathVariable Long workspaceId,@CookieValue(name="access_token", defaultValue ="") String authToken) {
		return ResponseEntity.ok(service.getMember(workspaceId,authToken));
	}
	
	@GetMapping("/getMembers/{workspaceId}")
	public ResponseEntity<GetMembersResponse> getMembers(@PathVariable Long workspaceId,@CookieValue(name="access_token", defaultValue ="") String authToken) {
		return ResponseEntity.ok(service.getMembers(workspaceId,authToken));
	}	
}

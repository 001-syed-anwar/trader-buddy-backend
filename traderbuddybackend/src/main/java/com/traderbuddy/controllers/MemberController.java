package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.request.UpdateMemberRequest;
import com.traderbuddy.dto.response.GetMemberResponse;
import com.traderbuddy.dto.response.GetMembersResponse;
import com.traderbuddy.services.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService service;

	@GetMapping("/getMember/{id}")
	public ResponseEntity<GetMemberResponse> getMember(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.getMember(id, authToken));
	}

	@GetMapping("/getMember/workspace/{workspaceId}")
	public ResponseEntity<GetMemberResponse> getMemberByWorkspace(@PathVariable Long workspaceId,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.getMemberByWorkspaceId(workspaceId, authToken));
	}

	@GetMapping("/getMembers/{workspaceId}")
	public ResponseEntity<GetMembersResponse> getMembers(@PathVariable Long workspaceId,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.getMembers(workspaceId, authToken));
	}

	@PutMapping("/roles")
	public ResponseEntity<Void> updateMemberRole(@RequestBody UpdateMemberRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.updateMemberRole(request, authToken);
			return ResponseEntity.accepted().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/leave/{id}")
	public ResponseEntity<Void> leave(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.leave(id, authToken);
			return ResponseEntity.accepted().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/remove/{id}")
	public ResponseEntity<Void> remove(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.remove(id, authToken);
			return ResponseEntity.accepted().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}

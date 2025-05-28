package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.services.DirectMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/dm")
@RequiredArgsConstructor
public class DirectMessageContoller {
	private final DirectMessageService service;
	
	@GetMapping("/get/{memberTwo}/{workspaceId}")
	public ResponseEntity<Long> getOrCreateDm(@PathVariable Long memberTwo,@PathVariable Long workspaceId, @CookieValue(name="access_token",defaultValue="") String token){
		return ResponseEntity.ok(service.getOrCreate(memberTwo,workspaceId,token));
	}
}

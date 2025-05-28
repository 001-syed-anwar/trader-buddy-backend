package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.request.AddReactionRequest;
import com.traderbuddy.services.ReactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reaction")
public class ReactionController {
	private final ReactionService service;
	
	@PostMapping("/react")
	public ResponseEntity<Void> addReactionToMessage(@RequestBody AddReactionRequest request,@CookieValue(name = "access_token",defaultValue = "") String token){
		service.addOrRemove(request,token);
		return ResponseEntity.ok().build();
	}
}

package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.request.CreateChannelRequest;
import com.traderbuddy.dto.request.UpdateChannelRequest;
import com.traderbuddy.dto.response.CreateChannelResponse;
import com.traderbuddy.dto.response.GetChannelResponse;
import com.traderbuddy.dto.response.GetChannelsResponse;
import com.traderbuddy.dto.response.UpdateChannelResponse;
import com.traderbuddy.services.ChannelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/channel")
@RequiredArgsConstructor
public class ChannelController {
	private final ChannelService service;

	@PostMapping("/create")
	public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			return ResponseEntity.ok(service.create(request, authToken));
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	@PutMapping("/update")
	public ResponseEntity<UpdateChannelResponse> updateChannel(@RequestBody UpdateChannelRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			return ResponseEntity.ok(service.update(request, authToken));
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteChannel(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.delete(id, authToken);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/getAll/{workspaceId}")
	public ResponseEntity<GetChannelsResponse> getChannel(@PathVariable Long workspaceId,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			return ResponseEntity.ok(service.get(workspaceId, authToken));
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<GetChannelResponse> getChannelById(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			return ResponseEntity.ok(service.getById(id, authToken));
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}

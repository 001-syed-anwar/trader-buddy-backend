package com.traderbuddy.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.request.EditMessageRequest;
import com.traderbuddy.dto.request.SendMessageRequest;
import com.traderbuddy.dto.response.MessageResponse;
import com.traderbuddy.dto.response.MessageResponseDto;
import com.traderbuddy.services.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
@Slf4j
public class MessageController {
	private final MessageService service;
	
	@GetMapping("/getMessages")
	public ResponseEntity<List<MessageResponse>> getPaginatedMessages(@RequestParam(required = false) String cursor,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(required = false) Long channelId,
			@RequestParam(required = false ) Long parentMessageId,
			@RequestParam(required = false) Long dmId,
			@CookieValue(name = "access_token", defaultValue = "") String token) {
		log.info("{},{},{}",channelId,parentMessageId,dmId);
		return ResponseEntity.ok(service.getMessages(cursor, limit, channelId,parentMessageId,dmId, token));
	}
	
	@GetMapping("/getMessage/{id}")
	public ResponseEntity<MessageResponseDto> getMessage( @PathVariable long id,
			@CookieValue(name = "access_token", defaultValue = "") String token) {
		MessageResponseDto response=service.getMessage(id, token);
		log.info("the response is {}",response);
		return ResponseEntity.accepted().body(response);
	}

	@PostMapping("/send")
	public ResponseEntity<Void> sendMessage(@RequestBody SendMessageRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.sendMessage(request, authToken);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteMessage(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String token) {
		try {
			service.deleteMessage(id, token);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PutMapping("/edit/{id}")
	public ResponseEntity<Void> editMessage(@PathVariable Long id,@RequestBody EditMessageRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.editMessage(id, authToken, request);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}

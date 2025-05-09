package com.traderbuddy.auth.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.requestTypes.AuthenticationRequest;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
	private final UserRepository repository;
	@PostMapping("/getUser")
	public ResponseEntity<User> authenticate(@RequestBody AuthenticationRequest request) throws IOException {
		User user=repository.findByEmail(request.getEmail()).orElseThrow(()->new UsernameNotFoundException("The user is not found in the db!"));
		return ResponseEntity.ok(user);
	}
}

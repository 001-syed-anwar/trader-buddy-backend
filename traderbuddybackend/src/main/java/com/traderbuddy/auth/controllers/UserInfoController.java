package com.traderbuddy.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.config.JwtService;
import com.traderbuddy.auth.dto.request.AuthenticationRequest;
import com.traderbuddy.auth.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserInfoController {
	private final UserRepository repository;
	private final JwtService jwtService;
	@GetMapping("/me")
	public ResponseEntity<AuthenticationRequest> getUserDetails(HttpServletRequest request) {
		final String jwt=extractTokenFromCookies(request);
		AuthenticationRequest authResponse =AuthenticationRequest.builder()
				.email(jwtService.getUsername(jwt))
				.password(repository.findByEmail(jwtService.getUsername(jwt)).get().getPassword())
				.build();
		return ResponseEntity.ok(authResponse);
	}
	
	private String extractTokenFromCookies(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("access_token".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}

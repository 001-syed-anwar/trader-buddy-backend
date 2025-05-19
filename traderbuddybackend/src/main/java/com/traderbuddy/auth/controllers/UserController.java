package com.traderbuddy.auth.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.dto.response.GetUserResponse;
import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
	private final UserRepository repository;
	private final JwtService jwtService;
	@GetMapping("/getUser/{email}")
	public ResponseEntity<GetUserResponse> authenticate(@PathVariable String email) throws IOException {
		User user=repository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("The user is not found in the db!"));
		GetUserResponse response=GetUserResponse.builder()
				.firstname(user.getFirstname())
				.lastname(user.getLastname())
				.email(user.getEmail())
				.id(user.getId())
				.profileImg(user.getProfileImg())
				.build();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/me")
	public ResponseEntity<GetUserResponse> getUserDetails(HttpServletRequest request) {
		final String jwt=extractTokenFromCookies(request);
		GetUserResponse response=GetUserResponse.builder()
				.email(jwtService.getUsername(jwt))
				.build();
		return ResponseEntity.ok(response);
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

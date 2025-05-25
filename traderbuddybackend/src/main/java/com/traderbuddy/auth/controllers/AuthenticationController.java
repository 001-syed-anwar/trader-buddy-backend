package com.traderbuddy.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.dto.request.AuthenticationRequest;
import com.traderbuddy.auth.dto.request.RegisterRequest;
import com.traderbuddy.auth.dto.response.AuthenticationResponse;
import com.traderbuddy.auth.services.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
	private final AuthenticationService authenticationService;
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request,
			HttpServletResponse response, Authentication authentication) {
		AuthenticationResponse authResponse = authenticationService.register(request);
		Cookie jwtCookie = new Cookie("access_token", authResponse.getToken());
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(60 * 24);
		jwtCookie.setDomain("localhost");
		response.addCookie(jwtCookie);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request,
			HttpServletResponse response, Authentication authentication) {
		AuthenticationResponse authResponse = authenticationService.authenticate(request);
		Cookie jwtCookie = new Cookie("access_token", authResponse.getToken());
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(60 * 24);
		jwtCookie.setDomain("localhost");
		response.addCookie(jwtCookie);
		return ResponseEntity.ok(authResponse);
	}
	
	@PostMapping("/me")
	public ResponseEntity<AuthenticationResponse> getUserDetails(@RequestBody AuthenticationRequest request,
			HttpServletResponse response) {
		AuthenticationResponse authResponse = authenticationService.me(request);
		return ResponseEntity.ok(authResponse);
	}
	
	@PostMapping("/clear")
	public ResponseEntity<String> clearToken(HttpServletResponse response) {
	    Cookie jwtCookie = new Cookie("access_token", "");
	    jwtCookie.setHttpOnly(true);
	    jwtCookie.setSecure(true); 
	    jwtCookie.setPath("/");
	    jwtCookie.setMaxAge(0);
	    jwtCookie.setDomain("localhost");
	    response.addCookie(jwtCookie);
	    return ResponseEntity.ok("Token cleared");
	}
}

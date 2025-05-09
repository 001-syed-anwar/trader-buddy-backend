package com.traderbuddy.auth.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {
	
	@GetMapping("/initiate")
	public void authenticate(HttpServletResponse response) throws IOException {
		response.sendRedirect("/oauth2/authorization/google");
	}
}

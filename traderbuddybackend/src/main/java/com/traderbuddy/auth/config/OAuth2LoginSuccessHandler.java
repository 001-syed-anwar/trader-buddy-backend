package com.traderbuddy.auth.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.traderbuddy.auth.services.JwtService;
import com.traderbuddy.auth.user.Role;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserRepository repository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = (String) oAuth2User.getAttributes().get("email");
		String firstName = (String) oAuth2User.getAttributes().get("given_name");
		String lastName = (String) oAuth2User.getAttributes().get("family_name");
		String profileImg = (String) oAuth2User.getAttributes().get("picture");
		if (repository.findByEmail(email).isEmpty()) {
			User user = User.builder().firstname(firstName).lastname(lastName).email(email).role(Role.USER)
					.profileImg(profileImg).build();
			repository.save(user);
		}
		var user = userDetailsService.loadUserByUsername(email);
		String jwtToken = jwtService.generateToken(user);

		Cookie cookie = new Cookie("access_token", jwtToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setDomain("localhost");
		cookie.setMaxAge(24 * 60 * 60);
		response.addCookie(cookie);

		response.setContentType("application/json");
		response.sendRedirect("http://localhost:3000/");
	}
}

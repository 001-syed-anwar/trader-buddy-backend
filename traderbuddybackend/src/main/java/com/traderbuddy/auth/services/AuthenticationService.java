package com.traderbuddy.auth.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.config.JwtAuthenticationFilter;
import com.traderbuddy.auth.config.JwtService;
import com.traderbuddy.auth.dto.request.AuthenticationRequest;
import com.traderbuddy.auth.dto.request.RegisterRequest;
import com.traderbuddy.auth.dto.response.AuthenticationResponse;
import com.traderbuddy.auth.user.Role;
import com.traderbuddy.auth.user.User;
import com.traderbuddy.auth.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	public AuthenticationResponse register(RegisterRequest request) {
		var user=User
				.builder()
				.firstname(request.getFirstName())
				.lastname(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();
		repository.save(user);
		Map<String,Object> customClaims=new HashMap<>();
		//customClaims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		var jwtToken=jwtService.generateToken(customClaims,user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user=repository.findByEmail(request.getEmail()).orElseThrow(()->new UsernameNotFoundException("User is not registerd or not fount in database!"));
		Map<String,Object> customClaims=new HashMap<>();
		//customClaims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		var jwtToken=jwtService.generateToken(customClaims,user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}
	
	public AuthenticationResponse me(AuthenticationRequest request) {
		logger.info("the email from the request is : {}",request.getEmail());
		//authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		var user=repository.findByEmail(request.getEmail()).orElseThrow(()->new UsernameNotFoundException("User is not registerd or not fount in database!"));
		Map<String,Object> customClaims=new HashMap<>();
		//customClaims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		var jwtToken=jwtService.generateToken(customClaims,user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}
}

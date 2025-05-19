package com.traderbuddy.auth.services;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private static final String SECRET_KEY = "b95d07eed7c83ec5be1e3bf7ec1528a9c0d0d1ddc24863fb6a0ca9ef38451a5451b7147dc9d5b4ccecdeb6001a93414826a9e1d460eabbfc8df540f9308512c708f0241179e01c96c5feba66fb58ba8680d622930fba4817ab9ae9ee2db74b283ae069982035ba755d7ce5183e821b497f0d785e630fcbf5a2b789f09049a7bfdb4e71a4a24511cfcfc76f244a495cc799cd676459c04ae58f8332a11ce7893e4db5febc2efee41a9aed5a3c2dc9164458d793d15d433ba0efd8393679cb123a8853386ad4d7e7075283426ca823a8770514f904ae2aabc61f3492dd1a0a773148c73d5f4318690fa7f0116285549021935bc96fd941fa9a8dbc91304d8d556c";

	public String getUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public boolean isTakenValid(String token, UserDetails userDetails) {
		final String username = getUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return new Date(System.currentTimeMillis()).after(extractClaims(token, Claims::getExpiration));
	}

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public String generateToken(Map<String, Object> customClaims, UserDetails userDetails) {
		return Jwts.builder().setClaims(customClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		byte[] data = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(data);
	}
}

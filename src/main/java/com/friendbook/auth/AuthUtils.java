package com.friendbook.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.friendbook.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AuthUtils {

	@Value("${jwt.secretKey}")
	private String jwtSecretKet;

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(jwtSecretKet.getBytes(StandardCharsets.UTF_8));
	}

	@SuppressWarnings("deprecation")
	public String generateAccessToken(User user) {
		return Jwts.builder().setSubject(user.getUserName()).claim("userId", user.getUserId().toString())
				.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 10))
				.signWith(getSecretKey()).compact();
	}

	public String getUserNameFromToken(String token) {
		Claims claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
		return claims.getSubject();
	}

}

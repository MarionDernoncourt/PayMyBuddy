package com.paymybuddy.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

	private JwtEncoder jwtEncoder;
	private JwtDecoder jwtDecoder;

	public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
	}

	public String generateJwtToken(String username) {

		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(username)
				.build();
		JwtEncoderParameters params = JwtEncoderParameters.from(
				JwsHeader.with(MacAlgorithm.HS256).build(), claims);
		
		return this.jwtEncoder.encode(params).getTokenValue();
	}

	public String getUsernameFromJwtToken(String token) {
		Jwt jwt = jwtDecoder.decode(token);
		return jwt.getSubject();
	}

	public boolean validateJwtToken(String token) {
		try {
			jwtDecoder.decode(token);
			return true;
		} catch (Exception e) {
			System.err.println("Invalid JWT : " + e.getMessage());
			;
			return false;
		}
	}
}

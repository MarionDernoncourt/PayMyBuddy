package com.paymybuddy.backend.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;


/**
 * Service de gestion des tokens JWT (JSON Web Tokens).
 * 
 * Cette classe fournit des méthodes pour :
 * - générer un token JWT signé avec un nom d'utilisateur comme sujet,
 * - extraire le nom d'utilisateur depuis un token JWT,
 * - valider un token JWT (vérifier sa validité, sa signature et sa date d'expiration).
 * 
 * Le token JWT généré est valide pendant 1 heure et utilise l'algorithme HMAC SHA-256 pour la signature.
 * 
 * Ce service est utilisé pour sécuriser les échanges en authentifiant les utilisateurs via des tokens stateless.
 */

@Component
public class JwtService {

	private JwtEncoder jwtEncoder;
	private JwtDecoder jwtDecoder;

	public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
		this.jwtEncoder = jwtEncoder;
		this.jwtDecoder = jwtDecoder;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);


	public String generateJwtToken(String username) {

		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("self")
				.issuedAt(now)
				.expiresAt(now.plus(1, ChronoUnit.HOURS))
				.subject(username)
				.build();
		
		logger.debug("Génération d'un JWT pour {}", username);
		
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
			logger.warn("Invalid JWT : " + e.getMessage());
			;
			return false;
		}
	}
}

package com.paymybuddy.backend.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Configuration de la sécurité Spring Security pour l'application.
 * 
 * Cette classe configure : - Les règles de sécurité HTTP (routes ouvertes,
 * authentification, session sans état). - La gestion des tokens JWT (encodeur,
 * décodeur, clé secrète). - Le service utilisateur personnalisé pour la gestion
 * des identifiants. - Le chiffrement des mots de passe avec BCrypt.
 * 
 * Elle garantit la protection des API via JWT, en mode sans session
 * (stateless).
 */

@Configuration
public class SpringSecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);
	/**
	 * Injection de la clé secrete pour les JWT (définies dans
	 * application.properties)
	 */
	@Value("${jwt.secret}")
	private String secret;

	/**
	 * Configure la chaîne de filtres de sécurité HTTP. - Active CORS avec
	 * configuration par défaut. - Désactive CSRF car API stateless. - Utilise une
	 * politique de session sans état (pas de session serveur). - Autorise librement
	 * les endpoints /login et /register. - Demande une authentification pour toutes
	 * les autres requêtes. - Configure le support OAuth2 JWT. - Supporte aussi HTTP
	 * Basic (utile pour tests, outils).
	 * 
	 * @param http HttpSecurity à configurer.
	 * @return la chaîne de filtres de sécurité.
	 * @throws Exception en cas d'erreur.
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		logger.info("Configuration de la sécurité HTTP avec endpoints /login et /register en accès libre");

		return http.cors(Customizer.withDefaults()) // active CORS : permet au front d'acceder au back
				.csrf(csrf -> csrf.disable()) // API stateless (pas de session côté serveur)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // pas de session côté serveur

				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/login", "/api/auth/register").permitAll() // libre acces a login et register
						.anyRequest().authenticated()) // demande authentification pour les autres requests
				.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())) // config pour utiliser JWT comme methode d'authentification
				.httpBasic(Customizer.withDefaults()) // permet le HTTP basic pour les tests (via postman)
				.build();
	}

	/**
	 * Fournit la clé secrète HMAC SHA-256 pour signer/verifier les JWT.
	 * 
	 * @return la clé secrète utilisée pour le JWT.
	 */
	@Bean
	public SecretKey jwtSecretKey() {
		return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	}

	/**
	 * Crée un encodeur JWT utilisant la clé secrète.
	 * 
	 * @param secretKey clé secrète injectée.
	 * @return encodeur JWT.
	 */
	@Bean
	public JwtEncoder jwtEncoder(SecretKey secretKey) {
		logger.debug("Initialisation de l'encodeur JWT avec la clé HMAC");
		JWKSource<SecurityContext> jwtkSource = new ImmutableSecret<>(secretKey);
		return new NimbusJwtEncoder(jwtkSource);
	}

	/**
	 * Crée un décodeur JWT utilisant la clé secrète.
	 * 
	 * @param jwtSecretKey clé secrète injectée.
	 * @return décodeur JWT.
	 */
	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
	}

	/**
	 * Fournit le service de gestion des utilisateurs personnalisé pour Spring
	 * Security.
	 * 
	 * @param myUserDetailsService implémentation custom du UserDetailsService.
	 * @return service utilisateur à utiliser.
	 */
	@Bean
	public UserDetailsService usersDtailsService(MyUserDetailsService myUserDetailsService) {
		return myUserDetailsService;
	}

	/**
	 * Fournit un encodeur de mot de passe BCrypt pour sécuriser les mots de passe
	 * utilisateur.
	 * 
	 * @return BCryptPasswordEncoder pour le hachage des mots de passe.
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

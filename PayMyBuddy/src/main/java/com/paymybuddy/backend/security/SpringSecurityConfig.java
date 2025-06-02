package com.paymybuddy.backend.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

@Configuration
public class SpringSecurityConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
				.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
					.anyRequest().authenticated())
				.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	@Bean
	public SecretKey jwtSecretKey() {
		return new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey secretKey) {
		JWKSource<SecurityContext> jwtkSource = new ImmutableSecret<>(secretKey);
		return new NimbusJwtEncoder(jwtkSource);
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
	}

	@Bean
	public UserDetailsService usersDtailsService(MyUserDetailsService myUserDetailsService) {
			return myUserDetailsService;
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

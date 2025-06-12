package com.paymybuddy.backend.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration CORS de l'application Spring.
 * 
 * Cette classe permet de configurer les règles de Cross-Origin Resource Sharing (CORS)
 * afin d'autoriser le frontend à communiquer avec l'API backend 
 * depuis un domaine différent.
 **/


@Configuration
public class WebConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

	/**
	 * Configure les règles CORS pour Spring MVC (niveau controllers)
	 */
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		logger.debug("CORS MVC configuré : origine autorisée = http://localhost:8080");

		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:8080")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")

						.allowCredentials(true);
			}
		};
	}
		
	/**
	 * Configure les règles CORS pour Spring Security
	 */
		@Bean
		public CorsConfigurationSource corsConfigurationSource() {
			logger.debug("CORS Security configuré : méthodes autorisées = GET, POST, PUT, DELETE");

			 CorsConfiguration config = new CorsConfiguration();

		        config.setAllowedOrigins(List.of("http://localhost:8080")); // Frontend Angular
		        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		        config.setAllowedHeaders(List.of("*"));
		        config.setAllowCredentials(true); // Si tu utilises des cookies/sessions

		        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		        source.registerCorsConfiguration("/**", config);
		        return source;
		
	}
}

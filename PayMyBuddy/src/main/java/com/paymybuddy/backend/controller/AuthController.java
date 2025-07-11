package com.paymybuddy.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.paymybuddy.backend.dto.LoginUserDTO;
import com.paymybuddy.backend.dto.RegistrationUserDTO;
import com.paymybuddy.backend.dto.ValidLoginUserDTO;
import com.paymybuddy.backend.dto.ValidRegistrationUserDTO;
import com.paymybuddy.backend.service.AuthService;

import jakarta.validation.Valid;

/**
 * Contrôleur REST pour l'authentification des utilisateurs.
 * 
 * Gère les requêtes de login et d'inscription via les endpoints /login et /register.
 */

@Controller
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	
	public AuthController(AuthService authService	 ) {
		this.authService = authService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginUserDTO loginUser) {
		try {
		ValidLoginUserDTO user = authService.login(loginUser);
		return ResponseEntity.status(HttpStatus.OK).body(user);
		}catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()))	;
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(
			@RequestBody @Valid RegistrationUserDTO registrationUser) {
		try {
			ValidRegistrationUserDTO validRegistation = authService.registerUser(registrationUser);
			return ResponseEntity.status(HttpStatus.CREATED).body(validRegistation);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}

	}
}

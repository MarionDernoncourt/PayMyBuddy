package com.paymybuddy.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.LoginUserDTO;
import com.paymybuddy.backend.dto.RegistrationUserDTO;
import com.paymybuddy.backend.dto.ValidLoginUserDTO;
import com.paymybuddy.backend.dto.ValidRegistrationUserDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.IUserRepository;
import com.paymybuddy.backend.security.JwtService;
import com.paymybuddy.backend.security.PasswordUtils;

/**
 * Service gérant l'authentification et l'inscription des utilisateurs,
 * incluant la validation des identifiants, le hachage des mots de passe
 * et la génération de tokens JWT.
 */

@Service
public class AuthService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	private final IUserRepository userRepository;
	private final PasswordUtils passwordUtils;
	private final JwtService jwtService;

	public AuthService(IUserRepository userRepository, PasswordUtils passwordUtils, JwtService jwtUtils) {
		this.userRepository = userRepository;
		this.passwordUtils = passwordUtils;
		this.jwtService = jwtUtils;
	}

	public ValidLoginUserDTO login(LoginUserDTO loginUser) {
		logger.info("Tentative de login du user : {}", loginUser.getEmail());
		
		User user = userRepository.findByEmailIgnoreCase(loginUser.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
		
		if(!passwordUtils.verifyPassword(loginUser.getPassword(), user.getPassword())) {
			logger.warn("Echec de login, mod de passe incorrect pour {}", loginUser.getEmail());
			throw new IllegalArgumentException("Mot de passe incorrect");
		}
		
		String token = jwtService.generateJwtToken(user.getUsername());
		
		logger.info("Login réussi pour {}", loginUser.getEmail());
		
		return new ValidLoginUserDTO(user.getUsername(), user.getEmail(), token);
	}

	public ValidRegistrationUserDTO registerUser(RegistrationUserDTO registrationUser) {
		logger.info("Tentative d'inscription pour l'email {}", registrationUser.getEmail());

		if (userRepository.findByEmailIgnoreCase(registrationUser.getEmail()).isPresent()) {
			logger.warn("Echec de l'inscription : email déjà utilisé ({})", registrationUser.getEmail());
			throw new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur");
		}
		if (userRepository.findByUsernameIgnoreCase(registrationUser.getUsername()).isPresent()) {
			logger.warn("Echec de l'inscription : username déjà utilisé ({})", registrationUser.getUsername());
			throw new IllegalArgumentException("Ce pseudo est déjà pris, merci d'en choisir un autre");
		}

		User newUser = new User();

		newUser.setUsername(registrationUser.getUsername());
		newUser.setEmail(registrationUser.getEmail());
		newUser.setPassword(passwordUtils.hashPassword((registrationUser.getPassword())));

		User savedUser = userRepository.save(newUser);
		
		logger.info("Nouvel utilisateur inscrit avec succès : {}", savedUser.getEmail());

		return new ValidRegistrationUserDTO(savedUser.getUsername(), savedUser.getEmail());
	}
}

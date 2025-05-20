package com.paymybuddy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.dto.RegistrationUserDTO;
import com.paymybuddy.backend.dto.LoginUserDTO;
import com.paymybuddy.backend.dto.ValidRegistrationUserDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.security.PasswordUtils;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	private final PasswordUtils passwordUtils;

	public UserService(UserRepository userRepository, PasswordUtils passwordUtils) {
		this.userRepository = userRepository;
		this.passwordUtils = passwordUtils;
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

	



//	public FriendDTO addFriends(int userId, String email) {

//		User user = userRepository.findUserById(userId).get();
	//	if (user.getEmail().equalsIgnoreCase(email)) {
		//	throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous même");
//		}
//
//		User friend = userRepository.findUserByEmail(email).get();
//		if (user.getFriends().contains(friend)) {
//			throw new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis");
//		}

	//	user.getFriends().add(friend);
	//	userRepository.save(user);

//		return new FriendDTO(friend.getEmail());
//	}

}

package com.paymybuddy.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.dto.ProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilResponseDTO;
import com.paymybuddy.backend.dto.UsernameDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.UserRepository;
import com.paymybuddy.backend.security.JwtService;
import com.paymybuddy.backend.security.PasswordUtils;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordUtils passwordUtils;
	private final JwtService jwtService;

	public UserService(UserRepository userRepository, PasswordUtils passwordUtils, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordUtils = passwordUtils;
		this.jwtService = jwtService;
	}

	public List<UsernameDTO> getFriends(String username) {
		logger.info("Entrée dans getFriends");

		List<User> friends = userRepository.findFriendsByUsername(username);

		List<UsernameDTO> usernames = friends.stream().map(friend -> new UsernameDTO(friend.getUsername()))
				.collect(Collectors.toList());

		return usernames;

	}

	@Transactional
	public FriendDTO addFriends(String username, String email) {
		logger.info("Entrée dans addFriends");

		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
		if (user.getEmail().equalsIgnoreCase(email)) {
			throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous même");
		}

		User friend = userRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur trouvé"));

		if (user.getFriends().contains(friend)) {
			throw new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis");
		}
		user.addFriend(friend);

		userRepository.save(user);

		return new FriendDTO(email);

	}

	public BigDecimal getAccountBalance(String username) {
		logger.info("Tentative de récupération du solde du user {}", username);

		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

		BigDecimal currentBalance = user.getAccountBalance();

		logger.info("Solde de {} : {}", user.getUsername(), currentBalance);

		return currentBalance;

	}

	public void addFixedAmountToAccount(String username) {

		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
		BigDecimal currentBalance = user.getAccountBalance();
		BigDecimal newBalance = currentBalance.add(new BigDecimal("50.00"));
		user.setAccountBalance(newBalance);

		userRepository.save(user);
	}

	public ProfilDTO getUserProfil(String username) {

		User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
		return new ProfilDTO(user.getUsername(), user.getEmail());
	}

	public UpdateProfilResponseDTO updateUserProfil(String connectedUser, UpdateProfilDTO updateProfil) {

		User user = userRepository.findByUsernameIgnoreCase(connectedUser)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

		boolean usernameChanged = false;

		if (!user.getUsername().equalsIgnoreCase(updateProfil.getUsername())) {
			userRepository.findByUsernameIgnoreCase(updateProfil.getUsername()).ifPresent(u -> {
				throw new IllegalArgumentException(
						"Ce username " + updateProfil.getUsername() + " est déjà utilisé par un autre utilisateur");
			});
		}
		if (!user.getEmail().equalsIgnoreCase(updateProfil.getEmail())) {
			userRepository.findByEmailIgnoreCase(updateProfil.getEmail()).ifPresent(u -> {
				throw new IllegalArgumentException(
						"Cet email " + updateProfil.getEmail() + " est déjà utilisé par un autre utilisateur");
			});
		}

		if (!user.getUsername().equalsIgnoreCase(updateProfil.getUsername())) {
			usernameChanged = true;
			user.setUsername(updateProfil.getUsername());

		}
		if (!user.getEmail().equalsIgnoreCase(updateProfil.getEmail())) {
			user.setEmail(updateProfil.getEmail());
		}

		if (updateProfil.getPassword() != null && !updateProfil.getPassword().isBlank()
				&& !updateProfil.getPassword().equals("*********")) {
			user.setPassword(passwordUtils.hashPassword((updateProfil.getPassword())));

		}

		userRepository.save(user);

		System.out.println(user.getUsername());
		String newToken = null;
		if (usernameChanged) {
			newToken = jwtService.generateJwtToken(user.getUsername());
		}
System.out.println(newToken);
		return new UpdateProfilResponseDTO(user.getUsername(), user.getEmail(), newToken);
	}

}

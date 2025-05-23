package com.paymybuddy.backend.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
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
		;
		userRepository.save(user);

		logger.info("Liste friendsOf de l'ami : {}", friend.getFriendsOf());
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
	
}

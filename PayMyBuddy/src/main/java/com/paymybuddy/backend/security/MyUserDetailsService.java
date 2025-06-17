package com.paymybuddy.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.IUserRepository;

/**
 * Service de sécurité utilisé par Spring Security pour charger les détails
 * d'un utilisateur à partir de son nom d'utilisateur.
 * 
 * Cette classe implémente l'interface {@link UserDetailsService}, qui est utilisée
 * automatiquement par Spring Security pour récupérer les informations d'un utilisateur
 * lorsque celui-ci est authentifié via un token JWT (ou autre méthode d'authentification).
 * 
 * Elle permet ainsi à Spring de valider l'identité de l'utilisateur en retrouvant ses
 * données (username et mot de passe) dans la base de données.
 * 
 * Ce service n'est pas utilisé directement dans les méthodes de connexion/inscription,
 * mais est indispensable au bon fonctionnement de la sécurité côté backend.
 */

@Service
public class MyUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

	private final IUserRepository userRepository;

	public MyUserDetailsService(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Chargement de l'utilisateur par username : {}", username);
		
		User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
		
		logger.debug("Utilisateur {} chargé avec succès", username);
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.build();
	}

}

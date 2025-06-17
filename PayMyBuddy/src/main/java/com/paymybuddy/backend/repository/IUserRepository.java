package com.paymybuddy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.backend.model.User;

/**
 * Repository Spring Data pour gérer les entités User.
 * 
 * Fournit des méthodes CRUD ainsi que des méthodes personnalisées
 * pour rechercher un utilisateur par email ou username,
 * et récupérer la liste des amis d'un utilisateur donné.
 */

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUsernameIgnoreCase(String username);

	@Query("SELECT f FROM User u JOIN u.friends f WHERE u.username = :username")
	List<User> findFriendsByUsername(@Param("username") String username);

}

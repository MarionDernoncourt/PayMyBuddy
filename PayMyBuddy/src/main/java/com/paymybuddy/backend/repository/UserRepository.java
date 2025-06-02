package com.paymybuddy.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.backend.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUsernameIgnoreCase(String username);

	@Query("SELECT f FROM User u JOIN u.friends f WHERE u.username = :username")
	List<User> findFriendsByUsername(@Param("username") String username);

}

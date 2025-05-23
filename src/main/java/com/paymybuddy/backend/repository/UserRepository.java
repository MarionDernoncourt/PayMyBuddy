package com.paymybuddy.backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.backend.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{

	Optional<User> findByEmailIgnoreCase(String email);
	Optional<User> findByUsernameIgnoreCase(String username);
}

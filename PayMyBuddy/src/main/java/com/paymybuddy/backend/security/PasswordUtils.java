package com.paymybuddy.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordUtils {

	private final BCryptPasswordEncoder encoder;
	
	public PasswordUtils(BCryptPasswordEncoder encoder) {
		this.encoder = encoder;
	}

	public  String hashPassword(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	public  boolean verifyPassword(String rawPassword, String hashedPassword) {
		return encoder.matches(rawPassword, hashedPassword);
	}
}

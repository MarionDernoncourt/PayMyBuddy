package com.paymybuddy.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.paymybuddy.backend.service.TransactionService;
import com.paymybuddy.backend.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootApplication

public class PayMyBuddyApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;

	public static void main(String[] args) {
		
		SpringApplication.run(PayMyBuddyApplication.class, args);
		
			}

	@Transactional
	@Override
	public void run(String... args) throws Exception {

		
		
	}

}

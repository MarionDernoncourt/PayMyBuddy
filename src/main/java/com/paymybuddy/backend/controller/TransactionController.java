package com.paymybuddy.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/")
	public ResponseEntity<List<TransactionDTO>> getUserTransactions(Principal principal) {
		try {
			String connectedUserUsername = principal.getName();
			List<TransactionDTO> transactions = transactionService.getUserTransactions(connectedUserUsername);
			return ResponseEntity.status(HttpStatus.OK).body(transactions);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/transaction")
	public ResponseEntity<TransactionDTO> sendTransaction(Principal principal,
			@RequestBody TransactionDTO transactionDTO) {
		try {
			String connectedUserUsername = principal.getName();
			TransactionDTO transaction = transactionService.sendTransaction(connectedUserUsername, transactionDTO);
			return ResponseEntity.status(HttpStatus.OK).body(transaction);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}

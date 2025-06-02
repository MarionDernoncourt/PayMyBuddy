package com.paymybuddy.backend.controller;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/friends")
	public ResponseEntity<FriendDTO> addFriend(Principal principal, @RequestBody FriendDTO friend) {
		try {
			String connectedUserUsername = principal.getName();
			FriendDTO addedFriend = userService.addFriends(connectedUserUsername, friend.getEmail());
			return ResponseEntity.status(HttpStatus.OK).body(addedFriend);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new FriendDTO(e.getMessage()));
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/account")
	public ResponseEntity<BigDecimal> getAccountBalance(Principal principal) {
		try {
			String connectedUserUsername = principal.getName();

			BigDecimal accountBalance = userService.getAccountBalance(connectedUserUsername);
			return ResponseEntity.status(HttpStatus.OK).body(accountBalance);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

	}

	@PostMapping("/account/recharge")
	public ResponseEntity<Void> rechargeAccount(Principal principal) {
		try {
			String connectedUserUsername = principal.getName();

			userService.addFixedAmountToAccount(connectedUserUsername);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
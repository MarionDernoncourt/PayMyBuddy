package com.paymybuddy.backend.dto;

public class UsernameDTO {

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UsernameDTO(String username) {
		super();
		this.username = username;
	}
	public UsernameDTO() {};
}

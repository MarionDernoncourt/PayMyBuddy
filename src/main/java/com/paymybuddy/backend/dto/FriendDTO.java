package com.paymybuddy.backend.dto;

public class FriendDTO {


	private String email;

	public FriendDTO() {
			}
	public FriendDTO(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

package com.paymybuddy.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class RegistrationUserDTO {

	@NotBlank(message = "L'email est obligatoire")
	private String username;
	private String email;
	private String password;
	
	public RegistrationUserDTO(String username, String email, String password) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public RegistrationUserDTO() {};
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}

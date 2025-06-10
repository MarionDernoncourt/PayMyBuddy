package com.paymybuddy.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.backend.dto.LoginUserDTO;
import com.paymybuddy.backend.dto.RegistrationUserDTO;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerIT {

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	
	
	@Test
	public void registrationUser() throws Exception {
		RegistrationUserDTO newUser = new RegistrationUserDTO("Severus", "SRogue@poudlard.com", "Lily");
		String newUserJson = objectMapper.writeValueAsString(newUser);
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(newUserJson))
				.andExpect(status().isCreated());
	}

	@Test
	public void registrationUser_withUserEmailAlreadyRegister() throws Exception {
		RegistrationUserDTO newUser = new RegistrationUserDTO("Drago", "hpotter@gryffondor", "12345");
		String newUserJson = objectMapper.writeValueAsString(newUser);
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(newUserJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void registrationUser_withUserUsernameAlreadyRegister() throws Exception {
		RegistrationUserDTO newUser = new RegistrationUserDTO("Harry", "harry@poudlard.om", "12345");
		String newUserJson = objectMapper.writeValueAsString(newUser);
		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(newUserJson))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void loginUser() throws Exception {
		LoginUserDTO loginUser = new LoginUserDTO("hermione@gryffondor.com", "123456");
		String loginUserJSON = objectMapper.writeValueAsString(loginUser);
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginUserJSON))
				.andExpect(status().isOk());
	}

	@Test
	public void loginUser_withWrongEmail() throws Exception {
		LoginUserDTO loginUser = new LoginUserDTO("harryr@gryffondor.com", "nimbus3000");
		String loginUserJSON = objectMapper.writeValueAsString(loginUser);
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginUserJSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void loginUser_withWrongPassword() throws Exception {
		LoginUserDTO loginUser = new LoginUserDTO("hpotter@gryffondor.com", "Harry");
		String loginUserJSON = objectMapper.writeValueAsString(loginUser);
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginUserJSON))
				.andExpect(status().isBadRequest());
	}


}

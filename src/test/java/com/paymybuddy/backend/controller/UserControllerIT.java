package com.paymybuddy.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;
	@MockBean
	private JwtDecoder jwtDecoder;


	@BeforeEach
	void setUp() {
		Jwt jwt = Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "test-user")
				.claim("scope", "read write").build();

		when(jwtDecoder.decode("mock-token")).thenReturn(jwt);
	}

	@Test
	public void addFriendTest() throws Exception {
		String username = "username";
		FriendDTO friend = new FriendDTO("friend@gmail.com");

		when(userService.addFriends(username, "friend@gmail.com")).thenReturn(friend);

	    String json = "{\"email\":\"friend@gmail.com\"}";
		
		mockMvc.perform(post("/api/users/friends")
				.header("Authorization", "Bearer mock-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk());
	}
}

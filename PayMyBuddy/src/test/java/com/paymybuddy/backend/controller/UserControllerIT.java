package com.paymybuddy.backend.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymybuddy.backend.dto.FriendDTO;
import com.paymybuddy.backend.dto.ProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilDTO;
import com.paymybuddy.backend.dto.UpdateProfilResponseDTO;
import com.paymybuddy.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;
	@MockBean
	private JwtDecoder jwtDecoder;

	@BeforeEach
	void setUp() {
		Jwt jwt = Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
				.claim("scope", "read write").build();

		when(jwtDecoder.decode("mock-token")).thenReturn(jwt);
	}
	
	

	@Test
	public void addFriendTest() throws Exception {
		String username = "Harry";
		FriendDTO friend = new FriendDTO("mcgonagall@poudlard.com");

		when(userService.addFriends(username, "mcgonagall@poudlard.com")).thenReturn(friend);

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(friend);

		mockMvc.perform(
				post("/api/users/addfriends")
						.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
								.claim("scope", "read write").build()))
						.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void addFriendTest_WithNonExistantEmail() throws Exception {
		String username = "Harry";
		FriendDTO friend = new FriendDTO("sophie@gmail.com");

		when(userService.addFriends(username, "sophie@gmail.com"))
				.thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(friend);

		mockMvc.perform(
				post("/api/users/addfriends")
						.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
								.claim("scope", "read write").build()))
						.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Utilisateur non trouvé")));
		;

	}

	@Test
	public void addFriendTest_WithUserConnectedEmail() throws Exception {
		String username = "Harry";
		FriendDTO friend = new FriendDTO("hpotter@gryffondor.com");

		when(userService.addFriends(username, "hpotter@gryffondor.com"))
				.thenThrow(new IllegalArgumentException("Aucun utilisateur trouvé"));

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(friend);

		mockMvc.perform(
				post("/api/users/addfriends")
						.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
								.claim("scope", "read write").build()))
						.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Aucun utilisateur trouvé")));
		;
	}
	
	@Test
	public void addFriendTest_WithUserAlreadyInFriendsList() throws Exception {
		String username = "Harry";
		FriendDTO friend = new FriendDTO("hpotter@gryffondor.com");

		when(userService.addFriends(username, "hpotter@gryffondor.com"))
				.thenThrow(new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis"));

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(friend);

		mockMvc.perform(
				post("/api/users/addfriends")
						.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
								.claim("scope", "read write").build()))
						.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Cet utilisateur est déjà dans votre liste d'amis")));
		;
	}
	
	@Test
	public void addFriendTest_WithoutAuthentication() throws Exception {
		FriendDTO friend = new FriendDTO ("ron@gryffondor.com");
		String json = new ObjectMapper().writeValueAsString(friend);
		
		mockMvc.perform(post("/api/users/addfriends").contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testGetAccountBalance() throws Exception {
		BigDecimal amount = new BigDecimal("25.50");
		
		when(userService.getAccountBalance("Harry")).thenReturn(amount);
		
		 mockMvc.perform(get("/api/users/account")
		            .with(jwt().jwt(Jwt.withTokenValue("mock-token")
		                .header("alg", "none")
		                .claim("sub", "Harry")
		                .claim("scope", "read write")
		                .build())))
		        .andExpect(status().isOk())
		        .andExpect(content().string("25.50"));
	}
	
	@Test
	public void testGetAccountBalance_WithNonExistantUser() throws Exception {
		
		when(userService.getAccountBalance("Harry")).thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));
		
		 mockMvc.perform(get("/api/users/account")
		            .with(jwt().jwt(Jwt.withTokenValue("mock-token")
		                .header("alg", "none")
		                .claim("sub", "Harry")
		                .claim("scope", "read write")
		                .build())))
		        .andExpect(status().isBadRequest());
	}
	
	@Test
	public void testGetProfilUser() throws Exception {
		
		ProfilDTO user = new ProfilDTO("Harry", "hpotter@gryffondor.com");
		
		when(userService.getUserProfil("Harry")).thenReturn(user);
		
		mockMvc.perform(get("/api/users/profil")
			       .with(jwt().jwt(Jwt.withTokenValue("mock-token")
			                .header("alg", "none")
			                .claim("sub", "Harry")
			                .claim("scope", "read write")
			                .build())))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value("hpotter@gryffondor.com"))
		.andExpect(jsonPath("$.username").value("Harry"));
		}
	
	@Test
	public void testGetProfilUser_WithNonExistantUser() throws Exception {
		
		ProfilDTO user = new ProfilDTO("Sophie", "sophie@gmail.com");
		
		when(userService.getUserProfil("Sophie")).thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));
		
		mockMvc.perform(get("/api/users/profil")
			       .with(jwt().jwt(Jwt.withTokenValue("mock-token")
			                .header("alg", "none")
			                .claim("sub", "Sophie")
			                .claim("scope", "read write")
			                .build())))
		.andExpect(status().isBadRequest());
		}
	
	@Test
	public void testGetProfilUser_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/users/profil")).andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testUpdateProfilUser() throws Exception {
		UpdateProfilDTO updateProfilDTO = new UpdateProfilDTO("newHarry", "newhpotter@gryffondor.com", "newPassword");
		
		String json = new ObjectMapper().writeValueAsString(updateProfilDTO);	
		
		UpdateProfilResponseDTO responseDTO = new UpdateProfilResponseDTO("newHarry", "newhpotter@gryffondor.com", "newPassword");
		
		when(userService.updateUserProfil(eq("Harry"), any(UpdateProfilDTO.class))).thenReturn(responseDTO);
		
		mockMvc.perform(post("/api/users/updateProfil")
				   .with(jwt().jwt(Jwt.withTokenValue("mock-token")
			                .header("alg", "none")
			                .claim("sub", "Harry")
			                .claim("scope", "read write")
			                .build()))
					.contentType(MediaType.APPLICATION_JSON)
					.content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.username").value("newHarry"))
		.andExpect(jsonPath("$.email").value("newhpotter@gryffondor.com"));
		
	}
	
	@Test
	public void testUpdateUserProfil_EmailOrUsernameAlreadyExist() throws Exception {
		 UpdateProfilDTO updateProfilDTO = new UpdateProfilDTO("Hermione", "hermione@gryffondor.com", "newPassword");
			String json = new ObjectMapper().writeValueAsString(updateProfilDTO);	

			when(userService.updateUserProfil(eq("Harry"), any(UpdateProfilDTO.class))).thenThrow(new IllegalArgumentException("Cet email est déjà utilisé par un autre utilisateur"));
			
			mockMvc.perform(post("/api/users/updateProfil")
					.with(jwt().jwt(Jwt.withTokenValue("mock-token")
			                .header("alg", "none")
			                .claim("sub", "Harry")
			                .claim("scope", "read write")
			                .build()))
					.contentType(MediaType.APPLICATION_JSON)
					.content(json))
			.andExpect(status().isBadRequest());
		 
	}
	
}

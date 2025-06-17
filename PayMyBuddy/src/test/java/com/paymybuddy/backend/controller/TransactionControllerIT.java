package com.paymybuddy.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

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
import com.paymybuddy.backend.dto.SendTransactionDTO;
import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.service.TransactionService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)

public class TransactionControllerIT {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	TransactionService transactionService;
	@MockBean
	private JwtDecoder jwtDecoder;

	@BeforeEach
	void setUp() {
		Jwt jwt = Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
				.claim("scope", "read write").build();

		when(jwtDecoder.decode("mock-token")).thenReturn(jwt);
	}

	@Test
	public void testGeReceivedTransactions() throws Exception {

		String username = "Harry";

		List<TransactionDTO> transactions = List
				.of(new TransactionDTO("description", BigDecimal.valueOf(100.0), "Harry"));

		when(transactionService.getSentTransactions(username)).thenReturn(transactions);

		mockMvc.perform(get("/api/transactions/").with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none")
				.claim("sub", "Harry").claim("scope", "read write").build())).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].description").value("description"));
	}

	@Test
	public void testGetReceivedTransactions_WithUnknownUser() throws Exception {

		String username = "unknown";

		when(transactionService.getSentTransactions(username)).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/api/transactions/").with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none")
				.claim("sub", "unknown").claim("scope", "read write").build())).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGeReceivedTransactions_ShouldReturnSecurityException() throws Exception {
		String username = "hacker";

		mockMvc.perform(get("/api/transactions/").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

	}

	@Test
	public void testSendTransaction() throws Exception {
		String username = "Harry";

		SendTransactionDTO transaction = new SendTransactionDTO("cinéma", BigDecimal.valueOf(10.00), "Harry",
				"friend@mail.com");

		SendTransactionDTO returnedTransaction = new SendTransactionDTO("cinéma", BigDecimal.valueOf(10.00), "Harry",
				"friend@mail.com");

		when(transactionService.sendTransaction(eq(username), any(SendTransactionDTO.class)))
				.thenReturn(returnedTransaction);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonTransaction = objectMapper.writeValueAsString(transaction);

		System.out.println("test sysout : " + transaction.getDescription());
		mockMvc.perform(post("/api/transactions/transaction")
				.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
						.claim("scope", "read write").build()))
				.contentType(MediaType.APPLICATION_JSON).content(jsonTransaction)).andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("cinéma")).andExpect(jsonPath("$.amount").value(10.00))
				.andExpect(jsonPath("$.description").value("cinéma"));
	}

	@Test
	public void testSendTransaction_ToUnknowUser() throws Exception {
		String username = "Harry";

		SendTransactionDTO transaction = new SendTransactionDTO("cinéma", BigDecimal.valueOf(10.00), "Harry",
				"friend@mail.com");

		when(transactionService.sendTransaction(eq(username), any(SendTransactionDTO.class)))
				.thenThrow(new IllegalArgumentException("Utilisateur non trouvé"));

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonTransaction = objectMapper.writeValueAsString(transaction);

		mockMvc.perform(post("/api/transactions/transaction")
				.with(jwt().jwt(Jwt.withTokenValue("mock-token").header("alg", "none").claim("sub", "Harry")
						.claim("scope", "read write").build()))
				.contentType(MediaType.APPLICATION_JSON).content(jsonTransaction)).andExpect(status().isBadRequest());
	}
	
	@Test
	public void testSendTransaction_Unauthorized() throws Exception {
		String username = "Harry";

		SendTransactionDTO transaction = new SendTransactionDTO("cinéma", BigDecimal.valueOf(10.00), "Harry",
				"friend@mail.com");

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonTransaction = objectMapper.writeValueAsString(transaction);

		mockMvc.perform(post("/api/transactions/transaction")
				
				.contentType(MediaType.APPLICATION_JSON).content(jsonTransaction)).andExpect(status().isUnauthorized());
	}
}

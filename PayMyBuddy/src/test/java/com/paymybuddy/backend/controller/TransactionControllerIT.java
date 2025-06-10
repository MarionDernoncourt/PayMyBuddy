package com.paymybuddy.backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

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

	@Test
	public void testGetUserTransactions() throws Exception {

		String username = "username";

		TransactionDTO transaction = new TransactionDTO("description", BigDecimal.valueOf(100.0), "receiver");

		List<TransactionDTO> transactions = List.of(
				new TransactionDTO("description", BigDecimal.valueOf(100.0), "receiver"));

		when(transactionService.getReceivedTransactions(username)).thenReturn(transactions);

		mockMvc.perform(get("/api/transactions/").with(jwt().jwt(jwt -> jwt.claim("sub", username)))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("description"));
	}

	@Test
	public void testGetUserTransactions_WithUnknownUser() throws Exception {

		String username = "unknown";

		TransactionDTO transaction = new TransactionDTO("description", BigDecimal.valueOf(100.0), "receiver");

		List<TransactionDTO> transactions = List.of(
				new TransactionDTO("description", BigDecimal.valueOf(100.0),"receiver"));

		when(transactionService.getReceivedTransactions(username)).thenThrow(new IllegalArgumentException());

		mockMvc.perform(get("/api/transactions/").with(jwt().jwt(jwt -> jwt.claim("sub", "unknown")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetUserTransactions_ShouldReturnSecurityException() throws Exception {
		String username = "hacker";

		TransactionDTO transaction = new TransactionDTO("description", BigDecimal.valueOf(100.0), "receiver");

		List<TransactionDTO> transactions = List.of(
				new TransactionDTO("description", BigDecimal.valueOf(100.0), "receiver"));

		when(transactionService.getReceivedTransactions(username)).thenThrow(new SecurityException());

		mockMvc.perform(get("/api/transactions/")
				.with(jwt().jwt(jwt -> jwt.claim("sub", "hacker")))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());

	}
}



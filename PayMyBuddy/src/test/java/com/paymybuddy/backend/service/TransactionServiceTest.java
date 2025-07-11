package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.backend.dto.SendTransactionDTO;
import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.ITransactionRepository;
import com.paymybuddy.backend.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

	@Mock
	private ITransactionRepository transactionRepository;
	@Mock
	private IUserRepository userRepository;
	@InjectMocks
	private TransactionService transactionService;

	private User sender;
	private User receiver;
	private Transaction transaction;
	private SendTransactionDTO transactionDTO;

	@BeforeEach
	void setUp() {
		sender = new User();
		sender.setId(1);
		sender.setUsername("sender");
		sender.setEmail("sender@gmail.com");
		sender.setAccountBalance(BigDecimal.valueOf(100.00));

		receiver = new User();
		receiver.setId(2);
		receiver.setUsername("receiver");
		receiver.setEmail("receiver@gmail.com");
		receiver.setAccountBalance(BigDecimal.valueOf(20.00));

		transaction = new Transaction();
		transaction.setDescription("Test transaction");
		transaction.setAmount(BigDecimal.valueOf(50.00));
		transaction.setSender(sender);
		transaction.setReceiver(receiver);

		transactionDTO = new SendTransactionDTO();
		transactionDTO.setSenderEmail("sender@gmail.com");
		transactionDTO.setReceiverUsername("receiver");
		transactionDTO.setAmount(BigDecimal.valueOf(20.00));
		transactionDTO.setDescription("Lunch payment");
	}

	@Test
	public void testGetUserTransactions() {

		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));
		when(transactionRepository.findBySender(any(User.class)))
				.thenReturn(List.of(transaction));

		List<TransactionDTO> result = transactionService.getSentTransactions("sender");

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Test transaction", result.get(0).getDescription());
	}

	@Test
	public void testSendTransaction() {
		String connectedUserUsername = "sender";
		BigDecimal amount = BigDecimal.valueOf(20.00);

		when(userRepository.findByUsernameIgnoreCase(connectedUserUsername)).thenReturn(Optional.of(sender));
		when(userRepository.findByUsernameIgnoreCase("receiver")).thenReturn(Optional.of(receiver));

		Transaction savedTransaction = new Transaction();
		savedTransaction.setAmount(amount);
		savedTransaction.setDescription("Lunch Payment");
		savedTransaction.setSender(sender);
		savedTransaction.setReceiver(receiver);

		when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

		SendTransactionDTO result = transactionService.sendTransaction(connectedUserUsername, transactionDTO);

		assertNotNull(result);
		assertEquals("Lunch Payment", result.getDescription());

		assertEquals(BigDecimal.valueOf(80.00), sender.getAccountBalance());
		assertEquals(BigDecimal.valueOf(40.00), receiver.getAccountBalance());

	}

	@Test
	public void testSendTransaction_userNotFount() {
		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));

		assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transactionDTO);
		});
	}

	@Test
	public void testSendTransaction_insufficientBalance() {
		sender.setAccountBalance(BigDecimal.valueOf(10.00));
		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));

		assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transactionDTO);
		});
	}
	
	
}

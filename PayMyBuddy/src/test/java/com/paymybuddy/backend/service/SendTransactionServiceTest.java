package com.paymybuddy.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.backend.dto.SendTransactionDTO;
import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.ITransactionRepository;
import com.paymybuddy.backend.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
public class SendTransactionServiceTest {

	@Mock
	private IUserRepository userRepository;
	@Mock
	private ITransactionRepository transactionRepository;
	@InjectMocks
	private TransactionService transactionService;

	private User sender;
	private User receiver;
	private SendTransactionDTO transaction;

	@BeforeEach
	void setUp() {
		sender = new User();
		sender.setUsername("sender");
		sender.setEmail("sender@mail.com");
		sender.setAccountBalance(BigDecimal.valueOf(50.00));

		receiver = new User();
		receiver.setUsername("receiver");
		receiver.setEmail("receiver@mail.com");
		receiver.setAccountBalance(BigDecimal.valueOf(20.00));

		transaction = new SendTransactionDTO();
		transaction.setDescription("restaurant");
		transaction.setAmount(BigDecimal.valueOf(10.00));
		transaction.setSenderEmail("sender@mail.com");
		;
		transaction.setReceiverUsername("receiver");

	}

	@Test
	public void testSendTransaction() {
		String connectedUsername = "sender";

		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));
		when(userRepository.findByUsernameIgnoreCase("receiver")).thenReturn(Optional.of(receiver));
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction tx = invocation.getArgument(0);
			tx.setSender(sender);
			tx.setReceiver(receiver);
			return tx;
		});

		SendTransactionDTO response = transactionService.sendTransaction(connectedUsername, transaction);

		assertEquals(BigDecimal.valueOf(40.00), sender.getAccountBalance());
		assertEquals(BigDecimal.valueOf(30.00), receiver.getAccountBalance());

		assertEquals("restaurant", response.getDescription());
		assertEquals("sender@mail.com", response.getSenderEmail());
		assertEquals("receiver", response.getReceiverUsername());
		assertEquals(BigDecimal.valueOf(10.00), response.getAmount());
	}

	@Test
	public void testSendTransaction_WhenReceiverUserNotFound() {
		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));
		when(userRepository.findByUsernameIgnoreCase("receiver")).thenReturn(Optional.empty());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transaction);
		});

		assertEquals("Destinataire non trouvé", exception.getMessage());
	}

	@Test
	public void testSendTransacion_WhenConnectedUserIsNotSender() {

		transaction.setSenderEmail("not-harry@mail.com");
		when(userRepository.findByUsernameIgnoreCase("Harry")).thenReturn(Optional.of(sender));
		when(userRepository.findByUsernameIgnoreCase("receiver")).thenReturn(Optional.of(receiver));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> transactionService.sendTransaction("Harry", transaction));

		assertEquals("L'utilisateur connecté n'est pas l'expéditeur", exception.getMessage());
	}

	@Test
	public void testSendTransaction_WhenInsufficientBalance() {
		transaction.setAmount(BigDecimal.valueOf(100.00));

		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));
		when(userRepository.findByUsernameIgnoreCase("receiver")).thenReturn(Optional.of(receiver));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transaction);
		});

		assertEquals("Solde insuffisant", exception.getMessage());
	}

	@Test
	public void testSendTransaction_WhenSenderIsReceiver() {
		transaction.setReceiverUsername("sender");

		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.of(sender));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transaction);
		});

		assertEquals("Vous ne pouvez pas vous envoyer de l'argent à vous meme", exception.getMessage());
	}
	
	@Test
	public void testSendTransaction_WhenConnectedUserIsNotFound() {
		when(userRepository.findByUsernameIgnoreCase("sender")).thenReturn(Optional.empty());
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.sendTransaction("sender", transaction);
		});
		
		assertEquals("Utilisateur non trouvé", exception.getMessage());
		
		
	}

}

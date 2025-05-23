package com.paymybuddy.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.model.User;
import com.paymybuddy.backend.repository.TransactionRepository;
import com.paymybuddy.backend.repository.UserRepository;

@Service
public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	private UserRepository userRepository;
	private TransactionRepository transactionRepository;
	private UserService userService;

	public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository,
			UserService userService) {
		this.userRepository = userRepository;
		this.transactionRepository = transactionRepository;
		this.userService = userService;
	}

	public List<TransactionDTO> getUserTransactions(String username) {
		logger.info("Tentative de récupération des transactions du user : {}", username);

		User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> {
			logger.warn("Utilisateur {} non trouvé", username);
			return new IllegalArgumentException("Utilisateur non trouvé");
		});

		List<Transaction> transactions = transactionRepository.findBySenderOrReceiver(user, user);

		List<TransactionDTO> transactionDTO = transactions.stream()
				.map(transaction -> new TransactionDTO(transaction.getDescription(), transaction.getAmount(),
						transaction.getSender().getEmail(), transaction.getReceiver().getEmail()))
				.toList();

		logger.info("Nombre de transactions trouvées : {}", transactionDTO.size());
		return transactionDTO;
	}

	public TransactionDTO sendTransaction(String connectedUserUsername, TransactionDTO transactionDTO) {
		logger.info("Tentative de création d'une transaction de {} à {}", transactionDTO.getSenderEmail(),
				transactionDTO.getReceiverEmail());

		User senderUser = userRepository.findByUsernameIgnoreCase(connectedUserUsername)
				.orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

		User receiverUser = userRepository.findByEmailIgnoreCase(transactionDTO.getReceiverEmail())
				.orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé"));

		if (!senderUser.getEmail().equalsIgnoreCase(transactionDTO.getSenderEmail())) {
			logger.warn("L'utilisateur connecté ({}) n'est pas l'expéditeur({})", connectedUserUsername,
					transactionDTO.getSenderEmail());
			throw new IllegalArgumentException("L'utilisateur connecté n'est pas l'expéditeur");
		}
		if (senderUser.getUsername().equalsIgnoreCase(receiverUser.getEmail())) {
			logger.warn("Echec de la transaction : Expéditeur identique à au destinataire : {}",
					transactionDTO.getSenderEmail());
			throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer de l'argent à vous meme");
		}

		validateBalance(senderUser, transactionDTO.getAmount());

		updateBalances(senderUser, receiverUser, transactionDTO.getAmount());

		Transaction transaction = createTransaction(senderUser, receiverUser, transactionDTO.getAmount(),
				transactionDTO.getDescription());

		logger.info("Montant transféré : {}€ de {} à {}", transaction.getAmount(), senderUser.getEmail(),
				receiverUser.getEmail());
		logger.info("Transaction effectuée avec succès");
		return mapToTransactionDTO(transaction);
	}

	private void validateBalance(User senderUser, BigDecimal amount) {
		if (senderUser.getAccountBalance().compareTo(amount) < 0) {
			logger.info("Solde insuffisant sur le compte {} : demandé {}€, disponible {}", senderUser.getEmail(),
					amount, senderUser.getAccountBalance());
			throw new IllegalArgumentException("Solde insuffisant");
		}
	}

	private void updateBalances(User senderUser, User receiverUser, BigDecimal amount) {
		senderUser.setAccountBalance(senderUser.getAccountBalance().subtract(amount));
		receiverUser.setAccountBalance(receiverUser.getAccountBalance().add(amount));
		userRepository.save(senderUser);
		userRepository.save(receiverUser);

	}

	public Transaction createTransaction(User senderUser, User receiverUser, BigDecimal amount, String description) {
		Transaction transaction = new Transaction();

		transaction.setAmount(amount);
		transaction.setDescription(description);
		transaction.setReceiver(receiverUser);
		transaction.setSender(senderUser);

		return transactionRepository.save(transaction);
	}

	private TransactionDTO mapToTransactionDTO(Transaction transaction) {
		return new TransactionDTO(transaction.getDescription(), transaction.getAmount(),
				transaction.getSender().getEmail(), transaction.getReceiver().getEmail());
	}

}

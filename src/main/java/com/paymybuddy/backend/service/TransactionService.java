package com.paymybuddy.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.dto.TransactionDTO;
import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.repository.TransactionRepository;

@Service
public class TransactionService {

		@Autowired
		private TransactionRepository transactionRepository;
		
		public List<TransactionDTO> getTransactions() {
			Iterable<Transaction> transactions = transactionRepository.findAll();
			
			List<TransactionDTO> dtoListTransaction = new ArrayList<>();
			
			for(Transaction transaction : transactions) {
				dtoListTransaction.add(new TransactionDTO(
						transaction.getId()	,
						transaction.getDescription(),
						transaction.getAmount(),
						transaction.getReceiver().getEmail(),
						transaction.getSender().getEmail()
						));
			}
			
			return dtoListTransaction;
				
		}
		
		public Optional<Transaction> getTransactionById(int id) {
			return transactionRepository.findById(id);
		}
		
		public Transaction saveTransaction(Transaction transaction) {
			return transactionRepository.save(transaction);
		}
}

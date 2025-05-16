package com.paymybuddy.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.repository.TransactionRepository;

@Service
public class TransactionService {

		@Autowired
		private TransactionRepository transactionRepository;
		
		public Iterable<Transaction> getTransactions() {
			return transactionRepository.findAll();
		}
		
		public Optional<Transaction> getTransactionById(int id) {
			return transactionRepository.findById(id);
		}
		
		public Transaction saveTransaction(Transaction transaction) {
			return transactionRepository.save(transaction);
		}
}

package com.paymybuddy.backend.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.model.User;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
	
	List<Transaction> findBySender(User receiver);

}

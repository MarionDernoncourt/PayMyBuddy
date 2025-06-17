package com.paymybuddy.backend.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.backend.model.Transaction;
import com.paymybuddy.backend.model.User;

/**
 * Repository Spring Data pour gérer les entités Transaction.
 * 
 * Fournit les opérations CRUD de base
 * et une méthode personnalisée pour récupérer les transactions par émetteur.
 */

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {
	
	List<Transaction> findBySender(User receiver);

}

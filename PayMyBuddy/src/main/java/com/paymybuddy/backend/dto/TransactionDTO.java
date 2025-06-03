package com.paymybuddy.backend.dto;

import java.math.BigDecimal;


public class TransactionDTO {

	public TransactionDTO(String description, BigDecimal amount,  String receiverUsername) {
		
		this.description = description;
		this.amount = amount;
		
		this.receiverUsername = receiverUsername;
	}
	
	public TransactionDTO() {
	};
	

	private String description;
	private BigDecimal amount;
	private String receiverUsername;
	
	
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
	public String getreceiverUsername() {
		return receiverUsername;
	}
	public void setreceiverUsername(String receiverEmail) {
		this.receiverUsername = receiverEmail;
	}
	
}

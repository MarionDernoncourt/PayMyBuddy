package com.paymybuddy.backend.dto;

import java.math.BigDecimal;

public class SendTransactionDTO {

	private String description;
	private BigDecimal amount;
	private String senderEmail;
	private String receiverUsername;

	public SendTransactionDTO() {};
	
	public SendTransactionDTO(String description, BigDecimal amount, String senderUsername, String receiverUsername) {
		super();
		this.description = description;
		this.amount = amount;
		this.senderEmail = senderUsername;
		this.receiverUsername = receiverUsername;
	}
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
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderUsername) {
		this.senderEmail = senderUsername;
	}
	public String getReceiverUsername() {
		return receiverUsername;
	}
	public void setReceiverUsername(String receiverUsername) {
		this.receiverUsername = receiverUsername;
	}
}

package com.paymybuddy.backend.dto;

import java.math.BigDecimal;


public class TransactionDTO {

	public TransactionDTO(String description, BigDecimal amount,  String senderEmail,
	 String receiverEmail) {
		
		this.description = description;
		this.amount = amount;
		this.senderEmail = senderEmail;
		this.receiverEmail = receiverEmail;
	}
	
	public TransactionDTO() {
	};
	

	private String description;
	private BigDecimal amount;
	private String senderEmail;
	private String receiverEmail;
	
	
	
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
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	
	public String getReceiverEmail() {
		return receiverEmail;
	}
	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}
	
}

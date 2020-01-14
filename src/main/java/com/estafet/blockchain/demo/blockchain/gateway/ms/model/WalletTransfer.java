package com.estafet.blockchain.demo.blockchain.gateway.ms.model;

public class WalletTransfer {
	
	public final static String BANK_ADDRESS = "0x68a3162c185934A0417f54E8C964E4b6Ee38F77d";
	public final static String CONTRACT_ADDRESS = "0x69e1bA170c4d853cAA5f4586C136C10D5C95925c";
	
	private int amount;
	
	private String fromAddress;
	
	private String toAddress;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	
}

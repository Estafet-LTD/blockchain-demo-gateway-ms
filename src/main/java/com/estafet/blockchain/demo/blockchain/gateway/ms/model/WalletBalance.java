package com.estafet.blockchain.demo.blockchain.gateway.ms.model;

import java.math.BigInteger;

public class WalletBalance {

	private String address;
	
	private BigInteger balance;

	public WalletBalance(String address, BigInteger balance) {
		this.address = address;
		this.balance = balance;
	}

	public WalletBalance() {	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}
	
}

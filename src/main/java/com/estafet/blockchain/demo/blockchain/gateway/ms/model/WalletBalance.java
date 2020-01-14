package com.estafet.blockchain.demo.blockchain.gateway.ms.model;

public class WalletBalance {

	private String address;
	
	private int balance;

	public WalletBalance(String address, int balance) {
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

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
}

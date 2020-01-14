package com.estafet.blockchain.demo.blockchain.gateway.ms.model;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WalletTransfer {
	
	private int amount;
	
	private String fromAddress;
	
	private String toAddress;

    public static WalletTransfer fromJSON(String message) {
        try {
            return new ObjectMapper().readValue(message, WalletTransfer.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
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

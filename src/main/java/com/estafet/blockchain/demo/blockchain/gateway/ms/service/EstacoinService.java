package com.estafet.blockchain.demo.blockchain.gateway.ms.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.BankPaymentConfirmationProducer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.UpdateWalletBalanceProducer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletBalance;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.web3j.Estacoin;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentBlockChainMessage;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentConfirmationMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.UpdateWalletBalanceMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

@Service
public class EstacoinService {

	@Autowired
	private Tracer tracer;

	@Autowired
	Web3j web3j;

	@Autowired
	BankPaymentConfirmationProducer bankPaymentConfirmationProducer;
	
	@Autowired
	UpdateWalletBalanceProducer updateWalletBalanceProducer;

	@SuppressWarnings("deprecation")
	public WalletBalance getBalance(String address) {
		Span span = tracer.buildSpan("EstacoinService.getBalance").start();
		try {
			span.setBaggageItem("address", address);
			Estacoin contract = Estacoin.load(System.getenv("CONTRACT_ADDRESS"), web3j, credentials(),
					new DefaultGasProvider());
			return new WalletBalance(address, contract.totalSupply().send().intValue());
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	private Credentials credentials() {
		return Credentials.create(System.getenv("ETHEREUM_CREDENTIALS"));
	}

	@SuppressWarnings("deprecation")
	public TransactionReceipt transfer(String fromAddress, String toAddress, int amount) {
		Span span = tracer.buildSpan("EstacoinService.transfer").start();
		try {
			span.setBaggageItem("fromAddress", fromAddress);
			span.setBaggageItem("toAddress", toAddress);
			span.setBaggageItem("amount", Integer.toString(amount));
			Estacoin contract = Estacoin.load(fromAddress, web3j, credentials(), getGasPrice(), getGasLimit());
			return contract.transfer(toAddress, BigInteger.valueOf(amount)).send();
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	private BigInteger getGasLimit() {
		return new BigInteger(System.getenv("GAS_LIMIT"));
	}

	private BigInteger getGasPrice() {
		return new BigInteger(System.getenv("GAS_PRICE"));
	}

	public TransactionReceipt transfer(WalletTransfer walletTransfer) {
		return transfer(walletTransfer.getFromAddress(), walletTransfer.getToAddress(), walletTransfer.getAmount());
	}

	public void handleBankPaymentMessage(BankPaymentBlockChainMessage message) {
		transfer(System.getenv("BANK_ADDRESS"), message.getWalletAddress(), message.getCryptoAmount());
		BankPaymentConfirmationMessage confirmationMessage = new BankPaymentConfirmationMessage();
		confirmationMessage.setSignature("hjhjhjh");
		confirmationMessage.setStatus("SUCCESS");
		confirmationMessage.setTransactionId(message.getTransactionId());
		bankPaymentConfirmationProducer.sendMessage(confirmationMessage);
	}

	private RuntimeException handleException(Span span, Exception e) {
		Tags.ERROR.set(span, true);
		Map<String, Object> logs = new HashMap<String, Object>();
		logs.put("event", "error");
		logs.put("error.object", e);
		logs.put("message", e.getMessage());
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		logs.put("stack", sw.toString());
		span.log(logs);
		return new RuntimeException(e);
	}

	public void handleWalletPaymentMessage(WalletPaymentMessage message) {
		transfer(message.getFromWalletAddress(), message.getToWalletAddress(), message.getCryptoAmount());
		UpdateWalletBalanceMessage updateWalletBalanceMessage = new UpdateWalletBalanceMessage();
		updateWalletBalanceMessage.setBalance(getBalance(message.getFromWalletAddress()).getBalance());
		updateWalletBalanceMessage.setSignature("fjdjdjdjd");
		updateWalletBalanceMessage.setWalletAddress(message.getFromWalletAddress());
		updateWalletBalanceProducer.sendMessage(updateWalletBalanceMessage);
	}

}

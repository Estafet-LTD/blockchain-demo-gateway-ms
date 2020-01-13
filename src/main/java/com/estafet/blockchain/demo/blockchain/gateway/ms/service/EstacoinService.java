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

import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.TransactionHashConfirmationProducer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.EstacoinTransfer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.web3j.Estacoin;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentBlockChainMessage;
import com.estafet.blockchain.demo.messages.lib.transaction.TransactionHashConfirmationMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

@Service
public class EstacoinService {

	private static final BigInteger GAS_PRICE = BigInteger.valueOf(1L);
	private static final BigInteger GAS_LIMIT = BigInteger.valueOf(500_000L);

	@Autowired
	private Tracer tracer;

	@Autowired
	Web3j web3j;

	@Autowired
	TransactionHashConfirmationProducer transactionHashConfirmationProducer;

	public Integer getBalance(String address) {
		Span span = tracer.buildSpan("EstacoinService.getBalance").start();
		try {
			span.setBaggageItem("address", address);
			Estacoin contract = Estacoin.load(EstacoinTransfer.BANK_ADDRESS, web3j, credentials(),
					new DefaultGasProvider());
			return contract.balanceOf(address).send().intValue();
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	private Credentials credentials() {
		return Credentials.create(System.getenv("ETHEREUM_CREDENTIALS"));
	}

	public String transfer(String fromAddress, String toAddress, int amount) {
		Span span = tracer.buildSpan("EstacoinService.transfer").start();
		try {
			span.setBaggageItem("fromAddress", fromAddress);
			span.setBaggageItem("toAddress", toAddress);
			span.setBaggageItem("amount", Integer.toString(amount));
			Estacoin contract = Estacoin.load(fromAddress, web3j, credentials(), GAS_PRICE, GAS_LIMIT);
			TransactionReceipt tr = contract.transfer(toAddress, BigInteger.valueOf(amount)).send();
			return tr.getTransactionHash();
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	public String transfer(EstacoinTransfer estacoinTransfer) {
		return transfer(estacoinTransfer.getFromAddress(), estacoinTransfer.getToAddress(),
				estacoinTransfer.getAmount());
	}

	public void handleBankPaymentMessage(BankPaymentBlockChainMessage message) {
		String hash = transfer(EstacoinTransfer.BANK_ADDRESS, message.getWalletAddress(), message.getCryptoAmount());
		confirmHash(message.getTransactionId(), hash);
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
		String hash = transfer(message.getFromWalletAddress(), message.getToWalletAddress(), message.getCryptoAmount());
		confirmHash(message.getTransactionId(), hash);
	}

	private void confirmHash(String transactionId, String hash) {
		TransactionHashConfirmationMessage transactionHashConfirmationMessage = new TransactionHashConfirmationMessage();
		transactionHashConfirmationMessage.setHash(hash);
		transactionHashConfirmationMessage.setTransactionId(transactionId);
		transactionHashConfirmationProducer.sendMessage(transactionHashConfirmationMessage);
	}
}

package com.estafet.blockchain.demo.blockchain.gateway.ms.service;

import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.BankPaymentConfirmationProducer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.UpdateWalletBalanceProducer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.jms.UpdateWalletReceiverBalanceProducer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Service
public class EstacoinService {

	Logger logger = LoggerFactory.getLogger(EstacoinService.class);

	@Autowired
	private Tracer tracer;

	@Autowired
	Web3j web3j;

	@Autowired
	BankPaymentConfirmationProducer bankPaymentConfirmationProducer;

	@Autowired
	UpdateWalletBalanceProducer updateWalletBalanceProducer;

	@Autowired
	UpdateWalletReceiverBalanceProducer updateWalletReceiverBalanceProducer;

	Estacoin contract = null;

	@PostConstruct
	public void init() {
		this.contract = Estacoin.load(System.getenv("CONTRACT_ADDRESS"), web3j, credentials(),
				new DefaultGasProvider());
	}

	@SuppressWarnings("deprecation")
	public WalletBalance getBalance(String address) {
		logger.info("start getBalance ");
		Span span = tracer.buildSpan("EstacoinService.getBalance").start();
		try {
			span.setBaggageItem("address", address);
			logger.info("getBalance address =" + address);
			return new WalletBalance(address, contract.balanceOf(address).send());
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	@SuppressWarnings("deprecation")
	public WalletBalance getBankTotalSupply() {
		logger.info("start getBankTotalSupply ");
		Span span = tracer.buildSpan("EstacoinService.getBankTotalSupply").start();
		try {
			return new WalletBalance(null, contract.totalSupply().send());
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	@SuppressWarnings("deprecation")
	public TransactionReceipt transferEstacoinFromBank(String toAddress, BigInteger amount) {
		logger.info("start transferEstacoinFromBank ");
		Span span = tracer.buildSpan("EstacoinService.transferEstacoinFromBank").start();
		try {
			span.setBaggageItem("address", toAddress);
			span.setBaggageItem("amount", String.valueOf(amount));
			logger.info("transferEstacoinFromBank toAddress =" + toAddress+" amount= "+amount);
			TransactionReceipt receipt = contract.transfer(toAddress, amount).send();
			span.setBaggageItem("transactionHash", receipt.getTransactionHash());
			span.setBaggageItem("blockHash", receipt.getBlockHash());
			return receipt;
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
	public TransactionReceipt transfer(String fromAddress, String toAddress, BigInteger amount) {
		logger.info("start transfer ");
		Span span = tracer.buildSpan("EstacoinService.transfer").start();
		try {
			span.setBaggageItem("fromAddress", fromAddress);
			span.setBaggageItem("toAddress", toAddress);
			span.setBaggageItem("amount", String.valueOf(amount));
			logger.info("transfer toAddress =" + toAddress+" amount= "+amount+" fromAddress="+fromAddress);
			TransactionReceipt receipt = contract.transferWalletToWallet(fromAddress, toAddress, amount).send();
			span.setBaggageItem("transactionHash", receipt.getTransactionHash());
			span.setBaggageItem("blockHash", receipt.getBlockHash());
			return receipt;
		} catch (Exception e) {
			throw handleException(span, e);
		} finally {
			span.finish();
		}
	}

	public TransactionReceipt transfer(WalletTransfer walletTransfer) {
		return transfer(walletTransfer.getFromAddress(), walletTransfer.getToAddress(), walletTransfer.getAmount());
	}

	public TransactionReceipt transferEstacoinFromBank(WalletTransfer walletTransfer) {
		return transferEstacoinFromBank(walletTransfer.getToAddress(), walletTransfer.getAmount());
	}

	public void handleBankPaymentMessage(BankPaymentBlockChainMessage message) {
		logger.info("start handleBankPaymentMessage ");
		transferEstacoinFromBank(message.getWalletAddress(),
				new BigInteger(Integer.toString(message.getCryptoAmount())));

		BankPaymentConfirmationMessage confirmationMessage = new BankPaymentConfirmationMessage();
		confirmationMessage.setSignature("hjhjhjh");
		confirmationMessage.setStatus("SUCCESS");
		confirmationMessage.setTransactionId(message.getTransactionId());

		bankPaymentConfirmationProducer.sendMessage(confirmationMessage);

		logger.info("BankPaymentConfirmationMessage from BankPaymentConsumer = "+confirmationMessage.toJSON());

		UpdateWalletBalanceMessage updateWalletReceiverBalanceMessage = getUpdateWalletBalanceMessage(
				message.getWalletAddress());
		updateWalletReceiverBalanceProducer.sendMessage(updateWalletReceiverBalanceMessage);

		logger.info("UpdateWalletBalanceMessage receiver = "+updateWalletReceiverBalanceMessage.toJSON());
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
		logger.info("start handleWalletPaymentMessage ");
		transfer(message.getFromWalletAddress(), message.getToWalletAddress(),
				BigInteger.valueOf(message.getCryptoAmount()));

		UpdateWalletBalanceMessage updateWalletSenderBalanceMessage = getUpdateWalletBalanceMessage(
				message.getFromWalletAddress());
		updateWalletBalanceProducer.sendMessage(updateWalletSenderBalanceMessage);

		UpdateWalletBalanceMessage updateWalletReceiverBalanceMessage = getUpdateWalletBalanceMessage(
				message.getToWalletAddress());
		updateWalletReceiverBalanceProducer.sendMessage(updateWalletReceiverBalanceMessage);
	}

	private UpdateWalletBalanceMessage getUpdateWalletBalanceMessage(String walletAddress) {
		UpdateWalletBalanceMessage updateWalletBalanceMessage = new UpdateWalletBalanceMessage();
		updateWalletBalanceMessage.setBalance(getBalance(walletAddress).getBalance().intValue());
		updateWalletBalanceMessage.setSignature("fjdjdjdjd");
		updateWalletBalanceMessage.setWalletAddress(walletAddress);
		return updateWalletBalanceMessage;
	}

}

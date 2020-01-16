package com.estafet.blockchain.demo.bank.ms.container.tests;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.net.HttpURLConnection;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentBlockChainMessage;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentConfirmationMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.UpdateWalletBalanceMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;
import com.estafet.demo.commons.lib.wallet.WalletUtils;
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ITBlockchainGatewayTest {

	BankPaymentConfirmationTopicConsumer bankPaymentConfirmationTopicConsumer = new BankPaymentConfirmationTopicConsumer();
	UpdateWalletBalanceTopicConsumer updateWalletBalanceTopicConsumer = new UpdateWalletBalanceTopicConsumer();	
	
	@Before
	public void before() {
		RestAssured.baseURI = PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	@After
	public void after() {
		bankPaymentConfirmationTopicConsumer.closeConnection();
		updateWalletBalanceTopicConsumer.closeConnection();
	}

	@Test
	public void testRestTransfer() {
		String bankAddress = PropertyUtils.instance().getProperty("BANK_ADDRESS");
		String toAddress = WalletUtils.generateWalletAddress();
		WalletTransfer transfer = new WalletTransfer();
		transfer.setAmount(new BigInteger("40"));
		transfer.setFromAddress(bankAddress);
		transfer.setToAddress(toAddress);
		
		given().contentType(ContentType.JSON)
			.body(transfer.toJSON())
			.when()
				.post("/transfer")
			.then()
				.statusCode(HttpURLConnection.HTTP_OK);
		
		get("/balance/" + bankAddress).then()
			.statusCode(HttpURLConnection.HTTP_OK)
			.body("balance", is(40));		
	}
	
	@Test
	public void testBank2Wallet() {
		String walletAddress = WalletUtils.generateWalletAddress();
		BankPaymentBlockChainMessage bankPaymentBlockChainMessage = new BankPaymentBlockChainMessage();
		bankPaymentBlockChainMessage.setCryptoAmount(40);
		bankPaymentBlockChainMessage.setTransactionId("dhdhdhd");
		bankPaymentBlockChainMessage.setWalletAddress(walletAddress);
		bankPaymentBlockChainMessage.setSignature("djddjdj");
		BankPaymentTopicProducer.send(bankPaymentBlockChainMessage.toJSON());
		BankPaymentConfirmationMessage confirmationMessage = bankPaymentConfirmationTopicConsumer.consume();
		assertEquals("dhdhdhd", confirmationMessage.getTransactionId());
		get("/balance/" + walletAddress).then()
			.statusCode(HttpURLConnection.HTTP_OK)
			.body("balance", is(547447373));
	}
	
	@Test
	public void testWallet2Wallet() {
		String bankAddress = PropertyUtils.instance().getProperty("BANK_ADDRESS");
		String wallet1 = WalletUtils.generateWalletAddress();
		String wallet2 = WalletUtils.generateWalletAddress();
		
		WalletTransfer initialTransferWallet1 = new WalletTransfer();
		initialTransferWallet1.setAmount(new BigInteger("240"));
		initialTransferWallet1.setFromAddress(bankAddress);
		initialTransferWallet1.setToAddress(wallet1);
		
		get("/balance/" + wallet1).then()
			.statusCode(HttpURLConnection.HTTP_OK)
			.body("balance", is(240));
		
		WalletTransfer initialTransferWallet2 = new WalletTransfer();
		initialTransferWallet2.setAmount(new BigInteger("30"));
		initialTransferWallet2.setFromAddress(bankAddress);
		initialTransferWallet2.setToAddress(wallet2);
		
		get("/balance/" + wallet2).then()
			.statusCode(HttpURLConnection.HTTP_OK)
			.body("balance", is(30));
		
		WalletPaymentMessage walletPaymentMessage = new WalletPaymentMessage();
		walletPaymentMessage.setCryptoAmount(70);
		walletPaymentMessage.setFromWalletAddress(wallet1);
		walletPaymentMessage.setToWalletAddress(wallet2);
		walletPaymentMessage.setTransactionId("djssjsj");
		walletPaymentMessage.setSignature("djsjajaaj");
		
		WalletPaymentTopicProducer.send(walletPaymentMessage.toJSON());
		UpdateWalletBalanceMessage updateWalletBalanceMessage = updateWalletBalanceTopicConsumer.consume();
		assertEquals(100, updateWalletBalanceMessage.getBalance());
		
	}	
	

}

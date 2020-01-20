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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentBlockChainMessage;
import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentConfirmationMessage;
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class ITBankTransferJMS {

	BankPaymentConfirmationTopicConsumer topic = new BankPaymentConfirmationTopicConsumer();
	
	@Before
	public void before() {
		RestAssured.baseURI = PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	@After
	public void after() {
		topic.closeConnection();
	}


	@Test
	public void testBank2Wallet() throws InterruptedException {
		String walletAddress = WalletTestUtils.generateWalletAddress();
		BankPaymentBlockChainMessage bankPaymentBlockChainMessage = new BankPaymentBlockChainMessage();
		bankPaymentBlockChainMessage.setCryptoAmount(40);
		bankPaymentBlockChainMessage.setTransactionId("dhdhdhd");
		bankPaymentBlockChainMessage.setWalletAddress(walletAddress);
		bankPaymentBlockChainMessage.setSignature("djddjdj");
		BankPaymentTopicProducer.send(bankPaymentBlockChainMessage.toJSON());
		Thread.sleep(20000);
		BankPaymentConfirmationMessage confirmationMessage = topic.consume();
		assertEquals("dhdhdhd", confirmationMessage.getTransactionId());
	}
	

}

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
import com.estafet.blockchain.demo.messages.lib.wallet.UpdateWalletBalanceMessage;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

import io.restassured.RestAssured;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class ITWalletTransferJMS {

	UpdateWalletBalanceTopicConsumer topic = new UpdateWalletBalanceTopicConsumer();	
	
	@Before
	public void before() {
		RestAssured.baseURI = PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	@After
	public void after() {
		topic.closeConnection();
	}

	@Test
	public void testWallet2Wallet() throws InterruptedException {
		String wallet1 = WalletTestUtils.generateWalletAddress();
		String wallet2 = WalletTestUtils.generateWalletAddress();
		
		WalletTestUtils.initialiseAddress(wallet1, 240);
		WalletTestUtils.initialiseAddress(wallet2, 30);
		
		WalletPaymentMessage walletPaymentMessage = new WalletPaymentMessage();
		walletPaymentMessage.setCryptoAmount(70);
		walletPaymentMessage.setFromWalletAddress(wallet1);
		walletPaymentMessage.setToWalletAddress(wallet2);
		walletPaymentMessage.setTransactionId("djssjsj");
		walletPaymentMessage.setSignature("djsjajaaj");
		
		WalletPaymentTopicProducer.send(walletPaymentMessage.toJSON());
		Thread.sleep(20000);
		UpdateWalletBalanceMessage updateWalletBalanceMessage = topic.consume();
		assertEquals(100, updateWalletBalanceMessage.getBalance());
		
	}	
	

}

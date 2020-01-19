package com.estafet.blockchain.demo.bank.ms.container.tests;

import java.math.BigInteger;
import java.net.HttpURLConnection;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class ITBankTransferRestAPI {

	@Before
	public void before() {
		RestAssured.baseURI = PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	@Test
	public void testBankToWalletTransferRestAPI() {
		String toAddress = WalletTestUtils.generateWalletAddress();
		WalletTransfer transfer = new WalletTransfer();
		transfer.setAmount(new BigInteger("40"));
		transfer.setToAddress(toAddress);

		given().contentType(ContentType.JSON).body(transfer.toJSON()).when().post("/transfer-from-bank").then()
				.statusCode(HttpURLConnection.HTTP_OK);

		get("/balance/" + toAddress).then().statusCode(HttpURLConnection.HTTP_OK).body("balance", is(40));

	}

	@Test
	public void testBankToWalletTestUtils() {
		WalletTestUtils.initialiseAddress(WalletTestUtils.generateWalletAddress(), 230);
	}

}

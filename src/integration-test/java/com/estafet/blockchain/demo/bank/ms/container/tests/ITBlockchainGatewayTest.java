package com.estafet.blockchain.demo.bank.ms.container.tests;

import static org.junit.Assert.*;

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
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ITBlockchainGatewayTest {

	CurrencyConverterConsumer topic = new CurrencyConverterConsumer();
	
	@Before
	public void before() {
		RestAssured.baseURI = PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	@After
	public void after() {
		topic.closeConnection();
	}

	@Test
	public void testGetAccount() {
		get("/balance/" + WalletTransfer.BANK_ADDRESS).then()
			.statusCode(HttpURLConnection.HTTP_OK)
			.body("id", is(1000))
			.body("walletAddress", is("abcd"))
			.body("accountName", is("Dennis"))
			.body("publicKey", is("dddd"))
			.body("currency", is("USD"))
			.body("balance",is(150.0f))
			.body("pendingBalance",is(0.0f))
			.body("pending", is(false));		
	}

	@Test
	public void testCredit() {
		given().contentType(ContentType.JSON)
			.body("{ \"amount\": 7800.67 }")
			.when()
				.post("/account/2000/credit")
			.then()
				.statusCode(HttpURLConnection.HTTP_OK)
				.body("id", is(2000))
				.body("balance", is(13200.67f))
				.body("pending", is(false));
	}

	@Test
	public void testDedit() {
		given().contentType(ContentType.JSON)
			.body("{ \"amount\": 20.0 }")
			.when()
				.post("/account/1000/debit")
			.then()
				.statusCode(HttpURLConnection.HTTP_OK)
				.body("id", is(1000))
				.body("currency", is("USD"))
				.body("balance", is(150.00f))
				.body("pendingBalance", is(-20.00f))
				.body("pending", is(true));;
	}
	
	

}

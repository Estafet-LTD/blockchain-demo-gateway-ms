package com.estafet.blockchain.demo.bank.ms.container.tests;

import org.springframework.web.client.RestTemplate;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletAddress;
import com.estafet.microservices.scrum.lib.commons.properties.PropertyUtils;

public class WalletUtils {

	public static String generateWalletAddress() {
		return new RestTemplate()
				.postForObject(getBlockchainGatewayServiceURI() + "/generate-wallet-account", null, WalletAddress.class)
				.getAddress();
	}

	private static String getBlockchainGatewayServiceURI() {
		return PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

}

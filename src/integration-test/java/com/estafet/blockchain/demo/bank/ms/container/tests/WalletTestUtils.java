package com.estafet.blockchain.demo.bank.ms.container.tests;

import java.math.BigInteger;

import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletAddress;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletBalance;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.openshift.boost.commons.lib.properties.PropertyUtils;

public class WalletTestUtils {

	public static String generateWalletAddress() {
		return new RestTemplate()
				.postForObject(getBlockchainGatewayServiceURI() + "/generate-wallet-account", null, WalletAddress.class)
				.getAddress();
	}

	private static String getBlockchainGatewayServiceURI() {
		return PropertyUtils.instance().getProperty("BLOCKCHAIN_GATEWAY_MS_SERVICE_URL");
	}

	public static void initialiseAddress(String toAddress, int amount) {
		WalletTransfer transfer = new WalletTransfer();
		transfer.setAmount(new BigInteger(Integer.toString(amount)));
		transfer.setToAddress(toAddress);
		new RestTemplate().postForObject(getBlockchainGatewayServiceURI() + "/transfer-from-bank", transfer,
				TransactionReceipt.class);
		if (getBalance(toAddress) != amount) {
			throw new RuntimeException("Balance transfer from bank failed");
		}
	}

	public static int getBalance(String address) {
		return new RestTemplate()
				.getForObject(getBlockchainGatewayServiceURI() + "/balance/" + address, WalletBalance.class)
				.getBalance().intValue();
	}

}

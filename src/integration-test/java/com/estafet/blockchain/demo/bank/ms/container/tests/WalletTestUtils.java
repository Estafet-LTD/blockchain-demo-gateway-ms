package com.estafet.blockchain.demo.bank.ms.container.tests;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletBalance;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.boostcd.commons.properties.PropertyUtils;

public class WalletTestUtils {

	public static String generateWalletAddress() {
		try {
			String seed = UUID.randomUUID().toString();
			ECKeyPair ecKeyPair = Keys.createEcKeyPair();
			WalletFile aWallet = Wallet.createLight(seed, ecKeyPair);
			String sPrivatekeyInHex = ecKeyPair.getPrivateKey().toString(16);
			Credentials.create(sPrivatekeyInHex);
			return aWallet.getAddress();
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException
				| CipherException e) {
			throw new RuntimeException(e);
		}
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

package com.estafet.blockchain.demo.blockchain.gateway.ms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletBalance;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.service.EstacoinService;
import com.estafet.openshift.boost.commons.lib.model.API;

@RestController
public class BlockchainGatewayController {

	@Autowired
	private EstacoinService estaCoinService;

	@Value("${app.version}")
	private String appVersion;

	@GetMapping("/api")
	public API getAPI() {
		return new API(appVersion);
	}

	@GetMapping("/balance/{address}")
	public WalletBalance getBalance(@PathVariable String address) {
		return estaCoinService.getBalance(address);
	}

	@PostMapping("/transfer")
	public ResponseEntity<TransactionReceipt> transfer(@RequestBody WalletTransfer walletTransfer)
			throws Exception {
		return new ResponseEntity<TransactionReceipt>(estaCoinService.transfer(walletTransfer),
				HttpStatus.OK);
	}

	@GetMapping("/bank-total-supply")
	public ResponseEntity<WalletBalance> getBankTotalSupply()
			throws Exception {
		return new ResponseEntity<WalletBalance>(estaCoinService.getBankTotalSupply(),
				HttpStatus.OK);
	}

	@PostMapping("/transfer-from-bank")
	public ResponseEntity<TransactionReceipt> transferEstacoinFromBank(@RequestBody WalletTransfer walletTransfer )
			throws Exception {
		return new ResponseEntity<TransactionReceipt>(estaCoinService.transferEstacoinFromBank(walletTransfer),
				HttpStatus.OK);
	}
}

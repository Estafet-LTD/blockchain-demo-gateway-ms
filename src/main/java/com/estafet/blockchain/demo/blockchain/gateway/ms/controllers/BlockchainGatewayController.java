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

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.API;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletBalance;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.WalletTransfer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.service.EstacoinService;
import com.estafet.blockchain.demo.messages.lib.transaction.TransactionHashConfirmationMessage;

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
	public ResponseEntity<TransactionHashConfirmationMessage> transfer(@RequestBody WalletTransfer walletTransfer)
			throws Exception {
		return new ResponseEntity<TransactionHashConfirmationMessage>(estaCoinService.transfer(walletTransfer),
				HttpStatus.OK);
	}
}

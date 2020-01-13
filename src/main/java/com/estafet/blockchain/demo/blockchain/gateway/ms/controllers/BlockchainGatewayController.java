package com.estafet.blockchain.demo.blockchain.gateway.ms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.estafet.blockchain.demo.blockchain.gateway.ms.model.API;
import com.estafet.blockchain.demo.blockchain.gateway.ms.model.EstacoinTransfer;
import com.estafet.blockchain.demo.blockchain.gateway.ms.service.EstacoinService;

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
	public Integer getBalance(@PathVariable String address) {
		return estaCoinService.getBalance(address);
	}
	
	@PostMapping("/transfer")
	public String transfer(@RequestBody EstacoinTransfer estacoinTransfer) throws Exception {
		return estaCoinService.transfer(estacoinTransfer);
	}
}

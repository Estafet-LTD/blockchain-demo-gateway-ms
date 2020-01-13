package com.estafet.blockchain.demo.blockchain.gateway.ms.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.blockchain.demo.blockchain.gateway.ms.service.EstacoinService;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;

import io.opentracing.Tracer;

@Component
public class WalletPaymentConsumer {

	public final static String TOPIC = "wallet.payment.topic";
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EstacoinService estacoinService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			estacoinService.handleWalletPaymentMessage(WalletPaymentMessage.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();	
			}
		}
	}

}

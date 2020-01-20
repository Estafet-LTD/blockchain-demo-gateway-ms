package com.estafet.blockchain.demo.blockchain.gateway.ms.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.estafet.blockchain.demo.blockchain.gateway.ms.service.EstacoinService;
import com.estafet.blockchain.demo.messages.lib.wallet.WalletPaymentMessage;

import io.opentracing.Tracer;

@Component
public class WalletPaymentConsumer {

	Logger logger = LoggerFactory.getLogger(WalletPaymentConsumer.class);

	public final static String TOPIC = "wallet.payment.topic";
	
	@Autowired
	private Tracer tracer;
	
	@Autowired
	private EstacoinService estacoinService;

	@JmsListener(destination = TOPIC, containerFactory = "myFactory")
	public void onMessage(String message) {
		try {
			logger.info("WalletPaymentConsumer consume the message ="+message);
			estacoinService.handleWalletPaymentMessage(WalletPaymentMessage.fromJSON(message));
		} finally {
			if (tracer.activeSpan() != null) {
				tracer.activeSpan().close();	
			}
		}
	}

}

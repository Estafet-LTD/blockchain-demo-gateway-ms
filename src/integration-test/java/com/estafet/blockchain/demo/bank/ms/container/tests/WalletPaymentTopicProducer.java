package com.estafet.blockchain.demo.bank.ms.container.tests;

import com.estafet.microservices.scrum.lib.commons.jms.TopicProducer;

public class WalletPaymentTopicProducer extends TopicProducer {

	public WalletPaymentTopicProducer() {
		super("new.wallet.topic");
	}
	
	public static void send(String message) {
		new WalletPaymentTopicProducer().sendMessage(message);
	}

}

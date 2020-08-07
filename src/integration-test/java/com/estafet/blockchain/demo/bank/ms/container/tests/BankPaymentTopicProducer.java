package com.estafet.blockchain.demo.bank.ms.container.tests;

import com.estafet.boostcd.jms.TopicProducer;

public class BankPaymentTopicProducer extends TopicProducer {

	public BankPaymentTopicProducer() {
		super("currency.converter.out.topic");
	}
	
	public static void send(String message) {
		new BankPaymentTopicProducer().sendMessage(message);
	}

}

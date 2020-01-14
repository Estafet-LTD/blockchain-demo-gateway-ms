package com.estafet.blockchain.demo.bank.ms.container.tests;

import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentCurrencyConverterMessage;
import com.estafet.microservices.scrum.lib.commons.jms.TopicConsumer;

public class TransactionConfirmationTopicConsumer extends TopicConsumer {

	public TransactionConfirmationTopicConsumer() {
		super("currency.converter.input.topic");
	}

	public BankPaymentCurrencyConverterMessage consume() {
		return super.consume(BankPaymentCurrencyConverterMessage.class);
	}

}

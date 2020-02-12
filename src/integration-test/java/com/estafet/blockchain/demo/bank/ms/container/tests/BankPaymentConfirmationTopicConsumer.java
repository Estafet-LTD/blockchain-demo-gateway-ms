package com.estafet.blockchain.demo.bank.ms.container.tests;

import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentConfirmationMessage;
import com.estafet.openshift.boost.commons.lib.jms.TopicConsumer;

public class BankPaymentConfirmationTopicConsumer extends TopicConsumer {

	public BankPaymentConfirmationTopicConsumer() {
		super("bank.payment.confirmation.topic");
	}

	public BankPaymentConfirmationMessage consume() {
		return super.consume(BankPaymentConfirmationMessage.class);
	}

}

package com.estafet.blockchain.demo.bank.ms.container.tests;

import com.estafet.blockchain.demo.messages.lib.wallet.UpdateWalletBalanceMessage;
import com.estafet.boostcd.jms.TopicConsumer;

public class UpdateWalletReceiverBalanceTopicConsumer extends TopicConsumer {

	public UpdateWalletReceiverBalanceTopicConsumer() {
		super("update.wallet.receiver.balance.topic");
	}

	public UpdateWalletBalanceMessage consume() {
		return super.consume(UpdateWalletBalanceMessage.class);
	}

}

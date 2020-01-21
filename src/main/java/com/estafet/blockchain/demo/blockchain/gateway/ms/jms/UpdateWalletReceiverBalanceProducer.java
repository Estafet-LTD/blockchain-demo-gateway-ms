package com.estafet.blockchain.demo.blockchain.gateway.ms.jms;

import com.estafet.blockchain.demo.messages.lib.wallet.UpdateWalletBalanceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.UUID;

@Component
public class UpdateWalletReceiverBalanceProducer {

	@Autowired 
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(UpdateWalletBalanceMessage message) {
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.convertAndSend("update.wallet.receiver.balance.topic", message.toJSON(), new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setStringProperty("message.event.interaction.reference", UUID.randomUUID().toString());
				return message;
			}
		});
	}
}

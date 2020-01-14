package com.estafet.blockchain.demo.blockchain.gateway.ms.jms;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

import com.estafet.blockchain.demo.messages.lib.bank.BankPaymentConfirmationMessage;

@Component
public class BankPaymentConfirmationProducer {

	@Autowired 
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(BankPaymentConfirmationMessage message) {
		jmsTemplate.setPubSubDomain(true);
		jmsTemplate.convertAndSend("bank.payment.confirmation.topic", message.toJSON(), new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JMSException {
				message.setStringProperty("message.event.interaction.reference", UUID.randomUUID().toString());
				return message;
			}
		});
	}
}

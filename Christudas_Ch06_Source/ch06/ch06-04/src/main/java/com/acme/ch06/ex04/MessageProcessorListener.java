/*
 * Copyright (c) 2019/2020 Binildas A Christudas, Apress Media, LLC. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of the author, publisher or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. AUTHOR, PUBLISHER AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE AUTHOR,
 * PUBLISHER OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA,
 * OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF THE AUTHOR, PUBLISHER HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */
package com.acme.ch06.ex04;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import org.springframework.amqp.core.AmqpTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class MessageProcessorListener implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorListener.class);

    private AmqpTemplate amqpTemplate;

    public MessageProcessorListener(AmqpTemplate amqpTemplate){
		this.amqpTemplate = amqpTemplate;
	}

    public void onMessage(Message message) {

		//LOGGER.info("Start");

		/* ASSUME THAT WE RECEIVED THE HEAVY JOB FOR EXECUTION HERE */
		String messageBody = new String(message.getBody());
		String correlationId = new String(message.getMessageProperties().getCorrelationId());
		LOGGER.debug("Listener received message: " + messageBody + "; correlationId : " + correlationId);

		/* ASSUME THAT THE HEAVY JOB EXECUTION TAKES PLACE HERE */

		/* ASSUME THAT WE HAVE EXECUTED THE HEAVY JOB AND NOW WE CAN SEND THE PROCESSED RESPONSE BACK */
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setCorrelationId(correlationId.getBytes());
		Message messageToSend = new Message(messageBody.getBytes(), messageProperties);
		amqpTemplate.send("my.routingkey.1", messageToSend);
		//LOGGER.info("End");

    }

}

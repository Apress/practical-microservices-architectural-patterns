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
package com.acme.ecom.messaging;

import com.acme.ecom.dto.QuoteDTO;
import com.acme.ecom.exception.QuotesMessageRollbackException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.JMSException;
//import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class StockOrderServiceImpl implements StockOrderService{

	private static final Logger LOGGER = LoggerFactory.getLogger(StockOrderService.class);

    private JmsTemplate jmsTemplate;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

	public void sendOrderMessage(final QuoteDTO quoteDTO)throws QuotesMessageRollbackException{

		LOGGER.info("Start");

		Integer testCase = quoteDTO.getTest();
		Integer secondsToSleep = quoteDTO.getDelay();
		if(secondsToSleep > 0){
			LOGGER.debug("Sleeping for : {} seconds", secondsToSleep);
			try{
				Thread.sleep(1000 * quoteDTO.getDelay());
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
			LOGGER.debug("Waking up after : {} seconds", secondsToSleep);
		}

		jmsTemplate.send(new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {

				LOGGER.info("Inside createMessage");
				return session.createObjectMessage(quoteDTO);
			}
		});

		if(testCase == 3){
			throw new QuotesMessageRollbackException("Explicitly thrown by Message Sender to Roll Back!");
		}

		LOGGER.info("End");
	}

}

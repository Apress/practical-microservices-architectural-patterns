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
package com.acme.ecom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//import org.axonframework.amqp.eventhandling.AMQPMessageConverter;
import org.axonframework.extensions.amqp.eventhandling.AMQPMessageConverter;
//import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.extensions.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.rabbitmq.client.Channel;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Configuration
public class EcomAppConfiguration {


	private static final Logger LOGGER = LoggerFactory.getLogger(EcomAppConfiguration.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Qualifier("transactionManager")
	@Autowired
	protected PlatformTransactionManager txManager;

	@Value("${axon.amqp.exchange}")
	private String rabbitMQExchange;

	@Value("${axon.amqp.queue}")
	private String rabbitMQQueue;


	@Bean
	public FanoutExchange eventBusExchange() {
		return new FanoutExchange(rabbitMQExchange, true, false);
	}

	// Event bus queue
	@Bean
	public Queue eventBusQueue() {
		return new Queue(rabbitMQQueue, true, false, false);
	}

	// binding queue to exachange
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(eventBusQueue()).to(eventBusExchange());
	}

	 @Bean
		public SpringAMQPMessageSource myQueueMessageSource(AMQPMessageConverter messageConverter) {

		    return new SpringAMQPMessageSource(messageConverter) {
		        @RabbitListener(queues = "${axon.amqp.queue}")
		        @Override
		        public void onMessage(Message message, Channel channel) {
		        	LOGGER.debug("New event received -> "+message);
		            super.onMessage(message, channel);
		        }
		    };
		}

}

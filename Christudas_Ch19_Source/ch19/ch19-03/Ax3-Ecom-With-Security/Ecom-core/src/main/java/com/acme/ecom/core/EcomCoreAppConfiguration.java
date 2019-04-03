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
package com.acme.ecom.core;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.axonframework.amqp.eventhandling.AMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.model.GenericJpaRepository;
import org.axonframework.common.jpa.SimpleEntityManagerProvider;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import com.acme.ecom.core.inventory.model.Inventory;
import com.acme.ecom.core.order.model.Order;
import com.acme.ecom.core.order.saga.handler.OrderProcessSaga;
import com.rabbitmq.client.Channel;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Configuration
//@RefreshScope
public class EcomCoreAppConfiguration {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

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
	@Qualifier(value = "inventoryRepository")
	public GenericJpaRepository<Inventory> genericInventoryJpaRepository(EventBus eventBus) {
		SimpleEntityManagerProvider entityManagerProvider = new SimpleEntityManagerProvider(entityManager);
		GenericJpaRepository<Inventory> genericJpaRepository = new GenericJpaRepository<Inventory>(entityManagerProvider,
				Inventory.class, eventBus, ((String id) -> Long.valueOf(id)));
		return genericJpaRepository;
	}

	@Bean
	@Qualifier(value = "orderRepository")
	public GenericJpaRepository<Order> genericOrderJpaRepository(EventBus eventBus) {
		SimpleEntityManagerProvider entityManagerProvider = new SimpleEntityManagerProvider(entityManager);
		GenericJpaRepository<Order> genericJpaRepository = new GenericJpaRepository<Order>(entityManagerProvider,
				Order.class, eventBus, ((String id) -> Long.valueOf(id)));
		return genericJpaRepository;
	}

	@Bean
	public SpringAMQPMessageSource ecomCoreEventQueue(AMQPMessageConverter messageConverter) {

	    return new SpringAMQPMessageSource(messageConverter) {

	        @RabbitListener(queues = "${axon.amqp.queue}")
	        @Override
	        @Transactional
	        public void onMessage(Message message, Channel channel) {
	        	logger.debug("New event received -> "+message);
	        	//if(!message.toString().contains("OrderCreatedEvent")) {
	            super.onMessage(message, channel);
	        	//}
	        }
	    };
	}
	@Bean
	public CommandBus commandBus(org.axonframework.common.transaction.TransactionManager transactionManager) {
		SimpleCommandBus commandBus = new AsynchronousCommandBus();
		commandBus.registerHandlerInterceptor(new TransactionManagingInterceptor(transactionManager));
		return commandBus;
	}

	@Bean("orderSagaConfiguration")
	public SagaConfiguration<OrderProcessSaga> orderSagaConfiguration() {
		return SagaConfiguration.trackingSagaManager(OrderProcessSaga.class,"ECOM_CORE_EVENT_PROCESSOR");
	}

}

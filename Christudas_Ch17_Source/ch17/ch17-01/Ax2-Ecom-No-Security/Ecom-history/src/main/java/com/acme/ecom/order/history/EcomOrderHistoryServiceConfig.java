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
package com.acme.ecom.order.history;


import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.amqp.DefaultAMQPMessageConverter;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.serializer.xml.XStreamSerializer;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Configuration
public class EcomOrderHistoryServiceConfig {


	@Value("${ecom.amqp.rabbit.address}")
	private String rabbitMQAddress;

	@Value("${ecom.amqp.rabbit.username}")
	private String rabbitMQUser;

	@Value("${ecom.amqp.rabbit.password}")
	private String rabbitMQPassword;

	@Value("${ecom.amqp.rabbit.vhost}")
	private String rabbitMQVhost;

	@Value("${ecom.amqp.rabbit.exchange}")
	private String rabbitMQExchange;

	@Value("${ecom.amqp.rabbit.queue}")
	private String rabbitMQQueue;

	// Serializer
	@Bean
	public XStreamSerializer xstreamSerializer() {
		return new XStreamSerializer();
	}

	// Connection Factory
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(rabbitMQAddress);
		connectionFactory.setUsername(rabbitMQUser);
		connectionFactory.setPassword(rabbitMQPassword);
		connectionFactory.setVirtualHost(rabbitMQVhost);
		connectionFactory.setConnectionTimeout(500000);
		connectionFactory.setRequestedHeartBeat(20);
		return connectionFactory;
	}

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


	// Event bus
	@Bean
	public EventBus eventBus() {
		ClusteringEventBus clusteringEventBus = new ClusteringEventBus(new DefaultClusterSelector(simpleCluster()),
				terminal());
		return clusteringEventBus;
	}

	// Terminal
	@Bean
	public EventBusTerminal terminal() {
		SpringAMQPTerminal terminal = new SpringAMQPTerminal();
		terminal.setConnectionFactory(connectionFactory());
		terminal.setSerializer(xstreamSerializer());
		terminal.setExchangeName(rabbitMQExchange);
		terminal.setListenerContainerLifecycleManager(listenerContainerLifecycleManager());
		terminal.setDurable(true);
		terminal.setTransactional(true);
		return terminal;
	}

	// Cluster definition
	// @Bean
	SimpleCluster simpleCluster() {
		SimpleCluster simpleCluster = new SimpleCluster(rabbitMQQueue);
		return simpleCluster;
	}

	// Message converter
	@Bean
	DefaultAMQPMessageConverter defaultAMQPMessageConverter() {
		return new DefaultAMQPMessageConverter(xstreamSerializer());
	}

	// Message listener configuration
	@Bean
	ListenerContainerLifecycleManager listenerContainerLifecycleManager() {
		ListenerContainerLifecycleManager listenerContainerLifecycleManager = new ListenerContainerLifecycleManager();
		listenerContainerLifecycleManager.setConnectionFactory(connectionFactory());
		return listenerContainerLifecycleManager;
	}

	// Event listener
	@Bean
	public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor() {
		AnnotationEventListenerBeanPostProcessor processor = new AnnotationEventListenerBeanPostProcessor();
		processor.setEventBus(eventBus());
		return processor;
	}

}

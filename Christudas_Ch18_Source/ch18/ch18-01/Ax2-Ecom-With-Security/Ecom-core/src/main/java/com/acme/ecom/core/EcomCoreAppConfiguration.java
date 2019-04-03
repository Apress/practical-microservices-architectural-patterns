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

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerBeanPostProcessor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.common.jpa.SimpleEntityManagerProvider;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.axonframework.eventhandling.DefaultClusterSelector;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventBusTerminal;
import org.axonframework.eventhandling.SimpleCluster;
import org.axonframework.eventhandling.amqp.DefaultAMQPMessageConverter;
import org.axonframework.eventhandling.amqp.spring.ListenerContainerLifecycleManager;
import org.axonframework.eventhandling.amqp.spring.SpringAMQPTerminal;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.axonframework.repository.GenericJpaRepository;
import org.axonframework.saga.GenericSagaFactory;
import org.axonframework.saga.SagaManager;
import org.axonframework.saga.SagaRepository;
import org.axonframework.saga.annotation.AnnotatedSagaManager;
import org.axonframework.saga.repository.mongo.DefaultMongoTemplate;
import org.axonframework.saga.repository.mongo.MongoSagaRepository;
import org.axonframework.saga.repository.mongo.MongoTemplate;
import org.axonframework.saga.spring.SpringResourceInjector;
import org.axonframework.serializer.xml.XStreamSerializer;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.acme.ecom.core.inventory.model.Inventory;
import com.acme.ecom.core.order.model.Order;
import com.acme.ecom.core.order.saga.handler.OrderProcessSaga;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Configuration
@RefreshScope
public class EcomCoreAppConfiguration {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager entityManager;

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

	@Value("${spring.data.mongodb.uri}")
	private String mongoDdUri;


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

	// Command bus
	@Bean
	public SimpleCommandBus commandBus() {
		SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
		simpleCommandBus.setDispatchInterceptors(Arrays.asList(new BeanValidationInterceptor()));
		SpringTransactionManager transcationMgr = new SpringTransactionManager(txManager);
		simpleCommandBus.setTransactionManager(transcationMgr);
		return simpleCommandBus;
	}

	// Event bus
	@Bean
	public EventBus eventBus() {
		ClusteringEventBus clusteringEventBus = new ClusteringEventBus(new DefaultClusterSelector(simpleCluster()),
				terminal());
		clusteringEventBus.subscribe(getSagaManager());
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

	// Command Handler
	@Bean
	public AnnotationCommandHandlerBeanPostProcessor annotationCommandHandlerBeanPostProcessor() {
		AnnotationCommandHandlerBeanPostProcessor processor = new AnnotationCommandHandlerBeanPostProcessor();
		processor.setCommandBus(commandBus());
		return processor;
	}

	// Command Gateway
	@Bean
	public CommandGatewayFactoryBean<CommandGateway> commandGatewayFactoryBean() {
		CommandGatewayFactoryBean<CommandGateway> factory = new CommandGatewayFactoryBean<CommandGateway>();
		factory.setCommandBus(commandBus());
		return factory;
	}

	@Autowired
	@Qualifier("transactionManager")
	protected PlatformTransactionManager txManager;

	/**
	 * Configures a GenericJpaRepository as a Spring Bean. The Repository would
	 * be used to access the Account entity.
	 *
	 */
	@Bean
	public SimpleEntityManagerProvider getSimpleEntityManagerProvider() {
		SimpleEntityManagerProvider entityManagerProvider = new SimpleEntityManagerProvider(entityManager);
		return entityManagerProvider;
	}

	@Bean
	@Qualifier(value = "inventoryRepository")
	public GenericJpaRepository<Inventory> genericInventoryJpaRepository() {
		SimpleEntityManagerProvider entityManagerProvider = new SimpleEntityManagerProvider(entityManager);
		GenericJpaRepository<Inventory> genericJpaRepository = new GenericJpaRepository<Inventory>(
				entityManagerProvider, Inventory.class);
		genericJpaRepository.setEventBus(eventBus());
		return genericJpaRepository;
	}

	@Bean
	@Qualifier(value = "orderRepository")
	public GenericJpaRepository<Order> genericOrderJpaRepository() {
		SimpleEntityManagerProvider entityManagerProvider = new SimpleEntityManagerProvider(entityManager);
		GenericJpaRepository<Order> genericJpaRepository = new GenericJpaRepository<Order>(entityManagerProvider,
				Order.class);
		genericJpaRepository.setEventBus(eventBus());
		return genericJpaRepository;
	}

	@Bean
	SpringResourceInjector getResourceInjector() {
		return new SpringResourceInjector();
	}

   @Bean(name ="mongo")
   public Mongo getMongo(){
	try{
	   Mongo mongo =new MongoClient(new MongoClientURI(mongoDdUri));
	   return mongo;
	   }catch(Exception ex){
		   logger.error("Error while creating mongo connection",ex);
		   return null;
		}
   }

   @Bean(name ="mongoSagaTemplate")
   public MongoTemplate getSagaMongoTemplate(){
	   MongoTemplate template=new DefaultMongoTemplate(getMongo());
	   return template;

   }
	// Saga repository
	@Bean(name = "sagaRepository")
	public SagaRepository sagaRepository() {
		MongoSagaRepository repository = new MongoSagaRepository(getSagaMongoTemplate());
		repository.setResourceInjector(getResourceInjector());
		return repository;
	}

	// Saga manager
	@Bean(name = "sagaManager")
	@SuppressWarnings("unchecked")
	public SagaManager getSagaManager() {
		GenericSagaFactory sagaFactory = new GenericSagaFactory();
		sagaFactory.setResourceInjector(getResourceInjector());
		AnnotatedSagaManager sagaManager = new AnnotatedSagaManager(sagaRepository(), sagaFactory,
				OrderProcessSaga.class);
		sagaManager.setSynchronizeSagaAccess(false);
		sagaManager.setSuppressExceptions(false);
		return sagaManager;

	}

}

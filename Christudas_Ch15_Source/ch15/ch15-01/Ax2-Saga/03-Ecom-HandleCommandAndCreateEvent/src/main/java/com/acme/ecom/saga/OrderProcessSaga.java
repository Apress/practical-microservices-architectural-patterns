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
package com.acme.ecom.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.acme.ecom.order.api.event.OrderCancelledEvent;
import com.acme.ecom.order.api.event.OrderConfirmedEvent;
import com.acme.ecom.order.api.event.OrderCreatedEvent;
import com.acme.ecom.product.api.command.ProductStockRevertCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class OrderProcessSaga extends AbstractAnnotatedSaga {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessSaga.class);

	private static final long serialVersionUID = -7209131793034337691L;

	private Integer orderId;
	private Integer productId;
	private Integer count;

	 @Autowired
	 @Qualifier("distributedCommandGateway")
	 private transient CommandGateway commandGateway;

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderCreationEvent(OrderCreatedEvent orderCreatedEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order ID for OrderCreatedEvent : {}", orderCreatedEvent.getOrderId());
		orderId = orderCreatedEvent.getOrderId();
		productId = orderCreatedEvent.getProductId();
		count = orderCreatedEvent.getNumber();
		LOGGER.debug("Saga started for new order created with id: {}, for {} nos. of product with id: {}", new Object[]{orderId, count, productId});
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handleOrderCanceledEvent(OrderCancelledEvent orderCancelledEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order ID for OrderCancelledEvent : {}", orderCancelledEvent.getOrderId());
		commandGateway.send(new ProductStockRevertCommand(productId, count));// This is the compensating command
		LOGGER.debug("Saga ending for order with id: {}, Command send for reverting {} nos. of product with id: ", new Object[]{orderId, count, productId});
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handleOrderConfirmationEvent(OrderConfirmedEvent orderConfirmedEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order ID for OrderConfirmedEvent : {}", orderConfirmedEvent.getOrderId());
		LOGGER.debug("Saga ending for order with id: {}, Order confirmed.", orderId);
		LOGGER.info("End");
	}


	public CommandGateway getCommandGateway() {
		return commandGateway;
	}

	public void setCommandGateway(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
}

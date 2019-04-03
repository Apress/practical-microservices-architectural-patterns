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
package com.acme.ecom.core.order.saga.handler;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.acme.ecom.common.core.order.event.OrderCancelledEvent;
import com.acme.ecom.common.core.order.event.OrderCreatedEvent;
import com.acme.ecom.common.delivery.event.OrderDeliveredEvent;
import com.acme.ecom.common.delivery.event.OrderDeliveryFailedEvent;
import com.acme.ecom.common.shipping.event.OrderShippedEvent;
import com.acme.ecom.core.order.command.OrderDeliveryFailureRollbackCommand;
import com.acme.ecom.core.order.command.OrderUpdateCommand;
import com.acme.ecom.core.order.model.OrderStatus;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class OrderProcessSaga extends AbstractAnnotatedSaga {
	private final Logger logger = LoggerFactory.getLogger(OrderProcessSaga.class);
	private static final long serialVersionUID = -7209131793034337691L;
	private Long orderId;

	@Autowired
	private  transient CommandGateway commandGateway;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderCreationEvent(OrderCreatedEvent orderCreatedEvent) {
		logger.debug("New order created event request recieved ---->" + orderCreatedEvent.getOrderId());
		this.orderId = orderCreatedEvent.getOrderId();

	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderShippedEvent(OrderShippedEvent orderShippedEvent) {
		System.out.println("OrderProcessSaga.handleOrderShippedEvent");
		logger.debug("Order shipping event request recieved  for order---->" + orderShippedEvent.getOrderId());
		commandGateway.send(new OrderUpdateCommand(orderShippedEvent.getOrderId(),OrderStatus.SHIPPED));
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderCanceledEvent(OrderCancelledEvent orderCancelledEvent) {
		System.out.println("EndSaga: OrderProcessSaga.handleOrderCanceledEvent");
		logger.debug("Order cancelled by the user---->" + orderCancelledEvent.getOrderId());
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderDeliveredEvent(OrderDeliveredEvent orderDeliveredEvent) {
		System.out.println("EndSaga: OrderProcessSaga.handleOrderDeliveredEvent");
		logger.debug("Order delivered  event request recieved  for order---->" + orderDeliveredEvent.getOrderId());
		commandGateway.send(new OrderUpdateCommand(orderDeliveredEvent.getOrderId(),OrderStatus.DELIVERED));
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderDeliveryFailureEvent(OrderDeliveryFailedEvent orderDeliveryFailedEvent) {
		System.out.println("EndSaga: OrderProcessSaga.handleOrderDeliveryFailureEvent");
		logger.debug("Order delivery failed for order---->" + orderDeliveryFailedEvent.getOrderId());
		commandGateway.send(new OrderDeliveryFailureRollbackCommand(orderDeliveryFailedEvent.getOrderId(),
				orderDeliveryFailedEvent.getFailureReason()));
	}

	public CommandGateway getCommandGateway() {
		return commandGateway;
	}

	public void setCommandGateway(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}
}

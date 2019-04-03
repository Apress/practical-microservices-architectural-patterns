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

import java.util.List;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.ecom.common.core.dto.LineItemDTO;
import com.acme.ecom.common.core.order.event.OrderCancelledEvent;
import com.acme.ecom.common.core.order.event.OrderCreatedEvent;
import com.acme.ecom.common.delivery.event.OrderDeliveredEvent;
import com.acme.ecom.common.delivery.event.OrderDeliveryFailedEvent;
import com.acme.ecom.common.shipping.event.OrderShippedEvent;
import com.acme.ecom.core.inventory.command.InventoryUpdateCommand;
import com.acme.ecom.core.order.command.OrderDeliveryFailureRollbackCommand;
import com.acme.ecom.core.order.command.OrderUpdateCommand;
import com.acme.ecom.core.order.model.OrderStatus;
import com.acme.ecom.core.order.model.ProductStockOperation;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
@Saga(configurationBean = "orderSagaConfiguration")
public class OrderProcessSaga  {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessSaga.class);

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

	private List<LineItemDTO> orderedItems;

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderCreationEvent(OrderCreatedEvent orderCreatedEvent) {

		LOGGER.info("Start");
		LOGGER.debug("New order created event request recieved for Order with ID: {}", orderCreatedEvent.getOrderId());
		this.orderId = orderCreatedEvent.getOrderId();
		this.orderedItems=orderCreatedEvent.getLineItems();
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handleOrderShippedEvent(OrderShippedEvent orderShippedEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order shipping event request recieved for Order with ID: {}", orderShippedEvent.getOrderId());
		commandGateway.send(new OrderUpdateCommand(orderShippedEvent.getOrderId(),OrderStatus.SHIPPED));
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handleOrderCanceledEvent(OrderCancelledEvent orderCancelledEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order cancelled by the user for Order with ID: {}", orderCancelledEvent.getOrderId());
		rollbackInventory();
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handleOrderDeliveredEvent(OrderDeliveredEvent orderDeliveredEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order delivered  event request recieved for Order with ID: {}", orderDeliveredEvent.getOrderId());
		commandGateway.send(new OrderUpdateCommand(orderDeliveredEvent.getOrderId(),OrderStatus.DELIVERED));
		LOGGER.info("End");
	}

	@SagaEventHandler(associationProperty = "orderId")
	@EndSaga
	public void handleOrderDeliveryFailureEvent(OrderDeliveryFailedEvent orderDeliveryFailedEvent) {

		LOGGER.info("Start");
		LOGGER.debug("Order delivery failed for Order with ID: {}", orderDeliveryFailedEvent.getOrderId());
		commandGateway.send(new OrderDeliveryFailureRollbackCommand(orderDeliveryFailedEvent.getOrderId(),
				orderDeliveryFailedEvent.getFailureReason()));
		rollbackInventory();
		LOGGER.info("End");
	}

	private void rollbackInventory(){
		if(orderedItems!=null){
			for(LineItemDTO lineItemDTO:orderedItems){
			commandGateway.send(new InventoryUpdateCommand(lineItemDTO, ProductStockOperation.ADD));
			}
		}else{
			LOGGER.error("Order items empty for Order with ID: {} unexpected",this.orderId);
		}
	}

	public CommandGateway getCommandGateway() {
		return commandGateway;
	}

	public void setCommandGateway(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}
}

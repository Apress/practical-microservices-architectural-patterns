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
package com.acme.ecom.core.order.command.handler;

import java.util.Date;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.stereotype.Component;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.GenericJpaRepository;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.ecom.common.core.dto.LineItemDTO;
import com.acme.ecom.common.core.inventory.event.InventoryUpdateEvent;
import com.acme.ecom.core.inventory.model.Inventory;
import com.acme.ecom.core.order.command.OrderCancelCommand;
import com.acme.ecom.core.order.command.OrderCreateCommand;
import com.acme.ecom.core.order.command.OrderDeliveryFailureRollbackCommand;
import com.acme.ecom.core.order.command.OrderUpdateCommand;
import com.acme.ecom.core.order.model.LineItem;
import com.acme.ecom.core.order.model.Order;
import com.acme.ecom.core.order.model.OrderStatus;
import com.acme.ecom.core.order.model.ProductStockOperation;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class OrderCommandHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommandHandler.class);

/*
	@Autowired
	@Qualifier(value = "inventoryRepository")
	private GenericJpaRepository<Inventory> inventoryRepository;
*/
	@Autowired
	@Qualifier(value = "orderRepository")
	private GenericJpaRepository<Order> orderRepository;

	@Autowired
	private CommandGateway commandGateway;

	@Transactional
	@CommandHandler
	public void handleNewOrder(OrderCreateCommand orderCreatedCommand)throws Exception {

		LOGGER.info("Start");
		Long orderId = Long.valueOf(new Random().nextInt());
		LOGGER.debug("OrderCreationCommandHandler/create new order is executing for Order with ID: {}", orderId);
		Order order = new Order(orderId);
		order.setOrderDate(new Date());
		order.setOrderStatus(OrderStatus.PAID);
		order.setUserId(orderCreatedCommand.getUserId());
		double total = 0;
		if (orderCreatedCommand.getLineItems() != null) {
			for (LineItemDTO lineItemDto : orderCreatedCommand.getLineItems()) {
				if (lineItemDto.getInventoryId() != null) {
					LineItem lineItem = new LineItem(new Random().nextLong(), lineItemDto.getProductId(),
							lineItemDto.getQuantity(), lineItemDto.getPrice(), lineItemDto.getInventoryId());
					total = total + lineItemDto.getPrice();
					order.addLineItem(lineItem);

				}
			}
		}
		order.setTotal(total);
		Aggregate<Order> orderAggregate =  orderRepository.newInstance(()->order);
		orderAggregate.invoke((Order orderSaved) ->{orderSaved.notifyOrderCreation(); return orderSaved;});

		LOGGER.info("End");
	}

	@CommandHandler
	@Transactional
	public void handleOrderUpdate(OrderUpdateCommand orderUpdateCommand) {

		LOGGER.info("Start");
		LOGGER.debug("OrderUpdate command is executing for Order with ID: {} and Status {}", orderUpdateCommand.getOrderId(), orderUpdateCommand.getOrderStatus());
		Aggregate<Order>  orderAggregate=orderRepository.load(orderUpdateCommand.getOrderId().toString());
		orderAggregate.invoke((Order order) ->{order.updateOrderStatus(orderUpdateCommand.getOrderStatus()); return order; });
		LOGGER.info("End");
	}

	@CommandHandler
	@Transactional
	public void handleOrderCancel(OrderCancelCommand orderCancelCommand) {

		LOGGER.info("Start");
		LOGGER.debug("Order cancelling command is executing for Order with ID: {}", orderCancelCommand.getOrderId());
		Aggregate<Order>  orderAggregate=orderRepository.load(orderCancelCommand.getOrderId().toString());
		orderAggregate.invoke((Order order) ->{order.cancelOrder(); return order; });
		LOGGER.info("End");
	}

	@CommandHandler
	@Transactional
	public void handleOrderDeliveryFailure(OrderDeliveryFailureRollbackCommand  orderDeliveryFailureRollbackCommand) {

		LOGGER.info("Start");
		LOGGER.debug("Order delivery failure command is executing for Order with ID: {}", orderDeliveryFailureRollbackCommand.getOrderId());
		Aggregate<Order>  orderAggregate=orderRepository.load(orderDeliveryFailureRollbackCommand.getOrderId().toString());
		orderAggregate.invoke((Order order) ->{order.updateOrderStatus(OrderStatus.DELIVERY_FAILED); return order; });
		LOGGER.info("End");
	}



}

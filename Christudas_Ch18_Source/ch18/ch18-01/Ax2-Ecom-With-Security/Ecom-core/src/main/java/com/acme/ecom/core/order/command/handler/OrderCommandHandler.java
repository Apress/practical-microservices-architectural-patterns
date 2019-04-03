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

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.acme.ecom.common.core.dto.LineItemDTO;
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
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier(value = "inventoryRepository")
	private Repository<Inventory> inventoryRepository;

	@Autowired
	@Qualifier(value = "orderRepository")
	private Repository<Order> orderRepository;

	@CommandHandler
	public void handleNewOrder(OrderCreateCommand orderCreatedCommand) {
		logger.debug("OrderCreationCommandHandler/create new order is executing.....");
		Order order = new Order(Long.valueOf(new Random().nextInt()));
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
					Inventory inventory = inventoryRepository.load(lineItemDto.getInventoryId());
					inventory.updateProductStock(lineItemDto.getQuantity(), ProductStockOperation.DEPRECIATE);
				}
			}
		}
		order.setTotal(total);
		order.notifyOrderCreation();
		orderRepository.add(order);
	}

	@CommandHandler
	public void handleOrderUpdate(OrderUpdateCommand orderUpdateCommand) {
		logger.debug("OrderUpdate command is executing..... "+orderUpdateCommand.getOrderId()+"/"+orderUpdateCommand.getOrderStatus());
		Order order = orderRepository.load(orderUpdateCommand.getOrderId());
		order.updateOrderStatus(orderUpdateCommand.getOrderStatus());
	}

	@CommandHandler
	public void handleOrderCancel(OrderCancelCommand orderCancelCommand) {
		logger.debug("Order cancelling command is executing..... "+orderCancelCommand.getOrderId());
		Order order = orderRepository.load(orderCancelCommand.getOrderId());
		order.cancelOrder();
		rollbackInventory(order);
	}

	@CommandHandler
	public void handleOrderDeliveryFailure(OrderDeliveryFailureRollbackCommand  orderDeliveryFailureRollbackCommand) {
		logger.debug("Order delivery failure command is executing..... "+orderDeliveryFailureRollbackCommand.getOrderId());
		Order order = orderRepository.load(orderDeliveryFailureRollbackCommand.getOrderId());
		order.updateOrderStatus(OrderStatus.DELIVERY_FAILED);
		rollbackInventory(order);
	}

	private void rollbackInventory(Order order){
		for(LineItem lineItem:order.getLineItems()){
			Inventory inventory = inventoryRepository.load(lineItem.getInventoryId());
			inventory.updateProductStock(lineItem.getQuantity(), ProductStockOperation.ADD);
		}
	}

}

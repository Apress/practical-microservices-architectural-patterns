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
package com.acme.ecom.core.web;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.acme.ecom.common.core.dto.LineItemDTO;
import com.acme.ecom.common.core.dto.OrderDTO;
import com.acme.ecom.core.exception.OutOfStockException;
import com.acme.ecom.core.inventory.command.InventoryUpdateCommand;
import com.acme.ecom.core.order.command.OrderCancelCommand;
import com.acme.ecom.core.order.command.OrderCreateCommand;
import com.acme.ecom.core.order.model.ProductStockOperation;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private CommandGateway commandGateway;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public OrderCreationStatus createOrder(@RequestBody OrderDTO orderDTO) {

		LOGGER.info("Start");
		OrderCreationStatus orderCreationStatus = null;
		//Step1 :  deprecating  inventory of products.............
		List<LineItemDTO> inventoryUpdatedList = new ArrayList<>();
		try {
			for (LineItemDTO lineItemDTO : orderDTO.getLineItems()) {
				commandGateway.sendAndWait(new InventoryUpdateCommand(lineItemDTO, ProductStockOperation.DEPRECIATE));
				inventoryUpdatedList.add(lineItemDTO);
			}
		} catch (Exception ex) {
			Throwable e = ex.getCause();
			if (e instanceof OutOfStockException) {
				LOGGER.error("Error while creating new order due to Out of Stock", e);
				orderCreationStatus = OrderCreationStatus.OUT_OF_STOCK;
			} else {
				LOGGER.error("Unknown Error while updating inventory", e);
				orderCreationStatus = OrderCreationStatus.FAILED;
			}
			//Step2 : Any exception rollback updated inventory
			for (LineItemDTO lineItemDTO : inventoryUpdatedList) {
				commandGateway.send(new InventoryUpdateCommand(lineItemDTO, ProductStockOperation.ADD));
			}
			return orderCreationStatus;
		}
		//Step 3: create new order

		try {
			OrderCreateCommand orderCommand = new OrderCreateCommand(orderDTO.getUserId(), orderDTO.getLineItems());
			commandGateway.sendAndWait(orderCommand);
			orderCreationStatus = OrderCreationStatus.SUCCESS;
		} catch (Exception ex) {
				LOGGER.error("Unknown Error while creating new order", ex);
				orderCreationStatus = OrderCreationStatus.FAILED;
				// Step 4: Any exception rollback all updated inventory
				for (LineItemDTO lineItemDTO : orderDTO.getLineItems()) {
					commandGateway.send(new InventoryUpdateCommand(lineItemDTO, ProductStockOperation.ADD));
				}
		}
		LOGGER.debug("Order Creation happening for User with ID: {}, and operation is {}", orderDTO.getUserId(),
				orderCreationStatus);
		LOGGER.info("Ending");
		return orderCreationStatus;
	}

	@RequestMapping(value = "{orderId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void cancelOrder(@PathVariable Long orderId) {

		LOGGER.info("Start");
		OrderCancelCommand orderCommand = new OrderCancelCommand(orderId);
		commandGateway.send(orderCommand);
		LOGGER.debug("Order Cancell happening for Order with ID: {}", orderId);
		LOGGER.info("End");
	}
}

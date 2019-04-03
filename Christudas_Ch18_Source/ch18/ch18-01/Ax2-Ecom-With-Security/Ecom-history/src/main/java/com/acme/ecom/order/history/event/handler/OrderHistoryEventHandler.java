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
package com.acme.ecom.order.history.event.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.axonframework.domain.Message;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventhandling.annotation.Timestamp;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acme.ecom.common.core.dto.LineItemDTO;
import com.acme.ecom.common.core.order.event.OrderCreatedEvent;
import com.acme.ecom.common.core.order.event.OrderUpdatedEvent;
import com.acme.ecom.order.history.model.LineItem;
import com.acme.ecom.order.history.model.Order;
import com.acme.ecom.order.history.repository.OrderHistoryRepository;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class OrderHistoryEventHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrderHistoryRepository orderHistoryRepository;

	@EventHandler
	public void handleOrderCreationEvent(OrderCreatedEvent event, Message eventMessage,
			@Timestamp DateTime moment) {
		logger.debug("New order creation  message recieved -------->" + event.getOrderId() + " for user " + event.getUserId());
		Order order =new Order();
		order.setOrderId(event.getOrderId());
		order.setUserId(event.getUserId());
		order.setCreationDate(new Date());
		order.setOrderStatus(event.getOrderStatus());
		List<LineItem> lineItems=new ArrayList<LineItem>();
		if(event.getLineItems()!=null){
			for(LineItemDTO lineItemDTO:event.getLineItems()){
				LineItem lineItem=new LineItem();
				lineItem.setProductId(lineItemDTO.getProductId());
				lineItem.setPrice(lineItemDTO.getPrice());
				lineItem.setQuantity(lineItemDTO.getQuantity());
				lineItems.add(lineItem);
			}
		}
		order.setLineItems(lineItems);
		orderHistoryRepository.save(order);

	}

	@EventHandler
	public void handleOrderUpdatedEvent(OrderUpdatedEvent event, Message eventMessage,
			@Timestamp DateTime moment) {
		logger.debug("Order update  message recieved -------->" + event.getOrderId() + "/ " + event.getOrderStatus());
		Order order =orderHistoryRepository.findByOrderId(event.getOrderId());
		order.setOrderStatus(event.getOrderStatus());
		if(event.getOrderStatus().equals("SHIPPED")){
			order.setShippedDate(event.getDate());
		}else if(event.getOrderStatus().equals("DELIVERED")){
			order.setDeliveredDate(event.getDate());
		}else if(event.getOrderStatus().equals("CANCELLED")){
			order.setCancelledDate(event.getDate());
		}else if(event.getOrderStatus().equals("DELIVERY_FAILED")){
			order.setDeliveryFailReason(event.getDescription());
		}
		orderHistoryRepository.save(order);
	}


}

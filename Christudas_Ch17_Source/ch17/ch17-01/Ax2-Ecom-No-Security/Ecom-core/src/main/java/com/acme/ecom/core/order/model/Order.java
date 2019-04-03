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
package com.acme.ecom.core.order.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.axonframework.domain.AbstractAggregateRoot;

import com.acme.ecom.common.core.dto.LineItemDTO;
import com.acme.ecom.common.core.order.event.OrderCancelledEvent;
import com.acme.ecom.common.core.order.event.OrderCreatedEvent;
import com.acme.ecom.common.core.order.event.OrderUpdatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Entity(name="order")
@Table(name = "CUSTOMER_ORDER")
public class Order extends AbstractAggregateRoot<Long> {

	private transient final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 2717666342574509152L;

	@Id
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name="ORDER_STATUS")
	private OrderStatus orderStatus;

	@Column(name="TOTAL")
	private Double total;

	@Column(name="ORDER_DATE")
	private Date orderDate;

	@Column(name="USER_ID")
	private String userId;

	@OneToMany(cascade = CascadeType.ALL, mappedBy="order",orphanRemoval=true)
	private Set<LineItem> lineItems;

	public Order() {

	}

	public Order(Long id) {
		super();
		this.id = id;
	}

	public Set<LineItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(Set<LineItem> lineItems) {
		this.lineItems = lineItems;
	}

	public void addLineItem(LineItem lineItem){
		if(this.lineItems==null){
			this.lineItems=new HashSet<LineItem>();
		}
		lineItem.setOrder(this);
		this.lineItems.add(lineItem);
	}

	@Override
	public Long getIdentifier() {
		return this.id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void notifyOrderCreation() {
		List<LineItemDTO> lineItemDTOs = new ArrayList<LineItemDTO>();
		for (LineItem lineItem : lineItems) {
			lineItemDTOs.add(new LineItemDTO(lineItem.getProduct(), lineItem.getPrice(), lineItem.getQuantity(),
					lineItem.getInventoryId()));
		}
		registerEvent(new OrderCreatedEvent(id, userId, orderStatus.name(), total, orderDate, lineItemDTOs));
	}

	public void updateOrderStatus(OrderStatus orderStatus){
		this.orderStatus=orderStatus;
		registerEvent(new OrderUpdatedEvent(this.id, orderStatus.name(), new Date(),null));
	}

	public void cancelOrder(){
		this.orderStatus=OrderStatus.CANCELLED;
		registerEvent(new OrderUpdatedEvent(this.id, orderStatus.name(), new Date(),null));
		logger.debug("Registered OrderUpdatedEvent");
		registerEvent(new OrderCancelledEvent(this.id));
		logger.debug("Registered OrderCancelledEvent");
	}

	public void notifyOderFailure(String failureReason){
		this.orderStatus=OrderStatus.DELIVERY_FAILED;
		registerEvent(new OrderUpdatedEvent(this.id, orderStatus.name(), new Date(),failureReason));
	}

}

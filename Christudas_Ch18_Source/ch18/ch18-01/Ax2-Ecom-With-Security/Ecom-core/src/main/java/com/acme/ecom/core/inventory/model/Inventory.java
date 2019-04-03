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
package com.acme.ecom.core.inventory.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.axonframework.domain.AbstractAggregateRoot;

import com.acme.ecom.common.core.inventory.event.InventoryUpdateEvent;
import com.acme.ecom.core.exception.OutOfStockException;
import com.acme.ecom.core.order.model.ProductStockOperation;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Entity
@Table(name = "INVENTORY")
public class Inventory extends AbstractAggregateRoot<Long> {

	private static final long serialVersionUID = 2717666342574509152L;

	@Id
	private Long id;

	@Column(name="SKU")
	private String sku;

	@Column(name="QUANTITY")
	private Integer quantity;

	public Inventory() {

	}

	public Inventory(Long id, String sku, Integer quantity) {
		super();
		this.id = id;
		this.sku = sku;
		this.quantity = quantity;
		registerEvent(new InventoryUpdateEvent(id, sku, quantity));
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

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void updateProductStock(Integer count, ProductStockOperation stockoperation) {
		if (stockoperation.equals(ProductStockOperation.DEPRECIATE)) {
			if (this.quantity - count >= 0) {
				this.quantity = this.quantity - count;
			} else {
				throw new OutOfStockException(this.id);
			}
		} else {
			this.quantity = this.quantity + count;
		}
		registerEvent(new InventoryUpdateEvent(id, sku, quantity));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Inventory [id=").append(id).append(", sku=").append(sku).append(", quantity=").append(quantity)
				.append("]");
		return builder.toString();
	}

}

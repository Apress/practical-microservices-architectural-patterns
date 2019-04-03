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
package com.acme.ecom.core.inventory.command.handler;

import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.GenericJpaRepository;
import org.axonframework.commandhandling.model.inspection.AnnotatedAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.ecom.core.inventory.command.InventoryCreateCommad;
import com.acme.ecom.core.inventory.command.InventoryUpdateCommand;
import com.acme.ecom.core.inventory.model.Inventory;
import com.acme.ecom.core.order.model.Order;
import com.acme.ecom.core.order.model.OrderStatus;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class InventoryCommandHandler {

	 private static final Logger LOGGER = LoggerFactory.getLogger(InventoryCommandHandler.class);

	@Autowired
	@Qualifier(value = "inventoryRepository")
	private GenericJpaRepository<Inventory> inventoryRepository;

	public void setInventoryRepository(GenericJpaRepository<Inventory> inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	@CommandHandler
	@Transactional
	public void handleInventoryCreation(InventoryCreateCommad inventoryCreatedCommand)  {

		LOGGER.info("Start");
		Integer id = new Random().nextInt();
		LOGGER.debug("InventoryCommandHandler/create new inventory is executing....."+id);
		try{
		inventoryRepository.newInstance(()->new Inventory(Long.valueOf(id), inventoryCreatedCommand.getSku(), inventoryCreatedCommand.getQuantity()));
		}catch(Exception ex){
			LOGGER.error("InventoryCommandHandler error ",ex);
		}
		LOGGER.info("End");
	}

	@CommandHandler
	@Transactional
	public void handleInventoryUpdation(InventoryUpdateCommand inventoryUpdateCommand)  {

		LOGGER.info("Start");
		LOGGER.debug("InventoryCommandHandler/ inventory updating is executing  for product....."+inventoryUpdateCommand.getLineItemDTO().getProductId());
		Aggregate<Inventory>  inventoryAggregate=inventoryRepository.load(inventoryUpdateCommand.getLineItemDTO().getInventoryId().toString());
		inventoryAggregate.invoke((Inventory inventory) ->{inventory.updateProductStock(inventoryUpdateCommand.getLineItemDTO().getQuantity(),
				inventoryUpdateCommand.getProductStockOperation()); return inventory; });
		LOGGER.info("End");
	}
}

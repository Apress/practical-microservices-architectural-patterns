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
package com.acme.ecom.order.eventhandler;

import javax.sql.DataSource;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.acme.ecom.order.api.event.OrderCancelledEvent;
import com.acme.ecom.order.api.event.OrderConfirmedEvent;
import com.acme.ecom.order.api.event.OrderCreatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class OrderEventHandler {

	private static final String NEW = "NEW";
	private static final String CONFIRMED = "CONFIRMED";
	private static final String CANCELLED = "CANCELLED";

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventHandler.class);

    @Autowired
    DataSource dataSource;

    @EventHandler
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {

        LOGGER.info("Start");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO ecom_order_view VALUES(?,?,?,?,?)", new Object[]{event.getOrderId(), event.getPrice(), event.getNumber(), event.getProductDescription(), NEW});
        LOGGER.debug("Inserted into Order View with Order ID: {}; Price: {}; Numbers: {}; Product Desc: {}; Order Status: {}", new Object[]{event.getOrderId(), event.getPrice(), event.getNumber(), event.getProductDescription(), NEW});
        LOGGER.info("End");
    }


    @EventHandler
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {

        LOGGER.info("Start");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("UPDATE ecom_order_view SET status=? WHERE id =?", new Object[]{CONFIRMED, event.getOrderId()});
        LOGGER.debug("Updated Order View for Order with ID: {} Order Status: {}", new Object[]{event.getOrderId(), CONFIRMED});
        LOGGER.info("End");
    }


    @EventHandler
    public void handleOrderCancelledEvent(OrderCancelledEvent event) {

        LOGGER.info("Start");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("UPDATE ecom_order_view SET status=? WHERE id =?", new Object[]{CANCELLED, event.getOrderId()});
        LOGGER.debug("Updated Order View for Order with ID: {} Order Status: {}", new Object[]{event.getOrderId(), CANCELLED});
        LOGGER.info("End");
    }

}

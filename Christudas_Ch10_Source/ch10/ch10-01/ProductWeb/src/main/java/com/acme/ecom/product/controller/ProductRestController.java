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
package com.acme.ecom.product.controller;

import com.acme.ecom.product.model.Product;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.AsyncRestTemplate;

import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@CrossOrigin
@RestController
public class ProductRestController{

    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);
	private static String PRODUCT_SERVICE_URL = "http://localhost:8080/products/";

	public ProductRestController(){
	}


    //------------------- Retreive all Products --------------------------------------------------------
    @RequestMapping(value = "/productsweb", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    public DeferredResult<List<Product>> getAllProducts() {

    	LOGGER.info("Start");
    	LOGGER.debug(Thread.currentThread().toString());
    	DeferredResult<List<Product>> deferredResult = new DeferredResult<>();

        ParameterizedTypeReference<List<Product>> responseTypeRef = new ParameterizedTypeReference<List<Product>>() {};
		ListenableFuture<ResponseEntity<List<Product>>> entity = asyncRestTemplate.exchange(PRODUCT_SERVICE_URL, HttpMethod.GET,
				(HttpEntity<Product>) null, responseTypeRef);

        entity.addCallback(new ListenableFutureCallback<ResponseEntity<List<Product>>>() {
            @Override
            public void onFailure(Throwable ex) {

    			LOGGER.debug(Thread.currentThread().toString());
    			LOGGER.error(ex.getMessage());
            }

            @Override
            public void onSuccess(ResponseEntity<List<Product>> result) {

        		List<Product> products = result.getBody();
        		products.forEach(item->LOGGER.debug(item.toString()));
    			LOGGER.debug(Thread.currentThread().toString());
                deferredResult.setResult(products);
            }
        });

    	LOGGER.debug(Thread.currentThread().toString());
        return deferredResult;
    }


    //------------------- Retreive a Product --------------------------------------------------------
    @RequestMapping(value = "/productsweb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Product> getProduct(@PathVariable("id") String id) {

    	LOGGER.info("Start");
    	LOGGER.debug(Thread.currentThread().toString());
    	DeferredResult<Product> deferredResult = new DeferredResult<>();

        ListenableFuture<ResponseEntity<Product>> entity = asyncRestTemplate.getForEntity(PRODUCT_SERVICE_URL + "/" + id, Product.class);

        entity.addCallback(new ListenableFutureCallback<ResponseEntity<Product>>() {
            @Override
            public void onFailure(Throwable ex) {

    			LOGGER.debug(Thread.currentThread().toString());
    			LOGGER.error(ex.getMessage());
            }

            @Override
            public void onSuccess(ResponseEntity<Product> result) {

    			LOGGER.debug(Thread.currentThread().toString());
                deferredResult.setResult(result.getBody());
            }
        });

        return deferredResult;

    }

}

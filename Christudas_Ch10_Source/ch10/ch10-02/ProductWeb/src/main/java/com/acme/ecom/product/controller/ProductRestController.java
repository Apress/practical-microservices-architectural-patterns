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

import com.acme.ecom.product.model.ECom.Product;
import com.acme.ecom.product.model.ECom.Products;

import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@CrossOrigin
@RestController
public class ProductRestController{

	@Autowired
	RestTemplate restTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);
	private static String PRODUCT_SERVICE_URL = "http://localhost:8081/products/";

	@Autowired
	public ProductRestController(RestTemplate restTemplate){
		this.restTemplate = restTemplate;
	}


    @RequestMapping(value = "/productsweb", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    //@RequestMapping(value = "/productsweb", method = RequestMethod.GET)
    public  Products getAllProducts() {

    	LOGGER.info("Start");
		Products products = restTemplate.getForObject(PRODUCT_SERVICE_URL, Products.class);
		List<Product> productsList = products.getProductList();
        if(productsList.isEmpty()){
    		LOGGER.debug("No products retreived from repository");
        }
        productsList.forEach(item->LOGGER.debug(item.toString()));
    	LOGGER.info("Ending");
    	Products productsParent =
    			Products.newBuilder().addAllProduct(productsList).build();
    	return productsParent;

    }


   @RequestMapping(value = "/productsweb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   //@RequestMapping(value = "/productsweb/{id}", method = RequestMethod.GET)
    public Product getProduct(@PathVariable("id") String id) {

        System.out.println("Fetching Product with id " + id);
		Product product = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/" + id, Product.class);
        if (product == null) {
            LOGGER.debug("Product with id {} not found", id);
        }
        LOGGER.debug("Product Retreived: {}", product);
		return product;
    }

}

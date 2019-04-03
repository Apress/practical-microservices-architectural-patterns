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
package com.acme.ecom.product.service;

import com.acme.ecom.product.model.Product;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Service
public class ProductService{

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Async
	public CompletableFuture<Product> getProduct(String id) throws InterruptedException{

    	LOGGER.info("Start");
    	LOGGER.debug("Fetching Product with id: {}", id);

        //Product product = productRepository.findOne(id);
        Product product = getTheProduct(id);
        if (product == null) {
    		LOGGER.debug("Product with id: {} not found", id);
        }
    	LOGGER.info("Ending");
        Thread.sleep(2000L); // Delay to mock a long running task
        return CompletableFuture.completedFuture(product);
	}

    @Async
    public CompletableFuture<List<Product>> getAllProducts() throws InterruptedException{

    	LOGGER.info("Start");
    	LOGGER.debug("Fetching all the products from the repository");

        //List<Product> products = productRepository.findAll();
        List<Product> products = getAllTheProducts();
        if (products.size() == 0) {
    		LOGGER.debug("No Products Retreived from the repository");
        }
    	LOGGER.debug(Thread.currentThread().toString());
    	LOGGER.info("Ending");
        Thread.sleep(2000L); // Delay to mock a long running task
        return CompletableFuture.completedFuture(products);
    }

    private Product getTheProduct(String id){

    	Product product = new Product();
    	product.setProductId(id);
    	product.setName("Kamsung D3");
    	product.setCode("KAMSUNG-TRIOS");
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ....");
    	product.setDescription("Kamsung Trios 12 inch with Touch");
    	product.setImgUrl("kamsung.jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("Mobile");

    	return product;
	}

    private List<Product> getAllTheProducts(){

		LOGGER.info("Start");

		List<Product> products = new ArrayList<Product>();

    	Product product = new Product();
    	product.setProductId("1");
    	product.setName("Kamsung D3");
    	product.setCode("KAMSUNG-TRIOS");
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ....");
    	product.setDescription("Kamsung Trios 12 inch with Touch");
    	product.setImgUrl("kamsung.jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	product = new Product();
    	product.setProductId("2");
    	product.setName("Lokia Pomia");
    	product.setCode("LOKIA-POMIA");
    	product.setTitle("Lokia 12 inch , white , 14px ....");
    	product.setDescription("Lokia Pomia 10 inch with NFC");
    	product.setImgUrl("lokia.jpg");
    	product.setPrice(9000.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	//TODO: Add rest of products and catagories...........
		LOGGER.info("Ending...");
		return products;
    }

}

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

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

import com.acme.ecom.product.model.ECom.Product;
import com.acme.ecom.product.model.ECom.Products;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
public class ProductRestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

    //------------------- Retreive all Products --------------------------------------------------------
    //@RequestMapping(value = "/products", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    //@RequestMapping(value = "/products", method = RequestMethod.GET ,produces = "application/x-protobuf")
    public  Products getAllProducts() {

    	LOGGER.info("Start");
        List<Product> products = getAllTheProducts();
        if(products.isEmpty()){
    		LOGGER.debug("No products retreived from repository");
        }
        products.forEach(item->LOGGER.debug(item.toString()));
    	LOGGER.info("Ending");
    	Products productsParent = Products.newBuilder().addAllProduct(products).build();
    	return productsParent;
    }

    //------------------- Retreive a Product --------------------------------------------------------
   //@RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    //@RequestMapping(value = "/products", method = RequestMethod.GET ,produces = "application/x-protobuf")
	public Product getProduct(@PathVariable("id") String id) {

		LOGGER.info("Start");
		LOGGER.debug("Fetching Product with id: {}", id);

		Product product = getTheProduct(id);
		if (product == null) {
			LOGGER.debug("Product with id: {} not found", id);
		}
		LOGGER.info("Ending");
		return product;
	}

    private Product getTheProduct(String id){

		Product product =
			Product.newBuilder().setProductId(id)
								.setName("Kamsung D3")
								.setCode("KAMSUNG-TRIOS")
								.setTitle("Kamsung Trios 12 inch , black , 12 px ....")
								.setDescription("Kamsung Trios 12 inch with Touch")
								.setImgUrl("kamsung.jpg")
								.setPrice(12000.00)
								.setProductCategoryName("Mobile").build();
		return product;

	}

    private List<Product> getAllTheProducts(){

		LOGGER.info("Start");

		List<Product> products = new ArrayList<Product>();


		Product product1 =
			Product.newBuilder().setProductId("1")
								.setName("Kamsung D3")
								.setCode("KAMSUNG-TRIOS")
								.setTitle("Kamsung Trios 12 inch , black , 12 px ....")
								.setDescription("Kamsung Trios 12 inch with Touch")
								.setImgUrl("kamsung.jpg")
								.setPrice(12000.00)
								.setProductCategoryName("Mobile").build();
    	products.add(product1);

		Product product2 =
			Product.newBuilder().setProductId("2")
								.setName("Lokia Pomia")
								.setCode("LOKIA-POMIA")
								.setTitle("Lokia 12 inch , white , 14px ....")
								.setDescription("Lokia Pomia 10 inch with NFC")
								.setImgUrl("lokia.jpg")
								.setPrice(9000.00)
								.setProductCategoryName("Mobile").build();
    	products.add(product2);

    	//TODO: Add rest of products and catagories...........
		LOGGER.info("Ending...");
		return products;
    }


}

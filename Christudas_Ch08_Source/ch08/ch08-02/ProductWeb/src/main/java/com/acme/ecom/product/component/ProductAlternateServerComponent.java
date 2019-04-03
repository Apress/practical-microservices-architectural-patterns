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
package com.acme.ecom.product.component;

import com.acme.ecom.product.model.Product;
import com.acme.ecom.product.client.ProductAlternateServiceProxy;
import com.acme.ecom.product.client.ProductServiceProxy;

import org.springframework.hateoas.Resources;
import org.springframework.hateoas.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@EnableFeignClients(basePackageClasses = ProductAlternateServiceProxy.class)
@ComponentScan(basePackageClasses = ProductAlternateServiceProxy.class)
@Component
public class ProductAlternateServerComponent implements ProductServiceProxy{

	private ProductAlternateServiceProxy productAlternateServiceProxy;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductAlternateServerComponent.class);

	@Autowired
	public ProductAlternateServerComponent(ProductAlternateServiceProxy productAlternateServiceProxy){
		this.productAlternateServiceProxy = productAlternateServiceProxy;
	}


    @Override
    public ResponseEntity<Resources<Resource<Product>>> getAllProducts() {

        LOGGER.info("Delegating...");
		return productAlternateServiceProxy.getAllProducts();
    }

    @Override
    public ResponseEntity<Resource<Product>> getProduct(@PathVariable("productId") String productId) {

        LOGGER.info("Delegating...");
		return productAlternateServiceProxy.getProduct(productId);

    }

}

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

//import com.acme.ecom.product.repository.ProductRepository;
import com.acme.ecom.product.model.Product;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Link;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.Map;
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
    @RequestMapping(value = "/products", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resources<Resource<Product>>> getAllProducts() {

    	LOGGER.info("Start");
        List<Product> products = getAllTheProducts();
        Link links[]={linkTo(methodOn(ProductRestController.class).getAllProducts()).withSelfRel(),linkTo(methodOn(ProductRestController.class).getAllProducts()).withRel("getAllProducts")};
        if(products.isEmpty()){
    		LOGGER.debug("No products retreived from repository");
            return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
        }
        List<Resource<Product>> list = new ArrayList<Resource<Product>> ();
        for(Product product:products){
      		list.add(new Resource<Product>(product, linkTo(methodOn(ProductRestController.class).getProduct(product.getProductId())).withSelfRel()));
        }
        list.forEach(item->LOGGER.debug(item.toString()));
        Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links) ;
    	LOGGER.info("Ending");
        return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
    }

    //------------------- Retreive a Product --------------------------------------------------------
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<Product>> getProduct(@PathVariable("id") String id) {

    	LOGGER.info("Start");
    	LOGGER.debug("Fetching Product with id: {}", id);

        Product product = getTheProduct(id);
        if (product == null) {
    		LOGGER.debug("Product with id: {} not found", id);
            return new ResponseEntity<Resource<Product>>(HttpStatus.NOT_FOUND);
        }
        Resource<Product> productRes =new Resource<Product>(product, linkTo(methodOn(ProductRestController.class).getProduct(product.getProductId())).withSelfRel());
    	LOGGER.info("Ending");
        return new ResponseEntity<Resource<Product>>(productRes, HttpStatus.OK);
    }

    private Product getTheProduct(String id){

    	Product product=new Product();
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

    	Product product=new Product();
    	product.setProductId("1");
    	product.setName("Kamsung D3");
    	product.setCode("KAMSUNG-TRIOS");
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ....");
    	product.setDescription("Kamsung Trios 12 inch with Touch");
    	product.setImgUrl("kamsung.jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	product=new Product();
    	product.setProductId("2");
    	product.setName("Lokia Pomia");
    	product.setCode("LOKIA-POMIA");
    	product.setTitle("Lokia 12 inch , white , 14px ....");
    	product.setDescription("Lokia Pomia 10 inch with NFC");
    	product.setImgUrl("lokia.jpg");
    	product.setPrice(9000.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	product=new Product();
    	product.setProductId("3");
    	product.setName("Maple Phone");
    	product.setCode("MAPLE-PHONE");
    	product.setTitle("Maple 12 inch , grey , 18px ....");
    	product.setDescription("Maple Phone 18 inch with NFC");
    	product.setImgUrl("maple.jpg");
    	product.setPrice(9080.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	product=new Product();
    	product.setProductId("4");
    	product.setName("Xi Phone");
    	product.setCode("XI-PHONE");
    	product.setTitle("Siomi 10 inch , grey , 14px ....");
    	product.setDescription("Siomi Phone 10 inch with NFC");
    	product.setImgUrl("siomi.jpg");
    	product.setPrice(7080.00);
    	product.setProductCategoryName("Mobile");
    	products.add(product);

    	//TODO: Add rest of products and catagories...........
		LOGGER.info("Ending...");
		return products;
    }


}

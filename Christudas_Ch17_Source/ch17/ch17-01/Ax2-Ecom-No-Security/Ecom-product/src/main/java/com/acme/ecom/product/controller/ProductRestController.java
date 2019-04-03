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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.acme.ecom.product.model.Product;
import com.acme.ecom.product.repository.ProductRepository;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
public class ProductRestController {

	@Autowired
	private ProductRepository productRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

	// ------------------- Update a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Resource<Product>> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {
		LOGGER.debug("Updating Product with id: " + id);

		Product currentProduct = productRepository.findOne(id);

		if (currentProduct == null) {
			LOGGER.debug("Product with id: " + id + " not found");
			return new ResponseEntity<Resource<Product>>(HttpStatus.NOT_FOUND);
		}

		currentProduct.setName(product.getName());
		currentProduct.setCode(product.getCode());
		currentProduct.setTitle(product.getTitle());
		currentProduct.setDescription(product.getDescription());
		currentProduct.setImgUrl(product.getImgUrl());
		currentProduct.setPrice(product.getPrice());
		currentProduct.setProductCategoryName(product.getProductCategoryName());

		Product newProduct = productRepository.save(currentProduct);

		Resource<Product> productRes = new Resource<Product>(newProduct,
				linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
		return new ResponseEntity<Resource<Product>>(productRes, HttpStatus.OK);
	}

	// ------------------- Create a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public ResponseEntity<Resource<Product>> postProduct(@RequestBody Product product, UriComponentsBuilder ucBuilder) {
		LOGGER.debug("Creating Product with code: " + product.getCode());

		List<Product> products = productRepository.findByCode(product.getCode());
		if (products.size() > 0) {
			LOGGER.debug("A Product with code " + product.getCode() + " already exist");
			return new ResponseEntity<Resource<Product>>(HttpStatus.CONFLICT);
		}

		Product newProduct = productRepository.save(product);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri());
		Resource<Product> productRes = new Resource<Product>(newProduct,
				linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
		// return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		return new ResponseEntity<Resource<Product>>(productRes, headers, HttpStatus.OK);
	}

	// ------------------- Retreive a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Resource<Product>> getProduct(@PathVariable("id") String id) {

		LOGGER.debug("Fetching Product with id " + id);
		Product product = productRepository.findOne(id);
		if (product == null) {
			LOGGER.debug("Product with id " + id + " not found");
			return new ResponseEntity<Resource<Product>>(HttpStatus.NOT_FOUND);
		}
		Resource<Product> productRes = new Resource<Product>(product,new Link[]{linkTo(methodOn(ProductRestController.class).getProduct(product.getId())).withSelfRel()
				,linkTo(ProductRestController.class).slash("productImg").slash(product.getImgUrl()).withRel("imgUrl")
		});
		return new ResponseEntity<Resource<Product>>(productRes, HttpStatus.OK);
	}

	// ------------------- Retreive all Products
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Resources<Resource<Product>>> getAllProducts() {

		LOGGER.debug("ProductRestController.getAllProducts : Start");
		List<Product> products = productRepository.findAll();
		Link links[] = { linkTo(methodOn(ProductRestController.class).getAllProducts()).withSelfRel(),
				linkTo(methodOn(ProductRestController.class).getAllProducts()).withRel("getAllProducts")
				,linkTo(methodOn(ProductRestController.class).getAllProductsByCategory("")).withRel("getAllProductsByCategory")
				,linkTo(methodOn(ProductRestController.class).getAllProductsByName("")).withRel("getAllProductsByName")
				};
		if (products.isEmpty()) {
			LOGGER.debug("ProductRestController.getAllProducts : products.isEmpty()...");
			return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
		}
		List<Resource<Product>> list = new ArrayList<Resource<Product>>();
		addLinksToProduct(products, list);
		Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
		LOGGER.debug("ProductRestController.getAllProducts : Ending...");
		return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
	}

	// ------------------- Delete a Product
	// --------------------------------------------------------
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteProduct(@PathVariable("id") String id) {

		LOGGER.debug("Fetching & Deleting Product with id: " + id);
		Product product = productRepository.findOne(id);
		if (product == null) {
			LOGGER.debug("Unable to delete. Product with id: " + id + " not found");
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}

		productRepository.delete(id);
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}

	// ------------------- Delete All Products
	// --------------------------------------------------------
	@RequestMapping(value = "/products", method = RequestMethod.DELETE)
	public ResponseEntity<Product> deleteAllProducts() {

		long count = productRepository.count();
		LOGGER.debug("Deleting " + count + " products");
		productRepository.deleteAll();
		return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
	}

	// ------------------- Retreive all Products by category
	// --------------------------------------------------------
	@RequestMapping(value = "/productsByCategory", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Resources<Resource<Product>>> getAllProductsByCategory(@RequestParam("category") String category) {

			LOGGER.debug("ProductRestController.getAllProductsByCategory : Start with param "+category);
			List<Product> products = productRepository.findByProductCategoryName(category);
			Link links[] = {
					//	linkTo(methodOn(ProductRestController.class)).slash("/products/category/").withSelfRel()
					};
			if (products.isEmpty()) {
				LOGGER.debug("ProductRestController.getAllProductsByCategory : products.isEmpty()...");
				return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
			}
			List<Resource<Product>> list = new ArrayList<Resource<Product>>();
			addLinksToProduct(products, list);
			Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
			LOGGER.debug("ProductRestController.getAllProductsByCategory : Ending...");
			return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
		}

	private void addLinksToProduct(List<Product> products, List<Resource<Product>> list) {
		for (Product product : products) {
			list.add(new Resource<Product>(product,
					new Link[]{linkTo(methodOn(ProductRestController.class).getProduct(product.getId())).withSelfRel()
							,linkTo(ProductRestController.class).slash("productImg").slash(product.getImgUrl()).withRel("imgUrl")
					}));
		}
	}

	// ------------------- Retreive all Products by name like % %
	// --------------------------------------------------------
		@RequestMapping(value = "/productsByName", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
		public ResponseEntity<Resources<Resource<Product>>> getAllProductsByName(@RequestParam("name") String name) {

				LOGGER.debug("ProductRestController.getAllProductsByName : Start with param "+name);
				List<Product> products = productRepository.findByNameRegex(name);
				Link links[] = {
						//	linkTo(methodOn(ProductRestController.class)).slash("/products/category/").withSelfRel()
						};
				if (products.isEmpty()) {
					LOGGER.debug("ProductRestController.getAllProductsByName : products.isEmpty()...");
					return new ResponseEntity<Resources<Resource<Product>>>(HttpStatus.NOT_FOUND);
				}
				List<Resource<Product>> list = new ArrayList<Resource<Product>>();
				addLinksToProduct(products, list);
				Resources<Resource<Product>> productRes = new Resources<Resource<Product>>(list, links);// ,
				LOGGER.debug("ProductRestController.getAllProductsByName : Ending...");
				return new ResponseEntity<Resources<Resource<Product>>>(productRes, HttpStatus.OK);
			}
	// ------------------- Retreiving product image
	// --------------------------------------------------------

}

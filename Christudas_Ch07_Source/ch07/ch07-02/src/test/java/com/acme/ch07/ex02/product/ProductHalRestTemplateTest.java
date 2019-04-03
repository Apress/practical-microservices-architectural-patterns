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
package com.acme.ch07.ex02.product;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.acme.ch07.ex02.product.EcomProductMicroserviceApplication;
import com.acme.ch07.ex02.product.model.Product;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.HttpEntity;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ProductHalRestTemplateTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductHalRestTemplateTest.class);
	private static String PRODUCT_SERVICE_URL = "http://127.0.0.1:8080/products";

	@Before
	public void setUp(){
		deleteAllProducts();
	}


    @Test
    public void testPostProduct(){

    	LOGGER.info("Start");
    	try{

		Product productNew1 = createProduct("1");
		Product productNew2 = createProduct("2");

		RestTemplate restTemplate = restTemplate();
		Product productRetreived1 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew1, Product.class);
		Product productRetreived2 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew2, Product.class);
		LOGGER.debug("productRetreived1 : {}"+ productRetreived1);
		LOGGER.debug("productRetreived2 : {}"+ productRetreived2);

    	assertTrue("successfully saved",true);
    	}catch(Exception ex){
    		assertTrue("successfully failed",true);
    	}
    	LOGGER.info("End");
    }

    @Test
    public void testGetAllProducts(){

    	LOGGER.info("Start");

		testPostProduct();
		List<Product> productList = getAllProducts();
		LOGGER.debug("productList.size() : " + productList.size());

    	assertTrue(productList.size() > 0);
    	LOGGER.info("End");
    }

    @Test
    public void testPutProduct(){


    	LOGGER.info("Start");
    	try{

		Product productNew3 = createProduct("3");

		RestTemplate restTemplate = restTemplate();
		Product productRetreived3 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew3, Product.class);
		LOGGER.debug("productRetreived3 : {}"+ productRetreived3);
		productRetreived3.setPrice(productRetreived3.getPrice() * 2);
		restTemplate.put( PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), productRetreived3, Product.class);
		Product productAgainRetreived = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), Product.class);
		LOGGER.debug("productAgainRetreived : {}"+ productAgainRetreived);

    	assertTrue("successfully saved",true);
    	}catch(Exception ex){
    		assertTrue("successfully failed",true);
    	}
    	LOGGER.info("End");
    }

    public void deleteAllProducts(){

    	LOGGER.info("Start");
		RestTemplate restTemplate = restTemplate();
		List<Product> productList = getAllProducts();
		LOGGER.debug("productList.size() : " + productList.size());
		productList.forEach(item->restTemplate.delete(PRODUCT_SERVICE_URL + "/" + item.getId()));
    	LOGGER.info("End");
    }

    public List<Product> getAllProducts(){

		RestTemplate restTemplate = restTemplate();

		ParameterizedTypeReference<PagedResources<Product>> responseTypeRef = new ParameterizedTypeReference<PagedResources<Product>>() {

			};
		ResponseEntity<PagedResources<Product>> responseEntity = restTemplate.exchange(PRODUCT_SERVICE_URL, HttpMethod.GET,
				(HttpEntity<Product>) null, responseTypeRef);
		PagedResources<Product> resources = responseEntity.getBody();
		Collection<Product> products = resources.getContent();
		List<Product> productList = new ArrayList<Product>(products);
		return productList;

    }

    private RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		return new RestTemplate(Arrays.asList(converter));
	}

	private Product createProduct(String id){

    	Product product=new Product();
    	product.setName("Kamsung D3" + "-" + id);
    	product.setCode("KAMSUNG-TRIOS" + "-" + id);
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ...." + "-" + id);
    	product.setDescription("Kamsung Trios 12 inch with Touch" + "-" + id);
    	product.setImgUrl("kamsung" + id + ".jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("testCategory");
    	return product;
	}
}

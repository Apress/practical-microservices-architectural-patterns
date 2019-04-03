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
package com.acme.ch07.ex03.product;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

import com.acme.ch07.ex03.product.model.Product;

import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ProductControllerRestTemplateTest {

	//private static String PRODUCT_SERVICE_URL = "http://localhost:8080/productdata"; // DO NOT Try with this URL since Delete All will not function
	private static String PRODUCT_SERVICE_URL = "http://localhost:8080/products";
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductControllerRestTemplateTest.class);

	@Before
	public void setUp(){
		;
		//deleteAllProducts();
	}

    @Test
    public void testPostProduct(){

    	LOGGER.info("Start");

		Product productNew1 = createProduct("1");
		RestTemplate restTemplate = restTemplate();
		Product productRetreived1 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew1, Product.class);
    	LOGGER.debug("Product with product ID {} created", productRetreived1.getId());

    	LOGGER.info("End");
    }

    @Test
    public void testGetAProduct(){

    	LOGGER.info("Start");

		Product productNew2 = createProduct("2");
		RestTemplate restTemplate = restTemplate();
		Product productRetreived2 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew2, Product.class);
    	LOGGER.debug("Product with product ID {} created", productRetreived2.getId());

		String uri = PRODUCT_SERVICE_URL + "/" + productRetreived2.getId();

		Product productRetreivedAgain2 = restTemplate.getForObject(uri, Product.class);
    	LOGGER.debug("Product with product ID {} retreived", productRetreivedAgain2.getId());

    	LOGGER.info("End");
    }

    @Test
    public void testPutProduct(){

    	LOGGER.info("Start");

		Product productNew3 = createProduct("3");
		RestTemplate restTemplate = restTemplate();
		Product productRetreived3 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew3, Product.class);
		System.out.println("productRetreived3 : "+ productRetreived3);
    	LOGGER.debug("productRetreived3 : {}", productRetreived3);
		productRetreived3.setPrice(productRetreived3.getPrice() * 2);
		restTemplate.put( PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), productRetreived3, Product.class);
		Product productAgainRetreived3 = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), Product.class);
    	LOGGER.debug("productAgainRetreived3 : {}", productAgainRetreived3);

    	LOGGER.info("End");
    }

    @Test
    public void testDeleteAProduct(){

    	LOGGER.info("Start");
		Product productNew4 = createProduct("4");

		RestTemplate restTemplate = restTemplate();
		Product productRetreived4 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew4, Product.class);
    	LOGGER.debug("productRetreived4 : {}", productRetreived4);

		restTemplate.delete(PRODUCT_SERVICE_URL + "/" + productRetreived4.getId());

    	LOGGER.info("End");
    }



    @Test
    public void testDeleteAllProducts(){

    	LOGGER.info("Start");

		RestTemplate restTemplate = restTemplate();
		Product productNew5 = createProduct("5");
		Product productRetreived5 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew5, Product.class);
		Product productNew6 = createProduct("6");
		Product productRetreived6 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew6, Product.class);

		restTemplate.delete(PRODUCT_SERVICE_URL);
		//deleteAllProducts();

    	LOGGER.info("End");
    }


    private List<Product> getAllProducts(){

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
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json, application/json"));
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

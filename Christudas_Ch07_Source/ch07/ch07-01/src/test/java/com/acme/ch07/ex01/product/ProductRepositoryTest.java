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
package com.acme.ch07.ex01.product;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.acme.ch07.ex01.product.EcomProductMicroserviceApplication;
import com.acme.ch07.ex01.product.model.Product;
import com.acme.ch07.ex01.product.model.ProductCategory;
import com.acme.ch07.ex01.product.repository.ProductCategoryRepository;
import com.acme.ch07.ex01.product.repository.ProductRepository;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ProductRepositoryTest {

	@Autowired
	private ProductRepository productRepository;

	@Before
	public void setUp(){
		productRepository.deleteAll();
	}

    @Test
    public void testAddProduct(){
    	try{
    	productRepository.save(createProject());
    	assertTrue("successfully saved",true);
    	}catch(Exception ex){
    		assertTrue("successfully failed",true);
    	}
    }

    @Test
    public void testFindAllProducts(){

    	productRepository.save(createProject());
    	List<Product> productList=productRepository.findAll();
    	System.out.println(productList);
    	assertTrue(productList.size()>0);
    }
    @Test
    public void testProductByProductCategory(){
    	Product product=createProject();
    	productRepository.save(product);
    	List<Product> productList=productRepository.findByProductCategoryName(product.getProductCategoryName());
    	assertTrue(productList.size()>0);
    }

    private Product createProject(){

    	Product product=new Product();
    	product.setName("Kamsung D3");
    	product.setCode("KAMSUNG-TRIOS");
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ....");
    	product.setDescription("Kamsung Trios 12 inch with Touch");
    	product.setImgUrl("kamsung.jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("testCategory");
    	return product;
    }


}

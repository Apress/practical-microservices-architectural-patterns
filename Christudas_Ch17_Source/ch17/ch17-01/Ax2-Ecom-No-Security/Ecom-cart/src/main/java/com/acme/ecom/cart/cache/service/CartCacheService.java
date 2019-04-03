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
package com.acme.ecom.cart.cache.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.acme.ecom.cart.dto.CustomerCartDTO;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class CartCacheService {
	 private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CacheManager cacheManager;
	/**
	 * Adding user cart to cache
	 */
	public void updateUserCartInCache(CustomerCartDTO userCart){
		logger.debug("updating/adding user  "+ userCart.getUserId() +"  into cache---->");
		cacheManager.getCache("customerCache").put(userCart.getUserId(), userCart);
	}
	/**
	 * Getting user cart from cache
	 * @param userId
	 */
	public CustomerCartDTO getUserCartFromCache(String userId){

		ValueWrapper value = cacheManager.getCache("customerCache").get(userId);
		if(value != null){
			logger.debug("Returning cache for the  customer ----->  "+ userId );
			return (CustomerCartDTO) value.get();
		}else{
			logger.debug("Customer is not existin the cache  ----->  "+ userId );
			CustomerCartDTO userCartDto = new CustomerCartDTO(userId);
			updateUserCartInCache(userCartDto);
			logger.debug("Customer is added to the cache  ----->  "+ userId );
			return userCartDto;
		}

	}
}

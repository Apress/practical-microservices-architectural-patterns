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
package com.acme.ecom.user.service.impl;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acme.ecom.user.controller.UserController;
import com.acme.ecom.user.dto.CustomerDTO;
import com.acme.ecom.user.dto.UserCredentialDTO;
import com.acme.ecom.user.exception.InvalidUserException;
import com.acme.ecom.user.model.Address;
import com.acme.ecom.user.model.OnlineUser;
import com.acme.ecom.user.model.RoleEnum;
import com.acme.ecom.user.model.User;
import com.acme.ecom.user.model.UserRole;

import com.acme.ecom.user.repository.OnlineUserRepository;
import com.acme.ecom.user.repository.UserRepository;
import com.acme.ecom.user.service.CustomerService;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private OnlineUserRepository onlineUserRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional
	public void saveCustomer(CustomerDTO customer) {

		LOGGER.info("Start");
    	LOGGER.debug("Saving Customer with First Name: {}", customer.getFirstName());

		OnlineUser onlineUser=new OnlineUser();
		onlineUser.setScreenName(customer.getUserName());
		onlineUser.setPassword(customer.getPassword());
		onlineUser.setActive(true);
		onlineUser.addRole(new UserRole(RoleEnum.CUSTOMER_READ));
		onlineUser.addRole(new UserRole(RoleEnum.ORDER_READ));
		onlineUser.addRole(new UserRole(RoleEnum.PRODUCT_READ));
		onlineUser.addRole(new UserRole(RoleEnum.ORDER_WRITE));
		onlineUserRepository.save(onlineUser);

		User user=new User();
		user.setUserId(customer.getUserName());
		user.setFirstName(customer.getFirstName());
		user.setLastName(customer.getLastName());
		user.setEmail(customer.getEmail());
		user.setPhone(customer.getPhone());
		if(customer.getBillingAddress()!=null){
			Address billAddress=new Address();
			billAddress.setAppartment(customer.getBillingAddress().getAppartment());
			billAddress.setCountry(customer.getBillingAddress().getCountry());
			billAddress.setPin(customer.getBillingAddress().getPin());
			billAddress.setProvince(customer.getBillingAddress().getProvince());
			billAddress.setState(customer.getBillingAddress().getState());
			billAddress.setStreet(customer.getBillingAddress().getStreet());
			user.setBillingAddress(billAddress);
		}
		if(customer.getShippingAddress()!=null){
			Address shippingAddress=new Address();
			shippingAddress.setAppartment(customer.getBillingAddress().getAppartment());
			shippingAddress.setCountry(customer.getBillingAddress().getCountry());
			shippingAddress.setPin(customer.getBillingAddress().getPin());
			shippingAddress.setProvince(customer.getBillingAddress().getProvince());
			shippingAddress.setState(customer.getBillingAddress().getState());
			shippingAddress.setStreet(customer.getBillingAddress().getStreet());
			user.setShippingAddress(shippingAddress);
		}
		userRepository.save(user);
		LOGGER.info("End");
	}

	@Override
	public User findCustomer(String customerId) {

		LOGGER.info("Start");
    	LOGGER.debug("Finding Customer with customerId: {}", customerId);
		User user=userRepository.findByUserId(customerId);
		if(user==null){
		 throw new InvalidUserException(customerId);
		}
		LOGGER.info("Ending");
     	return user;
	}


	public User validateCustomer(UserCredentialDTO userCredentialDTO) {

		LOGGER.info("Start");
    	LOGGER.debug("Validaing Customer with Username: {}", userCredentialDTO.getUserName());
		OnlineUser onlineUser=onlineUserRepository.findByScreenName(userCredentialDTO.getUserName());
		if(onlineUser==null||(!userCredentialDTO.getPassword().equals(onlineUser.getPassword()))){
		 throw new InvalidUserException(userCredentialDTO.getUserName());
		}
		User user=userRepository.findByUserId(userCredentialDTO.getUserName());
		LOGGER.info("Ending");
		return user;
	}

}

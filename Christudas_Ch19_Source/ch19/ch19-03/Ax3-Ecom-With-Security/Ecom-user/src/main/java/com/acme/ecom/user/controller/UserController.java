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
package com.acme.ecom.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acme.ecom.user.dto.CustomerDTO;
import com.acme.ecom.user.model.OnlineUser;
import com.acme.ecom.user.model.User;
import com.acme.ecom.user.repository.OnlineUserRepository;
import com.acme.ecom.user.service.CustomerService;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OnlineUserRepository onlineUserRepository;
	// ------------------- Create a customer
	// --------------------------------------------------------
	@RequestMapping(value = "/customer", method = RequestMethod.POST)
	public ResponseEntity<Resource<String>> postCustomer(@RequestBody CustomerDTO customer) {

    	LOGGER.debug("Creating Customer with First Name: {}", customer.getFirstName());
		try {
			customerService.saveCustomer(customer);
			Resource<String> userRes = new Resource<String>("SUCCESS");
			return new ResponseEntity<Resource<String>>(userRes, HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error(" Customer existing  with samename: " + customer.getUserName());
			Resource<String> userRes = new Resource<String>("FAIL");
			return new ResponseEntity<Resource<String>>(userRes, HttpStatus.OK);
		}
	}
	/**
	 * Retrieve customer information
	 * @param authentication
	 * @return
	 */

	@RequestMapping(value = "/customer", method = RequestMethod.GET)
	//public ResponseEntity<Resource<User>> validateCredential(Authentication authentication) {
	public ResponseEntity<Resource<User>> retreiveUserFromCredential(Authentication authentication) {
		LOGGER.debug("Retriving customer information for  " +authentication.getPrincipal().toString());
		User user=customerService.findCustomer(authentication.getPrincipal().toString());
		Resource<User> userRes = new Resource<User>(user);
		return new ResponseEntity<Resource<User>>(userRes,HttpStatus.OK);
	}

}

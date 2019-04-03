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
package com.acme.ecom.controller;

import com.acme.ecom.model.user.User;
import com.acme.ecom.repository.user.UserRepository;
import com.acme.ecom.model.trade.StockTransaction;
import com.acme.ecom.repository.trade.StockTransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
@RequestMapping("/api")
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockTransactionRepository stockTransactionRepository;

	//======================== Users ================================
    @GetMapping("/users")
    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();
		users.forEach(item->LOGGER.debug(item.toString()));
		return users;

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {

		LOGGER.info("Start");
		LOGGER.debug("User Received : {}", user);
		Date userCreatedDate = new Date();
		//quote.setStatus(Quote.NEW);
		user.setLastQuoteAt(userCreatedDate);
		user.setCreatedAt(userCreatedDate);
		user.setUpdatedAt(userCreatedDate);

        LOGGER.debug("User to be saved : {}", user);
        User userSaved = userRepository.save(user);
        LOGGER.info("Ending...");
        return userSaved;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                           @Valid @RequestBody User userDetails) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAmountSold(userDetails.getAmountSold());
        user.setAmountBought(userDetails.getAmountBought());
        user.setLastQuoteAt(userDetails.getLastQuoteAt());
        user.setUpdatedAt(new Date());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") Long userId) {

        User user = userRepository.findOne(userId);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

	//======================== Stock Transactions ================================

    @GetMapping("/trade")
    public List<StockTransaction> getAllTransactions() {

        List<StockTransaction> stockTransactions = stockTransactionRepository.findAll();
		stockTransactions.forEach(item->LOGGER.debug(item.toString()));
		return stockTransactions;

    }

    @GetMapping("/trade/{id}")
    public ResponseEntity<StockTransaction> getTransactionById(@PathVariable(value = "id") Long transactionId) {

        StockTransaction stockTransaction = stockTransactionRepository.findOne(transactionId);
        if(stockTransaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(stockTransaction);
    }

    @PostMapping("/trade")
    public StockTransaction createStockTransaction(@Valid @RequestBody StockTransaction stockTransaction) {

		LOGGER.info("Start");
		LOGGER.debug("StockTransaction Received : {}", stockTransaction);
		Date transactionCreatedDate = new Date();
		//quote.setStatus(Quote.NEW);
		stockTransaction.setQuoteCreated(transactionCreatedDate);
		stockTransaction.setCreatedAt(transactionCreatedDate);
		stockTransaction.setUpdatedAt(transactionCreatedDate);
		stockTransaction.setStatus(StockTransaction.NEW);

        LOGGER.debug("StockTransaction to be saved : {}", stockTransaction);
        StockTransaction stockTransactionSaved = stockTransactionRepository.save(stockTransaction);
        LOGGER.info("Ending...");
        return stockTransactionSaved;
    }

}

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
package com.acme.ecom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.acme.ecom.exception.QuotesBaseException;
import com.acme.ecom.exception.QuotesReconcileRollbackException;
import com.acme.ecom.repository.user.UserRepository;
import com.acme.ecom.model.user.User;

import java.util.Optional;
import java.util.List;
import java.util.Date;

import com.acme.ecom.dto.QuoteDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class QuotesReconcileServiceImpl implements QuotesReconcileService{

	private static final Logger LOGGER = LoggerFactory.getLogger(QuotesReconcileServiceImpl.class);
	private static volatile boolean flipFlop = false;

	@Autowired
	private UserRepository userRepository;

	private static synchronized void flipFlop()throws QuotesBaseException{

		if(flipFlop){
			flipFlop = false;
		}
		else{
			flipFlop = true;
		}

		if(flipFlop){
			throw new QuotesReconcileRollbackException("Explicitly thrown by Reconcile Application to Roll Back!");
		}
	}

	@Override
	public void reconcile(QuoteDTO quoteDTO)throws QuotesBaseException{

		LOGGER.info("Start");
		Integer testCase = quoteDTO.getTest();

		Optional sellerQueried = userRepository.findById(quoteDTO.getSellerId());
		Optional buyerQueried  = userRepository.findById(quoteDTO.getBuyerId());
		User seller = null;
		User buyer = null;
		User sellerSaved = null;
		User buyerSaved = null;
		Optional sellerQueriedAgain = null;
		Optional buyerQueriedAgain = null;
		Date updatedDate = null;

		if(sellerQueried.isPresent() && buyerQueried.isPresent()){

			seller = (User) sellerQueried.get();
			buyer  = (User) buyerQueried.get();
			LOGGER.debug("Seller Before Reconciliation : {}", seller);
			LOGGER.debug("Buyer Before Reconciliation : {}", buyer);
			updatedDate = new Date();

			seller.setAmountSold(seller.getAmountSold() + quoteDTO.getAmount());
			seller.setUpdatedAt(updatedDate);
			if(quoteDTO.getCreatedAt().after(seller.getLastQuoteAt())){
				seller.setLastQuoteAt(quoteDTO.getCreatedAt());
			}
			sellerSaved = userRepository.save(seller);
			sellerQueriedAgain = userRepository.findById(quoteDTO.getSellerId());
			LOGGER.debug("Seller After Reconciliation : {}", (User) sellerQueriedAgain.get());

			buyer.setAmountBought(buyer.getAmountBought() + quoteDTO.getAmount());
			buyer.setUpdatedAt(updatedDate);
			if(quoteDTO.getCreatedAt().after(buyer.getLastQuoteAt())){
				buyer.setLastQuoteAt(quoteDTO.getCreatedAt());
			}
			buyerSaved = userRepository.save(buyer);
			buyerQueriedAgain = userRepository.findById(quoteDTO.getBuyerId());
			LOGGER.debug("Buyer After Reconciliation : {}", (User) buyerQueriedAgain.get());

			if(testCase.equals(6)){
				throw new QuotesReconcileRollbackException("Explicitly thrown by Reconcile Application to Roll Back!");
			}
			if(testCase.equals(7)){
				flipFlop();
			}

		}
		else{
			LOGGER.debug("Either Byuer or Seller cannot be tracked - Reconciliation Not Done");
		}

		LOGGER.info("End");
	}

	@Override
	public Optional findUserById(Long id){
		return userRepository.findById(id);
	}

}
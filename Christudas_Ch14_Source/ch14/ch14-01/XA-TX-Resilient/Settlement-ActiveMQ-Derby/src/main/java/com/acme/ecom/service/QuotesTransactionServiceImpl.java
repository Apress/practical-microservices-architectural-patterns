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

import com.acme.ecom.dto.QuoteDTO;
import com.acme.ecom.model.trade.StockTransaction;
import com.acme.ecom.repository.trade.StockTransactionRepository;

import java.util.Optional;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class QuotesTransactionServiceImpl implements QuotesTransactionService{

	private static final Logger LOGGER = LoggerFactory.getLogger(QuotesTransactionServiceImpl.class);

	@Autowired
	private StockTransactionRepository stockTransactionRepository;

	@Override
	public boolean insertUniqueNoErrorOnDuplicate(QuoteDTO quoteDTO){

		LOGGER.info("Start");
		StockTransaction stockTransaction = null;
		StockTransaction stockTransactionSaved = null;
		boolean inserted = false;
		Optional stockTransactionQueried = stockTransactionRepository.findOptionalByQuoteId(quoteDTO.getId());
		if(!stockTransactionQueried.isPresent()){

			stockTransaction = convertQuoteToStockTransaction(quoteDTO);
			stockTransaction.setStatus(StockTransaction.NEW);
			Date now = new Date();
			stockTransaction.setCreatedAt(now);
			stockTransaction.setUpdatedAt(now);

			stockTransactionSaved = stockTransactionRepository.save(stockTransaction);
			inserted = true;
			LOGGER.debug("Stock Transaction After Save : {}", stockTransactionSaved);
		}
		else{
			LOGGER.debug("Stock Transaction with quoteId : {} exist; Cannot insert duplicate", quoteDTO.getId());
			// Do Nothing - we know this is a duplicate message
		}

		LOGGER.info("Ending...");
		return inserted;
	}

	@Override
	public void markSettled(Long quoteId){

		LOGGER.info("Start");
		StockTransaction stockTransaction = null;
		StockTransaction stockTransactionSaved = null;
		Optional stockTransactionQueried = stockTransactionRepository.findOptionalByQuoteId(quoteId);
		if(stockTransactionQueried.isPresent()){
			stockTransaction = (StockTransaction) stockTransactionQueried.get();
			LOGGER.debug("Stock Transaction Queried Before Settling : {}", stockTransaction);
			stockTransaction.setStatus(StockTransaction.SETTLED);
			stockTransaction.setUpdatedAt(new Date());
			stockTransactionSaved = stockTransactionRepository.save(stockTransaction);
			LOGGER.debug("Stock Transaction After Save : {}", stockTransactionSaved);
		}
		else{
			LOGGER.debug("Stock Transaction with quoteId : {} cannot be tracked", quoteId);
		}

		LOGGER.info("End");
	}

	@Override
	public boolean isNotSettled(Long quoteId){

		LOGGER.info("Start");
		boolean isNotSettled = true;
		StockTransaction stockTransaction = null;
		Optional stockTransactionQueried = stockTransactionRepository.findOptionalByQuoteId(quoteId);
		if(stockTransactionQueried.isPresent()){
			stockTransaction = (StockTransaction) stockTransactionQueried.get();
			LOGGER.debug("Stock Transaction Queried to test settlement status : {}", stockTransaction);
			if(stockTransaction.getStatus().equals(StockTransaction.SETTLED)){
				isNotSettled = false;
			}
		}
		return isNotSettled;
	}

	public Optional<StockTransaction> findOptionalByQuoteId(Long quoteId){

		LOGGER.info("Start");
		return stockTransactionRepository.findOptionalByQuoteId(quoteId);
	}

	private StockTransaction convertQuoteToStockTransaction(QuoteDTO quoteDTO){

		StockTransaction stockTransaction = new StockTransaction();

		stockTransaction.setStockSymbol(quoteDTO.getSymbol());
		stockTransaction.setSellerId(quoteDTO.getSellerId());
		stockTransaction.setBuyerId(quoteDTO.getBuyerId());
		stockTransaction.setAmount(quoteDTO.getAmount());
		stockTransaction.setQuoteId(quoteDTO.getId());
		stockTransaction.setTest(quoteDTO.getTest());
		stockTransaction.setQuoteCreated(quoteDTO.getCreatedAt());

		return stockTransaction;
	}
}

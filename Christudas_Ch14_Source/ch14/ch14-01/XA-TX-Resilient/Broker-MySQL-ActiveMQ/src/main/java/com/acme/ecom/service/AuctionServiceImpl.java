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
import org.springframework.stereotype.Service;

import com.acme.ecom.repository.quote.QuoteRepository;
import com.acme.ecom.dto.QuoteDTO;
import com.acme.ecom.model.quote.Quote;
import com.acme.ecom.exception.QuotesConfirmRollbackException;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Service
public class AuctionServiceImpl implements AuctionService{

	private static final Logger LOGGER = LoggerFactory.getLogger(AuctionServiceImpl.class);
	private static volatile boolean flipFlop = false;

	@Autowired
	private QuoteRepository quoteRepository;

	private static synchronized void flipFlop()throws QuotesConfirmRollbackException{

		if(flipFlop){
			flipFlop = false;
		}
		else{
			flipFlop = true;
		}

		if(flipFlop){
			throw new QuotesConfirmRollbackException("Explicitly thrown by Auction Confirm Application to Roll Back!");
		}
	}

	@Override
	public QuoteDTO confirmQuote(QuoteDTO quoteDTO)throws QuotesConfirmRollbackException{

		LOGGER.info("Start");

		Integer testCase = quoteDTO.getTest();
		Optional quoteQueried = quoteRepository.findById(quoteDTO.getId());
		Quote quote = null;
		Quote quoteSaved = null;
		Optional quoteQueriedAgain = null;
		if(quoteQueried.isPresent()){
			quote = (Quote) quoteQueried.get();
			LOGGER.debug("Quote Queried : {}", quote);
			quote.setStatus(Quote.CONFIRMED);
			quote.setUpdatedAt(new Date());
			quoteSaved = quoteRepository.save(quote);

			quoteQueriedAgain = quoteRepository.findById(quoteSaved.getId());
			if(quoteQueried.isPresent()){
				LOGGER.debug("Quote Queried Again : {}", (Quote) quoteQueriedAgain.get());
			}
		}

		if(testCase == 4){
			flipFlop();
		}

		LOGGER.info("return");
		return getQuoteDTOFromQuote((Quote) quoteQueriedAgain.get());
	}


	@Override
	public List<QuoteDTO> findNewQuotes(){

		LOGGER.info("Start");
		List<Quote> newQuotes = quoteRepository.findByStatus(Quote.NEW);
		List<QuoteDTO> newQuotesToReturn = new ArrayList<QuoteDTO>();
		newQuotes.forEach(item->LOGGER.debug(item.toString()));

		newQuotes.forEach(item->{
			newQuotesToReturn.add(getQuoteDTOFromQuote((Quote) item));
		});

		LOGGER.info("Ending");
		return newQuotesToReturn;
	}

	@Override
	public Optional<QuoteDTO> findQuoteById(Long id){

		LOGGER.info("Start");

		QuoteDTO quoteDTO = null;
		Optional quoteQueried = quoteRepository.findById(id);
		if(quoteQueried.isPresent()){
			LOGGER.debug("Quote Queried : {}", ((Quote) quoteQueried.get()).getId());
			quoteDTO = getQuoteDTOFromQuote((Quote) quoteQueried.get());
		}
		else{
			LOGGER.debug("Quote Queried Does NOT Contain anything!");
		}
		Optional<QuoteDTO> optionalQuoteDTO = Optional.ofNullable(quoteDTO);

		LOGGER.info("return");
		return optionalQuoteDTO;
	}

	@Override
	public List<QuoteDTO> findQuotesByStatus(String status){

		LOGGER.info("Start");

		List<QuoteDTO> quoteDTOs = new ArrayList<QuoteDTO>();

		List<Quote> quotes = quoteRepository.findByStatus(status);
		quotes.forEach(quote ->{
			QuoteDTO quoteDTO = getQuoteDTOFromQuote(quote);
			quoteDTOs.add(quoteDTO);

		});

		LOGGER.info("return");
		return quoteDTOs;
	}

	private QuoteDTO getQuoteDTOFromQuote(Quote quote){

		QuoteDTO quoteDTO = new QuoteDTO();
		quoteDTO.setId(quote.getId());
		quoteDTO.setSymbol(quote.getSymbol());
		quoteDTO.setSellerId(quote.getSellerId());
		quoteDTO.setBuyerId(quote.getBuyerId());
		quoteDTO.setAmount(quote.getAmount());
		quoteDTO.setStatus(quote.getStatus());
		quoteDTO.setTest(quote.getTest());
		quoteDTO.setDelay(quote.getDelay());
		quoteDTO.setCreatedAt(quote.getCreatedAt());
		quoteDTO.setUpdatedAt(quote.getUpdatedAt());
		return quoteDTO;
	}
}
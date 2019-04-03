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

import com.acme.ecom.service.AuctionService;
import com.acme.ecom.messaging.StockOrderService;
import com.acme.ecom.exception.QuotesBaseException;
import com.acme.ecom.exception.QuotesMessageRollbackException;
import com.acme.ecom.exception.QuotesConfirmRollbackException;

import java.util.Optional;
import java.util.List;

import com.acme.ecom.dto.QuoteDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class BrokerServiceImpl implements BrokerService{

	private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceImpl.class);
	private static volatile boolean flipFlop = false;

	@Autowired
	@Qualifier("auctionServiceRequired_TX")
	AuctionService auctionServiceRequired_TX;

	@Autowired
	@Qualifier("stockOrderServiceRequired_TX")
	StockOrderService stockOrderServiceRequired_TX;

	@Autowired
	@Qualifier("auctionServiceRequiresNew_TX")
	AuctionService auctionServiceRequiresNew_TX;

	@Autowired
	@Qualifier("stockOrderServiceRequiresNew_TX")
	StockOrderService stockOrderServiceRequiresNew_TX;

	private static synchronized void flipFlop()throws QuotesBaseException{

		if(flipFlop){
			flipFlop = false;
		}
		else{
			flipFlop = true;
		}

		if(flipFlop){
			throw new QuotesBaseException("Explicitly thrown by Broker Application to Roll Back!");
		}
	}

	@Override
	public List<QuoteDTO> findNewQuotes(){

		LOGGER.info("Start");
		List<QuoteDTO> newQuotes = auctionServiceRequired_TX.findNewQuotes();
		LOGGER.info("End");
		return newQuotes;
	}

	@Override
	public void processNewQuote(Long id)throws QuotesBaseException{

		LOGGER.info("Start");
		Optional<QuoteDTO> quoteQueried = auctionServiceRequired_TX.findQuoteById(id);
		QuoteDTO quoteDTO = null;
		Integer testCase = 0;
		Optional<QuoteDTO> quoteQueriedAgain = null;
		QuoteDTO quoteDTOQueriedAgain = null;

		if(quoteQueried.isPresent()){

			quoteDTO = (QuoteDTO) quoteQueried.get();
			testCase = quoteDTO.getTest();
			LOGGER.debug("Quote Queried : {}", quoteDTO.getId());

			if((testCase == 1) || (testCase == 5) || (testCase == 6) || (testCase == 7)){
				LOGGER.debug("Test Case : {}", testCase);
				auctionServiceRequired_TX.confirmQuote(quoteDTO);
				stockOrderServiceRequired_TX.sendOrderMessage(quoteDTO);
			}
			else if(testCase == 2){
				LOGGER.debug("Test Case : {}", testCase);
				auctionServiceRequired_TX.confirmQuote(quoteDTO);
				stockOrderServiceRequired_TX.sendOrderMessage(quoteDTO);

				quoteQueriedAgain = auctionServiceRequired_TX.findQuoteById(id);
				if(quoteQueriedAgain.isPresent()){
					quoteDTOQueriedAgain = (QuoteDTO) quoteQueriedAgain.get();
					LOGGER.debug("Quote Queried again : {}", quoteDTOQueriedAgain);
				}
				flipFlop();
			}
			else if(testCase == 3){
				LOGGER.debug("Test Case : {}", testCase);
				auctionServiceRequired_TX.confirmQuote(quoteDTO);
				try{
					stockOrderServiceRequiresNew_TX.sendOrderMessage(quoteDTO);
				}
				catch(QuotesMessageRollbackException quotesMessageRollbackException){
					LOGGER.error(quotesMessageRollbackException.getMessage());
				}

				quoteQueriedAgain = auctionServiceRequired_TX.findQuoteById(id);
				if(quoteQueriedAgain.isPresent()){
					quoteDTOQueriedAgain = (QuoteDTO) quoteQueriedAgain.get();
					LOGGER.debug("Quote Queried again : {}", quoteDTOQueriedAgain);
				}
			}
			else if(testCase == 4){
				LOGGER.debug("Test Case : {}", testCase);
				try{
					auctionServiceRequiresNew_TX.confirmQuote(quoteDTO);
				}
				catch(QuotesConfirmRollbackException quotesConfirmRollbackException){
					LOGGER.error(quotesConfirmRollbackException.getMessage());
				}
				stockOrderServiceRequired_TX.sendOrderMessage(quoteDTO);
			}
			else if(testCase == 8){
				LOGGER.debug("Test Case : {}", testCase);
				try{
					auctionServiceRequiresNew_TX.confirmQuote(quoteDTO);// PROPAGATION_REQUIRES_NEW Because, during next time out
																		// of QuotesProcessorTask shouldn't fetch this quote
				}
				catch(QuotesConfirmRollbackException quotesConfirmRollbackException){
					LOGGER.error(quotesConfirmRollbackException.getMessage());
				}
				stockOrderServiceRequired_TX.sendOrderMessage(quoteDTO);
			}
			else{
				LOGGER.debug("Undefined Test Case");
			}
		}
		LOGGER.info("End");
	}

}
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
package com.acme.ecom.messaging;

import com.acme.ecom.exception.QuotesBaseException;
import com.acme.ecom.exception.QuotesReconcileRollbackException;
import com.acme.ecom.dto.QuoteDTO;
import com.acme.ecom.model.user.User;
import com.acme.ecom.model.trade.StockTransaction;
import com.acme.ecom.service.QuotesReconcileService;
import com.acme.ecom.service.QuotesTransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

import java.util.Optional;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class SettlementListener implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettlementListener.class);
	private static volatile boolean flipFlop = false;

	@Autowired
	@Qualifier("quotesTransactionServiceRequired_TX")
	QuotesTransactionService quotesTransactionServiceRequired_TX;

	@Autowired
	@Qualifier("quotesTransactionServiceRequiresNew_TX")
	QuotesTransactionService quotesTransactionServiceRequiresNew_TX;

	@Autowired
	@Qualifier("quotesReconcileServiceRequired_TX")
	QuotesReconcileService quotesReconcileServiceRequired_TX;

	@Autowired
	@Qualifier("quotesReconcileServiceRequiresNew_TX")
	QuotesReconcileService quotesReconcileServiceRequiresNew_TX;

	private static synchronized void flipFlop()throws QuotesBaseException{

		if(flipFlop){
			flipFlop = false;
		}
		else{
			flipFlop = true;
		}

		if(flipFlop){
			throw new RuntimeException("Explicitely throwing Settlement Message Receive Exception");
		}
	}

	public void setQuotesReconcileServiceRequired_TX(QuotesReconcileService quotesReconcileServiceRequired_TX){
		this.quotesReconcileServiceRequired_TX = quotesReconcileServiceRequired_TX;
	}

	public void setQuotesReconcileServiceRequiresNew_TX(QuotesReconcileService quotesReconcileServiceRequiresNew_TX){
		this.quotesReconcileServiceRequiresNew_TX = quotesReconcileServiceRequiresNew_TX;
	}

    public void onMessage(Message message) {

        LOGGER.info("Start");

        ObjectMessage objectMessage = null;
        QuoteDTO quoteDTO = null;

        try {
            objectMessage = (ObjectMessage) message;
            quoteDTO = (QuoteDTO) objectMessage.getObject();
        }
        catch (JMSException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

        quotesTransactionServiceRequiresNew_TX.insertUniqueNoErrorOnDuplicate(quoteDTO);

        try {
    		reconcile(quoteDTO);
        }
        catch (QuotesBaseException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

        LOGGER.info("End");
    }

    private void reconcile(QuoteDTO quoteDTO)throws QuotesBaseException{

		LOGGER.info("Start");

		Optional sellerQueriedAgain = null;
		Optional buyerQueriedAgain = null;
		Optional stockTransactionQueriedAgain = null;
		User seller = null;
		User buyer = null;
		StockTransaction stockTransaction = null;

		Integer testCase = quoteDTO.getTest();

		if(!quotesTransactionServiceRequired_TX.isNotSettled(quoteDTO.getId())){

			LOGGER.debug("Quote will not be attempted to be settled again");
			return;
		}

		if(testCase.equals(1) || testCase.equals(2) || testCase.equals(4) || testCase.equals(8)){

			LOGGER.debug("testCase : {}", testCase);

			try{
				quotesReconcileServiceRequired_TX.reconcile(quoteDTO);
				quotesTransactionServiceRequired_TX.markSettled(quoteDTO.getId());
			}
			catch(QuotesBaseException quotesBaseException){
				LOGGER.error(quotesBaseException.getMessage());
			}
			sellerQueriedAgain = quotesReconcileServiceRequired_TX.findUserById(quoteDTO.getSellerId());
			buyerQueriedAgain = quotesReconcileServiceRequired_TX.findUserById(quoteDTO.getBuyerId());;
			if(sellerQueriedAgain.isPresent()){
				seller = (User) sellerQueriedAgain.get();
				LOGGER.debug("Seller Queried again : {}", seller);
			}
			if(buyerQueriedAgain.isPresent()){
				buyer = (User) buyerQueriedAgain.get();
				LOGGER.debug("Buyer Queried again : {}", buyer);
			}

		}
		else if(testCase.equals(5)){

			LOGGER.debug("testCase : {}", testCase);

			try{
				quotesReconcileServiceRequiresNew_TX.reconcile(quoteDTO);
				quotesTransactionServiceRequiresNew_TX.markSettled(quoteDTO.getId());
			}
			catch(QuotesBaseException quotesBaseException){
				LOGGER.error(quotesBaseException.getMessage());
			}

			flipFlop();
		}
		else if(testCase.equals(6)){

			LOGGER.debug("testCase : {}", testCase);
			try{
				quotesReconcileServiceRequiresNew_TX.reconcile(quoteDTO);
				quotesTransactionServiceRequired_TX.markSettled(quoteDTO.getId());
			}
			catch(QuotesBaseException quotesBaseException){
				LOGGER.error(quotesBaseException.getMessage());
			}
		}
		else if(testCase.equals(7)){

			LOGGER.debug("testCase : {}", testCase);
			try{
				quotesReconcileServiceRequired_TX.reconcile(quoteDTO);
				quotesTransactionServiceRequired_TX.markSettled(quoteDTO.getId());
			}
			catch(QuotesBaseException quotesBaseException){
				LOGGER.error(quotesBaseException.getMessage());
			}
			sellerQueriedAgain = quotesReconcileServiceRequired_TX.findUserById(quoteDTO.getSellerId());
			buyerQueriedAgain = quotesReconcileServiceRequired_TX.findUserById(quoteDTO.getBuyerId());;
			stockTransactionQueriedAgain = quotesTransactionServiceRequired_TX.findOptionalByQuoteId(quoteDTO.getId());;
			if(sellerQueriedAgain.isPresent()){
				seller = (User) sellerQueriedAgain.get();
				LOGGER.debug("Seller Queried again : {}", seller);
			}
			if(buyerQueriedAgain.isPresent()){
				buyer = (User) buyerQueriedAgain.get();
				LOGGER.debug("Buyer Queried again : {}", buyer);
			}
			if(stockTransactionQueriedAgain.isPresent()){
				stockTransaction = (StockTransaction) stockTransactionQueriedAgain.get();
				LOGGER.debug("Stock Transaction Queried again : {}", stockTransaction);
			}
			flipFlop();
		}
		else{
			LOGGER.debug("Undefined Test Case");
		}
	}

}
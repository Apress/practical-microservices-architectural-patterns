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

import com.acme.ecom.model.quote.Quote;
import com.acme.ecom.repository.quote.QuoteRepository;
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
public class QuotesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuotesController.class);

    @Autowired
    QuoteRepository quoteRepository;

    @GetMapping("/quotes")
    public List<Quote> getAllQuotes() {

        List<Quote> quotes = quoteRepository.findAll();
		quotes.forEach(item->LOGGER.debug(item.toString()));
		return quotes;

    }

    @GetMapping("/quotes/{id}")
    public ResponseEntity<Quote> getQuoteById(@PathVariable(value = "id") Long quoteId) {
        Quote quote = quoteRepository.findOne(quoteId);
        if(quote == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(quote);
    }

    @PostMapping("/quotes")
    public Quote createQuote(@Valid @RequestBody Quote quote) {

		LOGGER.info("Start");
		LOGGER.debug("Quote Received : {}", quote);
		Date quoteCreatedDate = new Date();
		quote.setStatus(Quote.NEW);
		quote.setCreatedAt(quoteCreatedDate);
		quote.setUpdatedAt(quoteCreatedDate);

        LOGGER.debug("Quote to be saved : {}", quote);
        Quote quoteSaved = quoteRepository.save(quote);
        LOGGER.info("Ending...");
        return quoteSaved;
    }

    @PutMapping("/quotes/{id}")
    public ResponseEntity<Quote> updateQuote(@PathVariable(value = "id") Long quoteId,
                                           @Valid @RequestBody Quote quoteDetails) {
        Quote quote = quoteRepository.findOne(quoteId);
        if(quote == null) {
            return ResponseEntity.notFound().build();
        }
        quote.setStatus(quoteDetails.getStatus());// Only status can be changed
        quote.setUpdatedAt(new Date());

        Quote updatedQuote = quoteRepository.save(quote);
        return ResponseEntity.ok(updatedQuote);
    }

    @DeleteMapping("/quotes/{id}")
    public ResponseEntity<Quote> deleteQuote(@PathVariable(value = "id") Long quoteId) {

        Quote quote = quoteRepository.findOne(quoteId);
        if(quote == null) {
            return ResponseEntity.notFound().build();
        }

        quoteRepository.delete(quote);
        return ResponseEntity.ok().build();
    }
}

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
package com.acme.ecom.gateway.zuul.auth.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import com.acme.ecom.common.security.dto.TokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class AuthenticationPostFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(AuthenticationPostFilter.class);

	public static final String MAGIC_KEY = "obfuscate";

	@Autowired
	private CacheManager cacheManager;

	@Override
		public String filterType() {
		return "post";
	}

	@Override
		public int filterOrder() {
		return 1;
	}

	@Override
		public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		return request.getParameter("grant_type") != null &&  (StringUtils.equals(request.getParameter("grant_type"), "password"));
	}

	  @Override
	public Object run() {

		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		if (request.getRequestURL().indexOf("/oauth/token") > 0 ) {

			log.info("SessionId of the request in post filter  :::::" + request.getSession().getId());
			HttpServletResponse response = ctx.getResponse();
			if (response.getStatus() == HttpServletResponse.SC_OK) {
				try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
					final String responseData = CharStreams
							.toString(new InputStreamReader(responseDataStream, "UTF-8"));
					TokenDTO tokenDTO = new ObjectMapper().readValue(responseData, TokenDTO.class);
					log.info("User ="+request.getParameter("username")+ "  Token  :::::" + tokenDTO.getAccess_token());
					long expiryTime = System.currentTimeMillis() + ((Integer.valueOf(tokenDTO.getExpires_in()) - 1) * 1000);
					String refToken = expiryTime + ":" + createReferenceToken(request.getParameter("username"), expiryTime);
					log.info("Reference token = " + refToken);
					ctx.addZuulResponseHeader("Access-Control-Expose-Headers", "x-token");
					ctx.addZuulResponseHeader("x-token", refToken);
					cacheManager.getCache("AUTH_CACHE").put(refToken, tokenDTO);
				} catch (Exception e) {
					log.warn("Error reading body", e);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}


			}
		}

		return null;
	}


	  private  String createReferenceToken(String username, long expires){
	        StringBuilder signatureBuilder = new StringBuilder();
	        signatureBuilder.append(username);
	        signatureBuilder.append(":");
	        signatureBuilder.append(expires);
	        signatureBuilder.append(":");
	        signatureBuilder.append(MAGIC_KEY);
	        MessageDigest digest;
	        try {
	            digest = MessageDigest.getInstance("MD5");
	        } catch (NoSuchAlgorithmException e) {
	            throw new IllegalStateException("MD5 algorithm not available!");
	        }
	        log.info(new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes()))));
	        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
	    }


	}

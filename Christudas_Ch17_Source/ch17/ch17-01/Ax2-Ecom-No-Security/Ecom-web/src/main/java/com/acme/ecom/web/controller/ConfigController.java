package com.acme.ecom.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RestController
@RequestMapping(path="/configController")
public class ConfigController {

	@Value("${ecom.apigateway.url}")
	private String apiGatewayURL;

	@RequestMapping(method=RequestMethod.GET)
    public HttpEntity< Map<String,String>> getAPIGatewayURL() {
		   Map<String,String> keyValue=new HashMap<String, String>();
		   keyValue.put("apiGatewayURL", apiGatewayURL);
	       return new ResponseEntity< Map<String,String>>(keyValue, HttpStatus.OK);
	  }

}

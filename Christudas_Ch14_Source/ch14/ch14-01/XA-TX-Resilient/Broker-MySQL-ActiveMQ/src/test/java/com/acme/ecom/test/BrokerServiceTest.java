package com.acme.ecom.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.acme.ecom.service.BrokerService;
import com.acme.ecom.dto.QuoteDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring-sender-mysql.xml")
public class BrokerServiceTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(BrokerServiceTest.class);

	@Autowired
	@Qualifier("brokerServiceRequired_TX")
	BrokerService brokerService;

	@Test
	public void testSubmitQuote() throws Exception{

		LOGGER.info("Start");

    	LOGGER.debug("Going to sleep");
    	Thread.sleep(1000 * 60 * 60 * 3);

		LOGGER.info("End");
	}

}

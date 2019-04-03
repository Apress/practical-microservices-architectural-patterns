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
package com.acme.ch06.ex04;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import java.util.Hashtable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.amqp.core.AmqpTemplate;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.InputStream;
import java.io.DataInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class NetworkServer implements MessageListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServer.class);

	private static volatile Hashtable<Long, Socket> clientSockets = new Hashtable<Long, Socket>();
	private volatile long count = 1000;
	private static AmqpTemplate amqpTemplate = null;
	private static ThreadPoolExecutor executorPool = null;

	private void init(){

		//LOGGER.info("Start");
		RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		executorPool = new ThreadPoolExecutor(3, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);

		MonitorThread monitor = new MonitorThread(executorPool, 1 * 30);
		Thread monitorThread = new Thread(monitor);
		monitorThread.start();
		//LOGGER.info("End");
	}


	public static void main(String[] args){

		//LOGGER.info("Start");

        ApplicationContext context = new ClassPathXmlApplicationContext("network-server-context.xml");//loading beans
        amqpTemplate = (AmqpTemplate) context.getBean("amqpTemplate");// getting a reference to the sender bean

		NetworkServer server = new NetworkServer();
		server.listen();
		//LOGGER.info("End");

	}

	private synchronized long getNextCorrelationId(){
		return ++count;
	}

	public static void addItem(long key, Socket socket){
		clientSockets.put(key, socket);
	}
	public static Socket getItem(long key){
		return clientSockets.get(key);
	}
	public static void removeItem(long key){
		clientSockets.remove(key);
	}

	private void listen(){

		LOGGER.info("Start");
		init();

		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		NetworkReaderTask networkReaderTask = null;
		long correlationIdNew = 0L;


		try{
			serverSocket = new ServerSocket(1100);

			LOGGER.debug("Listening for new connections...");
			while(true){
				clientSocket = serverSocket.accept();
				LOGGER.debug("Received a new connection.");

				correlationIdNew = getNextCorrelationId();
				addItem(correlationIdNew, clientSocket);
				networkReaderTask = new NetworkReaderTask(amqpTemplate, correlationIdNew);
				executorPool.execute(new Worker(networkReaderTask));

			}
		}
		catch(IOException ioexception){
			ioexception.printStackTrace();
		}
	}

    public void onMessage(Message message) {

		//LOGGER.info("Start");

		NetworkWriterTask networkWriterTask = new NetworkWriterTask(message);
		if(null == executorPool){
			LOGGER.error("executorPool is null");
		}
		if(null == message){
			LOGGER.error("message is null");
		}
		executorPool.execute(new Worker(networkWriterTask));
		//LOGGER.info("End");
	}

}
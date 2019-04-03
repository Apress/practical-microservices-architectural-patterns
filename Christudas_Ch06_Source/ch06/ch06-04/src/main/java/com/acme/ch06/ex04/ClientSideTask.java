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

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;

import java.io.DataOutputStream;
import java.io.DataInputStream;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
public class ClientSideTask implements Task{

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientSideTask.class);

	private static final String MSG_SEED = "Msg # : ";
	private long id = 0L;

	public ClientSideTask(long id){
		this.id = id;
	}

	public void execute(){

		//LOGGER.info("Start");
		Socket clientSocket = null;
		OutputStream outputStream = null;
		DataOutputStream dataOutputStream = null;
		InputStream inputStream = null;
		DataInputStream dataInputStream = null;

		try{
			//LOGGER.debug("Client.connect. Connecting");
			clientSocket = new Socket("localhost", 1100);
			//LOGGER.debug("Client.connect. Connected");

			outputStream = clientSocket.getOutputStream();
			inputStream = clientSocket.getInputStream();

			dataOutputStream = new DataOutputStream(outputStream);
			dataInputStream = new DataInputStream(inputStream);

			dataOutputStream.writeUTF(MSG_SEED + id);
			dataOutputStream.flush();
			String fromServer = dataInputStream.readUTF();
			LOGGER.debug("Message Send to Server: " + MSG_SEED + id + "; Message Received back from Server: " + fromServer);

		}
		catch(Exception exception){
			exception.printStackTrace();
		}
		//LOGGER.info("End");
	}

}
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
package com.axon.concurrency.user.command.handler;

import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.axon.concurrency.user.command.UserCreationCommand;
import com.axon.concurrency.user.command.UserModificationCommand;
import com.axon.concurrency.user.model.User;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class UserCommandHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCommandHandler.class);

	@Autowired
	@Qualifier(value = "userRepository")
	private Repository<User> userRepository;

	@CommandHandler
	public void handleUserCreationCommand(UserCreationCommand userCreationCommand) {

		LOGGER.info("Start");
		User user = new User(userCreationCommand.getId(), userCreationCommand.getUserName(), userCreationCommand.getAge());
    	userRepository.add(user);
    	LOGGER.debug("New User Created : {}", user);
    	LOGGER.info("End");
	}

	@CommandHandler
	public void handleUserModifyCommand(UserModificationCommand userModificationCommand) {

		LOGGER.info("Start");
		User user = userRepository.load(userModificationCommand.getId());
		Long versionInitial = user.getVersion();
		LOGGER.debug("User Found : {}; versionInitial : {}", user, versionInitial);
		user.setAge(userModificationCommand.getAge());
		user.setUserName(userModificationCommand.getUserName());
		LOGGER.debug("User Modified to : {}", user);
		User userQueried = userRepository.load(userModificationCommand.getId());
		Long versionQueried = userQueried.getVersion();
		LOGGER.debug("User Queried : {}; versionQueried : {}", userQueried, versionQueried);
		Long seconds = 20L;
		LOGGER.debug("Thread Sleeping for {} seconds", seconds);
		try{
			Thread.sleep(1000 * seconds);
		}catch(Exception exception){
			LOGGER.error(exception.getMessage());
		}
		User userQueriedAgain = userRepository.load(userModificationCommand.getId());
		Long versionQueriedAgain = userQueriedAgain.getVersion();
		LOGGER.debug("User Queried Again : {}; versionQueriedAgain : {}", userQueriedAgain, versionQueriedAgain);
		LOGGER.info("End");
	}

}

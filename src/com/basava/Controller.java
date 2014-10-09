package com.basava;

import com.basava.impl.Delegator;
import com.basava.impl.SMTPResponder;
import com.basava.impl.chat.GTalkChatClient;
import com.basava.interfaces.Pollable;
import com.basava.interfaces.Respondable;
import com.basava.mail.imapimpl.ImapMailReader;

/**
 * @author basavar
 *
 */
public class Controller 
{

	public static void main(String[] args) 
	{
		/*
		 * 
		 * 1. Initialize poller
		 * 2. Initialize Delegator/factory
		 * 3. Initia
		 */
		String userName = "mifivetester@gmail.com";
		String password = "mi5tester";
		
		if ( args.length > 1 )
		{
			userName = args[0];
			password = args[1];
		}
		
//		Pollable chatPoller = new GTalkChatClient("mifivetester@gmail.com", "mi5tester");
		Pollable imapMailPoller = new ImapMailReader(userName, password);
		Delegator delegator = new Delegator("./config/taskhandler.properties");
//		Respondable gChatResponder = new  GTalkChatResponder();
		Respondable smtpProcessor = new SMTPResponder(userName, password);

//		Thread gChatPollerThread = new Thread(chatPoller, "chatPoller");
		Thread imapMailPollerThread = new Thread(imapMailPoller, "imapMailPoller");
		Thread delegatorThread = new Thread(delegator, "delegator");
//		Thread gChatResponderThread = new Thread(gChatResponder, "chatResponder");
		Thread smtpProcessorThread = new Thread(smtpProcessor, "smtpProcessor");
		
//		gChatPollerThread.start();
		imapMailPollerThread.start();
		delegatorThread.start();
//		gChatResponderThread.start();
		smtpProcessorThread.start();
		
	}
}

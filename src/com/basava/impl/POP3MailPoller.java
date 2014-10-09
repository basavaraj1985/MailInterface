package com.basava.impl;

import com.basava.interfaces.Pollable;
import com.basava.mail.GmailUtilities;

/**
 * @author basavar
 *
 */
public class POP3MailPoller extends Pollable 
{

	private String userName, password;
	private GmailUtilities gmail ;
	
	public POP3MailPoller(String un, String pwd) 
	{
		userName = un;
		password = pwd;
		gmail = new GmailUtilities();
	}
	
	@Override
	public void execute()
	{
		int newMessageCount = 0;
		try {
			newMessageCount = gmail.getNewMessageCount();
			if ( newMessageCount > 0 )
			{
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void preCondition() 
	{
		gmail.setUserPass(userName, password);
		try {
			gmail.connect();
			gmail.openFolder("INBOX");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

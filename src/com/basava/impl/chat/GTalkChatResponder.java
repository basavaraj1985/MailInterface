package com.basava.impl.chat;

import com.basava.interfaces.IJob;
import com.basava.interfaces.IJob.Type;
import com.basava.interfaces.Respondable;

/**
 * @author basavar
 *
 */
public class GTalkChatResponder extends Respondable 
{
	
	@Override
	public void sendReply() 
	{
		IJob jobToProcess = peekJobToProcessFromQueue();
		if ( null != jobToProcess &&  Type.GTalkChatClient == jobToProcess.getType() )
		{
			jobToProcess = getJobToProcess();
			StringBuffer response = jobToProcess.getResponse();
			GTalkChatClient.loginAndSendMessage("mifivetester@gmail.com", "mi5tester", response.toString(), jobToProcess.getRequestor());
		}
		
	}

}

package com.basava.interfaces;

import java.util.List;

/**
 * @author basavar
 *
 */
public interface IJob 
{
	public enum Type 
	{ GTalkChatClient,
		POP3Mailer,
		IMAPMail
		} 
	
	public Type getType();
	public void setType(Type type);
	
	public String getRequestor();
	public void setRequestor(String req);

	public String getCommand();
	public void setCommand(String cmd);
	
	public List<String> getCommandRequestParameters();
	public void setCommandRequestParameters(List<String> requestParameters);
	
	public String getTimeStamp();
	public void setTimeStamp(String ts);

	public StringBuffer getResponse();
	public void setResponse(String response);
	
}

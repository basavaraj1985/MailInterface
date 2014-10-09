package com.basava.impl;

import java.util.List;

import com.basava.interfaces.IJob;
import com.basava.lib.IOLib;

/**
 * @author basavar
 *
 */
public class AutomationJob implements IJob 
{
	private Type type;
	private String requestor;
	private String command;
	private List<String> commandRequestParameters;
	private StringBuffer response = new StringBuffer();
	private String timeStamp;
	
	public AutomationJob() 
	{
		timeStamp = IOLib.now();
	}
	
	@Override
	public String getCommand() 
	{
		return command;
	}

	@Override
	public void setCommand(String cmd) 
	{
		command = cmd;
	}

	@Override
	public List<String> getCommandRequestParameters() 
	{
		return commandRequestParameters;
	}

	@Override
	public void setCommandRequestParameters(List<String> requestParameters) 
	{
		commandRequestParameters = requestParameters;
	}

	@Override
	public StringBuffer getResponse() 
	{
		return response;
	}

	@Override
	public void setResponse(String response) 
	{
		this.response = new StringBuffer(response);
	}

	@Override
	public String getRequestor() 
	{
		return requestor;
	}

	@Override
	public void setRequestor(String req) 
	{
		requestor = req;
	}

	@Override
	public String getTimeStamp() 
	{
		return timeStamp;
	}

	@Override
	public void setTimeStamp(String ts) 
	{
		timeStamp = ts;
	}

	@Override
	public Type getType() 
	{
		return type;
	}

	@Override
	public void setType(Type type) 
	{
		this.type = type;
	}
}

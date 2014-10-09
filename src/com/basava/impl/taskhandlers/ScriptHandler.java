package com.basava.impl.taskhandlers;

import com.basava.impl.Delegator;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class ScriptHandler extends TaskHandler {

	public ScriptHandler(IJob job) 
	{
		super(job);
	}

	@Override
	public void execute(IJob job) 
	{
		String command = job.getCommand();
		String commandValue = Delegator.getTaskHandlerPropValue(command);
		
		String[] scriptTokens = commandValue.split("script:");
		if ( scriptTokens.length < 2 )
		{
			System.err.println("Incorrect value mapped for key : " + command );
			job.setResponse("Incorrect value mapped for key : " + command );
			Respondable.addResponseJobToProcess(job);
			return;
		}
		
		String scriptToRun = scriptTokens[1];
		StringBuffer buffer = new StringBuffer();
		ExecuteCommand.executeCommand(buffer, scriptToRun, job);
		job.setResponse(buffer.toString());
		Respondable.addResponseJobToProcess(job);
	
	}

}

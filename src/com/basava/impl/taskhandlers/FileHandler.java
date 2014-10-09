package com.basava.impl.taskhandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.basava.impl.Delegator;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class FileHandler extends TaskHandler {

	public FileHandler(IJob job) 
	{
		super(job);
	}

	@Override
	public void execute(IJob job) 
	{
		String command = job.getCommand();
		String commandValue = Delegator.getTaskHandlerPropValue(command);
		
		String[] fileResponseTokens = commandValue.split("file:");
		if ( fileResponseTokens.length < 2 )
		{
			System.err.println("Incorrect value mapped for key : " + command );
			job.setResponse("Incorrect value mapped for key : " + command );
			Respondable.addResponseJobToProcess(job);
			return;
		}
		String fileToLoad = fileResponseTokens[1];
		File file = new File(fileToLoad);
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ( ( line = reader.readLine() ) != null )
			{
				buffer.append(line);
				buffer.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		job.setResponse(buffer.toString());
		Respondable.addResponseJobToProcess(job);
	}
}

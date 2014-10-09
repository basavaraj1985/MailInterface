package com.basava.impl.taskhandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.basava.impl.Delegator;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class HelpSender extends TaskHandler 
{
	public HelpSender(IJob job) 
	{
		super(job);
	}

	@Override
	public void execute(IJob job) 
	{
		File file = new File("./config/help.txt");
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
		
		List<String> supportedCommnads = Delegator.getSupportedCommnads();
		StringBuffer commandBuffer = new StringBuffer();
		for( String s : supportedCommnads )
		{
			commandBuffer.append(s);
			commandBuffer.append(System.getProperty("line.separator"));
		}
		
		String finalResponse = buffer.toString().replaceAll("\\$commands", commandBuffer.toString());
		job.setResponse(finalResponse);
		Respondable.addResponseJobToProcess(job);
	}

}

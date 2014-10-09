package com.basava.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.basava.impl.taskhandlers.ClassHandler;
import com.basava.impl.taskhandlers.ExecuteCommand;
import com.basava.impl.taskhandlers.FileHandler;
import com.basava.impl.taskhandlers.HelpSender;
import com.basava.impl.taskhandlers.ScriptHandler;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Pollable;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class Delegator implements Runnable
{
	private static Properties props;
	
	public Delegator( String file ) 
	{
		props = new Properties();
		try {
			props.load(new BufferedReader(new FileReader(file)));
		} catch (FileNotFoundException e1) {
			System.err.println("File not found exception : " + e1.getMessage() );
			e1.printStackTrace();
		} catch (IOException e2) {
			System.err.println("IO exception : " + e2.getMessage() );
			e2.printStackTrace();
		}
	}
	
	/**
	 * Returns supported list of commands
	 * @return
	 */
	public static List<String> getSupportedCommnads()
	{
		List<String> result = new ArrayList<String>();
		Set<Object> keySet = props.keySet();
		Iterator<Object> iterator = keySet.iterator();
		while ( iterator.hasNext() )
		{
			String next = (String) iterator.next();
			result.add(next);
		}
		return result;
	}
	
	public static String getTaskHandlerPropValue(String key)
	{
		return props.getProperty(key);
	}
	
	@Override
	public void run() 
	{
		while ( true )
		{
			IJob jobToProcess = Pollable.remove();
			if ( jobToProcess != null)
			{
				String command = jobToProcess.getCommand();
				String commandValue = props.getProperty(command);
				if ( commandValue == null )
				{
					TaskHandler executor = new HelpSender(jobToProcess);
					Thread thread = new Thread(executor, "HelpSender");
					thread.start();
					continue;
				}
				
				if ( commandValue.startsWith("class:"))
				{
					TaskHandler executor = new ClassHandler(jobToProcess);
					Thread thread = new Thread(executor, "ClassHandlerThread");
					thread.start();
					continue;
				}
				else if ( commandValue.startsWith("file:")) 
				{
					TaskHandler executor = new FileHandler(jobToProcess);
					Thread thread = new Thread(executor, "FileHandlerThread");
					thread.start();
					continue;
				}
				else if ( commandValue.startsWith("script:"))
				{
					TaskHandler executor = new ScriptHandler(jobToProcess);
					Thread thread = new Thread(executor, "ScriptHandlerThread");
					thread.start();
					continue;
				}
				else if ( commandValue.startsWith("command:"))
				{
					String[] commandToRunTokens = commandValue.split("command:");
					if ( commandToRunTokens.length < 2 )
					{
						System.err.println("Incorrect value mapped for key : " + command );
						continue;
					}
					StringBuffer buffer = new StringBuffer();
					ExecuteCommand.executeCommand(buffer, commandToRunTokens[1], jobToProcess);
					Respondable.addResponseJobToProcess(jobToProcess);
				}
				else
				{
					jobToProcess.setResponse(commandValue);
					Respondable.addResponseJobToProcess(jobToProcess);
				}
				
//				if ( command.equalsIgnoreCase("execute") || command.equalsIgnoreCase("run"))
//				{
//					TaskHandler executor = new ExecuteCommand(jobToProcess);
//					Thread thread = new Thread(executor, "Execute Command Worker Thread!");
//					thread.start();
//				}
			}
		}
	}

}

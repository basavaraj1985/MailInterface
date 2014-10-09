package com.basava.impl.taskhandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class ExecuteCommand extends TaskHandler 
{
	private static String lineSeparator = System.getProperty("line.separator");
	
	public ExecuteCommand(IJob job) 
	{
		super(job);
	}
	
	public static void main(String[] args) {
		StringBuffer buffer = new StringBuffer();
		executeCommand(buffer, "/Users/basavar/check.sh", null);
//		executeCommand(buffer, "banner hello");
	}
	
	@Override
	public void execute(IJob job) 
	{
		List<String> commandsToExecute = job.getCommandRequestParameters();
		if ( commandsToExecute == null || commandsToExecute.isEmpty() )
		{
			job.setResponse("I need set of commands, line separated to execute in the body of mail");
			Respondable.addResponseJobToProcess(job);
			return;
		}
		
		StringBuffer buffer = new StringBuffer();
		for ( String command2Execute : commandsToExecute )
		{
			if ( command2Execute.trim().equals("/eoc"))
			{
				break;
			}
			executeCommand( buffer, command2Execute, job );
		}
		job.setResponse(buffer.toString());
		Respondable.addResponseJobToProcess(job);
	}

	/**
	 * Executes a command and puts output, error output in buffer
	 * @param buffer
	 * @param command2Execute
	 */
	public static void executeCommand(StringBuffer buffer, String command2Execute, IJob job) 
	{
		try {
			Runtime runTime = Runtime.getRuntime();
//			System.out.println("Executing command " + command2Execute );
			buffer.append("Executing command : " + command2Execute ).append(lineSeparator);
			Process process = null;
			if ( command2Execute.endsWith(".bat") || command2Execute.endsWith(".cmd"))
			{
				System.out.println("Executing command " + command2Execute );
//				process = runTime.exec(new String[]{ "cmd.exe", "/c", "start", command2Execute });
				process = runTime.exec("cmd.exe /c start " + command2Execute );
//				ProcessBuilder processBuilder = new ProcessBuilder( "start", command2Execute );
//				process = processBuilder.start();
				System.out.println("Execution of " + command2Execute + " has started!");
				if ( job != null )
				{
					job.setResponse("Execution of " + command2Execute + " has started!");
					Respondable.addResponseJobToProcess(job);
				}
			}
			else if ( command2Execute.endsWith(".sh"))
			{
				System.out.println("Executing command " + command2Execute );
//				process = runTime.exec(new String[]{ "/bin/sh", command2Execute });
				ProcessBuilder processBuilder = new ProcessBuilder( "/bin/sh", command2Execute );
				process = processBuilder.start();
				System.out.println("Execution of " + command2Execute + " has started!");
				if ( job != null )
				{
					job.setResponse("Execution of " + command2Execute + " has started!");
					Respondable.addResponseJobToProcess(job);
				}
			}
			else
			{
				System.out.println("Executing command " + command2Execute );
				process = runTime.exec(command2Execute);
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = reader.readLine();
			while( line != null )
			{
				buffer.append(line);
				buffer.append(lineSeparator);
				line = reader.readLine();
			}
			line = null;
			
			int status = process.waitFor();
			
			if( status != 0 )
			{
				buffer.append("Error Output: ").append(lineSeparator);
			}
			while ( ( line = errorReader.readLine()) != null )
			{
				buffer.append(line).append(lineSeparator);
			}
		} catch (IOException e) {
			buffer.append(e.getMessage()).append(lineSeparator);
			e.printStackTrace();
		} catch (InterruptedException e) {
			buffer.append(e.getMessage()).append(lineSeparator);
			e.printStackTrace();
		} finally {
			System.out.println(buffer.toString());
		}
	}

}

package com.basava.interfaces;

/**
 * @author basavar
 *
 */
public abstract class TaskHandler implements Runnable 
{
	private IJob job;
	
	public TaskHandler(IJob job) 
	{
		this.job = job;
	}
	
	/**
	 * Execute the job and save response in job's response structure
	 * @param job
	 */
	public abstract void execute(IJob job);
	
	@Override
	public void run() 
	{
		try 
		{
			execute(job);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}

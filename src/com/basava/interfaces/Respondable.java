package com.basava.interfaces;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author basavar
 *
 */
public abstract class Respondable implements Runnable 
{
	private static Queue<IJob> responseQ = new ArrayBlockingQueue<IJob>(500);

	public IJob peekJobToProcessFromQueue()
	{
		return responseQ.peek();
	}
	
	/**
	 * 
	 * @param job
	 * @return
	 */
	public static boolean addResponseJobToProcess(IJob job)
	{
		return responseQ.add(job);
	}
	
	/**
	 * 
	 * @return null if queue is empty
	 */
	public static IJob getJobToProcess()
	{
		return responseQ.poll();
	}
	
	/**
	 * 
	 * @param job
	 */
	public abstract void sendReply();
	
	@Override
	public void run() 
	{
		while ( true )
		{
			IJob job = responseQ.peek();
			if ( null != job )
			{
				sendReply();
				System.out.println("A Reply has been sent to : " + job.getRequestor() );
			}
			else
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

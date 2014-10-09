package com.basava.interfaces;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author basavar
 *
 */
public abstract class Pollable implements Runnable 
{
	private static Queue<IJob> requestQ = new ArrayBlockingQueue<IJob>(500);
	
	/**
	 * Put your one-time to be executed before 
	 */
	public abstract void preCondition();
	
	/**
	 * Poll your source of job requests and put them in
	 * request Queue for processing
	 */
	public abstract void execute();
	
	public static  synchronized void add(IJob j)
	{
		boolean add = requestQ.add(j);
		if( !add )
		{
			System.err.println("Could not add job request to queue " + j.getCommand() );
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static synchronized IJob remove()
	{
		return requestQ.poll();
	}
	
	public boolean aaaRequestor(String id)
	{
		return true;
	}
	
	@Override
	public void run() 
	{
		preCondition();
		while( true )
		{
			try {
				execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
} 

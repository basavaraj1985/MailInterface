package com.basava.impl.taskhandlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.basava.impl.Delegator;
import com.basava.interfaces.IJob;
import com.basava.interfaces.Respondable;
import com.basava.interfaces.TaskHandler;

/**
 * @author basavar
 *
 */
public class ClassHandler extends TaskHandler {

	public ClassHandler(IJob job) 
	{
		super(job);
	}

	@Override
	public void execute(IJob job) 
	{
		String command = job.getCommand();
		String commandValue = Delegator.getTaskHandlerPropValue(command);
		
		// Reflectively load appropriate task handler and execute in a thread
		String[] classToLoadTokens = commandValue.split("class:");
		if (classToLoadTokens.length < 2) 
		{
			System.err.println("Incorrect value mapped for key : " + command);
			return;
		}
		String classToLoad = classToLoadTokens[1];

		Class<?> classLoaded = null;
		try {
			classLoaded = Class.forName(classToLoad);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			System.err.println("Class : " + classToLoad
					+ " could not be loaded! Configure " + command
					+ " properly!");
			return;
		}

		Object[] argsList = new Object[1];
		argsList[0] = job;
		Class<?>[] types = new Class[1];
		types[0] = IJob.class;

		Object objectInstance = null;
		try {
			// Constructor<?> constructor = classLoaded.getConstructor();
			// objectInstance = classLoaded.newInstance();
			Constructor<?> declaredConstructor = classLoaded
					.getDeclaredConstructor(types);
			objectInstance = declaredConstructor.newInstance(argsList);
		} catch (Throwable e) {
			System.err.println(e);
		}

		Method methodToBeInvoked = null;
		Method[] declaredMethods = classLoaded.getDeclaredMethods();
		for (int i = 0; i < declaredMethods.length; i++) {
			if (declaredMethods[i].getName().compareTo("execute") == 0) {
				methodToBeInvoked = declaredMethods[i];
				break;
			}
		}

		String errorMessage = null;
		try {
			methodToBeInvoked.invoke(objectInstance, argsList);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			errorMessage = e.getLocalizedMessage();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			errorMessage = e.getLocalizedMessage();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			errorMessage = e.getLocalizedMessage();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = e.getLocalizedMessage();
		}

		if (errorMessage != null) {
			System.err.println("Error while reflective invocation of executor for command : "
							+ command + " Class : " + classToLoad);
			job.setResponse(errorMessage);
			Respondable.addResponseJobToProcess(job);
		}

	}
}
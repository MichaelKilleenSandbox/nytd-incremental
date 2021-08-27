/**
 * Filename: GlobalExceptionAction.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Oct 6, 2009
 *  Author: adam
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.actions;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;


/**
 * Logs an exception that has previously been placed onto the value stack.
 * 
 * @author Adam Russell (18816)
 */
public class GlobalExceptionAction extends ActionSupport
{
	protected final Logger log = Logger.getLogger(getClass());
	private Exception exception = null;
	private String exceptionStack = null;
	
	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS
	 */
	public final String execute()
	{	
		try
		{
			String exceptionMessage = exception.getMessage();
			if (exceptionMessage != null)
			{
				log.error(exceptionMessage);
			}
			if (exceptionStack != null)
			{
				log.error(exceptionStack);
			}
		}
		// Catch everything so we don't get caught
		// in an infinite loop of GlobalExceptionActions.
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		return Action.SUCCESS;
	}

	/**
	 * @return the exception
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/**
	 * @return the exceptionStack
	 */
	public String getExceptionStack()
	{
		return exceptionStack;
	}

	/**
	 * @param exceptionStack the exceptionStack to set
	 */
	public void setExceptionStack(String exceptionStack)
	{
		this.exceptionStack = exceptionStack;
	}
}

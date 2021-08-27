/**
 * Filename: ValidationException
 * 
 * Copyright 2009, ICF International
 * Created: Dec 16, 2009
 * Author: 15670
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.util;

/**
 * @author 15670 (Douglas W Sjoquist)
 * Date: Dec 16, 2009
 * Time: 2:41:00 PM
 */
public class ValidationException extends RuntimeException
{

	protected ValidationResult validationResult;

	public ValidationException(ValidationResult validationResult)
	{
		this.validationResult = validationResult;
	}

	public ValidationException(ValidationResult validationResult, String message)
	{
		super(message);
		this.validationResult = validationResult;
	}

	public ValidationException(ValidationResult validationResult, String message, Throwable cause)
	{
		super(message, cause);
		this.validationResult = validationResult;
	}

	public ValidationException(ValidationResult validationResult, Throwable cause)
	{
		super(cause);
		this.validationResult = validationResult;
	}

	public ValidationResult getValidationResult()
	{
		return validationResult;
	}

	@Override
	public String toString()
	{
		return super.toString() + "\n" + validationResult.buildDetailMessage();
	}
}

/**
 * Filename: ValidationResultItem
 * 
 * Copyright 2009, ICF International
 * Created: Dec 17, 2009
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
 * Date: Dec 17, 2009
 * Time: 11:14:56 AM
 */
public class ValidationResultItem
{
	protected String category;
	protected String item;
	protected String message;
	protected boolean fatal;

	public ValidationResultItem(String category, String item, String message, boolean fatal)
	{
		this.category = category;
		this.item = item;
		this.message = message;
		this.fatal = fatal;
	}

	public String getCategory()
	{
		return category;
	}

	public String getItem()
	{
		return item;
	}

	public String getMessage()
	{
		return message;
	}

	public boolean isFatal()
	{
		return fatal;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(fatal ? "** " : "   ");
		if (item != null)
		{
			sb.append("[");
			sb.append(item);
			sb.append("] ");
		}
		sb.append(message);
		return sb.toString();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ValidationResultItem that = (ValidationResultItem) o;

		if (category != null ? !category.equals(that.category) : that.category != null)
			return false;
		if (item != null ? !item.equals(that.item) : that.item != null)
			return false;
		if (message != null ? !message.equals(that.message) : that.message != null)
			return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = category != null ? category.hashCode() : 0;
		result = 31 * result + (item != null ? item.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		return result;
	}
}

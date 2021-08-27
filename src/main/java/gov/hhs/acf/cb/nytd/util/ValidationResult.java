/**
 * Filename: ValidationResults
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 15670 (Douglas W Sjoquist)
 * Date: Dec 16, 2009
 * Time: 1:33:16 PM
 */
public class ValidationResult
{
	protected transient Logger log = Logger.getLogger(getClass());

	protected LinkedHashMap<String, List<ValidationResultItem>> messages = new LinkedHashMap<String, List<ValidationResultItem>>();
	protected boolean fatal;

	public ValidationResult()
	{
	}

	public boolean isFatal()
	{
		return fatal;
	}

	public void addMessages(ValidationResult vr)
	{
		for (String category : vr.getCategories())
		{
			for (ValidationResultItem validationResultItem : vr.getMessages(category))
			{
				addMessage(category, validationResultItem.getItem(), validationResultItem.getMessage(),
						validationResultItem.isFatal());
			}
		}
	}

	public void addMessage(String category, String item, String message)
	{
		addMessage(category, item, message, false);
	}

	public void addMessage(String category, String item, String message, boolean treatAsFatal)
	{
		getCategoryMessages(category, true)
				.add(new ValidationResultItem(category, item, message, treatAsFatal));
		if (treatAsFatal)
		{
			fatal = true;
		}
	}

	private List<ValidationResultItem> getCategoryMessages(String category, boolean createIfMissing)
	{
		List<ValidationResultItem> result = messages.get(category);
		if ((result == null) && createIfMissing)
		{
			result = new ArrayList<ValidationResultItem>();
			messages.put(category, result);
		}
		return result;
	}

	public boolean isValid()
	{
		return messages.isEmpty();
	}

	public boolean checkForRequiredField(String category, String message, String propertyName, Object value)
	{
		boolean result = true;
		if ((value == null) || ((value instanceof String) && StringUtils.isEmpty((String) value)))
		{
			addMessage(category, message, propertyName, true);
			result = false;
		}
		return result;
	}

	public boolean checkForRegexMatch(String category, String message, String propertyName, String value,
			String regexValidation)
	{
		boolean result = true;
		Pattern pattern = Pattern.compile(regexValidation);
		if (!pattern.matcher(value).matches())
		{
			addMessage(category, message, propertyName, true);
			result = false;
		}
		return result;
	}

	public String buildSummaryMessage()
	{
		StringBuilder sb = new StringBuilder();
		Set<String> categories = getCategories();
		if (!categories.isEmpty())
		{
			sb.append("There were validation errors in ");
			sb.append(categories.size());
			sb.append(" categories");
			for (String category : categories)
			{
				List<ValidationResultItem> errors = messages.get(category);
				int warningCount = 0;
				int fatalCount = 0;
				for (ValidationResultItem error : errors)
				{
					if (error.isFatal())
					{
						fatalCount += 1;
					}
					else
					{
						warningCount += 1;
					}
				}
				sb.append("\n  ");
				if (fatalCount > 0)
				{
					sb.append(fatalCount);
					sb.append(" fatal errors, ");
				}
				if (warningCount > 0)
				{
					sb.append(warningCount);
					sb.append(" warnings, ");
				}
				sb.append(" in ");
				sb.append(category);
			}
		}
		return sb.toString();
	}

	public List<ValidationResultItem> getMessages()
	{
		List<ValidationResultItem> result = new ArrayList<ValidationResultItem>();
		for (String category : getCategories())
		{
			List<ValidationResultItem> list = messages.get(category);
			if (list != null)
			{
				result.addAll(list);
			}
		}
		return result;
	}

	public List<ValidationResultItem> getMessages(String category)
	{
		List<ValidationResultItem> list = messages.get(category);

		List<ValidationResultItem> result = new ArrayList<ValidationResultItem>();
		if (list != null)
		{
			result.addAll(list);
		}
		return result;
	}

	public String getCategoryHeading(String category)
	{
		StringBuilder sb = new StringBuilder();
		List<ValidationResultItem> errors = messages.get(category);
		int warningCount = 0;
		int fatalCount = 0;
		int errorCount = 0;
		if (errors != null)
		{
			errorCount = errors.size();
			for (ValidationResultItem error : errors)
			{
				if (error.isFatal())
				{
					fatalCount += 1;
				}
				else
				{
					warningCount += 1;
				}
			}
		}
		sb.append(category);
		sb.append(" has ");
		if (errorCount == 0)
		{
			sb.append(getPlural("error", 0));
		}
		else
		{
			String sep = "";
			if (fatalCount > 0)
			{
				sb.append(getPlural("fatal error", fatalCount));
				sep = " and ";
			}
			if (warningCount > 0)
			{
				sb.append(sep);
				sb.append(getPlural("warning", warningCount));
			}
		}
		return sb.toString();
	}

	private String getPlural(String base, int c)
	{
		switch (c)
		{
			case 0:
				return "no " + base + "s";
			case 1:
				return "one " + base;
			default:
				return c + " " + base + "s";
		}
	}

	public LinkedHashSet<String> getCategories()
	{
		return new LinkedHashSet<String>(messages.keySet());
	}

	public int getErrorCount()
	{
		int result = 0;
		for (List<ValidationResultItem> items : messages.values())
		{
			result += items.size();
		}
		return result;
	}

	public String buildDetailMessage()
	{
		StringBuilder sb = new StringBuilder();
		Set<String> categories = getCategories();
		String sep = "";
		boolean anyFatal = false;
		for (String category : categories)
		{
			sb.append(sep);
			sep = "\n";
			sb.append(category);
			List<ValidationResultItem> errors = messages.get(category);
			for (ValidationResultItem error : errors)
			{
				sb.append("\n  ");
				if (error.isFatal())
				{
					anyFatal = true;
				}
				sb.append(error);
			}
		}
		if (anyFatal)
		{
			sb.append("\n** These errors will prevent further processing until they are corrected");
		}
		return sb.toString();
	}

}

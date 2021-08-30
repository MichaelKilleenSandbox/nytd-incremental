package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.models.Element;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Helper Class to do hold the common functions used throughout.
 * 
 * 
 * @author Satinder Gill
 * @version $Revision: 1.0 $ $Date: 7/23/2009 $
 */
public class CommonFunctions
{
	protected static Logger log = Logger.getLogger(CommonFunctions.class);

	/**
	 * Ensure the existance of a directory.
	 * 
	 * @param directory directory to ensure exists
	 * @throws IOException directory doesn't exist and can't be created
	 */
	public static void mkdir(File directory) throws IOException
	{
		if (directory.isDirectory())
		{
			return;
		}
		if (directory.exists() && !directory.isDirectory())
		{
			throw new IOException(String.format("%s already exists but is not a directory.", directory.getAbsolutePath()));
		}
		boolean creationSuccessful;
		creationSuccessful = directory.mkdirs();
		if (!creationSuccessful)
		{
			throw new IOException(String.format("Unable to create directory %s.", directory.getAbsolutePath()));
		}
	}

	/**
	 * Convert a Boolean to an Integer.
	 */
	public static Integer booleanToInt(Boolean value)
	{
		assert (value != null);
		return value.booleanValue() ? 1 : 0;
	}

	/**
	 * Returns a string which is the concatenation of the strings in the given
	 * collection.
	 * 
	 * @param separator
	 *           the separator between elements.
	 * @param collection
	 *           the collection to concatenate
	 * @return string which is the concatenation of the strings in the given
	 *         collection
	 */
	public static String join(String separator, Collection<String> collection)
	{
		if (collection.isEmpty())
		{
			return "";
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (String i : collection)
		{
			stringBuilder.append(i + separator);
		}

		stringBuilder.delete(stringBuilder.length() - separator.length(), stringBuilder.length());

		return stringBuilder.toString();
	}

	/**
	 * This function is used to calculate the indexes of the substring to be
	 * displayed on a page.
	 * 
	 * 
	 * 
	 * @param clickedWhat
	 * @param clickCounterString
	 * @param subToShowString
	 * @returns an array of int
	 */
	public int[] calculateToFromIndex(String clickedWhat, String clickCounterString, String subToShowString,
			int totalSubmissions)
	{
		// declares an array of integers, allocates memory for 2 integers
		int[] toFromArray = new int[3];
		int clickCounter = Integer.parseInt(clickCounterString);
		int submissionToShow = 0;
		int fromIndex = 0;
		int toIndex = 0;

		if (subToShowString.equalsIgnoreCase("All"))
			submissionToShow = totalSubmissions;
		else
			submissionToShow = Integer.parseInt(subToShowString);

		log.debug("clickCounter, totalFederalSubmissions, submissionToShow, are: " + clickCounter + " "
				+ submissionToShow);

		// if "next" is clicked
		if (clickedWhat != null && Integer.parseInt(clickedWhat) == 2)
		{
			log.debug("satinder : in NEXT");
			clickCounter++;
			fromIndex = (clickCounter * submissionToShow);
			toIndex = (fromIndex + submissionToShow);

			if (toIndex > totalSubmissions)
				toIndex = totalSubmissions; // to prevent IndexOutOfBoundsException

			log.debug("fromIndex, toIndex, clickCounter are: " + fromIndex + " " + toIndex + " " + clickCounter);
		}
		// if "previous" is clicked
		else if (clickedWhat != null && Integer.parseInt(clickedWhat) == 1)
		{
			log.debug("satinder : in PREVIOUS");
			clickCounter--;

			if (clickCounter < 0)
				clickCounter = 0; // to prevent IndexOutOfBoundsException

			fromIndex = (clickCounter * submissionToShow);
			toIndex = (fromIndex + submissionToShow);

			log.debug("fromIndex, toIndex, clickCounter are: " + fromIndex + " " + toIndex + " " + clickCounter);

		}

		toFromArray[0] = fromIndex;
		toFromArray[1] = toIndex;
		toFromArray[2] = clickCounter;

		return toFromArray;
	}

	public String getSelectedElementNumber(List<Element> elementNumberDropDown, String elementNumberSelected)
	{
		String stringToReturn = "All";

		Iterator<Element> elementIterator = elementNumberDropDown.iterator();
		while (elementIterator.hasNext())
		{

			Element elem = (Element) elementIterator.next();
			long l = Long.parseLong(elementNumberSelected);
			if (l == elem.getId().longValue())
				stringToReturn = elementNumberSelected + " " + elem.getDescription();
		}
		return stringToReturn;
	}
	
	public static <F, T> Collection<T> convertCollection(Collection<F> fromCollection,
	                                                     Class<F> fromClass,
	                                                     Class<T> toClass,
	                                                     String conversionMethod)
	                                                     throws Exception
	{
		Collection<T> toCollection = new HashSet<T>(fromCollection.size());
		Method convert = toClass.getMethod(conversionMethod, fromClass);

		for (F i : fromCollection)
		{
			toCollection.add((T) convert.invoke(null, i));
		}

		return toCollection;
	}
	
	public static Collection<Long> convertCollectionOfStringsToLongs(Collection<String> fromCollection) throws Exception
	{
		return convertCollection(fromCollection, String.class, Long.class, "valueOf");
	}
	
	public static <FK, FV, TK, TV> Map<TK, TV> convertMap(Map<FK, FV> fromMap,
	                                                      Class<FK> fromKeyClass,
	                                                      Class<FV> fromValueClass,
	                                                      Class<TK> toKeyClass,
	                                                      Class<TV> toValueClass,
	                                                      String keyConversionMethod,
	                                                      String valueConversionMethod)
	                                                      throws Exception
	{
		Map<TK, TV> toMap = new HashMap<TK, TV>(fromMap.size());
		Method keyConvert = toKeyClass.getMethod(keyConversionMethod, fromKeyClass);
		Method valueConvert = toValueClass.getMethod(valueConversionMethod, fromValueClass);
		
		for (FK key : fromMap.keySet())
		{
			toMap.put((TK) keyConvert.invoke(null, key),
			          (TV) valueConvert.invoke(null, fromMap.get(key)));
		}
		
		return toMap;
	}

	/**
	 * Given a list of Object arrays of length 2, return a Map with String keys
	 * and values. This is intended to be used to convert Hibernate Query results
	 * into maps suitable for use in Struts select tags, i.e., an id and some
	 * text.
	 * 
	 * @param queryResult
	 *           Hibernate Query result
	 * @return string key/value map
	 */
	public static Map<String, String> getSelectMapFromQueryResult(List<Object[]> queryResult)
	{
		Map<String, String> selectMap = new LinkedHashMap<String, String>();

		for (Object[] pair : queryResult)
		{
			assert (pair.length == 2);
			selectMap.put(String.valueOf(pair[0]), String.valueOf(pair[1]));
		}

		return selectMap;
	}

	/**
	 * Returns a list of parameter values with a given name suitable to be used
	 * in a URL.
	 * 
	 * @param paramVals
	 *           list of parameters, as a Java Collection
	 * @param name
	 *           name of the parameter
	 * @param isFirst
	 *           whether or not parameters be first in URL (should param start
	 *           with ? or &)
	 * @return URL-style multi-value parameter with given name
	 */
	public static String getURLMultiValueParam(Collection<String> paramVals, String name, boolean isFirst)
	{
		String result = "";
		boolean firstIter = true;

		if (paramVals.isEmpty())
		{
			return result;
		}

		for (String param : paramVals)
		{
			String sep = "&";
			if (firstIter)
			{
				if (isFirst)
				{
					sep = "?";
				}
				firstIter = false;
			}

			result = String.format("%s%s%s=%s", result, sep, name, urlEncode(param));
		}

		return result;
	}

	public static String urlEncode(String value)
	{
		try
		{
			String encodedValue = URLEncoder.encode(value, "UTF-8");
			return encodedValue;
		}
		catch (UnsupportedEncodingException e)
		{
			log.error(e.getMessage(), e);
			return value;
		}
	}
	
	public static String urlDecode(String value)
	{
		try
		{
			String decodedValue = URLDecoder.decode(value, "UTF-8");
			return decodedValue;
		}
		catch (UnsupportedEncodingException e)
		{
			log.error(e.getMessage(), e);
			return value;
		}
	}
	
//	public static String getSPSSFIELDNAME(TableFieldBean field)
//	{
//
//
//		if(Constants.SPSS_FIELD_NAMES.containsKey(field.getName()))
//		{
//			return Constants.SPSS_FIELD_NAMES.get(field.getName());
//		}
//
//		return field.getName().toUpperCase();
//	}
	
	/*
	 * Returns spss-code equivalent of the element values.
	 * 
	 * @Param datum
	 * 			Element of a youth record in a submitted file.
	 */
	
	public static String getSPSSCode(TableDatumBean datum) {
		String value = null;
		StringBuffer elementId = new StringBuffer(Constants.STR_0);
		if(datum.getColumn().contains(Constants.OPEN_PARENTHESIS))
		{
			elementId=new StringBuffer(datum.getColumn().substring(
					(datum.getColumn().indexOf(Constants.OPEN_PARENTHESIS) +1), 
					datum.getColumn().indexOf(Constants.CLOSE_PARANTHESIS)
					));
		}
		value = datum.getValue();
		switch(Integer.parseInt(elementId.toString()))
		{
			
			case 5: if(value !=null && value.length() > 0 &&   Constants.e5_sex.containsKey(value))
				{
					value = Integer.toString(Constants.e5_sex.get(value));
				}
				else
				if(value !=null && value.length() > 0 &&   !Constants.e5_sex.containsKey(value))
				{
					value = Constants.STR_OUT_OF_RANGE;
				}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_MISSING;
				}
				break;
			case 6: case 7: case 8:	case 9:
			case 10: case 11:
			case 12:  if(value !=null && value.length() > 0 &&   Constants.element_YES_NO.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
					else if(value == null || (value != null && value.length() == 0))
					{
						value = Constants.STR_MISSING;
					}
				break;
			case 13: if(value !=null && value.length() > 0 &&   Constants.element_YES_NO_UNKNOWN_DECLINED.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO_UNKNOWN_DECLINED.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO_UNKNOWN_DECLINED.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
					else if(value == null || (value != null && value.length() == 0))
					{
						value = Constants.STR_MISSING;
					}
				break;
			case 14: case 16: case 17: case 19:
			case 20: case 21: case 22: case 23:
			case 24: case 25: case 26: case 27:
			case 28: case 29: case 30: case 31:
			case 32: case 33: case 36:
				if(value !=null && value.length() > 0 &&   Constants.element_YES_NO.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 37: case 38: case 39: case 40: case 41: case 45: case 47: case 48: 
			case  49: case 50: case 51: case 52: 
				if(value !=null && value.length() > 0 &&   Constants.element_YES_NO_DECLINED.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO_DECLINED.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO_DECLINED.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 42: case 43: case 44: case 53: 
				if(value !=null && value.length() > 0 &&   Constants.element_YES_NO_NA_DECLINED.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO_NA_DECLINED.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO_NA_DECLINED.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 54: case   55:
				if(value !=null && value.length() > 0 &&   Constants.element_YES_NO_DNK_DECLINED.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO_DNK_DECLINED.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO_DNK_DECLINED.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 56: case 57: case 58:
				if(value !=null && value.length() > 0 &&   Constants.element_YES_NO_NA_DNK_DECLINED.containsKey(value))
				{
					value = Integer.toString(Constants.element_YES_NO_NA_DNK_DECLINED.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.element_YES_NO_NA_DNK_DECLINED.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 18:
				if(value !=null && value.length() > 0 &&   Constants.e18_Edu.containsKey(value))
				{
					value = Integer.toString(Constants.e18_Edu.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.e18_Edu.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 34:
				if(value !=null && value.length() > 0 &&   Constants.e34_Outcomes.containsKey(value))
				{
					value = Integer.toString(Constants.e34_Outcomes.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.e34_Outcomes.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 46:
				if(value !=null && value.length() > 0 &&   Constants.e46_HigherEdu.containsKey(value))
				{
					value = Integer.toString(Constants.e46_HigherEdu.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   !Constants.e46_HigherEdu.containsKey(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
			case 15:
				if(value !=null && value.length() > 0 &&   Constants.e15_FIPS.containsKey(value))
				{
					value = Integer.toString(Constants.e15_FIPS.get(value));
				}
				else
					if(value !=null && value.length() > 0 &&   Constants.STR_NULL.equalsIgnoreCase(value))
					{
						value = Constants.STR_OUT_OF_RANGE;
					}
				else if(value == null || (value != null && value.length() == 0))
				{
					value = Constants.STR_77;
				}
				break;
		}
		return value;
	}
}
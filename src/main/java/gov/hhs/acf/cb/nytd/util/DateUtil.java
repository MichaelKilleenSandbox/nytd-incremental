package gov.hhs.acf.cb.nytd.util;

import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date utility class
 */
public class DateUtil
{
	protected static Logger log = Logger.getLogger(DateUtil.class);
	
	private static SimpleDateFormat defaultFormat = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Returns current Date in String "mm/DD/YYYY" .
	 * 
	 * @return String
	 */
	public static String getCurrentDate(String formatter)
	{
		Calendar cal = new GregorianCalendar();
		Date currentDate1 = cal.getTime();
		String currentDate = formatToString(currentDate1, formatter);
		return currentDate;
	}

	public static String formatDateAndTimezone(int format, Calendar date)
	{
		String dateString;
		switch (format)
		{
			case DateFormat.SHORT:
				dateString = DateUtil.getHourMintueWithTimeZone(date);
				break;
			case DateFormat.MEDIUM:
				dateString = DateUtil.getHourMintueWithTimeZone(date);
				break;
			case DateFormat.LONG:
				dateString = DateUtil.getHourMintueSecondWithTimeZone(date);
				break;
			default:
				dateString = DateUtil.getHourMintueWithTimeZone(date);
		}
		return dateString.replaceAll("E[DS]T", "ET");
	}

	/**
	 * Converts Date (String type) of any format to Date format
	 * 
	 * @param inputDate
	 * @param formatter
	 * @return Date
	 */
	public static Date formatToDate(String inputDate, SimpleDateFormat formatter)
	{
		Date myDate = null;
		try
		{
			formatter.setLenient(true);
			myDate = formatter.parse(inputDate);
		}
		catch (ParseException p)
		{
			log.error(p.getMessage(), p);
		}
		return myDate;
	}

	/**
	 * Converts Date of any format to String format
	 * 
	 * @param aDate
	 * @param formatter
	 * @return String
	 */
	public static String formatToString(Date aDate, String formatter)
	{
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate != null)
		{
			df = new SimpleDateFormat(formatter);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * Converts Date to Calendar
	 * 
	 * @param date
	 * @return Calendar
	 */
	public static Calendar toCalendar(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return cal;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static Calendar toCalendar(String str)
	{
		Date date = formatToDate(str, defaultFormat);
		return toCalendar(date);
	}

	/**
	 * 
	 * @param cal
	 * @return
	 */
	public static String toDateString(Calendar cal)
	{
		String month = Integer.toString((cal.get(Calendar.MONTH)) + 1);
		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		return (month + "/" + day + "/" + cal.get(Calendar.YEAR));
	}

	/**
	 * @param calendar
	 * @return
	 */
	public static String getHourMintueSecondWithTimeZone(Calendar calendar)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss 'ET'");
		return formatter.format(calendar.getTime());
	}

	/**
	 * @param calendar
	 * @return
	 */
	public static String getHourMintueWithTimeZone(Calendar calendar)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm 'ET");
		return formatter.format(calendar.getTime());
	}
	
	public static String toYYYYMMDD(String ddMMMyy)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		String dateReturn = null;
		try
		{
			
			Calendar cal = Calendar.getInstance();
			 cal.setTime(formatter.parse(ddMMMyy));
			 formatter = new SimpleDateFormat("yyyy-MM-dd");
			 dateReturn = formatter.format(cal.getTime());
		}
		catch (ParseException e)
		{
				e.printStackTrace();
		}
		
		return dateReturn;
		
	}
}

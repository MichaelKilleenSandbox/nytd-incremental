/**
 * Copyright 2009, ICF International
 * Created: Nov 25, 2009
 * Author: 16939
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

import java.util.*;


/**
 * @author 16939 This class calculates the current reporting period and
 *         calculates the time left for the regular submission for the current
 *         reporting period
 */
public class CalculateReportingPeriodCountDown
{
	ArrayList<Integer> listFirstRP = new ArrayList<Integer>();
	List<Integer> listSecondRP = new LinkedList<Integer>();

	/**
	 * @return String current Reporting Period
	 */
	public String calculateCurrentReportingPeriod()
	{
		listFirstRP.clear();
		listFirstRP.add(0, 9);
		listFirstRP.add(1, 10);
		listFirstRP.add(2, 11);
		listFirstRP.add(3, 0);
		listFirstRP.add(4, 1);
		listFirstRP.add(5, 2);

		listSecondRP.clear();
		listSecondRP.add(0, 3);
		listSecondRP.add(1, 4);
		listSecondRP.add(2, 5);
		listSecondRP.add(3, 6);
		listSecondRP.add(4, 7);
		listSecondRP.add(5, 8);

		GregorianCalendar todayDate = new GregorianCalendar();
		todayDate.setLenient(false);
		int currentMonth = todayDate.get(GregorianCalendar.MONTH);
		String currentReportingPeriod = null;
		if (listFirstRP.contains(currentMonth))
		{
			// October =9 , November =10 and December = 11 according
			// GregorianCalendar
			if (currentMonth == 9 || currentMonth == 10 || currentMonth == 11)
			{
				int year = todayDate.get(GregorianCalendar.YEAR);
				year++;
				currentReportingPeriod = year + "A";
			}
			else
			{
				currentReportingPeriod = todayDate.get(GregorianCalendar.YEAR) + " A";
			}
		}

		if (listSecondRP.contains(currentMonth))
		{
			currentReportingPeriod = todayDate.get(GregorianCalendar.YEAR) + " B";
		}

		return currentReportingPeriod;
	}

	public String calculateSubmissionCountDown()
	{
		String countDownMessage = null;
		int totalDaysInReportingPeriod = 0;

		String result = calculateCurrentReportingPeriod();

		GregorianCalendar currentCal = new GregorianCalendar();
		currentCal.setLenient(false);
		int currentMonth = currentCal.get(GregorianCalendar.MONTH);

		int totalDaysLapsedFromSubmission = 0;
		boolean reportingPeriodCheck = false;
		GregorianCalendar dynamicCal = new GregorianCalendar();
		if (result.endsWith("A"))
		{
			totalDaysInReportingPeriod = calculateReportingPeriodTotalDays(listFirstRP, dynamicCal);
			reportingPeriodCheck = true;
		}
		if (result.endsWith("B"))
		{
			reportingPeriodCheck = false;
			totalDaysInReportingPeriod = calculateReportingPeriodTotalDays(listSecondRP, dynamicCal);
		}

		int currentMonthIndex = 0;
		if (reportingPeriodCheck)
		{
			currentMonthIndex = listFirstRP.indexOf(currentMonth);
			for (int i = 0; i < currentMonthIndex; i++)
			{
				totalDaysLapsedFromSubmission += CalculateDaysInAMonth(listFirstRP.get(i));
			}
		}
		else
		{
			currentMonthIndex = listSecondRP.indexOf(currentMonth);
			for (int i = 0; i < currentMonthIndex; i++)
			{
				totalDaysLapsedFromSubmission += CalculateDaysInAMonth(listSecondRP.get(i));
			}
		}

		GregorianCalendar currentMonthDay = new GregorianCalendar();
		int dayOfMonth = currentMonthDay.get(GregorianCalendar.DAY_OF_MONTH);

		// Deduct 1 for the current day because day has not passed yet
		totalDaysLapsedFromSubmission += dayOfMonth - Constants.CURRENTDAY;
		int totalDaysLeftForSubmission = totalDaysInReportingPeriod - totalDaysLapsedFromSubmission;

		int totalHoursLeftForSubmission = Constants.TOTALHOURSINADAY
				- currentMonthDay.get(GregorianCalendar.HOUR_OF_DAY);

		int totalMinutesLeftForSubmission = Constants.TOTALMINUTESINANHOUR
				- currentMonthDay.get(GregorianCalendar.MINUTE);

		countDownMessage = result + " Report Period submission is due in " + totalDaysLeftForSubmission
				+ " Days: " + totalHoursLeftForSubmission + " Hours: " + totalMinutesLeftForSubmission
				+ " Minutes";

		return countDownMessage;

	}

	private int calculateReportingPeriodTotalDays(List list, GregorianCalendar cal)
	{
		int totalDaysInReportingPeriod = 0;
		System.out.println("the list size" + list.size());
		Iterator listIterator = list.iterator();
		int count = 0;
		while (listIterator.hasNext())
		{
			Integer integer = (Integer) listIterator.next();
			System.out.println("counter" + count++);
			totalDaysInReportingPeriod += CalculateDaysInAMonth(integer.intValue());
		}

		// adding 45 days as per SRS [BR-06-03-02]
		totalDaysInReportingPeriod += Constants.REPORTINGPERIODGRACEPERIOD;

		// Check the day. Is it Saturday Or Sunday?
		// If true, extend the due date since Saturday and Sunday are not business
		// days
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.DAY_OF_YEAR, totalDaysInReportingPeriod);
		// GregorianCalendar.DAY_OF_WEEK) = 7 corresponds to Saturday
		// GregorianCalendar.DAY_OF_WEEK) = 1 corresponds to Sunday

		if (calendar.get(GregorianCalendar.DAY_OF_WEEK) == 7)
		{
			totalDaysInReportingPeriod += 2;
		}
		if (calendar.get(GregorianCalendar.DAY_OF_WEEK) == 1)
		{
			totalDaysInReportingPeriod += 1;
		}
		return totalDaysInReportingPeriod;

	}

	private int CalculateDaysInAMonth(int month)
	{
		GregorianCalendar cal2 = new GregorianCalendar();
		// GregorianCalendar month is off by one digit
		// checking year and setting it based on Federal Fiscal year
		if (cal2.get(cal2.MONTH) == 9 || cal2.get(cal2.MONTH) == 10 || cal2.get(cal2.MONTH) == 11)
		{
			int calYear = cal2.get(cal2.YEAR);
			calYear += 1;
			cal2.set(GregorianCalendar.YEAR, calYear);
			cal2.set(GregorianCalendar.MONTH, month);
			cal2.set(GregorianCalendar.DAY_OF_MONTH, 1);
		}
		else
		{
			cal2.set(GregorianCalendar.YEAR, month, 1);
		}
		return cal2.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
	}
}
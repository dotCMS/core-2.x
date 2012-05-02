package com.dotmarketing.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class DateUtil {
	
	public static final String DIFF_YEARS = "diffYears";
	public static final String DIFF_MONTHS = "diffMonths";
	public static final String DIFF_DAYS = "diffDays";
	public static final String DIFF_HOURS = "diffHours";
	public static final String DIFF_MINUTES = "diffMinutes";

	/**
	 * This method allows you to add to a java.util.Date returning a Date instead of void like the Calendar does
	 * @param date The date to modify
	 * @param calendarField The static field from java.util.Calendar to add
	 * @param numberToAdd The number to add
	 * @return Date
	 */
	public static Date addDate(Date date, int calendarField, int numberToAdd){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, numberToAdd);
		return c.getTime();
	}
	
	/**
	 * This method will set the time on a date to 00:00:00
	 * @param date
	 * @return Date
	 */
	public static Date minTime(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.AM_PM, Calendar.AM);
		return c.getTime();
	}
	
	/**
	 * This method will set the time on a date to 23:59:59
	 * @param date
	 * @return Date
	 */
	public static Date maxTime(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR,11);
		c.set(Calendar.MINUTE,59);
		c.set(Calendar.SECOND,59);
		c.set(Calendar.AM_PM, Calendar.PM);
		return c.getTime();
	}
	
	/**
	 * This method will return the diff between to dates
	 * @param from Date
	 * @param to Date
	 * @return HashMap
	 */
	public static HashMap<String, Long> diffDates(Date from, Date to){
		HashMap<String, Long> result = new HashMap<String, Long>(8);
		try {
			Calendar toCal = new GregorianCalendar();
			toCal.setTime(to);
			Calendar fromCal = new GregorianCalendar();
			fromCal.setTime(from);
			long diffYears = 0;
			long diffMonths = -1;
			int fromMonth = fromCal.get(Calendar.MONTH);
			int currentMonth;
			for (;fromCal.before(toCal);) {
				fromCal.add(Calendar.MONTH, 1);
				++diffMonths;
				currentMonth = fromCal.get(Calendar.MONTH);
				if (currentMonth == fromMonth)
					++diffYears;
			}
			
			result.put(DIFF_YEARS, diffYears);
			result.put(DIFF_MONTHS, diffMonths);
			
			long milliSecondDiff = to.getTime() - from.getTime(); 
			long diffDays = milliSecondDiff / (24 * 3600 * 1000);
			long diffHours = milliSecondDiff / (3600 * 1000);
			long timeLeft = milliSecondDiff % (3600 * 1000);
			long diffMinutes = timeLeft / (60 * 1000);
			
			result.put(DIFF_DAYS, diffDays);
			result.put(DIFF_HOURS, diffHours);
			result.put(DIFF_MINUTES, diffMinutes);
		} catch (Exception e) {
			Logger.warn(DateUtil.class, e.toString());
		}
		return result;
	}
	
	/**
	 * This method try to parse a string into a Date object using an array with the valid formats
	 * @param date the string to be parsed
	 * @param formats the valid format to parse the string
	 * @return return the Date object that represent the string 
	 * @throws java.text.ParseException
	 */

	public static Date convertDate (String date, String[] formats) throws java.text.ParseException {
		Date ret = null;
		for (String pattern : formats) {
			try {
				ret = new SimpleDateFormat(pattern).parse(date);
				break;
			} catch (java.text.ParseException e) { }
		}
		if(ret == null) throw new java.text.ParseException(date, 0);

		return ret;
	}
	
	/**
	 * This method takes a Date and the desired format to convert it to a String
	 */
	public static String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}
	
}

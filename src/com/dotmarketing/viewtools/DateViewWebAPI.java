/**
 *  com.dotmarketing.viewtools
 */
package com.dotmarketing.viewtools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.util.DateUtil;
import com.dotmarketing.util.Logger;

/**
 * WebAPI class to manage custom date views
 *
 * @author  Armando Siem
 * @since   1.6.0
 */
public class DateViewWebAPI extends DateTool implements ViewTool {
	
	/**
	  * Init method of the WebAPI
	  * @param		obj Obj
	  */
	
	public void init(Object obj) {
	}
	
	/**
	  * Method of the API to show custom diff date result with the current date.
	  * @param		date date to diff with the current date.
	  * @return		String with a result message.
	  */
	public String friendly(Date date) {
		String sinceMessage = "less than a minute ago";
		Date now = new Date();
		
		try {
			SimpleDateFormat sdf;
			
			if (date == null || date.after(now))
				return sinceMessage;
			
			HashMap<String, Long> diffDates = DateUtil.diffDates(date, now);
			if (6 < diffDates.get(DateUtil.DIFF_DAYS)) {
				sdf = new SimpleDateFormat("EEE., MMMM d, h:mm aa");
				sinceMessage = sdf.format(date);
			} else if (1 < diffDates.get(DateUtil.DIFF_DAYS)) {
				sdf = new SimpleDateFormat("EEEE 'at' h:mm aa");
				sinceMessage = sdf.format(date);
			} else if (date.getDate() != now.getDate()) {
				sdf = new SimpleDateFormat("'Yesterday at' h:mm aa");
				sinceMessage = sdf.format(date);
			} else if (0 < diffDates.get(DateUtil.DIFF_HOURS)) {
				sinceMessage = "" + diffDates.get(DateUtil.DIFF_HOURS) + " hour(s) ago";
			} else if (0 < diffDates.get(DateUtil.DIFF_MINUTES)) {
				sinceMessage = "" + diffDates.get(DateUtil.DIFF_MINUTES) + " minutes(s) ago";
			}
		} catch (Exception e) {
			Logger.warn(DateViewWebAPI.class, e.toString());
		}
		
		return sinceMessage;
	}
	
	public int getOffSet()
	{
		Date now = new Date();
		return getOffSet(now);
	}
	
	public int getOffSet(Date date)
	{
		GregorianCalendar gc = new GregorianCalendar();		
 		TimeZone tz = TimeZone.getDefault();
	 	int offset = tz.getOffset((date).getTime());
	 	return offset;	 	
	}
}
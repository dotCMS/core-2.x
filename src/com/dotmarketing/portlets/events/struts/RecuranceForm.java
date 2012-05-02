package com.dotmarketing.portlets.events.struts;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

/** @author Hibernate CodeGenerator */
public class RecuranceForm extends ValidatorForm {

	String occurs;
	int interval;
	String startDateString;
	String endDateString;
	String startTimeString;
	String endTimeString;
	Date starting;
	Date ending;

	String parent;
	int dayOfMonth;
	String inode;
	String dispatch;
	boolean mon;
	boolean tue;
	boolean wed;
	boolean thu;
	boolean fri;
	boolean sat;
	boolean sun;

	/**
	 * Returns the interval.
	 * @return int
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * Returns the occurs.
	 * @return String
	 */
	public String getOccurs() {
		return occurs;
	}

	public String getDaysOfWeek() {
		StringBuffer sb = new StringBuffer();
		if (sun)
			sb.append(1);
		if (mon)
			sb.append(2);
		if (tue)
			sb.append(3);
		if (wed)
			sb.append(4);
		if (thu)
			sb.append(5);
		if (fri)
			sb.append(6);
		if (sat)
			sb.append(7);
		return sb.toString();
	}

	public void setDaysOfWeek(String x) {
		if (x != null) {

			sun = x.indexOf("1") > -1;
			mon = x.indexOf("2") > -1;
			tue = x.indexOf("3") > -1;
			wed = x.indexOf("4") > -1;
			thu = x.indexOf("5") > -1;
			fri = x.indexOf("6") > -1;
			sat = x.indexOf("7") > -1;
		}
	}

	/**
	 * Sets the interval.
	 * @param interval The interval to set
	 */
	public void setInterval(int interval) {
        //Logger.info("interval:" + interval);
		this.interval = interval;
	}

	/**
	 * Sets the occurs.
	 * @param occurs The occurs to set
	 */
	public void setOccurs(String occurs) {
		this.occurs = occurs;
	}

	/**
	 * Returns the parent.
	 * @return int
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * Returns the dispatch.
	 * @return String
	 */
	public String getDispatch() {
		return dispatch;
	}

	/**
	 * Sets the dispatch.
	 * @param dispatch The dispatch to set
	 */
	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	/**
	 * Returns the inode.
	 * @return String
	 */
	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	/**
	 * Sets the inode.
	 * @param inode The inode to set
	 */
	public void setInode(String inode) {
		this.inode = inode;
	}

	/**
	 * Returns the fri.
	 * @return boolean
	 */
	public boolean isFri() {
		return fri;
	}

	/**
	 * Returns the mon.
	 * @return boolean
	 */
	public boolean isMon() {
		return mon;
	}

	/**
	 * Returns the sat.
	 * @return boolean
	 */
	public boolean isSat() {
		return sat;
	}

	/**
	 * Returns the sun.
	 * @return boolean
	 */
	public boolean isSun() {
		return sun;
	}

	/**
	 * Returns the thu.
	 * @return boolean
	 */
	public boolean isThu() {
		return thu;
	}

	/**
	 * Returns the tue.
	 * @return boolean
	 */
	public boolean isTue() {
		return tue;
	}

	/**
	 * Returns the wed.
	 * @return boolean
	 */
	public boolean isWed() {
		return wed;
	}

	/**
	 * Sets the fri.
	 * @param fri The fri to set
	 */
	public void setFri(boolean fri) {
		this.fri = fri;
	}

	/**
	 * Sets the mon.
	 * @param mon The mon to set
	 */
	public void setMon(boolean mon) {
		this.mon = mon;
	}

	/**
	 * Sets the sat.
	 * @param sat The sat to set
	 */
	public void setSat(boolean sat) {
		this.sat = sat;
	}

	/**
	 * Sets the sun.
	 * @param sun The sun to set
	 */
	public void setSun(boolean sun) {
		this.sun = sun;
	}

	/**
	 * Sets the thu.
	 * @param thu The thu to set
	 */
	public void setThu(boolean thu) {
		this.thu = thu;
	}

	/**
	 * Sets the tue.
	 * @param tue The tue to set
	 */
	public void setTue(boolean tue) {
		this.tue = tue;
	}

	/**
	 * Sets the wed.
	 * @param wed The wed to set
	 */
	public void setWed(boolean wed) {
		this.wed = wed;
	}

	/**
	 * Returns the dayOfMonth.
	 * @return int
	 */
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	/**
	 * Sets the dayOfMonth.
	 * @param dayOfMonth The dayOfMonth to set
	 */
	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public void setStarting(java.util.Date start) {
		starting = start;
	}

	public void setEnding(java.util.Date end) {
		ending = end;
	}

	/**
	 * Returns the endDateString.
	 * @return String
	 */
	public String getEndDateString() {
		return UtilMethods.dateToHTMLDate(ending) + " " + UtilMethods.dateToHTMLTime(ending);
	}

	/**
	 * Returns the endTimeString.
	 * @return String
	 */
	public String getEndTimeString() {
		return endTimeString;
	}

	/**
	 * Returns the startDateString.
	 * @return String
	 */
	public String getStartDateString() {
		return UtilMethods.dateToHTMLDate(starting) + " " + UtilMethods.dateToHTMLTime(starting);
	}

	/**
	 * Returns the startTimeString.
	 * @return String
	 */
	public String getStartTimeString() {
		return startTimeString;
	}

	/**
	 * Sets the endDateString.
	 * @param endDateString The endDateString to set
	 */
	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
		if (!endDateString.equals(""))
			try {
				this.ending = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(endDateString);			
			} catch(ParseException ex) {
				//Logger.info("Exception parsing end date: " + ex.getMessage());
				Logger.error(this,ex.getMessage(),ex);
			}
	}

	/**
	 * Sets the endTimeString.
	 * @param endTimeString The endTimeString to set
	 */
	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}

	/**
	 * Sets the startDateString.
	 * @param startDateString The startDateString to set
	 */
	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
		if (!startDateString.equals(""))
			try {
				this.starting = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(startDateString);			
			} catch(ParseException ex) {
				//Logger.info("Exception parsing start date: " + ex.getMessage());
				Logger.error(this,ex.getMessage(),ex);
			}
	}

	/**
	 * Sets the startTimeString.
	 * @param startTimeString The startTimeString to set
	 */
	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public java.util.Date getStarting() {
		return this.starting;

	}
	
	public java.util.Date getEnding() {
		return this.ending;
	}

}

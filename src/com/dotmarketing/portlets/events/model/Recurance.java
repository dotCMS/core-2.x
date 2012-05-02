package com.dotmarketing.portlets.events.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.dotmarketing.beans.Inode;

/** @author Hibernate CodeGenerator */
public class Recurance extends Inode implements Serializable {

	String occurs;
	int interval;
	Date starting;
	Date ending;
	String daysOfWeek;
	int dayOfMonth;

	/**
	 * Returns the daysOfWeek.
	 * @return String
	 */
	public String getDaysOfWeek() {
		return daysOfWeek;
	}

	public Recurance() {
		starting = new java.util.Date();
		ending = new java.util.Date();
		interval = 1;
		dayOfMonth = 1;
		occurs = "day";
        setType("recurance");
	}

	/**
	 * Returns the ending.
	 * @return Date
	 */
	public Date getEnding() {
		return ending;
	}

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

	/**
	 * Returns the starting.
	 * @return Date
	 */
	public Date getStarting() {
		return starting;
	}

	/**
	 * Sets the daysOfWeek.
	 * @param daysOfWeek The daysOfWeek to set
	 */
	public void setDaysOfWeek(String daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	/**
	 * Sets the ending.
	 * @param ending The ending to set
	 */
	public void setEnding(Date ending) {
		this.ending = ending;
	}

	/**
	 * Sets the interval.
	 * @param interval The interval to set
	 */
	public void setInterval(int interval) {
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
	 * Sets the starting.
	 * @param starting The starting to set
	 */
	public void setStarting(Date starting) {
		this.starting = starting;
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
    
    public void setStartTime(java.util.Date newStartTime){

        
        
        GregorianCalendar _starting = new GregorianCalendar();
        _starting.setTime(starting);
        
        
        GregorianCalendar _newStartTime = new GregorianCalendar();
        _newStartTime.setTime(newStartTime);


        
        _starting.set(Calendar.HOUR_OF_DAY, _newStartTime.get(Calendar.HOUR_OF_DAY));
        _starting.set(Calendar.MINUTE, _newStartTime.get(Calendar.MINUTE));

        this.starting = _starting.getTime();
        
        
    }
    
    public void setEndTime(java.util.Date newEndTime){

        GregorianCalendar _ending = new GregorianCalendar();
        GregorianCalendar _newEndTime = new GregorianCalendar();
        _ending.setTime(ending);
        _newEndTime.setTime(newEndTime);
        _ending.set(Calendar.HOUR_OF_DAY, _newEndTime.get(Calendar.HOUR_OF_DAY));
        _ending.set(Calendar.MINUTE, _newEndTime.get(Calendar.MINUTE));
        this.ending = _ending.getTime();
        
        
    }
    

}

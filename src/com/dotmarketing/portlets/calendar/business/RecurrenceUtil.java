package com.dotmarketing.portlets.calendar.business;

import com.dotmarketing.portlets.calendar.model.Event;
import com.dotmarketing.util.UtilMethods;

public class RecurrenceUtil {
	
	public static String RECURRENCE_PREFIX = "-recurrence";
	public static String RECURRENCE_SEPARATOR = "@";
	
	public static String getBaseEventIdentifier(String recurrentIdentifier){
		if(recurrentIdentifier.contains(RECURRENCE_PREFIX)){
			return recurrentIdentifier.substring(0, recurrentIdentifier.indexOf(RECURRENCE_PREFIX));
		}
		return recurrentIdentifier;
	}
	
	public static String getRecurrentEventIdentifier(Event recurrentEvent){
		return recurrentEvent.getIdentifier()+RECURRENCE_PREFIX
	       +RECURRENCE_SEPARATOR+recurrentEvent.getStartDate().getTime()
	       +RECURRENCE_SEPARATOR+recurrentEvent.getEndDate().getTime();
	}
	
	public static String[] getRecurrenceDates(String recurrentEventIdentifier){
		String[] recDates = null;
		if(recurrentEventIdentifier.contains(RECURRENCE_PREFIX) && 
				recurrentEventIdentifier.contains(RECURRENCE_SEPARATOR)){
				String idAux = "";
				try{
					idAux = recurrentEventIdentifier.substring(recurrentEventIdentifier.indexOf(RECURRENCE_SEPARATOR)+1);
				}catch(IndexOutOfBoundsException e){}
				if(UtilMethods.isSet(idAux)){
					recDates = idAux.split(RECURRENCE_SEPARATOR);
				}
			}
		return recDates;
		
	}
	
	

}

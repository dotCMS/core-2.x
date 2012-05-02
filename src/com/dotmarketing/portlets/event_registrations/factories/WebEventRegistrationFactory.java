package com.dotmarketing.portlets.event_registrations.factories;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.event_registrations.struts.ViewRegistrationsForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventRegistrationForm;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

/**
 *
 * @author  Maria Ahues
 */
public class WebEventRegistrationFactory {
	
	public static java.util.List getAllWebEventRegistrations() {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' order by date_posted desc");
		return dh.list();
	}
	public static java.util.List getAllWebEventRegistrations(String orderby) {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List getWebEventRegistrationsByCondition(String condition) {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and " + condition);
		return dh.list();
	}
	
	public static java.util.List getTodayWebEventRegistrations() {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and  date_posted >= ? order by date_posted desc");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setParam(cal.getTime());
		return dh.list();
	}

	public static java.util.List getEventAttendees(WebEventRegistration eventRegistration, String orderby) {
		String condition = "event_registration_inode = '" + eventRegistration.getInode()+"'";
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventLocation.class, condition, orderby);
	}
	
	public static java.util.List getEventAttendees(WebEventRegistrationForm eventRegistrationForm, String orderby) {
		String condition = "event_registration_inode = '" + eventRegistrationForm.getInode()+"'";
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventLocation.class, condition, orderby);
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<WebEventAttendee> getEventAttendees(WebEventRegistration eventRegistration) {
		String condition = "event_registration_inode = '" + eventRegistration.getInode()+"'";
		String orderby = "first_name, last_name";
		return (java.util.List<WebEventAttendee>)InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventAttendee.class, condition, orderby);
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<WebEventAttendee> getEventAttendees(WebEventRegistrationForm eventRegistrationForm) {
		String condition = "event_registration_inode = '" + eventRegistrationForm.getInode()+"'";
		String orderby = "first_name, last_name";
		return (java.util.List<WebEventAttendee>)InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventAttendee.class, condition, orderby);
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<WebEventAttendee> getEventAttendeesByEmail(WebEventRegistration eventRegistration, String email) {
		String condition = "event_registration_inode = '" + eventRegistration.getInode() + "' and email = '" + email + "'";
		String orderby = "first_name, last_name";
		return (java.util.List<WebEventAttendee>)InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventAttendee.class, condition, orderby);
	}

	/*public static WebEventRegistration getWebEventRegistration(long inode) {
		return (WebEventRegistration) InodeFactory.getInode(inode, WebEventRegistration.class);
	}*/

	public static WebEventRegistration getWebEventRegistration(String inode) {
		return (WebEventRegistration) InodeFactory.getInode(inode, WebEventRegistration.class);
	}

	public static WebEventRegistration newInstance() {
		WebEventRegistration m = new WebEventRegistration();
		return m;
	}

	public static void saveWebEventRegistration(WebEventRegistration WebEventRegistration) {
		InodeFactory.saveInode(WebEventRegistration);
	}

	public static void deleteWebEventRegistration(WebEventRegistration WebEventRegistration) {
		InodeFactory.deleteInode(WebEventRegistration);
	}
	
	public static List getFilteredRegistrations(ViewRegistrationsForm vForm) {
		
    	String eventInode = vForm.getEventInode();
    	String locationInode = vForm.getLocationInode();
    	Date startDate = vForm.getStartDate();
    	Date endDate = vForm.getEndDate();
    	String regNumber = vForm.getRegistrationNumber();
    	String invoiceNumber = vForm.getInvoiceNumber();
    	int paymentStatus = vForm.getPaymentStatus();
		int institute = vForm.getInstitute();

    	String condition = "";
    	
    	if (UtilMethods.isSet(regNumber) && !regNumber.equals("0")) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.inode = '" + regNumber+"'";
    	}
    	if (paymentStatus > 0) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.registration_status = " + paymentStatus;
    	}
    	if (InodeUtils.isSet(eventInode) && !eventInode.equals("0")) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.event_inode = '" + eventInode+"'";
    	}
    	if (InodeUtils.isSet(locationInode) && !locationInode.equals("0")) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.event_location_inode = '" + locationInode+"'";
    	}
    	if (UtilMethods.isSet(startDate)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.date_posted >= ? ";
    	}
    	if (UtilMethods.isSet(endDate)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.date_posted <= ? ";
    	}
    	if (UtilMethods.isSet(invoiceNumber)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event_registration.invoice_number = '" + invoiceNumber + "'";
    	}
    	//institute
    	if (institute == 1) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event.is_institute = " + DbConnectionFactory.getDBTrue();
    	}
    	//webinar
    	else if (institute == 2) {
    		if (condition.length()>0) condition += " and ";
    		condition += " web_event.is_institute = " + DbConnectionFactory.getDBFalse();
    	}
    	
    	//to create the query and get the results
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		String query = "select {web_event_registration.*} from web_event_registration as web_event_registration, inode as web_event_registration_1_, web_event as web_event";
		query += " where web_event_registration_1_.type='event_registration' and web_event_registration_1_.inode = web_event_registration.inode and web_event_registration.event_inode = web_event.inode ";

		if (condition.length()>0) {
			query += "and " + condition;
		}
		String orderBy = vForm.getOrderBy();
		String direction = vForm.getSelectedDirection(); 
		if (UtilMethods.isSet(orderBy))
			orderBy = " order by web_event_registration." + orderBy + " " + direction; 
		else
			orderBy = " order by web_event_registration.date_posted desc";
			
		query += orderBy;
		
		dh.setSQLQuery(query);

    	if (UtilMethods.isSet(startDate)) {
    		dh.setDate(startDate);
    	}
    	if (UtilMethods.isSet(endDate)) {
    		dh.setDate(endDate);
    	}
		return dh.list();

		
	}
	public static List getRegistrationsPerEventLocation(String locationInode) {
		
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		String query = "from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and event_location_inode = '" + locationInode + "'" ;
		query += " order by date_posted desc";
		dh.setQuery(query);
		return dh.list();
	}
	public static List getAttendeesPerEventLocation(String locationInode) {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		String query = "from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and event_location_inode = '" + locationInode + "'";
		query += " order by date_posted desc";
		dh.setQuery(query);
		return getEventAttendeesByRegistratiosList(dh.list());
	}
	
	
	@SuppressWarnings("unchecked")
	public static java.util.List<WebEventAttendee> getEventAttendeesByRegistratiosList(List eventRegistrationList) {
		String condition = "(event_registration_inode = -1 or event_registration_inode is null)";
		if (eventRegistrationList != null){
			if (eventRegistrationList.size() > 0){
				condition = "event_registration_inode in (";
				for (int i=0;i<eventRegistrationList.size();i++){
					WebEventRegistration eventRegistration = (WebEventRegistration) eventRegistrationList.get(i);
					if (eventRegistration.getRegistrationStatus() != 6){
						if (i > 0)
							condition += ",'" + eventRegistration.getInode()+"'";
						else
							condition +="'"+eventRegistration.getInode()+"'";
					}
				}
				condition += ") ";
			}
		}
		String orderby = "first_name, last_name";
		return (java.util.List<WebEventAttendee>)InodeFactory.getInodesOfClassByConditionAndOrderBy(WebEventAttendee.class, condition, orderby);
	}
	public static java.util.List getWebEventRegistrationsByUser(String inode) {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and user_inode = ?"
				);
		dh.setParam(inode);
			
		return dh.list();
	}
	public static java.util.List getWebEventRegistrationsByUser(String inode, String orderBy) {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventRegistration where type='event_registration' and user_inode = ? order by " + orderBy);
		dh.setParam(inode);
			
		return dh.list();
	}
	public static java.util.List getRegistrationsForEmailReminders() {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		
		StringBuffer sb = new StringBuffer();
		sb.append("select {web_event_registration.*} from web_event_registration as web_event_registration, inode as web_event_registration_1_, web_event as web_event, web_event_location as web_event_location ");
		sb.append("where web_event_registration_1_.type='event_registration' and web_event_registration.inode = web_event_registration_1_.inode ");
		sb.append("and web_event_registration.event_inode = web_event.inode ");
		sb.append("and web_event_registration.event_location_inode = web_event_location.inode ");
		sb.append("and web_event.inode = web_event_location.web_event_inode ");
		sb.append("and web_event.is_institute = " + DbConnectionFactory.getDBFalse());
		sb.append("and web_event_location.start_date > ? ");
		sb.append("and web_event_location.start_date < ? ");
		//only send emails to paid registrations
		sb.append("and web_event_registration.registration_status = " + DbConnectionFactory.getDBTrue() + " ");
		sb.append("and web_event_registration.reminder_email_sent = " + DbConnectionFactory.getDBFalse());
		
		dh.setSQLQuery(sb.toString());
		
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		Date date1 = cal.getTime();
		Logger.info(WebEventRegistrationFactory.class, "Email Reminder start date1 = " + date1);
		cal.setTime(new Date());
		cal.add(GregorianCalendar.DAY_OF_YEAR, 3);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		Date date2 = cal.getTime();
		Logger.info(WebEventRegistrationFactory.class,"Email Reminder start date2 = " + date2);
		dh.setParam(date1);
		dh.setParam(date2);
		return dh.list();
	}

	public static java.util.List getRegistrationsAfterWebinar() {
		DotHibernate dh = new DotHibernate(WebEventRegistration.class);
		
		StringBuffer sb = new StringBuffer();
		sb.append("select {web_event_registration.*} from web_event_registration as web_event_registration, inode as web_event_registration_1_, web_event as web_event, web_event_location as web_event_location ");
		sb.append("where web_event_registration_1_.type='event_registration' and web_event_registration.inode = web_event_registration_1_.inode ");
		sb.append("and web_event_registration.event_inode = web_event.inode ");
		sb.append("and web_event_registration.event_location_inode = web_event_location.inode ");
		sb.append("and web_event.inode = web_event_location.web_event_inode ");
		sb.append("and web_event.is_institute = " + DbConnectionFactory.getDBFalse() + " ");
		sb.append("and web_event_location.end_date > ? ");
		sb.append("and web_event_location.end_date < ? ");
		//only send emails to paid registrations
		sb.append("and web_event_registration.registration_status = " + DbConnectionFactory.getDBTrue() + " ");
		sb.append("and web_event_registration.post_email_sent = " + DbConnectionFactory.getDBFalse());
		
		dh.setSQLQuery(sb.toString());
		
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.add(GregorianCalendar.DAY_OF_YEAR, -2);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		Date date1 = cal.getTime();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		Date date2 = cal.getTime();
		dh.setParam(date1);
		dh.setParam(date2);
		return dh.list();
	}
	
	
	
}
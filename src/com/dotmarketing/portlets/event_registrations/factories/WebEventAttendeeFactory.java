package com.dotmarketing.portlets.event_registrations.factories;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;

/**
 *
 * @author  Maru
 */
public class WebEventAttendeeFactory {
	
	public static java.util.List getAllWebEventAttendees() {
		DotHibernate dh = new DotHibernate(WebEventAttendee.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventAttendee order by first_name, last_name");
		return dh.list();
	}
	public static java.util.List getAllWebEventAttendees(String orderby) {
		DotHibernate dh = new DotHibernate(WebEventAttendee.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventAttendee order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List<WebEventAttendee> getWebEventAttendeesByKeyword(String keyword) {
		DotHibernate dh = new DotHibernate(WebEventAttendee.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventAttendee where lower(first_name) = ? or lower(last_name) = ? or lower(email) = ?");
		dh.setParam(keyword.toLowerCase());
		dh.setParam(keyword.toLowerCase());
		dh.setParam(keyword.toLowerCase());
		return dh.list();
	}
	
	public static java.util.List<WebEventAttendee> getWebEventAttendeesByEventRegistration(List<WebEventRegistration> eventsRegistration) 
	{
		List returnValue = new ArrayList();
		DotHibernate dh = new DotHibernate(WebEventAttendee.class);		
		String eventsRegistrationInode = "";
		if(eventsRegistration!=null && eventsRegistration.size() > 0)
		{
			for(WebEventRegistration registration : eventsRegistration)
			{
				eventsRegistrationInode += "'" + registration.getInode() + "',";
			}
			eventsRegistrationInode = eventsRegistrationInode.substring(0,eventsRegistrationInode.lastIndexOf(","));
			String query = "from inode in class com.dotmarketing.portlets.event_registrations.model.WebEventAttendee where event_registration_inode in (" + eventsRegistrationInode + ")";
			dh.setQuery(query);
			returnValue = dh.list(); 
			return returnValue;
		}
		else {
			return new ArrayList<WebEventAttendee>();
		}
	}

	public static WebEventRegistration getWebEventRegistration(WebEventAttendee webEventAttendee) {
		return (WebEventRegistration) InodeFactory.getInode(webEventAttendee.getEventRegistrationInode(), WebEventRegistration.class);
	}
	
	/*public static WebEventAttendee getWebEventAttendee(long inode) {
		return (WebEventAttendee) InodeFactory.getInode(inode, WebEventAttendee.class);
	}*/

	public static WebEventAttendee getWebEventAttendee(String inode) {
		return (WebEventAttendee) InodeFactory.getInode(inode, WebEventAttendee.class);
	}

	public static WebEventAttendee newInstance() {
		WebEventAttendee m = new WebEventAttendee();
		return m;
	}

	public static void saveWebEventAttendee(WebEventAttendee WebEventAttendee) {
		InodeFactory.saveInode(WebEventAttendee);
	}

	public static void deleteWebEventAttendee(WebEventAttendee WebEventAttendee) {
		InodeFactory.deleteInode(WebEventAttendee);
	}

}
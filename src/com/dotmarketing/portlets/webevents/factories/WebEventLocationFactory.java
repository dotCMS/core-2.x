package com.dotmarketing.portlets.webevents.factories;

import java.util.Date;
import java.util.GregorianCalendar;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
/**
 *
 * @author  Maru
 */
public class WebEventLocationFactory {

	public static java.util.List getAllWebEventLocations() {
		DotHibernate dh = new DotHibernate(WebEventLocation.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEventLocation where type='web_event_location' order by start_date desc");
		return dh.list();
	}
	public static java.util.List getAllWebEventLocations(String orderby) {
		DotHibernate dh = new DotHibernate(WebEventLocation.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEventLocation where type='web_event_location' order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List getWebEventLocationsPerEvent(String webEventInode, String orderby) {
		DotHibernate dh = new DotHibernate(WebEventLocation.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEventLocation where  type='web_event_location' and web_event_inode = ? order by " + orderby);
		dh.setParam(webEventInode);
		return dh.list();
	}

	public static java.util.List getUpcomingWebEventLocationsPerEvent(String webEventInode, String orderby) {
		DotHibernate dh = new DotHibernate(WebEventLocation.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEventLocation where type='web_event_location' and  web_event_inode = ? and start_date >= ? order by " + orderby);
		dh.setParam(webEventInode);
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setDate(cal.getTime());
		return dh.list();
	}

	public static java.util.List getPastWebEventLocationsPerEvent(String webEventInode, String orderby) {
		DotHibernate dh = new DotHibernate(WebEventLocation.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEventLocation where type='web_event_location' and web_event_inode = ? and start_date < ? order by " + orderby);
		dh.setParam(webEventInode);
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
		cal.set(GregorianCalendar.MINUTE, 59);
		cal.set(GregorianCalendar.SECOND, 59);
		dh.setParam(cal.getTime());
		return dh.list();
	}
	
	public static WebEvent getWebEvent(WebEventLocation webeventLocation) {
		return WebEventFactory.getWebEvent(webeventLocation.getWebEventInode());
	}

	public static WebEventLocation getWebEventLocation(String inode) {
		return (WebEventLocation) InodeFactory.getInode(inode, WebEventLocation.class);
	}

	public static WebEventLocation newInstance() {
		WebEventLocation m = new WebEventLocation();
		return m;
	}

	public static void saveWebEventLocation(WebEventLocation WebEventLocation) {
		InodeFactory.saveInode(WebEventLocation);
	}

	public static void deleteWebEventLocation(WebEventLocation WebEventLocation) {
		InodeFactory.deleteInode(WebEventLocation);
	}

}

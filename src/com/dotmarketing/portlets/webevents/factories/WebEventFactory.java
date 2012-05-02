package com.dotmarketing.portlets.webevents.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
/**
 *
 * @author  Maru
 */
public class WebEventFactory {

	public static java.util.List getAllWebEvents() {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEvent where type='web_event' order by sort_order");
		return dh.list();
	}
	public static java.util.List getUpcomingPublicWebEvents() {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event, inode as web_event_1_, web_event_location as web_event_location where web_event_1_.type='web_event' and web_event_1_.inode = web_event.inode and web_event.inode = web_event_location.web_event_inode and web_event_location.start_date >= ?  and web_event.show_on_web = " + DbConnectionFactory.getDBTrue() + " order by web_event.sort_order");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setDate(cal.getTime());
		return new ArrayList(new HashSet(dh.list()));
	}
	public static java.util.List getUpcomingPublicWebEvents(boolean institute) {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		String condition = "";
		if (institute) {
			condition = " and web_event.is_institute = " + DbConnectionFactory.getDBTrue();
		}
		else {
			condition = " and web_event.is_institute = " + DbConnectionFactory.getDBFalse();
		}
		String query = "select {web_event.*} from web_event as web_event, inode as web_event_1_, web_event_location as web_event_location where web_event_1_.type='web_event' and web_event_1_.inode = web_event.inode and web_event.inode = web_event_location.web_event_inode and web_event_location.start_date >= ?  and web_event.show_on_web = " + DbConnectionFactory.getDBTrue() + condition + " order by web_event.sort_order";
		dh.setSQLQuery(query);
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setDate(cal.getTime());
		return new ArrayList(new HashSet(dh.list()));
	}
	
	
	public static java.util.List getUpcomingWebEvents() {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event, inode as web_event_1_, web_event_location as web_event_location where web_event_1_.type='web_event' and web_event_1_.inode = web_event.inode and web_event.inode = web_event_location.web_event_inode and web_event_location.start_date >= ?  order by web_event.sort_order");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setDate(cal.getTime());
		return new ArrayList(new HashSet(dh.list()));
	}
	public static java.util.List getEventsWithNoLocations() {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event left join web_event_location as web_event_location on web_event.inode = web_event_location.web_event_inode, inode as web_event_1_ where web_event_1_.inode = web_event.inode and web_event_location.web_event_inode is null");
		return dh.list();
	}
	public static java.util.List getEventsWithNoLocationsByKeywords(String keywords) {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event left join web_event_location as web_event_location on web_event.inode = web_event_location.web_event_inode, inode as web_event_1_ where web_event_1_.inode = web_event.inode and web_event_location.web_event_inode is null and web_event.title like '%" + keywords + "%' ");
		return dh.list();
	}
	public static java.util.List getPastWebEvents() {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event, inode as web_event_1_, web_event_location as web_event_location where web_event_1_.inode = web_event.inode and web_event.inode = web_event_location.web_event_inode and web_event_location.start_date < ? order by web_event.sort_order");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
		cal.set(GregorianCalendar.MINUTE, 59);
		cal.set(GregorianCalendar.SECOND, 59);
		dh.setParam(cal.getTime());
		return new ArrayList(new HashSet(dh.list()));
	}
	public static java.util.List getAllWebEventsByKeywords(String keywords) {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEvent where type='web_event' and title like '%" + keywords + "%' order by sort_order");
		return dh.list();
	}
	public static java.util.List getUpcomingWebEventsByKeywords(String keywords) {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setSQLQuery("select {web_event.*} from web_event as web_event,  inode as web_event_1_, web_event_location as web_event_location where web_event_1_.type='web_event' and web_event_1_.inode = web_event.inode and web_event.inode = web_event_location.web_event_inode and web_event.title like '%" + keywords + "%' and web_event_location.start_date >= ? order by web_event.sort_order");
		dh.setParam(new Date());
		return dh.list();
	}
	public static java.util.List getAllWebEvents(String orderby) {
		DotHibernate dh = new DotHibernate(WebEvent.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.webevents.model.WebEvent where type='web_event' order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List getEventLocations(WebEvent event, String orderby) {
		return WebEventLocationFactory.getWebEventLocationsPerEvent(event.getInode(),orderby);
	}
	public static java.util.List getUpcomingEventLocations(WebEvent event, String orderby) {
		return WebEventLocationFactory.getUpcomingWebEventLocationsPerEvent(event.getInode(),orderby);
	}

	public static WebEvent getWebEvent(String inode) {
		return (WebEvent) InodeFactory.getInode(inode, WebEvent.class);
	}

	public static WebEvent newInstance() {
		WebEvent m = new WebEvent();
		return m;
	}

	public static void saveWebEvent(WebEvent webEvent) {
		InodeFactory.saveInode(webEvent);
	}

	public static void deleteWebEvent(WebEvent webEvent) {
		InodeFactory.deleteInode(webEvent);
	}

}

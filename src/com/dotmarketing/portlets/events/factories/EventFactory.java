package com.dotmarketing.portlets.events.factories;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.factories.EmailFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.Recurance;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilHTML;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

/**
 * 
 * @author will
 */

public class EventFactory {

	private static CategoryAPI categoryAPI = APILocator.getCategoryAPI();

	public static CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public static void setCategoryAPI(CategoryAPI categoryAPI) {
		EventFactory.categoryAPI = categoryAPI;
	}

	public static Event getEvent(String inode) {
		DotHibernate dh = new DotHibernate(Event.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.events.model.Event where type='event' and inode = ?");
		dh.setParam(inode);
		return (Event) dh.load();
	}

	public static Event newInstance() {
		Event e = new Event();

		return e;
	}

	public static void deleteEvent(Event e) {
		InodeFactory.deleteInode(e);
	}

	public static void deleteEventSeries(Event e) {
		if (InodeFactory.countChildrenOfClass(e, Recurance.class) > 0) {
			Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
			RecuranceFactory.deleteRecurringEvents(r);
		}
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRange(java.util.Date from, java.util.Date to) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		dh
		.setSQLQuery("select {event.*} from event, inode event_1_ where event_1_.type='event' and event.inode = event_1_.inode and start_date > ? and start_date < ?  order by start_date");

		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRange(java.util.Date from, java.util.Date to, User user) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		String query = "select {event.*} from event, inode event_1_ where event_1_.type='event' and event.inode = event_1_.inode and start_date > ? and start_date < ? and "
			+ "(event.approval_status = "
			+ Constants.EVENT_APPROVED_STATUS
			+ " or event.user_id = '"
			+ user.getUserId()
			+ "') " + "order by start_date";

		dh.setSQLQuery(query);

		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		dh
		.setSQLQuery("select {event.*} from event, inode event_1_, tree where event_1_.type='event' and event.inode = event_1_.inode and tree.parent = ? and tree.child = event.inode and start_date > ? and start_date < ?  order by start_date");

		dh.setParam(cat.getInode());
		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		dh.setSQLQuery("select {event.*} from event, inode event_1_, tree where event_1_.type='event' and "
				+ "event.inode = event_1_.inode and tree.parent = ? and tree.child = event.inode and "
				+ "start_date > ? and start_date < ?  and show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and " + "event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " " + "order by start_date");

		dh.setParam(cat.getInode());
		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat,
			int maxEvents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		dh.setSQLQuery("select {event.*} from event, inode event_1_, tree where event_1_.type='event' and "
				+ "event.inode = event_1_.inode and tree.parent = ? and tree.child = event.inode and "
				+ "start_date > ? and start_date < ?  and show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and " + "event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " " + "order by start_date");

		dh.setParam(cat.getInode());
		dh.setParam(from);
		dh.setParam(to);
		dh.setMaxResults(maxEvents);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat1,
			Category cat2, Category cat3, String facilityInode) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);

		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");

		if (InodeUtils.isSet(cat1.getInode()))
			query.append(", tree tree1 ");
		if (InodeUtils.isSet(cat2.getInode()))
			query.append(", tree tree2 ");
		if (InodeUtils.isSet(cat3.getInode()))
			query.append(", tree tree3 ");
		if (InodeUtils.isSet(facilityInode))
			query.append(", tree tree4 ");

		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (InodeUtils.isSet(cat1.getInode())) {
			query.append(" tree1.parent = ? and tree1.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			query.append(" tree2.parent = ? and tree2.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			query.append(" tree3.parent = ? and tree3.child = event.inode and ");
		}
		if (InodeUtils.isSet(facilityInode))
			query.append(" tree4.parent = ? and tree4.child = event.inode and ");
		if (!InodeUtils.isSet(facilityInode)) 
			query
			.append(" event.inode not in (select tree4.child from tree tree4, facility where facility.inode = tree4.parent) and ");

		query.append("start_date > ? and start_date < ? order by start_date");

		dh.setSQLQuery(query.toString());

		if (InodeUtils.isSet(cat1.getInode())) {
			dh.setParam(cat1.getInode());
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			dh.setParam(cat2.getInode());
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			dh.setParam(cat3.getInode());
		}
		if (InodeUtils.isSet(facilityInode))
			dh.setParam(facilityInode);

		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat1,
			Category cat2, Category cat3, String facilityInode, User user) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);

		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");

		if (InodeUtils.isSet(cat1.getInode()))
			query.append(", tree tree1 ");
		if (InodeUtils.isSet(cat2.getInode()))
			query.append(", tree tree2 ");
		if (InodeUtils.isSet(cat3.getInode()))
			query.append(", tree tree3 ");
		if (InodeUtils.isSet(facilityInode))
			query.append(", tree tree4 ");

		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (InodeUtils.isSet(cat1.getInode())) {
			query.append(" tree1.parent = ? and tree1.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			query.append(" tree2.parent = ? and tree2.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			query.append(" tree3.parent = ? and tree3.child = event.inode and ");
		}
		if (InodeUtils.isSet(facilityInode))
			query.append(" tree4.parent = ? and tree4.child = event.inode and ");
		if (!InodeUtils.isSet(facilityInode))
			query
			.append(" event.inode not in (select tree4.child from tree tree4, facility where facility.inode = tree4.parent) and ");

		query.append("start_date > ? and start_date < ? and ");
		query.append("(event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " or event.user_id = '"
				+ user.getUserId() + "') ");
		query.append("order by start_date");

		dh.setSQLQuery(query.toString());

		if (InodeUtils.isSet(cat1.getInode())) {
			dh.setParam(cat1.getInode());
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			dh.setParam(cat2.getInode());
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			dh.setParam(cat3.getInode());
		}
		if (InodeUtils.isSet(facilityInode))
			dh.setParam(facilityInode);

		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParent(java.util.Date from, java.util.Date to, List<Inode> parents,
			User user) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);

		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");
		int i = 1;
		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode()))
				query.append(", tree tree" + i + " ");
			i++;
		}
		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		i = 1;
		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode()))
				query.append("tree" + i + ".parent = ? and tree" + i + ".child = event.inode and ");
			i++;
		}
		query.append("start_date > ? and start_date < ? ");
		//	query.append(" and  (event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " or event.user_id = '"
		//		+ user.getUserId() + "') ");
		query.append(" order by start_date");

		dh.setSQLQuery(query.toString());

		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode())){
				dh.setParam(inode.getInode());
			}
		}
		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}


	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParent(java.util.Date from, java.util.Date to, List<Inode> parents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);

		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");
		int i = 1;
		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode()))
				query.append(", tree tree" + i + " ");
			i++;
		}
		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		i = 1;
		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode()))
				query.append("tree" + i + ".parent = ? and tree" + i + ".child = event.inode and ");
			i++;
		}
		query.append("event.start_date > ? and event.start_date < ?  ");
		//	query.append("and (event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " ) ");
		query.append("order by event.start_date");

		dh.setSQLQuery(query.toString());

		for (Inode inode : parents) {
			if (InodeUtils.isSet(inode.getInode())){
				dh.setParam(inode.getInode());
			}
		}
		dh.setParam(from);
		dh.setParam(to);

		return dh.list();

	}


	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat1,
			Category cat2, Category cat3) {
		return getPublicEventsByDateRangeYParent(from, to, cat1, cat2, cat3, 0);
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to, Category cat1,
			Category cat2, Category cat3, int maxEvents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");

		if (InodeUtils.isSet(cat1.getInode()))
			query.append(", tree tree1 ");
		if (InodeUtils.isSet(cat2.getInode()))
			query.append(", tree tree2 ");
		if (InodeUtils.isSet(cat3.getInode()))
			query.append(", tree tree3 ");

		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (InodeUtils.isSet(cat1.getInode())) {
			query.append(" tree1.parent = ? and tree1.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			query.append(" tree2.parent = ? and tree2.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			query.append(" tree3.parent = ? and tree3.child = event.inode and ");
		}

		query.append("show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and start_date > ? and start_date < ? and event.approval_status = " + Constants.EVENT_APPROVED_STATUS
				+ " ");
		query.append("order by start_date");

		dh.setSQLQuery(query.toString());

		if (InodeUtils.isSet(cat1.getInode())) {
			dh.setParam(cat1.getInode());
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			dh.setParam(cat2.getInode());
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			dh.setParam(cat3.getInode());
		}

		dh.setParam(from);
		dh.setParam(to);
		if (maxEvents > 0)
			dh.setMaxResults(maxEvents);

		return dh.list();

	}

	public static java.util.List<Event> getPublicEvents(java.util.Date from, java.util.Date to, String keyword, String[] cats, int maxEvents) {
		if (cats == null)
			cats = new String[0];
		List<Integer> intCatsList = new ArrayList<Integer>();
		for (int i = 0; i < cats.length; i++) {
			try {
				intCatsList.add(Integer.parseInt(cats[i]));
			} catch (NumberFormatException e) { }
		}
		int[] intCats = new int[intCatsList.size()];
		for (int i = 0; i < intCats.length; i++) {
			intCats[i] = intCatsList.get(i);
		}        
		return getPublicEvents (from, to, keyword, intCats, maxEvents);
	}

	public static java.util.List<Event> getPublicEventsWithOrCategories(java.util.Date from, java.util.Date to, String keyword, String[] cats, int maxEvents) {
		if (cats == null)
			cats = new String[0];
		List<Integer> intCatsList = new ArrayList<Integer>();
		for (int i = 0; i < cats.length; i++) {
			try {
				intCatsList.add(Integer.parseInt(cats[i]));
			} catch (NumberFormatException e) { }
		}
		int[] intCats = new int[intCatsList.size()];
		for (int i = 0; i < intCats.length; i++) {
			intCats[i] = intCatsList.get(i);
		}        
		return getPublicEventsWithOrCategories(from, to, keyword, intCats, maxEvents);
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEvents(java.util.Date from, java.util.Date to, String keyword, int[] cats, int maxEvents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");

		for (int i = 0; i < cats.length; i++) {
			query.append(", tree tree" + i + " ");
		}

		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (UtilMethods.isSet(keyword)) {
			query.append("(event.title like '%" + keyword + "%' or ");
			query.append("event.subtitle like '%" + keyword + "%' or ");
			query.append("event.description like '%" + keyword + "%') and ");
		}

		for (int i = 0; i < cats.length; i++) {
			query.append(" tree" + i + ".parent = " + cats[i] + " and tree" + i + ".child = event.inode and ");
		}

		query.append("show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and start_date > ? and start_date < ? and event.approval_status = " + Constants.EVENT_APPROVED_STATUS
				+ " ");
		query.append("order by start_date");

		dh.setSQLQuery(query.toString());
		dh.setParam(from);
		dh.setParam(to);
		if (maxEvents > 0)
			dh.setMaxResults(maxEvents);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsWithOrCategories(java.util.Date from, java.util.Date to, String keyword, int[] cats, int maxEvents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		StringBuffer query = new StringBuffer("select {event.*} from event, inode event_1_ ");

		query.append("where event_1_.type='event' and event.inode = event_1_.inode and ");
		query.append("show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() +
				" and event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " and start_date > ? and start_date < ? ");

		if (UtilMethods.isSet(keyword)) {
			query.append(" and (event.title like '%" + keyword + "%' or ");
			query.append("event.subtitle like '%" + keyword + "%' or ");
			query.append("event.description like '%" + keyword + "%') ");
		}

		if (0 < cats.length) {
			int i = 0;
			query.append(" and event.inode in ( select distinct tree.child from tree where tree.parent in (");

			StringBuilder categories = new StringBuilder(100);
			categories.ensureCapacity(25);
			for (; i < cats.length; i++) {
				if (categories.length() == 0)
					categories.append(cats[i]);
				else
					categories.append(", " + cats[i]);
			}

			query.append(categories.toString() + " )) ");
		}

		query.append("order by start_date");

		dh.setSQLQuery(query.toString());
		dh.setParam(from);
		dh.setParam(to);
		if (maxEvents > 0)
			dh.setMaxResults(maxEvents);

		return dh.list();

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParentNotInCat(java.util.Date from, java.util.Date to,
			Category cat1, Category cat2, Category cat3, Category cat4) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		StringBuffer query = new StringBuffer("select distinct {event.*} from event, inode event_1_ ");

		if (InodeUtils.isSet(cat1.getInode()))
			query.append(", tree tree1 ");
		if (InodeUtils.isSet(cat2.getInode()))
			query.append(", tree tree2 ");
		if (InodeUtils.isSet(cat3.getInode()))
			query.append(", tree tree3 ");

		query.append(" where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (InodeUtils.isSet(cat1.getInode())) {
			query.append(" tree1.parent = ? and tree1.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			query.append(" tree2.parent = ? and tree2.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			query.append(" tree3.parent = ? and tree3.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat4.getInode())) {
			query.append(" event.inode not in (select tree4.child from tree tree4 where tree4.parent = ?) and ");
		}

		query.append("show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and start_date > ? and start_date < ? and event.approval_status = " + Constants.EVENT_APPROVED_STATUS
				+ " ");
		query.append("order by start_date");

		dh.setSQLQuery(query.toString());

		if (InodeUtils.isSet(cat1.getInode())) {
			dh.setParam(cat1.getInode());
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			dh.setParam(cat2.getInode());
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			dh.setParam(cat3.getInode());
		}

		if (InodeUtils.isSet(cat4.getInode())) {
			dh.setParam(cat4.getInode());
		}

		dh.setParam(from);
		dh.setParam(to);

		java.util.List<Event> events = dh.list();
		return events;

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParentNotInCat(java.util.Date from, java.util.Date to,
			Category cat1, Category cat2, Category cat3, Category cat4, int maxEvents) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(from);
		cal.add(GregorianCalendar.SECOND, -1);
		from = cal.getTime();

		cal.setTime(to);
		cal.add(GregorianCalendar.SECOND, 1);
		to = cal.getTime();

		DotHibernate dh = new DotHibernate(Event.class);
		StringBuffer query = new StringBuffer("select distinct {event.*} from event, inode event_1_ ");

		if (InodeUtils.isSet(cat1.getInode()))
			query.append(", tree tree1 ");
		if (InodeUtils.isSet(cat2.getInode()))
			query.append(", tree tree2 ");
		if (InodeUtils.isSet(cat3.getInode()))
			query.append(", tree tree3 ");

		query.append(" where event_1_.type='event' and event.inode = event_1_.inode and ");

		if (InodeUtils.isSet(cat1.getInode())) {
			query.append(" tree1.parent = ? and tree1.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			query.append(" tree2.parent = ? and tree2.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			query.append(" tree3.parent = ? and tree3.child = event.inode and ");
		}
		if (InodeUtils.isSet(cat4.getInode())) {
			query.append(" event.inode not in (select tree4.child from tree tree4 where tree4.parent = ?) and ");
		}

		query.append("show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and start_date > ? and start_date < ? and event.approval_status = " + Constants.EVENT_APPROVED_STATUS
				+ " ");
		query.append("order by start_date");

		dh.setSQLQuery(query.toString());

		if (InodeUtils.isSet(cat1.getInode())) {
			dh.setParam(cat1.getInode());
		}
		if (InodeUtils.isSet(cat2.getInode())) {
			dh.setParam(cat2.getInode());
		}
		if (InodeUtils.isSet(cat3.getInode())) {
			dh.setParam(cat3.getInode());
		}

		if (InodeUtils.isSet(cat4.getInode())) {
			dh.setParam(cat4.getInode());
		}

		dh.setParam(from);
		dh.setParam(to);
		dh.setMaxResults(maxEvents);

		java.util.List<Event> events = dh.list();
		return events;

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to,
			String calendarCategories, String eventCategories, String keyword, int maxEvents) {
		try {
			List<Event> returnEvents;
			GregorianCalendar cal = new GregorianCalendar();

			DotHibernate dh = new DotHibernate(Event.class);
			String select = "select distinct {event.*} ";
			String fromTables = "from event, inode event_1_ ";
			String where = "where event_1_.type='event' and show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
			+ " and event.approval_status = " + Constants.EVENT_APPROVED_STATUS
			+ " and event.inode = event_1_.inode ";

			if (UtilMethods.isSet(from)) {
				cal.setTime(from);
				cal.add(GregorianCalendar.SECOND, -1);
				from = cal.getTime();
				where += "and start_date > ? ";
			}

			if (UtilMethods.isSet(to)) {
				cal.setTime(to);
				cal.add(GregorianCalendar.SECOND, 1);
				to = cal.getTime();
				where += "and start_date < ? ";
			}

			if (UtilMethods.isSet(keyword)) {
				where += "and event.title like '%" + keyword + "%' ";
			}

			if (UtilMethods.isSet(calendarCategories)) {
				fromTables += ", tree calendarTree ";
				where += "and calendarTree.parent in (" + calendarCategories + ") and calendarTree.child = event.inode ";
			}

			if (UtilMethods.isSet(eventCategories)) {
				fromTables += ", tree eventTree ";
				where += "and eventTree.parent in (" + eventCategories + ") and eventTree.child = event.inode ";
			}

			String order = "order by start_date";
			String query = select + fromTables + where + order;

			dh.setSQLQuery(query);

			if (UtilMethods.isSet(from)) {
				dh.setParam(from);
			}

			if (UtilMethods.isSet(to)) {
				dh.setParam(to);
			}

			if (maxEvents > 0) {
				dh.setMaxResults(maxEvents);
			}

			returnEvents = dh.list();
			return returnEvents;
		} catch (Exception ex) {
			Logger.warn(EventFactory.class, ex.toString());
			return new ArrayList<Event>();
		}
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicEventsByDateRangeYParent(java.util.Date from, java.util.Date to,
			String[] calendarCategories, String[] eventCategories, String keyword, int maxEvents, User user, boolean respectFrontendRoles) {
		try {
			List<Event> returnEvents;
			GregorianCalendar cal = new GregorianCalendar();

			DotHibernate dh = new DotHibernate(Event.class);
			StringBuffer select = new StringBuffer();
			StringBuffer fromTables = new StringBuffer();
			StringBuffer where = new StringBuffer();

			select.append("select distinct {event.*} ");

			fromTables.append("from event, inode event_1_ ");

			where.append("where event_1_.type='event' and show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
			where.append(" and event.approval_status = " + Constants.EVENT_APPROVED_STATUS);		
			where.append(" and event.inode = event_1_.inode ");

			if (UtilMethods.isSet(from)) 
			{
				cal.setTime(from);
				cal.add(GregorianCalendar.SECOND, -1);
				from = cal.getTime();
//				where.append("and start_date >= ? ");
				where.append("and start_date > ? ");
			}

			if (UtilMethods.isSet(to)) 
			{
				cal.setTime(to);
				cal.add(GregorianCalendar.SECOND, 1);
				to = cal.getTime();
//				where.append("and start_date <= ? ");
				where.append("and start_date < ? ");
			}

			if (UtilMethods.isSet(keyword)) 
			{
				where.append("and event.title like '%" + keyword + "%' ");
			}

			int eventSize = (eventCategories != null ? eventCategories.length : 0);
			int calendarSize = (calendarCategories != null ? calendarCategories.length : 0);

			String[] genericCategories = new String[eventSize + calendarSize];

			int j = 0;
			for(int i = 0;i < eventSize;i++)
			{
				genericCategories[j] = eventCategories[i];
				j++;
			}

			for(int i = 0;i < calendarSize;i++)
			{
				genericCategories[j] = calendarCategories[i];
				j++;
			}

			if (genericCategories != null && genericCategories.length > 0)
			{				
				StringBuffer inodeEventCategories = new StringBuffer();
				for(int i = 0;i < genericCategories.length;i++)
				{
					Category category = categoryAPI.findByName(genericCategories[i], user, respectFrontendRoles);
					String categoryInode = (InodeUtils.isSet(category.getInode()) ? category.getInode() : "");
					inodeEventCategories.append(categoryInode + ",");
				}
				inodeEventCategories = new StringBuffer(inodeEventCategories.toString().substring(0,inodeEventCategories.lastIndexOf(",")));

				fromTables.append(", tree eventTree ");				
				where.append(" and eventTree.parent in (" + inodeEventCategories + ") and eventTree.child = event.inode ");
			}


			String order = "order by start_date";
			String query = select.toString() + fromTables.toString() + where.toString() + order;

			dh.setSQLQuery(query);

			if (UtilMethods.isSet(from)) 
			{
				dh.setParam(from);
			}

			if (UtilMethods.isSet(to)) 
			{
				dh.setParam(to);
			}

			if (maxEvents > 0) 
			{
				dh.setMaxResults(maxEvents);
			}

			returnEvents = dh.list();
			return returnEvents;
		} 
		catch (Exception ex) 
		{
			Logger.warn(EventFactory.class, ex.toString());
			return new ArrayList<Event>();
		}
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getUpcomingEventsByParent(Inode cat) {

		return InodeFactory.getChildrenClassByConditionAndOrderBy(cat, Event.class, "start_date >= now()", "start_date");

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getPublicUpcomingEventsByParent(Inode cat) {
		return InodeFactory.getChildrenClassByConditionAndOrderBy(cat, Event.class, "end_date >= now() and show_public = "
				+ com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and event.approval_status = "
				+ Constants.EVENT_APPROVED_STATUS + " ", "start_date");
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByFilter(String filter) {

		DotHibernate dh = new DotHibernate(Event.class);
		dh
		.setQuery("from event in class com.dotmarketing.portlets.events.model.Event where title like ? or description like  ? order by start_date");
		dh.setParam("%" + filter + "%");
		dh.setParam("%" + filter + "%");
		return dh.list();

	}

	public static List<Event> getPublicEventsByPageHierarchy(List<Category> parents) {

		List<Event> al = new java.util.ArrayList<Event>();
		Iterator<Category> i = parents.iterator();

		while (i.hasNext()) {

			Category c = (com.dotmarketing.portlets.categories.model.Category) i.next();
			al = getPublicUpcomingEventsByParent(c);
			if (al.size() > 0)
				break;
		}
		return al;
	}

	public static List<Event> getEventsByPageHierarchy(List<Category> parents) {

		List<Event> al = new ArrayList<Event>();
		Iterator<Category> i = parents.iterator();

		while (i.hasNext()) {

			Inode c = (Inode) i.next();

			al = getUpcomingEventsByParent(c);
			if (al.size() > 0)
				break;

		}

		return al;
	}

	/**
	 * Get the list of approved events that are in conflict with a simple event.
	 * 
	 * @param e
	 * @return The list of events in conflict, a empty list is returned if no
	 *         conflict has.
	 */
	@SuppressWarnings("unchecked")
	public static List<Event> findConflicts(Event e, Facility f) {
		if (e.isTimeTBD())
			return new ArrayList<Event>();
		if (f == null || !InodeUtils.isSet(f.getInode()))
			return new ArrayList<Event>();
		DotHibernate dh = new DotHibernate(Event.class);

		String query = "select {event.*} from event, inode event_1_, tree where event_1_.type='event' and event.inode = event_1_.inode and setup_date <= ? and break_date >= ? and "
			+ "tree.child = event.inode and tree.parent = ? and event.inode <> ? and event.approval_status = "
			+ Constants.EVENT_APPROVED_STATUS + " " + "order by start_date";

		dh.setSQLQuery(query);
		dh.setParam(e.getBreakDate());
		dh.setParam(e.getSetupDate());
		dh.setParam(f.getInode());
		dh.setParam(e.getInode());
		return dh.list();
	}

	/**
	 * Get the list of approved events that are in conflict with a recurrent
	 * event.
	 * 
	 * @param r
	 *            Recurance object of the event
	 * @return The list of events in conflict, a empty list is returned if no
	 *         conflict has.
	 */
	@SuppressWarnings("unchecked")
	public static List<Event> findConflicts(Event e, Recurance r, Facility f) {
		if (e.isTimeTBD())
			return new ArrayList<Event>();

		if (r == null || f == null || !InodeUtils.isSet(f.getInode())) {
			return new ArrayList<Event>();
		}

		// Initializing variables
		int interval = r.getInterval();

		GregorianCalendar startDate = new GregorianCalendar();
		GregorianCalendar endDate = new GregorianCalendar();
		GregorianCalendar endTime = new GregorianCalendar();

		GregorianCalendar setupDate = new GregorianCalendar();
		GregorianCalendar breakDate = new GregorianCalendar();

		startDate.setTime(r.getStarting());
		endDate.setTime(r.getEnding());
		endTime.setTime(r.getEnding());

		setupDate.setTime(e.getSetupDate());
		breakDate.setTime(e.getBreakDate());

		endDate.set(Calendar.HOUR_OF_DAY, 23);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);

		// Getting the dates list of events to be created
		List<Date[]> datesList = new ArrayList<Date[]>();

		while (startDate.getTime().before(endDate.getTime())) {

			if ("day".equals(r.getOccurs())) {

				// build the start time/date
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(startDate.getTime());
				cal.set(Calendar.HOUR_OF_DAY, startDate.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, startDate.get(Calendar.MINUTE));
				Date eventStartDate = cal.getTime();

				// build end date/time
				cal = new GregorianCalendar();
				cal.setTime(startDate.getTime());
				cal.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
				Date eventEndDate = cal.getTime();

				// if this event is after, die
				if (cal.getTime().after(endDate.getTime()))
					break;

				Date[] datePair = new Date[4];
				datePair[0] = eventStartDate;
				datePair[1] = eventEndDate;
				datePair[2] = setupDate.getTime();
				datePair[3] = breakDate.getTime();
				datesList.add(datePair);

				// add to start date
				startDate.add(Calendar.DAY_OF_MONTH, interval);
				setupDate.add(Calendar.DAY_OF_MONTH, interval);
				breakDate.add(Calendar.DAY_OF_MONTH, interval);
			}
			// event days of week
			else if ("week".equals(r.getOccurs())) {
				if (r.getDaysOfWeek() == null) {
					return new ArrayList<Event>();
				}
				for (int j = 1; j < 8; j++) {
					String x = Integer.toString(startDate.get(Calendar.DAY_OF_WEEK));
					if (r.getDaysOfWeek().indexOf(x) > -1) {

						// build the start time/date
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(startDate.getTime());
						cal.set(Calendar.HOUR_OF_DAY, startDate.get(Calendar.HOUR_OF_DAY));
						cal.set(Calendar.MINUTE, startDate.get(Calendar.MINUTE));
						Date eventStartDate = cal.getTime();

						// build end date/time
						cal = new GregorianCalendar();
						cal.setTime(startDate.getTime());
						cal.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
						cal.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
						Date eventEndDate = cal.getTime();

						// if this event is after, die
						if (cal.getTime().after(endDate.getTime()))
							break;

						Date[] datePair = new Date[4];
						datePair[0] = eventStartDate;
						datePair[1] = eventEndDate;
						datePair[2] = setupDate.getTime();
						datePair[3] = breakDate.getTime();
						datesList.add(datePair);

					}
					startDate.add(Calendar.DAY_OF_MONTH, 1);
					setupDate.add(Calendar.DAY_OF_MONTH, 1);
					breakDate.add(Calendar.DAY_OF_MONTH, 1);
				}
				startDate.add(Calendar.WEEK_OF_YEAR, interval - 1);
				setupDate.add(Calendar.WEEK_OF_YEAR, interval - 1);
				breakDate.add(Calendar.WEEK_OF_YEAR, interval - 1);

			} else {

				// build the start time/date

				if (startDate.get(Calendar.DAY_OF_MONTH) > r.getDayOfMonth()) {
					startDate.add(Calendar.MONTH, 1);
				}

				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(startDate.getTime());
				cal.set(Calendar.HOUR_OF_DAY, startDate.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, startDate.get(Calendar.MINUTE));
				cal.set(Calendar.DAY_OF_MONTH, r.getDayOfMonth());
				Date eventStartDate = cal.getTime();

				// build end date/time
				cal = new GregorianCalendar();
				cal.setTime(startDate.getTime());
				cal.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
				cal.set(Calendar.DAY_OF_MONTH, r.getDayOfMonth());
				Date eventEndDate = cal.getTime();

				// if this event is after, die
				if (cal.getTime().after(endDate.getTime()))
					break;

				Date[] datePair = new Date[4];
				datePair[0] = eventStartDate;
				datePair[1] = eventEndDate;
				datePair[2] = setupDate.getTime();
				datePair[3] = breakDate.getTime();
				datesList.add(datePair);

				// add to start date
				startDate.add(Calendar.MONTH, interval);
				setupDate.add(Calendar.MONTH, interval);
				breakDate.add(Calendar.MONTH, interval);

			}

		}

		// Getting current recurrance parent events to be excluded from the
		// query
		List<Event> currentRecuranceEvents = InodeFactory.getParentsOfClass(r, Event.class);

		// Building the query
		StringBuffer sb = new StringBuffer();

		// from part
		sb.append("select {event.*} from event, inode event_1_, tree where event_1_.type='event' and event.inode = event_1_.inode and (");

		// where part

		// dates ranges
		Iterator it = datesList.iterator();
		while (it.hasNext()) {
			it.next();
			sb.append("(setup_date <= ? and break_date >= ?)");
			if (it.hasNext())
				sb.append(" or ");
		}
		sb.append(") and ");

		// excluding recurance parent events
		sb.append("event.inode not in (");
		it = currentRecuranceEvents.iterator();
		while (it.hasNext()) {
			Event ev = (Event) it.next();
			sb.append(ev.getInode());
			sb.append(", ");
		}
		sb.append(e.getInode());
		sb.append(") and ");

		// facility part
		sb.append("tree.child = event.inode and tree.parent = ? and ");

		sb.append("event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " ");
		sb.append("order by start_date");

		// Making the query object
		DotHibernate dh = new DotHibernate(Event.class);
		String query = sb.toString();
		dh.setSQLQuery(query);

		// Including parameters

		// dates
		it = datesList.iterator();
		while (it.hasNext()) {
			Date[] datesPair = (Date[]) it.next();
			dh.setParam(datesPair[3]);
			dh.setParam(datesPair[2]);
		}
		dh.setParam(f.getInode());
		return dh.list();
	}

	@SuppressWarnings("unchecked")
	public static boolean hasPermissionsOverTheEvent(User user, Event ev) throws PortalException, SystemException {
		Iterator<Role> rolesIt;
		try {
			rolesIt = APILocator.getRoleAPI().loadRolesForUser(user.getUserId()).iterator();
		} catch (DotDataException e) {
			Logger.error(EventFactory.class,e.getMessage(),e);
			throw new SystemException(e);
		}
		while (rolesIt.hasNext()) {
			Role role = (Role) rolesIt.next();
			if (role.getName().equals(Config.getStringProperty("EVENTS_ADMINISTRATOR"))) {
				return true;
			}
		}
		if (ev.getUserId() == null || (ev.getUserId().equals(user.getUserId())))
			return true;
		else
			return false;

	}

	public static boolean isAnEventAdministrator(User user) throws PortalException, SystemException {
		String x = Config.getStringProperty("EVENTS_ADMINISTRATOR");
		try {
			return com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user, x);
		} catch (DotDataException e) {
			Logger.error(EventFactory.class,e.getMessage(),e);
			throw new SystemException(e);
		}
	}

	/**
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public static List getEventsWaitingForApproval() {

		DotConnect dc = new DotConnect();
		StringBuffer sb = new StringBuffer();

		sb.append("select inode ");
		sb.append("from event ");
		sb.append("where approval_status = " + Constants.EVENT_WAITING_APPROVAL_STATUS + " ");
		sb.append("order by start_date");

		dc.setSQL(sb.toString());
		return dc.getResults();
	}

	@SuppressWarnings("unchecked")
	public static List<User> getEventAdministrators() throws PortalException, SystemException {
		Role adminRole;
		try {
			adminRole = APILocator.getRoleAPI().loadRoleByKey(Config.getStringProperty("EVENTS_ADMINISTRATOR"));
		} catch (Exception e) {
			Logger.error(EventFactory.class,e.getMessage(),e);
			throw new SystemException(e);
		}
		try {
			return APILocator.getRoleAPI().findUsersForRole(adminRole);
		} catch (Exception e) {
			Logger.error(EventFactory.class,e.getMessage(),e);
			throw new SystemException(e);
		}
	}

	public static void sendEmailNotification(Event e, Facility fac, User currentUser, boolean eventChange, Host host) {

		Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);

		String subject = "";
		String from = "";
		String to = "";

		try {
			if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS) {
				if (!e.getContactEmail().trim().equals("")) {
					List<User> administrators =  EventFactory.getEventAdministrators();
					Iterator<User> it = administrators.iterator();
					from = e.getContactEmail();
					while (it.hasNext()) {
						User admin = it.next();
						to += to.equals("")?admin.getEmailAddress():"," + admin.getEmailAddress();
						if (eventChange)
							subject = "Event Change Notification (The event request has been modified)";
						else
							subject = "New Event Request Notification";
					}
				}
			} else {
				if (!e.getContactEmail().trim().equals("")) {

					to = e.getContactEmail();
					from = currentUser.getEmailAddress();

					if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS)
						subject = "Event Approved Notification";
					else
						subject = "Event Disapproved Notification";
				}
			}

			if(UtilMethods.isSet(from) && UtilMethods.isSet(to)){
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("from", from);
				parameters.put("to", to);
				parameters.put("subject", subject);
				parameters.put("eventTitle", e.getTitle());
				parameters.put("approvalStatus", Constants.EVENT_APPROVAL_STATUSES[e.getApprovalStatus()]);
				parameters.put("eventSubtitle", e.getSubtitle());
				String dateTime = "";
				String setupDateTime = "";
				if (e.isTimeTBD())
					dateTime = (UtilMethods.dateToHTMLDateRange(e.getStartDate(), e.getEndDate(), GregorianCalendar
							.getInstance().getTimeZone())
							+ " time TBD");
				else
					dateTime = (UtilMethods.dateToHTMLDateTimeRange(e.getStartDate(), e.getEndDate(), GregorianCalendar
							.getInstance().getTimeZone()));
				setupDateTime = (UtilMethods.dateToHTMLDateTimeRange(e.getSetupDate(), e.getBreakDate(), GregorianCalendar
						.getInstance().getTimeZone()));
				parameters.put("eventDateTime", dateTime);
				parameters.put("eventSetupDateTime", setupDateTime);
				parameters.put("eventRecurance", UtilHTML.recuranceToString(e, r));
				parameters.put("eventFacility", InodeUtils.isSet(fac.getInode())?fac.getFacilityName():"None / Off-Campus");
				parameters.put("eventWebAddress", e.getWebAddress());
				parameters.put("eventReceivedAdminApproval", e.isReceivedAdminApproval() ? "Yes" : "No");
				parameters.put("eventUserName", currentUser.getFullName());
				parameters.put("eventUserEmailAddress", currentUser.getEmailAddress());
				parameters.put("eventContactName", e.getContactName());
				parameters.put("eventContactPhone", e.getContactPhone());
				parameters.put("eventContactFax", e.getContactFax());
				parameters.put("eventContactEmail", e.getContactEmail());
				parameters.put("eventContactCompany", e.getContactCompany());
				parameters.put("eventDescription", e.getDescription());
				parameters.put("eventDirections", e.getDirections());
				String emailTemplate = Config.getStringProperty("EVENT_CHANGE_NOTIFICATION_EMAIL");
				parameters.put("emailTemplate", emailTemplate);
				EmailFactory.sendParameterizedEmail(parameters, null, host, currentUser);
			}
		} catch (Exception e1) {
			Logger.error(EventFactory.class, "Error ocurred sending the event change notifications.", e1);
		}

	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Event> getEventsByDateRangeYParents(java.util.Date from, java.util.Date to, List cats) 
	{
		StringBuffer query = new StringBuffer();

		query.append("(start_date >= " + UtilMethods.dateToShortJDBCForQuery(from) + " ");
		query.append(" or ");
		query.append(" end_date = " + UtilMethods.dateToShortJDBCForQuery(from) + " )" );
		query.append(" and ");
		query.append(" start_date < " + UtilMethods.dateToShortJDBCForQuery(to) + " ");
		query.append(" and approval_status <> "+ Constants.EVENT_DISAPPROVED_STATUS+ " ");

		return InodeFactory.getChildrenClassByConditionAndOrderBy(cats, Event.class, query.toString(), "start_date");
	}


	/**
	 * @param maxEvents Max number of events to be shown
	 * @param from The starting date
	 * @param to   the end date
	 * @return List<Event>
	 * This method returns a list of the latest current
	 * events given a number of max events to be shown
	 * starting form the latest event.
	 */
	@SuppressWarnings("unchecked")
	public static List<Event> getLatestEvents(int maxEvents, Date from , Date to){

		DotHibernate dh = new DotHibernate(Event.class);
		dh.setSQLQuery("select {event.*} from event, inode event_1_ where "
				+ "event_1_.type='event' and  event.inode = event_1_.inode and show_public = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue()
				+ " and " + "(event.start_date >= " + UtilMethods.dateToShortJDBCForQuery(from) + " "
				+ " or " + " end_date = " + UtilMethods.dateToShortJDBCForQuery(from) + " )" 
				+ " and " + " start_date < " + UtilMethods.dateToShortJDBCForQuery(to) + " "
				+ " order by  start_date desc");
		//	+ " and " + "event.approval_status = " + Constants.EVENT_APPROVED_STATUS + " " + "order by  start_date desc");
		if(maxEvents != 0){
			dh.setMaxResults(maxEvents);
		}

		return dh.list();

	}

}

package com.dotmarketing.portlets.jobs.factories;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portlets.jobs.model.Jobs;
/**
 * @author Steven Sajous
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JobsFactory {
	
	public static Jobs getJob(String id) {
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.inode = ?");
		dh.setParam(id);
		return (Jobs)dh.load();
	}
	
	public static List getJobs() {
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs order by jobs.entrydate desc");
		return dh.list();
	}
	
	public static List getJobs(String orderby) {
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs order by " + orderby);
		return dh.list();
	}
	public static List getJobsRollByMonth(String orderby,int numberOfMonths) {
		DotHibernate dh = new DotHibernate(Jobs.class);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.MONTH,numberOfMonths);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.entrydate >= ? order by " + orderby);
		dh.setParam(calendar.getTime());
		return dh.list();
	}
	public static Jobs save(Jobs x) {
		DotHibernate.saveOrUpdate(x);
		return x;
	}	
	
	public static List getJobsByLocation(String location, int limit, int offset) {
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expdate >= ? and (jobs.joblocation like ?  or jobs.city like ?) order by jobs.entrydate desc");
		dh.setParam(new java.util.Date());
		dh.setParam(locationString);
		dh.setParam(locationString);

		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		

		return dh.list();
	}

	public static List getJobsBySearchAndLocation(String s, String location, int limit, int offset) {
		String superString = "%" + s + "%";
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expdate >= ? and (jobs.organization like ?   or jobs.description like ?   or jobs.jobtitle like ? ) and (jobs.city like ? or jobs.joblocation like ?) order by jobs.entrydate desc");
		dh.setParam(new java.util.Date());
		dh.setParam(superString);
		dh.setParam(superString);
		dh.setParam(superString);
		dh.setParam(locationString);
		dh.setParam(locationString);

		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}

	public static List getJobsBySearch(String s, int limit, int offset) {
		String superString = "%" + s + "%";
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expdate >= ? and (jobs.organization like ? or jobs.description like ?  or jobs.jobtitle like ?) order by jobs.entrydate desc");
		dh.setParam(new java.util.Date());
		dh.setParam(superString);
		dh.setParam(superString);
		dh.setParam(superString);
		
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}

	public static List getActiveJobs(int limit, int offset) {
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and expdate >= ? order by jobs.entrydate desc");
		dh.setParam(new java.util.Date());

		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}
	public static List getPremiumJobs() {
		DotHibernate dh = new DotHibernate(Jobs.class);
		dh.setQuery("from jobs in class com.dotmarketing.portlets.jobs.model.Jobs where jobs.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and jobs.premiumlisting = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by jobs.entrydate desc");
		return dh.list();		
	}

}

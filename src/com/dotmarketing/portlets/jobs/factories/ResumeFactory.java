package com.dotmarketing.portlets.jobs.factories;
import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portlets.jobs.model.Resume;
/**
 * @author Steven Sajous
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ResumeFactory {
	
	public static Resume getResume(String id) {
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume where resume.inode = ?");
		dh.setParam(id);
		return (Resume)dh.load();
	}
	
	public static List getResumes(String orderBy) {
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume order by " + orderBy);
		return dh.list();
	}
	
	public static List getResumes() {
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume order by creationdate desc");
		return dh.list();
	}

	public static Resume save(Resume x) {
		DotHibernate.saveOrUpdate(x);
		return x;
	}
    
    public static void delete(Resume r) {
        DotHibernate.delete(r);
    }

	public static List getActiveResumes(int limit, int offset) {
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume where resume.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expirationdate >= ? order by resume.creationdate desc");
		dh.setParam(new java.util.Date());
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}

	public static List getResumesBySearch(String s, int limit, int offset) {
		String superString = "%" + s + "%";
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume where resume.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expirationdate >= ? and (resume.name like ?   or resume.objective like ?   or 	resume.qualification like ?   ) order by resume.creationdate desc");
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

	public static List getResumesByLocation(String location, int limit, int offset) {
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume where resume.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expirationdate >= ? and (resume.location like ?  or resume.city like ?) order by resume.creationdate desc");
		dh.setParam(new java.util.Date());
		dh.setParam(locationString);
		dh.setParam(locationString);
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}

	public static List getResumesBySearchAndLocation(String s, String location, int limit, int offset) {
		String superString = "%" + s + "%";
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Resume.class);
		dh.setQuery("from resume in class com.dotmarketing.portlets.jobs.model.Resume where resume.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + "  and expirationdate >= ? and (resume.name like ?   or resume.objective like ?   or resume.qualification like ?) and (resume.city like ? or resume.location like ?) order by resume.creationdate desc");
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

    
    
}

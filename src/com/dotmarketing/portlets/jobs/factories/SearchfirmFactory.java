package com.dotmarketing.portlets.jobs.factories;
import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.jobs.model.Searchfirm;
/**
 * @author Steven Sajous
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SearchfirmFactory extends InodeFactory{
	
	public static Searchfirm getSearchfirm(String id) {
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm where searchfirm.inode = ?");
		dh.setParam(id);
		return (Searchfirm)dh.load();
	}
	
	public static List getSearchfirms(String orderby) {
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm order by " + orderby);		
		return dh.list();
	}
	
	public static List getSearchfirms() {
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm order by creationdate desc, searchfirm.name");		
		return dh.list();
	}

	public static void save(Searchfirm x) {
		saveInode(x);
	}
    
    public static void delete(Searchfirm r) {
        DotHibernate.delete(r);
    }
    
    public static List getSearchFirmsByOrderBy(String field) {
        DotHibernate dh = new DotHibernate(Searchfirm.class);
        dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm order by ?, creationdate desc");
        dh.setParam("searchfirm." + field);
        return dh.list();        
    }
	public static List getSearchFirmsBySearchAndLocation(String s, String location, int limit, int offset) {
		String superString = "%" + s + "%";
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm where searchfirm.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and (searchfirm.organization like ?   or searchfirm.description like ? or searchfirm.title like ?) and (searchfirm.streetaddress1 like ?  or searchfirm.streetaddress2 like ?) order by searchfirm.creationdate desc");
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

	public static List getSearchFirmsByLocation(String location, int limit, int offset) {
		String locationString = "%" + location + "%";
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm where searchfirm.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and (searchfirm.streetaddress1 like ? or searchfirm.streetaddress2 like ?) order by searchfirm.creationdate desc");
		dh.setParam(locationString);
		dh.setParam(locationString);
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}
	
	public static List getSearchFirmsBySearch(String s, int limit, int offset) {
		String superString = "%" + s + "%";
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm where searchfirm.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and (searchfirm.organization like ?   or searchfirm.description like ?   or searchfirm.title like ?   ) order by searchfirm.creationdate desc");
		dh.setParam(superString);
		dh.setParam(superString);
		dh.setParam(superString);
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		
		return dh.list();
	}

	public static List getActiveFirmlist(int limit, int offset) {
		DotHibernate dh = new DotHibernate(Searchfirm.class);
		dh.setQuery("from searchfirm in class com.dotmarketing.portlets.jobs.model.Searchfirm where searchfirm.active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by searchfirm.creationdate desc");
		if (limit!=0) {
			dh.setFirstResult(offset);
			dh.setMaxResults(limit);
		}		
		return dh.list();
	}

}

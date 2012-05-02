package com.dotmarketing.portlets.userclicks.factories;


import com.dotmarketing.beans.Clickstream;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;


/**
 *
 * @author  Rocco
 */
public class UserClickFactory {

	private static final String GET_TOP_USER_CLICKSTREAMS = "SELECT {clickstream.*} from clickstream where user_id = ? order by clickstream_id desc";
	private static final String COUNT_USER_CLICKS = "SELECT count(*) as test from clickstream where user_id = ?";

    public static java.util.List getTopUserClicks(String UserId){
    	DotHibernate dh = new DotHibernate(Clickstream.class);
    	dh.setMaxResults(5);
    	dh.setSQLQuery(GET_TOP_USER_CLICKSTREAMS);
    	dh.setParam(UserId);
    	
    	return dh.list();   	
    }
    
    public static java.util.List getUserClicks(String UserId, int offset, int limit){
    	DotHibernate dh = new DotHibernate(Clickstream.class);
    	dh.setQuery("from inode in class " + Clickstream.class.getName() + " where user_id = ? order by clickstream_id desc");
    	dh.setParam(UserId);
    	dh.setFirstResult(offset);
    	dh.setMaxResults(limit);
    	return dh.list();   	
  
    }
    
    public static java.util.List getAllUserClicks(String UserId){
    	DotHibernate dh = new DotHibernate(Clickstream.class);
    	dh.setQuery("from inode in class " + Clickstream.class.getName() + " where user_id = ? order by clickstream_id desc");
    	dh.setParam(UserId);
    	return dh.list();   	
  
    }
    
    public static int countUserClicks(String UserId){
    	DotConnect db = new DotConnect();
    	db.setSQL(COUNT_USER_CLICKS);
    	db.addParam(UserId);
    	return db.getInt("test");
	
    }
    

	
}

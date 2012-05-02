package com.dotmarketing.portlets.banners.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.cache.BannerCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.listeners.BannersListener;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;


/**
 *
 * @author  will
 */
public class BannerFactory {

	private static CategoryAPI categoryAPI = APILocator.getCategoryAPI();

	public static java.util.List getActiveBanners() {
        DotHibernate dh = new DotHibernate(Banner.class);
        dh.setQuery("from banner in class com.dotmarketing.portlets.banners.model.Banner where active = ? and start_date <= ? and end_date >= ?");
		dh.setParam(true);
        java.util.Date d =  new java.util.Date();
        dh.setParam(d);
		dh.setParam(d);
        return ( java.util.List ) dh.list();
    }

	/**
	 * Method saveBanner.
	 */
	public static void saveBanner(Banner b) {	
		
	    Logger.debug(TreeMap.class, "class name in saveBanner=" + b.getClass());
		DotHibernate.saveOrUpdate(b);
	}
	
	public static Banner getBanner(String id) {
		/*long x = 0;
		try {
			x=Long.parseLong(id);
		}
		catch(Exception e){}*/
		DotHibernate dh = new DotHibernate(Banner.class);
		dh.setQuery("from banner in class com.dotmarketing.portlets.banners.model.Banner where banner.inode=?");
		dh.setParam(id);
		return (Banner) dh.load();
		
	}	

    public static java.util.List getBannersByOrderAndKeywords(String keywords, String orderby) {
        DotHibernate dh = new DotHibernate(Banner.class);
        dh.setQuery("from banner in class com.dotmarketing.portlets.banners.model.Banner where title like '%" + keywords + "%' order by " + orderby);
        return (java.util.List) dh.list();
    }

    public static java.util.List getBannersByOrder(String orderby) {
        DotHibernate dh = new DotHibernate(Banner.class);
        dh.setQuery("from banner in class com.dotmarketing.portlets.banners.model.Banner where active = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by " + orderby);
        return (java.util.List) dh.list();
    }
    
    public static List getBanners(String path, String placement) {
        DotHibernate dh = new DotHibernate(Banner.class);
        
        String query = "from banner in class com.dotmarketing.portlets.banners.model.Banner where path = ? and placement like '%" + placement + "%' and active = " + DbConnectionFactory.getDBTrue();
        dh.setQuery(query);
        dh.setParam(path);
        return (java.util.List) dh.list();
    }
    
    public static List getBannersWithOrder(String path, String placement, User user, boolean respectFrontendRoles) 
    	throws DotDataException, DotSecurityException 
    {
    	ArrayList banners = new ArrayList();
    	Entity entity = EntityFactory.getEntity("Banner");
    	List categories = EntityFactory.getEntityCategories(entity);
    	Category category = (Category) categories.get(0);
    	List<Category> childrenCategories = categoryAPI.getChildren(category, user, respectFrontendRoles); 
    	for(Category childCategory : childrenCategories)
    	{
    		String categoryName = childCategory.getKey();
    		banners.addAll(getBanners(path,categoryName));
    	}
    	return banners;
    }

    public static List getBannerList(String path, String placement, User user, boolean respectFrontendRoles) {
    	
    	List banners = new ArrayList();
    	try {

	    	while (banners.size()==0 && path!=null && path.length()>0) {
	
	    		banners = BannerCache.getBannersFromCache(path, placement, user, respectFrontendRoles);
	    		
	        	
	    		if (banners.size()>0) {
	        		return banners;
	        	}
	
	    		if (path.endsWith(Config.getStringProperty("VELOCITY_PAGE_EXTENSION"))) {
	        		path = path.substring(0,path.lastIndexOf("/")+1);
	        	}
	        	else {
	        		int idx1 = path.lastIndexOf("/");
	    			path = path.substring(0,path.length()-1);
	        		idx1 = path.lastIndexOf("/");
	        		if (idx1 != -1) {
	        			path = path.substring(0,idx1+1);
	        		}
	        		else {
	        			path = "";
	        		}
	        	}
	    	}
    	}
    	catch (Exception e) {
	        Logger.error(BannerFactory.class, e.toString(), e);	        
    	}
    	return banners;
    }
    
    private static void addBannerToSession (HttpServletRequest req, Banner banner) {
    	HttpSession session = req.getSession();
		Logger.debug(BannerFactory.class, "addBannerToSession: session.getAttribute(BannersListener.BANNERS_ATTRIBUTE_KEY) != null: " + (session.getAttribute(BannersListener.BANNERS_ATTRIBUTE_KEY) != null));
        Set banners = (Set) session.getAttribute(BannersListener.BANNERS_ATTRIBUTE_KEY);

        Logger.debug(BannerFactory.class, "addBannerToSession: session.getAttribute(banners.contains(banner): " +banners.contains(banner));
        if (!banners.contains(banner)) {
        	banners.add(banner);
    	}
    }
    
    public static void updateBannerViews(HttpServletRequest req, Banner banner) {
		Logger.debug(BannerFactory.class, "updateBannerViews: banner: " + banner.getInode());
		Logger.debug(BannerFactory.class, "updateBannerViews: before banner.getNmbrViews(): " + banner.getNmbrViews());
    	addBannerToSession (req, banner);
    	banner.setNmbrViews(banner.getNmbrViews()+1);
		Logger.debug(BannerFactory.class, "updateBannerViews: after banner.getNmbrViews(): " + banner.getNmbrViews());
    }
    
    public static void updateBannerClicks(HttpServletRequest req, Banner banner) {
    	addBannerToSession (req, banner);
    	banner.setNmbrClicks(banner.getNmbrClicks()+1);
    }

	public CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public void setCategoryAPI(CategoryAPI categoryAPI) {
		this.categoryAPI = categoryAPI;
	}
}

/*
 * Created on May 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dotmarketing.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.banners.factories.BannerFactory;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

/**
 * @author David & Salvador
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BannerCache{
	
    public static void removeFromBannerCache(String path, String placement, Banner banner) {
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	cache.remove(path, getPrimaryGroup());
    }
    
    public static void updateBannerCache(String previousPath, String previousPlacement,String newPath, String newPlacement,	Banner banner){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	cache.put(previousPath,banner,getPrimaryGroup());
    }

    public static void addToBannerCache(String path, String placement, Banner banner){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	HashMap bannerPlacements = new HashMap();
    	//If the entry is null I create a new Instance of HashMap
    	try{
    		bannerPlacements = (HashMap) cache.get(path, getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(BannerCache.class,"Cache Entry not found", e);
		}
    	
    	//If the entry is null I create a new instance of a HashSet to store the banner 
    	Set banners = (Set) bannerPlacements.get(placement);
        if (banners == null) 
        {
        	banners = new HashSet();
    	}
        //Add the banner to the HashSet
   		banners.add(banner);
   		//Add the HashSet to the HashMap
   		bannerPlacements.put(placement,banners);
   		//Add the HashMap to the cache
   		cache.put(path,bannerPlacements, getPrimaryGroup());
    }

    public static ArrayList getBannersFromCache(String path, String placement, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	HashMap bannerPlacements = new HashMap();
        //Find the HashMap
    	try{
    		bannerPlacements = (HashMap) cache.get(path,getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(BannerCache.class,"Cache Entry not found", e);
		}
    	if (bannerPlacements==null) 
    	{
    		bannerPlacements = new HashMap();
    	}
    	//Find the HashSet in the HashMap
    	ArrayList banners = (ArrayList) bannerPlacements.get(placement);
        if (banners == null) 
        {
            //If the Cache is empty, I populate the cache first
        	banners = new ArrayList();
        	//to do this lazily... 
            //List bannerList = BannerFactory.getBanners(path, placement);
        	List bannerList = BannerFactory.getBannersWithOrder(path, placement, user, respectFrontendRoles);
            //Add the list to the HashSet
           	banners.addAll(bannerList);
           	//Add the HashSet to the HashMap
           	bannerPlacements.put(placement,banners);
           	//Add the HashMap to the cache
        	cache.put(path,bannerPlacements, getPrimaryGroup());
    	}
        return banners;
    }
    
    public static void clearCache(){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		cache.flushGroup(getPrimaryGroup());
	}
    
    public static String[] getGroups() {
    	String[] groups = {getPrimaryGroup()};
    	return groups;
    }
    
    public static String getPrimaryGroup() {
    	return "BannerCache";
    }
   
    
}

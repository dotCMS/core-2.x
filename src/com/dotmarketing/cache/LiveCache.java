/*
 * Created on May 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dotmarketing.cache;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.PublishFactory;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

/**
 * 
 * This cache is used to know when an asset is live, it doesn't store real valuable info in the cache
 * 
 * @author David
 * @author Jason Tesser
 *
 */
public class LiveCache {
    
    /**
     * This method adds the given asset uri to the cache using the 
     * host id + identifier uri as the key
     * This method also send a signal to the cluster to invalidate key cluster wide
     */
    public static void addToLiveAssetToCache(WebAsset asset){
    	
    	HostAPI hostAPI = APILocator.getHostAPI();
    	
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        //The default value for velocity page extension
        String ext = Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
		// we use the identifier uri for our mappings.
        try{
        	Identifier id = IdentifierCache.getIdentifierFromIdentifierCache(asset.getIdentifier());
        	//Obtain the host of the webassets
        	User systemUser = APILocator.getUserAPI().getSystemUser();
    		Host host = hostAPI.findParentHost(asset, systemUser, false);
    		if(host == null) return;
    		
    		//Obtain the URI for future uses
    		String uri = id.getURI();
    		//Obtain the inode value of the host;
    		String hostId = host.getIdentifier();

    		//if this is an index page, map its directories to it
    		if(uri.endsWith("." + ext))
    		{		    
    		    Logger.debug(LiveCache.class, "Mapping: " + uri + " to " + uri);
    		    
    		    //Add the entry to the cache
    			cache.put(getPrimaryGroup() + hostId + ":" + uri,uri, getPrimaryGroup() + "_" + hostId);

    			if(uri.endsWith("/index." + ext))
    			{
    			    //Add the entry to the cache
    			    Logger.debug(LiveCache.class, "Mapping: " + uri.substring(0,uri.lastIndexOf("/index." + ext)) + " to " + uri);			    
    				cache.put(getPrimaryGroup() + hostId + ":" + uri.substring(0,uri.lastIndexOf("/index." + ext)),uri, getPrimaryGroup() + "_" + hostId);
    				//Add the entry to the cache
    			    Logger.debug(LiveCache.class, "Mapping: " + uri.substring(0,uri.lastIndexOf("/index." + ext)) + " to " + uri);
    				cache.put(getPrimaryGroup() + hostId + ":" + uri.substring(0,uri.lastIndexOf("index." + ext)),uri, getPrimaryGroup() + "_" + hostId);
    			}
    		}
    		else if (asset instanceof Link) {
    			Folder parent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
    			String path = ((Link)asset).getURI(parent);
    			//add the entry to the cache
    		    Logger.debug(LiveCache.class, "Mapping: " + uri + " to " + path);
    			cache.put(getPrimaryGroup() + hostId + ":" + uri,path, getPrimaryGroup() + "_" + hostId);
    		} else {
    			String path = FileFactory.getRelativeAssetPath(asset);
    			//add the entry to the cache
    		    Logger.debug(LiveCache.class, "Mapping: " + uri + " to " + path);
    			cache.put(getPrimaryGroup() + hostId + ":" + uri,path, getPrimaryGroup() + "_" + hostId);
    		}
        } catch (DotDataException e) {
        	Logger.error(LiveCache.class,"Unable to retrieve identifier", e);
        	throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
        	Logger.error(LiveCache.class,"Unable to retrieve identifier", e);
        	throw new DotRuntimeException(e.getMessage(), e);
		}
    }
    

    /**
     * This method return the asset uri when the asset exists in the cache 
     * @param URI
     * @param host
     * @return null if the asset is not in the cache, the asset uri if the asset is in the cache
     */
	public static String getPathFromCache(String URI, Host host){
		if (URI.equals("/")) {
			String pointer = (String) VirtualLinksCache.getPathFromCache(host.getHostname() + ":/cmsHomePage");
			if (!UtilMethods.isSet(pointer)) {
				pointer = (String) VirtualLinksCache.getPathFromCache("/cmsHomePage");
			}
			if (UtilMethods.isSet(pointer))
				URI = pointer; 
		}
	    return getPathFromCache (URI, host.getIdentifier());
	}
	
    /**
     * This method return the asset uri when the asset exists in the cache 
     * @param URI
     * @param hostId
     * @return null if the asset is not in the cache, the asset uri if the asset is in the cache
     */
	public static String getPathFromCache(String URI, String hostId){
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		String _uri = null;
		try{
			_uri = (String) cache.get(getPrimaryGroup() + hostId + ":" + URI,getPrimaryGroup() + "_" + hostId);
		}catch (DotCacheException e) {
			Logger.debug(LiveCache.class,"Cache Entry not found", e);
    	}

		if(_uri != null)
		{
			if(_uri.equals(WebKeys.Cache.CACHE_NOT_FOUND))
				return null;
		    return _uri;
		}
		
		String ext = Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
		if (URI.endsWith("/")) {
			//it's a folder path, so I add index.{pages ext} at the end
			URI += "index." + ext;

			// try again with an index page this time
			try{
				_uri = (String) cache.get(getPrimaryGroup() + hostId + ":" + URI,getPrimaryGroup() + "_" + hostId);
			}catch (DotCacheException e) {
				Logger.debug(LiveCache.class,"Cache Entry not found", e);
	    	}
	
			if(_uri != null)
			{
				if(_uri.equals(WebKeys.Cache.CACHE_NOT_FOUND))
					return null;
			    return _uri;
			}
		}
		
		
		// lets try to lazy get it.
		Identifier id = IdentifierFactory.getIdentifierByURI(URI, hostId);

		if(!InodeUtils.isSet(id.getInode())) 
		{
			cache.put(getPrimaryGroup() + hostId + ":" + URI, WebKeys.Cache.CACHE_NOT_FOUND, getPrimaryGroup() + "_" + hostId);

			//it's a folder path, so I add index.html at the end
			URI += "/index." + ext;
			id = IdentifierFactory.getIdentifierByURI(URI, hostId);
			if(!InodeUtils.isSet(id.getInode()))
			{
				cache.put(getPrimaryGroup() + hostId + ":" + URI, WebKeys.Cache.CACHE_NOT_FOUND, getPrimaryGroup() + "_" + hostId);
			    return null;
			}
		}

		WebAsset asset = null;
		if(id.getURI().endsWith("." + ext))
		{
		    asset = (WebAsset) IdentifierFactory.getLiveChildOfClass(id, HTMLPage.class);
		}
		else
		{
		    asset = (WebAsset) IdentifierFactory.getLiveChildOfClass(id, File.class);
		}
		
		if(InodeUtils.isSet(asset.getInode()))
		{
		    Logger.debug(PublishFactory.class, "Lazy Mapping: " + id.getURI() + " to " + URI);
		    //The cluster entry doesn't need to be invalidated when loading the entry lazily, 
		    //if the entry gets invalidated from the cluster in this case causes an invalidation infinite loop
		    addToLiveAssetToCache(asset);
		} else {
			//Identifier exists but the asset is not live
			cache.put(getPrimaryGroup() + hostId + ":" + URI, WebKeys.Cache.CACHE_NOT_FOUND, getPrimaryGroup() + "_" + hostId);
		    return null;
		}
		try{
			return (String) cache.get(getPrimaryGroup() + hostId + ":" + URI,getPrimaryGroup() + "_" + hostId);
		}catch (DotCacheException e) {
			Logger.debug(LiveCache.class,"Cache Entry not found", e);
			return null;
    	}
	}

	/**
	 * This method removes the asset key from the cache and send an invalidation message
	 * to the cluster when the cms is in cluster
	 * @param asset
	 */
	public static void removeAssetFromCache(WebAsset asset){
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	HostAPI hostAPI = APILocator.getHostAPI();

		try{
	    	User systemUser = APILocator.getUserAPI().getSystemUser();
	    	Host host = hostAPI.findParentHost(asset, systemUser, false);
	    	if(host == null)
	    		return;
		    String hostId = host.getIdentifier();
			Identifier identifier = IdentifierCache.getIdentifierFromIdentifierCache(asset.getIdentifier());
			cache.remove(getPrimaryGroup() + hostId + ":" + identifier.getURI(),getPrimaryGroup() + "_" + hostId);
		}catch (Exception e) {
			Logger.error(LiveCache.class, "Unable to remove asset from live cache", e);
		}
	}
	
	public static void clearCache(String hostId){
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
	    //clear the cache
	    cache.flushGroup(getPrimaryGroup() + "_" + hostId);
	}
	public static String[] getGroups() {
    	String[] groups = {getPrimaryGroup()};
    	return groups;
    }
    
    public static String getPrimaryGroup() {
    	return "LiveCache";
    }
}
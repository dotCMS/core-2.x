package com.dotmarketing.cache;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Logger;

/**
 * @author David
 */
public class FolderCache {
    
    public static void addFolder(Folder f){
    	HostAPI hostAPI = APILocator.getHostAPI();
    	UserAPI userAPI = APILocator.getUserAPI();
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		// we use the identifier uri for our mappings.
		String inode = f.getInode();

        Host host;
		try {
			host = hostAPI.findParentHost(f, userAPI.getSystemUser(), false);
		} catch (Exception e) {
			throw new DotRuntimeException(e.getMessage(), e);
		}

        String folderPath = host!=null 
        		? host.getIdentifier() + ":" + f.getPath()  
				:  ":" + f.getPath();
		cache.put(getPrimaryGroup() + inode, f, getPrimaryGroup());
       
        cache.put(getPrimaryGroup() + folderPath, f, getPrimaryGroup());
        
	}
    

    public static Folder getFolderByInodeCacheOnly(String inode){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	Folder f = null;
    	try{
    		f = (Folder) cache.get(getPrimaryGroup() + inode,getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FolderCache.class,"Cache Entry not found", e);
    	}
        return f;
    }
    
    
    
    

    public static Folder getFolderByInode(String inode){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	Folder f = null;
    	try{
    		f = (Folder) cache.get(getPrimaryGroup() + inode,getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FolderCache.class,"Cache Entry not found", e);
    	}
        if (f == null) {
            f = FolderFactory.getFolderByInode(inode);
            addFolder(f);
        }
        return f;
    }

    public static Folder getFolderByPathAndHostName(String path, String hostName){
    	HostAPI hostAPI = APILocator.getHostAPI();
    	UserAPI userAPI = APILocator.getUserAPI();

    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        String folderPath = hostName + ":" + path;
        Folder f = null;
    	try{
    		f = (Folder) cache.get(getPrimaryGroup() + folderPath,getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FolderCache.class,"Cache Entry not found", e);
    	}
        if (f == null) {
            Host host;
			try {
				host = hostAPI.findByName(hostName, userAPI.getSystemUser(), false);
			} catch (Exception e) {
				throw new DotRuntimeException(e.getMessage(), e);
			} 
            f = FolderFactory.getFolderByPath(path, host);
            addFolder(f);
        }
        
        return f;
    }

    public static Folder getFolderByPathAndHostId(String path, String hostId){
    	HostAPI hostAPI = APILocator.getHostAPI();
    	UserAPI userAPI = APILocator.getUserAPI();
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        String folderPath = hostId + ":" + path;
        Folder f = null;
    	try{
    		f = (Folder) cache.get(getPrimaryGroup() + folderPath, getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FolderCache.class,"Cache Entry not found", e);
    	}
        if (f == null) {
            Host host;
			try {
				host = hostAPI.find(hostId, userAPI.getSystemUser(), false);
			} catch (Exception e) {
				throw new DotRuntimeException(e.getMessage(), e);
			} 
            f = FolderFactory.getFolderByPath(path, host);
            addFolder(f);
        }
        
        return f;
    }

    public static void removeFolder(Folder f){
    	HostAPI hostAPI = APILocator.getHostAPI();
    	UserAPI userAPI = APILocator.getUserAPI();
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        String inode = f.getInode();

        Host host;
		try {
			host = hostAPI.findParentHost(f, userAPI.getSystemUser(), false);
		} catch (Exception e) {
			throw new DotRuntimeException(e.getMessage(), e);
		} 
        String folderPath = host!=null 
				? host.getIdentifier() + ":" + f.getPath()  
				:  ":" + f.getPath();

        cache.remove(getPrimaryGroup() + inode, getPrimaryGroup());
        cache.remove(getPrimaryGroup() + folderPath, getPrimaryGroup());
    }

    public static void clearCache(){
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
	    //clear the cache
	    cache.flushGroup(getPrimaryGroup());
	}
	public static String[] getGroups() {
    	String[] groups = {getPrimaryGroup()};
    	return groups;
    }
    
    public static String getPrimaryGroup() {
    	return "FolderCache";
    }
}

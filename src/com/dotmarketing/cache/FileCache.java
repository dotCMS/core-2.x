package com.dotmarketing.cache;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

/**
 * @author David
 * @author Jason Tesser
 */
public class FileCache {

    
    public static void addFile(File f){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		// we use the identifier uri for our mappings.
		String inode = f.getInode();
		cache.put(getPrimaryGroup() + inode, f, getPrimaryGroup());
        
	}
    
    /*public static File getFileByInode(long fileInode) throws DotHibernateException{
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	File f = null;
    	try{
    		f = (File) cache.get(getPrimaryGroup() + fileInode, getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FileCache.class,"Cache Entry not found", e);
        }
        if (f == null) {
            f = (File)new HibernateUtil(File.class).load(fileInode);
            if(f != null && InodeUtils.isSet(f.getInode())){
            	addFile(f);
	    	}
        }
        return f;
	}*/

    public static File getFileByInode(String fileInode) throws DotHibernateException{
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	File f = null;
    	try{
    		f = (File) cache.get(getPrimaryGroup() + fileInode, getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FileCache.class,"Cache Entry not found", e);
    	}
        if (f == null) {
            f = (File)new HibernateUtil(File.class).load(fileInode);
            if(f != null && InodeUtils.isSet(f.getInode())){
            	addFile(f);
	    	}
        }
        return f;
	}
    
    public static File loadFromCacheOnly(String fileInode) {
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	File f = null;
    	try{
    		f = (File) cache.get(getPrimaryGroup() + fileInode, getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(FileCache.class,"Cache Entry not found", e);
    	}
        return f;
	}
    
    
    

    public static void removeFile(File f){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        String inode = f.getInode();
        LiveCache.removeAssetFromCache(f);
        WorkingCache.removeAssetFromCache(f);
        cache.remove(getPrimaryGroup() + inode, getPrimaryGroup());
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
    	return "FileCache";
    }
}

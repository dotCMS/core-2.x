package com.dotmarketing.portlets.files.business;

import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.Logger;

public class FileCacheImpl extends FileCache {
	
	private DotCacheAdministrator cache;
	
	private static String primaryGroup = "FileCache";
    // region's name for the cache
    private static String[] groupNames = {primaryGroup};

	public FileCacheImpl() {
        cache = CacheLocator.getCacheAdministrator();
	}

	@Override
	protected File add(String key, File file) {
		key = primaryGroup + key;

        // Add the key to the cache
        cache.put(key, file, primaryGroup);

        try {
			return (File) cache.get(key,primaryGroup);
		} catch (DotCacheException e) {
			Logger.warn(this, "Cache Entry not found after adding", e);
			return file;
		}
	}
	
	@Override
	protected File get(String key) {
		key = primaryGroup + key;
    	File file = null;
    	try{
    		file = (File)cache.get(key,primaryGroup);
    	}catch (DotCacheException e) {
			Logger.debug(this, "Cache Entry not found", e);
		}
        return file;	
	}

    /* (non-Javadoc)
	 * @see com.dotmarketing.business.PermissionCache#clearCache()
	 */
    public void clearCache() {
        // clear the cache
        cache.flushGroup(primaryGroup);
    }

    /* (non-Javadoc)
	 * @see com.dotmarketing.business.PermissionCache#remove(java.lang.String)
	 */
    protected void remove(String key){
    	key = primaryGroup + key;
    	try{
    		cache.remove(key,primaryGroup);
    	}catch (Exception e) {
			Logger.debug(this, "Cache not able to be removed", e);
		} 
    }
    public String[] getGroups() {
    	return groupNames;
    }
    public String getPrimaryGroup() {
    	return primaryGroup;
    }
}

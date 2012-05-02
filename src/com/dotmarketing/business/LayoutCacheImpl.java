/**
 * 
 */
package com.dotmarketing.business;

import com.dotmarketing.util.Logger;

/**
 * @author Jason Tesser
 *
 */
public class LayoutCacheImpl extends LayoutCache {

	private DotCacheAdministrator cache;

	private String primaryGroup = "dotCMSLayoutCache";

	// region's name for the cache
	private String[] groupNames = {primaryGroup};

	public LayoutCacheImpl() {
		cache = CacheLocator.getCacheAdministrator();
	}
	
	/* (non-Javadoc)
	 * @see com.dotmarketing.business.LayoutCache#add(java.lang.String, com.dotmarketing.business.Role)
	 */
	@Override
	protected Layout add(String key, Layout layout) {
		key = primaryGroup + key;

		// Add the key to the cache
		cache.put(key, layout,primaryGroup);

		try {
			return (Layout)cache.get(key,primaryGroup);
		} catch (DotCacheException e) {
			Logger.warn(this,"Cache Entry not found after adding", e);
			return layout;
		}
	}

	/* (non-Javadoc)
	 * @see com.dotmarketing.business.LayoutCache#clearCache()
	 */
	@Override
	public void clearCache() {
		cache.flushGroup(primaryGroup);
	}

	/* (non-Javadoc)
	 * @see com.dotmarketing.business.LayoutCache#get(java.lang.String)
	 */
	@Override
	protected Layout get(String key) {
		key = primaryGroup + key;
		Layout layout = null;
		try{
			layout = (Layout)cache.get(key,primaryGroup);
		}catch (DotCacheException e) {
			Logger.debug(this, "Cache Entry not found", e);
		}
		return layout;
	}

	/* (non-Javadoc)
	 * @see com.dotmarketing.business.LayoutCache#remove(java.lang.String)
	 */
	@Override
	protected void remove(String key) {
		key = primaryGroup + key;
		try{
			cache.remove(key,primaryGroup);
		}catch (Exception e) {
			Logger.debug(this, "Cache not able to be removed", e);
		} 
	}

	/* (non-Javadoc)
	 * @see com.dotmarketing.business.Cachable#getGroups()
	 */
	public String[] getGroups() {
		return groupNames;
	}

	/* (non-Javadoc)
	 * @see com.dotmarketing.business.Cachable#getPrimaryGroup()
	 */
	public String getPrimaryGroup() {
		return primaryGroup;
	}

}

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
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotCacheAdministrator;
import com.dotmarketing.business.DotCacheException;
import com.dotmarketing.business.Versionable;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

/**
 * @author David
 *
 */
public class IdentifierCache{
	
    public static void addIdentifierToIdentifierCache(Identifier id){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        if (id != null && InodeUtils.isSet(id.getInode())) {
            //Obtain the key for the new entrance
            String key =  id.getHostId() + "-" + id.getURI();
            
            //Add the new entry to the cache
            cache.put(getPrimaryGroup() + key,id, getPrimaryGroup());        
            cache.put(getPrimaryGroup() + id.getInode(),id, getPrimaryGroup());
        }
    }
	
	public static void addVersionableToIdentifierCache(Versionable versionable){
        //we use the identifier uri for our mappings.
    	Identifier id = IdentifierFactory.getIdentifierByInode(versionable);
        addIdentifierToIdentifierCache(id);        
    }
    
    public static Identifier getPathFromIdCache(String URI, Host host){
	    return getPathFromIdCache(URI,host.getIdentifier());
	}

    /**
     * Will look for an identifier in the cache.  This method will NOT goto database  
     * @return
     */
    public static Identifier loadFromCacheOnly(String identId){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
	    Identifier value = null;
	    try{
	    	value = (Identifier) cache.get(getPrimaryGroup() + identId, getPrimaryGroup());
	    }catch (DotCacheException e) {
			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
    	}
	    return value;
    }
    
    /**
     * This method find the identifier associated with the given URI
     * this methods will try to find the identifier in memory but if it is not found in memory
     * it'll be found in db and put in memory. 
     * @param URI uri of the identifier
     * @param hostId host where the identifier belongs
     * @return The identifier or an empty (inode = 0) identifier if it wasn't found in momory and db. 
     */
	public static Identifier getPathFromIdCache(String URI, String hostId) {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		Identifier value = null;
		try{
			value = (Identifier) cache.get(getPrimaryGroup() + hostId + "-" + URI,getPrimaryGroup());
		}catch (DotCacheException e) {
			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
    	}
        
	    //If not found
	    if(value == null)
	    {
		    //If the entry is not in the cache I will find it at DB
		    Identifier id = IdentifierFactory.getIdentifierByURI(URI,hostId);
		    if(InodeUtils.isSet(id.getInode()))
		    {
		        //Add the entry to the cache
		        IdentifierCache.addIdentifierToIdentifierCache(id);
		        //return the value
		        try{
		        	value = (Identifier) cache.get(getPrimaryGroup() + hostId + "-" + URI,getPrimaryGroup());
		        }catch (DotCacheException e) {
					Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
		    	}
		    }
	    }

        if (value == null)
            value = new Identifier ();
        
        //Return the entry
	    return value;
	}

	public static void removeURIFromIdCache(String URI, Host host) {
	    removeURIFromIdCache (URI, host.getIdentifier());
	}
	
    public static void removeURIFromIdCache(String URI, String hostId) {
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	cache.remove(getPrimaryGroup() + getPathFromIdCache(URI, hostId).getInode(),getPrimaryGroup());
        String key = hostId + "-" + URI;
		cache.remove(getPrimaryGroup() + key,getPrimaryGroup());
	}

	@SuppressWarnings("deprecation")
	public static void removeAssetFromIdCache(Versionable versionable){
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
		Identifier id = IdentifierFactory.getIdentifierByInode(versionable);
        if (InodeUtils.isSet(id.getInode())) {
    		//Obtain the key of the entry to delete
    		String key = id.getHostId() + "-" + id.getURI();
    		//Remove the element from the cache
    		cache.remove(getPrimaryGroup() + key,getPrimaryGroup());
//            cache.remove(asset.getInode());
            cache.remove(getPrimaryGroup() + id.getInode(),getPrimaryGroup());
        }
	}


	@SuppressWarnings("deprecation")
	public static Identifier getIdentifierFromIdentifierCache(String identId)throws DotHibernateException {
		DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
	    Identifier value = null;
	    try{
	    	value = (Identifier) cache.get(getPrimaryGroup() + identId,getPrimaryGroup());
	    }catch (DotCacheException e) {
			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
    	}
	    
	    //If not found
	    if(value == null && InodeUtils.isSet(identId))
	    {
	    	Identifier ident = (Identifier)InodeFactory.getInode(identId, Identifier.class);	    	
	    	if(ident != null && InodeUtils.isSet(ident.getInode())){
	            addIdentifierToIdentifierCache(ident);
	            try{
	            	value= (Identifier) cache.get(getPrimaryGroup() + identId,getPrimaryGroup());
	            }catch (DotCacheException e) {
	    			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
	        	}
	    	}
	    }
        
        if (value == null){
            value = new Identifier ();
            if(InodeUtils.isSet(identId))
            	Logger.debug(IdentifierCache.class,"Identifier value from the Identifier cache is null for Inode with versionId " + identId);
        }
	    //Return the entry
	    return value;
	}

	public static void removeFromIdCacheByInode(WebAsset inode) {
		removeFromIdCacheByInode (inode.getInode());
	}
	
    public static void removeFromIdCacheByInode(String inode){
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
        String key = inode;
		cache.remove(getPrimaryGroup() + key,getPrimaryGroup());
	}
    
    public static Identifier getIdentifierFromIdentifierCache(Versionable versionable) throws DotHibernateException {
    	DotCacheAdministrator cache = CacheLocator.getCacheAdministrator();
    	Identifier value = null;
    	try{
    		value = (Identifier) cache.get(getPrimaryGroup() + versionable.getVersionId(),getPrimaryGroup());
    	}catch (DotCacheException e) {
			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
    	}
	    if(value == null)
	    {
	    	Identifier ident = null;
    		ident = (Identifier)new HibernateUtil(Identifier.class).load(versionable.getVersionId());
	    	if(ident != null && InodeUtils.isSet(ident.getInode())){
	            addIdentifierToIdentifierCache(ident);
	            try{
	            	value= (Identifier) cache.get(getPrimaryGroup() + versionable.getVersionId(),getPrimaryGroup());
	            }catch (DotCacheException e) {
	    			Logger.debug(IdentifierCache.class,"Cache Entry not found", e);
	        	}
	    	}
	    }
        
        if (value == null)
            value = new Identifier ();
    	return value;
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
    	return "IdentifierCache";
    }
}

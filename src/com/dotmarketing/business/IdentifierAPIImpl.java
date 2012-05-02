package com.dotmarketing.business;

import java.util.List;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

public class IdentifierAPIImpl implements IdentifierAPI{

	private ContentletAPI conAPI;
	public IdentifierAPIImpl() {
		conAPI = APILocator.getContentletAPI();
	}
	public Identifier findFromInode(String inodeOrIdentifier) throws DotDataException  {
		Identifier ident = null;
		ident = IdentifierCache.loadFromCacheOnly(inodeOrIdentifier);
		if(ident == null || !InodeUtils.isSet(ident.getInode())){
			try{
				Contentlet con = conAPI.find(inodeOrIdentifier, APILocator.getUserAPI().getSystemUser(), false);
				if(con != null && InodeUtils.isSet(con.getInode())){
					return IdentifierCache.getIdentifierFromIdentifierCache(con);
				}
			}catch (Exception e) {
				Logger.debug(this, "Unable to find inodeOrIdentifier as content : ",e);
			}
		}else{
			return ident;
		}
		
		try {
			ident = IdentifierCache.getIdentifierFromIdentifierCache(inodeOrIdentifier);
		}catch (DotHibernateException e) {
			Logger.debug(this, "Unable to find inodeOrIdentifier as identifier : ",e);
		}
		
		if(ident == null || !InodeUtils.isSet(ident.getInode())){
			return ident = IdentifierCache.getIdentifierFromIdentifierCache(InodeFactory.getInode(inodeOrIdentifier, Inode.class));
		}else{
			return ident;
		}
	}
	
	
	public Identifier find(String identifier) throws DotDataException  {
		Identifier ident = null;
		ident = IdentifierCache.loadFromCacheOnly(identifier);
		if(ident != null && InodeUtils.isSet(ident.getInode())){
			return ident;
		}

		ident = (Identifier) InodeFactory.getInode(identifier, Identifier.class);
		if(ident != null && InodeUtils.isSet(ident.getInode())){
			IdentifierCache.addIdentifierToIdentifierCache(ident);
			return ident;
		}
		else{
			throw new DotDataException("No such identifier : " + identifier);
		}


		
		
		
	}
	
	
	
	
	
	public boolean isIdentifier(String identifierInode) throws DotDataException {
		return IdentifierFactory.isIdentifier(identifierInode);
	}
	
	public List findVersionsandLiveandWorkingChildrenOfClass(Inode o, Class c) {
		List versionsList = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(o, c);
		return versionsList;
	}
}

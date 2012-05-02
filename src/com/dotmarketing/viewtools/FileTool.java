package com.dotmarketing.viewtools;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

public class FileTool implements ViewTool {

	public void init(Object initData) {

	}

	public File getNewFile(){
		return new File();
	}
	
	public File getFile(String identifier, boolean live){
		Identifier id;
		try {
			id = IdentifierCache.getIdentifierFromIdentifierCache(identifier);
		} catch (DotHibernateException e1) {
			Logger.error(FileTool.class,e1.getMessage(),e1);
			return new File();
		}
		String p = null;
		if(live){
			p = LiveCache.getPathFromCache(id.getURI(), id.getHostId());
		}else{
			p = WorkingCache.getPathFromCache(id.getURI(), id.getHostId());
		}
        p = p.substring(5, p.lastIndexOf("."));
        File file = null;
		try {
			file = FileCache.getFileByInode(p);
		} catch (DotHibernateException e) {
			Logger.error(FileTool.class,e.getMessage(),e);
		}
        if(file == null){
        	file = new File();
        }
        return file;
	}
	
	public String getURI(File file){
		if(file != null && InodeUtils.isSet(file.getIdentifier())){
			return UtilMethods.espaceForVelocity("/dotAsset/" + file.getIdentifier() + "." + file.getExtension());
		}else{
			return "";
		}
	}
	
}

package com.dotmarketing.viewtools;


import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Logger;

public class WebsiteWebAPI implements ViewTool {
	
    public void init(Object obj) {
    }
    
    @Deprecated
    public Folder getFolder (String parentFolder, long hostId) {
    	
    	try {
			return getFolder(parentFolder,String.valueOf(hostId));
		} catch (Exception e) {
			Logger.error(this, "Website getFolder Method : Unable to parse to String " ,e);
	    }
		return null;
    }
    
    public Folder getFolder (String parentFolder, String hostId) {
        Folder folder = FolderFactory.getFolderByPath(parentFolder, hostId);
        return folder;
    }
    
    @Deprecated
    public List<Folder> getSubFolders (String parentFolder, long hostId) {
        
        try {
			List<Folder> subFolders = getSubFolders (parentFolder, String.valueOf(hostId));
			return subFolders;
		} catch (Exception e) {
			Logger.error(this, "Website getSubFolders Method : Unable to parse to String " ,e);
		}
		return new ArrayList<Folder>();
    }
    
    public List<Folder> getSubFolders (String parentFolder, String hostId) {
        Folder folder = FolderFactory.getFolderByPath(parentFolder, hostId);
        List<Folder> subFolders = FolderFactory.getFoldersByParentSortByTitle(folder.getInode());
        return subFolders;
    }

    public List<Folder> getSubFolders (Folder parentFolder) {
        List<Folder> subFolders = FolderFactory.getFoldersByParentSortByTitle(parentFolder.getInode());
        return subFolders;
    }
}
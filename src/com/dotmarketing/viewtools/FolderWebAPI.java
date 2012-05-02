package com.dotmarketing.viewtools;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.model.Folder;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class FolderWebAPI implements ViewTool{

	private FolderAPI folderAPI;
	public void init(Object arg0) {
		folderAPI = new FolderAPI();
	}
	
	public List<Inode> findMenuItems(String path, HttpServletRequest req) throws PortalException, SystemException, DotDataException, DotSecurityException{
		Host host = WebAPILocator.getHostWebAPI().getCurrentHost(req);
		return findMenuItems(folderAPI.findFolderByPath(path, host.getIdentifier()));
	}
	
	public List<Inode> findMenuItems(String folderInode){
		return findMenuItems(folderAPI.find(folderInode));
	}
	
	@Deprecated
	public List<Inode> findMenuItems(long folderInode){
		return findMenuItems(String.valueOf(folderInode));
	}
	
	public List<Inode> findMenuItems(Folder folder){
		return folderAPI.findMenuItems(folder);
	}
	
	public Folder findCurrentFolder(String path, Host host){
		return folderAPI.findFolderByPath(path, host.getIdentifier());
	}
}

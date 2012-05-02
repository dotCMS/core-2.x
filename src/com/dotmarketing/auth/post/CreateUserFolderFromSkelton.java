package com.dotmarketing.auth.post;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.IdentifierAPIImpl;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.PublishFactory;
import com.dotmarketing.portlets.contentlet.business.HostAPIImpl;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.PropsUtil;

/**
 * Use this Post Login Hook to create a folder with the name of the user's userId.  it will create the folder and copy all HTMLPages into that folder.
 * It will also copy any sub folders with their HTMLPages and so on. The following parameters must be set in the portal-ext.properties file to use this hook.
 * 
 * The folders should begin and end with a / ie... /path/
 * 1. auth.post.CreateUserFolderFromSkelton.skeleton  this should be the path of the skeleton folder to copy
 * 2. auth.post.CreateUserFolderFromSkelton.destination this is the path where the new directory with the user's userId should be placed
 * 
 * This is an optional Parameter
 * 1. auth.post.CreateUserFolderFromSkelton.host this is the hostname of the host the folder is in.  If you do not specify a host it will use the default
 * @author jtesser
 *
 */
public class CreateUserFolderFromSkelton extends Action {
	 public String SKELETON = PropsUtil.get("auth.post.CreateUserFolderFromSkelton.skeleton");
	 public String DESTINATION = PropsUtil.get("auth.post.CreateUserFolderFromSkelton.destination");
	 public String ROLE = PropsUtil.get("auth.post.CreateUserFolderFromSkelton.role");
	 private IdentifierAPIImpl identAPI;
	 private PermissionAPI perAPI;
	 
	@SuppressWarnings("unchecked")
	public void run(HttpServletRequest request, HttpServletResponse response)throws ActionException {
		try {
			HibernateUtil.startTransaction();
		
			User user = null;
			identAPI = new IdentifierAPIImpl();
			perAPI = APILocator.getPermissionAPI();
			User systemUser = APILocator.getUserAPI().getSystemUser();
			try {
				user = com.liferay.portal.util.PortalUtil.getUser(request);
			} catch (Exception e) {
				Logger.error(this, "Unabel to get user from session ", e);
				return;
			}
			if(!com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user, ROLE)){
				return;
			}
	
			HostAPIImpl hostAPI = new HostAPIImpl();
			FolderAPI folderAPI = new FolderAPI();
			String sHost = PropsUtil.get("auth.post.CreateUserFolderFromSkelton.host");
			Host host;
			Folder toFolder = new Folder();
			Folder skelFolder;
			
			if(UtilMethods.isSet(sHost)){
				host = hostAPI.findByName(sHost, systemUser, false);
			}else{
				host = hostAPI.findDefaultHost(systemUser, false);
			}
			skelFolder = folderAPI.findFolderByPath(SKELETON, host.getIdentifier());
			Folder userFolder = folderAPI.findFolderByPath(DESTINATION, host.getIdentifier());
			
			if(folderAPI.doesFolderExist(DESTINATION + user.getUserId() + "/", host.getIdentifier())){
				Logger.info(this, "Folder already exists for " + user.getUserId());
				List<Inode> relatedAssets = new ArrayList();
				relatedAssets = PublishFactory.getUnpublishedRelatedAssets(skelFolder, relatedAssets, false, APILocator.getUserAPI().getSystemUser(), false);
	//			for (Inode asset : relatedAssets) {
	//				if(asset instanceof WebAsset){
	//					identAPI.getIdentifier(asset.getIdentifier()).setOwner(user.getUserId());
	//				}else{
	//					asset.setOwner(user.getUserId());
	//				}
	//				try{
	//					HibernateUtil.save(asset);
	//				}catch(DotHibernateException dhe){
	//					Logger.error("Unable to save asset : ", dhe);
	//				}
	//			}
				return;
			}
			
			
			toFolder.setOwner(user.getUserId());
			toFolder.setHostId(host.getIdentifier());
			toFolder.setTitle(user.getUserId());
			toFolder.setName(user.getUserId());
			toFolder.setPath(DESTINATION + user.getUserId() + "/");
			folderAPI.save(toFolder);
			userFolder.addChild(toFolder);
			folderAPI.save(userFolder);
			List<Folder> folders = folderAPI.findSubFolders(skelFolder);
			
			for (Folder folder : folders) {
				folderAPI.copyFolder(folder, toFolder);
			}
			List<Inode> relatedAssets = new ArrayList<Inode>();
			relatedAssets = PublishFactory.getUnpublishedRelatedAssets(toFolder, relatedAssets, false, APILocator.getUserAPI().getSystemUser(), false);
			
			for (Inode asset : relatedAssets) {
				if(asset instanceof WebAsset){
					IdentifierCache.getIdentifierFromIdentifierCache(asset).setOwner(user.getUserId());
				}else{
					asset.setOwner(user.getUserId());
				}
				try{
					HibernateUtil.save(asset);
				}catch(DotHibernateException dhe){
					Logger.error(this, "Unable to save asset : ", dhe);
				}
			}
			
			HibernateUtil.commitTransaction();
			
		} catch (DotDataException e) {
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.error(this, e1.getMessage(), e1);
			}
			Logger.error(this, e.getMessage(), e);
			throw new ActionException(e);
		} catch (DotSecurityException e) {
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.error(this, e1.getMessage(), e1);
			}
			Logger.error(this, e.getMessage(), e);
			throw new ActionException(e);
		}
	}
}

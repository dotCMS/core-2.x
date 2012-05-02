package com.dotmarketing.portlets.folders.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.dotcms.enterprise.cmis.QueryResult;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.BaseInodeAPI;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.business.GenericAPI;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.query.QueryUtil;
import com.dotmarketing.business.query.ValidationException;
import com.dotmarketing.business.query.GenericQueryFactory.Query;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.FolderCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.business.FileAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.business.HTMLPageAPI;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class FolderAPI extends BaseInodeAPI implements GenericAPI<Folder> {
	
	public static final String SYSTEM_FOLDER_ID = FolderFactory.SYSTEM_FOLDER;

	/**
	 * Will get a folder for you on a given path for a particular host 
	 * @param path
	 * @param hostId
	 * @return
	 */
	public Folder findFolderByPath(String path, String hostId) {
		return FolderFactory.getFolderByPath(path, hostId);
	}
	
	/**
	 * Will get a folder for you on a given path for the default host 
	 * @param path
	 * @param hostId
	 * @return
	 */
	public Folder findFolderByPath(String path) {
		try {
			HostAPI hostAPI = APILocator.getHostAPI();
			User systemUser = APILocator.getUserAPI().getSystemUser();
			Host defaultHost = hostAPI.findDefaultHost(systemUser, false);
			return findFolderByPath(path, defaultHost.getIdentifier());
		} catch (DotDataException e) {
			Logger.error(FolderAPI.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(FolderAPI.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	} 
	
	/**
	 * 
	 * @param folder to get HTMLPages for
	 * @return a List of all live HTMLPages
	 */
	public List<HTMLPage> findLiveHTMLPages(Folder folder){
		HTMLPageAPI htmlPageAPi = APILocator.getHTMLPageAPI();
		return htmlPageAPi.findLiveHTMLPages(folder);
	}
	
	/**
	 * 
	 * @param folder
	 * @return List of sub folders for passed in folder
	 */
	public List<Folder> findSubFolders(Folder folder){
		return FolderFactory.getFoldersByParent(folder.getInode());
	}
	
	
	/**
	 * 
	 * @param folder
	 * @return List of sub folders for passed in folder
	 */
	public List<Folder> findSubFolders(Host host){
		return FolderFactory.getFoldersByParent(host.getIdentifier());
	}
	
	/**
	 * 
	 * @param folder Recursively
	 * @return List of sub folders for passed in folder
	 */
	public List<Folder> findSubFoldersRecursively(Folder folder){
		List<Folder> subFolders = FolderFactory.getFoldersByParent(folder.getInode());
		List<Folder> toIterateOver = new ArrayList<Folder>(subFolders);
		for(Folder f: toIterateOver) {
			subFolders.addAll(findSubFoldersRecursively(f));
		}
		return subFolders;
	}
	
	/**
	 * Retrieves the parent folder of the given folder, or null if it is a top folder
	 * @param subfolder
	 * @return
	 */
	public Folder findParentFolder(Folder subfolder) {
		Tree tree = TreeFactory.getTreeByChildAndRelationType(subfolder, "child");
		try {
			Folder parent = find(tree.getParent());
			if(InodeUtils.isSet(parent.getInode()))
				return parent;
		} catch (ClassCastException e) {
			//Parent not a folder a host instead so this is a top level folder
		}
		return null;
	}
	
	/**
	 * 
	 * @param folder
	 * @return List of sub folders for passed in folder
	 */
	public List<Folder> findSubFoldersRecursively(Host host){
		List<Folder> subFolders = FolderFactory.getFoldersByParent(host.getIdentifier());
		List<Folder> toIterateOver = new ArrayList<Folder>(subFolders);
		for(Folder f: toIterateOver) {
			subFolders.addAll(findSubFoldersRecursively(f));
		}
		return subFolders;
	}
	
	/**
	 * Will copy a folder to a new location with all it contains.  
	 * @param folderToCopy
	 * @param newParentFolder
	 * @throws DotDataException 
	 */
	public void copyFolder(Folder folderToCopy, Folder newParentFolder) throws DotDataException{
		FolderFactory.copyFolder(folderToCopy, newParentFolder);
	}
	
	public boolean doesFolderExist(String path, String hostId){
		Folder folder = FolderFactory.getFolderByPath(path, hostId);
		return InodeUtils.isSet(folder.getInode()) ? true:false;
	}
	
	@SuppressWarnings("unchecked")
	public List<Inode> findMenuItems(Folder folder){
		return FolderFactory.getMenuItems(folder);
	}
	
	/**
	 * Takes a folder and a user and deletes all underneith assets
	 * User needs edit permssions on folder to delete everything undernieth
	 * @param folder
	 * @param user
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public void delete(Folder folder, User user) throws DotDataException, DotSecurityException {

		boolean localTransaction = false;
		try{
			localTransaction =	 DbConnectionFactory.getConnection().getAutoCommit();
		}
		catch(Exception e){
			throw new DotDataException(e.getMessage());
		}
		if(localTransaction){
			HibernateUtil.startTransaction();
		}
		
		
		// start transactional delete
		try{
		
			PermissionAPI	papi = APILocator.getPermissionAPI();
			if(!papi.doesUserHavePermission(folder, PermissionAPI.PERMISSION_EDIT_PERMISSIONS, user)){
				Logger.error(this.getClass(), "User " + user.getUserId() + " does not have permissions to folder " + folder.getInode());
				HibernateUtil.rollbackTransaction();
				// this is not a data exception, but we don't have an interface.
				throw new DotSecurityException("User "+"does not have edit permissions on folder " + folder.getPath());
			}
			
			Folder faker = new Folder();
			faker.setShowOnMenu(folder.isShowOnMenu());
			faker.setInode(folder.getInode());

			List<Folder> folderChildren = (List<Folder>)InodeFactory.getChildrenClass(folder, Folder.class);

			// recursivily delete
			for (Folder childFolder : folderChildren) {
				// sub deletes use system user - if a user has rights to parent
				// permission (checked above) they can delete to children
				delete(childFolder, APILocator.getUserAPI().getSystemUser());
			}
			
			//delete assets in this folder
			_deleteChildrenAssetsFromFolder(folder);
			APILocator.getPermissionAPI().removePermissions(folder);
			
			//http://jira.dotmarketing.net/browse/DOTCMS-6362
			APILocator.getContentletAPIImpl().removeFolderReferences(folder);
			
			// delete folder itself
			FolderFactory.delete(folder);
			
			
	
			//delete the menus using the fake proxy inode
			if (folder.isShowOnMenu()) {
				//RefreshMenus.deleteMenus();
				RefreshMenus.deleteMenu(faker);
			}
			

		}
		catch(Exception e){
			Logger.error(this.getClass(), e.getMessage(),e);
      		HibernateUtil.rollbackTransaction();
    		throw new DotDataException(e.getMessage());
		}
		
		if(localTransaction){
			HibernateUtil.commitTransaction();
		}

	}
	

	
	private static void _deleteChildrenAssetsFromFolder(Folder folder)  {

		try{
			PermissionAPI perAPI =  APILocator.getPermissionAPI();
			
			ContentletAPI capi = APILocator.getContentletAPI();
			User sys = APILocator.getUserAPI().getSystemUser();
			
			
			/************ conList *****************/
			List<Contentlet> conList = capi.findContentletsByFolder(folder, sys , false);
			for(Contentlet c : conList){
				capi.delete(c, sys, false);
			}
			
			/************ htmlPages *****************/
			List<HTMLPage> htmlPages = InodeFactory.getChildrenClass(folder, HTMLPage.class);
			for (HTMLPage page: htmlPages) {
				APILocator.getHTMLPageAPI().delete(page, sys, false);
			}
				
			/************ Files *****************/
			List<File> files = InodeFactory.getChildrenClass(folder, File.class);
			for (File file: files) {
				APILocator.getFileAPI().delete(file, sys, false);
			}
	
			/************ Links *****************/
			List<Link> links = InodeFactory.getChildrenClass(folder, Link.class);
			for (Link link: links) {
				if (link.isWorking()) {
	
					Identifier identifier = IdentifierFactory.getIdentifierByInode(link);
	
	                if(!InodeUtils.isSet(identifier.getInode())) {
	                    Logger.warn(FolderFactory.class, "_deleteChildrenAssetsFromFolder: link inode = " + link.getInode() + 
	                            " doesn't have a valid identifier associated.");
	                    continue;
	                }
	
	                perAPI.removePermissions(link);
	
		            List<Link> versions = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(identifier, Link.class);
		            
		            for (Link version : versions) {
						perAPI.removePermissions(version);
						TreeFactory.deleteTreesByChild(version);
						HibernateUtil.delete(version);
					}
					perAPI.removePermissions(identifier);
					HibernateUtil.delete(identifier);
				}
			}
			
			/************ Structures *****************/
			StructureFactory.updateFolderReferences(folder);
		}
		catch(Exception e){
			Logger.error(FolderAPI.class, e.getMessage(), e);
			throw new DotStateException(e.getMessage());
			
		}
		

		

	}
	
	

	/**
	 * @param id the inode or id of the folder
	 * @return Folder with a given id or inode 
	 */
	public Folder find(String id) {
		Folder f = FolderFactory.getFolderByInode(id);
		return f;
	}

	/**
	 * Not Implemanted Yet
	 * @deprecated
	 */
	public List<Folder> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Saves a folder
	 * @throws DotDataException 
	 */
	public void save(Folder folder) throws DotDataException {
		saveInode(folder);

	}
	//http://jira.dotmarketing.net/browse/DOTCMS-3232
	public Folder findSystemFolder() throws DotDataException 
	{
		return FolderFactory.getSystemFolder();
	}
	
	
	//http://jira.dotmarketing.net/browse/DOTCMS-3392
	@SuppressWarnings("unchecked")
	public  java.util.List findFolderItems(Folder parentFolder,
			String condition) {
		List menuList = FolderFactory.getFolderItems(parentFolder, condition);
		
		return menuList;
	}
	
	public Folder createFolders(String path, Host host) throws DotDataException {
		
		StringTokenizer st = new StringTokenizer(path, "/");
		StringBuffer sb = new StringBuffer("/");
		
		Folder parent = null;
		
		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			sb.append(name + "/");
			Folder f = findFolderByPath(sb.toString(), host.getIdentifier());
			if (!InodeUtils.isSet(f.getInode())) {
				f.setName(name);
				f.setTitle(name);
				f.setPath(sb.toString());
				f.setShowOnMenu(false);
				f.setSortOrder(0);
				f.setHostId(host.getIdentifier());
				InodeFactory.saveInode(f);
				if (parent == null) {
					TreeFactory.saveTree(new Tree(host.getIdentifier(), f.getInode()));
				} else {
					TreeFactory.saveTree(new Tree(parent.getInode(), f.getInode()));
					InodeFactory.saveInode(parent);
				}

			}
			
			parent = f;
			
		}
		return parent;
		
	}	
	
	public List<Map<String, Serializable>> DBSearch(Query query, User user,boolean respectFrontendRoles) throws ValidationException,DotDataException {
		Map<String, String> dbColToObjectAttribute = new HashMap<String, String>();

		if(UtilMethods.isSet(query.getSelectAttributes())){
			
			if(!query.getSelectAttributes().contains("title")){
				query.getSelectAttributes().add("title" + " as " + QueryResult.CMIS_TITLE);
			}
		}else{
			List<String> atts = new ArrayList<String>();
			atts.add("*");
			atts.add("title" + " as " + QueryResult.CMIS_TITLE);
			query.setSelectAttributes(atts);
		}
				
		return QueryUtil.DBSearch(query, dbColToObjectAttribute, null, user, false,respectFrontendRoles);
	}

	/**
	 * @deprecated
	 * Not implemented
	 * because it does not take a user
	 */
	
	public void delete(Folder object) throws DotDataException {
		throw new DotDataException("This is not implemented.  Use delete(Folder, User)");
		
	}

}

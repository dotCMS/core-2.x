package com.dotmarketing.factories;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.MultiTree;
import com.dotmarketing.beans.PermissionAsset;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.business.Role;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.exception.WebAssetException;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.containers.business.ContainerAPI;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.business.FileAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.business.HTMLPageAPI;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.business.MenuLinkAPI;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.business.TemplateAPI;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.services.ContainerServices;
import com.dotmarketing.services.PageServices;
import com.dotmarketing.services.TemplateServices;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PaginatedArrayList;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.business.NoSuchUserException;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 *
 * @author maria, david(2005)
 */
public class WebAssetFactory {

	public enum Direction {
		PREVIOUS,
		NEXT
	};
	
	public enum AssetType {
		HTMLPAGE("HTMLPAGE"),
		FILE_ASSET("FILE_ASSET"),
		CONTAINER("CONTAINER"),
		TEMPLATE("TEMPLATE"),
		LINK("LINK")
		;
		
		private String value;
		
		AssetType (String value) {
			this.value = value;
		}
		
		public String toString () {
			return value;
		}
			
		public static AssetType getObject (String value) {
			AssetType[] ojs = AssetType.values();
			for (AssetType oj : ojs) {
				if (oj.value.equals(value))
					return oj;
			}
			return null;
		}
	}
	
	
	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();
	private static HTMLPageAPI htmlPageAPI = APILocator.getHTMLPageAPI();
	private static FileAPI fileAPI = APILocator.getFileAPI();
	private static ContainerAPI containerAPI = APILocator.getContainerAPI();
	private static TemplateAPI templateAPI = APILocator.getTemplateAPI();
	private static MenuLinkAPI linksAPI = APILocator.getMenuLinkAPI();
	
	private static final int ITERATION_LIMIT = 500;
	private final static int MAX_LIMIT_COUNT = 100;

	/**
	 * @param permissionAPI the permissionAPI to set
	 */
	public static void setPermissionAPI(PermissionAPI permissionAPIRef) {
		permissionAPI = permissionAPIRef;
	}

	public static void createAsset(WebAsset webasset, String userId, Inode parent) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// create new identifier, with the URI
		Identifier id = IdentifierFactory.createNewIdentifier(webasset, (Folder) parent);
		id.setOwner(userId);
		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		InodeFactory.saveInode(id);

	}

	public static void createAsset(WebAsset webasset, String userId, Host host) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// create new identifier, without URI
		Identifier id = IdentifierFactory.createNewIdentifier(webasset, host);
		id.setOwner(userId);
		InodeFactory.saveInode(id);

	}

	public static void createAsset(WebAsset webasset, String userId, Inode parent, Identifier identifier) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);

		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		webasset.setIdentifier(identifier.getInode());


		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);

	}

	public static void createAsset(WebAsset webasset, String userId, Identifier identifier) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);

		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		webasset.setIdentifier(identifier.getInode());


		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);

	}

	public static void createAsset(WebAsset webasset, String userId, Inode parent, Identifier identifier,
			boolean working) throws DotDataException {
		webasset.setWorking(working);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);
		webasset.addParent(identifier);
		webasset.setIdentifier(identifier.getInode());

		InodeFactory.saveInode(webasset);

	}
	
	public static void createAsset(WebAsset webasset, String userId, Inode parent, Identifier identifier,
			boolean working, boolean isLive) throws DotDataException {
		webasset.setWorking(working);
		webasset.setLive(isLive);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);
		webasset.addParent(identifier);
		webasset.setIdentifier(identifier.getInode());

		InodeFactory.saveInode(webasset);

	}

	public static void createAsset(WebAsset webasset, String userId, Identifier identifier, boolean working) throws DotDataException {
		webasset.setWorking(working);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);
		webasset.addParent(identifier);
		webasset.setIdentifier(identifier.getInode());

		InodeFactory.saveInode(webasset);

	}
    
	public static void createAsset(WebAsset webasset, String userId, Inode parent, boolean isLive) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(isLive);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		InodeFactory.saveInode(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// create new identifier, with the URI
		Identifier id = IdentifierFactory.createNewIdentifier(webasset, (Folder) parent);
		id.setOwner(userId);
		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		InodeFactory.saveInode(id);

	}
	
	public static WebAsset getParentWebAsset(Inode i) {
		DotHibernate dh = new DotHibernate(WebAsset.class);
		dh.setQuery("from inode in class " + WebAsset.class.getName() + " where ? in inode.children.elements");
		dh.setParam(i.getInode());
		return (WebAsset) dh.load();
	}



	public static void renameAsset(WebAsset webasset) {
		List versions = getAssetVersionsandLive(webasset);
		Iterator versIter = versions.iterator();
		while (versIter.hasNext()) {
			WebAsset currWebAsset = (WebAsset) versIter.next();
			currWebAsset.setFriendlyName(webasset.getFriendlyName());
		}
	}

	public static boolean editAsset(WebAsset currWebAsset, String userId) {

		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
		WebAsset workingwebasset = null;

		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());

		if (!workingwebasset.isLocked()) {
			// sets lock true
			workingwebasset.setLocked(true);
			workingwebasset.setModUser(userId);
			workingwebasset.setModDate(new Date());
			// persists the webasset
			InodeFactory.saveInode(workingwebasset);
			return true;
		}
		
		User userMod = null;
		try{
			userMod = APILocator.getUserAPI().loadUserById(workingwebasset.getModUser(),APILocator.getUserAPI().getSystemUser(),false);
		}catch(Exception ex){
			if(ex instanceof NoSuchUserException){
				try {
					userMod = APILocator.getUserAPI().getSystemUser();
				} catch (DotDataException e) {
					Logger.error(WebAssetFactory.class,e.getMessage(),e);
				}
			}
		}
		
		if ((workingwebasset.isLocked()) && (userMod.getUserId().equals(userId))) {
			return true;
		}
		return false;
	}

	public static WebAsset getBackAssetVersion(WebAsset versionWebAsset) throws Exception {
		Identifier id = (Identifier) IdentifierFactory.getParentIdentifier(versionWebAsset);
		if (!InodeUtils.isSet(id.getInode())) {
			throw new Exception("Web asset Identifier not found!");
		}
		WebAsset working = (WebAsset) IdentifierFactory.getWorkingChildOfClass(id, versionWebAsset.getClass());
		if (!InodeUtils.isSet(working.getInode())) {
			throw new Exception("Working copy not found!");
		}
		return swapAssets(working, versionWebAsset);
	}

	/**
	 * This method is odd. You send it an asset, but that may not be the one
	 * that get published. The method will get the identifer of the asset you
	 * send it and find the working version of the asset and make that the live
	 * version.
	 *
	 * @param currWebAsset
	 *            This asset's identifier will be used to find the "working"
	 *            asset.
	 * @return This method returns the OLD live asset or null. Wierd.
	 */
	@SuppressWarnings("unchecked")
	public static WebAsset publishAsset(WebAsset currWebAsset) throws WebAssetException {

		Logger.debug(WebAssetFactory.class, "Publishing asset!!!!");
		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
		// gets the current working asset

		WebAsset workingwebasset = null;

		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());
		
		if (!InodeUtils.isSet(workingwebasset.getInode())) {
			workingwebasset = currWebAsset;
		}

		Logger.debug(WebAssetFactory.class, "workingwebasset=" + workingwebasset.getInode());

		List<WebAsset> livewebassets = new ArrayList<WebAsset>();

		try {
			// gets the current working asset
			livewebassets = (List<WebAsset>) IdentifierFactory.getLiveChildrenOfClass(identifier, currWebAsset.getClass());
		
		} catch (Exception e) {
		}
		if(workingwebasset.isDeleted()){
			throw new WebAssetException("You may not publish deleted assets!!!");
		}
		for (WebAsset livewebasset : livewebassets)
			if ((livewebasset != null) && (InodeUtils.isSet(livewebasset.getInode()))
					&& (livewebasset.getInode() != workingwebasset.getInode())) {

				Logger.debug(WebAssetFactory.class, "livewebasset.getInode()=" + livewebasset.getInode());
				// sets previous live to false
				livewebasset.setLive(false);
				livewebasset.setModDate(new java.util.Date());
				// removes from the folder
				java.util.List parents = InodeFactory.getParentsOfClass(livewebasset, Folder.class);
				java.util.Iterator parentsIter = parents.iterator();
				while (parentsIter.hasNext()) {
					Inode inode = (Inode) parentsIter.next();
					inode.deleteChild(livewebasset);
				}
				identifier.addChild(livewebasset);

				// persists it
				InodeFactory.saveInode(livewebasset);
			}
		// sets new working to live
		workingwebasset.setLive(true);
		workingwebasset.setModDate(new java.util.Date());
		
		// persists the webasset
		InodeFactory.saveInode(workingwebasset);
		
		Logger.debug(WebAssetFactory.class, "InodeFactory.saveInode(workingwebasset)");

		
		return livewebassets.size()>0?livewebassets.get(0):null;
	}
	
	
	/**
	 * This method is odd. You send it an asset, but that may not be the one
	 * that get published. The method will get the identifer of the asset you
	 * send it and find the working version of the asset and make that the live
	 * version.
	 *
	 * @param currWebAsset
	 *            This asset's identifier will be used to find the "working"
	 *            asset.
	 * @param user           
	 * @return This method returns the OLD live asset or null. Wierd.
	 * @throws DotHibernateException 
	 */
	@SuppressWarnings("unchecked")
	public static WebAsset publishAsset(WebAsset currWebAsset, User user) throws WebAssetException, DotHibernateException {

		return publishAsset(currWebAsset,user,true);
	}
	
	/**
	 * This method is odd. You send it an asset, but that may not be the one
	 * that get published. The method will get the identifer of the asset you
	 * send it and find the working version of the asset and make that the live
	 * version.
	 *
	 * @param currWebAsset
	 *            This asset's identifier will be used to find the "working"
	 *            asset.
	 * @param user   
	 * @param isNewVersion - if passed false then the webasset's mod user and mod date will NOT be altered. @see {@link ContentletAPI#checkinWithoutVersioning(Contentlet, java.util.Map, List, List, User, boolean)}checkinWithoutVersioning.        
	 * @return This method returns the OLD live asset or null. Wierd.
	 * @throws DotHibernateException 
	 */
	@SuppressWarnings("unchecked")
	public static WebAsset publishAsset(WebAsset currWebAsset, User user, boolean isNewVersion) throws WebAssetException, DotHibernateException {

		Logger.debug(WebAssetFactory.class, "Publishing asset!!!!");
		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
		// gets the current working asset

		WebAsset workingwebasset = null;

		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());
		
		if (!InodeUtils.isSet(workingwebasset.getInode())) {
			workingwebasset = currWebAsset;
		}

		Logger.debug(WebAssetFactory.class, "workingwebasset=" + workingwebasset.getInode());

		List<WebAsset> livewebassets = new ArrayList<WebAsset>();

		try {
			// gets the current working asset
			livewebassets = (List<WebAsset>) IdentifierFactory.getLiveChildrenOfClass(identifier, currWebAsset.getClass());
		
		} catch (Exception e) {
		}
		if(workingwebasset.isDeleted()){
			throw new WebAssetException("You may not publish deleted assets!!!");
		}
		
		HibernateUtil.startTransaction();
		try{
			for (WebAsset livewebasset : livewebassets)
				if ((livewebasset != null) && (InodeUtils.isSet(livewebasset.getInode()))
						&& (livewebasset.getInode() != workingwebasset.getInode())) {

					Logger.debug(WebAssetFactory.class, "livewebasset.getInode()=" + livewebasset.getInode());
					// sets previous live to false
					livewebasset.setLive(false);
					if(isNewVersion){
					  livewebasset.setModDate(new java.util.Date());
					  livewebasset.setModUser(user.getUserId());
					}
					// removes from the folder
					java.util.List parents = InodeFactory.getParentsOfClass(livewebasset, Folder.class);
					java.util.Iterator parentsIter = parents.iterator();
					while (parentsIter.hasNext()) {
						Inode inode = (Inode) parentsIter.next();
						inode.deleteChild(livewebasset);
					}
					identifier.addChild(livewebasset);

					// persists it
					InodeFactory.saveInode(livewebasset);
				}
			// sets new working to live
			workingwebasset.setLive(true);
			if(isNewVersion){
				workingwebasset.setModDate(new java.util.Date());
				workingwebasset.setModUser(user.getUserId());
			}
			// persists the webasset
			InodeFactory.saveInode(workingwebasset);
			HibernateUtil.commitTransaction();
		}catch(Exception e){
			HibernateUtil.rollbackTransaction();
		}
		
		Logger.debug(WebAssetFactory.class, "InodeFactory.saveInode(workingwebasset)");

		
		return livewebassets.size()>0?livewebassets.get(0):null;
	}

	public static WebAsset getLiveAsset(WebAsset currWebAsset) throws Exception {

		Logger.debug(WebAssetFactory.class, "Publishing asset!!!!");
		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);

		WebAsset livewebasset = null;

		// gets the current working asset
		livewebasset = (WebAsset) IdentifierFactory.getLiveChildOfClass(identifier, currWebAsset.getClass());
		
		return livewebasset;
	}

	public static boolean archiveAsset(WebAsset currWebAsset) throws DotDataException {
		return archiveAsset(currWebAsset, (String)null);
	}
	
	public static boolean archiveAsset(WebAsset currWebAsset, User user) throws DotDataException {
		return archiveAsset(currWebAsset, user.getUserId());
	}	
	
	public static boolean archiveAsset(WebAsset currWebAsset, String userId) throws DotDataException {

		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);

		WebAsset workingwebasset = null;

			// gets the current working asset
			workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());
		

		WebAsset live = (WebAsset) IdentifierFactory.getLiveChildOfClass(identifier, currWebAsset.getClass());
		
		//Delete the HTML Page from the Structure Detail
		if(currWebAsset instanceof HTMLPage)
		{
			List<Structure> structures = (List<Structure>) StructureFactory.getStructures();
			for(Structure structure : structures)
			{
				if(structure.getDetailPage() == identifier.getInode())
				{
					structure.setDetailPage("");
					StructureFactory.saveStructure(structure);
				}
			}
		}		
        
		else if (currWebAsset instanceof File)
		{
         RefreshMenus.deleteMenu(currWebAsset);
		}
		
		User userMod = null;
		try{
			userMod = APILocator.getUserAPI().loadUserById(workingwebasset.getModUser(),APILocator.getUserAPI().getSystemUser(),false);
		}catch(Exception ex){
			if(ex instanceof NoSuchUserException){
				try {
					userMod = APILocator.getUserAPI().getSystemUser();
				} catch (DotDataException e) {
					Logger.error(WebAssetFactory.class,e.getMessage(),e);
				}
			}
		}
		if(userMod!=null){
		   workingwebasset.setModUser(userMod.getUserId());
		}
		
		
		if (userId == null || !workingwebasset.isLocked() || workingwebasset.getModUser().equals(userId)) {

			if (InodeUtils.isSet(live.getInode())) {
				live.setLive(false);
				live.setModDate(new Date ());
				InodeFactory.saveInode(live);
			}

			//Reset the mod date
			workingwebasset.setModDate(new Date ());
			// sets deleted to true
			workingwebasset.setDeleted(true);
			// persists the webasset
			InodeFactory.saveInode(workingwebasset);

			return true;
		}
		return false;
	}

	public static boolean deleteAssetVersion(WebAsset currWebAsset) {

		if (!currWebAsset.isLive() && !currWebAsset.isWorking()) {
			// it's a version so delete from database
			InodeFactory.deleteInode(currWebAsset);
			return true;
		}
		return false;

	}

	public static void unLockAsset(WebAsset currWebAsset) throws DotDataException {

		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
		WebAsset workingwebasset = null;
		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());
		
		// unlocks current working asset
		workingwebasset.setLocked(false);
		
		
		User modUser = null;
		try{
			modUser = APILocator.getUserAPI().loadUserById(workingwebasset.getModUser(),APILocator.getUserAPI().getSystemUser(),false);
		}catch(Exception ex){
			if(ex instanceof NoSuchUserException){
				modUser = APILocator.getUserAPI().getSystemUser(); 
			}
		}
		if(modUser!=null){
		   workingwebasset.setModUser(modUser.getUserId());
		}
		// persists the webasset
		InodeFactory.saveInode(workingwebasset);
	}

	public static void unArchiveAsset(WebAsset currWebAsset) throws DotDataException {

		RefreshMenus.deleteMenu(currWebAsset);
		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
		// gets the current working asset
		WebAsset workingwebasset = null;

		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());
		

		WebAsset livewebasset = null;
		try {
			// gets the current working asset
			livewebasset = (WebAsset) IdentifierFactory.getLiveChildOfClass(identifier, currWebAsset.getClass());	
		} catch (Exception e) {
		}

		// sets deleted to true
		workingwebasset.setDeleted(false);
		// persists the webasset
		InodeFactory.saveInode(workingwebasset);

		if ((livewebasset != null) && (InodeUtils.isSet(livewebasset.getInode()))
				&& (livewebasset.getInode() != workingwebasset.getInode())) {

			// sets previous live to false
			livewebasset.setDeleted(false);
			// persists it
			InodeFactory.saveInode(livewebasset);
		}
	}

	public static boolean unPublishAsset(WebAsset currWebAsset, String userId, Inode parent) {
		ContentletAPI conAPI = APILocator.getContentletAPI();
		HostAPI hostAPI = APILocator.getHostAPI();
		
		// gets the identifier for this asset
		Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);

		WebAsset workingwebasset = null;

		// gets the current working asset
		workingwebasset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(identifier, currWebAsset.getClass());

		WebAsset livewebasset = null;
		
		User modUser = null;
		try{
			modUser = APILocator.getUserAPI().loadUserById(workingwebasset.getModUser(),APILocator.getUserAPI().getSystemUser(),false);
		}catch(Exception ex){
			if(ex instanceof NoSuchUserException){
				try {
					modUser = APILocator.getUserAPI().getSystemUser();
				} catch (DotDataException e) {
					Logger.error(WebAssetFactory.class,e.getMessage(),e);
				} 
			}
		}
		if(modUser!=null){
		   workingwebasset.setModUser(modUser.getUserId());
		}

		if (!workingwebasset.isLocked() || workingwebasset.getModUser().equals(userId)) {
			try {
				// gets the current working asset
				
				// gets the current working asset
				livewebasset = (WebAsset) IdentifierFactory
				.getLiveChildOfClass(identifier, currWebAsset.getClass());
				
				livewebasset.setLive(false);
				livewebasset.setWorking(true);
				livewebasset.setModDate(new java.util.Date());
				livewebasset.setModUser(userId);
				InodeFactory.saveInode(livewebasset);

				if ((livewebasset.getInode() != workingwebasset.getInode())) {

					// sets previous working to false
					workingwebasset.setWorking(false);
					workingwebasset.setLocked(false);
					// persists it
					InodeFactory.saveInode(workingwebasset);
					// removes from folder or parent inode
					if(parent != null)
						parent.deleteChild(workingwebasset);
				}

				if (currWebAsset instanceof HTMLPage) {
					//remove page from the live directory
					PageServices.unpublishPageFile((HTMLPage)currWebAsset);

					//Refreshing the menues
					//RefreshMenus.deleteMenus();
					RefreshMenus.deleteMenu(currWebAsset);

				} else if (currWebAsset instanceof Container) {
					//remove container from the live directory
					ContainerServices.unpublishContainerFile((Container)currWebAsset);
				} else if (currWebAsset instanceof Template) {
					//remove template from the live directory
					TemplateServices.unpublishTemplateFile((Template)currWebAsset);
				} else if( currWebAsset instanceof Link ) {
					// Removes static menues to provoke all possible dependencies be generated.
					if( parent instanceof Folder ) {
						Folder parentFolder = (Folder)parent;			
						Host host = hostAPI.findParentHost(parentFolder, APILocator.getUserAPI().getSystemUser(), false);
						RefreshMenus.deleteMenu(host);
					}
				} else if (currWebAsset instanceof File) {
				    RefreshMenus.deleteMenu(currWebAsset);
				   }
				
				

				LiveCache.removeAssetFromCache(currWebAsset);

				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	// TO-DO
	// Do this one with a language condition...
	public static java.util.List getAssetVersions(WebAsset currWebAsset) {
		// gets the identifier for this asset
		if (currWebAsset.isWorking()) {
			Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
			return IdentifierFactory.getVersionsChildrenOfClass(identifier, currWebAsset.getClass());
		}
		return new java.util.ArrayList();
	}

	/*
	 * public static java.util.List getWorkingAssetsOfClass(Class c) { return
	 * IdentifierFactory.getLiveOfClass(c); }
	 */

	// TO-DO
	// Do this one with a language condition.
	public static java.util.List getAssetVersionsandLive(WebAsset currWebAsset) {
		// gets the identifier for this asset
		if (currWebAsset.isWorking()) {
			Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
			return IdentifierFactory.getVersionsandLiveChildrenOfClass(identifier, currWebAsset.getClass());
		}
		return new java.util.ArrayList();
	}

	// Do this one with a language condition.
	public static java.util.List getAssetVersionsandLiveandWorking(WebAsset currWebAsset) {
		// gets the identifier for this asset
		if (currWebAsset.isWorking()) {
			Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);
			return IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(identifier, currWebAsset.getClass());
		}
		return new java.util.ArrayList();
	}

	// Swap assets properties and tree relationships to convert the newAsset
	// into the workingAsset
	// This method don�t swap the multitree relationships and correctly set the
	// working/live and parent folder
	// relationships and properties
	@SuppressWarnings("deprecation")
	private static WebAsset swapAssets(WebAsset workingAsset, WebAsset newAsset) throws Exception {
		Folder parentFolder = null;
		if (!isAbstractAsset(workingAsset)){
			parentFolder = (Folder) InodeFactory.getParentOfClass(workingAsset, Folder.class);
		}
		Identifier identifier = (Identifier) IdentifierFactory.getIdentifierByInode(workingAsset);
		

		// Retrieving assets properties excluding (inode, children, parents and
		// parent)
		Map workingAssetProps = PropertyUtils.describe(workingAsset);
		workingAssetProps.remove("class");
		workingAssetProps.remove("inode");
		workingAssetProps.remove("children");
		workingAssetProps.remove("parents");
		workingAssetProps.remove("parent");

		Map newAssetProps = PropertyUtils.describe(newAsset);
		newAssetProps.remove("class");
		newAssetProps.remove("inode");
		newAssetProps.remove("children");
		newAssetProps.remove("parents");
		newAssetProps.remove("parent");

		boolean newAssetLive = newAsset.isLive();

		// Swaping props
		Iterator keys = workingAssetProps.keySet().iterator();
		while (keys.hasNext()) {
			try {
				String key = (String) keys.next();
				Object x = workingAssetProps.get(key);
				if(x != null && !key.equalsIgnoreCase("working")&& !key.equalsIgnoreCase("live")){
					PropertyUtils.setProperty(newAsset, key, x);
				}
			} catch (NoSuchMethodException e) {
			} catch (InvocationTargetException e) {
			}

		}
		keys = newAssetProps.keySet().iterator();
		while (keys.hasNext()) {
			try {
				String key = (String) keys.next();
				Object x = newAssetProps.get(key);
				if(x!=null && !key.equalsIgnoreCase("working")&& !key.equalsIgnoreCase("live")){
					PropertyUtils.setProperty(workingAsset, key, x);
				}
			} catch (NoSuchMethodException e) {
			} catch (InvocationTargetException e) {
			}
		}

		// Setting working/live/locked/date/user properties
		workingAsset.setWorking(true);
		workingAsset.setLive(newAssetLive);
		workingAsset.setDeleted(false);
		workingAsset.setLocked(false);
		workingAsset.setModDate(new java.util.Date());

		newAsset.setWorking(false);
		newAsset.setLocked(false);

		if (!isAbstractAsset(workingAsset)) {
			// Removing the folder for the new version
			parentFolder.deleteChild(newAsset);
		}
		// Saving changes
		InodeFactory.saveInode(workingAsset);
		InodeFactory.saveInode(newAsset);

		// Swaping tree relationships
		TreeFactory.swapTrees(workingAsset, newAsset);

		if (!isAbstractAsset(workingAsset)) {
			// Setting folders and identifiers
			parentFolder.addChild(workingAsset);
			parentFolder.deleteChild(newAsset);
		}
		identifier.addChild(workingAsset);
		identifier.addChild(newAsset);

		if (!isAbstractAsset(workingAsset)) {
			if (newAsset.isLive()) {
				parentFolder.addChild(newAsset);
			}
		}

		DotHibernate.flush();
		DotHibernate.getSession().refresh(workingAsset);
		DotHibernate.getSession().refresh(newAsset);

		return workingAsset;

	}

	/**
	 * This method save the new asset as the new working version and change the
	 * current working as an old version.
	 *
	 * @param newWebAsset
	 *            New webasset version to be converted as the working asset.
	 * @return The current working webasset (The new version), after the method
	 *         execution is must use this class as the working asset instead the
	 *         class you give as parameter.
	 * @throws Exception
	 *             The method throw an exception when the new asset identifier
	 *             or the working folder cannot be found.
	 */
	public static WebAsset saveAsset(WebAsset newWebAsset, Identifier id) throws Exception {
		if (!InodeUtils.isSet(id.getInode())) {
			throw new Exception("Web asset Identifier not found!");
		}
		WebAsset currWebAsset = null;
		
		// gets the current working asset
		currWebAsset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(id, newWebAsset.getClass());
		
		//http://jira.dotmarketing.net/browse/DOTCMS-5927
		if (!InodeUtils.isSet(currWebAsset.getInode())) {
			currWebAsset = (WebAsset) IdentifierFactory.getLiveChildOfClass(id, newWebAsset.getClass());
			if(InodeUtils.isSet(currWebAsset.getInode()) && !currWebAsset.isWorking() && currWebAsset.isLive()){
				currWebAsset.setWorking(true);
				InodeFactory.saveInode(currWebAsset);
			}else if(!InodeUtils.isSet(currWebAsset.getInode()) || !currWebAsset.isLive()){
				throw new Exception("Working copy not found!");
			}
		}

		WebAsset workingAsset = swapAssets(currWebAsset, newWebAsset);

		// Check
		List workingAssets = null;
		// gets the current working asset
			workingAssets = IdentifierFactory.getWorkingChildrenOfClass(id, workingAsset.getClass());

		// if there is more than one working asset
		if (workingAssets.size() > 1) {
			Iterator iter = workingAssets.iterator();
			while (iter.hasNext()) {
				WebAsset webAsset = (WebAsset) iter.next();
				if (!webAsset.getInode().equals(workingAsset.getInode())) {
					webAsset.setWorking(false);
					InodeFactory.saveInode(webAsset);
				}
			}
		}

		return workingAsset;
	}

	public static List getAssetsPerConditionWithPermission(Host host, String condition, Class c,
			int limit, int offset, String orderby, String parent, User user) {
		return getAssetsPerConditionWithPermission(host.getIdentifier(), condition, c, limit, offset, orderby, parent, user);
	}

	@SuppressWarnings("unchecked")
	public static List<WebAsset> getAssetsPerConditionWithPermission(String hostId, String condition, Class c,
			int limit, int offset, String orderby, String parent, User user) {
		DotHibernate dh = new DotHibernate(c);

		StringBuffer sb = new StringBuffer();
		try {


			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select {" + tableName + ".*} from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree2");
			}
			sb.append(" where " + condition);

			if (InodeUtils.isSet(parent)) {
				sb.append(" and " + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "'");
			}

			if(c.equals(Container.class) || c.equals(Template.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from inode,identifier where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else if(c.equals(HTMLPage.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from inode,identifier where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else
			{
				sb.append(") and " + tableName + ".inode in (select tree.child from identifier, tree where host_inode = '"
						+ hostId + "' and tree.parent = identifier.inode)");
			}
			if(orderby != null)
				sb.append(" order by " + orderby);

			Logger.debug(WebAssetFactory.class, sb.toString());

			List<WebAsset> toReturn = new ArrayList<WebAsset>();
			int internalLimit = ITERATION_LIMIT;
			int internalOffset = 0;
			boolean done = false;
			
			while(!done) { 
				Logger.debug(WebAssetFactory.class, sb.toString());
				dh.setSQLQuery(sb.toString());

				dh.setFirstResult(internalOffset);
				dh.setMaxResults(internalLimit);
				
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				List<WebAsset> list = dh.list();
				toReturn.addAll(permAPI.filterCollection(list, PermissionAPI.PERMISSION_READ, false, user));
				if(limit > 0 && toReturn.size() >= limit + offset)
					done = true;
				else if(list.size() < internalLimit)
					done = true;
				
				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				toReturn = new ArrayList<WebAsset>(); 
			} else if(limit > 0) {
				int toIndex = offset + limit > toReturn.size()?toReturn.size():offset + limit;
				toReturn = toReturn.subList(offset, toIndex);
			} else if (offset > 0) {
				toReturn = toReturn.subList(offset, toReturn.size());
			}
			
			return toReturn;

		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList<WebAsset>();
	}

	@SuppressWarnings("unchecked")
	public static List<WebAsset> getAssetsPerConditionWithPermission(String condition, Class c, int limit,
			int offset, String orderby, String parent, User user) {
		DotHibernate dh = new DotHibernate(c);

		StringBuffer sb = new StringBuffer();
		try {

			if(offset < 0) offset = 0;
			
			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select {" + tableName + ".*} from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition, tree tree, identifier identifier ");
			if (parent != null) {
				sb.append(", tree tree2");
			}

			sb.append(" where " + condition);

			if (parent != null) {
				sb.append(" and " + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "'");
			}
			sb.append(" and " + tableName + "_condition.inode = tree.child ");
			sb.append(" and tree.parent = identifier.inode) ");

			if(orderby != null)
				sb.append(" order by " + orderby);

			List<WebAsset> toReturn = new ArrayList<WebAsset>();
			int internalLimit = 500;
			int internalOffset = 0;
			boolean done = false;
			
			while(!done) { 
				Logger.debug(WebAssetFactory.class, sb.toString());
				dh.setSQLQuery(sb.toString());

				dh.setFirstResult(internalOffset);
				dh.setMaxResults(internalLimit);
				
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				List<WebAsset> list = dh.list();
				toReturn.addAll(permAPI.filterCollection(list, PermissionAPI.PERMISSION_READ, false, user));
				if(limit > 0 && toReturn.size() >= limit + offset)
					done = true;
				else if(list.size() < internalLimit)
					done = true;
				
				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				toReturn = new ArrayList<WebAsset>(); 
			} else if(limit > 0) {
				int toIndex = offset + limit > toReturn.size()?toReturn.size():offset + limit;
				toReturn = toReturn.subList(offset, toIndex);
			} else if (offset > 0) {
				toReturn = toReturn.subList(offset, toReturn.size());
			}
			
			return toReturn;

		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList<WebAsset>();

	}

	@SuppressWarnings("unchecked")
	public static List<WebAsset> getAssetsPerConditionWithPermissionWithParent(String hostId, String condition, Class c,
			int limit, String fromAssetId, Direction direction, String orderby, String parent, boolean showDeleted, User user) {
		DotHibernate dh = new DotHibernate(c);

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select {" + tableName + ".*} from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree2");
			}
			String sqlDel = showDeleted ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			sb.append(" where working = " +  com.dotmarketing.db.DbConnectionFactory.getDBTrue() +" and deleted = " + sqlDel);
			
			if(UtilMethods.isSet(condition))
				sb.append(" and (" + condition + ") ");

			if (InodeUtils.isSet(parent)) {
				sb.append(" and (" + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "') ");
			}

			if(c.equals(Container.class) || c.equals(Template.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from identifier, inode where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from identifier, inode where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			sb.append(" order by " + orderby);

			Logger.debug(WebAssetFactory.class, sb.toString());

			dh.setSQLQuery(sb.toString());
			int firstResult = 0;
			dh.setFirstResult(firstResult);
			dh.setMaxResults(MAX_LIMIT_COUNT);
			
			PermissionAPI permAPI = APILocator.getPermissionAPI();
			List<WebAsset> list = dh.list();
			
			int pos = 0;
			boolean offsetFound = false;
			while (UtilMethods.isSet(fromAssetId) && !offsetFound && (list != null) && (0 < list.size())) {
				pos = 0;
				for (WebAsset webAsset: list) {
					if (webAsset.getIdentifier().equals(fromAssetId)) {
						offsetFound = true;
						break;
					} else {
						++pos;
					}
				}
				
				if (!offsetFound) {
					firstResult += MAX_LIMIT_COUNT;
					dh.setFirstResult(firstResult);
					list = dh.list();
				}
			}
			
			if ((pos == 0) && !offsetFound) {
				--pos;
				offsetFound = true;
			}
			
			List<WebAsset> result = new ArrayList<WebAsset>(limit);
			
			WebAsset webAsset;
			while (offsetFound && (result.size() < limit) && (list != null) && (0 < list.size())) {
				if (direction.equals(Direction.NEXT)) {
					++pos;
					while ((result.size() < limit) && (pos < list.size())) {
						webAsset = (WebAsset) list.get(pos);
						if (permAPI.doesUserHavePermission(webAsset, PermissionAPI.PERMISSION_READ, user, false)) {
							result.add(webAsset);
						}
						++pos;
					}
					
					if (result.size() < limit) {
						firstResult += MAX_LIMIT_COUNT;
						dh.setFirstResult(firstResult);
						list = dh.list();
						pos = -1;
					}
				} else {
					--pos;
					while ((result.size() < limit) && (-1 < pos)) {
						webAsset = (WebAsset) list.get(pos);
						if (permAPI.doesUserHavePermission(webAsset, PermissionAPI.PERMISSION_READ, user, false)) {
							result.add(webAsset);
						}
						--pos;
					}
					
					if (result.size() < limit) {
						firstResult -= MAX_LIMIT_COUNT;
						if (-1 < firstResult) {
							dh = new DotHibernate(c);
							dh.setSQLQuery(sb.toString());
							dh.setFirstResult(firstResult);
							dh.setMaxResults(MAX_LIMIT_COUNT);
							list = dh.list();
							pos = MAX_LIMIT_COUNT;
						} else {
							list = null;
						}
					}
				}
			}
			
			if (direction.equals(Direction.PREVIOUS))
				Collections.reverse(result);
			
			return result;
			
		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList<WebAsset>();
	}

	@SuppressWarnings("unchecked")
	public static List<WebAsset> getAssetsPerConditionWithPermissionWithParent(String condition, Class c, int limit,
			String fromAssetId, Direction direction, String orderby, String parent, boolean showDeleted, User user) {
		DotHibernate dh = new DotHibernate(c);

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select {" + tableName + ".*} from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition ");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree ");
			}
			
			String sqlDel = showDeleted ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			sb.append(" where working = " +  com.dotmarketing.db.DbConnectionFactory.getDBTrue()  +"  and deleted = " + sqlDel);
			
			if(UtilMethods.isSet(condition)) {
				sb.append(" and (" + condition + " )");
			}
			if (InodeUtils.isSet(parent)) {
				sb.append(" and (" + tableName + "_condition.inode = tree.child");
				sb.append(" and tree.parent = '" + parent + "')");
			}
			sb.append(")");


			sb.append(" order by " + orderby);

			Logger.debug(WebAssetFactory.class, sb.toString());

			dh.setSQLQuery(sb.toString());
			int firstResult = 0;
			dh.setFirstResult(firstResult);
			dh.setMaxResults(MAX_LIMIT_COUNT);
			
			PermissionAPI permAPI = APILocator.getPermissionAPI();
			List<WebAsset> list = dh.list();
			
			int pos = 0;
			boolean offsetFound = false;
			while (UtilMethods.isSet(fromAssetId) && !offsetFound && (list != null) && (0 < list.size())) {
				pos = 0;
				for (WebAsset webAsset: list) {
					if (webAsset.getIdentifier().equals(fromAssetId)) {
						offsetFound = true;
						break;
					} else {
						++pos;
					}
				}
				
				if (!offsetFound) {
					firstResult += MAX_LIMIT_COUNT;
					dh.setFirstResult(firstResult);
					list = dh.list();
				}
			}
			
			if ((pos == 0) && !offsetFound) {
				--pos;
				offsetFound = true;
			}
			
			List<WebAsset> result = new ArrayList<WebAsset>(limit);
			
			WebAsset webAsset;
			while (offsetFound && (result.size() < limit) && (list != null) && (0 < list.size())) {
				if (direction.equals(Direction.NEXT)) {
					++pos;
					while ((result.size() < limit) && (pos < list.size())) {
						webAsset = (WebAsset) list.get(pos);
						if (permAPI.doesUserHavePermission(webAsset, PermissionAPI.PERMISSION_READ, user, false)) {
							result.add(webAsset);
						}
						++pos;
					}
					
					if (result.size() < limit) {
						firstResult += MAX_LIMIT_COUNT;
						dh.setFirstResult(firstResult);
						list = dh.list();
						pos = -1;
					}
				} else {
					--pos;
					while ((result.size() < limit) && (-1 < pos)) {
						webAsset = (WebAsset) list.get(pos);
						if (permAPI.doesUserHavePermission(webAsset, PermissionAPI.PERMISSION_READ, user, false)) {
							result.add(webAsset);
						}
						--pos;
					}
					
					if (result.size() < limit) {
						firstResult -= MAX_LIMIT_COUNT;
						if (-1 < firstResult) {
							dh = new DotHibernate(c);
							dh.setSQLQuery(sb.toString());
							dh.setFirstResult(firstResult);
							dh.setMaxResults(MAX_LIMIT_COUNT);
							list = dh.list();
							pos = MAX_LIMIT_COUNT;
						} else {
							list = null;
						}
					}
				}
			}
			
			if (direction.equals(Direction.PREVIOUS))
				Collections.reverse(result);
			
			return result;
		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList<WebAsset>();

	}
	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndConditionWithParent(String hostId, Role[] roles,
			String condition, int limit, String fromAssetId, Direction direction, String orderby, Class assetsClass, String tableName, String parentId, boolean showDeleted, User user) {
		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermissionWithParent(hostId, condition, assetsClass, limit, fromAssetId, direction, orderby, parentId, showDeleted, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class, "Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndConditionWithParent(Role[] roles,
			String condition, int limit, String fromAssetId, Direction direction, String orderby, Class assetsClass, String tableName, String parentId, boolean showDeleted, User user) {

		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermissionWithParent(condition, assetsClass, limit, fromAssetId, direction, orderby, parentId, showDeleted, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
		
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}

			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndCondition(String hostId, Role[] roles,
			String condition, int limit, int offset, String orderby, Class assetsClass, String tableName, User user) {
		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermission(hostId, condition, assetsClass, limit, offset, orderby, null, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}

	/**
	 * 
	 * @param hostId
	 * @param roles
	 * @param condition
	 * @param limit
	 * @param offset
	 * @param orderby
	 * @param assetsClass
	 * @param tableName
	 * @param parent
	 * @return
	 * @deprecated
	 */
	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndCondition(String hostId, Role[] roles,
			String condition, int limit, int offset, String orderby, Class assetsClass, String tableName, String parent, User user) {
		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermission(hostId, condition, assetsClass,
				limit, offset, orderby, parent, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}

	// Generic method for all Assets.
	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndCondition(Role[] roles,
			String condition, int limit, int offset, String orderby, Class assetsClass, String tableName, User user) {

		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermission(condition, assetsClass, limit, offset, orderby, null, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<PermissionAsset> getAssetsAndPermissionsPerRoleAndCondition(Role[] roles,
			String condition, int limit, int offset, String orderby, Class assetsClass, String tableName, String parent, User user) {

		java.util.List<PermissionAsset> entries = new java.util.ArrayList<PermissionAsset>();
		orderby = tableName + "." + orderby;
		java.util.List<WebAsset> elements = WebAssetFactory.getAssetsPerConditionWithPermission(condition, assetsClass, limit, offset, orderby, parent, user);
		java.util.Iterator<WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			entries.add(permAsset);
		}
		return entries;
	}

	public static boolean isAbstractAsset(WebAsset asset) {
		if (asset instanceof Container || asset instanceof Template)
			return true;
		return false;
	}

	public static void changeAssetMenuOrder(Inode asset, int newValue, User user) throws ActionException, DotDataException {

		// Checking permissions
		if (!permissionAPI.doesUserHavePermission(asset, PERMISSION_WRITE, user))
			throw new ActionException(WebKeys.USER_PERMISSIONS_EXCEPTION);

		if (asset instanceof Folder) {
			if (newValue == -1) {
				((Folder)asset).setShowOnMenu(false);
			} else {
				((Folder)asset).setShowOnMenu(true);
			}
			((Folder)asset).setSortOrder(newValue);
			RefreshMenus.deleteMenu(((Folder)asset));
		} else if (asset instanceof WebAsset) {
			if (newValue == -1) {
				((WebAsset)asset).setShowOnMenu(false);
			} else {
				((WebAsset)asset).setShowOnMenu(true);
			}
			((WebAsset)asset).setSortOrder(newValue);
			RefreshMenus.deleteMenu(((WebAsset)asset));
		}
		InodeFactory.saveInode(asset);
	}

	/**
	 * This method totally removes an asset from the cms
	 * @param currWebAsset
	 * @return
	 */
	public static boolean deleteAsset(WebAsset currWebAsset)
	{
		return deleteAsset(currWebAsset, null);	
	}

	/**
	 * This method totally removes an asset from the cms
	 * @param currWebAsset
	 * @param user If the user is passed (not null) the system will check for write permission of the user in the asset
	 * @return true if the asset was sucessfully removed
	 */
	public static boolean deleteAsset(WebAsset currWebAsset, User user)
	{
		boolean returnValue = false;
		try
		{
			if (!UtilMethods.isSet(currWebAsset) || !InodeUtils.isSet(currWebAsset.getInode()))
			{
				return returnValue;
			}
			//Checking permissions
			int permission = PERMISSION_WRITE;
			
			if(permissionAPI.doesUserHavePermission(currWebAsset, permission, user))
			{
				//### Delete the IDENTIFIER entry from cache ###
				LiveCache.removeAssetFromCache(currWebAsset);
				WorkingCache.removeAssetFromCache(currWebAsset);
				IdentifierCache.removeAssetFromIdCache(currWebAsset);
				//### END Delete the entry from cache ###


				//Get the identifier of the webAsset
				Identifier identifier = IdentifierFactory.getParentIdentifier(currWebAsset);

				//### Get and delete the webAsset ###
				List<WebAsset> webAssetList = new ArrayList<WebAsset>();
				if(currWebAsset instanceof Container)
				{
					ContainerServices.unpublishContainerFile((Container)currWebAsset);
					webAssetList = InodeFactory.getChildrenClass(identifier,Container.class);
				}
				else if(currWebAsset instanceof HTMLPage)
				{
					PageServices.unpublishPageFile((HTMLPage)currWebAsset);
					RefreshMenus.deleteMenu(currWebAsset);
					webAssetList = InodeFactory.getChildrenClass(identifier,HTMLPage.class);
				}
				else if(currWebAsset instanceof Template)
				{
					TemplateServices.unpublishTemplateFile((Template)currWebAsset);
					webAssetList = InodeFactory.getChildrenClass(identifier,Template.class);
				}
				else if(currWebAsset instanceof Link)
				{
					webAssetList = InodeFactory.getChildrenClass(identifier,Link.class);
				}
				else if(currWebAsset instanceof File)
				{
					webAssetList = InodeFactory.getChildrenClass(identifier,File.class);
					RefreshMenus.deleteMenu(currWebAsset);
				}
				for(WebAsset webAsset : webAssetList)
				{
					//Delete the permission of each version of the asset
					permissionAPI.removePermissions(webAsset);
					InodeFactory.deleteInode(webAsset);
				}
				//### END Get and delete the webAsset and the identifier ###

				//### Get and delete the tree entries ###
				List<Tree> treeList = new ArrayList<Tree>();
				treeList.addAll(TreeFactory.getTreesByChild(identifier));
				treeList.addAll(TreeFactory.getTreesByParent(identifier));
				for(Tree tree : treeList)
				{
					TreeFactory.deleteTree(tree);
				}
				//### END Get and delete the tree entries ###

				//### Get and delete the multitree entries ###
				List<MultiTree> multiTrees = new ArrayList<MultiTree>();
				if (currWebAsset instanceof Container || currWebAsset instanceof HTMLPage)
				{
					multiTrees = MultiTreeFactory.getMultiTree(identifier);
				}
				if(UtilMethods.isSet(multiTrees))
				{
					for(MultiTree multiTree : multiTrees)
					{
						MultiTreeFactory.deleteMultiTree(multiTree);
					}
				}
				//### END Get and delete the multitree entries ###



				//### Delete the Identifier ###
				InodeFactory.deleteInode(identifier);
				//### Delete the Identifier ###
				returnValue = true;
			}
			else
			{
				throw new Exception(WebKeys.USER_PERMISSIONS_EXCEPTION);
			}
		}
		catch(Exception ex)
		{
			String message = ex.getMessage();
			throw ex;
		}
		finally
		{
			return returnValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static int getAssetsCountPerConditionWithPermissionWithParent(String condition, Class c, int limit, int offset, String parent, boolean showDeleted, User user) {
		DotConnect dc = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select " + tableName + "_1_.identifier as identifier, " + tableName + "_1_.inode as inode from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition ");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree ");
			}
			
			String sqlDel = showDeleted ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			sb.append(" where working = " +  com.dotmarketing.db.DbConnectionFactory.getDBTrue()  +"  and deleted = " + sqlDel);
			
			if(UtilMethods.isSet(condition)) {
				sb.append(" and (" + condition + " )");
			}
			if (InodeUtils.isSet(parent)) {
				sb.append(" and (" + tableName + "_condition.inode = tree.child");
				sb.append(" and tree.parent = '" + parent + "')");
			}
			sb.append(")");


			Logger.debug(WebAssetFactory.class, sb.toString());

			dc.setSQL(sb.toString());
			
			int startRow = offset;
			
			if (limit != 0) {
				dc.setStartRow(startRow);
				dc.setMaxRows(limit);
			}
			
			List<Map<String, String>> list = dc.loadResults();
			List<Permissionable> assetsList = new ArrayList<Permissionable>();
			WebAsset permissionable;
			
			PermissionAPI permAPI = APILocator.getPermissionAPI();
			
			while ((assetsList.size() < limit) && (list != null) && (0 < list.size())) {
				for (Map<String, String> map: list) {
					permissionable = (WebAsset) c.newInstance();
					permissionable.setIdentifier(map.get("identifier"));
					permissionable.setInode(map.get("inode"));
					
					if (permAPI.doesUserHavePermission(permissionable, PermissionAPI.PERMISSION_READ, user, false)) {
						assetsList.add(permissionable);
						if (limit < assetsList.size())
							break;
					}
				}
				
				if (assetsList.size() < limit) {
					dc = new DotConnect();
					dc.setSQL(sb.toString());
					startRow += limit;
					dc.setStartRow(startRow);
					dc.setMaxRows(limit);
					list = dc.loadResults();
				}
			}
			
			return assetsList.size();
		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsCountPerConditionWithPermissionWithParent failed:" + e, e);
		}

		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int getAssetsCountPerConditionWithPermission(String condition, Class c, int limit, int offset, String parent, User user) {
		DotConnect dc = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {

			if(offset < 0) offset = 0;
			
			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select " + tableName + "_1_.identifier as identifier, " + tableName + "_1_.inode as inode from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition, tree tree, identifier identifier ");
			if (parent != null) {
				sb.append(", tree tree2");
			}

			sb.append(" where " + condition);

			if (parent != null) {
				sb.append(" and " + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "'");
			}
			sb.append(" and " + tableName + "_condition.inode = tree.child ");
			sb.append(" and tree.parent = identifier.inode) ");

			List<Permissionable> toReturn = new ArrayList<Permissionable>();
			int internalLimit = 500;
			int internalOffset = 0;
			boolean done = false;
			
			while(!done) { 
				Logger.debug(WebAssetFactory.class, sb.toString());
				dc.setSQL(sb.toString());

				dc.setStartRow(internalOffset);
				dc.setMaxRows(internalLimit);
				
				List<Map<String, String>> list = dc.loadResults();
				List<Permissionable> assetsList = new ArrayList<Permissionable>();
				WebAsset permissionable;
				
				for (Map<String, String> map: list) {
					permissionable = (WebAsset) c.newInstance();
					permissionable.setIdentifier(map.get("identifier"));
					permissionable.setInode(map.get("inode"));
					assetsList.add(permissionable);
				}
				
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				toReturn.addAll(permAPI.filterCollection(assetsList, PermissionAPI.PERMISSION_READ, false, user));
				if(limit > 0 && toReturn.size() >= limit + offset)
					done = true;
				else if(assetsList.size() < internalLimit)
					done = true;
				
				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				toReturn = new ArrayList<Permissionable>(); 
			} else if(limit > 0) {
				int toIndex = offset + limit > toReturn.size()?toReturn.size():offset + limit;
				toReturn = toReturn.subList(offset, toIndex);
			} else if (offset > 0) {
				toReturn = toReturn.subList(offset, toReturn.size());
			}
			
			return toReturn.size();

		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsCountPerConditionWithPermission failed:" + e, e);
		}

		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int getAssetsCountPerConditionWithPermissionWithParent(String hostId, String condition, Class c, int limit, int offset, String parent, boolean showDeleted, User user) {
		DotConnect dc = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select " + tableName + "_1_.identifier as identifier, " + tableName + "_1_.inode as inode from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree2");
			}
			String sqlDel = showDeleted ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			sb.append(" where working = " +  com.dotmarketing.db.DbConnectionFactory.getDBTrue() +" and deleted = " + sqlDel);
			
			if(UtilMethods.isSet(condition))
				sb.append(" and (" + condition + ") ");

			if (InodeUtils.isSet(parent)) {
				sb.append(" and (" + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "') ");
			}

			if(c.equals(Container.class) || c.equals(Template.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from identifier, inode where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from identifier, inode where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}

			Logger.debug(WebAssetFactory.class, sb.toString());

			dc.setSQL(sb.toString());
			
			int startRow = offset;
			
			if (limit != 0) {
				dc.setStartRow(startRow);
				dc.setMaxRows(limit);
			}
			
			List<Map<String, String>> list = dc.loadResults();
			List<Permissionable> assetsList = new ArrayList<Permissionable>();
			WebAsset permissionable;
			
			PermissionAPI permAPI = APILocator.getPermissionAPI();
			
			while ((assetsList.size() < limit) && (list != null) && (0 < list.size())) {
				for (Map<String, String> map: list) {
					permissionable = (WebAsset) c.newInstance();
					permissionable.setIdentifier(map.get("identifier"));
					permissionable.setInode(map.get("inode"));
					
					if (permAPI.doesUserHavePermission(permissionable, PermissionAPI.PERMISSION_READ, user, false)) {
						assetsList.add(permissionable);
						if (limit < assetsList.size())
							break;
					}
				}
				
				if (assetsList.size() < limit) {
					dc = new DotConnect();
					dc.setSQL(sb.toString());
					startRow += limit;
					dc.setStartRow(startRow);
					dc.setMaxRows(limit);
					list = dc.loadResults();
				}
			}
			
			return assetsList.size();
			
		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsCountPerConditionWithPermissionWithParent failed:" + e, e);
		}

		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int getAssetsCountPerConditionWithPermission(String hostId, String condition, Class c, int limit, int offset, String parent, User user) {
		DotConnect dc = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {


			String tableName = ((Inode) c.newInstance()).getType();

			sb.append("select " + tableName + "_1_.identifier as identifier, " + tableName + "_1_.inode as inode from " + tableName + ", inode " + tableName + "_1_ where "
					+ tableName + ".inode = " + tableName + "_1_.inode and " + tableName + ".inode in (");
			sb.append("select distinct " + tableName + "_condition.inode ");
			sb.append(" from " + tableName + " " + tableName + "_condition");
			if (InodeUtils.isSet(parent)) {
				sb.append(", tree tree2");
			}
			sb.append(" where " + condition);

			if (InodeUtils.isSet(parent)) {
				sb.append(" and " + tableName + "_condition.inode = tree2.child");
				sb.append(" and tree2.parent = '" + parent + "'");
			}

			if(c.equals(Container.class) || c.equals(Template.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from inode,identifier where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else if(c.equals(HTMLPage.class))
			{
				sb.append(") and " + tableName + ".inode in (select inode.inode from inode,identifier where host_inode = '"
						+ hostId + "' and inode.identifier = identifier.inode)");
			}
			else
			{
				sb.append(") and " + tableName + ".inode in (select tree.child from identifier, tree where host_inode = '"
						+ hostId + "' and tree.parent = identifier.inode)");
			}
			
			Logger.debug(WebAssetFactory.class, sb.toString());

			List<Permissionable> toReturn = new ArrayList<Permissionable>();
			int internalLimit = ITERATION_LIMIT;
			int internalOffset = 0;
			boolean done = false;
			
			while(!done) { 
				Logger.debug(WebAssetFactory.class, sb.toString());
				dc.setSQL(sb.toString());

				dc.setStartRow(internalOffset);
				dc.setMaxRows(internalLimit);
				
				List<Map<String, String>> list = dc.loadResults();
				List<Permissionable> assetsList = new ArrayList<Permissionable>();
				WebAsset permissionable;
				
				for (Map<String, String> map: list) {
					permissionable = (WebAsset) c.newInstance();
					permissionable.setIdentifier(map.get("identifier"));
					permissionable.setInode(map.get("inode"));
					assetsList.add(permissionable);
				}
				
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				toReturn.addAll(permAPI.filterCollection(assetsList, PermissionAPI.PERMISSION_READ, false, user));
				if(limit > 0 && toReturn.size() >= limit + offset)
					done = true;
				else if(assetsList.size() < internalLimit)
					done = true;
				
				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				toReturn = new ArrayList<Permissionable>(); 
			} else if(limit > 0) {
				int toIndex = offset + limit > toReturn.size()?toReturn.size():offset + limit;
				toReturn = toReturn.subList(offset, toIndex);
			} else if (offset > 0) {
				toReturn = toReturn.subList(offset, toReturn.size());
			}
			
			return toReturn.size();

		} catch (Exception e) {
			Logger.warn(WebAssetFactory.class, "getAssetsCountPerConditionWithPermission failed:" + e, e);
		}

		return 0;
	}

	public PaginatedArrayList<PermissionAsset> getAssetsAndPermissions(String hostId, Role[] roles,
			boolean includeArchived, int limit, int offset, String orderBy, String tableName, String parent, String query, User user) {
		PaginatedArrayList<PermissionAsset>  paginatedEntries = new PaginatedArrayList<PermissionAsset> ();
		long totalCount = 0;
	
		AssetType type = AssetType.getObject(tableName.toUpperCase());
		java.util.List<? extends WebAsset> elements = null;
		Map<String,Object> params = new HashMap<String, Object>();
		if(UtilMethods.isSet(query)){				
			params.put("title", query.toLowerCase().replace("\'","\\\'"));
		}
		try {
		if (type.equals(AssetType.HTMLPAGE)){
			if(UtilMethods.isSet(query)){				
				params.put("pageUrl", query.toLowerCase());
			}
		    elements = htmlPageAPI.findHtmlPages(user, includeArchived, params, hostId,null,null, parent, offset, limit, orderBy);
		}else if (type.equals(AssetType.FILE_ASSET)){
			if(UtilMethods.isSet(query)){				
				params.put("fileName", query.toLowerCase().replace("\'","\\\'"));
			}
			elements = fileAPI.findFiles(user, includeArchived, params, hostId, null,null, parent, offset, limit, orderBy);
		}else if (type.equals(AssetType.CONTAINER)){
			elements = containerAPI.findContainers(user, includeArchived, params, hostId, null, null, parent, offset, limit, orderBy);
		}else if (type.equals(AssetType.TEMPLATE)){
			elements = templateAPI.findTemplates(user, includeArchived, params, hostId, null, null,  parent, offset, limit, orderBy);
		}else if (type.equals(AssetType.LINK)){
			elements = linksAPI.findLinks(user, includeArchived, params, hostId, null, null, parent, offset, limit, orderBy);
		}
		} catch (DotSecurityException e) {
			Logger.warn(WebAssetFactory.class, "getAssetsAndPermissions failed:" + e, e);
		} catch (DotDataException e) {
			Logger.warn(WebAssetFactory.class, "getAssetsAndPermissions failed:" + e, e);
		}
		
		
	    totalCount =  elements!=null?((PaginatedArrayList)elements).getTotalResults():0;
		java.util.Iterator<? extends WebAsset> elementsIter = elements.iterator();

		while (elementsIter.hasNext()) {

			WebAsset asset = elementsIter.next();
			Folder folderParent = null;
			if (!WebAssetFactory.isAbstractAsset(asset))
				folderParent = (Folder) InodeFactory.getParentOfClass(asset, Folder.class);
			
			Host host=null;
			try {
				host = APILocator.getHostAPI().findParentHost(asset, user, false);
			} catch (DotDataException e1) {
				Logger.error(WebAssetFactory.class,"Could not load host : ",e1);
			} catch (DotSecurityException e1) {
				Logger.error(WebAssetFactory.class,"User does not have required permissions : ",e1);
			}
			if(host!=null){
					if(host.isArchived()){
					 continue;
					}
				}
			
			java.util.List<Integer> permissions = new ArrayList<Integer>();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(asset, roles, user);
			} catch (DotDataException e) {
				Logger.error(WebAssetFactory.class,"Could not load permissions : ",e);
			}

			PermissionAsset permAsset = new PermissionAsset();
			if (!WebAssetFactory.isAbstractAsset(asset))
				permAsset.setPathToMe(folderParent.getPath());
			else
				permAsset.setPathToMe("");
			permAsset.setPermissions(permissions);
			permAsset.setAsset(asset);
			paginatedEntries.add(permAsset);
		}
		
		paginatedEntries.setTotalResults(totalCount);
		
		return paginatedEntries;
	}
	

}
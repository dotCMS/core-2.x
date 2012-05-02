/**
 * 
 */
package com.dotmarketing.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.MultiTree;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.VirtualLinksCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.MultiTreeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.business.HTMLPageCache;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.services.ContainerServices;
import com.dotmarketing.services.PageServices;
import com.dotmarketing.services.TemplateServices;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.model.User;

/**
 * @author jtesser
 * All methods in the BaseWebAssetAPI should be protected or private. The BaseWebAssetAPI is intended to be extended by other APIs for WebAsset Objects.
 * This api will eventually fade out when all web assets move to content
 *  
 */
public abstract class BaseWebAssetAPI extends BaseInodeAPI {

	/**
	 * PLEASE REMOVE ME!!!!!!
	 * @deprecated
	 * @param parent
	 * @param webAsset The type of WebAsset children to look for
	 * @return
	 */
	protected List findLiveChildren(Inode parent, WebAsset webAsset){
		String condition = (" and deleted =" + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + " and live =" + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
		return getChildrenClassByCondition(parent, webAsset.getClass(), condition);
	}
	
	/**
	 * Save the asset.
	 * 
	 * @param currWebAsset
	 * @throws DotDataException
	 */
	protected abstract void save(WebAsset currWebAsset) throws DotDataException; 
	
	protected void unLockAsset(WebAsset currWebAsset) throws DotDataException {

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
		save(workingwebasset);
	}
	
	protected void createAsset(WebAsset webasset, String userId) throws DotDataException {

		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		save(webasset);

		// create new identifier, without URI
		Identifier id = IdentifierFactory.createNewIdentifier(webasset, (Host)null);
		id.setOwner(userId);
		InodeFactory.saveInode(id);

	}
	
	protected void createAsset(WebAsset webasset, String userId, Inode parent) throws DotDataException {
		webasset.setWorking(true);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		save(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// create new identifier, with the URI
		Identifier id = IdentifierFactory.createNewIdentifier(webasset, (Folder) parent);
		id.setOwner(userId);
		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		InodeFactory.saveInode(id);
	}
	
	protected void createAsset(WebAsset webasset, String userId, Identifier identifier, boolean working) throws DotDataException {
		webasset.setWorking(working);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		save(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);
		webasset.addParent(identifier);
		webasset.setIdentifier(identifier.getInode());

		save(webasset);

	}
	
	protected void createAsset(WebAsset webasset, String userId, Inode parent, Identifier identifier, boolean working) throws DotDataException {
		webasset.setInode(UUID.randomUUID().toString());
		webasset.setWorking(working);
		webasset.setLive(false);
		webasset.setDeleted(false);
		webasset.setLocked(false);
		webasset.setModDate(new java.util.Date());
		webasset.setModUser(userId);
		// persists the webasset
		save(webasset);

		// adds the webasset as child of the folder or parent inode
		parent.addChild(webasset);

		// adds asset to the existing identifier
		identifier.addChild(webasset);
		webasset.addParent(identifier);
		webasset.setIdentifier(identifier.getInode());

		save(webasset);
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
	 * @throws DotDataException
	 *             The method throw an exception when the new asset identifier
	 *             or the working folder cannot be found.
	 */
	protected WebAsset saveAsset(WebAsset newWebAsset, Identifier id) throws DotDataException {
		if (!InodeUtils.isSet(id.getInode())) {
			throw new DotDataException("Web asset Identifier not found!");
		}
		WebAsset currWebAsset = null;
		
		// gets the current working asset
		currWebAsset = (WebAsset) IdentifierFactory.getWorkingChildOfClass(id, newWebAsset.getClass());
		
		if (!InodeUtils.isSet(currWebAsset.getInode())) {
			throw new DotDataException("Working copy not found!");
		}
		
		WebAsset workingAsset = null;
		
		try {
			workingAsset = swapAssets(currWebAsset, newWebAsset);
		} catch (Exception e) {
			throw new DotRuntimeException(e.getMessage(), e);
		}

		// Check
		List workingAssets = null;
		// gets the current working asset
			workingAssets = IdentifierFactory.getWorkingChildrenOfClass(id, workingAsset.getClass());

		// if there is more than one working asset
		if (workingAssets.size() > 1) {
			Iterator iter = workingAssets.iterator();
			while (iter.hasNext()) {
				WebAsset webAsset = (WebAsset) iter.next();
				if (webAsset.getInode() != workingAsset.getInode()) {
					webAsset.setWorking(false);
					save(webAsset);
				}
			}
		}

		return workingAsset;
	}
	
	/**
	 * Swap assets properties and tree relationships to convert the newAsset
	 * into the workingAsset
	 * This method don't swap the multitree relationships and correctly set the
	 * working/live and parent folder
	 * relationships and properties
	 */
	private WebAsset swapAssets(WebAsset workingAsset, WebAsset newAsset) throws Exception {
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
				if(x != null){
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
				if(x!=null){
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
		
		// Saving changes
		save(workingAsset);
		save(newAsset);
		
		DotHibernate.flush();
		DotHibernate.getSession().refresh(workingAsset);
		DotHibernate.getSession().refresh(newAsset);

		return workingAsset;
	}
	
	protected boolean isAbstractAsset(WebAsset asset) {
		if (asset instanceof Container || asset instanceof Template)
			return true;
		return false;
	}
	
	/**
	 * This method totally removes an asset from the cms
	 * @param currWebAsset
	 * @param user If the user is passed (not null) the system will check for write permission of the user in the asset
	 * @param respectFrontendRoles
	 * @return true if the asset was sucessfully removed
	 * @exception Exception
	 */
	public static boolean deleteAsset(WebAsset currWebAsset) {
		boolean returnValue = false;
		try
		{
			if (!UtilMethods.isSet(currWebAsset) || !InodeUtils.isSet(currWebAsset.getInode()))
			{
				return returnValue;
			}
			
			PermissionAPI permissionAPI = APILocator.getPermissionAPI();
			
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
				CacheLocator.getContainerCache().remove(currWebAsset.getInode());
				webAssetList = InodeFactory.getChildrenClass(identifier,Container.class);
			}
			else if(currWebAsset instanceof HTMLPage)
			{
				PageServices.unpublishPageFile((HTMLPage)currWebAsset);
				RefreshMenus.deleteMenu(currWebAsset);
				CacheLocator.getHTMLPageCache().remove((HTMLPage)currWebAsset);
				webAssetList = InodeFactory.getChildrenClass(identifier,HTMLPage.class);
			}
			else if(currWebAsset instanceof Template)
			{
				TemplateServices.unpublishTemplateFile((Template)currWebAsset);
				CacheLocator.getTemplateCache().remove(currWebAsset.getInode());
				webAssetList = InodeFactory.getChildrenClass(identifier,Template.class);
			}
			else if(currWebAsset instanceof Link)
			{
				VirtualLinksCache.removePathFromCache(((Link)currWebAsset).getUrl());
				webAssetList = InodeFactory.getChildrenClass(identifier,Link.class);
			}
			else if(currWebAsset instanceof File)
			{
				webAssetList = InodeFactory.getChildrenClass(identifier,File.class);
				FileCache.removeFile((File)currWebAsset);
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
		catch(Exception ex)
		{
			Logger.warn(BaseWebAssetAPI.class, ex.getMessage());
			throw ex;
		}
		finally
		{
			return returnValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsAndPermissionsPerRoleAndConditionWithParent(String condition, Class assetsClass, String parentId, boolean showDeleted, User user) {
		return WebAssetFactory.getAssetsCountPerConditionWithPermissionWithParent(condition, assetsClass, 100000, 0, parentId, showDeleted, user);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(String condition, Class c, User user) {
		return getCountAssetsPerConditionWithPermission(condition, c, null, user);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(String condition, Class c, String parent, User user) {
		return WebAssetFactory.getAssetsCountPerConditionWithPermission(condition, c, -1, 0, parent, user);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsAndPermissionsPerRoleAndConditionWithParent(String hostId, String condition, Class assetsClass, String parentId, boolean showDeleted, User user) {		
		return WebAssetFactory.getAssetsCountPerConditionWithPermissionWithParent(hostId, condition, assetsClass, 100000, 0, parentId, showDeleted, user);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(Host host, String condition, Class c, User user) {
		return getCountAssetsPerConditionWithPermission(host.getIdentifier(), condition, c, user);
	}

	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(String hostId, String condition, Class c, User user) {
		return getCountAssetsPerConditionWithPermission(hostId, condition, c, null, user);
	}

	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(Host host, String condition, Class c, String parent, User user) {
		return getCountAssetsPerConditionWithPermission(host.getIdentifier(), condition, c, parent, user);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountAssetsPerConditionWithPermission(String hostId, String condition, Class c, String parent, User user) {
		return WebAssetFactory.getAssetsCountPerConditionWithPermission(hostId, condition, c, -1, 0, parent, user);
	}
}
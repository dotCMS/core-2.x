package com.dotmarketing.portlets.files.factories;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.awt.Container;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.runtime.resource.ResourceManager;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.exception.WebAssetException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.PublishFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.velocity.DotResourceCache;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;

/**
 * 
 * @author will
 */
public class FileFactory {

	public static File getChildImage(Inode i) {

        return (File) InodeFactory.getChildOfClassbyCondition(i, File.class,
                "(file_name like '%.jpg' or file_name like '%.gif')");

    }

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getLiveFiles() {
        DotHibernate dh = new DotHibernate(File.class);
        dh.setQuery("from inode in class com.dotmarketing.portlets.files.model.File where type='file_asset' and live = "
                + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = "
                + com.dotmarketing.db.DbConnectionFactory.getDBFalse());
        return dh.list();
    }

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getWorkingFiles() {
        DotHibernate dh = new DotHibernate(File.class);
        dh.setQuery("from inode in class com.dotmarketing.portlets.files.model.File where type='file_asset' and working = "
                + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = "
                + com.dotmarketing.db.DbConnectionFactory.getDBFalse());
        return dh.list();
    }

    public static File getChildMP3(Inode i) {
        return (File) InodeFactory.getChildOfClassbyCondition(i, File.class, "file_name like '%.mp3'");

    }

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getChildrenFilesByOrder(Inode i) {

        return InodeFactory.getChildrenClassByOrder(i, File.class, "sort_order");

    }

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFileChildrenByCondition(Inode i, String condition) {

        return InodeFactory.getChildrenClassByConditionAndOrderBy(i, File.class, condition, "file_name, sort_order");

    }
    
    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFileChildrenByConditionAndOrder(Inode i, String condition,String order) {

        return InodeFactory.getChildrenClassByConditionAndOrderBy(i, File.class, condition, order);

    }

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFileChildren(Inode i) {

        return InodeFactory.getChildrenClassByOrder(i, File.class, "inode, sort_order");

    }

    public static boolean existsFileName(Inode parent, String fileName) {

        Logger.debug(FileFactory.class, "UtilMethods.sqlify(fileName)" + UtilMethods.sqlify(fileName));
        File f = (File) InodeFactory.getChildOfClassbyCondition(parent, File.class, "file_name = '" + UtilMethods.sqlify(fileName) + "' and working = " + DbConnectionFactory.getDBTrue());
        return (InodeUtils.isSet(f.getInode()));
    }

    public static File getFileByURI(String uri, Host host, boolean live) {
        return getFileByURI(uri, host.getIdentifier(), live);
    }

    public static File getFileByURI(String uri, String hostId, boolean live) {

        uri = uri.replaceAll(Config.getStringProperty("VIRTUAL_FILE_PREFIX"), "");
        Logger.debug(FileFactory.class, "getFileByURI=" + uri);
        Identifier i = IdentifierFactory.getIdentifierByURI(uri, hostId);
        return getFileByIdentifier(i, live);

    }

    public static File getFileByIdentifier(Identifier i, boolean live) {

        if (live) {
            return (File) InodeFactory.getChildOfClassbyCondition(i, File.class, "live = "
                    + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = "
                    + com.dotmarketing.db.DbConnectionFactory.getDBFalse());

        } else {
            return (File) InodeFactory.getChildOfClassbyCondition(i, File.class, "working = "
                    + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = "
                    + com.dotmarketing.db.DbConnectionFactory.getDBFalse());

        }

    }
    
    public static File getArchivedFileByIdentifier(Identifier i) {
            return (File) InodeFactory.getChildOfClassbyCondition(i, File.class, "deleted = "
                    + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
    }

    public static String getVirtualFileURI(File file) {

        Identifier identifier = (Identifier) InodeFactory.getParentOfClass(file, Identifier.class);
        if (InodeUtils.isSet(identifier.getInode())) {
            return (Config.getStringProperty("VIRTUAL_FILE_PREFIX") + identifier.getURI()).intern();
        }
        return null;
    }

    public static String getVersionFileURI(File file) {

        return Config.getStringProperty("VERSION_FILE_PREFIX") + file.getInode() + "."
                + UtilMethods.getFileExtension(file.getFileName()).intern();

    }

    public static String getRelativeAssetPath(Inode inode) {
        String _inode = inode.getInode();
        return getRelativeAssetPath(_inode, UtilMethods.getFileExtension(((com.dotmarketing.portlets.files.model.File) inode).getFileName())
                            .intern());
    }

    public static String getRelativeAssetPath(String inode, String ext) {
        String _inode = inode;
        String path = "";

       	path = java.io.File.separator + _inode.charAt(0)
       		+ java.io.File.separator + _inode.charAt(1) + java.io.File.separator + _inode + "." + ext;

        return path;
    }

    public static String getRealAssetPath(Inode inode) {
        String _inode = inode.getInode();
        return getRealAssetPath (_inode, UtilMethods.getFileExtension(((com.dotmarketing.portlets.files.model.File) inode).getFileName())
                .intern());
    }

    public static String getRealAssetPath(String inode, String ext) {
        String _inode = inode;
        String path = "";

        String realPath = Config.getStringProperty("ASSET_REAL_PATH");
        if (UtilMethods.isSet(realPath) && !realPath.endsWith(java.io.File.separator))
            realPath = realPath + java.io.File.separator;

        String assetPath = Config.getStringProperty("ASSET_PATH");
        if (UtilMethods.isSet(assetPath) && !assetPath.endsWith(java.io.File.separator))
            assetPath = assetPath + java.io.File.separator;
        
        path = ((!UtilMethods.isSet(realPath)) ? assetPath : realPath)
                + _inode.charAt(0) + java.io.File.separator + _inode.charAt(1)
                + java.io.File.separator + _inode + "." + ext;

        if (!UtilMethods.isSet(realPath))
            return Config.CONTEXT.getRealPath(path);
        else
            return path;
    	
    }
    
    /**
     * This method returns the path for the file assets directory
     * @return
     */
    public static String getRealAssetPath(){
    	
    	String realPath = null;
		String assetPath = null;
		try {
            realPath = Config.getStringProperty("ASSET_REAL_PATH");
        } catch (Exception e) { }
        try {
            assetPath = Config.getStringProperty("ASSET_PATH");
        } catch (Exception e) { }
        
        if(!UtilMethods.isSet(realPath)){
        	return Config.CONTEXT.getRealPath(assetPath);
        }else{
        	return realPath;
        }
    }

    public static String getRelativeAssetsRootPath() {
        String path = "";

        path = Config.getStringProperty("ASSET_PATH");

        return path;
    }

    public static String getRealAssetsRootPath() {
        String realPath = Config.getStringProperty("ASSET_REAL_PATH");
        if (UtilMethods.isSet(realPath) && !realPath.endsWith(java.io.File.separator))
            realPath = realPath + java.io.File.separator;
        if (!UtilMethods.isSet(realPath))
            return Config.CONTEXT.getRealPath(getRelativeAssetsRootPath());
        else
            return realPath;
    }

    // remove the uri from a single cache
    /*
     * Never used methods
     * 
     * private synchronized static void removeURI(Object key, java.util.Map map) {
     * map.remove(key); }
     * 
     * private static long howBigIsCache(java.util.Map map) {
     * 
     * long size = 0;
     * 
     * for (Iterator it = map.entrySet().iterator(); it.hasNext();) { Map.Entry
     * me = (Map.Entry) it.next(); size += me.getKey().toString().length(); size +=
     * me.getValue().toString().length(); } return size; }
     */

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFilesByCondition(String condition) {

        DotHibernate dh = new DotHibernate(File.class);
        dh.setQuery("from inode in class class com.dotmarketing.portlets.files.model.File where type='file_asset' and " + condition
                + " order by file_name, sort_order");
        return dh.list();
    }
    
	public static java.util.List<File> getFilesPerRoleParentAndCondition(Role[] roles,
            com.dotmarketing.portlets.folders.model.Folder folderParent, String condition, User user) 
    {
		String order = "";
		return getFilesPerRoleParentAndCondition(roles, folderParent, condition, user, order);
	}

    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFilesPerRoleParentAndCondition(Role[] roles,
            com.dotmarketing.portlets.folders.model.Folder folderParent, String condition, User user, String order) {
    	PermissionAPI permissionAPI = APILocator.getPermissionAPI();
        java.util.List<File> entries = new java.util.ArrayList<File>();
               
        java.util.List permissions = new ArrayList();
		try {
			permissions = permissionAPI.getPermissionIdsFromRoles(folderParent, roles, user);
		} catch (DotDataException e) {
			Logger.error(FileFactory.class, "Could not load permissions : ",e);
		}
        
        if (permissions.contains(String.valueOf(PERMISSION_READ))) {
            // read permission
            entries = InodeFactory.getChildrenClassByConditionAndOrderBy(folderParent, File.class, condition,order);
        }
        return entries;
    }
    
    @SuppressWarnings("unchecked")
	public static java.util.List<File> getFilesByParentFolderPerRoleAndCondition(Role[] roles,
            Folder folderParent, String condition, User user) throws DotDataException {
    	PermissionAPI permissionAPI = APILocator.getPermissionAPI();
        List<File> entries = new ArrayList<File>();
		
		List<File> elements = InodeFactory.getChildrenClassByCondition(folderParent, File.class, condition);;
		for(File file : elements){
//			List permissions = permissionAPI.getPermissionIdsFromRoles(file, roles, user);
			
			if (permissionAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_READ, user, true)) {				
				entries.add(file);
			}
		}
		
		return entries;
    }

    /**
     * Move a file into the given directory
     * 
     * @param file
     *            File to be moved
     * @param parent
     *            Destination Folder
     * @return true if move success, false otherwise
     */
    public static boolean moveFile(File file, Folder parent) {
    
		Identifier identifier = com.dotmarketing.factories.IdentifierFactory.getParentIdentifier(file);

		// gets working container
		File workingWebAsset = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
		// gets live container
		File liveWebAsset = (File) IdentifierFactory.getLiveChildOfClass(identifier, File.class);

		// checks if another identifer with the same name exists in the same
		// folder
		if (FileFactory.existsFileName(parent, file.getFileName())) {
			return false;
		}

		// assets cache
		if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
			LiveCache.removeAssetFromCache(liveWebAsset);
		}
		WorkingCache.removeAssetFromCache(workingWebAsset);

		// gets old parent
		Folder oldParent = (Folder) InodeFactory.getParentOfClass(workingWebAsset, Folder.class);

		oldParent.deleteChild(workingWebAsset);
		if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
			oldParent.deleteChild(liveWebAsset);
		}
		//add new Parent
		parent.addChild(workingWebAsset);
		if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
			parent.addChild(liveWebAsset);
		}

		// gets identifier for this webasset and changes the uri and
		// persists it
		HostAPI hostAPI = APILocator.getHostAPI();
		User systemUser;
		Host newHost;
		try {
			systemUser = APILocator.getUserAPI().getSystemUser();
			newHost = hostAPI.findParentHost(parent, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(FileFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
			
		} catch (DotSecurityException e) {
			Logger.error(FileFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
		identifier.setHostId(newHost.getIdentifier());
		identifier.setURI(workingWebAsset.getURI(parent));
		InodeFactory.saveInode(identifier);
		
		IdentifierCache.removeFromIdCacheByInode(identifier.getInode());
		IdentifierCache.removeFromIdCacheByInode(liveWebAsset.getInode());
//		IdentifierCache.addAssetToIdentifierCache(liveWebAsset);
		
		// Add to Preview and Live Cache
		if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
			LiveCache.removeAssetFromCache(liveWebAsset);
			LiveCache.addToLiveAssetToCache(liveWebAsset);
		}
		WorkingCache.removeAssetFromCache(workingWebAsset);
		WorkingCache.addToWorkingAssetToCache(workingWebAsset);

        if (file.isShowOnMenu()) {
        	//existing folder with different show on menu ... need to regenerate menu
            //RefreshMenus.deleteMenus();
        	RefreshMenus.deleteMenu(oldParent,parent);
        }        
        
        return true;
    
    }
    
    /**
     * Copy a file into the given directory
     * 
     * @param file
     *            File to be copied
     * @param parent
     *            Destination Folder
     * @return true if copy success, false otherwise
     */
    public static File copyFile(File file, Folder parent) {

        File newFile = new File();
        try {

            newFile.copy(file);
            newFile.setLocked(false);
            newFile.setLive(false);

            // gets filename before extension
            String fileName = com.dotmarketing.util.UtilMethods.getFileName(file.getFileName());
            // gets file extension
            String fileExtension = com.dotmarketing.util.UtilMethods.getFileExtension(file.getFileName());

            // Setting file name
            if (FileFactory.existsFileName(parent, file.getFileName())) {
                // adds "copy" word to the filename
                newFile.setFileName(fileName + "_copy." + fileExtension);
                newFile.setFriendlyName(file.getFriendlyName() + " (COPY) ");
            } else {
                newFile.setFileName(fileName + "." + fileExtension);
            }

            // persists the webasset
            InodeFactory.saveInode(newFile);

            saveFileData(file, newFile,null);
            
            // Adding to the parent folder
            parent.addChild(newFile);

            // creates new identifier for this webasset and persists it
            Identifier newIdentifier = IdentifierFactory.createNewIdentifier(newFile, parent);

            Logger.debug(FileFactory.class, "identifier=" + newIdentifier.getURI());

            WorkingCache.removeAssetFromCache(newFile);
            WorkingCache.addToWorkingAssetToCache(newFile);
            PermissionAPI permissionAPI = APILocator.getPermissionAPI();
            // Copy permissions
            permissionAPI.copyPermissions(file, newFile);
            
        } catch (Exception e) {
            throw new DotRuntimeException("An error ocurred trying to copy the file.", e);
        }
        return newFile;


    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static boolean renameFile (File file, String newName, User user) throws Exception {
		PermissionAPI permissionAPI = APILocator.getPermissionAPI();
    	// Checking permissions
    	if (!permissionAPI.doesUserHavePermission(file, PERMISSION_WRITE, user))
    		throw new ActionException(WebKeys.USER_PERMISSIONS_EXCEPTION);

    	//getting old file properties
    	String oldFileName = file.getFileName();
    	String ext = UtilMethods.getFileExtension(oldFileName);
    	Folder folder = (Folder)InodeFactory.getParentOfClass(file, Folder.class);

    	Identifier ident = IdentifierFactory.getIdentifierByInode(file);

    	String newFileName = newName;
    	if(UtilMethods.isSet(ext)){
    		newFileName = newFileName + "." + ext;
    	}

    	if(FileFactory.existsFileName(folder, newFileName) || file.isLocked())
    		return false;

    	List<File> versions = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(ident, File.class);
    	
    	boolean islive = false;
    	

		for (File f : versions) {

    		
	    	// sets filename for this new file
	    	f.setFileName(newFileName);

	    	InodeFactory.saveInode(f);

	    	if (f.isLive())
	    		islive = true;
    	}
    	
		LiveCache.removeAssetFromCache(file);
		WorkingCache.removeAssetFromCache(file);
   		IdentifierCache.removeAssetFromIdCache(file);

		ident.setURI(folder.getPath() + newFileName);
    	InodeFactory.saveInode(ident);

    	if (islive){
    		LiveCache.removeAssetFromCache(file);
    		LiveCache.addToLiveAssetToCache(file);
    	}
    	WorkingCache.removeAssetFromCache(file);
   		WorkingCache.addToWorkingAssetToCache(file);
   		IdentifierCache.removeAssetFromIdCache(file);
   		IdentifierCache.addVersionableToIdentifierCache(file);
    	
    	//RefreshMenus.deleteMenus();
   		RefreshMenus.deleteMenu(file);

    	return true;

    }
    
	/**
	 * This method will save the newFile as the new working version for the given identifier and data 
	 * if the given file is new it will copy the permissions from the folder and 
	 * if the file given is set to live = true it will publish it
	 * @param newFile New File to save
	 * @param data New data to store
	 * @param folder Parent folder to be assigned to the new file
	 * @param identifier Identifier of the asset
	 * @param user User how is making the modification if null no user id will be set as the last modified user
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings("unchecked")
	public static File saveFile (File newFile, java.io.File dataFile, Folder folder, Identifier identifier, User user) 
    	throws Exception {
    	File workingFile = null;
    	if (identifier == null || !InodeUtils.isSet(identifier.getInode())) {
    		
    		WebAssetFactory.createAsset(newFile, user == null?"":user.getUserId(), folder);
    		saveFileData(newFile, null, dataFile);
    		FileCache.removeFile(newFile);
    		workingFile = newFile;
    	} else {
    		if(UtilMethods.isSet(dataFile)){
				WebAssetFactory.createAsset(newFile, user == null?"":user.getUserId(), folder, identifier,true);
				workingFile = newFile;
				if(UtilMethods.isSet(dataFile))
				saveFileData(workingFile,null, dataFile);
	
				List workingAssets = null;
				workingAssets = IdentifierFactory.getWorkingChildrenOfClass(identifier, workingFile.getClass());
	
				// if there is more than one working asset
				if (workingAssets.size() > 1) {
					Iterator iter = workingAssets.iterator();
					while (iter.hasNext()) {
						WebAsset webAsset = (WebAsset) iter.next();
						if (!webAsset.getInode().equals(workingFile.getInode())) {
							webAsset.setWorking(false);
							InodeFactory.saveInode(webAsset);
						}
					}
				}
			}else{
				WebAssetFactory.createAsset(newFile, user == null?"":user.getUserId(), folder, identifier, false);
    			workingFile = (File) WebAssetFactory.saveAsset(newFile, identifier);
    			saveFileData(workingFile, newFile, null);
    			FileCache.removeFile(workingFile);
    			// Get parents of the old version so you can update the working
    			// information to this new version.
    			java.util.List<Tree> parentTrees = TreeFactory.getTreesByChild(newFile);
    			
    			// update parents to new version delete old versions parents if
    			// not live.
    			for (Tree tree : parentTrees) {
    				// to keep relation types from parent only if it exists
    				Tree newTree = TreeFactory.getTree(tree.getParent(), workingFile.getInode());
    				if (!InodeUtils.isSet(newTree.getChild())) {
    					newTree.setParent(tree.getParent());
    					newTree.setChild(workingFile.getInode());
    					newTree.setRelationType(tree.getRelationType());
    					newTree.setTreeOrder(0);
    					TreeFactory.saveTree(newTree);
    				}
    			}
			}
    	}
    	FileCache.removeFile(workingFile);
		if (workingFile.isLive()){
			LiveCache.removeAssetFromCache(workingFile);
			LiveCache.addToLiveAssetToCache(workingFile);
		}else{
			LiveCache.removeAssetFromCache(newFile);
			LiveCache.addToLiveAssetToCache(newFile);
		}
		WorkingCache.removeAssetFromCache(workingFile);
		WorkingCache.addToWorkingAssetToCache(workingFile);
		
    	return workingFile;
    }
    
	public static byte[] getFileData(File file) throws IOException {
		// creates the new file as
		// inode{1}/inode{2}/inode.file_extension
		java.io.File workingFile = getAssetIOFile(file);
		byte[] currentData = new byte[0];
		FileInputStream is = new FileInputStream(workingFile);
		int size = is.available();
		currentData = new byte[size];
		is.read(currentData);
		return currentData;
	}
	
    /**
     * This method will copy the file data from file to version if version is not null and version inode > 0
     * and will replace current file data if newData passed is not null
     * @param file 
     * @param version
     * @param newData
     * @throws Exception
     */
	public static void saveFileData(File file, File destination, java.io.File newDataFile) 
		throws Exception {
		
		String fileName = file.getFileName();

		//This was added for http://jira.dotmarketing.net/browse/DOTCMS-5390 but this breaks the original intent of the 
		//method. See the doc for the method above. Caused http://jira.dotmarketing.net/browse/DOTCMS-5539 so commented out.
//		if(newDataFile ==null || newDataFile.length() ==0){
//			throw new DotStateException("Null or 0 lenght java.io.file passed in for file:" + file.getInode());
//		}
		
		
		String assetsPath = FileFactory.getRealAssetsRootPath();
		new java.io.File(assetsPath).mkdir();

		// creates the new file as
		// inode{1}/inode{2}/inode.file_extension
		java.io.File workingFile = getAssetIOFile(file);
		
		//http://jira.dotmarketing.net/browse/DOTCMS-1873
		//To clear velocity cache
		DotResourceCache vc = CacheLocator.getVeloctyResourceCache();
        vc.remove(ResourceManager.RESOURCE_TEMPLATE + workingFile.getPath());
		
		//If a new version was created, we move the current data to the new version
		if (destination != null && InodeUtils.isSet(destination.getInode())) {
			java.io.File newVersionFile = getAssetIOFile(destination);
//			FileUtil.copyFile(workingFile, newVersionFile);
			FileUtils.copyFile(workingFile, newVersionFile);
//			FileInputStream is = new FileInputStream(workingFile);
//			FileChannel channelFrom = is.getChannel(); 
//			java.io.File newVersionFile = getAssetIOFile(destination);
//			FileChannel channelTo = new FileOutputStream(newVersionFile).getChannel();
//			channelFrom.transferTo(0, channelFrom.size(), channelTo);
//			channelTo.force(false);
//			channelTo.close();
//			channelFrom.close();
		}
		
		if (newDataFile != null) {
			// Saving the new working data
			FileUtils.copyFile(newDataFile, workingFile);

	
			file.setSize((int) newDataFile.length());

			
			// checks if it's an image
			if (UtilMethods.isImage(fileName)) {
				InputStream in = null;
				try{
					// gets image height
					in = new BufferedInputStream(new FileInputStream(workingFile));
					byte[] imageData = new byte[in.available()];
					in.read(imageData);
					Image image = Toolkit.getDefaultToolkit().createImage(imageData);
					MediaTracker mediaTracker = new MediaTracker(new Container());
					mediaTracker.addImage(image, 0);
					mediaTracker.waitForID(0);
					int imageWidth = image.getWidth(null);
					int imageHeight = image.getHeight(null);
					
					in.close();
					in = null;
					// gets image width
					file.setHeight(imageHeight);
					file.setWidth(imageWidth);
				}
				catch(Exception e){
					Logger.error(FileFactory.class, "Unable to read image " + workingFile + " : " + e.getMessage());
				}
				finally{
					if(in != null){
						try{
							in.close();
						}
						catch(Exception e){
							Logger.error(FileFactory.class, "Unable to close image " + e.getMessage());
						}
					}
					
				}
			}
			//Wiping out the thumbnails and resized versions		
			//http://jira.dotmarketing.net/browse/DOTCMS-5911
			String inode = file.getInode();
			if(UtilMethods.isSet(inode)){
				java.io.File tumbnailDir = new java.io.File(Config.CONTEXT.getRealPath("/assets/dotGenerated/" + inode.charAt(0) + "/" + inode.charAt(1)));
				if(tumbnailDir!=null){
					java.io.File[] files = tumbnailDir.listFiles();
					if(files!=null){
						for (java.io.File iofile : files) {
							try {
								if(iofile.getName().startsWith("dotGenerated_")){
									iofile.delete();
								}
							} catch (SecurityException e) {
								Logger.error(FileFactory.class, "EditFileAction._saveWorkingFileData(): " + iofile.getName()
										+ " cannot be erased. Please check the file permissions.");
							} catch (Exception e) {
								Logger.error(FileFactory.class, "EditFileAction._saveWorkingFileData(): " + e.getMessage());
							}
						}
					}
				}
			}
		}		
	}
	
	public class ThumbnailsFileNamesFilter implements FilenameFilter {

		List<File> versions;
		
		@SuppressWarnings("unchecked")
		public ThumbnailsFileNamesFilter (Identifier fileIden) {
			versions = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(fileIden, File.class);
		}
		
		public boolean accept(java.io.File dir, String name) {
			
			for (File file : versions) {
				if (name.startsWith(String.valueOf(file.getInode()) + "_thumb") ||
						name.startsWith(String.valueOf(file.getInode()) + "_resized"))
					return true;
			}
			return false;
		}
		
	}
	
    public static java.io.File getAssetIOFile (File file) throws IOException {
    	
		String fileName = file.getFileName();
		String suffix = UtilMethods.getFileExtension(fileName);
		
		String assetsPath = FileFactory.getRealAssetsRootPath();
		String fileInode = file.getInode();

		// creates the path where to save the working file based on the inode
		String fileFolderPath = String.valueOf(fileInode);
		if (fileFolderPath.length() == 1) {
			fileFolderPath = fileFolderPath + "0";
		}
		
		fileFolderPath = assetsPath + java.io.File.separator + 
			fileFolderPath.substring(0, 1) + java.io.File.separator + 
			fileFolderPath.substring(1, 2);
		
		new java.io.File(fileFolderPath).mkdirs();
		
		String filePath = fileFolderPath + java.io.File.separator + 
			fileInode + "." + suffix;
		
		// creates the new file as
		// inode{1}/inode{2}/inode.file_extension
		java.io.File assetFile = new java.io.File(filePath);
		if (!assetFile.exists())
			assetFile.createNewFile();
		
		return assetFile;
    }
    
	public static String getMimeType (String filename) {
        if(filename != null){
            filename = filename.toLowerCase();
        }
        
        String mimeType = Config.CONTEXT.getMimeType(filename);
        if (!UtilMethods.isSet(mimeType)) {
			mimeType = com.dotmarketing.portlets.files.model.File.UNKNOWN_MIME_TYPE;
		}
        
		return mimeType;
	}

	public static void publishFile(File file, User user, boolean respectFrontendRoles) throws WebAssetException, DotSecurityException, DotDataException {
		
		PublishFactory.publishAsset(file, user, respectFrontendRoles);
		
	}
    
}

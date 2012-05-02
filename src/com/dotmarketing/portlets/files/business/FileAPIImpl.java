package com.dotmarketing.portlets.files.business;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.runtime.resource.ResourceManager;

import com.dotcms.enterprise.cmis.QueryResult;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.BaseWebAssetAPI;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.business.IdentifierAPI;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.query.QueryUtil;
import com.dotmarketing.business.query.ValidationException;
import com.dotmarketing.business.query.GenericQueryFactory.Query;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.velocity.DotResourceCache;
import com.liferay.portal.model.User;

public class FileAPIImpl extends BaseWebAssetAPI implements FileAPI {

	protected PermissionAPI permissionAPI;
	protected FileFactory fileFactory;
	protected IdentifierAPI identifierAPI;
	protected ContentletAPI contentletAPI;
	protected FolderAPI folderAPI;
	
	public FileAPIImpl () {
		permissionAPI = APILocator.getPermissionAPI();
		fileFactory = FactoryLocator.getFileFactory();
		identifierAPI = APILocator.getIdentifierAPI();
		contentletAPI = APILocator.getContentletAPI();
		folderAPI = APILocator.getFolderAPI();
	}
	
	public File copy(File source, Folder destination, boolean forceOverwrite, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException {
		
		if (!permissionAPI.doesUserHavePermission(source, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to read the source file.");
		}

		if (!permissionAPI.doesUserHavePermission(destination, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to write in the destination folder.");
		}

		// gets filename before extension
		String fileName = UtilMethods.getFileName(source.getFileName());
		// gets file extension
		String fileExtension = UtilMethods.getFileExtension(source.getFileName());

		boolean isNew = false;
		File newFile;
		if (forceOverwrite) {
			newFile = getWorkingFileByFileName(source.getFileName(), destination);
			if (newFile == null) {
				isNew = true;
			}
		} else {
			isNew = true;
		}

		try {
			newFile = new File();
			newFile.copy(source);
			newFile.setLocked(false);
			newFile.setLive(source.isLive());
			
			// Setting file name
			if (!forceOverwrite) {
				newFile.setFileName(getCopyFileName(fileName, fileExtension, destination));
				
				if (!UtilMethods.getFileName(newFile.getFileName()).equals(fileName))
					newFile.setFriendlyName(source.getFriendlyName() + " (COPY) ");
			}

			if (isNew) {
				// persists the webasset
				save(newFile);

				saveFileData(source, newFile, null);

				// Adding to the parent folder
				TreeFactory.saveTree(new Tree(destination.getInode(), newFile.getInode()));

				// creates new identifier for this webasset and persists it
				Identifier newIdentifier = IdentifierFactory.createNewIdentifier(newFile, destination);

				Logger.debug(FileFactory.class, "identifier=" + newIdentifier.getURI());
			} else {
				java.io.File sourceFile = getAssetIOFile(source);
				java.io.File uploadedFile = java.io.File.createTempFile(fileName, "." + fileExtension);
				FileUtils.copyFile(sourceFile, uploadedFile);

				newFile = saveFile(newFile, uploadedFile, destination, user, respectFrontendRoles);
			}
			// Copy permissions
			permissionAPI.copyPermissions(source, newFile);

			save(newFile);
		} catch (Exception e) {
			throw new DotRuntimeException("An error ocurred trying to copy the file.", e);
		}

		return newFile;
	}

	@SuppressWarnings("unchecked")
	public File getWorkingFileByFileName(String fileName, Folder folder) {
		List<File> files = getChildrenClassByCondition(folder, File.class, "working=" + DbConnectionFactory.getDBTrue()
				+ " and file_name='" + fileName + "'");

		if (0 < files.size())
			return files.get(0);

		return null;
	}

	@SuppressWarnings("unchecked")
	private String getCopyFileName(String fileName, String fileExtension, Folder folder) {
		String result = new String(fileName);

		List<File> files = getChildrenClassByCondition(folder, File.class, DbConnectionFactory.getDBTrue() + "="
				+ DbConnectionFactory.getDBTrue());

		boolean isValidFileName = false;
		String temp;

		while (!isValidFileName) {
			isValidFileName = true;
			temp = result + "." + fileExtension;

			for (File file : files) {
				if (file.getFileName().equals(temp)) {
					isValidFileName = false;

					break;
				}
			}

			if (!isValidFileName)
				result += "_copy";
			else
				result = temp;
		}

		return result;
	}

	private void save(File file) throws DotDataException {
		fileFactory.save(file);
	}

	protected void save(WebAsset currWebAsset) throws DotDataException {
		save((File) currWebAsset);
	}

	protected String getRelativeAssetsRootPath() {
		String path = "";
		path = Config.getStringProperty("ASSET_PATH");
		return path;
	}

	protected String getRealAssetsRootPath() {
		String realPath = Config.getStringProperty("ASSET_REAL_PATH");
		if (UtilMethods.isSet(realPath) && !realPath.endsWith(java.io.File.separator))
			realPath = realPath + java.io.File.separator;
		if (!UtilMethods.isSet(realPath))
			return Config.CONTEXT.getRealPath(getRelativeAssetsRootPath());
		else
			return realPath;
	}

	public java.io.File getAssetIOFile(File file) throws IOException {

		String fileName = file.getFileName();
		String suffix = UtilMethods.getFileExtension(fileName);

		String assetsPath = getRealAssetsRootPath();
		String fileInode = file.getInode();

		// creates the path where to save the working file based on the inode
		String fileFolderPath = String.valueOf(fileInode);
		if (fileFolderPath.length() == 1) {
			fileFolderPath = fileFolderPath + "0";
		}

		fileFolderPath = assetsPath + java.io.File.separator + fileFolderPath.substring(0, 1) + java.io.File.separator
				+ fileFolderPath.substring(1, 2);

		new java.io.File(fileFolderPath).mkdirs();

		String filePath = fileFolderPath + java.io.File.separator + fileInode + "." + suffix;

		// creates the new file as
		// inode{1}/inode{2}/inode.file_extension
		java.io.File assetFile = new java.io.File(filePath);
		if (!assetFile.exists())
			assetFile.createNewFile();

		return assetFile;
	}

	protected void saveFileData(File file, File destination, java.io.File newDataFile) throws DotDataException,
			IOException {

		String fileName = file.getFileName();

		String assetsPath = getRealAssetsRootPath();
		new java.io.File(assetsPath).mkdir();

		// creates the new file as
		// inode{1}/inode{2}/inode.file_extension
		java.io.File workingFile = getAssetIOFile(file);

		// To clear velocity cache
		DotResourceCache vc = CacheLocator.getVeloctyResourceCache();
		vc.remove(ResourceManager.RESOURCE_TEMPLATE + workingFile.getPath());

		// If a new version was created, we move the current data to the new
		// version
		if (destination != null && InodeUtils.isSet(destination.getInode())) {
			java.io.File newVersionFile = getAssetIOFile(destination);
			FileUtils.copyFile(workingFile, newVersionFile);
		}

		if (newDataFile != null) {
			// Saving the new working data
			FileUtils.copyFile(newDataFile, workingFile);

			// checks if it's an image
			if (UtilMethods.isImage(fileName)) {

				// gets image height
				BufferedImage img = javax.imageio.ImageIO.read(workingFile);
				int height = img.getHeight();
				file.setHeight(height);

				// gets image width
				int width = img.getWidth();
				file.setWidth(width);

			}

			// Wiping out the thumbnails and resized versions
			String folderPath = workingFile.getParentFile().getAbsolutePath();
			Identifier identifier = identifierAPI.findFromInode(file.getIdentifier());

			java.io.File directory = new java.io.File(folderPath);
			java.io.File[] files = directory.listFiles(new ThumbnailsFileNamesFilter(identifier));

			for (java.io.File iofile : files) {
				try {
					iofile.delete();
				} catch (SecurityException e) {
					Logger.error(FileAPIImpl.class, "FileAPIImpl.saveFileData(): " + iofile.getName()
							+ " cannot be erased. Please check the file permissions.");
				} catch (Exception e) {
					Logger.error(FileAPIImpl.class, "FileAPIImpl.saveFileData(): " + e.getMessage());
				}
			}
		}
	}

	protected String getMimeType(String filename) {
		if (filename != null) {
			filename = filename.toLowerCase();
		}
		
		String mimeType = Config.CONTEXT.getMimeType(filename);
		if (!UtilMethods.isSet(mimeType)) {
			mimeType = com.dotmarketing.portlets.files.model.File.UNKNOWN_MIME_TYPE;
		}
		
		return mimeType;
	}

	public File saveFile(File file, java.io.File uploadedFile, Folder folder, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException {

		String fileName = UtilMethods.getFileName(file.getFileName());
		File currentFile = getWorkingFileByFileName(file.getFileName(), folder);
		
		boolean fileExists = (currentFile != null) && InodeUtils.isSet(currentFile.getInode());
		
		if (fileExists) {
			if (!permissionAPI.doesUserHavePermission(currentFile, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
				throw new DotSecurityException("You don't have permission to read the source file.");
			}
		}

		if (!permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to write in the destination folder.");
		}

		File workingFile = null;

		try {
			long uploadFileMaxSize = Long.parseLong(Config.getStringProperty("UPLOAD_FILE_MAX_SIZE"));

			// Checking the max file size
			if ((uploadedFile != null) && ((uploadFileMaxSize > 0) && (uploadedFile.length() > uploadFileMaxSize))) {
				if (currentFile != null)
					unLockAsset(currentFile);
				throw new DotDataException("Uploaded file is bigger than the file max size allowed.");
			}

			// CHECK THE FOLDER PATTERN
			if (UtilMethods.isSet(file.getFileName()) && !FolderFactory.matchFilter(folder, file.getFileName())) {
				// when there is an error saving should unlock working asset
				if (currentFile != null)
					unLockAsset(currentFile);
				throw new DotDataException("message.file_asset.error.filename.filters");
			}

			// Setting some flags
			boolean editFile = false;
			boolean newUploadedFile = true;

			// checks if the file is new or it's being edited
			if (fileExists) {
				editFile = true;
				// if it's being edited it keeps the same file name as the
				// current one
				fileName = currentFile.getFileName();
			}

			// checks if another identifier with the same name exists in the
			// same
			// folder
			if (!editFile && (getWorkingFileByFileName(fileName, folder) != null)) {
				throw new DotDataException("message.file_asset.error.filename.exists");
			}

			// to check if a file is being uploaded
			if (fileName.length() == 0) {
				newUploadedFile = false;
			}

			// getting mime type
			if (editFile && newUploadedFile && (file.getMimeType() != null) && (currentFile != null) && (!file.getMimeType().equals(currentFile.getMimeType()))) {
				// when there is an error saving should unlock working asset
				unLockAsset(currentFile);
				throw new DotDataException("message.file_asset.error.mimetype");
			}

			save(file);
			//fileFactory.deleteFromCache(file);
			// get the file Identifier
			Identifier ident = null;
			if (fileExists) {
				ident = IdentifierCache.getIdentifierFromIdentifierCache(currentFile);
				fileFactory.deleteFromCache(currentFile);
			} else {
				ident = new Identifier();
			}
			// Saving the file, this creates the new version and save the new
			// data
			if (newUploadedFile && uploadedFile.length() > 0) {
				
				workingFile = saveFile(file, uploadedFile, folder, ident, user, respectFrontendRoles);

			} else {
				workingFile = saveFile(file, null, folder, ident, user, respectFrontendRoles);
			}
			uploadedFile.delete();
			fileFactory.deleteFromCache(workingFile);
			ident = IdentifierCache.getIdentifierFromIdentifierCache(workingFile);

			// Refreshing the menues
			if (file.isShowOnMenu()) {
				// existing folder with different show on menu ... need to
				// regenerate menu
				RefreshMenus.deleteMenu(file);
			}

		} catch (IOException e) {
			Logger.error(this, "\n\n\nEXCEPTION IN FILE SAVING!!! " + e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage());
		}

		return workingFile;
	}

	protected File saveFile(File newFile, java.io.File dataFile, Folder folder, Identifier identifier, User user,
			boolean respectFrontendRoles) throws DotDataException, DotSecurityException, IOException {

		if (!permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to write in the destination folder.");
		}

		
		File workingFile = getWorkingFileById(identifier.getInode(), user, respectFrontendRoles);
		if(InodeUtils.isSet(newFile.getInode())) {
			File fileCopy = new File();
			fileCopy.copy(newFile);
			newFile = fileCopy;
		}
		if(dataFile != null && dataFile.length() > 0){
			newFile.setSize((int)dataFile.length());
		}
		if ((identifier == null) || !InodeUtils.isSet(identifier.getInode())) {
			createAsset(newFile, user == null ? "" : user.getUserId(), folder);
			saveFileData(newFile, null, dataFile);
			fileFactory.deleteFromCache(newFile);
			workingFile = newFile;
		} else {
			createAsset(newFile, user == null ? "" : user.getUserId(), folder, identifier, false);
			workingFile = (File) saveAsset(newFile, identifier);
			if (UtilMethods.isSet(dataFile)) {
				saveFileData(workingFile, newFile, dataFile);
			} else {
				saveFileData(workingFile, newFile, null);
			}
			fileFactory.deleteFromCache(workingFile);
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

		return workingFile;
	}

	public boolean delete(File file, User user, boolean respectFrontendRoles) throws DotSecurityException, Exception {
		if(permissionAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			return deleteAsset(file);
		} else {
			throw new DotSecurityException(WebKeys.USER_PERMISSIONS_EXCEPTION);
		}
	}

	public List<File> getAllHostFiles(Host parentHost, boolean live, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {

		List<File> files = fileFactory.getAllHostFiles(parentHost, live);
		return permissionAPI.filterCollection(files, PermissionAPI.PERMISSION_READ, respectFrontendRoles, user);

	}

	public List<File> getFolderFiles(Folder parentFolder, boolean live, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException {

		List<File> files = fileFactory.getFolderFiles(parentFolder, live);
		return permissionAPI.filterCollection(files, PermissionAPI.PERMISSION_READ, respectFrontendRoles, user);
		
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
				
		return QueryUtil.DBSearch(query, dbColToObjectAttribute, null, user, true,respectFrontendRoles);
	}

	public File getWorkingFileById(String fileId, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		File file = fileFactory.getWorkingFileById(fileId);
		if(file == null)
			return file;
		if(!permissionAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles))
			throw new DotSecurityException("User " + user.getUserId() + "has no permissions to read file id " + fileId);
		
		return file;
	}
	
	public File get(String inode, User user, boolean respectFrontendRoles) throws DotHibernateException, DotSecurityException, DotDataException {
		File file = fileFactory.get(inode);
		
		if (!permissionAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
			throw new DotSecurityException(WebKeys.USER_PERMISSIONS_EXCEPTION);
		}
		
		return file;
	}

	public Folder getFileFolder(File file) throws DotDataException, DotSecurityException {
		if(!file.isWorking()) {
			file = fileFactory.getWorkingFileById(file.getIdentifier());
		}
		if(file == null)
			return null;
		return fileFactory.getFileFolder(file);
	}

	public List<File> findFiles(User user, boolean includeArchived,
			Map<String, Object> params, String hostId, String inode, String identifier, String parent,
			int offset, int limit, String orderBy) throws DotSecurityException,
			DotDataException {
		return fileFactory.findFiles(user, includeArchived, params, hostId, inode, identifier, parent, offset, limit, orderBy);
	}

}
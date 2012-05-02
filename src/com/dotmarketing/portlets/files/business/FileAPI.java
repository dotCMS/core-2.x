package com.dotmarketing.portlets.files.business;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.query.ValidationException;
import com.dotmarketing.business.query.GenericQueryFactory.Query;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.liferay.portal.model.User;

/**
 * Interface for the API to manage Files and Assets.
 */

public interface FileAPI {
	
	/**
	 * Copies a file to a specified folder.
	 * 
	 * @param source
	 * @param destination
	 * @param forceOverwrite
	 *            If true, will overwrite the file if already exists in the
	 *            folder otherwise creates a new copy renaming the file.
	 * @param user
	 * @param respectFrontendRoles
	 * @return File
	 * @exception DotDataException
	 * @exception DotSecurityException
	 */
	public File copy(File source, Folder destination, boolean forceOverwrite, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException;

	/**
	 * Retrieves a working version of file with the specified filename and folder.
	 * 
	 * @param fileName
	 * @param folder
	 * @return File
	 */
	public File getWorkingFileByFileName(String fileName, Folder folder);
	
	/**
	 * Saves the file. If the file does not exists in the specified folder, it
	 * will create a new one. If does exists, it will be added as a new version.
	 * 
	 * @param file
	 * @param uploadedFile
	 * @param categories
	 * @param folder
	 * @param user
	 * @param respectFrontendRoles
	 * @return File
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public File saveFile(File file, java.io.File fileData, Folder folder, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException; 
	
	/**
	 * Delete specified file.
	 * 
	 * @param file
	 * @param user
	 * @param respectFrontendRoles
	 * @return boolean
	 * @throws DotSecurityException
	 * @throws Exception
	 */
	public boolean delete(File file, User user, boolean respectFrontendRoles) throws DotSecurityException, Exception;
	
	/**
	 * Get the file system file related to the dotcms file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public java.io.File getAssetIOFile(File file) throws IOException;
	/**
	 * Retrieves a list of all files attached to the given host
	 * @param parentHost The parent host of the files
	 * @param live If set to true then it retrieves the file live versions, if not it will retrieve the working versions
	 * @return
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public List<File> getAllHostFiles(Host parentHost, boolean live, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException;

	/**
	 * Retrieves a list of all files directly attached (direct children) to the given folder
	 * @param parentFolder The parent folder of the files
	 * @param live If set to true then it retrieves the file live versions, if not it will retrieve the working versions
	 * @return
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public List<File> getFolderFiles(Folder parentFolder, boolean live, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException;
	
		/**
	 * Will search the DB.  Use the SQLQueryBuilderFactory or GenericQueryBuiilderFactory to build
	 * your query object
	 * @param query
	 * @param user
	 * @param respectFrontendRoles
	 * @return
	 * @throws ValidationException
	 * @throws DotDataException
	 */
	public List<Map<String, Serializable>> DBSearch(Query query, User user,boolean respectFrontendRoles) throws ValidationException,DotDataException;

	/**
	 * Retrieves the working version of the page given its identifier
	 * @param fileId
	 * @return
	 * @throws DotSecurityException 
	 * @throws DotDataException 
	 */
	public File getWorkingFileById(String fileId, User user,boolean respectFrontendRoles) throws DotDataException, DotSecurityException;

	
	/**
	 * Return the object File version with the specified inode
	 * 
	 * @param inode
	 * @param user
	 * @param respectFrontendRoles
	 * @return File
	 * @throws DotHibernateException
	 * @throws DotSecurityException
	 * @throws DotDataException
	 */
	public File get(String inode, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException, DotDataException;
	
	/**
	 * Retrieves the parent folder of the given file
	 * @param file
	 * @return
	 * @throws DotDataException
	 * @throws DotSecurityException
	 */
	public Folder getFileFolder(File file) throws DotDataException, DotSecurityException;
	

	/**
	 * Retrieves a paginated list of files the user can use 
	 * @param user
	 * @param includeArchived
	 * @param params
	 * @param hostId
	 * @param inode
	 * @param identifier
	 * @param parent
	 * @param offset
	 * @param limit
	 * @param orderBy
	 * @return
	 * @throws DotSecurityException
	 * @throws DotDataException
	 */
	public List<File> findFiles(User user, boolean includeArchived, Map<String,Object> params, String hostId, String inode, String identifier, String parent, int offset, int limit, String orderBy) throws DotSecurityException, DotDataException;
		
	
}
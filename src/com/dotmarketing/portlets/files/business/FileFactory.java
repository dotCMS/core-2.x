package com.dotmarketing.portlets.files.business;

import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.liferay.portal.model.User;

public interface FileFactory {
	
	/**
	 * Save file into a persistent repository
	 * 
	 * @param file
	 * @throws DotDataException
	 */
	public void save(File file) throws DotDataException;
	
	/**
	 * Delete file from persistent repository.
	 * 
	 * @param file
	 * @throws DotDataException
	 */
	public void delete(File file) throws DotDataException;
	
	/**
	 * Delete file from cache.
	 * 
	 * @param file
	 * @throws DotDataException
	 */
	public void deleteFromCache(File file) throws DotDataException;
	
	/**
	 * Retrieves the list of all files attached to a given host
	 * @param host The parent host
	 * @param live If true it will return the live versions of the files
	 * @return
	 * @throws DotDataException
	 */
	public List<File> getAllHostFiles(Host host, boolean live) throws DotDataException;
	
	/**
	 * Retrieves all files directly associated to the given folder
	 * 
	 * @param folder
	 * @param live
	 * @return
	 * @throws DotDataException
	 */
	public List<File> getFolderFiles(Folder folder, boolean live) throws DotDataException;
	
	/**
	 * Retrieves the working version of the file given its identifier, returns null if no file is found 
	 * @param id
	 * @return
	 * @throws DotDataException 
	 */
	public File getWorkingFileById(String id) throws DotDataException;
	
	
	/**
	 * Returns the object File with the specified inode
	 * 
	 * @param inode
	 * @return File
	 * @throws DotHibernateException
	 */
	public File get(String inode) throws DotHibernateException;
	
	/**
	 * Returns the parent folder of a given file
	 * @param file
	 * @return
	 * @throws DotDataException
	 */
	public Folder getFileFolder(File file) throws DotDataException;
	

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
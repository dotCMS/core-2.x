/**
 * 
 */
package com.dotmarketing.webdav;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

/**
 * @author Jason Tesser
 *
 */
public class ResourceFactorytImpl implements ResourceFactory {

	private DotWebdavHelper dotDavHelper;
	private static final String AUTOPUB_PATH = "/webdav/autopub";
	private static final String NONPUB_PATH = "/webdav/nonpub";
	private HostAPI hostAPI = APILocator.getHostAPI();
	
	public ResourceFactorytImpl() {
		super();
		dotDavHelper = new DotWebdavHelper();
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResourceFactory#getResource(java.lang.String, java.lang.String)
	 */
	public Resource getResource(String host, String url) {
		Logger.debug(this, "WebDav ResourceFactory: Host is " + host + " and the url is " + url);
		try{
			HibernateUtil.startTransaction();
			boolean isFolder = false;
			boolean isResource = false;
			boolean isWebDavRoot = url.equals(AUTOPUB_PATH) || url.equals(NONPUB_PATH) || url.equals(AUTOPUB_PATH + "/") || url.equals(NONPUB_PATH + "/");
			boolean autoPub = url.startsWith(AUTOPUB_PATH);
			boolean nonPub = url.startsWith(NONPUB_PATH);
			String actualPath = ""; 
			if(isWebDavRoot){
				WebdavRootResourceImpl wr = new WebdavRootResourceImpl();
				return wr;
			}else{
				if(autoPub){
					actualPath = url.replaceAll(AUTOPUB_PATH, "");
					if(actualPath.startsWith("/")){
						actualPath = actualPath.substring(1);
					}
				}else if(nonPub){
					actualPath = url.replaceAll(NONPUB_PATH, "");
					if(actualPath.startsWith("/")){
						actualPath = actualPath.substring(1);
					}
				}else{
					return null;
				}
				String[] splitPath = actualPath.split("/");
				if(splitPath != null && splitPath.length == 1){
					if(splitPath[0].equalsIgnoreCase("system")){
						SystemRootResourceImpl sys = new SystemRootResourceImpl();
						return sys;
					}else{
						HostResourceImpl hr = new HostResourceImpl(url,hostAPI.findByName(splitPath[0], APILocator.getUserAPI().getSystemUser(), false));
						return hr;
					}
				}
			}
			
			
			if(dotDavHelper.isTempResource(url)){
				java.io.File tempFile = dotDavHelper.loadTempFile(url);
				if(tempFile == null || !tempFile.exists()){
					return null;
				}else if(tempFile.isDirectory()){
						TempFolderResourceImpl tr = new TempFolderResourceImpl(url,tempFile,dotDavHelper.isAutoPub(url));
						return tr;
				}else{
					TempFileResourceImpl tr = new TempFileResourceImpl(tempFile,url,dotDavHelper.isAutoPub(url));
					return tr;
				}
			}
			
			if(actualPath.endsWith("system/languages") || actualPath.endsWith("system/languages/") 
					|| actualPath.endsWith("system/languages/archived") || actualPath.endsWith("system/languages/archived/")){
		        ClassLoader classLoader = getClass().getClassLoader();
				java.io.File file = new java.io.File(classLoader.getResource("content").getFile());
				if(file.exists() && file.isDirectory()){
					if(actualPath.contains("/archived") && actualPath.endsWith("/")){
						actualPath = actualPath.replace("system/languages/", "");
						if(actualPath.endsWith("/")){
							actualPath = actualPath.substring(0, actualPath.length()-1);
						}
						LanguageFolderResourceImpl lfr = new LanguageFolderResourceImpl(actualPath);
						return lfr;
					}else{
						LanguageFolderResourceImpl lfr = new LanguageFolderResourceImpl("");
						return lfr;
					}
				}
			}
			if(actualPath.startsWith("system/languages") && (actualPath.endsWith(".properties") || actualPath.endsWith(".native"))){
				ClassLoader classLoader = getClass().getClassLoader();
				String fileRelPath = actualPath;
				if(actualPath.contains("system/languages/")){
					fileRelPath = actualPath.replace("system/languages/", "");
					if(fileRelPath.contains("archived")){
						fileRelPath = fileRelPath.replace("archived/", "");
					}
				}
				java.io.File file = new java.io.File(classLoader.getResource("content").getFile() + java.io.File.separator + fileRelPath);
				if(file.exists()){
					LanguageFileResourceImpl lfr = new LanguageFileResourceImpl(fileRelPath);
					return lfr;
				}
			}
			
			if(dotDavHelper.isResource(url)){
				isResource = true;
			}else if(dotDavHelper.isFolder(url)){
				isFolder = true;
			}
			if(!isFolder && !isResource){
				return null;
			}
			
			if(isResource){
				File file = dotDavHelper.loadFile(url);
				if(file == null || !InodeUtils.isSet(file.getInode())){
					Logger.debug(this, "The file for url " + url + " returned null or not in db");
					return null;
				}
				FileResourceImpl fr = new FileResourceImpl(file,url);
				return fr;
			}else{
				Folder folder = dotDavHelper.loadFolder(url);
				if(folder == null || !InodeUtils.isSet(folder.getInode())){
					Logger.debug(this, "The folder for url " + url + " returned null or not in db");
					return null;
				}
				FolderResourceImpl fr = new FolderResourceImpl(folder, url);
				return fr;
			}
		}catch (Exception e) {
			Logger.error(this, e.getMessage(), e);
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.error(ResourceFactorytImpl.class,e1.getMessage(),e1);
			}
			return null;
		}finally{
			try {
				HibernateUtil.commitTransaction();
			} catch (DotHibernateException e) {
				Logger.error(ResourceFactorytImpl.class,e.getMessage(),e);
				try {
					HibernateUtil.rollbackTransaction();
				} catch (DotHibernateException e1) {
					Logger.error(ResourceFactorytImpl.class,e1.getMessage(),e1);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResourceFactory#getSupportedLevels()
	 */
	public String getSupportedLevels() {
		return "1,2,3";
	}

}

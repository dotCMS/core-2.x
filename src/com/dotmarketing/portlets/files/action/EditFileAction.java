package com.dotmarketing.portlets.files.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.cms.factories.PublicEncryptionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portal.struts.DotPortletActionInterface;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.HostAPIImpl;
import com.dotmarketing.portlets.files.business.FileAPI;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.files.struts.FileForm;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.ParamUtil;
import com.liferay.util.servlet.SessionMessages;
import com.liferay.util.servlet.UploadPortletRequest;

/**
 * @author Maria
 */

public class EditFileAction extends DotPortletAction implements DotPortletActionInterface {
	
	public static boolean debug = true;
	private ContentletAPI conAPI = APILocator.getContentletAPI();
	
	public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
			ActionResponse res) throws Exception {
		
		String cmd = req.getParameter(Constants.CMD);
		String referer = req.getParameter("referer");
		FileForm fileForm = (FileForm) form;
		
		// wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		
		if ((referer != null) && (referer.length() != 0)) {
			referer = URLDecoder.decode(referer, "UTF-8");
		}
		
		Logger.debug(this, "EditFileAction cmd=" + cmd);
		
		DotHibernate.startTransaction();
		
		User user = _getUser(req);
		
		try {
			Logger.debug(this, "Calling Retrieve method");
			_retrieveWebAsset(req, res, config, form, user, File.class, WebKeys.FILE_EDIT);
			
		} catch (Exception ae) {
			_handleException(ae, req);
			return;
		}
		
		/*
		 * We are editing the container
		 */
		if ((cmd != null) && cmd.equals(Constants.EDIT)) {
			try {
				Logger.debug(this, "Calling Edit method");
				_editWebAsset(req, res, config, form, user);
				
			} catch (Exception ae) {
				if ((referer != null) && (referer.length() != 0)) {
					if (ae.getMessage().equals(WebKeys.EDIT_ASSET_EXCEPTION)) {
						
						// The web asset edit threw an exception because it's
						// locked so it should redirect back with message
						java.util.Map<String, String[]> params = new java.util.HashMap<String, String[]>();
						params.put("struts_action", new String[] { "/ext/director/direct" });
						params.put("cmd", new String[] { "editFile" });
						params.put("file", new String[] { req.getParameter("inode") });
						params.put("referer", new String[] { URLEncoder.encode(referer, "UTF-8") });
						
						String directorURL = com.dotmarketing.util.PortletURLUtil.getActionURL(httpReq,
								WindowState.MAXIMIZED.toString(), params);
						
						_sendToReferral(req, res, directorURL);
						return;
					}
				}
				_handleException(ae, req);
				return;
			}
		}
		
		/*
		 * If we are updating the container, copy the information from the
		 * struts bean to the hbm inode and run the update action and return to
		 * the list
		 */
		if ((cmd != null) && cmd.equals(Constants.ADD)) {
			try {
				Logger.debug(this, "Calling Save method");
				
				if (Validator.validate(req, form, mapping)) {
					
					_saveWebAsset(req, res, config, form, user);
					
					
					String subcmd = req.getParameter("subcmd");
					
					if ((subcmd != null) && subcmd.equals(com.dotmarketing.util.Constants.PUBLISH)) {
						Logger.debug(this, "Calling Publish method");
						_publishWebAsset(req, res, config, form, user, WebKeys.FILE_FORM_EDIT);
					}	
					
					_sendToReferral(req, res, referer);
				}
				
			} catch (Exception ae) {
				_handleException(ae, req);
			}
			
		}
		/*
		 * If we are deleteing the container, run the delete action and return
		 * to the list
		 * 
		 */
		else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
			try {
				Logger.debug(this, "Calling Delete method");
				_deleteWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
		}
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.FULL_DELETE)) 
		{	
			try 
			{
				Logger.debug(this,"Calling Full Delete Method");
				WebAsset webAsset = (WebAsset) req.getAttribute(WebKeys.FILE_EDIT);
				if(WebAssetFactory.deleteAsset(webAsset,user)) {
					SessionMessages.add(httpReq, "message", "message." + webAsset.getType() + ".full_delete");
				} else {
					SessionMessages.add(httpReq, "error", "message." + webAsset.getType() + ".full_delete.error");
				}
			}
			catch(Exception ae) 
			{
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
		}
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.FULL_DELETE_LIST)) 
		{	
			try 
			{
				Logger.debug(this,"Calling Full Delete Method");
				String [] inodes = req.getParameterValues("publishInode");			
				boolean returnValue = true;				
				for(String inode  : inodes)
				{
					WebAsset webAsset = (WebAsset) InodeFactory.getInode(inode,File.class);
					returnValue &= WebAssetFactory.deleteAsset(webAsset,user);
				}
				if(returnValue)
				{
					SessionMessages.add(httpReq,"message","message.file_asset.full_delete");
				}
				else
				{
					SessionMessages.add(httpReq,"error","message.file_asset.full_delete.error");
				}
			}
			catch(Exception ae) 
			{
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
		}
		/*
		 * If we are undeleting the container, run the undelete action and
		 * return to the list
		 * 
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.UNDELETE)) {
			try {
				Logger.debug(this, "Calling UnDelete method");
				_undeleteWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
			
		}
		/*
		 * If we are deleting the container version, run the deeleteversion
		 * action and return to the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.DELETEVERSION)) {
			try {
				Logger.debug(this, "Calling Delete Version Method");
				_deleteVersionWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
		}
		/*
		 * If we are unpublishing the container, run the unpublish action and
		 * return to the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.UNPUBLISH)) {
			try {
				Logger.debug(this, "Calling Unpublish Method");
				_unPublishWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
			
		}
		/*
		 * If we are getting the container version back, run the getversionback
		 * action and return to the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.GETVERSIONBACK)) {
			try {
				Logger.debug(this, "Calling Get Version Back Method");
				_getVersionBackWebAsset(req, res, config, form, user);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
		}
		/*
		 * If we are getting the container versions, run the assetversions
		 * action and return to the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.ASSETVERSIONS)) {
			try {
				Logger.debug(this, "Calling Get Versions Method");
				_getVersionsWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT, WebKeys.FILE_VERSIONS);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		}
		/*
		 * If we are unlocking the container, run the unlock action and return
		 * to the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.UNLOCK)) {
			try {
				Logger.debug(this, "Calling Unlock Method");
				_unLockWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
				
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
			_sendToReferral(req, res, referer);
			
		}
		/*
		 * If we are copying the container, run the copy action and return to
		 * the list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.COPY)) {
			try {
				Logger.debug(this, "Calling Copy Method");
				_copyWebAsset(req, res, config, form, user);
				
			} catch (Exception ae) {
				_handleException(ae, req);
			}
			_sendToReferral(req, res, referer);
		}
		/*
		 * If we are moving the container, run the copy action and return to the
		 * list
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.MOVE)) {
			try {
				Logger.debug(this, "Calling Move Method");
				_moveWebAsset(req, res, config, form, user);
				
			} catch (Exception ae) {
				_handleException(ae, req);
			}
			_sendToReferral(req, res, referer);
		} else
			Logger.debug(this, "Unspecified Action");
		
		DotHibernate.commitTransaction();
		setForward(req,"portlet.ext.files.edit_file");
	}
	
	// /// ************** ALL METHODS HERE *************************** ////////
	public void _moveWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
	throws Exception {
		
		Logger.debug(this, "I'm moving the webasset");
		
		// gets the current container being edited from the request object
		File webAsset = (File) req.getAttribute(WebKeys.FILE_EDIT);
		
		// gets folder parent
		String parentInode = req.getParameter("parent");
		
		if (parentInode != null && parentInode.length() != 0 && !parentInode.equalsIgnoreCase("")) {
			
			// the new parent is being passed through the request
			Folder parent = (Folder) InodeFactory.getInode(parentInode, Folder.class);

			if(FileFactory.moveFile(webAsset, parent)) {
				SessionMessages.add(req, "message", "message.file_asset.move");
			} else {
				SessionMessages.add(req, "error", "message.file_asset.error.filename.exists");
				throw new ActionException("message.file_asset.error.filename.exists");
			}
			FileCache.removeFile(webAsset);
		}
		
	}
	
	public void _editWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
	throws Exception {
		
		FileAPI fileAPI = APILocator.getFileAPI();
		FolderAPI folderAPI = APILocator.getFolderAPI();
		
		// calls edit method from super class that returns parent folder
		super._editWebAsset(req, res, config, form, user, WebKeys.FILE_EDIT);
		
		// This can't be done on the WebAsset so it needs to be done here.
		File file = (File) req.getAttribute(WebKeys.FILE_EDIT);
		
		
		Folder parentFolder = null;
		
		if(req.getParameter("parent") != null) {
			parentFolder = folderAPI.find(req.getParameter("parent"));
		} else {
			parentFolder = fileAPI.getFileFolder(file);
		}

		// setting parent folder path and inode on the form bean
		if(parentFolder != null) {
			FileForm cf = (FileForm) form;
			cf.setSelectedparent(parentFolder.getName());
			cf.setParent(parentFolder.getInode());
			cf.setSelectedparentPath(parentFolder.getPath());
			file.setParent(parentFolder.getInode());
		}
		FileCache.removeFile(file);
	}
	
	@SuppressWarnings("unchecked")
	public void _saveWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
	throws Exception {
		
		try {
			
			// wraps request to get session object
			ActionRequestImpl reqImpl = (ActionRequestImpl) req;
			HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
			FileForm fileForm = (FileForm) form;
			UploadPortletRequest uploadReq = PortalUtil.getUploadPortletRequest(req);
			
			
			// gets the new information for the container from the request
			// object
			File file = new File();
			file.setTitle(fileForm.getTitle());
			BeanUtils.copyProperties(file,fileForm);
			req.setAttribute(WebKeys.FILE_FORM_EDIT,file);
						
			// gets the current container being edited from the request object
			File currentFile = (File) req.getAttribute(WebKeys.FILE_EDIT);
			
			// parent folder
			String parent = ParamUtil.getString(req, "parent");
			Folder folder = (Folder) InodeFactory.getInode(parent, Folder.class);
			//http://jira.dotmarketing.net/browse/DOTCMS-5899
			Identifier id = IdentifierFactory.getIdentifierByInode(currentFile);
			if(UtilMethods.isSet(id.getInode())){
				String URI = id.getURI();
				String uriPath = URI.substring(0,URI.lastIndexOf("/")+1);
				if(!uriPath.equals(folder.getPath())){
					id.setURI(folder.getPath()+currentFile.getFileName());
					InodeFactory.saveInode(id);
				}
			}
		
			req.setAttribute(WebKeys.PARENT_FOLDER, folder);	// Since the above query is expensive, save it into request object
			
			//Checking permissions
			if(currentFile!=null){
				file.setInode(currentFile.getInode());
				file.setIdentifier(currentFile.getIdentifier());
			}
			_checkPermissions(file, folder, user, httpReq);
			file.setInode("");
			file.setIdentifier("");
			
			
			// gets user id from request for modified user
			String userId = user.getUserId();
			
			boolean previousShowMenu = file.isShowOnMenu();
			
			long uploadFileMaxSize = Long.parseLong(Config.getStringProperty("UPLOAD_FILE_MAX_SIZE")); 

			//Checking the max file size
			java.io.File uploadedFile = uploadReq.getFile("uploadedFile");

			
			//Do we have an uploaded file or not?
			boolean haveUpload = (uploadedFile.exists() && uploadedFile.length()>0);
			boolean tmpFile = true;
			
			if(!haveUpload){

				// look for a edited file in the users session, if it is there, use that
				if(UtilMethods.isSet(fileForm.get_imageToolSaveFile())){
					String x =fileForm.get_imageToolSaveFile().trim();
					if( x != null){
						x = PublicEncryptionFactory.decryptString(x);
						uploadedFile = new java.io.File( x.trim());
						haveUpload = (uploadedFile.exists() && uploadedFile.length()>0);
					}
					else{
						x=null;
					}
					reqImpl.getHttpServletRequest().getSession().removeAttribute(WebKeys.IMAGE_TOOL_SAVE_FILES);
				}
			}
			
			
			
			if(haveUpload) {
				if ((uploadFileMaxSize > 0) && (uploadedFile.length() > uploadFileMaxSize)) {
					// when there is an error saving should unlock working asset
					WebAssetFactory.unLockAsset(currentFile);
					throw new ActionException();
				}
			}
			
			
			// CHECK THE FOLDER PATTERN
			if (UtilMethods.isSet(file.getFileName()) && !FolderFactory.matchFilter(folder, file.getFileName())) {
				SessionMessages.add(req, "error", "message.file_asset.error.filename.filters");
				// when there is an error saving should unlock working asset
				WebAssetFactory.unLockAsset(currentFile);
				throw new ActionException("message.file_asset.error.filename.filters");
			}
			
			
			// Checking permissions
			_checkPermissions(currentFile, folder, user, httpReq);
			
			
			//Setting some flags
			boolean editing = (InodeUtils.isSet(currentFile.getInode()));

			

			if(!haveUpload){
				uploadedFile = APILocator.getFileAPI().getAssetIOFile(currentFile);
				tmpFile = false;
			}
			
			
			// if we don't have a file anywhere, die
			if(!(uploadedFile.exists() && uploadedFile.length()>0)){
				SessionMessages.add(req, "error", "message.file_asset.error.nofile");
				// when there is an error saving should unlock working asset
				WebAssetFactory.unLockAsset(currentFile);
				throw new ActionException("message.file_asset.error.nofile");
				
				
			}
			
			
			
			
			
			
			String fileName = (editing) ? currentFile.getFileName() : fileForm.getFileName();
			
			

			
			//getting mime type
			String mimeType = FileFactory.getMimeType(fileName);
			
			
			
			
			
			file.setMimeType(mimeType);

			if (editing && haveUpload && ! 
					FileFactory.getMimeType(fileName).equals(FileFactory.getMimeType(uploadedFile.getName()))) {
				SessionMessages.add(req, "error", "message.file_asset.error.mimetype");
				// when there is an error saving should unlock working asset
				Logger.error(this.getClass(), "MimeType Mismatch:" + fileName + " : " + uploadedFile.getName());
				WebAssetFactory.unLockAsset(currentFile);
				DotHibernate.rollbackTransaction();
				throw new ActionException("message.file_asset.error.mimetype");
			}
			
			
			
			// checks if another identifier with the same name exists in the same
			// folder
			if ((!editing) && (FileFactory.existsFileName(folder, fileName))) {
				SessionMessages.add(req, "error", "message.file_asset.error.filename.exists");
				throw new ActionException("message.file_asset.error.filename.exists");
			}

			//sets new file properties
			file.setFileName(fileName);
			file.setModUser(userId);
			
			if (haveUpload) {
				file.setSize((int)uploadedFile.length());
			}
			else {
				file.setSize(currentFile.getSize());
				file.setWidth(currentFile.getWidth());
				file.setHeight(currentFile.getHeight());
			}
			InodeFactory.saveInode(file);
			FileCache.removeFile(file);
			
			//http://jira.dotmarketing.net/browse/DOTCMS-5622
			
			//Check for an existing identifier of the asset
			Identifier existingIdentifier = (Identifier) IdentifierFactory.getIdentifierByURI(file.getURI(folder), folder.getHostId());
			
			//Check for a working version of the asset
			WebAsset working = (WebAsset) IdentifierFactory.getWorkingChildOfClass(existingIdentifier, file.getClass());
			
			// get the file Identifier
			Identifier ident = null;
			
			//If Identifier exists but no working version is available then reuse identifier
			if(InodeUtils.isSet(existingIdentifier.getInode()) && !InodeUtils.isSet(working.getInode())){
				ident = existingIdentifier;
			}else{
				if (InodeUtils.isSet(currentFile.getInode())){
					ident = IdentifierCache.getIdentifierFromIdentifierCache(currentFile);
					FileCache.removeFile(currentFile);
				}else{
					ident = new Identifier();
				}
			}
			//Saving the file, this creates the new version and save the new data
			File workingFile = null;
			
			workingFile = FileFactory.saveFile(file, uploadedFile, folder, ident, user);
				 
			if(uploadedFile != null && uploadedFile.exists() &&tmpFile){
				uploadedFile.delete();
			}
			FileCache.removeFile(workingFile);
			ident = IdentifierCache.getIdentifierFromIdentifierCache(workingFile);

			SessionMessages.add(req, "message", "message.file_asset.save");

			//updating caches
			if (workingFile.isLive()){
				LiveCache.removeAssetFromCache(workingFile);
				LiveCache.addToLiveAssetToCache(workingFile);
			}else{
				LiveCache.removeAssetFromCache(file);
				
			}
			WorkingCache.removeAssetFromCache(workingFile);
			WorkingCache.addToWorkingAssetToCache(workingFile);

			SessionMessages.add(req, "message", "message.file_asset.save");

   			// for file in a popup
			if (req.getParameter("popup") != null) {
				req.setAttribute("fileInode", file.getInode() + "");
				req.setAttribute("fileName", file.getFileName() + "");
			}
			
			req.setAttribute(WebKeys.FILE_FORM_EDIT, workingFile);
			
			// copies the information back into the form bean
			BeanUtils.copyProperties(form, req.getAttribute(WebKeys.FILE_FORM_EDIT));
			
			//Refreshing the menues
			if (previousShowMenu != file.isShowOnMenu()) {
				//existing folder with different show on menu ... need to regenerate menu
				RefreshMenus.deleteMenu(file);
			}
			
		} catch (Exception e) {
			Logger.error(this, "\n\n\nEXCEPTION IN FILE SAVING!!! " + e.getMessage(), e);
			throw new ActionException(e.getMessage());
		}
	}
	
	public void _copyWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
	throws Exception {
		
		// wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		
		Logger.debug(this, "I'm copying the File");
		
		try {
			// gets the current template being edited from the request object
			File file = (File) req.getAttribute(WebKeys.FILE_EDIT);
			
			// gets folder parent
			String parentInode = req.getParameter("parent");
			Folder parent = null;
			if (parentInode != null && parentInode.length() != 0 && !parentInode.equalsIgnoreCase("")) {
				// the parent is being passed through the request
				parent = (Folder) InodeFactory.getInode(parentInode, Folder.class);
				Logger.debug(this, "Parent Folder=" + parent.getInode());
			} else {
				parent = (Folder) InodeFactory.getParentOfClass(file, Folder.class);
				Logger.debug(this, "Parent Folder=" + parent.getInode());
			}
			
			// Checking permissions
			_checkCopyAndMovePermissions(file, parent, user, httpReq, "copy");
			
			FileFactory.copyFile(file, parent);
			
			SessionMessages.add(req, "message", "message.file_asset.copy");
			
		} catch (IOException e) {
			Logger.error(this, e.toString(), e);
			throw new ActionException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void _getVersionBackWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form,
			User user) throws Exception {
		
		File workingFile = (File) super._getVersionBackWebAsset(req, res, config, form, user, File.class,
				WebKeys.FILE_EDIT);
		File fileVersion = (File) req.getAttribute(WebKeys.FILE_EDIT);
		Identifier identifier = IdentifierCache.getIdentifierFromIdentifierCache(fileVersion);
		String parentInode = req.getParameter("parent");
		Folder parent = null;
		if (parentInode != null && parentInode.length() != 0 && !parentInode.equalsIgnoreCase("")) {
			// the parent is being passed through the request
			parent = (Folder) InodeFactory.getInode(parentInode, Folder.class);
			Logger.debug(this, "Parent Folder=" + parent.getInode());
		} else {
			parent = (Folder) InodeFactory.getParentOfClass(workingFile, Folder.class);
			Logger.debug(this, "Parent Folder=" + parent.getInode());
		}
		
		WebAssetFactory.createAsset(fileVersion, user == null?"":user.getUserId(),parent, identifier,true);
		List workingAssets = null;
		workingAssets = IdentifierFactory.getWorkingChildrenOfClass(identifier, workingFile.getClass());

		// if there is more than one working asset
		if (workingAssets.size() > 1) {
			Iterator iter = workingAssets.iterator();
			while (iter.hasNext()) {
				WebAsset webAsset = (WebAsset) iter.next();
				if (!webAsset.getInode().equals(fileVersion.getInode())) {
					webAsset.setWorking(false);
					InodeFactory.saveInode(webAsset);
				}
			}
		}
		WorkingCache.removeAssetFromCache(workingFile);
		WorkingCache.addToWorkingAssetToCache(workingFile);
		FileCache.removeFile(workingFile);
		FileCache.removeFile(fileVersion);
		if (fileVersion.isLive()){
			LiveCache.removeAssetFromCache(fileVersion);
			LiveCache.addToLiveAssetToCache(fileVersion);
		}
	}
	
}
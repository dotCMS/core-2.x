package com.dotmarketing.portlets.folders.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.web.UserWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.Contentlet;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.hostvariable.bussiness.HostVariableAPI;
import com.dotmarketing.portlets.hostvariable.model.HostVariable;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;
/**
 * @author David
 * @deprecated Should be deleted soon, not used anymore
 */

public class EditHostAction extends DotPortletAction {

	protected PermissionAPI permAPI = APILocator.getPermissionAPI();
	protected HostAPI hostAPI = APILocator.getHostAPI();
	protected UserWebAPI userWebAPI = WebAPILocator.getUserWebAPI();

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {


		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		String cmd = req.getParameter(Constants.CMD);

		//View Folders Page
		String referer = "";
		if (UtilMethods.isSet(req.getParameter("referer"))) {
			referer = req.getParameter("referer");
		} else {
			java.util.Map<String, String[]> params = new java.util.HashMap<String, String[]>();
			params.put("struts_action",
					new String[] { "/ext/folders/view_folders" });
			params.put("openNodes", new String[] { req.getParameter("openNodes") });
			params.put("view", new String[] { req.getParameter("view") });
			params.put("content", new String[] { req.getParameter("content") });

			referer = com.dotmarketing.util.PortletURLUtil.getActionURL(
					httpReq, WindowState.MAXIMIZED.toString(), params);
		}

		Logger.debug(this, "EditHostAction cmd=" + cmd);

		String forward = "portlet.ext.folders.edit_host";

		DotHibernate.startTransaction();

		

		
		try {
			
			_editHost(req, res, config, form);
			_editHostVars(req);

		} catch (Exception ae) {
			_handleException(ae, req);
			return;
		}

		/*
		 * We are editing the host
		 */
		if ((cmd != null) && cmd.equals(Constants.ADD)) {
			try {
				_updateHost(req, res, config, form);
				_sendToReferral(req,res,referer);
				return;
			} catch (ActionException ae) {
				_handleException(ae, req, false);
				if (ae.getMessage().equals("message.host.error.host.exists")) {
					setForward(req, forward);
					return;
				}
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		}


		/*
		 * If we are deleteing the host,
		 * run the delete action and return to the list of folders
		 */
		else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
			try {
				_deleteHost(req, res, config, form);

				_sendToReferral(req,res,referer);

			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		}

		/*
		 * If we are deleteing the host,
		 * run the delete action and return to the list of folders
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.SET_AS_DEFAULT)) {
			try {
				_setDefaultHost(req, res, config, form);

				_sendToReferral(req,res,referer);

			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		}
		
		BeanUtils.copyProperties(form,req.getAttribute(WebKeys.HOST_EDIT));
		DotHibernate.commitTransaction();
		setForward(req,forward);
	}

	///// ************** ALL METHODS HERE *************************** ////////

	public void _editHost(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
	throws Exception {
		User user = _getUser(req);
		Host host = hostAPI.find(req.getParameter("id"), user, false) ;
		if(host == null)
			host = new Host();
		req.setAttribute(WebKeys.HOST_EDIT, host);
	}

	public void _updateHost(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
	throws Exception {
		
		User user = _getUser(req);
		User systemUser = APILocator.getUserAPI().getSystemUser();
		
		BeanUtils.copyProperties(req.getAttribute(WebKeys.HOST_EDIT),form);

		Host host = (Host) req.getAttribute(WebKeys.HOST_EDIT);
		host.setAliases(host.getAliases().replaceAll("http://", ""));
		host.setAliases(host.getAliases().replaceAll("https://", ""));
		
		boolean hasPermission = false;
		if(InodeUtils.isSet(host.getInode())) {
			hasPermission = permAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_EDIT, user);
		} else {
			Host systemHost = hostAPI.findSystemHost(systemUser, false);
			hasPermission = permAPI.doesUserHavePermission(systemHost, PermissionAPI.PERMISSION_CAN_ADD_CHILDREN, user);
		}
		if(!hasPermission) {
			throw new DotSecurityException("You don't have permissions to save this host");
		}
		
		//Check it is the unique host with this url
		Host secondHost = hostAPI.findByName(host.getHostname().trim(), systemUser, false);
		if (secondHost != null && (!host.getInode().equalsIgnoreCase(secondHost.getInode()))) {
			SessionMessages.add(req, "message", "message.host.already.exists");
			throw new ActionException ("message.host.error.host.exists");
		}

		if (host.isDefault()) {
			Host defaultHost = hostAPI.findDefaultHost(systemUser, false);
			if (InodeUtils.isSet(defaultHost.getInode()) && (!host.getInode().equalsIgnoreCase(defaultHost.getInode()))) {
				//sets the other default host as not default
				defaultHost.setDefault(false);
				hostAPI.save(defaultHost, systemUser, false);
			}
			
		}

		//saves this host
		hostAPI.save(host, user, false);

		//For messages to be displayed on messages page
		SessionMessages.add(req, "message", "message.host.save");
	}

	public void _setDefaultHost(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
	throws Exception {
		
		User user = _getUser(req);
		
		Host host = ( Host ) req.getAttribute(WebKeys.HOST_EDIT);
		//checks if this host is already the default
		if (host.isDefault()) {
			SessionMessages.add(req, "message", "message.host.already.default");
			return;
		}
		//gets default host
		Host defaultHost = hostAPI.findDefaultHost(user, false);
		if (defaultHost != null) {
			//if default host exists it sets default to false
			defaultHost.setDefault(false);
			//saves this host
			hostAPI.save(defaultHost, user, false);
		}
		//sets new host to true
		host.setDefault(true);
		//saves new host
		hostAPI.save(host, user, false);

	}

	public void _deleteHost(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
	throws Exception {

		User systemUser = userWebAPI.getSystemUser();
		User user = _getUser(req);
		
		Host host = ( Host ) req.getAttribute(WebKeys.HOST_EDIT);
		//checks if the host the default host.
		if (host.isDefault()) {
			SessionMessages.add(req, "message", "message.host.cannot.delete.default");
			return;
		}
		//gets all the hosts
		List<Host> hosts = hostAPI.findAll(systemUser, false);
		
		//check the number of hosts we have
		if (hosts.size()==1) {
			//we only have one host, we can't delete it
			SessionMessages.add(req, "message", "message.host.cannot.delete.last");
			return;
		}	
		

		PermissionAPI pAPI = APILocator.getPermissionAPI();

		pAPI.removePermissions(host);
		//deletes host
		hostAPI.delete(host, user, false);

		//For messages to be displayed on messages page
		SessionMessages.add(req, "message", "message.host.delete");

	}


	private void _getFoldersAndAssetsToDelete(Set toDelete, Folder parent, String selectedFolder,HttpSession session) {
		//Get children assets to delete
		_getChildrenAssetsToDelete (toDelete, parent);

		//Iterate through the children folders
		List children = InodeFactory.getChildrenClass(parent,Folder.class);
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			Folder childFolder = (Folder) iter.next();
			_getFoldersAndAssetsToDelete(toDelete, childFolder,selectedFolder,session);
			toDelete.add(childFolder);
		}
	}

	private void _getChildrenAssetsToDelete (Set toDelete, Folder folder) {
		//Removing HTMLPages
		Set children = new HashSet(InodeFactory.getChildrenClass(folder, HTMLPage.class));
		Iterator it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
		//Removing Containers
		children = new HashSet(InodeFactory.getChildrenClass(folder, Container.class));
		it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
		//Removing Contentlet
		children = new HashSet(InodeFactory.getChildrenClass(folder, Contentlet.class));
		it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
		//Removing Links
		children = new HashSet(InodeFactory.getChildrenClass(folder, Link.class));
		it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
		//Removing Templates
		children = new HashSet(InodeFactory.getChildrenClass(folder, Template.class));
		it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
		//Removing Files
		children = new HashSet(InodeFactory.getChildrenClass(folder, File.class));
		it = children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			if (child instanceof WebAsset) {
				WebAsset asset = (WebAsset) child;
				Identifier id = IdentifierFactory.getIdentifierByInode(asset);
				toDelete.add(id);
				List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
				Iterator childrenversions = allversions.iterator();
				while (childrenversions.hasNext()) {
					WebAsset version = (WebAsset) childrenversions.next();
					toDelete.add(version);
				}
			}
		}
	}

	public void _editHostVars(ActionRequest req) throws Exception {
		Host host = (Host) req.getAttribute(WebKeys.HOST_EDIT);

		List<HostVariable> hvars = new ArrayList<HostVariable>();
		if (InodeUtils.isSet(host.getIdentifier())) {
			HostVariableAPI hostVariableAPI = APILocator.getHostVariableAPI();
			User user = (User) req.getAttribute("USER");
			hvars = hostVariableAPI.getVariablesForHost(host.getIdentifier(), user, false);
			req.setAttribute("hvariableslist", hvars);
		}
		req.setAttribute("hvariableslist", hvars);
	}
}

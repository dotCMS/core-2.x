package com.dotmarketing.portlets.organization.action;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_PUBLISH;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.MultiTree;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.exception.WebAssetException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.MultiTreeFactory;
import com.dotmarketing.factories.PublishFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.containers.business.ContainerAPI;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.organization.struts.OrganizationForm;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.business.TemplateAPI;
import com.dotmarketing.portlets.templates.factories.TemplateFactory;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;

/**
 * @author Maria
 */

public class EditOrganizationAction extends DotPortletAction {

	public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
			ActionResponse res) throws Exception {

		String cmd = req.getParameter(Constants.CMD);

		// get the user
		User user = null;
		try {
			user = com.liferay.portal.util.PortalUtil.getUser(req);
		} catch (Exception e) {
			_handleException(e, req);
		}

		/*
		 * get the mainglist object, stick it in request
		 * 
		 */
		try {
			_retrieveOrganization(req, res, config, form);
		} catch (Exception ae) {
			_handleException(ae, req);
		}

		/*
		 * if we are saving
		 * 
		 */
		if ((cmd != null) && cmd.equals(Constants.ADD)) {
			try {
				if (Validator.validate(req, form, mapping)) {
					if (_saveOrganization(req, res, config, form, user)) {
						_sendToReferral(req, res, "");
						return;
					}
				} else {
					setForward(req, mapping.getInput());
					return;
				}
			} catch (Exception ae) {
				_handleException(ae, req);
			}
		}

		/*
		 * deleting the list, return to listing page
		 * 
		 */
		else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
			try {
				_deleteOrganization(req, res, config, form, user);
			} catch (Exception ae) {
				_handleException(ae, req);
			}
			_sendToReferral(req, res, "");
			return;
		}

		/*
		 * Copy copy props from the db to the form bean
		 * 
		 */
		if ((cmd != null) && cmd.equals(Constants.EDIT)) {
			try {
				_editOrganization(req, res, config, form);
			} catch (Exception ae) {
				_handleException(ae, req);
			}
		}

		/*
		 * return to edit page
		 * 
		 */
		setForward(req, "portlet.ext.organization.edit_organization");
	}

	private void _retrieveOrganization(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form)
			throws Exception {

		Organization organization = OrganizationFactory.getOrganization(req.getParameter("inode"));
		req.setAttribute(WebKeys.ORGANIZATION_EDIT, organization);

		List systems = OrganizationFactory.getAllSystems();
		req.setAttribute("systems", systems.iterator());

	}

	private void _editOrganization(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form)
			throws Exception {

		Organization organization = (Organization) req.getAttribute(WebKeys.ORGANIZATION_EDIT);
		BeanUtils.copyProperties(form, organization);
		OrganizationForm organizationForm = (OrganizationForm) form;

		// to set the previous parent system
		Organization parent = OrganizationFactory.getParentOrganization(organization);
		if (InodeUtils.isSet(parent.getInode())) {
			organizationForm.setParentSystem(parent.getInode());
		}

		// gets the partner portal information and sets it on the bean
		if (UtilMethods.isSet(organizationForm.getParentSystem()) && InodeUtils.isSet(organization.getInode())) {
			parent = OrganizationFactory.getOrganization(organizationForm.getParentSystem());
			organizationForm.setPartnerUrl(parent.getPartnerUrl());
			organizationForm.setPartnerKey(parent.getPartnerKey());
			organizationForm.setPartnerLogo(parent.getPartnerLogo());
		}

		// gets the categories and sets them on the bean
		java.util.List _cats = InodeFactory.getParentsOfClass(organization, Category.class);
		String[] categories = new String[_cats.size()];
		java.util.Iterator it = _cats.iterator();
		int i = 0;
		while (it.hasNext()) {
			Category cat = (Category) it.next();
			categories[i++] = String.valueOf(cat.getInode());
		}
		organizationForm.setCategories(categories);

		// gets partner logo
		if (InodeUtils.isSet(organizationForm.getPartnerLogo())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(organizationForm.getPartnerLogo(),
					Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
			organizationForm.setLogoImage(file.getInode());
		}
	}

	private void updateOrgsSharingPartnerPortal(Organization organization, String oldPartnerURL) {
		List organizations = OrganizationFactory.getOrganizationsSharingPartnerPortal(oldPartnerURL);
		Iterator orgIter = organizations.iterator();
		while (orgIter.hasNext()) {
			Organization orgSibbling = (Organization) orgIter.next();
			orgSibbling.setPartnerUrl(organization.getPartnerUrl());
			orgSibbling.setPartnerLogo(organization.getPartnerLogo());
			orgSibbling.setPartnerKey(organization.getPartnerKey());
		}
	}

	private boolean _saveOrganization(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form,
			User user) throws Exception {

		// wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		Organization organization = (Organization) req.getAttribute(WebKeys.ORGANIZATION_EDIT);
		OrganizationForm organizationForm = (OrganizationForm) form;

		Organization organizationByPartnerURL = OrganizationFactory
				.getFirstLevelOrganizationByPartnerURL(organizationForm.getPartnerUrl());
		;
		if (UtilMethods.isSet(organizationForm.getPartnerUrl()) && InodeUtils.isSet(organizationByPartnerURL.getInode()) 
				&& !organizationByPartnerURL.getInode().equalsIgnoreCase(organization.getInode())
				&& !organizationByPartnerURL.getInode().equalsIgnoreCase(organizationForm.getParentSystem())) {
			SessionMessages.add(httpReq, "error", "error.organization.duplicate.partner_url");
			return false;
		} else {

			String oldPartnerURL = organization.getPartnerUrl();

			BeanUtils.copyProperties(organization, form);

			// gets partner logo image inode and saves the identifier inode as
			// the Partner Logo column on the organization table
			String logoImageInode = organizationForm.getLogoImage();
			if (InodeUtils.isSet(logoImageInode)) {
				File file = (File) InodeFactory.getInode(logoImageInode, File.class);
				Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
				organization.setPartnerLogo(identifier.getInode());
			}

			InodeFactory.saveInode(organization);

			if (UtilMethods.isSet(organizationForm.getParentSystem())) {
				if (UtilMethods.isSet(organizationForm.getPartnerUrl())) {
					// we will update all facilities sharing the same partner
					// portal site
					updateOrgsSharingPartnerPortal(organization, oldPartnerURL);
				}
				// looks for the previous parent organization.
				Organization oldParent = OrganizationFactory.getParentOrganization(organization);
				// only if we changed it, it's going to save it
				if (!oldParent.getInode().equalsIgnoreCase(organizationForm.getParentSystem())) {
					if (InodeUtils.isSet(oldParent.getInode())) {
						oldParent.deleteChild(organization);
					}

					// there is a parent system
					Organization parent = OrganizationFactory.getOrganization(organizationForm.getParentSystem());
					parent.addChild(organization);
					organization.setParentOrganization(parent.getInode());
					InodeFactory.saveInode(organization);
				}
			} else {
				Organization oldParent = OrganizationFactory.getParentOrganization(organization);
				if (!oldParent.getInode().equalsIgnoreCase(organizationForm.getParentSystem())) {
					if (InodeUtils.isSet(oldParent.getInode())) {
						oldParent.deleteChild(organization);
					}
				}
				organization.setParentOrganization("");
			}

			// wipe out the old categories
			java.util.List _cats = InodeFactory.getParentsOfClass(organization, Category.class);
			java.util.Iterator it = _cats.iterator();
			while (it.hasNext()) {
				Category cat = (Category) it.next();
				cat.deleteChild(organization);
			}

			// add the new categories
			String[] arr = organizationForm.getCategories();
			if (arr != null) {
				for (int i = 0; i < arr.length; i++) {
					Category node = (Category) InodeFactory.getInode(arr[i], Category.class);
					node.addChild(organization);
				}
			}
			/*if (UtilMethods.isSet(organizationForm.getPartnerUrl())) {
				try {
					String partnerUrl = organizationForm.getPartnerUrl();
					_generatePartnerFolder(partnerUrl, req);

				} catch (Exception p) {
					
				}
			}*/
			SessionMessages.add(httpReq, "message", "message.organization.save");
			return true;
		}
	}

	private void _deleteOrganization(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form,
			User user) throws Exception {

		// wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		Organization organization = (Organization) req.getAttribute(WebKeys.ORGANIZATION_EDIT);
		InodeFactory.deleteInode(organization);
		// gets the session object for the messages
		SessionMessages.add(httpReq, "message", "message.organization.delete");
	}

	/**
	 * This method was create to add the partner folder in the CMS
	 * 
	 * @author Oswaldo Gallango
	 * @param uri
	 *            partner URL
	 * @param req
	 */
	@SuppressWarnings( { "unchecked", "unchecked" })
	private void _generatePartnerFolder(String uri, ActionRequest req) {

		String folderPath = Config.getStringProperty("PARTNERS_URL_FOLDER") + uri.substring(0, 2).toLowerCase() + uri
				+ "/";

		try {

			Host currentHost =  WebAPILocator.getHostWebAPI().getCurrentHost(req);

			Folder folder = FolderFactory.getFolderByPath(folderPath, currentHost);
			HTMLPage workingAsset = null;
			User user = _getUser(req);

			/* Check if exists the organization folder */
			if (!InodeUtils.isSet(folder.getInode())) {

				/* If don`t exits add the folder and the html index default page */
				Folder parentFolder = FolderFactory.createFolders(folderPath, currentHost, req);
				parentFolder.setHostId(currentHost.getIdentifier());
				InodeFactory.saveInode(parentFolder);

				Logger.debug(EditOrganizationAction.class, "Folder " + folderPath + " .Created");

				HTMLPage newHtmlPage = new HTMLPage();
				newHtmlPage.setTitle("index");
				newHtmlPage.setFriendlyName("index");
				newHtmlPage.setHttpsRequired(false);
				newHtmlPage.setPageUrl("index." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION"));
				newHtmlPage.setType("htmlpage");
				newHtmlPage.setLive(true);

				WebAssetFactory.createAsset(newHtmlPage, user.getUserId(), parentFolder);
				workingAsset = newHtmlPage;

				/** ** Added ** */
				String baseHomePageURL = Config.getStringProperty("PARTNERS_URL_FOLDER") + "/home." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
				Identifier homePageIdentifier = IdentifierFactory.getIdentifierByURI(baseHomePageURL, currentHost);
				Identifier newPageIdentifier = IdentifierFactory.getIdentifierByURI(folderPath + "index."
						+ Config.getStringProperty("VELOCITY_PAGE_EXTENSION"), currentHost);

				List<MultiTree> asociateMultiTrees = MultiTreeFactory.getMultiTree(homePageIdentifier);
				for (MultiTree tree : asociateMultiTrees) {
					/* Adding relation */
					MultiTree m = new MultiTree(newPageIdentifier.getInode(), tree.getParent2(), tree.getChild());
					MultiTreeFactory.saveMultiTree(m);
				}

				/** *** */

				List<Template> templates = TemplateFactory.getTemplateByCondition("title like '"
						+ Config.getStringProperty("PARTNERS_TEMPLATE") + "'");
				String template = ((Template) templates.get(0)).getInode();

				// Adds template children for partners pages
				Template templateInode = (Template) InodeFactory.getInode(template, Template.class);
				Identifier templateIdentifier = IdentifierFactory.getParentIdentifier(templateInode);
				Template templateWorking = (Template) IdentifierFactory.getWorkingChildOfClass(templateIdentifier,
						Template.class);
				templateWorking.addChild(workingAsset);

				DotHibernate.flush();
				DotHibernate.save(workingAsset);

				/* add the permission */
				_applyPermissions(req, workingAsset);

				/* Publishing asset */
				ActionRequestImpl reqImpl = (ActionRequestImpl) req;
				HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

				PublishFactory.publishAsset(workingAsset, httpReq);

			}

		}catch (WebAssetException wax) {
			Logger.error(this, wax.getMessage(),wax);
			SessionMessages.add(req, "error", "message.webasset.published.failed");
		}catch (Exception ex) {
			
			Logger.debug(EditOrganizationAction.class, "Error creating Folder " + folderPath + " .",ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void _applyPermissions(ActionRequest req, Permissionable asset) throws DotDataException, DotSecurityException {
		
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		FolderAPI folderAPI = APILocator.getFolderAPI();
		HostAPI hostAPI = APILocator.getHostAPI();
		ContainerAPI containerAPI = APILocator.getContainerAPI();
		TemplateAPI templateAPI = APILocator.getTemplateAPI();
		ContentletAPI contentletAPI = APILocator.getContentletAPI();
		
		// If the permission has been changed, the method save the permission
		// otherwise not

		Permissionable origAsset = asset;

		Permission permission = null;

		perAPI.removePermissions(asset);

		// Add permissions to anonymous users
		User systemUser = APILocator.getUserAPI().getSystemUser();
		Role roleAnonymous = APILocator.getRoleAPI().loadCMSAnonymousRole();

		if (roleAnonymous != null) {
			// adds read permissions to the inode
			Logger.debug(this, "_applyPermissions: Saving Read Permission for=" + roleAnonymous.getId());
			permission = new Permission(asset.getPermissionId(), roleAnonymous.getId(), PERMISSION_READ);
			perAPI.save(permission, asset, systemUser, false);

			// adds publish permissions to the inode
			Logger.debug(this, "_applyPermissions: Saving Publish Permission for=" + roleAnonymous.getId());
			permission = new Permission(asset.getPermissionId(), roleAnonymous.getId(), PERMISSION_PUBLISH);
			perAPI.save(permission, asset, systemUser, false);
		}

		// Add permissions to administrator user
		Role roleCMSAdministrator = APILocator.getRoleAPI().loadCMSAdminRole();

		if (roleCMSAdministrator != null) {
			// adds read permissions to the inode
			Logger.debug(this, "_applyPermissions: Saving Read Permission for=" + roleCMSAdministrator.getId());
			permission = new Permission(asset.getPermissionId(), roleCMSAdministrator.getId(), PERMISSION_READ);
			perAPI.save(permission, asset, systemUser, false);

			// adds write permissions to the inode
			Logger.debug(this, "_applyPermissions: Saving Write Permission for=" + roleCMSAdministrator.getId());
			permission = new Permission(asset.getPermissionId(), roleCMSAdministrator.getId(), PERMISSION_WRITE);
			perAPI.save(permission, asset, systemUser, false);

			// adds publish permissions to the inode
			Logger.debug(this, "_applyPermissions: Saving Publish Permission for=" + roleCMSAdministrator.getId());
			permission = new Permission(asset.getPermissionId(), roleCMSAdministrator.getId(), PERMISSION_PUBLISH);
			perAPI.save(permission, asset, systemUser, false);

		}

		if (asset instanceof Folder || asset instanceof Host || asset instanceof Structure) {

			Logger.debug(this, "_applyPermissions: applying permissions to children");

			java.util.List children = new ArrayList();

			if (asset instanceof Folder) {
				Folder folder = (Folder) asset;
				children.addAll(folderAPI.findSubFolders(folder));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, Contentlet.class));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, Container.class));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, Template.class));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, HTMLPage.class));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, Link.class));
				children.addAll(InodeFactory.getChildrenClass((Folder)asset, File.class));
				
			} else if (asset instanceof Host) {
				Host host = (Host) asset;
				children.addAll(folderAPI.findSubFolders(host));
				children.addAll(templateAPI.findTemplatesAssignedTo(host));
				children.addAll(containerAPI.findContainersUnder(host));
				children.addAll(contentletAPI.findContentletsByHost(host, systemUser, false));
			} else if (asset instanceof Structure) {
				children.addAll(contentletAPI.findByStructure((Structure)asset, systemUser, false, -1, 0));
			}

			java.util.Iterator iterChildren = children.iterator();
			while (iterChildren.hasNext()) {
				_applyPermissions(req, (Inode) iterChildren.next());
			}
		}

	}

}

package com.dotmarketing.portlets.folders.action;

import java.net.URLDecoder;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;

/**
 * <a href="ViewQuestionsAction.java.html"><b><i>View Source</i></b></a>
 * 
 * @author Maria Ahues
 * 
 */
public class LoadTreeAction extends DotPortletAction {

	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			RenderResponse res) throws Exception {

		Folder folder = null;

		if (InodeUtils.isSet(req.getParameter("inode"))) {
			folder = (Folder) InodeFactory.getInode(req.getParameter("inode"), Folder.class);
			req.setAttribute(WebKeys.FOLDER_PARENT, folder);
		}
		java.util.List entryList = new java.util.ArrayList();

		try {
			User user = _getUser(req);

			if (user != null) {
				java.util.Locale locale = user.getLocale();
				java.util.TimeZone timeZone = user.getTimeZone();
				com.dotmarketing.business.Role[] roles = (com.dotmarketing.business.Role[])APILocator.getRoleAPI().loadRolesForUser(user.getUserId()).toArray(new Role[0]);

				String openNodes = req.getParameter("openNodes");

				String strContainer = req.getParameter("container");
				String structureInode = "";
				if (InodeUtils.isSet(strContainer)) {
					Container container = (Container) InodeFactory.getInode(strContainer, Container.class);
					if (InodeUtils.isSet(container.getInode())) {
						Structure currentStructure = (Structure) InodeFactory.getParentOfClass(container,
								Structure.class);
						if (!InodeUtils.isSet(currentStructure.getInode()))
							currentStructure = StructureFactory.getDefaultStructure();
						structureInode = currentStructure.getInode();
					}
				}

				openNodes = URLDecoder.decode(openNodes, "UTF-8");
				String view = req.getParameter("view");
				try {
					view = URLDecoder.decode(view, "UTF-8");
				} catch (Exception e) {
					Logger.debug(this, "Couldn't decode view. It might be decoded already", e);
				}
				String content = req.getParameter("content");
				content = URLDecoder.decode(content, "UTF-8");

				// Check if the user is a CMS Administrator
				boolean adminUser = false;
				try {

					String adminRoleKey = "";

					Role adminRole = APILocator.getRoleAPI().loadCMSAdminRole();
					adminRoleKey = adminRole.getRoleKey();

					Role[] userRoles = roles;
					for (int i = 0; i < userRoles.length; i++) {
						Role userRole = (Role) userRoles[i];
						if (userRole.getRoleKey().equals(adminRoleKey)) {
							adminUser = true;
							break;
						}
					}
				} catch (Exception e) {
					Logger.debug(this, "render: exception trying to check admin user privilegies. ", e);
				}

				Logger.debug(this, "render: adminUser = " + adminUser);

				if ((req.getParameter("contentOnly") != null) && (req.getParameter("contentOnly").equals("true"))
						&& (folder != null)) {

					view = java.net.URLDecoder.decode(view, "UTF-8");
					entryList = com.dotmarketing.portlets.folders.factories.FolderFactory.getEntriesTree(folder,
							openNodes, view, content, structureInode, locale, timeZone, roles, adminUser, user);
				} else {
					entryList = com.dotmarketing.portlets.folders.factories.FolderFactory.getFolderTree(openNodes,
							view, content, structureInode, locale, timeZone, roles, adminUser, user);
				}

				req.setAttribute("cmsAdminUser", String.valueOf(adminUser));

			} else {
				return mapping.findForward(Constants.COMMON_ERROR);
			}
		} catch (Exception e) {
			Logger.warn(this, "EXCEPTION" + e.getMessage(), e);
		}

		req.setAttribute(WebKeys.FOLDER_ENTRY_LIST, entryList);

		try {
			return mapping.findForward("portlet.ext.folders.load_tree");
		} catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);
			return mapping.findForward(Constants.COMMON_ERROR);
		}

	}

}
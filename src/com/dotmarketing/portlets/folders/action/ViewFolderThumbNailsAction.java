package com.dotmarketing.portlets.folders.action;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

/**
 * <a href="ViewQuestionsAction.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Maria Ahues
 *
 */
public class ViewFolderThumbNailsAction extends DotPortletAction {

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {

		try {
			User user = _getUser(req);
			_viewThumbNails(req,res,user);
			return mapping.findForward("portlet.ext.folders.view_thumbnails");
		}
		catch (Exception e) {
			return _handleException(e, req, mapping);
		}
	}

	protected void _viewThumbNails(RenderRequest req, RenderResponse res, User user) {

		Folder f= (Folder) InodeFactory.getInode(req.getParameter("inode"),Folder.class);
		req.setAttribute(WebKeys.FOLDER_PARENT, f);
		java.util.List entryList = new java.util.ArrayList();
		
		try {
			Role[] roles = (Role[])APILocator.getRoleAPI().loadRolesForUser(user.getUserId()).toArray(new Role[0]);
			String order = "file_name";	
			String condition = "(lower(file_name) like '%.jpg' or lower(file_name) like '%.gif' or lower(file_name) like '%.png')";
			String view = req.getParameter("view");
			if (UtilMethods.isSet(view))
				condition += " and " + view;
			else
				condition = " and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
			entryList = FileFactory.getFilesPerRoleParentAndCondition(roles,f,condition, user, order);
		}
		catch (Exception e) {
		}

		req.setAttribute(WebKeys.FOLDER_THUMBNAIL_LIST, entryList);

		
	}

}
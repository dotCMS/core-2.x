package com.dotmarketing.portlets.organization.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.struts.OrganizationForm;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.RenderRequestImpl;

/**
 * @author  Maria Ahues
 * @version $Revision: 1.2 $
 *
 */
public class ViewOrganizationsAction extends DotPortletAction {

	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {

		Logger.debug(this, "Running ViewOrganizationsAction!!!!");

		HttpServletRequest httpRequest = ((RenderRequestImpl) req).getHttpServletRequest();
		HttpSession session = httpRequest.getSession();
		
		User user = _getUser(req);
		
		try {
			if (req.getWindowState().equals(WindowState.NORMAL)) {
				return mapping.findForward("portlet.ext.organization.view");
			}
			else {
				String orderby = (req.getParameter("orderby")!=null ? req.getParameter("orderby") : "title");
				OrganizationForm organizationForm = (OrganizationForm) form;
				String keywords = organizationForm.getKeywords();
				String[] categories = organizationForm.getCategories();
				String category = (categories!=null && categories.length>=1) ? categories[0] : "";
				
				List organizations = new ArrayList();
				if (!UtilMethods.isSet(keywords) && (!UtilMethods.isSet(category) || "0".equals(category))) {
					//get all organizations
					organizations = OrganizationFactory.getAllFirstLevelOrganizations(orderby);
				}
				else {
					//filtering organizations
					organizations = OrganizationFactory.getFilteredOrganizations(keywords, category, orderby, user, false);
				}
				
				session.setAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION,"true");
				session.setAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION,"true");
				
				req.setAttribute(WebKeys.ORGANIZATION_VIEW, organizations);
				return mapping.findForward("portlet.ext.organization.view_organizations");
			}
		}
		catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);
			return mapping.findForward(Constants.COMMON_ERROR);
		}
	}
}
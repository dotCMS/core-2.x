package com.dotmarketing.portlets.facilities.action;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;

public class ViewFacilitiesAction extends DotPortletAction {

	/* 
	 * @see com.liferay.portal.struts.PortletAction#render(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.portlet.PortletConfig, javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {

		//Logger.info("Running ViewFacilitiesAction!!!!");

		try {
			//gets the user
			User user = _getUser(req);

			if (req.getWindowState().equals(WindowState.NORMAL)) {
				return mapping.findForward("portlet.ext.facilities.view");
			}
			else {
				/** @see com.dotmarketing.portal.struts.DotPortletAction._viewWebAssets **/
				_getFacilities(req, user);		
				return mapping.findForward("portlet.ext.facilities.view_facilities");
			}
		}
		catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);
			return mapping.findForward(Constants.COMMON_ERROR);
		}
	}
	
	protected void _getFacilities(RenderRequest req, User user) {
		
		List facilities = new ArrayList();
		String query = req.getParameter("query");
		if (query!=null && query.length()>0) {
			String condition = "lower(facility_name) like '%" + query.toLowerCase() + "%'";
			facilities = InodeFactory.getInodesOfClassByCondition(Facility.class,condition,"sort_order");
		}
		else {
			facilities = InodeFactory.getInodesOfClass(Facility.class,"sort_order");
		}
		req.setAttribute(WebKeys.FACILITIES_LIST,facilities);
		
	}
}
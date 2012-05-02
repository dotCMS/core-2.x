package com.dotmarketing.portlets.jobs.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portlets.jobs.factories.SearchfirmFactory;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;

public class ViewSearchFirmsAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        
    	List searchfirms = _getSearchFirms(form,req,res);
		req.setAttribute(WebKeys.SEARCHFIRMS_LIST, searchfirms);
        return mapping.findForward("portlet.ext.jobs.view_searchfirms");
    }
    
    private List _getSearchFirms(ActionForm form, RenderRequest req, RenderResponse res)
    throws Exception {    	

		String orderby = req.getParameter("orderby");
		if ((orderby==null) || (orderby.length()==0)) {
			orderby = "creationdate desc, searchfirm.name";
		}
		List mylist = SearchfirmFactory.getSearchfirms(orderby);
		return mylist;
    }
}

package com.dotmarketing.portlets.jobs.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portlets.jobs.factories.ResumeFactory;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;

public class ViewResumesAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        
    	List resumes = _getResumes(form,req,res);
		req.setAttribute(WebKeys.RESUMES_LIST, resumes);
        return mapping.findForward("portlet.ext.jobs.view_resumes");
    }
    
    private List _getResumes(ActionForm form, RenderRequest req, RenderResponse res)
    throws Exception {    	

		String orderby = req.getParameter("orderby");
		if ((orderby==null) || (orderby.length()==0)) {
			orderby = "creationdate desc";
		}
		List resumes = ResumeFactory.getResumes(orderby);
		return resumes;
    }
}

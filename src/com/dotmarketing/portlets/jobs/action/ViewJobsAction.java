package com.dotmarketing.portlets.jobs.action;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portlets.jobs.factories.JobsFactory;
import com.dotmarketing.portlets.jobs.model.Jobs;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;

public class ViewJobsAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        
        if (req.getWindowState().equals(WindowState.NORMAL)) {
            return mapping.findForward("portlet.ext.jobs.view");
        } else {
        	List jobsList = _getJobs(form,req,res);
    		req.setAttribute(WebKeys.JOBS_LIST, jobsList);       
            return mapping.findForward("portlet.ext.jobs.view_jobs");
        }
    }
    
    private List _getJobs(ActionForm form, RenderRequest req, RenderResponse res)
    throws Exception {    	

    	String orderby = req.getParameter("orderby");
		if ((orderby==null) || (orderby.length()==0)) {
			orderby = "jobs.entrydate desc";
		}
		List mylist = JobsFactory.getJobsRollByMonth(orderby,-4);
		Iterator it = mylist.iterator();
		while(it.hasNext()) {
			Jobs j = (Jobs) it.next();
			if(j.getEntrydate() != null && j.getExpdate() != null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(j.getEntrydate());
				cal.add(Calendar.MONTH, 3);
				j.setExpdate(cal.getTime());
				JobsFactory.save(j);
			}
		}
		return mylist;
    	
    }
}

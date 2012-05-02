/*
 * Created on Mar 28, 2005
 */
package com.dotmarketing.portlets.jobs.cms.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.jobs.factories.EmailFactory;
import com.dotmarketing.portlets.jobs.factories.JobsFactory;
import com.dotmarketing.portlets.jobs.model.Jobs;
import com.dotmarketing.portlets.jobs.struts.JobsForm;
import com.dotmarketing.util.Logger;

/**
 * @author Maru
 */
public class AddJobAction  extends DispatchAction{

	public ActionForward unspecified(ActionMapping mapping, ActionForm jf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
        //copies back into the form
		JobsForm form = (JobsForm) jf;
		Jobs job =  JobsFactory.getJob(request.getParameter("inode"));
        BeanUtils.copyProperties(form, job);
        request.setAttribute("jobsForm",form);
        
		ActionForward af = (mapping.findForward("addJobPage"));
		return af;
	}

	public ActionForward save(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Getting and setting request attributes
		JobsForm form = (JobsForm) lf;
		Jobs job =  JobsFactory.getJob(request.getParameter("inode"));
		BeanUtils.copyProperties(job, form);
		
		//Checking errors
		ActionMessages aes = form.validate(mapping, request);
		if(aes != null && aes.size() > 0){
	        request.setAttribute("jobsForm",form);
			saveErrors(request,aes);
			return mapping.getInputForward();
		}

        Logger.debug(this, "_saveJob: Inode = " + job.getInode());
        InodeFactory.saveInode(job);

        //copies back into the form
        BeanUtils.copyProperties(form, job);
        request.setAttribute("jobsForm",form);
        
        //Forwarding to the page
		ActionForward af = mapping.findForward("addJobPreviewPage");
		return af;
	}
	
	public ActionForward receipt (ActionMapping mapping, ActionForm jf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
        //copies back into the form
		JobsForm form = (JobsForm) jf;
		Jobs job =  JobsFactory.getJob(request.getParameter("inode"));
        BeanUtils.copyProperties(form, job);
        request.setAttribute("jobsForm",form);

        //Forwarding to the page
		ActionForward af = mapping.findForward("addJobReceiptPage");
		return af;
	}

	public ActionForward success(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		JobsForm job = (JobsForm) form;
		Jobs newJob = (Jobs) InodeFactory.getInode("" + job.getInode(),Jobs.class);		
		
		//send confirmation email.
		EmailFactory.sendCareerPostingReceipt("" + job.getInode(),newJob.getEmail(),"addJob");

		ActionForward af = (mapping.findForward("addJobPageThankYou"));
		return af;
	}

}

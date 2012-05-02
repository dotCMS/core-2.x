package com.dotmarketing.portlets.jobs.action;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.jobs.factories.JobsFactory;
import com.dotmarketing.portlets.jobs.model.Jobs;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


public class EditJobAction extends DotPortletAction {
    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
            ActionResponse res) throws Exception {
        String cmd = req.getParameter(com.liferay.portal.util.Constants.CMD);
        
        //wraps request to get session object
        ActionRequestImpl reqImpl = (ActionRequestImpl) req;
        HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
        
        String referer = req.getParameter("referer");
        
        //get Job from inode
        _editJob(form, req, res);
        
        // Save / Update a Job
        if (com.liferay.portal.util.Constants.ADD.equals(cmd)) {
            Logger.debug(this, "Jobs:  Saving Jobs");
            
            ///Validate Job
            if (!Validator.validate(req, form, mapping)) {
                Logger.debug(this, "Jobs:  Validation Job Failed");
                setForward(req, mapping.getInput());
                
                return;
            } else {
                try {
                    _saveJob(form, req, res);
                } catch (Exception e) {
                    _handleException(e, req);
                }
                
                _sendToReferral(req, res, referer);
                
                return;
            }
        }
        // Delete a Job
        else if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
            Logger.debug(this, "Jobs:  Deleting Job");
            
            try {
                _deleteJob(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }
        
        // Activate a Job
        else if (Constants.ACTIVATE.equals(cmd)) {
            Logger.debug(this, "Jobs:  Activating Job");
            
            try {
                _activateJob(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }

        BeanUtils.copyProperties(form, req.getAttribute(WebKeys.JOB_EDIT));
        setForward(req, "portlet.ext.jobs.edit_job");
    }
    
    /*Private Methods*/
    
    //save Job
    private void _saveJob(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        
        //wraps request to get session object
        ActionRequestImpl reqImpl = (ActionRequestImpl) req;
        HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
        BeanUtils.copyProperties(req.getAttribute(WebKeys.JOB_EDIT), form);
        
        Jobs job = (Jobs) req.getAttribute(WebKeys.JOB_EDIT);

        if(job.getExpdate() == null) {
        	if(job.isActive()) {
        		Calendar calendar = new GregorianCalendar();
        		calendar.setTime(job.getEntrydate());
        		calendar.add(Calendar.MONTH,3);
        		job.setExpdate(calendar.getTime());
        	}
        }
        
        Logger.debug(this, "_saveJob: Inode = " + job.getInode());
        InodeFactory.saveInode(job);
        
        SessionMessages.add(req, "message", "message.job.save");
    }
    
    //delete Job
    private void _deleteJob(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Jobs job = (Jobs) req.getAttribute(WebKeys.JOB_EDIT);
        InodeFactory.deleteInode(job);
        SessionMessages.add(req, "message", "message.job.delete");
    }
    
    //delete Job
    private void _activateJob(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {

    	Jobs job = (Jobs) req.getAttribute(WebKeys.JOB_EDIT);
		String activeStr = req.getParameter("active");
		boolean active = Boolean.parseBoolean(activeStr);
        job.setActive(active);
        if(job.isActive()) {
        	Calendar cal = new GregorianCalendar();
        	cal.setTime(job.getEntrydate());
        	cal.add(Calendar.MONTH,3);
        	job.setExpdate(cal.getTime());
        }
		InodeFactory.saveInode(job);
    }

    //view Job for Action request
    private void _editJob(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Jobs job =  JobsFactory.getJob(req.getParameter("inode"));
        req.setAttribute(WebKeys.JOB_EDIT, job);
    }
}

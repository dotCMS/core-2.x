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
import com.dotmarketing.portlets.jobs.factories.ResumeFactory;
import com.dotmarketing.portlets.jobs.model.Resume;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;

public class EditResumeAction extends DotPortletAction {
    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
            ActionResponse res) throws Exception {
        String cmd = req.getParameter(com.liferay.portal.util.Constants.CMD);
        
        //wraps request to get session object
        ActionRequestImpl reqImpl = (ActionRequestImpl) req;
        HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
        
        String referer = req.getParameter("referer");
        
        //get Resume from inode
        _editResume(form, req, res);
        
        // Save / Update a Resume
        if (com.liferay.portal.util.Constants.ADD.equals(cmd)) {
            Logger.debug(this, "Resume:  Saving Resume");
            
            ///Validate Resume
            if (!Validator.validate(req, form, mapping)) {
                Logger.debug(this, "Resume:  Validation Resume Failed");
                setForward(req, mapping.getInput());
                
                return;
            } else {
                try {
                    _saveResume(form, req, res);
                } catch (Exception e) {
                    _handleException(e, req);
                }
                
                _sendToReferral(req, res, referer);
                
                return;
            }
        }
        // Delete a Resume
        else if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
            Logger.debug(this, "Resume:  Deleting Resume");
            
            try {
                _deleteResume(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }
        
        // Activate a Resume
        else if (Constants.ACTIVATE.equals(cmd)) {
            Logger.debug(this, "Resume:  Activating Resume");
            
            try {
                _activateResume(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }

        // Activate a Resume
        else if ("deletefile".equals(cmd)) {
            Logger.debug(this, "Resume:  Deleting File from Resume");
            
            try {
                _deleteResumeFile(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }

        BeanUtils.copyProperties(form, req.getAttribute(WebKeys.RESUME_EDIT));
        setForward(req, "portlet.ext.jobs.edit_resume");
    }
    
    /*Private Methods*/
    //save resume
    private void _saveResume(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {

        BeanUtils.copyProperties(req.getAttribute(WebKeys.RESUME_EDIT), form);
        Resume resume =  (Resume)  req.getAttribute(WebKeys.RESUME_EDIT);

		if(resume.getExpirationdate() == null) {
        	if(resume.isActive()) {
        		Calendar calendar = new GregorianCalendar();
        		calendar.setTime(resume.getCreationdate());
        		calendar.add(Calendar.MONTH,6);
        		resume.setExpirationdate(calendar.getTime());
        	}
        }

        resume = ResumeFactory.save(resume);
        req.setAttribute(WebKeys.RESUME_EDIT, resume);
    	
        SessionMessages.add(req, "message.resume.save");
    }
    
    //delete resume
    private void _deleteResume(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Resume resume = (Resume) req.getAttribute(WebKeys.RESUME_EDIT);
        InodeFactory.deleteInode(resume);
        SessionMessages.add(req, "message", "message.resume.delete");
    }
    
    //delete resume
    private void _deleteResumeFile(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Resume resume = (Resume) req.getAttribute(WebKeys.RESUME_EDIT);
       // FileUpload file = (FileUpload) InodeFactory.getChildOfClass(resume, FileUpload.class);
       // resume.deleteChild(file);
      //  InodeFactory.deleteInode(file);
    }

    //activate resume
    private void _activateResume(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
    	//to get http request object
        Resume r =  (Resume)req.getAttribute(WebKeys.RESUME_EDIT);
        
        String activeStr = req.getParameter("active");
		boolean active = Boolean.parseBoolean(activeStr);
        r.setActive(active);
        if(r.isActive()) {
        	Calendar cal = new GregorianCalendar();
        	cal.setTime(r.getCreationdate());
        	cal.add(Calendar.MONTH,6);
        	r.setExpirationdate(cal.getTime());
        }            
		InodeFactory.saveInode(r);
    }

    //edit resume
    private void _editResume(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Resume resume =  ResumeFactory.getResume(req.getParameter("inode"));
        req.setAttribute(WebKeys.RESUME_EDIT, resume);
    }
}

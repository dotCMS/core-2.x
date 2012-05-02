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
import com.dotmarketing.portlets.jobs.factories.SearchfirmFactory;
import com.dotmarketing.portlets.jobs.model.Searchfirm;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


public class EditSearchFirmAction extends DotPortletAction {
    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
            ActionResponse res) throws Exception {
        String cmd = req.getParameter(com.liferay.portal.util.Constants.CMD);
        
        //wraps request to get session object
        ActionRequestImpl reqImpl = (ActionRequestImpl) req;
        HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
        
        String referer = req.getParameter("referer");
        
        //get Searchfirm from inode
        _editSearchfirm(form, req, res);
        
        // Save / Update a Searchfirm
        if (com.liferay.portal.util.Constants.ADD.equals(cmd)) {
            Logger.debug(this, "Searchfirm:  Saving Searchfirm");
            
            ///Validate Searchfirm
            if (!Validator.validate(req, form, mapping)) {
                Logger.debug(this, "Searchfirm:  Validation Searchfirm Failed");
                setForward(req, mapping.getInput());
                
                return;
            } else {
                try {
                    _saveSearchfirm(form, req, res);
                } catch (Exception e) {
                    _handleException(e, req);
                }
                
                _sendToReferral(req, res, referer);
                
                return;
            }
        }
        // Delete a Searchfirm
        else if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
            Logger.debug(this, "Searchfirm:  Deleting Searchfirm");
            
            try {
                _deleteSearchfirm(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }
        
        // Activate a Searchfirm
        else if (Constants.ACTIVATE.equals(cmd)) {
            Logger.debug(this, "Searchfirm:  Activating Searchfirm");
            
            try {
                _activateSearchfirm(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            
            _sendToReferral(req, res, referer);
            
            return;
        }

        BeanUtils.copyProperties(form, req.getAttribute(WebKeys.SEARCHFIRM_EDIT));
        setForward(req, "portlet.ext.jobs.edit_searchfirm");
    }
    
    /*Private Methods*/
    //save Searchfirm
    private void _saveSearchfirm(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {

        BeanUtils.copyProperties(req.getAttribute(WebKeys.SEARCHFIRM_EDIT), form);
        Searchfirm s =  (Searchfirm)  req.getAttribute(WebKeys.SEARCHFIRM_EDIT);
        
		if(s.getExpirationdate() == null) {
        	if(s.isActive()) {
        		Calendar calendar = new GregorianCalendar();
        		calendar.setTime(s.getCreationdate());
        		calendar.add(Calendar.MONTH,12);
        		s.setExpirationdate(calendar.getTime());
        	}
        }
        SearchfirmFactory.save(s);
        SessionMessages.add(req, "message.searchfirm.save");
    }
    
    //delete Searchfirm
    private void _deleteSearchfirm(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Searchfirm Searchfirm = (Searchfirm) req.getAttribute(WebKeys.SEARCHFIRM_EDIT);
        InodeFactory.deleteInode(Searchfirm);
        SessionMessages.add(req, "message", "message.searchfirm.delete");
    }
    
    //activate Searchfirm
    private void _activateSearchfirm(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
    	//to get http request object
        Searchfirm s =  (Searchfirm)req.getAttribute(WebKeys.SEARCHFIRM_EDIT);
        String activeStr = req.getParameter("active");
		boolean active = Boolean.parseBoolean(activeStr);
        s.setActive(active);
        if(s.isActive()) {
        	Calendar cal = new GregorianCalendar();
        	cal.setTime(s.getCreationdate());
        	cal.add(Calendar.MONTH,12);
        	s.setExpirationdate(cal.getTime());
        }            
		InodeFactory.saveInode(s);
    }

    //edit Searchfirm
    private void _editSearchfirm(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Searchfirm searchfirm =  SearchfirmFactory.getSearchfirm(req.getParameter("inode"));
        req.setAttribute(WebKeys.SEARCHFIRM_EDIT, searchfirm);
    }
}

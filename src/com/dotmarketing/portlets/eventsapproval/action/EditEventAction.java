/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.eventsapproval.action;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.Recurance;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;


/**
 * @author David Torres
 *
 */
public class EditEventAction extends DotPortletAction {
    
    public static boolean debug = false;
    
    public void processAction(
            ActionMapping mapping, ActionForm form, PortletConfig config,
            ActionRequest req, ActionResponse res)
    throws Exception {
        
        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;
        String referer = req.getParameter("referer");
        
        if ((referer!=null) && (referer.length()!=0)) {
            referer = URLDecoder.decode(referer,"UTF-8");
        }
        
        DotHibernate dh = new DotHibernate();
        dh.startTransaction();
        User user = _getUser(req);
        
        try {
            _retrieveEvent(req, res, config, form, user);
            
        } catch (ActionException ae) {
            _handleException(ae, req);
        }
        
        Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);
        
        
        if (!EventFactory.isAnEventAdministrator(user)) 
        {
        	ActionRequestImpl reqImpl = (ActionRequestImpl) req;
    		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
            ActionMessages am = new ActionMessages();
            am.add("messages",new ActionMessage("message.permissions.error"));
            saveMessages(httpReq,am);
            List events = EventFactory.getEventsWaitingForApproval();
        	req.setAttribute("events", events);
        	
            setForward(req,"portlet.ext.eventsapproval.view_events");
            dh.commitTransaction();
            return;
        }
        
        /*
         * Change the event status to approved 
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.APPROVE)) {
            try {
                
                _approveEvent(req, res, config, form, user);
                setForward(req,"portlet.ext.eventsapproval.view_events");
                
            } catch (ActionException ae) {
                _handleException(ae, req);
                return;
            }
        }
        /*
         * Change the event status to disapproved 
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.DISAPPROVE)) {
            try {
                
                _disapproveEvent(req, res, config, form, user);
                setForward(req,"portlet.ext.eventsapproval.view_events");
                
            } catch (ActionException ae) {
                _handleException(ae, req);
                return;
            }
        }
        dh.commitTransaction();
        
        List events = EventFactory.getEventsWaitingForApproval();
    	req.setAttribute("events", events);
    	
        
    }
    
    
    ///// ************** ALL METHODS HERE *************************** ////////
    
    private void _retrieveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
    throws Exception {
        
        String inode = (req.getParameter("eventInode")!=null) ? req.getParameter("eventInode") : "";
        Event e = null;
        if(!InodeUtils.isSet(inode)){
            e = EventFactory.newInstance();	
        } else {
            e = EventFactory.getEvent(inode);
        }
        req.setAttribute(WebKeys.EVENT_EDIT, e);
        Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
        req.setAttribute(WebKeys.RECURANCE_EDIT, r);
    }
    
    private void _approveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
    throws Exception {
        
        Event e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);
        Recurance r = (Recurance) req.getAttribute(WebKeys.RECURANCE_EDIT);
        
        if (InodeUtils.isSet(e.getInode())) {
	        if (InodeUtils.isSet(r.getInode())) {
	            List events = InodeFactory.getParentsOfClass(r, Event.class);
	            Iterator it = events.iterator();
	            while (it.hasNext()) {
	                Event ev = (Event) it.next();
	                Facility facility = (Facility) InodeFactory.getParentOfClass(e,Facility.class);	               
	                List conflicts = EventFactory.findConflicts(ev,facility);
	                if(conflicts.size() == 0)
	                {
	                	ev.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS);
	                	InodeFactory.saveInode(ev);
	                }
	            }
	        } 
	        else 
	        {
	            e.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS);
	            InodeFactory.saveInode(e);
	        }	        
	        Facility fac = (Facility) InodeFactory.getParentOfClass(e, Facility.class);
			Host currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(req);
	        EventFactory.sendEmailNotification(e, fac, user, true, currentHost);
        }        
    }
    
    private void _disapproveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
    throws Exception {
        
        Event e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);
        Recurance r = (Recurance) req.getAttribute(WebKeys.RECURANCE_EDIT);
        
        if (InodeUtils.isSet(e.getInode())) {
	        if (InodeUtils.isSet(r.getInode())) {
	            List events = InodeFactory.getParentsOfClass(r, Event.class);
	            Iterator it = events.iterator();
	            while (it.hasNext()) {
	                Event ev = (Event) it.next();
	                if(ev.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS)
	                {
	                	ev.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_DISAPPROVED_STATUS);
	                	InodeFactory.saveInode(ev);
	                }
	            }
	        } else {
	            e.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_DISAPPROVED_STATUS);
	            InodeFactory.saveInode(e);
	        }

	        Facility fac = (Facility) InodeFactory.getParentOfClass(e, Facility.class);
			Host currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(req);
	        EventFactory.sendEmailNotification(e, fac, user, true, currentHost);
        }
    }
    
}


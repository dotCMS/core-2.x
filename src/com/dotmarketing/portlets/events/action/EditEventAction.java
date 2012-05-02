/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.events.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.portlets.events.factories.RecuranceFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.EventRegistration;
import com.dotmarketing.portlets.events.model.Recurance;
import com.dotmarketing.portlets.events.struts.EventForm;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionErrors;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author David Torres
 *
 */
public class EditEventAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	CategoryAPI catAPI;
	PermissionAPI permAPI;
	
	public EditEventAction() {
		catAPI = APILocator.getCategoryAPI();
		permAPI = APILocator.getPermissionAPI();
	}
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {

        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;
		String referer = req.getParameter("referer");		
		
		if ((referer!=null) && (referer.length()!=0)) {
			referer = URLDecoder.decode(referer,"UTF-8");
		}

        DotHibernate.startTransaction();
		User user = _getUser(req);
		
    	boolean admin = EventFactory.isAnEventAdministrator(user);
    	req.setAttribute("isAdmin", new Boolean (admin));

        try {
			_retrieveEvent(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }

		Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);
		
		if (!EventFactory.hasPermissionsOverTheEvent(user, e)) {
			//add message
			//wraps request to get session object
			ActionRequestImpl reqImpl = (ActionRequestImpl)req;
			HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
	    	
			SessionErrors.add(httpReq, "error", "error.event.no.permissions");
			
			_handleException(new Exception("User dont have permissions to edit this event."), req);
			return;
		}
		 
		req.setAttribute("isEventAdministrator", new Boolean(EventFactory.isAnEventAdministrator(user)));
		req.setAttribute("isCMSAdministrator", new Boolean(com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user, Config.getStringProperty("CMS_ADMINISTRATOR_ROLE"))));
		
        /*
         * We are editing the recurance
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
				_editEvent(req, res, config, form, user);
				setForward(req,"portlet.ext.events.edit_event");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        
        /*
         * Save the event occurrence 
         */
        else if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					if (!_saveEvent(req, res, config, form, user))
						setForward(req,"portlet.ext.events.edit_event");
					else {
					    _sendToReferral(req,res,referer);
					    return;
					}
				} else {
					setForward(req,"portlet.ext.events.edit_event");
				}

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * Save the event recurance 
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.SAVE_SERIES)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					if (!_saveEventSeries(req, res, config, form, user))
						setForward(req,"portlet.ext.events.edit_event");
					else {
					    _sendToReferral(req,res,referer);
					    return;
					}
				}
				else {
					setForward(req,"portlet.ext.events.edit_event");
				}

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * If we are deleting the event,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
            try {
				_deleteEvent(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
			return;
        }
        /*
         * If we are deleting the event and all the series,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.DELETE_SERIES)) {
            try {
				_deleteEventSeries(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
			return;
        }
        /*
         * We are going to show the event registrations page.
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.SHOW_REGISTRATIONS)) {
            try {
				_showRegistrations(req, res, config, form, user);
				setForward(req,"portlet.ext.events.view_event_registrations");
            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * We are going to reset the event status
         */
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.RESET_STATUS)) {
            try {
				_resetStatus(req, res, config, form, user);
				_sendToReferral(req,res,referer);
				return;
            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        DotHibernate.commitTransaction();

    }


	///// ************** ALL METHODS HERE *************************** ////////

	private void _retrieveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		
		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		
		Event e = null;
		if(!InodeUtils.isSet(inode)){
			e = EventFactory.newInstance();	
			e.setContactName(user.getFullName());
			e.setContactEmail(user.getEmailAddress());
		} else {
			e = EventFactory.getEvent(inode);
		}
        req.setAttribute("eventForm", form);
        req.setAttribute(WebKeys.EVENT_EDIT, e);
    	Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
    	req.setAttribute(WebKeys.RECURANCE_EDIT, r);
    	
	}
	
	@SuppressWarnings("unchecked")
	private void _editEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

        
        EventForm formBean = ( EventForm ) form;
        Event e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);
        BeanUtils.copyProperties(formBean, e);

		
		//Set the files
		if (InodeUtils.isSet(e.getInode())) {
			ArrayList<String> fileInodes = new ArrayList<String> ();
			List<Identifier> identifiers = InodeFactory.getChildrenClass(e, Identifier.class);
			Iterator<Identifier> it = identifiers.iterator();
			while (it.hasNext()) {
				Identifier identifier = it.next();
				File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
				fileInodes.add(file.getInode());
			}
			formBean.setFilesInodes((String[])fileInodes.toArray(new String[0]));
		}
		
        //set the list of selected categories for the event
        java.util.List<Category> _cat = InodeFactory.getParentsOfClass(e, Category.class);
        formBean.setCategories(_cat);

        //get category list
        java.util.List<Category> categories = InodeFactory.getInodesOfClass(Category.class, "sort_order");
        req.setAttribute("categories", categories);
        
        //get the facility
        Facility fac = (Facility) InodeFactory.getParentOfClass(e, Facility.class);
        formBean.setFacilityInode(fac.getInode());       
	}


	@SuppressWarnings("unchecked")
	private boolean _saveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		//wraps request 
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		//Copying form information to the object
		EventForm formBean = ( EventForm ) form;
		Event e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);        
		
		boolean newEvent = (!InodeUtils.isSet(e.getInode()))?true:false;
		
		
		boolean eventAdmin = EventFactory.isAnEventAdministrator(user);
		int actualApprovalStatus = e.getApprovalStatus();
		
		if(eventAdmin ){
			actualApprovalStatus = formBean.getApprovalStatus();
		}
		

		
		
		Facility fac = (Facility) InodeFactory.getParentOfClass(e, Facility.class);
		boolean approvalChange = false;
		
		if (formBean.getStartDate().compareTo(e.getStartDate()) != 0 
			|| formBean.getEndDate().compareTo(e.getEndDate()) != 0
			|| formBean.getSetupDate().compareTo(e.getSetupDate()) != 0
			|| formBean.getBreakDate().compareTo(e.getBreakDate()) != 0
		    || !fac.getInode().equalsIgnoreCase(formBean.getFacilityInode())) {
		    	approvalChange = true;
		}
		
		//Check event conflicts
		String newFacInode = formBean.getFacilityInode();
		Facility newFacility = (Facility) InodeFactory.getInode(newFacInode, Facility.class);
		
		Event eventCopy = new Event ();
		BeanUtils.copyProperties(eventCopy, formBean);
		if (!req.getParameter("continueWithConflicts").equals("true") && EventFactory.findConflicts(eventCopy, newFacility).size() > 0) {
		    SessionMessages.add(httpReq, "error", "message.event.has.conflicts");
		    req.setAttribute("conflict_found", "true");
		    return false;
		}
		
		BeanUtils.copyProperties(e, formBean);
		e.setUserId(user.getUserId());
		
		if (approvalChange && ! eventAdmin) {
		    e.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS);
		} else {
		    e.setApprovalStatus(actualApprovalStatus);
		}

		InodeFactory.saveInode(e);
		User systemUser = APILocator.getUserAPI().getSystemUser();
		
		if (newEvent && eventAdmin) {
			Role role = APILocator.getRoleAPI().loadRoleByKey(Config.getStringProperty("EVENTS_ADMINISTRATOR"));
			if (UtilMethods.isSet(role)) {
				try {
					Permission permission = new Permission(e.getInode(), role.getId(), PermissionAPI.PERMISSION_READ);
					permAPI.save(permission, e, systemUser, false);
					permission = new Permission(e.getInode(), role.getId(), PermissionAPI.PERMISSION_WRITE);
					permAPI.save(permission, e, systemUser, false);
					permission = new Permission(e.getInode(), role.getId(), PermissionAPI.PERMISSION_PUBLISH);
					permAPI.save(permission, e, systemUser, false);
				} catch (Exception ex) {
				}
			}
			
			role = APILocator.getRoleAPI().loadRoleByKey(Config.getStringProperty("EVENTS_USER"));
			if (UtilMethods.isSet(role)) {
				try {
					Permission permission = new Permission(e.getInode(), role.getId(), PermissionAPI.PERMISSION_READ);
					permAPI.save(permission, e, systemUser, false);
					permission = new Permission(e.getInode(), role.getId(), PermissionAPI.PERMISSION_WRITE);
					permAPI.save(permission, e, systemUser, false);
				} catch (Exception ex) {
				}
			}
		}
		
		//wipe out the old categories
		java.util.List<Category> _cats = InodeFactory.getParentsOfClass(e, Category.class);
		Iterator<Category> it = _cats.iterator();
		while (it.hasNext()) {	
			Category cat = ( Category ) it.next();
			if(catAPI.canUseCategory(cat, user, false)){
				catAPI.removeParent(e, cat, user, false);
			}
		}
		
		//add the new categories
		String[] arr = formBean.getCategories();
		
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				Category node = ( Category ) catAPI.find(arr[i], user, false);
				catAPI.addParent(e, node, user, false);
			}
		}
		
		//wipe out the old facility 
		java.util.List<Facility> facilities = InodeFactory.getParentsOfClass(e, Facility.class);
		Iterator<Facility> it2 = facilities.iterator();
		while (it2.hasNext()) {
			fac = it2.next();
			fac.deleteChild(e);
		}
		
		//add the new facility
		newFacInode = formBean.getFacilityInode();
		if(InodeUtils.isSet(newFacInode))
		{
			newFacility = (Facility) InodeFactory.getInode(newFacInode, Facility.class);
			newFacility.addChild(e);
		}
	    
		_saveFiles(e, formBean.getFilesInodes());
		
		req.setAttribute(WebKeys.EVENT_EDIT, e);
    	Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
    	req.setAttribute(WebKeys.RECURANCE_EDIT, r);
		
		//add message
		SessionMessages.add(httpReq, "message", "message.event.saved");
		
		Host host = WebAPILocator.getHostWebAPI().getCurrentHost(req);
		
		//Email notifications
		if (newEvent) 
		{
		    EventFactory.sendEmailNotification(e, newFacility, user, false, host);
		}
		else if (approvalChange)
		{
		    EventFactory.sendEmailNotification(e, newFacility, user, true, host);
		}		    
		return true;		
	}

	private boolean _saveEventSeries(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

		EventForm formBean = ( EventForm ) form;
        Event e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);
        Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
		if (InodeFactory.countChildrenOfClass(e, Recurance.class) > 0) {
			r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
		}

		String newFacInode = formBean.getFacilityInode();
		Facility newFacility = (Facility) InodeFactory.getInode(newFacInode, Facility.class);
		
		Event eventCopy = new Event ();
		BeanUtils.copyProperties(eventCopy, formBean);
		if (!req.getParameter("continueWithConflicts").equals("true") && EventFactory.findConflicts(eventCopy, r, newFacility).size() > 0) {
		    SessionMessages.add(req, "error", "message.event.has.conflicts");
		    req.setAttribute("conflict_found", "true");
		    req.setAttribute("saving_series", "true");
		    return false;
		}
		
        if (!_saveEvent(req, res, config, form, user))
            return false;
        
		formBean = ( EventForm ) form;
        e = ( Event ) req.getAttribute(WebKeys.EVENT_EDIT);
        
		if (InodeFactory.countChildrenOfClass(e, Recurance.class) > 0) {
			r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
			r.setStartTime(e.getStartDate());
			r.setEndTime(e.getEndDate());
			RecuranceFactory.buildRecurringEvents(r,e);
		}
		
		_saveFiles(e, formBean.getFilesInodes());

		req.setAttribute(WebKeys.EVENT_EDIT, e);
    	r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
    	req.setAttribute(WebKeys.RECURANCE_EDIT, r);

    	//add message
		SessionMessages.add(req, "message", "message.event.saved");
		
		return true;

	}
	
	@SuppressWarnings("unchecked")
	private void _saveFiles (Event e, String[] fileInodes) {
		
		java.util.List<Identifier> _files = InodeFactory.getChildrenClass(e, Identifier.class);
		Iterator<Identifier> it = _files.iterator();
		while (it.hasNext()) {
			Identifier iden = it.next();
			e.deleteChild(iden);
		}

		for (int i = 0; i < fileInodes.length; i++) {
			String inode = fileInodes[i];
			File file = (File)InodeFactory.getInode(inode, File.class);
			Identifier identifier = IdentifierFactory.getParentIdentifier(file);
			e.addChild(identifier);
		}
	}
	
	private void _deleteEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

        Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);
        EventFactory.deleteEvent(e);
		SessionMessages.add(req, "message", "message.events.deleted");

	}

	private void _deleteEventSeries(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

        Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);
        EventFactory.deleteEventSeries(e);
		SessionMessages.add(req, "message", "message.events.seriesdeleted");

	}

	/**
	 * @param req
	 * @param res
	 * @param config
	 * @param form
	 * @param user
	 */
	@SuppressWarnings("unchecked")
	private void _showRegistrations(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user) 
	throws Exception {
		Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);
        List<EventRegistration> registrations = InodeFactory.getChildrenClass(e, EventRegistration.class);
        req.setAttribute(WebKeys.EVENT_REGISTRATIONS, registrations);
	}
	
	@SuppressWarnings("unchecked")
	private void _resetStatus(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user) 
	throws Exception {
		Event e = (Event) req.getAttribute(WebKeys.EVENT_EDIT);

		Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);
		if (InodeUtils.isSet(r.getInode())) { //Recur over the children
			List<Event> l = InodeFactory.getParentsOfClass(r, Event.class);
			Iterator<Event> i = l.iterator();
			while (i.hasNext()) {
				Event event = i.next();
				e.setReceivedAdminApproval(false);
				event.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS);
				InodeFactory.saveInode(event);
			}
		} else { //Simple event without recurrence
			e.setReceivedAdminApproval(false);
			e.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS);
			InodeFactory.saveInode(e);
		}
	}
	
}
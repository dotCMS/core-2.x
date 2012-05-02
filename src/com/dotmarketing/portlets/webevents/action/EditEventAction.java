/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.webevents.action;

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

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.factories.WebEventLocationFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.portlets.webevents.struts.WebEventForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author David Torres
 *
 */
public class EditEventAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	private CategoryAPI catAPI;
	
	public EditEventAction () {
		catAPI = APILocator.getCategoryAPI();
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
		
        try {
			_retrieveEvent(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }

        /*
         * We are editing the recurance
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
            	//_reorderEvents(form,req,res);
				_editEvent(req, res, config, form, user);
				setForward(req,"portlet.ext.webevents.edit_event");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
		// Reordering categories
		else if ((cmd != null) && cmd.equals("reorder")) {
            Logger.debug(this, "Reordering Events");
			
			try {
				_reorderEvents(form,req,res);
				_sendToReferral(req,res,referer);
			}
			catch(Exception e) {
				_handleException(e, req);
			}
			setForward(req,"portlet.ext.webevents.edit_event");
		}
        
        /*
         * Save the event occurrence 
         */
        else if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					_saveEvent(req, res, config, form, user);
					_sendToReferral(req,res,referer);
				} else { 
			        WebEvent e = ( WebEvent ) req.getAttribute(WebKeys.WEBEVENTS_EDIT);
			        //get Locations list
			        java.util.List locations = WebEventFactory.getEventLocations(e,"start_date desc");
			        req.setAttribute(WebKeys.WEBEVENT_LOCATIONS, locations);
					setForward(req,"portlet.ext.webevents.edit_event");
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
        }
        DotHibernate.commitTransaction();

    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		WebEvent e = null;
		if(!InodeUtils.isSet(inode)){
			e = WebEventFactory.newInstance();	
		} else {
			e = WebEventFactory.getWebEvent(inode);
		}
        req.setAttribute(WebKeys.WEBEVENTS_FORM, form);
        req.setAttribute(WebKeys.WEBEVENTS_EDIT, e);
	}
	
	private void _editEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

        WebEventForm formBean = ( WebEventForm ) form;
        WebEvent e = ( WebEvent ) req.getAttribute(WebKeys.WEBEVENTS_EDIT);
        BeanUtils.copyProperties(formBean, e);

		//Set the files
		if (InodeUtils.isSet(e.getInode())) {
			ArrayList<String> fileInodes = new ArrayList<String>();
			List identifiers = InodeFactory.getChildrenClass(e, Identifier.class);
			Iterator it = identifiers.iterator();
			while (it.hasNext()) {
				Identifier identifier = (Identifier)it.next();
				File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
				fileInodes.add(file.getInode());
			}
			formBean.setFilesInodes(fileInodes);
		}
		
		//IMAGES
		if (InodeUtils.isSet(e.getEventImage1())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getEventImage1(),Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier,File.class);
			formBean.setEventImage1(file.getInode());
		}
		if (InodeUtils.isSet(e.getEventImage2())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getEventImage2(),Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier,File.class);
			formBean.setEventImage2(file.getInode());
		}
		if (InodeUtils.isSet(e.getEventImage3())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getEventImage3(),Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier,File.class);
			formBean.setEventImage3(file.getInode());
		}
		if (InodeUtils.isSet(e.getEventImage4())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getEventImage4(),Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier,File.class);
			formBean.setEventImage4(file.getInode());
		}

		
		
		//set the list of selected categories for the event
        java.util.List _cat = InodeFactory.getParentsOfClass(e, Category.class);
        formBean.setCategories(_cat);

        //get category list
        java.util.List categories = InodeFactory.getInodesOfClass(Category.class, "sort_order");
        req.setAttribute(WebKeys.WEBEVENT_CATEGORIES, categories);
        
        //get Locations list
        java.util.List locations = WebEventFactory.getEventLocations(e,"start_date desc");
        req.setAttribute(WebKeys.WEBEVENT_LOCATIONS, locations);
        

	}


	private void _saveEvent(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		WebEventForm formBean = ( WebEventForm ) form;
        WebEvent e = ( WebEvent ) req.getAttribute(WebKeys.WEBEVENTS_EDIT);
		BeanUtils.copyProperties(e, formBean);
		InodeFactory.saveInode(e);
		
		//wipe out the old categories
		//java.util.Set _parents = e.getParents();
		List _parents = InodeFactory.getParentsOfClass(e, WebEvent.class);
		List<Category> _cats = catAPI.getParents(e, false, user, false);
		for(Category cat : _cats) {
			if(catAPI.canUseCategory(cat, user, false)){
				catAPI.removeParent(e, cat, user, false);
			}
		}
		
		//add the new categories
		String[] arr = formBean.getCategories();
		
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				Category cat = catAPI.find(arr[i], user, false);
				catAPI.addParent(e, cat, user, false);
			}
		}

		WebEventFactory.saveWebEvent(e);

		//Save the event files
		_saveFiles(e, formBean.getFilesInodesList());
		
		//Save the event images
		_saveImages(e, formBean);
		
		req.setAttribute(WebKeys.WEBEVENTS_EDIT, e);
		
		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.webevent.saved");
		
	}
	//reorder categories
	private void _reorderEvents(ActionForm form, ActionRequest req, ActionResponse res) throws Exception {
		
		int count = 0;
		try {
			count = Integer.parseInt(req.getParameter("count"));
		}
		catch (Exception e) {}
		String[] order = new String[(count)];
		for (int i = 0; i < order.length; i++) {
			WebEvent event = (WebEvent) InodeFactory.getInode(req.getParameter("inode" + i), WebEvent.class);
			String newOrderString  = req.getParameter("newOrder" + i);
			int newOrder = 0;
			if(UtilMethods.isInt(newOrderString))
			{
				newOrder = Integer.parseInt(newOrderString);
			}
			event.setSortOrder(newOrder);
			InodeFactory.saveInode(event);
		}
		SessionMessages.add(req, "message", "message.webevent.reorder");

	}

	private void _saveImages(WebEvent e, WebEventForm form) {

		//gets image inode and saves the identifier inode 
        String eventImage1 = form.getEventImage1();
        if (InodeUtils.isSet(eventImage1)) {
        	File file = (File) InodeFactory.getInode(eventImage1,File.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
        	e.setEventImage1(identifier.getInode());
        }
		
		//gets image inode and saves the identifier inode 
        String eventImage2 = form.getEventImage2();
        if (InodeUtils.isSet(eventImage2)) {
        	File file = (File) InodeFactory.getInode(eventImage2,File.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
        	e.setEventImage2(identifier.getInode());
        }

		//gets image inode and saves the identifier inode 
        String eventImage3 = form.getEventImage3();
        if (InodeUtils.isSet(eventImage3)) {
        	File file = (File) InodeFactory.getInode(eventImage3,File.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
        	e.setEventImage3(identifier.getInode());
        }

		//gets image inode and saves the identifier inode 
        String eventImage4 = form.getEventImage4();
        if (InodeUtils.isSet(eventImage4)) {
        	File file = (File) InodeFactory.getInode(eventImage4,File.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
        	e.setEventImage4(identifier.getInode());
        }
        
        //Save changes
        WebEventFactory.saveWebEvent(e);
	
	}
	
	private void _saveFiles (WebEvent e, String[] fileInodes) {
		
		java.util.List _files = InodeFactory.getChildrenClass(e, Identifier.class);
		Iterator it = _files.iterator();
		while (it.hasNext()) {
			Identifier iden = ( Identifier ) it.next();
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

        WebEvent e = ( WebEvent ) req.getAttribute(WebKeys.WEBEVENTS_EDIT);
        
        //delete all locations for this event
        List locations = WebEventFactory.getEventLocations(e,"start_date");
        Iterator locationsIter = locations.iterator();
        while (locationsIter.hasNext()) {
        	WebEventLocation location = (WebEventLocation) locationsIter.next();
        	WebEventLocationFactory.deleteWebEventLocation(location);
        }
        //delete this event
        WebEventFactory.deleteWebEvent(e);
		SessionMessages.add(req, "message", "message.webevents.deleted");

	}

}


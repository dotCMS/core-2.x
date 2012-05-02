/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.webevents.action;

import java.net.URLDecoder;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.factories.WebEventLocationFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.portlets.webevents.struts.WebEventLocationForm;
import com.dotmarketing.util.InodeUtils;
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
public class EditEventLocationAction extends DotPortletAction {
	
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

        DotHibernate.startTransaction();
		User user = _getUser(req);
		
        try {
        	_reorderEvents(form,req,res);
			_retrieveEventLocation(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }

        /*
         * We are editing the recurance
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
				_editEventLocation(req, res, config, form, user);
				setForward(req,"portlet.ext.webevents.edit_location");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        
        /*
         * Save the EventLocation occurrence 
         */
        else if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					if (_saveEventLocation(req, res, config, form, user)) {
						_sendToReferral(req,res,referer);
					}
					else {
						setForward(req,"portlet.ext.webevents.edit_location");
					}
				} else setForward(req,"portlet.ext.webevents.edit_location");

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * If we are deleting the EventLocation,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
            try {
				_deleteEventLocation(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
        }
        DotHibernate.commitTransaction();

    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveEventLocation(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		WebEventLocation e = null;
		if(!InodeUtils.isSet(inode)){
			e = WebEventLocationFactory.newInstance();	
		} else {
			e = WebEventLocationFactory.getWebEventLocation(inode);
		}
        req.setAttribute(WebKeys.WEBEVENTS_LOCATION_FORM, form);
        req.setAttribute(WebKeys.WEBEVENTS_LOCATION_EDIT, e);
	}
	
	private void _editEventLocation(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

        WebEventLocationForm formBean = ( WebEventLocationForm ) form;

        if (!UtilMethods.isSet(formBean.getTitle())) {
        	WebEvent event = WebEventFactory.getWebEvent(formBean.getWebEventInode());
        	formBean.setTitle(event.getTitle());
        }

        WebEventLocation e = ( WebEventLocation ) req.getAttribute(WebKeys.WEBEVENTS_LOCATION_EDIT);
        BeanUtils.copyProperties(formBean, e);
        
        //set links on the form bean
		if (InodeUtils.isSet(e.getHotelLink())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getHotelLink(),Identifier.class);
			Link link = (Link) IdentifierFactory.getWorkingChildOfClass(identifier,Link.class);
			formBean.setHotelLink(link.getInode());
			formBean.setSelectedhotelLink(link.getProtocal() + link.getUrl());
		}
		if (InodeUtils.isSet(e.getPastEventLink())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(e.getPastEventLink(),Identifier.class);
			Link link = (Link) IdentifierFactory.getWorkingChildOfClass(identifier,Link.class);
			formBean.setPastEventLink(link.getInode());
			formBean.setSelectedpastEventLink(link.getProtocal() + link.getUrl());
		}

	}


	private boolean _saveEventLocation(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		WebEventLocationForm formBean = ( WebEventLocationForm ) form;
        WebEventLocation e = ( WebEventLocation ) req.getAttribute(WebKeys.WEBEVENTS_LOCATION_EDIT);
        
        Date startDate = formBean.getStartDate();
        Date endDate = formBean.getEndDate();
        if (startDate.after(endDate)) {
			//add message
			SessionMessages.add(httpReq, "error", "message.webeventlocation.startDateAfterendDate");
			return false;
        }
		BeanUtils.copyProperties(e, formBean);
		InodeFactory.saveInode(e);
		
		req.setAttribute(WebKeys.WEBEVENTS_LOCATION_EDIT, e);
		
		//save link relationships 
		//gets link inode and saves the identifier inode 
        String hotelLink = formBean.getHotelLink();
        if (InodeUtils.isSet(hotelLink)) {
        	Link link = (Link) InodeFactory.getInode(hotelLink,Link.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(link);
        	e.setHotelLink(identifier.getInode());
        }
		//gets link inode and saves the identifier inode 
        String pastEventLink = formBean.getPastEventLink();
        if (InodeUtils.isSet(pastEventLink)) {
        	Link link = (Link) InodeFactory.getInode(pastEventLink,Link.class);
        	Identifier identifier = IdentifierFactory.getIdentifierByInode(link);
        	e.setPastEventLink(identifier.getInode());
        }
		
		
		//add message
		SessionMessages.add(httpReq, "message", "message.webeventlocation.saved");
		return true;
	}

	private void _deleteEventLocation(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

        WebEventLocation e = ( WebEventLocation ) req.getAttribute(WebKeys.WEBEVENTS_LOCATION_EDIT);
        WebEventLocationFactory.deleteWebEventLocation(e);
		SessionMessages.add(req, "message", "message.webeventlocation.deleted");

	}
	
//	reorder events
	private void _reorderEvents(ActionForm form, ActionRequest req, ActionResponse res) throws Exception {
		
		int count = 0;
		try {
			count = Integer.parseInt(req.getParameter("count"));
		}
		catch (Exception e) {}
		String[] order = new String[(count)];
		for (int i = 0; i < order.length; i++) {
			WebEvent event = (WebEvent) InodeFactory.getInode(req.getParameter("inode" + i), WebEvent.class);
			event.setSortOrder(Integer.parseInt(req.getParameter("newOrder" + i)));
			InodeFactory.saveInode(event);
		}
		//SessionMessages.add(req, "message", "message.webevent.reorder");

	}

}


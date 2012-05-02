package com.dotmarketing.portlets.event_registrations.action;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.event_registrations.factories.WebEventAttendeeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventRegistrationFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.event_registrations.struts.WebEventAttendeeForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventRegistrationForm;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author Maria Ahues
 *
 */
public class EditRegistrationAction extends DotPortletAction {
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {

        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;
		String referer = req.getParameter("referer");

		//if ((referer!=null) && (referer.length()!=0)) {
		//	referer = URLDecoder.decode(referer,"UTF-8");
		//}

        DotHibernate.startTransaction();
		User user = _getUser(req);
		
        try {
			_retrieveEventRegistration(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }

        /*
         * We are editing the registration
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
				_editEventRegistration(req, res, config, form);
				setForward(req,"portlet.ext.webevents_registration.edit_registration");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        /*
         * We are saving an attendee
         */
        if ((cmd != null) && cmd.equals("add_attendee")) {
            try {
    			_addAttendee(req, res, config, form, mapping, user);
				_editEventRegistration(req, res, config, form);
				setForward(req,"portlet.ext.webevents_registration.edit_registration");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        /*
         * We are deleting an attendee
         */
        if ((cmd != null) && cmd.equals("delete_attendee")) {
            try {
        		_deleteAttendee(req, res, config, form, user);
				_editEventRegistration(req, res, config, form);
				setForward(req,"portlet.ext.webevents_registration.edit_registration");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        
        /*
         * Save the event registration 
         */
        else if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					_saveEventRegistration(req, res, config, form, user);
					_editEventRegistration(req, res, config, form);
					//_sendToReferral(req,res,referer);
					setForward(req,"portlet.ext.webevents_registration.edit_registration");
				} else { 
					setForward(req,"portlet.ext.webevents_registration.edit_registration");
				}

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        else if ((cmd != null) && cmd.equals("sendInvoice")) {
            try {
            	_sendInvoice(mapping, req, res, config, form);
				_editEventRegistration(req, res, config, form);
            } catch (Exception ae) {
				_handleException(ae, req);
            }
			setForward(req,"portlet.ext.webevents_registration.edit_registration");
        }
        /*
         * If we are deleting the registration,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
            try {
				_deleteEventRegistration(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
        }
        DotHibernate.commitTransaction();

    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _sendInvoice(ActionMapping mapping, ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
	throws Exception {
        WebEventRegistration registration = ( WebEventRegistration ) req.getAttribute(WebKeys.WEBEVENTS_REG_EDIT);
		UserProxy proxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
		User user =  APILocator.getUserAPI().loadUserById(proxy.getUserId(), APILocator.getUserAPI().getSystemUser(), false);
		String path = "";
		
		WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
		
		if (event.isInstitute()) {
			if (registration.getPaymentType() == 1)
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.creditCardConfirmationEmail").getPath();
			else
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.checkConfirmationEmail").getPath();
		} else {
			//if it's been paid we send the regular email
			if (registration.getRegistrationStatus() == 1)
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.webinarConfirmationEmail").getPath();
			else
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.webinarCheckConfirmationEmail").getPath();
		}
		
		try {
			Host currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(req);
			StringBuffer writer = UtilMethods.getURL("http://"
					+ currentHost.getHostname() + path
					+ "?registrationInode=" + registration.getInode());
			
			String[] reportEmails = Config.getStringArrayProperty("BCCEMAIL_REGISTRATION_ADDRESSES");
			
			StringBuffer bcc = new StringBuffer();
			for (String email : reportEmails) {
				if (bcc.toString().length() > 0)
					bcc.append(", ");
				bcc.append(email);
			}
			
			Mailer m = new Mailer();
			m.setToEmail(user.getEmailAddress());
			m.setSubject(Config.getStringProperty("WEB_EVENT_REGISTRATION_EMAIL_TITLE"));
			m.setHTMLBody(writer.toString().trim());
			m.setFromEmail(Config.getStringProperty("EMAIL_REGISTRATION_ADDRESS"));
			m.setBcc(bcc.toString());
			m.sendMessage();
			
			//wraps request to get session object
			ActionRequestImpl reqImpl = (ActionRequestImpl)req;
			HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
			//add message
			SessionMessages.add(httpReq, "message", "message.webevent_registration.email_sent");
		} catch (Exception e) {
			Logger.error(EditRegistrationAction.class,e.getMessage());
		}
		
	}
	
	private void _retrieveEventRegistration(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		WebEventRegistration e = null;
		if(inode.equals("0")){
			e = WebEventRegistrationFactory.newInstance();	
		} else {
			e = WebEventRegistrationFactory.getWebEventRegistration(inode);
		}
        req.setAttribute(WebKeys.WEBEVENTS_REG_FORM, form);
        req.setAttribute(WebKeys.WEBEVENTS_REG_EDIT, e);
	}
	
	private void _editEventRegistration(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form)
		throws Exception {

        WebEventRegistrationForm formBean = ( WebEventRegistrationForm ) form;
        WebEventRegistration e = ( WebEventRegistration ) req.getAttribute(WebKeys.WEBEVENTS_REG_EDIT);
        BeanUtils.copyProperties(formBean, e);

        
        //statuses list
        List<HashMap> statuses = new ArrayList<HashMap>();
        String[] statusesArray = Config.getStringArrayProperty("EREG_STATUSES");
        for (int i=0;i<statusesArray.length;i++) {
        	HashMap<String, String> hs = new HashMap<String, String>();
        	String status = statusesArray[i];
        	hs.put("statusName",Config.getStringProperty(status + "_FN"));
        	hs.put("statusValue",Config.getStringProperty(status));
            statuses.add(hs);
        }
        req.setAttribute("registrationStatuses",statuses.iterator());
        
        //paymentTypes list
        List<HashMap> paymentTypes = new ArrayList<HashMap>();
        String[] paymentTypesArray  = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
        for (int i=0;i<paymentTypesArray.length;i++) {
        	String pType = paymentTypesArray[i];
        	HashMap<String, String> hs = new HashMap<String, String>();
        	hs.put("paymentTypeName",Config.getStringProperty(pType + "_FN"));
        	hs.put("paymentTypeValue",Config.getStringProperty(pType));
        	paymentTypes.add(hs);
        }
        
        req.setAttribute("paymentTypes",paymentTypes.iterator());

        //get registrant contact info
        if (InodeUtils.isSet(e.getUserInode())){
		UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(e.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
        User user = APILocator.getUserAPI().loadUserById(registrantUser.getUserId(), APILocator.getUserAPI().getSystemUser(), false);
		
		formBean.setRegistrantFirstName(user.getFirstName());
		formBean.setRegistrantLastName(user.getLastName());
		formBean.setRegistrantEmail(user.getEmailAddress());
		formBean.setUserId(user.getUserId());
		if (InodeUtils.isSet(registrantUser.getInode())) {
			Organization organization = (Organization) InodeFactory.getParentOfClass(registrantUser,Organization.class);
			if (InodeUtils.isSet(organization.getInode())) {
				formBean.setRegistrantFacility(organization.getTitle().trim());
				formBean.setRegistrantFacilityInode(organization.getInode());
			
				Organization parentSystem = OrganizationFactory.getParentOrganization(organization);
				if (InodeUtils.isSet(parentSystem.getInode())) {
					formBean.setRegistrantSystem(parentSystem.getTitle().trim());
					formBean.setRegistrantSystemInode(parentSystem.getInode());
				}
			}
		}        
        }
        else{
        	formBean.setRegistrantFirstName("");
    		formBean.setRegistrantLastName("");
    		formBean.setRegistrantEmail("");
        }
        //get Attendee list
        List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(e);
        req.setAttribute(WebKeys.WEBEVENT_REG_ATTENDEES, attendees);
		
		WebEventAttendee attendee = WebEventAttendeeFactory.getWebEventAttendee(formBean.getCurrentAttendeeInode());
		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		
		if (InodeUtils.isSet(attendee.getInode())) {
			BeanUtils.copyProperties(attendeeForm,attendee);
			req.setAttribute("WebEventAttendeeForm",attendeeForm);
		}
	}


	private void _saveEventRegistration(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		WebEventRegistrationForm formBean = ( WebEventRegistrationForm ) form;
        WebEventRegistration e = ( WebEventRegistration ) req.getAttribute(WebKeys.WEBEVENTS_REG_EDIT);
        boolean runAutoStatus = ((e.getTotalPaid() != formBean.getTotalPaid()) ? true : false);      
        e.setTotalPaid(formBean.getTotalPaid());
        e.setCheckBankName(formBean.getCheckBankName());
        e.setCheckNumber(formBean.getCheckNumber());
        e.setPoNumber(formBean.getPoNumber());
        e.setBillingAddress1(formBean.getBillingAddress1());
        e.setBillingAddress2(formBean.getBillingAddress2());
        e.setBillingCity(formBean.getBillingCity());
        e.setBillingContactEmail(formBean.getBillingContactEmail());
        e.setBillingContactName(formBean.getBillingContactName());
        e.setBillingContactPhone(formBean.getBillingContactPhone());
        e.setBillingState(formBean.getBillingState());
        e.setBillingZip(formBean.getBillingZip());
        e.setRegistrationStatus(formBean.getRegistrationStatus());
        e.setPaymentType(formBean.getPaymentType());
        e.setInvoiceNumber(formBean.getInvoiceNumber());
        //all attendees for this event registration
        java.util.List attendees = WebEventRegistrationFactory.getEventAttendees(e);
        int total = 0;
        Iterator attendeesIter = attendees.iterator();
        while (attendeesIter.hasNext()) {
        	WebEventAttendee attendee = (WebEventAttendee) attendeesIter.next();
        	total += attendee.getRegistrationPrice();
        }
		//sets new total due
        e.setTotalDue(total - e.getTotalPaid());
        e.setTotalRegistration(total);
        formBean.setTotalDue(e.getTotalDue());
        if(runAutoStatus)
        {
        	_setRegistrationStatus(formBean);
        	e.setRegistrationStatus(formBean.getRegistrationStatus());
        }
		e.setModified_QB(true);
		WebEventRegistrationFactory.saveWebEventRegistration(e);
		req.setAttribute(WebKeys.WEBEVENTS_REG_EDIT, e);
		
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		//add message
		SessionMessages.add(httpReq, "message", "message.webevent_registration.saved");
		
	}

	private void _deleteEventRegistration(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

        WebEventRegistration e = ( WebEventRegistration ) req.getAttribute(WebKeys.WEBEVENTS_REG_EDIT);
        
        //delete all attendees for this event registration
        java.util.List attendees = WebEventRegistrationFactory.getEventAttendees(e);
        Iterator attendeesIter = attendees.iterator();
        while (attendeesIter.hasNext()) {
        	WebEventAttendee attendee = (WebEventAttendee) attendeesIter.next();
        	WebEventAttendeeFactory.deleteWebEventAttendee(attendee);
        }
        //delete this event
        WebEventRegistrationFactory.deleteWebEventRegistration(e);
		SessionMessages.add(req, "message", "message.webevent_registration.deleted");

	}
	private void _deleteAttendee(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;
        //get attendee to delete
        WebEventAttendee attendee = WebEventAttendeeFactory.getWebEventAttendee(registrationForm.getCurrentAttendeeInode());
        WebEventAttendeeFactory.deleteWebEventAttendee(attendee);
		registrationForm.setCurrentAttendeeInode(null);
		
		req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
		SessionMessages.add(req, "message", "message.webevent_registration.delete_attendee");
	
	}
	
	private void _setRegistrationStatus(WebEventRegistrationForm registrationForm) {
		if (registrationForm.getTotalDue() > 0) {
			registrationForm.setRegistrationStatus(Config.getIntProperty("EREG_WAITING"));
		}
		else if (registrationForm.getTotalDue() < 0) {
			registrationForm.setRegistrationStatus(Config.getIntProperty("EREG_REIMBURSEMENT"));
		}
		else {
			registrationForm.setRegistrationStatus(Config.getIntProperty("EREG_PAID"));
		}
	}
	
	private void _addAttendee(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, ActionMapping mapping, User user)
	throws Exception {
		
		//this event registration
        WebEventRegistration e = ( WebEventRegistration ) req.getAttribute(WebKeys.WEBEVENTS_REG_EDIT);
        
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;
		//String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
		//String userId = registrationForm.getUserId();
		//user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
		//UserProxy userProxy = UserProxyFactory.getUserProxy(user);
		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setFirstName(registrationForm.getCurrentAttendeeFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastName());
		attendeeForm.setTitle(registrationForm.getCurrentAttendeeTitle());
		attendeeForm.setBadgeName(registrationForm.getCurrentAttendeeBadgeName());
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		attendeeForm.setRegistrationPrice(registrationForm.getCurrentAttendeePrice());

		//validates the form bean
		if (Validator.validate(req, form, mapping)) {

			//get all attendees for this event registration
	        WebEventAttendee attendee = WebEventAttendeeFactory.getWebEventAttendee(registrationForm.getCurrentAttendeeInode());
	        if (InodeUtils.isSet(attendee.getInode())) {
	    		///sets total due and new registration status
				registrationForm.setTotalDue(registrationForm.getTotalDue() - attendee.getRegistrationPrice());
				registrationForm.setTotalDue(registrationForm.getTotalDue() + registrationForm.getCurrentAttendeePrice());
				_setRegistrationStatus(registrationForm);
				
	        	//updating existing one
	        	attendee.setFirstName(registrationForm.getCurrentAttendeeFirstName());
	        	attendee.setLastName(registrationForm.getCurrentAttendeeLastName());
	        	attendee.setTitle(registrationForm.getCurrentAttendeeTitle());
	        	attendee.setBadgeName(registrationForm.getCurrentAttendeeBadgeName());
	        	attendee.setEmail(registrationForm.getCurrentAttendeeEmail());
	        	attendee.setRegistrationPrice(registrationForm.getCurrentAttendeePrice());
	        	WebEventAttendeeFactory.saveWebEventAttendee(attendee);
				req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
				registrationForm.setCurrentAttendeeInode(null);
				SessionMessages.add(httpReq, "message", "message.webevent_registration.update_attendee");
	        }
	        else {
	        	//adding new one
	            java.util.List currentAttendees = WebEventRegistrationFactory.getEventAttendeesByEmail(e,registrationForm.getCurrentAttendeeEmail());
	        	/*if (currentAttendees.size()>0) {
					SessionMessages.add(httpReq, "message", "error.attendee.already.registered");
					req.setAttribute("WebEventAttendeeForm",attendeeForm);
	        	}
	        	else {*/
	        		///sets total due and new registration status
    			registrationForm.setTotalDue(registrationForm.getTotalDue() + registrationForm.getCurrentAttendeePrice());
    			_setRegistrationStatus(registrationForm);

        		attendee.setFirstName(registrationForm.getCurrentAttendeeFirstName());
            	attendee.setLastName(registrationForm.getCurrentAttendeeLastName());
            	attendee.setTitle(registrationForm.getCurrentAttendeeTitle());
            	attendee.setBadgeName(registrationForm.getCurrentAttendeeBadgeName());
            	attendee.setEmail(registrationForm.getCurrentAttendeeEmail());
            	attendee.setRegistrationPrice(registrationForm.getCurrentAttendeePrice());
            	attendee.setEventRegistrationInode(e.getInode());
            	WebEventAttendeeFactory.saveWebEventAttendee(attendee);
    			req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
    			registrationForm.setCurrentAttendeeInode(null);
				SessionMessages.add(httpReq, "message", "message.webevent_registration.add_attendee");
	        	//}
	        }
	        //new------------------------------------------
	        java.util.List attendees = WebEventRegistrationFactory.getEventAttendees(e);
	        int total = 0;
	        Iterator attendeesIter = attendees.iterator();
	        while (attendeesIter.hasNext()) {
	        	WebEventAttendee attendee2 = (WebEventAttendee) attendeesIter.next();
	        	total += attendee2.getRegistrationPrice();
	        }
			//sets new total due
	        e.setTotalDue(total - e.getTotalPaid());
	        e.setTotalRegistration(total);
	        e.setModified_QB(true);
	        registrationForm.setTotalDue(e.getTotalDue());
	        //new-----------------------------------------
		} else {
			req.setAttribute("WebEventAttendeeForm",attendeeForm);
		}
	}
	

}


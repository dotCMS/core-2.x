package com.dotmarketing.portlets.event_registrations.cms.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventRegistrationFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.event_registrations.struts.WebEventAttendeeForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventRegistrationForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;

/**
 * 
 * @author Oswaldo Gallango
 *
 */

public class EventRegistrationDetailAction extends DispatchAction {
	
	/**
	 * Beginning the Event Registration process This method forwards to the user
	 * registration pages to check the user info
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		
		WebEventRegistrationForm form = (WebEventRegistrationForm) lf;
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		getRegisterInfo(form, request, response);
		return mapping.findForward("webEventDetailPage");
	}
	
	public ActionForward saveAttendee(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;
		
		if (!Validator.validate(request, registrationForm, mapping)) {
			
			return mapping.findForward("webEventDetailPage");
		}
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		if(!InodeUtils.isSet(String.valueOf(registrationForm.getCurrentAttendeeInode()))
				|| !UtilMethods.isSet(registrationForm.getCurrentAttendeeFirstName())
						|| !UtilMethods.isSet(registrationForm.getCurrentAttendeeLastName())
								|| !UtilMethods.isSet(registrationForm.getCurrentAttendeeEmail())
								|| !InodeUtils.isSet(registrationForm.getCurrentAttendeeInode())){
			
			ActionMessages messages = new ActionMessages ();
			messages.add("message" ,new ActionMessage ("error.dotmarketing.webattendee.updated"));
			saveMessages(request, messages);
			
			registrationForm.setCurrentAttendeeBadgeName("");
			registrationForm.setCurrentAttendeeEmail("");
			registrationForm.setCurrentAttendeeFirstName("");
			registrationForm.setCurrentAttendeeLastName("");
			registrationForm.setCurrentAttendeeTitle("");
			registrationForm.setCurrentAttendeeInode(null);
			
		}else{
			
			saveAttendeeList(registrationForm, request, response);
			
			ActionMessages messages = new ActionMessages ();
			messages.add("message" ,new ActionMessage ("dotmarketing.webattendee.updated"));
			saveMessages(request, messages);
		}
		
		return mapping.findForward("webEventDetailPage");
		
	}
	
	public ActionForward save(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		//WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		//saveRegistration(registrationForm, request, response);
		
		ActionMessages messages = new ActionMessages ();
		messages.add("message" ,new ActionMessage ("dotmarketing.webregistration.updated"));
		saveMessages(request, messages);
		
		//return mapping.findForward("webEventDetailPage");
		return mapping.findForward("registrationHistoryPage");
		
	}
	
	public ActionForward back(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		
		session.removeAttribute("webEventRegistrationForm");
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		return mapping.findForward("registrationHistoryPage");
		
	}
	
public void saveAttendeeList(WebEventRegistrationForm form, HttpServletRequest request, HttpServletResponse response){
		
		
		List<WebEventAttendeeForm> currentAttendees = form.getEventAttendees();
		List<WebEventAttendeeForm> updatedAttendees = new ArrayList<WebEventAttendeeForm>();
		
		java.util.Iterator attendeesIter = currentAttendees.iterator();
		
		while(attendeesIter.hasNext()){
			try {
				
				WebEventAttendeeForm attendee = (WebEventAttendeeForm) attendeesIter.next();
				WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm ();
				if(attendee.getInode().equalsIgnoreCase(form.getCurrentAttendeeInode())){
					attendee.setBadgeName(form.getCurrentAttendeeBadgeName());
					attendee.setTitle(form.getCurrentAttendeeTitle());
					attendee.setEmail(form.getCurrentAttendeeEmail());
					attendee.setFirstName(form.getCurrentAttendeeFirstName());
					attendee.setLastName(form.getCurrentAttendeeLastName());
					
					WebEventAttendee dbAttendee = (WebEventAttendee) InodeFactory.getInode(attendee.getInode(), WebEventAttendee.class);
					BeanUtils.copyProperties(dbAttendee, attendee);
					InodeFactory.saveInode(dbAttendee);
					
					WebEventRegistration dbReg = (WebEventRegistration) InodeFactory.getInode(form.getInode(), WebEventRegistration.class);
					dbReg.setModified_QB(true);
					InodeFactory.saveInode(dbReg);
					
				}
				BeanUtils.copyProperties(attendeeForm, attendee);
				updatedAttendees.add(attendeeForm);
				
				
			} catch (IllegalAccessException e) {
				Logger.error(this,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				Logger.error(this,e.getMessage(),e);
			}
			
			
		}
		
		form.setEventAttendees(updatedAttendees);
		form.setCurrentAttendeeBadgeName("");
		form.setCurrentAttendeeEmail("");
		form.setCurrentAttendeeFirstName("");
		form.setCurrentAttendeeLastName("");
		form.setCurrentAttendeeTitle("");
		form.setCurrentAttendeeInode("");
	}


	/*public void updateAttendeeList(WebEventRegistrationForm form, HttpServletRequest request, HttpServletResponse response){
		
		
		List<WebEventAttendeeForm> currentAttendees = form.getEventAttendees();
		List<WebEventAttendeeForm> updatedAttendees = new ArrayList<WebEventAttendeeForm>();
		
		java.util.Iterator attendeesIter = currentAttendees.iterator();
		
		while(attendeesIter.hasNext()){
			try {
				
				WebEventAttendeeForm attendee = (WebEventAttendeeForm) attendeesIter.next();
				WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm ();
				if(attendee.getInode().equalsIgnoreCase(form.getCurrentAttendeeInode())){
					attendee.setBadgeName(form.getCurrentAttendeeBadgeName());
					attendee.setTitle(form.getCurrentAttendeeTitle());
					attendee.setEmail(form.getCurrentAttendeeEmail());
					attendee.setFirstName(form.getCurrentAttendeeFirstName());
					attendee.setLastName(form.getCurrentAttendeeLastName());
				}
				BeanUtils.copyProperties(attendeeForm, attendee);
				updatedAttendees.add(attendeeForm);
				
				
			} catch (IllegalAccessException e) {
				Logger.error(this,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				Logger.error(this,e.getMessage(),e);
			}
			
			
		}
		
		form.setEventAttendees(updatedAttendees);
		form.setCurrentAttendeeBadgeName("");
		form.setCurrentAttendeeEmail("");
		form.setCurrentAttendeeFirstName("");
		form.setCurrentAttendeeLastName("");
		form.setCurrentAttendeeTitle("");
		form.setCurrentAttendeeInode(0);
	}
	
	public void saveRegistration(WebEventRegistrationForm form, HttpServletRequest request, HttpServletResponse response){
		
		List<WebEventAttendeeForm> currentAttendees = form.getEventAttendees();
		
		for(WebEventAttendeeForm attendee : currentAttendees){
			try {
				if (attendee.getInode() > 0) {
					WebEventAttendee dbAttendee = (WebEventAttendee) InodeFactory.getInode(attendee.getInode(), WebEventAttendee.class);
					BeanUtils.copyProperties(dbAttendee, attendee);
					InodeFactory.saveInode(dbAttendee);
				}
			} catch (IllegalAccessException e) {
				Logger.error(this,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				Logger.error(this,e.getMessage(),e);
			}
			
			
		}
		
		form.setEventAttendees(currentAttendees);
		
		form.setCurrentAttendeeBadgeName("");
		form.setCurrentAttendeeEmail("");
		form.setCurrentAttendeeFirstName("");
		form.setCurrentAttendeeLastName("");
		form.setCurrentAttendeeTitle("");
		form.setCurrentAttendeeInode(0);
	}*/
	
	
	public void getRegisterInfo(WebEventRegistrationForm form, HttpServletRequest request, HttpServletResponse response){
		
		String inode = (String) request.getParameter("inode");
		WebEventRegistration webEvent = WebEventRegistrationFactory.getWebEventRegistration(inode);
		form.setBillingAddress1(webEvent.getBillingAddress1());
		form.setBillingAddress2(webEvent.getBillingAddress2());
		form.setBillingCity(webEvent.getBillingCity());
		form.setBillingState(webEvent.getBillingState());
		form.setBillingZip(webEvent.getBillingZip());
		form.setBillingContactEmail(webEvent.getBillingContactEmail());
		form.setBillingContactName(webEvent.getBillingContactName());
		form.setBillingContactPhone(webEvent.getBillingContactPhone());
		form.setPaymentType(webEvent.getPaymentType());
		form.setCardName(webEvent.getCardName());
		form.setCardExpMonth(webEvent.getCardExpMonth());
		form.setCardExpYear(webEvent.getCardExpYear());
		form.setCardNumber(webEvent.getCardNumber());
		form.setCardType(webEvent.getCardType());
		form.setCardVerificationValue(webEvent.getCardVerificationValue());
		form.setCheckBankName(webEvent.getCheckBankName());
		form.setCheckNumber(webEvent.getCheckNumber());
		form.setEventInode(webEvent.getEventInode());
		form.setEventLocationInode(webEvent.getEventLocationInode());
		form.setTotalRegistration(webEvent.getTotalRegistration());
		form.setTotalPaid(webEvent.getTotalPaid());
		form.setUserInode(webEvent.getUserInode());
		
		form.setModified_QB(true);
		List<WebEventAttendeeForm> currentAttendees = new ArrayList<WebEventAttendeeForm>();
		List<WebEventAttendee> AttendeeList = WebEventRegistrationFactory.getEventAttendees(webEvent);
		java.util.Iterator attendeesIter = AttendeeList.iterator();
		
		while(attendeesIter.hasNext()){
			try {
				
				WebEventAttendee attendee = (WebEventAttendee) attendeesIter.next();
				WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm ();
				BeanUtils.copyProperties(attendeeForm, attendee);
				currentAttendees.add(attendeeForm);
				
			} catch (IllegalAccessException e) {
				Logger.error(this,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				Logger.error(this,e.getMessage(),e);
			}
			
			
		}
		
		form.setEventAttendees(currentAttendees);
		form.setCurrentAttendeeBadgeName("");
		form.setCurrentAttendeeEmail("");
		form.setCurrentAttendeeFirstName("");
		form.setCurrentAttendeeLastName("");
		form.setCurrentAttendeeTitle("");
		form.setCurrentAttendeeInode("");
		
	}
	
	
	
	
}

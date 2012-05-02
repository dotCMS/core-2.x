/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.event_registrations.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventAttendeeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventRegistrationFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.event_registrations.struts.ViewRegistrationsForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventAttendeeForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventRegistrationForm;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.factories.WebEventLocationFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.portlet.ActionResponseImpl;
import com.liferay.portlet.RenderRequestImpl;
import com.liferay.util.servlet.SessionMessages;



/**
 * @author Maria Ahues
 *  
 */
public class ViewRegistrationsAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			RenderResponse res) throws Exception {
		
		String cmd = req.getParameter(Constants.CMD);

		if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
			_deleteEventRegistration(form, req, res);
			cmd = com.liferay.portal.util.Constants.SEARCH;
		}
		if(cmd != null && cmd.equals("viewLocations")){
			_viewLocations(mapping, form, config, req, res);
			return mapping.findForward("portlet.ext.webevents_registration.view_registrations");
		}
		if (req.getWindowState().equals(WindowState.NORMAL)) {
			_viewTodaysRegistrations(mapping, form, config, req, res);
			_getRegistrationStatus(req);
			
			return mapping.findForward("portlet.ext.webevents_registration.view");
		} else {
			_viewRegistrations(mapping, form, config, req, res);
			_getRegistrationStatus(req);
			
			((ViewRegistrationsForm) form).setInstitute(0);
			
			return mapping.findForward("portlet.ext.webevents_registration.view_registrations");
		}
	}
	
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {
		
		String cmd = req.getParameter(Constants.CMD);
		
		if(cmd != null && cmd.equals("exportRegistration")){
			_exportRegistration(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		if(cmd != null && cmd.equals("exportGenesys")){
			_exportGenesys(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		if(cmd != null && cmd.equals("exportRegistrationToQB")){
			_exportRegistrationToQB(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		if(cmd != null && cmd.equals("exportRegistrationsFinance")){
			_exportRegistrationsFinance(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		
		if(cmd != null && cmd.equals("exportCEOLetters")){
			_exportCEOLetters(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		if(cmd != null && cmd.equals("exportAttendees")){
			_exportAttendees(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		
		if(cmd != null && cmd.equals("printBadges")){
			_printBadges(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
		if(cmd != null && cmd.equals("exportCertificates")){
			_exportCertificates(req, res,config,form);
			setForward(req,"portlet.ext.webevents_registration.view_registrations");
		}
	}
	
	
	private void _viewTodaysRegistrations(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
			PortletResponse res) throws Exception {
		
		List registrations = WebEventRegistrationFactory.getTodayWebEventRegistrations();
		req.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, registrations);
		
	}
	
	private void _getRegistrationStatus(PortletRequest req) {
		
		//statuses list
		String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("EREG_STATUSES");
		java.util.HashMap<String, String> statuses = new java.util.HashMap<String, String>();
		for (int i=0;i<statusesArray.length;i++) {
			String status = statusesArray[i];
			statuses.put(com.dotmarketing.util.Config.getStringProperty(status),com.dotmarketing.util.Config.getStringProperty(status + "_FN"));
		}
		req.setAttribute(WebKeys.WEBEVENTS_REG_STATUSES, statuses);
		
	}
	
	private void _viewLocations(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
	PortletResponse res) throws Exception{
		String isAttendeesLinkVisible = "0";
		String isWebinar = "false";
		
		HttpSession session = ((RenderRequestImpl)req).getHttpServletRequest().getSession();
		ViewRegistrationsForm vForm = (ViewRegistrationsForm) form;
		String eventInode = vForm.getEventInode();
		WebEvent event = WebEventFactory.getWebEvent(eventInode);
		
		req.setAttribute("isAttendeesLinkVisible", isAttendeesLinkVisible);
		req.setAttribute("isWebinar", isWebinar);
		
		//set the events drop down
		List events = WebEventFactory.getAllWebEvents();
		Iterator eventsIter = events.iterator();
		req.setAttribute("events", eventsIter);
		
		
		//set the locations drop down
		List locations = new ArrayList();
		if (InodeUtils.isSet(eventInode)) {
			//long eventInodeLong = Long.parseLong(eventInode);
			locations = WebEventLocationFactory.getWebEventLocationsPerEvent(eventInode,"start_date desc");
		}
		Iterator locationsIter = locations.iterator();
		List<HashMap> finalLocations = new ArrayList<HashMap>();
		while (locationsIter.hasNext()) {
			WebEventLocation location = (WebEventLocation) locationsIter.next();
			String locationStr = location.toString();
			HashMap<String,String> hs = new HashMap<String,String>();
			hs.put("optionName", locationStr);
			hs.put("optionValue", String.valueOf(location.getInode()));
			finalLocations.add(hs);
		}
		req.setAttribute("locations", finalLocations.iterator());
		
		
		//set the systems drop down
		List systems = OrganizationFactory.getAllSystems();
		Iterator systemsIter = systems.iterator();
		req.setAttribute("systems", systemsIter);
		
		//set the facilities drop down
		List facilities = new ArrayList();
		Iterator facilitiesIter = facilities.iterator();
		req.setAttribute("facilities", facilitiesIter);
		
		//get registrations here.
		List<WebEventRegistration> resultsRegistrations = new ArrayList<WebEventRegistration>();
		req.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, resultsRegistrations);
		session.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, resultsRegistrations);
	}
	
	private void _viewRegistrations(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
			PortletResponse res) throws Exception {

		String isAttendeesLinkVisible;
		String isWebinar = "false";
		String orderBy = (req.getParameter("orderBy")!=null ? req.getParameter("orderBy") : "");
		String orderDirection;
		String previousColumn;
		HttpSession session = ((RenderRequestImpl)req).getHttpServletRequest().getSession();
		
		ViewRegistrationsForm vForm = (ViewRegistrationsForm) form;
		if (UtilMethods.isSet(orderBy)){
			vForm.setOrderBy(orderBy);
			orderDirection = vForm.getSelectedDirection();
			previousColumn = vForm.getSelectedColumn();
			if (UtilMethods.isSet(previousColumn)){
				if (previousColumn.equals(orderBy)){
					if (UtilMethods.isSet(orderDirection)){
						if (orderDirection.equals("DESC"))
							orderDirection = "ASC";
						else
							orderDirection = "DESC";
					}
					else
						orderDirection = "DESC";
				}
				else{
					previousColumn = orderBy;
					orderDirection = "DESC";
				}
			}
			else{
				previousColumn = orderBy;
				orderDirection = "DESC";
			}
		}
		else{
			previousColumn = "";
			orderDirection = "";
		}
		vForm.setSelectedColumn(previousColumn);
		vForm.setSelectedDirection(orderDirection);
		vForm.setOrderBy(orderBy);
		String eventInode = vForm.getEventInode();
		String locationInode = vForm.getLocationInode();
		
		WebEvent event = WebEventFactory.getWebEvent(eventInode);
		
		//verify is the attendees link is visible or not
		if ((eventInode != null) && (locationInode != null))
			if (eventInode.equals("0") || (locationInode.equals("0")))
				isAttendeesLinkVisible = "0";
			else {
				if (event.isInstitute()) {
					isAttendeesLinkVisible = "1";
					isWebinar = "false";
				}
				else {
					isAttendeesLinkVisible = "0";
					isWebinar = "true";
				}
			}
		else
			isAttendeesLinkVisible = "0";
		
		
		req.setAttribute("isAttendeesLinkVisible", isAttendeesLinkVisible);
		req.setAttribute("isWebinar", isWebinar);
		String systemInode = vForm.getSystem();
		String firstName = vForm.getFirstName();
		String lastName = vForm.getLastName();
		String facilityInode = vForm.getFacility();
		String facilityTitle = vForm.getFacilityTitle();
		String registrationNumber = vForm.getRegistrationNumber();
		String invoiceNumber = vForm.getInvoiceNumber();
		int paymentStatus = vForm.getPaymentStatus();
		//set the events drop down
		List events = WebEventFactory.getAllWebEvents();
		Iterator eventsIter = events.iterator();
		req.setAttribute("events", eventsIter);
		
		
		//set the locations drop down
		List locations = new ArrayList();
		if (InodeUtils.isSet(eventInode)) {
			//long eventInodeLong = Long.parseLong(eventInode);
			locations = WebEventLocationFactory.getWebEventLocationsPerEvent(eventInode,"start_date desc");
		}
		Iterator locationsIter = locations.iterator();
		List<HashMap> finalLocations = new ArrayList<HashMap>();
		while (locationsIter.hasNext()) {
			WebEventLocation location = (WebEventLocation) locationsIter.next();
			String locationStr = location.toString();
			HashMap<String,String> hs = new HashMap<String,String>();
			hs.put("optionName", locationStr);
			hs.put("optionValue", String.valueOf(location.getInode()));
			finalLocations.add(hs);
		}
		req.setAttribute("locations", finalLocations.iterator());
		
		
		//set the systems drop down
		List systems = OrganizationFactory.getAllSystems();
		Iterator systemsIter = systems.iterator();
		req.setAttribute("systems", systemsIter);
		
		//set the facilities drop down
		List facilities = new ArrayList();
		if (InodeUtils.isSet(systemInode)) {
			Organization parentSystem = OrganizationFactory.getOrganization(systemInode);
			facilities = OrganizationFactory.getChildrenOrganizations(parentSystem,"title");
		}
		Iterator facilitiesIter = facilities.iterator();
		req.setAttribute("facilities", facilitiesIter);
		
		//long facilityInodeLong = (UtilMethods.isSet(facilityInode)) ? Long.parseLong(facilityInode) : 0;
		//long systemInodeLong = (UtilMethods.isSet(systemInode)) ? Long.parseLong(systemInode) : 0;
		
		//if any of the user filters are set
		if (UtilMethods.isSet(invoiceNumber) || UtilMethods.isSet(firstName) ||  UtilMethods.isSet(lastName) 
				|| InodeUtils.isSet(facilityInode) || InodeUtils.isSet(systemInode)
				|| InodeUtils.isSet(eventInode) || InodeUtils.isSet(locationInode)
				|| UtilMethods.isSet(facilityTitle) || UtilMethods.isSet(registrationNumber)|| paymentStatus > 0) {
			
			//get registrations here.
			List registrations = WebEventRegistrationFactory.getFilteredRegistrations(vForm);
			
			//filter the registrations 
			if (UtilMethods.isSet(firstName) ||  UtilMethods.isSet(lastName) || InodeUtils.isSet(facilityInode) || InodeUtils.isSet(systemInode)  || UtilMethods.isSet(facilityTitle)) {
				
				List<WebEventRegistration> resultsRegistrations = new ArrayList<WebEventRegistration>();
				List<WebEventRegistrationForm> resultsRegistrationsForm = new ArrayList<WebEventRegistrationForm>();
				Iterator registrationsIter = registrations.iterator();
				
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					//WebEventRegistrationForm registrationForm = new  WebEventRegistrationForm();
					//BeanUtils.copyProperties(registrationForm, registration);
					UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
					String userId = userProxy.getUserId();
					String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
					try {
						
						User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false); 
						
						if ((user.getFirstName().toLowerCase().startsWith(firstName.toLowerCase()) || !UtilMethods.isSet(firstName))
								&& (user.getLastName().toLowerCase().startsWith(lastName.toLowerCase()) || !UtilMethods.isSet(lastName))) {
							
							if (InodeUtils.isSet(facilityInode)) {
								//if the user selected a facility then i dont need to check the system
								Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
								if (organization.getInode().equalsIgnoreCase(facilityInode)) {
									//resultsRegistrationsForm.add(registrationForm);
									resultsRegistrations.add(registration);
								}
							}
							else {
								//if the user selected a system but not a facility i need to check the system
								if (InodeUtils.isSet(systemInode)) {
									Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
									Organization parentSystem = OrganizationFactory.getParentOrganization(organization);
									if (parentSystem.getInode().equalsIgnoreCase(systemInode)) {
										//resultsRegistrationsForm.add(registrationForm);
										resultsRegistrations.add(registration);
									}
								}
								else {
									//the user didn't select either a facility or system.
									//if the user entered a facility name
									if (UtilMethods.isSet(facilityTitle)) {
										Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
										if (organization.getTitle().toLowerCase().contains(facilityTitle.toLowerCase())) {
											//resultsRegistrationsForm.add(registrationForm);
											resultsRegistrations.add(registration);
										}
									}
									else {
										//first name and last name only
										//resultsRegistrationsForm.add(registrationForm);
										resultsRegistrations.add(registration);
									}
								}
							}
						}
					} catch (Exception e) {
						//user doesnt exist by userId
					}					
				}
				///filtered by user parameters
				req.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, resultsRegistrations);
				session.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, resultsRegistrations);
			} else {
				///all registrations filtered by event, location, start date, end date
				req.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, registrations);
				session.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, registrations);
			}
		} else {
			///today's registrations 
			List registrations = WebEventRegistrationFactory.getTodayWebEventRegistrations();
			req.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, registrations);
			session.setAttribute(WebKeys.WEBEVENTS_REG_VIEW, registrations);
			
		}
	}
	
	private List<WebEventAttendeeForm> getAttendeesByEventRegistration(WebEventRegistrationForm registrationForm){
			List<WebEventAttendeeForm> attendeesResult = new ArrayList<WebEventAttendeeForm>();
			List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registrationForm);
			Iterator attendeesIter = attendees.iterator();
			try{
			while (attendeesIter.hasNext()) {
				WebEventAttendee attendee = (WebEventAttendee) attendeesIter.next();
				WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
				BeanUtils.copyProperties(attendeeForm,attendee);
				attendeesResult.add(attendeeForm);
			}
			}
			catch(Exception e){
				attendeesResult = new ArrayList<WebEventAttendeeForm>();
			}
			return attendeesResult;
	}
	
	@SuppressWarnings("unchecked")
	private void _exportRegistration(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<HashMap> paymentTypes = getpaymentTypes();
		
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"registrations_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			
			if(registrations != null && registrations.size() > 0) {
				
				out.print("WebOrderID,Contact FirstName,Contact LastName,Company,Contact Email,Contact Phone,Payment Type,");
				out.print("Card Type,Card Number,Card ExpMonth,Card ExpYear,Card Name,Check number,Bank name (check),PO Number,Billing Name,");
				out.println("Bill Address1,Bill Address2,Bill City,Bill State,Bill Zip,Bill Country,Institute/Webinar name,Location and Date,Institute/Webinar,Total,Number of attendees");
				out.print("\r\n");
				
				Iterator registrationsIter = registrations.iterator();
				
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					try {
						
						WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
						WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
						
						if(InodeUtils.isSet(event.getInode()) &&InodeUtils.isSet(eventLocation.getInode())){
							
							
							UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
							User user = APILocator.getUserAPI().loadUserById(registrantUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
							
							String orgName = "";
							if (InodeUtils.isSet(registrantUser.getInode())) {
								Organization organization = (Organization) InodeFactory.getParentOfClass(registrantUser,Organization.class);
								if (organization.getTitle()!=null) {
									orgName = organization.getTitle().trim();
								}else{
									orgName = "No Facility";
								}
							}
							
							String paymentType = "";
							for(HashMap paymentMethod : paymentTypes) {
								if(paymentMethod.get("paymentTypeValue").equals(String.valueOf(registration.getPaymentType()))){
									paymentType = ((String) paymentMethod.get("paymentTypeName")); 
								}
							}
							
								/*Order Info*/
								out.print("\"" +registration.getInode() +"\",");
								out.print((user.getFirstName() == null ? "," : "\"" + user.getFirstName() + "\","));
								out.print((user.getLastName() == null ? "," : "\"" + user.getLastName() + "\","));
								out.print("\"" +orgName + "\",");
								out.print((user.getEmailAddress() == null ? "," : "\"" + user.getEmailAddress() + "\","));
								out.print((registration.getBillingContactPhone() == null ? "," : "\"" + registration.getBillingContactPhone() + "\","));
								
								/*Payment Info*/
								out.print((registration.getPaymentType() == 0 ? "," : "\"" + paymentType + "\","));
								out.print((registration.getCardType() == null ? "," : "\"" + registration.getCardType() + "\","));
								out.print((registration.getCardNumber() == null ? "," : "\"" + registration.getCardNumber() + "\","));
								out.print((registration.getCardExpMonth() == null ? "," : "\"" + UtilMethods.getMonthName(Integer.parseInt(registration.getCardExpMonth())) + "\","));
								out.print((registration.getCardExpYear() == null ? "," : "\"" + registration.getCardExpYear() + "\","));
								out.print((registration.getCardName() == null ? "," : "\"" + registration.getCardName() + "\","));
								out.print((registration.getCheckNumber() == null ? "," : "\"" + registration.getCheckNumber() + "\","));
								out.print((registration.getCheckBankName() == null ? "," : "\"" + registration.getCheckBankName() + "\","));
								out.print((registration.getPoNumber() == null ? "," : "\"" + registration.getPoNumber() + "\","));
								
								/*Billing Info*/
								out.print((registration.getBillingContactName() == null ? "," : "\"" + registration.getBillingContactName() + "\","));
								out.print((registration.getBillingAddress1() == null ? "," : "\"" + registration.getBillingAddress1() + "\","));
								out.print((registration.getBillingAddress2() == null ? "," : "\"" + registration.getBillingAddress2() + "\","));
								out.print((registration.getBillingCity() == null ? "," : "\"" + registration.getBillingCity() + "\","));
								out.print((registration.getBillingState() == null ? "," : "\"" + registration.getBillingState() + "\","));
								out.print((registration.getBillingZip() == null ? "," : "\"" + registration.getBillingZip() + "\","));
								out.print(",");
								
								
								/*Event Info*/					
								out.print((event.getTitle() == null ? "," : "\"" + event.getTitle() + "\","));
								String location = "";
								if (event.isInstitute()) {
									location = (eventLocation == null ? ",":"\"" +eventLocation.getHotelName().trim()+" "+ UtilMethods.dateToHTMLDate(eventLocation.getStartDate())+". "+eventLocation.getCity().trim() +"-"+ eventLocation.getShortDescription().trim()+"\",");
								}
								else {
									location = "\"" + eventLocation.toString() + "\",";
								}
								location.replaceAll("\n","");
								out.print(location);
								out.print((event.isInstitute()  ? "\"Institute\"," : "\"Webinar\","));
								
								/*Total Cost of the registration*/
								out.print(("\"$ "+registration.getTotalRegistration() + "\","));
								
								/*Number of attendees*/
								
								List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
								out.print(("\""+attendees.size() + "\""));
								out.print("\r\n");
							
						}
						
					}catch(Exception p){
						
					}
					
					
				}
			}else {
				out.print("There are no Registrations to show");
				out.print("\r\n");
			}
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			
			//out.print("0");
			//out.print("\r\n");
			out.print("There are no Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _exportGenesys(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"registrations_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			
			if(registrations != null && registrations.size() > 0) {
				
				out.print("FirstName,LastName,Email,Webinar,Date and Time");
				out.print("\r\n");
				
				Iterator registrationsIter = registrations.iterator();
				
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					try {
						
						WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
						WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
						
						if(InodeUtils.isSet(event.getInode()) && InodeUtils.isSet(eventLocation.getInode())){
							
							UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
							User user = APILocator.getUserAPI().loadUserById(registrantUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
							
							/*Order Info*/
							out.print((user.getFirstName() == null ? "," : "\"" + user.getFirstName() + "\","));
							out.print((user.getLastName() == null ? "," : "\"" + user.getLastName() + "\","));
							out.print((user.getEmailAddress() == null ? "," : "\"" + user.getEmailAddress() + "\","));
								
							/*Event Info*/					
							out.print((event.getTitle() == null ? "," : "\"" + event.getTitle() + "\","));
							
							String location = (eventLocation == null ? ",":"\"" +eventLocation.toString() +"\",");
							location.replaceAll("\n","");
							out.print(location);
								
							out.print("\r\n");
							
						}
						
					}catch(Exception p){
						
					}
					
					
				}
			}else {
				out.print("There are no Registrations to show");
				out.print("\r\n");
			}
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			
			//out.print("0");
			//out.print("\r\n");
			out.print("There are no Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}

	@SuppressWarnings("unchecked")
	private void _exportRegistrationToQB(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<HashMap> paymentTypes = getpaymentTypes();
		
		// THE SAME METHOD USED IN QUICKBOOKS.JAVA
		String condition = " registration_status in ("+Config.getStringProperty("EREG_INVOICED")+","+Config.getStringProperty("EREG_PAID")+") and (invoice_number is NULL or invoice_number = '' or modified_QB = " + DbConnectionFactory.getDBTrue() + ")";
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) WebEventRegistrationFactory.getWebEventRegistrationsByCondition(condition);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"quickbooks_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			
			if(registrations != null && registrations.size() > 0) {
				
				out.print("WebOrderID,Contact FirstName,Contact LastName,Company,Contact Email,Contact Phone,Payment Type,");
				out.print("Card Type,Card Number,Card ExpMonth,Card ExpYear,Card Name,Check number,Bank name (check),PO Number,Billing Name,");
				out.println("Bill Address1,Bill Address2,Bill City,Bill State,Bill Zip,Bill Country,Event name,Location,Total,Number of attendees");
				out.print("\r\n");
				
				Iterator registrationsIter = registrations.iterator();
				
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					try {
						
						WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
						WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
						
						if(InodeUtils.isSet(event.getInode()) && InodeUtils.isSet(eventLocation.getInode())){
							
							 
							UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
							User user = APILocator.getUserAPI().loadUserById(registrantUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
							
							String orgName = "";
							if (InodeUtils.isSet(registrantUser.getInode())) {
								Organization organization = (Organization) InodeFactory.getParentOfClass(registrantUser,Organization.class);
								if ((organization != null) && (InodeUtils.isSet(organization.getInode()))){
									if (organization.getTitle()!=null) {
										orgName = organization.getTitle().trim();
									}else{
										orgName = "";
									}
								}
								else
									orgName = user.getFirstName() + " " + user.getLastName();
							}
							
							String paymentType = "";
							for(HashMap paymentMethod : paymentTypes) {
								if(paymentMethod.get("paymentTypeValue").equals(String.valueOf(registration.getPaymentType()))){
									paymentType = ((String) paymentMethod.get("paymentTypeName")); 
								}
							}
							
								/*Order Info*/
								out.print("\"" +registration.getInode() +"\",");
								out.print((user.getFirstName() == null ? "," : "\"" + user.getFirstName() + "\","));
								out.print((user.getLastName() == null ? "," : "\"" + user.getLastName() + "\","));
								out.print("\"" +orgName + "\",");
								out.print((user.getEmailAddress() == null ? "," : "\"" + user.getEmailAddress() + "\","));
								out.print((registration.getBillingContactPhone() == null ? "," : "\"" + registration.getBillingContactPhone() + "\","));
								
								/*Payment Info*/
								out.print((registration.getPaymentType() == 0 ? "," : "\"" + paymentType + "\","));
								out.print((registration.getCardType() == null ? "," : "\"" + registration.getCardType() + "\","));
								out.print((registration.getCardNumber() == null ? "," : "\"" + registration.getCardNumber() + "\","));
								out.print((registration.getCardExpMonth() == null ? "," : "\"" + UtilMethods.getMonthName(Integer.parseInt(registration.getCardExpMonth())) + "\","));
								out.print((registration.getCardExpYear() == null ? "," : "\"" + registration.getCardExpYear() + "\","));
								out.print((registration.getCardName() == null ? "," : "\"" + registration.getCardName() + "\","));
								out.print((registration.getCheckNumber() == null ? "," : "\"" + registration.getCheckNumber() + "\","));
								out.print((registration.getCheckBankName() == null ? "," : "\"" + registration.getCheckBankName() + "\","));
								out.print((registration.getPoNumber() == null ? "," : "\"" + registration.getPoNumber() + "\","));
								
								/*Billing Info*/
								out.print((registration.getBillingContactName() == null ? "," : "\"" + registration.getBillingContactName() + "\","));
								out.print((registration.getBillingAddress1() == null ? "," : "\"" + registration.getBillingAddress1() + "\","));
								out.print((registration.getBillingAddress2() == null ? "," : "\"" + registration.getBillingAddress2() + "\","));
								out.print((registration.getBillingCity() == null ? "," : "\"" + registration.getBillingCity() + "\","));
								out.print((registration.getBillingState() == null ? "," : "\"" + registration.getBillingState() + "\","));
								out.print((registration.getBillingZip() == null ? "," : "\"" + registration.getBillingZip() + "\","));
								out.print((registration.getBillingCountry() == null ? "," : "\"" + registration.getBillingCountry() + "\","));
								
								
								/*Event Info*/					
								out.print((event.getTitle() == null ? "," : "\"" + event.getTitle() + "\","));
								
								String location = (eventLocation == null ? ",":"\"" +eventLocation.getHotelName().trim()+" "+ UtilMethods.dateToHTMLDate(eventLocation.getStartDate())+". "+eventLocation.getCity().trim() +"-"+ eventLocation.getShortDescription().trim()+"\",");
								location.replaceAll("\n","");
								out.print(location);
								
								/*Total Cost of the registration*/
								out.print(("\"$ "+registration.getTotalRegistration() + "\","));
								
								/*Number of attendees*/
								
								List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
								out.print(("\""+attendees.size() + "\""));
								out.print("\r\n");
							
						}
						
					}catch(Exception p){
						
					}
				}
			}else {
				out.print("There are no Registrations to show");
				out.print("\r\n");
			}
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			//out.print("0");
			//out.print("\r\n");
			out.print("There are no Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void _exportRegistrationsFinance(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		List<HashMap> paymentTypes = getpaymentTypes();
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"registrationsFinance_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		ServletOutputStream out = httpRes.getOutputStream();
		StringBuffer sb = new StringBuffer();
		float total = 0;
		int numAttendees = 0;
		float regTotal = 0;
		try {
			
			if(registrations != null && registrations.size() > 0) {
				//Sets the file head
				sb.append("WebOrderID,Company,Payment Type,Total,Number of attendees\n\n");
				Iterator registrationsIter = registrations.iterator();
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
					WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
					
					if(InodeUtils.isSet(event.getInode()) && InodeUtils.isSet(eventLocation.getInode())){		
						UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
						//gets the organization's name
						String orgName = "";
						if (InodeUtils.isSet(registrantUser.getInode())) {
							Organization organization = (Organization) InodeFactory.getParentOfClass(registrantUser,Organization.class);
							if (organization.getTitle()!=null) {
								orgName = organization.getTitle().trim();
							}else{
								orgName = "No Facility";
							}
						}
						//gets the payment type
						String paymentType = "";
						for(HashMap paymentMethod : paymentTypes) {
							if(paymentMethod.get("paymentTypeValue").equals(String.valueOf(registration.getPaymentType()))){
								paymentType = ((String) paymentMethod.get("paymentTypeName")); 
							}
						}
						sb.append("\"" +registration.getInode() +"\",");
						sb.append("\"" +orgName + "\",");
						sb.append((registration.getPaymentType() == 0 ? "," : "\"" + paymentType + "\","));
						//total += registration.getTotalRegistration();
						regTotal = registration.getTotalDue() + registration.getTotalPaid(); 
						total += regTotal;
						//sb.append(("\"$ "+ registration.getTotalRegistration() + "\","));
						sb.append(("\"$ "+ regTotal + "\","));
						List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
						numAttendees += attendees.size();
						sb.append(("\""+attendees.size() + "\"\r\n"));
					}
				}
				sb.append(",,,\"" + total +" \","+ numAttendees);
			}else {
				sb.append("There are no Information to show");
				sb.append("\r\n");
			}
			out.print(sb.toString());
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			out.print("There are no Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}
	@SuppressWarnings("unchecked")
	public List<HashMap> getpaymentTypes () {
		List<HashMap> paymentTypes = new ArrayList<HashMap>();
		String[] paymentTypesArray  = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
		for (int i=0;i<paymentTypesArray.length;i++) {
			String pType = paymentTypesArray[i];
			HashMap<String, String> hs = new HashMap<String, String>();
			hs.put("paymentTypeName",Config.getStringProperty(pType + "_FN"));
			hs.put("paymentTypeValue",Config.getStringProperty(pType));
			paymentTypes.add(hs);
		}
		
		return paymentTypes;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void _exportAttendees(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		boolean firstRegistration = true;
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"attendees_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".xls\"");
		Hashtable statesTable = new Hashtable();
		Hashtable organizationsTable = new Hashtable();
		int numStates = 0;
		int numOrganizations = 0;
		String eventStartDate = null;
		String eventEndDate = null;
		String eventTitle = null;
		String facility = null;
		String facilityCity = null;
		String facilityState = null;
		String eventLoc = null;
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer sbHeader = new StringBuffer();
			sbHeader.append("<head>");
			sbHeader.append("<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">");
			sbHeader.append("<meta name=ProgId content=Excel.Sheet>");
			sbHeader.append(_setStyleAttendessListHTML());
			sbHeader.append("</head>");
			if(registrations != null && registrations.size() > 0) {
				Iterator registrationsIter = registrations.iterator();
		
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					try {
						List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
						WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
						WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
						UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
						Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy,Organization.class);
						if (InodeUtils.isSet(organization.getInode())){
							if (organizationsTable.get(organization.getInode()) == null){
								++numOrganizations;
								organizationsTable.put(organization.getInode(),organization.getTitle());
							}
							if (statesTable.get(organization.getState()) == null){
								++numStates;
								statesTable.put(organization.getState(),"");
							}
						}
						if (firstRegistration) {
							eventStartDate = UtilMethods.dateToHTMLDate(eventLocation.getStartDate());
							eventEndDate = UtilMethods.dateToHTMLDate(eventLocation.getEndDate());
							eventTitle = event.getTitle();
							eventLoc = eventLocation.getState() + ", " + eventLocation.getCity();
							firstRegistration = false;
						}
						for(WebEventAttendee attendee : attendees){
							facility = organization.getTitle();
							facilityCity = organization.getCity();
							facilityState = organization.getState();
							if (facility == null)
								facility = "No Facility";
							if (facilityCity == null)
								facilityCity = "--";
							if (facilityState == null)
								facilityState = "--";
							sb.append("<tr height=16 style='mso-height-source:userset;height:12.0pt'>");
							sb.append("<td class=xl32 width=90>" + attendee.getFirstName() + "</td>");
							sb.append("<td class=xl32 width=90>" + attendee.getLastName() + "</td>");
							sb.append("<td class=xl32 width=90>" + attendee.getTitle() + "</td>");
							sb.append("<td class=xl32 width=90>" + facility + "</td>");
							sb.append("<td class=xl32 width=90>" + facilityCity + "</td>");
							sb.append("<td class=xl32 width=90>" + facilityState + "</td>");
							sb.append("<td class=xl32 width=90>" +attendee.getEmail()+"</td>");
							sb.append("</tr>");
						}
					}catch(Exception p){
						
					}
				}
				sbHeader.append("<body link=blue vlink=purple class=xl31>");
				sbHeader.append("<table width=1000 border=0 cellpadding=0 cellspacing=0>");
				sbHeader.append("<tr class=xl29>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24>" + eventTitle + "</td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("</tr>");
				sbHeader.append("<tr class=xl29>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl30>Attendee List</td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("</tr>");
				sbHeader.append("<tr class=xl29>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl30></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl30>" + eventStartDate + "-" + eventEndDate + " " + eventLoc + "</td>");
				sbHeader.append("<td class=xl30></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("</tr>");
				sbHeader.append("<tr class=xl29>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl30></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl30>" + String.valueOf(numOrganizations) + " Organizations~" + String.valueOf(numStates)+ " States</td>");
				sbHeader.append("<td class=xl30></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("<td class=xl24></td>");
				sbHeader.append("</tr>");
				sbHeader.append("<tr class=xl27 style='mso-height-source:userset;height:15.0pt'>");
				sbHeader.append("<td class=xl27 style='height:15.0pt'>First Name</td>");
				sbHeader.append("<td class=xl27>Last Name</td>");
				sbHeader.append("<td class=xl27>Title</td>");
				sbHeader.append("<td class=xl27>Facility</td>");
				sbHeader.append("<td class=xl27>City</td>");
				sbHeader.append("<td class=xl27>St</td>");
				sbHeader.append("<td class=xl27>Email</td>");
				sbHeader.append("</tr>");
			}else {
				sbHeader.append("<tr class=xl29 height=26 style='mso-height-source:userset;height:20.1pt'>");
				sbHeader.append("<td height=26 class=xl24 width=90 style='height:20.1pt;width:68pt'></td>");
				sbHeader.append("<td class=xl24 width=90 style='width:68pt'></td>");
				sbHeader.append("<td class=xl24 width=185 style='width:139pt'></td>");
				sbHeader.append("<td class=xl30 width=199 style='width:149pt'>There are no Attendees Registrations to show</td>");
				sbHeader.append("<td class=xl24 width=67 style='width:50pt'></td>");
				sbHeader.append("<td class=xl24 width=26 style='width:20pt'></td>");
				sbHeader.append("<td class=xl24 width=230 style='width:173pt'></td>");
				sbHeader.append("</tr>");
			}
			sb.append("</table>");
			sb.append("</body>");
			out.print(sbHeader.toString());
			out.print(sb.toString());
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			out.print("\r\n");
			out.print("There are no Attendees Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _exportCertificates(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"certificates_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");

		String facility = null;
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("First_Name,");
			sb.append("Last_Name,");
			sb.append("Company");
			sb.append("\r\n");

			if(registrations != null && registrations.size() > 0) {
				Iterator registrationsIter = registrations.iterator();
		
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					try {
						List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
						UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
						Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy,Organization.class);
						for(WebEventAttendee attendee : attendees){
							facility = organization.getTitle();

							if (facility == null)
								facility = "No Facility";
							
							sb.append("\"" + attendee.getFirstName() + "\",");
							sb.append("\"" + attendee.getLastName() + "\",");
							sb.append("\"" + facility + "\"");
							sb.append("\r\n");
						}
					}catch(Exception p){
						
					}
				}
			}else {
				sb.append("There are no Attendees Registrations to show");
			}
			out.print(sb.toString());
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			out.print("\r\n");
			out.print("An Exception Ocurred! - There are no Attendees Registrations to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}

	@SuppressWarnings("unchecked")
	private void _exportCEOLetters(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		StringBuffer sb = new StringBuffer();
		StringBuffer sbHeader = new StringBuffer();
		StringBuffer sbAttendees = null;
		String ceoFirstName;
		String ceoLastName;
		String ceoFullName;
		String facilityTitle;
		String facilityCity;
		String facilityState;
		String facilityZip;
		String facilityStreet;
		int posCeoName = 0;
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		httpRes.setContentType("application/octet-stream\n");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"ceoLetters_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".xls\"\n");
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			

			if(registrations != null && registrations.size() > 0) {
				//Sets the header
				sbHeader.append("<head>\n");
				sbHeader.append("<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n");
				sbHeader.append("<meta name=ProgId content=Excel.Sheet>\n");
				sbHeader.append(_setStyleCEOLettersHTML());
				sbHeader.append("</head>\n");
				sbHeader.append("<body>\n");
				sbHeader.append("<table border=0 cellpadding=0 cellspacing=0 width=914>\n");
				
				sbHeader.append("	<tr class=xl2>\n");
				sbHeader.append("		<td width=64>First Name</td>\n");
				sbHeader.append("		<td width=64>Last Name</td>\n");
				sbHeader.append("		<td width=212>Attendees</td>\n");
				sbHeader.append("		<td width=286>Company</td>\n");
				sbHeader.append("		<td width=64>Address</td>\n");
				sbHeader.append(" 		<td width=64>City</td>\n");
				sbHeader.append(" 		<td width=47>State</td>\n");
				sbHeader.append("		<td width=49>Zip</td>\n");
				sbHeader.append("		<td width=64>Location</td>\n");
				sbHeader.append("	</tr>\n");

				Iterator registrationsIter = registrations.iterator();
				while (registrationsIter.hasNext()) {
					
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					WebEventLocation eventLocation = WebEventLocationFactory.getWebEventLocation(registration.getEventLocationInode());
					UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
					Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy,Organization.class);
					List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
					sbAttendees = new StringBuffer();
					
					for(WebEventAttendee attendee : attendees){
						if (sbAttendees.length() == 0)
							sbAttendees.append(attendee.getFirstName() + " " + attendee.getLastName());
						else
							sbAttendees.append(", " + attendee.getFirstName() + " " + attendee.getLastName());
					}
					
					ceoFullName = registration.getCeoName();
					if ((ceoFullName != null) && !ceoFullName.equals("")){
						ceoFullName = ceoFullName.trim();
						posCeoName = ceoFullName.indexOf(" ");
						if (posCeoName > -1){
							ceoFirstName = ceoFullName.substring(0,posCeoName);
							ceoLastName = ceoFullName.substring(posCeoName + 1);
						}
						else{
							ceoFirstName = ceoFullName;
							ceoLastName = "";
						}
					}
					else{
						ceoFirstName = "";
						ceoLastName = "";
					}
					if (organization != null){
						facilityTitle = organization.getTitle();
						if (facilityTitle == null)
							facilityTitle = "No Facility";
						facilityStreet = organization.getStreet1();
						if (facilityStreet == null)
							facilityStreet = "--";
						facilityCity = organization.getCity();
						if (facilityCity == null)
							facilityCity = "--";
						facilityState = organization.getState();
						if (facilityState == null)
							facilityState = "--";
						facilityZip = organization.getZip();
						if (facilityZip == null)
							facilityZip = "--";
					}
					else{
						facilityTitle = "No Facility";
						facilityStreet = "--";
						facilityCity = "--";
						facilityState = "--";
						facilityZip = "--";
					}	
					sbHeader.append("	<tr class=xl1>\n");
					sbHeader.append("		<td width=64>" + ceoFirstName + "</td>\n");
					sbHeader.append("		<td width=64>" + ceoLastName + "</td>\n");
					sbHeader.append("		<td width=212>" +  sbAttendees.toString() + "</td>\n");
					sbHeader.append("		<td width=286>" + facilityTitle + "</td>\n");
					sbHeader.append("		<td width=64>" + facilityStreet + "</td>\n");
					sbHeader.append("		<td width=64>" + facilityCity + "</td>\n");
					sbHeader.append("		<td width=47>" + facilityState + "</td>\n");
					sbHeader.append("		<td align=right width=49>" + facilityZip + "</td>\n");
					sbHeader.append("		<td width=64>" + eventLocation.getCity() + "</td>\n");
					sbHeader.append("	</tr>\n");
				}
				sb.append("</table>");
				sb.append("</body>");
				sb.append("</html>");
			}else {
				sbHeader.append("\n");
				sbHeader.append("There are no Registrations to show");
				sbHeader.append("\n");
			}
			
			out.print(sbHeader.toString());
			out.print(sb.toString());
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			out.print("\r\n");
			out.print("There are no information to show.");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();	
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _printBadges(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		String facilityCity;
		String facilityState;
		String facilityTitle;
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<WebEventRegistration> registrations = (List<WebEventRegistration>) session.getAttribute(WebKeys.WEBEVENTS_REG_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"badges_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".xls\"");
		
		ServletOutputStream out = httpRes.getOutputStream();
		StringBuffer sb = new StringBuffer();
		
		//Sets the header
		sb.append("<head>\n");
		sb.append("<meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\n");
		sb.append("<meta name=ProgId content=Excel.Sheet>\n");
		sb.append(_setStyleprintBadgesHTML());
		sb.append("</head>\n");
		sb.append("<body>\n");
		try {
			if(registrations != null && registrations.size() > 0) {
				sb.append("<table width=469>\n");
				sb.append(" <tr>\n");
				sb.append(" 	<td class=xl1 width=78>First_Name</td>\n");
				sb.append(" 	<td class=xl1 width=71>Last_Name</td>\n");
				sb.append("  	<td class=xl1 width=64>Badge</td>\n");
				sb.append(" 	<td class=xl1 width=64>Title</td>\n");
				sb.append("  	<td class=xl1 width=64>Facility</td>\n");
				sb.append("  	<td class=xl1 width=64>City</td>\n");
				sb.append("  	<td class=xl1 width=64>State</td>\n");
				sb.append(" </tr>\n");
				Iterator registrationsIter = registrations.iterator();
				//boolean existsPrintBadges = false;
				
				while (registrationsIter.hasNext()) {
					WebEventRegistration registration = (WebEventRegistration) registrationsIter.next();
					if (registration.getRegistrationStatus() == 6)
						continue;
					//if(!registration.isBadgePrinted()){
						UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
						Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy,Organization.class);
						if (organization != null){
							facilityCity = organization.getCity();
							facilityState = organization.getState();
							facilityTitle = organization.getTitle();
						}
						else{
							facilityCity = "";
							facilityState = "";
							facilityTitle = "No Facility";
						}
						//existsPrintBadges = true;
						List<WebEventAttendee> attendees = WebEventRegistrationFactory.getEventAttendees(registration);
						
						registration.setBadgePrinted(true);
						InodeFactory.saveInode(registration);
						
						for(WebEventAttendee attendee : attendees){
							sb.append("<tr>\n");
							sb.append("<td class=xl2 width=78>" + attendee.getFirstName() + "</td>\n");
							sb.append("<td class=xl2 width=71>"+attendee.getLastName()+"</td>\n");
							sb.append("<td class=xl2 width=64>" +attendee.getBadgeName()+"</td>\n");
							sb.append("<td class=xl2 width=64>" +attendee.getTitle() + "</td>\n");
							sb.append("<td class=xl2 width=64>" + facilityTitle + "</td>\n");
							sb.append("<td class=xl2 width=64>" + facilityCity + "</td>\n");
							sb.append("<td class=xl2 width=64>" + facilityState + "</td>\n");
							sb.append("</tr>\n");
						}	
					//}
				}
				/*
				if(!existsPrintBadges){
					out.print("There are no Badges to print");
					out.print("\r\n");
				}*/
			}else {
				out.print("0");
				out.print("\r\n");
				out.print("There are no Badges to print");
				out.print("\r\n");
			}
			out.print(sb.toString());
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			
			out.print("0");
			out.print("\r\n");
			out.print("There are no Badges to print");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}
	}
	
	private String _setStyleAttendessListHTML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<style>");
		sb.append("<!--table");
		sb.append("@page");
		sb.append("	{margin:.98in .79in .98in .79in;");
		sb.append("	mso-header-margin:.5in;");
		sb.append("	mso-footer-margin:.5in;");
		sb.append("	mso-page-orientation:landscape;}");
		sb.append("tr");
		sb.append("	{mso-height-source:auto;}");
		sb.append("col");
		sb.append("	{mso-width-source:auto;}");
		sb.append("br");
		sb.append("	{mso-data-placement:same-cell;}");
		sb.append(".style0");
		sb.append("	{mso-number-format:General;");
		sb.append("	text-align:general;");
		sb.append("	vertical-align:bottom;");
		sb.append("	white-space:nowrap;");
		sb.append("	mso-rotate:0;");
		sb.append("	mso-background-source:auto;");
		sb.append("	mso-pattern:auto;");
		sb.append("	color:windowtext;");
		sb.append("	font-size:10.0pt;");
		sb.append("	font-weight:400;");
		sb.append("	font-style:normal;");
		sb.append("	text-decoration:none;");
		sb.append("	font-family:Arial;");
		sb.append("	mso-generic-font-family:auto;");
		sb.append("	mso-font-charset:0;");
		sb.append("	border:none;");
		sb.append("	mso-protection:locked visible;");
		sb.append("	mso-style-name:Normal;");
		sb.append("	mso-style-id:0;}");
		sb.append("td");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	padding-top:1px;");
		sb.append("	padding-right:1px;");
		sb.append("	padding-left:1px;");
		sb.append("	mso-ignore:padding;");
		sb.append("	color:windowtext;");
		sb.append("	font-size:10.0pt;");
		sb.append("	font-weight:400;");
		sb.append("	font-style:normal;");
		sb.append("	text-decoration:none;");
		sb.append("	font-family:Arial;");
		sb.append("	mso-generic-font-family:auto;");
		sb.append("	mso-font-charset:0;");
		sb.append("	mso-number-format:General;");
		sb.append("	text-align:general;");
		sb.append("	vertical-align:bottom;");
		sb.append("	border:none;");
		sb.append("	mso-background-source:auto;");
		sb.append(" mso-pattern:auto;");
		sb.append(" mso-protection:locked visible;");
		sb.append(" white-space:nowrap;");
		sb.append("	mso-rotate:0;}");
		sb.append(".xl24");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:16.0pt;");
		sb.append("	font-weight:700;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;");
		sb.append("	text-align:center;}");
		sb.append(".xl25");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:10.0pt;");
		sb.append("	font-weight:700;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;");
		sb.append("	text-align:right;}");
		sb.append(".xl26");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:10.0pt;");
		sb.append("	font-weight:700;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;}");
		sb.append(".xl27");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:12.0pt;");
		sb.append("	text-decoration:underline;");
		sb.append("	text-underline-style:single;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;");
		sb.append("	white-space:normal;}");
		sb.append(".xl30");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:12.0pt;");
		sb.append("	font-weight:700;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;");
		sb.append("	text-align:center;}");
		sb.append(".xl32");
		sb.append("	{mso-style-parent:style0;");
		sb.append("	font-size:8.0pt;");
		sb.append("	font-family:Arial, sans-serif;");
		sb.append("	mso-font-charset:0;");
		sb.append("	border:.5pt solid windowtext;");
		sb.append("	background:white;");
		sb.append("	mso-pattern:auto none;");
		sb.append("	white-space:normal;}");
		sb.append("-->");
		sb.append("</style>");
		return sb.toString();
	}
	private String _setStyleCEOLettersHTML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<style>\n");
		sb.append("	<!--\n");
		sb.append("		.xl1\n");
		sb.append("			{padding-top:1px;\n");
		sb.append("			padding-right:1px;\n");
		sb.append("			padding-left:1px;\n");
		sb.append("			mso-ignore:padding;\n");
		sb.append("			color:windowtext;\n");
		sb.append("			font-size:7.5pt;\n");
		sb.append("			font-weight:400;\n");
		sb.append("			font-style:normal;\n");
		sb.append("			text-decoration:none;\n");
		sb.append("			font-family:Verdana, sans-serif;\n");
		sb.append("			mso-font-charset:0;\n");
		sb.append("			mso-number-format:General;\n");
		sb.append("			text-align:general;\n");
		sb.append("			vertical-align:bottom;\n");
		sb.append("			border:.5pt solid windowtext;\n");
		sb.append("			mso-background-source:auto;\n");
		sb.append("			mso-pattern:auto;\n");
		sb.append("			white-space:normal;}\n");
		sb.append("		.xl2\n");
		sb.append("			{padding-top:1px;\n");
		sb.append("			padding-right:1px;\n");
		sb.append("			padding-left:1px;\n");
		sb.append("			mso-ignore:padding;\n");
		sb.append("			color:windowtext;\n");
		sb.append("			font-size:7.5pt;\n");
		sb.append("			font-weight:700;\n");
		sb.append("			font-style:normal;\n");
		sb.append("			text-decoration:none;\n");
		sb.append("			font-family:Verdana, sans-serif;\n");
		sb.append("			mso-font-charset:0;\n");
		sb.append("			mso-number-format:General;\n");
		sb.append("			text-align:general;\n");
		sb.append("			vertical-align:bottom;\n");
		sb.append("			border:.5pt solid windowtext;\n");
		sb.append("			mso-background-source:auto;\n");
		sb.append("			mso-pattern:auto;\n");
		sb.append("			white-space:normal;}\n");
		sb.append("	-->\n");
		sb.append("</style>\n");
		return sb.toString();
	}	
	private String _setStyleprintBadgesHTML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<style>\n");
		sb.append("<!--\n");
		sb.append(".xl1\n");
		sb.append("{padding-top:1px;\n");
		sb.append("padding-right:1px;\n");
		sb.append("padding-left:1px;\n");
		sb.append("mso-ignore:padding;\n");
		sb.append("color:black;\n");
		sb.append("font-size:10.0pt;\n");
		sb.append("font-weight:400;\n");
		sb.append("font-style:normal;\n");
		sb.append("text-decoration:none;\n");
		sb.append("font-family:Arial;\n");
		sb.append("mso-generic-font-family:auto;\n");
		sb.append("mso-font-charset:0;\n");
		sb.append("mso-number-format:General;\n");
		sb.append("text-align:center;\n");
		sb.append("vertical-align:bottom;\n");
		sb.append("border:.5pt solid black;\n");
		sb.append("background:silver;\n");
		sb.append("mso-pattern:black none;\n");
		sb.append("white-space:nowrap;}\n");
		sb.append(".xl2\n");
		sb.append("{padding-top:1px;\n");
		sb.append("padding-right:1px;\n");
		sb.append("padding-left:1px;\n");
		sb.append("mso-ignore:padding;\n");
		sb.append("color:black;\n");
		sb.append("font-size:10.0pt;\n");
		sb.append("font-weight:400;\n");
		sb.append("font-style:normal;\n");
		sb.append("text-decoration:none;\n");
		sb.append("font-family:Arial;\n");
		sb.append("mso-generic-font-family:auto;\n");
		sb.append("mso-font-charset:0;\n");
		sb.append("mso-number-format:General;\n");
		sb.append("text-align:general;\n");
		sb.append("vertical-align:bottom;\n");
		sb.append("border:.5pt solid silver;\n");
		sb.append("mso-background-source:auto;\n");
		sb.append("mso-pattern:auto;\n");
		sb.append("white-space:normal;}\n");
		sb.append("-->\n");
		sb.append("</style>\n");
		return sb.toString();
	}	
	private void _deleteEventRegistration(ActionForm form, RenderRequest req, RenderResponse res)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		WebEventRegistration e = null;
		if(!InodeUtils.isSet(inode)){
			e = WebEventRegistrationFactory.newInstance();	
		} else {
			e = WebEventRegistrationFactory.getWebEventRegistration(inode);
		}
        
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
	
}

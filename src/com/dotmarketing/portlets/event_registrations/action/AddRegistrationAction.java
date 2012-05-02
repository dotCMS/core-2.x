package com.dotmarketing.portlets.event_registrations.action;

import java.net.URLDecoder;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessor;
import com.dotmarketing.cms.creditcard.model.linkpoint.LinkPointCreditCardProcessorException;
import com.dotmarketing.cms.creditcard.model.linkpoint.LinkPointCreditCardProcessorResponse;
import com.dotmarketing.cms.factories.PublicAddressFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.event_registrations.factories.WebEventRegistrationFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.event_registrations.struts.WebEventAttendeeForm;
import com.dotmarketing.portlets.event_registrations.struts.WebEventRegistrationForm;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.factories.WebEventLocationFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author Maria Ahues
 *
 */
public class AddRegistrationAction extends DotPortletAction {

	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {

		String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;
		String referer = req.getParameter("referer");
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		//Logger.info("************Parameter: "+cmd+"***********");
		if ((referer!=null) && (referer.length()!=0)) {
			referer = URLDecoder.decode(referer,"UTF-8");
		}

		DotHibernate.startTransaction();
		User user = _getUser(req);

		try {
			_retrieveEventRegistration(req, res, config, form, user);

		} catch (ActionException ae) {
			_handleException(ae, req);
		}

		/*
		 * Step 4
		 */
		if ((cmd != null) && cmd.equals("step4")) {
			try {
				String forward = _addEventRegistrationStep4(req, res, config, form, mapping, user);
				if (!forward.startsWith("step")) {
					setForward(req,forward);
				}
				else {
					cmd = forward;
				}
			} catch (ActionException ae) {
				_handleException(ae, req);
			}
		}
		/*
		 * Step 1
		 */
		if ((cmd != null) && cmd.equals("step1")) {
			try {
				_addEventRegistrationStep1(req, res, config, form, user);
				setForward(req,"portlet.ext.webevents_registration.add_registration.step1");
			} catch (ActionException ae) {
				_handleException(ae, req);
			}
		}
		/*
		 * Step 1
		 */
		if ((cmd != null) && cmd.equals("add_attendee")) {
			try {
				_addAttendee(req, res, config, form, mapping, user);
				setForward(req,"portlet.ext.webevents_registration.add_registration.step2");
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
				setForward(req,"portlet.ext.webevents_registration.add_registration.step2");
			} catch (ActionException ae) {
				_handleException(ae, req);
			}
		}
		/*
		 * Step 2
		 */
		if ((cmd != null) && (cmd.equals("step2") || cmd.equals("edit_attendee"))) {
			try {

				WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;
				if(!InodeUtils.isSet(registrationForm.getEventLocationInode())){
					SessionMessages.add(req, "error", "error.webregistration.location");
					_addEventRegistrationStep1(req, res, config, form, user);
					setForward(req,"portlet.ext.webevents_registration.add_registration.step1");
				} else {
					if (cmd.equals("edit_attendee")) {
						_addEventRegistrationStep2(req, res, config, form, user);
					}
					WebEvent event = WebEventFactory.getWebEvent(registrationForm.getEventInode());
					if (event.isInstitute()) {
						setForward(req,"portlet.ext.webevents_registration.add_registration.step2");
					}
					else {
						cmd = "register_user";
					}
				}
			} catch (ActionException ae) {
				_handleException(ae, req);
			}
		}
		if ((cmd != null) && cmd.equals("register_user")) {
			WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;
			WebEvent event = WebEventFactory.getWebEvent(registrationForm.getEventInode());

			//the user hasnt been selected
			if (!UtilMethods.isSet(registrationForm.getUserId())) {

				List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();
				if (currentAttendees.size()>0 || !event.isInstitute()) {

					//wraps request to get session object


					java.util.Map<String,String[]> params2 = new java.util.HashMap<String,String[]>();
					params2.put("struts_action",new String[] {"/ext/event_registration/add_registration"});
					params2.put("cmd",new String[] { "step3" });
					referer = com.dotmarketing.util.PortletURLUtil.getActionURL(httpReq,WindowState.MAXIMIZED.toString(),params2);
					httpReq.getSession().setAttribute(WebKeys.WEBEVENTS_REG_USER, referer);



					java.util.Map<String,String[]> params = new java.util.HashMap<String,String[]>();

					params.put("struts_action",new String[] {"/ext/usermanager/edit_usermanager"});
					params.put("cmd",new String[] {"load_register_user"});

					params.put("referer",new String[] { referer });
					params.put("without_password",new String[] {"1"});
					String af = com.dotmarketing.util.PortletURLUtil.getActionURL(httpReq,WindowState.MAXIMIZED.toString(),params);

					_sendToReferral(req, res, af);
					return;
				}
				else {
					SessionMessages.add(req, "error", "message.webevent_registration.attendees_required");
				}
				setForward(req,"portlet.ext.webevents_registration.add_registration.step2");
			}
			else {
				//the user is set go to step3
				cmd = "step3";
			}
		}
		/*
		 * Step 3
		 */
		if ((cmd != null) && cmd.equals("step3")) {
			try {

				httpReq.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_USER);
				_addEventRegistrationStep3(req, res, config, form, user);
				setForward(req,"portlet.ext.webevents_registration.add_registration.step3");
			} catch (ActionException ae) {
				_handleException(ae, req);
			}
		}

		DotHibernate.commitTransaction();

	}


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveEventRegistration(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "0";
		WebEventRegistration e = null;
		if(inode.equals("0")){
			e = WebEventRegistrationFactory.newInstance();	
		} else {
			e = WebEventRegistrationFactory.getWebEventRegistration(inode);
		}
		req.setAttribute(WebKeys.WEBEVENTS_REG_FORM, form);
		req.setAttribute(WebKeys.WEBEVENTS_REG_EDIT, e);
	}
	private void _addAttendee(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, ActionMapping mapping, User user)
	throws Exception {

		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setFirstName(registrationForm.getCurrentAttendeeFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastName());
		attendeeForm.setTitle(registrationForm.getCurrentAttendeeTitle());
		attendeeForm.setBadgeName(registrationForm.getCurrentAttendeeBadgeName());
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		attendeeForm.setInode(registrationForm.getCurrentAttendeeInode());

		//validates the form bean
		if (Validator.validate(req, form, mapping)) {

			WebEventLocation loc = WebEventLocationFactory.getWebEventLocation(registrationForm.getEventLocationInode());

			//need to update this for backend
			if (httpReq.getSession().getAttribute("isPartner") != null 
					&& httpReq.getSession().getAttribute("isPartner").equals("true")){
				Organization organization = (Organization)httpReq.getSession().getAttribute("userOrganization");
				if (organization.getInstitute_price() > 0)
					attendeeForm.setRegistrationPrice(organization.getInstitute_price());
				else
					attendeeForm.setRegistrationPrice(loc.getPartnerPrice());
			}
			else
				attendeeForm.setRegistrationPrice(loc.getNonPartnerPrice());


			List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();

			if (req.getParameter("isnew") != null
					&& req.getParameter("isnew").equals("true")) {
				if (currentAttendees.contains(attendeeForm)) {
					SessionMessages.add(httpReq, "error", "error.attendee.already.registered");
					req.setAttribute("WebEventAttendeeForm",attendeeForm);
				} else {
					currentAttendees.add(attendeeForm);
					req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
				}
			} else {
				WebEventAttendeeForm lastAttendeeForm = new WebEventAttendeeForm();
				lastAttendeeForm.setEmail(registrationForm.getCurrentAttendeeLastEmail());
				lastAttendeeForm.setFirstName(registrationForm.getCurrentAttendeeLastFirstName());
				lastAttendeeForm.setLastName(registrationForm.getCurrentAttendeeLastLastName());

				if (currentAttendees!=null && currentAttendees.contains(lastAttendeeForm)) {
					currentAttendees.remove(lastAttendeeForm);
					currentAttendees.add(attendeeForm);
				} else {
					currentAttendees.add(attendeeForm);
				}
				req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
			}
		} else {
			//sends the bean back to the page
			req.setAttribute("WebEventAttendeeForm",attendeeForm);
		}
	}
	private void _deleteAttendee(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		attendeeForm.setFirstName(registrationForm.getCurrentAttendeeLastFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastLastName());

		List currentAttendees = registrationForm.getEventAttendees();
		currentAttendees.remove(attendeeForm);

		req.setAttribute("WebEventAttendeeForm",new WebEventAttendeeForm());
		SessionMessages.add(req, "message", "message.webevent_registration.delete_attendee");

	}

	@SuppressWarnings("unchecked")
	private void _addEventRegistrationStep1(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		WebEventRegistrationForm vForm = (WebEventRegistrationForm) form;
		String eventInode = vForm.getEventInode();
		boolean showPastEvents = false;
		String showPastEventsParam = req.getParameter("showPastEventsCheck");
		if (showPastEventsParam != null)
			if (showPastEventsParam.equals("true"))
				showPastEvents = true;

		//set the events drop down
		List events;
		if (showPastEvents)
			events = WebEventFactory.getAllWebEvents();
		else
			events = WebEventFactory.getUpcomingWebEvents();


		Iterator eventsIter = events.iterator();
		req.setAttribute("events", eventsIter);

		//set the locations drop down
		List locations = new ArrayList();

		if (InodeUtils.isSet(eventInode)) {
			if (showPastEvents)
				locations = WebEventLocationFactory.getWebEventLocationsPerEvent(eventInode,"start_date");
			else
				locations = WebEventLocationFactory.getUpcomingWebEventLocationsPerEvent(eventInode,"start_date");
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


	}

	private Address retrieveAddress(String userID) throws Exception {
		String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();

		Address address = null;
		if (UtilMethods.isSet(userID)) {
			List addresses = PublicAddressFactory.getAddressesByUserId(userID);
			if (addresses.size() == 0) {
				address = PublicAddressFactory.getInstance();
				address.setCompanyId(companyId);
				address.setUserId(userID);
			} else {
				address = (Address) addresses.get(0);
			}
		} else {
			address = PublicAddressFactory.getInstance();
			address.setCompanyId(companyId);
			address.setUserId(userID);
		}
		return address;
	}

	private void _addEventRegistrationStep2(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());

		List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();
		for(WebEventAttendeeForm att : currentAttendees){
			if(att.getEmail().equals(attendeeForm.getEmail())){
				req.setAttribute("WebEventAttendeeForm",att);
			}
		}		
	}

	private void _addEventRegistrationStep3(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;

		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

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

		//cardTypes list
		List<HashMap> cardTypes = new ArrayList<HashMap>();
		String[] cardTypesArray  = Config.getStringArrayProperty("EREG_CREDIT_CARD_TYPES");

		for (int i=0;i<cardTypesArray.length;i++) {
			String cType = cardTypesArray[i];
			HashMap<String, String> hs = new HashMap<String, String>();
			hs.put("cardTypeName",cType);
			hs.put("cardTypeValue",cType);
			cardTypes.add(hs);
		}

		req.setAttribute("cardTypes",cardTypes.iterator());

		//cardExpYears list
		List<HashMap> cardExpYears = new ArrayList<HashMap>();
		Calendar today = new GregorianCalendar();
		int thisYear = today.get(Calendar.YEAR);
		for (int i=0;i<=10;i++)  {
			HashMap<String, String> hs = new HashMap<String, String>();
			hs.put("yearName",String.valueOf(thisYear));
			hs.put("yearValue",String.valueOf(thisYear++));
			cardExpYears.add(hs);
		}
		req.setAttribute("cardExpYears",cardExpYears.iterator());

		//cardExpYears list
		List<HashMap> cardExpMonths = new ArrayList<HashMap>();
		int thisMonth = 0;
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();

		for (int i=0;i<12;i++)  {
			HashMap<String, String> hs = new HashMap<String, String>();
			hs.put("monthName",months[thisMonth]);
			hs.put("monthValue",String.valueOf(i+1));
			thisMonth++;
			cardExpMonths.add(hs);
		}
		req.setAttribute("cardExpMonths",cardExpMonths.iterator());

		//get address for the user selected
		String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
		String userId = registrationForm.getUserId();
		if(!UtilMethods.isSet(userId) && UtilMethods.isSet(httpReq.getSession().getAttribute(WebKeys.WEBEVENTS_REG_USERID))){
			userId = (String) httpReq.getSession().getAttribute(WebKeys.WEBEVENTS_REG_USERID);
		}
		httpReq.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_USERID);

		user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
		UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
		registrationForm.setUserInode(userProxy.getInode());
		Address address = retrieveAddress(userId);
		registrationForm.setBillingAddress1(address.getStreet1());
		registrationForm.setBillingAddress2(address.getStreet2());
		registrationForm.setBillingCity(address.getCity());
		registrationForm.setBillingContactEmail(user.getEmailAddress());
		registrationForm.setBillingContactName(user.getFullName());
		registrationForm.setBillingContactPhone(address.getPhone());
		registrationForm.setBillingState(address.getState());
		registrationForm.setBillingZip(address.getZip());

		WebEvent event = WebEventFactory.getWebEvent(registrationForm.getEventInode());
		if (!event.isInstitute()) {

			WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
			attendeeForm.setEmail(user.getEmailAddress());
			attendeeForm.setFirstName(user.getFirstName());
			attendeeForm.setLastName(user.getLastName());
			attendeeForm.setTitle("");
			attendeeForm.setBadgeName("");

			WebEventLocation loc = WebEventLocationFactory.getWebEventLocation(registrationForm.getEventLocationInode());

			//need to update this for backend
			if (httpReq.getSession().getAttribute("isPartner") != null && httpReq.getSession().getAttribute("isPartner").equals("true")){

				Organization organization = (Organization)httpReq.getSession().getAttribute("userOrganization");
				if (organization.getInstitute_price() > 0)
					attendeeForm.setRegistrationPrice(organization.getInstitute_price());
				else
					attendeeForm.setRegistrationPrice(loc.getPartnerPrice());
			}
			else
				attendeeForm.setRegistrationPrice(loc.getNonPartnerPrice());


			List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();
			if (!currentAttendees.contains(attendeeForm)) {
				currentAttendees.add(attendeeForm);
			}
		}
		Organization org = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
		boolean isPartner = false;
		if (InodeUtils.isSet(org.getInode())){
			isPartner = ((org.getPartnerKey() != null && !org.getPartnerKey().equals("") )? true : false);
		}
		WebEventLocation loc = WebEventLocationFactory.getWebEventLocation(registrationForm.getEventLocationInode());
		List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();
		for (int i=0;i < currentAttendees.size();i++){
			WebEventAttendeeForm attendeeForm = (WebEventAttendeeForm)currentAttendees.get(i);
			if (isPartner){
				if (loc.isDefaultContractPartnerPrice()){
					attendeeForm.setRegistrationPrice(org.getInstitute_price());
				}
				else{
					attendeeForm.setRegistrationPrice(loc.getPartnerPrice());
				}
			}
			else
				attendeeForm.setRegistrationPrice(loc.getNonPartnerPrice());
		}
		// Token Generation to avoid transactions duplication
		generateToken(httpReq);
		saveToken(httpReq);
	}

	private String _addEventRegistrationStep4(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, ActionMapping mapping, User user)
	throws Exception {

		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
		try { 
			if (!isTokenValid(httpReq)) {

				// Duplicated transaction?
				// waiting 2 min max for the transaction processing
				for (int i = 0; i < 120; i++) {
					if (SessionMessages.size(req) > 0) {
						// Token Generation to avoid transactions duplication
						generateToken(httpReq);
						saveToken(httpReq);
						return "portlet.ext.webevents_registration.add_registration.step3";
					} else {
						WebEventRegistration registration = (WebEventRegistration) httpReq.getSession().getAttribute(WebKeys.WEBEVENTS_REG_BEAN);
						if (registration != null) {
							SessionMessages.add(req, "error", "error.web_event.registration.already.processed2");
							if (registration.getPaymentType() == 1)
								return "portlet.ext.webevents_registration.add_registration.creditCardConfirmation";
							else
								return "portlet.ext.webevents_registration.add_registration.checkConfirmation";
						}
						Thread.sleep(1000);
					}
				}

				// No transaction processing found?
				SessionMessages.add(req, "error", "error.web_event.registration.already.processed");
				return "step1";
			}
			resetToken(httpReq);
			httpReq.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_BEAN);

			WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) form;

			if (!Validator.validate(req, registrationForm, mapping)) {
				generateToken(httpReq);
				saveToken(httpReq);
				return "step3";
			}

			// Starting transactional processing
			DotHibernate.startTransaction();

			// Saving registration
			WebEventRegistration registration = new WebEventRegistration();
			BeanUtils.copyProperties(registration, registrationForm);
			registration.setDatePosted(new Date());
			registration.setLastModDate(new Date());
			registration.setCardNumber(UtilMethods.obfuscateCreditCard(registrationForm.getCardNumber()));
			registration.setCardVerificationValue("");
			InodeFactory.saveInode(registration);

			// Saving attendees
			int registrationAmount = 0;
			List<WebEventAttendeeForm> attendeesForms = registrationForm.getEventAttendees();
			for (WebEventAttendeeForm attendeeForm : attendeesForms) {
				WebEventAttendee attendee = new WebEventAttendee();
				BeanUtils.copyProperties(attendee, attendeeForm);
				attendee.setEventRegistrationInode(registration.getInode());
				InodeFactory.saveInode(attendee);
				registrationAmount += attendee.getRegistrationPrice();
			}

			if (registrationForm.getPaymentType() == 1) {

				try {
					// updating amounts
					registration.setTotalDue(0);
					registration.setTotalPaid(registrationAmount);
					registration.setTotalRegistration(registrationAmount);
					registration.setRegistrationStatus(Config.getIntProperty("EREG_PAID"));
					InodeFactory.saveInode(registration);

					//retrieving user company
					String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
					String userId = registrationForm.getUserId();
					user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
					UserProxy up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false);
					Organization organization = (Organization) InodeFactory.getParentOfClass(up, Organization.class);
					String organizationName = "Doesn't belong to an Organization";
					if (InodeUtils.isSet(organization.getInode())) {
						organizationName = organization.getTitle();
					}


					// Place the credit card order, send the order to the credit
					// card third party gateway
					/*CreditCardProcessorResponse ccResponse = com.dotmarketing.factories.creditcard.LinkPointCreditCardProcessor.processCreditCardOrderInstitutes(registration.getInode(), 
							null, true, req.getRemoteUser(), null, null, 
							null, (float)registration.getTotalPaid(), registrationForm.getCardNumber(),
							Integer.parseInt(registration.getCardExpMonth()), Integer.parseInt(registration.getCardExpYear()),
							registrationForm.getCardVerificationValue(), registration.getCardName(),
							organizationName, registration.getBillingAddress1(), registration.getBillingAddress2(),
							registration.getBillingCity(), registration.getBillingState(),
							registration.getBillingZip(), Config.getStringProperty("US_COUNTRY_CODE"),
							registration.getBillingContactPhone(), null, registration.getBillingContactEmail(),
							Config.getStringProperty("WEB_EVENT_REGISTRATION_COMMENTS"), "");*/

					Date expirationDate = new Date();
					GregorianCalendar gc = new GregorianCalendar();
					gc.set(Calendar.YEAR,Integer.parseInt(registration.getCardExpYear()));
					gc.set(Calendar.MONTH,Integer.parseInt(registration.getCardExpMonth()));						
					gc.set(Calendar.DATE,gc.getActualMaximum(Calendar.DATE));
					expirationDate = gc.getTime();

					//LinkPointCreditCardProcessor lpccp = new LinkPointCreditCardProcessor();
					CreditCardProcessor lpccp = CreditCardProcessor.getInstance();
					lpccp.setOrderId(registration.getInode());
					lpccp.setTaxExempt(true);
					lpccp.setClientIPAddress(req.getRemoteUser());
					lpccp.setDiscount(0);
					lpccp.setTax(0);
					lpccp.setShipping(0);
					lpccp.setAmount(registration.getTotalPaid());
					lpccp.setCreditCardNumber(registration.getCardNumber());
					lpccp.setCreditCardExpirationDate(expirationDate);
					lpccp.setCreditCardCVV(registration.getCardVerificationValue());
					lpccp.setBillingFirstName(registration.getBillingContactName());
					lpccp.setBillingStreet(registration.getBillingAddress1());
					lpccp.setBillingStreet2(registration.getBillingAddress2());
					lpccp.setBillingCity(registration.getBillingCity());
					lpccp.setBillingState(registration.getBillingState());
					lpccp.setBillingZip(registration.getBillingZip());
					lpccp.setBillingCountry(Config.getStringProperty("US_COUNTRY_CODE"));
					lpccp.setBillingPhone(registration.getBillingContactPhone());
					lpccp.setBillingEmailAdress(registration.getBillingContactEmail());
					lpccp.setOrderComments(Config.getStringProperty("WEB_EVENT_REGISTRATION_COMMENTS"));
					LinkPointCreditCardProcessorResponse ccResponse = (LinkPointCreditCardProcessorResponse) lpccp.process();

					if (!ccResponse.orderApproved()) {
						DotHibernate.rollbackTransaction();
						ActionMessages ae = new ActionMessages();
						ae.add(Globals.ERROR_KEY, new ActionMessage(
								"error.cc_processing.card.denied", ccResponse.getError()));
						saveErrors(httpReq, ae);
						generateToken(httpReq);
						saveToken(httpReq);
						return "step3";
					}


				} catch (LinkPointCreditCardProcessorException e) {
					Logger.warn(this,"Credit card processor exception placing a credit card order: " + e.getMessage(), e);
					DotHibernate.rollbackTransaction();
					switch (e.getCode()) {
					case LinkPointCreditCardProcessorException.COMMUNICATION_ERROR:
						SessionMessages.add(req, "error", "error.cc_processing.communication.error");
						break;
					case LinkPointCreditCardProcessorException.DATA_MISSING:
						SessionMessages.add(req, "error", "error.cc_processing.invalid.card.data");
						break;
					default:
						SessionMessages.add(req, "error", "error.cc_processing.unknown");
					}
					generateToken(httpReq);
					saveToken(httpReq);
					return "step3";
				} catch (Exception e) {
					Logger.error(this,"Unknown Error placing a credit card order: "+ e.getMessage(), e);
					DotHibernate.rollbackTransaction();
					SessionMessages.add(req, "error", "error.cc_processing.unknown");
					generateToken(httpReq);
					saveToken(httpReq);
					return "step3";
				}

			} else {
				// updating amounts
				registration.setTotalDue(registrationAmount);
				registration.setTotalPaid(0);
				registration.setTotalRegistration(registrationAmount);
				registration.setRegistrationStatus(Config.getIntProperty("EREG_WAITING"));
				InodeFactory.saveInode(registration);
			}

			// Commiting
			DotHibernate.commitTransaction();

			// To be used in the confirmation page
			httpReq.getSession().setAttribute(WebKeys.WEBEVENTS_REG_BEAN, registration);

			//cleaning the form
			registrationForm.resetAllForm();
			httpReq.getSession().removeAttribute("WebEventRegistrationForm");

			// Send confirmation email
			try {
				sendRegistrationReceipt(mapping, form, httpReq);
			} catch (Exception e) {
				Logger.warn(this,"An error ocurred triying to send the confirmation email.",e);
			}
			WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
			if (event.isInstitute()) {
				if (registration.getPaymentType() == 1)
					return "portlet.ext.webevents_registration.add_registration.creditCardConfirmation";
				else
					return "portlet.ext.webevents_registration.add_registration.checkConfirmation";
			}
			else {
				if (registration.getPaymentType() == 1)
					return "portlet.ext.webevents_registration.add_registration.webinarConfirmation";
				else
					return "portlet.ext.webevents_registration.add_registration.webinarCheckConfirmation";

			}

		} catch (Exception e) { // General exceptions catch
			Logger.error(this, "Unknown Error placing an order: "+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			SessionMessages.add(req, "error", "error.cc_processing.unknown");
			generateToken(httpReq);
			saveToken(httpReq);
			return "step3";
		}
	}
	public static boolean sendRegistrationReceipt(ActionMapping mapping,ActionForm lf, HttpServletRequest request) {

		WebEventRegistration registration = (WebEventRegistration) request.getSession().getAttribute(WebKeys.WEBEVENTS_REG_BEAN);

		UserProxy proxy = null;
		User user;
		try {
			proxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
			user = APILocator.getUserAPI().loadUserById(proxy.getUserId(), APILocator.getUserAPI().getSystemUser(), false);
		} catch (Exception e1) {
			Logger.error(AddRegistrationAction.class,e1.getMessage());
			return false;
		}

		String path = "";

		WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
		if (event.isInstitute()) {
			if (registration.getPaymentType() == 1)
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.creditCardConfirmationEmail").getPath();
			else
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.checkConfirmationEmail").getPath();
		} else {
			if (registration.getPaymentType() == 1)
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.webinarConfirmationEmail").getPath();
			else
				path = mapping.findForward("portlet.ext.webevents_registration.add_registration.webinarCheckConfirmationEmail").getPath();
		}

		try {
			Host currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(request);
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

			return true;
		} catch (Exception e) {
			Logger.error(AddRegistrationAction.class,e.getMessage());
			return false;
		}

	}

}


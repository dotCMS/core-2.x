package com.dotmarketing.portlets.event_registrations.cms.action;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessor;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessorResponse;
import com.dotmarketing.cms.creditcard.model.authorize.AuthorizeCreditCardProcessorException;
import com.dotmarketing.cms.factories.PublicAddressFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventAttendeeFactory;
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

/**
 * @author David
 * 
 */
public class WebEventRegistrationAction extends DispatchAction {

	/**
	 * Beginning the Event Registration process This method forwards to the user
	 * registration pages to check the user info
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession();

		WebEventRegistrationForm form = (WebEventRegistrationForm) lf;
		String eventLocationInode = form.getEventLocationInode(); 
		String eventInode = form.getEventInode();
		if (!InodeUtils.isSet(eventLocationInode)) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		lf = new WebEventRegistrationForm();
		((WebEventRegistrationForm) lf).setEventLocationInode(eventLocationInode);
		((WebEventRegistrationForm) lf).setEventInode(eventInode);
		session.setAttribute("webEventRegistrationForm",lf);

		// Removing session attributes from past registration
		request.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_BEAN);
		request.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_ERRORS);

		String referrer = request.getRequestURI();
		String refererForgotPassword = referrer + "?" + request.getQueryString();
		refererForgotPassword = URLEncoder.encode(refererForgotPassword,"UTF-8");
		request.setAttribute("from", "webEventRegistration");
		request.setAttribute("refererForgotPassword",refererForgotPassword);

		// Forwarding to login/registration page
		if (session.getAttribute(WebKeys.CMS_USER) != null) {
			User user = (User) session.getAttribute(WebKeys.CMS_USER);
			UserProxy proxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			request.setAttribute("userProxyInode", proxy.getInode());
			request.setAttribute("referrer", referrer + "?dispatch=toStep1");
		}
		else {
			request.setAttribute("referrer", referrer + "?eventInode=" + eventInode + "&eventLocationInode=" + eventLocationInode);
		}

		ActionMessages messages = new ActionMessages();
		messages.add(Globals.MESSAGES_KEY, new ActionMessage("error.login.webevent"));
		saveMessages(request, messages);
		return mapping.findForward("registrantInfoPage");
	}

	/**
	 * This action goes to the add registrants page
	 */
	public ActionForward toStep1(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession sess = request.getSession();
		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;
		
		WebEvent event = WebEventFactory.getWebEvent(registrationForm.getEventInode());
		User user = (User) sess.getAttribute(WebKeys.CMS_USER);

		if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		registrationForm.setUserInode(com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false).getInode());
		
		registrationForm.resetCurrentAttendee();
		registrationForm.setDatePosted(new Date());
		registrationForm.setLastModDate(new Date());

		
		if (event.isInstitute()) {
			//code for institutes
			
			// Token Generation to avoid transactions duplication
			generateToken(request);
			saveToken(request);

			return mapping.findForward("step1");
		}
		else {

			//code for webinars
			WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
			attendeeForm.setFirstName(user.getFirstName());
			attendeeForm.setLastName(user.getLastName());
			attendeeForm.setTitle("");
			attendeeForm.setBadgeName("");
			attendeeForm.setEmail(user.getEmailAddress());

			WebEventLocation loc = WebEventLocationFactory.getWebEventLocation(registrationForm.getEventLocationInode());
			
			if (request.getSession().getAttribute("isPartner") != null
					&& request.getSession().getAttribute("isPartner")
							.equals("true")){
				if (loc.isDefaultContractPartnerPrice()){
					Organization organization = (Organization)request.getSession().getAttribute("userOrganization");
					if (InodeUtils.isSet(organization.getInode()))
						if (organization.getInstitute_price() > 0)
							attendeeForm.setRegistrationPrice(organization.getInstitute_price());
						else
							attendeeForm.setRegistrationPrice(loc.getPartnerPrice());
				}
				else
					attendeeForm.setRegistrationPrice(loc.getPartnerPrice());	
			}
			else
				attendeeForm.setRegistrationPrice(loc.getNonPartnerPrice());
			List<WebEventAttendeeForm> currentAttendees = registrationForm.getEventAttendees();
			if (!currentAttendees.contains(attendeeForm)) {
				currentAttendees.add(attendeeForm);
			}
			// Token Generation to avoid transactions duplication
			generateToken(request);
			saveToken(request);
			return toStep2(mapping, lf,request, response);
		}
	}

	/**
	 * Add/Save a registrant info
	 */
	public ActionForward saveAttendee(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;

		if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		if (!Validator.validate(request, registrationForm, mapping)) {
			return mapping.findForward("step1");
		}

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setFirstName(registrationForm
				.getCurrentAttendeeFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastName());
		attendeeForm.setTitle(registrationForm.getCurrentAttendeeTitle());
		attendeeForm.setBadgeName(registrationForm
				.getCurrentAttendeeBadgeName());
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		registrationForm.resetCurrentAttendee();

		WebEventLocation loc = WebEventLocationFactory
				.getWebEventLocation(registrationForm.getEventLocationInode());
		if (request.getSession().getAttribute("isPartner") != null
				&& request.getSession().getAttribute("isPartner")
						.equals("true")){
			if (loc.isDefaultContractPartnerPrice()){
				Organization organization = (Organization)request.getSession().getAttribute("userOrganization");
				if (InodeUtils.isSet(organization.getInode()))
					if (organization.getInstitute_price() > 0)
						attendeeForm.setRegistrationPrice(organization.getInstitute_price());
					else
						attendeeForm.setRegistrationPrice(loc.getPartnerPrice());
			}
			else
				attendeeForm.setRegistrationPrice(loc.getPartnerPrice());	
		}
		else
			attendeeForm.setRegistrationPrice(loc.getNonPartnerPrice());


		List<WebEventAttendeeForm> currentAttendees = registrationForm
				.getEventAttendees();

		if (request.getParameter("isnew") != null
				&& request.getParameter("isnew").equals("true")) {

			if (currentAttendees.contains(attendeeForm)) {
				ActionErrors errors = new ActionErrors();
				errors.add("attendees", new ActionMessage(
						"error.attendee.already.registered"));
				saveMessages(request, errors);
			} else {
				currentAttendees.add(attendeeForm);
			}
		} else {
			WebEventAttendeeForm lastAttendeeForm = new WebEventAttendeeForm();
			lastAttendeeForm.setEmail(registrationForm.getCurrentAttendeeLastEmail());
			lastAttendeeForm.setFirstName(registrationForm.getCurrentAttendeeLastFirstName());
			lastAttendeeForm.setLastName(registrationForm.getCurrentAttendeeLastLastName());
			
			if (currentAttendees.contains(lastAttendeeForm)) {
				currentAttendees.remove(lastAttendeeForm);
				currentAttendees.add(attendeeForm);
			} else {
				currentAttendees.add(attendeeForm);
			}
		}

		return mapping.findForward("step1");
	}

	/**
	 * Delete a registrant
	 */
	public ActionForward deleteAttendee(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;

		if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		attendeeForm.setFirstName(registrationForm.getCurrentAttendeeFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastName());

		List currentAttendees = registrationForm.getEventAttendees();
		currentAttendees.remove(attendeeForm);
		registrationForm.resetCurrentAttendee();

		return mapping.findForward("step1");
	}

	/**
	 * Loads the data to edit a registrant already added
	 */
	public ActionForward editAttendee(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;

		if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		WebEventAttendeeForm attendeeForm = new WebEventAttendeeForm();
		attendeeForm.setEmail(registrationForm.getCurrentAttendeeEmail());
		attendeeForm.setFirstName(registrationForm.getCurrentAttendeeFirstName());
		attendeeForm.setLastName(registrationForm.getCurrentAttendeeLastName());

		List<WebEventAttendeeForm> currentAttendees = registrationForm
				.getEventAttendees();
		if (currentAttendees.indexOf(attendeeForm) >= 0) {
			attendeeForm = currentAttendees.get(currentAttendees
					.indexOf(attendeeForm));
			registrationForm.setCurrentAttendeeFirstName(attendeeForm
					.getFirstName());
			registrationForm.setCurrentAttendeeLastName(attendeeForm
					.getLastName());
			registrationForm.setCurrentAttendeeTitle(attendeeForm.getTitle());
			registrationForm.setCurrentAttendeeBadgeName(attendeeForm
					.getBadgeName());
			registrationForm.setCurrentAttendeeEmail(attendeeForm.getEmail());
		}

		return mapping.findForward("step1");
	}

	/**
	 * Forwards to the checkout info page
	 */
	public ActionForward toStep2(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;

		if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
			ActionForward forward = mapping.findForward("upcommingEvents");
			return forward;
		}

		HttpSession sess = request.getSession();

		User user = (User) sess.getAttribute(WebKeys.CMS_USER);

		List addresses = PublicAddressFactory.getAddressesByUserId(user
				.getUserId());

		if (addresses.size() > 0) {
			Address mailingAddress = (Address) addresses.get(0);
			// Prepopulation of the billing address info based on the user
			// address info
			if (!UtilMethods.isSet(registrationForm.getBillingAddress1()))
				registrationForm
						.setBillingAddress1(mailingAddress.getStreet1());
			if (!UtilMethods.isSet(registrationForm.getBillingAddress2()))
				registrationForm
						.setBillingAddress2(mailingAddress.getStreet2());
			if (!UtilMethods.isSet(registrationForm.getBillingCity()))
				registrationForm.setBillingCity(mailingAddress.getCity());
			if (!UtilMethods.isSet(registrationForm.getBillingContactEmail()))
				registrationForm.setBillingContactEmail(user.getEmailAddress());
			if (!UtilMethods.isSet(registrationForm.getBillingContactName()))
				registrationForm.setBillingContactName(user.getFullName());
			if (!UtilMethods.isSet(registrationForm.getBillingContactPhone()))
				registrationForm.setBillingContactPhone(mailingAddress
						.getPhone());
			if (!UtilMethods.isSet(registrationForm.getBillingState()))
				registrationForm.setBillingState(mailingAddress.getState());
			if (!UtilMethods.isSet(registrationForm.getBillingZip()))
				registrationForm.setBillingZip(mailingAddress.getZip());
			if (!UtilMethods.isSet(registrationForm.getBillingCountry()))
				registrationForm.setBillingCountry(mailingAddress.getCountry());
		}
		
		//save the registration
		//Starting transactional processing
		DotHibernate.startTransaction();

		String ceoName = (String)request.getSession().getAttribute("ceoName");
		String howDidYouHear = String.valueOf ((Long)request.getSession().getAttribute("howDidYouHear"));
		
		if (ceoName == null)
			ceoName = "";
		if (howDidYouHear == null)
			howDidYouHear = "0";
		registrationForm.setCeoName(ceoName);
		registrationForm.setHowDidYouHear(howDidYouHear);
		WebEventRegistration registration;
		if (InodeUtils.isSet(registrationForm.getInode())){
			registration = WebEventRegistrationFactory.getWebEventRegistration(registrationForm.getInode());
		}
		else{
			registration = new WebEventRegistration();
		}
		ArrayList events = new ArrayList();
		events.add(registration);
		List<WebEventAttendee> attendees = WebEventAttendeeFactory.getWebEventAttendeesByEventRegistration(events);
		for (WebEventAttendee attendee : attendees) {
			InodeFactory.deleteInode(attendee);
		}
		registrationForm.setTotalDue(0);
		registrationForm.setTotalPaid(0);
		registrationForm.setTotalRegistration(0);
		
		
		BeanUtils.copyProperties(registration, registrationForm);
		
		registration.setTotalPaid(0);
		registration.setRegistrationStatus(Config.getIntProperty("EREG_INCOMPLETE"));
		registrationForm.setRegistrationStatus(Config.getIntProperty("EREG_INCOMPLETE"));
		InodeFactory.saveInode(registration);
		registrationForm.setInode(registration.getInode());
		request.setAttribute("registrationInode",String.valueOf(registration.getInode()));
		// Saving attendees
		
		float registrationAmount = 0;
		List<WebEventAttendeeForm> attendeesForms = registrationForm.getEventAttendees();
		for (WebEventAttendeeForm attendeeForm : attendeesForms) {
			WebEventAttendee attendee = new WebEventAttendee();
			BeanUtils.copyProperties(attendee, attendeeForm);
			attendee.setEventRegistrationInode(registrationForm.getInode());
			InodeFactory.saveInode(attendee);
			registrationAmount += attendee.getRegistrationPrice();
		}
		registration.setTotalRegistration(registrationAmount);
		registration.setTotalDue(registrationAmount);
		InodeFactory.saveInode(registration);

		//Commiting
		DotHibernate.commitTransaction();
		//------------------------------------------------------------------
		
		return mapping.findForward("step2");
	}

	/**
	 * Place the registration order and forwards to the confirmation page
	 */
	public ActionForward toStep3(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try { // A big try catch to support unknown errors

			WebEventRegistrationForm registrationForm = (WebEventRegistrationForm) lf;

			if (!InodeUtils.isSet(registrationForm.getEventLocationInode())) {
				ActionForward forward = mapping.findForward("upcommingEvents");
				return forward;
			}

			if (!isTokenValid(request)) {

				// Duplicated transaction?
				// waiting 2 min max for the transaction processing

				for (int i = 0; i < 120; i++) {
					ActionMessages messages = (ActionMessages) request.getSession().getAttribute(WebKeys.WEBEVENTS_REG_ERRORS);
					if (messages != null && messages.size() > 0) {
						saveMessages(request, messages);
						request.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_ERRORS); //
						// Token Generation to avoid transactions duplication
						generateToken(request);
						saveToken(request);
						return mapping.findForward("step2");
					} else {
						WebEventRegistration registration = (WebEventRegistration) request.getSession().getAttribute(WebKeys.WEBEVENTS_REG_BEAN);
						if (registration != null) {
							ActionMessages ae = new ActionMessages();
							
							WebEvent event = WebEventFactory.getWebEvent(registrationForm.getEventInode());
							
							ae.add(Globals.ERROR_KEY,new ActionMessage("error.web_event.registration.already.processed2"));
							saveErrors(request, ae);
							if (event.isInstitute()) {
								if (registration.getPaymentType() == 1)
									return mapping.findForward("creditCardConfirmation");
								else
									return mapping.findForward("checkConfirmation");
							}
							else {
								if (registration.getPaymentType() == 1)
									return mapping.findForward("webinarCCConfirmation");
								else
									return mapping.findForward("webinarCheckConfirmation");
							}
						}
						Thread.sleep(1000);
					}
				}

				// No transaction processing found?
				ActionMessages ae = new ActionMessages();
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.web_event.registration.already.processed"));
				saveErrors(request, ae);
				ActionForward forward = mapping.findForward("/dotCMS/myAccount");
				return forward;

			}
			resetToken(request);
			request.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_ERRORS);
			request.getSession().removeAttribute(WebKeys.WEBEVENTS_REG_BEAN);

			if (!Validator.validate(request, registrationForm, mapping)) {
				ActionMessages ae = getErrors(request);
				request.getSession().setAttribute(WebKeys.WEBEVENTS_REG_ERRORS, ae);
				generateToken(request);
				saveToken(request);
				return mapping.findForward("step2");
			}

			// Starting transactional processing
			DotHibernate.startTransaction();

			WebEventRegistration registration = null;
			if(!InodeUtils.isSet(registrationForm.getInode())){
				registration = WebEventRegistrationFactory.newInstance();	
			} else {
				registration = WebEventRegistrationFactory.getWebEventRegistration(registrationForm.getInode());
			}
			BeanUtils.copyProperties(registration, registrationForm);
			//saving state in the registration
			if (registration.getBillingState()!=null && registration.getBillingState().equals("otherCountry")) {
				registration.setBillingState(registrationForm.getBillingStateOtherCountryText());
			}
			
			registration.setCardNumber(UtilMethods.obfuscateCreditCard(registrationForm.getCardNumber()));
			registration.setCardVerificationValue(registrationForm.getCardVerificationValue());
			
			InodeFactory.saveInode(registration);

			// Getting registration Total
			// WE DONT SAVE HERE AGAIN WE SAVED BEFORE... THIS WAS CREATING DUPLICATE ATTENDEES
			float registrationAmount = 0;
			List<WebEventAttendeeForm> attendeesForms = registrationForm.getEventAttendees();
			for (WebEventAttendeeForm attendeeForm : attendeesForms) {
				registrationAmount += attendeeForm.getRegistrationPrice();
			}

			if (registrationForm.getPaymentType() == 1) {

				try {

					// updating amounts
					registration.setTotalDue(0);
					registration.setTotalPaid(registrationAmount);
					registration.setTotalRegistration(registrationAmount);
					registration.setRegistrationStatus(Config.getIntProperty("EREG_PAID"));
					InodeFactory.saveInode(registration);

					// Place the credit card order, send the order to the credit
					// card third party gateway

					CreditCardProcessorResponse accr = creditCardPayment(registrationForm,request,response);
					String returnCode = accr.getCode();
					
					if(!returnCode.equals(CreditCardProcessorResponse.APPROVED)) {
						DotHibernate.rollbackTransaction();

						StringTokenizer strTok = new StringTokenizer(accr.getMessage(), "&");
						String message = strTok.nextToken();
						message = strTok.nextToken();
						
						ActionMessages ae = new ActionMessages();
						ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.card.denied", message.substring(8)));
						saveErrors(request, ae);
						generateToken(request);
						saveToken(request);
						ActionForward af = mapping.findForward("step2");
						return af;
					}

				} catch (AuthorizeCreditCardProcessorException e) {
					Logger.warn(this,
							"Credit card processor exception placing a credit card order: "
									+ e.getMessage(), e);
					DotHibernate.rollbackTransaction();
					ActionMessages ae = new ActionMessages();
					
					ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.communication.error"));					
					
					saveErrors(request, ae);
					request.getSession().setAttribute(
							WebKeys.WEBEVENTS_REG_ERRORS, ae);
					generateToken(request);
					saveToken(request);
					return mapping.findForward("step2");
				} catch (Exception e) {
					Logger.error(this,
							"Unknown Error placing a credit card order: "
									+ e.getMessage(), e);
					DotHibernate.rollbackTransaction();
					ActionMessages ae = new ActionMessages();
					ae.add(Globals.ERROR_KEY, new ActionMessage(
							"error.cc_processing.unknown"));
					saveErrors(request, ae);
					request.getSession().setAttribute(
							WebKeys.WEBEVENTS_REG_ERRORS, ae);
					generateToken(request);
					saveToken(request);
					return mapping.findForward("step2");
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
			request.removeAttribute("registrationInode");
			// To be used in the confirmation page
			request.getSession().setAttribute(
					WebKeys.WEBEVENTS_REG_BEAN, registration);

			registrationForm.resetAllForm();
			request.getSession().removeAttribute("ceoName");
			request.getSession().removeAttribute("howDidYouHear");
			// Send confirmation email
			try {
				sendRegistrationReceipt(mapping, lf, request, response);
			} catch (Exception e) {
				Logger.warn(this,"An error ocurred triying to send the confirmation email.",e);
			}

			// To the confirmation page
			WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());
			if (event.isInstitute()) {
				if (registration.getPaymentType() == 1)
					return mapping.findForward("creditCardConfirmation");
				else
					return mapping.findForward("checkConfirmation");
			}
			else {
				if (registration.getPaymentType() == 1)
					return mapping.findForward("webinarCCConfirmation");
				else
					return mapping.findForward("webinarCheckConfirmation");
			}


		} catch (Exception e) { // General exceptions catch
			Logger.error(this, "Unknown Error placing an order: "
					+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			ActionMessages ae = new ActionMessages();
			ae.add(Globals.ERROR_KEY, new ActionMessage(
					"error.cc_processing.unknown"));
			saveErrors(request, ae);
			request.getSession().setAttribute(
					WebKeys.WEBEVENTS_REG_ERRORS, ae);
			generateToken(request);
			saveToken(request);
			return mapping.findForward("step2");
		}
	}

	public static boolean sendRegistrationReceipt(ActionMapping mapping,
			ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) {

		WebEventRegistration registration = (WebEventRegistration) request.getSession().getAttribute(WebKeys.WEBEVENTS_REG_BEAN);

		UserProxy proxy;
		User user;
		try {
			proxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
			user = APILocator.getUserAPI().loadUserById(proxy.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
		} catch (Exception e1) {
			Logger.error(WebEventRegistrationAction.class,e1.getMessage());
			return false;
		}

		// build a decent default context
		String path = "";
		
		WebEvent event = WebEventFactory.getWebEvent(registration.getEventInode());

		if (event.isInstitute()) {
			if (registration.getPaymentType() == 1)
				path = mapping.findForward("creditCardConfirmationEmail").getPath();
			else
				path = mapping.findForward("checkConfirmationEmail").getPath();
		}
		else {
			if (registration.getPaymentType() == 1)
				path = mapping.findForward("webinarCCConfirmationEmail").getPath();
			else
				path = mapping.findForward("webinarCheckConfirmationEmail").getPath();
		}

		try {
			Host currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(request);
			StringBuffer writer = UtilMethods.getURL("http://"
					+ currentHost.getHostname() + path
					+ "?registrationInode=" + registration.getInode());

			String[] reportEmails = Config
					.getStringArrayProperty("BCCEMAIL_REGISTRATION_ADDRESSES");

			StringBuffer bcc = new StringBuffer();
			for (String email : reportEmails) {
				if (bcc.toString().length() > 0)
					bcc.append(", ");
				bcc.append(email);
			}

			Mailer m = new Mailer();
			m.setToEmail(user.getEmailAddress());
			m.setSubject(Config
					.getStringProperty("WEB_EVENT_REGISTRATION_EMAIL_TITLE"));
			m.setHTMLBody(writer.toString().trim());
			m.setFromEmail(Config.getStringProperty("EMAIL_REGISTRATION_ADDRESS"));
			m.setBcc(bcc.toString());
			m.sendMessage();

			return true;
		} catch (Exception e) {
			Logger.error(WebEventRegistrationAction.class,e.getMessage());
			return false;
		}

	}

	public CreditCardProcessorResponse creditCardPayment(WebEventRegistrationForm form, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		UserProxy proxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(form.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
		User user;
		user = APILocator.getUserAPI().loadUserById(proxy.getUserId(),APILocator.getUserAPI().getSystemUser(),false);

		float registrationAmount = 0;
		List<WebEventAttendeeForm> attendeesForms = form.getEventAttendees();
		for (WebEventAttendeeForm attendeeForm : attendeesForms) {
			registrationAmount += attendeeForm.getRegistrationPrice();
		}

		//AuthorizeCreditCardProcessor authorizeCCP = new AuthorizeCreditCardProcessor();
		CreditCardProcessor authorizeCCP = CreditCardProcessor.getInstance();

		authorizeCCP.setRequest(request);
		authorizeCCP.setResponse(response);
		
		//Fill the data
		authorizeCCP.setBillingFirstName(user.getFirstName());
		authorizeCCP.setBillingLastName(user.getLastName());
		String street = form.getBillingAddress1().trim() + " " + form.getBillingAddress2().trim();
		authorizeCCP.setBillingStreet(street);
		authorizeCCP.setBillingCity(form.getBillingCity());
		authorizeCCP.setBillingState(form.getBillingState());
		authorizeCCP.setBillingCountry(form.getBillingCountry());
		authorizeCCP.setBillingZip(form.getBillingZip());
		authorizeCCP.setBillingPhone(form.getBillingContactPhone());
		authorizeCCP.setBillingEmailAdress(form.getBillingContactEmail());
		
    	GregorianCalendar gc = new GregorianCalendar();
    	int month = Integer.parseInt(form.getCardExpMonth());
    	int year = Integer.parseInt(form.getCardExpYear());
    	gc.set(GregorianCalendar.MONTH,month);
    	gc.set(GregorianCalendar.YEAR,year);
    	gc.set(GregorianCalendar.DATE,1);
    	    	
    	authorizeCCP.setCreditCardNumber(form.getCardNumber());
    	authorizeCCP.setCreditCardName(form.getCardName());
    	authorizeCCP.setCreditCardSExpirationDate(gc.getTime());
    	authorizeCCP.setAmount(registrationAmount);
    	authorizeCCP.setCreditCardCVV(form.getCardVerificationValue());
    	CreditCardProcessorResponse ccpr = authorizeCCP.process();
    	return ccpr;
	}

}
package com.dotmarketing.portlets.event_registrations.struts;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.CreditCardValidator;
import org.apache.commons.validator.EmailValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;

public class WebEventRegistrationForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;

	private String inode; // persistent

	private String eventInode; // persistent

	private String eventLocationInode; // persistent

	private String userInode; // persistent

	private String userId; // persistent

	private int registrationStatus; // persistent

	private java.util.Date datePosted; // persistent

	private java.util.Date lastModDate; // persistent

	private float totalPaid; // persistent

	private float totalDue; // persistent

	private float totalRegistration;

	private int paymentType; // persistent

	private String billingAddress1; // persistent

	private String billingAddress2; // persistent

	private String billingCity; // persistent

	private String billingState; // persistent

	private String billingStateOtherCountryText; // persistent
	
	private String billingCountry; // persistent
		
	private String billingZip; // persistent

	private String billingContactName; // persistent

	private String billingContactPhone; // persistent

	private String billingContactEmail; // persistent

	private String cardName; // persistent

	private String cardType; // persistent

	private String cardNumber; // persistent

	private String cardExpMonth;

	private String cardExpYear;

	private String cardVerificationValue; // persistent

	private String checkNumber; // persistent

	private String checkBankName; // persistent

	private String poNumber; // persistent

	private String invoiceNumber; // persistent

	private boolean badgePrinted; // persistent

	private String howDidYouHear; // persistent

	private String ceoName; // persistent

	// Info from liferay users
	private String registrantFacility;

	private String registrantFacilityInode;

	private String registrantSystem;

	private String registrantSystemInode;

	private String registrantFirstName;

	private String registrantLastName;

	private String registrantEmail;

	// Web attendees for the web event registrations frontend
	private List<WebEventAttendeeForm> eventAttendees = new ArrayList<WebEventAttendeeForm>();

	private String currentAttendeeFirstName; // persistent

	private String currentAttendeeLastName; // persistent

	private String currentAttendeeBadgeName; // persistent

	private String currentAttendeeEmail; // persistent

	private String currentAttendeeInode; // persistent

	private String currentAttendeeLastEmail; // persistent

	private String currentAttendeeLastFirstName; // persistent

	private String currentAttendeeLastLastName; // persistent

	private String currentAttendeeTitle; // persistent

	private float currentAttendeePrice; // persistent
	
	private boolean modified_QB; // persistent

	public WebEventRegistrationForm() {
		modified_QB = true;
	}

	public boolean isCreditCardPayment() {
		return paymentType == Config.getIntProperty("EREG_CREDIT_CARD");
	}
	public boolean isCheckPayment() {
		return paymentType == Config.getIntProperty("EREG_CHECK");
	}
	public boolean isPOPayment() {
		return paymentType == Config.getIntProperty("EREG_PURCHASE_ORDER");
	}

	public WebEventRegistrationForm(String eventInode, String eventLocationInode,
			String userInode) {
		this.eventInode = eventInode;
		this.eventLocationInode = eventLocationInode;
		this.userInode = userInode;
	}

	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = super.validate(mapping, request);
		if (errors == null)
			errors = new ActionErrors();

		// Web Events registration validations
		if ((UtilMethods.isSet(request.getParameter("dispatch")) && request
				.getParameter("dispatch").equals("editAttendee"))
				|| (UtilMethods.isSet(request.getParameter("dispatch")) && request
						.getParameter("dispatch").equals("saveAttendee"))
				|| (UtilMethods.isSet(request.getParameter("cmd")) && request
						.getParameter("cmd").equals("add_attendee"))) {

			if (!UtilMethods.isSet(currentAttendeeEmail)) {
				errors.add("currentAttendeeEmail", new ActionMessage(
						"error.form.mandatory", "attendee email address"));
			} else {
				EmailValidator validator = EmailValidator.getInstance();
				if (!validator.isValid(currentAttendeeEmail)) {
					errors.add(Globals.ERROR_KEY, new ActionMessage(
							"error.form.format", "email"));
				}

			}
			if (!UtilMethods.isSet(currentAttendeeFirstName)) {
				errors.add("currentAttendeeFirstName", new ActionMessage(
						"error.form.mandatory", "first name"));
			}
			if (!UtilMethods.isSet(currentAttendeeLastName)) {
				errors.add("currentAttendeeLastName", new ActionMessage(
						"error.form.mandatory", "last name"));
			}
			if (!UtilMethods.isSet(currentAttendeeTitle)) {
				errors.add("currentAttendeeTitle", new ActionMessage(
						"error.form.mandatory", "Title"));
			}
		} else if ((UtilMethods.isSet(request.getParameter("dispatch")) && request
				.getParameter("dispatch").equals("toStep3"))
				|| (UtilMethods.isSet(request.getParameter("cmd")) && request
						.getParameter("cmd").equals("step4"))) {
			if (!UtilMethods.isSet(billingAddress1)) {
				errors.add("billingAddress1", new ActionMessage(
						"error.form.mandatory", "Street1"));
			}
			if (!UtilMethods.isSet(billingCity)) {
				errors.add("billingCity", new ActionMessage(
						"error.form.mandatory", "City"));
			}
			if (!UtilMethods.isSet(billingState)) {
				errors.add("billingState", new ActionMessage(
						"error.form.mandatory", "State"));
			}
			if (!UtilMethods.isSet(billingZip)) {
				errors.add("billingZip", new ActionMessage(
						"error.form.mandatory", "Zip"));
			}
			if (!UtilMethods.isSet(billingContactName)) {
				errors.add("billingContactName", new ActionMessage(
						"error.form.mandatory",
						"Billing Contacts Person Name"));
			}
			if (!UtilMethods.isSet(billingContactPhone)) {
				errors.add("billingContactPhone", new ActionMessage(
						"error.form.mandatory", "Phone"));
			}
			if (!UtilMethods.isSet(billingContactEmail)) {
				errors.add("billingContactEmail", new ActionMessage(
						"error.form.mandatory", "Email"));
			} else {

				EmailValidator validator = EmailValidator.getInstance();
				if (!validator.isValid(billingContactEmail)) {
					errors.add(Globals.ERROR_KEY, new ActionMessage(
							"error.form.format", "email"));
				}
			}
			if (isCreditCardPayment()) {
				if (!UtilMethods.isSet(cardName)) {
					errors.add("cardName", new ActionMessage(
							"error.form.mandatory", "Credit Card Name"));
				}
				if (!UtilMethods.isSet(cardNumber)) {
					errors.add("cardNumber", new ActionMessage(
							"error.form.mandatory", "Credit Card Number"));
				}
				if (!UtilMethods.isSet(cardVerificationValue)) {
					errors.add("cardVerificationValue", new ActionMessage(
							"error.form.mandatory", "Card CVV Number"));
				} else {
					if (!cardNumber.equals("4111-1111-1111-1111")
							|| cardNumber.equals("4111111111111111")) {
						CreditCardValidator cardValidator = new CreditCardValidator();
						if (cardType.equals("Visa")) {
							cardValidator = new CreditCardValidator(
									CreditCardValidator.VISA);
						} else if (cardType.equals("MasterCard")) {
							cardValidator = new CreditCardValidator(
									CreditCardValidator.MASTERCARD);
						} else if (cardType.equals("Discover")) {
							cardValidator = new CreditCardValidator(
									CreditCardValidator.DISCOVER);
						} else if (cardType.equals("American Express")) {
							cardValidator = new CreditCardValidator(
									CreditCardValidator.AMEX);
						}
						if (!cardValidator.isValid(cardNumber)) {
							errors.add("cardNumber", new ActionMessage(
									"error.invalid.credit.card.number"));
						}
					}
				}
				int month = Integer.parseInt(this.cardExpMonth) - 1;
				int year = Integer.parseInt(cardExpYear);
				GregorianCalendar cal = new GregorianCalendar();
				int currentMonth = cal.get(GregorianCalendar.MONTH);
				int currentYear = cal.get(GregorianCalendar.YEAR);
				if (year < currentYear
						|| (year == currentYear && month < currentMonth)) {
					errors.add("cardExpMonth", new ActionMessage(
							"error.invalid.credit.card.expiration"));
				}
			}else if (isPOPayment()){
					if (!UtilMethods.isSet(poNumber)) {
						errors.add("poNumber", new ActionMessage(
								"error.form.mandatory", "PO Number"));
					}
			}
		}
		return errors;
	}

	public String getEventInode() {
		return this.eventInode;
	}

	public void setEventInode(String eventInode) {
		this.eventInode = eventInode;
	}

	public String getEventLocationInode() {
		return this.eventLocationInode;
	}

	public void setEventLocationInode(String eventLocationInode) {
		this.eventLocationInode = eventLocationInode;
	}

	public String getUserInode() {
		return this.userInode;
	}

	public void setUserInode(String userInode) {
		this.userInode = userInode;
	}

	public java.util.Date getDatePosted() {
		return this.datePosted;
	}

	public void setDatePosted(java.util.Date datePosted) {
		this.datePosted = datePosted;
	}

	public java.util.Date getLastModDate() {
		return this.lastModDate;
	}

	public void setLastModDate(java.util.Date lastModDate) {
		this.lastModDate = lastModDate;
	}

	public float getTotalPaid() {
		return this.totalPaid;
	}

	public void setTotalPaid(float totalPaid) {
		this.totalPaid = totalPaid;
	}

	public float getTotalDue() {
		return this.totalDue;
	}

	public void setTotalDue(float totalDue) {
		this.totalDue = totalDue;
	}

	public String getBillingAddress1() {
		return this.billingAddress1;
	}

	public void setBillingAddress1(String billingAddress1) {
		this.billingAddress1 = billingAddress1;
	}

	public String getBillingAddress2() {
		return this.billingAddress2;
	}

	public void setBillingAddress2(String billingAddress2) {
		this.billingAddress2 = billingAddress2;
	}

	public String getBillingCity() {
		return this.billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return this.billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getBillingZip() {
		return this.billingZip;
	}

	public void setBillingZip(String billingZip) {
		this.billingZip = billingZip;
	}

	public String getBillingContactName() {
		return this.billingContactName;
	}

	public void setBillingContactName(String billingContactName) {
		this.billingContactName = billingContactName;
	}

	public String getBillingContactPhone() {
		return this.billingContactPhone;
	}

	public void setBillingContactPhone(String billingContactPhone) {
		this.billingContactPhone = billingContactPhone;
	}

	public String getBillingContactEmail() {
		return this.billingContactEmail;
	}

	public void setBillingContactEmail(String billingContactEmail) {
		this.billingContactEmail = billingContactEmail;
	}

	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return Returns the cardExpMonth.
	 */
	public String getCardExpMonth() {
		return cardExpMonth;
	}

	/**
	 * @param cardExpMonth
	 *            The cardExpMonth to set.
	 */
	public void setCardExpMonth(String cardExpMonth) {
		this.cardExpMonth = cardExpMonth;
	}

	/**
	 * @return Returns the cardExpYear.
	 */
	public String getCardExpYear() {
		return cardExpYear;
	}

	/**
	 * @param cardExpYear
	 *            The cardExpYear to set.
	 */
	public void setCardExpYear(String cardExpYear) {
		this.cardExpYear = cardExpYear;
	}

	public String getCardVerificationValue() {
		return this.cardVerificationValue;
	}

	public void setCardVerificationValue(String cardVerificationValue) {
		this.cardVerificationValue = cardVerificationValue;
	}

	public String getCheckNumber() {
		return this.checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getCheckBankName() {
		return this.checkBankName;
	}

	public void setCheckBankName(String checkBankName) {
		this.checkBankName = checkBankName;
	}

	public String getPoNumber() {
		return this.poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getHowDidYouHear() {
		return this.howDidYouHear;
	}

	public void setHowDidYouHear(String howDidYouHear) {
		this.howDidYouHear = howDidYouHear;
	}

	public String getCeoName() {
		return this.ceoName;
	}

	public void setCeoName(String ceoName) {
		this.ceoName = ceoName;
	}

	/**
	 * @return Returns the badgePrinted.
	 */
	public boolean isBadgePrinted() {
		return badgePrinted;
	}

	/**
	 * @param badgePrinted
	 *            The badgePrinted to set.
	 */
	public void setBadgePrinted(boolean badgePrinted) {
		this.badgePrinted = badgePrinted;
	}

	/**
	 * @return Returns the inode.
	 */
	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	/**
	 * @param inode
	 *            The inode to set.
	 */
	public void setInode(String inode) {
		this.inode = inode;
	}

	/**
	 * @return Returns the totalRegistration.
	 */
	public float getTotalRegistration() {
		return totalRegistration;
	}

	/**
	 * @param totalRegistration
	 *            The totalRegistration to set.
	 */
	public void setTotalRegistration(float totalRegistration) {
		this.totalRegistration = totalRegistration;
	}

	public List<WebEventAttendeeForm> getEventAttendees() {
		return eventAttendees;
	}

	public void setEventAttendees(List<WebEventAttendeeForm> attendees) {
		this.eventAttendees = attendees;
	}

	public String getCurrentAttendeeBadgeName() {
		return currentAttendeeBadgeName;
	}

	public void setCurrentAttendeeBadgeName(String currentAttendeeBadgeName) {
		this.currentAttendeeBadgeName = currentAttendeeBadgeName;
	}

	public String getCurrentAttendeeEmail() {
		return currentAttendeeEmail;
	}

	public void setCurrentAttendeeEmail(String currentAttendeeEmail) {
		this.currentAttendeeEmail = currentAttendeeEmail;
	}

	public String getCurrentAttendeeFirstName() {
		return currentAttendeeFirstName;
	}

	public void setCurrentAttendeeFirstName(String currentAttendeeFirstName) {
		this.currentAttendeeFirstName = currentAttendeeFirstName;
	}

	public String getCurrentAttendeeLastName() {
		return currentAttendeeLastName;
	}

	public void setCurrentAttendeeLastName(String currentAttendeeLastName) {
		this.currentAttendeeLastName = currentAttendeeLastName;
	}

	public String getCurrentAttendeeTitle() {
		return currentAttendeeTitle;
	}

	public void setCurrentAttendeeTitle(String currentAttendeeTitle) {
		this.currentAttendeeTitle = currentAttendeeTitle;
	}

	public void resetCurrentAttendee() {
		setCurrentAttendeeBadgeName("");
		setCurrentAttendeeEmail("");
		setCurrentAttendeeFirstName("");
		setCurrentAttendeeLastName("");
		setCurrentAttendeeTitle("");
	}

	public String getCurrentAttendeeLastEmail() {
		return currentAttendeeLastEmail;
	}

	public void setCurrentAttendeeLastEmail(String currentAttendeeLastEmail) {
		this.currentAttendeeLastEmail = currentAttendeeLastEmail;
	}

	/**
	 * @return Returns the registrantEmail.
	 */
	public String getRegistrantEmail() {
		return registrantEmail;
	}

	/**
	 * @param registrantEmail
	 *            The registrantEmail to set.
	 */
	public void setRegistrantEmail(String registrantEmail) {
		this.registrantEmail = registrantEmail;
	}

	/**
	 * @return Returns the registrantFacility.
	 */
	public String getRegistrantFacility() {
		return registrantFacility;
	}

	/**
	 * @param registrantFacility
	 *            The registrantFacility to set.
	 */
	public void setRegistrantFacility(String registrantFacility) {
		this.registrantFacility = registrantFacility;
	}

	/**
	 * @return Returns the registrantFirstName.
	 */
	public String getRegistrantFirstName() {
		return registrantFirstName;
	}

	/**
	 * @param registrantFirstName
	 *            The registrantFirstName to set.
	 */
	public void setRegistrantFirstName(String registrantFirstName) {
		this.registrantFirstName = registrantFirstName;
	}

	/**
	 * @return Returns the registrantLastName.
	 */
	public String getRegistrantLastName() {
		return registrantLastName;
	}

	/**
	 * @param registrantLastName
	 *            The registrantLastName to set.
	 */
	public void setRegistrantLastName(String registrantLastName) {
		this.registrantLastName = registrantLastName;
	}

	/**
	 * @return Returns the registrantSystem.
	 */
	public String getRegistrantSystem() {
		return registrantSystem;
	}

	/**
	 * @param registrantSystem
	 *            The registrantSystem to set.
	 */
	public void setRegistrantSystem(String registrantSystem) {
		this.registrantSystem = registrantSystem;
	}

	public void resetAllForm() {
		this.billingAddress1 = "";
		this.billingAddress2 = "";
		this.billingCity = "";
		this.billingContactEmail = "";
		this.billingContactName = "";
		this.billingContactPhone = "";
		this.billingState = "";
		this.billingZip = "";
		this.cardExpMonth = "";
		this.cardExpYear = "";
		this.cardName = "";
		this.cardNumber = "";
		this.cardType = "";
		this.cardVerificationValue = "";
		this.ceoName = "";
		this.checkBankName = "";
		this.checkNumber = "";
		this.currentAttendeeBadgeName = "";
		this.currentAttendeeEmail = "";
		this.currentAttendeeFirstName = "";
		this.currentAttendeeLastEmail = "";
		this.currentAttendeeLastName = "";
		this.currentAttendeeTitle = "";
		this.eventAttendees = new ArrayList<WebEventAttendeeForm>();
		this.eventInode = "";
		this.eventLocationInode = "";
		this.howDidYouHear = "";
		this.inode = "";
		this.invoiceNumber = "";
		this.paymentType = 0;
		this.poNumber = "";
		this.registrantEmail = "";
		this.registrantFacility = "";
		this.registrantFirstName = "";
		this.registrantLastName = "";
		this.registrantSystem = "";
		this.registrationStatus = 0;
		this.totalDue = 0;
		this.totalPaid = 0;
		this.totalRegistration = 0;
		this.userInode = "";
	}

	/**
	 * @return Returns the paymentType.
	 */
	public int getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType
	 *            The paymentType to set.
	 */
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return Returns the registrationStatus.
	 */
	public int getRegistrationStatus() {
		return registrationStatus;
	}

	/**
	 * @param registrationStatus
	 *            The registrationStatus to set.
	 */
	public void setRegistrationStatus(int registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

	/**
	 * @return Returns the registrantFacilityInode.
	 */
	public String getRegistrantFacilityInode() {
		return registrantFacilityInode;
	}

	/**
	 * @param registrantFacilityInode
	 *            The registrantFacilityInode to set.
	 */
	public void setRegistrantFacilityInode(String registrantFacilityInode) {
		this.registrantFacilityInode = registrantFacilityInode;
	}

	/**
	 * @return Returns the registrantSystemInode.
	 */
	public String getRegistrantSystemInode() {
		return registrantSystemInode;
	}

	/**
	 * @param registrantSystemInode
	 *            The registrantSystemInode to set.
	 */
	public void setRegistrantSystemInode(String registrantSystemInode) {
		this.registrantSystemInode = registrantSystemInode;
	}

	/**
	 * @return Returns the currentAttendeePrice.
	 */
	public float getCurrentAttendeePrice() {
		return currentAttendeePrice;
	}

	/**
	 * @param currentAttendeePrice
	 *            The currentAttendeePrice to set.
	 */
	public void setCurrentAttendeePrice(float currentAttendeePrice) {
		this.currentAttendeePrice = currentAttendeePrice;
	}

	/**
	 * @return Returns the currentAttendeeInode.
	 */
	public String getCurrentAttendeeInode() {
		return currentAttendeeInode;
	}

	/**
	 * @param currentAttendeeInode
	 *            The currentAttendeeInode to set.
	 */
	public void setCurrentAttendeeInode(String currentAttendeeInode) {
		this.currentAttendeeInode = currentAttendeeInode;
	}

	/**
	 * @return Returns the userId.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            The userId to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return Returns the modified_QB.
	 */
	public boolean isModified_QB() {
		return modified_QB;
	}

	/**
	 * @param modified_QB
	 *            The modified_QB to set.
	 */
	public void setModified_QB(boolean modified_QB) {
		this.modified_QB = modified_QB;
	}

	/**
	 * @return Returns the currentAttendeeLastFirstName.
	 */
	public String getCurrentAttendeeLastFirstName() {
		return currentAttendeeLastFirstName;
	}

	/**
	 * @param currentAttendeeLastFirstName The currentAttendeeLastFirstName to set.
	 */
	public void setCurrentAttendeeLastFirstName(String currentAttendeeLastFirstName) {
		this.currentAttendeeLastFirstName = currentAttendeeLastFirstName;
	}

	/**
	 * @return Returns the currentAttendeeLastLastName.
	 */
	public String getCurrentAttendeeLastLastName() {
		return currentAttendeeLastLastName;
	}

	/**
	 * @param currentAttendeeLastLastName The currentAttendeeLastLastName to set.
	 */
	public void setCurrentAttendeeLastLastName(String currentAttendeeLastLastName) {
		this.currentAttendeeLastLastName = currentAttendeeLastLastName;
	}

	/**
	 * @return Returns the billingCountry.
	 */
	public String getBillingCountry() {
		return billingCountry;
	}

	/**
	 * @param billingCountry The billingCountry to set.
	 */
	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	/**
	 * @return Returns the billingStateOtherCountryText.
	 */
	public String getBillingStateOtherCountryText() {
		return billingStateOtherCountryText;
	}

	/**
	 * @param billingStateOtherCountryText The billingStateOtherCountryText to set.
	 */
	public void setBillingStateOtherCountryText(String billingStateOtherCountryText) {
		this.billingStateOtherCountryText = billingStateOtherCountryText;
	}


}

package com.dotmarketing.portlets.order_manager.struts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.CreditCardValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;


public class OrderForm extends ValidatorForm  {

	private static final long serialVersionUID = 1L;
	private String inode; //identifier
    private String userInode; //persistent
    private String userId;
    private int orderStatus; //persistent
    private int paymentStatus; //persistent
    private java.util.Date datePosted; //persistent
    private java.util.Date lastModDate; //persistent
    private String billingAddressType; //persistent
    private String billingAddress1; //persistent
    private String billingAddress2; //persistent
    private String billingCity; //persistent
    private String billingState; //persistent
    private String billingStateOtherCountryText;
    private String billingZip; //persistent
    private String billingCountry; //persistent
    private String billingPhone; //persistent
    private String billingFax; //persistent
    private String billingContactName; //persistent
	private String billingFirstName; //persistent
	private String billingLastName; //persistent
    private String billingContactPhone; //persistent
    private String billingContactEmail; //persistent
    private String billingCompany; //persistent
    private String shippingAddressType; //persistent
    private String shippingAddress1; //persistent
    private String shippingAddress2; //persistent
    private String shippingLabel; //persistent
    private String shippingCity; //persistent
    private String shippingState; //persistent
    private String shippingStateOtherCountryText; 
    private String shippingZip; //persistent
    private String shippingCountry; //persistent
    private String shippingPhone; //persistent
    private String shippingFax; //persistent
    private String shippingFirstName; //persistent
	private String shippingLastName; //persistent
	private String shippingCompany; //persistent
    private String paymentType; //persistent
    private String nameOnCard; //persistent
    private String cardType; //persistent
    private String cardNumber; //persistent
    private int cardExpMonth; //persistent
    private int cardExpYear; //persistent
    private String cardVerificationValue; //persistent
    private float orderSubTotal; //persistent
    private float orderDiscount; //persistent
    private float orderSubTotalDiscount; //persistent
    private float orderShipping; //persistent
    private int orderShipType; //persistent
    private float orderTax; //persistent
    private String taxExemptNumber; //persistent
    private String discountCodes; //persistent
    private float orderTotal; //persistent
    private float orderTotalPaid; //persistent
    private float orderTotalDue; //persistent  
    private String invoiceNumber; //persistent
    private java.util.Date invoiceDate; //persistent
    private String checkNumber; //persistent
    private String checkBankName; //persistent
    private String poNumber; //persistent
    private String trackingNumber; //persistent
    private boolean isShippingZero;
    private String contactName;
    private String contactEmail;
    private String contactSystem;
    private String contactFacility;
    private boolean modified_QB; // persistent
    private boolean modified_FH; // persistent
    private String backendUser; //persistent
    private String backendUserName;
    //new
    private String homeAddress1; 
    private String homeAddress2; 
    private String homeLabel; 
    private String homeCity; 
    private String homeState; 
    private String homeStateOtherCountryText; 
    private String homeZip; 
    private String homeCountry; 
    private String homePhone; 
    private String homeFax; 
	private String homeFirstName;
	private String homeLastName;
    private String homeContactName; 
    private String homeContactPhone; 
    private String homeContactEmail; 
    
    private String workAddress1; 
    private String workAddress2; 
    private String workLabel; 
    private String workCity; 
    private String workState; 
    private String workStateOtherCountryText; 
    private String workZip; 
    private String workCountry; 
    private String workPhone; 
    private String workFax;
	private String workFirstName;
	private String workLastName;
    private String workContactName; 
    private String workContactPhone; 
    private String workContactEmail; 
    
    private List<OrderItemForm> orderItemList = new ArrayList<OrderItemForm> ();

    public boolean isCreditCardPayment() {
    	String creditCarType = Config.getStringProperty("ECOM_CREDIT_CARD");
    	return creditCarType.equals(paymentType.trim());
    }
    
    
    
    public OrderForm() {
    	paymentType = "";
    	cardExpMonth = -1;
    	cardExpYear = -1;
    	this.modified_QB = true;
    	this.modified_FH = true;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }
    
    
    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public String getUserInode() {
        return this.userInode;
    }

    public void setUserInode(String userInode) {
        this.userInode = userInode;
    }
    public int getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
    public int getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
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
    	if (billingState != null)
    		return this.billingState.trim();
    	else
    		return null;
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
    public String getBillingCountry() {
        return this.billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }
    public String getBillingPhone() {
        return this.billingPhone;
    }

    public void setBillingPhone(String billingPhone) {
        this.billingPhone = billingPhone;
    }
    public String getBillingFax() {
        return this.billingFax;
    }

    public void setBillingFax(String billingFax) {
        this.billingFax = billingFax;
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
    public String getShippingAddress1() {
        return this.shippingAddress1;
    }

    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }
    public String getShippingAddress2() {
        return this.shippingAddress2;
    }

    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }
    public String getShippingCity() {
        return this.shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    public String getShippingState() {
    	if (this.shippingState != null)
    		return this.shippingState.trim();
    	else
    		return "";
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }
    public String getShippingZip() {
        return this.shippingZip;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }
    public String getShippingCountry() {
        return this.shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }
    public String getShippingPhone() {
        return this.shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }
    public String getShippingFax() {
        return this.shippingFax;
    }

    public void setShippingFax(String shippingFax) {
        this.shippingFax = shippingFax;
    }
    public String getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType.trim();
    }
    public String getNameOnCard() {
        return this.nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
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
    public int getCardExpMonth() {
        return this.cardExpMonth;
    }

    public void setCardExpMonth(int cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
    }
    public int getCardExpYear() {
        return this.cardExpYear;
    }

    public void setCardExpYear(int cardExpYear) {
        this.cardExpYear = cardExpYear;
    }
    public String getCardVerificationValue() {
        return this.cardVerificationValue;
    }

    public void setCardVerificationValue(String cardVerificationValue) {
        this.cardVerificationValue = cardVerificationValue;
    }
    public float getOrderSubTotal() {
        return this.orderSubTotal;
    }

    public void setOrderSubTotal(float orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }
    public float getOrderShipping() {
        return this.orderShipping;
    }

    public void setOrderShipping(float orderShipping) {
        this.orderShipping = orderShipping;
    }
    public int getOrderShipType() {
        return this.orderShipType;
    }

    public void setOrderShipType(int orderShipType) {
        this.orderShipType = orderShipType;
    }
    public float getOrderTax() {
        return this.orderTax;
    }

    public void setOrderTax(float orderTax) {
        this.orderTax = orderTax;
    }
    public String getDiscountCodes() {
        return this.discountCodes;
    }

    public void setDiscountCodes(String discountCodes) {
        this.discountCodes = discountCodes;
    }
    public float getOrderTotal() {
        return this.orderTotal;
    }

    public void setOrderTotal(float orderTotal) {
        this.orderTotal = orderTotal;
    }
    public float getOrderTotalPaid() {
        return this.orderTotalPaid;
    }

    public void setOrderTotalPaid(float orderTotalPaid) {
        this.orderTotalPaid = orderTotalPaid;
    }
    public float getOrderTotalDue() {
        return this.orderTotalDue;
    }

    public void setOrderTotalDue(float orderTotalDue) {
        this.orderTotalDue = orderTotalDue;
    }
    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    public java.util.Date getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceDate(java.util.Date invoiceDate) {
        this.invoiceDate = invoiceDate;
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
    
    public ActionErrors validate  (User user)throws Exception
    {
    	ActionErrors ae = new ActionErrors();
    	//Billing Address
    	if(!UtilMethods.isSet(billingAddressType))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Address")));
    	}
    	if(!UtilMethods.isSet(billingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(billingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-City")));
    	}
    	if(!UtilMethods.isSet(billingCountry))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Country")));
    	}
    	if(!UtilMethods.isSet(billingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-State")));    		
    	}

    	
    	if(!UtilMethods.isSet(billingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Zip")));
    	}  
    	//Shpping Address
    	if(!UtilMethods.isSet(shippingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(shippingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-City")));
    	}
    	if(!UtilMethods.isSet(shippingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-State")));    		
    	}

    	
    	if(!UtilMethods.isSet(shippingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Zip")));
    	}
    	if(!UtilMethods.isSet(shippingPhone))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Phone")));
    	}
    	if(!UtilMethods.isSet(shippingAddressType))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Address")));
    	}
    	//Payment Type
    	if (paymentType.equals("cc"))
    	{
    		if(!UtilMethods.isSet(nameOnCard))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Name-on-Card")));
        	}    		
    		if(!UtilMethods.isSet(cardNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Card-Number")));
        	}
    		else
    		{    			
    			CreditCardValidator cardValidator = new CreditCardValidator ();
    			if (cardType.equals("vs")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.VISA);
    			} else if (cardType.equals("mc")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.MASTERCARD);
    			} else if (cardType.equals("dc")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.DISCOVER);
    			} else if (cardType.equals("ae")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.AMEX);
    			}
    			if (!cardNumber.equals("5111111111111111") && !cardNumber.equals("4111111111111111") && !cardValidator.isValid(cardNumber)) 
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.invalid.credit.card.number"));
    			}
    		}
    		/*if(!UtilMethods.isSet(cardVerificationValue))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Card Verification Value"));
        	}
    		else
    		{
    			if(cardVerificationValue.length() < 3 || cardVerificationValue.length() > 4)
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.form.length","Card Verification Value","3 or 4"));    				
    			}
    			if(!UtilMethods.isInt(cardVerificationValue))
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.form.format","Card Verification Value"));
    			}
    		}*/
    		Date now = new Date();
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(now);
    		if ((cardExpYear < calendar.get(Calendar.YEAR)) || 
    		   ((cardExpYear == calendar.get(Calendar.YEAR)) && (cardExpMonth < calendar.get(Calendar.MONTH))))
    		{
    			ae.add(Globals.ERROR_KEY, new ActionMessage("error.invalid.credit.card.expiration"));
    		}
    	}
    	else if (paymentType.equals("ch"))
    	{
    		/*if(!UtilMethods.isSet(checkNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Number"));
        	}
    		if(!UtilMethods.isSet(checkBankName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Bank Name"));
        	}*/
    	}
    	else if (paymentType.equals("po"))
    	{
    		/*if(!UtilMethods.isSet(poNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","PO Number"));       
        	}*/
    	}
    	///Contact Info
    	if(UtilMethods.isSet(billingAddressType) && billingAddressType.equals("Work"))
    	{
    		/*if(!UtilMethods.isSet(billingContactName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Billing Contact Person Name"));
        	}*/
    		if(!UtilMethods.isSet(billingFirstName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Contact-Person-First-Name")));
        	}
    		if(!UtilMethods.isSet(billingLastName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Contact-Person-Last-Name")));
        	}
        	if(!UtilMethods.isSet(billingContactPhone))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Phone")));
        	}
        	if(!UtilMethods.isSet(billingContactEmail))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Phone")));
        	}
    	}
    	return ae;
    }

	public String getBillingAddressType() {
		return billingAddressType;
	}

	public void setBillingAddressType(String billingAddressType) {
		this.billingAddressType = billingAddressType;
	}

	public String getShippingAddressType() {
		return shippingAddressType;
	}

	public void setShippingAddressType(String shippingAddressType) {
		this.shippingAddressType = shippingAddressType;
	}

	public float getOrderDiscount() {
		return orderDiscount;
	}

	public void setOrderDiscount(float orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public float getOrderSubTotalDiscount() {
		return orderSubTotalDiscount;
	}

	public void setOrderSubTotalDiscount(float orderSubTotalDiscount) {
		this.orderSubTotalDiscount = orderSubTotalDiscount;
	}

	public List<OrderItemForm> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItemForm> orderItemList) {
		this.orderItemList = orderItemList;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getTaxExemptNumber() {
		return taxExemptNumber;
	}

	public void setTaxExemptNumber(String taxExemptNumber) {
		this.taxExemptNumber = taxExemptNumber;
	}
	
	public ActionErrors validateBackEnd(User user)
   throws Exception {
    	ActionErrors ae = new ActionErrors();
    	//Billing Address    	
    	if(!UtilMethods.isSet(billingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(billingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-City")));
    	}
    	if(!UtilMethods.isSet(billingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-State")));    		
    	}
    	
    	if(!UtilMethods.isSet(billingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Zip")));
    	} 
    	
    	if(!UtilMethods.isSet(billingPhone))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Phone")));
    	} 
    	//Shpping Address
    	if(!UtilMethods.isSet(shippingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(shippingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-City")));
    	}
    	if(!UtilMethods.isSet(shippingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-State")));    		
    	}

    	
    	if(!UtilMethods.isSet(shippingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Zip")));
    	}
    	if(!UtilMethods.isSet(shippingPhone))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Phone")));
    	}    	
    	//Payment Type
    	if (paymentType.equals("cc"))
    	{
    		if(!UtilMethods.isSet(nameOnCard))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Name-on-Card")));
        	}    		
    		if(!UtilMethods.isSet(cardNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Card-Number")));
        	}
    		else
    		{    			
    			CreditCardValidator cardValidator = new CreditCardValidator ();
    			if (cardType.equals("vs")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.VISA);
    			} else if (cardType.equals("mc")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.MASTERCARD);
    			} else if (cardType.equals("dc")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.DISCOVER);
    			} else if (cardType.equals("ae")) {
    				cardValidator = new CreditCardValidator (CreditCardValidator.AMEX);
    			}
    			if (!cardNumber.equals("5111111111111111") && !cardNumber.equals("4111111111111111") && !cardValidator.isValid(cardNumber)) 
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.invalid.credit.card.number"));
    			}
    		}
    		/*if(!UtilMethods.isSet(cardVerificationValue))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Card Verification Value"));
        	}
    		else
    		{
    			if(cardVerificationValue.length() < 3 || cardVerificationValue.length() > 4)
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.form.length","Card Verification Value","3 or 4"));    				
    			}
    			if(!UtilMethods.isInt(cardVerificationValue))
    			{
    				ae.add(Globals.ERROR_KEY, new ActionMessage("error.form.format","Card Verification Value"));
    			}
    		}*/
    		Date now = new Date();
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(now);
    		if ((cardExpYear < calendar.get(Calendar.YEAR)) || 
    		   ((cardExpYear == calendar.get(Calendar.YEAR)) && (cardExpMonth < calendar.get(Calendar.MONTH))))
    		{
    			ae.add(Globals.ERROR_KEY, new ActionMessage("error.invalid.credit.card.expiration"));
    		}
    	}
    	else if (paymentType.equals("ch"))
    	{
    		/*if(!UtilMethods.isSet(checkNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Number"));
        	}
    		if(!UtilMethods.isSet(checkBankName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Bank Name"));
        	}*/
    	}
    	else if (paymentType.equals("po"))
    	{
    		/*if(!UtilMethods.isSet(poNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","PO Number"));       
        	}*/
    	}
    	return ae;
    }
	
	public ActionErrors validateBackEndEdit(User user)
    throws Exception {
    	ActionErrors ae = new ActionErrors();
    	//Billing Address    	
    	if(!UtilMethods.isSet(billingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(billingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-City")));
    	}
    	if(!UtilMethods.isSet(billingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-State")));    		
    	}
    	else
    	{
    		if(UtilMethods.isSet(billingCountry)){
    			if ((billingCountry.equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE")))&& (billingState.equals("otherCountry")))
    				ae.add(Globals.ERROR_KEY,new ActionMessage("error.invalid.state.US"));
    			else{
    				if (!(billingState.equals("otherCountry")))
	    				if (billingState.length() != 2)
						{
							ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.length",LanguageUtil.get(user, "Billing-State"),"2"));
						}
    			}
    		}
    	}
    	
    	if(!UtilMethods.isSet(billingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Billing-Zip")));
    	} 
    	/*
    	if(!UtilMethods.isSet(billingPhone))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Billing Phone"));
    	} 
    	*/
    	//Shpping Address
    	if(!UtilMethods.isSet(shippingAddress1))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Address-Street-1")));
    	}
    	if(!UtilMethods.isSet(shippingCity))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-City")));
    	}
    	if(!UtilMethods.isSet(shippingState))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-State")));    		
    	}
    	else
    	{
    		if(UtilMethods.isSet(shippingCountry)){
    			if ((shippingCountry.equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE")))&& (shippingState.equals("otherCountry")))
    				ae.add(Globals.ERROR_KEY,new ActionMessage("error.invalid.state.US"));
    			else{
    				if (!(shippingState.equals("otherCountry")))
	    				if (shippingState.length() != 2)
						{
							ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.length",LanguageUtil.get(user, "Shipping-State"),"2"));
						}
    			}
    		}
    	}
    	
    	if(!UtilMethods.isSet(shippingZip))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Shipping-Zip")));
    	}
    	/*
    	if(!UtilMethods.isSet(shippingPhone))
    	{
    		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Shipping Phone"));
    	}   
    	*/ 	
    	//Payment Type
    	if (paymentType.equals("cc"))
    	{
    		/*if(!UtilMethods.isSet(nameOnCard))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Name On Card"));
        	}    		
    		if(!UtilMethods.isSet(cardNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Card Number"));
        	}    		
    		if(!UtilMethods.isSet(cardVerificationValue))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Card Verification Value"));
        	}    		
    		Date now = new Date();
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(now);
    		if ((cardExpYear < calendar.get(Calendar.YEAR)) || 
    		   ((cardExpYear == calendar.get(Calendar.YEAR)) && (cardExpMonth < calendar.get(Calendar.MONTH))))
    		{
    			ae.add(Globals.ERROR_KEY, new ActionMessage("error.invalid.credit.card.expiration"));
    		}*/
    	}
    	else if (paymentType.equals("ch"))
    	{
    		/*if(!UtilMethods.isSet(checkNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Number"));
        	}
    		if(!UtilMethods.isSet(checkBankName))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Check Bank Name"));
        	}*/
    	}
    	else if (paymentType.equals("po"))
    	{
    		/*if(!UtilMethods.isSet(poNumber))
        	{
        		ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","PO Number"));       
        	}*/
    	}
    	return ae;
    }


	public boolean getIsShippingZero() {
		return isShippingZero;
	}

	public void setIsShippingZero(boolean isShippingZero) {
		this.isShippingZero = isShippingZero;
	}
	
	/**
	 * @param contactName
	 *            The contact name to set.
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	
	/**
	 * @return Returns the contact name.
	 */
	public String getContactName() {
		return contactName;
	}
	
	/**
	 * @param contactEmail
	 *            The contact email to set.
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	
	/**
	 * @return Returns the contact email.
	 */
	public String getContactEmail() {
		return contactEmail;
	}
	
	/**
	 * @param contactSystem
	 *            The contact system to set.
	 */
	public void setContactSystem(String contactSystem) {
		this.contactSystem = contactSystem;
	}

	
	/**
	 * @return Returns the contact system.
	 */
	public String getContactSystem() {
		return contactSystem;
	}

	/**
	 * @param contactFacility
	 *            The contact facility to set.
	 */
	public void setContactFacility(String contactFacility) {
		this.contactFacility = contactFacility;
	}

	
	/**
	 * @return Returns the contact facility.
	 */
	public String getContactFacility() {
		return contactFacility;
	}
	
	/**
	 * @return Returns the modified_FH.
	 */
	public boolean isModified_FH() {
		return modified_FH;
	}

	/**
	 * @param modified_FH
	 *            The modified_FH to set.
	 */
	public void setModified_FH(boolean modified_FH) {
		this.modified_FH = modified_FH;
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



	public String getBillingStateOtherCountryText() {
		return billingStateOtherCountryText;
	}



	public void setBillingStateOtherCountryText(String billingStateText) {
		this.billingStateOtherCountryText = billingStateText;
	}



	public String getShippingStateOtherCountryText() {
		return shippingStateOtherCountryText;
	}



	public void setShippingStateOtherCountryText(String shippingStateText) {
		this.shippingStateOtherCountryText = shippingStateText;
	}



	/**
	 * @return Returns the userId.
	 */
	public String getUserId() {
		return userId;
	}



	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}



	public String getBackendUser() {
		return backendUser;
	}



	public void setBackendUser(String backendUser) {
		this.backendUser = backendUser;
	}



	public String getBackendUserName() {
		return backendUserName;
	}



	public void setBackendUserName(String backendUserName) {
		this.backendUserName = backendUserName;
	}



	public String getShippingLabel() {
		return shippingLabel;
	}



	public void setShippingLabel(String shippingLabel) {
		this.shippingLabel = shippingLabel;
	}



	public String getHomeAddress1() {
		return homeAddress1;
	}



	public void setHomeAddress1(String homeAddress1) {
		this.homeAddress1 = homeAddress1;
	}



	public String getHomeAddress2() {
		return homeAddress2;
	}



	public void setHomeAddress2(String homeAddress2) {
		this.homeAddress2 = homeAddress2;
	}



	public String getHomeCity() {
		return homeCity;
	}



	public void setHomeCity(String homeCity) {
		this.homeCity = homeCity;
	}



	public String getHomeCountry() {
		return homeCountry;
	}



	public void setHomeCountry(String homeCountry) {
		this.homeCountry = homeCountry;
	}



	public String getHomeFax() {
		return homeFax;
	}



	public void setHomeFax(String homeFax) {
		this.homeFax = homeFax;
	}



	public String getHomeLabel() {
		return homeLabel;
	}



	public void setHomeLabel(String homeLabel) {
		this.homeLabel = homeLabel;
	}



	public String getHomePhone() {
		return homePhone;
	}



	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}



	public String getHomeState() {
		return homeState;
	}



	public void setHomeState(String homeState) {
		this.homeState = homeState;
	}



	public String getHomeStateOtherCountryText() {
		return homeStateOtherCountryText;
	}



	public void setHomeStateOtherCountryText(String homeStateOtherCountryText) {
		this.homeStateOtherCountryText = homeStateOtherCountryText;
	}



	public String getHomeZip() {
		return homeZip;
	}



	public void setHomeZip(String homeZip) {
		this.homeZip = homeZip;
	}



	public String getWorkAddress1() {
		return workAddress1;
	}



	public void setWorkAddress1(String workAddress1) {
		this.workAddress1 = workAddress1;
	}



	public String getWorkAddress2() {
		return workAddress2;
	}



	public void setWorkAddress2(String workAddress2) {
		this.workAddress2 = workAddress2;
	}



	public String getWorkCity() {
		return workCity;
	}



	public void setWorkCity(String workCity) {
		this.workCity = workCity;
	}



	public String getWorkCountry() {
		return workCountry;
	}



	public void setWorkCountry(String workCountry) {
		this.workCountry = workCountry;
	}



	public String getWorkFax() {
		return workFax;
	}



	public void setWorkFax(String workFax) {
		this.workFax = workFax;
	}



	public String getWorkLabel() {
		return workLabel;
	}



	public void setWorkLabel(String workLabel) {
		this.workLabel = workLabel;
	}



	public String getWorkPhone() {
		return workPhone;
	}



	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}



	public String getWorkState() {
		return workState;
	}



	public void setWorkState(String workState) {
		this.workState = workState;
	}



	public String getWorkStateOtherCountryText() {
		return workStateOtherCountryText;
	}



	public void setWorkStateOtherCountryText(String workStateOtherCountryText) {
		this.workStateOtherCountryText = workStateOtherCountryText;
	}



	public String getWorkZip() {
		return workZip;
	}



	public void setWorkZip(String workZip) {
		this.workZip = workZip;
	}



	public String getHomeContactEmail() {
		return homeContactEmail;
	}



	public void setHomeContactEmail(String homeContactEmail) {
		this.homeContactEmail = homeContactEmail;
	}



	public String getHomeContactName() {
		return homeContactName;
	}



	public void setHomeContactName(String homeContactName) {
		this.homeContactName = homeContactName;
	}



	public String getHomeContactPhone() {
		return homeContactPhone;
	}



	public void setHomeContactPhone(String homeContactPhone) {
		this.homeContactPhone = homeContactPhone;
	}



	public String getWorkContactEmail() {
		return workContactEmail;
	}



	public void setWorkContactEmail(String workContactEmail) {
		this.workContactEmail = workContactEmail;
	}



	public String getWorkContactName() {
		return workContactName;
	}



	public void setWorkContactName(String workContactName) {
		this.workContactName = workContactName;
	}



	public String getWorkContactPhone() {
		return workContactPhone;
	}



	public void setWorkContactPhone(String workContactPhone) {
		this.workContactPhone = workContactPhone;
	}



	/**
	 * @return the billingFirstName
	 */
	public String getBillingFirstName() {
		return billingFirstName;
	}



	/**
	 * @param billingFirstName the billingFirstName to set
	 */
	public void setBillingFirstName(String billingFirstName) {
		this.billingFirstName = billingFirstName;
	}



	/**
	 * @return the billingLastName
	 */
	public String getBillingLastName() {
		return billingLastName;
	}



	/**
	 * @param billingLastName the billingLastName to set
	 */
	public void setBillingLastName(String billingLastName) {
		this.billingLastName = billingLastName;
	}



	/**
	 * @return the workFirstName
	 */
	public String getWorkFirstName() {
		return workFirstName;
	}



	/**
	 * @param workFirstName the workFirstName to set
	 */
	public void setWorkFirstName(String workFirstName) {
		this.workFirstName = workFirstName;
	}



	/**
	 * @return the workLastName
	 */
	public String getWorkLastName() {
		return workLastName;
	}



	/**
	 * @param workLastName the workLastName to set
	 */
	public void setWorkLastName(String workLastName) {
		this.workLastName = workLastName;
	}



	/**
	 * @return the homeFirstName
	 */
	public String getHomeFirstName() {
		return homeFirstName;
	}



	/**
	 * @param homeFirstName the homeFirstName to set
	 */
	public void setHomeFirstName(String homeFirstName) {
		this.homeFirstName = homeFirstName;
	}



	/**
	 * @return the homeLastName
	 */
	public String getHomeLastName() {
		return homeLastName;
	}



	/**
	 * @param homeLastName the homeLastName to set
	 */
	public void setHomeLastName(String homeLastName) {
		this.homeLastName = homeLastName;
	}



	public String getBillingCompany() {
		return billingCompany;
	}



	public void setBillingCompany(String billingCompany) {
		this.billingCompany = billingCompany;
	}



	public String getShippingCompany() {
		return shippingCompany;
	}



	public void setShippingCompany(String shippingCompany) {
		this.shippingCompany = shippingCompany;
	}



	public String getShippingFirstName() {
		return shippingFirstName;
	}



	public void setShippingFirstName(String shippingFirstName) {
		this.shippingFirstName = shippingFirstName;
	}



	public String getShippingLastName() {
		return shippingLastName;
	}



	public void setShippingLastName(String shippingLastName) {
		this.shippingLastName = shippingLastName;
	}
	
	
}

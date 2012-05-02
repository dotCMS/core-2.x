package com.dotmarketing.portlets.order_manager.model;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class Order extends Inode {

	private static final long serialVersionUID = 1L;
	private String inode; //identifier
    private String userInode; //persistent
    private int orderStatus; //persistent
    private int paymentStatus; //persistent
    private java.util.Date datePosted; //persistent
    private java.util.Date lastModDate; //persistent
    private String billingAddress1; //persistent
    private String billingAddress2; //persistent
    private String billingCity; //persistent
    private String billingState; //persistent
    private String billingZip; //persistent
    private String billingCountry; //persistent
    private String billingPhone; //persistent
    private String billingFax; //persistent
	private String billingFirstName; //persistent
	private String billingLastName; //persistent
    private String billingContactName; //persistent
    private String billingContactPhone; //persistent
    private String billingContactEmail; //persistent
    private String shippingAddress1; //persistent
    private String shippingLabel; //persistent
    private String shippingAddress2; //persistent
    private String shippingCity; //persistent
    private String shippingState; //persistent
    private String shippingZip; //persistent
    private String shippingCountry; //persistent
    private String shippingPhone; //persistent
    private String shippingFax; //persistent
    private String paymentType; //persistent
    private String nameOnCard; //persistent
    private String cardType; //persistent
    private String cardNumber; //persistent
    private int cardExpMonth; //persistent
    private int cardExpYear; //persistent
    private String cardVerificationValue; //persistent
    private float orderSubTotal; //persistent
    private float orderShipping; //persistent
    private int orderShipType; //persistent
    private float orderTax; //persistent
    private String taxExemptNumber; //persistent
    private String discountCodes; //persistent
    private float orderTotal; //persistent
    private float orderTotalPaid; //persistent
    private float orderTotalDue; //persistent
    private float orderDiscount; //persistent
    private String invoiceNumber; //persistent
    private java.util.Date invoiceDate; //persistent
    private String checkNumber; //persistent
    private String checkBankName; //persistent
    private String poNumber; //persistent
    private String trackingNumber; //persistent
    private boolean modified_QB; // persistent
    private boolean modified_FH; // persistent
    private String backendUser; //persistent

    public Order() {
    	setType("ecom_order");
    	this.modified_QB = true;
    	this.modified_FH = true;
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
        return this.shippingState;
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
        this.paymentType = paymentType;
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
    public String getTaxExemptNumber() {
        return this.taxExemptNumber;
    }

    public void setTaxExemptNumber(String taxExemptNumber) {
        this.taxExemptNumber = taxExemptNumber;
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
	public float getOrderDiscount() {
		return orderDiscount;
	}
	public void setOrderDiscount(float orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public User getUser() throws Exception
	{
		User user = APILocator.getUserAPI().getDefaultUser();
		UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userInode,APILocator.getUserAPI().getSystemUser(), false);
		String userId = userProxy.getUserId();
		if (UtilMethods.isSet(userId))
		try{
			
			return APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);		
		}
		catch(Exception e)
		{
			Logger.error(this.getClass(), "User Not Found");
			Logger.debug(this.getClass(), "User Not Found", e);
		}
		return user;		 
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

	public String getBackendUser() {
		return backendUser;
	}

	public void setBackendUser(String backendUser) {
		this.backendUser = backendUser;
	}

	public String getShippingLabel() {
		return shippingLabel;
	}

	public void setShippingLabel(String shippingLabel) {
		this.shippingLabel = shippingLabel;
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
	
}

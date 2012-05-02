package com.dotmarketing.portlets.event_registrations.model;

import com.dotmarketing.beans.Inode;

public class WebEventRegistration extends Inode {

	private static final long serialVersionUID = 1L;
	
    private String eventInode; //persistent
    private String eventLocationInode; //persistent
    private String userInode; //persistent
    private int registrationStatus; //persistent
    private java.util.Date datePosted; //persistent
    private java.util.Date lastModDate; //persistent
    private float totalPaid; //persistent
    private float totalDue; //persistent
    private float totalRegistration;
    private int paymentType; //persistent
    private String billingAddress1; //persistent
    private String billingAddress2; //persistent
    private String billingCity; //persistent
    private String billingState; //persistent
    private String billingZip; //persistent
	private String billingCountry; // persistent
    private String billingContactName; //persistent
    private String billingContactPhone; //persistent
    private String billingContactEmail; //persistent
    private String cardName; //persistent
    private String cardType; //persistent
    private String cardNumber; //persistent
    private String cardExpMonth;
    private String cardExpYear;
    private String cardVerificationValue; //persistent
    private String checkNumber; //persistent
    private String checkBankName; //persistent
    private String poNumber; //persistent
    private String invoiceNumber; //persistent
    private boolean badgePrinted; //persistent
    private String howDidYouHear; //persistent
    private String ceoName; //persistent
    private boolean modified_QB; // persistent
    private boolean reminderEmailSent;
    private boolean postEmailSent;

    public WebEventRegistration(String eventInode, String eventLocationInode, String userInode, int registrationStatus, java.util.Date datePosted, java.util.Date lastModDate, int totalPaid, int totalDue, int paymentType, String billingAddress1, String billingAddress2, String billingCity, String billingState, String billingZip, String billingContactName, String billingContactPhone, String billingContactEmail, String cardName, String cardType, String cardNumber, String cardVerificationValue, String checkNumber, String checkBankName, String poNumber, String invoiceNumber, boolean badgePrinted, String howDidYouHear, String ceoName) {
        this.eventInode = eventInode;
        this.eventLocationInode = eventLocationInode;
        this.userInode = userInode;
        this.registrationStatus = registrationStatus;
        this.datePosted = datePosted;
        this.lastModDate = lastModDate;
        this.totalPaid = totalPaid;
        this.totalDue = totalDue;
        this.paymentType = paymentType;
        this.billingAddress1 = billingAddress1;
        this.billingAddress2 = billingAddress2;
        this.billingCity = billingCity;
        this.billingState = billingState;
        this.billingZip = billingZip;
        this.billingContactName = billingContactName;
        this.billingContactPhone = billingContactPhone;
        this.billingContactEmail = billingContactEmail;
        this.cardName = cardName;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardVerificationValue = cardVerificationValue;
        this.checkNumber = checkNumber;
        this.checkBankName = checkBankName;
        this.poNumber = poNumber;
        this.invoiceNumber = invoiceNumber;
        this.badgePrinted = badgePrinted;
        this.howDidYouHear = howDidYouHear;
        this.ceoName = ceoName;
        this.modified_QB = true;
    }

    public WebEventRegistration() {
    	this.modified_QB = true;
    	setType("web_event_registration");
    }

    public WebEventRegistration(String eventInode, String eventLocationInode, String userInode) {
        this.eventInode = eventInode;
        this.eventLocationInode = eventLocationInode;
        this.userInode = userInode;
        this.modified_QB = true;
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
    public int getRegistrationStatus() {
        return this.registrationStatus;
    }

    public void setRegistrationStatus(int registrationStatus) {
        this.registrationStatus = registrationStatus;
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
    public int getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
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
	 * @param cardExpMonth The cardExpMonth to set.
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
	 * @param cardExpYear The cardExpYear to set.
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
	 * @param badgePrinted The badgePrinted to set.
	 */
	public void setBadgePrinted(boolean badgePrinted) {
		this.badgePrinted = badgePrinted;
	}

	/**
	 * @return Returns the totalRegistration.
	 */
	public float getTotalRegistration() {
		return totalRegistration;
	}

	/**
	 * @param totalRegistration The totalRegistration to set.
	 */
	public void setTotalRegistration(float totalRegistration) {
		this.totalRegistration = totalRegistration;
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
	 * @return Returns the postEmailSent.
	 */
	public boolean isPostEmailSent() {
		return postEmailSent;
	}

	/**
	 * @param postEmailSent The postEmailSent to set.
	 */
	public void setPostEmailSent(boolean postEmailSent) {
		this.postEmailSent = postEmailSent;
	}

	/**
	 * @return Returns the reminderEmailSent.
	 */
	public boolean isReminderEmailSent() {
		return reminderEmailSent;
	}

	/**
	 * @param reminderEmailSent The reminderEmailSent to set.
	 */
	public void setReminderEmailSent(boolean reminderEmailSent) {
		this.reminderEmailSent = reminderEmailSent;
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
    
	
	
}

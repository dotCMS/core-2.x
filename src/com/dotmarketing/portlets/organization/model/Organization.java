package com.dotmarketing.portlets.organization.model;

import com.dotmarketing.beans.Inode;

public class Organization extends Inode {

	
	private static final long serialVersionUID = 1L;

	private String title; //persistent
    private String ceoName; //persistent
    private String partnerUrl; //persistent
    private String partnerKey; //persistent
    private String partnerLogo; //persistent
    private String street1; //persistent
    private String street2; //persistent
    private String city; //persistent
    private String state; //persistent
    private String zip; //persistent
    private String phone; //persistent
    private String fax; //persistent
    private String country; //persistent
    private boolean system; //persistent
    private String parentOrganization;
    private float institute_price;
    
    /**
	 * @return Returns the institute_price.
	 */
	public float getInstitute_price() {
		return institute_price;
	}

	/**
	 * @param institute_price The institute_price to set.
	 */
	public void setInstitute_price(float institute_price) {
		this.institute_price = institute_price;
	}

	public Organization(String title, String ceoName, String partnerUrl, String partnerKey, String partnerLogo, String street1, String street2, String city, String state, String zip, String phone, String fax, String country, boolean system) {
        this.title = title;
        this.ceoName = ceoName;
        this.partnerUrl = partnerUrl;
        this.partnerKey = partnerKey;
        this.partnerLogo = partnerLogo;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.fax = fax;
        this.country = country;
        this.system = system;
    	super.setType("organization");
    }

    public Organization() {
    	super.setType("organization");
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getCeoName() {
        return this.ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }
    public String getPartnerUrl() {
        return this.partnerUrl;
    }

    public void setPartnerUrl(String partnerUrl) {
        this.partnerUrl = partnerUrl;
    }
    public String getPartnerKey() {
        return this.partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }
    public String getPartnerLogo() {
        return this.partnerLogo;
    }

    public void setPartnerLogo(String partnerLogo) {
        this.partnerLogo = partnerLogo;
    }
    public String getStreet1() {
        return this.street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    public String getStreet2() {
        return this.street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	/**
	 * @return Returns the isSystem.
	 */
	public boolean isSystem() {
		return system;
	}
	
	/**
	 * @param isSystem The isSystem to set.
	 */
	public void setSystem(boolean system) {
		this.system = system;
	}
	
	
	
	/**
	 * @return Returns the parentOrganization.
	 */
	public String getParentOrganization() {
		return parentOrganization;
	}
	/**
	 * @param parentOrganization The parentOrganization to set.
	 */
	public void setParentOrganization(String parentOrganization) {
		this.parentOrganization = parentOrganization;
	}
}

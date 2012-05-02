package com.dotmarketing.portlets.jobs.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.dotmarketing.util.InodeUtils;

/** @author Hibernate CodeGenerator */
public class Searchfirm extends com.dotmarketing.beans.Inode implements Serializable {

    /** nullable persistent field */
    private String name;
    
    private String organization;

    /** nullable persistent field */
    private String title;

    /** nullable persistent field */
    private String streetaddress1;

    /** nullable persistent field */
    private String streetaddress2;

    /** nullable persistent field */
    private String phone;

    /** nullable persistent field */
    private String fax;

    /** nullable persistent field */
    private String email;

    /** nullable persistent field */
    private String url;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String contactinfo;

    /** nullable persistent field */
    private String cctype;

    /** nullable persistent field */
    private String ccnum;

    /** nullable persistent field */
    private String ccexp;

    /** persistent field */
    private java.util.Date creationdate;

    /** nullable persistent field */
    private java.util.Date expirationdate;

    /** persistent field */
    private boolean active;
    private boolean linking;
    

    /** default constructor */
    public Searchfirm() {
    	setType("search_firm");
        this.creationdate = new java.util.Date();
    }

    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.lang.String getTitle() {
        return this.title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    public java.lang.String getStreetaddress1() {
        return this.streetaddress1;
    }

    public void setStreetaddress1(java.lang.String streetaddress1) {
        this.streetaddress1 = streetaddress1;
    }
    public java.lang.String getStreetaddress2() {
        return this.streetaddress2;
    }

    public void setStreetaddress2(java.lang.String streetaddress2) {
        this.streetaddress2 = streetaddress2;
    }
    public java.lang.String getPhone() {
        return this.phone;
    }

    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }
    public java.lang.String getFax() {
        return this.fax;
    }

    public void setFax(java.lang.String fax) {
        this.fax = fax;
    }
    public java.lang.String getEmail() {
        return this.email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }
    public java.lang.String getUrl() {
        return this.url;
    }

    public void setUrl(java.lang.String url) {
        this.url = url;
    }
    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public java.lang.String getContactinfo() {
        return this.contactinfo;
    }

    public void setContactinfo(java.lang.String contactinfo) {
        this.contactinfo = contactinfo;
    }
    public java.lang.String getCctype() {
        return this.cctype;
    }

    public void setCctype(java.lang.String cctype) {
        this.cctype = cctype;
    }
    public java.lang.String getCcnum() {
        return this.ccnum;
    }

    public void setCcnum(java.lang.String ccnum) {
        this.ccnum = ccnum;
    }
    public java.lang.String getCcexp() {
        return this.ccexp;
    }

    public void setCcexp(java.lang.String ccexp) {
        this.ccexp = ccexp;
    }
    public java.util.Date getCreationdate() {
        return this.creationdate;
    }

    public void setCreationdate(java.util.Date creationdate) {
        this.creationdate = creationdate;
    }
    public java.util.Date getExpirationdate() {
        return this.expirationdate;
    }

    public void setExpirationdate(java.util.Date expirationdate) {
        this.expirationdate = expirationdate;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	/**
	 * Returns the organization.
	 * @return String
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Sets the organization.
	 * @param organization The organization to set
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}


	/**
	 * @return Returns the active.
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active The active to set.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @return Returns the linking.
	 */
	public boolean isLinking() {
		return linking;
	}
	/**
	 * @param linking The linking to set.
	 */
	public void setLinking(boolean linking) {
		this.linking = linking;
	}
}

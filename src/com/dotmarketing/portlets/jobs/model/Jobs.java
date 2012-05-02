package com.dotmarketing.portlets.jobs.model;

import java.io.Serializable;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;

/** @author Hibernate CodeGenerator */
public class Jobs extends Inode implements Serializable {

    /** persistent field */
    private String organization;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String title;

    /** persistent field */
    private String streetaddress1;

    /** nullable persistent field */
    private String streetaddress2;

    /** persistent field */
    private String city;

    /** nullable persistent field */
    private String state;

    /** nullable persistent field */
    private String zip;

    /** nullable persistent field */
    private String phone;

    /** nullable persistent field */
    private String fax;

    /** nullable persistent field */
    private String email;

    /** persistent field */
    private String jobtitle;

    /** nullable persistent field */
    private String joblocation;

    /** nullable persistent field */
    private String salary;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String requirements;

    /** nullable persistent field */
    private String contactinfo;

    /** nullable persistent field */
    private String cctype;

    /** nullable persistent field */
    private String ccnum;

    /** nullable persistent field */
    private String ccexp;

    /** nullable persistent field */
    private java.util.Date expdate;

    /** persistent field */
    private java.util.Date entrydate;
    
    private boolean active;
    
    private boolean premiumlisting;
    
    private boolean blind;
    
    /** default constructor */
    public Jobs() {
    	setType("jobs");
    	this.entrydate = new java.util.Date();
    	this.cctype = "";
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
	 * @return Returns the blind.
	 */
	public boolean isBlind() {
		return blind;
	}
	/**
	 * @param blind The blind to set.
	 */
	public void setBlind(boolean blind) {
		this.blind = blind;
	}
	/**
	 * @return Returns the ccexp.
	 */
	public String getCcexp() {
		return ccexp;
	}
	/**
	 * @param ccexp The ccexp to set.
	 */
	public void setCcexp(String ccexp) {
		this.ccexp = ccexp;
	}
	/**
	 * @return Returns the ccnum.
	 */
	public String getCcnum() {
		return ccnum;
	}
	/**
	 * @param ccnum The ccnum to set.
	 */
	public void setCcnum(String ccnum) {
		this.ccnum = ccnum;
	}
	/**
	 * @return Returns the cctype.
	 */
	public String getCctype() {
		return cctype;
	}
	/**
	 * @param cctype The cctype to set.
	 */
	public void setCctype(String cctype) {
		this.cctype = cctype;
	}
	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return Returns the contactinfo.
	 */
	public String getContactinfo() {
		return contactinfo;
	}
	/**
	 * @param contactinfo The contactinfo to set.
	 */
	public void setContactinfo(String contactinfo) {
		this.contactinfo = contactinfo;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the entrydate.
	 */
	public java.util.Date getEntrydate() {
		return entrydate;
	}
	/**
	 * @param entrydate The entrydate to set.
	 */
	public void setEntrydate(java.util.Date entrydate) {
		this.entrydate = entrydate;
	}
	/**
	 * @return Returns the expdate.
	 */
	public java.util.Date getExpdate() {
		return expdate;
	}
	/**
	 * @param expdate The expdate to set.
	 */
	public void setExpdate(java.util.Date expdate) {
		this.expdate = expdate;
	}
	/**
	 * @return Returns the fax.
	 */
	public String getFax() {
		return fax;
	}
	/**
	 * @param fax The fax to set.
	 */
	public void setFax(String fax) {
		this.fax = fax;
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
	 * @param inode The inode to set.
	 */
	public void setInode(String inode) {
		this.inode = inode;
	}
	/**
	 * @return Returns the joblocation.
	 */
	public String getJoblocation() {
		return joblocation;
	}
	/**
	 * @param joblocation The joblocation to set.
	 */
	public void setJoblocation(String joblocation) {
		this.joblocation = joblocation;
	}
	/**
	 * @return Returns the jobtitle.
	 */
	public String getJobtitle() {
		return jobtitle;
	}
	/**
	 * @param jobtitle The jobtitle to set.
	 */
	public void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the organization.
	 */
	public String getOrganization() {
		return organization;
	}
	/**
	 * @param organization The organization to set.
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	/**
	 * @return Returns the phone.
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone The phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return Returns the premiumlisting.
	 */
	public boolean isPremiumlisting() {
		return premiumlisting;
	}
	/**
	 * @param premiumlisting The premiumlisting to set.
	 */
	public void setPremiumlisting(boolean premiumlisting) {
		this.premiumlisting = premiumlisting;
	}
	/**
	 * @return Returns the requirements.
	 */
	public String getRequirements() {
		return requirements;
	}
	/**
	 * @param requirements The requirements to set.
	 */
	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	/**
	 * @return Returns the salary.
	 */
	public String getSalary() {
		return salary;
	}
	/**
	 * @param salary The salary to set.
	 */
	public void setSalary(String salary) {
		this.salary = salary;
	}
	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return Returns the streetaddress1.
	 */
	public String getStreetaddress1() {
		return streetaddress1;
	}
	/**
	 * @param streetaddress1 The streetaddress1 to set.
	 */
	public void setStreetaddress1(String streetaddress1) {
		this.streetaddress1 = streetaddress1;
	}
	/**
	 * @return Returns the streetaddress2.
	 */
	public String getStreetaddress2() {
		return streetaddress2;
	}
	/**
	 * @param streetaddress2 The streetaddress2 to set.
	 */
	public void setStreetaddress2(String streetaddress2) {
		this.streetaddress2 = streetaddress2;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the zip.
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * @param zip The zip to set.
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}


}

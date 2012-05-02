package com.dotmarketing.portlets.events.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.dotmarketing.beans.Inode;

/** @author Hibernate CodeGenerator */
public class Event extends Inode implements Serializable, Comparable{

    /** identifier field */
    //private long inode;

    /** nullable persistent field */
    private String title;

    /** nullable persistent field */
    private String subtitle;

    /** nullable persistent field */
    private java.util.Date startDate;

    /** nullable persistent field */
    private java.util.Date endDate;

    /** nullable persistent field */
    private String location;

    /** nullable persistent field */
    private String address1;

    /** nullable persistent field */
    private String address2;

    /** nullable persistent field */
    private String address3;

    /** nullable persistent field */
    private String city;

    /** nullable persistent field */
    private String state;

    /** nullable persistent field */
    private String zip;

    /** nullable persistent field */
    private String country;

    /** nullable persistent field */
    private String email;

    /** nullable persistent field */
    private String phone;

    /** nullable persistent field */
    private String fax;

    /** nullable persistent field */
    private String url;

    /** nullable persistent field */
    private boolean registration;

    /** nullable persistent field */
    private String includeFile;

    /** nullable persistent field */
    private boolean showPublic;

    /** nullable persistent field */
    private String contactName;

    /** nullable persistent field */
    private String contactCompany;

    /** nullable persistent field */
    private String contactPhone;

    /** nullable persistent field */
    private String contactEmail;

    /** nullable persistent field */
    private String contactFax;

    /** nullable persistent field */
    private String directions;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String emailResponse;
    
    /** nullable persistent field */
    private String webAddress;

    /** nullable persistent field */
    private Date setupDate;

    /** nullable persistent field */
    private Date breakDate;

    /** nullable persistent field */
    private int approvalStatus;

    /** nullable persistent field */
    private String commentsEquipment;

    /** nullable persistent field */
    private boolean receivedAdminApproval;

    private String userId;

	private boolean timeTBD;
	private boolean featured;
    public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	/** default constructor */
    public Event() {
        super.setType("event");
        startDate = new java.util.Date();
        endDate = new java.util.Date();
        setupDate = new java.util.Date();
        breakDate = new java.util.Date();
        showPublic = false;
    }

    public java.lang.String getTitle() {
        return this.title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    public java.util.Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public java.lang.String getLocation() {
        return this.location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    public java.lang.String getAddress1() {
        return this.address1;
    }

    public void setAddress1(java.lang.String address1) {
        this.address1 = address1;
    }

    public java.lang.String getAddress2() {
        return this.address2;
    }

    public void setAddress2(java.lang.String address2) {
        this.address2 = address2;
    }

    public java.lang.String getAddress3() {
        return this.address3;
    }

    public void setAddress3(java.lang.String address3) {
        this.address3 = address3;
    }

    public java.lang.String getCity() {
        return this.city;
    }

    public void setCity(java.lang.String city) {
        this.city = city;
    }

    public java.lang.String getState() {
        return this.state;
    }

    public void setState(java.lang.String state) {
        this.state = state;
    }

    public java.lang.String getZip() {
        return this.zip;
    }

    public void setZip(java.lang.String zip) {
        this.zip = zip;
    }

    public java.lang.String getCountry() {
        return this.country;
    }

    public void setCountry(java.lang.String country) {
        this.country = country;
    }

    public java.lang.String getEmail() {
        return this.email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
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

    public java.lang.String getUrl() {
        return this.url;
    }

    public void setUrl(java.lang.String url) {
        this.url = url;
    }



    public java.lang.String getIncludeFile() {
        return this.includeFile;
    }

    public void setIncludeFile(java.lang.String includeFile) {
        this.includeFile = includeFile;
    }

    public boolean getShowPublic() {
        return this.showPublic;
    }

    public void setShowPublic(boolean showPublic) {
        this.showPublic = showPublic;
    }

    public java.lang.String getContactName() {
        return this.contactName;
    }

    public void setContactName(java.lang.String contactName) {
        this.contactName = contactName;
    }

    public java.lang.String getContactCompany() {
        return this.contactCompany;
    }

    public void setContactCompany(java.lang.String contactCompany) {
        this.contactCompany = contactCompany;
    }

    public java.lang.String getContactPhone() {
        return this.contactPhone;
    }

    public void setContactPhone(java.lang.String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public java.lang.String getContactEmail() {
        return this.contactEmail;
    }

    public void setContactEmail(java.lang.String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public java.lang.String getContactFax() {
        return this.contactFax;
    }

    public void setContactFax(java.lang.String contactFax) {
        this.contactFax = contactFax;
    }

    public java.lang.String getDirections() {
        return this.directions;
    }

    public void setDirections(java.lang.String directions) {
        this.directions = directions;
    }

    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public java.lang.String getEmailResponse() {
        return this.emailResponse;
    }

    public void setEmailResponse(java.lang.String emailResponse) {
        this.emailResponse = emailResponse;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("inode", getInode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( !(other instanceof Event) ) return false;
        Event castOther = (Event) other;
        return new EqualsBuilder()
            .append(this.getInode(), castOther.getInode())
            .isEquals();
    }

    public int compareTo(Object other) {
        if ( !(other instanceof Event) ) return 0;
        Event castOther = (Event) other;


       if(this.getStartDate().before(castOther.getStartDate())) return -1;
       if(this.getStartDate().after(castOther.getStartDate())) return 0;

        return 0;

    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getInode())
            .toHashCode();
    }

	/**
	 * Returns the showPublic.
	 * @return boolean
	 */
	public boolean isShowPublic() {
		return showPublic;
	}

	/**
	 * Returns the registration.
	 * @return boolean
	 */
	public boolean isRegistration() {
		return registration;
	}

	/**
	 * Sets the registration.
	 * @param registration The registration to set
	 */
	public void setRegistration(boolean registration) {
		this.registration = registration;
	}

	/**
	 * Returns the webAddress.
	 * @return String
	 */
	public String getWebAddress() {
		return webAddress;
	}

	/**
	 * Sets the webAddress.
	 * @param webAddress The webAddress to set
	 */
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
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
	/**
	 * @return Returns the subtitle.
	 */
	public String getSubtitle() {
		return subtitle;
	}
	/**
	 * @param subtitle The subtitle to set.
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
    public int getApprovalStatus() {
        return approvalStatus;
    }
    public void setApprovalStatus(int approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    public Date getBreakDate() {
        return breakDate;
    }
    public void setBreakDate(Date breakDate) {
        this.breakDate = breakDate;
    }
    public String getCommentsEquipment() {
        return commentsEquipment;
    }
    public void setCommentsEquipment(String commentsEquipment) {
        this.commentsEquipment = commentsEquipment;
    }
    public boolean isReceivedAdminApproval() {
        return receivedAdminApproval;
    }
    public void setReceivedAdminApproval(boolean receivedAdminApproval) {
        this.receivedAdminApproval = receivedAdminApproval;
    }
    public Date getSetupDate() {
        return setupDate;
    }
    public void setSetupDate(Date setupDate) {
        this.setupDate = setupDate;
    }
    public boolean isTimeTBD() {
        return timeTBD;
    }
    public void setTimeTBD(boolean timeTBD) {
        this.timeTBD = timeTBD;
    }
}

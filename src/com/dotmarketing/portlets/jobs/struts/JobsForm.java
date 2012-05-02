package com.dotmarketing.portlets.jobs.struts;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.liferay.portal.util.Constants;

/** @author Hibernate CodeGenerator */
public class JobsForm extends ValidatorForm implements Serializable {

    /** persistent field */
    private String inode;

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
    
    
    private java.util.List paymentmethods;
    
    private String webexpirationdate;
    
    private java.util.Date expdate;
    
    private boolean active;
    
    private boolean premiumlisting;
    
    private boolean blind;

    /** persistent field */
   // private java.util.Date entrydate;

    /** full constructor */

    /** default constructor */
    public JobsForm() {
    	paymentmethods = new java.util.ArrayList();
	    paymentmethods.add("Invoice Me");
	    paymentmethods.add("Visa");
	    paymentmethods.add("Master Card");
	    paymentmethods.add("American Express");
	    //expdate = new java.util.Date();
	    this.cctype = "";
    }


    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public java.lang.String getOrganization() {
        return this.organization;
    }

    public void setOrganization(java.lang.String organization) {
        this.organization = organization;
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
    public java.lang.String getJobtitle() {
        return this.jobtitle;
    }

    public void setJobtitle(java.lang.String jobtitle) {
        this.jobtitle = jobtitle;
    }
    public java.lang.String getJoblocation() {
        return this.joblocation;
    }

    public void setJoblocation(java.lang.String joblocation) {
        this.joblocation = joblocation;
    }
    public java.lang.String getSalary() {
        return this.salary;
    }

    public void setSalary(java.lang.String salary) {
        this.salary = salary;
    }
    public java.lang.String getDescription() {
        return this.description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    public java.lang.String getRequirements() {
        return this.requirements;
    }

    public void setRequirements(java.lang.String requirements) {
        this.requirements = requirements;
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

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	/**
	 * Returns the webexpirationdate.
	 * @return String
	 */
	public String getWebexpirationdate() {
		if(expdate!=null) 
		 			this.webexpirationdate = new SimpleDateFormat("MM/dd/yyyy").format(this.expdate);
		return webexpirationdate;
	}

	/**
	 * Sets the webexpirationdate.
	 * @param webexpirationdate The webexpirationdate to set
	 */
	public void setWebexpirationdate(String webexpirationdate) {
		this.webexpirationdate = webexpirationdate;
		try {
			expdate = new SimpleDateFormat("MM/dd/yyyy").parse(webexpirationdate);
		} catch(java.text.ParseException ex) {
		}
	}

	/**
	 * Returns the expirationdate.
	 * @return java.util.Date
	 */
	public java.util.Date getExpdate() {
		return expdate;
	}

	/**
	 * Sets the expirationdate.
	 * @param expirationdate The expirationdate to set
	 */
	public void setExpdate(java.util.Date expirationdate) {
		this.expdate = expirationdate;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (request.getParameter("cmd")!=null && 
        	((request.getParameter("cmd").equalsIgnoreCase(Constants.ADD)) || 
        		(request.getParameter("cmd").equalsIgnoreCase(Constants.UPDATE)))) {
            Logger.debug(this, "Jobs Form validation!!!!!!" + mapping.getForward());
            return super.validate(mapping, request);
        }
        if (request.getParameter("dispatch")!=null && (request.getParameter("dispatch").equalsIgnoreCase("save"))) {
            Logger.debug(this, "Jobs Form validation!!!!!!" + mapping.getForward());
            return super.validate(mapping, request);
        }
        return null;
    }

	/**
	 * Returns the paymentmethods.
	 * @return java.util.List
	 */
	public java.util.List getPaymentmethods() {
		return paymentmethods;
	}

	/**
	 * Sets the paymentmethods.
	 * @param paymentmethods The paymentmethods to set
	 */
	public void setPaymentmethods(java.util.List paymentmethods) {
		this.paymentmethods = paymentmethods;
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
}

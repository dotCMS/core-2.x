package com.dotmarketing.portlets.jobs.struts;

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
public class ResumeForm extends ValidatorForm {

    /** persistent field */
    private String inode;

    /** nullable persistent field */
    private boolean member;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String streetname1;

    /** nullable persistent field */
    private String streetname2;

    /** nullable persistent field */
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

    /** nullable persistent field */
    private String exclusion1;

    /** nullable persistent field */
    private String exclusion2;

    /** nullable persistent field */
    private String exclusion3;
    
    private String exclusion4;

    /** nullable persistent field */
    private boolean verified;

    /** nullable persistent field */
    private String objective;

    /** nullable persistent field */
    private String location;

    /** nullable persistent field */
    private String salary;

    /** nullable persistent field */
    private String qualification;

    /** persistent field */
   // private java.util.Date creationdate;
    
    private String webexpirationdate;

    /** nullable persistent field */
    private java.util.Date expirationdate;
    
    /** nullable persistent field */
    private String cctype;

    /** nullable persistent field */
    private String ccnum;

    /** nullable persistent field */
    private String ccexp;
    
    private boolean active;
    
    static final int MAX_FILE_SIZE = 2000 * 1024; //200k Max

    /** default constructor */
    public ResumeForm() {
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

	/**
	 * @return Returns the member.
	 */
	public boolean isMember() {
		return member;
	}
	/**
	 * @param member The member to set.
	 */
	public void setMember(boolean member) {
		this.member = member;
	}
    public java.lang.String getName() {
        return this.name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
    public java.lang.String getStreetname1() {
        return this.streetname1;
    }

    public void setStreetname1(java.lang.String streetname1) {
        this.streetname1 = streetname1;
    }
    public java.lang.String getStreetname2() {
        return this.streetname2;
    }

    public void setStreetname2(java.lang.String streetname2) {
        this.streetname2 = streetname2;
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
    public java.lang.String getExclusion1() {
        return this.exclusion1;
    }

    public void setExclusion1(java.lang.String exclusion1) {
        this.exclusion1 = exclusion1;
    }
    public java.lang.String getExclusion2() {
        return this.exclusion2;
    }

    public void setExclusion2(java.lang.String exclusion2) {
        this.exclusion2 = exclusion2;
    }
    public java.lang.String getExclusion3() {
        return this.exclusion3;
    }

    public void setExclusion3(java.lang.String exclusion3) {
        this.exclusion3 = exclusion3;
    }
    public boolean getVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public java.lang.String getObjective() {
        return this.objective;
    }

    public void setObjective(java.lang.String objective) {
        this.objective = objective;
    }
    public java.lang.String getLocation() {
        return this.location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }
    public java.lang.String getSalary() {
        return this.salary;
    }

    public void setSalary(java.lang.String salary) {
        this.salary = salary;
    }
    public java.lang.String getQualification() {
        return this.qualification;
    }

    public void setQualification(java.lang.String qualification) {
        this.qualification = qualification;
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
	 * Returns the exclusion4.
	 * @return String
	 */
	public String getExclusion4() {
		return exclusion4;
	}

	/**
	 * Sets the exclusion4.
	 * @param exclusion4 The exclusion4 to set
	 */
	public void setExclusion4(String exclusion4) {
		this.exclusion4 = exclusion4;
	}

	/**
	 * Returns the ccexp.
	 * @return String
	 */
	public String getCcexp() {
		return ccexp;
	}

	/**
	 * Returns the ccnum.
	 * @return String
	 */
	public String getCcnum() {
		return ccnum;
	}

	/**
	 * Returns the cctype.
	 * @return String
	 */
	public String getCctype() {
		return cctype;
	}


	/**
	 * Returns the verified.
	 * @return boolean
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * Sets the ccexp.
	 * @param ccexp The ccexp to set
	 */
	public void setCcexp(String ccexp) {
		this.ccexp = ccexp;
	}

	/**
	 * Sets the ccnum.
	 * @param ccnum The ccnum to set
	 */
	public void setCcnum(String ccnum) {
		this.ccnum = ccnum;
	}

	/**
	 * Sets the cctype.
	 * @param cctype The cctype to set
	 */
	public void setCctype(String cctype) {
		this.cctype = cctype;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && 
            	((request.getParameter("cmd").equalsIgnoreCase(Constants.ADD)) || 
            		(request.getParameter("cmd").equalsIgnoreCase(Constants.UPDATE)))) {
            Logger.debug(this, "Resume Form validation!!!!!!");
                return super.validate(mapping, request);
        }
        if (request.getParameter("dispatch")!=null && (request.getParameter("dispatch").equalsIgnoreCase("save"))) {
            return super.validate(mapping, request);
		}
		return null;
	}	


	/**
	 * Returns the webexpirationdate.
	 * @return String
	 */
	public String getWebexpirationdate() {
		if(expirationdate!=null) 
		 			this.webexpirationdate = new SimpleDateFormat("MM/dd/yyyy").format(this.expirationdate);
		return webexpirationdate;
	}

	/**
	 * Sets the webexpirationdate.
	 * @param webexpirationdate The webexpirationdate to set
	 */
	public void setWebexpirationdate(String webexpirationdate) {
		this.webexpirationdate = webexpirationdate;
		try {
			expirationdate = new SimpleDateFormat("MM/dd/yyyy").parse(webexpirationdate);
		} catch(java.text.ParseException ex) {
		}
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
	
}

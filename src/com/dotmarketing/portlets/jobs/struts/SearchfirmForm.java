package com.dotmarketing.portlets.jobs.struts;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.liferay.portal.util.Constants;
/**
 * @author will
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SearchfirmForm extends ValidatorForm  implements Serializable{

     /** persistent field */
    private String inode;

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
    
    /** nullable persistent field */
    private java.util.Date expirationdate;
    
    private String webexpirationdate;    

    /** persistent field */
    private boolean linking;
    
    private boolean active;
    
    /** default constructor */
    public SearchfirmForm() {
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
    
    public java.util.Date getExpirationdate() {
        return this.expirationdate;
    }

    public void setExpirationdate(java.util.Date expirationdate) {
        this.expirationdate = expirationdate;
        if(expirationdate != null)
            this.webexpirationdate = new SimpleDateFormat("MM/dd/yyyy").format(this.expirationdate);        
    }
    
    public String getWebexpirationdate() {
        return this.webexpirationdate;
    }

    public void setWebexpirationdate(String webexpirationdate) {
        this.webexpirationdate = webexpirationdate;
        try {
            this.expirationdate = new SimpleDateFormat().parse(webexpirationdate);
        } catch(java.text.ParseException ex) {
        }
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


	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && 
        	((request.getParameter("cmd").equalsIgnoreCase(Constants.ADD)) || 
        		(request.getParameter("cmd").equalsIgnoreCase(Constants.UPDATE)))) {
            Logger.debug(this, "Search Firm Form validation!!!!!!" + mapping.getForward());
            return super.validate(mapping, request);
        }
        if (request.getParameter("dispatch")!=null && (request.getParameter("dispatch").equalsIgnoreCase("save"))) {
            Logger.debug(this, "Search Firm Form validation!!!!!!" + mapping.getForward());
            return super.validate(mapping, request);
        }
        return null;
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

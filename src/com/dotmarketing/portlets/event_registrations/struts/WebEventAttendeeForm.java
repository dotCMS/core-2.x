package com.dotmarketing.portlets.event_registrations.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.util.Constants;


public class WebEventAttendeeForm extends ValidatorForm {
	private static final long serialVersionUID = 1L;
	
    private String inode; //persistent
    private String eventRegistrationInode; //persistent
    private String firstName; //persistent
    private String lastName; //persistent
    private String badgeName; //persistent
    private String email; //persistent
    private String title; //persistent
    private float registrationPrice; //persistent

    public WebEventAttendeeForm(String eventRegistrationInode, String firstName, String lastName, String badgeName, String email, String title, int registrationPrice) {
        this.eventRegistrationInode = eventRegistrationInode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.badgeName = badgeName;
        this.email = email;
        this.title = title;
        this.registrationPrice = registrationPrice;
    }

    public WebEventAttendeeForm() {
        this.eventRegistrationInode = null;
        this.firstName = "";
        this.lastName = "";
        this.badgeName = "";
        this.email = "";
        this.title = "";
        this.registrationPrice = 0;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }
    public String getEventRegistrationInode() {
        return this.eventRegistrationInode;
    }

    public void setEventRegistrationInode(String eventRegistrationInode) {
        this.eventRegistrationInode = eventRegistrationInode;
    }
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getBadgeName() {
        return this.badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public float getRegistrationPrice() {
        return this.registrationPrice;
    }

    public void setRegistrationPrice(float registrationPrice) {
        this.registrationPrice = registrationPrice;
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebEventAttendeeForm) {
			WebEventAttendeeForm other = (WebEventAttendeeForm) obj;
			if (other.email.toLowerCase().trim().equals(this.email.toLowerCase().trim()) &&
				other.firstName.toLowerCase().trim().equals(this.firstName.toLowerCase().trim()) &&	
				other.lastName.toLowerCase().trim().equals(this.lastName.toLowerCase().trim())) {	
				return true;
			}
		}
		return false;
	}
	
	
    
}

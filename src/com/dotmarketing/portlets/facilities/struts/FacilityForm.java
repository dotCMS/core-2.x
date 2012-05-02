package com.dotmarketing.portlets.facilities.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.util.Constants;

/** @author Hibernate CodeGenerator */
public class FacilityForm extends ValidatorForm {

    /** identifier field */
    private String inode;

    /** nullable persistent field */
    private String facilityName;

    /** nullable persistent field */
    private String facilityDescription;

    /** nullable persistent field */
    private boolean active;

    /** nullable persistent field */
    private int sortOrder;

    /** default constructor */
    public FacilityForm() {
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
	 * @return Returns the facilityDescription.
	 */
	public String getFacilityDescription() {
		return facilityDescription;
	}
	/**
	 * @param facilityDescription The facilityDescription to set.
	 */
	public void setFacilityDescription(String facilityDescription) {
		this.facilityDescription = facilityDescription;
	}
	/**
	 * @return Returns the facilityName.
	 */
	public String getFacilityName() {
		return facilityName;
	}
	/**
	 * @param facilityName The facilityName to set.
	 */
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
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
	 * @return Returns the sortOrder.
	 */
	public int getSortOrder() {
		return sortOrder;
	}
	/**
	 * @param sortOrder The sortOrder to set.
	 */
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }

}

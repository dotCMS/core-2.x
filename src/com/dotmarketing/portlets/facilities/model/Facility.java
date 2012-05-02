package com.dotmarketing.portlets.facilities.model;

import java.io.Serializable;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;

/** @author Hibernate CodeGenerator */
public class Facility extends Inode implements Serializable{

    /** identifier field */
    //private long inode;

    /** nullable persistent field */
    private String facilityName;

    /** nullable persistent field */
    private String facilityDescription;

    /** nullable persistent field */
    private boolean active;

    /** nullable persistent field */
    private int sortOrder;

    /** default constructor */
    public Facility() {
        super.setType("facility");
        active = true;
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
}

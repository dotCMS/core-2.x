package com.dotmarketing.portlets.entities.model;

import java.io.Serializable;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;


public class Entity extends Inode implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] categories;

	/** persistent field */
	private String entityName;

	/** default constructor */
	public Entity() {
		super.setType("entity");
	}

	/** minimal constructor */
	public Entity(java.lang.String entityName) {
		this.entityName = entityName;
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
	 * Returns the entityName.
	 * @return String
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entityName.
	 * @param entityName The entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Returns the categories.
	 * @return String[]
	 */
	public String[] getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 * @param categories The categories to set
	 */
	public void setCategories(String[] categories) {
		this.categories = categories;
	}

}

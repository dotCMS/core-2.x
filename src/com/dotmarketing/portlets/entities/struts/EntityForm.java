package com.dotmarketing.portlets.entities.struts;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.liferay.portal.util.Constants;

public class EntityForm extends ValidatorForm {

    private static final long serialVersionUID = 1L;

	private String[] categories;

	/** identifier field */
	private String inode;

	/** persistent field */
	private String entityName;

	/** default constructor */
	public EntityForm() {
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
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
    		Logger.debug(this, "Entity validation!!!!!!");
            return super.validate(mapping, request);
        }
        return null;
    }

}

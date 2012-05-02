package com.dotmarketing.portlets.webevents.struts;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.util.Constants;

public class WebEventForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;
	
	private String inode; //identifier
    private String title; //persistent
    private String subtitle; //persistent
    private String summary; //persistent
    private String description; //persistent
    private String termsConditions; //persistent
    private String comments; //persistent
    private boolean partnersOnly; //persistent
    private boolean showOnWeb; //persistent
    private int sortOrder; //persistent
    private boolean institute; //persistent
    
    /*IMAGES*/
    private String eventImage1; //persistent
    private String selectedeventImage1;
    private String eventImage2; //persistent
    private String selectedeventImage2;
    private String eventImage3; //persistent
    private String selectedeventImage3;
    private String eventImage4; //persistent
    private String selectedeventImage4;
    /*CATEGORIES*/
    private String[] categories;
	private String[] filesInodesList;
	private String filesInodes;

    public WebEventForm() {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.SAVE)) {
            return super.validate(mapping, request);
        }
        return null;
    }
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
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
	 * @return Returns the eventImage1.
	 */
	public String getEventImage1() {
		return eventImage1;
	}

	/**
	 * @param eventImage1 The eventImage1 to set.
	 */
	public void setEventImage1(String eventImage1) {
		this.eventImage1 = eventImage1;
	}

	/**
	 * @return Returns the eventImage2.
	 */
	public String getEventImage2() {
		return eventImage2;
	}

	/**
	 * @param eventImage2 The eventImage2 to set.
	 */
	public void setEventImage2(String eventImage2) {
		this.eventImage2 = eventImage2;
	}

	/**
	 * @return Returns the eventImage3.
	 */
	public String getEventImage3() {
		return eventImage3;
	}

	/**
	 * @param eventImage3 The eventImage3 to set.
	 */
	public void setEventImage3(String eventImage3) {
		this.eventImage3 = eventImage3;
	}

	/**
	 * @return Returns the eventImage4.
	 */
	public String getEventImage4() {
		return eventImage4;
	}

	/**
	 * @param eventImage4 The eventImage4 to set.
	 */
	public void setEventImage4(String eventImage4) {
		this.eventImage4 = eventImage4;
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
	 * @return Returns the partnersOnly.
	 */
	public boolean isPartnersOnly() {
		return partnersOnly;
	}

	/**
	 * @param partnersOnly The partnersOnly to set.
	 */
	public void setPartnersOnly(boolean partnersOnly) {
		this.partnersOnly = partnersOnly;
	}

	/**
	 * @return Returns the showOnWeb.
	 */
	public boolean isShowOnWeb() {
		return showOnWeb;
	}

	/**
	 * @param showOnWeb The showOnWeb to set.
	 */
	public void setShowOnWeb(boolean showOnWeb) {
		this.showOnWeb = showOnWeb;
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

	/**
	 * @return Returns the summary.
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary The summary to set.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return Returns the termsConditions.
	 */
	public String getTermsConditions() {
		return termsConditions;
	}

	/**
	 * @param termsConditions The termsConditions to set.
	 */
	public void setTermsConditions(String termsConditions) {
		this.termsConditions = termsConditions;
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
	 * @return Returns the categories.
	 */
	public String[] getCategories() {
		return categories;
	}

	/**
	 * @param categories The categories to set.
	 */
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	
	public void setCategories(java.util.List al) {
		if (al != null) {
			String[] x = new String[al.size()];
			java.util.Iterator i = al.iterator();
			int n = 0;

			while (i.hasNext()) {
				Category cat = (Category) i.next();
				x[n++] = cat.getInode();
			}
			setCategories(x);
		}
	}

	/**
	 * @return Returns the selectedeventImage1.
	 */
	public String getSelectedeventImage1() {
		return selectedeventImage1;
	}

	/**
	 * @param selectedeventImage1 The selectedeventImage1 to set.
	 */
	public void setSelectedeventImage1(String selectedeventImage1) {
		this.selectedeventImage1 = selectedeventImage1;
	}

	/**
	 * @return Returns the selectedeventImage2.
	 */
	public String getSelectedeventImage2() {
		return selectedeventImage2;
	}

	/**
	 * @param selectedeventImage2 The selectedeventImage2 to set.
	 */
	public void setSelectedeventImage2(String selectedeventImage2) {
		this.selectedeventImage2 = selectedeventImage2;
	}

	/**
	 * @return Returns the selectedeventImage3.
	 */
	public String getSelectedeventImage3() {
		return selectedeventImage3;
	}

	/**
	 * @param selectedeventImage3 The selectedeventImage3 to set.
	 */
	public void setSelectedeventImage3(String selectedeventImage3) {
		this.selectedeventImage3 = selectedeventImage3;
	}

	/**
	 * @return Returns the selectedeventImage4.
	 */
	public String getSelectedeventImage4() {
		return selectedeventImage4;
	}

	/**
	 * @param selectedeventImage4 The selectedeventImage4 to set.
	 */
	public void setSelectedeventImage4(String selectedeventImage4) {
		this.selectedeventImage4 = selectedeventImage4;
	}

	
	/**
	 * @param filesInodes The filesInodes to set.
	 */
	public void setFilesInodes(String filesInodes) {
		this.filesInodes = filesInodes;
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer (filesInodes,",");
		while (tok.hasMoreTokens()) {
			String inode = tok.nextToken();
			if (!inode.trim().equals("")) {
				list.add(inode);
			}
		}
		if (list.size() == 0)
			this.filesInodesList = new String[0];
		else
			this.filesInodesList = (String[])list.toArray(new String[0]);
	}

	/**
	 * @return Returns the filesInodes.
	 */
	public String getFilesInodes() {
		return filesInodes;
	}
	
	/**
	 * @return Returns the filesInodesList.
	 */
	public String[] getFilesInodesList() {
		return filesInodesList;
	}

	/**
	 * @param filesInodesList The filesInodesList to set.
	 */
	public void setFilesInodesList(String[] filesInodesList) {
		this.filesInodesList = filesInodesList;
	}

	public void setFilesInodes(java.util.List filesInodesList) {
		if (filesInodesList != null) {
			java.util.Iterator i = filesInodesList.iterator();
			String inodeList = "";
			while (i.hasNext()) {
				String in = (String) i.next();
				inodeList += in + ",";
			}
			setFilesInodes (inodeList);
		}
	}

	/**
	 * @return Returns the institute.
	 */
	public boolean isInstitute() {
		return institute;
	}

	/**
	 * @param institute The institute to set.
	 */
	public void setInstitute(boolean institute) {
		this.institute = institute;
	}


}

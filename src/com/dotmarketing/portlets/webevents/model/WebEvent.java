package com.dotmarketing.portlets.webevents.model;

import com.dotmarketing.beans.Inode;

public class WebEvent extends Inode implements Comparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String title; //persistent
    private String subtitle; //persistent
    private String summary; //persistent
    private String description; //persistent
    private String termsConditions; //persistent
    private String comments; //persistent
    private boolean partnersOnly; //persistent
    private boolean showOnWeb; //persistent
    private int sortOrder; //persistent
    private String eventImage1; //persistent
    private String eventImage2; //persistent
    private String eventImage3; //persistent
    private String eventImage4; //persistent
    private boolean institute; //persistent

    public WebEvent() {
    	super.setType("web_event");
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

	public int compareTo(Object other) {
        if ( !(other instanceof WebEvent) ) return 0;

        WebEvent castOther = (WebEvent) other;

        if(this.getSortOrder()< castOther.getSortOrder()) return -1;
        if(this.getSortOrder()> castOther.getSortOrder()) return 1;

        return 0;
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

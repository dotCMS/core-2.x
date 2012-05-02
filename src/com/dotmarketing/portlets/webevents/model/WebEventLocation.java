package com.dotmarketing.portlets.webevents.model;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.util.UtilMethods;

public class WebEventLocation extends Inode {

	private static final long serialVersionUID = 1L;
	
    private String webEventInode; //persistent
    private String city; //persistent
    private String state; //persistent
    private java.util.Date startDate; //persistent
    private java.util.Date endDate; //persistent
    private boolean showOnWeb; //persistent
    private boolean webRegActive; //persistent
    private String hotelName; //persistent
    private String hotelLink; //persistent
    private String pastEventLink; //persistent
    private float partnerPrice; //persistent
    private float nonPartnerPrice; //persistent
    private String shortDescription; //persistent
    private String textEmail; //persistent
    private boolean almostAtCapacity = false; //persistent
    private boolean full = false; //persistent
    private boolean defaultContractPartnerPrice = false; //persistent

    public WebEventLocation() {
    	super.setType("web_event_location");
    }

	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return Returns the endDate.
	 */
	public java.util.Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return Returns the hotelLink.
	 */
	public String getHotelLink() {
		return hotelLink;
	}

	/**
	 * @param hotelLink The hotelLink to set.
	 */
	public void setHotelLink(String hotelLink) {
		this.hotelLink = hotelLink;
	}

	/**
	 * @return Returns the hotelName.
	 */
	public String getHotelName() {
		return hotelName;
	}

	/**
	 * @param hotelName The hotelName to set.
	 */
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	/**
	 * @return Returns the nonPartnerPrice.
	 */
	public float getNonPartnerPrice() {
		return nonPartnerPrice;
	}

	/**
	 * @param nonPartnerPrice The nonPartnerPrice to set.
	 */
	public void setNonPartnerPrice(float nonPartnerPrice) {
		this.nonPartnerPrice = nonPartnerPrice;
	}

	/**
	 * @return Returns the partnerPrice.
	 */
	public float getPartnerPrice() {
		return partnerPrice;
	}

	/**
	 * @param partnerPrice The partnerPrice to set.
	 */
	public void setPartnerPrice(float partnerPrice) {
		this.partnerPrice = partnerPrice;
	}

	/**
	 * @return Returns the pastEventLink.
	 */
	public String getPastEventLink() {
		return pastEventLink;
	}

	/**
	 * @param pastEventLink The pastEventLink to set.
	 */
	public void setPastEventLink(String pastEventLink) {
		this.pastEventLink = pastEventLink;
	}

	/**
	 * @return Returns the shortDescription.
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription The shortDescription to set.
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
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
	 * @return Returns the startDate.
	 */
	public java.util.Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return Returns the state.
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state The state to set.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return Returns the textEmail.
	 */
	public String getTextEmail() {
		return textEmail;
	}

	/**
	 * @param textEmail The textEmail to set.
	 */
	public void setTextEmail(String textEmail) {
		this.textEmail = textEmail;
	}

	/**
	 * @return Returns the webEventInode.
	 */
	public String getWebEventInode() {
		return webEventInode;
	}

	/**
	 * @param webEventInode The webEventInode to set.
	 */
	public void setWebEventInode(String webEventInode) {
		this.webEventInode = webEventInode;
	}

	/**
	 * @return Returns the webRegActive.
	 */
	public boolean isWebRegActive() {
		return webRegActive;
	}

	/**
	 * @param webRegActive The webRegActive to set.
	 */
	public void setWebRegActive(boolean webRegActive) {
		this.webRegActive = webRegActive;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		WebEvent event = (WebEvent) WebEventFactory.getWebEvent(webEventInode);
		java.util.Date startDate = this.getStartDate();
		java.util.Date endDate = this.getEndDate();
		String response = "";
		if (event.isInstitute()) {
			response = UtilMethods.dateToLongHTMLDateRange(startDate,endDate);
			sb.append(response);
			sb.append(", ");
			sb.append((UtilMethods.isSet(this.getHotelName())) ? this.getHotelName() + ", " : "");
			sb.append((UtilMethods.isSet(this.getCity())) ? this.getCity() + ", " : "");
			sb.append((UtilMethods.isSet(this.getState())) ? this.getState() : "");
		}
		else {
			//sb.append(UtilMethods.dateToLongHTMLDateTimeRange(this.getStartDate(),this.getEndDate()));
			sb.append(UtilMethods.dateToLongHTMLDateRange(this.getStartDate(),this.getEndDate()));
		}
		return sb.toString();
		
	}

	public String toShortString() {
		StringBuffer sb = new StringBuffer();
		WebEvent event = (WebEvent) WebEventFactory.getWebEvent(webEventInode);
		java.util.Date startDate = this.getStartDate();
		java.util.Date endDate = this.getEndDate();
		String response = "";
		if (event.isInstitute()) {
			response = UtilMethods.dateToLongHTMLDateRange(startDate,endDate);
			sb.append(response);
			sb.append(", ");
			sb.append((UtilMethods.isSet(this.getCity())) ? this.getCity() + ", " : "");
			sb.append((UtilMethods.isSet(this.getState())) ? this.getState() : "");
		}
		else {
			sb.append(UtilMethods.dateToLongHTMLDateRange(this.getStartDate(),this.getEndDate()));
		}
		return sb.toString();
		
	}

	public boolean isAlmostAtCapacity() {
		return almostAtCapacity;
	}

	public void setAlmostAtCapacity(boolean almostAtCapacity) {
		this.almostAtCapacity = almostAtCapacity;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

	public boolean isDefaultContractPartnerPrice() {
		return defaultContractPartnerPrice;
	}

	public void setDefaultContractPartnerPrice(boolean defaultContractPartnerPrice) {
		this.defaultContractPartnerPrice = defaultContractPartnerPrice;
	}
}

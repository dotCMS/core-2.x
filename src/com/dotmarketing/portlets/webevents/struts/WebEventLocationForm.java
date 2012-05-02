package com.dotmarketing.portlets.webevents.struts;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.util.Constants;

public class WebEventLocationForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;
	
	private String inode; //identifier
    private String webEventInode; //persistent
    private String city; //persistent
    private String state; //persistent
    private String title;

    /*DATES*/
    private java.util.Date startDate; //persistent
    private String strStartDate;
    private java.util.Date endDate; //persistent
    private String strEndDate;
    private String strStartTime;
    private String strEndTime;

    private boolean showOnWeb; //persistent
    private boolean webRegActive; //persistent
    private String hotelName; //persistent

    /*LINKS*/
    private String hotelLink; //persistent
    private String selectedhotelLink; //persistent
    private String pastEventLink; //persistent
    private String selectedpastEventLink; //persistent
    
    private float partnerPrice; //persistent
    private float nonPartnerPrice; //persistent
    private String shortDescription; //persistent
    private String textEmail; //persistent
    private boolean almostAtCapacity = false; //persistent
    private boolean full = false; //persistent
    private boolean defaultContractPartnerPrice = false; //persistent
    
    public WebEventLocationForm() {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.SAVE)) {
    		ActionErrors ae = new ActionErrors();
    		WebEvent event = (WebEvent) WebEventFactory.getWebEvent(webEventInode);
    		
    		if (event.isInstitute() && !UtilMethods.isSet(city)) { 
    			ae.add(Globals.ERROR_KEY,new ActionMessage("message.webeventlocation.city","City"));
    		}
    		if (event.isInstitute() && !UtilMethods.isSet(state)) { 
    			ae.add(Globals.ERROR_KEY,new ActionMessage("message.webeventlocation.state","State"));
    		}
    		/*
    		if (!event.isInstitute() && !UtilMethods.isSet(strStartTime)) { 
    			ae.add(Globals.ERROR_KEY,new ActionMessage("message.webeventlocation.starttime","Start Time"));
    		}
    		if (!event.isInstitute() && !UtilMethods.isSet(strEndTime)) { 
    			ae.add(Globals.ERROR_KEY,new ActionMessage("message.webeventlocation.endtime","End Time"));
    		}*/
        	ae.add(super.validate(mapping, request));
            return ae;
        }
        return null;
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

	/**
	 * @return Returns the selectedhotelLink.
	 */
	public String getSelectedhotelLink() {
		return selectedhotelLink;
	}

	/**
	 * @param selectedhotelLink The selectedhotelLink to set.
	 */
	public void setSelectedhotelLink(String selectedhotelLink) {
		this.selectedhotelLink = selectedhotelLink;
	}

	/**
	 * @return Returns the selectedpastEventLink.
	 */
	public String getSelectedpastEventLink() {
		return selectedpastEventLink;
	}

	/**
	 * @param selectedpastEventLink The selectedpastEventLink to set.
	 */
	public void setSelectedpastEventLink(String selectedpastEventLink) {
		this.selectedpastEventLink = selectedpastEventLink;
	}

	/**
	 * @return Returns the strEndDate.
	 */
	public String getStrEndDate() {
		if (endDate!=null && !UtilMethods.isSet(strEndDate)) {
			return new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		return strEndDate;
	}

	/**
	 * @param strEndDate The strEndDate to set.
	 */
	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
		try {
			if (UtilMethods.isSet(strEndTime)) {
				this.endDate = new SimpleDateFormat("MM/dd/yyyy H:mm:ss").parse(strEndDate + " " + strEndTime);			
			}
			else {
				this.endDate = new SimpleDateFormat("MM/dd/yyyy").parse(strEndDate);			
			}
		} 
		catch(Exception ex) {
		}
	}

	/**
	 * @return Returns the strStartDate.
	 */
	public String getStrStartDate() {
		if (startDate!=null && !UtilMethods.isSet(strStartDate)) {
			return new SimpleDateFormat("MM/dd/yyyy").format(startDate);
		}
		return strStartDate;
	}
	/**
	 * @param strStartDate The strStartDate to set.
	 */
	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
		try {
			if (UtilMethods.isSet(strStartTime)) {
				this.startDate = new SimpleDateFormat("MM/dd/yyyy H:mm:ss").parse(strStartDate + " " + strStartTime);			
			}
			else {
				this.startDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);			
			}
		} 
		catch(Exception ex) {
		}
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
	 * @return Returns the strEndTime.
	 */
	public String getStrEndTime() {
		if (endDate!=null && !UtilMethods.isSet(strEndTime)) {
			return new SimpleDateFormat("H:mm:ss").format(endDate);
		}
		return strEndTime;
	}

	/**
	 * @param strEndTime The strEndTime to set.
	 */
	public void setStrEndTime(String strEndTime) {
		this.strEndTime = strEndTime;
	}

	/**
	 * @return Returns the strStartTime.
	 */
	public String getStrStartTime() {
		if (startDate!=null && !UtilMethods.isSet(strStartTime)) {
			return new SimpleDateFormat("H:mm:ss").format(startDate);
		}
		return strStartTime;
	}

	/**
	 * @param strStartTime The strStartTime to set.
	 */
	public void setStrStartTime(String strStartTime) {
		this.strStartTime = strStartTime;
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

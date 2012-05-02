package com.dotmarketing.portlets.events.struts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;


/** @author Hibernate CodeGenerator */
public class EventForm extends ValidatorForm {

	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String contactCompany;
	private String contactEmail;
	private String contactFax;
	private String contactName;
	private String contactPhone;
	private String country;
	private String description;
	private String directions;
	private String dispatch;
	private String email;
	private String emailResponse;
	private String fax;
	private String includeFile;
	private String location;
	private String phone;
	private String state;
	private String title;
	private String subtitle;
	private String url;
	private String zip;
	private String[] categories;
	private String[] filesInodes;
	private boolean registration;
	private boolean showPublic;
	private String inode;
    private String webAddress;
    private java.util.Date startDate;
    private java.util.Date endDate;
	//private String setupDateString;
    private java.util.Date setupDate;
	//private String breakDateString;
    private java.util.Date breakDate;
    private String commentsEquipment;
	private boolean receivedAdminApproval;
	private boolean timeTBD;
	private String facilityInode;
	private int approvalStatus;

	public EventForm() {}

	public void setAddress1(java.lang.String address1) {
		this.address1 = address1;
	}

	public java.lang.String getAddress1() {
		return this.address1;
	}

	public void setAddress2(java.lang.String address2) {
		this.address2 = address2;
	}

	public java.lang.String getAddress2() {
		return this.address2;
	}

	public void setAddress3(java.lang.String address3) {
		this.address3 = address3;
	}

	public java.lang.String getAddress3() {
		return this.address3;
	}

	/**
	 * Sets the categories.
	 * @param categories The categories to set
	 */
	public void setCategories(String[] categories) {
		ArrayList list = new ArrayList ();
		for (int i = 0; i < categories.length; i++) {
			String cat = categories[i];
			if (!cat.trim().equals("")) {
				list.add(cat);
			}
		}
		if (list.size() == 0)
			this.categories = null;
		else
			this.categories = (String[])list.toArray(new String[0]);
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
	 * Returns the categories.
	 * @return String[]
	 */
	public String[] getCategories() {
		return categories;
	}

	public void setCity(java.lang.String city) {
		this.city = city;
	}

	public java.lang.String getCity() {
		return this.city;
	}

	public void setContactCompany(java.lang.String contactCompany) {
		this.contactCompany = contactCompany;
	}

	public java.lang.String getContactCompany() {
		return this.contactCompany;
	}

	public void setContactEmail(java.lang.String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public java.lang.String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactFax(java.lang.String contactFax) {
		this.contactFax = contactFax;
	}

	public java.lang.String getContactFax() {
		return this.contactFax;
	}

	public void setContactName(java.lang.String contactName) {
		this.contactName = contactName;
	}

	public java.lang.String getContactName() {
		return this.contactName;
	}

	public void setContactPhone(java.lang.String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public java.lang.String getContactPhone() {
		return this.contactPhone;
	}

	public void setCountry(java.lang.String country) {
		this.country = country;
	}

	public java.lang.String getCountry() {
		return this.country;
	}

	public void setDescription(java.lang.String description) {
		//Logger.info("setDescription=" + description);
		this.description = description;
	}

	public java.lang.String getDescription() {
		return this.description;
	}

	public void setDirections(java.lang.String directions) {
		this.directions = directions;
	}

	public java.lang.String getDirections() {
		return this.directions;
	}

	/**
	 * Sets the dispatch.
	 * @param dispatch The dispatch to set
	 */
	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	/**
	 * Returns the dispatch.
	 * @return String
	 */
	public String getDispatch() {
		return dispatch;
	}

	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	public java.lang.String getEmail() {
		return this.email;
	}

	public void setEmailResponse(java.lang.String emailResponse) {
		this.emailResponse = emailResponse;
	}

	public java.lang.String getEmailResponse() {
		return this.emailResponse;
	}

	/**
	 * Sets the endDateString.
	 * @param endDateString The endDateString to set
	 */
	public void setEndDateString(String endDateString) {
        if (endDateString.equals(""))
            return;
		try {
			this.endDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(endDateString);			
		} catch(ParseException ex) {
			//Logger.info("Exception parsing end date: " + ex.getMessage());
			Logger.error(this,ex.getMessage(),ex);
		}
	}

	/**
	 * Returns the endDateString.
	 * @return String
	 */
	public String getEndDateString() {
		return UtilMethods.dateToHTMLDate(endDate) + " " + UtilMethods.dateToHTMLTime(endDate);
	}

	public java.util.Date getEndDate () {
		return this.endDate;
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
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	
	public void setFax(java.lang.String fax) {
		this.fax = fax;
	}

	public java.lang.String getFax() {
		return this.fax;
	}

	public void setIncludeFile(java.lang.String includeFile) {
		this.includeFile = includeFile;
	}

	public java.lang.String getIncludeFile() {
		return this.includeFile;
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public String getInode() {
		if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
	}

	public void setLocation(java.lang.String location) {
		this.location = location;
	}

	public java.lang.String getLocation() {
		return this.location;
	}

	public void setPhone(java.lang.String phone) {
		this.phone = phone;
	}

	public java.lang.String getPhone() {
		return this.phone;
	}

	/**
	 * Sets the registration.
	 * @param registration The registration to set
	 */
	public void setRegistration(boolean registration) {
		this.registration = registration;
	}

	public boolean getRegistration() {
		return this.registration;
	}

	/**
	 * Returns the registration.
	 * @return boolean
	 */
	public boolean isRegistration() {
		return registration;
	}

	/**
	 * Sets the showPublic.
	 * @param showPublic The showPublic to set
	 */
	public void setShowPublic(boolean showPublic) {
		this.showPublic = showPublic;
	}

	/**
	 * Returns the showPublic.
	 * @return boolean
	 */
	public boolean isShowPublic() {
		return showPublic;
	}

	/**
	 * Sets the startDateString.
	 * @param startDateString The startDateString to set
	 */
	public void setStartDateString(String startDateString) {
        if (startDateString.equals(""))
            return;
		try {
			this.startDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(startDateString);			
		} catch(ParseException ex) {
			//Logger.info("Exception parsing date: " + ex.getMessage());
			Logger.error(this,ex.getMessage(),ex);
		}
	}

	/**
	 * Returns the startDateString.
	 * @return String
	 */
	public String getStartDateString() {
		return UtilMethods.dateToHTMLDate(startDate) + " " + UtilMethods.dateToHTMLTime(startDate);
	}

	public void setState(java.lang.String state) {
		this.state = state;
	}

	public java.lang.String getState() {
		return this.state;
	}

	public void setTitle(java.lang.String title) {
		//Logger.info("setTitle=" + title);
		this.title = title;
	}

	public java.lang.String getTitle() {
		return this.title;
	}

	public void setUrl(java.lang.String url) {
		this.url = url;
	}

	public java.lang.String getUrl() {
		return this.url;
	}

	public void setZip(java.lang.String zip) {
		this.zip = zip;
	}

	public java.lang.String getZip() {
		return this.zip;
	}

	public boolean equals(Object other) {
		if (!(other instanceof EventForm)) {
			return false;
		}

		EventForm castOther = (EventForm) other;

		return new EqualsBuilder().append(this.getInode(), castOther.getInode()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getInode()).toHashCode();
	}

	public void reset() {
		this.categories = new String[0];
		this.filesInodes = new String[0];
	}

	public String toString() {
		return new ToStringBuilder(this).append("inode", getInode()).toString();
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		if ("save".equals(dispatch)) {
			return super.validate(mapping, request);
		}

		return null;
	}
	/**
	 * Returns the webAddress.
	 * @return String
	 */
	public String getWebAddress() {
		return webAddress;
	}

	/**
	 * Sets the webAddress.
	 * @param webAddress The webAddress to set
	 */
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
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
	 * @return Returns the filesInodes.
	 */
	public String[] getFilesInodes() {
		return filesInodes;
	}

	/**
	 * @param filesInodes The filesInodes to set.
	 */
	public void setFilesInodes(String[] filesInodes) {
		ArrayList list = new ArrayList ();
		if (filesInodes != null) {
			for (int i = 0; i < filesInodes.length; i++) {
				StringTokenizer tok = new StringTokenizer (filesInodes[i],",");
				while (tok.hasMoreTokens()) {
					String inode = tok.nextToken();
					if (!inode.trim().equals("")) {
						list.add(inode);
					}
				}
			}
		}
		if (list.size() == 0)
			this.filesInodes = new String[0];
		else
			this.filesInodes = (String[])list.toArray(new String[0]);
	}

	public void setFilesInodes(java.util.List filesInodesList) {
		if (filesInodesList != null) {
			ArrayList inodeList = new ArrayList();
			java.util.Iterator i = filesInodesList.iterator();
			int n = 0;
			while (i.hasNext()) {
				Inode in = (Inode) i.next();
				inodeList.add(in.getInode());
			}

			setFilesInodes ((String[])inodeList.toArray(new String[0]));
		}
	}

    public java.util.Date getBreakDate() {
        return breakDate;
    }
    public void setBreakDate(java.util.Date breakDate) {
        this.breakDate = breakDate;
    }
    public String getBreakDateString() {
		return UtilMethods.dateToHTMLDate(breakDate) + " " + UtilMethods.dateToHTMLTime(breakDate);
    }
    public void setBreakDateString(String breakDateString) {
        if (breakDateString.equals(""))
            return;
		try {
			this.breakDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(breakDateString);			
		} catch(ParseException ex) {
			//Logger.info("Exception parsing end date: " + ex.getMessage());
			Logger.error(this,ex.getMessage(),ex);
		}
    }
    public String getCommentsEquipment() {
        return commentsEquipment;
    }
    public void setCommentsEquipment(String commentsEquipment) {
        this.commentsEquipment = commentsEquipment;
    }
    public boolean isReceivedAdminApproval() {
        return receivedAdminApproval;
    }
    public void setReceivedAdminApproval(boolean receivedAdminApproval) {
        this.receivedAdminApproval = receivedAdminApproval;
    }
    public java.util.Date getSetupDate() {
        return setupDate;
    }
    public void setSetupDate(java.util.Date setupDate) {
        this.setupDate = setupDate;
    }
    public String getSetupDateString() {
		return UtilMethods.dateToHTMLDate(setupDate) + " " + UtilMethods.dateToHTMLTime(setupDate);
    }
    public void setSetupDateString(String setupDateString) {
        if (setupDateString.equals(""))
            return;
		try {
			this.setupDate = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(setupDateString);			
		} catch(ParseException ex) {
			//Logger.info("Exception parsing end date: " + ex.getMessage());
			Logger.error(this,ex.getMessage(),ex);
		}
    }
    public String getFacilityInode() {
        return facilityInode;
    }
    public void setFacilityInode(String facilityInode) {
        this.facilityInode = facilityInode;
    }
    public int getApprovalStatus() {
        return approvalStatus;
    }
    public void setApprovalStatus(int approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    public boolean isTimeTBD() {
        return timeTBD;
    }
    public void setTimeTBD(boolean timeTBD) {
        this.timeTBD = timeTBD;
    }
}

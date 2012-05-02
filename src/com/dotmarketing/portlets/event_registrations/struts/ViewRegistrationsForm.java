package com.dotmarketing.portlets.event_registrations.struts;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.UtilMethods;

public class ViewRegistrationsForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;
	private String eventInode;
	private String locationInode;
	private Date startDate;
	private Date endDate;
	private String strStartDate;
	private String strEndDate;
	private String firstName;
	private String lastName;
	private String system;
	private String facility;
	private String facilityTitle;
	private String registrationNumber;
	private String orderBy;
	private int institute;
	private String selectedColumn;
	private String selectedDirection;
	private String invoiceNumber;
	private int paymentStatus;
	private int[] paymentStatusArray;
	
	public ViewRegistrationsForm() {
		eventInode = "0";
		locationInode = "";
		strStartDate = "";
		strEndDate = "";
		firstName = "";
		lastName = "";
		system = "";
		facility = "";
		facilityTitle = "";
		registrationNumber = "";
		institute = 1;
	}
	
	/**
	 * @return Returns the endDate.
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return Returns the facility.
	 */
	public String getFacility() {
		return facility;
	}
	/**
	 * @param facility The facility to set.
	 */
	public void setFacility(String facility) {
		if (facility!=null) {
			this.facility = facility;
		}
	}
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		if (firstName!=null)
			this.firstName = firstName;
	}
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		if (lastName!=null)
			this.lastName = lastName;
	}
	/**
	 * @return Returns the startDate.
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
		if (strEndDate!=null)
			this.strEndDate = strEndDate;
		try {
			this.endDate = new SimpleDateFormat("MM/dd/yyyy").parse(strEndDate);			
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
		if (strStartDate!=null)
			this.strStartDate = strStartDate;
		try {
			this.startDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);			
		} 
		catch(Exception ex) {
		}
	}
	/**
	 * @return Returns the system.
	 */
	public String getSystem() {
		return system;
	}
	/**
	 * @param system The system to set.
	 */
	public void setSystem(String system) {
		if (system!=null)
			this.system = system;
	}
	/**
	 * @return Returns the eventInode.
	 */
	public String getEventInode() {
		return eventInode;
	}
	/**
	 * @param eventInode The eventInode to set.
	 */
	public void setEventInode(String eventInode) {
		this.eventInode = eventInode;
	}
	/**
	 * @return Returns the locationInode.
	 */
	public String getLocationInode() {
		return locationInode;
	}
	/**
	 * @param locationInode The locationInode to set.
	 */
	public void setLocationInode(String locationInode) {
		this.locationInode = locationInode;
	}
	/**
	 * @return Returns the facilityTitle.
	 */
	public String getFacilityTitle() {
		return facilityTitle;
	}
	/**
	 * @param facilityTitle The facilityTitle to set.
	 */
	public void setFacilityTitle(String facilityTitle) {
		if (facilityTitle!=null)
			this.facilityTitle = facilityTitle;
	}
	/**
	 * @return Returns the registrationNumber.
	 */
	public String getRegistrationNumber() {
		return registrationNumber;
	}
	/**
	 * @param registrationNumber The registrationNumber to set.
	 */
	public void setRegistrationNumber(String registrationNumber) {
		if (registrationNumber!=null)
			this.registrationNumber = registrationNumber;
	}
	/**
	 * @return Returns the institute.
	 */
	public int getInstitute() {
		return institute;
	}
	/**
	 * @param institute The institute to set.
	 */
	public void setInstitute(int institute) {
		this.institute = institute;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSelectedColumn() {
		return selectedColumn;
	}
	public void setSelectedColumn(String selectedColumn) {
		this.selectedColumn = selectedColumn;
	}
	public String getSelectedDirection() {
		return selectedDirection;
	}
	public void setSelectedDirection(String selectedDirection) {
		this.selectedDirection = selectedDirection;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public int[] getPaymentStatusArray() {
		return paymentStatusArray;
	}

	public void setPaymentStatusArray(int[] paymentStatusArray) {
		this.paymentStatusArray = paymentStatusArray;
	}
}

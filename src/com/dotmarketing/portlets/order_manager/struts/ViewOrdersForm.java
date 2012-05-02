package com.dotmarketing.portlets.order_manager.struts;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.UtilMethods;

public class ViewOrdersForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;
	
	private Date startDate;
	private Date endDate;
	private String strStartDate;
	private String strEndDate;
	private String firstName;
	private String lastName;
	private String system;
	private String facility;
	private String facilityTitle;
	private String orderInode;
	private String email;
    private int orderStatus; //persistent
    private int paymentStatus; //persistent
    private String trackingNumber;
	private int[] orderStatusArray;
	private int[] paymentStatusArray;
	private boolean orderOutsideUS = false;
	private String invoiceNumber; 
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
		this.facility = facility;
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
		this.system = system;
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
		this.facilityTitle = facilityTitle;
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the orderInode.
	 */
	public String getOrderInode() {
		return orderInode;
	}
	/**
	 * @param orderInode The orderInode to set.
	 */
	public void setOrderInode(String orderInode) {
		this.orderInode = orderInode;
	}
	/**
	 * @return Returns the orderStatus.
	 */
	public int getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus The orderStatus to set.
	 */
	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * @return Returns the paymentStatus.
	 */
	public int getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus The paymentStatus to set.
	 */
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	/**
	 * @return Returns the paymentStatus.
	 */
	public String getTrackingNumber() {
		return trackingNumber;
	}
	/**
	 * @param paymentStatus The paymentStatus to set.
	 */
	public void setTrackingNumer(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	/**
	 * @param orderStatus The orderStatus to set.
	 */
	public void setOrderStatusArray(int[] orderStatusArray) {
		this.orderStatusArray = orderStatusArray;
	}
	/**
	 * @return Returns the paymentStatus.
	 */
	public int[] getOrderStatusArray() {
		return orderStatusArray;
	}
	
	/**
	 * @param orderStatus The orderStatus to set.
	 */
	public void setPaymentStatusArray(int[] paymentStatusArray) {
		this.paymentStatusArray = paymentStatusArray;
	}
	/**
	 * @return Returns the paymentStatus.
	 */
	public int[] getPaymentStatusArray() {
		return paymentStatusArray;
	}
	public boolean isOrderOutsideUS() {
		return orderOutsideUS;
	}
	public void setOrderOutsideUS(boolean orderOutsideUS) {
		this.orderOutsideUS = orderOutsideUS;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

}

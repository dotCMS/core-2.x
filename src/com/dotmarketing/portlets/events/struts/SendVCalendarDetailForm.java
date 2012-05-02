package com.dotmarketing.portlets.events.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * Used to manage the data from sendVCalendar form
 * @author Armando Siem
 * @version 1.6
 * @since 1.6
 */
public class SendVCalendarDetailForm extends ValidatorForm
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Inode of the event to be sent
	private String eventInode;
	
	// Email info
	private String fromEmail;
	private String fromName;
	private String toEmail;
	private String cc;
	private String bcc;
	private String subject;
	private String toName;
	
	@SuppressWarnings("deprecation")
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		return null;
	}

	public String getEventInode() {
		return eventInode;
	}

	public void setEventInode(String eventInode) {
		this.eventInode = eventInode;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}
}
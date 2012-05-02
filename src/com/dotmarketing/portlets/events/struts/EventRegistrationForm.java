package com.dotmarketing.portlets.events.struts;

import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;


/** @author David Torres */
public class EventRegistrationForm extends ValidatorForm {

	private String inode;
	private String eventInode;
	private Date registrationDate;
	private String parent1Name;
	private String parent2Name;
	private String student1Name;
	private String student1Grade;
	private String student2Name;
	private String student2Grade;
	private String student3Name;
	private String student3Grade;
	private String student4Name;
	private String student4Grade;
	private String student5Name;
	private String student5Grade;
	private int numberAttending;
	private String comments;
	private String email;
	private String fullName;
	private int registationRandomId;

	public EventRegistrationForm() {
		this.comments = "";
		this.email = "";
		this.eventInode = "";
		this.inode = "";
		this.numberAttending = 1;
		this.parent1Name = "";
		this.parent2Name = "";
		this.registrationDate = new Date();
		this.student1Name = "";
		this.student1Grade = "";
		this.student2Name = "";
		this.student2Grade = "";
		this.student3Name = "";
		this.student3Grade = "";
		this.student4Name = "";
		this.student4Grade = "";
		this.student5Name = "";
		this.student5Grade = "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public String getInode() {
		if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
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
	 * @return Returns the numberAttending.
	 */
	public int getNumberAttending() {
		return numberAttending;
	}
	/**
	 * @param numberAttending The numberAttending to set.
	 */
	public void setNumberAttending(int numberAttending) {
		this.numberAttending = numberAttending;
	}
	/**
	 * @return Returns the parent1Name.
	 */
	public String getParent1Name() {
		return parent1Name;
	}
	/**
	 * @param parent1Name The parent1Name to set.
	 */
	public void setParent1Name(String parent1Name) {
		this.parent1Name = parent1Name;
	}
	/**
	 * @return Returns the parent2Name.
	 */
	public String getParent2Name() {
		return parent2Name;
	}
	/**
	 * @param parent2Name The parent2Name to set.
	 */
	public void setParent2Name(String parent2Name) {
		this.parent2Name = parent2Name;
	}
	/**
	 * @return Returns the registrationDate.
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}
	/**
	 * @param registrationDate The registrationDate to set.
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	/**
	 * @return Returns the student1Grade.
	 */
	public String getStudent1Grade() {
		return student1Grade;
	}
	/**
	 * @param student1Grade The student1Grade to set.
	 */
	public void setStudent1Grade(String student1Grade) {
		this.student1Grade = student1Grade;
	}
	/**
	 * @return Returns the student1Name.
	 */
	public String getStudent1Name() {
		return student1Name;
	}
	/**
	 * @param student1Name The student1Name to set.
	 */
	public void setStudent1Name(String student1Name) {
		this.student1Name = student1Name;
	}
	/**
	 * @return Returns the student2Grade.
	 */
	public String getStudent2Grade() {
		return student2Grade;
	}
	/**
	 * @param student2Grade The student2Grade to set.
	 */
	public void setStudent2Grade(String student2Grade) {
		this.student2Grade = student2Grade;
	}
	/**
	 * @return Returns the student2Name.
	 */
	public String getStudent2Name() {
		return student2Name;
	}
	/**
	 * @param student2Name The student2Name to set.
	 */
	public void setStudent2Name(String student2Name) {
		this.student2Name = student2Name;
	}
	/**
	 * @return Returns the student3Grade.
	 */
	public String getStudent3Grade() {
		return student3Grade;
	}
	/**
	 * @param student3Grade The student3Grade to set.
	 */
	public void setStudent3Grade(String student3Grade) {
		this.student3Grade = student3Grade;
	}
	/**
	 * @return Returns the student3Name.
	 */
	public String getStudent3Name() {
		return student3Name;
	}
	/**
	 * @param student3Name The student3Name to set.
	 */
	public void setStudent3Name(String student3Name) {
		this.student3Name = student3Name;
	}
	/**
	 * @return Returns the student4Grade.
	 */
	public String getStudent4Grade() {
		return student4Grade;
	}
	/**
	 * @param student4Grade The student4Grade to set.
	 */
	public void setStudent4Grade(String student4Grade) {
		this.student4Grade = student4Grade;
	}
	/**
	 * @return Returns the student4Name.
	 */
	public String getStudent4Name() {
		return student4Name;
	}
	/**
	 * @param student4Name The student4Name to set.
	 */
	public void setStudent4Name(String student4Name) {
		this.student4Name = student4Name;
	}
	/**
	 * @return Returns the student5Grade.
	 */
	public String getStudent5Grade() {
		return student5Grade;
	}
	/**
	 * @param student5Grade The student5Grade to set.
	 */
	public void setStudent5Grade(String student5Grade) {
		this.student5Grade = student5Grade;
	}
	/**
	 * @return Returns the student5Name.
	 */
	public String getStudent5Name() {
		return student5Name;
	}
	/**
	 * @param student5Name The student5Name to set.
	 */
	public void setStudent5Name(String student5Name) {
		this.student5Name = student5Name;
	}
	public boolean equals(Object other) {
		if (!(other instanceof EventRegistrationForm)) {
			return false;
		}

		EventRegistrationForm castOther = (EventRegistrationForm) other;

		EqualsBuilder eb = new EqualsBuilder ();
		eb.append(this.getInode(), castOther.getInode());
		
		return eb.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getInode()).toHashCode();
	}

	public void reset() {
		this.comments = "";
		this.email = "";
		this.eventInode = "";
		this.inode = "";
		this.numberAttending = 1;
		this.parent1Name = "";
		this.parent2Name = "";
		this.registrationDate = new Date();
		this.student1Name = "";
		this.student1Grade = "";
		this.student2Name = "";
		this.student2Grade = "";
		this.student3Name = "";
		this.student3Grade = "";
		this.student4Name = "";
		this.student4Grade = "";
		this.student5Name = "";
		this.student5Grade = "";
	}

	public String toString() {
		return new ToStringBuilder(this).append("inode", getInode()).toString();
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors ();
		
		if (this.fullName.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"message.contentlet.required", "Full Name"));
		}
		
		/*if (this.parent1Name.equals("")) {
			ActionError error = new ActionError ("prompt.parent1_name");
			errors.add("parent1Name", error);
		}
		if (this.student1Name.equals("")) {
			ActionError error = new ActionError ("prompt.student1_name");
			errors.add("student1Name", error);
		}
		if (this.student1Grade.equals("")) {
			ActionError error = new ActionError ("prompt.student1_grade");
			errors.add("student1Grade", error);
		}
		if (this.numberAttending == 0) {
			ActionError error = new ActionError ("prompt.number_attending");
			errors.add("numberAttending", error);
		}*/
		if (this.email.equals("")) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.required", "Email"));
		} else {
			StringTokenizer tok = new StringTokenizer(email, "@");
			if (tok.countTokens() != 2) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.format", "Email"));
			}
		}
		return errors;
	}
	
	/**
	 * @return Returns the registationRandomId.
	 */
	public int getRegistationRandomId() {
		return registationRandomId;
	}
	/**
	 * @param registationRandomId The registationRandomId to set.
	 */
	public void setRegistationRandomId(int registationRandomId) {
		this.registationRandomId = registationRandomId;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
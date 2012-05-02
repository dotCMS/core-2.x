package com.dotmarketing.portlets.events.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;

/** @author David Torres */
public class EventRegistration extends Inode implements Serializable, Comparable{
	
	/** identifier field */
	private String inode;

	Date registrationDate;
	private String fullName;
	int numberAttending;
	private String comments;

	/** nullable persistent field */
	private String email;
	
	/** default constructor */
	public EventRegistration() {
		super.setType("event_registration");
		registrationDate = new Date();
	}

	
	public boolean equals(Object other) {
		if ( !(other instanceof EventRegistration) ) return false;
		EventRegistration castOther = (EventRegistration) other;
		return new EqualsBuilder()
		.append(this.getInode(), castOther.getInode())
		.isEquals();
	}
	
	public int compareTo(Object other) {
		if ( !(other instanceof EventRegistration) ) return 0;
		EventRegistration castOther = (EventRegistration) other;
		
		if (this.getRegistrationDate().before(castOther.getRegistrationDate())) return -1;
		if(this.getRegistrationDate().after(castOther.getRegistrationDate())) return 0;
		
		return 0;
		
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
		.append(getInode())
		.toHashCode();
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}


	public void setInode(String inode) {
		this.inode = inode;
	}


	public int getNumberAttending() {
		return numberAttending;
	}


	public void setNumberAttending(int numberAttending) {
		this.numberAttending = numberAttending;
	}


	public Date getRegistrationDate() {
		return registrationDate;
	}


	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	
}

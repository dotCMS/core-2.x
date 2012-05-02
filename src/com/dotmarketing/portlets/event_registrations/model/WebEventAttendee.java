package com.dotmarketing.portlets.event_registrations.model;

import com.dotmarketing.beans.Inode;

public class WebEventAttendee extends Inode {
	private static final long serialVersionUID = 1L;
	
    private String eventRegistrationInode; //persistent
    private String firstName; //persistent
    private String lastName; //persistent
    private String badgeName; //persistent
    private String email; //persistent
    private String title; //persistent
    private float registrationPrice; //persistent

    public WebEventAttendee(String eventRegistrationInode, String firstName, String lastName, String badgeName, String email, String title, int registrationPrice) {
        this.eventRegistrationInode = eventRegistrationInode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.badgeName = badgeName;
        this.email = email;
        this.title = title;
        this.registrationPrice = registrationPrice;
    }

    public WebEventAttendee() {
    	setType("web_event_attendee");
    }

    public String getEventRegistrationInode() {
        return this.eventRegistrationInode;
    }

    public void setEventRegistrationInode(String eventRegistrationInode) {
        this.eventRegistrationInode = eventRegistrationInode;
    }
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getBadgeName() {
        return this.badgeName;
    }

    public void setBadgeName(String badgeName) {
        this.badgeName = badgeName;
    }
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public float getRegistrationPrice() {
        return this.registrationPrice;
    }

    public void setRegistrationPrice(float registrationPrice) {
        this.registrationPrice = registrationPrice;
    }
}

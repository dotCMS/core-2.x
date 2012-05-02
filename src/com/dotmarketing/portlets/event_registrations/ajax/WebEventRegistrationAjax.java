package com.dotmarketing.portlets.event_registrations.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.portlets.event_registrations.factories.WebEventAttendeeFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.util.Logger;


/**
 * @author David
 */
public class WebEventRegistrationAjax {

	public Map getAttendeeMap(String inode) {
		WebEventAttendee attendee = WebEventAttendeeFactory.getWebEventAttendee(inode);
		return getAttendeeMap (attendee);
	}
	
	private Map<String,String> getAttendeeMap (WebEventAttendee attendee) {
		Map<String,String> map = new HashMap<String,String> ();
		map.put("firstName",attendee.getFirstName());
		map.put("lastName",attendee.getLastName());
		map.put("badgeName",attendee.getBadgeName());
		map.put("email",attendee.getEmail());
		map.put("title",attendee.getTitle());
		map.put("inode",String.valueOf(attendee.getInode()));

		return map;
	}
	
	public List<Map> getAttendeesByKeyword (String keyword) {
		List<WebEventAttendee> attendees = WebEventAttendeeFactory.getWebEventAttendeesByKeyword(keyword);
		ArrayList<Map> attendeeList = new ArrayList<Map> ();
		for (WebEventAttendee attendee : attendees) {
			try {
				attendeeList.add(getAttendeeMap(attendee));
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			}			
		}
		return attendeeList;
	}
	
}
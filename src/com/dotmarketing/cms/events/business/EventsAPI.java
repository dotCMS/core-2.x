/**
 *  Package com.dotmarketing.cms.events.business
 *
 * @author Armando Siem
 */

package com.dotmarketing.cms.events.business;

import java.util.HashMap;
import java.util.List;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.GenericAPI;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.EmailFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

/**
 * Class to manage the business process of the Events
 */

public class EventsAPI implements GenericAPI<Event> {

	private CategoryAPI categoryAPI = APILocator.getCategoryAPI();

	/**
	  * Look for the event with the specified id
	  * 
	  * @param		id long with event id
	  * @return		Event
	  */
	
	public Event find(String id) {
		return EventFactory.getEvent(id);
	}
	
	/**
	  * Delete the specified event
	  * 
	  * @param		event Event
	  */
	
	public void delete(Event event) {
		EventFactory.deleteEvent(event);
	}
	
	/**
	  * Return all the events
	  * 
	  * @return		List<Event>
	  */
	
	public List<Event> findAll() {
		return InodeFactory.getInodesOfClass(Event.class);
	}
	
	/**
	  * Save the specified event
	  * 
	  * @param		event Event
	  */
	
	public void save(Event event) {
		InodeFactory.saveInode(event);
	}
	
	/**
	  * Create a new event with specified parameters
	  * 
	  * @param		title String with the title of the new
	  * @param		submittedBy String with the username who is submitting the new
	  * @param		email String with the email
	  * @param		description String with the description
	  * @param		categoryKey String with a category key
	 * @throws DotDataException 
	 * @throws DotSecurityException 
	  */
	
	public void createEvent(String title, String submittedBy, String email, String description, String categoryKey, User user) 
		throws DotDataException, DotSecurityException {
		
		Event event = new Event();
		event.setTitle(title);
		event.setContactName(submittedBy);
		event.setContactEmail(email);
		event.setDescription(description);
		event.setTimeTBD(true);
		event.setShowPublic(false);
		event.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS);
		save(event);
		
		Category category = categoryAPI.findByKey(categoryKey, user, true);
		categoryAPI.addParent(event, category, user, true);
		
	}
	
	/**
	  * Create a new event with specified parameters and send the info
	  * 
	  * @param		title String with the title of the new
	  * @param		submittedBy String with the username who is submitting the new
	  * @param		email String with the email
	  * @param		description String with the description
	  * @param		categoryKey String with a category key of the new
	  * @param		user User
	  * @param		host Host
	 * @throws DotDataException 
	 * @throws DotSecurityException 
	  */
	
	public void createEventAndSendInfo(String title, String submittedBy, String email, String description, String categoryKey, User user, Host host) throws DotDataException, DotSecurityException {
		createEvent(title, submittedBy, email, description, categoryKey, user);
		
		Company company = PublicCompanyFactory.getDefaultCompany();
		HashMap<String, Object> parameters = new HashMap<String, Object> ();
		parameters.put("subject", Config.getStringProperty("SUBMIT_EVENT_EMAIL_SUBJECT"));
		parameters.put("emailTemplate", Config.getStringProperty("SUBMIT_EVENT_EMAIL_TEMPLATE"));
		parameters.put("to", email);
		parameters.put("cc", Config.getStringProperty("SUBMIT_EVENT_COPY_EMAIL"));
		parameters.put("from", company.getEmailAddress());
		parameters.put("title", title);
		parameters.put("submittedBy", submittedBy);
		parameters.put("email", email);
		parameters.put("description", description);
		parameters.put("categoryId", categoryKey);
		
		try {
			EmailFactory.sendParameterizedEmail(parameters, null, host, user);
		} catch (Exception e) {
			Logger.warn(this, e.toString());
		}
	}

	public CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public void setCategoryAPI(CategoryAPI categoryAPI) {
		this.categoryAPI = categoryAPI;
	}
}
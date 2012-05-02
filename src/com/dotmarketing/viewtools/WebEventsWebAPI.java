package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.event_registrations.factories.WebEventRegistrationFactory;
import com.dotmarketing.portlets.event_registrations.model.WebEventAttendee;
import com.dotmarketing.portlets.event_registrations.model.WebEventRegistration;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.portlets.webevents.factories.WebEventLocationFactory;
import com.dotmarketing.portlets.webevents.model.WebEvent;
import com.dotmarketing.portlets.webevents.model.WebEventLocation;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

public class WebEventsWebAPI implements ViewTool {
	private static final java.text.SimpleDateFormat DATE_TO_PRETTY_HTML_DATE_2 = new java.text.SimpleDateFormat(
    "MMMM d, yyyy");
    private HttpServletRequest request;

    public void init(Object obj) {
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();

    }
    
    public void init(HttpServletRequest req, HttpServletResponse response) {
        this.request = req;

    }

	public WebEvent getWebEvent(String inode) {
		return (WebEvent) InodeFactory.getInodeOfClassByCondition(WebEvent.class, "inode = '" + inode + "' and show_on_web = " + DbConnectionFactory.getDBTrue());
	}
	
	public WebEventLocation getWebEventLocation(String inode) {
		return (WebEventLocation) InodeFactory.getInode(inode, WebEventLocation.class);
	}
	
	@Deprecated
	public WebEvent getWebEvent(long inode) {
		return getWebEvent(String.valueOf(inode));
	}
	
	public List getWebEventFiles(WebEvent e) {
		ArrayList<HashMap> files = new ArrayList<HashMap>();
		List identifiers = InodeFactory.getChildrenClass(e, Identifier.class);
		Iterator it = identifiers.iterator();
		while (it.hasNext()) {
			Identifier identifier = (Identifier)it.next();
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
			HashMap<String,String> hm = new HashMap<String,String>();
			hm.put("filepath", identifier.getURI());
			hm.put("filename", file.getTitle());
			files.add(hm);
		}
		return files;
	}
	
	@Deprecated
	public WebEventLocation getWebEventLocation(long inode) {
		return getWebEventLocation(String.valueOf(inode));
	}
	
		
	public List getWebEventLocationsPerEvent(String eventInode) {
		return WebEventLocationFactory.getWebEventLocationsPerEvent(eventInode, "start_date");
	}
	
	@Deprecated
	public List getWebEventLocationsPerEvent(long eventInode) {
		return getWebEventLocationsPerEvent(String.valueOf(eventInode));
	}
	
	public List getWebEventLocationsPerEvent(WebEvent event) {
		return WebEventLocationFactory.getWebEventLocationsPerEvent(event.getInode(), "start_date");
	}
	
	public List getUpcomingWebEventLocationsPerEvent(WebEvent event) {
		List locations = WebEventLocationFactory.getUpcomingWebEventLocationsPerEvent(event.getInode(), "start_date");
//		return locations.subList(0, locations.size() > 3?3:locations.size());
		return locations;
	}
	
	public List getPastWebEventLocationsPerEvent(WebEvent event) {
		List locations = WebEventLocationFactory.getPastWebEventLocationsPerEvent(event.getInode(), "start_date");
		return locations.subList(0, locations.size() > 3?3:locations.size());
	}
	
	public List getUpcomingWebEvents() {
		List events = WebEventFactory.getUpcomingPublicWebEvents();
    	Collections.sort(events);
    	return events;
	}
	
	public List getUpcomingWebEvents(boolean institute) {
		List events = WebEventFactory.getUpcomingPublicWebEvents(institute);
    	Collections.sort(events);
    	return events;
	}
	
	private List<WebEvent> sortEventsByTitle(List<WebEvent> webEvents){
		List<WebEvent> result = new ArrayList<WebEvent>(30);
		
		int i;
		WebEvent tempWebEvent;
		for (WebEvent webEvent: webEvents) {
			if (result.size() == 0) {
				result.add(webEvent);
			} else {
				for (i = 0; i < result.size(); ++i) {
					tempWebEvent = result.get(i);
					if (0 < tempWebEvent.getTitle().compareToIgnoreCase(webEvent.getTitle())) {
						break;
					}
				}
				
				result.add(i, webEvent);
			}
		}
		
		return result;
	}
	
	public List getUpcomingWebEventsOrderByTitle(boolean institute) {
		List<WebEvent> events = WebEventFactory.getUpcomingPublicWebEvents(institute);
		events = sortEventsByTitle(events);
		return events;
	}

	public List getPastWebEvents () {
		return WebEventFactory.getPastWebEvents();
	}
	
	public float getEventLocationPrice (WebEventLocation loc) {
		
		if (request.getSession().getAttribute("isPartner") != null && request.getSession().getAttribute("isPartner").equals("true")){
			Organization organization = (Organization)request.getSession().getAttribute("userOrganization");
			if (organization != null){
				if (loc.isDefaultContractPartnerPrice())
					if (organization.getInstitute_price() > 0)
						return organization.getInstitute_price();
					else
						return loc.getPartnerPrice();
				else
					return loc.getPartnerPrice();
		}	
			else{
				return loc.getNonPartnerPrice();
			}
		}
		else
			return loc.getNonPartnerPrice();
			
			
	}
	
	public boolean isEventLocationAlmostAtCapacity (WebEventLocation loc) {
		return loc.isAlmostAtCapacity();	
	}

	public WebEventRegistration getRegistration (String registrationInode) {
		return WebEventRegistrationFactory.getWebEventRegistration(registrationInode);
	}

	public List<WebEventAttendee> getRegistrationAttendees (WebEventRegistration reg) {
		if (reg == null)
			return new ArrayList<WebEventAttendee>();
		
		return WebEventRegistrationFactory.getEventAttendees(reg);
	}
	
	@Deprecated
	public String getHotelLinkURL (long linkInode) {
		try {
			return getHotelLinkURL (String.valueOf(linkInode));
		} catch (Exception e) {
			Logger.error(this, "WebEvents getHotelLinkURL Method : Unable to parse to String " ,e);
		}
		return "";
	}
	
	public String getHotelLinkURL (String linkInode) {
		Identifier id = (Identifier) InodeFactory.getInode(linkInode, Identifier.class);
		Link link = (Link) IdentifierFactory.getLiveChildOfClass(id, Link.class);
		return link.getWorkingURL();
	}
	
	@Deprecated
	public String getPastEventLinkURL (long linkInode) {
		try {
			return getPastEventLinkURL (String.valueOf(linkInode));
		} catch (Exception e) {
			Logger.error(this, "WebEvents getPastEventLinkURL Method : Unable to parse to String " ,e);
		}
		return "";
	}
	
	public String getPastEventLinkURL (String linkInode) {
		Identifier id = (Identifier) InodeFactory.getInode(linkInode, Identifier.class);
		Link link = (Link) IdentifierFactory.getLiveChildOfClass(id, Link.class);
		return link.getWorkingURL();
	}
	
	public String getEventDateRange (Date date1, Date date2) {
		return UtilMethods.getEventDateRange(date1, date2);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map> getPaymentMethods (boolean institute) {
		List<Map> paymentsList = new ArrayList<Map> ();
		String[] paymentMethods;
		if (institute) {
			paymentMethods = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
		}
		else {
			paymentMethods = Config.getStringArrayProperty("EREG_WEBINAR_PAYMENT_TYPES");
		}
		String partnerOnly = "EREG_PURCHASE_ORDER";
		
		for (String paymentMethod : paymentMethods) {
			
			String value = Config.getStringProperty(paymentMethod);
			String friendlyName = Config.getStringProperty(paymentMethod + "_FN");
			Map paymentMethodMap = new HashMap ();
			paymentMethodMap.put("value", value);
			paymentMethodMap.put("friendlyName", friendlyName);
			paymentMethodMap.put("name", paymentMethod);

			if (paymentMethod.equals(partnerOnly)) {
				if (request.getSession().getAttribute("isPartner") != null && request.getSession().getAttribute("isPartner").equals("true")){
					paymentsList.add(paymentMethodMap);
				}				
			}
			else {
				paymentsList.add(paymentMethodMap);
			}
		}
		return paymentsList;
	}
	
	public List<Map> getPaymentMethods () {
		List<Map> paymentsList = new ArrayList<Map> ();
		String[] paymentMethods = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
		for (String paymentMethod : paymentMethods) {
			String value = Config.getStringProperty(paymentMethod);
			String friendlyName = Config.getStringProperty(paymentMethod + "_FN");
			Map paymentMethodMap = new HashMap ();
			paymentMethodMap.put("value", value);
			paymentMethodMap.put("friendlyName", friendlyName);
			paymentMethodMap.put("name", paymentMethod);
			paymentsList.add(paymentMethodMap);
		}
		return paymentsList;
	}

	public List<String> getCreditCardTypes () {
		String[] cardTypes = Config.getStringArrayProperty("EREG_CREDIT_CARD_TYPES");
		ArrayList<String> retList = new ArrayList<String>();
		for (String type : cardTypes) {
			retList.add(type);
		}
		return retList;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebEventRegistration> getWebEventRegistrationByUser(String userId){
		if (userId == null)
			return new ArrayList<WebEventRegistration>();
		String inode;
		try {
			inode = String.valueOf(com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false).getInode());
		} catch (Exception e) {
			Logger.error(this, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}	
		return WebEventRegistrationFactory.getWebEventRegistrationsByUser(inode,"date_posted desc");
	}
	
	@SuppressWarnings("unchecked")
	public List<WebEventRegistration> getWebEventRegistrationByUserInode(String inode){
		if (inode == null)
			return new ArrayList<WebEventRegistration>();
		
		
		return WebEventRegistrationFactory.getWebEventRegistrationsByUser(inode);
	}
	
	public java.util.HashMap getRegistrationStatus() {
    	
    	//statuses list
    	String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("EREG_STATUSES");
    	java.util.HashMap<String, String> statuses = new java.util.HashMap<String, String>();
    	for (int i=0;i<statusesArray.length;i++) {
    		String status = statusesArray[i];
    		statuses.put(com.dotmarketing.util.Config.getStringProperty(status),com.dotmarketing.util.Config.getStringProperty(status + "_FN"));
    	}
    	return statuses;
    	
    }
	
	public String valueOfStatus(Object value){
		
		return String.valueOf(value);
	}
	 public String dateToPrettyHTMLDate(java.util.Date x) {
	        if (x == null) {
	            return "";
	        }

	        return DATE_TO_PRETTY_HTML_DATE_2.format(x);
	    }
	 
	 
	 public String getContactOrganizationTitle(WebEventRegistration registration){
		 String title = "";
		 Organization organization;
		 if (registration != null){
			 UserProxy userProxy;
			try {
				userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(registration.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
				throw new DotRuntimeException(e.getMessage(), e);
			}	
			 if (InodeUtils.isSet(userProxy.getInode())){
				 organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
				 if (InodeUtils.isSet(organization.getInode())){
					 title = organization.getTitle()!=null?organization.getTitle():"";
				 }
			 }
		 }
		 return title;
	 }
}
/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.webevents.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portlets.webevents.factories.WebEventFactory;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;

/**
 * @author David Torres
 *  
 */
public class ViewEventsAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        if (req.getWindowState().equals(WindowState.NORMAL)) {
            _viewEvents(mapping, form, config, req, res);
            return mapping.findForward("portlet.ext.webevents.view");
        } else {
            _viewEvents(mapping, form, config, req, res);
            return mapping.findForward("portlet.ext.webevents.view_events");
        }
    }

    private void _viewEvents(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
            PortletResponse res) throws Exception {

    	String keywords = req.getParameter("keywords");
    	String showPastEvents = req.getParameter("showPastEvents");
    	List events = new ArrayList();
    	List eventsNoLocations = new ArrayList();
    	
    	if ("true".equals(showPastEvents)) {
	    	if (UtilMethods.isSet(keywords)) {
		    	//Get all current events
		    	events = WebEventFactory.getAllWebEventsByKeywords(keywords);
	    	}
	    	else {
		    	//Get all current events
		    	events = WebEventFactory.getAllWebEvents();
		    }
    	}
    	else {
	    	if (UtilMethods.isSet(keywords)) {
		    	//Get all current events
		    	events = WebEventFactory.getUpcomingWebEventsByKeywords(keywords);
		    	eventsNoLocations = WebEventFactory.getEventsWithNoLocationsByKeywords(keywords);
		    	events.addAll(eventsNoLocations);
	    	}
	    	else {
		    	//Get all current events
		    	events = WebEventFactory.getUpcomingWebEvents();
		    	eventsNoLocations = WebEventFactory.getEventsWithNoLocations();
		    	events.addAll(eventsNoLocations);
		    }
    	}
    	Set eventsSet = new HashSet();
    	eventsSet.addAll(events);
    	events = new ArrayList();
    	events.addAll(eventsSet);
    	Collections.sort(events);
        req.setAttribute(WebKeys.WEBEVENTS_VIEW, events);
    }

}

/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.eventsapproval.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.events.factories.EventFactory;

/**
 * @author David Torres
 *
 */
public class ViewEventsAction extends DotPortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        if (req.getWindowState().equals(WindowState.NORMAL)) {
        	_viewEvents (mapping,form, config, req, res);
            return mapping.findForward("portlet.ext.eventsapproval.view");
        } else {
        	_viewEvents (mapping,form, config, req, res);
        	return mapping.findForward("portlet.ext.eventsapproval.view_events");
        }
    }

    private void _viewEvents (ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            PortletResponse res) throws Exception {

        List events = EventFactory.getEventsWaitingForApproval();
        
    	req.setAttribute("events", events);
    	
    }

}

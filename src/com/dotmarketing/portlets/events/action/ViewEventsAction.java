/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.events.action;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.model.User;

/**
 * @author David Torres
 * 
 */
public class ViewEventsAction extends DotPortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			RenderResponse res) throws Exception {
		if (req.getWindowState().equals(WindowState.NORMAL)) {
			_setFilters(mapping, form, config, req, res);
			return mapping.findForward("portlet.ext.events.view");
		} else {
			_setFilters(mapping, form, config, req, res);
			_viewEvents(mapping, form, config, req, res);
			return mapping.findForward("portlet.ext.events.view_events");
		}
	}

	private void _setFilters(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			PortletResponse res) throws Exception {

		PortletSession session = req.getPortletSession();

		// Filtering parameters

		String facilityInode = "";

		if (req.getParameter("resetFilters") == null || !req.getParameter("resetFilters").equals("true")) {
			try {
				facilityInode = (String) session.getAttribute("facilityInode");
			} catch (Exception e) {
			}
			try {
				facilityInode = (String) req.getParameter("facilityInode");
			} catch (Exception e) {
			}
		}
		if(req.getParameter("facilityInode") == null){
			facilityInode ="";
		}
		session.setAttribute("facilityInode", facilityInode);
		
		req.setAttribute("facilityInode", facilityInode);

		req.setAttribute("selectedCategories", req.getParameter("selectedCategories"));

	}

	private void _viewEvents(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			PortletResponse res) throws Exception {

		PortletSession session = req.getPortletSession();
		PortletRequest request = req;

		GregorianCalendar cal = new java.util.GregorianCalendar();
		GregorianCalendar today = new java.util.GregorianCalendar();
		GregorianCalendar lastDayOfMonth = new java.util.GregorianCalendar();
		GregorianCalendar firstDayOfMonth = new java.util.GregorianCalendar();
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);

		try {
			cal.set(GregorianCalendar.MONTH, Integer.parseInt(request.getParameter("month")));
		} catch (Exception e) {
		}

		try {
			cal.set(GregorianCalendar.YEAR, Integer.parseInt(request.getParameter("year")));
		} catch (Exception e) {
		}

		firstDayOfMonth.setTime(cal.getTime());
		firstDayOfMonth.set(GregorianCalendar.DAY_OF_MONTH, 1);

		lastDayOfMonth.setTime(cal.getTime());
		lastDayOfMonth.add(GregorianCalendar.MONTH, 1);
		lastDayOfMonth.set(GregorianCalendar.DAY_OF_MONTH, 1);
		lastDayOfMonth.add(GregorianCalendar.SECOND, -1);

		java.util.List events = null;

		String selectedCategories = (String) req.getAttribute("selectedCategories");
		if (selectedCategories == null)
			selectedCategories = "";
		List<Inode> parents = new ArrayList<Inode>();
		StringTokenizer st = new StringTokenizer(selectedCategories, ",");
		while (st.hasMoreTokens()) {
			try {
				Category cat = new Category();
				cat.setInode(st.nextToken());
				parents.add(cat);
			} catch (Exception e) {
			}
		}

		String facilityInode =  req.getAttribute("facilityInode").toString();
		if (InodeUtils.isSet(facilityInode)) {
			Inode x = new Inode();
			x.setInode(facilityInode);
			parents.add(x);
		}
		User user = _getUser(req);

		boolean admin = EventFactory.isAnEventAdministrator(user);

		if (admin)
			events = EventFactory.getEventsByDateRangeYParent(firstDayOfMonth.getTime(), lastDayOfMonth.getTime(), parents);
		else
			events = EventFactory.getEventsByDateRangeYParent(firstDayOfMonth.getTime(), lastDayOfMonth.getTime(), parents,
					user);

		req.setAttribute("events", events);
		req.setAttribute("calendar", cal);
		req.setAttribute("isAdmin", new Boolean(admin));
		req.setAttribute("userId", user.getUserId());

	}

}

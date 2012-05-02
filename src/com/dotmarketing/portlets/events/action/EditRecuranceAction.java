/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.events.action;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.portlets.events.factories.RecuranceFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.Recurance;
import com.dotmarketing.portlets.events.struts.RecuranceForm;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Validator;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.util.servlet.SessionMessages;

/**
 * @author David Torres
 *
 */
public class EditRecuranceAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {

        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;

        String referer = req.getParameter("referer");
		if ((referer!=null) && (referer.length()!=0)) {
			referer = URLDecoder.decode(referer,"UTF-8");
		}

		DotHibernate.startTransaction();
		User user = _getUser(req);

		boolean admin = EventFactory.isAnEventAdministrator(user);
    	req.setAttribute("isAdmin", new Boolean (admin));
		
        /*
         * We are editing the recurrence
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
				_editRecurance(req, res, config, form, user);
				setForward(req,"portlet.ext.events.edit_recurance");
	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        
        /*
         * If we are updating the campaign, copy the information
         * from the struts bean to the hbm inode and run the
         * update action and return to the list
         */
        else if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					if (!_saveRecurance(req, res, config, form, user)) {
						setForward(req,"portlet.ext.events.edit_recurance");
						return;
					}
				}
				_sendToReferral(req,res,referer);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        DotHibernate.commitTransaction();

    }

	///// ************** ALL METHODS HERE *************************** ////////

	
	private void _editRecurance(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

        
		RecuranceForm rf = (RecuranceForm) form;
		String eventInode = req.getParameter("parent"); 
		Event e = (Event) InodeFactory.getInode(eventInode, Event.class);
		req.setAttribute("event",e);
		
		Recurance r = (Recurance) InodeFactory.getChildOfClass(e, Recurance.class);

		if (!InodeUtils.isSet(r.getInode())) {
			Calendar startC = GregorianCalendar.getInstance();
			startC.setTime(e.getStartDate());
			Calendar endC = GregorianCalendar.getInstance();
			endC.setTime(e.getEndDate());
			Calendar tempC = GregorianCalendar.getInstance();
			tempC.set(endC.get(Calendar.YEAR), endC.get(Calendar.MONTH), endC.get(Calendar.DATE), 
					startC.get(Calendar.HOUR_OF_DAY), startC.get(Calendar.MINUTE));
			if (tempC.after(endC)) {
				r.setEnding(tempC.getTime());
				tempC.set(startC.get(Calendar.YEAR), startC.get(Calendar.MONTH), startC.get(Calendar.DATE), 
						endC.get(Calendar.HOUR_OF_DAY), endC.get(Calendar.MINUTE));
				r.setStarting(tempC.getTime());
			} else {
				r.setStarting(startC.getTime());
				r.setEnding(endC.getTime());
			}
		}
		req.setAttribute("recurance",r);

		BeanUtils.copyProperties(rf, r);
		
	}

	private boolean _saveRecurance(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

		RecuranceForm rf = (RecuranceForm) form;
		Recurance r = (Recurance) InodeFactory.getInode(rf.getInode(), Recurance.class);
		if (_recuranceChanged(r, rf)) {
	        Event e = (Event) InodeFactory.getInode(rf.getParent(), Event.class);

	        Recurance recuranceCopy = new Recurance ();
	        BeanUtils.copyProperties(recuranceCopy, rf);
			Facility newFacility = (Facility) InodeFactory.getParentOfClass(e, Facility.class);
			if (!req.getParameter("continueWithConflicts").equals("true") && EventFactory.findConflicts(e, recuranceCopy, newFacility).size() > 0) {
				req.setAttribute("event",e);
				req.setAttribute("recurance",r);
				req.setAttribute("recuranceForm",rf);
			    SessionMessages.add(req, "error", "message.event.recurance.has.conflicts");
			    req.setAttribute("conflict_found", "true");
			    return false;
			}

			BeanUtils.copyProperties(r, rf);
			InodeFactory.saveInode(r);
	        e.setStartDate(r.getStarting());
	        e.setEndDate(r.getEnding());
	        e.setApprovalStatus(com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS);
	        RecuranceFactory.buildRecurringEvents(r,e);

	        e = (Event) InodeFactory.getParentOfClass(r, Event.class);
	        if (InodeUtils.isSet(e.getInode())) {
	    		Host host = WebAPILocator.getHostWebAPI().getCurrentHost(req);
	            EventFactory.sendEmailNotification(e, newFacility, user, true, host);
	        }
		}
		return true;
	}
	
	private boolean _recuranceChanged (Recurance r, RecuranceForm rf) {
	    EqualsBuilder eb = new EqualsBuilder ();
	    eb.append(rf.getDayOfMonth(), r.getDayOfMonth());
	    eb.append(rf.getDaysOfWeek(), r.getDaysOfWeek());
	    eb.append(rf.getStarting(), r.getStarting());
	    eb.append(rf.getEnding(), r.getEnding());
	    eb.append(rf.getInterval(), r.getInterval());
	    eb.append(rf.getOccurs(), r.getOccurs());
	    return !eb.isEquals();
	}
	
}


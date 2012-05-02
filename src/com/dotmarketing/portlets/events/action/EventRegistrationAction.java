package com.dotmarketing.portlets.events.action;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.EventRegistration;
import com.dotmarketing.portlets.events.struts.EventRegistrationForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;

/**
 * <a href="LoginAction.java.html"> <b><i>View Source </i> </b> </a>
 *
 * @author Brian Wing Shun Chan
 * @version $Revision: 1.12 $
 *
 */
public class EventRegistrationAction extends DispatchAction {
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Logger.debug(EventRegistrationAction.class, "Loading Event Registration");
		try
		{
			EventRegistrationForm form = (EventRegistrationForm) lf;
			form.setRegistationRandomId(new Random().nextInt());

			String event_inode = request.getParameter("event_inode");
			Event e = (Event) InodeFactory.getInode(event_inode, Event.class);
			request.setAttribute("event", e);

			form.setEventInode(e.getInode());
			request.setAttribute("eventRegistrationForm", form);

			Logger.debug(EventRegistrationAction.class, "END Loading Event Registration");
			if (e.isRegistration()) {
				ActionForward af = (mapping.findForward("eventRegistrationPage"));
				return af;
			} else {
				return null;
			}
		}
		catch(Exception ex)
		{
			Logger.debug(EventRegistrationAction.class, ex.toString());
			throw ex;
		}
	}
	
	public ActionForward list(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionForward af = (mapping.findForward("eventRegistrationListPage"));
		return af;
	}
	
	public ActionForward save(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Logger.debug(EventRegistrationAction.class, "Saving Event Registration");
		try
		{
			//Getting and setting request attributes
			EventRegistrationForm form = (EventRegistrationForm) lf;
			String event_inode = form.getEventInode();		

			Event e = (Event) InodeFactory.getInode(event_inode, Event.class);

			request.setAttribute("eventRegistrationForm", form);
			request.setAttribute("event", e);

			if (e==null || !InodeUtils.isSet(e.getInode())) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR,new ActionError("There was an error with your registration."));			
				saveErrors(request, errors);
				return mapping.getInputForward();
			}

			//Checking errors
			ActionErrors aes = form.validate(mapping, request);
			if(aes != null && aes.size() > 0){
				saveErrors(request, aes);			
				return mapping.getInputForward();
			}		

			//Checking repetead registrations
			HttpSession s = request.getSession();
			EventRegistrationForm lastReg = (EventRegistrationForm) s.getAttribute("lastEventRegistrationForm");
			if (lastReg != null && form.getRegistationRandomId() == lastReg.getRegistationRandomId())
			{
				return mapping.findForward("registrationSuccessfulPage");
			}
			s.setAttribute("lastEventRegistrationForm", form);		

			//Saving registration
			EventRegistration registration = (EventRegistration) InodeFactory.getInode("", EventRegistration.class);
			BeanUtils.copyProperties(registration, form);
			InodeFactory.saveInode(registration);     	

			//Association with the event 
			e.addChild(registration);
			InodeFactory.saveInode(e);        

			//Sending email
			String fromEmail = request.getParameter("fromEmail");
			Mailer mailer = new Mailer();
			mailer.setFromEmail(fromEmail);
			mailer.setToEmail(form.getEmail());
			mailer.setBcc(fromEmail);
			mailer.setSubject("Registration for the event: " + e.getTitle());
			mailer.setHTMLAndTextBody(_buildEmailBody(e, registration));
			mailer.sendMessage();            

			//Forwarding to the page
			ActionForward af = mapping.findForward("registrationSuccessfulPage");
			Logger.debug(EventRegistrationAction.class, "END Saving Event Registration");
			return af;
		}
		catch(Exception ex)
		{
			Logger.debug(EventRegistrationAction.class, ex.toString());
			throw ex;
		}
	}
	
	private String _buildEmailBody (Event e, EventRegistration reg) throws DotDataException, DotSecurityException {
		
		HostAPI hostAPI = APILocator.getHostAPI();
		Host host = hostAPI.findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);

		StringBuffer sb = new StringBuffer ();
		sb.append("<html><link rel=\"stylesheet\" href=\"http://" + host.getHostname() + "/global/css/masterStyle.css\" type=\"text/css\"><body bgcolor=\"white\">");
		sb.append("<div id=\"bodyArea\"><table border=\"1\" id=\"bodyID\" bgcolor=\"#ffffff\" cellpadding=\"4\" cellspacing=\"2\">");
		sb.append("<img src=\"http://" + host.getHostname() + "/global/images/logo.gif\"><BR>");
		sb.append("<h2>You have been successfully registered to Event: " + (e.getTitle()==null?"":e.getTitle()) + ".<br></h2>");
		sb.append("<br>Here is your registration information:<br>");
		sb.append("<B>Registration Date:</B> ");
		sb.append(UtilMethods.dateToPrettyHTMLDate(reg.getRegistrationDate())); 
		sb.append("<br>");
		sb.append("<B>Name:</B> " + reg.getFullName());
		sb.append("<br>");		
		sb.append("<B>Registration Comments:</B><br>");
		sb.append(reg.getComments());
		sb.append("<br>");
		//sb.append("<B>Number Attending:</B> ");
		//sb.append(reg.getNumberAttending());
		sb.append("</td></tr></table></div></body></html>");
		return sb.toString();
	}
}

package com.dotmarketing.portlets.events.action;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.struts.SendVCalendarDetailForm;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

/**
 * 
 * Action that let you submit an event from the frontend and send a email with the event in vcalendar format
 * check the struts-cms.xml to see how this action is mapped to struts
 * 
 * @author Armando Siem
 * 
 */
public class SendVCalendarDetail extends DispatchAction {
	public SendVCalendarDetail() {
	}
	
	private static class StringDataSource implements DataSource {
        private String contents ;
        private String mimetype ;
        private String name ;
        
        public StringDataSource(String contents, String mimetype, String name) {
            this.contents = contents ;
            this.mimetype = mimetype ;
            this.name = name ;
        }
        
        public String getContentType() {
            return mimetype;
        }
        
        public String getName() {
            return name;
        }
        
        public InputStream getInputStream() {
            return new StringBufferInputStream(contents);
        }
        
        public OutputStream getOutputStream() {
            throw new IllegalAccessError("This datasource cannot be written to");
        }
    }
	
	/**
	 * This is the default method. Get the email info and the event inode to create the body and send the email.
	 * @param	mapping ActionMapping.
	 * @param	lf ActionForm.
	 * @param	request HttpServletRequest.
	 * @param	response HttpServletResponse.
	 * @return	ActionForward.
	 * @exception	Exception.
	 */
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String referer = request.getHeader("referer");
		ActionForward af = new ActionForward(referer);
		af.setRedirect(true);
		
		try {
			SendVCalendarDetailForm sendVCalendarDetailForm = (SendVCalendarDetailForm) lf;
			ActionErrors errors = new ActionErrors();
			
			if (!InodeUtils.isSet(sendVCalendarDetailForm.getEventInode())) {
				errors.add(Globals.ERROR_KEY, new ActionMessage("errors.event.invalid"));
				request.getSession().setAttribute(Globals.ERROR_KEY, errors);
				
				return af;
			}
			
			if (!UtilMethods.isSet(sendVCalendarDetailForm.getToEmail())) {
				errors.add(Globals.ERROR_KEY, new ActionMessage("errors.email", sendVCalendarDetailForm.getToEmail()));
				request.getSession().setAttribute(Globals.ERROR_KEY, errors);
				
				return af;
			}
			
			if (!UtilMethods.isSet(sendVCalendarDetailForm.getFromEmail())) {
				errors.add(Globals.ERROR_KEY, new ActionMessage("errors.email", sendVCalendarDetailForm.getFromEmail()));
				request.getSession().setAttribute(Globals.ERROR_KEY, errors);
				
				return af;
			}
			
			Event event = (Event) InodeFactory.getInode(sendVCalendarDetailForm.getEventInode(), Event.class);
			if (!UtilMethods.isSet(event)) {
				errors.add(Globals.ERROR_KEY, new ActionMessage("errors.event.invalid"));
				request.getSession().setAttribute(Globals.ERROR_KEY, errors);
				
				return af;
			}
			
			String vCalendarBody = createVCalendarEventEmailBody(event);
			
			String fromEmail = sendVCalendarDetailForm.getFromEmail();
			String fromName = sendVCalendarDetailForm.getFromName();
			String toEmail = sendVCalendarDetailForm.getToEmail();
			String cc = sendVCalendarDetailForm.getCc();
			String bcc = sendVCalendarDetailForm.getBcc();
			String subject = sendVCalendarDetailForm.getSubject();
			String toName = sendVCalendarDetailForm.getToName();
			
			if (!sendVCalendarEventEmail(fromEmail, fromName, toEmail, cc, bcc, subject, toName, vCalendarBody, event.getDescription())) {
				errors.add(Globals.ERROR_KEY, new ActionMessage("error.processing.your.email"));
				request.getSession().setAttribute(Globals.ERROR_KEY, errors);
			} else {
				ActionMessages messages = new ActionMessages();
				messages.add(Globals.MESSAGE_KEY, new ActionMessage("message.event.vcalendar.email_sent"));
				request.getSession().setAttribute(Globals.MESSAGE_KEY, messages);
			}
		} catch (Exception e) {
			Logger.warn(this, e.toString());
		}
		
		return af;
	}
	
	/**
	 * Create the email body in VCalendar format from a event inode
	 * @param	event Event.
	 * @return	String.
	 */
	private String createVCalendarEventEmailBody(Event event) {
		StringBuilder result = new StringBuilder(512);
		result.ensureCapacity(128);
		
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat timeformat = new SimpleDateFormat("HHmm");
			
			java.util.Calendar gcal = new GregorianCalendar();
			gcal.set(GregorianCalendar.HOUR, 8);
			
			gcal.setTime(event.getStartDate());
			String startTime = timeformat.format(gcal.getTime());
			
			gcal.setTime(event.getEndDate());
			String endTime = timeformat.format(gcal.getTime());
			
			StringBuilder description = new StringBuilder(512);
			description.ensureCapacity(128);
			
			description.append(event.getTitle() + "=0D=0A=");
			SimpleDateFormat descriptionDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			description.append("Date: " + descriptionDateFormat.format(event.getStartDate()) + "=0D=0A=");
			SimpleDateFormat descriptionTimeFormat = new SimpleDateFormat("HH:mm:ss aa");
			description.append("Time: " + descriptionTimeFormat.format(event.getStartDate()) + " - " + descriptionTimeFormat.format(event.getEndDate()) + "=0D=0A=");
			description.append("Location: " + event.getLocation() + "=0D=0A=");
			description.append("Contact: " + event.getContactName() + "=0D=0A=");
			description.append("Organization: " + event.getContactCompany() + "=0D=0A=");
			description.append("Contact Phone: " + event.getContactPhone() + "=0D=0A=");
			description.append("Contact Email: " + event.getContactEmail() + "=0D=0A=");
			description.append("Description: " + event.getDescription());
			
			result.append("BEGIN:VCALENDAR\n");
			result.append("PRODID:" + Config.getStringProperty("PRODID") + "\n");
			result.append("METHOD:" + Config.getStringProperty("METHOD") + "\n");
			result.append("SCALE:" + Config.getStringProperty("SCALE") + "\n");
			result.append("VERSION:" + Config.getStringProperty("VERSION") + "\n");
			result.append("BEGIN:VEVENT\n");
			try {
				result.append("DTSTART:" + dateformat.format(event.getStartDate()) + "T" + startTime + "00\n");
			} catch(NullPointerException ex) {
				result.append("DTSTART:" + dateformat.format(event.getStartDate()) + "T\n");
			}
			try {
				result.append("DTEND:" + dateformat.format(event.getEndDate()) + "T" + endTime + "00\n");
			} catch(NullPointerException ex) {
				result.append("DTEND:" + dateformat.format(event.getEndDate()) + "T\n");
			}
			result.append("LOCATION:" + (event.getLocation() == null ? "" : event.getLocation()) + "\n");
			result.append("UID:040000008200E00074C5B7101A82E00800\n");
			result.append("DESCRIPTION;ENCODING=QUOTED-PRINTABLE:");
			result.append(description.toString().trim().replaceAll("\r\n", "=0D=0A="));
			result.append("\n");
			result.append("SUMMARY;ENCODING=QUOTED-PRINTABLE:");
			result.append(event.getTitle());
			result.append("\n");
			result.append("PRIORITY:1\n");
			result.append("END:VEVENT\n");
			result.append("END:VCALENDAR\n");
		} catch (Exception e) {
			Logger.warn(this, e.toString());
		}
		
		return result.toString();
	}
	
	/**
	 * Send the email with an email body in VCalendar format
	 * @param	fromEmail String.
	 * @param	fromName String.
	 * @param	toEmail String.
	 * @param	cc String.
	 * @param	bcc String.
	 * @param	subject String.
	 * @param	toName String.
	 * @param	vCalendarBody String.
	 * @param	textBody String.
	 * @return	boolean.
	 */
	private boolean sendVCalendarEventEmail(String fromEmail, String fromName, String toEmail, String cc, String bcc, String subject, String toName, String vCalendarBody, String textBody) {
		try {
			/*
			 * Get the mail session from 
			 * the container Context
			 */
			Session session = null;
			try {
				Context ctx = (Context) new InitialContext().lookup("java:comp/env");
				session = (javax.mail.Session) ctx.lookup("mail/MailSession");
			} catch (NamingException e1) {
				Logger.error(this,e1.getMessage(),e1);
			}

			if(session ==null){
				Logger.debug(this, "No Mail Session Available.");
				return false;
			}

			Logger.debug(this, "Delivering mail using: " + session.getProperty("mail.smtp.host") + " as server.");

			MimeMessage message = new MimeMessage(session);
			
			Multipart mp = new MimeMultipart();
			
			if ((fromEmail != null) && (fromName != null) && (0 < fromEmail.trim().length())) {
				message.setFrom(new InternetAddress(fromEmail, fromName));
			} else if ((fromEmail != null) && (0 < fromEmail.trim().length())) {
				message.setFrom(new InternetAddress(fromEmail));
			}

			if (toName != null) {
				String[] recipients = toEmail.split("[;,]");
				for (String recipient : recipients) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient, toName));
				}
			} else {
				String[] recipients = toEmail.split("[;,]");
				for (String recipient : recipients) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				}
			}
			if (UtilMethods.isSet(cc)) {
				String[] recipients = cc.split("[;,]");
				for (String recipient : recipients) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient));
				}
			}
			if (UtilMethods.isSet(bcc)) {
				String[] recipients = bcc.split("[;,]");
				for (String recipient : recipients) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
				}
			}

			message.setSubject(subject);
			 
	        MimeBodyPart meetingPart = new MimeBodyPart();
	        meetingPart.setDataHandler(new DataHandler(new StringDataSource(vCalendarBody, "text/calendar", "")));
	        mp.addBodyPart(meetingPart, 0);
	        
	        message.setContent(mp);
	        
	        meetingPart = new MimeBodyPart();
	        meetingPart.setText(textBody);
	        mp.addBodyPart(meetingPart, 0);
	        
	        message.setContent(mp);
	
	        // send message
	        Transport.send(message);
		} catch (Exception e) {
			Logger.warn(this, e.toString());
			return false;
		}
		
		return true;
	}
}
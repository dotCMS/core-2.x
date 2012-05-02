/*
 * Created on Mar 29, 2005
 *
 */
package com.dotmarketing.portlets.jobs.factories;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;

/**
 * @author maria
 *
 */
public class EmailFactory {

	public static String sendCareerPostingReceipt(String inode, String email, String type) {
        StringBuffer result = new StringBuffer();
        try {
        	
        		String serverName = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false).getHostname();
	            StringBuffer query = new StringBuffer("inode=");
	            query.append("'"+inode+"'");   
	            query.append("&dispatch=receipt");
            	result = UtilMethods.getURL("http://" + serverName + "/dotCMS/" + type + "?" + query.toString());
            	Logger.debug(EmailFactory.class, "http://" + serverName + "/dotCMS/" + type + "?" + query.toString());
                Mailer mail = new Mailer();
                mail.setFromEmail(Config.getStringProperty("CAREERS_EMAIL"));
                mail.setToEmail(email); 
                mail.setBcc(Config.getStringProperty("CAREERPOSTING_EMAIL")); 
                mail.setSubject(Config.getStringProperty("CAREERPOSTING_SUBJECT"));
                mail.setHTMLBody(result.toString());
                mail.sendMessage();
        } catch (Exception ex) {
        		Logger.error(EmailFactory.class, "==========================Exception =====================");
        		Logger.error(EmailFactory.class, ex.getMessage());
        }
        return result.toString();

	}		

}

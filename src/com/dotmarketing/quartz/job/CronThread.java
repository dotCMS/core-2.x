package com.dotmarketing.quartz.job;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portlets.campaigns.factories.CampaignFactory;
import com.dotmarketing.util.Logger;


public class CronThread implements Runnable {
    private int emailCampaign = 1; //every minute

    public CronThread() {
        //super(name);
    }

    public void run() {
	    Logger.info(this, "Starting annoying CronThread running every minute");
        
        GregorianCalendar greg = null;

        /* unlock all email
         * campaigns in case
         * we died in the midst of sending
         */
        CampaignFactory.unlockAllCampaigns();

        //Fire up the cron thread
        while (true) {
            greg = new GregorianCalendar();

            try {
                try {
                	if ((greg.get(Calendar.MINUTE) % emailCampaign) == 0) {
                		//EmailFactory.deliverCampaigns();
                	}
                } catch (Exception e) {
                    Logger.error(this, "CronThread: Error occurred delivering campaigns.", e);
                }
            } finally {
                DotHibernate.closeSession();
            }
    	    Logger.debug(this, "CronThread.Minute: " + greg.get(Calendar.MINUTE));

            try {
                Thread.sleep(1000 * 60); // sleep a minute
            } catch (Exception e) {
                break;
            }
        }
    }
    
  
    /* (non-Javadoc)
     * @see java.lang.Thread#destroy()
     */
    public void destroy() {
        DotHibernate.closeSession();
    }
}

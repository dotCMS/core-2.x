package com.dotmarketing.quartz.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.util.Logger;

public class DistributionJournalCleanup implements Runnable, Job {

    public DistributionJournalCleanup() {
    }

    public void run() 
    {
    	try 
    	{
    		HibernateUtil.startTransaction();
    		APILocator.getDistributedJournalAPI().processJournalEntries();
    		HibernateUtil.commitTransaction();
    	}
    	catch (Exception e) 
    	{
    		Logger.error(this, "Error ocurred while trying to process journal entries.", e);
    		try {
				HibernateUtil.rollbackTransaction();
			} catch (DotHibernateException e1) {
				Logger.error(this, e1.getMessage(), e1);
			}
    	}
    	finally
    	{
    		DotHibernate.closeSession();
    		DbConnectionFactory.closeConnection();
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#destroy()
     */
    public void destroy() 
    {
        
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	Logger.debug(this, "Running DistributionJournalCleanup - " + new Date());

    	try {
			run();
		} catch (Exception e) {
			Logger.info(this, e.toString());
		} finally {
			DotHibernate.closeSession();
		}
	}
}

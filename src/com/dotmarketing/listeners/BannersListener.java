package com.dotmarketing.listeners;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.util.Logger;

/**
 * The listener that keeps track of all clickstreams in the container as well as
 * the creating new Clickstream objects and initiating logging when the
 * clickstream dies (session has been invalidated).
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody </a>
 */
public class BannersListener implements ServletContextListener, HttpSessionListener {

    public static final String BANNERS_ATTRIBUTE_KEY = "banners";
    
    private static Map sesions_banners = Collections.synchronizedMap(new HashMap());

    public BannersListener() {
        Logger.debug(this, "BannersListener constructed");
    }

    public void contextInitialized(ServletContextEvent sce) {

        sce.getServletContext().setAttribute(BANNERS_ATTRIBUTE_KEY, sesions_banners);
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

    public void sessionCreated(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        Logger.debug(this, "Session " + session.getId() + " was created, adding a banners to the session.");
        Set banners = new HashSet();
        session.setAttribute(BANNERS_ATTRIBUTE_KEY, banners);
        sesions_banners.put(session.getId(), banners);
    }

    public void sessionDestroyed(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        Logger.debug(this, "Session " + session.getId() + " was destroyed, logging the banners info and removing it.");

        Set banners = (Set) sesions_banners.get(session.getId());
        
        if (banners != null) {
        	
        	Logger.debug(this, "sessionDestroyed, banners.size(): " + banners.size());
        	try {
        		Iterator it = banners.iterator();
        		while (it.hasNext()) {
        			Banner sessBanner = (Banner) it.next();
        			int numViews = sessBanner.getNmbrViews();
        			int numClicks = sessBanner.getNmbrClicks();
        			String inode = sessBanner.getInode();
        			Logger.debug(this, "sessionDestroyed, banners inode: " + inode + ", views: " + numViews + ", numClicks: " + numClicks);
        			
        			Banner bann = (Banner) InodeFactory.getInode(inode, Banner.class);
        			bann.setNmbrViews(numViews);
        			bann.setNmbrClicks(numClicks);
        			
        			InodeFactory.saveInode(bann);
        		}
        	} catch (Exception e) {
        		// log.error(e.getMessage(), e);
        		Logger.debug(this.getClass(), e.getMessage());
        	} finally {
        		sesions_banners.remove(session.getId());
        		DotHibernate.closeSession();
        	}
        } 
    }
}
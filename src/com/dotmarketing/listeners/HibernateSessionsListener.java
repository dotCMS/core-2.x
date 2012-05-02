package com.dotmarketing.listeners;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.scripting.util.BSFUtil;
import com.dotmarketing.util.Config;


public class HibernateSessionsListener implements ServletRequestListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
	 */
	public void requestDestroyed(ServletRequestEvent arg0) {
		DotHibernate.closeSession();
		DbConnectionFactory.closeConnection();
		if(Config.getBooleanProperty("ENABLE_SCRIPTING", false)){
			BSFUtil.getInstance().terminateThreadLocalManager();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
	 */
	public void requestInitialized(ServletRequestEvent arg0) {
		
	}

}

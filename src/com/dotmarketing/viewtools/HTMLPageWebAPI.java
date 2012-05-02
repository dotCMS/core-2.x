package com.dotmarketing.viewtools;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.web.UserWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.htmlpages.business.HTMLPageAPI;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

public class HTMLPageWebAPI implements ViewTool {

	private HttpServletRequest request;
	private User backuser = null;
	private UserWebAPI userAPI;
	private HTMLPageAPI htmlPageAPI = APILocator.getHTMLPageAPI();
	
	public void init(Object obj) {
		ViewContext context = (ViewContext) obj;
		this.request = context.getRequest();
		userAPI = WebAPILocator.getUserWebAPI();
		try {
			backuser = userAPI.getLoggedInUser(request);
		} catch (Exception e) {
			Logger.error(this, "Error finding the logged in user", e);
		}
	}

	/**
	 * 
	 * @param page
	 * @param container
	 * @return true/false on whether or not a Page has content with a specificed container
	 */
	public boolean hasContent(HTMLPage page, Container container){
		return htmlPageAPI.hasContent(page, container);
	}
	
	/**
	 * This method will currently hit the db
	 * @param path
	 * @param host
	 * @return HTMLPage from a path on a given host 
	 */
	public HTMLPage loadPageByPath(String path, Host host){
		return htmlPageAPI.loadPageByPath(path, host);
	}
	
	/**
	 * Retrives from cache.  
	 * @param path
	 * @param host
	 * @return The Identifer for a specific Page given the path of the page
	 */
	public Identifier loadPageIdentifier(String path, Host host){
		return IdentifierCache.getPathFromIdCache(path, host);
	}
	
	/**
	 * This method finds the HTMLPage depending on the inode and hits the database
	 * @param pageInode
	 * @param user
	 * @param respectedFrontendRoles
	 * @throws DotDataException 
	 */
	public boolean canUserPublish(String pageId, boolean respectedFrontendRoles) throws DotDataException{
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		Identifier pageIdentifier;
		try {
			pageIdentifier = IdentifierCache.getIdentifierFromIdentifierCache(pageId);
		} catch (DotHibernateException e) {
			Logger.error("Unable to retrieve identifier : ",e.getMessage() ,e);
			return false;
		}
		HTMLPage page = (HTMLPage)IdentifierFactory.getWorkingChildOfClass(pageIdentifier, HTMLPage.class);
		
		/*Identifier ident = (Identifier)IdentifierFactory.getIdentifierByInode(pageInode);
		HTMLPage page = HTMLPageFactory.*/
		//System.out.println(perAPI.doesUserHavePermission(page, PermissionAPI.PERMISSION_PUBLISH, user, false));
		return perAPI.doesUserHavePermission(page, PermissionAPI.PERMISSION_PUBLISH, backuser, false);
	}
	
	/**
	 * This method finds the HTMLPage depending on the inode and hits the database
	 * @param pageInode
	 * @param user
	 * @param respectedFrontendRoles
	 * @throws DotDataException 
	 * @deprecated
	 */
	public boolean canUserPublish(long pageId, boolean respectedFrontendRoles) throws DotDataException{
		return canUserPublish(String.valueOf(pageId), respectedFrontendRoles);
	}
	
}

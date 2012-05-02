package com.dotmarketing.beans;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.VirtualLinksCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;

public class CMSRequestObject {

	Identifier identifier;

	String publicUri;

	String translatedUri;

	String redirectUri;

	int responseCode;

	boolean previewMode = false;

	boolean adminMode = false;

	boolean editMode = false;

	String _velocityPageExtenstion;

	Host host;
	User user = null;

	HttpServletRequest request;

	HttpServletResponse response;

	/**
	 * Public constructor for a CMSRequestObject
	 * 
	 * @param req
	 * @param res
	 */
	public CMSRequestObject(ServletRequest req, ServletResponse res) {

		this.request = (HttpServletRequest) req;
		this.response = (HttpServletResponse) res;
		_velocityPageExtenstion = Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
		setPublicUri();
		try {
			setHost();
		} catch (Exception e) {
			throw new DotRuntimeException("Unable to retrieve current request host");
		}
		setUser();
		setLivePreviewEditState();
		setTranslatedUri();
		setIdentifier();
		setResponseCode();

	}

	private void setPublicUri() {
		try {
			this.publicUri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		} catch (UnsupportedEncodingException e) {

			LogFactory.getLog(this.getClass()).debug(e);
		}
	}

	public boolean isAdminMode() {
		if (request.getSession(false) != null) {
			return (request.getSession(false).getAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION) != null);
		}
		return adminMode;
	}

	public boolean isCmsResource() {

		return (InodeUtils.isSet(identifier.getInode()));
	}

	public boolean isEditMode() {
		if (request.getSession(false) != null) {
			return (request.getSession(false).getAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION) != null);
		}
		return editMode;
	}

	private void setHost() throws PortalException, SystemException, DotDataException, DotSecurityException {
		HostWebAPI hostWebAPI = WebAPILocator.getHostWebAPI();
		if (host == null) {
			host = hostWebAPI.getCurrentHost(request);
		}

	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public boolean isPreviewMode() {
		if (request.getSession(false) != null) {
			return (request.getSession(false).getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION) != null);
		}
		return previewMode;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getTranslatedUri() {
		return translatedUri;
	}

	private void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;

		if (adminMode) {
			request.getSession().setAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION, "true");
			request.setAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION, "true");
		} else {
			request.getSession().removeAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION);
			request.removeAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION);

		}
	}

	private void setEditMode(boolean editMode) {
		this.editMode = editMode;
		if (editMode) {
			setPreviewMode(false);
			request.getSession().setAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION, "true");
			request.setAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION, "true");
		} else {
			request.getSession().removeAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION);
			request.removeAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION);

		}

	}
	
	
	private void setUser(){
		
		user = null;
		try {
			if (request.getSession(false) != null)
				user = (com.liferay.portal.model.User) request.getSession(false).getAttribute(
						com.dotmarketing.util.WebKeys.CMS_USER);
		} catch (Exception nsue) {
			Logger.warn(this, "Exception trying to getUser: " + nsue.getMessage(), nsue);
		}

	}

	private void setPreviewMode(boolean previewMode) {
		this.previewMode = previewMode;

		if (previewMode) {
			setEditMode(false);
			request.getSession().setAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION, "true");
			request.setAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION, "true");
		} else {
			request.getSession().removeAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION);
			request.removeAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION);

		}

	}

	public boolean isVelocityPage() {

		if (UtilMethods.isSet(translatedUri)) {
			return (translatedUri.endsWith(_velocityPageExtenstion));
		}
		return false;
	}

	private void setTranslatedUri() {
		if (translatedUri != null) {
			return;
		}

		/* if edit mode and the dotAsset Servlet */
		if (publicUri.startsWith("/dotAsset/")) {
			if (request.getParameter("path") != null) {
				publicUri = request.getParameter("path");
			} else {
				String id = UtilMethods.getFileName(publicUri);
				try {
					identifier = IdentifierCache.getIdentifierFromIdentifierCache(id);
					if (identifier != null) {
						publicUri = identifier.getURI();
					
						if(previewMode || editMode){
							translatedUri = WorkingCache.getPathFromCache(publicUri, host);
						}
						else{
							translatedUri = LiveCache.getPathFromCache(publicUri, host);
						}
						translatedUri = (Config.getStringProperty("ASSET_PATH") + translatedUri).replace('\\','/');
						return;
					}

				} catch (Exception ex) {
					Logger.debug(this.getClass(), "Identifier not found for " + id);
				}
			}
			
		}

		if (previewMode || editMode) {

			translatedUri = WorkingCache.getPathFromCache(publicUri, host);

			// if there is no page, allow the user to create it.
			if (!UtilMethods.isSet(translatedUri)
					&& (publicUri.endsWith(_velocityPageExtenstion) || InodeUtils.isSet(FolderFactory.getFolderByPath(publicUri, host)
							.getInode()))) {
				String url = publicUri;
				if (!publicUri.endsWith(_velocityPageExtenstion)) {
					url = url + "index." + _velocityPageExtenstion;
				}
				responseCode = 302;
				translatedUri = "/html/portlet/ext/htmlpages/page_not_found_404.jsp?url=" + url + "&hostId="
						+ host.getIdentifier();

			}

			/* if live mode */
		} else {

			translatedUri = LiveCache.getPathFromCache(publicUri, host);

		}

		// if absolute link somewhere else (I don't why we whould have this)
		if (UtilMethods.isSet(translatedUri)
				&& (translatedUri.startsWith("http://") || translatedUri.startsWith("https://"))) {

			responseCode = 302;
		}

		// Next, try virtual links;
		if (!UtilMethods.isSet(translatedUri)) {
			String testUri = publicUri;
			if (publicUri.endsWith("/")) {
				testUri = publicUri.substring(0, publicUri.length() - 1);
			}
			translatedUri = VirtualLinksCache.getPathFromCache(host.getHostname() + ":" + testUri);

			if (!UtilMethods.isSet(translatedUri)) {
				translatedUri = VirtualLinksCache.getPathFromCache(testUri);
			}
			if (UtilMethods.isSet(translatedUri)) {
				request.setAttribute(WebKeys.CLICKSTREAM_URI_OVERRIDE, publicUri);
			}
		}

		/* close db sesson */
		DotHibernate.closeSession();

	}

	private void setResponseCode() {
		if (responseCode > 200) {
			return;
		}
		/*
		 * If someone is trying to go right to an asset without going through
		 * the cms, give them a 403
		 */
		if (publicUri.startsWith(Config.getStringProperty("ASSET_PATH"))) {
			responseCode = 403;
			return;
		}

		// if we don't have a webasset
		if (identifier == null || !InodeUtils.isSet(identifier.getInode())) {
			responseCode = 200;
			return;
		}


		boolean signedIn = (user != null);
/*
		if (!PermissionFactory.userHasReadPermission(user, identifier)) {
			if (signedIn) {
				responseCode = 403;
			} else {
				responseCode = 401;
				request.getSession(true).setAttribute(com.liferay.portal.util.WebKeys.LAST_PATH,
						new ObjectValuePair(publicUri, request.getParameterMap()));
				request.getSession(true).setAttribute(com.dotmarketing.util.WebKeys.REDIRECT_AFTER_LOGIN, publicUri);

			}
		} else {
			responseCode = 200;
		}
*/
	}

	private void setLivePreviewEditState() {
		if ("1".equals(request.getParameter("livePage"))) {
			setPreviewMode(false);
			setEditMode(false);
		} else if ("1".equals(request.getParameter("previewPage"))) {
			setEditMode(true);
		} else if ("2".equals(request.getParameter("previewPage"))) {
			setPreviewMode(true);
		}
	}

	private void setIdentifier() {
		if (identifier != null && InodeUtils.isSet(identifier.getInode())) {
			return;
		} else if (isVelocityPage()) {
			identifier = IdentifierCache.getPathFromIdCache(translatedUri, host);

		} else {
			identifier = IdentifierCache.getPathFromIdCache(publicUri, host);
		}

	}
}

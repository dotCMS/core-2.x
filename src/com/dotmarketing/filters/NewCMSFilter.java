package com.dotmarketing.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.beans.CMSRequestObject;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.ClickstreamFactory;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class NewCMSFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		CMSRequestObject cmsObject = new CMSRequestObject(req, res);
		
		String uri = request.getRequestURI();
		HostWebAPI hostWebAPI = WebAPILocator.getHostWebAPI();
        Host host;
		try {
			host = hostWebAPI.getCurrentHost(request);
		} catch (PortalException e) {
    		Logger.error(this, "Unable to retrieve current request host for URI " + uri);
    		throw new ServletException(e.getMessage(), e);
		} catch (SystemException e) {
    		Logger.error(this, "Unable to retrieve current request host for URI  " + uri);
    		throw new ServletException(e.getMessage(), e);
		} catch (DotDataException e) {
    		Logger.error(this, "Unable to retrieve current request host for URI  " + uri);
    		throw new ServletException(e.getMessage(), e);
		} catch (DotSecurityException e) {
    		Logger.error(this, "Unable to retrieve current request host for URI  " + uri);
    		throw new ServletException(e.getMessage(), e);
		}


		String translatedUri = cmsObject.getTranslatedUri();
		int responseCode = cmsObject.getResponseCode();
		switch (responseCode) {
		case 404:
			response.sendError(404);
			return;
		case 403:
			response.sendError(403);
			return;
		case 401:
			response.sendError(401);
			return;
		case 302:
			response.sendRedirect(translatedUri);
			return;
		}


		// add the CMS_REQUEST_OBJECT to request
		request.setAttribute("CMS_REQUEST_OBJECT", cmsObject);


		if(translatedUri != null){
			request.getRequestDispatcher(translatedUri).forward(request, response);
			return;
		}


		chain.doFilter(req, res);

	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
package com.dotmarketing.velocity;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_CAN_ADD_CHILDREN;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_PUBLISH;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

import com.dotcms.enterprise.LicenseUtil;
import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.portal.PortletAPI;
import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.ClickstreamFactory;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.containers.factories.ContainerFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.htmlpages.business.HTMLPageAPI;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.languagesmanager.business.LanguageAPI;
import com.dotmarketing.portlets.languagesmanager.model.Language;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.workflows.factories.WorkflowsFactory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.CookieUtil;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PortletURLUtil;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.VelocityProfiler;
import com.dotmarketing.util.VelocityUtil;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.viewtools.HTMLPageWebAPI;
import com.dotmarketing.viewtools.RequestWrapper;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.util.servlet.SessionMessages;

public abstract class VelocityServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContentletAPI conAPI = APILocator.getContentletAPI();

	private static PortletURLUtil portletURLUtil = new PortletURLUtil();

	private static UtilMethods utilMethods = new UtilMethods();

	private static InodeUtils inodeUtils = new InodeUtils();

	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();

	private static PortletAPI portletAPI = APILocator.getPortletAPI();

	private static LanguageAPI langAPI = APILocator.getLanguageAPI();

	private static HostWebAPI hostWebAPI = WebAPILocator.getHostWebAPI();

	/**
	 * @param permissionAPI
	 *            the permissionAPI to set
	 */
	public static void setPermissionAPI(PermissionAPI permissionAPIRef) {
		permissionAPI = permissionAPIRef;
	}

	private String CHARSET = null;

	private String VELOCITY_HTMLPAGE_EXTENSION = null;

	private VelocityEngine ve = null;

	private static List<Language> languages = null;

	private static ToolboxManager toolboxManager = null;

	private static ServletContext myContext;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL) && LicenseUtil.getLevel() < 299){
			request.getRequestDispatcher("/portal/no_license.jsp").forward(request, response);
			return;
		}
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE) && LicenseUtil.getLevel() < 399){
			request.getRequestDispatcher("/portal/no_license.jsp").forward(request, response);
			return;
		}
		if(!LicenseUtil.isASAllowed()) {
			request.getRequestDispatcher("/portal/no_license.jsp").forward(request, response);
			return;

		}
    	Long profileTime=null;
    	if (Config.getBooleanProperty("VELOCITY_PROFILING", false)) {
    		profileTime=Calendar.getInstance().getTimeInMillis();
    	}
    	try {

    		//Check if the uri is a physical file. Fix for the cases when the site configure VELOCITY_PAGE_EXTENSION as htm, html or any known extension.
    		//Example: /html/js/tinymce/jscripts/tiny_mce/plugins/advlink/link.htm
    		String uri = request.getRequestURI();
            uri = URLDecoder.decode(uri, "UTF-8");
            File file = new File(Config.CONTEXT.getRealPath(uri));
            if (file.exists()) {
            	FileInputStream fileIS = new FileInputStream(file);
            	ServletOutputStream servletOS = response.getOutputStream();
            	int b;
            	for (; -1 < (b = fileIS.read()); ) {
            		servletOS.write(b);
            	}
            	fileIS.close();
            	servletOS.flush();
            	servletOS.close();
            	return;
            }

	        // If we are at a directory, e.g. /home
	        // we need to redirect to /home/
	    	String forwardFor = (String) request.getRequestURL().toString();
	        if(request.getAttribute(Globals.MAPPING_KEY) == null &&  forwardFor != null && !forwardFor.endsWith("/") && !forwardFor.endsWith("." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION"))) {
	            // The query string parameters should be preserved as well
	            String queryString = request.getQueryString();
	           	response.sendRedirect(forwardFor + "/" + (UtilMethods.isSet(queryString)?"?"+queryString:""));
	            return;
	        }

	        HttpSession session = request.getSession();
	        boolean ADMIN_MODE = (session.getAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION) != null);
	        boolean PREVIEW_MODE = ((session.getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION) != null) && ADMIN_MODE);
	        boolean EDIT_MODE = ((session.getAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION) != null) && ADMIN_MODE);

	        String value = request.getHeader("X-Requested-With");
	        if ((value != null) && value.equals("XMLHttpRequest") && EDIT_MODE && ADMIN_MODE) {
	        	ADMIN_MODE = false;
	        }

	        // ### VALIDATE ARCHIVE ###
	        if ((EDIT_MODE || PREVIEW_MODE) && isArchive(request)) {
	            PREVIEW_MODE = true;
	            EDIT_MODE = false;
	            request.setAttribute("archive", true);
	        }
	        // ### END VALIDATE ARCHIVE ###

	        LanguageWebAPI langWebAPI = WebAPILocator.getLanguageWebAPI();
	        langWebAPI.checkSessionLocale(request);

            if (PREVIEW_MODE && ADMIN_MODE) {
                // preview mode has the left hand menu and edit buttons on the
                // working page

            	Logger.debug(VelocityServlet.class, "VELOCITY SERVLET I'M ON PREVIEW MODE!!!");

                doPreviewMode(request, response);
            } else if (EDIT_MODE && ADMIN_MODE) {
                // edit mode has the left hand menu and edit buttons on the
                // working page

            	Logger.debug(VelocityServlet.class, "VELOCITY SERVLET I'M ON EDIT MODE!!!");

                doEditMode(request, response);
            } else if (ADMIN_MODE) {
                // admin mode has the left hand menu and shows the live page in
                // the frame
            	Logger.debug(VelocityServlet.class, "VELOCITY SERVLET I'M ON ADMIN MODE!!!");

                doAdminMode(request, response);
            } else {
                // live mode has no frame and shows the live page
            	Logger.debug(VelocityServlet.class, "VELOCITY SERVLET I'M ON LIVE MODE!!!");

                doLiveMode(request, response);
            }


        } catch (ResourceNotFoundException rnfe) {

            // response.sendError(404);
        	request.setAttribute(Constants.SERVE_URL, request.getRequestURI());
        	request.getRequestDispatcher("/localResourceServlet").forward(request, response);

        } catch (ParseErrorException pee) {
        	Logger.error(this, "Template Parse Exception : " + pee.toString(), pee);
        	try{
        		response.sendError(500, "Template Parse Exception");
        	}catch (Throwable t) {
        		Logger.error(this, t.getMessage(), t);
        		PrintWriter out = response.getWriter();
            	out.println("Template Parse Exception");
                out.println("On template:" + request.getRequestURI() + request.getQueryString());
			}

        } catch (MethodInvocationException mie) {
        	Logger.error(this, "MethodInvocationException" + mie.toString(), mie);
        	try{
        		response.sendError(500, "MethodInvocationException Error on template");
        	}catch (Throwable t) {
        		Logger.error(this, t.getMessage(), t);
        		PrintWriter out = response.getWriter();
                out.println("MethodInvocationException Error on template:" + request.getRequestURI() + request.getQueryString());
			}
        } catch (Exception e) {
            Logger.error(this, e.toString(), e);
            try{
        		response.sendError(500, "MethodInvocationException Error on template");
        	}catch (Throwable t) {
        		Logger.error(this, t.getMessage(), t);
        		PrintWriter out = response.getWriter();
                out.println("Error on template:" + request.getRequestURI() + request.getQueryString());
			}
        }finally{
	        // catchall
        	// added finally because of
			// http://jira.dotmarketing.net/browse/DOTCMS-1334
	        DotHibernate.commitTransaction();
	        DbConnectionFactory.closeConnection();
        }
        if (profileTime!=null) {
    		profileTime=Calendar.getInstance().getTimeInMillis()-profileTime;
    		VelocityProfiler.log(VelocityServlet.class, "VelocityPage time: " + request.getRequestURL() + " " + profileTime + " millis" );
    	}
    }

	public void init(ServletConfig config) throws ServletException {
		/*
		 * Read velocity config frm system-ext.properties. We have to set a
		 * couple of properties to get velocity to use our resource loader.
		 */
		try {
			ve = VelocityUtil.getEngine();

		} catch (Exception s) {
			Logger.warn(this, s.toString(), s);
		}

		// build the dirs
		new File(config.getServletContext().getRealPath(
				"/WEB-INF/velocity/working")).mkdirs();
		new File(config.getServletContext().getRealPath(
				"/WEB-INF/velocity/live")).mkdir();

		languages = langAPI.getLanguages();

		// build the velocity toolbox
		myContext = config.getServletContext();
		toolboxManager = ServletToolboxManager.getInstance(config
				.getServletContext(), "/WEB-INF/toolbox.xml");

		Config.initializeConfig();
		CHARSET = Config.getStringProperty("CHARSET");
		VELOCITY_HTMLPAGE_EXTENSION = Config
				.getStringProperty("VELOCITY_HTMLPAGE_EXTENSION");

	}

	protected void doAdminMode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// LIVE MODE - LIVE PAGE

		com.liferay.portal.model.User backendUser = null;
		backendUser = com.liferay.portal.util.PortalUtil.getUser(request);

		response.setContentType(CHARSET);
		Context context = createContext(request, response);

		String uri = URLDecoder.decode(request.getRequestURI(), UtilMethods
				.getCharsetConfiguration());
		uri = UtilMethods.cleanURI(uri);

		Host host = hostWebAPI.getCurrentHost(request);

		Identifier id = IdentifierFactory.getIdentifierByURI(uri, host);
		request.setAttribute("idInode", id.getInode());

		HTMLPage htmlPage = (HTMLPage) IdentifierFactory
				.getWorkingChildOfClass(id, HTMLPage.class);
		HTMLPageAPI htmlPageAPI = APILocator.getHTMLPageAPI();
		getBackendContext(context, htmlPage, "", id.getURI(), request, true,
				false, false, host);

		boolean canUserWriteOnTemplate = permissionAPI.doesUserHavePermission(
				htmlPageAPI.getTemplateForWorkingHTMLPage(htmlPage),
				PERMISSION_WRITE, backendUser);
		context.put("EDIT_TEMPLATE_PERMISSION", canUserWriteOnTemplate);

		Template template = null;

		if (request.getParameter("leftMenu") != null) {
			template = ve.getTemplate("/preview_left_menu.vl");
		} else if (request.getParameter("mainFrame") != null) {
			template = ve.getTemplate("/live/" + id.getInode() + "."
					+ VELOCITY_HTMLPAGE_EXTENSION);
		} else {
			template = ve.getTemplate("/preview_mode.vl");
		}

		Logger.debug(VelocityServlet.class, "Got the template!!!!"
				+ id.getInode());

		PrintWriter out = response.getWriter();
		request.setAttribute("velocityContext", context);
		try {

			template.merge(context, out);

		} catch (ParseErrorException e) {
			out.append(e.getMessage());
		}
	}

	protected void doLiveMode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uri = URLDecoder.decode(request.getRequestURI(), UtilMethods
				.getCharsetConfiguration());
		uri = UtilMethods.cleanURI(uri);

		Host host = hostWebAPI.getCurrentHost(request);

		// Map with all identifier inodes for a given uri.
		String idInode = IdentifierCache.getPathFromIdCache(uri, host).getInode();

		// Checking the path is really live using the livecache
		String cachedUri = LiveCache.getPathFromCache(uri, host);

		// if we still have nothing.
		if (!InodeUtils.isSet(idInode) || cachedUri == null) {
			throw new ResourceNotFoundException(String.format(
					"Resource %s not found in Live mode!", uri));
		}

		response.setContentType(CHARSET);

		request.setAttribute("idInode", String.valueOf(idInode));
		Logger.debug(VelocityServlet.class, "VELOCITY HTML INODE=" + idInode);

		/*
		 * JIRA http://jira.dotmarketing.net/browse/DOTCMS-4659
		//Set long lived cookie regardless of who this is */
		String _dotCMSID = UtilMethods.getCookieValue(request.getCookies(),
				com.dotmarketing.util.WebKeys.LONG_LIVED_DOTCMS_ID_COOKIE);

		if (!UtilMethods.isSet(_dotCMSID)) {
			// create unique generator engine
			Cookie idCookie = CookieUtil.createCookie();
			response.addCookie(idCookie);
		}

		com.liferay.portal.model.User user = null;

		try {
			user = (com.liferay.portal.model.User) request.getSession()
					.getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
		} catch (Exception nsue) {
			Logger.warn(this, "Exception trying to getUser: "
					+ nsue.getMessage(), nsue);
		}



		boolean signedIn = false;

		if (user != null) {
			signedIn = true;
		}
		Identifier ident = IdentifierCache.getPathFromIdCache(uri, host);

		Logger.debug(VelocityServlet.class, "Page Permissions for URI=" + uri);

		HTMLPage page = null;
		try {
			// we get the page and check permissions below
			page = APILocator.getHTMLPageAPI().loadLivePageById(idInode, APILocator.getUserAPI().getSystemUser(), true);
		} catch (Exception e) {
			Logger.error(HTMLPageWebAPI.class, "unable to load live version of page: " + idInode + " because " + e.getMessage());
			return;
		}

		// Check if the page is visible by a CMS Anonymous role
		if (!permissionAPI.doesUserHavePermission(page, PERMISSION_READ, user)) {
			// this page is protected. not anonymous access

			/*******************************************************************
			 * If we need to redirect someone somewhere to login before seeing a
			 * page, we need to edit the /portal/401.jsp page to sendRedirect
			 * the user to the proper login page. We are not using the
			 * REDIRECT_TO_LOGIN variable in the config any longer.
			 ******************************************************************/
			if (!signedIn) {
//              No need for the below LAST_PATH attribute on the front end http://jira.dotmarketing.net/browse/DOTCMS-2675
//				request.getSession().setAttribute(WebKeys.LAST_PATH,
//						new ObjectValuePair(uri, request.getParameterMap()));
				request
						.getSession()
						.setAttribute(
								com.dotmarketing.util.WebKeys.REDIRECT_AFTER_LOGIN,
								uri);

				Logger.debug(VelocityServlet.class,
						"VELOCITY CHECKING PERMISSION: Page doesn't have anonymous access"
								+ uri);

				Logger.debug(VelocityServlet.class, "401 URI = " + uri);

				Logger
						.debug(VelocityServlet.class, "Unauthorized URI = "
								+ uri);
				response.sendError(401,
						"The requested page/file is unauthorized");
				return;

			} else if (!permissionAPI.getReadRoles(ident).contains(
					APILocator.getRoleAPI().loadLoggedinSiteRole())) {
				// user is logged in need to check user permissions
				Logger.debug(VelocityServlet.class,
						"VELOCITY CHECKING PERMISSION: User signed in");

				// check user permissions on this asset
				if (!permissionAPI.doesUserHavePermission(ident,
						PERMISSION_READ, user)) {
					// the user doesn't have permissions to see this page
					// go to unauthorized page
					Logger
							.warn(VelocityServlet.class,
									"VELOCITY CHECKING PERMISSION: Page doesn't have any access for this user");
					response.sendError(403,
							"The requested page/file is forbidden");
					return;
				}
			}
		}



		Logger.debug(VelocityServlet.class, "Recording the ClickStream");
		if (user != null) {
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			if (!userProxy.isNoclicktracking()) {
				ClickstreamFactory.addRequest((HttpServletRequest) request,	((HttpServletResponse) response),  host);
			}
		}
		else{
			ClickstreamFactory.addRequest((HttpServletRequest) request,	((HttpServletResponse) response),  host);
		}





		// Begin Page Caching
		boolean buildCache = false;
		String key = getPageCacheKey(request);
		if(key!=null){

			String cachedPage = CacheLocator.getBlockDirectiveCache().get(key, (int) page.getCacheTTL());

			if(cachedPage == null
					|| "refresh".equals(request.getParameter("dotcache"))
					|| "refresh".equals(request.getAttribute("dotcache"))
					|| "refresh".equals(request.getSession().getAttribute("dotcache"))
			){
				//build cached response
				buildCache = true;
			}else{
				// have cached response and are not refreshing, send it
				response.getWriter().write(cachedPage);
				return;
			}
		}

		Writer out = (buildCache)  ? new StringWriter(4096) : new VelocityFilterWriter(response.getWriter());
		Context context = createContext(request, response);


		Logger.debug(VelocityServlet.class, "HTMLPage Identifier:" + idInode);

		request.setAttribute("velocityContext", context);

		try {

			ve.getTemplate(
					"/live/" + idInode + "." + VELOCITY_HTMLPAGE_EXTENSION)
					.merge(context, out);

		} catch (ParseErrorException e) {
			// out.append(e.getMessage());
		}

		context = null;
		if(buildCache){
			String trimmedPage = out.toString().trim();
			response.getWriter().write(trimmedPage);
			response.getWriter().close();
			synchronized (key) {
				String x = CacheLocator.getBlockDirectiveCache().get(key, (int) page.getCacheTTL());
				if(x != null){
					return;
				}
				CacheLocator.getBlockDirectiveCache().add(getPageCacheKey(request),trimmedPage, (int) page.getCacheTTL());
			}
		}else{
			out.close();
		}

	}

	@SuppressWarnings("unchecked")
	protected void doPreviewMode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uri = URLDecoder.decode(request.getRequestURI(), UtilMethods
				.getCharsetConfiguration());
		uri = UtilMethods.cleanURI(uri);

		Host host = hostWebAPI.getCurrentHost(request);

		StringBuilder preExecuteCode = new StringBuilder();
		Boolean widgetPreExecute=false;

		// Getting the user to check the permissions
		com.liferay.portal.model.User user = null;
		try {
			user = (com.liferay.portal.model.User) request.getSession()
					.getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
		} catch (Exception nsue) {
			Logger.warn(this, "Exception trying getUser: " + nsue.getMessage(),
					nsue);
		}

		// Getting the identifier from the uri
		Identifier id = IdentifierFactory.getIdentifierByURI(uri, host);
		request.setAttribute("idInode", id.getInode());
		Logger.debug(VelocityServlet.class, "VELOCITY HTML INODE="
				+ id.getInode());

		Template template = null;
		Template hostVariablesTemplate = null;

		// creates the context where to place the variables
		response.setContentType(CHARSET);
		Context context = createContext(request, response);

		HTMLPage htmlPage = (HTMLPage) IdentifierFactory
				.getWorkingChildOfClass(id, HTMLPage.class);
		HTMLPageAPI htmlPageAPI = APILocator.getHTMLPageAPI();
		// to check user has permission to write on this page
		boolean hasWritePermOverHTMLPage = permissionAPI.doesUserHavePermission(htmlPage, PERMISSION_WRITE, user);
		boolean hasPublishPermOverHTMLPage = permissionAPI.doesUserHavePermission(htmlPage, PERMISSION_PUBLISH, user);
		context.put("EDIT_HTMLPAGE_PERMISSION", new Boolean(hasWritePermOverHTMLPage));
		context.put("PUBLISH_HTMLPAGE_PERMISSION", new Boolean(hasPublishPermOverHTMLPage));

		boolean canUserWriteOnTemplate = permissionAPI.doesUserHavePermission(
				htmlPageAPI.getTemplateForWorkingHTMLPage(htmlPage),
				PERMISSION_WRITE, user);
		context.put("EDIT_TEMPLATE_PERMISSION", canUserWriteOnTemplate);

		com.dotmarketing.portlets.templates.model.Template cmsTemplate = com.dotmarketing.portlets.htmlpages.factories.HTMLPageFactory
				.getHTMLPageTemplate(htmlPage, true);
		Identifier templateIdentifier = IdentifierFactory
				.getParentIdentifier(cmsTemplate);

		Logger.debug(VelocityServlet.class, "VELOCITY TEMPLATE INODE="
				+ cmsTemplate.getInode());

		getBackendContext(context, htmlPage, cmsTemplate.getInode(), id
				.getURI(), request, true, false, true, host);
		context.put("previewPage", "2");
		context.put("livePage", "0");
		// get the containers for the page and stick them in context
		List<Identifier> identifiers = (List<Identifier>) InodeFactory
				.getChildrenClass(cmsTemplate, Identifier.class);
		Iterator<Identifier> i = identifiers.iterator();

		while (i.hasNext()) {
			Identifier ident = (Identifier) i.next();
			Container c = (Container) IdentifierFactory.getWorkingChildOfClass(
					ident, Container.class);
			context
					.put(
							String.valueOf("container" + ident.getInode()),
							"/working/"
									+ ident.getInode()
									+ "."
									+ Config
											.getStringProperty("VELOCITY_CONTAINER_EXTENSION"));

			context.put("EDIT_CONTAINER_PERMISSION" + ident.getInode(),
					permissionAPI.doesUserHavePermission(c, PERMISSION_WRITE,
							user));

			// to check user has permission to write this container
			Structure st = (Structure) InodeFactory.getParentOfClass(c,
					Structure.class);
			boolean hasWritePermOverTheStructure = permissionAPI
					.doesUserHavePermission(st, PERMISSION_WRITE, user);
			context.put("ADD_CONTENT_PERMISSION" + ident.getInode(),
					new Boolean(hasWritePermOverTheStructure));

			Logger.debug(VelocityServlet.class, String.valueOf("container"
					+ ident.getInode())
					+ "=/working/"
					+ ident.getInode()
					+ "."
					+ Config.getStringProperty("VELOCITY_CONTAINER_EXTENSION"));

			String sort = (c.getSortContentletsBy() == null) ? "tree_order" : c
					.getSortContentletsBy();

			boolean staticContainer = !UtilMethods.isSet(c.getLuceneQuery());

			List<Contentlet> contentlets = null;

			// get contentlets only for main frame
			if (request.getParameter("mainFrame") != null) {
				if (staticContainer) {
					Logger.debug(VelocityServlet.class, "Static Container!!!!");

					Logger.debug(VelocityServlet.class, "html="
							+ htmlPage.getInode() + " container="
							+ c.getInode());

					// The container doesn't have categories
					Identifier idenHtmlPage = IdentifierCache
							.getIdentifierFromIdentifierCache(htmlPage);
					Identifier idenContainer = IdentifierCache
							.getIdentifierFromIdentifierCache(c);
					contentlets = conAPI
							.findPageContentlets(
									idenHtmlPage.getInode(),
									idenContainer.getInode(),
									sort,
									true,
									-1,
									user, true);
					Logger
							.debug(
									VelocityServlet.class,
									"Getting contentlets for language="
											+ (String) request
													.getSession()
													.getAttribute(
															com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE)
											+ " contentlets ="
											+ contentlets.size());

				}

				if(UtilMethods.isSet(contentlets) && contentlets.size() > 0){
					Set<String> contentletIdentList = new HashSet<String>();
					List<Contentlet> contentletsFilter = new ArrayList<Contentlet>();
					for(Contentlet cont : contentlets){
						if(!contentletIdentList.contains(cont.getIdentifier())){
							contentletIdentList.add(cont.getIdentifier());
							contentletsFilter.add(cont);
						}
					}
					contentlets = contentletsFilter;
				}
				List<String> contentletList = new ArrayList<String>();

				if (contentlets != null && contentlets.size() > 0) {
					Iterator<Contentlet> iter = contentlets.iterator();
					int count = 0;

					while (iter.hasNext() && (count < c.getMaxContentlets())) {
						count++;

						Contentlet contentlet = (Contentlet) iter.next();
						Identifier contentletIdentifier = IdentifierCache
								.getIdentifierFromIdentifierCache(contentlet);

						boolean hasWritePermOverContentlet = permissionAPI
								.doesUserHavePermission(contentlet,
										PERMISSION_WRITE, user);

						context.put("EDIT_CONTENT_PERMISSION"
								+ contentletIdentifier.getInode(), new Boolean(
								hasWritePermOverContentlet));

						contentletList.add(String.valueOf(contentletIdentifier.getInode()));
						Logger.debug(this, "Adding contentlet=" + contentletIdentifier.getInode());
						Structure contStructure =contentlet.getStructure();
						if(contStructure.getStructureType()== Structure.STRUCTURE_TYPE_WIDGET){
							Field field=contStructure.getFieldVar("widgetPreexecute");
							if (field!= null && UtilMethods.isSet(field.getValues())) {
								preExecuteCode.append(field.getValues().trim()+ "\n");
								widgetPreExecute=true;
							}
						}

					}
				}

				// sets contentletlist with all the files to load per
				// container
				context
						.put("contentletList" + ident.getInode(),
								contentletList);
				context.put("totalSize" + ident.getInode(), new Integer(
						contentletList.size()));
			}
		}

		Logger
				.debug(
						VelocityServlet.class,
						"Before finding template: /working/"
								+ templateIdentifier.getInode()
								+ "."
								+ Config
										.getStringProperty("VELOCITY_TEMPLATE_EXTENSION"));

		Logger.debug(VelocityServlet.class, "Velocity directory:"
				+ ve.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));

		if (request.getParameter("leftMenu") != null) {
			/*
			 * try to get the messages from the session
			 */

			List<String> list = new ArrayList<String>();
			if (SessionMessages.contains(request, "message")) {
				list.add((String) SessionMessages.get(request, "message"));
				SessionMessages.clear(request);
			}
			if (SessionMessages.contains(request, "custommessage")) {
				list
						.add((String) SessionMessages.get(request,
								"custommessage"));
				SessionMessages.clear(request);
			}

			if (list.size() > 0) {
				ArrayList<String> mymessages = new ArrayList<String>();
				Iterator<String> it = list.iterator();

				while (it.hasNext()) {
					try {
						String message = (String) it.next();
						Company comp = PublicCompanyFactory.getDefaultCompany();
						mymessages.add(LanguageUtil.get(comp.getCompanyId(),
								user.getLocale(), message));
					} catch (Exception e) {
					}
				}
				context.put("vmessages", mymessages);
			}

			template = ve.getTemplate("/preview_left_menu.vl");
		} else if (request.getParameter("mainFrame") != null) {
			hostVariablesTemplate = ve.getTemplate("/working/" + host.getIdentifier() + "." + Config.getStringProperty("VELOCITY_HOST_EXTENSION"));
			template = ve.getTemplate("/working/"
					+ templateIdentifier.getInode() + "."
					+ Config.getStringProperty("VELOCITY_TEMPLATE_EXTENSION"));
		} else {
			template = ve.getTemplate("/preview_mode.vl");
		}

		PrintWriter out = response.getWriter();
		request.setAttribute("velocityContext", context);
		try {

		   if(widgetPreExecute){
				ve.evaluate(context, out, "", preExecuteCode.toString());
		   }
           if(hostVariablesTemplate != null)
        	   hostVariablesTemplate.merge(context, out);
		   template.merge(context, out);

		} catch (ParseErrorException e) {
			out.append(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected void doEditMode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uri = request.getRequestURI();
		uri = UtilMethods.cleanURI(uri);

		Host host = hostWebAPI.getCurrentHost(request);

		StringBuilder preExecuteCode = new StringBuilder();
		Boolean widgetPreExecute=false;

		// Getting the user to check the permissions
		com.liferay.portal.model.User backendUser = null;
		try {
			backendUser = com.liferay.portal.util.PortalUtil.getUser(request);
		} catch (Exception nsue) {
			Logger.warn(this, "Exception trying getUser: " + nsue.getMessage(),
					nsue);
		}

		// Getting the identifier from the uri
		Identifier id = IdentifierFactory.getIdentifierByURI(uri, host);
		request.setAttribute("idInode", String.valueOf(id.getInode()));
		Logger.debug(VelocityServlet.class, "VELOCITY HTML INODE="
				+ id.getInode());

		Template template = null;
		Template hostVariablesTemplate = null;

		// creates the context where to place the variables
		response.setContentType(CHARSET);
		Context context = createContext(request, response);

		HTMLPage htmlPage = (HTMLPage) IdentifierFactory
				.getWorkingChildOfClass(id, HTMLPage.class);
		HTMLPageAPI htmlPageAPI = APILocator.getHTMLPageAPI();
		// to check user has permission to write on this page
		boolean hasAddChildrenPermOverHTMLPage = permissionAPI.doesUserHavePermission(htmlPage, PERMISSION_CAN_ADD_CHILDREN, backendUser);
		boolean hasWritePermOverHTMLPage = permissionAPI.doesUserHavePermission(htmlPage, PERMISSION_WRITE, backendUser);
		boolean hasPublishPermOverHTMLPage = permissionAPI.doesUserHavePermission(htmlPage, PERMISSION_PUBLISH, backendUser);
		context.put("ADD_CHILDREN_HTMLPAGE_PERMISSION", new Boolean(hasAddChildrenPermOverHTMLPage));
		context.put("EDIT_HTMLPAGE_PERMISSION", new Boolean(hasWritePermOverHTMLPage));
		context.put("PUBLISH_HTMLPAGE_PERMISSION", new Boolean(hasPublishPermOverHTMLPage));
		context.put("canAddForm" ,new Boolean(LicenseUtil.getLevel() > 199? true : false ));
		context.put("canViewDiff" ,new Boolean(LicenseUtil.getLevel() > 199? true : false ));

		boolean canUserWriteOnTemplate = permissionAPI.doesUserHavePermission(
				htmlPageAPI.getTemplateForWorkingHTMLPage(htmlPage),
				PERMISSION_WRITE, backendUser)
				&& portletAPI.hasTemplateManagerRights(backendUser);
		context.put("EDIT_TEMPLATE_PERMISSION", canUserWriteOnTemplate);

		com.dotmarketing.portlets.templates.model.Template cmsTemplate = com.dotmarketing.portlets.htmlpages.factories.HTMLPageFactory
																				.getHTMLPageTemplate(htmlPage, true);
		if(cmsTemplate == null){//DOTCMS-4051
			cmsTemplate = new com.dotmarketing.portlets.templates.model.Template();
			Logger.debug(VelocityServlet.class,"HTMLPAGE TEMPLATE NOT FOUND");
		}

		Identifier templateIdentifier = IdentifierFactory
				.getParentIdentifier(cmsTemplate);

		Logger.debug(VelocityServlet.class, "VELOCITY TEMPLATE INODE="
				+ cmsTemplate.getInode());

		getBackendContext(context, htmlPage, cmsTemplate.getInode(), id
				.getURI(), request, true, true, false, host);
		// added to show tabs
		context.put("previewPage", "1");
		// get the containers for the page and stick them in context
		List<Identifier> identifiers = InodeFactory.getChildrenClass(
				cmsTemplate, Identifier.class);
		Iterator<Identifier> i = identifiers.iterator();

		while (i.hasNext()) {
			Identifier ident = (Identifier) i.next();
			Container c = (Container) IdentifierFactory.getWorkingChildOfClass(
					ident, Container.class);
			context
					.put(
							String.valueOf("container" + ident.getInode()),
							"/working/"
									+ ident.getInode()
									+ "."
									+ Config
											.getStringProperty("VELOCITY_CONTAINER_EXTENSION"));

			boolean hasWritePermissionOnContainer = permissionAPI
					.doesUserHavePermission(c, PERMISSION_WRITE, backendUser,
							false)
					&& portletAPI.hasContainerManagerRights(backendUser);
			boolean hasReadPermissionOnContainer = permissionAPI.doesUserHavePermission(c, PERMISSION_READ, backendUser, false);
			context.put("EDIT_CONTAINER_PERMISSION" + ident.getInode(),	hasWritePermissionOnContainer);
			if(Config.getBooleanProperty("SIMPLE_PAGE_CONTENT_PERMISSIONING", true))
				context.put("USE_CONTAINER_PERMISSION" + ident.getInode(), true);
			else
				context.put("USE_CONTAINER_PERMISSION" + ident.getInode(), hasReadPermissionOnContainer);

			// to check user has permission to write this container
			Structure st = (Structure) InodeFactory.getParentOfClass(c,
					Structure.class);
			boolean hasWritePermOverTheStructure = permissionAPI
					.doesUserHavePermission(st, PERMISSION_WRITE, backendUser);
			context.put("ADD_CONTENT_PERMISSION" + ident.getInode(),
					new Boolean(hasWritePermOverTheStructure));

			Logger.debug(VelocityServlet.class, String.valueOf("container"
					+ ident.getInode())
					+ "=/working/"
					+ ident.getInode()
					+ "."
					+ Config.getStringProperty("VELOCITY_CONTAINER_EXTENSION"));

			String sort = (c.getSortContentletsBy() == null) ? "tree_order" : c
					.getSortContentletsBy();

			List<Contentlet> contentlets = null;

			boolean staticContainer = !UtilMethods.isSet(c.getLuceneQuery());

			// get contentlets only for main frame
			if (request.getParameter("mainFrame") != null) {
				if (staticContainer) {
					Logger.debug(VelocityServlet.class, "Static Container!!!!");

					Logger.debug(VelocityServlet.class, "html="
							+ htmlPage.getInode() + " container="
							+ c.getInode());

					// The container doesn't have categories
					Identifier idenHtmlPage = IdentifierCache
							.getIdentifierFromIdentifierCache(htmlPage);
					Identifier idenContainer = IdentifierCache
							.getIdentifierFromIdentifierCache(c);
					contentlets = conAPI.findPageContentlets(idenHtmlPage.getInode(), idenContainer.getInode(), sort, true, -1, backendUser, true);
					Logger
							.debug(
									VelocityServlet.class,
									"Getting contentlets for language="
											+ (String) request
													.getSession()
													.getAttribute(
															com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE)
											+ " contentlets ="
											+ contentlets.size());

				} else{
					String luceneQuery = c.getLuceneQuery();
					int limit = c.getMaxContentlets();
					String sortBy = c.getSortContentletsBy();
					int offset = 0;
					contentlets = conAPI.search(luceneQuery, limit, offset, sortBy, backendUser, true);
				}

				if(UtilMethods.isSet(contentlets) && contentlets.size() > 0){
					Set<String> contentletIdentList = new HashSet<String>();
					List<Contentlet> contentletsFilter = new ArrayList<Contentlet>();
					for(Contentlet cont : contentlets){
						if(!contentletIdentList.contains(cont.getIdentifier())){
							contentletIdentList.add(cont.getIdentifier());
							contentletsFilter.add(cont);
						}
					}
					contentlets = contentletsFilter;
				}
				List<String> contentletList = new ArrayList<String>();

				if (contentlets != null) {
					Iterator<Contentlet> iter = contentlets.iterator();
					int count = 0;

					while (iter.hasNext() && (count < c.getMaxContentlets())) {
						count++;

						Contentlet contentlet = (Contentlet) iter.next();
						Identifier contentletIdentifier = IdentifierCache
								.getIdentifierFromIdentifierCache(contentlet);

						boolean hasWritePermOverContentlet = permissionAPI
								.doesUserHavePermission(contentlet,
										PERMISSION_WRITE, backendUser);

						context.put("EDIT_CONTENT_PERMISSION"
								+ contentletIdentifier.getInode(), new Boolean(
								hasWritePermOverContentlet));

						contentletList.add(String.valueOf(contentletIdentifier.getInode()));
						Logger.debug(this, "Adding contentlet=" + contentletIdentifier.getInode());
						Structure contStructure =contentlet.getStructure();
						if(contStructure.getStructureType()== Structure.STRUCTURE_TYPE_WIDGET){
							Field field=contStructure.getFieldVar("widgetPreexecute");
							if (field!= null && UtilMethods.isSet(field.getValues())) {
								preExecuteCode.append(field.getValues().trim()+ "\n");
								widgetPreExecute=true;
							}

						}
					}
				}
				// sets contentletlist with all the files to load per
				// container
				context.put("contentletList" + ident.getInode(), contentletList);
				context.put("totalSize" + ident.getInode(), new Integer(contentletList.size()));
				// ### Add the structure fake contentlet ###
				if (contentletList.size() == 0) {
					Structure structure = ContainerFactory
							.getContainerStructure(c);
					contentletList
							.add(structure.getInode() + "");
					// sets contentletlist with all the files to load per
					// container
					context.remove("contentletList" + ident.getInode());
					context.remove("totalSize" + ident.getInode());
					//http://jira.dotmarketing.net/browse/DOTCMS-2876
					context.put("contentletList" + ident.getInode(),
							new long[0]);
					context.put("totalSize" + ident.getInode(), 0);
				}
				// ### END Add the structure fake contentlet ###

			}
		}

		Logger
				.debug(
						VelocityServlet.class,
						"Before finding template: /working/"
								+ templateIdentifier.getInode()
								+ "."
								+ Config
										.getStringProperty("VELOCITY_TEMPLATE_EXTENSION"));

		Logger.debug(VelocityServlet.class, "Velocity directory:"
				+ ve.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH));

		if (request.getParameter("leftMenu") != null) {
			/*
			 * try to get the messages from the session
			 */

			List<String> list = new ArrayList<String>();
			if (SessionMessages.contains(request, "message")) {
				list.add((String) SessionMessages.get(request, "message"));
				SessionMessages.clear(request);
			}
			if (SessionMessages.contains(request, "custommessage")) {
				list
						.add((String) SessionMessages.get(request,
								"custommessage"));
				SessionMessages.clear(request);
			}

			if (list.size() > 0) {
				ArrayList<String> mymessages = new ArrayList<String>();
				Iterator<String> it = list.iterator();

				while (it.hasNext()) {
					try {
						String message = (String) it.next();
						Company comp = PublicCompanyFactory.getDefaultCompany();
						mymessages.add(LanguageUtil.get(comp.getCompanyId(),
								backendUser.getLocale(), message));
					} catch (Exception e) {
					}
				}
				context.put("vmessages", mymessages);
			}

			template = ve.getTemplate("/preview_left_menu.vl");
		} else if (request.getParameter("mainFrame") != null) {
			hostVariablesTemplate = ve.getTemplate("/working/" + host.getIdentifier() + "." + Config.getStringProperty("VELOCITY_HOST_EXTENSION"));
			template = ve.getTemplate("/working/"
					+ templateIdentifier.getInode() + "."
					+ Config.getStringProperty("VELOCITY_TEMPLATE_EXTENSION"));
		} else {
			// Return a resource not found right away if the page is not found,
			// not try to load the frames
			if (!InodeUtils.isSet(templateIdentifier.getInode()))
				throw new ResourceNotFoundException("");
			template = ve.getTemplate("/preview_mode.vl");
		}

		PrintWriter out = response.getWriter();
		request.setAttribute("velocityContext", context);
		try {
			if(widgetPreExecute){
				ve.evaluate(context, out, "", preExecuteCode.toString());
			}
			if(hostVariablesTemplate != null)
				hostVariablesTemplate.merge(context, out);
			template.merge(context, out);

		} catch (ParseErrorException e) {
			out.append(e.getMessage());
		}
	}

	private void getBackendContext(Context context, HTMLPage htmlPage,
			String cmsTemplateInode, String idURI, HttpServletRequest request,
			boolean ADMIN_MODE, boolean EDIT_MODE, boolean PREVIEW_MODE, Host host) throws DotDataException {
		context.put("context", context);

		// stick some useful variables in the context
		if (htmlPage != null) {
			context.put("HTMLPAGE_INODE", String.valueOf(htmlPage.getInode()));
			context.put("HTMLPAGE_IDENTIFIER", String.valueOf(htmlPage
					.getIdentifier()));
			context.put("HTMLPAGE_TITLE", htmlPage.getTitle());
			context.put("HTMLPAGE_META", htmlPage.getMetadata());
			//http://jira.dotmarketing.net/browse/DOTCMS-6427
			context.put("HTMLPAGE_DESCRIPTION", htmlPage.getSeoDescription());
			context.put("HTMLPAGE_KEYWORDS", htmlPage.getSeoKeywords());
			context.put("HTMLPAGE_SECURE", String.valueOf(htmlPage
					.isHttpsRequired()));
			context.put("HTMLPAGE_REDIRECT", htmlPage.getRedirect());
			context.put("friendlyName", htmlPage.getFriendlyName());
			context.put("pageTitle", htmlPage.getTitle());
			Date moddate = null;
			if (UtilMethods.isSet(htmlPage.getModDate())) {
				moddate = htmlPage.getModDate();
			} else {
				moddate = htmlPage.getStartDate();
			}

			moddate = new Date(moddate.getTime());

			context.put("HTML_PAGE_LAST_MOD_DATE", moddate);

			try {
				context.put("htmlPageInode", htmlPage.getInode());

				// for browsing the tree
				String view = java.net.URLEncoder
						.encode(
								"(working="
										+ com.dotmarketing.db.DbConnectionFactory
												.getDBTrue()
										+ " and deleted="
										+ com.dotmarketing.db.DbConnectionFactory
												.getDBFalse()
										+ "and language_id = "
										+ (String) request
												.getSession()
												.getAttribute(
														com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE)
										+ ")", "UTF-8");
				context.put("view", view);
			} catch (Exception e) {
				Logger.warn(this, e.toString(), e);
			}
		}

		context.put("HTMLPAGE_SERVER_NAME", request.getServerName());
		context.put("VTLSERVLET_URI", UtilMethods.encodeURIComponent(idURI));
		if (request.getQueryString() != null
				&& request.getQueryString().length() > 0) {
			context.put("queryString", request.getQueryString());
		} else {
			context.put("queryString", "");
		}
		context.put("TEMPLATE_INODE", String.valueOf(cmsTemplateInode));
		context.put("UtilMethods", utilMethods);
		context.put("InodeUtils", inodeUtils);
		context.put("PortletURLUtil", portletURLUtil);
		context.put("mainFrame", request.getParameter("mainFrame"));
		context.put("previewFrame", request.getParameter("previewFrame"));

		if (EDIT_MODE) {
			// gets user id from request for mod user
			com.liferay.portal.model.User backendUser = null;

			try {
				backendUser = com.liferay.portal.util.PortalUtil
						.getUser(request);
//				Skin skin = backendUser.getSkin();
//				context.put("USER_SKIN", skin.getSkinId());
			} catch (Exception nsue) {
				Logger.warn(this, "Exception trying yo getUser: "
						+ nsue.getMessage(), nsue);
			}

			// to check user has permission to publish this page
			boolean permission = permissionAPI.doesUserHavePermission(htmlPage,
					PERMISSION_PUBLISH, backendUser);
			context.put("permission", new Boolean(permission));

			// Check if the user is a CMS Administrator
			boolean adminUser = false;
			try {
				Company company = null;
				company = com.dotmarketing.cms.factories.PublicCompanyFactory
						.getDefaultCompany();

				String adminRoleKey = "";
				try {
					Role adminRole = APILocator.getRoleAPI().loadRoleByKey(Config.getStringProperty("CMS_ADMINISTRATOR_ROLE"));
					adminRoleKey = adminRole.getRoleKey();
				} catch (Exception e) {
				}

				Role[] userRoles = (Role[]) APILocator.getRoleAPI().loadRolesForUser(
						backendUser.getUserId()).toArray(new Role[0]);
				for (int i = 0; i < userRoles.length; i++) {
					Role userRole = (Role) userRoles[i];
					if (userRole.getRoleKey().equals(adminRoleKey)) {
						adminUser = true;
					}
				}
			} catch (Exception e) {
			}
			context.put("cmsAdminUser", new Boolean(adminUser));

		}

		// gets pageChannel for this path
		String pageChannel = UtilMethods.getPageChannel(idURI);
		context.put("pageChannel", pageChannel);
		context.put("PREVIEW_MODE", new Boolean(PREVIEW_MODE));
		context.put("EDIT_MODE", new Boolean(EDIT_MODE));
		context.put("ADMIN_MODE", new Boolean(ADMIN_MODE));

		// for publish button on admin control
		// I HAVE TO FIX THIS!!!!! FOR THE NEW ONE
		context.put("TEMPLATE_LIVE_CONTENT", new Boolean(false));
		context.put("CONTAINER_LIVE_CONTENT", new Boolean(false));
		context.put("CONTENTLET_LIVE_CONTENT", new Boolean(false));

		// If we are on preview mode and seeing the live page
		String livePage = (EDIT_MODE && ADMIN_MODE) ? "0" : "1";

		Logger.debug(VelocityServlet.class, "livePage=" + livePage);

		context.put("livePage", livePage);

		context.put("language", (String) request.getSession().getAttribute(
				com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE));

		if (ADMIN_MODE) {

			//Making sure you are viewing the latest list of languages on any admin mode
			languages = langAPI.getLanguages();
			context.put("languages", languages);

			// gets user id from request for mod user
			com.liferay.portal.model.User backendUser = null;

			try {
				backendUser = com.liferay.portal.util.PortalUtil
						.getUser(request);
//				Skin skin = backendUser.getSkin();
//				context.put("USER_SKIN", skin.getSkinId());
				context.put("backendUser", backendUser);
			} catch (Exception nsue) {
				Logger.warn(this, "Exception trying yo getUser: "
						+ nsue.getMessage(), nsue);
			}

			HttpSession session = request.getSession();
			context.put("directorURL", session
					.getAttribute(com.dotmarketing.util.WebKeys.DIRECTOR_URL));
			context
					.put(
							"viewFoldersURL",
							session
									.getAttribute(com.dotmarketing.util.WebKeys.VIEW_FOLDERS_URL));
			context
					.put(
							"previewPageURL",
							session
									.getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_PAGE_URL));
			context
					.put(
							"viewContentsURL",
							session
									.getAttribute(com.dotmarketing.util.WebKeys.VIEW_CONTENTS_URL));
			context
					.put(
							"viewBrowserURL",
							session
									.getAttribute(com.dotmarketing.util.WebKeys.VIEW_BROWSER_URL));

			List<WorkflowTask> workflowTasksList = WorkflowsFactory
					.getOpenedWorkflowTasksAssociatedWithAsset(htmlPage,
							backendUser);
			context.put("workflowTasksList", workflowTasksList);

		}

		context.put("host", host);

	}

	public static Context getBasicContext() {
		Context context = new VelocityContext();
		context.put("UtilMethods", utilMethods);
		context.put("quote", "\"");
		context.put("pounds", "##");
		context.put("return", "\n");
		context.put("velocityContext", context);
		context.put("language", "1");
		context.put("InodeUtils", inodeUtils);
		return context;
	}

	public static Context createContext(HttpServletRequest request,
			HttpServletResponse response) throws PortalException, SystemException, DotDataException, DotSecurityException {
		//http://jira.dotmarketing.net/browse/DOTCMS-2917
		RequestWrapper rw = new RequestWrapper(request);

		ChainedContext context = new ChainedContext((Context) null, rw,
				response, myContext);
		context.put("context", context);
		Logger.debug(VelocityServlet.class, "ChainedContext=" + context);
		/*
		 * if we have a toolbox manager, get a toolbox from it See
		 * /WEB-INF/toolbox.xml
		 */
		context.setToolbox(toolboxManager.getToolboxContext(context));

		// Stick some escaped variables
		context.put("quote", "\"");
		context.put("pounds", "##");
		context.put("return", "\n");
		context.put("velocityContext", context);

		context.put("InodeUtils", inodeUtils);
		context.put("UtilMethods", utilMethods);
		context.put("PortletURLUtil", portletURLUtil);
		// put the list of languages on the page
		context.put("languages", languages);
		context.put("language", (String) request.getSession().getAttribute(
				com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE));

		Host host = hostWebAPI.getCurrentHost(request);
		context.put("host", host);
		context.put("pdfExport", false);
		com.liferay.portal.model.User user = null;

		try {
			user = (com.liferay.portal.model.User) request.getSession()
					.getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
			context.put("user", user);
		} catch (Exception nsue) {
			Logger.error(VelocityServlet.class, nsue.getMessage(), nsue);
		}

		return context;
	}

	// EACH CLIENT MAY HAVE ITS OWN VARIABLES
	// WE HAVE THE CLASS CLIENT THAT WILL IMPLEMENT THIS METHOD AND WILL BE ON
	// THE WEB.XML FILE
	protected abstract void _setClientVariablesOnContext(
			HttpServletRequest request, ChainedContext context);

	private boolean isArchive(HttpServletRequest request) throws PortalException, SystemException, DotDataException, DotSecurityException {
		String uri = request.getRequestURI();
		uri = UtilMethods.cleanURI(uri);

		Host host = null;
		String hostId = "";

		/*String pageHostId = request.getParameter("host_id");
		if (pageHostId != null) {
			try {
				hostId = Long.parseLong(pageHostId);
			} catch (Exception ex) {
			}
		}*/
		hostId = request.getParameter("host_id");
		if (!InodeUtils.isSet(hostId)) {
			host = hostWebAPI.getCurrentHost(request);
			hostId = host.getIdentifier();
		}

		// Getting the identifier from the uri
		Identifier id = IdentifierCache.getPathFromIdCache(uri, hostId);
		request.setAttribute("idInode", String.valueOf(id.getInode()));
		HTMLPage htmlPage = (HTMLPage) IdentifierFactory
				.getWorkingChildOfClass(id, HTMLPage.class);
		boolean isArchived = htmlPage.isDeleted();
		return isArchived;
	}

	/**
	 * @author will this filter class strips all leading whitespace from the
	 *         server response which is helpful for xml feeds and the like.
	 */

	public class VelocityFilterWriter extends FilterWriter {

		private boolean firstNonWhiteSpace = false;

		public VelocityFilterWriter(Writer arg0) {
			super(arg0);

		}

		@Override
		public void write(char[] arg0) throws IOException {
			if (firstNonWhiteSpace) {
				super.write(arg0);
			} else {

				for (int i = 0; i < arg0.length; i++) {
					if (arg0[i] > 32) {
						firstNonWhiteSpace = true;
					}
					if (firstNonWhiteSpace) {
						super.write(arg0[i]);
					}

				}

			}

		}

		@Override
		public void write(String arg0) throws IOException {
			if (firstNonWhiteSpace) {
				super.write(arg0);
			} else {
				char[] stringChar = arg0.toCharArray();
				for (int i = 0; i < stringChar.length; i++) {

					if (stringChar[i] > 32) {
						firstNonWhiteSpace = true;
						super.write(arg0.substring(i, stringChar.length));
						break;
					}

				}

			}

		}

	}



	/**
	 * This method trys to build a cache key based on
	 * the information given in the request - if the page
	 * can't be cached, or caching is not availbale then return null
	 * @param request
	 * @return
	 */
	private String getPageCacheKey(HttpServletRequest request){
		// no license
		if(LicenseUtil.getLevel() < 100) {
			return null;
		}
		//don't cache posts
		if(!"GET".equalsIgnoreCase(request.getMethod())){
			return null;
		}
		// nocache passed either as a session var, as a request var or as a request attribute
		if("no".equals(request.getParameter("dotcache"))
				|| "no".equals(request.getAttribute("dotcache"))
				|| "no".equals(request.getSession().getAttribute("dotcache"))){
			return null;
		}

		String idInode = (String) request.getAttribute("idInode");

		User user  =  (com.liferay.portal.model.User) request.getSession().getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);

		HTMLPage page = null;
		try {
			page = APILocator.getHTMLPageAPI().loadLivePageById(idInode, user, true);
		} catch (Exception e) {
			Logger.error(HTMLPageWebAPI.class, "unable to load live version of page: " + idInode + " because " + e.getMessage());
			return null;
		}
		if(page  == null || page.getCacheTTL() <1){
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(page.getInode());
		sb.append("_" + page.getModDate().getTime());


		String userId = (user != null) ? user.getUserId() : "PUBLIC";
		sb.append("_" + userId);

		String language =	(String) request.getSession().getAttribute(com.dotmarketing.util.WebKeys.HTMLPAGE_LANGUAGE);
		sb.append("_" + language);

		String urlMap = (String) request.getAttribute(WebKeys.WIKI_CONTENTLET_INODE);
		if(urlMap != null){
			sb.append("_" + urlMap);
		}

		if(UtilMethods.isSet(request.getQueryString())){
			sb.append("_" + request.getQueryString());
		}


		return sb.toString();

	}

}

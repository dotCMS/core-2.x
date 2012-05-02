package com.dotmarketing.portlets.htmlpages.factories;


import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.util.List;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.templates.factories.TemplateFactory;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.services.PageServices;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;

/**
 *
 * @author  will
 */
public class HTMLPageFactory {
	
	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();
	private static HostAPI hostAPI = APILocator.getHostAPI();

	/**
	 * @param permissionAPI the permissionAPI to set
	 */
	public static void setPermissionAPI(PermissionAPI permissionAPIRef) {
		permissionAPI = permissionAPIRef;
	}

	public static java.util.List getChildrenHTMLPageByOrder(Inode i) {
		
		return InodeFactory.getChildrenClassByOrder(i, HTMLPage.class, "sort_order");

	}

	public static java.util.List getActiveHTMLPages() {
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.htmlpages.model.HTMLPage where type='htmlpage'");
		return dh.list();
	}

	public static java.util.List getHTMLPagesByOrderAndParent(String orderby, Inode i) {

		return InodeFactory.getChildrenClassByOrder(i, HTMLPage.class, orderby);

	}

	public static java.util.List getHTMLPagesByOrder(String orderby) {
		
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.htmlpages.model.HTMLPage where type='htmlpage' and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by "
				+ orderby);

		return dh.list();
	}

	public static java.util.List getHTMLPageChildrenByCondition(Inode i, String condition) {
		return InodeFactory.getChildrenClassByConditionAndOrderBy(i, HTMLPage.class, condition, "page_url, sort_order");
	}
	
	
	public static java.util.List getHTMLPageByCondition(String condition) {
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.htmlpages.model.HTMLPage where type='htmlpage' and " + condition + " order by page_url, sort_order");
		return dh.list();
	}


	public static HTMLPage getLiveHTMLPageByPath(String path, Host host){
	    return getLiveHTMLPageByPath (path, host.getIdentifier());
	}
	
	public static int findNumOfContent(HTMLPage page, Container container){
		DotConnect dc = new DotConnect();
		StringBuffer buffy = new StringBuffer();
		buffy.append("select count(t.child) as contentletCount ");
		buffy.append("from multi_tree t ");
		buffy.append("where t.parent1 = ? and t.parent2 = ?");
		dc.setSQL(buffy.toString());
		dc.addParam(page.getInode());
		dc.addParam(container.getInode());
		int count = dc.getInt("contentletCount");
		return count;
	}
	
	public static HTMLPage getLiveHTMLPageByPath(String path, String hostId){
        Identifier id = IdentifierFactory.getIdentifierByURI(path, hostId);

        Logger.debug(HTMLPageFactory.class, "Looking for page : " + path);
		Logger.debug(HTMLPageFactory.class, "got id " + id.getInode());
        
        //if this page does not exist, create it, add it to the course folder, use the course template, etc...
        if(!InodeUtils.isSet(id.getInode())){
            return  new HTMLPage();
        }
        
	    return (HTMLPage) IdentifierFactory.getLiveChildOfClass(id, HTMLPage.class);
	    
	}

	public static HTMLPage getLiveHTMLPageByIdentifier(Identifier ident){
		return (HTMLPage) IdentifierFactory.getLiveChildOfClass(ident, HTMLPage.class);
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<HTMLPage> getLiveHTMLPages() {
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.htmlpages.model.HTMLPage where type='htmlpage' and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse());
		return dh.list();
	}

	public static java.util.List getWorkingHTMLPages() {
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.htmlpages.model.HTMLPage where type='htmlpage' and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse());
		return dh.list();
	}

	public static java.util.List getHTMLPageChildren(Inode i) {
		
		
		return InodeFactory.getChildrenClassByOrder(i, HTMLPage.class, "inode, sort_order");
		
		

	}

	public static Template getHTMLPageTemplate(Inode i) {
		
		Identifier templateIdent = (Identifier) InodeFactory.getParentOfClassByRelationType(i, Identifier.class, "parentPageTemplate");
		List<Template> templates;
		templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and working = " + DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");
		if(templates.size() > 1)
			Logger.error(HTMLPageFactory.class, "Page " + ((HTMLPage)i).getPageUrl() + " has more than one template associated!!");
		if(templates.size() > 0)
			return (Template) templates.get(0);
		else
			return null;
	}

	public static Template getHTMLPageTemplate(Inode i, boolean previewMode) {

		Identifier templateIdent = (Identifier) InodeFactory.getParentOfClassByRelationType(i, Identifier.class, "parentPageTemplate");
		List<Template> templates;
		if (previewMode) {
			templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and working = " + DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");
		}
		else {
			templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and live = " + DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");
		}
		if(templates.size() > 1)
			Logger.error(HTMLPageFactory.class, "Page " + ((HTMLPage)i).getPageUrl() + " has more than one template associated!!");
		if(templates.size() > 0)
			return (Template) templates.get(0);
		else
			return null;

	}

	public static Template getWorkingNotLiveHTMLPageTemplate(Inode i) {
		
		Identifier templateIdent = (Identifier) InodeFactory.getParentOfClassByRelationType(i, Identifier.class, "parentPageTemplate");
		List<Template> templates;
		templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and working = " + DbConnectionFactory.getDBTrue() + " and live = " + DbConnectionFactory.getDBFalse() + " and identifier = '" + templateIdent.getInode()+"'");
		if(templates.size() > 1)
			Logger.error(HTMLPageFactory.class, "Page " + ((HTMLPage)i).getPageUrl() + " has more than one template associated!!");
		if(templates.size() > 0)
			return (Template) templates.get(0);
		else
			return null;
	}

	public static HTMLPage getHTMLPageByLiveAndFolderAndTitle(Inode parent, String title) 
	{
		return (HTMLPage) InodeFactory.getChildOfClassbyCondition(parent, HTMLPage.class, "title =  '" + title + "' and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
	}
	
	public static HTMLPage getHTMLPageByFolderAndURI(Inode folder, String fileURI,boolean live) 
	{
		String liveDB = "";
		if(live)
		{
			liveDB = "live = " + DbConnectionFactory.getDBTrue();
		}
		else
		{
			liveDB = "working = " + DbConnectionFactory.getDBTrue();
		}
		return (HTMLPage) InodeFactory.getChildOfClassbyCondition(folder, HTMLPage.class, "page_url =  '" + fileURI + "' and " + liveDB);
	}

	public static void deleteChildren(HTMLPage parent, Class c) {
		InodeFactory.deleteChildrenOfClass(parent, c);
	}

	public static boolean existsPageName(Inode parent, String pageName) {
		HTMLPage page = (HTMLPage) InodeFactory.getChildOfClassbyCondition(parent, HTMLPage.class, "page_url like '" + pageName + "'");
		Logger.debug(HTMLPageFactory.class, "existsFileName=" + page.getInode());
		return (InodeUtils.isSet(page.getInode()));
	}

	
	public static int getNumberOfContainersPerContenlet(HTMLPage htmlPage, Contentlet contentlet){
		
		DotConnect dc = new DotConnect();
		StringBuffer sb = new StringBuffer();
		sb.append("select count(*) mycount ");
		sb.append("from tree tree1, tree tree2, tree tree3, tree tree4, template template, identifier identifier, containers containers ");
		sb.append("where tree1.child = ? ");
		sb.append("and tree1.parent = template.inode ");
		sb.append("and template.working = 't' ");
		sb.append("and template.inode = tree2.parent ");
		sb.append("and tree2.child = identifier.inode ");
		sb.append("and identifier.inode = tree3.parent ");
		sb.append("and tree3.child = containers.inode ");
		sb.append("and containers.working = 't' ");
		sb.append("and containers.inode = tree4.parent ");
		sb.append("and tree4.child = ?");
		
		dc.setSQL(sb.toString());
		dc.addParam(htmlPage.getInode());
		dc.addParam(contentlet.getInode());
		
		int count = dc.getInt("mycount");
		
		return count;
	}
	
	

	
	public static HTMLPage getWorkingHTMLPageByPath(String path, Host host){
	    return getWorkingHTMLPageByPath (path, host.getIdentifier());
	}
	
	public static HTMLPage getWorkingHTMLPageByPath(String path, String hostId){
        Identifier id = IdentifierFactory.getIdentifierByURI(path, hostId);

        Logger.debug(HTMLPageFactory.class, "Looking for page : " + path);
		Logger.debug(HTMLPageFactory.class, "got id " + id.getInode());
        
        //if this page does not exist, create it, add it to the course folder, use the course template, etc...
        if(!InodeUtils.isSet(id.getInode())){
            return  new HTMLPage();
        }
        
	    return (HTMLPage) IdentifierFactory.getWorkingChildOfClass(id, HTMLPage.class);

	    
	    
	}
	
	
	public static Template getTemplate(HTMLPage htmlpage) {
		
		Identifier templateIdent = (Identifier) InodeFactory.getParentOfClassByRelationType(htmlpage, Identifier.class, "parentPageTemplate");
		return (Template) InodeFactory.getInodeOfClassByCondition(Template.class, "working=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");

	}
	
	/**
	 * Method used to move an htmlpage to a different folder
	 * @param currentHTMLPage
	 * @param parent
	 * @return
	 */
	public static boolean moveHTMLPage (HTMLPage currentHTMLPage, Folder parent) {
	

		
		Identifier identifier = com.dotmarketing.factories.IdentifierFactory
				.getParentIdentifier(currentHTMLPage);

		//gets working container
		HTMLPage workingWebAsset = (HTMLPage) IdentifierFactory
				.getWorkingChildOfClass(identifier, HTMLPage.class);
		//gets live container
		HTMLPage liveWebAsset = (HTMLPage) IdentifierFactory
				.getLiveChildOfClass(identifier, HTMLPage.class);


        if (HTMLPageFactory.existsPageName(parent, workingWebAsset.getPageUrl())) {
        	return false;
        }

        //moving folders
        Folder oldParent = (Folder) InodeFactory.getParentOfClass(workingWebAsset, Folder.class);
        oldParent.deleteChild(workingWebAsset);
        if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
        	oldParent.deleteChild(liveWebAsset);
        }

        parent.addChild(workingWebAsset);
        if ((liveWebAsset != null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
        	parent.addChild(liveWebAsset);
        }

        //updating caches
        WorkingCache.removeAssetFromCache(workingWebAsset);
        IdentifierCache.removeAssetFromIdCache(workingWebAsset);

        if ((liveWebAsset!=null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
        	LiveCache.removeAssetFromCache(liveWebAsset);
        }

        //gets identifier for this webasset and changes the uri and
        // persists it
		User systemUser;
		Host newHost;
		try {
			systemUser = APILocator.getUserAPI().getSystemUser();
	        newHost = hostAPI.findParentHost(parent, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
        identifier.setHostId(newHost.getIdentifier());
        identifier.setURI(workingWebAsset.getURI(parent));
        InodeFactory.saveInode(identifier);

        //Add to Preview and Live Cache
        if ((liveWebAsset!=null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
        	LiveCache.removeAssetFromCache(liveWebAsset);
        	LiveCache.addToLiveAssetToCache(liveWebAsset);
        }
        WorkingCache.removeAssetFromCache(workingWebAsset);
        WorkingCache.addToWorkingAssetToCache(workingWebAsset);
        IdentifierCache.removeFromIdCacheByInode(workingWebAsset);
        IdentifierCache.addVersionableToIdentifierCache(workingWebAsset);

        //republishes the page to reset the VTL_SERVLETURI variable
        if ((liveWebAsset!=null) && (InodeUtils.isSet(liveWebAsset.getInode()))) {
        	PageServices.invalidate(liveWebAsset);
        }

        //Wipe out menues
        //RefreshMenus.deleteMenus();
        RefreshMenus.deleteMenu(oldParent,parent);
        
        return true;
	
	}
	
	@SuppressWarnings("deprecation")
	public static HTMLPage copyHTMLPage (HTMLPage currentHTMLPage, Folder parent) throws DotDataException {
		
		if (!currentHTMLPage.isWorking()) {
			Identifier id = IdentifierFactory.getIdentifierByInode(currentHTMLPage);
			currentHTMLPage = (HTMLPage) IdentifierFactory.getWorkingChildOfClass(id, HTMLPage.class);
		}
		Folder currentParentFolder = (Folder) InodeFactory.getParentOfClass(currentHTMLPage, Folder.class);
		
	    Logger.debug(HTMLPageFactory.class, "Copying HTMLPage: " + currentHTMLPage.getURI(currentParentFolder) + " to: " + parent.getPath());

	    //gets the new information for the template from the request object
		HTMLPage newHTMLPage = new HTMLPage();

		newHTMLPage.copy(currentHTMLPage);
		newHTMLPage.setLocked(false);
		newHTMLPage.setLive(false);

		//gets page url before extension
		String pageURL = com.dotmarketing.util.UtilMethods
				.getFileName(currentHTMLPage.getPageUrl());
		//gets file extension
		String fileExtension = com.dotmarketing.util.UtilMethods
				.getFileExtension(currentHTMLPage.getPageUrl());
		
		boolean isCopy = false;
		while (HTMLPageFactory.existsPageName(parent, pageURL + "." + fileExtension)) {
			pageURL = pageURL + "_copy";
			isCopy = true;
		}
		
		newHTMLPage.setPageUrl(pageURL + "." + fileExtension);
		
		if (isCopy)
			newHTMLPage.setFriendlyName(currentHTMLPage.getFriendlyName() + " (COPY)");

		//persists the webasset
		InodeFactory.saveInode(newHTMLPage);

		//Add the new page to the folder
		parent.addChild(newHTMLPage);

		//creates new identifier for this webasset and persists it
		com.dotmarketing.factories.IdentifierFactory
				.createNewIdentifier(newHTMLPage, parent);

		//gets current template from html page and attach it to the new page
		Template currentTemplate = HTMLPageFactory.getHTMLPageTemplate(currentHTMLPage);
		TreeFactory.saveTree(new Tree(currentTemplate.getIdentifier(), newHTMLPage.getInode(), "parentPageTemplate", 0));

		WorkingCache.removeAssetFromCache(newHTMLPage);
		WorkingCache.addToWorkingAssetToCache(newHTMLPage);
		LiveCache.removeAssetFromCache(newHTMLPage);
		LiveCache.addToLiveAssetToCache(newHTMLPage);

		//Copy permissions
		permissionAPI.copyPermissions(currentHTMLPage, newHTMLPage);
		
		return newHTMLPage;
	}
	
	
    @SuppressWarnings({ "unchecked", "deprecation" })
	public static boolean renameHTMLPage (HTMLPage page, String newName, User user) throws Exception {

    	// Checking permissions
    	if (!permissionAPI.doesUserHavePermission(page, PERMISSION_WRITE, user))
    		throw new ActionException(WebKeys.USER_PERMISSIONS_EXCEPTION);

    	//getting old file properties
    	Folder folder = (Folder)InodeFactory.getParentOfClass(page, Folder.class);
    	
		Host host;
		try {
			User systemUser = APILocator.getUserAPI().getSystemUser();
	        host = hostAPI.findParentHost(folder, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

    	Identifier ident = IdentifierFactory.getIdentifierByInode(page);

    	HTMLPage tempPage = new HTMLPage();
    	tempPage.copy(page);
    	// sets filename for this new file
    	
    	String newNamePage = newName + "." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
    	
    	tempPage.setPageUrl(newNamePage);
    	tempPage.setFriendlyName(newNamePage);

    	Identifier testIdentifier = (Identifier) IdentifierFactory
			.getIdentifierByURI(tempPage.getURI(folder), host);

    	if(InodeUtils.isSet(testIdentifier.getInode()) || page.isLocked())
    		return false;

    	List<HTMLPage> versions = IdentifierFactory.getVersionsandLiveandWorkingChildrenOfClass(ident, HTMLPage.class);
    	
    	boolean islive = false;
    	HTMLPage workingVersion = null;
    	
    	for (HTMLPage version : versions) {

	    	// sets filename for this new file
    		version.setPageUrl(newNamePage);
    		version.setFriendlyName(newNamePage);

	    		
	    	InodeFactory.saveInode(version);
	    	if (version.isLive())
	    		islive = true;
	    	if (version.isWorking())
	    		workingVersion = version;
    	}
    	
   		LiveCache.removeAssetFromCache(workingVersion);
   		WorkingCache.removeAssetFromCache(workingVersion);
   		IdentifierCache.removeAssetFromIdCache(workingVersion);
   		

   		
    	ident.setURI(page.getURI(folder));
    	InodeFactory.saveInode(ident);
    	
    	if (islive){
    		LiveCache.removeAssetFromCache(workingVersion);
    		LiveCache.addToLiveAssetToCache(workingVersion);
    	}
    	WorkingCache.removeAssetFromCache(workingVersion);
   		WorkingCache.addToWorkingAssetToCache(workingVersion);
   		IdentifierCache.removeAssetFromIdCache(workingVersion);
   		IdentifierCache.addVersionableToIdentifierCache(workingVersion);
    	
   		if(page.isShowOnMenu())
   		{
   			//RefreshMenus.deleteMenus();
   			RefreshMenus.deleteMenu(page);
   		}
    	return true;
	}

	public static Folder getParentFolder(HTMLPage object) {
		Folder folder = (Folder) InodeFactory.getParentOfClass(object, Folder.class);
		return folder;
	}

	public static Host getParentHost(HTMLPage object) {
		Folder folder = (Folder) InodeFactory.getParentOfClass(object, Folder.class);
		Host host;
		try {
			User systemUser = APILocator.getUserAPI().getSystemUser();
			host = hostAPI.findParentHost(folder, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(HTMLPageFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
		return host;
	}
}

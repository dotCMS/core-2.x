package com.dotmarketing.portlets.htmlpages.business;

import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.templates.model.Template;
import com.liferay.portal.model.User;

public interface HTMLPageFactory {
	
	public void save(HTMLPage htmlPage) throws DotDataException;
	
	public HTMLPage getLiveHTMLPageByPath(String path, Host host);
	
	public HTMLPage getLiveHTMLPageByPath(String path, String hostId);

	public int findNumOfContent(HTMLPage page, Container container);
	
	public Template getHTMLPageTemplate(HTMLPage i);
	
	public Template getHTMLPageTemplate(Inode i, boolean live);
	
	public Folder getParentFolder(HTMLPage object);

	public Host getParentHost(HTMLPage object);

	public HTMLPage loadWorkingPageById(String pageId) throws DotDataException;
	
	public HTMLPage loadLivePageById(String pageId) throws DotDataException;
	
	public List<HTMLPage> findHtmlPages(User user, boolean includeArchived, Map<String,Object> params, String hostId, String inode, String identifier, String parent, int offset, int limit, String orderBy) throws DotSecurityException, DotDataException;
	
}
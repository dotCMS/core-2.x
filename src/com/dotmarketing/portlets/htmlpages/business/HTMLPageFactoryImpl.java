package com.dotmarketing.portlets.htmlpages.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.templates.factories.TemplateFactory;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PaginatedArrayList;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class HTMLPageFactoryImpl implements HTMLPageFactory {
	static HTMLPageCache htmlPageCache = CacheLocator.getHTMLPageCache();
	
	public void save(HTMLPage htmlPage) throws DotDataException {
		HibernateUtil.save(htmlPage);

		htmlPageCache.remove(htmlPage);

		WorkingCache.removeAssetFromCache(htmlPage);
		WorkingCache.addToWorkingAssetToCache(htmlPage);
		LiveCache.removeAssetFromCache(htmlPage);
		if (htmlPage.isLive()) {
			LiveCache.addToLiveAssetToCache(htmlPage);
		}
	}
	
	public HTMLPage getLiveHTMLPageByPath(String path, Host host) {
	    return getLiveHTMLPageByPath (path, host.getIdentifier());
	}
	
	public HTMLPage getLiveHTMLPageByPath(String path, String hostId) {
        Identifier id = IdentifierFactory.getIdentifierByURI(path, hostId);

        Logger.debug(HTMLPageFactory.class, "Looking for page : " + path);
		Logger.debug(HTMLPageFactory.class, "got id " + id.getInode());
        
        //if this page does not exist, create it, add it to the course folder, use the course template, etc...
        if(!InodeUtils.isSet(id.getInode())){
            return  new HTMLPage();
        }
        
	    return (HTMLPage) IdentifierFactory.getLiveChildOfClass(id, HTMLPage.class);
	    
	}
	
	public int findNumOfContent(HTMLPage page, Container container) {
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
	
	public Template getHTMLPageTemplate(HTMLPage i) {
		return getHTMLPageTemplate(i, false);
	}
	

	public Template getHTMLPageTemplate(Inode i, boolean live) {

		Identifier templateIdent = (Identifier) InodeFactory.getParentOfClassByRelationType(i, Identifier.class, "parentPageTemplate");
		List<Template> templates;
		if (!live) {
			templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and working = " + DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");
		} else {
			templates = TemplateFactory.getTemplateByCondition("deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + "  and live = " + DbConnectionFactory.getDBTrue() + " and identifier = '" + templateIdent.getInode()+"'");
		}
		if(templates.size() > 1)
			Logger.error(HTMLPageFactory.class, "Page " + ((HTMLPage)i).getPageUrl() + " has more than one template associated!!");
		if(templates.size() > 0)
			return (Template) templates.get(0);
		else
			return null;

	}	
	
	public Folder getParentFolder(HTMLPage object) {
		Folder folder = (Folder) InodeFactory.getParentOfClass(object, Folder.class);
		return folder;
	}

	public Host getParentHost(HTMLPage object) {
		HostAPI hostAPI = APILocator.getHostAPI();
		
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

	@SuppressWarnings("unchecked")
	public HTMLPage loadWorkingPageById(String pageId) throws DotDataException {
    	HibernateUtil hu = new HibernateUtil(HTMLPage.class);
    	hu.setSQLQuery("select {htmlpage.*} from htmlpage, inode htmlpage_1_ where htmlpage_1_.identifier = ? and htmlpage.working = ? " +
    			"and htmlpage_1_.inode = htmlpage.inode");
    	hu.setParam(pageId);
    	hu.setParam(true);
    	List<HTMLPage> pages = hu.list();
    	if(pages.size() == 0)
    		return null;
    	return pages.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public HTMLPage loadLivePageById(String pageId) throws DotDataException {
		HTMLPage page = htmlPageCache.get(pageId);
		if(page ==null){
	    	HibernateUtil hu = new HibernateUtil(HTMLPage.class);
	    	hu.setSQLQuery("select {htmlpage.*} from htmlpage, inode htmlpage_1_ where htmlpage_1_.identifier = ? and htmlpage.live = ? " +
	    			"and htmlpage_1_.inode = htmlpage.inode");
	    	hu.setParam(pageId);
	    	hu.setParam(true);
	    	List<HTMLPage> pages = hu.list();
	    	if(pages.size() == 0)
	    		return null;

	    	page = pages.get(0);
	    	htmlPageCache.add(page);
	    	
		}
    	return page;
	}
	public List<HTMLPage> findHtmlPages(User user, boolean includeArchived,
			Map<String, Object> params, String hostId, String inode, String identifier, String parent,
			int offset, int limit, String orderBy) throws DotSecurityException,
			DotDataException {

		PaginatedArrayList<HTMLPage> assets = new PaginatedArrayList<HTMLPage>();
		List<Permissionable> toReturn = new ArrayList<Permissionable>();
		int internalLimit = 500;
		int internalOffset = 0;
		boolean done = false;

		StringBuffer conditionBuffer = new StringBuffer();
		String condition = !includeArchived?" asset.working = " + DbConnectionFactory.getDBTrue() + " and asset.deleted = " +DbConnectionFactory.getDBFalse():
			" asset.working = " + DbConnectionFactory.getDBTrue();
		conditionBuffer.append(condition);

		List<Object> paramValues =null;
		if(params!=null && params.size()>0){
			conditionBuffer.append(" and (");
			paramValues = new ArrayList<Object>();
			int counter = 0;
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if(counter==0){
					if(entry.getValue() instanceof String){
						if(entry.getKey().equalsIgnoreCase("inode")){
							conditionBuffer.append(" asset." + entry.getKey()+ " = '" + entry.getValue() + "'");
						}else{
							conditionBuffer.append(" lower(asset." + entry.getKey()+ ") like ? ");
							paramValues.add("%"+ ((String)entry.getValue()).toLowerCase()+"%");
						}
					}else{
						conditionBuffer.append(" asset." + entry.getKey()+ " = " + entry.getValue());
					}	
				}else{
					if(entry.getValue() instanceof String){
						if(entry.getKey().equalsIgnoreCase("inode")){
							conditionBuffer.append(" OR asset." + entry.getKey()+ " = '" + entry.getValue() + "'");
						}else{
							conditionBuffer.append(" OR lower(asset." + entry.getKey()+ ") like ? ");
							paramValues.add("%"+ ((String)entry.getValue()).toLowerCase()+"%");
						}
					}else{
						conditionBuffer.append(" OR asset." + entry.getKey()+ " = " + entry.getValue());
					}	
				}

				counter+=1;
			}
			conditionBuffer.append(" ) ");
		}

		StringBuffer query = new StringBuffer();
		query.append("select asset from asset in class " + HTMLPage.class.getName() + ", " +
				"inode in class " + Inode.class.getName()+", ident in class " + Identifier.class.getName());
		if(UtilMethods.isSet(parent)){
			query.append(" ,tree in class " + Tree.class.getName() + " where asset.inode=inode.inode " +
					"and inode.identifier = ident.inode and tree.parent = '"+parent+"' and tree.child=asset.inode");

		}else{
			query.append(" where asset.inode=inode.inode and inode.identifier = ident.inode");
		}
		if(UtilMethods.isSet(hostId)){	
			query.append(" and ident.hostId = '"+ hostId +"'");
		}
		if(UtilMethods.isSet(inode)){	
			query.append(" and asset.inode = '"+ inode +"'");
		}
		if(UtilMethods.isSet(identifier)){	
			query.append(" and inode.identifier = '"+ identifier +"'");
		}
		if(!UtilMethods.isSet(orderBy)){
			orderBy = "modDate desc";
		}

		List<HTMLPage> resultList = new ArrayList<HTMLPage>();
		DotHibernate dh = new DotHibernate(HTMLPage.class);
		String type;
		int countLimit = 100;
		int size = 0;
		try {
			type = ((Inode) HTMLPage.class.newInstance()).getType();
			query.append(" and asset.type='"+type+ "' and " + conditionBuffer.toString() + " order by asset." + orderBy);
			dh.setQuery(query.toString());

			if(paramValues!=null && paramValues.size()>0){
				for (Object value : paramValues) {
					dh.setParam((String)value);
				}			
			}

			while(!done) { 
				dh.setFirstResult(internalOffset);
				dh.setMaxResults(internalLimit);		
				resultList = dh.list();
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				toReturn.addAll(permAPI.filterCollection(resultList, PermissionAPI.PERMISSION_READ, false, user));
				if(countLimit > 0 && toReturn.size() >= countLimit + offset)
					done = true;
				else if(resultList.size() < internalLimit)
					done = true;

				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				size = 0;
			} else if(countLimit > 0) {
				int toIndex = offset + countLimit > toReturn.size()?toReturn.size():offset + countLimit;
				size = toReturn.subList(offset, toIndex).size();
			} else if (offset > 0) {
				size = toReturn.subList(offset, toReturn.size()).size();
			}
			assets.setTotalResults(size);
			int from = offset<toReturn.size()?offset:0;
			int pageLimit = 0;
			for(int i=from;i<toReturn.size();i++){
				if(pageLimit<limit){
					assets.add((HTMLPage) toReturn.get(i));
					pageLimit+=1;
				}else{
					break;
				}

			}

		} catch (Exception e) {

			Logger.error(HTMLPageFactoryImpl.class, "findHtmlPages failed:" + e, e);
			throw new DotRuntimeException(e.toString());
		}

		return assets;
	}
	
}
package com.dotmarketing.portlets.templates.business;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.dotmarketing.business.PermissionedWebAssetUtil;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.portlets.templates.model.TemplateWrapper;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PaginatedArrayList;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class TemplateFactoryImpl implements TemplateFactory {
	static TemplateCache templateCache = CacheLocator.getTemplateCache();

	private final String subTemplatesSQL = 
		"select {template.*} from template, inode template_1_, inode template_identifier, " +
		"tree template_page_tree, inode parentPage, tree pageFolderTree	where " +
		"pageFolderTree.parent = ? and pageFolderTree.child = parentPage.inode and parentPage.type='htmlpage' and " +
		"template_page_tree.child = parentPage.inode and " +
		"template_page_tree.parent = template_identifier.inode and " +
		"template.inode = template_1_.inode and template_identifier.inode = template_1_.identifier and " +
		"template.working = " + DbConnectionFactory.getDBTrue();
	
	private final String templatesUnderHostSQL = 
		"select {template.*} from template, inode template_1_, " +
		"tree template_host_tree where " +
		"template_host_tree.parent = ? and template_host_tree.child = template.inode and " +
		"template.inode = template_1_.inode and " +
		"template.working = " + DbConnectionFactory.getDBTrue();

	
	@SuppressWarnings("unchecked")
	public List<Template> findTemplatesUnder(Folder parentFolder) throws DotHibernateException {
		HibernateUtil hu = new HibernateUtil(Template.class);
		hu.setSQLQuery(subTemplatesSQL);
		hu.setParam(parentFolder.getInode());
		return new ArrayList<Template>(new HashSet<Template>(hu.list()));
	}
	
	@SuppressWarnings("unchecked")
	public List<Template> findTemplatesAssignedTo(Host parentHost, boolean includeArchived) throws DotHibernateException {
		HibernateUtil hu = new HibernateUtil(Template.class);
		String query = !includeArchived?templatesUnderHostSQL + " and template.deleted = " + DbConnectionFactory.getDBFalse():templatesUnderHostSQL;
		hu.setSQLQuery(templatesUnderHostSQL);
		hu.setParam(parentHost.getIdentifier());
		return new ArrayList<Template>(new HashSet<Template>(hu.list()));
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Template> findTemplatesUserCanUse(User user, String hostName, String query,boolean searchHost ,int offset, int limit) throws DotDataException, DotSecurityException {
		return PermissionedWebAssetUtil.findTemplatesForLimitedUser(query, hostName, searchHost, "title", offset, limit, PermissionAPI.PERMISSION_READ, user, false);
	}

	public void delete(Template template) throws DotDataException {
		HibernateUtil.delete(template);
	}
	
	public void save(Template template) throws DotDataException {
		HibernateUtil.save(template);
		templateCache.add(template.getInode(), template);
		//WorkingCache.removeAssetFromCache(template);
		//WorkingCache.addToWorkingAssetToCache(template);
		//LiveCache.removeAssetFromCache(template);
		//if (template.isLive()) {
		//	LiveCache.addToLiveAssetToCache(template);
		//}
	}
	
	public void deleteFromCache(Template template) throws DotDataException {
		templateCache.remove(template.getInode());
		//WorkingCache.removeAssetFromCache(template);
		//if (template.isLive()) {
		//	LiveCache.removeAssetFromCache(template);
		//}
		IdentifierCache.removeAssetFromIdCache(template);
	}

	@SuppressWarnings("unchecked")
	public Template findWorkingTemplate(String id) throws DotDataException {
		Template ret  = templateCache.get(id);
		if(ret!=null && InodeUtils.isSet(ret.getInode())){
			return ret;
		}
		HibernateUtil hu = new HibernateUtil(Template.class);
		hu.setSQLQuery("select {template.*} from template, inode template_1_ " +
				"where template_1_.identifier = ? and " +
				"template.inode = template_1_.inode and " +
				"template.working = " + DbConnectionFactory.getDBTrue());
		hu.setParam(id);
		List<Template>  list = hu.list();
		if(list.isEmpty()){
			return null;
		}else{
			ret = list.get(0);
			templateCache.add(ret.getIdentifier(), ret);
			return ret;
		}
		
	}

	public List<Template> findTemplates(User user, boolean includeArchived,
			Map<String, Object> params, String hostId, String inode, String identifier, String parent,
			int offset, int limit, String orderBy) throws DotSecurityException,
			DotDataException {
		
		PaginatedArrayList<Template> assets = new PaginatedArrayList<Template>();
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
		query.append("select asset from asset in class " + Template.class.getName() + ", " +
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

		List<Template> resultList = new ArrayList<Template>();
		DotHibernate dh = new DotHibernate(Template.class);
		String type;
		int countLimit = 100;
		int size = 0;
		try {
			type = ((Inode) Template.class.newInstance()).getType();
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
					assets.add((Template) toReturn.get(i));
					pageLimit+=1;
				}else{
					break;
				}

			}

		} catch (Exception e) {

			Logger.error(TemplateFactoryImpl.class, "findTemplates failed:" + e, e);
			throw new DotRuntimeException(e.toString());
		}

		return assets;
		

	}

}

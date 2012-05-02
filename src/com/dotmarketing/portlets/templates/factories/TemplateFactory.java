package com.dotmarketing.portlets.templates.factories;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

/**
 * 
 * @author will, david (2005)
 */
public class TemplateFactory {

	public static java.util.List getActiveTemplates() {
		DotHibernate dh = new DotHibernate(Template.class);
		dh
				.setQuery("from inode in class com.dotmarketing.portlets.templates.model.Template where type='template'");

		return dh.list();
	}

	public static java.util.List getTemplatesByOrderAndParent(String orderby,
			Inode i) {
		return InodeFactory.getChildrenClassByOrder(i, Template.class, orderby);
	}

	public static java.util.List getTemplatesByOrder(String orderby) {
		DotHibernate dh = new DotHibernate(Template.class);
		dh
				.setQuery("from inode in class com.dotmarketing.portlets.templates.model.Template where type='template' and working = "
						+ com.dotmarketing.db.DbConnectionFactory.getDBTrue()
						+ " or live = "
						+ com.dotmarketing.db.DbConnectionFactory.getDBTrue()
						+ " order by " + orderby);

		return dh.list();
	}

	public static java.util.List getTemplatesByOrderAndWorking(String orderby) {
		DotHibernate dh = new DotHibernate(Template.class);
		dh
				.setQuery("from inode in class com.dotmarketing.portlets.templates.model.Template where type='template' and working = "
						+ com.dotmarketing.db.DbConnectionFactory.getDBTrue()
						+ "  order by " + orderby);

		return dh.list();
	}

	@SuppressWarnings("unchecked")
	public static java.util.List<Template> getTemplateByCondition(String condition) {
		DotHibernate dh = new DotHibernate(Template.class);
		dh
				.setQuery("from inode in class com.dotmarketing.portlets.templates.model.Template where type='template' and "
						+ condition + " order by title, sort_order");

		return dh.list();
	}

	public static Template getTemplateByLiveAndFolderAndTitle(Inode parent,
			String title) {
		return (Template) InodeFactory.getChildOfClassbyCondition(parent,
				Template.class, " title =  '" + title + "' and live = "
						+ com.dotmarketing.db.DbConnectionFactory.getDBTrue());
	}

	public static List getTemplates(com.liferay.portal.model.User user) {
		java.util.List templates = new ArrayList();
		try {
			String condition = "working="
					+ com.dotmarketing.db.DbConnectionFactory.getDBTrue()
					+ " and deleted="
					+ com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			// gets all user roles
			Role[] roles = APILocator.getRoleAPI().loadRolesForUser(user.getUserId()).toArray(new Role[0]);
		
			int limit = 0;
			int offset = 0;
			String orderby = "title";
			// gets all templates this user has permissions to read
			templates = WebAssetFactory
					.getAssetsAndPermissionsPerRoleAndCondition(roles,
							condition, limit, offset, orderby, Template.class,
							"template", user);
		} catch (Exception e) {
		}
		return templates;

	}

	public static List getTemplates(Role[] roles, User user) {
		java.util.List templates = new ArrayList();
		try {
			String condition = "working="
					+ com.dotmarketing.db.DbConnectionFactory.getDBTrue()
					+ " and deleted="
					+ com.dotmarketing.db.DbConnectionFactory.getDBFalse();
			int limit = 0;
			int offset = 0;
			String orderby = "title";
			// gets all templates this user has permissions to read
			templates = WebAssetFactory
					.getAssetsAndPermissionsPerRoleAndCondition(roles,
							condition, limit, offset, orderby, Template.class,
							"template", user);
		} catch (Exception e) {
		}
		return templates;

	}

	public static File getImageFile(Template template) {
		String imageIdentifierInode = template.getImage();
		Identifier identifier = new Identifier();
		try {
			identifier = IdentifierCache.getIdentifierFromIdentifierCache(imageIdentifierInode);
		} catch (DotHibernateException e) {
			Logger.error(TemplateFactory.class,e.getMessage(),e);
		}
		File imageFile = new File();
		if(InodeUtils.isSet(identifier.getInode())){
			imageFile = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
		}
		return imageFile;
	}

	@SuppressWarnings("unchecked")
	public static Template copyTemplate(Template currentTemplate) throws DotDataException {

		Template newTemplate = new Template();

		newTemplate.copy(currentTemplate);
		newTemplate.setLocked(false);
		newTemplate.setLive(false);
		newTemplate.setImage(currentTemplate.getImage());
		newTemplate.setFriendlyName(currentTemplate.getFriendlyName()
				+ " (COPY) ");
		newTemplate.setTitle(currentTemplate.getTitle() + " (COPY) ");

		// persists the webasset
		InodeFactory.saveInode(newTemplate);

		// gets containers children (we attach identifier to templates instead
		// of the container inode)
		java.util.List<Identifier> children = (java.util.List<Identifier>) InodeFactory
				.getChildrenClass(currentTemplate, Identifier.class);
		java.util.Set<Identifier> childrenSet = new java.util.HashSet<Identifier>();
		childrenSet.addAll(children);
		
		for(Identifier id : children ){
			newTemplate.addChild(id);
		}
		
		//Copy the host
		HostAPI hostAPI = APILocator.getHostAPI();
		Host h;
		try {
			h = hostAPI.findParentHost(currentTemplate, APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotSecurityException e) {
			Logger.error(TemplateFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} 
		TreeFactory.saveTree(new Tree(h.getIdentifier(), newTemplate.getInode()));
		

		// creates new identifier for this webasset and persists it
		Identifier newIdentifier = com.dotmarketing.factories.IdentifierFactory.createNewIdentifier(newTemplate, h);
		Logger.debug(TemplateFactory.class, "Parent newIdentifier="
				+ newIdentifier.getInode());
		
		
		//Copy the host again
		newIdentifier.setHostId(h.getIdentifier());
		// Copy permissions
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		perAPI.copyPermissions(currentTemplate, newTemplate);

		
		
		
		return newTemplate;
	}

}

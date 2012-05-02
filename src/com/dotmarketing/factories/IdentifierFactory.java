package com.dotmarketing.factories;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.Versionable;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Parameter;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

/**
 *
 * @author will
 * 
 */
public class IdentifierFactory {

	public static Object getLiveChildOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			} catch (IllegalAccessException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			}
		}
        Inode ret = (Inode) InodeFactory.getInodeOfClassByCondition(c, "live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() +
                    " and identifier = '" + o.getInode()+"'");
        if(InodeUtils.isSet(ret.getInode())) {
            return ret;
        }
		return InodeFactory.getChildOfClassbyCondition(o, c, "live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
	}

	public static Object getChildOfClassByRelationType(Inode o, Class c, String relationType, boolean previewMode) {
		return getChildOfClassByRelationType(o.getInode(), c, relationType, previewMode);
	}

	public static Object getChildOfClassByRelationType(String inode, Class c, String relationType, boolean previewMode) {
		String condition = (previewMode) ? " working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " "
				: " live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " ";
		return InodeFactory.getChildOfClassByRelationTypeAndCondition(inode, c, relationType, condition);

	}

	public static Object getLiveChildOfClassByCondition(Inode o, Class c, String condition) {
		if(!InodeUtils.isSet(o.getInode())){
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			} catch (IllegalAccessException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			}
		}
		condition += " and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        Inode ret = (Inode) InodeFactory.getInodeOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'");
        if(InodeUtils.isSet(ret.getInode())) {
        	return ret;
        }
		return InodeFactory.getChildOfClassbyCondition(o, c, condition);
	}

	public static Object getLiveChildOfClassByRelationType(Inode o, Class c, String relationType) {
		return getChildOfClassByRelationType(o, c, relationType, false);
	}

	public static Object getWorkingChildOfClassByRelationType(Inode o, Class c, String relationType) {
		return getChildOfClassByRelationType(o, c, relationType, true);
	}

	public static java.util.List getLiveChildrenOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = " live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByCondition(o, c, condition);
	}

	public static java.util.List getLiveChildrenOfClass(Inode o, Class c, String condition) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		condition += " and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByCondition(o, c, condition);
	}

	public static java.util.List getWorkingChildrenOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByCondition(o, c, "working="
				+ com.dotmarketing.db.DbConnectionFactory.getDBTrue());
	}

	public static java.util.List getWorkingChildrenOfClass(Inode o, Class c, String orderBy) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'", orderBy);
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByConditionAndOrderBy(o, c, condition, orderBy);
	}

	public static java.util.List getWorkingChildrenOfClassByCondition(Inode o, Class c, String condition) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		condition += " and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition +
                " and identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByCondition(o, c, condition);
	}

	public static Object getWorkingChildOfClass(Inode inode, Class c) {
		if(!InodeUtils.isSet(inode.getInode())){
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			} catch (IllegalAccessException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			}
		}
        Inode ret = (Inode) InodeFactory.getInodeOfClassByCondition(c, "working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() +
                " and identifier = '" + inode.getInode()+"'");
        if(InodeUtils.isSet(ret.getInode())) {
            return ret;
        }
		return InodeFactory.getChildOfClassbyCondition(inode, c, "working = "
				+ com.dotmarketing.db.DbConnectionFactory.getDBTrue());
	}

	public static Object getWorkingChildOfClassByCondition(Inode inode, Class c, String condition) {
		if(!InodeUtils.isSet(inode.getInode())){
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			} catch (IllegalAccessException e) {
				Logger.error(IdentifierFactory.class,e.getMessage(),e);
			}
		}
		condition += " and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        Inode ret = (Inode) InodeFactory.getInodeOfClassByCondition(c, condition +
                " and identifier = '" + inode.getInode()+"'");
        if(InodeUtils.isSet(ret.getInode())) {
        	return ret;
        }
		return InodeFactory.getChildOfClassbyCondition(inode, c, condition);
	}

	public static java.util.List getWorkingOfClass(Class c) {
		try {
			DotHibernate dh = new DotHibernate(c);
			String type = ((Inode) c.newInstance()).getType();
			dh.setQuery("from inode in class " + c.getName() + " where type='"+type+"' and working = "
					+ com.dotmarketing.db.DbConnectionFactory.getDBTrue());

			return dh.list();
		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getWorkingOfClass failed:" + e, e);
		}
		return new java.util.ArrayList();
	}

	public static java.util.List getLiveOfClass(Class c) {
		try {
			DotHibernate dh = new DotHibernate(c);
			String type = ((Inode) c.newInstance()).getType();
			dh.setQuery("from inode in class " + c.getName() + " where type='"+type+"' and live = "
					+ com.dotmarketing.db.DbConnectionFactory.getDBTrue());

			return dh.list();
		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getWorkingOfClass failed:" + e, e);
		}
		return new java.util.ArrayList();
	}

	public static void updateIdentifierURI(WebAsset webasset, Folder folder) {

		Identifier identifier = getIdentifierByInode(webasset);

		if (webasset instanceof HTMLPage) {
			identifier.setURI(folder.getPath() + ((HTMLPage) webasset).getPageUrl());
		} else if (webasset instanceof File) {
			identifier.setURI(folder.getPath() + ((File) webasset).getFileName());
		} else if (webasset instanceof Link) {
			identifier.setURI(folder.getPath() + ((Link) webasset).getProtocal() + ((Link) webasset).getUrl());
		}

		else {
			identifier.setURI(folder.getPath() + identifier.getInode());
		}

		InodeFactory.saveInode(identifier);

	}

	public static java.util.List getVersionsandLiveandWorkingChildrenOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
        List ret = InodeFactory.getInodesOfClassByCondition(c, "identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByConditionAndOrderBy(o, c, " 1 = 1 ", "mod_date desc");
	}

	public static java.util.List getVersionsandLiveChildrenOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "working <> " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition + " and identifier = '" + o.getInode()+"'", "mod_date desc ");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByConditionAndOrderBy(o, c, condition, "mod_date desc ");
	}

	public static java.util.List getVersionsandLiveChildrenOfClass(Inode o, Class c, String orderBy) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "working <> " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition + " and identifier = '" + o.getInode()+"'", orderBy);
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByConditionAndOrderBy(o, c, "working <> "
				+ com.dotmarketing.db.DbConnectionFactory.getDBTrue(), orderBy);

	}

	public static java.util.List getVersionsChildrenOfClass(Inode o, Class c) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "live <> " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition + " and identifier = '" + o.getInode()+"'");
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByCondition(o, c, condition);

	}

	public static java.util.List getVersionsChildrenOfClass(Inode o, Class c, String orderBy) {
		if(!InodeUtils.isSet(o.getInode())){
			return new ArrayList();
		}
		String condition = "live <> " + com.dotmarketing.db.DbConnectionFactory.getDBTrue();
        List ret = InodeFactory.getInodesOfClassByCondition(c, condition + " and identifier = '" + o.getInode()+"'", orderBy);
        if(ret.size() > 0) {
        	return ret;
        }
		return InodeFactory.getChildrenClassByConditionAndOrderBy(o, c, condition, orderBy);

	}

	public static Identifier getIdentifierByURI(String uri, Host host) {
		return getIdentifierByURI(uri, host.getIdentifier());
	}

	public static Identifier getIdentifierByURI(String uri, String hostId) {
		DotHibernate dh = new DotHibernate(Identifier.class);
		dh.setQuery("from inode in class com.dotmarketing.beans.Identifier where type='identifier' and uri = ? and host_inode = ?");
		dh.setParam(uri);
		dh.setParam(hostId);
		return (Identifier) dh.load();
	}


	public static Identifier getParentIdentifier(WebAsset webasset) {

		return (Identifier) getIdentifierByInode(webasset);

	}

	/**
	 * Gets the parent Identifer by the inode.
	 * @param inode This takes an inode and finds the identifier for it.
	 * @return the Identifier inode
	 */
    public static Identifier getIdentifierByInode(Versionable versionable) {
    	if(versionable == null){
    		return new Identifier();
    	}
        if (!InodeUtils.isSet(versionable.getInode())) {
            return new Identifier();
        }

        Identifier id = new Identifier ();
        id = (Identifier) InodeFactory.getInode(versionable.getVersionId(), Identifier.class);
        if(!InodeUtils.isSet(id.getInode()))
        	id = (Identifier) InodeFactory.getParentOfClass(String.valueOf(versionable.getInode()), Identifier.class);
        return id;
	}

	public static Identifier getIdentifierByWebAsset(WebAsset webAsset) {
		return getIdentifierByInode((Inode) webAsset);
	}

	public static Identifier createNewIdentifier(WebAsset webasset, Folder folder) {

		Identifier identifier = new Identifier();

		try {
			User systemUser = APILocator.getUserAPI().getSystemUser();
			HostAPI hostAPI = APILocator.getHostAPI();
			
	
			// get an inode #
	
			if (InodeUtils.isSet(webasset.getInode())) {
				Logger.debug(IdentifierFactory.class, "createNewIdentifier Identifier=" + identifier.getInode());
	
				identifier.setURI(webasset.getURI(folder));
	
				Host host = hostAPI.findParentHost(folder, systemUser, false);
	
				identifier.setHostId(host.getIdentifier());
	
				Logger.debug(IdentifierFactory.class, "createNewIdentifier for asset=" + identifier.getURI());
	
				InodeFactory.saveInode(identifier);
				identifier.addChild(webasset);
				webasset.setIdentifier(identifier.getInode());
	
				// set the identifier on the inode for future reference.
				// and for when we get rid of identifiers all together
				InodeFactory.saveInode(identifier);
	
				Logger.debug(IdentifierFactory.class, "createNewIdentifier Web Asset=" + webasset.getInode());
	
				InodeFactory.saveInode(identifier);
				InodeFactory.saveInode(webasset);
				DotHibernate.flush();
				IdentifierCache.removeAssetFromIdCache(webasset);
				IdentifierCache.addVersionableToIdentifierCache(webasset);
			}
		} catch (DotSecurityException e) {
			Logger.error(IdentifierFactory.class, "Unable to create new identifier", e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotDataException e) {
			Logger.error(IdentifierFactory.class, "Unable to create new identifier", e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

		return identifier;
	}

	public static Identifier createNewIdentifier(Versionable versionable, Host host) {

		Identifier identifier = new Identifier();
        identifier.setURI(versionable.getVersionType() + "." + versionable.getInode());
        identifier.setHostId(host != null?host.getIdentifier():null);
		// get an inode #
		Logger.debug(IdentifierFactory.class, "createNewIdentifier for asset=" + versionable.getInode());

		// set the identifier on the inode for future reference.
		// and for when we get rid of identifiers all together
		InodeFactory.saveInode(identifier);
		
		try{
			APILocator.getRelationshipAPI().addRelationship(identifier.getInode(), versionable.getInode(), "child");
		}catch (Exception e) {
			Logger.error(IdentifierFactory.class,"Unable to add relationship to identifier", e);
		}
		
		Logger.debug(IdentifierFactory.class, "createNewIdentifier Web Asset=" + versionable.getVersionId());

		//Hack until code gets refactored with new architecture
		if(versionable instanceof WebAsset){
			((WebAsset)versionable).setIdentifier(identifier.getInode());
			InodeFactory.saveInode((WebAsset)versionable);	
		}
		
		DotHibernate.flush();
//		IdentifierCache.addVersionableToIdentifierCache(versionable);

		return identifier;
	}

	public static List getIdentifiersPerConditionWithPermission(Host host, String condition, Class c, Role[] roles,
			int limit, int offset, String orderby) {
		return getIdentifiersPerConditionWithPermission(host.getIdentifier(), condition, c, roles, limit, offset, orderby);
	}

	public static List getIdentifiersPerConditionWithPermission(String hostId, String condition, Class c, Role[] roles,
			int limit, int offset, String orderby) {

		DotHibernate dh = new DotHibernate(Identifier.class);

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			String rolesStr = "";

			for (int i = 0; i < roles.length; i++) {
				rolesStr += roles[i].getId();
				if (i != (roles.length - 1)) {
					rolesStr += ",";
				}
			}
			sb
					.append("select {identifier.*} from identifier, inode identifier_1_ where identifier.inode = identifier_1_.inode and identifier.inode in ( ");
			sb.append("select distinct identifier.inode from identifier identifier, ");
			sb.append("tree tree, " + tableName + " " + tableName + "_condition, permission permission ");
			sb.append("where " + condition);
			sb.append("and identifier.inode = tree.parent and identifier_1_.type='identifier' ");
			sb.append("and tree.child = " + tableName + "_condition.inode ");
			sb.append("and identifier.inode = permission.inode_id ");
			sb.append("and permission.roleid in (" + rolesStr + ") ");
			String permCond = DotConnect.bitOR("permission.permission", String.valueOf(PERMISSION_READ));
			sb.append(" and " + permCond + " <> 0) ");
			sb.append("and identifier.host_inode = " + hostId + " ");

			if (limit != 0) {
				dh.setFirstResult(offset);
				dh.setMaxResults(limit);
			}
			Logger.debug(IdentifierFactory.class, sb.toString());

			dh.setSQLQuery(sb.toString());
			return dh.list();

		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getIdentifiersPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList();
	}

	public static List getIdentifiersPerConditionWithPermission(String condition, Class c, Role[] roles, int limit,
			int offset, String orderby) {

		DotHibernate dh = new DotHibernate(Identifier.class);

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			String rolesStr = "";

			for (int i = 0; i < roles.length; i++) {
				rolesStr += roles[i].getId();
				if (i != (roles.length - 1)) {
					rolesStr += ",";
				}
			}
			sb
					.append("select {identifier.*} from identifier, inode identifier_1_ where identifier.inode = identifier_1_.inode and identifier.inode in ( ");
			sb.append("select distinct identifier.inode from identifier identifier, ");
			sb.append("tree tree, " + tableName + " " + tableName + "_condition, permission permission ");
			sb.append("where " + condition);
			sb.append("and identifier.inode = tree.parent  and identifier_1_.type='identifier' ");
			sb.append("and tree.child = " + tableName + "_condition.inode ");
			sb.append("and identifier.inode = permission.inode_id ");
			sb.append("and permission.roleid in (" + rolesStr + ") ");
			String permCond = DotConnect.bitOR("permission.permission", String.valueOf(PERMISSION_READ));
			sb.append(" and " + permCond + " <> 0) ");

			if (limit != 0) {
				dh.setFirstResult(offset);
				dh.setMaxResults(limit);
			}
			Logger.debug(IdentifierFactory.class, sb.toString());

			dh.setSQLQuery(sb.toString());
			return dh.list();

		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getIdentifiersPerConditionWithPermission failed:" + e, e);
		}

		return new ArrayList();
	}

	public static int getCountIdentifiersPerConditionWithPermission(Host host, String condition, Class c, Role[] roles) {
		return getCountIdentifiersPerConditionWithPermission(host.getIdentifier(), condition, c, roles);
	}

	public static int getCountIdentifiersPerConditionWithPermission(String hostId, String condition, Class c, Role[] roles) {
		DotConnect db = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			String rolesStr = "";

			for (int i = 0; i < roles.length; i++) {
				rolesStr += roles[i].getId();
				if (i != (roles.length - 1)) {
					rolesStr += ",";
				}
			}
			sb.append("select count(distinct identifier.inode) mycount from identifier identifier, ");
			sb.append("tree tree, " + tableName + " " + tableName + "_condition, permission permission ");
			sb.append("where " + condition);
			sb.append("and identifier.inode = tree.parent ");
			sb.append("and tree.child = " + tableName + "_condition.inode ");
			sb.append("and identifier.inode = permission.inode_id ");
			sb.append("and permission.roleid in (" + rolesStr + ") ");
			String permCond = DotConnect.bitOR("permission.permission", String.valueOf(PERMISSION_READ));
			sb.append(" and " + permCond + " <> 0 ");
			sb.append("and identifier.host_inode = '" + hostId + "' ");

			Logger.debug(IdentifierFactory.class, sb.toString());

			db.setSQL(sb.toString());
			return db.getInt("mycount");

		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getIdentifiersPerConditionWithPermission failed:" + e, e);
		}

		return 0;
	}

	public static int getCountIdentifiersPerConditionWithPermission(String condition, Class c, Role[] roles) {

		DotConnect db = new DotConnect();

		StringBuffer sb = new StringBuffer();
		try {

			String tableName = ((Inode) c.newInstance()).getType();

			String rolesStr = "";

			for (int i = 0; i < roles.length; i++) {
				rolesStr += roles[i].getId();
				if (i != (roles.length - 1)) {
					rolesStr += ",";
				}
			}
			sb.append("select count(distinct identifier.inode) as mycount from identifier identifier, ");
			sb.append("tree tree, " + tableName + " " + tableName + "_condition, permission permission ");
			sb.append("where " + condition);
			sb.append("and identifier.inode = tree.parent ");
			sb.append("and tree.child = " + tableName + "_condition.inode ");
			sb.append("and identifier.inode = permission.inode_id ");
			sb.append("and permission.roleid in (" + rolesStr + ") ");
			String permCond = DotConnect.bitOR("permission.permission", String.valueOf(PERMISSION_READ));
			sb.append(" and " + permCond + " <> 0 ");

			Logger.debug(IdentifierFactory.class, sb.toString());

			db.setSQL(sb.toString());
			return db.getInt("mycount");

		} catch (Exception e) {
			Logger.warn(IdentifierFactory.class, "getIdentifiersPerConditionWithPermission failed:" + e, e);
		}

		return 0;
	}

	public static Identifier getIdentifierByInodeNoLock(Inode inode) {
        return getIdentifierByInodeNoLock (inode, true);
	}

    public static Identifier getIdentifierByInodeNoLock(Inode inode, boolean create) {
        DotHibernate dh = new DotHibernate(Identifier.class);
    	if (!InodeUtils.isSet(inode.getInode())) {
            return new Identifier();
        }

        Identifier id = null;

        StringBuffer querie = new StringBuffer();
        if (InodeUtils.isSet(inode.getIdentifier())) {
        	querie.append("Select {identifier.*} from identifier with (nolock), inode identifier_1_ with (nolock)");
        	querie.append(" where identifier.inode = identifier_1_.inode and identifier_1_.type='identifier'  and identifier.inode='"+inode.getIdentifier()+"'");

        	dh.setSQLQuery(querie.toString());
            id = (Identifier) dh.load();

        } else {
            Logger.debug(IdentifierFactory.class, "getIdentifierByInode: " + inode.getInode());
            querie.append("Select {identifier.*} from identifier with (nolock), inode identifier_1_ with (nolock), tree with (nolock)");
        	querie.append(" where tree.child = '"+inode.getInode()+"' and tree.parent = identifier.inode and identifier_1_.type='identifier'  and identifier.inode = identifier_1_.inode");

        	dh.setSQLQuery(querie.toString());
            id = (Identifier) dh.list().get(0);
        }

        /* save it for future reference */
        if (InodeUtils.isSet(id.getInode()) && (!InodeUtils.isSet(inode.getIdentifier()))) {
            inode.setIdentifier(id.getInode());
        }
        return id;
    }

    @SuppressWarnings("unchecked")
	public static List<Identifier> getAllIdentifiers() {
		DotHibernate dh = new DotHibernate(Identifier.class);
		dh.setQuery("from inode in class com.dotmarketing.beans.Identifier where type='identifier' ");

		return (List<Identifier>) dh.list();

	}
	public static java.util.List<String> getIdentifiersWithWorkingChildrenWebAsset(String webAssetName, String condition) {

		ArrayList<String> inodesList = new ArrayList();
		DotConnect db = new DotConnect();
		String query = "select identifier.inode " +
					    "from identifier, " +
					   	"inode, " +
					   	webAssetName + " " +
			  			"where inode.identifier = identifier.inode and " +
			  			"inode.inode = '" + webAssetName + "'.inode and " +
			  			webAssetName + ".working = " + DbConnectionFactory.getDBTrue();

		if (UtilMethods.isSet(condition)) {
			query += " and " + condition;
		}
		db.setSQL(query);

		ArrayList<Map<String, String>> results = db.getResults();
		for (Map<String, String> result : results) {
			String inode = result.get("inode");
			try {
				inodesList.add(inode);
			} catch (NumberFormatException e) {
				Logger.warn(IdentifierFactory.class, "Exception getIdentifiersWithWorkingChildrenWebAsset", e);
			}
		}
		return inodesList;
	}

	public static boolean isIdentifier(String identifierInode){
		DotConnect dc = new DotConnect();
		dc.setSQL("select count(*) as count from identifier where inode = ?");
		dc.addParam(identifierInode);
		ArrayList<Map<String, String>> results = dc.getResults();
		int count = Parameter.getInt(results.get(0).get("count"),0);
		if(count > 0){
			return true;
		}
		return false;
	}
	
	public static List getParentsOfClass(Inode inode, Class clazz){
		
		
		return InodeFactory.getParentsOfClass(inode, clazz);
		
		
		
	}
	
	public static Object getParentOfClass(Inode inode, Class clazz){
		
		
		return InodeFactory.getParentOfClass(inode, clazz);
		
		
		
	}
	
	
	
}

package com.dotmarketing.portlets.folders.factories;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_PUBLISH;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.PermissionCache;
import com.dotmarketing.business.Role;
import com.dotmarketing.business.web.UserWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.FolderCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.factories.HTMLPageFactory;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.factories.LinkFactory;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.services.PageServices;
import com.dotmarketing.util.AssetsComparator;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;
import com.liferay.portlet.ActionRequestImpl;

/**
 *
 * @author maria
 */
public class FolderFactory {
	private static int nodeId;
	
	private static java.text.DateFormat loginDateFormat;

	private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();
	
	private static HostAPI hostAPI = APILocator.getHostAPI();
	
	private static UserWebAPI userWebAPI = WebAPILocator.getUserWebAPI();
	
	public static final String SYSTEM_FOLDER = "SYSTEM_FOLDER";
	
	/**
	 * @param permissionAPI the permissionAPI to set
	 */
	public static void setPermissionAPI(PermissionAPI permissionAPIRef) {
		permissionAPI = permissionAPIRef;
	}

	public static boolean existsFolder(String folderInode) {
		DotConnect dc = new DotConnect();
		dc.setSQL("select count(*) as count  from folder where inode = ?");
		dc.addParam(folderInode);
		ArrayList results = dc.getResults();
		if (((String) ((Map) results.get(0)).get("count")).equals("0"))
			return false;
		else
			return true;
	}

	
	public static void delete(Folder f) throws DotDataException{
		
		
		
		
		
		// delete the folder from the tree
		DotConnect db = new DotConnect();
		db.setSQL("delete from tree where child = ? or parent =?");
		db.addParam(f.getInode());
		db.addParam(f.getInode());
		db.loadResult();
		
		HibernateUtil.delete(f);
		FolderCache.removeFolder(f);
	}
	
	
	
	
	/*public static boolean existsFolder(long folderInode) {
		return existsFolder(Long.toString(folderInode));
	}*/
	
	public static Folder getFolderByInode(String folderInode)
	{
		Folder folder = null;
	    Object object = InodeFactory.getInode(folderInode,Folder.class);
	    if(object instanceof Identifier){
	    	folder = null;
	    }else{
	    	folder = (Folder)object;
	    }
		
		return folder;
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<Folder> getFoldersByParent(String pfolderOrHostId) {
		
		DotHibernate dh = new DotHibernate(Folder.class);
		
		String query = "SELECT {folder.*} from folder folder, inode folder_1_, tree tree where tree.parent = ? and tree.child = folder.inode and folder_1_.type = 'folder' and folder_1_.inode = folder.inode order by name, sort_order";
		dh.setSQLQuery(query);
		dh.setParam(pfolderOrHostId);
		
		return (java.util.List<Folder>) dh.list();
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<Folder> getFoldersByParentSortByTitle(String pfolderOrHostId) {
		
		DotHibernate dh = new DotHibernate(Folder.class);
		
		String query = "SELECT {folder.*} from folder folder, inode folder_1_, tree tree where tree.parent = ? and tree.child = folder.inode and folder_1_.type = 'folder' and folder_1_.inode = folder.inode order by folder.title";
		dh.setSQLQuery(query);
		dh.setParam(pfolderOrHostId);
		
		return (java.util.List<Folder>) dh.list();
	}
	
	public static java.util.List getFoldersByParentAndCondition(
			String pfolderOrHostId, String condition) {
		
		DotHibernate dh = new DotHibernate(Folder.class);
		
		dh
		.setSQLQuery("SELECT {folder.*} from folder folder, inode folder_1_, tree tree where tree.parent = ? and tree.child = folder.inode and folder_1_.type = 'folder' and folder_1_.inode = folder.inode and "
				+ condition + " order by sort_order, name");
		dh.setParam(pfolderOrHostId);
		
		return (java.util.List) dh.list();
	}
	
	public static Folder getFolderByPath(String path, Host host) {
		return getFolderByPath(path, host.getIdentifier());
	}
	
	public static Folder getFolderByPath(String path, String hostId) {
		
		// Normal path of execution, condition: the folder has the host attached
		// as parent
		if( path != null && !path.endsWith("/") ) {
			path += "/";
		}
		DotHibernate dh = new DotHibernate(Folder.class);
		dh.setSQLQuery("select {folder.*} from folder, inode folder_1_, tree where path = ? and folder_1_.type = 'folder' and folder.inode = folder_1_.inode and tree.child = folder.inode and tree.parent = ?");
		dh.setParam(path);
		dh.setParam(hostId);
		Folder f = (Folder) dh.load();
		
		if (InodeUtils.isSet(f.getInode())) {
			return f;
		}
		
		// Safe path, condition: the folder don't have the host attached as
		// parent
		dh = new DotHibernate(Folder.class);
		dh.setSQLQuery("select {folder.*} from folder, inode folder_1_ where path = ? and folder_1_.type = 'folder' and folder.inode = folder_1_.inode");
		dh.setParam(path);
		Iterator foldersIt = dh.list().iterator();
		
		while (foldersIt.hasNext()) {
			Folder folder = (Folder) foldersIt.next();
			Host parentHost;
			try {
				User systemUser = APILocator.getUserAPI().getSystemUser();
				parentHost = hostAPI.findParentHost(folder, systemUser, false);
			} catch (DotDataException e) {
				Logger.error(FolderFactory.class, e.getMessage(), e);
				throw new DotRuntimeException(e.getMessage(), e);
			} catch (DotSecurityException e) {
				Logger.error(FolderFactory.class, e.getMessage(), e);
				throw new DotRuntimeException(e.getMessage(), e);
			}
			if (parentHost != null && parentHost.getIdentifier().equalsIgnoreCase(hostId))
				return folder;
		}
		
		return new Folder();
	}
	
	public static List getFolderTree(String hostId, String openNodes,
			String view, String content, String structureInode, Locale locale,
			TimeZone timeZone, Role[] roles, boolean isAdminUser, User user) {
		return getFolderTree(openNodes, view, content, structureInode, locale,
				timeZone, roles, isAdminUser, user);
	}
	
	public static List getFolderTree(String openNodes, String view,
			String content, String structureInode, Locale locale,
			TimeZone timeZone, Role[] roles, boolean isAdminUser, User user) {
		
		List<String> entryList = new ArrayList<String>();
		
		int endIndex = ((openNodes.length() - 1 <= 0) ? 1 : openNodes.length() - 1);
		String openNodesAux = openNodes.substring(1,endIndex);
		String patter = "[|]";
		String[] openNodesArray = openNodesAux.split(patter);
		
		loginDateFormat = java.text.DateFormat.getDateTimeInstance(
				java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, locale);
		loginDateFormat.setTimeZone(timeZone);
		
		List<Host> hosts = null;
		
		try {
			User systemUser = APILocator.getUserAPI().getSystemUser();
			hosts = hostAPI.findAll(systemUser, false);
		} catch (DotDataException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

		Iterator<Host> it = hosts.iterator();
		
		nodeId = 0;
		
		while (it.hasNext()) {
			
			Host host = (Host) it.next();
			
			List<Folder> folders = getFoldersByParent(host.getIdentifier());

			java.util.List permissions = new ArrayList();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(host, roles, user);
			} catch (DotDataException e) {
				Logger.error(FolderFactory.class, "Could not load permissions : ",e);
			}
			
			
			StringBuffer entry = new StringBuffer();
			entry.append((++nodeId) + "|0|");
			if (it.hasNext())
				entry.append("1|");
			else
				entry.append("0|");
			
			// String hostName = host.getHostname().length() > 21 ?
			// host.getHostname().substring(0, 19) + "..." : host.getHostname();
			String hostName = host.getHostname();
			
			entry.append(host.getInode() + "|" + hostName + "|0|0|"
					+ folders.size() + "|");
			if (isOpenNode(openNodesArray, host.getInode()))
			{
				entry.append("1|");
			} else {
				entry.append("0|");
			}
			entry.append(" |0|0|0|0| |");
			
			if (permissions.contains(Integer.valueOf(PERMISSION_READ)))
				entry.append("1|");
			else
				entry.append("0|");
			if (permissions.contains(Integer.valueOf(PERMISSION_WRITE)))
				entry.append("1|");
			else
				entry.append("0|");
			if (permissions.contains(Integer.valueOf(PERMISSION_PUBLISH)))
				entry.append("1|");
			else
				entry.append("0|");
			entry.append(host.getHostname() + ":/" + "|0");
			
			entryList.add(entry.toString());
			
			// decode the view in case it's encoded
			try {
				if (view != null) {
					view = URLDecoder.decode(view, "UTF-8");
				}
			} catch (Exception e) {
			}
			
			if (isOpenNode(openNodesArray, host.getInode())) 
			{
				entryList = createFolderList(folders, entryList, host
						.getInode(), nodeId, openNodes, view, content,
						structureInode, roles, isAdminUser, user);
			}
		}
		
		return entryList;
	}
	
	public static List getEntriesTree(Folder mainFolder, String openNodes,
			String view, String content, String structureInode, Locale locale,
			TimeZone timeZone, Role[] roles, boolean isAdminUser, User user) {
		
		
		Host host = null;
		try {
			User systemUser = APILocator.getUserAPI().getSystemUser();
			host = hostAPI.findParentHost(mainFolder, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

		List<String> entryList = new ArrayList<String>();
		
		loginDateFormat = java.text.DateFormat.getDateTimeInstance(
				java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, locale);
		loginDateFormat.setTimeZone(timeZone);
		
		List<Folder> folders = new ArrayList<Folder>();
		folders.add(mainFolder);
		
		nodeId = 0;
		
		// decode the view in case it's encoded
		try {
			if (view != null) {
				view = URLDecoder.decode(view, "UTF-8");
			}
		} catch (Exception e) {
		}
		
		entryList = createFolderList(folders, entryList, host.getIdentifier(), 0,
				openNodes, view, content, structureInode, roles, isAdminUser, user);
		
		return entryList;
	}
	
	private static List<String> createFolderList(List<Folder> folders,
			List<String> entryList, String parentFolderId, int parentId,
			String openNodes, String view, String content, String structureInode,
			Role[] roles, boolean adminUser, User user) {
		
		Logger.debug(FolderFactory.class, "createFolderList: parentFolderId = "
				+ parentFolderId + ", parentId = " + parentId + "openNodes = "
				+ openNodes + ", view = " + view + ", content = " + content
				+ ", isAdmin = " + adminUser);
		
		java.util.Iterator itr = folders.iterator();
		
		while (itr.hasNext()) {
			boolean readPermission = true;
			Folder folder = (Folder) itr.next();
			
			StringBuffer permsb = new StringBuffer();
			
			java.util.List permissions = new ArrayList();
			try {
				permissions = permissionAPI.getPermissionIdsFromRoles(folder, roles, user);
			} catch (DotDataException e) {
				Logger.error(FolderFactory.class, "Could not load permissions : ",e);
			}
			
			if (permissions.contains(Integer.valueOf(PERMISSION_READ))) {
				// read permission
				permsb.append("1").append("|");
			} else {
				// read permission
				permsb.append("0").append("|");
				
				if (!itr.hasNext()) {
					entryList = replaceEntryNext(entryList, parentId);
				}
				readPermission = false;
			}
			
			if (readPermission) {
				if (permissions.contains(Integer.valueOf(PERMISSION_WRITE))) {
					// write permission
					permsb.append("1").append("|");
				} else {
					// write permission
					permsb.append("0").append("|");
				}
				
				if (permissions.contains(Integer.valueOf(PERMISSION_PUBLISH))) {
					// publish permission
					permsb.append("1");
				} else {
					// publish permission
					permsb.append("0");
				}
				
				String permStr = permsb.toString();
				
				StringBuffer sb = new StringBuffer();
				
				sb.append(++nodeId).append("|");
				sb.append(parentId).append("|");
				
				if (!itr.hasNext()) {
					sb.append("1");
				} else {
					sb.append("0");
				}
				
				sb.append("|");
				sb.append(folder.getInode()).append("|");
				
				sb.append(UtilMethods.javaScriptify(folder.getName())).append(
				"|");
				
				// folder type == 9
				sb.append("9").append("|");
				sb.append(parentFolderId).append("|");
				
				// gets folder children for this folder
				folders = getFoldersByParent(folder.getInode());
				
				List files = null;
				List htmlpages = null;
				List links = null;
				
				int size = folders.size();
				
				if (content.indexOf("files") != -1) {
					files = FileFactory
					.getFileChildrenByCondition(folder, view);
					size += files.size();
				}
				
				if (content.indexOf("htmlpages") != -1) {
					htmlpages = HTMLPageFactory.getHTMLPageChildrenByCondition(
							folder, view);
					size += htmlpages.size();
				}
				
				if (content.indexOf("links") != -1) {
					links = LinkFactory
					.getLinkChildrenByCondition(folder, view);
					size += links.size();
				}
				
				sb.append(size).append("|");
				
				String folderPath = UtilMethods.javaScriptify(folder.getPath());
				String showOnMenu = (folder.isShowOnMenu()) ? "1" : "0";
				
				if (openNodes.indexOf(folder.getInode()) != -1) {
					
					Logger.debug(FolderFactory.class, "opened node: "
							+ folder.getInode());
					
					// it's going to be open
					sb.append("1").append("|");
					
					// misc text
					sb.append(" ").append("|");
					
					// live
					sb.append("0").append("|");
					
					// working
					sb.append("0").append("|");
					
					// deleted
					sb.append("0").append("|");
					
					// locked
					sb.append("0").append("|");
					
					// mod_date
					sb.append(" ").append("|");
					
					// permissions
					sb.append(permStr).append("|");
					
					// folder path
					sb.append(folderPath).append("|");
					
					// show on menu
					sb.append(showOnMenu);
					
					entryList.add(sb.toString());
					
					// adds the files from this folder to the entry list
					int childParentId = nodeId;
					
					// adds the files children to the entry list
					entryList = createFolderList(folders, entryList, folder
							.getInode(), childParentId, openNodes, view,
							content, structureInode, roles, adminUser, user);
					
					int versionCount = 0;
					
					if (content.indexOf("files") != -1) {
						entryList = createEntryList(entryList, files, folder
								.getInode(), childParentId, openNodes, roles,
								"11", 1, folderPath, versionCount, user);
					}
					
					// add one for each content type in refresh view from
					// view_folders.jsp
					if (content.indexOf("htmlpages") != -1) {
						entryList = createEntryList(entryList, htmlpages,
								folder.getInode(), childParentId, openNodes,
								roles, "15", 1, folderPath, versionCount, user);
					}
					
					// add one for each content type in refresh view from
					// view_folders.jsp
					if (content.indexOf("links") != -1) {
						entryList = createEntryList(entryList, links, folder
								.getInode(), childParentId, openNodes, roles,
								"16", 1, folderPath, versionCount, user);
					}
					
					if (content.equals("allcontent")) {
						// files
						files = FileFactory.getFileChildrenByCondition(folder,
								view);
						entryList = createEntryList(entryList, files, folder
								.getInode(), childParentId, openNodes, roles,
								"11", 1, folderPath, versionCount, user);
						
						// htmlpages
						htmlpages = HTMLPageFactory
						.getHTMLPageChildrenByCondition(folder, view);
						entryList = createEntryList(entryList, htmlpages,
								folder.getInode(), childParentId, openNodes,
								roles, "15", 1, folderPath, versionCount, user);
						
						// links
						links = LinkFactory.getLinkChildrenByCondition(folder,
								view);
						entryList = createEntryList(entryList, links, folder
								.getInode(), childParentId, openNodes, roles,
								"16", 1, folderPath, versionCount, user);
					}
				} else {
					// it's going to be closed
					sb.append("0").append("|");
					
					// misc text
					sb.append(" ").append("|");
					
					// live
					sb.append("0").append("|");
					
					// working
					sb.append("0").append("|");
					
					// deleted
					sb.append("0").append("|");
					
					// locked
					sb.append("0").append("|");
					
					// mod_date
					sb.append(" ").append("|");
					
					// permissions
					sb.append(permStr).append("|");
					
					// folder path
					sb.append(folderPath).append("|");
					
					// show on menu
					sb.append(showOnMenu);
					
					entryList.add(sb.toString());
				}
			}
		}
		
		return entryList;
	}
	
	private static List<String> createEntryList(List<String> entryList,
			List entries, String parentFolderId, int parentId, String openNodes,
			Role[] roles, String entryType, long childrenSize,
			String folderPath, int versionCount, User user) {

		// go across the entries to know if there is a live or working version
		java.util.Iterator itr = entries.iterator();
		while (itr.hasNext()) {
			WebAsset webasset = (WebAsset) itr.next();
			if (webasset.isLive()) {
				versionCount--;
			}
		}
		
		itr = entries.iterator();
		while (itr.hasNext()) {
			WebAsset webasset = (WebAsset) itr.next();
			
			// Getting permissions
			boolean readPermission = true;
			
			StringBuffer permsb = new StringBuffer();
			
			java.util.List permissionsList = new ArrayList();
			try {
				permissionsList = permissionAPI.getPermissionIdsFromRoles(webasset, roles, user);
			} catch (DotDataException e) {
				Logger.error(FolderFactory.class, "Could not load permissions : ",e);
			}
			
			if (permissionsList.contains(Integer.valueOf(PERMISSION_READ))) {
				// read permission
				permsb.append("1").append("|");
				readPermission = true;
			} else {
				// read permission
				permsb.append("0").append("|");
				readPermission = false;
			}
			
			if (readPermission) {
				if (permissionsList.contains(Integer.valueOf(PERMISSION_WRITE))) {
					// write permission
					permsb.append("1").append("|");
				} else {
					// write permission
					permsb.append("0").append("|");
				}
				
				if (permissionsList.contains(Integer.valueOf(PERMISSION_PUBLISH))) {
					// publish permission
					permsb.append("1");
				} else {
					// publish permission
					permsb.append("0");
				}
				
				String permissions = permsb.toString();
				
				String webassetVersion = "";
				if (!webasset.isWorking() && !webasset.isLive()
						&& versionCount > 0) {
					webassetVersion = " (version " + versionCount + ") ";
					versionCount--;
				} else {
					// versionCount = 0;
				}
				
				StringBuffer sb = new StringBuffer();
				sb.append(++nodeId).append("|");
				sb.append(parentId).append("|");
				
				if (!itr.hasNext()) {
					sb.append("1");
				} else {
					sb.append("0");
				}
				
				sb.append("|");
				sb.append(webasset.getInode()).append("|");
				
				// it's a file
				if (webasset instanceof com.dotmarketing.portlets.files.model.File) {
					com.dotmarketing.portlets.files.model.File file = (com.dotmarketing.portlets.files.model.File) webasset;
						sb.append(UtilMethods.javaScriptify(file.getFileName()) + webassetVersion).append("|");
	                
				}
				// it's an html page
				else if (webasset instanceof com.dotmarketing.portlets.htmlpages.model.HTMLPage) {
					com.dotmarketing.portlets.htmlpages.model.HTMLPage htmlpage = (com.dotmarketing.portlets.htmlpages.model.HTMLPage) webasset;
						sb.append(UtilMethods.javaScriptify(htmlpage.getPageUrl()) + webassetVersion).append("|");
	                
				} else if (webasset instanceof com.dotmarketing.portlets.links.model.Link) {
					com.dotmarketing.portlets.links.model.Link link = (com.dotmarketing.portlets.links.model.Link) webasset;
					if (link.getUrl() != null) {
						String workingUrl = "";
						// if the link is internal we dont show the host name
						if (link.isInternal()) {
							workingUrl = link.getUrl();
							int idx = workingUrl.indexOf("/");
							if (idx > 0) {
								workingUrl = workingUrl.substring(idx,
										workingUrl.length());
							}
							workingUrl = UtilMethods.javaScriptify(workingUrl);
						} else {
							// if it's external we show the complete url
							workingUrl = UtilMethods.javaScriptify(link
									.getProtocal()
									+ link.getUrl());
						}
						
							sb.append(workingUrl + webassetVersion).append("|");
						
					} else {
						sb.append(link.getTitle()).append("|");
					}
				} else {
					if (webasset.getTitle().length() < 30) {
						sb.append(
								UtilMethods.javaScriptify(webasset.getTitle())
								+ webassetVersion).append("|");
					} else {
						sb.append(
								UtilMethods.javaScriptify(webasset.getTitle()
										.substring(0, 30))
										+ webassetVersion).append("...|");
					}
				}
				
				// entry type (files=11,containers=12)
				sb.append(entryType).append("|");
				sb.append(parentFolderId).append("|");
				
				sb.append(childrenSize).append("|");
				
				String showOnMenu = (webasset.isShowOnMenu()) ? "1" : "0";
				
				if (openNodes.indexOf(webasset.getInode()) != -1) {
					// gets versions and live for this working asset
					entries = WebAssetFactory.getAssetVersionsandLive(webasset);
					
					// it's going to be open
					sb.append("1").append("|");
					
					// if its a file append title instead of friendly name
					// friendly name is used by portfolio to add description to
					// images.
					if (webasset instanceof com.dotmarketing.portlets.files.model.File) {
						if (webasset.getTitle().length() < 30) {
							sb.append(
									UtilMethods.javaScriptify(webasset
											.getTitle())
											+ webassetVersion).append("|");
						} else {
							sb.append(
									UtilMethods.javaScriptify(webasset
											.getTitle().substring(0, 30))
											+ webassetVersion).append("...|");
						}
					} else {
						// friendly name
						if (webasset.getFriendlyName().length() < 30) {
							sb.append(
									UtilMethods.javaScriptify(webasset
											.getFriendlyName())
											+ webassetVersion).append("|");
						} else {
							sb.append(
									UtilMethods
									.javaScriptify(webasset
											.getFriendlyName()
											.substring(0, 30))
											+ webassetVersion).append("...|");
						}
					}
					
					// live
					sb.append((webasset.isLive()) ? "1" : "0").append("|");
					
					// working
					sb.append((webasset.isWorking()) ? "1" : "0").append("|");
					
					// deleted
					sb.append((webasset.isDeleted()) ? "1" : "0").append("|");
					
					// locked
					sb.append((webasset.isLocked()) ? "1" : "0").append("|");
					
					// mod_date
					sb.append(loginDateFormat.format(webasset.getModDate()))
					.append("|");
					
					// permissions
					sb.append(permissions).append("|");
					
					// folder path
					sb.append(folderPath).append("|");
					
					// show on menu
					sb.append(showOnMenu);
					
					entryList.add(sb.toString());
					
					// adds the files from this folder to the entry list
					int childParentId = nodeId;
					
					entryList = createEntryList(entryList, entries, webasset
							.getInode(), childParentId, openNodes, roles,
							entryType, 0, folderPath, entries.size(), user);
				} else {
					// it's going to be closed
					sb.append("0").append("|");
					
					// if its a file append title instead of friendly name
					// friendly name is used by portfolio to add description to
					// images.
					if (webasset instanceof com.dotmarketing.portlets.files.model.File) {
						if (webasset.getTitle().length() < 30) {
							sb.append(
									UtilMethods.javaScriptify(webasset
											.getTitle())
											+ webassetVersion).append("|");
						} else {
							sb.append(
									UtilMethods.javaScriptify(webasset
											.getTitle().substring(0, 30))
											+ webassetVersion).append("...|");
						}
					} else {
						String friendlyName = UtilMethods.isSet(webasset
								.getFriendlyName()) ? webasset
										.getFriendlyName() : "";
										// friendly name
										if (friendlyName.length() < 30) {
											sb.append(
													UtilMethods.javaScriptify(friendlyName)
													+ webassetVersion).append("|");
										} else {
											sb.append(
													UtilMethods.javaScriptify(friendlyName
															.substring(0, 30))
															+ webassetVersion).append("...|");
										}
					}
					
					// live
					sb.append((webasset.isLive()) ? "1" : "0").append("|");
					
					// working
					sb.append((webasset.isWorking()) ? "1" : "0").append("|");
					
					// deleted
					sb.append((webasset.isDeleted()) ? "1" : "0").append("|");
					
					// locked
					sb.append((webasset.isLocked()) ? "1" : "0").append("|");
					
					// mod_date
					sb.append(loginDateFormat.format(webasset.getModDate()))
					.append("|");
					
					// permissions
					sb.append(permissions).append("|");
					
					// folder path
					sb.append(folderPath).append("|");
					
					// show on menu
					sb.append(showOnMenu);
					
					entryList.add(sb.toString());
				}
			}
		}
		
		return entryList;
	}
	
	public static List<Folder> getFoldersByParent(Folder folder, User user, boolean respectFrontendRoles) throws DotDataException {
		List<Folder> entries = new ArrayList<Folder>();
		List<Folder> elements = getFoldersByParentSortByTitle(folder.getInode());
		for(Folder childFolder : elements){
			if(permissionAPI.doesUserHavePermission(childFolder, PERMISSION_READ, user, respectFrontendRoles)){
				entries.add(childFolder);
			}
		}
		return entries;
	}
	
	private static List<String> replaceEntryNext(List<String> entryList,
			int parentId) {
		for (int k = entryList.size() - 1; k >= 0; k--) {
			String entry = (String) entryList.get(k);
			String[] entryArray = entry.split("\\|");
			
			if (entryArray[1].equals(String.valueOf(parentId))) {
				// found the closest sibling
				entryArray[2] = "1";
				entry = UtilMethods.join(entryArray, "|");
				entry = entry.substring(0, entry.length() - 1);
				entryList.set(k, entry);
				
				return entryList;
			}
		}
		
		return entryList;
	}
	
	
	@SuppressWarnings("unchecked")
	public static java.util.List getMenuItems(Folder folder) {
		return getMenuItems(folder,1);
	}

	public static java.util.List getMenuItems(Host host) {
		return getMenuItems(host,1);
	}
	
	public static java.util.List getMenuItems(Folder folder,int orderDirection) {
		List<Folder> folders = new ArrayList<Folder>();
		folders.add(folder);
		return getMenuItems(folders, orderDirection);
	}
	
	public static java.util.List getMenuItems(Host host,int orderDirection) {
		List<Folder> subFolders = APILocator.getFolderAPI().findSubFolders(host);
		return getMenuItems(subFolders, orderDirection);
	}
	
	private static List getMenuItems(List<Folder> folders,int orderDirection) {
		
		List menuList = new ArrayList();
		
		for (Folder folder : folders) {
			String condition = "show_on_menu="
				+ com.dotmarketing.db.DbConnectionFactory.getDBTrue();
			
			// gets all subfolders
			List subFolders = InodeFactory
			.getChildrenClassByCondition(folder, Folder.class,
					condition);
			
			condition += (" and deleted =" + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + " and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
			
			// gets all links for this folder
			List linksListSubChildren = InodeFactory
			.getChildrenClassByCondition(folder, Link.class,
					condition);
			
			// gets all html pages for this folder
			List htmlPagesSubListChildren = InodeFactory
			.getChildrenClassByCondition(folder,
					HTMLPage.class, condition);
			
			// gets all files for this folder
			List filesListSubChildren = InodeFactory
			.getChildrenClassByCondition(folder, File.class,
					condition);
			
			// gets all subitems
			menuList.addAll(subFolders);
			menuList.addAll(linksListSubChildren);
			menuList.addAll(htmlPagesSubListChildren);
			menuList.addAll(filesListSubChildren);
			
			Comparator comparator = new AssetsComparator(orderDirection);
			java.util.Collections.sort(menuList, comparator);
		}
		
		return menuList;
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List getAllMenuItems(Inode inode) {
		return getAllMenuItems(inode,1);
	}
	
	public static java.util.List getAllMenuItems(Inode inode,int orderDirection) {
		/*String condition = "show_on_menu="
			+ com.dotmarketing.db.DbConnectionFactory.getDBTrue();*/
		
		
		String condition = "";
		
		
		// gets all subfolders
		java.util.List folderListChildren = InodeFactory
		.getChildrenClass(inode, Folder.class);
		
		condition += ("show_on_menu=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted =" + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + " and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
		
		// gets all links for this folder
		java.util.List linksListSubChildren = InodeFactory
		.getChildrenClassByCondition(inode, Link.class,
				condition);
		
		// gets all html pages for this folder
		java.util.List htmlPagesSubListChildren = InodeFactory
		.getChildrenClassByCondition(inode,
				HTMLPage.class, condition);
		
		// gets all files for this folder
		java.util.List filesListSubChildren = InodeFactory
		.getChildrenClassByCondition(inode, File.class,
				condition);
		
		// gets all subitems
		java.util.List menuList = new java.util.ArrayList();
		menuList.addAll(folderListChildren);
		menuList.addAll(linksListSubChildren);
		menuList.addAll(htmlPagesSubListChildren);
		menuList.addAll(filesListSubChildren);
		
		Comparator comparator = new AssetsComparator(orderDirection);
		java.util.Collections.sort(menuList, comparator);
		
		return menuList;
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List getFolderItems(Folder parentFolder,
			String condition) {
		condition += (" and deleted =" + com.dotmarketing.db.DbConnectionFactory.getDBFalse());
		
		// gets all links for this folder
		java.util.List linksListSubChildren = InodeFactory
		.getChildrenClassByCondition(parentFolder, Link.class,
				condition);
		
		// gets all html pages for this folder
		java.util.List htmlPagesSubListChildren = InodeFactory
		.getChildrenClassByCondition(parentFolder, HTMLPage.class,
				condition);
		
		// gets all files for this folder
		java.util.List filesListSubChildren = InodeFactory
		.getChildrenClassByCondition(parentFolder, File.class,
				condition);
		
		// gets all subitems
		java.util.List menuList = new java.util.ArrayList();
		menuList.addAll(linksListSubChildren);
		menuList.addAll(htmlPagesSubListChildren);
		menuList.addAll(filesListSubChildren);
		
		Comparator comparator = new AssetsComparator();
		java.util.Collections.sort(menuList, comparator);
		
		return menuList;
	}
	
	public static Folder createFolders(String path, String hostId,
			ActionRequest req) throws DotDataException {
		try {
			HttpServletRequest request = ((ActionRequestImpl)req).getHttpServletRequest();
			User user = userWebAPI.getLoggedInUser(request);
			boolean respectFrontend = !userWebAPI.isLoggedToBackend(request);
			Host host = hostAPI.find(hostId, user, respectFrontend);
			return createFolders(path, host, req);
		} catch (PortalException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotDataException(e.getMessage(), e);
			
		} catch (SystemException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotDataException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotDataException(e.getMessage(), e);
		}
	}
	
	public static Folder createFolders(String path, Host host, ActionRequest req) throws DotDataException {
		return createFolders (path, host);
	}
	
	public static Folder createFolders(String path, Host host) throws DotDataException {
				
		StringTokenizer st = new StringTokenizer(path, "/");
		StringBuffer sb = new StringBuffer("/");
		
		Folder parent = null;

		while (st.hasMoreTokens()) {
			String name = st.nextToken();
			sb.append(name + "/");
			Folder f = getFolderByPath(sb.toString(), host);
			if (!InodeUtils.isSet(f.getInode())) {
				f.setName(name);
				f.setTitle(name);
				f.setPath(sb.toString());
				f.setShowOnMenu(false);
				f.setSortOrder(0);
				f.setHostId(host.getIdentifier());
				InodeFactory.saveInode(f);
			}
			if (parent == null) {
				Tree tree = TreeFactory.getTree(host.getIdentifier(),  f.getInode());
				if(!InodeUtils.isSet(tree.getParent())){
					tree = new Tree(host.getIdentifier(), f.getInode());
					TreeFactory.saveTree(tree);
					try {
						hostAPI.save(host, APILocator.getUserAPI().getSystemUser(), false);
					} catch (DotSecurityException e) {
						Logger.error(FolderFactory.class, e.getMessage(), e);
						throw new DotRuntimeException(e.getMessage(), e);
					}
				}

			} else {
				parent.addChild(f);
				InodeFactory.saveInode(parent);
			}

			parent = f;

		}
		return parent;

	}

	@SuppressWarnings("unchecked")
	private static void copyFolder(Folder folder, Host destination,
			Hashtable copiedObjects) throws DotDataException {
		
		boolean rename =  hostAPI.doesHostContainsFolder((Host) destination, folder.getName());
		
		Folder newFolder = new Folder();
		newFolder.copy(folder);
		newFolder.setName(folder.getName());
		while(rename) {
			newFolder.setName(newFolder.getName() + "_copy");
			rename = hostAPI.doesHostContainsFolder((Host) destination, newFolder.getName());
		}
		
		
		newFolder.setPath("/" + newFolder.getName() + "/");
		newFolder.setHostId(((Host) destination).getIdentifier());
		InodeFactory.saveInode(newFolder);
		TreeFactory.saveTree(new Tree(destination.getIdentifier(), newFolder.getInode()));
		
		saveCopiedFolder(folder, newFolder, copiedObjects);
	}
	
	@SuppressWarnings("unchecked")
	private static void copyFolder(Folder folder, Folder destination,
			Hashtable copiedObjects) throws DotDataException {
		
		boolean rename = FolderFactory.folderContains(folder.getName(),	(Folder) destination);
		
		Folder newFolder = new Folder();
		newFolder.copy(folder);
		newFolder.setName(folder.getName());
		while(rename) {
			newFolder.setName(newFolder.getName() + "_copy");
			rename = FolderFactory.folderContains(newFolder.getName(), (Folder) destination);
		}
		
		
		newFolder.setPath(((Folder) destination).getPath() + newFolder.getName() + "/");
		newFolder.setHostId(((Folder) destination).getHostId());
		InodeFactory.saveInode(newFolder);
		TreeFactory.saveTree(new Tree(destination.getInode(), newFolder.getInode()));
		
		saveCopiedFolder(folder, newFolder, copiedObjects);
	}
	
	private static void saveCopiedFolder(Folder source, Folder newFolder,
			Hashtable copiedObjects) throws DotDataException {
		
		if (copiedObjects == null)
			copiedObjects = new Hashtable();

		// Copying folder permissions
		permissionAPI.copyPermissions(source, newFolder);
		
		// Copying children html pages
		Map<String, HTMLPage[]> pagesCopied;
		if (copiedObjects.get("HTMLPages") == null) {
			pagesCopied = new HashMap<String, HTMLPage[]>();
			copiedObjects.put("HTMLPages", pagesCopied);
		} else {
			pagesCopied = (Map<String, HTMLPage[]>) copiedObjects
			.get("HTMLPages");
		}
		
		List<HTMLPage> pages = InodeFactory.getChildrenClass(source,
				HTMLPage.class);
		for (HTMLPage page : pages) {
			if (page.isWorking()) {
				HTMLPage newPage = HTMLPageFactory
				.copyHTMLPage(page, newFolder);
				// Saving copied pages to update template - pages relationships
				// later
				pagesCopied.put(page.getInode(),
						new HTMLPage[] { page, newPage });
			}
		}
		
		// Copying Files
		Map<String, File[]> filesCopied;
		if (copiedObjects.get("Files") == null) {
			filesCopied = new HashMap<String, File[]>();
			copiedObjects.put("Files", filesCopied);
		} else {
			filesCopied = (Map<String, File[]>) copiedObjects.get("Files");
		}
		
		List<File> files = InodeFactory.getChildrenClass(source, File.class);
		for (File file : files) {
			if (file.isWorking()) {
				File newFile = FileFactory.copyFile(file, newFolder);
				// Saving copied pages to update template - pages relationships
				// later
				filesCopied.put(file.getInode(), new File[] { file, newFile });
			}
		}
		
		// Copying links
		Map<String, Link[]> linksCopied;
		if (copiedObjects.get("Links") == null) {
			linksCopied = new HashMap<String, Link[]>();
			copiedObjects.put("Links", linksCopied);
		} else {
			linksCopied = (Map<String, Link[]>) copiedObjects.get("Links");
		}
		
		List<Link> links = InodeFactory.getChildrenClass(source, Link.class);
		for (Link link : links) {
			if (link.isWorking()) {
				Link newLink = LinkFactory.copyLink(link, newFolder);
				// Saving copied pages to update template - pages relationships
				// later
				linksCopied.put(link.getInode(), new Link[] { link, newLink });
			}
		}
		
		// Copying Inner Folders
		List<Folder> childrenFolder = InodeFactory.getChildrenClass(source,
				Folder.class);
		for (Folder childFolder : childrenFolder) {
			copyFolder(childFolder, newFolder, copiedObjects);
		}
		
	}
	
	public static void copyFolder(Folder folder, Host destination) throws DotDataException {
		copyFolder(folder, destination, null);
	}
	
	public static void copyFolder(Folder folder, Folder destination) throws DotDataException {
		copyFolder(folder, destination, null);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean folderContains(String name, Folder destination) {
		List<Folder> children = InodeFactory.getChildrenClass(destination,
				Folder.class);
		for (Folder folder : children) {
			if (folder.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static boolean moveFolder(Folder folder, Object destination) throws DotDataException {
		
		User systemUser = APILocator.getUserAPI().getSystemUser();
		boolean contains = false;
		if (destination instanceof Folder) {
			contains = FolderFactory.folderContains(folder.getName(), (Folder) destination);
		} else {
			contains = hostAPI.doesHostContainsFolder((Host) destination, folder.getName());
		}
		if (contains) return false;
		
		//removing from current parent
		Folder currentParentFolder = (Folder) InodeFactory.getParentOfClass(folder, Folder.class);
		Host currentParentHost = new Host();
		if (InodeUtils.isSet(currentParentFolder.getInode())) 
		{
			TreeFactory.deleteTree(TreeFactory.getTree(currentParentFolder.getInode(), folder.getInode()));
		} 
		else 
		{
			try {
				currentParentHost = hostAPI.findParentHost(folder, systemUser, false);
			} catch (DotSecurityException e) {
				Logger.error(FolderFactory.class, e.getMessage(), e);
				throw new DotRuntimeException(e.getMessage(), e);
			} 
			TreeFactory.deleteTree(TreeFactory.getTree(currentParentHost.getIdentifier(), folder.getInode()));
		}
		if(destination instanceof Host) 
			TreeFactory.saveTree(new Tree(((Host)destination).getIdentifier(), folder.getInode()));
		else
			TreeFactory.saveTree(new Tree(((Folder)destination).getInode(), folder.getInode()));
		
		//Setting the new host inode and folder path
		if (destination instanceof Host) 
		{
			folder.setPath("/" + folder.getName() + "/");
			folder.setHostId(((Host)destination).getIdentifier());
		} 
		else if (destination instanceof Folder) 
		{
			folder.setPath(((Folder) destination).getPath() + folder.getName() + "/");
			folder.setHostId(((Folder)destination).getHostId());
		}
		InodeFactory.saveInode(folder);
		
		updateMovedFolderAssets (folder);		
		//### RECURSIVE CALL ###
		moveRecursiveFolders(folder);
		//### END RECURSIVE CALL ###
		
        if (folder.isShowOnMenu()) 
        {
            //existing folder with different show on menu ... need to regenerate menu
            //RefreshMenus.deleteMenus();
        	if (destination instanceof Folder)
        	{
        		RefreshMenus.deleteMenu((Folder) destination);
    		}
        	else if (destination instanceof Host)
        	{
        		RefreshMenus.deleteMenu((Host) destination);
    		}
    		
    		//removing from current parent    		
        	if (InodeUtils.isSet(currentParentFolder.getInode()))
        	{
        		RefreshMenus.deleteMenu((Folder) currentParentFolder);
    		}
        	else if (currentParentHost!= null && InodeUtils.isSet(currentParentHost.getInode()))
        	{
        		RefreshMenus.deleteMenu((Host) currentParentHost);
    		}
        }

		return true;
	}
	
	/***
	 * This methos update recursively the inner folders of the specified folder
	 * @param folder
	 */
	@SuppressWarnings("unchecked")
	private static void moveRecursiveFolders(Folder folder)
	{		
		Stack<Folder> innerFolders = new Stack<Folder>();
		innerFolders.addAll((List<Folder>)InodeFactory.getChildrenClass(folder, Folder.class));
		
		while (!innerFolders.empty()) {
			Folder nextFolder = innerFolders.pop();
			
			nextFolder.setPath(folder.getPath() + nextFolder.getName() + "/");
			nextFolder.setHostId(folder.getHostId());
			InodeFactory.saveInode(nextFolder);
			
			updateMovedFolderAssets (nextFolder);
			moveRecursiveFolders(nextFolder);
		}		
	}
	
	/**
	 * this method updates the asset info for the new paths
	 * @param theFolder the folder moved
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void updateMovedFolderAssets (Folder theFolder) {
		
		User systemUser;
		Host newHost;
		try {
			systemUser = APILocator.getUserAPI().getSystemUser();
			newHost = hostAPI.findParentHost(theFolder, systemUser, false);
		} catch (DotDataException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(FolderFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

		List<HTMLPage> htmlPages = InodeFactory.getChildrenClass(theFolder, HTMLPage.class);
		for (HTMLPage page: htmlPages) {
			Identifier identifier = IdentifierFactory.getIdentifierByInode(page);
			
			if (page.isWorking()) {
				//updating caches
				WorkingCache.removeAssetFromCache(page);
				IdentifierCache.removeAssetFromIdCache(page);
			}
			
			if (page.isLive()) {
				LiveCache.removeAssetFromCache(page);
			}
			
			if (page.isWorking()) {
				//gets identifier for this webasset and changes the uri and
				// persists it
				identifier.setHostId(newHost.getIdentifier());
				identifier.setURI(page.getURI(theFolder));
				InodeFactory.saveInode(identifier);
			}
			
			//Add to Preview and Live Cache
			if (page.isLive()) {
				LiveCache.removeAssetFromCache(page);
				LiveCache.addToLiveAssetToCache(page);
			}
			if (page.isWorking()) {
				WorkingCache.removeAssetFromCache(page);
				WorkingCache.addToWorkingAssetToCache(page);
				IdentifierCache.removeFromIdCacheByInode(page);
				IdentifierCache.addVersionableToIdentifierCache(page);
			}
			
			//republishes the page to reset the VTL_SERVLETURI variable
			if (page.isLive()) {
				PageServices.invalidate(page);
			}
			
		}
		
		List<File> files = InodeFactory.getChildrenClass(theFolder, File.class);
		for (File file: files) {
			Identifier identifier = IdentifierFactory.getIdentifierByInode(file);
			
			//assets cache
			if (file.isLive())
				LiveCache.removeAssetFromCache(file);
			
			if (file.isWorking())
				WorkingCache.removeAssetFromCache(file);
			
			if (file.isWorking()) {
				//gets identifier for this webasset and changes the uri and
				// persists it
				identifier.setHostId(newHost.getIdentifier());
				identifier.setURI(file.getURI(theFolder));
				InodeFactory.saveInode(identifier);
			}
			
			//Add to Preview and Live Cache
			if (file.isLive()) {
				LiveCache.addToLiveAssetToCache(file);
			}
			if (file.isWorking())
				WorkingCache.addToWorkingAssetToCache(file);
			
		}
		
		List<Link> links = InodeFactory.getChildrenClass(theFolder, Link.class);
		for (Link link: links) {
			if (link.isWorking()) {
				
				Identifier identifier = IdentifierFactory.getIdentifierByInode(link);
				
				// gets identifier for this webasset and changes the uri and
				// persists it
				identifier.setHostId(newHost.getIdentifier());
				identifier.setURI(link.getURI(theFolder));
				InodeFactory.saveInode(identifier);
			}
			
		}
		IdentifierCache.clearCache();
	}
	
	public static boolean moveFolder(Folder folder, Folder destination) throws DotDataException {
		return moveFolder(folder, (Object)destination);
	}
	
	public static boolean moveFolder(Folder folder, Host destination) throws DotDataException {
		return moveFolder(folder, (Object)destination);
	}
	
	/**
	 * Checks if folder1 is child of folder2
	 * @param folder1
	 * @param folder2
	 * @return
	 */
	public static boolean isChildFolder(Folder folder1, Folder folder2) {
		Folder parentFolder = (Folder) InodeFactory.getParentOfClass(folder1, Folder.class);
		if (!InodeUtils.isSet(parentFolder.getInode()))
			return false;
		else  {
			if (parentFolder.getInode().equalsIgnoreCase(folder2.getInode())) {
				return true;
			}
			return isChildFolder (parentFolder, folder2);
		}
	}
	
	public static boolean renameFolder(String folderInode,String newName)
	{
		Folder folder = getFolderByInode(folderInode);
		return renameFolder(folder,newName);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean renameFolder(Folder folder,String newName)
	{
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		FolderAPI folderAPI = APILocator.getFolderAPI();
		try
		{
			List<Folder> children = null;
			Folder parent = (Folder) InodeFactory.getParentOfClass(folder, Folder.class);
			if (InodeUtils.isSet(parent.getInode ())) {
				children = InodeFactory.getChildrenClass(parent, Folder.class);
			} else {
				Host h = hostAPI.findParentHost(folder, APILocator.getUserAPI().getSystemUser(), false); 
				children = folderAPI.findSubFolders(h);
			}
			
			for (Folder child : children) {
				if (!child.getInode().equalsIgnoreCase(folder.getInode()) && child.getName().equals(newName))
					return false;
			}
			
			String originalName = folder.getName();
			int folderLevel = folderLevel(folder);
			//Rename the folder
			folder.setName(newName);
			String opath = folder.getPath();
			if(opath.endsWith("/")){
				opath = opath.substring(0, opath.length()-1);
			}
			opath = opath.substring(0,opath.lastIndexOf("/"));
			folder.setPath(opath + "/" + newName + "/");
			InodeFactory.saveInode(folder);
			
			//Change the URI of the internal pages, files and links
			renameInternalFiles(folder,originalName,newName,folderLevel);
			
			//Clean the caches
			IdentifierCache.clearCache();
			PermissionCache perCache = CacheLocator.getPermissionCache();
			perAPI.clearCache();
			
			//return successful
			return true;
		}
		catch(Exception ex)
		{
			Logger.error(FolderFactory.class,ex.getMessage(), ex);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void renameInternalFiles(Folder folder,String originalName,String newName,int folderLevel)
	{
		changePathByFolder(folder,originalName,newName,folderLevel);
		
		//Get the HTMLPages
		List<HTMLPage> pages = InodeFactory.getChildrenClass(folder,HTMLPage.class);
		for(HTMLPage page : pages)
		{
			changeURIByAsset(page,originalName,newName,folderLevel);
		}
		
		//Get the Files
		List<File> files = InodeFactory.getChildrenClass(folder,File.class);
		for(File file : files)
		{
			changeURIByAsset(file,originalName,newName,folderLevel);
		}
		
		//Get the Links
		List<Link> links = InodeFactory.getChildrenClass(folder,Link.class);
		for(Link link : links)
		{
			changeURIByAsset(link,originalName,newName,folderLevel);
		}
		
		//Get the Folders for recursion
		List<Folder> folders = InodeFactory.getChildrenClass(folder,Folder.class);
		for(Folder internalFolder : folders)
		{
			renameInternalFiles(internalFolder,originalName,newName,folderLevel);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void changeURIByAsset(Inode inode,String originalName,String newName,int folderLevel)
	{
		Identifier identifier = IdentifierFactory.getIdentifierByInode(inode);
		String URI = identifier.getURI();
		URI = renameByFolderLevel(URI,originalName,newName,folderLevel);
		identifier.setURI(URI);
		InodeFactory.saveInode(identifier);
	}
	
	private static void changePathByFolder(Folder folder,String originalName,String newName,int folderLevel)
	{
		String path = folder.getPath();
		path = renameByFolderLevel(path,originalName,newName,folderLevel);
		folder.setPath(path);
		InodeFactory.saveInode(folder);
	}
	
	private static int folderLevel(Folder folder)
	{
		int folderLevel = 1;
		Folder parentFolder = (Folder) InodeFactory.getParentOfClass(folder,Folder.class);
		while (InodeUtils.isSet(parentFolder.getInode()))
		{
			folderLevel++;
			parentFolder = (Folder) InodeFactory.getParentOfClass(parentFolder,Folder.class);
		}
		return folderLevel;
	}
	
	private static String renameByFolderLevel(String path,String originalName,String newName,int folderLevel)
	{
		String URIInitial;
		String URIReplace;
		String URIFinal;
		
		int initialCharacter = -1;
		int finalCharacter = -1;
		for(int i = 1;i <= folderLevel;i++)
		{
			initialCharacter = path.indexOf("/",initialCharacter + 1);
		}
		finalCharacter = path.indexOf("/",initialCharacter + 1);
		
		//Split the URI
		URIInitial = path.substring(0,initialCharacter + 1);
		URIReplace = path.substring(initialCharacter + 1,finalCharacter);
		URIFinal = path.substring(finalCharacter);
		
		URIReplace = URIReplace.replace(originalName,newName);
		return URIInitial + URIReplace + URIFinal;
	}
	
	public static boolean matchFilter(Folder folder,String fileName)
	{
		//return value
		Perl5Matcher p5m = new Perl5Matcher();
		Perl5Compiler p5c = new Perl5Compiler();
		boolean match = false;
		try
		{
			//Obtain the filters
			String filesMasks  = folder.getFilesMasks();
			filesMasks = (filesMasks != null ? filesMasks.trim() : filesMasks);
			
			if (UtilMethods.isSet(filesMasks))
			{
				String[] filesMasksArray = filesMasks.split(",");
				int length = filesMasksArray.length;
				
				//Try to match de filters
				for(int i = 0; i < length;i++)
				{
					String regex = filesMasksArray[i];
					regex = regex.replace(".","\\.");
					regex = regex.replace("*",".*");
					regex = "^" + regex.trim() + "$";
					Pattern pattern = p5c.compile(regex,Perl5Compiler.CASE_INSENSITIVE_MASK);
					match = match || p5m.matches(fileName,pattern);
					if(match)
					{
						break;
					}
				}
			}
			else
			{
				match = true;
			}
		}
		catch(Exception ex)
		{
			Logger.debug(FolderFactory.class,ex.toString());
		}
		return match;
	}
	
	/**
	 * Gets the tree containing all the items in the list. If the item is a folder, gets the files and folders contained by the folder.
	 * It is executed recursively until reaching the depth specified. This method will change later
	 * @param items
	 * @param ids
	 * @param level
	 * @param counter
	 * @param depth
	 * @throws DotDataException 
	 */
	public static List getNavigationTree(List items,List<Integer> ids,int level,InternalCounter counter, int depth, User user) throws DotDataException
	{	
		boolean show = true;
		StringBuffer sb = new StringBuffer();
		List v = new ArrayList<Object>();
		int internalCounter = counter.getCounter();
		String className = "class" + internalCounter;
		String id = "list" + internalCounter;
		ids.add(internalCounter);
		counter.setCounter(++internalCounter);
		
		sb.append("<ul id='" + id + "' >\n");		
		Iterator itemsIter = items.iterator();		
		while (itemsIter.hasNext()) {
			Inode item = (Inode) itemsIter.next();			
			String title = "";
			String inode = "";
			if (item instanceof Folder) {
				Folder folder = ((Folder)item); 
				title = folder.getTitle();
				title = retrieveTitle(title,user);
				inode = folder.getInode();
				if(folder.isShowOnMenu()){
					if(!permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_PUBLISH, user, false)){					
						show = false;					
					}
					if(permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_READ, user, false)){
					
						sb.append("<li class=\"" + className + "\" id=\"inode_" + inode + "\" >\n" + title + "\n");
						List childs = FolderFactory.getMenuItems(folder);
						if(childs.size() > 0)
						{
							
							depth--;
							
							int nextLevel = level + 1;
							
							if(depth > 0){
								sb.append(getNavigationTree(childs,ids,nextLevel,counter, depth, user).get(0));
							}					
						}
						sb.append("</li>\n");
					}
				}
			}
			else {
				WebAsset asset = ((WebAsset)item); 
				title = asset.getTitle();
				title = retrieveTitle(title,user);
				inode = asset.getInode();				
				if(asset.isShowOnMenu()){
					if(!permissionAPI.doesUserHavePermission(asset, PermissionAPI.PERMISSION_PUBLISH, user, false)){					
						show = false;					
					}
					if(permissionAPI.doesUserHavePermission(asset, PermissionAPI.PERMISSION_READ, user, false)){					
						sb.append("<li class=\"" + className + "\" id=\"inode_" + inode + "\" >" + title + "</li>\n");								
					}
				}
			}
		}
		sb.append("</ul>\n");
		v.add(sb);
		v.add(new Boolean(show));
		
		return v;
	}
	
	/**
	 * Builds the navigation tree containing all the items in the list and the files, HTML pages, links, folders contained recursively by
	 * those items until the specified depth. This method will change later
	 * @param items
	 * @param depth
	 * @throws DotDataException 
	 */
	public static List<Object> buildNavigationTree(List items, int depth, User user) throws DotDataException	
	{
		depth = depth - 1;
		int level = 0;
		List<Object> v = new ArrayList<Object>();
		InternalCounter counter = new FolderFactory().new InternalCounter();
		counter.setCounter(0);
		List<Integer> ids = new ArrayList<Integer>();
		List l = buildNavigationTree(items,ids,level,counter, depth, user);
		StringBuffer sb = new StringBuffer("");
		if(l != null && l.size() > 0){
			sb = (StringBuffer)l.get(0);
			sb.append("<script language='javascript'>\n");
			for(int i = ids.size() - 1;i >= 0;i--)
			{
				int internalCounter = (Integer) ids.get(i);
				String id = "list" + internalCounter;
				String className = "class" + internalCounter;
				String sortCreate = "Sortable.create(\"" + id + "\",{dropOnEmpty:true,tree:true,constraint:false,only:\"" + className + "\"});\n";			
				sb.append(sortCreate);
			}
			
			sb.append("\n");
			sb.append("function serialize(){\n");
			sb.append("var values = \"\";\n");
			for(int i = 0;i < ids.size();i++)
			{
				int internalCounter = (Integer) ids.get(i);
				String id = "list" + internalCounter;		
				String sortCreate = "values += \"&\" + Sortable.serialize('" + id + "');\n";			
				sb.append(sortCreate);
			}
			sb.append("return values;\n");
			sb.append("}\n");
			
			sb.append("</script>\n");
			
			sb.append("<style>\n");
			for(int i = 0;i < ids.size();i++)
			{
				int internalCounter = (Integer) ids.get(i);
				String className = "class" + internalCounter;	
				String style = "li." + className + " { cursor: move;}\n";			
				sb.append(style);
			}
			sb.append("</style>\n");
		}
		v.add(sb.toString());
		if(l != null && l.size() > 0){
			v.add(l.get(1));
		}else{
			v.add(new Boolean(false));
		}
		
		return v;
	}
	
	/**
	 * Builds the navigation tree containing all the items and the files, HTML pages, links, folders contained recursively by
	 * those items in the list until the specified depth. This method will change later
	 * @param items
	 * @param ids
	 * @param level
	 * @param counter
	 * @param depth
	 * @throws DotDataException 
	 */
	public static List buildNavigationTree(List items,List<Integer> ids,int level,InternalCounter counter, int depth, User user) throws DotDataException
	{	
		boolean show = true;
		StringBuffer sb = new StringBuffer();
		List v = new ArrayList<Object>();
		int internalCounter = counter.getCounter();
		String className = "class" + internalCounter;
		String id = "list" + internalCounter;
		ids.add(internalCounter);
		counter.setCounter(++internalCounter);
		
		sb.append("<ul id='" + id + "' >\n");
		if(items != null){
			Iterator itemsIter = items.iterator();		
			while (itemsIter.hasNext()) {
				Inode item = (Inode) itemsIter.next();			
				String title = "";
				String inode = "";
				if (item instanceof Folder) {
					Folder folder = ((Folder)item); 
					title = folder.getTitle();					
					title = retrieveTitle(title,user);
					inode = folder.getInode();
					if(folder.isShowOnMenu()){
						if(!permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_PUBLISH, user, false)){
							show = false;					
						}
						if(permissionAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_READ, user, false)){
						
							sb.append("<li class=\"" + className + "\" id=\"inode_" + inode + "\" >\n" + title + "\n");
							List childs = FolderFactory.getMenuItems(folder);
							if(childs.size() > 0)
							{
								int nextLevel = level + 1;
								if(depth >0){
									List<Object> l = getNavigationTree(childs,ids,nextLevel,counter, depth, user);
									if(show){
										show = ((Boolean)l.get(1)).booleanValue();
									}
									sb.append((StringBuffer)(l.get(0)));
								}
							}						
							sb.append("</li>\n");
						}					
					}			
				}
				else {
					WebAsset asset = ((WebAsset)item); 
					title = asset.getTitle();
					title = retrieveTitle(title,user);
					inode = asset.getInode();
					if(asset.isShowOnMenu()){
						if(!permissionAPI.doesUserHavePermission(asset, PermissionAPI.PERMISSION_PUBLISH, user, false)){					
							show = false;					
						}
						if(permissionAPI.doesUserHavePermission(asset, PermissionAPI.PERMISSION_READ, user, false)){						
								sb.append("<li class=\"" + className + "\" id=\"inode_" + inode + "\" >" + title + "</li>\n");
						}	
					}			
				}			
			}
		}
		
		sb.append("</ul>\n");
		v.add(sb);
		v.add(new Boolean(show));
		
		return v;
	}

	private static String retrieveTitle(String title,User user) {
		try
		{
			String regularExpressionString = "(.*)\\$glossary.get\\('(.*)'\\)(.*)";
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regularExpressionString);					
			Matcher matcher = pattern.matcher(title);
			if(matcher.matches())
			{				
				String tempTitle = matcher.group(2);				
				tempTitle = matcher.group(1) + LanguageUtil.get(user,tempTitle) + matcher.group(3);				
				title = tempTitle;
			}		
		}
		catch(Exception ex)
		{
			String message = ex.getMessage();
		}
		finally{
			return title;
		}
	}
	
	private static boolean isOpenNode(String[] openNodes,String node)
	{
		boolean returnValue = false;
		for(String actualNode : openNodes)
		{
			if(actualNode.equals(node))
			{
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}
	//http://jira.dotmarketing.net/browse/DOTCMS-3232
	public static Folder getSystemFolder() throws DotDataException{
		Folder folder = new Folder();
		folder = FolderCache.getFolderByInode(SYSTEM_FOLDER);
		if(folder.getInode().equalsIgnoreCase(SYSTEM_FOLDER)){
			return folder;
		}else{
			folder = getFolderByInode(SYSTEM_FOLDER);
		}	
		if(UtilMethods.isSet(folder.getInode())&& folder.getInode().equalsIgnoreCase(SYSTEM_FOLDER)){
			FolderCache.addFolder(folder);
			return folder;
		}else{	
			DotConnect dc = new DotConnect();
			Folder folder1 = new Folder();
			folder1.setInode(SYSTEM_FOLDER);
			folder1.setName("system folder");
			folder1.setTitle("System folder");
			folder1.setPath("/System folder");
			try {
				folder1.setHostId(APILocator.getHostAPI().findSystemHost(APILocator.getUserAPI().getSystemUser(), true).getIdentifier());
			} catch (DotSecurityException e) {
				Logger.error(FolderFactory.class,e.getMessage(),e);
				throw new DotDataException(e.getMessage(), e);
			}
			folder1.setFilesMasks("");
			folder1.setSortOrder(0);
			folder1.setShowOnMenu(false);
			//InodeFactory.saveInode(folder1);
			String InodeQuery = "INSERT INTO INODE(INODE, OWNER, IDATE, TYPE, IDENTIFIER) VALUES (?,null,?,?,null)";
			dc.setSQL(InodeQuery);
			dc.addParam(folder1.getInode());
			dc.addParam(folder1.getiDate());
			dc.addParam(folder1.getType());
			dc.loadResult();
			String hostQuery = "INSERT INTO FOLDER(INODE, NAME, PATH, TITLE, SHOW_ON_MENU, SORT_ORDER, HOST_INODE, FILES_MASKS) VALUES (?,?,?,?,?,?,?,?)";
			dc.setSQL(hostQuery);
			dc.addParam(folder1.getInode());
			dc.addParam(folder1.getName());
			dc.addParam(folder1.getPath());
			dc.addParam(folder1.getTitle());
			dc.addParam(folder1.isShowOnMenu());
			dc.addParam(folder1.getSortOrder());
			dc.addParam(folder1.getHostId());
			dc.addParam(folder1.getFilesMasks());
			dc.loadResult();
			FolderCache.addFolder(folder1);
			return folder1;
		}
	  }		

	
	public class InternalCounter
	{
		private int counter;
		
		public int getCounter()
		{
			return counter;
		}
		
		public void setCounter(int counter)
		{
			this.counter = counter;
		}		
	}
}

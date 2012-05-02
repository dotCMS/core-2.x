/**
 * 
 */
package com.dotmarketing.portlets.contentlet.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.lucene.queryParser.ParseException;
import org.quartz.SimpleTrigger;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.query.SQLQueryFactory;
import com.dotmarketing.business.query.GenericQueryFactory.Query;
import com.dotmarketing.cache.FieldsCache;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.FolderCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.StructureCache;
import com.dotmarketing.cache.VirtualLinksCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.menubuilders.RefreshMenus;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.portlets.structure.business.FieldAPI;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.portlets.virtuallinks.model.VirtualLink;
import com.dotmarketing.quartz.QuartzUtils;
import com.dotmarketing.quartz.SimpleScheduledTask;
import com.dotmarketing.quartz.job.UpdateContentsOnDeleteHost;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.lucene.LuceneUtils;
import com.liferay.portal.model.User;

/**
 * @author jtesser
 * @author david torres
 *
 */
public class HostAPIImpl implements HostAPI {
	
	private PermissionAPI permissionAPI = APILocator.getPermissionAPI();
	private ContentletAPI contentAPI = APILocator.getContentletAPI();
	private ContentletFactory conFac = FactoryLocator.getContentletFactory();
	private HostCache hostCache = CacheLocator.getHostCache();
	private Host systemHost;
	private FieldAPI fieldAPI = APILocator.getFieldAPI();
	private FolderAPI folderAPI = APILocator.getFolderAPI();

	public HostAPIImpl() {
	}
	
	/**
	 * 
	 * @return the default host from cache.  If not found, returns from content search and adds to cache
	 * @throws DotSecurityException, DotDataException 
	 */
	public Host findDefaultHost(User user, boolean respectFrontendRoles) throws DotSecurityException, DotDataException {
		
		Host host = null;
		try{
			host  = hostCache.getDefaultHost();
			if(host != null){
				if(permissionAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles));
					return host;
			}
		}
		catch(Exception e){
			Logger.debug(HostAPIImpl.class, e.getMessage(), e);
		}
		
		try {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
	    	Field f = st.getFieldVar("isDefault");
			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + " +working:true +" + f.getFieldContentlet() +  ":true", 0, 0, null, user, respectFrontendRoles);
			if(list.size() > 1) 
				Logger.fatal(this, "More of one host is marked as default!!");
			else if (list.size() == 0)
				return createDefaultHost();
			host = new Host(list.get(0));
			hostCache.add(host);
			return host;
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * 
	 * @param hostName
	 * @return the host with the passed in name
	 * @throws DotSecurityException  
	 * @throws DotDataException 
	 */
	public Host findByName(String hostName, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		
		Host host = null;
		try{
			host  = hostCache.get(hostName);
			if(host != null){
				if(permissionAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles));
					return host;
			}
		}
		catch(Exception e){
			Logger.debug(HostAPIImpl.class, e.getMessage(), e);
		}
		
		
		
		
		try {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
	    	Field hostNameField = st.getFieldVar("hostName");
			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + 
					" +working:true +" + hostNameField.getFieldContentlet() + ":" + hostName, 0, 0, null, user, respectFrontendRoles);
			if(list.size() > 1) {
				Logger.fatal(this, "More of one host has the same name or alias = " + hostName + "!!");
				int i=0;
				for(Contentlet c : list){
					Logger.fatal(this, "\tdupe Host " + (i+1) + ": " + list.get(i).getTitle() );
					i++;
				}
			}else if (list.size() == 0){
				return null;
			}
			host = new Host(list.get(0));
			
			hostCache.add(host);
			
			return host;
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param hostName
	 * @return the host with the passed in name
	 */
	public Host findByAlias(String alias, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		Host host = null;
		try{
			host  = hostCache.getHostByAlias(alias);
			if(host != null){
				if(permissionAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles));
				 if(host.isDefault()){	
				     return host;
				 }
			}
		}catch(Exception e){
			Logger.debug(HostAPIImpl.class, e.getMessage(), e);
		}
		
		try {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
	    	Field aliasesField = st.getFieldVar("aliases");

			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + 
					" +working:true +" + aliasesField.getFieldContentlet() + ":" + alias, 0, 0, null, user, respectFrontendRoles);
			if(list.size() > 1){
				for(Contentlet cont: list){
					boolean isDefaultHost = (Boolean)cont.get("isDefault");
					if(isDefaultHost){
						host = new Host(cont);
						if(host.isDefault()){
							break;
						}
					}
				}
				if(host==null){
					Logger.error(this, "More of one host match the same alias " + alias + "!!");
					host = new Host(list.get(0));
				}
			}else if (list.size() == 0){
				return null;
			}else{
			   host = new Host(list.get(0));
			}
			return host;
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	}


	public Host find(String id, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		Host host = null;
		try{
			host  = hostCache.get(id);
			if(host != null){
				if(permissionAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles));
					return host;
			}
		}
		catch(Exception e){
			Logger.debug(HostAPIImpl.class, e.getMessage(), e);
		}
		
		
		try {
			if(!UtilMethods.isSet(id))
				return null;
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + " +working:true +identifier:" + id, 0, 0, null, user, respectFrontendRoles);
			if(list.size() > 1)
				Logger.error(this, "More of one working version of host match the same identifier " + id + "!!");
			else if (list.size() == 0) {
				// http://jira.dotmarketing.net/browse/DOTCMS-7352
			    // maybe we're reindexing for the first time so hosts
			    // can't be found on index
			    try {
    			    Contentlet con=contentAPI.findContentletByIdentifier(id, false, APILocator.getLanguageAPI().getDefaultLanguage().getId(), user, respectFrontendRoles);
    			    if(con!=null) {
    			        host = new Host(con);
    			        hostCache.add(host);
    			        return host;
    			    }
    			    else
    			        return null;
			    }
			    catch(Exception ex) {
			        Logger.warn(this, ex.getMessage());
			        return null;
			    }
			}
			host = new Host(list.get(0));
			hostCache.add(host);
			return host;
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the list of all hosts in the system
	 * @throws DotSecurityException 
	 * @throws DotDataException 
	 * 
	 */
	public List<Host> findAll(User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		try {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + " +working:true", 0, 0, null, user, respectFrontendRoles);
			return convertToHostList(list);
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	}


	/**
	 * @throws DotSecurityException 
	 * @throws DotDataException 
	 */
	public Host save(Host host, User user, boolean respectFrontendRoles) throws DotSecurityException, DotDataException {
		if(host != null)
			hostCache.remove(host);
		Contentlet c;
		try {
			c = contentAPI.checkout(host.getInode(), user, respectFrontendRoles);
		} catch (DotContentletStateException e) {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
			c = new Contentlet();
			c.setStructureInode(st.getInode());
		}
		contentAPI.copyProperties(c, host.getMap());
		c.setInode("");
		c.setLive(true);
		c = contentAPI.checkin(c, user, respectFrontendRoles);
		Host h =  new Host(c);
		hostCache.add(h);
		return h;

	}
	
	public List<Host> getHostsWithPermission(int permissionType, boolean includeArchived, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		try {
	    	Structure st = StructureCache.getStructureByVelocityVarName("Host");
			List<Contentlet> list = contentAPI.search("+structureInode:" + st.getInode() + " +working:true", 0, 0, null, user, respectFrontendRoles);
			list = permissionAPI.filterCollection(list, permissionType, respectFrontendRoles, user);
			if (includeArchived) {
				return convertToHostList(list);
			} else {
				List<Host> hosts = convertToHostList(list);
				
				List<Host> filteredHosts = new ArrayList<Host>();
				for (Host host: hosts) {
					if (!host.isArchived())
						filteredHosts.add(host);
				}
				
				return filteredHosts;
			}
		} catch (ParseException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
	}
	
	public List<Host> getHostsWithPermission(int permissionType, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		return getHostsWithPermission(permissionType, true, user, respectFrontendRoles);
	}

	public Host findSystemHost (User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		if(systemHost != null){
			return systemHost;
		}
		
		try {
			SQLQueryFactory factory = new SQLQueryFactory("SELECT * FROM Host WHERE isSystemHost = 1");
			List<Map<String, Serializable>> hosts = factory.execute();
			if(hosts.size() == 0) {
				createSystemHost();
			} else {
				systemHost = new Host(conFac.find((String)hosts.get(0).get("inode")));
			}
			if(hosts.size() > 1){
				Logger.fatal(this, "There is more than one working version of the system host!!");
			}
		} catch (Exception e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}
		return systemHost;
	}
	
	public Host findSystemHost () throws DotDataException {

		try {
			return findSystemHost(	APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotSecurityException e) {
			Logger.error(HostAPIImpl.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		}

	}
	
	
	
	
	
	public Host findParentHost(Folder folder, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		return find(folder.getHostId(), user, respectFrontendRoles);
	}
	
	public Host findParentHost(WebAsset asset, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		if (asset instanceof Container || asset instanceof Template) {

			Host host = null;
			List<Tree> trees = TreeFactory.getTreesByChild(asset);
			for(Tree tree : trees) {
				host = find(tree.getParent(), user, respectFrontendRoles);
				if(host != null) break;
			}
			return host;
		}
		Folder parentFolder = (Folder) InodeFactory.getParentOfClass(asset,
				Folder.class);
		if(InodeUtils.isSet(parentFolder.getInode()))
			return findParentHost(parentFolder, user, respectFrontendRoles);
		else
			return null;
	}	

	public boolean doesHostContainsFolder(Host parent, String folderName) throws DotDataException {
		List<Tree> trees = TreeFactory.getTreesByParent(parent.getIdentifier());
		FolderAPI folderAPI = APILocator.getFolderAPI();
		for (Tree tree : trees) {
			Folder folder = folderAPI.find(tree.getChild());
			if (folder.getName().equals(folderName))
				return true;
		}
		return false;

	}

	public void delete(Host host, User user, boolean respectFrontendRoles) {
		DotConnect dc = new DotConnect();
		dc.setSQL("select inode.inode,type,identifier,host_inode from identifier,inode where inode.identifier = identifier.inode " +
				  "and host_inode= ? and type!='contentlet' order by type");
		dc.addParam(host.getIdentifier());
		List<HashMap<String, String>> inodes = dc.getResults();	
		class DeleteHostThread extends Thread {
			private User user;
			private boolean respectFrontendRoles;
			private Host host;
			private List<HashMap<String, String>> inodes;

			public DeleteHostThread(Host host,User user, boolean respectFrontendRoles,List<HashMap<String, String>> inodes) {
				this.host = host;
				this.user = user;
				this.respectFrontendRoles = respectFrontendRoles;
				this.inodes = inodes;
			}
			public void run() {
				try {
					deleteHost();
				} catch (Exception e) {
					Logger.error(HostAPIImpl.class, e.getMessage(), e);
					throw new DotRuntimeException(e.getMessage(), e);
				}
			}
			public void deleteHost(){
				if(host != null){
					hostCache.remove(host);
				}
				DotHibernate.startTransaction();
				DotConnect dc = new DotConnect();
				List<String> identsDeleted = new ArrayList<String>();
				List<String> inodesDeleted = new ArrayList<String>();
					
				String selectFolders= "SELECT * from folder where host_inode = ?";
					
				String deleteInodes = "DELETE FROM inode WHERE inode = ?";
				
				String deleteIdentifier = "DELETE FROM identifier WHERE inode = ?";
					
				String deleteHTMLPages = "DELETE from htmlpage where inode in(select inode.inode from identifier,inode where " +
	                                     "inode.identifier=identifier.inode and host_inode= ?)";
				
				String deleteFileAssets = "DELETE from file_asset where inode in(select inode.inode from identifier,inode where " +
							              "inode.identifier=identifier.inode and host_inode= ?)";
					
				String deleteLinks = "DELETE from links where inode in(select inode.inode from identifier,inode where " +
	                  			     "inode.identifier=identifier.inode and host_inode= ?)";
					
		        String deleteTemplates = "DELETE from template where inode in(select inode.inode from identifier,inode where " +
	                  					 "inode.identifier=identifier.inode and host_inode= ?)";
					
				String deleteContainers = "DELETE from containers where inode in(select inode.inode from identifier,inode where " +
	                                      "inode.identifier=identifier.inode and host_inode= ?)";
					
				String deletefolders = "DELETE from folder where host_inode = ?";
					
				String deleteTree = "delete from tree where child = ? or parent =?";
					
				String deletePermissionRef = "delete from permission_reference where asset_id = ? or reference_id = ?";
				
				String deletePermission = "delete from permission where inode_id = ?";
				
				String updateContentletToSystemFolder = "Update Contentlet set folder = ? where folder = ?";
				
				String updateStructureToSystemFolderHost = "Update structure set folder = ?, host = ? where inode = ?";
				
				String selectStructures= "SELECT * from structure where host  = ?";
					
				//Deletion of assets in a host
				try {
				    for(HashMap<String, String> inde:inodes){
				    String inode = inde.get("inode");
					String identifier = inde.get("identifier");
					if(InodeUtils.isSet(inode)){
						  //remove permissionsRef
						  dc.setSQL(deletePermissionRef);
						  dc.addParam(identifier);
						  dc.addParam(identifier);
						  dc.loadResult();
						//remove permissions
						  dc.setSQL(deletePermission);
						  dc.addParam(inode);						 
						  dc.loadResult();
						  dc.setSQL(deletePermission);
						  dc.addParam(identifier);						 
						  dc.loadResult();
						  //remove from Tree
						  dc.setSQL(deleteTree);
						  dc.addParam(inode);
						  dc.addParam(inode);
						  dc.loadResult();
						  dc.setSQL(deleteTree);
						  dc.addParam(identifier);
						  dc.addParam(identifier);
						  dc.loadResult();
						  if(!inodesDeleted.contains(inode) && inode!=null && inode!="")
						      inodesDeleted.add(inode);
					      if(!identsDeleted.contains(identifier) && identifier!=null && identifier!="")
						      identsDeleted.add(identifier);
					    }   
					 }	
					dc.setSQL(deleteFileAssets);
					dc.addParam(host.getIdentifier());
					dc.loadResult();
					dc.setSQL(deleteHTMLPages);
					dc.addParam(host.getIdentifier());
					dc.loadResult();			
					dc.setSQL(deleteLinks);
					dc.addParam(host.getIdentifier());
					dc.loadResult();	
					dc.setSQL(deleteContainers);
					dc.addParam(host.getIdentifier());
					dc.loadResult();
					dc.setSQL(deleteTemplates);
					dc.addParam(host.getIdentifier());
					dc.loadResult();
					
					
					//remove Inodes
					for(String inode:inodesDeleted){
						dc.setSQL(deleteInodes);
					    dc.addParam(inode);
						dc.loadResult();
					}
					//remove Identifiers 
					for(String ident:identsDeleted){
					   dc.setSQL(deleteIdentifier);
					   dc.addParam(ident);
					   dc.loadResult();
					   
					   dc.setSQL(deleteInodes);
				       dc.addParam(ident);
					   dc.loadResult();
					}
					
					
					//update structures with system host
					dc.setSQL(selectStructures);
					dc.addParam(host.getIdentifier());
					List<HashMap<String, String>> structures = dc.getResults();	
					for(HashMap<String, String> structure:structures){
					  String structureInode=structure.get("inode"); 
					  //update structure to SYSTEMFOLDER
					  dc.setSQL(updateStructureToSystemFolderHost);
					  dc.addParam("SYSTEM_FOLDER");
					  dc.addParam("SYSTEM_HOST");
					  dc.addParam(structureInode);
					  dc.loadResult();
					
					}
					
					
					//Deletion of folders in a host
					dc.setSQL(selectFolders);
					dc.addParam(host.getIdentifier());
					List<HashMap<String, String>> folders = dc.getResults();	
					for(HashMap<String, String> folder:folders){
					  String folderInode=folder.get("inode");
					  
					  //update content to SYSTEMFOLDER
					  dc.setSQL(updateContentletToSystemFolder);
					  dc.addParam(folderAPI.findSystemFolder().getInode());
					  dc.addParam(folderInode);
					  dc.loadResult();
					  
					  //remove from Tree
					  dc.setSQL(deleteTree);
					  dc.addParam(folderInode);
					  dc.addParam(folderInode);
					  dc.loadResult();
					}
					dc.setSQL(deletefolders);
					dc.addParam(host.getIdentifier());
					dc.loadResult();
					contentAPI.UpdateContentWithSystemHost(host.getIdentifier());
						
					Contentlet c = contentAPI.find(host.getInode(), user, respectFrontendRoles);
					contentAPI.delete(c, user, respectFrontendRoles);
					removeHostFromContents(host);
					
					
					FileCache.clearCache();
					CacheLocator.getHTMLPageCache().clearCache();
					CacheLocator.getMenuLinkCache().clearCache();
					CacheLocator.getTemplateCache().clearCache();
					CacheLocator.getContainerCache().clearCache();
					FolderCache.clearCache();
					StructureCache.clearCache();
					IdentifierCache.clearCache();
					CacheLocator.getPermissionCache().clearCache();
					hostCache.remove(host);
			} catch (Exception e) {
				DotHibernate.rollbackTransaction();
				Logger.error(HostAPIImpl.class, e.getMessage(), e);
				throw new DotRuntimeException(e.getMessage(), e);
			}
			DotHibernate.commitTransaction();
		  }
		}
		DeleteHostThread thread = new DeleteHostThread(host,user,respectFrontendRoles,inodes);

		if (inodes.size()> 50) {
			// Starting the thread
			thread.start();
			//SessionMessages.add(httpReq, "message", "message.contentlets.batch.deleting.background");
		} else {
			// Executing synchronous because there is not that many
			thread.deleteHost();
		}		
	}

	public void archive(Host host, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException,
			DotContentletStateException {
		if(host != null){
			hostCache.remove(host);
		}
		
		Contentlet c = contentAPI.find(host.getInode(), user, respectFrontendRoles);
		
		//retrieve all hosts that have this current host as tag storage host
		List<Host> hosts = retrieveHostsPerTagStorage(host.getTagStorage(), user);
		for(Host h: hosts) {
			if(!h.getIdentifier().equals(host.getIdentifier())){
				//prevents changing tag storage for archived host. 
				//the tag storage will change for all hosts which tag storage is archived host
				h.setTagStorage(h.getIdentifier());
				h = save(h, user, true);
			}
		}
		
		contentAPI.archive(c, user, respectFrontendRoles);
		host.setArchived(true);
		host.setModDate(new Date ());
		
	}

	public void unarchive(Host host, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException,
			DotContentletStateException {
		if(host != null){
			hostCache.remove(host);
		}
		Contentlet c = contentAPI.find(host.getInode(), user, respectFrontendRoles);
		contentAPI.unarchive(c, user, respectFrontendRoles);
		host.setArchived(false);
		host.setModDate(new Date ());
		
	}

	private Host createDefaultHost() throws DotDataException,
			DotSecurityException {
		

		SQLQueryFactory factory = new SQLQueryFactory("SELECT * FROM Host WHERE isDefault = 1");
		List<Map<String, Serializable>> hosts = factory.execute();
		User systemUser = APILocator.getUserAPI().getSystemUser();
		Host defaultHost;
		if(hosts.size() == 0) {
			defaultHost = new Host();
			defaultHost.setDefault(true);
			defaultHost.setHostname("localhost");
			defaultHost = save(defaultHost, systemUser, false);
		} else {
			defaultHost = new Host(contentAPI.find((String)hosts.get(0).get("inode"), systemUser, false));
		}
		if(defaultHost != null){
			hostCache.remove(defaultHost);
		}
		return defaultHost;
		
	}


	private synchronized Host createSystemHost() throws DotDataException,
			DotSecurityException {
		
		SQLQueryFactory factory = new SQLQueryFactory("SELECT * FROM Host WHERE isSystemHost = 1");
		List<Map<String, Serializable>> hosts = factory.execute();
		User systemUser = APILocator.getUserAPI().getSystemUser();
		if(hosts.size() == 0) {
			Host systemHost = new Host();
			systemHost.setDefault(false);
			systemHost.setHostname("system");
			systemHost.setSystemHost(true);
			systemHost.setWorking(true);
			systemHost.setHost(null);
			systemHost = new Host(conFac.save(systemHost));
			systemHost.setIdentifier(Host.SYSTEM_HOST);
			systemHost.setLive(false);
			systemHost.setModDate(new Date());
			systemHost.setModUser(systemUser.getUserId());
			systemHost.setOwner(systemUser.getUserId());
			systemHost.setHost(null);
			systemHost.setFolder(null);
			conFac.save(systemHost);
			this.systemHost = systemHost;
		} else {
			this.systemHost = new Host(conFac.find((String)hosts.get(0).get("inode")));
		}
		return systemHost;
	}
	private List<Host> convertToHostList(List<Contentlet> list) {
		List<Host> hosts = new ArrayList<Host>();
		for(Contentlet c : list) {
			hosts.add(new Host(c));
		}
		return hosts;
	}

	public void publish(Host host, User user, boolean respectFrontendRoles) throws DotContentletStateException, DotDataException, DotSecurityException {
		
		if(host != null){
			hostCache.remove(host);
		}
		Contentlet c = contentAPI.find(host.getInode(), user, respectFrontendRoles);
		contentAPI.publish(c, user, respectFrontendRoles);
		host.setLive(true);
		hostCache.add(host);
	}

	public void unpublish(Host host, User user, boolean respectFrontendRoles) throws DotContentletStateException, DotDataException, DotSecurityException {
		if(host != null){
			hostCache.remove(host);
		}
		Contentlet c = contentAPI.find(host.getInode(), user, respectFrontendRoles);
		contentAPI.unpublish(c, user, respectFrontendRoles);
		host.setLive(false);
		hostCache.add(host);
	}

	public void makeDefault(Host host, User user, boolean respectFrontendRoles) throws DotContentletStateException, DotDataException, DotSecurityException {
		Host currentDefault = findDefaultHost(user, respectFrontendRoles);
		host.setDefault(true);
		if(host != null){
			hostCache.remove(host);
		}
		if(currentDefault != null)
			currentDefault.setDefault(false);
		save(host, user, respectFrontendRoles);
		if(currentDefault != null)
			save(currentDefault, user, respectFrontendRoles);
		
	}
	
	private void removeHostFromContents(Host host) throws DotSecurityException, DotDataException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("host", host);
		if(host != null){
			hostCache.remove(host);
		}
		try {
			if(!QuartzUtils.isJobSequentiallyScheduled("setup-host-" + host.getIdentifier(), "setup-host-group")) {
				Calendar startTime = Calendar.getInstance();
				SimpleScheduledTask task = new SimpleScheduledTask("delete-host-" + host.getIdentifier(),
						"delete-host-group",
						"Update content from deleted host " + host.getIdentifier(),
						UpdateContentsOnDeleteHost.class.getCanonicalName(),
						false,
						"delete-host-" + host.getIdentifier() + "-trigger",
						"delete-host-trigger-group",
						startTime.getTime(),
						null,
						SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT,
						5,
						true,
						parameters,
						0,
						0);
				QuartzUtils.scheduleTask(task);
			}
		} catch (Exception e) {
			Logger.error(this, e.getMessage(), e);
			throw new DotDataException(e.getMessage(), e);
		}
	}
	
	public Host DBSearch(String id, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		if (!UtilMethods.isSet(id))
			return null;
		
		Structure st = StructureCache.getStructureByVelocityVarName("Host");
		List<Field> fields = FieldsCache.getFieldsByStructureInode(st.getInode());
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT inode");
		for (Field field: fields) {
			if (fieldAPI.valueSettable(field) && !field.getFieldType().equals(Field.FieldType.BINARY.toString())) {
				sql.append(", " + field.getVelocityVarName());
			}
		}
		sql.append(" FROM host ");
		sql.append("WHERE identifier='" + id + "' AND ");
		sql.append("working=" + DbConnectionFactory.getDBTrue().replaceAll("'", ""));
		SQLQueryFactory sqlQueryFactory = new SQLQueryFactory(sql.toString());
		Query query = sqlQueryFactory.getQuery();
		
		List<Map<String, Serializable>> list = contentAPI.DBSearch(query, user, respectFrontendRoles);
		if (1 < list.size())
			Logger.error(this, "More of one working version of host match the same identifier " + id + "!!");
		else if (list.size() == 0)
			return null;
		
		Host host = new Host();
		
		for (String key: list.get(0).keySet()) {
			host.setProperty(key, list.get(0).get(key));
		}
		
		return host;
	}
	
	public void updateCache(Host host) {
		hostCache.remove(host);
		hostCache.add(new Host(host));
	}

	public List<String> parseHostAliases(Host host) {
		List<String> ret = new ArrayList<String>();
		if(host.getAliases() == null){
			return ret;
		}
		StringTokenizer tok = new StringTokenizer(host.getAliases(), ", \n\r\t");
		while (tok.hasMoreTokens()) {
			 ret.add(tok.nextToken());
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public void updateVirtualLinks(Host workinghost,Host updatedhost) throws DotDataException {//DOTCMS-5025
		
		String workingHostName = workinghost.getHostname();
		String updatedHostName = updatedhost.getHostname();		
		String workingURL = "";
		String newURL = "";
		DotHibernate dh = new DotHibernate(VirtualLink.class);
		List<VirtualLink> resultList = new ArrayList<VirtualLink>();
		dh.setQuery("select inode from inode in class " + VirtualLink.class.getName() + " where inode.url like ?");
		dh.setParam(workingHostName+":/%");
        resultList = dh.list();
		for (VirtualLink vl : resultList) {
			workingURL = vl.getUrl();
			newURL = updatedHostName+workingURL.substring(workingHostName.length());//gives url with updatedhostname	
			vl.setUrl(newURL);
			HibernateUtil.saveOrUpdate(vl);
		}
		
		VirtualLinksCache.clearCache();
	}
	
	@SuppressWarnings("unchecked")
	public void updateMenuLinks(Host workinghost,Host updatedhost) throws DotDataException {//DOTCMS-5090
		
		String workingHostName = workinghost.getHostname();
		String updatedHostName = updatedhost.getHostname();	
		String workingURL = "";
	    String newURL = "";
		DotHibernate dh = new DotHibernate(Link.class);
		List<Link> resultList = new ArrayList<Link>();
		dh.setQuery("select asset from asset in class " + Link.class.getName() + " where asset.url like ?");
        dh.setParam(workingHostName+"/%");	
        resultList = dh.list();
        for(Link link : resultList){
        	workingURL = link.getUrl();
			newURL = updatedHostName+workingURL.substring(workingHostName.length());//gives url with updatedhostname
			link.setUrl(newURL);
			HibernateUtil.saveOrUpdate(link);
        }
        CacheLocator.getMenuLinkCache().clearCache();
        RefreshMenus.deleteMenus();

		
	}	
	
	public List<Host> retrieveHostsPerTagStorage (String tagStorageId, User user) {
		List<Host> hosts = new ArrayList<Host>();
		List<Host> allHosts = new ArrayList<Host>();
		try {
			allHosts = findAll(user, true);
		} catch (DotDataException e) {
			e.printStackTrace();
		} catch (DotSecurityException e) {
			e.printStackTrace();
		}
		
		if (allHosts.size() > 0) {
			for (Host h: allHosts) {
				if(h.isSystemHost())
					continue;
				if (h.getTagStorage().equals(tagStorageId)){
					hosts.add(h);
				}
			}
		}

		return hosts;
		
	}
}
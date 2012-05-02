package com.dotmarketing.portlets.containers.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.BaseWebAssetAPI;
import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.services.ContainerServices;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

public class ContainerAPIImpl extends BaseWebAssetAPI implements ContainerAPI {
	
	protected PermissionAPI permissionAPI;
	protected ContainerFactory containerFactory;
	protected HostAPI hostAPI;
	
	public ContainerAPIImpl () {
		permissionAPI = APILocator.getPermissionAPI();
		containerFactory = FactoryLocator.getContainerFactory();
		hostAPI = APILocator.getHostAPI();
	}
	
	public Container copy(Container source, Host destination, User user, boolean respectFrontendRoles)
			throws DotDataException, DotSecurityException {

		if (!permissionAPI.doesUserHavePermission(source, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to read the source container.");
		}

		if (!permissionAPI.doesUserHavePermission(destination, PermissionAPI.PERMISSION_WRITE, user,
				respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to wirte in the destination folder.");
		}
		
		//gets the new information for the template from the request object
		Container newContainer = new Container();

		newContainer.copy(source);
		newContainer.setLocked(false);
		newContainer.setLive(source.isLive());
		
		String appendToName = getAppendToContainerTitle(source.getTitle(), destination);
       	newContainer.setFriendlyName(source.getFriendlyName() + appendToName);
       	newContainer.setTitle(source.getTitle() + appendToName);

		//persists the webasset
		save(newContainer);
		
		TreeFactory.saveTree(new Tree(destination.getIdentifier(), newContainer.getInode()));
		
		//Copy the structure relationship
        Structure st = (Structure) InodeFactory.getParentOfClass(source, Structure.class);
        TreeFactory.saveTree(new Tree(st.getInode(), newContainer.getInode()));
        
		//creates new identifier for this webasset and persists it
		Identifier newIdentifier = IdentifierFactory.createNewIdentifier(newContainer, destination);

		//Copy permissions
		permissionAPI.copyPermissions(source, newContainer);
		
		//saves to working folder under velocity
		ContainerServices.invalidate(newContainer, newIdentifier, true);
    	
		return newContainer;
	}
	
	private void save(Container container) throws DotDataException {
		containerFactory.save(container);
	}
	
	protected void save(WebAsset webAsset) throws DotDataException {
		save((Container) webAsset);
	}
	
	@SuppressWarnings("unchecked")
	private String getAppendToContainerTitle(String containerTitle, Host destination) {
		String temp = new String(containerTitle);
		String result = "";
		
		List<Container> containers = InodeFactory.getChildrenClassByConditionAndOrderBy(destination.getIdentifier(), Container.class, DbConnectionFactory.getDBTrue() + "=" + DbConnectionFactory.getDBTrue(), "title");
		
		boolean isContainerTitle = false;
		
		for (; !isContainerTitle;) {
			isContainerTitle = true;
			temp += result;
			
			for (Container container: containers) {
				if (container.getTitle().equals(temp)) {
					isContainerTitle = false;
					break;
				}
			}
			
			if (!isContainerTitle)
				result += " (COPY)";
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Container getWorkingContainerById(String id, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {

		DotHibernate dh = new DotHibernate(Container.class);
		dh.setSQLQuery("select {containers.*} from containers, inode containers_1_ where containers.inode = containers_1_.inode and " +
				"containers_1_.identifier = ? and working = ?");
		dh.setParam(id);
		dh.setParam(true);
		List<Container> list = dh.list();
		
		if(list.size() == 0)
			return null;

		Container container = list.get(0);

		if (!permissionAPI.doesUserHavePermission(container, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to read the source file.");
		}
		
		if(InodeUtils.isSet(container.getInode()))
			return container;
		else
			return null;
	}
	
	/**
	 * 
	 * Retrieves the children working containers attached to the given template
	 * 
	 * @param parentTemplate
	 * @return
	 * @author David H Torres
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Container> getContainersInTemplate(Template parentTemplate)  {
		List<Identifier> identifiers = InodeFactory.getChildrenClass(parentTemplate, Identifier.class);
		List<Container> containers = new ArrayList<Container>();
		for(Identifier id : identifiers) {
			Container cont = (Container) IdentifierFactory.getWorkingChildOfClass(id, Container.class);
			containers.add(cont);
		}
		return containers;
	}
	
	/**
	 * Retrieves all the containers attached to the given host
	 * @param parentPermissionable
	 * @return 
	 * @throws DotDataException 
	 * 
	 */
	public List<Container> findContainersUnder(Host parentPermissionable) throws DotDataException {
		return containerFactory.findContainersUnder(parentPermissionable);
	}
	
	@SuppressWarnings("unchecked")
	public Container save(Container container, Structure structure, Host host, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		Container currentContainer = null;
		List<Template> currentTemplates = null;
		Identifier identifier = null;
		
		if (UtilMethods.isSet(container.getIdentifier())) {
			currentContainer = getWorkingContainerById(container.getIdentifier(), user, respectFrontendRoles);
			currentTemplates = InodeFactory.getChildrenClass(currentContainer, Template.class);
			identifier = IdentifierFactory.getIdentifierByInode(currentContainer);
		}
		
		if ((identifier != null)  && !permissionAPI.doesUserHavePermission(currentContainer, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to write the container.");
		}
		
		if((structure != null) && !permissionAPI.doesUserHavePermission(structure, PermissionAPI.PERMISSION_READ, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to use the structure.");
		}
		
		if(!permissionAPI.doesUserHavePermission(host, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			throw new DotSecurityException("You don't have permission to write on the given host.");
		}
		
		String userId = user.getUserId();

		// it saves or updates the asset
		if (identifier != null) {
			createAsset(container, userId, identifier, false);
			container = (Container) saveAsset(container, identifier);
		} else {
			createAsset(container, userId);
		}
		
		// Get templates of the old version so you can update the working
		// information to this new version.
		if (currentTemplates != null) {
			Iterator<Template> it = currentTemplates.iterator();
			
			// update templates to new version
			while (it.hasNext()) {
				Template parentInode = (Template) it.next();
				TreeFactory.saveTree(new Tree(parentInode.getInode(), container.getInode()));
			}
		}
		
		// Associating the current structure
		if ((structure != null) && InodeUtils.isSet(structure.getInode()))
			TreeFactory.saveTree(new Tree(structure.getInode(), container.getInode()));

        //Saving the host of the template
        TreeFactory.saveTree(new Tree(host.getIdentifier(), container.getInode()));
        
		// saves to working folder under velocity
		ContainerServices.invalidate(container, true);
		
		return container;
	}
	
	public boolean delete(Container container, User user, boolean respectFrontendRoles) throws DotSecurityException, DotDataException {
		if(permissionAPI.doesUserHavePermission(container, PermissionAPI.PERMISSION_WRITE, user, respectFrontendRoles)) {
			return deleteAsset(container);
		} else {
			throw new DotSecurityException(WebKeys.USER_PERMISSIONS_EXCEPTION);
		}
	}

	public List<Container> findAllContainers(User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		List<Container> containers = containerFactory.findAllContainers();
		return permissionAPI.filterCollection(containers, PermissionAPI.PERMISSION_USE, respectFrontendRoles, user);
	}

	public Host getParentHost(Container cont, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		return hostAPI.findParentHost(cont, user, respectFrontendRoles);
	}

	public List<Container> findContainers(User user, boolean includeArchived,
			Map<String, Object> params, String hostId,String inode, String identifier, String parent,
			int offset, int limit, String orderBy) throws DotSecurityException,
			DotDataException {
		return containerFactory.findContainers(user, includeArchived, params, hostId, inode, identifier, parent, offset, limit, orderBy);
	}

}
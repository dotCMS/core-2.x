package com.dotmarketing.portlets.containers.factories;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.TreeFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.services.ContainerServices;
import com.dotmarketing.util.Logger;
/**
 *
 * @author  will
 */
public class ContainerFactory {
    
    public static java.util.List getChildrenContainerByOrder(Inode i) {
    	
    	return InodeFactory.getChildrenClassByOrder(i, Container.class, "sort_order");

    }
    
    public static java.util.List getActiveContainers() {
        DotHibernate dh = new DotHibernate(Container.class);
        dh.setQuery(
            "from inode in class com.dotmarketing.portlets.containers.model.Container where type='containers'");

        return dh.list();
    }

    public static java.util.List getContainersByOrderAndParent(String orderby,Inode o) {
    	
    	
		return InodeFactory.getChildrenClassByOrder(o, Container.class, orderby);

    }

    public static java.util.List getContainersByOrder(String orderby) {
        DotHibernate dh = new DotHibernate(Container.class);
        dh.setQuery(
            "from inode in class com.dotmarketing.portlets.containers.model.Container where type='containers' and working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by " + orderby);

        return dh.list();
    }
 
    public static java.util.List getContainerChildrenByCondition(Inode o,String condition) {
    	
    	return InodeFactory.getChildrenClassByConditionAndOrderBy(o, Container.class, condition, "title, sort_order");


    }
    
   	public static java.util.List getContainerByCondition(String condition) {
		DotHibernate dh = new DotHibernate(Container.class);
		dh.setQuery("from inode in class  com.dotmarketing.portlets.containers.model.Container where type='containers' and " + condition + " order by title, sort_order");
		return dh.list();
	}

    public static java.util.List getContainerChildren(Inode o) {
    	
    	return InodeFactory.getChildrenClassByOrder(o, Container.class, "inode, sort_order");

    }

	public static Container getContainerByLiveAndFolderAndTitle(Inode parent , String title) {
		
		
    	
		return (Container) InodeFactory.getChildOfClassbyCondition(parent, Container.class, "title =  '"+ title +"' and live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
		

	}
	
    public static boolean existsContainer(String friendlyName) {
        DotHibernate dh = new DotHibernate(Container.class);
        dh.setQuery("from inode in class com.dotmarketing.portlets.containers.model.Container where type='containers' and friendly_name = ?");
        dh.setParam(friendlyName);
        return (((java.util.List) dh.list()).size()>0);
    }
    
    public static Container getContainerByFriendlyName(String friendlyName) {
        DotHibernate dh = new DotHibernate(Container.class);
        dh.setQuery("from inode in class com.dotmarketing.portlets.containers.model.Container where type='containers' and friendly_name = ? and live=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
        dh.setParam(friendlyName);
        return (Container) dh.load();
    }
    
    public static Container copyContainer (Container currentContainer) throws DotDataException {
    	
    	HostAPI hostAPI = APILocator.getHostAPI();
    	
		//gets the new information for the template from the request object
		Container newContainer = new Container();

		newContainer.copy(currentContainer);
		newContainer.setLocked(false);
		newContainer.setLive(false);
       	newContainer.setFriendlyName(currentContainer.getFriendlyName()
				+ " (COPY) ");
       	newContainer.setTitle(currentContainer.getTitle() + " (COPY) ");

		//persists the webasset
		InodeFactory.saveInode(newContainer);

		
		//Copy the host
		Host h;
		try {
			h = hostAPI.findParentHost(currentContainer, APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotSecurityException e) {
			Logger.error(ContainerFactory.class, e.getMessage(), e);
			throw new DotRuntimeException(e.getMessage(), e);
		} 
        TreeFactory.saveTree(new Tree(h.getIdentifier(), newContainer.getInode()));
		
		//Copy the structure
        Structure st = (Structure) InodeFactory.getParentOfClass(currentContainer, Structure.class);
        st.addChild(newContainer);
        
		//creates new identifier for this webasset and persists it
		Identifier newIdentifier = IdentifierFactory.createNewIdentifier(newContainer, h);

		PermissionAPI perAPI = APILocator.getPermissionAPI();
		//Copy permissions
		perAPI.copyPermissions(currentContainer, newContainer);
		
		//saves to working folder under velocity
		ContainerServices.invalidate(newContainer, newIdentifier,
				true);
    	
		return newContainer;
    }
    
    public static Structure getContainerStructure(Container container)
    {
    	Structure structure = (Structure) InodeFactory.getParentOfClass(container,Structure.class);
    	return structure;
    }

}

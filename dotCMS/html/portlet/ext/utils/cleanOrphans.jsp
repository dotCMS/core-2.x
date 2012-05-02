<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.beans.Host" %>
<%@ page import="com.dotmarketing.portlets.folders.model.Folder" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.portlets.htmlpages.model.HTMLPage" %>
<%@ page import="com.dotmarketing.beans.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%

	HashSet orphans = new HashSet ();
	HashSet withoutfolder = new HashSet ();
	List folders = InodeFactory.getInodesOfClass(Folder.class);

	Iterator it = folders.iterator();
	
	while (it.hasNext()) {
		Folder folder = (Folder)it.next();

		System.out.println("Checking folder path: " + folder.getPath() + " - " + folder.getInode());
%>
	Checking folder path: <%= folder.getPath() %> - <%= folder.getInode() %><br>
<%
		int i = 0;
		
		Host host = (Host) InodeFactory.getParentOfClass(folder, Host.class);
		Folder folder2 = folder;
		while (!InodeUtils.isSet(host.getInode()) && InodeUtils.isSet(folder2.getInode())) {
			folder2 = (Folder) InodeFactory.getParentOfClass(folder2, Folder.class);
			host = (Host) InodeFactory.getParentOfClass(folder2, Host.class);
			if (i++ == 20) break;
		}
		if (!InodeUtils.isSet(host.getInode())) {
			System.out.println("Orphan found!!");
%>
			Orphan found!!<br>
<%
			orphans.add(folder);
		
			System.out.println("Checking HTMLPages!!");
			//Removing HTMLPages
			List children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.htmlpages.model.HTMLPage.class);
			Iterator it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			System.out.println("Checking Containers!!");
			//Removing Containers
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.containers.model.Container.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			System.out.println("Checking Contentlets!!");
			//Removing Contentlet
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.contentlet.model.Contentlet.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			//Removing Links
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.links.model.Link.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			//Removing Templates
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.templates.model.Template.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			//Removing Files
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.files.model.File.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
			//Removing Workflow Messages
			children = InodeFactory.getChildrenClass(folder, com.dotmarketing.portlets.workflowmessages.model.WorkflowMessage.class);
			it2 = children.iterator();
			while (it2.hasNext()) {
				Object child = it2.next();
				if (child instanceof com.dotmarketing.beans.WebAsset) {
					WebAsset asset = (WebAsset) child;
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					orphans.add(id);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						orphans.add(version);
					}
				}
			}
		} else {
%>

			Host <%= host.getIdentifier() %> Found!! <br>
<%
		}
	}

%>
	Checking assets without folder<br>
<%		
		//checking HTMLPages
		List inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.htmlpages.model.HTMLPage.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		Iterator inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking containers
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.containers.model.Container.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking contentlets
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.contentlet.model.Contentlet.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking links
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.links.model.Link.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking templates
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.templates.model.Template.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking files
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.files.model.File.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
		
		//checking workflows
		inodes = InodeFactory.getInodesOfClassByCondition(com.dotmarketing.portlets.workflowmessages.model.WorkflowMessage.class, "(live = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " or working = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")");
		inodesIt = inodes.iterator();
		while (inodesIt.hasNext()) {
			Object child = inodesIt.next();
			if (child instanceof com.dotmarketing.beans.WebAsset) {
				WebAsset asset = (WebAsset) child;
				if (!InodeUtils.isSet(((Folder)InodeFactory.getParentOfClass(asset, Folder.class)).getInode())) {
					com.dotmarketing.beans.Identifier id = com.dotmarketing.factories.IdentifierFactory.getIdentifierByInode(asset);
					List allversions = InodeFactory.getChildrenClass(id, asset.getClass());
					Iterator childrenversions = allversions.iterator();
					while (childrenversions.hasNext()) {
						WebAsset version = (WebAsset) childrenversions.next();
						withoutfolder.add(version);
					}
				}
			}
		}
%>
Orpahns to removed:<br>
<%
	it = orphans.iterator();
	while (it.hasNext()) {
		Inode in = (Inode)it.next();
		InodeFactory.deleteInode (in);
%>
--------------------------<br>
Inode: <%= in.getInode() %><br>
Type: <%= in.getClass().getName() %><br>
--------------------------<br>
<%
	}
%>

Orphans without folder:<br>
<%
	it = withoutfolder.iterator();
	while (it.hasNext()) {
		Inode in = (Inode)it.next();
		InodeFactory.deleteInode (in);
%>
--------------------------<br>
Inode: <%= in.getInode() %><br>
Type: <%= in.getClass().getName() %><br>
--------------------------<br>
<%
	}
%>


done
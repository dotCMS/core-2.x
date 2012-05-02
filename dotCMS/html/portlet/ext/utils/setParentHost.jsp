<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.beans.Host" %>
<%@ page import="com.dotmarketing.portlets.folders.model.Folder" %>
<%@ page import="com.dotmarketing.util.*" %>
<%

	java.util.List folders = com.dotmarketing.factories.InodeFactory.getInodesOfClass(com.dotmarketing.portlets.folders.model.Folder.class);

	java.util.Iterator it = folders.iterator();
	
	while (it.hasNext()) {
		try {
			com.dotmarketing.portlets.folders.model.Folder folder = (com.dotmarketing.portlets.folders.model.Folder) it.next();
			System.out.println ("Setting host to folder: " + folder.getPath() + ":" + folder.getInode());
%>
			<%= "Setting host to folder: " + folder.getPath() + ":" + folder.getInode() + "<br>"%>
<%
			Host host = (Host) InodeFactory.getParentOfClass(folder, com.dotmarketing.beans.Host.class);
			int i = 0;
			com.dotmarketing.portlets.folders.model.Folder pfolder = folder;
			while (!InodeUtils.isSet(host.getInode())) {
				pfolder = (Folder) InodeFactory.getParentOfClass(pfolder, Folder.class);
				host = (Host) InodeFactory.getParentOfClass(pfolder, Host.class);
				if (i++ == 100) break;
			}
			if (InodeUtils.isSet(host.getIdentifier())) {
				folder.setHostId(host.getIdentifier());
				com.dotmarketing.factories.InodeFactory.saveInode (folder);
			} else {
%>
				<%= "orphan folder<br>" %>
<%
				
			}
%>
			<%= "Setted host: " + host.getInode() + " to folder: " + folder.getPath() + "<br>" %>
<%
			System.out.println ("Setted host: " + host.getInode() + " to folder: " + folder.getPath());
		} catch (Exception e) {
			System.out.println ("Error!!! " + e.getMessage());
%>
			
			<%="Error: " + e.getMessage () + "<br>"%>
<%
		}
	}
%>
done
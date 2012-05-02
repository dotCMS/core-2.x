<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.dotmarketing.common.db.DotConnect"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.dotmarketing.db.DbConnectionFactory"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.liferay.portal.model.User"%>
<%@page import="com.dotmarketing.business.CacheLocator"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Identifier.host_inode for contentlet.folder</title>
</head>
<body>
<%
User user= com.liferay.portal.util.PortalUtil.getUser(request);
boolean hasAdminRole = user!=null &&  com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user,
		                                     com.dotmarketing.business.APILocator.getRoleAPI().loadCMSAdminRole());
if(!hasAdminRole) {
	out.println("You don't have permissions to run this script!");
}
else {
	out.println("Fixing identifiers! <br/>");
    Connection conn=DbConnectionFactory.getConnection();
    conn.setAutoCommit(false);
    try {
	    DotConnect dc = new DotConnect();
		dc.setSQL("select identifier.inode, contentlet.title, folder.host_inode from"+ 
				  " contentlet join inode on contentlet.inode=inode.inode "+
				  " join identifier on inode.identifier=identifier.inode "+
				  " join folder on folder.inode=contentlet.folder "+
				  " where contentlet.folder <> 'SYSTEM_FOLDER' and identifier.host_inode='SYSTEM_HOST'");
		List<Map<String,Object>> results=(List<Map<String,Object>>)dc.loadResults(conn);
		int count = dc.getNumRows();
		out.println("<b>got "+count+" identifiers with host_inode=SYSTEM_HOST but contentlet.folder&lt;&gt;SYSTEM_FOLDER</b><br/>");
		int idx=0;
		for(Map<String,Object> res : results) {
			String ident = (String)res.get("inode");
			String title = (String)res.get("title");
			String host = (String)res.get("host_inode");
			
			out.println("("+(++idx)+"/"+count+") <b>fixing identity:</b>"+ident+" <b>title:</b>"+title+" <b>host_inode:</b>"+host+"... ");
		    
			dc.setSQL("update identifier set host_inode=? where inode=?");
			dc.addParam(host);
			dc.addParam(ident);
			dc.loadResult(conn);
			
			out.println("done! <br/>");
		}
		conn.commit();
    }
    catch(Exception ex) {
    	out.println("Error!! see details<br/>");
    	out.println("<pre>"); 
    	ex.printStackTrace(response.getWriter()); 
    	out.println("</pre>");
    	conn.rollback();
    }
    finally {
    	conn.close();
    }
}
%>
</body>
</html>
<%@ page language="java" import="com.dotmarketing.portlets.events.model.*,com.dotmarketing.portlets.events.struts.*,java.util.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	java.util.List list = com.dotmarketing.factories.InodeFactory.getInodesOfClass(Event.class);
%>
<html>
  <head>
    
    <title>Event List</title>
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
      <table border="0" width="550" align="center" cellpadding="0" cellspacing="5">
        <tr>
          <td colspan="2" align="center"><strong>Event List</strong></td>
        </tr>
<%
	Iterator it = list.iterator();
	while (it.hasNext()) {
		Event ev = (Event)it.next();
		if (ev.isRegistration()) {
%>
        <tr>
          <td align="center"><strong><%=ev.getTitle()%></strong></td>
          <td align="center"><a href="/cms/registerToEvent?event_inode=<%=ev.getInode()%>">Register to event</a></td>
        </tr>
<%
		}
	}
%>
	
      </table>
    </form>
  </body>
</html:html>

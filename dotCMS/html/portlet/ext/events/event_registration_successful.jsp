<%@ page language="java" import="com.dotmarketing.portlets.events.model.*,com.dotmarketing.portlets.events.struts.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	Event e = (Event)request.getAttribute("event");
	//TODO remove that page
	
%>
<html>
  <head>
    
    <title>Event Registration</title>
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
      <table border="0" width="550" align="center" cellpadding="0" cellspacing="5">
        <tr>
          <td colspan="3" align="center">Thank You! for your registration to the event: <%=e.getTitle()%></td>
        </tr>
        <tr>
          <td colspan="3" align="center">You will receive a confirmation email soon!</td>
        </tr>
      </table>
  </body>
</html:html>

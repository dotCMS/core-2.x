<%@ include file="/html/portlet/ext/usercomments/init.jsp" %>

<%
	String me = "User Messages View"; 
%>
<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr class="beta">
	<td width="">

		<font class="beta" size="2"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /></portlet:renderURL>">
		View All Comments</a></font>
	</td>
</tr>
</table>

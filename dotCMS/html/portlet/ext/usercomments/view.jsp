<%@ include file="/html/portlet/ext/usercomments/init.jsp" %>

<%@ page import="com.dotmarketing.util.Config" %>
<form id="fm" method="post">

	<table border="0" cellpadding="0" cellspacing="0" width="100%" align="center" bgcolor="#dddddd">
		<tr>
			<td width="30" align="center">
			<td width="120"><b>Date</b></td>
			<td><b>Subject</b></td>
			<td><b>Method</b></td>
		</tr>
		
<%
	java.util.List comments = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.USER_COMMENTS_VIEW);
	if(comments != null){
		java.util.Iterator commentIter = comments.iterator();

		for (int commentShowed = 0; commentShowed < Config.getIntProperty("MAX_ITEMS_MINIMIZED_VIEW") && commentIter.hasNext(); commentShowed++) {
			com.dotmarketing.portlets.user.model.UserComment comment = (com.dotmarketing.portlets.user.model.UserComment) commentIter.next();
%>
		<tr bgcolor="#eeeeee">
			<td>
				<a href="<portlet:renderURL><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /><portlet:param name="cmd" value="delete" /><portlet:param name="commentId" value="<%=String.valueOf(comment.getInode())%>" /></portlet:renderURL>"><span class="deleteIcon"></span></a>
			</td>
			<td><%= com.dotmarketing.util.UtilMethods.dateToHTMLDate(comment.getDate()) %>	<%= com.dotmarketing.util.UtilMethods.dateToHTMLTime(comment.getDate()) %> </td>
			<td><%= comment.getSubject() %></td>
			<td nowrap><%=comment.getMethod().trim()%></td>
		</tr>
		<tr bgcolor="#ffffff">
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td colspan="2"><pre><%=comment.getComment()%></pre></td>
		</tr>
<%
		} 
	}
%>
	</table>
	<table align="right">
		<tr>
			<td nowrap>
   				<a class="portletOption" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /></portlet:renderURL>">all</a>&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
	</table>
</form>
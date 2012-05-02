<%@ include file="/html/portlet/ext/usercomments/init.jsp" %>

<%@ page import="com.dotmarketing.util.Config" %>
<%

	int numrows = 0;
	if (request.getAttribute("numrows")!=null) {
		numrows = ((Integer) request.getAttribute("numrows")).intValue();
	}
	
	int pageNumber = 1;

	if (request.getParameter("pageNumber")!=null) {
		pageNumber = Integer.parseInt(request.getParameter("pageNumber")); 
	}
	int perPage = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
	int minIndex = (pageNumber - 1) * perPage;
	int maxIndex = perPage * pageNumber;
	
	com.liferay.portal.model.User viewUser = (com.liferay.portal.model.User) request.getAttribute("viewUser");
	com.dotmarketing.beans.UserProxy userProxyComment = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(viewUser,com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(), false);

%>

<form id="fm" method="post">
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="Viewing User Comments" />


<table border="0" cellpadding="2" cellspacing="1" width="90%" align="center" bgcolor="#dddddd">
	<tr>
		<td width="50">&nbsp;</td>
		<td width="130"><b>Date</b></td>
		<td><b>Subject</b></td>
		<td width="120"><b>Method</b></td>
		<td width="120"><b>Direction</b></td>
		<td width="120"><b>Type</b></td>
		<td width="120"><b>Author</b></td>
	</tr>
		
<%
	java.util.List comments = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.USER_COMMENTS_VIEW);
	if(comments != null){
	java.util.Iterator commentIter = comments.iterator();
	while(commentIter.hasNext()){
		com.dotmarketing.portlets.user.model.UserComment comment = (com.dotmarketing.portlets.user.model.UserComment) commentIter.next();%>
	<tr bgcolor="#eeeeee">
		<td nowrap align="center">
			<a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /><portlet:param name="cmd" value="delete" /><portlet:param name="commentId" value="<%=String.valueOf(comment.getInode())%>" /></portlet:renderURL>"><span class="deleteIcon"></span></a>
		</td>
		<td><%= com.dotmarketing.util.UtilMethods.dateToHTMLDate(comment.getDate()) %>	<%= com.dotmarketing.util.UtilMethods.dateToHTMLTime(comment.getDate()) %> </td>
		<td><%= comment.getSubject() %></td>
		<td nowrap><%=comment.getMethod()%></td>
		<td nowrap><%=(comment.getTypeComment()==null)?"":comment.getTypeComment()%></td>
		<td nowrap><%=comment.getType()%></td>
		<td nowrap>
			<% com.liferay.portal.model.User cUser = null;
				try {
					cUser = com.dotmarketing.business.APILocator.getUserAPI().loadUserById(comment.getCommentUserId(),com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(),false);
				}catch(Exception e){
					System.out.println("Unable to retieve user for view_user_comments");
					e.printStackTrace(System.out);
				}
				if(cUser == null){
				 	out.print("Unknown User");
				}else{
		 		 	out.print(cUser.getFullName());
		 		} %>
		</td>
	</tr>
	<tr bgcolor="#ffffff">
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td colspan=4><pre><%=comment.getComment()%></pre><BR>&nbsp;<BR></td>
	</tr>
<%
		} 
	}
%>
</table>
<table border="0" cellpadding="2" cellspacing="1" width="90%" align="center">
	<tr>
		<td colspan="2" align=left>
		<% if (minIndex != 0) { %>
			<img src="<%= SKIN_COMMON_IMG %>/02_left.gif">
			<font class="gamma" size="2">
			<B><a class="bg" href="<portlet:renderURL><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /><portlet:param name="pageNumber" value="<%= String.valueOf(pageNumber-1) %>" /><portlet:param name="user_comment_id" value="<%= viewUser.getUserId()%>" /></portlet:renderURL>">Previous</a></b>
			</font>
		<% } %>
		</td>
		<td colspan="2" align=right>
		
		<% if (maxIndex < numrows) { %>
			<font class="gamma" size="2">
			<B><a class="bg" href="<portlet:renderURL><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /><portlet:param name="pageNumber" value="<%= String.valueOf(pageNumber+1) %>" /><portlet:param name="user_comment_id" value="<%= viewUser.getUserId()%>" /></portlet:renderURL>">Next</a></b>
			</font>
			<img src="<%= SKIN_COMMON_IMG %>/02_right.gif">
		<% } %>
		</td>
	</tr>

	<% if (numrows ==0) { %>
	<tr>
		<td colspan="4" align=center>
		<font class="bg" size="2">This user has no comments.</font>
		</td>
	</tr>
	<% } %>
</table>
</form>
</liferay:box>
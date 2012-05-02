<%@ include file="/html/portlet/ext/usercomments/init.jsp" %>
<%
	com.liferay.portal.model.User viewUser = (com.liferay.portal.model.User) request.getAttribute("viewUser");
	com.dotmarketing.portlets.user.model.UserComment comment = (com.dotmarketing.portlets.user.model.UserComment) request.getAttribute("comment");
	int pageNumber = 1;
	if(request.getParameter("pageNumber")!=null){
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(NumberFormatException nfe){
		}
	}
%>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value='<%= LanguageUtil.get(pageContext, "user-click-detail") %>' />

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td colspan=2 align=right>
			<a href="<portlet:actionURL><portlet:param name="struts_action" value="/admin/edit_user_profile" /><portlet:param name="p_u_e_a" value="<%=viewUser.getEmailAddress()%>" /></portlet:actionURL>"><font class="bg">Back to <%= viewUser.getFirstName()%>'s Profile</font></a>
			<a href="<portlet:renderURL><portlet:param name="struts_action" value="/ext/usercomments/view_user_comments" /><portlet:param name="pageNumber" value="<%= String.valueOf(pageNumber) %>" /><portlet:param name="user_comment_id" value="<%= viewUser.getUserId() %>" /></portlet:renderURL>">Back to <%= viewUser.getFirstName()%>'s Comments</a>
	</td>
</tr>
<tr>
	<td colspan=2>
	<font class="gamma" size="2">Comments about <%= viewUser.getFullName() %></font>
	</td>
</tr>
<tr>
	<td><font class="beta" size="2">Method</font></td>
	<td><%= comment.getMethod() %></td>
</tr>
<tr>
	<td><font class="beta" size="2">Type</font></td>
	<td><%= comment.getType() %></td>
</tr>
<tr>
	<td><font class="beta" size="2">Subject</font></td>
	<td><%= comment.getSubject() %></td>
</tr>
<tr>
	<td><font class="beta" size="2">Comment</font></td>
	<td><%= comment.getComment() %></td>
</tr>
<tr>
	<td><font class="beta" size="2">Date</font></td>
	<td><%= com.dotmarketing.util.UtilMethods.dateToHTMLDate(comment.getDate()) %>	<%= com.dotmarketing.util.UtilMethods.dateToHTMLTime(comment.getDate()) %></td>
</tr>
<tr>
	<td><font class="beta" size="2">Author</font></td>
	<td><% com.liferay.portal.model.User cUser = null;
								try {
									cUser = com.dotmarketing.business.APILocator.getUserAPI().loadUserById(comment.getCommentUserId(),com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(),false);
								}catch(Exception e){
									System.out.println("Unable to retrieve user for view_user_comments");
									e.printStackTrace(System.out);
								}
								if(cUser == null){
								 out.print("Unknown User");
								}else{
						 		 out.print(cUser.getFullName());
						 		}
						 %></td>
</tr>
</table>
</liferay:box>

<p>

<form id=fm2 name=fm2 method="post">
	<input type="hidden" name="p_u_e_a" value="">
</form>
<script language="javascript">
 function editUser(userId){
 	document.fm2.p_u_e_a.value=userId;
 	submitForm(document.fm2, '/c/admin/edit_user_profile');
 }
</script>


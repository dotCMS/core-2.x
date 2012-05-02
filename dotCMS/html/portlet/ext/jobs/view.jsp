<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td align="center">
		<a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-jobs") %></a>
	<td>
</tr>
<tr>
	<td align="center">
		<a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-resumes") %></a></font>
	</td>
</tr>
<tr>
	<td align="center">
		<a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-searchfirms") %></a></font>
	</td>
</tr>
</table>
<%@ include file="/html/portlet/ext/organization/init.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr class="beta">
	<td width="">
		<font class="beta" size="2"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/organization/view_organizations" /></portlet:renderURL>">View all Schools and Systems</a></font>
	|	<font class="beta" size="2"><a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/organization/edit_organization" /><portlet:param name="system" value="true" /><portlet:param name="cmd" value="edit" /></portlet:actionURL>">Add New System</a></font>
	|	<font class="beta" size="2"><a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/organization/edit_organization" /><portlet:param name="system" value="false" /><portlet:param name="cmd" value="edit" /></portlet:actionURL>">Add New School</a></font>
	</td>
</tr>
</table>

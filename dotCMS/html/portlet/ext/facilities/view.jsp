<%@ include file="/html/portlet/ext/facilities/init.jsp" %>


<table border="0" cellpadding="4" cellspacing="0" width="100%">
<Tr>
	<td align="center">
	<font class="gamma" size="2">
	<a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>">
	View Facilities</a>
	</font>
	</td>
</tr>
<Tr>
	<td align="center">
	<font class="gamma" size="2">
	<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /></portlet:actionURL>">
	Add New Facility</a>
	</font>
	</td>
</tr>
</table>

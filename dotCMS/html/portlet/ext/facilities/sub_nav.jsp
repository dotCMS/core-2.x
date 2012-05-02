<%@ include file="/html/portlet/ext/facilities/init.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr class="beta">
	<td width="">
		<font class="beta" size="2"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "View All Facilities") %></a></font>
		|   <font class="beta" size="2"><a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /></portlet:actionURL>">
		Add New Facility</a></font>
		|
		<font class="beta" size="2"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="resetFilters" value="true" /></portlet:renderURL>">
		View All Events</a></font>
		
	</td>
</tr>
</table>

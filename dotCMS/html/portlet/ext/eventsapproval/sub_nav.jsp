<%@ include file="/html/portlet/ext/eventsapproval/init.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr class="beta">
	<td width="">

		<font class="beta" size="2"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="resetFilters" value="true" /></portlet:renderURL>">
		View Events Calendar</a></font>
		|   <font class="beta" size="2"><a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="edit" /></portlet:actionURL>">
	    Add New Event Request</a></font>
	</td>
</tr>
</table>

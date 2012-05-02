<%@ page language="java" import="com.dotmarketing.portlets.events.model.*,com.dotmarketing.util.*"  %>
<%@ include file="/html/portlet/ext/events/init.jsp" %>

<% 
	Event event = (Event)request.getAttribute(com.dotmarketing.util.WebKeys.EVENT_EDIT);
	List registrations = (List)request.getAttribute(com.dotmarketing.util.WebKeys.EVENT_REGISTRATIONS);

%>


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="Event Registrations" />

<table border="0" align="center" cellspacing="0" cellpadding="0" width="90%">
	<tr>
		<td colspan="13" align="center"><strong>Event: <%=event.getTitle()%></strong></td>
	</tr>
	<tr>
		<td colspan="13" height="5"></td>
	</tr>
	<tr class="beta">
		<td width="1" class="beta"></td>
		<td align="center">&nbsp;<strong>Name</strong>&nbsp;</td>
		<td width="1" class="beta"></td>
		<td align="center">&nbsp;<strong>Comments</strong>&nbsp;</td>
		<td width="1" class="beta"></td>
		<td align="center">&nbsp;<strong>Email Address</strong>&nbsp;</td>
		<td width="1" class="beta"></td>
	</tr>
<%
	if (registrations.size() == 0) { 
%>
	<tr class="beta">
		<td colspan="12" height="1" class="beta"></td>
	</tr>
	<tr class="gamma">
		<td width="1" class="beta"></td>
		<td colspan="11" align="center">There is no registrations to this event</td>
		<td width="1" class="beta"></td>
	</tr>
	<tr class="beta">
		<td colspan="13" height="1"></td>
	</tr>
<%
	} else {
		Iterator it = registrations.iterator();
		while (it.hasNext()) {
			EventRegistration reg = (EventRegistration) it.next();
%>
	<tr class="beta">
		<td colspan="12" height="1" class="beta"></td>
	</tr>
	<tr class="gamma">
		<td width="1" class="beta"></td>
		<td class="gamma">&nbsp;<%=reg.getFullName()%></td>
		<td width="1" class="beta"></td>
		<td class="gamma">&nbsp;<%=reg.getComments().trim().equals("")?"No comments":reg.getComments()%>&nbsp;</td>
		<td width="1" class="beta"></td>
		<td class="gamma">&nbsp;<a class="beta" href="mailto:<%=reg.getEmail()%>?subject=<%=event.getTitle()%>"><%=reg.getEmail()%></a>&nbsp;</td>
		<td width="1" class="beta"></td>
	</tr>
	<tr class="beta">
		<td colspan="12" height="1"></td>
	</tr>
<%
		}
	}
%>
	<tr>
		<td colspan="12" align="right"><strong><a href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="edit" /><portlet:param name="inode" value="<%=event.getInode()%>" /></portlet:actionURL>" class="beta">Go to the event: <%=event.getTitle()%></a></strong></td>
	</tr>
</table>
</liferay:box>

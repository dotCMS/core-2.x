<%@ page language="java" import="java.util.*,com.dotmarketing.portlets.events.model.*,com.dotmarketing.util.*"  %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.liferay.portal.model.User" %>
<%@ page import="com.dotmarketing.portlets.events.model.Recurance" %>
<%@ page import="com.dotmarketing.portlets.events.factories.EventFactory" %>
<%@ page import="com.dotmarketing.util.InodeUtils" %>

<%@ include file="/html/portlet/ext/eventsapproval/init.jsp" %>

<% 
	List events = (List)request.getAttribute("events");
%>

<script language="javascript">
	function doApproveEvent(eventInode){
		window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/eventsapproval/edit_event" /><portlet:param name="cmd" value="approve" /></portlet:actionURL>&eventInode=' + eventInode;
	}

	function doDisapproveEvent(eventInode){
		window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/eventsapproval/edit_event" /><portlet:param name="cmd" value="disapprove" /></portlet:actionURL>&eventInode=' + eventInode;
	}
</script>

<br/>
<table cellspacing="0" cellpadding="0" align="center" border="0" class="listingTable" width="100%">
	<tr height="20" class="header">
		<td width="40%">&nbsp; Event Title</td>
		<td width="10%"align="center" nowrap>Facility</td>
		<td width="10%"align="center" nowrap>Room</td>		
		<td nowrap width="20%" align="left">Requested By</td>
		<td width="10%" align="center" nowrap>Approve</td>
		<td width="10%" align="center" nowrap >Disapp</td>
		<td width="10%" align="center" nowrap>Conflict</td>
	</tr>
	<%
		boolean conflictsDetected = false;
		int x = 1;
		Iterator i = events.iterator();
		while ( i.hasNext() ) {
			if (x++ > Config.getIntProperty("MAX_ITEMS_MINIMIZED_VIEW")) break;
	%>
		<%
			Map map = (Map) i.next();
			Event e = (Event)InodeFactory.getInode((String)map.get("inode"), Event.class);
			Facility fac = (Facility)InodeFactory.getParentOfClass(e, Facility.class);
			
			String str_style =(x % 2 == 0 ? "class=\"alternate_1\"" : "class=\"alternate_2\"");

			String fullUserName="";
			if(e.getUserId() != null){
				User eventUser = com.dotmarketing.business.APILocator.getUserAPI().loadUserById(e.getUserId(),com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(),false);
				fullUserName = eventUser.getFullName();
			}
			Recurance r = (Recurance)InodeFactory.getChildOfClass(e, Recurance.class);
			List conflicts;
			if (InodeUtils.isSet(r.getInode()))
				conflicts = EventFactory.findConflicts(e, r, fac);
			else
				conflicts = EventFactory.findConflicts(e, fac);
				
			if (conflicts.size() > 0)
				conflictsDetected = true;
			
			if(e.getTitle() != null && !e.getTitle().equals("") && !e.getTitle().equals("null")){
		%>
			
	<TR height="20" <%=str_style%> >
		<td>&nbsp;
			<a  href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							<portlet:param name="struts_action" value="/ext/events/edit_event" />
							<portlet:param name="cmd" value="edit" />
							<portlet:param name="inode" value="<%=e.getInode()%>" />
						  </portlet:actionURL>"
				<%=(conflicts.size() > 0)?"style='color=red'":""%>><%=e.getTitle()%></a>
		</td>
		<td nowrap><%=(InodeUtils.isSet(fac.getInode()))?fac.getFacilityName():"None / Off-Campus"%></td>
		<td nowrap><%if(e.getLocation() != null && e.getLocation().trim() != ""){%><%=e.getLocation()%><%}%></td>
		<td align="left" nowrap="nowrap"><%=fullUserName%></td>
		<td align="center"><%if (conflicts == null || conflicts.size() == 0) {%><a href="javascript:doApproveEvent('<%=e.getInode()%>')">Approve</a><% } else { %>Approve<% } %></td>
		<td align="center"><a href="javascript:doDisapproveEvent('<%=e.getInode()%>')">Disapprove</a></td>
		<td align="center" nowrap>
			<% 	
				if (conflicts != null && conflicts.size() > 0) {
					Event confEvent = (Event)conflicts.get(0);
					
			%>
			 <a href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/events/edit_event" />
				<portlet:param name="cmd" value="edit" />
				<portlet:param name="inode" value="<%=confEvent.getInode()%>" />
			  </portlet:actionURL>"><%=confEvent.getTitle()%></a>			
			<%
					
					} else {
			%>
			None
			<%
				}
			%>
		</td>
	</TR>
	<%}
			}%>
	<%if(events.size() ==0){%>
	<tr height="100%">
		<td colspan=4 valign="top" align="center">There are no events to approve</td>
	</tr>
	<%}%>
	<%
		if (conflictsDetected) {
	%>
	<tr height="100%">
		<td colspan=7 align="left"><font color="red">* Red Events are in conflict, can't be approved.</font></td>
	</tr>
	<%
		}
	%>
	
</table>
<br/>
<table height="100%" width="100%">
	<tr height="100%" width="100%">
		<td align="right"><a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/eventsapproval/view_events" /></portlet:renderURL>">view events for approval</a> | 
		<a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="resetFilters" value="true" /></portlet:renderURL>">view calendar</a> &nbsp;</td>
	</tr>
</table>

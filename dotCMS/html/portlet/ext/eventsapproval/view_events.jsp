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

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="Events to Approve" />


<table cellspacing="0" cellpadding="2" align="center" border="0" class="listingTable" width="100%">
	<TR  height="20" class="header">
		<td width="250"> &nbsp;&nbsp;Event Title</td>
		<td nowrap>Facility</td>
		<td nowrap>Room</td>
		<td nowrap align="center" >On Web <br/>Calendar</td>
		<td nowrap align="left" >Requested By</td>
		<td align="center" width="250">Date/Time</td>
		<td align="center" nowrap >Approve</td>
		<td align="center" nowrap >Disapp</td>
		<td align="center" nowrap >Conflict</td>
	</tr>
	<%
		boolean conflictsDetected = false;
		Iterator i = events.iterator();
		
		int counter=0;
		
		while ( i.hasNext() ) {%>
		<%
			Map map = (Map) i.next();
			Event e = (Event)InodeFactory.getInode((String)map.get("inode"), Event.class);
			Facility fac = (Facility)InodeFactory.getParentOfClass(e, Facility.class);
            String fullUserName= "";
			if(e.getUserId() != null){
				User eventUser = com.dotmarketing.business.APILocator.getUserAPI().loadUserById(e.getUserId(),com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(),false);
				fullUserName = eventUser.getFullName();
			}
			Recurance r = (Recurance)InodeFactory.getChildOfClass(e, Recurance.class);
			List conflicts;
			
			String str_style =(counter % 2 == 0 ? "class=\"alternate_1\"" : "class=\"alternate_2\"");
            counter++;

			if (InodeUtils.isSet(r.getInode()))
				conflicts = EventFactory.findConflicts(e, r, fac);
			else
				conflicts = EventFactory.findConflicts(e, fac);
			if (conflicts.size() > 0)
				conflictsDetected = true;
		%>
			
	<TR height="22" <%=str_style %> >
		<td > &nbsp;
			<a  href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							<portlet:param name="struts_action" value="/ext/events/edit_event" />
							<portlet:param name="cmd" value="edit" />
							<portlet:param name="inode" value="<%=e.getInode()%>" />
						  </portlet:actionURL>"
				<%=(conflicts.size() > 0)?"style='color=red'":""%>><%=e.getTitle()%></a>
		</td>
		<td nowrap="nowrap"><%=(InodeUtils.isSet(fac.getInode()))?fac.getFacilityName():"None / Off-Campus"%></td>
		<td nowrap><%if(e.getLocation() != null && e.getLocation().trim() != ""){%><%=e.getLocation()%><%}%></td>
		<td align="center" nowrap="nowrap"><%=(e.getShowPublic()) ? "Yes" : "No"%></td>
		<td align="left" nowrap="nowrap"><%=fullUserName%></td>
		<td align="left">
			<%if (InodeUtils.isSet(r.getInode())) { %>
			It occurs: <%=UtilHTML.recuranceToString(e, r)%>
			<%} else {%>
				<%if (e.isTimeTBD()) {%>
			<%=UtilMethods.dateToHTMLDateRange(e.getSetupDate(), e.getBreakDate(), GregorianCalendar.getInstance().getTimeZone()) + " at time TBD"%>
				<%} else {%>
			<%=UtilMethods.dateToHTMLDateTimeRange(e.getSetupDate(), e.getBreakDate(), GregorianCalendar.getInstance().getTimeZone())%>
				<%} %>
			<%}%>
		</td>
		<td align="center" nowrap="nowrap"><%if (conflicts == null || conflicts.size() == 0) {%><a href="javascript:doApproveEvent('<%=e.getInode()%>')">Approve</a><% } else { %>Approve<% } %></td>
		<td align="center" nowrap="nowrap"><a href="javascript:doDisapproveEvent('<%=e.getInode()%>')">Disapprove</a></td>
		<td align="center" nowrap="nowrap">
			<% 	
				if (conflicts != null && conflicts.size() > 0) {
					Event confEvent = (Event)conflicts.get(0);
			%>
			<A  href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
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
	<%}%>
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

</liferay:box>





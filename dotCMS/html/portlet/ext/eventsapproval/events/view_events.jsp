<%@ page language="java" import="java.util.*,com.dotmarketing.portlets.events.model.*,com.dotmarketing.util.*,com.dotmarketing.portlets.categories.model.*"  %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.business.APILocator" %>
<%@ page import="com.dotmarketing.portlets.categories.business.CategoryAPI" %>
<%@ page import="com.dotmarketing.portlets.categories.model.Category" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ include file="/html/portlet/ext/events/init.jsp" %>
<%@ page import="com.dotmarketing.util.InodeUtils" %>

<% 
	CategoryAPI catAPI = APILocator.getCategoryAPI();

	GregorianCalendar cal = (GregorianCalendar)request.getAttribute("calendar"); 
    
	List events = (List)request.getAttribute("events");
	TimeZone tz = cal.getTimeZone();
	
	boolean isAdmin = ((Boolean)request.getAttribute("isAdmin")).booleanValue();
	String userId = (String)request.getAttribute("userId");

	List facilities = InodeFactory.getInodesOfClass(Facility.class, "facility_name");
	String facilityInode = ((String)request.getAttribute("facilityInode")); 

	com.dotmarketing.portlets.entities.model.Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity("Event");
	java.util.List<com.dotmarketing.portlets.categories.model.Category> cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);

	String selectedCategories = (String) request.getAttribute("selectedCategories");
	if(selectedCategories==null) selectedCategories="";

%>

<script>


	function doSelectCategory(){
		var selectedCategories = "";
		
		<%for(Category child : cats){%>
			var ele = document.getElementById("category<%=child.getInode()%>");
			for(i=0;i<ele.length;i++){
				if(ele[i].selected && i >0){
					selectedCategories+=ele[i].value +",";
				}
			}
		<%}%>


		ele = document.getElementById("facilitySelect");
		facilityInode = ele[ele.selectedIndex].value;
		window.location = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="cmd" value="view" /><portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/><portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" /></portlet:renderURL>&selectedCategories=' + selectedCategories + '&facilityInode=' + facilityInode;
	}
</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="View Events" />

<table border="0" align="center" width="100%" style="border-top:1px solid #ccc; border-bottom:1px solid #ccc;">
	<tr><td colspan="5">&nbsp;</td></tr>
	<tr>
	<%-- Lots of jumping around --%>
		<%cal.add(cal.YEAR, -1);%>
		<td width="110" align="center">
		<a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
		<portlet:param name="struts_action" value="/ext/events/view_events" />
		<portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/>
		<portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" />
		</portlet:renderURL>"><b>&laquo; Previous Year</b>
		</a>
		</TD>
		
		<%cal.add(cal.YEAR, 1);%>
		<%cal.add(cal.MONTH, -1);%>
		
		<TD width="110" align="center">
		<a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
		<portlet:param name="struts_action" value="/ext/events/view_events" />
		<portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/>
		<portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" />
		</portlet:renderURL>"><b>&lsaquo; Previous Month</b>
		</a>
		</td>
		
		<%cal.add(cal.MONTH, 1);%>
		
		<td nowrap align="center">
		<b>Events for <%=com.dotmarketing.util.UtilMethods.getMonthName(((int) cal.get(cal.MONTH)) + 1)%> <%=cal.get(cal.YEAR)%>
		</b>
		</td>	
			
		<%cal.add(cal.MONTH, 1);%>
		
		<td width="110" align="center">
		<a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
		<portlet:param name="struts_action" value="/ext/events/view_events" />
		<portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/>
		<portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" />
		</portlet:renderURL>"><b>Next Month &rsaquo;</b></a>
		</TD>
		
		<%cal.add(cal.MONTH, -1);%>
		<%cal.add(cal.YEAR, 1);%>
		
		<td width="110" align="center"><a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
		<portlet:param name="struts_action" value="/ext/events/view_events" />
		<portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/>
		<portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" />
		</portlet:renderURL>"><b>Next Year &raquo;</b></a>
		</td>
		
		<%cal.add(cal.YEAR, -1);%>
		<%cal.set(cal.DAY_OF_MONTH, 1);%>
		<%cal.add(cal.DAY_OF_MONTH, (1 - cal.get(cal.DAY_OF_WEEK)));%>
	</tr>
	<tr><td colspan="5">&nbsp;</td></tr>
</table>

<br/>

<%-- Category --%>
<table   cellspacing="0" cellpadding="0" align="center" border="0" >
	<form id="viewForm" name="viewForm" method="get">
	<tr>
		<TD width="50%" valign="bottom">
			<table cellpadding="0" cellspacing="0" border="0" class='listingTable'> 
				<%


					Iterator catsIter = cats.iterator();
					while (catsIter.hasNext()) {
						com.dotmarketing.portlets.categories.model.Category category = (com.dotmarketing.portlets.categories.model.Category) catsIter.next();
						java.util.List<com.dotmarketing.portlets.categories.model.Category> children = catAPI.getChildren(category,user,false);
						if (children.size() > 1) {%>
							<tr>
								<td valign="top" align="right" nowrap="nowrap">
									<font class="bg" size="2"><%= category.getCategoryName()%>:</font>
								</td>
								<td>
									<select name="category<%=category.getInode()%>"  id="category<%=category.getInode()%>" style="width:200px;">
										<option value="">All</option>
										<%for(Category child : children){%>
											<option value="<%=child.getInode()%>" <%if(selectedCategories.indexOf(child.getInode() +",") > -1){%>selected="selected"<%}%>><%=child.getCategoryName()%></option>
										<%}%>
									</select>
								</td>
							</tr>
						<%}%>									
					 <%}%>	
			</table>
		</td>
		
		<td width="50%" valign="top" align="right" nowrap="nowrap">
			Search by Facility: 
				<select name="facility" id="facilitySelect"  size="1" style="width:200px">
					<option value="-1">All</option>
				<%
					Iterator it = facilities.iterator();
					while (it.hasNext()) {
						Facility fac = (Facility)it.next();
				%>
					<option value="<%=fac.getInode()%>" <%=facilityInode.equalsIgnoreCase(fac.getInode())?"selected":""%> ><%=fac.getFacilityName()%></option>
				<%
					}
				%>
				</select>
		
                <button dojoType="dijit.form.Button" onClick="doSelectCategory()" >Filter</button>
                
		</TD>
	</tr>
	</form>
</table>

<br/>

<%-- / Category --%>
<table cellspacing="0" cellpadding="0" align="center" border="0"  class="listingTable">
	<tr  height="20" class="header" >
		<td width="40%" colspan="2">&nbsp;&nbsp;Event</td>
		<td nowrap width="10%" align="center">Show on<br>&nbsp;&nbsp;Web Calendar&nbsp;&nbsp;</td>
		<td nowrap  width="10%" align="center">Registrations</td>
		<td nowrap  width="15%" align="center">Facility</td>
		<td align="left"  width="25%" nowrap>&nbsp; &nbsp;Date and Time</td>
	</tr>
	<%
		String day = "no";
		Iterator i = events.iterator();
		int x = 1; 
	
		String str_style = "class=\"alternate_2\"";
		while ( i.hasNext() ) {
	%>
		<%
			Event e = (Event) i.next();
			List registrations = com.dotmarketing.factories.InodeFactory.getChildrenClass(e, EventRegistration.class);
			int registrationNumber = registrations.size();
			Facility fac = (Facility)InodeFactory.getParentOfClass(e, Facility.class);
			
			if(! day.equals(com.dotmarketing.util.UtilMethods.dateToPrettyHTMLDate(e.getStartDate(), tz))){
				str_style = str_style.equals("class=\"alternate_1\"")?"class=\"alternate_2\"":"class=\"alternate_1\"";
		%>
		    
			<%day=com.dotmarketing.util.UtilMethods.dateToPrettyHTMLDate(e.getStartDate(), tz); %>
						
			<tr <%=str_style%>>
				<td colspan="6">&nbsp;&nbsp;<span><%=day%></span></td>
			</tr>
		<%}%>
		<tr <%=str_style %>>
			<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
			<td>
		
				<% if (isAdmin || userId.equals(e.getUserId())) { %>
				<a style="<% 	
						if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_DISAPPROVED_STATUS) {
							out.print("color: red;");
						} else if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS) {
							out.print("color: #2C548D;");
						}else if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS){
							out.print("color: #00CC66;");
						}else if (!isAdmin && e.getUserId().equals(userId)) {
							out.print("color: #00CC66;");
						}
				%>" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							<portlet:param name="struts_action" value="/ext/events/edit_event" />
							<portlet:param name="cmd" value="edit" />
							<portlet:param name="inode" value="<%=e.getInode()%>" />
						  </portlet:actionURL>"> 
				<% } else { %>
					<span style="<% 	
						if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_DISAPPROVED_STATUS) { 
							out.print("color: red;");
						} else if (e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_WAITING_APPROVAL_STATUS) {
							out.print("color: #2C548D;");
						}else if(e.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_APPROVED_STATUS){
							out.print("color: #00CC66;");
						}
					%>">
				<% } %>
					<%=e.getTitle()%>
				<% if (isAdmin || userId.equals(e.getUserId())) { %>
					</a>
				<% } else { %> 
					</span>   
				<% } %>
			</td>
			<td align="center" nowrap="nowrap"><%=(e.getShowPublic()) ? "Yes" : "No"%></td>
			<td align="center" nowrap="nowrap"><%=(e.isRegistration()) ? "Yes" : "No"%>
				<% if ((isAdmin || userId.equals(e.getUserId())) && e.isRegistration()) { %>
				<%if(registrationNumber > 0){%>
				<a href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/events/edit_event" />
				<portlet:param name="cmd" value="showRegistrations" />
				<portlet:param name="inode" value="<%=e.getInode()%>" />
				</portlet:actionURL>">Show<%}%>(<%=registrationNumber%>)
				</a>
				<% } %>
			</td>
			<td align="center" nowrap ><%=(InodeUtils.isSet(fac.getInode()))?fac.getFacilityName():"None / Off-Campus"%></td>
			<td nowrap align="left" nowrap="nowrap">    &nbsp; &nbsp;
				<% if (!e.isTimeTBD()) { %>
					<%=UtilMethods.dateToHTMLDateTimeRange(e.getSetupDate(), e.getBreakDate(), tz)%>
				<% } else { %>
					<%=UtilMethods.dateToHTMLDateRange(e.getSetupDate(), e.getBreakDate(), tz) + " at time TBD"%>
				<% } %>
			</td>   
		</tr>
	<%}%>
	<%if(events.size() ==0){%>
	<tr height="100%">
		<td colspan="4" valign="top" align="center">There are no events to show</td>
	</tr>
	<%}%>
	<tr height="100%">
		<td colspan="7" align="right" style="border-bottom:1px solid #ccc;">&nbsp;</td>
	</tr>

	<tr height="100%">
		<td colspan="7" align="left"><strong>Approval Legend:</strong></td>
	</tr>
	<tr height="100%">
		<td colspan="7" align="left"><span style="color:#00CC66">* Green Events are your approved events.</span></td>
	</tr>
	<tr height="100%">
		<td colspan="7" align="left"><span style="color:#2C548D">* Blue Events are waiting for approval.</span></td>
	</tr>
	<tr height="100%">
		<td colspan="7" align="left"><span style="color:red">* Red Events are disapproved.</span></td>
	</tr>
</table>

</liferay:box>
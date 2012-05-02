<%@ page language="java" import="java.util.*,com.dotmarketing.portlets.events.model.*,com.dotmarketing.util.*,com.dotmarketing.portlets.categories.model.*"  %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.business.APILocator" %>
<%@ page import="com.dotmarketing.portlets.categories.business.CategoryAPI" %>
<%@ page import="com.dotmarketing.portlets.categories.model.Category" %>
<%@ include file="/html/portlet/ext/events/init.jsp" %>
<%

	CategoryAPI catAPI = APILocator.getCategoryAPI();

    //Categories filtered
	com.dotmarketing.portlets.entities.model.Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity("Event");
	java.util.List<com.dotmarketing.portlets.categories.model.Category> cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);

	//Faclity filtering
	List facilities = InodeFactory.getInodesOfClass(Facility.class, "facility_name");
	String facilityInode = ((String)request.getAttribute("facilityInode")); 

	GregorianCalendar cal = new GregorianCalendar ();
	
	String selectedCategories = (String) request.getAttribute("selectedCategories");
	if(selectedCategories==null) selectedCategories="";
%>

<script>


	function doSelectCategory(){
		var selectedCategories = "";
		
		<%for(Category child : cats){
		java.util.List<com.dotmarketing.portlets.categories.model.Category> children = catAPI.getChildren(child, user, false);
		if (children.size() > 1) {%>
			var ele = document.getElementById("category<%=child.getInode()%>");
			for(i=0;i<ele.length;i++){
				if(ele[i].selected && i >0){
					selectedCategories+=ele[i].value +",";
				}
			}
			<%}%>
		<%}%>


		ele = document.getElementById("facilitySelect");
		facilityInode = ele[ele.selectedIndex].value;
		window.location = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="cmd" value="view" /><portlet:param name="month" value="<%=Integer.toString(cal.get(cal.MONTH))%>"/><portlet:param name="year" value="<%=Integer.toString(cal.get(cal.YEAR))%>" /></portlet:renderURL>&selectedCategories=' + selectedCategories + '&facilityInode=' + facilityInode;
	}
</script>

<table cellspacing="1" cellpadding="3" align="center" border=0 bgcolor="#FFFFFF">
	<form id="viewForm" name="viewForm" method="get">

				<%


					Iterator catsIter = cats.iterator();
					while (catsIter.hasNext()) {
						com.dotmarketing.portlets.categories.model.Category category = (com.dotmarketing.portlets.categories.model.Category) catsIter.next();
						java.util.List<com.dotmarketing.portlets.categories.model.Category> children = catAPI.getChildren(category, user, false);
						if (children.size() > 1) {%>
							<tr>
								<td valign="top" align="right" nowrap="nowrap">
									<font class="bg" size="2"><%= category.getCategoryName()%>:</font>
								</td>
								<td>
									<select name="category<%=category.getInode()%>" class="beta" id="category<%=category.getInode()%>" style="width:200px;">
										<option value="">All</option>
										<%for(Category child : children){%>
											<option value="<%=child.getInode()%>" <%if(selectedCategories.indexOf(child.getInode() +",") > -1){%>selected="selected"<%}%>><%=child.getCategoryName()%></option>
										<%}%>
									</select>
								</td>
							</tr>
						<%}%>									
					 <%}%>	
			<tr>
			<td>
				Search by Facility: 
			</td>
			<td>
				<select name="facility" id="facilitySelect" class="beta" size="1" style="width:200px">
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
		
                <button dojoType="dijit.form.Button" onclick="doSelectCategory()" >Show Events</button>

		</TD>
	</tr>
	<tr>
		<td colspan=2>&nbsp;
	</td>


	</tr>
	<tr>
		<td colspan=2 align="center">
            <button dojoType="dijit.form.Button" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="edit" /></portlet:actionURL>';" >Create New Event</button>
		</td>

	</tr>
	</form>
</table>
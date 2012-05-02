<%@ include file="/html/portlet/ext/facilities/init.jsp" %>
<%@ page import="com.dotmarketing.util.Config" %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/facilities/view_facilities"});
	
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	String query = (request.getParameter("query")!=null) ? request.getParameter("query") : (String) session.getAttribute(com.dotmarketing.util.WebKeys.CONTAINER_QUERY);
%>
<script language="Javascript">
function resetSearch() {
	form = document.getElementById('fm');
	form.resetQuery.value = "true";
	form.query.value = '';
	form.action = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>';
	submitForm(form);
}
function submitfm() {
	form = document.getElementById('fm');
	form.action = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>';
	submitForm(form);
}
function submitfmForm(cmd) {
	form = document.getElementById('fm_publish');
	form.cmd.value = cmd;
	form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /></portlet:actionURL>';
	submitForm(form);
}
function addAsset() {
	window.location.href = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /><portlet:param name="cmd" value="edit" /><portlet:param name="referer" value="<%=referer%>" /></portlet:actionURL>';
}

function submitfmDelete() {
	if(confirm("Are you sure you want to delete this facilities (this cannot be undone)?"))
	{
		form = document.getElementById('fm_publish');
		form.cmd.value = 'deleteselected';
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /><portlet:param name="cmd" value="deleteselected" /></portlet:actionURL>';
		submitForm(form);
	}
}
function checkAll(check) {
	form = document.getElementById("fm_publish");
	selectBox = form.delInode;
	for (i=0;i<selectBox.length;i++) {
		selectBox[i].checked = check;
	}
	togglePublish();
}
function togglePublish(){
	var cbArray = document.getElementsByName("delInode");
	var cbCount = cbArray.length;
	for(i = 0;i<cbCount;i++){
		if (cbArray[i].checked) {
			dijit.byId("deleteBtn").setAttribute("disabled", false); 
			break;
		}
			dijit.byId("deleteBtn").setAttribute("disabled", true); 
		}
}
</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value='<%= LanguageUtil.get(pageContext, "View All Facilities") %>' />

	<table border="0" cellpadding="0" cellspacing="0" width="100%" >
		<tr> 
			<td align="right">   
			<form id="fm" method="post">

			<input type="hidden" name="resetQuery" value="">
			<input type="text" name="query" value="<%= com.dotmarketing.util.UtilMethods.isSet(query) ? query : "" %>">
            <button dojoType="dijit.form.Button" onClick="submitfm()">Search</button>
			<button dojoType="dijit.form.Button" onClick="resetSearch()">Reset</button>
            </td> 
		</tr> 
		<tr>
			<td><font class="beta" size="2">
			<a class="gamma" href="javascript:checkAll(true)">Check all</a> | 
			<a class="gamma" href="javascript:checkAll(false)">Uncheck all</a>
			</font>
			</td>
			</form>
		</tr>
		<tr>
			<td>
				<form id="fm_publish" method="post">
				<% java.util.List facilities = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.FACILITIES_LIST); %>
				<input type="hidden" name="referer" value="<%=referer%>">
				<input type="hidden" name="cmd" id="cmd" value="reorder">
				<input type="hidden" name="count" id="count" value="<%=facilities.size()%>">
				<table border="0" cellpadding="0" cellspacing="0" width="100%" class="listingTable">
					<tr class="header">
						<Td width="5">&nbsp;</td>
						<Td width="150" nowrap>Facility Name</td>
						<Td  width="160">Facility Description</td>
						<Td width="75" align="center">Calendar</td>
						<Td width="80" align="center">Sort Order</td>
						<Td align="center" width="60">Active</td>
					</tr>

					<% 
						for (int k=0;k<facilities.size();k++) { 
							Facility facility = (Facility) facilities.get(k);				
							
							String str_style = "";
							if ((k%2)==0) {
								str_style = "class=\"alternate_1\"";
							}
							else{
								str_style = "class=\"alternate_2\"";
							}
							
							String description = facility.getFacilityDescription();
							if (description!=null && description.length()>50) {
								description = description.substring(0,50) + "...";
							}

						%>
						<tr <%=str_style%>>
							<td >
								<font class="gamma" size="2">
								<input type="checkbox" name="delInode" value="<%=facility.getInode()%>" onclick="togglePublish()">
								</font>
							</td>
							<td   nowrap>
								<font class="gamma" size="2">
								<%String facilityInode = facility.getInode();%>
								<a class="gamma" href="<portlet:actionURL><portlet:param name="struts_action" value="/ext/facilities/edit_facility" />
								<portlet:param name="cmd" value="edit" /><portlet:param name="inode" value="<%=facilityInode%>" /></portlet:actionURL>">
								<%=facility.getFacilityName()%>
								</a>
								</font>
							</td>
							<td >
								<font class="gamma" size="2">
								<%=description%>
								</font>
							</td>
							<td align="center" >
								<font class="gamma" size="2">
								<a href="<portlet:actionURL><portlet:param name="struts_action" value="/ext/events/view_events" />
								<portlet:param name="facility" value="<%=facilityInode%>" /></portlet:actionURL>">
								<span class="calMonthIcon"></span>
								</a>
								</font>
							</td>
							<td  align="center" >
								<font class="gamma" size="2">
								<input type="hidden" name="sInode<%=k%>" id="sInode<%=k%>" value="<%=facility.getInode()%>">
								<input type="text" class="form-text" size="5" name="sortOrder<%=k%>" id="sortOrder<%=k%>" value="<%=k%>">
								</font>
							</td>
							<td  align="center">
								<font class="gamma" size="2">
								<%=(facility.isActive()) ? "Yes" : "No" %>
								</font>
							</td>
						</tr>
					<%}%>
					<% if (facilities.size() ==0) { %>
					<tr>
						<td colspan="6" align=center>
						<font class="bg" size="2">There are no Facilities to show</font>
						</td>
					</tr>
					<% } %>
					<tr>
						<td colspan="6" align="center">
						<br/>
                        <button dojoType="dijit.form.Button" id="deleteBtn" onClick="submitfmDelete()" disabled="true">
                           Delete Selected Facilities
                         </button>
                        
                        <button dojoType="dijit.form.Button" onClick="submitfmForm('reorder')">
                          Reorder Facilities
                        </button>
                        
                        <button dojoType="dijit.form.Button" onClick="javascript:addAsset()">
                           Add Facility
                        </button>
						</td>
					</tr>
				</table>
				</form>
			</td>
		</tr>
	</table>
</liferay:box>

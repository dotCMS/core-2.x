<%@ page import="com.dotmarketing.portlets.user.factories.*" %>
<%@ page import="com.dotmarketing.portlets.user.model.*" %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%@ include file="/html/portlet/ext/facilities/init.jsp" %>
<% 
Facility facility;
if (request.getAttribute(com.dotmarketing.util.WebKeys.FACILITY_EDIT)!=null) {
	facility = (Facility) request.getAttribute(com.dotmarketing.util.WebKeys.FACILITY_EDIT);
}
else {
	facility = (Facility) com.dotmarketing.factories.InodeFactory.getInode(request.getParameter("inode"),Facility.class);
}
String referer = (request.getParameter("referer") != null ) ? request.getParameter("referer") : "" ;
%>
<script language="Javascript">
function submitfm(form,subcmd) {
	form.<portlet:namespace />cmd.value = '<%=Constants.ADD%>';
	form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/facilities/edit_facility" /></portlet:actionURL>';
	submitForm(form);
}

function cancelEdit() {
	self.location = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>';
}
function beLazy(){
	var ele = document.getElementById("facilityDescription");
	if(ele.value.length ==0 ){
		ele.value = document.getElementById("facilityName").value;
	}
}
</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"Add/Edit Facility\") %>" />


	<table border="0" cellpadding="2" cellspacing="2">
		<tr>
			<td>
				<table border="0" cellpadding="2" cellspacing="2" width="500">
						<html:form action='/ext/facilities/edit_facility' styleId="fm">
							<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="add">
							<input name="<portlet:namespace />referer" type="hidden" value="<%=referer%>">
							<input name="<portlet:namespace />redirect" type="hidden" value="<portlet:renderURL><portlet:param name="struts_action" value="/ext/facilities/view_facilities" /></portlet:renderURL>">
					    	<input name="<portlet:namespace />inode" type="hidden" value="<%=facility.getInode()%>">
						<tr>
							<td width="120">
								<font class="bg" size="2"><B>Facility Name:</B></font>
							</td>				
							<td>
							<html:text style="width:300" styleClass="form-text" property="facilityName" styleId="facilityName" onchange="beLazy()"/>
							</td>
						</tr>
		
						<tr>
							<td>
							<font class="bg" size="2"><B>Description:</B></font>
							</td>				
							<td>
							<html:text style="width:300"  styleClass="form-text" property="facilityDescription" styleId="facilityDescription" />
							</td>
						</tr>
						<tr>
							<td>
							<font class="bg" size="2"><B>Sort Order:</B></font>
							</td>				
							<td>
							<html:text styleClass="form-text" property="sortOrder" styleId="sortOrder"/>
							</td>
						</tr>
						<tr>
							<td>
							<font class="bg" size="2"><B>Active:</B></font>
							</td>				
							<td>
							<html:checkbox styleClass="form-text" property="active" styleId="active"/>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<button dojoType="dijit.form.Button" onClick="submitfm(document.getElementById('fm'),'')" >Save</button>	
								<button dojoType="dijit.form.Button" onClick="cancelEdit()">Cancel</button>
							</td>
						</tr>
					</table>
				</html:form>
			</td>
		</tr>
	</table>
</liferay:box>


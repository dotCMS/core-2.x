<%@ include file="/html/portlet/ext/workflows/init.jsp"%>
<%@ page import="com.dotmarketing.util.UtilMethods"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.portlet.WindowState"%>
<%@ page import="com.dotmarketing.portlets.workflows.struts.*"%>
<%@ page import="com.dotmarketing.cms.factories.*"%>
<%@ page import="com.dotmarketing.beans.WebAsset"%>
<%@ page import="com.dotmarketing.beans.Inode"%>
<%@ page import="com.dotmarketing.factories.InodeFactory"%>
<%@ page import="com.dotmarketing.portlets.files.model.File"%>
<%@ page import="com.dotmarketing.portlets.contentlet.model.Contentlet"%>
<%@ page import="com.dotmarketing.portlets.containers.model.Container"%>
<%@ page import="com.dotmarketing.portlets.htmlpages.model.HTMLPage"%>
<%@ page import="com.dotmarketing.portlets.links.model.Link"%>
<%@ page import="com.dotmarketing.portlets.templates.model.Template"%>
<%@page import="com.dotmarketing.util.Config"%>
<%@page import="com.dotmarketing.business.PermissionAPI"%>
<%@ page import="com.dotmarketing.util.InodeUtils" %>

<%
	int[] monthIds = CalendarUtil.getMonthIds();
	String[] months = CalendarUtil.getMonths(locale);
	String[] days = CalendarUtil.getDays(locale);
	
	String referer = request.getParameter ("referer");
	if (!UtilMethods.isSet(referer)) {
		java.util.Map params = new java.util.HashMap();
		params.put("struts_action",
				new String[] { "/ext/workflows/view_workflow_tasks" });
		referer = com.dotmarketing.util.PortletURLUtil.getActionURL(
				request, WindowState.MAXIMIZED.toString(), params);
	}
	
	WorkflowTaskForm form = (WorkflowTaskForm)request.getAttribute ("WorkflowTaskForm");
	WebAsset asset = null;
	Contentlet con  = null;
	if (UtilMethods.isSet(form.getWebasset())) {
		if(APILocator.getContentletAPI().isContentlet(form.getWebasset())){
			con = APILocator.getContentletAPI().find(form.getWebasset(),user,false);
		}else{
	    	asset = (WebAsset)InodeFactory.getInode (form.getWebasset(), Inode.class);
		}
	}
	
	User assignedToUser = null;
	Role assignedToRole = null;
	String assignedTo = (String) request.getAttribute("assignedTo");
	if(UtilMethods.isSet(form.getAssignedTo()) &&  !form.getAssignedTo().equalsIgnoreCase("NoBody")){
		assignedTo = form.getAssignedTo();
		assignedTo = assignedTo.startsWith("user-")?assignedTo.substring(5, assignedTo.length()):assignedTo;
		assignedToUser = APILocator.getUserAPI().loadUserById(assignedTo,APILocator.getUserAPI().getSystemUser(),false); 
	}else if(UtilMethods.isSet(form.getBelongsTo())){
		assignedTo = form.getBelongsTo();
		assignedTo = assignedTo.startsWith("role-")?assignedTo.substring(5, assignedTo.length()):assignedTo;
		assignedToRole = APILocator.getRoleAPI().loadRoleById(assignedTo);
	}	
	String workflowPortletTitle = (!InodeUtils.isSet(form.getInode()) ? LanguageUtil.get(pageContext, "New-Task"):LanguageUtil.get(pageContext, "Edit-Task"));

%>

<%@page import="com.dotmarketing.business.APILocator"%>

<%@page import="com.dotmarketing.business.Role"%><script language="javascript">

	dojo.require('dotcms.dojo.data.UsersReadStore');
	dojo.require('dijit.form.FilteringSelect');
	dojo.require("dijit.form.Textarea");
	function <portlet:namespace />setCalendarDate_0(year, month, day) 
	{	  
		var form = document.getElementById('WorkflowTaskForm');
		form.dueDateYear.value = year;
		form.dueDateMonth.value = --month;
		form.dueDateDay.value = day;
	}
	
	function saveTask () {
	
 		if (dojo.byId('assignedTo').value == '') {
	   		alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Please-select-a-user-or-role-to-assign-the-task")) %>');
	   		return;
   		}
	
		var form = document.getElementById('WorkflowTaskForm');
		form.action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="cmd" value="save" />
  					   </portlet:actionURL>";
  		form.submit();
	}
	
	function cancel () {
		document.location = '<%=referer%>';
	}
	
	function noDueDateChanged() {
		var form = document.getElementById('WorkflowTaskForm');
		var checked = form.noDueDate.checked;
		//form.dueDateYear.disabled = checked;
		//form.dueDateMonth.disabled = checked;
		//form.dueDateDay.disabled = checked;
		dijit.byId('dueDateField').attr('disabled', checked);
	}
	
	function publish (objId,assetId) {
		var href = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
		href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />'
		href = href + '<portlet:param name="cmd" value="publish" />';
		href = href + '<portlet:param name="referer" value="<%= referer %>" />';
		href = href + '</portlet:actionURL>&inode='+objId+'&asset_inode='+assetId;
		
		document.location.href = href;
	}
	
	function dueDateSelected() {
		var dueDate = dijit.byId('dueDateField').attr('value');
		
		document.getElementById('dueDateYear').value = dueDate.getFullYear();
		document.getElementById('dueDateMonth').value = dueDate.getMonth();
		document.getElementById('dueDateDay').value = dueDate.getDate();
	}
</script>

<script type="text/javascript">	
<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp"
	flush="true">
	<liferay:param name="calendar_num" value="1" />
</liferay:include>
</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value="<%=workflowPortletTitle%>" />
	
<html:form styleId="WorkflowTaskForm" action="/ext/workflows/edit_workflow_task">
<html:hidden property="inode"/>
<input type="hidden" name="referer" value="<%=referer%>"/>

<div class="shadowBoxLine">
<dl>
	<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Title") %>:</dt>
	<dd>
	<input type="text" dojoType="dijit.form.TextBox" name="title" style="width:350px;" value="<%=UtilMethods.webifyString(form.getTitle()) %>" />


	</dd>
	
	<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Description") %>:</dt>
	<dd>
		<textarea dojoType="dijit.form.Textarea" name="description"  style="width:350px;min-height:100px;"><%=UtilMethods.webifyString(form.getDescription()) %></textarea>
	</dd>
	
	<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Assignee") %>:</dt>
	<%
		if (!InodeUtils.isSet(form.getInode ())) {
	%>	
		<dd>
			<script type="text/javascript">
			    <%
			    	String selectedId = null;
		    		String selectedValue = null;
			    	if(assignedToUser != null) { 
			    		selectedId = "user-" + assignedToUser.getUserId();
			    		selectedValue = assignedToUser.getFullName();
			    	} else if(assignedToRole != null) {
			    		selectedId = "role-" + assignedToRole.getId();
			    		selectedValue = assignedToRole.getName();
			    	}
			    	int permissionsToCheck = PermissionAPI.PERMISSION_WRITE;
			    	boolean assignToViewers = Config.getBooleanProperty("ASSIGN_TASKS_TO_USERS_W_READ",false);
			    	if(assignToViewers)
			    	{
			    		permissionsToCheck = PermissionAPI.PERMISSION_READ;
			    	}
			    %>
			</script>
			<div dojoType="dotcms.dojo.data.UsersReadStore" jsId="usersRolesStore" includeRoles="true" hideSystemRoles="true"
				<%= UtilMethods.isSet(asset) && UtilMethods.isSet(asset.getInode())?"asset=\"" + asset.getInode() + "\" ":"" %> permission="<%= permissionsToCheck %>">
			</div> 
			<select id="assignedTo" name="assignedTo" dojoType="dijit.form.FilteringSelect" 
				store="usersRolesStore" searchDelay="300" pageSize="30" labelAttr="name" 
				invalidMessage="<%= LanguageUtil.get(pageContext, "Invalid-option-selected") %>"
				value= "<%= selectedId %>" >
			</select>
		</dd>
	<%
		} else {
	%>
		<input type="hidden" name="assignedTo"  id="assignedTo" value="user-<%= form.getAssignedTo() %>">
		<dd><%= UtilMethods.isSet(assignedToUser) ? assignedToUser.getFullName():UtilMethods.isSet(assignedToRole)?assignedToRole.getName():"" %></dd>		
	<%
		} 
	%>
	
	<dt><%= LanguageUtil.get(pageContext, "Due-date") %>:</dt>
	<dd>
		<!-- DISPLAY DATE-->
		<%--html:select property="dueDateMonth">
			<% for (int j = 0; j < months.length; j++) { %>
				<html:option value="<%= String.valueOf(monthIds[j]) %>"><%= months[j] %></html:option>
			<% } %>
		</html:select>
		
		<html:select property="dueDateDay">
			<% for (int j = 1; j <= 31; j++) { %>
				<html:option value="<%= String.valueOf(j) %>"><%= j %></html:option>
			<% } %>
		</html:select>
		
		<html:select property="dueDateYear">
			<%
				int previous = 100;
				GregorianCalendar gc = new GregorianCalendar ();
				int currentYear = gc.get(GregorianCalendar.YEAR);
				for (int j = currentYear - previous; j <= currentYear + 10; j++) {
			%>
				<html:option value="<%= String.valueOf(j) %>"><%= j %></html:option>
			<% } %>
		</html:select>
		
		<span
		id="<portlet:namespace />calendar_input_0_button"
		class="calMonthIcon"
		onClick="<portlet:namespace />calendarOnClick_0();"></span> --%>
<%
	Calendar dueDate = Calendar.getInstance();
	if (UtilMethods.isSet(form.getDueDate()))
		dueDate.setTime(form.getDueDate());
%>
		<input type="text" dojoType="dijit.form.DateTextBox" validate='return false;' invalidMessage="" id="dueDateField" name="dueDateField" value="<%= dueDate.get(Calendar.YEAR) + "-" + (dueDate.get(Calendar.MONTH) < 9 ? "0" : "") + (dueDate.get(Calendar.MONTH) + 1) + "-" + (dueDate.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + dueDate.get(Calendar.DAY_OF_MONTH) %>" onchange="dueDateSelected();" />
		<input type="hidden" name="dueDateMonth" id="dueDateMonth" value="<%= dueDate.get(Calendar.MONTH) %>" />
		<input type="hidden" name="dueDateDay" id="dueDateDay" value="<%= dueDate.get(Calendar.DATE) %>" />
		<input type="hidden" name="dueDateYear" id="dueDateYear" value="<%= dueDate.get(Calendar.YEAR) %>" />
	</dd>
<!-- /DISPLAY DATE-->

	<dt>&nbsp;</dt>
	<dd>
		<!--<html:checkbox styleId="noDueDate" property="noDueDate" onclick="noDueDateChanged();"/> <%= LanguageUtil.get(pageContext, "No-Due-Date") %>-->
		<input type="checkbox" name="noDueDate" id="noDueDate" dojoType="dijit.form.CheckBox" onclick="noDueDateChanged();" <%= form.isNoDueDate() ? "checked" : "" %> />
		<label for="noDueDate"><%= LanguageUtil.get(pageContext, "No-Due-Date") %></label>
	</dd>
	
	<html:hidden property="webasset"/>
	<% if(con != null){	%>
	
		<dt><%= LanguageUtil.get(pageContext, "Associated-Content") %>:</dt>
		<dd><%= APILocator.getContentletAPI().getName(con, user,false) %></dd>
			
		<% }else if (asset != null) {
			if (asset instanceof HTMLPage) {
				HTMLPage htmlPage = (HTMLPage) asset;
		%>
				<dt><%= LanguageUtil.get(pageContext, "Associated-Page") %>:</dt>
				<dd><%= htmlPage.getTitle() %> (<%=htmlPage.getURI()%>)</dd>
		<%
			} else if (asset instanceof Container) {
				Container container = (Container) asset;
		%>
				<dt><%= LanguageUtil.get(pageContext, "Associated-Container") %>:</dt>
				<dd><%= container.getTitle() %></dd>
		<%
			} else if (asset instanceof Template) {
				Template template = (Template) asset;
		%>
				<dt><%= LanguageUtil.get(pageContext, "Associated-Template") %>:</dt>
				<dd><%= template.getTitle() %></dd>
		<%
			} else if (asset instanceof Link) {
				Link link = (Link) asset;
		%>
				<dt><%= LanguageUtil.get(pageContext, "Associated-Link") %>:</dt>
				<dd><%= link.getTitle() %></dd>
		<%
			} else if (asset instanceof File) {
				File file = (File) asset;
		%>
				<dt><%= LanguageUtil.get(pageContext, "Associated-File") %>:</dt>
				<dd><%= file.getTitle() %> (<%= file.getURI() %>)</dd>
		<%
			}
		}
	%>
</dl>

</div>
<div class="clear"></div>
<div class="buttonRow">
	<button dojoType="dijit.form.Button" type="button" onClick="saveTask()" iconClass="saveIcon"><%=InodeUtils.isSet(form.getInode())? UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save-Task")) : UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Create-Task")) %></button>
	<button dojoType="dijit.form.Button" type="button" onClick="cancel()" iconClass="cancelIcon"><%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %></button>
</div>

</html:form>

</liferay:box>
<script type="text/javascript">
<!--
	dojo.addOnLoad(function() {
		noDueDateChanged();
	});
//-->
</script>


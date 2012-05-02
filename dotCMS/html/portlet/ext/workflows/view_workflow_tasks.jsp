<%@ include file="/html/portlet/ext/workflows/init.jsp"%>

<%@page import="java.util.List"%>
<%@page import="com.dotmarketing.util.Config"%>
<%@page import="java.util.*"%>
<%@page import="com.dotmarketing.cms.factories.*"%>
<%@page import="com.dotmarketing.util.UtilMethods"%>
<%@page import="com.dotmarketing.portlets.workflows.struts.*"%>
<%@page import="com.dotmarketing.portlets.workflows.model.*"%>
<%@page import="com.dotmarketing.portlets.workflows.factories.*"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="com.dotmarketing.beans.WebAsset"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.dotmarketing.portlets.htmlpages.model.HTMLPage"%>
<%@page import="com.dotmarketing.portlets.contentlet.model.Contentlet"%>
<%@page import="com.dotmarketing.portlets.files.model.File"%>
<%@page import="com.dotmarketing.portlets.containers.model.Container"%>
<%@page import="com.dotmarketing.portlets.links.model.Link"%>
<%@page import="com.dotmarketing.portlets.templates.model.Template"%>
<%@page import="com.dotmarketing.factories.InodeFactory"%>
<%@page import="com.dotmarketing.portlets.structure.model.Structure"%>
<%@page import="com.dotmarketing.portlets.structure.model.Field"%>
<%@page import="org.apache.commons.beanutils.BeanUtils"%>
<%@page import="org.apache.commons.beanutils.PropertyUtils"%>
<%@page import="com.dotmarketing.util.Parameter"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="com.liferay.util.cal.CalendarUtil"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.dotmarketing.beans.WebAsset"%>
<%@page import="com.dotmarketing.portlets.htmlpages.model.HTMLPage"%>
<%@page import="com.dotmarketing.factories.InodeFactory"%>
<%@page import="com.dotmarketing.portlets.contentlet.model.Contentlet"%>
<%@page import="com.dotmarketing.portlets.files.model.File"%>
<%@page import="com.dotmarketing.portlets.templates.model.Template"%>
<%@page import="com.dotmarketing.portlets.links.model.Link"%>
<%@page import="com.dotmarketing.portlets.containers.model.Container"%>
<%@page import="com.dotmarketing.portlets.structure.factories.StructureFactory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.commons.beanutils.PropertyUtils"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="com.dotmarketing.business.PermissionAPI"%>
<%@page import="com.liferay.portal.NoSuchRoleException"%>
<%@page import="com.dotmarketing.business.Role"%>
<%
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action", new String[] { "/ext/workflows/view_workflow_tasks" });

    String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED
    	.toString(), params);

	String selectedFilter = request.getParameter("selectedFilter");



   	String title = (String) request.getAttribute("title");


   	boolean showOpen = false;
   	boolean showResolved = false;
   	boolean showCancelled = false;
   	String[] statusii = new String[0];
   	if(request.getAttribute("workflow_status") != null){
   		statusii = (String[])request.getAttribute("workflow_status");
   	}
   	
	for(int i=0;i<statusii.length;i++){
		if(String.valueOf(WorkflowStatuses.OPEN).equalsIgnoreCase(statusii[i])){
	showOpen = true;
		}
		if(String.valueOf(WorkflowStatuses.RESOLVED).equalsIgnoreCase(statusii[i])){
	showResolved = true;
		}
		if(String.valueOf(WorkflowStatuses.CANCELLED).equalsIgnoreCase(statusii[i])){
	showCancelled = true;
		}
	}

   	String assignedTo = (String) request.getAttribute("assignedTo");
   	User assignedToUser = UtilMethods.isSet(assignedTo) && assignedTo.startsWith("user-")?
   			APILocator.getUserAPI().loadUserById(assignedTo.substring(5, assignedTo.length()),APILocator.getUserAPI().getSystemUser(),false):null;
	Role assignedToRole = UtilMethods.isSet(assignedTo) && assignedTo.startsWith("role-")?
	APILocator.getRoleAPI().loadRoleById(assignedTo.substring(5, assignedTo.length())):null;
   	

   	int pageNumber = (Integer) request.getAttribute("page");
   	String orderBy = (String) request.getAttribute("orderBy");
   	int count = (Integer) request.getAttribute(com.dotmarketing.util.WebKeys.WORKFLOW_FILTER_TASKS_COUNT);
   	int perPage = com.dotmarketing.util.Config.getIntProperty ("PER_PAGE");
	boolean hasNextPage = pageNumber * perPage < count;
	
	
    boolean isAdministrator = APILocator.getRoleAPI().doesUserHaveRole(user, APILocator.getRoleAPI().loadCMSAdminRole());
	List<Role> roles = APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
    
    
    
%>


<%@page import="com.dotmarketing.portlets.workflows.business.WorkflowAPI"%>
<%@page import="com.dotmarketing.util.WebKeys.WorkflowStatuses"%>
<%@ include file="/html/portlet/ext/workflows/workflows_js_inc.jsp" %>

<script type="text/javascript">	
	dojo.require("dijit.form.FilteringSelect");
	dojo.require("dotcms.dojo.data.UsersReadStore");

	function doFilter (page, orderBy) {
		var form = document.getElementById("filterForm");
		form.action = "<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
									<portlet:param name="struts_action" value="/ext/workflows/view_workflow_tasks" />
								</portlet:renderURL>&page="+page+"&order_by="+orderBy;
		form.submit ();
	}
	
	function resetFilters () {
		var form = document.getElementById("filterForm");
		form.action = "<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
									<portlet:param name="struts_action" value="/ext/workflows/view_workflow_tasks" />
									<portlet:param name="resetFilters" value="true" />
								</portlet:renderURL>";
		form.submit ();
	}
	
//Layout Initialization
	function  resizeBrowser(){
	    var viewport = dijit.getViewport();
	    var viewport_height = viewport.h;
	   
		var  e =  dojo.byId("borderContainer");
		dojo.style(e, "height", viewport_height -180+ "px");
		
	}
	
	dojo.connect(window, "onresize", this, "resizeBrowser");
	
function editTask(taskInode) {
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
							<portlet:param name="cmd" value="view" />
							<portlet:param name="inode" value="'
							+taskInode+
							'" />
							<portlet:param name="referer" value="<%= referer %>" />
							</portlet:actionURL>';
	}

<%if(UtilMethods.isSet(assignedToUser) || UtilMethods.isSet(assignedToRole)) {%>
dojo.addOnLoad(function(){
	    dijit.byId('assignedTo').attr('value', '<%=UtilMethods.isSet(assignedToUser)?"user-"+assignedToUser.getUserId():"role-"+assignedToRole.getId()%>');
	    dijit.byId('assignedTo').attr('displayedValue', '<%=UtilMethods.isSet(assignedToUser)?assignedToUser.getFullName():assignedToRole.getName()%>');
 });	
<%}%>

</script>


<liferay:box top="/html/common/box_top.jsp"
bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value='<%= LanguageUtil.get(pageContext, "Filtered-Tasks") %>' />

<div dojoType="dotcms.dojo.data.UsersReadStore" style="float:left;" jsId="userRolesStore" includeRoles="true" hideSystemRoles="true"></div> 



<!-- START Button Row -->
	<div class="buttonBoxLeft"><h3><%=LanguageUtil.get(pageContext, "javax.portlet.title.EXT_21")%></h3></div>
	<div class="buttonBoxRight">
		<button dojoType="dijit.form.Button"  iconClass="plusIcon" onclick="window.location='<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" /></portlet:actionURL>';"><%=LanguageUtil.get(pageContext, "Add-New")%></button>
	</div>
<!-- END Button Row -->

<!-- START Split Box -->
<div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="false" liveSplitters="true" id="borderContainer" class="shadowBox headerBox" style="height:100px;">
		
<!-- START Left Column -->	
	<div dojoType="dijit.layout.ContentPane" splitter="false" region="leading" style="width: 350px;" class="lineRight">
		
		<div style="margin-top:48px;">
			<form id="filterForm" action="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/workflows/view_workflow_tasks" />
				</portlet:renderURL>" method="post">
				<dl>
					<dt><%=LanguageUtil.get(pageContext, "Title")%>:</dt>
					<dd><input type="text" dojoType="dijit.form.TextBox" name="title" value="<%=title%>" /></dd>
					<dt><%=LanguageUtil.get(pageContext, "Assigned-To")%>:</dt>
					<dd>
					
					<%if(isAdministrator){ %>
				   		<select id="assignedTo" name="assignedTo" dojoType="dijit.form.FilteringSelect" 
				                store="userRolesStore" searchDelay="300" pageSize="30" labelAttr="name" 
				                invalidMessage="<%=LanguageUtil.get(pageContext, "Invalid-option-selected")%>">
				         </select>
			        <%}else{ %>
			        
				         <select id="assignedTo" name="assignedTo" dojoType="dijit.form.FilteringSelect" >
				         	<option value=""></option>
				         	<option value="user-<%=user.getUserId() %>"
				         	<%if(user.equals(assignedToUser)){ %>
				         		selected = "true"
				         	<%} %>
				         	><%=user.getFullName() %></option>
				         	<%for(Role r : roles){ %><%=r.getId() %>
				         		<%if(!r.getFQN().startsWith("Users --") && !r.isSystem()) {%>
				         		<option value="role-<%=r.getId() %>"
				         			<%if(r.equals(assignedToRole)){ %>
						         		selected = "true"
						         	<%} %>
				         		
				         		
				         		><%=r.getName() %></option>
				         		<%} %>
				         	<%} %>
				         
				         </select>
			         <%} %>
				         
					</dd>
					<dt><%=LanguageUtil.get(pageContext, "Show")%>:</dt>
					<dd>
						<input dojoType="dijit.form.CheckBox" <%if(showOpen){%> checked='checked' <%}%> type="checkbox" name="status" value="<%=WorkflowStatuses.OPEN%>" id="showOpen" /> <label for="showOpen"><%=LanguageUtil.get(pageContext, "open-tasks")%></label><br/> 
						<input dojoType="dijit.form.CheckBox" <%if(showResolved){%> checked='checked' <%}%>type="checkbox" name="status" value="<%=WorkflowStatuses.RESOLVED%>" id="showResolved"   /> <label for="showResolved"><%=LanguageUtil.get(pageContext, "resolved-tasks")%></label><br/> 
						<input dojoType="dijit.form.CheckBox" <%if(showCancelled){%> checked='checked' <%}%>type="checkbox" name="status" value="<%=WorkflowStatuses.CANCELLED%>" id="showCancelled" /> <label for="showCancelled"><%=LanguageUtil.get(pageContext, "cancelled-tasks")%></label><br/>
						<input dojoType="dijit.form.CheckBox" <%if(UtilMethods.isSet(request.getAttribute("includeReporter"))){%> checked='checked' <%}%>type="checkbox" name="includeReporter" value="true" id="includeReporter" /> <label for=""includeReporter""><%=LanguageUtil.get(pageContext, "Reported-by-me")%></label>

					</dd>
				</dl>
				<div class="buttonRow">
					<button dojoType="dijit.form.Button" iconClass="searchIcon" name="filterButton" onclick="doFilter(1, 'mod_date desc')"> <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Search")) %></button>
					<button dojoType="dijit.form.Button" name="resetButton"  iconClass="resetIcon" onclick="resetFilters()"><%=LanguageUtil.get(pageContext, "reset")%></button>    
				</div>
			</form>
		</div>
	</div>
<!-- END Left Column -->
	

<!-- START Right Column -->
	<div dojoType="dijit.layout.ContentPane" splitter="true" region="center" style="margin-top:37px;">
		
		<!-- START Listing Table -->
		<div id="workFlowWrapper" style="padding:10px; overflow-y:auto; overflow-x:hidden;">
		 
			<table class="listingTable">
				
				<tr>
					<th nowrap="nowrap" width="50%"><a href="javascript: doFilter(1, '<%=orderBy.equals("title")?"title desc":"title"%>')"><%=LanguageUtil.get(pageContext, "Title")%></a></th>
					<th nowrap="nowrap" width="10%" style="text-align:center;"><a href="javascript: doFilter(1, '<%=orderBy.equals("status")?"status desc":"status"%>')"><%=LanguageUtil.get(pageContext, "Status")%></a></th>
					<th nowrap="nowrap" width="10%"><a href="javascript: doFilter(1, '<%=orderBy.equals("assigned_to")?"assigned_to desc":"assigned_to"%>')"><%=LanguageUtil.get(pageContext, "Assignee")%></a></th>
					<th nowrap="nowrap" width="15%" style="text-align:center;"><a href="javascript: doFilter(1, '<%=orderBy.equals("mod_date")?"mod_date desc":"mod_date"%>')"><%=LanguageUtil.get(pageContext, "Last-Updated")%></a></th>
					<th nowrap="nowrap" width="15%" style="text-align:center;"><a href="javascript: doFilter(1, '<%=orderBy.equals("due_date")?"due_date desc":"due_date"%>')"><%=LanguageUtil.get(pageContext, "Due-Date")%></a></th>
					
					
				</tr>
		
				<%
					List<HashMap> workflowFilterTasks = (List<HashMap>) request
					.getAttribute(com.dotmarketing.util.WebKeys.WORKFLOW_FILTER_TASKS_LIST);
														
					List<Role> userRoles = APILocator.getRoleAPI().loadRolesForUser(user.getUserId());

		    
	                int k = 0;
	                for (HashMap task : workflowFilterTasks) {

						PermissionAPI permAPI = APILocator.getPermissionAPI();
							WebAsset asset = null;
							Contentlet content = null;
							String type ="&nbsp;";
							StringBuilder actions = new StringBuilder();
							if (UtilMethods.isSet((String)task.get("webasset"))) {
							    List inodes = new ArrayList();
							    if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (HTMLPage.class, "inode = '" + String.valueOf(task.get("webasset"))+"'")).size() > 0){
							        asset = (WebAsset) inodes.get(0);
							        if(permAPI.doesUserHavePermission(asset, permAPI.PERMISSION_PUBLISH,user)){
								        type = "Web Page";
								        actions.append(		
												"			<div dojoType=\"dijit.MenuItem\" iconClass=\"previewIcon\" class=\"pop_divider\" onClick=\"previewHTMLPage('"+asset.getInode()+"','"+referer+"')\" >"+
												"				<span>"+LanguageUtil.get(pageContext, "View-Page" )+"</span>"+
												"			</div>");

										        if(!asset.isDeleted() && !asset.isLive()){
										        	actions.append(
										        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"publishIcon\" onClick=\"publish('"+task.get("inode")+"','"+asset.getInode()+"');\" >"+
															"			<span>"+LanguageUtil.get(pageContext, "Publish" )+"</span>"+ 
															"		</div>"+
															"		<div dojoType=\"dijit.MenuItem\" iconClass=\"archiveIcon\" onClick=\"archive('"+task.get("inode")+"','"+asset.getInode()+"');\" >"+
															"			<span>"+LanguageUtil.get(pageContext,"Archive")+"</span>"+ 
															"		</div>");
										        }if(!asset.isDeleted() && asset.isLive()){
										        	actions.append(
										        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"publishIcon\" onClick=\"publish('"+task.get("inode")+"','"+asset.getInode()+"');\" >"+
															"			<span>"+LanguageUtil.get(pageContext, "Publish")+"</span>"+  
															"		</div>"+
															"		<div dojoType=\"dijit.MenuItem\" iconClass=\"unpublishIcon\" onClick=\"unpublish('"+task.get("inode")+"','"+asset.getInode()+"');\" >"+
															"			<span>"+LanguageUtil.get(pageContext, "Unpublish")+"</span>"+  
															"		</div>");
										        			
										        }if(asset.isDeleted()){
										        	actions.append(
										        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"unarchiveIcon\" onClick=\"unarchive('"+task.get("inode")+"','"+asset.getInode()+"');\" >"+
															"			<span>"+LanguageUtil.get(pageContext, "Unarchive")+"</span>"+  
															"		</div>");
										        }
										
							        }
							    } else if (APILocator.getContentletAPI().isContentlet((String.valueOf(task.get("webasset"))))){
							    	content = APILocator.getContentletAPI().find((String.valueOf(task.get("webasset"))),user, true);
							        type = "Content - "+content.getStructure().getName();
									
							        actions.append(
											"		<div dojoType=\"dijit.MenuItem\" iconClass=\"previewIcon\" class=\"pop_divider\" onClick=\"dijit.byId('divContent"+content.getInode()+"_pop_up').show()\" >"+
											"			<span>"+LanguageUtil.get(pageContext, "View-Content" )+"</span>"+  
											"		</div>");	
							        if(permAPI.doesUserHavePermission(content, permAPI.PERMISSION_PUBLISH,user)){
								        if(!content.isArchived() && !content.isLive()){
								        	actions.append(	
								        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"publishIcon\" onClick=\"publish('"+task.get("inode")+"','"+content.getInode()+"');\" >"+
													"			<span>"+LanguageUtil.get(pageContext, "Publish" )+"</span>"+  
													"		</div>"+
													"		<div dojoType=\"dijit.MenuItem\" iconClass=\"archiveIcon\" onClick=\"archive('"+task.get("inode")+"','"+content.getInode()+"');\" >"+
													"			<span>"+LanguageUtil.get(pageContext,"Archive")+"</span>"+  
													"		</div>");
								        }if(!content.isArchived() && content.isLive()){
								        	actions.append(
								        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"publishIcon\" onClick=\"publish('"+task.get("inode")+"','"+content.getInode()+"');\" >"+
													"			<span>"+LanguageUtil.get(pageContext, "Publish")+"</span>"+  
													"		</div>"+
													"		<div dojoType=\"dijit.MenuItem\" iconClass=\"unpublishIcon\" onClick=\"unpublish('"+task.get("inode")+"','"+content.getInode()+"');\" >"+
													"			<span>"+LanguageUtil.get(pageContext, "Unpublish")+"</span>"+  
													"		</div>");	
								        }if(content.isArchived()){
								        	actions.append(
								        			"		<div dojoType=\"dijit.MenuItem\" iconClass=\"unarchiveIcon\" onClick=\"unarchive('"+task.get("inode")+"','"+content.getInode()+"');\" >"+
													"			<span>"+LanguageUtil.get(pageContext, "Unarchive")+"</span>"+ 
													"		</div>");
								        }
							        }
							        
							    } else {
							        type = "Other";
							    }
							}%>
				
				<tr id="row<%=task.get("inode")%>" class="alternate_1"
					onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="cmd" value="view" />
									<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
									<portlet:param name="referer" value="<%= referer %>" />
									</portlet:actionURL>';">
					<td valign="top">
						<!-- Start Right Click Menu -->			
							<div dojoType="dijit.Menu" id="context<%=task.get("inode")%>" class="dotContextMenu" targetNodeIds="row<%=task.get("inode")%>" style="display: none;">
							   
							    <div dojoType="dijit.MenuItem" iconClass="workflowIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="cmd" value="view" />
									<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
									<portlet:param name="referer" value="<%= referer %>" />
									</portlet:actionURL>';">
							        <%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "View-Task"))%>
							    </div>
							    
								<%
					    			if (task.get("assigned_to") == null || !task.get("assigned_to").equals(user.getUserId()))	{
				    				java.util.Map paramsAssign = new java.util.HashMap();
				    				paramsAssign.put("struts_action",new String[] {"/ext/workflows/edit_workflow_task"});
				    				paramsAssign.put("cmd",new String[]{"view"});
				    				paramsAssign.put("inode",new String[]{String.valueOf(task.get("inode"))});
				    				paramsAssign.put("user_id",new String[]{user.getUserId()});
				    				paramsAssign.put("referer",new String[]{referer});
				    				String assignReferrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),paramsAssign);
				    				
				    				String assignToMeUserId = "user-" + user.getUserId();
							    %>
							    
								    <div dojoType="dijit.MenuItem" iconClass="assignWorkflowIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
										<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
										<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
										<portlet:param name="user_id" value="<%= assignToMeUserId %>" />
										<portlet:param name="cmd" value="assign_task" />
										<portlet:param name="referer" value="<%= assignReferrer %>" />
										</portlet:actionURL>';">
										<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Assign-To-Me"))%>
								    </div>
							    <% } %>
							    
							    
								<%
							    	if (task.get("assigned_to") != null 
			    	    				&& (String.valueOf(task.get("assigned_to")).equals(user.getUserId()) || isAdministrator) 
			    	    				&& !String.valueOf(task.get("status")).equals(com.dotmarketing.util.WebKeys.WorkflowStatuses.CANCELLED.toString() )
			    	    				&& !String.valueOf(task.get("status")).equals(com.dotmarketing.util.WebKeys.WorkflowStatuses.RESOLVED.toString() ))	{
							    %>
									<div dojoType="dijit.MenuItem" iconClass="resolveWorkflowIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
										<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
										<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
										<portlet:param name="cmd" value="change_status" />
										<portlet:param name="new_status" value="<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.RESOLVED.toString() %>" />
										<portlet:param name="referer" value="<%= referer %>" />
										</portlet:actionURL>';">
										<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Resolve-Task"))%>
									</div>
									
									<div dojoType="dijit.MenuItem" iconClass="cancelWorkflowIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
										<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
										<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
										<portlet:param name="cmd" value="change_status" />
										<portlet:param name="new_status" value="<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.CANCELLED.toString() %>" />
										<portlet:param name="referer" value="<%= referer %>" />
										</portlet:actionURL>';">
										<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Cancel-Task"))%>
									</div>
								
								<% } %>
							    
							    
							    <%
							    	if (String.valueOf(task.get("status")).equals(com.dotmarketing.util.WebKeys.WorkflowStatuses.CANCELLED.toString()) || String.valueOf(task.get("status")).equals(com.dotmarketing.util.WebKeys.WorkflowStatuses.RESOLVED.toString() ))	{
							    %>
									<div dojoType="dijit.MenuItem" iconClass="reopenWorkflowIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
										<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
										<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
										<portlet:param name="cmd" value="change_status" />
										<portlet:param name="new_status" value="<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString() %>" />
										<portlet:param name="referer" value="<%= referer %>" />
										</portlet:actionURL>'";>
										<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Reopen-Task"))%>							
									</div>
									
									<div dojoType="dijit.MenuItem" iconClass="deleteWorkflowIcon" onClick="deleteWorkFlowTask('<%=task.get("inode")%>')">
										<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Delete-Task"))%>	
									</div>
								<% } %>
								
								<% if(asset != null || content != null ){ %>
					    			<%=actions.toString()%>
	              				<% } %>
						
								<div dojoType="dijit.MenuItem" iconClass="closeIcon" class="pop_divider" onClick="hideMenuPopUp('context<%=task.get("inode")%>');">
									<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Close")) %>
								</div>
							</div>		
						<!-- / Right Click Menu -->
						
						<span class="workflowIcon"></span>
						<%=String.valueOf(task.get("title"))%>
						
	                 </td>
					 
					 <td align="center" valign="top"><%=task.get("status").equals("OPEN")? LanguageUtil.get(pageContext, "OPEN"):task.get("status").equals("RESOLVED")? LanguageUtil.get(pageContext, "RESOLVED"):LanguageUtil.get(pageContext, "CANCELLED")%></td>
					
					<% if (UtilMethods.isSet(task.get("assigned_to")) && !String.valueOf(task.get("assigned_to")).equals("Nobody")) { %>
						<td valign="top">
							<%
								if(user.getUserId().equals(String.valueOf(task.get("assigned_to")))){%>
									<%=LanguageUtil.get(pageContext, "me")%>
								<%}else{%>
									<%=UtilMethods.getUserFullName(String.valueOf(task.get("assigned_to"))) + " " + UtilMethods.getUserEmailAddress(String.valueOf(task.get("assigned_to")))%>
								<%}
							%>
						</td>
					<% } else { 
						Role role = new Role(); 
						if(task.get("belongs_to")!=null && !String.valueOf(task.get("belongs_to")).equals("")){
							role = APILocator.getRoleAPI().loadRoleById(String.valueOf(task.get("belongs_to")));
						} %>
							<td valign="top" nowrap="true">
								<%=LanguageUtil.get(pageContext, "Nobody")%>
									<%java.util.Map paramsAssign = new java.util.HashMap();
				    				paramsAssign.put("struts_action",new String[] {"/ext/workflows/edit_workflow_task"});
				    				paramsAssign.put("cmd",new String[]{"view"});
				    				paramsAssign.put("inode",new String[]{String.valueOf(task.get("inode"))});
				    				paramsAssign.put("user_id",new String[]{user.getUserId()});
				    				paramsAssign.put("referer",new String[]{referer});
				    				String assignReferrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),paramsAssign);
				    				
				    				String assignToMeUserId = "user-" + user.getUserId();%>
							
							
							(<a href="#" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
								<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
								<portlet:param name="inode" value='<%= String.valueOf(task.get("inode")) %>' />
								<portlet:param name="user_id" value="<%= assignToMeUserId %>" />
								<portlet:param name="cmd" value="assign_task" />
								<portlet:param name="referer" value="<%= assignReferrer %>" />
								</portlet:actionURL>';"><b><%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Assign-To-Me"))%></b></a>)
								
								<br>
							<%=UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Role"))%>	(<%=role.getName() == null?"None":role.getName()%>)</td>
					<% } %>
					<td align="center" valign="top"><%=UtilMethods.dateToHTMLDate(UtilMethods.htmlDateTimeToDate(String.valueOf(task.get("mod_date"))))%></td>
					<td align="center" valign="top"><%=UtilMethods.isSet(String.valueOf(task.get("due_date")))?UtilMethods.dateToHTMLDate(UtilMethods.htmlDateTimeToDate(String.valueOf(task.get("due_date")))):""%></td>
				</tr>
				
				<% } if (workflowFilterTasks.size() == 0) { %>
					<tr>
						<td colspan="8" valign="top">
							<div class="noResultsMessage"><%=LanguageUtil.get(pageContext, "No-Tasks-Found")%></div>
						</td>
					</tr>
				<% } %>
			</table>
			
		</div>
		<!-- END Listing table -->
		
		<!-- START Pagination -->
			<div class="yui-gb buttonRow">
				<div class="yui-u first" style="text-align:left;">
					<% if (pageNumber > 1) { %>
						<button dojoType="dijit.form.Button" onClick="doFilter(<%=pageNumber - 1%>, '<%=orderBy%>')" iconClass="previousIcon"><%=LanguageUtil.get(pageContext, "Previous")%></button>
					<% } %> &nbsp;
				</div>
				<div class="yui-u" style="text-align:center;">&nbsp;</div>
				<div class="yui-u" style="text-align:right;">
					<% if (hasNextPage) { %>
						<button dojoType="dijit.form.Button" onClick="doFilter(<%=pageNumber + 1%>, '<%=orderBy%>')" iconClass="nextIcon"><%=LanguageUtil.get(pageContext, "Next")%></button>
					<% } %> &nbsp;
				</div>
			</div>
		<!-- END Pagination -->
				
	</div>
<!-- END Right Column -->

</div>
<!-- END Split Box -->
	
</liferay:box>
<%
	List<HashMap> workflowTasks = (List<HashMap>) request.getAttribute(com.dotmarketing.util.WebKeys.WORKFLOW_FILTER_TASKS_LIST);
for (HashMap task : workflowTasks) {  

	WebAsset asset = null;
	Contentlet content = null;
	boolean isContenlet = false;
	if (UtilMethods.isSet((String)task.get("webasset"))) {
		isContenlet = APILocator.getContentletAPI().isContentlet(String.valueOf(task.get("webasset")));
	    if (isContenlet){
        	content = APILocator.getContentletAPI().find((String.valueOf(task.get("webasset"))), APILocator.getUserAPI().getSystemUser(),true);
%>
			<%@ include file="/html/portlet/ext/contentlet/view_contentlet_popup_inc.jsp" %>
<%
		} 
	}
  } 
%>

<script type="text/javascript">
resizeBrowser();
</script>


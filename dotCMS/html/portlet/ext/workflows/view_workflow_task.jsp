<%@ include file="/html/portlet/ext/workflows/init.jsp"%>

<%@page import="java.util.List"%>
<%@page import="com.dotmarketing.util.UtilMethods"%>
<%@page import="com.dotmarketing.portlets.workflows.factories.*"%>
<%@page import="com.dotmarketing.portlets.workflows.model.*"%>
<%@page import="com.dotmarketing.cms.factories.*"%>
<%@page import="com.dotmarketing.portlets.files.model.*"%>
<%@page import="com.dotmarketing.beans.WebAsset"%>
<%@page import="com.dotmarketing.beans.Inode"%>
<%@page import="com.dotmarketing.factories.InodeFactory"%>
<%@page import="com.dotmarketing.portlets.files.model.File"%>
<%@page import="com.dotmarketing.portlets.contentlet.model.Contentlet"%>
<%@page import="com.dotmarketing.portlets.containers.model.Container"%>
<%@page import="com.dotmarketing.portlets.htmlpages.model.HTMLPage"%>
<%@page import="com.dotmarketing.portlets.links.model.Link"%>
<%@page import="com.dotmarketing.portlets.templates.model.Template"%>
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
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.commons.beanutils.PropertyUtils"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="com.dotmarketing.business.PermissionAPI"%>
<%@ page import="com.dotmarketing.util.Config" %>
<%

	WorkflowTask task = (WorkflowTask) request.getAttribute (com.dotmarketing.util.WebKeys.WORKFLOW_TASK_EDIT);
	List<WorkflowComment> comments = WorkflowsFactory.getWorkflowCommentsOfTask (task);
	List<WorkflowHistory> history = WorkflowsFactory.getWorkflowHistoryOfTask (task);
	List<File> files = WorkflowsFactory.getWorkflowTaskFiles (task);

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/workflows/edit_workflow_task"});
	params.put("cmd",new String[] {"view"});
	params.put("inode",new String[] {String.valueOf(task.getInode())});
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	
	List<User> users = APILocator.getUserAPI().findAllUsers();
	PermissionAPI permAPI = APILocator.getPermissionAPI();
	WebAsset asset = null;
	Contentlet content = null;
	String actions ="";
    if (UtilMethods.isSet(task.getWebasset())) {
	    List inodes = new ArrayList();
	    if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (HTMLPage.class, "inode = '" + task.getWebasset()+"'")).size() > 0){
	        asset = (WebAsset) inodes.get(0);
	        if(permAPI.doesUserHavePermission(asset, permAPI.PERMISSION_PUBLISH,user)){
				if(!asset.isDeleted() && !asset.isLive()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"publish('"+task.getInode()+"','"+asset.getInode()+"');\" iconClass=\"publishIcon\">Publish</button>"+
					"<button dojoType=\"dijit.form.Button\" onClick=\"archive('"+task.getInode()+"','"+asset.getInode()+"');\" iconClass=\"archiveIcon\">Archive</button>";
		        }if(!asset.isDeleted() && asset.isLive()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"publish('"+task.getInode()+"','"+asset.getInode()+"');\"  iconClass=\"publishIcon\">Publish</button>" +
					"<button dojoType=\"dijit.form.Button\" onClick=\"unpublish('"+task.getInode()+"','"+asset.getInode()+"');\" iconClass=\"unpublishIcon\">Unpublish</button>";
		        }if(asset.isDeleted()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"unarchive('"+task.getInode()+"','"+asset.getInode()+"');\" iconClass=\"unarchiveIcon\">Unarchive</button>";
		        }
	        }
	    }else if (APILocator.getContentletAPI().isContentlet(task.getWebasset())){
	    	content = APILocator.getContentletAPI().find(task.getWebasset(),user, true);
	        actions = "<button id=\"buttondivContent"+content.getInode()+"_pop_up\" dojoType=\"dijit.form.Button\" onClick=\"dijit.byId('divContent"+content.getInode()+"_pop_up').show()\" iconClass=\"previewIcon\">View Content</button>";
		    if(permAPI.doesUserHavePermission(content, permAPI.PERMISSION_PUBLISH,user)){
				 if(!content.isArchived() && !content.isLive()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"publish('"+task.getInode()+"','"+content.getInode()+"');\" iconClass=\"publishIcon\">Publish</button>"+
					"<button dojoType=\"dijit.form.Button\" onClick=\"archive('"+task.getInode()+"','"+content.getInode()+"');\" iconClass=\"archiveIcon\">Archive</button>";
		        }if(!content.isArchived() && content.isLive()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"publish('"+task.getInode()+"','"+content.getInode()+"');\" iconClass=\"publishIcon\">Publish</button>"+
					"<button dojoType=\"dijit.form.Button\" onClick=\"unpublish('"+task.getInode()+"','"+content.getInode()+"');\" iconClass=\"unpublishIcon\">Unpublish</button>";
		        }if(content.isArchived()){
		        	actions = actions + "<button dojoType=\"dijit.form.Button\" onClick=\"unarchive('"+task.getInode()+"','"+content.getInode()+"');\" iconClass=\"unarchiveIcon\">UnArchive</button>";
		        }
	        }
			
	    }else if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (File.class, "inode = '" + task.getWebasset()+"'")).size() > 0){
	        asset = (WebAsset) inodes.get(0);
	    }else if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (Container.class, "inode = '" + task.getWebasset()+"'")).size() > 0){
	        asset = (WebAsset) inodes.get(0);
	    }else if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (Link.class, "inode = '" + task.getWebasset()+"'")).size() > 0){
	        asset = (WebAsset) inodes.get(0);
	    }else if ((inodes = (List) InodeFactory.getInodesOfClassByCondition (Template.class, "inode = '" + task.getWebasset()+"'")).size() > 0){
	        asset = (WebAsset) inodes.get(0);
	        
	    }
	}
	
%>

<script type="text/javascript" src="/html/js/dotCMS_EXT/dotcms_content_popup.js"></script>

<!-- Include the associated content action scripts -->
<%@ include file="/html/portlet/ext/workflows/workflows_js_inc.jsp" %>
<script language="javascript">

	dojo.require('dotcms.dojo.data.UsersReadStore');
	dojo.require('dijit.form.FilteringSelect');
	dojo.require('dotcms.dijit.FileBrowserDialog');

	
	function addComment () {
		var comment = document.getElementById("addCommentText").value;
		//document.getElementById("addCommentDiv").style.display = "none";
		
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
									<portlet:param name="cmd" value="add_comment" />
									<portlet:param name="referer" value="<%= referer %>" />
								</portlet:actionURL>&comment='+comment;
	}
	function showAssign () {
		document.getElementById("assignSelect").selectedIndex = -1;
		document.getElementById("assignDiv").style.display = "";
		document.getElementById("assignSelect").focus();
	}
	function hideAssign () {
		document.getElementById("assignSelect").selectedIndex = -1;
		document.getElementById("assignDiv").style.display = "none";
	}
	function assign () {
		var select = dijit.byId("assignSelect");
		//var value = select.options[select.selectedIndex].value;
		var value = select.getValue();
		document.getElementById("assignDiv").style.display = "none";
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
									<portlet:param name="cmd" value="assign_task" />
									<portlet:param name="referer" value="<%= referer %>" />
								</portlet:actionURL>&user_id='+value;
	}
	function assignToMe () {
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
									<portlet:param name="cmd" value="assign_task" />
									<portlet:param name="referer" value="<%= referer %>" />
								</portlet:actionURL>&user_id=user-<%= user.getUserId() %>';
	}	
	function changeStatus (newStatus) {
		if (confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-change-the-task-status")) %>')) {
			document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
									<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
									<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
									<portlet:param name="cmd" value="change_status" />
									<portlet:param name="referer" value="<%= referer %>" />
								</portlet:actionURL>&new_status='+newStatus;
		}
	}
	function attachFile(content,popup) {
		fileBrowser.show();
	}
	function attachFileCallback(file) {
		var fileInode = file.inode;
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
								<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
								<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
								<portlet:param name="cmd" value="add_file" />
								<portlet:param name="referer" value="<%= referer %>" />
							</portlet:actionURL>&file_inode='+fileInode;
	}
	
	function setImage(inode,name) 
	{
	   document.getElementById("attachedFileInode").value = inode;
	   submitParent();
	}

	function removeFile(fileInode) {
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
								<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
								<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
								<portlet:param name="cmd" value="remove_file" />
								<portlet:param name="referer" value="<%= referer %>" />
							</portlet:actionURL>&file_inode='+fileInode;
	}
	function editTask(){
		document.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />
							<portlet:param name="inode" value="<%= String.valueOf(task.getInode()) %>" />
							<portlet:param name="cmd" value='<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "edit")) %>' />
							<portlet:param name="referer" value="<%= referer %>" />
							</portlet:actionURL>';
	}
	function cancel () {
		document.location = "<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
		<portlet:param name="struts_action" value="/ext/workflows/view_workflow_tasks" />	
	    </portlet:actionURL>";
	}
</script>


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value='<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Workflow-Task-Detail")) %>' />

<style>
	a.actionsMenu{display:block;padding:3px 0 3px 5px;text-decoration:none;}
	a.actionsMenu:hover{background-color:#fcfcfc;}
</style>

<div class="yui-ge" style="margin-bottom:20px;">

<!-- START Task Overview -->
	<div class="yui-u first">
        
		<table class="listingTable">
			<tr>
				<th colspan="2"><h2><%= task.getTitle() %></h2></th>
				<th style="text-align:right;font-weight:normal;">
					<% if (user.getUserId().equals(task.getCreatedBy()) && (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString()))) { %>
						<button dojoType="dijit.form.Button" onClick="editTask();" iconClass="editIcon"><%= LanguageUtil.get(pageContext, "Edit-this-Task") %></button>
					<% } %>
				</th>
			</tr>
			
			<tr>
				<td>
					<strong><%= LanguageUtil.get(pageContext, "by") %>:</strong>
					<% if(UtilMethods.isSet(task.getCreatedBy()) && !APILocator.getUserAPI().loadUserById(task.getCreatedBy(),APILocator.getUserAPI().getSystemUser(),false).isNew()){%>
						<%= UtilMethods.getUserFullName(task.getCreatedBy()) %>
					<% } else  { %>
						<%= LanguageUtil.get(pageContext, "Nobody") %>	
					<% } %>
				</td>
				
				<td>
					<strong><%= LanguageUtil.get(pageContext, "Created-on") %>:</strong>
					<%= UtilMethods.dateToHTMLDate(task.getCreationDate()) %>
					<%= LanguageUtil.get(pageContext, "at") %> <%= UtilMethods.dateToHTMLTime(task.getCreationDate()) %> 
				</td>
				
				<td>
					<strong><%= LanguageUtil.get(pageContext, "Due") %>:</strong>
					<%= UtilMethods.dateToHTMLDate(task.getDueDate()) %>
				</td>
			</tr>
			
			<tr>
				<td>
					<strong><%= LanguageUtil.get(pageContext, "Assigned-To") %>:</strong>
					<%= UtilMethods.isSet(task.getBelongsTo())?APILocator.getRoleAPI().loadRoleById(task.getBelongsTo()).getName() + " (Role)":UtilMethods.getUserFullName(task.getAssignedTo()) %>
				</td>
				
				<td>
					<strong><%= LanguageUtil.get(pageContext, "Modified-on") %>:</strong>
					<%= UtilMethods.dateToHTMLDate(task.getModDate()) %> <%= LanguageUtil.get(pageContext, "at") %>  <%= UtilMethods.dateToHTMLTime(task.getModDate()) %>
				</td>
				
				<td>
					<strong><%= LanguageUtil.get(pageContext, "Status") %></strong>
					<%=task.getStatus().equals("OPEN")? LanguageUtil.get(pageContext, "OPEN"):task.getStatus().equals("RESOLVED")? LanguageUtil.get(pageContext, "RESOLVED"):LanguageUtil.get(pageContext, "CANCELLED") %>
				</td>
			</tr>
			
			<tr>
				<td colspan="3">
					<strong><%= LanguageUtil.get(pageContext, "Description") %>:</strong>
					<%=task.getDescription()%>
				</td>
			</tr>

			<% 
				if (asset != null || content != null ) {
					if (asset instanceof HTMLPage) {
						java.util.Map params2 = new java.util.HashMap();
						params2.put("struts_action",new String[] {"/ext/htmlpages/preview_htmlpage"});
						params2.put("previewPage",new String[] {"1"});
						params2.put("inode",new String[] {String.valueOf(asset.getInode())});
						params2.put("referer",new String[] {referer});
						String actionUrl = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params2);
			
						HTMLPage htmlPage = (HTMLPage) asset;
			%>
				<tr>
					<td colspan="3"> 
						<strong><%= LanguageUtil.get(pageContext, "Associated-Page") %>:</strong> 
						<a href="<%=actionUrl%>"><%= htmlPage.getTitle() %> (<%=htmlPage.getURI()%>)&nbsp;&nbsp;</a><%=actions%>
					</td>
				</tr>

			<% } else if (content != null) { %>
				<tr>
					<td colspan="3"> 
						<strong><%= LanguageUtil.get(pageContext, "Associated-Content") %>:</strong>
						
						

							

						<a href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
						<%if(content.getStructure().getName().equals("Event")){%>s
							<portlet:param name="struts_action" value="/ext/calendar/edit_event" />
						<%}else{%>
							<portlet:param name="struts_action" value="/ext/contentlet/edit_contentlet" />
						<%}%>
						<portlet:param name="inode" value="<%= String.valueOf(content.getInode()) %>" />
						<portlet:param name="cmd" value="edit" />
						<portlet:param name="referer" value="<%= referer %>" />
						</portlet:actionURL>">
							<span class="documentIcon"></span>
							<b>
								<%= APILocator.getContentletAPI().getName(content,APILocator.getUserAPI().getSystemUser(), false) %>
							</b>
						</a>
						<div style="float:right;">
							<%=actions%>
						</div>
						
					</td>
				</tr>

			<%
				} else if (asset instanceof Container) {
				Container container = (Container) asset;
			%>
				<tr>
					<td colspan="3">
						<strong><%= LanguageUtil.get(pageContext, "Associated-Container") %>:</strong>
						<%= container.getTitle() %>
					</td>
				</tr>
			<%
				} else if (asset instanceof Template) {
				Template template = (Template) asset;
			%>
				<tr>
					<td colspan="3">
						<strong><%= LanguageUtil.get(pageContext, "Associated-Template") %>:</strong>
						<%= template.getTitle() %>
					</td>
				</tr>
			<%
				} else if (asset instanceof Link) {
				Link link = (Link) asset;
			%>
				<tr>
					<td colspan="3">
						<strong><%= LanguageUtil.get(pageContext, "Associated-Link") %>:</strong>
						<%= link.getTitle() %>
					</td>
				</tr>
			<%
				} else if (asset instanceof File) {
				File file = (File) asset;
			%>
				<tr>
					<td colspan="3">
						<strong><%= LanguageUtil.get(pageContext, "Associated-File") %>:</strong>
						<%= file.getTitle() %> (<%= file.getURI() %>)
					</td>
				</tr>
			<% } %>
			<% } %>

		</table>
	</div>
<!-- END Task Overview -->

<!-- START Actions -->
	<div class="yui-u">
		<div class="callOutBox2" style="text-align:left;">
		<h3><%= LanguageUtil.get(pageContext, "Available-Workflow-Actions") %></h3>
	
			<% if (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) { %>
				<a class="actionsMenu" href="javascript: changeStatus ('<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.RESOLVED.toString() %>')">
					<span class="reopenWorkflowIcon"></span>
					<%= LanguageUtil.get(pageContext, "Resolve") %>
				</a>
			<% } %>
					
			<% if ( task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) { %>
				<a class="actionsMenu" href="javascript: showAssign ();">
					<span class="assignWorkflowIcon"></span>
					<%= LanguageUtil.get(pageContext, "Assign") %>
				</a>
				<% if (!task.getAssignedTo().equals(user.getUserId()) && task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) {%>
						<a class="actionsMenu" href="javascript: assignToMe ();">
							<span class="assignWorkflowIcon"></span>
							<%= LanguageUtil.get(pageContext, "To-Me") %>
						</a>
				<% } %>
				
				<div id="assignDiv" class="shadowBox callOutBox2" style="position:absolute;display:none;z-index:5;"> 
					<div id="assignToTD">
						<%
					    	int permissionsToCheck = PermissionAPI.PERMISSION_WRITE;
					    	boolean assignToViewers = Config.getBooleanProperty("ASSIGN_TASKS_TO_USERS_W_READ",false);
					    	if(assignToViewers)
					    	{
					    		permissionsToCheck = PermissionAPI.PERMISSION_READ;
					    	}
					    	 
					    %>
					
						<div dojoType="dotcms.dojo.data.UsersReadStore" jsId="usersRolesStore" 
						includeRoles="true"  hideSystemRoles="true" 
							<%= UtilMethods.isSet(asset) && UtilMethods.isSet(asset.getInode())?"asset=\"" + asset.getInode() + "\" ":"" %> permission="<%= permissionsToCheck %>">
						</div>
						<select id="assignSelect" name="assignSelect" dojoType="dijit.form.FilteringSelect" 
								store="usersRolesStore" searchDelay="300" pageSize="30" labelAttr="name" 
								invalidMessage="<%= LanguageUtil.get(pageContext, "Invalid-option-selected") %>"
						></select>

					</div>
					<div class="buttonRow">
                        <button dojoType="dijit.form.Button" onClick="assign()"><%= LanguageUtil.get(pageContext, "Assign") %></button>
                        <button dojoType="dijit.form.Button" onClick="hideAssign()"><%= LanguageUtil.get(pageContext, "Cancel") %></button>
					</div>
				</div>
				
			<% } %>
			
			<% if (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) { %>
				<a class="actionsMenu" href="javascript: changeStatus ('<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.CANCELLED.toString() %>')">
					<span class="cancelWorkflowIcon"></span>
					<%= LanguageUtil.get(pageContext, "Cancel-Task") %>
				</a>
			<% } %>		
			
			<% if (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.CANCELLED.toString()) || task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.RESOLVED.toString())) { %>
				<a class="actionsMenu"href="javascript: changeStatus ('<%= com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString() %>')">
					<span class="reopenWorkflowIcon"></span>
					<%= LanguageUtil.get(pageContext, "Reopen-Task") %>
				</a>
				
				<a class="actionsMenu"href="javascript: deleteWorkFlowTask('<%=task.getInode()%>')">
					<span class="deleteWorkflowIcon"></span>
					<%= LanguageUtil.get(pageContext, "Delete-Task") %>
				</a>
			<% } %>
		</div>
	</div>
<!-- END Actions -->	

</div>

<!-- START Tabs -->
	<div id="mainTabContainer" dolayout="false" dojoType="dijit.layout.TabContainer">
	
	<!-- START Comments Tab -->
		<div id="TabOne" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Comments") %>">

			<div class="buttonRow" style="text-align:right;">
				<%if (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) {%>
					<div dojoType="dijit.form.DropDownButton" iconClass="plusIcon">
						<span><%= LanguageUtil.get(pageContext, "Add-a-Comment") %></span>
						<div dojoType="dijit.TooltipDialog" id="dialog1" title="Login Form" execute="addComment();">
							<textarea id="addCommentText" rows="4" cols="60"></textarea>
							<div class="buttonRow">
                                <button dojoType="dijit.form.Button" type="button" onClick="addComment();" iconClass="infoIcon">
								    <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-Comment")) %>
                                </button>
							</div>
						</div>
					</div>
				<%}%>
			</div>

				
				<table class="listingTable">

				<%   
				    String str_style2="";
					int y =0;
					
					Iterator<WorkflowComment> commentsIt = comments.iterator();
					while (commentsIt.hasNext()) {
						WorkflowComment comment = commentsIt.next();
						
						if(y%2==0){
						  str_style2="class=\"alternate_1\"";
						}
						else{
						  str_style2="class=\"alternate_2\"";
						}
						y++;
				%>
					<tr <%=str_style2 %>>
						<td>
							<p>
								<strong><%= LanguageUtil.get(pageContext, "Comment-By") %>:</strong> <%= UtilMethods.getUserFullName(comment.getPostedBy()) %><br/>
								<strong><%= LanguageUtil.get(pageContext, "Created-on") %>:</strong> <%= UtilMethods.dateToHTMLDate(comment.getCreationDate()) %><br/>
								<%= comment.getComment() %><%if (commentsIt.hasNext()) { %><% } %>
							</p>
						</td>
					</tr>
				<% } %>

				<%	if (comments.size() == 0) { %>
					<tr>
						<td>
							<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "None") %></div>
						</td>
					</tr>
				<% } %>
				
				</table>
				
			<!-- END Comments -->
			
		</div>
	<!-- END Description Tab -->
	
	<!-- START Files Tab -->
		<div id="TabTwo" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Attached-Files") %>">
			
			<% if (task.getStatus().equals (com.dotmarketing.util.WebKeys.WorkflowStatuses.OPEN.toString())) { %>
				<div class="buttonRow" style="Text-align:right;">
					<button dojoType="dijit.form.Button" onClick="attachFile();" iconClass="browseIcon"><%= LanguageUtil.get(pageContext, "Attach-File") %></button> 
				</div>
			<% } else { %>
				<div class="buttonRow" style="Text-align:right;">
					<%= LanguageUtil.get(pageContext, "Attached-Files") %>
				</div>
			<% } %>
			
			<table class="listingTable">
				<%   
					int x=0;
					String str_style="";
					
					for (File file : files) {
						if(x%2==0){
						  str_style="class=\"alternate_1\"";
						}
						else{
						  str_style="class=\"alternate_2\"";
						}
						x++;
				%>
					<tr <%=str_style %>>
						<td>
							<img border="0" src="/icon?i=<%= UtilMethods.encodeURIComponent(file.getFileName()) %>"> &nbsp;
							<a href="#" onclick="javascript:window.open('<%= UtilMethods.encodeURIComponent("/dotAsset/" + file.getIdentifier()) %>','FileView','toolbar=no,menubar=no,toolbar=no,scrollbars=no,width=<%=file.getWidth() %>,height=<%=file.getHeight() %>');">
								<%= file.getFileName() %>
							</a> 
						</td>
						<td><a href="javascript:removeFile('<%= file.getInode() %>')"><%= LanguageUtil.get(pageContext, "remove") %></a></td>
					</tr>
				<% } %> 
				
				<% if (files.size() == 0) { %>
					<tr>
						<td colspan="2">
							<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "None") %></div>
						</td>
					</tr>
				<% } %>
			</table>
		</div>
	<!-- END Files Tab -->
	

	
	<!-- START History Tab -->
		<div id="TabThree" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Change-History") %>">
			<table class="listingTable">
				<%  
				    int z=0;
					String str_style3="";
							
					for (WorkflowHistory histItem : history) {
					
					  if(z%2==0){
					    str_style3="class=\"alternate_1\"";
			          }
					  else{
					    str_style3="class=\"alternate_2\"";
					  }
					  z++;
				%>
					<tr <%=str_style3 %>>
						<td>
							<strong><%= LanguageUtil.get(pageContext, "Change-by") %>:</strong> <%= UtilMethods.getUserFullName(histItem.getMadeBy()) %> (<%= UtilMethods.dateToHTMLDate(histItem.getCreationDate()) %> <%= UtilMethods.dateToHTMLTime(histItem.getCreationDate()) %>)<br>
							<%= histItem.getChangeDescription() %>
						</td>
					</tr>
				<% } %>
				
				<% if (history.size() == 0) { %>
					<tr>
						<td>
							<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-history-yet-on-this-task.") %></div>
						</td>
					</tr>
				<% } %>
			</table>
		</div>
	<!-- END History Tab -->
</div>

</liferay:box>

<div dojoAttachPoint="fileBrowser" jsId="fileBrowser" onFileSelected="attachFileCallback" onlyFiles="true" dojoType="dotcms.dijit.FileBrowserDialog">
</div>

<% if(content != null){ %>
	<%@ include file="/html/portlet/ext/contentlet/view_contentlet_popup_inc.jsp" %>
<%} %>
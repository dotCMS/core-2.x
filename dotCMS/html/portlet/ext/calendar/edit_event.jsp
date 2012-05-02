<%@ include file="/html/portlet/ext/contentlet/init.jsp"%>

<%@page import="com.dotmarketing.portlets.categories.business.CategoryAPI"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="com.dotmarketing.portlets.containers.model.Container"%>
<%@page import="com.dotmarketing.portlets.contentlet.struts.ContentletForm"%>
<%@page import="com.dotmarketing.portlets.calendar.struts.EventForm"%>
<%@page import="com.dotmarketing.portlets.structure.model.Field"%>
<%@page import="com.dotmarketing.portlets.entities.factories.EntityFactory"%>
<%@page import="com.dotmarketing.portlets.structure.model.ContentletRelationships"%>
<%@page import="com.dotmarketing.portlets.structure.model.ContentletRelationships.ContentletRelationshipRecords"%>
<%@page import="com.dotmarketing.portlets.categories.model.Category"%>
<%@page import="com.dotmarketing.portlets.entities.model.Entity"%>
<%@page import="com.dotmarketing.portlets.languagesmanager.model.Language"%>
<%@page import="com.dotmarketing.util.UtilMethods" %>
<%@page import="com.dotmarketing.util.InodeUtils" %>
<%@page import="com.liferay.portal.language.LanguageUtil" %>
<%@page import="com.dotmarketing.portlets.contentlet.business.ContentletAPI"%>
<%@page import="com.dotmarketing.portlets.structure.model.Structure"%>
<%@page import="com.dotmarketing.portlets.structure.factories.StructureFactory"%>
<%@page import="com.dotmarketing.business.PermissionAPI"%>
<%@page import="com.dotmarketing.business.Role"%>

<%
	//this file is a copy of the edit_contentlet.jsp that has
	//some modifications to support events custom fields and actions
 %>

<%
	PermissionAPI conPerAPI = APILocator.getPermissionAPI();
	ContentletAPI conAPI = APILocator.getContentletAPI();

	Contentlet contentlet = request.getAttribute(com.dotmarketing.util.WebKeys.CONTENTLET_EDIT) != null ?(Contentlet) request.getAttribute(com.dotmarketing.util.WebKeys.CONTENTLET_EDIT) :	conAPI.find(request.getParameter("inode"),user,false);

	Date dateOfStart = contentlet.getDateProperty("startDate");

	Date dateOfEnd = contentlet.getDateProperty("endDate");

	EventForm contentletForm = (EventForm) session.getAttribute("CalendarEventForm");

	//Content structure or user selected structure
	Structure structure = contentletForm.getStructure();
	if (!InodeUtils.isSet(structure.getInode())){
		structure = StructureFactory.getStructureByInode(request.getParameter("sibblingStructure"));
	}
	List<Field> fields = structure.getFields();

	//Categories
	Entity entity = EntityFactory.getEntity(structure.getName());
	List<Category> entityCategories = EntityFactory.getEntityCategories(entity);
	String[] selectedCategories = contentletForm.getCategories ();

	//Contentlet relationships
	ContentletRelationships contentletRelationships = (ContentletRelationships)
		request.getAttribute(com.dotmarketing.util.WebKeys.CONTENTLET_RELATIONSHIPS_EDIT);
	List<ContentletRelationships.ContentletRelationshipRecords> relationshipRecords = contentletRelationships.getRelationshipsRecords();

	//Contentlet references
	List<Map<String, Object>> references = null;
	try{
		references = conAPI.getContentletReferences(contentlet, user, false);
	}catch(DotContentletStateException dse){
		references = new ArrayList<Map<String, Object>>();
	}

	//This variable controls the name of the struts action used when the form is submitted
	//the normal action is /ext/contentlet/edit_contentlet but that can be changed
	String formAction = request.getParameter("struts_action") == null?"/ext/contentlet/edit_contentlet":request.getParameter("struts_action");

	//Variable used to return after the work is done with the contentlet
	String referer = "";
	if (request.getParameter("referer") != null) {
		referer = request.getParameter("referer");
	} else {
		Map params = new HashMap();
		params.put("struts_action",new String[] {"/ext/contentlet/edit_contentlet"});
		params.put("inode",new String[] { contentlet.getInode() + "" });
		params.put("cmd",new String[] { Constants.EDIT });
		referer = PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	}



	//Setting request attributes used by the included jsp pages
	request.setAttribute("contentlet", contentlet);
	request.setAttribute("contentletForm", contentletForm);
	request.setAttribute("structure", structure);
	request.setAttribute("selectedCategories", selectedCategories);
	request.setAttribute("entityCategories", entityCategories);
	request.setAttribute("relationshipRecords", relationshipRecords);
	request.setAttribute("references", references);
	request.setAttribute("referer", referer);
	request.setAttribute("fields", fields);


	boolean canEditAsset = conPerAPI.doesUserHavePermission(contentlet, PermissionAPI.PERMISSION_EDIT_PERMISSIONS, user);
	Integer catCounter = 0;
%>

<!-- global included dependencies -->


<%@ include file="/html/portlet/ext/contentlet/edit_contentlet_js_inc.jsp" %>
<%@ include file="/html/portlet/ext/contentlet/field/edit_field_js.jsp" %>



<html:form action="<%= formAction %>" styleId="fm">


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"edit-event\") %>" />

	<!--  DOTCMS-5175 -->
	<div  dojoAttachPoint="cmsFileBrowserImage"
			currentView="thumbnails"
			jsId="cmsFileBrowserImage"
			onFileSelected="addFileImageCallback"
			mimeTypes="image"
			dojoType="dotcms.dijit.FileBrowserDialog">
	</div>

	<div  dojoAttachPoint="cmsFileBrowserFile" currentView="list" jsId="cmsFileBrowserFile" onFileSelected="addFileCallback"
	  dojoType="dotcms.dijit.FileBrowserDialog" >
	</div>

	<div id="mainTabContainer" dolayout="false" dojoType="dijit.layout.TabContainer">

	<!--  Contentlet structure fields -->
	<% if(fields != null && fields.size()>0 &&  fields.get(0) != null && fields.get(0).getFieldType().equals(Field.FieldType.TAB_DIVIDER.toString())){
			Field f0 = fields.get(0);
			fields.remove(0);
	%>
		<div id="<%=f0.getFieldContentlet()%>" dojoType="dijit.layout.ContentPane" title="<%=f0.getFieldName()%>" onShow="showEditButtonsRow()" >
	<% } else { %>
		<div id="properties" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Content") %>" onShow="showEditButtonsRow()" >
	<% } %>

	<jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_basic_properties.jsp" />

<%
	/*### DRAW THE DYNAMIC FIELDS ###*/

	int counter = 0;
	boolean lineDividerOpen = false;
	boolean tabDividerOpen  = false;
	boolean categoriesTabFieldExists = false;
	boolean permissionsTabFieldExists = false;
	boolean relationshipsTabFieldExists = false;

	/* Events code only */
	Field startDateField = null;
	Field endDateField = null;
	Field locationField = new Field();

	/* End of Events code only */

   	for (Field f : fields) {
   		//out.println(f.getFieldName() + ":" + f.getFieldType() + "<BR>");
		if("hidden".equals(f.getFieldType())){

			continue;
		}
    	if(f.getFieldType().equals(Field.FieldType.LINE_DIVIDER.toString())) {
    		if(lineDividerOpen) { %>
				<%lineDividerOpen = false; %>
			<%}%>
			<div class="lineDividerTitle"><%=f.getFieldName() %></div>
				<% lineDividerOpen = true; %>
   		<% } else if(f.getFieldType().equals(Field.FieldType.TAB_DIVIDER.toString())) {
    			tabDividerOpen = true;
	    		if(lineDividerOpen) { %>
					<%lineDividerOpen = false; %>
				<%}%>
			</div>
			<div id="<%=f.getFieldContentlet()%>" dojoType="dijit.layout.ContentPane" title="<%=f.getFieldName()%>" onShow="showEditButtonsRow()" >
		<% } else if(f.getFieldType().equals(Field.FieldType.CATEGORIES_TAB.toString())) {
   	    	categoriesTabFieldExists = true;%>
 			<jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_categories.jsp" />
		<% } else if(f.getFieldType().equals(Field.FieldType.PERMISSIONS_TAB.toString())){
    	  	permissionsTabFieldExists = true;
			request.setAttribute(com.dotmarketing.util.WebKeys.PERMISSIONABLE_EDIT, contentlet);
			request.setAttribute(com.dotmarketing.util.WebKeys.PERMISSIONABLE_EDIT_BASE, structure);%>

			<%@ include file="/html/portlet/ext/common/edit_permissions_tab_inc.jsp" %>
		<% } else if(f.getFieldType().equals(Field.FieldType.RELATIONSHIPS_TAB.toString())){%>
    	   	<%if(counter==0){%>
				<% relationshipsTabFieldExists =  true; %>                    <jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_relationships.jsp" />
			<%}%>
    	   <%counter++;%>
		<% } else  {
			request.setAttribute("field", f);
    	  	Object formValue = null;
    	  	if(f.getFieldType().equals(Field.FieldType.CATEGORY.toString())) {
    			CategoryAPI catAPI = APILocator.getCategoryAPI();
    			formValue =  (List<Category>) catAPI.getParents(contentlet, user, false);
    			catCounter++;
    	  	} else {
    			formValue = (Object) contentletForm.getFieldValueByVar(f.getVelocityVarName());
    	  	}
    	  	request.setAttribute("value", formValue);

    	  	if (f.getFieldType().equals(Field.FieldType.WYSIWYG.toString())) {
    			List<String> disabled = contentlet.getDisabledWysiwyg();
    			if(InodeUtils.isSet(contentlet.getInode()) && disabled.contains(f.getFieldContentlet())) {
    				request.setAttribute("wysiwygDisabled", true);
    			} else {
    				request.setAttribute("wysiwygDisabled", false);
    			}
    	  	}

    	  	/* Calendar special fields */
			if(f.getVelocityVarName().equals("startDate")) {
				startDateField = f;%>
				<jsp:include page="/html/portlet/ext/calendar/edit_event_start_date_field.jsp" />
			<%} else if (f.getVelocityVarName().equals("location")) {
				locationField = f;%>
				<jsp:include page="/html/portlet/ext/calendar/edit_event_location_field.jsp" />
			<%	} else { /* END Calendar special fields */
	    	  	if(f.getFieldType().equals(Field.FieldType.HOST_OR_FOLDER.toString())){
	    	  		if(InodeUtils.isSet(contentlet.getHost())) {
						request.setAttribute("host",contentlet.getHost());
						request.setAttribute("folder",contentlet.getFolder());
	    	  		} else {
	    	  			String hostId = (String) session.getAttribute(com.dotmarketing.util.WebKeys.CMS_SELECTED_HOST_ID);
						request.setAttribute("host", hostId);
						request.setAttribute("folder", null);
	    	  		}
		  	    }
		  	    request.setAttribute("inode",contentlet.getInode());
		  	  	request.setAttribute("counter", catCounter.toString());
		  	    %>


				<jsp:include page="/html/portlet/ext/contentlet/field/edit_field.jsp" />
				<%-- END DATE is followed by the recurrance jsp --%>
				<%if(f.getVelocityVarName().equals("endDate")) {
					endDateField = f;%>
					<%@ include file="/html/portlet/ext/calendar/edit_event_recurrence_inc.jsp" %>
				<%}%>
			<%}%>
		<%}%>
   	<%}%>

	<%@ include file="/html/portlet/ext/calendar/edit_event_js_inc.jsp" %>
</div>
<!-- END Contentlet Properties -->

<!-- Contentlet categories Tab -->
<% if(categoriesTabFieldExists){ %>
	<div id="categories" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Categories") %>" onShow="showEditButtonsRow()" >
		<jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_categories.jsp" />
	</div>
<% } %>
<!-- END Contentlet categories Tab -->

<!-- Relationships Tab -->
    <% if(relationshipRecords != null && relationshipRecords.size() > 0 && !relationshipsTabFieldExists){
        relationshipsTabFieldExists = true;%>
		<div id="relationships" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Relationships") %>" onShow="showEditButtonsRow()" >
			<jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_relationships.jsp" />
		</div>
	<% } %>
<!-- Relationships Tab -->

<!-- Permissions Tab -->
	<% if(!permissionsTabFieldExists && canEditAsset){ %>
		<div id="permissions" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Permissions") %>" onShow="hideEditButtonsRow()" >
			<%
				request.setAttribute(com.dotmarketing.util.WebKeys.PERMISSIONABLE_EDIT, contentlet);
				request.setAttribute(com.dotmarketing.util.WebKeys.PERMISSIONABLE_EDIT_BASE, structure);
			%>
			<%@ include file="/html/portlet/ext/common/edit_permissions_tab_inc.jsp" %>
		</div>
    <% } %>
<!-- END Permissions Tab -->

<!-- Versions Tab -->
	<div id="versions" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Versions") %>" onShow="showEditButtonsRow()">
		<%@ include	file="/html/portlet/ext/common/edit_versions_inc.jsp"%>
	</div>
<!-- END Versions Tab -->

<!-- References Tab -->
	<%if(references != null && references.size() > 0){ %>
		<div id="references" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "References") %>" onShow="showEditButtonsRow()" >
			<jsp:include page="/html/portlet/ext/contentlet/edit_contentlet_references.jsp" />
		</div>
	<%}%>
<!-- END References Tab -->

</div>

<div class="clear"></div>
<!--  action buttons -->

<% if (InodeUtils.isSet(structure.getInode())) { %>

<div class="buttonRow" id="editEventButtonRow">

<%
	//check permissions to display the save and publish button or not
	boolean canUserWriteToContentlet = conPerAPI.doesUserHavePermission(contentlet,PermissionAPI.PERMISSION_WRITE,user);
%>
<% if (InodeUtils.isSet(contentlet.getInode()) && canUserWriteToContentlet) { %>

	<% if (!InodeUtils.isSet(contentlet.getInode()) || contentlet.isLive() || contentlet.isWorking()) { %>

		<% if (InodeUtils.isSet(contentlet.getInode())) { %>
	        <button dojoType="dijit.form.Button" onClick="copyContentlet()" type="button" iconClass="copyIcon">
	           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Copy")) %>
	        </button>
		<% } %>

        <button dojoType="dijit.form.Button" onClick="openAssignTo()" type="button" iconClass="saveAssignIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save---Assign")) %>
        </button>

        <button dojoType="dijit.form.Button" onClick="submitfmEvent(document.getElementById('fm'),'', <%=catCounter %>)" type="button" iconClass="saveIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
        </button>

	<% } else if (InodeUtils.isSet(contentlet.getInode())) { %>
        <button dojoType="dijit.form.Button" onClick="selectVersion(<%=contentlet.getInode()%>)" type="button" iconClass="resetIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Bring-Back-Version")) %>
        </button>
	<% } %>

<% } else if (!InodeUtils.isSet(contentlet.getInode())) { %>

        <button dojoType="dijit.form.Button" onClick="openAssignTo()" type="button" iconClass="saveAssignIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save---Assign")) %>
        </button>

        <button dojoType="dijit.form.Button" onClick="submitfmEvent(document.getElementById('fm'),'', <%=catCounter %>)" type="button" iconClass="saveIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
        </button>
<% } %>

<%
	//If the user has permissions to publish
	//A special case happens when the contentlet is new and CMS owner has permissions to publish
	//Then the save and publish button should appear
	boolean canUserPublishContentlet = conPerAPI.doesUserHavePermission(contentlet,PermissionAPI.PERMISSION_PUBLISH,user);
	if(!InodeUtils.isSet(contentlet.getInode())) {
		canUserPublishContentlet = conPerAPI.doesUserHavePermission(structure,PermissionAPI.PERMISSION_PUBLISH,user);
		if(!canUserPublishContentlet){
			canUserPublishContentlet = conPerAPI.doesRoleHavePermission(structure, PermissionAPI.PERMISSION_PUBLISH,com.dotmarketing.business.APILocator.getRoleAPI().loadCMSOwnerRole());
		}
	}
%>

<% if (canUserPublishContentlet) { %>
    <button dojoType="dijit.form.Button"  onClick="submitfmEvent(document.getElementById('fm'),'publish', <%=catCounter %>)" type="button" iconClass="publishIcon">
        <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save-Publish")) %>
    </button>
<% } %>

    <button dojoType="dijit.form.Button" onClick="cancelEdit()" type="button" iconClass="cancelIcon">
       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Cancel")) %>
    </button>

</div>

<% } %>

</liferay:box>

	<!-- hidden div to assign a task and save-->
	<input name="taskAssignment" id="taskAssignment" type="hidden" value="">
	<input name="taskComments" id="taskComments" type="hidden" value="">
	<!-- hidden div to assign a task and savev-->
	<div class="assignToDiv" id="assignTaskDiv"  dojoType="dijit.Dialog" style="display: none">
		<div style="padding:10px;padding-left:50px;border:0px solid gray;" id="assignTaskInnerDiv">
			<b><%= LanguageUtil.get(pageContext, "Workflow-Task-Info") %></b>
				<b><%= LanguageUtil.get(pageContext, "Comments") %>:</b><br />
				<textarea name="taskCommentsAux" id="taskCommentsAux" cols=40 rows=8><%= LanguageUtil.get(pageContext, "default-workflow-comment") %></textarea>
				<br />
				<br />
				<b><%= LanguageUtil.get(pageContext, "Assignee") %>: </b>
				<%


					boolean assignToViewers = Config.getBooleanProperty("ASSIGN_TASKS_TO_USERS_W_READ",false);
					int permissionsToCheck = assignToViewers?PermissionAPI.PERMISSION_READ:PermissionAPI.PERMISSION_WRITE;

					Set<Role> roles = null;
					Set<User> users = null;

				%>
			<script type="text/javascript">
				var assignUsersAssetInode = "<%=contentlet.getInode()%>";
				var assignUsersStore = new dotcms.dojo.data.UsersReadStore({ includeRoles: true, assetInode: assignUsersAssetInode, hideSystemRoles:true, permission: "<%= permissionsToCheck %>" });
			</script>
			<select id="taskAssignmentAux" name="taskAssignmentAux" dojoType="dijit.form.FilteringSelect"
					store="assignUsersStore" searchDelay="300" pageSize="30" labelAttr="name"
					invalidMessage="<%= LanguageUtil.get(pageContext, "Invalid-option-selected") %>">
			</select>

			<br />
			<br />
			<center>
                <button dojoType="dijit.form.Button" iconClass="saveAssignIcon" onClick="submitfm(document.getElementById('fm'),'assignto', <%=catCounter %>);" type="button">
                    <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>
                </button>
                <button dojoType="dijit.form.Button" iconClass="cancelIcon" onClick="cancelAssignTo()" type="button">
                    <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %>
                </button>
			</center>
		</div>
	</div>
</html:form>

<%--
if (!(dateOfStart==null) && !(dateOfEnd==null) && (dateOfStart.getHours() == 0) && (dateOfStart.getMinutes() == 0) && (dateOfEnd.getHours() == 23) && (dateOfEnd.getMinutes() == 59)){
%>

<script type="text/javascript">
		document.getElementById('alldayevent').checked=true;
		setAllDayEvent();
</script>


<% } --%>
<%-- http://jira.dotmarketing.net/browse/DOTCMS-2273 --%>
<!-- To show lightbox effect "Saving Content.."  -->
<div id="savingContentDialog" dojoType="dijit.Dialog" title="<%= LanguageUtil.get(pageContext, "saving-content") %>" style="display: none;">
	<div dojoType="dijit.ProgressBar" style="width:200px;text-align:center;" indeterminate="true" jsId="saveProgress" id="saveProgress"></div>
</div>
<script type="text/javascript">
	dojo.addOnLoad(function () {
		dojo.style(dijit.byId('savingContentDialog').closeButtonNode, 'visibility', 'hidden');
	});

</script>

<div id="saveContentErrors" style="display: none;" dojoType="dijit.Dialog">
	<div dojoType="dijit.layout.ContentPane" id="exceptionData" hasShadow="true"></div>
	<div class="formRow" style="text-align:center">
		<button dojoType="dijit.form.Button"  onClick="dijit.byId('saveContentErrors').hide()" type="button"><%= LanguageUtil.get(pageContext, "close") %></button>
	</div>
</div>



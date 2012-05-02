<%@ include file="/html/portlet/ext/entities/init.jsp" %>
<%@ page import="com.dotmarketing.util.UtilMethods"%>
<%@ page import="com.dotmarketing.util.UtilMethods"%>
<%@ page import="com.dotmarketing.util.InodeUtils" %>
<%
com.dotmarketing.portlets.entities.model.Entity entity;
if (request.getAttribute(com.dotmarketing.util.WebKeys.ENTITY_EDIT)!=null) {
	entity = (com.dotmarketing.portlets.entities.model.Entity) request.getAttribute(com.dotmarketing.util.WebKeys.ENTITY_EDIT);
}
else {
	entity = (com.dotmarketing.portlets.entities.model.Entity) com.dotmarketing.factories.InodeFactory.getInode(request.getParameter("inode"),com.dotmarketing.portlets.entities.model.Entity.class);
}
%>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"Add-/Edit\") %>" />

<script language="Javascript">
function submitfm(formId) {
		form = document.getElementById(formId);
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/entities/edit_entity" /></portlet:actionURL>';
		submitForm(form);
}
function doDelete() {
		if(!confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.entity.confirm.delete")) %>')){
			return false;
		}
		form = document.getElementById("fm");
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/entities/edit_entity" /></portlet:actionURL>';
		form.cmd.value="delete";
		submitForm(form);
}
function cancel()
{
	var href = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
	href += "<portlet:param name='struts_action' value='/ext/entities/view_entities' />";
	href += "</portlet:renderURL>";
	document.location.href = href;
}
</script>

<div class="headerBox shadowBoxLine">
<html:form action='/ext/entities/edit_entity' styleId="fm">
	<input name="<%= Constants.CMD %>" type="hidden" value="save">
	<html:hidden property="inode" />
	<input name="redirect" type="hidden" value="<portlet:actionURL  windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/entities/view_entities" /></portlet:actionURL>">

	<dl style="margin-top:45px;">
		<dt><%= LanguageUtil.get(pageContext,"Category-Group-Name") %></dt>
		<dd><html:text styleClass="form-text" property="entityName" /></dd>		
		<dd class="inputCaption"><%= LanguageUtil.get(pageContext,"To-select/deselect") %></dd>
	
		<dt><%= LanguageUtil.get(pageContext,"Categories") %></dt>
		<dd>
			<html:select property="categories" styleClass="selectMulti" size='20' style="width:300px;" multiple='true'>
				<%= com.dotmarketing.util.UtilHTML.getSelectCategories(entity, user, false) %>
			</html:select>
			<div class="callOutBox2 hintBox" style="top:250px;"><%= LanguageUtil.get(pageContext,"Note-") %></div>
		</dd>
	</dl>
	
	<div class="buttonRow">
		<%if(InodeUtils.isSet(entity.getInode())){%>
			<button dojoType="dijit.form.Button" onClick="doDelete()" iconClass="deleteIcon">
				<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "delete")) %>
			</button>
		<%}%>
	
		<button dojoType="dijit.form.Button" onClick="submitfm('fm')" iconClass="saveIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>
		</button>
		
		<button type="button" dojoType="dijit.form.Button" onClick="cancel();" iconClass="cancelIcon">
			Cancel
		</button>
	</div>
	
	

</html:form>

</liferay:box>

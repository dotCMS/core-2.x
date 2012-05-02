<%@page import="com.dotmarketing.util.UtilMethods"%>
<%@page import="com.liferay.portal.language.LanguageUtil"%>
<script language="Javascript">
	function publish (objId,assetId) {
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-publish-this-Associated-Type")) %>')){	
			var href = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
			href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />'
			href = href + '<portlet:param name="cmd" value="publish" />';
			href = href + '<portlet:param name="referer" value="<%= referer %>" />';
			href = href + '</portlet:actionURL>&inode='+objId+'&asset_inode='+assetId;
		
			document.location.href = href;
		}
	}
		
	function unpublish(objId,assetId) {
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-un-publish-this-Associated-Type")) %>')){
			var href = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
			href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />'
			href = href + '<portlet:param name="cmd" value="unpublish" />';
			href = href + '<portlet:param name="referer" value="<%= referer %>" />';
			href = href + '</portlet:actionURL>&inode='+objId+'&asset_inode='+assetId;
		
			document.location.href = href;
		}
	}

	
	function archive (objId, assetId) {
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-archive-this-Associated-Type")) %>')){
	   		var href = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
			href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />'
			href = href + '<portlet:param name="cmd" value="delete" />';
			href = href + '<portlet:param name="referer" value="<%= referer %>" />';
			href = href + '</portlet:actionURL>&inode='+objId+'&asset_inode='+assetId;
		
			document.location.href = href;
		}
	}


	function unarchive (objId, assetId) {
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-un-archive-this-Associated-Type")) %>')){
			var href = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">'
			href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />'
			href = href + '<portlet:param name="cmd" value="undelete" />';
			href = href + '<portlet:param name="referer" value="<%= referer %>" />';
			href = href + '</portlet:actionURL>&inode='+objId+'&asset_inode='+assetId;
		
			document.location.href = href;
		}
	}
	
	function previewHTMLPage (objId, referer) {
		top.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/htmlpages/preview_htmlpage" /><portlet:param name="previewPage" value="1" /></portlet:actionURL>&inode=' + objId + '&referer=' + referer;
	}
	
	function deleteWorkFlowTask(inode) {
	<%
		java.util.Map viewParams = new java.util.HashMap();
		viewParams.put("struts_action",new String[] {"/ext/workflows/view_workflow_tasks"});
		String viewReferer = com.dotmarketing.util.PortletURLUtil.getRenderURL(request,WindowState.MAXIMIZED.toString(),viewParams);
	%>
	  if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-workflow-task")) %>')){
			var href= '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">';
			href = href + '<portlet:param name="struts_action" value="/ext/workflows/edit_workflow_task" />';
			href = href + '<portlet:param name="cmd" value="full_delete" />';
			href = href + '<portlet:param name="referer" value="<%= viewReferer %>" />';
			href = href + '</portlet:actionURL>&inode='+inode;
			
			document.location.href = href;
		}
	}
</script>
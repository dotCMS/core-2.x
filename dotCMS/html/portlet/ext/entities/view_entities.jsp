<%@ include file="/html/portlet/ext/entities/init.jsp" %>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.liferay.portal.language.LanguageUtil"%>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.liferay.portal.language.LanguageUtil"%>
<%
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/entities/view_entities"});
	
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.dotmarketing.portlets.categories.model.Category"%>
<script>
function deleteEntity(inode) {
	if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.category.confirm.delete.categorygroup")) %>')){
		window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="cmd" value="<%= com.liferay.portal.util.Constants.DELETE %>" /><portlet:param name="struts_action" value="/ext/entities/edit_entity" /><portlet:param name="referer" value="<%=referer%>" /></portlet:actionURL>&inode=' + inode;
	} 
}

dojo.addOnLoad(function () {
	showDotCMSSystemMessage('<div class=\"messageIcon exclamation\"></div>  <%= LanguageUtil.get(pageContext, "message.categorygroup.depricated") %>');
});
</script>


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"category-groups\") %>" />
 

	<table class="listingTable">
		<tr>
			<th align="center" width="60"><%= LanguageUtil.get(pageContext, "Actions") %></th>						
			<th><%= LanguageUtil.get(pageContext, "Category-Group-Name") %></th>
			<th><%= LanguageUtil.get(pageContext, "Categories-Selected") %></th>
		</tr>

		<% java.util.List entities = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.ENTITY_VIEW);
		   HashMap<Long,List<Category>> entityCategories = (HashMap<Long,List<Category>>) request.getAttribute(com.dotmarketing.util.WebKeys.CATEGORY_VIEW);
		%>
		
		<% if (entities.size() ==0) { %>
			<tr>
				<td colspan="3" align="center">
					There are no Category Groups to show. 
					<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/ext/entities/edit_entity" />
					</portlet:actionURL>">Add</a> one now.
				</td>
			</tr>
		<% } %>
				
		<% 
		 String str_style="";
		 for (int k=0;k<entities.size();k++) {
		 
		 if(k%2==0){
		    str_style="class=\"alternate_1\"";
		 }
		 else{
		    str_style="class=\"alternate_2\"";
		 }

		
		 %>
			<%
			com.dotmarketing.portlets.entities.model.Entity entity = (com.dotmarketing.portlets.entities.model.Entity) entities.get(k);
			String entityInode = entity.getInode();
			List<Category> categories = entityCategories.get(entityInode);

			StringBuffer categoriesName = new StringBuffer();
			for(int z = 0;z < categories.size();z++)				
			{
				Category category = categories.get(z);
				String categoryName = category.getCategoryName();
				categoriesName.append(categoryName + ", ");
			}
			String categoriesNameString = categoriesName.toString();
			if(categories.size() > 0)
			{
				categoriesNameString = categoriesNameString.substring(0,categoriesNameString.lastIndexOf(","));
			}
			%>
			<tr <%=str_style %>>
				<td align="center" width="50" class="icons">						
						<a href="<portlet:actionURL  windowState="<%= WindowState.MAXIMIZED.toString() %>">
						<portlet:param name="struts_action" value="/ext/entities/edit_entity" />
						<portlet:param name="inode" value="<%=String.valueOf(entity.getInode())%>" />
						</portlet:actionURL>"><span class="editIcon"></span>
						</a> 

						<a href="javascript:deleteEntity('<%=entity.getInode()%>')">
						<span class="deleteIcon"></span>
						</a> 
				</td>
				<td nowrap>
						<a href="<portlet:actionURL  windowState="<%= WindowState.MAXIMIZED.toString() %>">
						<portlet:param name="struts_action" value="/ext/entities/edit_entity" />
						<portlet:param name="inode" value="<%=String.valueOf(entity.getInode())%>" />
						</portlet:actionURL>"><%=entity.getEntityName()%>
						</a> 					
				</td>
				<td width="75%">
						<%=categoriesNameString%>
				</td>
			</tr>
		<%}%>
		

	</table>

</liferay:box>


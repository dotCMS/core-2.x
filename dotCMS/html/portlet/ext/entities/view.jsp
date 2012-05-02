<%@ include file="/html/portlet/ext/entities/init.jsp" %>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.dotmarketing.portlets.categories.model.Category"%>
<table border="0" cellpadding="4" cellspacing="0" width="100%" class="listingTable">
<tr class="header">
	<td>
		<%= LanguageUtil.get(pageContext,"category-groups") %>
	</td>
	<td>
	<%= LanguageUtil.get(pageContext,"Categories-Selected") %>
	</td>
</tr>
<% 
	java.util.List entities = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.ENTITY_VIEW);
	HashMap<Long,List<Category>> entityCategories = (HashMap<Long,List<Category>>) request.getAttribute(com.dotmarketing.util.WebKeys.CATEGORY_VIEW);
%>
<% 
   String str_style="";
   for (int k=0; k<5 && k<entities.size();k++) {
    
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

	StringBuffer categoriesName = new StringBuffer("&nbsp;");
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
		<td nowrap> 
			&nbsp;<a href="<portlet:actionURL  windowState="<%= WindowState.MAXIMIZED.toString() %>">
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
<%if(entities.size() ==0){%>
	<tr >
		<td colspan="2" align="center" >
		<font class="gamma" size="2">
				<%= LanguageUtil.get(pageContext,"There-are-no-Category-Groups-to-show") %>. 
				<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/entities/edit_entity" /></portlet:actionURL>"><%= LanguageUtil.get(pageContext,"add") %></a><%= LanguageUtil.get(pageContext,"one-now") %>.
		</font>
		</td>
	</tr>
<%}%>

</table>

<table cellpadding="0" cellspacing="0" border="0" width="100%">

<tr><td>&nbsp;</td></tr>

<tr>
	<td align="right" class="gamma">
			<font class="gamma" size="2">
			<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/entities/view_entities" /></portlet:actionURL>"><%= LanguageUtil.get(pageContext,"all") %></a>
			| <a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/entities/edit_entity" /></portlet:actionURL>"><%= LanguageUtil.get(pageContext,"new") %></a> &nbsp; &nbsp;
			</font>
	</td>
</tr>
</table>
<%@ include file="/html/portlet/ext/containers/init.jsp" %>


<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value='<%= LanguageUtil.get(pageContext, "view-entities") %>' />

<table border="0" cellpadding="2" cellspacing="2" width="100%">
<tr class="beta">
	<td>
		<B><font class="beta" size="2"><%= LanguageUtil.get(pageContext, "Entity-Name") %></font></B>
	</td>
</tr>
<% java.util.List entities = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.CONTAINER_VIEW_PORTLET);%>
<% for (int k=0;k<5 && k<entities.size();k++) { %>
	<%com.dotmarketing.portlets.entities.model.Entity entity = (com.dotmarketing.portlets.entities.model.Entity) entities.get(k);%>
	<tr>
		<td>
			<font class="gamma" size="2">
			<a class="bg" href="<%= CTX_PATH %>/ext/containers/view_containers?parent=<%=entity.getInode()%>&parentName=<%=entity.getEntityName()%>"><%=entity.getEntityName()%></a> 
			</font>
		</td>
	</tr>
<% }%>
</table>
</liferay:box>

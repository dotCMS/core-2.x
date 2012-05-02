<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<%
	List resumes = (List)request.getAttribute(com.dotmarketing.util.WebKeys.RESUMES_LIST);
	
	int pageNumber = 1;
	if (request.getParameter("pageNumber")!=null) {
		pageNumber = Integer.parseInt(request.getParameter("pageNumber")); 
	}
	
	String orderby = (request.getParameter("orderby")!=null) ? request.getParameter("orderby") : "creationdate desc";
	
	int perPage = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
	int minIndex = (pageNumber - 1) * perPage;
	int maxIndex = perPage * pageNumber;

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/jobs/view_resumes"});
	params.put("pageNumber",new String[] { pageNumber + "" });
	params.put("orderby",new String[] { orderby });
	
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

%>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"view-resumes\") %>" />

	<table border="0" cellpadding="4" cellspacing="2" width="100%">
    <%if(resumes.size() == 0) {%>
    <tr>
        <td colspan="5" align="center"><font class="bg" size="2">There are no resumes right now.</a></td>
    </tr>
    <%} else {%>
    	<tr class="beta">
    		<td align="center" nowrap>&nbsp;</td>
    	     <td align="center" nowrap>&nbsp;</td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="inode" /></portlet:renderURL>">
    	     Resume #</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="name" /></portlet:renderURL>">
    	     Name</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="creationdate" /></portlet:renderURL>">
    	     Date Added</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="expirationdate" /></portlet:renderURL>">
    	     Expiration</a></B></td>
    	</tr> 
    <%}%>    
	<% for (int k=minIndex;k<maxIndex && k<resumes.size();k++) { 
		Resume resume = (Resume)resumes.get(k);
	%> 
		
		<tr <%if(resume.isActive()) {%>bgcolor="#eeeeee"<%}%>>
			<td>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_resume" /><portlet:param name="inode" value="<%=String.valueOf(resume.getInode())%>" /></portlet:actionURL>">
				edit</a>
			</td>
			<%if(resume.isActive()) {%>
				<td nowrap>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_resume" />
				<portlet:param name="inode" value="<%=String.valueOf(resume.getInode())%>" />
				<portlet:param name="<%=Constants.CMD%>" value="activate" />
				<portlet:param name="active" value="false" />
				<portlet:param name="referer" value="<%=referer%>" />
				</portlet:actionURL>">deactivate</a>
				</td>
			<%} else {%>
				<td nowrap>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_resume" />
				<portlet:param name="inode" value="<%=String.valueOf(resume.getInode())%>" />
				<portlet:param name="<%=Constants.CMD%>" value="activate" />
				<portlet:param name="active" value="true" />
				<portlet:param name="referer" value="<%=referer%>" />
				</portlet:actionURL>">publish</a>
				</td>
			<%}%>
			<td>
				<%= resume.getInode() %>
			</td>
			<td width="100%">
				<%= resume.getName() %>
			</td>
			<td>
				<font class="bg" size="2"><%= new java.text.SimpleDateFormat("MM/dd/yyyy").format(resume.getCreationdate())%></font>
			</td>
			<td>
				<font class="bg" size="2"><%=resume.getExpirationdate() != null? new java.text.SimpleDateFormat("MM/dd/yyyy").format(resume.getExpirationdate()) : "n/a"%></font>
			</td>
		</tr>

	<%}%>
		<tr>
			<td colspan="3" align=left>
			<% if (minIndex != 0) { %>
				<img src="<%= SKIN_COMMON_IMG %>/02_left.gif">
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber-1)%>" /></portlet:renderURL>">
				Previous</a></b>
			<% } %>
			</td>
			<td colspan="3" align=right>
			<% if (maxIndex < resumes.size()) { %>
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber+1)%>" /></portlet:renderURL>">
				Next</a></b>
				<img src="<%= SKIN_COMMON_IMG %>/02_right.gif">
			<% } %>
			</td>
		</tr>
	</table>
</liferay:box>
	
<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<%
	java.util.List jobs = (java.util.List)request.getAttribute(com.dotmarketing.util.WebKeys.JOBS_LIST);

	int pageNumber = 1;
	if (request.getParameter("pageNumber")!=null) {
		pageNumber = Integer.parseInt(request.getParameter("pageNumber")); 
	}
	String orderby = (request.getParameter("orderby")!=null) ? request.getParameter("orderby") : "jobs.entrydate desc";
	
	int perPage = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
	int minIndex = (pageNumber - 1) * perPage;
	int maxIndex = perPage * pageNumber;

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/jobs/view_jobs"});
	params.put("pageNumber",new String[] { pageNumber + "" });
	params.put("orderby",new String[] { orderby });
	
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

%>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"view-jobs\") %>" />

	<table border="0" cellpadding="4" cellspacing="2" width="100%">
	    <%if(jobs.size() == 0) {%>
	    <tr>
	        <td colspan="6" align="center"><font class="bg" size="2">There are no jobs right now.</a></td>
	    </tr>
	    <%} else {%>
    	<tr class="beta">
    	     <td align=center nowrap>&nbsp;</td>
    	     <td nowrap>&nbsp;</td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="inode" /></portlet:renderURL>">
    	     Job #</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="jobtitle" /></portlet:renderURL>">
    	     Job Title</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="entrydate" /></portlet:renderURL>">
    	     Date Added</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="expdate" /></portlet:renderURL>">
    	     Expiration</a></B></td>
    	</tr>
		<% for (int k=minIndex;k<maxIndex && k<jobs.size();k++) { 
				com.dotmarketing.portlets.jobs.model.Jobs job = (Jobs) jobs.get(k);
		%>
		<tr <%if(!job.isActive()) {%>bgcolor="#ffffff"<%}else{%> bgcolor="#eeeeee"<%}%>>
			<td align=center>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_job" /><portlet:param name="inode" value="<%=String.valueOf(job.getInode())%>" /></portlet:actionURL>">
				edit</a>
				</font>
			</td>
			<%if(job.isActive()) {%>
				<td nowrap>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_job" />
				<portlet:param name="inode" value="<%=String.valueOf(job.getInode())%>" />
				<portlet:param name="<%=Constants.CMD%>" value="activate" />
				<portlet:param name="active" value="false" />
				<portlet:param name="referer" value="<%=referer%>" />
				</portlet:actionURL>">deactivate</a>
				</td>
			<%} else {%>
				<td nowrap>
				<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_job" />
				<portlet:param name="inode" value="<%=String.valueOf(job.getInode())%>" />
				<portlet:param name="<%=Constants.CMD%>" value="activate" />
				<portlet:param name="active" value="true" />
				<portlet:param name="referer" value="<%=referer%>" />
				</portlet:actionURL>">publish</a>
				</td>
			<%}%>
			<td align=center>
				<font class="bg" size="2">
				<%=job.getInode()%>
				</font>
			</td>
			<td width="100%">
				<font class="bg" size="2">
				<%= job.getJobtitle() %>
				</font>
			</td>
			<td align=center>
				<font class="bg" size="2"><%= new java.text.SimpleDateFormat("MM/dd/yyyy").format(job.getEntrydate())%></font>
			</td>
			<td align=center>
			<%if(job.getExpdate()!=null){%>
			<font class="bg" size="2"><%= new java.text.SimpleDateFormat("MM/dd/yyyy").format(job.getExpdate())%></font>
			<%} else {%>
			<font class="bg" size="2">n/a</font>
			<%}%>
			</td>
		</tr>
		<%}%>
		<tr>
			<td colspan="3" align=left>
			<% if (minIndex != 0) { %>
				<img src="<%= SKIN_COMMON_IMG %>/02_left.gif">
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber-1)%>" /></portlet:renderURL>">
				Previous</a></b>
				</font>
			<% } %>
			</td>
			<td colspan="3" align=right>
			<% if (maxIndex < jobs.size()) { %>
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber+1)%>" /></portlet:renderURL>">
				Next</a></b>
				</font>
				<img src="<%= SKIN_COMMON_IMG %>/02_right.gif">
			<% } %>
			</td>
		</tr>
		<%}%>
	</table>
</liferay:box>
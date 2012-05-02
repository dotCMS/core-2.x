<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<%
	java.util.List searchfirms = (List)request.getAttribute(com.dotmarketing.util.WebKeys.SEARCHFIRMS_LIST);
	int pageNumber = 1;
	if (request.getParameter("pageNumber")!=null) {
		pageNumber = Integer.parseInt(request.getParameter("pageNumber")); 
	}
	String orderby = (request.getParameter("orderby")!=null) ? request.getParameter("orderby") : "creationdate desc, searchfirm.name";
	
	int perPage = 50;
	int minIndex = (pageNumber - 1) * perPage;
	int maxIndex = perPage * pageNumber;

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/jobs/view_searchfirms"});
	params.put("pageNumber",new String[] { pageNumber + "" });
	params.put("orderby",new String[] { orderby });
	
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

%>
<script>
	function jumpToFirm() {	
		id = document.getElementById("firmid");
		if(isNaN(id.value)) {
			alert("Please enter a valid firm id");
			id.focus();	
			return false;
		} else {
			window.location= '<portlet:actionURL><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" /></portlet:actionURL>&inode=' + id.value;
		}
	}
</script>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"view-searchfirms\") %>" />

	<table border="0" cellpadding="2" cellspacing="2" width="100%">
    <%if(searchfirms.size() == 0) {%>
    <tr>
        <td colspan="4" align="center"><font class="bg" size="2">There are no search firms right now.</a></td>
    </tr>
    <%} else {%>
	    <tr>
	    	<td align=center nowrap>&nbsp;</td>
	    	<td colspan="5" align="right"><font class="beta" size="2">Jump to Search Firm #<input type="text" name="firmid">
                <button dojoType="dijit.form.Button" onClick="return jumpToFirm()">Go</button></a>
	    	</td>
	    </tr>      
    	<tr class="beta">
    	     <td align="center">&nbsp;</td>
    	     <td align="center" nowrap>&nbsp;</td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="inode" /></portlet:renderURL>">
    	     Search Firm #</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="name" /></portlet:renderURL>">
    	     Search Firm</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="creationdate" /></portlet:renderURL>">
    	     Date Added</a></B></td>
    	     <td align=center nowrap><B><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="expirationdate" /></portlet:renderURL>">
    	     Expiration</a></B></td>
    	</tr> 
    <%}%>    
	<% for (int k=minIndex;k<maxIndex && k<searchfirms.size();k++) { 
		com.dotmarketing.portlets.jobs.model.Searchfirm searchfirm = (com.dotmarketing.portlets.jobs.model.Searchfirm)searchfirms.get(k);
	%>

		<tr <%if(searchfirm.isActive()) {%>bgcolor="#eeeeee"<%}%>>
		<td>
			<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" /><portlet:param name="inode" value="<%=String.valueOf(searchfirm.getInode())%>" /></portlet:actionURL>">
			edit</a>
		</td>		
		<%if(searchfirm.isActive()) {%>
			<td nowrap>
			<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" />
			<portlet:param name="inode" value="<%=String.valueOf(searchfirm.getInode())%>" />
			<portlet:param name="<%=Constants.CMD%>" value="activate" />
			<portlet:param name="active" value="false" />
			<portlet:param name="referer" value="<%=referer%>" />
			</portlet:actionURL>">deactivate</a>
			</td>
		<%} else {%>
			<td nowrap>
			<a class="bg" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" />
			<portlet:param name="inode" value="<%=String.valueOf(searchfirm.getInode())%>" />
			<portlet:param name="<%=Constants.CMD%>" value="activate" />
			<portlet:param name="active" value="true" />
			<portlet:param name="referer" value="<%=referer%>" />
			</portlet:actionURL>">publish</a>
			</td>
		<%}%>
			</td>
			<td>
				<font class="bg" size="2"><%= searchfirm.getInode() %></font>
			</td>			
			<td width="100%">
				<font class="bg" size="2">
				<%= searchfirm.getOrganization() %>
				</font>
			</td>
			<td align="center">
			<font class="bg" size="2"><%= new java.text.SimpleDateFormat("MM/dd/yyyy").format(searchfirm.getCreationdate())%></font>
			</td>
			<td align="center"><font class="bg" size="2"><%=(searchfirm.getExpirationdate() == null) ? "N/A" : new java.text.SimpleDateFormat("MM/dd/yyyy").format(searchfirm.getExpirationdate())%></a>
			</td>
		</tr>

	<%}%>
		<tr>
			<td colspan="2" align=left>
			<% if (minIndex != 0) { %>
				<img src="<%= SKIN_COMMON_IMG %>/02_left.gif">
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber-1)%>" /></portlet:renderURL>">
				Previous</a></b>
			<% } %>
			</td>
			<td colspan="3" align=right>
			<% if (maxIndex < searchfirms.size()) { %>
				<font class="gamma" size="2">
				<b><a class="bg" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /><portlet:param name="orderby" value="<%=orderby%>" /><portlet:param name="pageNumber" value="<%=String.valueOf(pageNumber+1)%>" /></portlet:renderURL>">
				Next</a></b>
				<img src="<%= SKIN_COMMON_IMG %>/02_right.gif">
			<% } %>
			</td>
		</tr>

	</table>
</liferay:box>
	
	
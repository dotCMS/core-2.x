<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.portlets.order_manager.struts.UsersForm" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.liferay.portal.util.Constants" %>
<%@ page import="com.liferay.portal.model.User" %>
<%    
	UsersForm usersForm = (UsersForm) request.getAttribute("usersForm");
	int actualPage = usersForm.getPage();
	int totalPages = usersForm.getTotalPages();
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/order_manager/view_users"});
	params.put("cmd",new String[] {"after_register_user"});
	
	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	//referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
	
%>
<script language="JavaScript">
	function goToCheckout() {
		var form = document.getElementById("usersForm");
		
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_users' />";
        href += "<portlet:param name='cmd' value='register_user' />";
        href += "<portlet:param name='referer' value='<%=referrer%>' />";
        href += "</portlet:actionURL>"; 
		form.action = href;
		
		form.submit ();
	}

	function selectUser(userId) 
	{
        form = document.getElementById("usersForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_users' />";
        href += "<portlet:param name='cmd' value='select' />";
        href += "</portlet:actionURL>";    
        href += "&userID=" + userId;
		form.action = href;		
		form.submit();
	}

	function search() 
	{
        form = document.getElementById("usersForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_users' />";
        href += "</portlet:actionURL>";    
		form.action = href;		
		form.submit();
	}
	
	function searchInitial() 
	{
		form = document.getElementById("usersForm");
		form.page.value = 1;
		search();
	}
	
	function viewAll() 
	{
		form = document.getElementById("usersForm");
		form.page.value = 1;
		form.filter.value = "";
		search();
	}
	
	function back() 
	{
		form = document.getElementById("usersForm");
		var page = 	form.page.value;
		page = parseInt(page);
		page = page - 1;
		form.page.value = page;
		search();
	}
	
	function next() 
	{	
		form = document.getElementById("usersForm");
		var page = 	form.page.value;
		page = parseInt(page);
		page = page + 1;
		form.page.value = page;
		search();	
	}
	
	function clickForm()
	{
		if (window.event && window.event.keyCode == 13)
		{
			searchInitial();
		}
		else 
		{
			return true;
		}
	}
		
</script>

<html:form action="/ext/order_manager/view_users" styleId="usersForm">
<html:hidden property="page" />
<html:hidden property="pageSize" />
<input type="hidden" name="referer" value="<%=referrer%>">

<div class="yui-gc portlet-toolbar">
	<div class="yui-u first">
		<input type="text" dojoType="dijit.form.TextBox" name="filter" onkeypress="clickForm();" style="width:250px;" value="<%= UtilMethods.isSet(usersForm.getFilter()) ? usersForm.getFilter() : "" %>" />
		
		<!--<span dojoType="dijit.Tooltip" connectId="filter" id="tooltip_1">
			<%= LanguageUtil.get(pageContext, "By-First-Name-Last-Name-or-email") %>
		</span>-->
		
		<button dojoType="dijit.form.Button" onclick="viewAll();" iconClass="resetIcon">
		   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Reset")) %>
		</button>
		
		<button dojoType="dijit.form.Button" onclick="searchInitial();" iconClass="searchIcon">
		   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "search")) %>
		</button>
	</div>
	<div class="yui-u" style="text-align:right;">
		<button dojoType="dijit.form.Button"  onclick="goToCheckout();" iconClass="plusIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-New-User")) %>
		</button>
	</div>
</div>



<table class="listingTable"
	<tr>
		<th><%= LanguageUtil.get(pageContext, "Full-Name") %></th>
		<th><%= LanguageUtil.get(pageContext, "Email-Address") %></th>
		<th width="120"><%= LanguageUtil.get(pageContext, "Action") %></th>
	</tr>
	<%				
	List<com.liferay.portal.model.User> users = usersForm.getUsers();
	int k = 0;
	if(users.size() > 0)
	{
	for(int i = 0;i < users.size();i++)
	{	
		User actualUser = users.get(i);
		String fullName = actualUser.getFullName();
		String email = actualUser.getEmailAddress();
		String  userId = actualUser.getUserId();					
		//Select the color
		String str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
		k++;
	%>
	<tr <%=str_style%> >
		<td><%=fullName%></td>
		<td><%=email%></td>
		<td><a href="javascript:selectUser('<%=userId%>')"><%= LanguageUtil.get(pageContext, "Select-User") %></a></td>
	</tr>		
	<%	}
	}else{%>
		<tr><td colspan="3">&nbsp;</td></tr>
		<tr><td colspan="3" align="center"><b><bean:message key="message.order_manager.shopping_cart_users_empty" /></b></td></tr>
		<tr><td colspan="3">&nbsp;</td></tr>										
	<%} %>
	</table>
			
<div class="yui-gb buttonRow">
	<div class="yui-u first" style="text-align:left">
	<%if (actualPage > 1){%>
        <button dojoType="dijit.form.Button" onClick="back();" iconClass="previousIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Back")) %>
        </button>
	<%}%>&nbsp;
	</div>
	
	<div class="yui-u">
		<%= LanguageUtil.get(pageContext, "Page") %> <%=actualPage%> / <%=totalPages%></td>
	</div>
	
	<div class="yui-u" style="text-align:right">
		<%if (actualPage < totalPages){%>
	        <button dojoType="dijit.form.Button" onClick="next();" iconClass="nextIcon">
	           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Next")) %>
	        </button>
		<%}%>&nbsp;
	</div>
</div>


	</html:form>


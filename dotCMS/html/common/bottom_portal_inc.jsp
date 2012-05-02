<%@ page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="com.liferay.util.ParamUtil"%>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.dotmarketing.util.Config" %>
<%@page import="com.liferay.portal.language.LanguageUtil"%>
<%@page import="com.dotmarketing.db.DbConnectionFactory"%>




<%if((DbConnectionFactory.isOracle() ||  DbConnectionFactory.isMsSql()) 
		&& "100".equals(System.getProperty("dotcms_level")) 
		&& session.getAttribute("db-community-edition-warning") ==null){ %>
		<%session.setAttribute("db-community-edition-warning", "1");  %>
	<script>
		function closeCotDbWarningDialog(){
			dijit.byId('dotDbWarningDialog').hide();
			<%if(request.getAttribute("licenseManagerPortletUrl") != null){ %>
				window.location='<%=request.getAttribute("licenseManagerPortletUrl") %>';
			<%}%>
		}
	
	</script>
		
		
	<div id="dotDbWarningDialog" dojoType="dijit.Dialog" style="display:none" title="<%= LanguageUtil.get(pageContext, "db-community-edition-warning-title") %>">
		<div dojoType="dijit.layout.ContentPane" style="width:400px;height:150px;" class="box" hasShadow="true" id="dotDbWarningDialogCP">
			<%= LanguageUtil.get(pageContext, "db-community-edition-warning-text") %>
			<br>&nbsp;<br>
			<div class="buttonRow">
				<button dojoType="dijit.form.Button" onClick="closeCotDbWarningDialog()" iconClass="cancelIcon"><%= LanguageUtil.get(pageContext, "close") %></button>
			</div>
		</div>
	</div>
	<script>
		dojo.addOnLoad (function(){
			dojo.style(dijit.byId("dotDbWarningDialog").closeButtonNode, "visibility", "hidden"); 
			dijit.byId("dotDbWarningDialog").show();
		});
	</script>
	
<%} %>





<div style="float:left;">
	<%String serverId = Config.getStringProperty("DIST_INDEXATION_SERVER_ID");%>
	<% if (UtilMethods.isSet(serverId)){ %>
		&nbsp;Server: <%=serverId%> &nbsp;
	<%} %>
	<a  href="javascript:showAboutDotCMSMessage()"><%= LanguageUtil.get(pageContext, "about")  %></a>
	&nbsp;|&nbsp;
	<a  href="javascript:showDisclaimerMessage()"><%= LanguageUtil.get(pageContext, "disclaimer")  %></a>
	&nbsp;|&nbsp;
	<a  href="#" onClick="dijit.byId('showSupport').show();"><%=LanguageUtil.get(pageContext, "Support") %></a>
	<script type="text/javascript">
		function showAboutDotCMSMessage(){
	       var myDialog = dijit.byId("dotBackEndDialog");
	       myDialog.titleNode.innerHTML="<%= LanguageUtil.get(pageContext, "about") %> dotCMS";
	       dijit.byId("dotBackEndDialogCP").setHref("/html/portal/about.jsp");
	       myDialog.show();
		}
	
	
		function showDisclaimerMessage(){
	       var myDialog = dijit.byId("dotBackEndDialog");
	       myDialog.titleNode.innerHTML="<%= UnicodeLanguageUtil.get(pageContext, "disclaimer") %>";
	       dijit.byId("dotBackEndDialogCP").setHref("/html/portal/disclaimer.jsp");
	       myDialog.show();
		}	
	</script>
</div>
		




<div style="float:right">
	&copy;<%=new GregorianCalendar().get(Calendar.YEAR)%> <a href="http://www.dotcms.org" target="_blank">dotCMS Inc.</a> <%= LanguageUtil.get(pageContext, "All-rights-reserved") %>.
</div>





<iframe name="hidden_iframe" id="hidden_iframe" style="position:absolute;top:-100px;width:0px; height:0px; border: 0px;"></iframe>
<script>

function setKeepAlive(){
	var myId=document.getElementById("hidden_iframe");
	myId.src ="/html/common/keep_alive.jsp?r=<%=System.currentTimeMillis()%>";
}
// 15 minutes
setTimeout("setKeepAlive()", 60000 * 15);
</script>

<div id="dotBackEndDialog" dojoType="dijit.Dialog" style="display:none" title="<%= LanguageUtil.get(pageContext, "about") %> dotCMS">
	<div dojoType="dijit.layout.ContentPane" style="width:400px;height:150px;" class="box" hasShadow="true" id="dotBackEndDialogCP">
	</div>
</div>
<div id="showSupport" dojoType="dijit.Dialog" style="display: none">
	<table width="600"><tr>
		<td valign="top" width="50%" style="padding:10px;border-right:1px solid #dcdcdc;">
			<h2><%=LanguageUtil.get(pageContext, "Report-a-Bug") %></h2>
			<p><%=LanguageUtil.get(pageContext, "dotCMS-is-dedicated-to-quality-assurance") %></p>
			<div class="buttonRow">
				<button dojoType="dijit.form.Button" iconClass="bugIcon" onclick="window.open('http://www.dotcms.org/jira');">
					<%=LanguageUtil.get(pageContext, "Report-a-Bug") %>
				</button>
		</td>
		<td valign="top" width="50%" style="padding:10px 10px 10px 20px;">
			<h2><%=LanguageUtil.get(pageContext, "Professional-Support") %></h2>
			<p><%=LanguageUtil.get(pageContext, "Let-our-support-engineers-get-you-back-on-track") %></p>
			<div style="text-align:center;font-size:146.5%;color:#990000;">+1 877-9-DOTCMS</div>
			<div style="text-align:center;font-size:77%;color:#999;"><%=LanguageUtil.get(pageContext, "Toll-Free") %>+1 877-936-8267</div>
			<div style="text-align:center;font-size:146.5%;color:#999;"><%=LanguageUtil.get(pageContext, "or") %></div>
			<div style="text-align:center;">
				<a href="http://www.dotcms.org/enterprise/" target="_blank"><%=LanguageUtil.get(pageContext, "Click-here-to-login-to-your-account") %></a>
			</div>
		</td>
	</tr></table>
</div>

	
	
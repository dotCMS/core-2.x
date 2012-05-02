<%@page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.dotmarketing.util.InodeUtils" %>
<%@page import="com.dotmarketing.portlets.categories.model.Category" %>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="com.dotmarketing.portlets.categories.business.CategoryAPI"%>
<%@ include file="/html/portlet/ext/categories/init.jsp" %>
	
<script type="text/javascript">
	dojo.require('dotcms.dijit.FileBrowserDialog');
	dojo.require('dotcms.dijit.form.FileSelector');
	dojo.require("dojox.grid.EnhancedGrid");
    dojo.require("dojox.grid.enhanced.plugins.Pagination");
    dojo.require("dojox.grid.enhanced.plugins.Search");
    dojo.require("dojox.data.AndOrReadStore");
    dojo.require("dojo.date.stamp");
    dojo.require("dojo.date.locale");
    dojo.require("dijit.form.TextBox");
    dojo.require("dijit.form.DateTextBox");
    dojo.require("dijit.form.CheckBox");
	
	dojo.addOnLoad(function () {
	});
</script>

<%             
	CategoryAPI catAPI = APILocator.getCategoryAPI();
	com.dotmarketing.portlets.categories.model.Category cat = (com.dotmarketing.portlets.categories.model.Category) request.getAttribute(com.dotmarketing.util.WebKeys.CATEGORY_EDIT);
	if(cat == null) cat = new Category();
	java.util.List<Category> children = new java.util.ArrayList<Category>();
	String catName ="";
    StringBuffer childString = new StringBuffer();
	boolean allowCategoryAdd = false;
	boolean topLevel = false;
	
	
	if(!InodeUtils.isSet(cat.getInode())){
		children = (java.util.List<Category>) catAPI.findTopLevelCategories(user, false);
		catName= "Top Level";
		topLevel = true;
		
	}
	else {
		children = catAPI.getChildren(cat, user, false);
		catName = cat.getCategoryName();
		childString.append(":" + cat.getInode() + ":");
	}
	java.util.Iterator parents = catAPI.getCategoryTreeUp(cat, user, false).iterator();

	if(com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user, com.dotmarketing.business.APILocator.getRoleAPI().loadCMSAdminRole())){
	    allowCategoryAdd = true;	
	}
	else if(catAPI.canAddChildren(cat, user, false)){
		allowCategoryAdd = true;		
	}
	
	
%>

<script>

	function doSubmit(formName) {
		form = document.getElementById(formName);
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/categories/view_category" /></portlet:actionURL>';
		submitForm(form);
	}
	
	function deleteCategory(x){

		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.category.delete.category")) %>')){
			var form = document.getElementById("addingForm");
			form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/categories/view_category" /></portlet:actionURL>';
			form.inode.value=x;
			form.cmd.value="delete";
			form.submit();
		}
	}

		function fillVelocityVarName()
		{
			
			var form = document.getElementById("addingForm");
			var relation = form.categoryName.value;
			var upperCase = false;
			var newString = "";
			for(i=0;i < relation.length ; i++){
				var c = relation.charAt(i);
				if(upperCase){
					c=c.toUpperCase();
				}
				else{
					c=c.toLowerCase();
				}
				if(c == ' '){
					upperCase = true;
				}
				else{
					upperCase = false;
					newString+=c;
				}
			}
			var re = /[^a-zA-Z0-9]+/g;
			newString = newString.replace(re, "");
			
			form.categoryVelocityVarName.value = newString;
		
		dojo.byId('addingForm').submit();
	}
	
//Layout Initialization
	function  resizeBrowser(){
	    var viewport = dijit.getViewport();
	    var viewport_height = viewport.h;
	   
		var  e =  dojo.byId("borderContainer");
		dojo.style(e, "height", viewport_height -175+"px");
		
	}
	
	//dojo.addOnLoad(resizeBrowser);

	dojo.connect(window, "onresize", this, "resizeBrowser");


</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext,\"view-categories\") %>" />

<!-- START Quick Add Button -->
	<div class="buttonBoxLeft"><h3><%= LanguageUtil.get(pageContext, "Hierarchy") %></h3></div>
	
	<div class="buttonBoxRight">
		<%if(allowCategoryAdd){%>
			<form id="addingForm" action="<portlet:actionURL><portlet:param name="struts_action" value="/ext/categories/edit_category" /></portlet:actionURL>" method="POST">
				<input type="hidden" name="cmd" value="<%= Constants.ADD %>">
				<input type="hidden" name="inode" value="">
				<input type="hidden" name="itsnew" value="true">
				<input type="hidden" name="redirect" value="<portlet:actionURL>
					<portlet:param name="struts_action" value="/ext/categories/view_category" />
					<portlet:param name="inode" value="<%=String.valueOf(cat.getInode())%>" />
					</portlet:actionURL>">
				<input type="hidden" name="parent" value="<%=String.valueOf(cat.getInode())%>">
				<input type="hidden" name="categoryVelocityVarName" value="">
				
				<input type="text" dojoType="dijit.form.TextBox" name="categoryName" />
		 		<button dojoType="dijit.form.Button" onclick="fillVelocityVarName()" id="submitButton" iconClass="plusIcon" type="button">
		 			<%= LanguageUtil.get(pageContext, "add-category") %>
				</button>
			</form>
		<%}%>
	</div>
<!-- END Quick Add Button -->
	
<!-- START Split Box -->
<div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="false" liveSplitters="true" id="borderContainer" class="shadowBox headerBox" style="height:100px;">
		
<!-- START Left Column -->	
	<div dojoType="dijit.layout.ContentPane" splitter="false" region="leading" style="width: 350px;" class="lineRight">
	
		<div style="padding:17px 10px;white-space: nowrap;margin-top:32px;">

			<%Category treeParent= null;%>
			<%int hasParents = 0;%>
			<%while (parents!=null && parents.hasNext()) {%>
				<%com.dotmarketing.portlets.categories.model.Category nextInode = (com.dotmarketing.portlets.categories.model.Category) parents.next();%>
				<%for(int j=1;j<hasParents;j++){%>
					<span class="shimIcon"></span>
				<%}%>
				<%if(treeParent != null){%>
					<a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/categories/view_category" /><portlet:param name="inode" value="<%=String.valueOf(treeParent.getInode())%>" /></portlet:renderURL>"><span class="toggleCloseIcon"></span></a>
				<%}%>
				<%if(!nextInode.getInode().equals(cat.getInode())) {%> 
					<A href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/categories/view_category" /><portlet:param name="inode" value="<%=String.valueOf(nextInode.getInode())%>" /></portlet:renderURL>"><%=nextInode.getCategoryName().equals("Top Level")?LanguageUtil.get(pageContext, "Top-Level1"):nextInode.getCategoryName()%></a>
				<%}else{ %>
					<b><%=nextInode.getCategoryName().equals("Top Level")?LanguageUtil.get(pageContext, "Top-Level1"):nextInode.getCategoryName()%></b>
				<%} %>
				<BR>
				<% hasParents++;%>
				<%treeParent= nextInode;%>
			<%}%>

		</div>
	</div>
<!-- END Left Column -->

<!-- START Right Column -->
	<div dojoType="dijit.layout.ContentPane" splitter="true" region="center" style="margin-top:32px;">
				
	<!-- START Listing Table -->
		<div id="workFlowWrapper" style="padding:10px;overflow-y: auto;overflow-x: hidden;">
		<form action="<portlet:actionURL><portlet:param name="struts_action" value="/ext/categories/view_category" /></portlet:actionURL>" method="POST" id="fm1">
			<input type="hidden" name="cmd" value="REORDER">
			<input type="hidden" name="count" value="<%=children.size()%>">
			<input type="hidden" name="inode" value="<%=request.getParameter("inode")%>">
			<table  class="listingTable">
			    <tr>
			          <th><%= LanguageUtil.get(pageContext, "action") %></th>
			          <th><%= LanguageUtil.get(pageContext, "children") %></th>
			          <th nowrap><%= LanguageUtil.get(pageContext, "unique-key") %></th>
			          <th><%= LanguageUtil.get(pageContext, "order") %></th>
				</tr>
			
			    <%int x = 1;
				  int k=0;
				  String str_style; 
				%>
			    <% Iterator it = children.iterator(); %>
			    <% while (it.hasNext() ) { %>
		          <%
			          com.dotmarketing.portlets.categories.model.Category nextInode = (com.dotmarketing.portlets.categories.model.Category) it.next();
			          childString.append(":" + nextInode.getInode() + ":");
			          boolean canEdit = catAPI.canEditCategory(nextInode,user, false);
			          int numberOfChildren = catAPI.getAllChildren(nextInode, user, false).size();
					  if(k%2==0){
			             str_style="class=\"alternate_1\"";
					  }
					  else{
			             str_style="class=\"alternate_2\"";
					  } 
				  %>
				  
				  <tr <%=str_style %>>
		                <td nowrap>
		                	<input type="hidden" name="inode<%=(x-1)%>" value="<%=nextInode.getInode()%>">
		                    <% if(canEdit){ %>
		                        <a id="Edit<%=String.valueOf(nextInode.getInode())%>" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
								   <portlet:param name="struts_action" value="/ext/categories/edit_category" />
								   <portlet:param name="inode" value="<%=String.valueOf(nextInode.getInode())%>" />
								   <portlet:param name="parent" value="<%=String.valueOf(cat.getInode())%>" />
								   </portlet:actionURL>">
		    					     <span class="editIcon"></span>
								</a>
								<span dojoType="dijit.Tooltip" connectId="Edit<%=String.valueOf(nextInode.getInode())%>"><%= LanguageUtil.get(pageContext,"edit-category") %></span>
		
								<a id="Del<%=String.valueOf(nextInode.getInode())%>" href="javascript: deleteCategory('<%=nextInode.getInode()%>');">
								  <span class="deleteIcon"></span>
								</a>
								<span dojoType="dijit.Tooltip" connectId="Del<%=String.valueOf(nextInode.getInode())%>"><%= LanguageUtil.get(pageContext,"delete-category") %></span>
								<%--
								<a id="Add<%=String.valueOf(nextInode.getInode())%>" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							    <portlet:param name="struts_action" value="/ext/categories/view_category" />
							    <portlet:param name="inode" value="<%=String.valueOf(nextInode.getInode())%>" />
							    <portlet:param name="parent" value="<%=String.valueOf(cat.getInode())%>" />
							    </portlet:renderURL>">
									<span class="fixIcon"></span>
								</a> 
								--%>
								<span dojoType="dijit.Tooltip" connectId="Add<%=String.valueOf(nextInode.getInode())%>"><%= LanguageUtil.get(pageContext, "add-category") %></span>

								
							<% } %>
						</td>
		                <td width="100%">
		                    <% if(canEdit){ %>
		                   	 <a href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
							    <portlet:param name="struts_action" value="/ext/categories/view_category" />
							    <portlet:param name="inode" value="<%=String.valueOf(nextInode.getInode())%>" />
							    <portlet:param name="parent" value="<%=String.valueOf(cat.getInode())%>" />
							    </portlet:renderURL>"><%=nextInode.getCategoryName()%></a> 
							<% } else { %>
								<%=nextInode.getCategoryName()%>
							<% } %>
							(<%= numberOfChildren %>)
		                 </td>
		                 <td nowrap>
							<% if(UtilMethods.isSet(nextInode.getKey())){ %>
								<%=nextInode.getKey()%>
							<% } %>
		                	&nbsp;
		                </td>
						<td  align="right"><input dojoType="dijit.form.TextBox" name="newOrder<%=(x-1)%>" type="text" value="<%=x++%>" style="width:40px"></td>
		          </tr>
			    <%
					k++;
				}%>
			</table>
		<!-- END Listing Table -->
		
		<!-- Start Reorder Button -->
			<%if(children.size() >1){%>
			  <div class="portlet-toolbar" style="text-align:right;">
			        <button dojoType="dijit.form.Button" iconClass="reorderIcon" onClick="doSubmit('fm1');">
			             <%= LanguageUtil.get(pageContext,"re-order") %>
			        </button>
			  </div>
			<%}%>
		<!-- END Reorder Button -->
		
		<!-- Start NO Children -->
			<%if(children.size() ==0){%>
				<div style="text-align:cenetr;"><%= LanguageUtil.get(pageContext, "no-children") %></div>
			<%}%>
		<!-- END NO Children -->
		
		</form>
	</div>
	<!-- END Right Column -->

</div>	
<!-- START Right Column -->
	
</liferay:box>
<script type="text/javascript">
resizeBrowser();
</script>

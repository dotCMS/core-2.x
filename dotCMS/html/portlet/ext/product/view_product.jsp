<%@ page language="java" import="java.util.*" %>
<%@ page language="java" import="com.dotmarketing.portlets.product.struts.ProductForm" %>
<%@ page language="java" import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page language="java" import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.webevents.model.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.portlets.categories.model.*" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ include file="/html/portlet/ext/product/init.jsp" %>


<style>

.productHoverClass{
	cursor:pointer;
	background:#FBF7BA;
}
.productHoverClass a{
	font-weight: normal;
	color: #0D5B8A;
}
.innerTable, .innerTable tr, .innerTable td{
	border:0;padding:0 5px;margin:0;
}
.alternate_1{
	background:#fff;
}
.alternate_2{
	background:#eee;
}
</style>



<%
	boolean respectFrontendRoles = false;

	/* paginate*/
	int pageNumber = 1;
	if (request.getParameter("pageNumber")!=null) {
		pageNumber = Integer.parseInt(request.getParameter("pageNumber")); 
	}

	int lineSize = 90;
	int perPage = 10;
	try{
		perPage = Integer.parseInt((String)request.getAttribute("productListPerPage"));
	}
	catch(Exception wer){
		
	}
	
	
	
	int minIndex = (pageNumber - 1) * perPage;
	int maxIndex = perPage * pageNumber;

	/****/

	ProductForm productForm = (ProductForm) request.getAttribute("DotProductForm");
	String keyword = productForm.getKeyword();
	keyword = UtilMethods.isSet(keyword) ? keyword : "";
	
	String[] productTypes = productForm.getProductTypes();

	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/product/view_product"});
	params.put("pageNumber",new String[] {Integer.toString(pageNumber)});
	if(productTypes != null)
	{
		params.put("productTypes",productTypes);
	}
	params.put("keyword",new String[] { keyword });

	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	

	params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/product/view_product"});
	params.put("pageNumber",new String[] {Integer.toString(pageNumber + 1)});
	if(productTypes != null)
	{
		params.put("productTypes",productTypes);
	}
	params.put("keyword",new String[] { keyword });

	String next = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

	params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/product/view_product"});
	params.put("pageNumber",new String[] {Integer.toString(pageNumber - 1)});
	if(productTypes != null)
	{
		params.put("productTypes",productTypes);
	}
	params.put("keyword",new String[] { keyword });

	String back = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

	referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
%>
<script language="JavaScript">

	function showAll()
	{
		var href= "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		href += "<portlet:param name='struts_action' value='/ext/product/view_product' />";
		href += "</portlet:renderURL>";
		window.location=href;
		

	}
	
	function searchProduct()
	{
		var href= "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		href += "<portlet:param name='struts_action' value='/ext/product/view_product' />";
		href += "</portlet:renderURL>";
		
		var form = document.getElementById('fmProduct');		
		form.action = href;	
		form.submit();
	}
	
	function reorder()
	{
		var href= "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		href += "<portlet:param name='struts_action' value='/ext/product/edit_product' />";
		href += "<portlet:param name='cmd' value='<%=com.dotmarketing.util.Constants.REORDER%>' />";
		href += "<portlet:param name='referrer' value='<%=referrer%>' />";
		href += "</portlet:actionURL>";
		
		var form = document.getElementById('fmProduct');		
		form.action = href;	
		form.submit();
	}
	
	function newProduct()
	{
		var href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/ext/product/edit_product' /><portlet:param name='referrer' value='<%=referrer%>' /></portlet:actionURL>";
		document.location.href = href;
	}

	function editProduct(href)
	{
		document.location.href = href;
	}
	
	function deleteProduct(href)
	{
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-product")) %>')){
			document.location.href = href;
		}
	}
	
	function copyProduct(href)
	{
		document.location.href = href;
	}
	
	function addFormat(href)
	{
		document.location.href = href;
	}
</script>
<html:form action="/ext/product/view_product" styleId="fmProduct">
<html:hidden property="orderBy" />
<html:hidden property="direction" />


<div class="yui-gc portlet-toolbar">
	<div class="yui-u first">
		<%String[] selectCatsString = productForm.getProductTypes();
		//////GETS THE ENTITY ASK QUINTS
		com.dotmarketing.portlets.entities.model.Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_PRODUCTS_TYPE);
		/////GET ALL THE MAIN CATEGORIES FOR THIS ENTITY
		java.util.List cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);
		if (cats.size()>0) { %>


			<!-- <%= LanguageUtil.get(pageContext, "Product-Type") %>: -->
			<% Iterator catsIter = cats.iterator();
				///FOR EACH CATEGORY WE GET THE CHILDREN
				while (catsIter.hasNext()) {
				Category category = (Category) catsIter.next();
				List children = InodeFactory.getChildrenClassByOrder(category, Category.class, "sort_order");
				if (children.size()>1) { %>
					<select name='productTypes' style="width:200px;"  dojoType="dijit.form.FilteringSelect">
						<option value=""><%= LanguageUtil.get(pageContext, "All") %></option>
						<%= com.dotmarketing.util.UtilHTML.getSelectCategories(category,1,selectCatsString,user,respectFrontendRoles) %>
					</select>
				<% } %>
			<% } %>
		<%}%>

		<!-- <%= LanguageUtil.get(pageContext, "Search") %>: -->
		<input type="text" name="keyword" dojoType="dijit.form.TextBox" style="width:200px;" value="<%= com.dotmarketing.util.UtilMethods.isSet(keyword) ? keyword : "" %>">

		
        <button dojoType="dijit.form.Button" onClick="searchProduct();" iconClass="searchIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Search")) %>
        </button>
		
        <button dojoType="dijit.form.Button" onClick="showAll();" iconClass="resetIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Reset")) %>
        </button>
	</div>
	<div class="yui-u" style="text-align:right;">
		<button dojoType="dijit.form.Button" type="button" onClick="newProduct();" iconClass="plusIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-New-Product")) %>
		</button>
	</div>
</div>


<input type="hidden" name="<portlet:namespace />cmd" value="">
<input type="hidden" name="<portlet:namespace />redirect" value="">
<input type="hidden" name="inode" value="">
	

<table class="listingTable">
	<tr>
		<th nowrap><%= LanguageUtil.get(pageContext, "Actions") %></th>
		<th width="100%"><%= LanguageUtil.get(pageContext, "Title") %></th>
		<th nowrap><%= LanguageUtil.get(pageContext, "Sort-Order") %></th>
	</tr>
	<% 
		List products = productForm.getProducts();
		List formats = productForm.getFormats();
		String str_style="";
		if (products.size() > 0)
		{
			for(int k=minIndex;k<maxIndex && k<products.size(); k++) 
			{
				Product product = (Product) products.get(k);
				List productFormats = (List) formats.get(k);
				
				/*if(k%2==0){
				 	str_style="alternate_1";
				}else{
				 	str_style="alternate_2";
                 }*/
			
		%>
		<tr  class="<%=str_style %>"  onmouseover="this.className='productHoverClass';" onmouseout="this.className='<%=str_style %>';">
			<td nowrap>
				<a href="javascript:editProduct('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/product/edit_product" />
				<portlet:param name="inode" value="<%=product.getInode()%>" />
				<portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>')">
				<span class="editIcon"></span>
				</a>
				
				<a href="javascript:if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-product")) %>')) {deleteProduct('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
				<portlet:param name="struts_action" value="/ext/product/edit_product" />
				<portlet:param name="inode" value="<%=String.valueOf(product.getInode())%>" />
				<portlet:param name="cmd" value="<%=com.liferay.portal.util.Constants.DELETE%>" />
				<portlet:param name="referrer" value="<%=referrer%>" />
				</portlet:actionURL>')}">
				<span class="deleteIcon"></span>
				</a>
				
				<a href="javascript:copyProduct('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
				<portlet:param name="struts_action" value="/ext/product/edit_product" />
				<portlet:param name="inode" value="<%=String.valueOf(product.getInode())%>" />
				<portlet:param name="cmd" value="<%=com.dotmarketing.util.Constants.COPY%>" />
				<portlet:param name="referrer" value="<%=referrer%>" />
				</portlet:actionURL>')">
				<span class="copyIcon"></span>
				</a>
			</td>
			
			<td nowrap="true"					
				onclick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
				<portlet:param name="struts_action" value="/ext/product/edit_product" />
				<portlet:param name="inode" value="<%=product.getInode()%>" />
				<portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>';">
				
				<div style="float:left;">
					<a title="<%=product.getTitle()%>" href="javascript:editProduct('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>">
					<portlet:param name="struts_action" value="/ext/product/edit_product" />
					<portlet:param name="inode" value="<%=product.getInode()%>" />
					<portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>')">
					<b><%=UtilMethods.truncatify(product.getTitle(),lineSize)%></b>
					</a>
				</div>
				<div style="float:right;padding-right:10px;">
					<button dojoType="dijit.form.Button" type="button" onClick="window.location.href='<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/ext/product/edit_format' /><portlet:param name='productInode' value='<%=product.getInode()%>' /><portlet:param name='referrer' value='<%=referrer + "#formats"%>' /></portlet:actionURL>';">
						<%= LanguageUtil.get(pageContext, "add-variant") %>
					</button>
				</div>
			</td>
			<td align="center"><input maxlength="3" type="text" dojoType="dijit.form.TextBox" name="<%="reorder_" + product.getInode()%>" value="<%=product.getSortOrder()%>" style="width:30px;" ></td>	
		</tr> 
				     
		<%for(int l=0; l < productFormats.size();l++){
			ProductFormat format = (ProductFormat) productFormats.get(l);
		%>
						<tr  class="<%=str_style %>"  onmouseover="this.className='productHoverClass';" onmouseout="this.className='<%=str_style %>';">
							<td>&nbsp;</td>
							<td nowrap onclick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/product/edit_format" /><portlet:param name="inode" value="<%=format.getInode()%>" /><portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>';">
								<table width="100%" class="innerTable">
									<tr>
										<td nowrap>
											<a href="javascript:editProduct('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/product/edit_format" /><portlet:param name="inode" value="<%=format.getInode()%>" /><portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>')"><span class="editIcon"></span></a>
											
											<a href="javascript:deleteProduct('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
											<portlet:param name="struts_action" value="/ext/product/edit_format" />
											<portlet:param name="inode" value="<%=String.valueOf(format.getInode())%>" />
											<portlet:param name="cmd" value="<%=com.liferay.portal.util.Constants.DELETE%>" />
											<portlet:param name="referrer" value="<%=referrer%>" />
											</portlet:actionURL>')"><span class="deleteIcon"></span></a>
											
											<a href="javascript:copyProduct('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
											<portlet:param name="struts_action" value="/ext/product/edit_format" />
											<portlet:param name="inode" value="<%=String.valueOf(format.getInode())%>" />
											<portlet:param name="cmd" value="<%=com.dotmarketing.util.Constants.COPY%>" />
											<portlet:param name="referrer" value="<%=referrer%>" />
											</portlet:actionURL>')"><span class="copyIcon"></span></a>																													
										</td>
										<td width="100%">
											<a title="<%=format.getFormatName()%>" href="javascript:editProduct('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/product/edit_format" /><portlet:param name="inode" value="<%=format.getInode()%>" /><portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>')">
												<%=UtilMethods.truncatify(format.getFormatName(),lineSize)%>
											</a>
										</td>
										<td nowrap>
											<%if (format.getInventoryQuantity() < format.getReorderTrigger()){ %>
												<font face="Arial" size="2" color="#ff0000">
													<%=format.getInventoryQuantity()%> <%= LanguageUtil.get(pageContext, "on-hand") %>
												</font>
											<%}else{ %>
												<%=format.getInventoryQuantity()%> <%= LanguageUtil.get(pageContext, "on-hand") %>
											<%} %>
										</td>
									</tr>
								</table>
							</td>
							<td>&nbsp;</td>																														
						</tr>														
					<% 
					}
				}%>
			<%}else{%>
				<tr><td colspan="5">
					<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-Products-Formats-to-show") %></div>
				</td></tr>					
			<%} %>
		</table>
			
		<%if(perPage > 3){ %>
			<div class="buttonRow" style="text-align:right;"> 
				<button dojoType="dijit.form.Button" onClick="reorder();" iconClass="reorderIcon">
					<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Reorder")) %>
				</button>
			</div>
		<%}else{%>
			<a href="javascript:showAll();"><%= LanguageUtil.get(pageContext, "show-all") %></a>
		<%} %>
				

<%if(perPage > 3){ %>
	<div class="yui-g buttonRow">
		<div class="yui-u first" style="text-align:left;">
			<% if (minIndex != 0 || maxIndex < products.size() ){ %>
				<% if (minIndex != 0) { %>
   					<button dojoType="dijit.form.Button" onClick="window.location='<%=back%>'" iconClass="previousIcon">
   						<%= LanguageUtil.get(pageContext, "Previous") %>
					</button>
				<% } %>
			<%} %>
		</div>
		<div class="yui-u" style="text-align:right;">
			<% if (minIndex != 0 || maxIndex < products.size() ){ %>
				<% if (maxIndex < products.size()) { %>
   					<button dojoType="dijit.form.Button" onClick="window.location='<%=next%>'" iconClass="nextIcon">
   						<%= LanguageUtil.get(pageContext, "Next") %>
					</button>
				<% } %>
			<%} %>
		</div>
	</div>
<%} %>

</html:form>

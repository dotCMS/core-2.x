<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.cms.product.struts.ProductsForm" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductPrice" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.liferay.portal.util.Constants" %>
<%@ page import="com.dotmarketing.cms.product.model.ShoppingCart" %>
<%@page import="com.dotmarketing.portlets.categories.model.Category"%>

<%
	ProductsForm productsForm = (ProductsForm) request.getAttribute("productsForm");
	ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART);
	if (shoppingCart == null)
	{
		shoppingCart = new ShoppingCart();
		session.setAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART,shoppingCart);		
	}
	
	String edit = (String) request.getParameter("edit");
	String referrer = (String) request.getParameter("referer");
	if(!UtilMethods.isSet(referrer))
	{
		java.util.Map params = new java.util.HashMap();
		params.put("struts_action",new String[] {"/ext/order_manager/view_products"});

		referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	}
	//referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
%>

<script language="JavaScript">
	function addShoppingCart() 
	{
        form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='<%=Constants.ADD%>' />";
        href += "<portlet:param name='referer' value='<%=referrer%>' />";
        href += "</portlet:actionURL>";    
		form.action = href;
		form.submit();
	}
	
	function viewShoppingCart()
	{
        form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='<%=Constants.VIEW%>' />";
        href += "</portlet:actionURL>";    
		form.action = href;		
		form.submit();
	}
	
	function searchProducts()
	{
		form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='referer' value='<%=referrer%>' />";
        href += "</portlet:actionURL>"; 
        <%if(UtilMethods.isSet(edit)){ %>
			href += "&edit=true";	
		<%}%>  
		form.action = href;	
		form.submit();
	}
	
	function proceedCheckOut()
	{
		form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_users' />";
        href += "</portlet:actionURL>";    
		document.location.href = href;
	}	
	
	function searchAllProducts()
	{
		form = document.getElementById("productsForm");
		form.categoryInode.value = "";
		form.filter.value = "";
		searchProducts();		
	}
	
	function fillQuantity(parameter,radio)
	{
		if(radio.checked)
		{
			parameter = document.getElementById(parameter);
			var value = parameter.value;
			if(value == 0)
			{
				parameter.value = 1;	
			}
		}
		else
		{
			parameter = document.getElementById(parameter);
			parameter.value = 0;
		}		
	}
	
</script>

<!--
	<%= LanguageUtil.get(pageContext, "New-Order") %>: <%= LanguageUtil.get(pageContext, "Select-Products") %>
-->

<html:form action="/ext/order_manager/view_products" styleId="productsForm" >
<input type="hidden" name="orderBy" value="title">

<div class="yui-g portlet-toolbar">
	<div class="yui-u first">
		<select dojoType="dijit.form.FilteringSelect" name="categoryInode" value="<%= UtilMethods.isSet(productsForm.getCategoryInode()) ? productsForm.getCategoryInode() : "" %>">
			<option value=""><%= LanguageUtil.get(pageContext, "All") %></option>
<%
		if (productsForm.getListTypeProducts() != null) {
			for (Category typeProduct: productsForm.getListTypeProducts()) {
%>
			<option value="<%= typeProduct.getInode() %>"><%= typeProduct.getCategoryName() %></option>
<%
			}
		}
%>
		</select>
		
		<input type="text" dojoType="dijit.form.TextBox" name="filter" style="width:175px;" value="<%= UtilMethods.isSet(productsForm.getFilter()) ? productsForm.getFilter() : "" %>" />
		
		<button dojoType="dijit.form.Button"  onClick="searchProducts()" iconClass="searchIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Search")) %>
		</button>
		
		<button dojoType="dijit.form.Button" onClick="searchAllProducts()" iconClass="resetIcon">
			Reset
		</button>
	</div>
	<div class="yui-u" style="text-align:right;">
		<button dojoType="dijit.form.Button" onClick="addShoppingCart();" iconClass="plusIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-to-Shopping-Cart" )) %>
        </button>
		
		<%if(!UtilMethods.isSet(edit)){ %>
           <button dojoType="dijit.form.Button" onClick="viewShoppingCart();" iconClass="cartIcon">
              <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "View-Shopping-Cart" )) %>
           </button>
		<%}%>
	</div>
</div>


<table class="listingTable shadowBox">
	<tr>
		<th nowrap><%= LanguageUtil.get(pageContext, "Add") %></td>
		<th width="45%"><%= LanguageUtil.get(pageContext, "Title") %></th>
		<th width="25%"><%= LanguageUtil.get(pageContext, "Type") %></th>
		<th width="15%"><%= LanguageUtil.get(pageContext, "Partner-Price") %></th>
		<th width="15%"><%= LanguageUtil.get(pageContext, "Non-Partner-Price") %></th>
		<th nowrap><%= LanguageUtil.get(pageContext, "Quantity") %></th>
	</tr>
	<%
		List<Product> products = productsForm.getListProducts();
		int k = 0;				
		if(products.size() > 0)
		{
			for(int i = 0;i < products.size();i++){
			Product product = productsForm.getListProducts().get(i);
			List<ProductFormat> formats = product.getFormats();
			for(int j = 0;j < formats.size();j++)
			{
				ProductFormat format = formats.get(j);
				//Quantity
				String quantityParameter = product.getInode() + "|" + format.getInode() + "|" + "QUANTITY";
				quantityParameter = request.getParameter(quantityParameter);
				int quantity = (UtilMethods.isSet(quantityParameter) && UtilMethods.isInt(quantityParameter) ? Integer.parseInt(quantityParameter) : 0);
				ProductPrice productPrice = format.getQuantityPrice(1);
				//Selected
				String selectedParameter = product.getInode() + "|" + format.getInode() + "|" + "ADD";
				selectedParameter = request.getParameter(selectedParameter);
				String selected = (UtilMethods.isSet(selectedParameter) &&  selectedParameter.equals("on") ? "CHECKED" : "");
				
				
				//Select the color
				String str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
				k++;
	%>
	
			<tr <%=str_style %>>
				<td align="center">
					<input type="checkbox" dojoType="dijit.form.CheckBox" name="<%=product.getInode()%>|<%=format.getInode()%>|ADD" id="<%=product.getInode()%>|<%=format.getInode()%>|ADD" <%=selected%> onclick="fillQuantity('<%=product.getInode()%>|<%=format.getInode()%>|QUANTITY',this);" >
				</td>
				<td><%=product.getTitle()%><br>&nbsp;&nbsp;&nbsp;<%=format.getFormatName()%></td>
				<td><%=product.getProductType().getCategoryName() %></td>
				<td><%=UtilMethods.toPriceFormat(productPrice.getPartnerPrice())%></td>
				<td><%=UtilMethods.toPriceFormat(productPrice.getRetailPrice())%></td>
				<td align="center">
					<input type="text" dojoType="dijit.form.TextBox" name="<%=product.getInode()%>|<%=format.getInode()%>|QUANTITY" id="<%=product.getInode()%>|<%=format.getInode()%>|QUANTITY" value="<%=quantity%>" style="width:25px;">
				</td>
			</tr>		
		<%}%>
	<%}%>
	<%}else{%>
		<tr>
			<td colspan="6">
				<div class="noResultsMessage"><bean:message key="message.order_manager.shopping_cart_products_empty" /></div>
			</td>
		</tr>	
	<%} %>
</table>

<div class="buttonRow" style="text-align:right;">
    <button dojoType="dijit.form.Button" onClick="addShoppingCart();" iconClass="plusIcon">
       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-to-Shopping-Cart")) %>
    </button>
    
	<%if(!UtilMethods.isSet(edit)){ %>
        <button dojoType="dijit.form.Button" onClick="viewShoppingCart();" iconClass="cartIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "View-Shopping-Cart")) %>
        </button>
	<%}%>
</div>


</html:form>


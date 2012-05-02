<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.cms.product.struts.ProductsForm" %>
<%@ page import="com.dotmarketing.cms.product.model.ShoppingCart" %>
<%@ page import="com.dotmarketing.cms.product.model.Holder" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductPrice" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.liferay.portal.util.Constants" %>
<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>
<%    
	ProductsForm productsForm = (ProductsForm) request.getAttribute("productsForm");
	ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART);
	if (shoppingCart == null)
	{
		shoppingCart = new ShoppingCart();
		session.setAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART,shoppingCart);		
	}
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/order_manager/view_products"});
	params.put("cmd",new String[] {Constants.VIEW});
	
	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	//referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
	
%>
<script language="JavaScript">
	function updateCart() 
	{
        form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='<%=Constants.UPDATE%>' />";
        href += "<portlet:param name='referrer' value='<%=referrer%>' />";        
        href += "</portlet:actionURL>";    
		form.action = href;		
		form.submit();
	}
	
	function continueShopping() 
	{		
		form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "</portlet:actionURL>";    
		document.location.href = href;
	}
	
	function removeItem(inode) 
	{		
		form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='<%=Constants.DELETE%>' />";
        href += "<portlet:param name='referrer' value='<%=referrer%>' />";
        href += "</portlet:actionURL>";
        href += "&formatInode=" + inode;
		form.action = href;		
		form.submit();
	}
	
	function addDiscount()
	{
        form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='addDiscount' />";
        href += "<portlet:param name='referrer' value='<%=referrer%>' />";        
        href += "</portlet:actionURL>";    
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
	
	
	function removeDiscount(discountId)
	{
        form = document.getElementById("productsForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='removeDiscount' />";
        href += "<portlet:param name='referrer' value='<%=referrer%>' />";        
        href += "</portlet:actionURL>"; 
        href += "&discountId=" + discountId;   
		form.action = href;		
		form.submit();
	}	
	
</script>
<!--
	<%= LanguageUtil.get(pageContext, "New-Order") %>: <%= LanguageUtil.get(pageContext, "Shopping-Cart") %></b>
-->


<html:form action="/ext/order_manager/view_products" styleId="productsForm" >

<div class="portlet-toolbar" style="text-align:right;">
	
	<button dojoType="dijit.form.Button" onClick="continueShopping();" iconClass="cartIcon">
	   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Continue-Shopping")) %>
	</button>
	
	<%if(shoppingCart.getHolders().size() > 0){ %>
	    <button dojoType="dijit.form.Button" onClick="proceedCheckOut();" iconClass="nextIcon">
	       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Proceed-to-CheckOut")) %>
	    </button>
	<%}%>
	
</div>

<table class="listingTable">
	<tr>
		<th nowrap><%= LanguageUtil.get(pageContext, "Remove") %></th>
		<th width="40%"><%= LanguageUtil.get(pageContext, "Title") %></th>
		<th width="25%"><%= LanguageUtil.get(pageContext, "Format") %></th>
		<th width="15%"><%= LanguageUtil.get(pageContext, "Type") %></th>
		<th width="10%"><%= LanguageUtil.get(pageContext, "Partner-Price") %></th>
		<th width="10%"><%= LanguageUtil.get(pageContext, "Non-Partner-Price") %></th>
		<th nowrap><%= LanguageUtil.get(pageContext, "Quantity") %></th>
	</tr>
		<%
		
		List<Holder> holders = shoppingCart.getHolders();
		List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
		int k = 0;
		if (holders.size() > 0)
		{
		int totalQuantity = 0;
		float totalPartnerPrice = 0;
		float totalRetailPrice = 0;				
		
		for(int i = 0;i < holders.size();i++)
		{	
			Holder holder = holders.get(i);
			ProductFormat format = holder.getFormat();
			Product product = format.getProduct();
			int quantity = holder.getQuantity();
			ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);
			//Select the color
			String str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
			k++;
			totalQuantity += quantity;
			totalPartnerPrice += quantity * productPrice.getPartnerPriceWithDiscount();
			totalRetailPrice += quantity * productPrice.getRetailPriceWithDiscount();
		%>
			<tr <%=str_style %>>
				<td>
					<a href="javascript:removeItem('<%=format.getInode()%>');">
					<span class="deleteIcon"></span>
				</td>
				<td><%=product.getTitle()%></td>
				<td><%=format.getFormatName()%></td>
				<td><%=product.getProductType().getCategoryName() %></td>
				<td align="right">$ <%=UtilMethods.toPriceFormat(productPrice.getPartnerPriceWithDiscount())%></td>
				<td align="right">$ <%=UtilMethods.toPriceFormat(productPrice.getRetailPriceWithDiscount())%></td>
				<td align="center"><input type="text" dojoType="dijit.form.TextBox" name="<%=product.getInode()%>|<%=format.getInode()%>|QUANTITY" value="<%=quantity%>" style="width:25px;"></td>
			</tr>
		<%}%>
	
	<tr>
		<td colspan="4">&nbsp;</td>
		<td align="right"><b>$ <%=UtilMethods.toPriceFormat(totalPartnerPrice)%></b></td>
		<td align="right"><b>$ <%=UtilMethods.toPriceFormat(totalRetailPrice)%></b></td>
		<td align="center"><b><%=totalQuantity%></b></td>
	</tr>
				 
	<% }else{%>
		<tr>
			<td colspan="7">
				<div class="noResultsMessage"><bean:message key="message.order_manager.shopping_cart_empty" /></div>
			</td>
		</tr>			
	<%}%>
</table>

	<div class="buttonRow" style="text-align:right;">
		<button dojoType="dijit.form.Button" onClick="updateCart();" iconClass="resetIcon">
		   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Update-Cart")) %>
		</button>
	</div>

		
<div class="callOutBox">
	<%= LanguageUtil.get(pageContext, "Do-you-have-a-coupon-If-so-enter-the-code-here") %>
	<input type="text" dojoType="dijit.form.TextBox" value="" name="discount" id="discount" style="width:150px;">
    <button dojoType="dijit.form.Button" onClick="addDiscount();">
       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Apply-Discount-Code")) %>
    </button>
</div>

<table border="0" width="100%" class="listingTable" cellspacing="0" cellpadding="0">
	<tr class="header">
		<th><%= LanguageUtil.get(pageContext, "Action") %></th>
		<th><%= LanguageUtil.get(pageContext, "Code-ID") %></th>
		<th><%= LanguageUtil.get(pageContext, "Description") %></th>
		<th><%= LanguageUtil.get(pageContext, "Amount") %></th>
	</tr>
	<%			
	for(int i = 0;i < discounts.size();i++)
	{	
		DiscountCode discount = discounts.get(i);
		String precurrency = "";
		String postcurrency = "";
		String discountAmount = "";
		if(discount.getDiscountType() == 1)
		{
			postcurrency = "%";
			discountAmount = UtilMethods.toPercentageFormat(discount.getDiscountAmount());
		}
		else
		{
			precurrency = "$";
			discountAmount = UtilMethods.toPercentageFormat(discount.getDiscountAmount());
		}%>
		<tr>
			<td>
				<a href="javascript:removeDiscount('<%=discount.getCodeId()%>');"><span class="deleteIcon"></span></a>
			</td>			
			<td><%=discount.getCodeId()%></td>
			<td><%=discount.getCodeDescription()%></td>
			<td><%=precurrency%> <%=discountAmount%> <%=postcurrency%></td>
		</tr>					
	  <%}%>
	  
</table>

</html:form>


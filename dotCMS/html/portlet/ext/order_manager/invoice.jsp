<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.cms.product.model.ShoppingCart" %>
<%@ page import="com.dotmarketing.cms.product.model.Holder" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>
<%@ page import="com.dotmarketing.portlets.order_manager.struts.OrderForm" %>


<form action="/dotCMS/viewCart" method="post">
<%
ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");
OrderForm orderForm = (OrderForm) session.getAttribute("shoppingCartOrderForm");
List<Holder> holders = shoppingCart.getHolders();
com.liferay.portal.model.User actualUser = shoppingCart.getUser();
float subTotal = 0;

if(orderForm == null){
%>
<table width="99%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Thank-but-doesn-t-exists-an-order-to-display") %></b></td>
	</tr>
</table>	
<%} else {%>
<table width="90%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Thank-you-for-your-order--") %> <%=orderForm.getInode() %></b></td>
	</tr>
	<tr>
		<td>
		<%= LanguageUtil.get(pageContext, "Here-is-a-summary-of-your-order-An-email-will-be-sent-to-this-email-address") %>:  <%=UtilMethods.getUserEmail(actualUser) %> <%= LanguageUtil.get(pageContext, "with-the-order-information-You-will-be-able-to-view-the-status-and-order-history-on-Your-Profile-information") %><br>
		<%= LanguageUtil.get(pageContext, "You-will-also-get-an-email-with-tracking-information-once-the-order-is-been-shipped") %>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Items-Ordered") %>:</b></td>
	</tr>
	<tr>
	<td>
	<!-- ITEMS -->
	<table width="99%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
			<td><b><%= LanguageUtil.get(pageContext, "Item-Description") %></b></td>
			<td><b><%= LanguageUtil.get(pageContext, "Qty") %></b></td>
			<td><b><%= LanguageUtil.get(pageContext, "Price-Each") %></b></td>
			<td><b><%= LanguageUtil.get(pageContext, "Amount") %></b></td>
		</tr>
		<tr>
			<td height="1" style="border-bottom: dashed 1px silver;" colspan="5" class="clear">&nbsp;</td>
		</tr>
		<%for(int i = 0; i < holders.size();i++)
		  {
			Holder holder = holders.get(i);
			ProductFormat format = holder.getFormat();
			Product product = format.getProduct();
			List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
		 %>
		<tr>
			<td><img src="/thumbnail?inode=$!{product.smallImageInode}&w=50&h=50" onclick="opencenter('$!{product.largeImageInode}')" style="padding: 10px;"></td>
			<td><%=product.getTitle() %> <br> <%=format.getFormatName() %></td>
			<td><%=holder.getQuantity() %></td>
			<td>
				<% 
				float priceAmount = UtilMethods.getItemPriceWithDiscount(format,holder.getPrice(),discounts);
				float lineTotal = holder.getQuantity() * priceAmount;
				subTotal += lineTotal;
			%>				
				$ <%=UtilMethods.toPriceFormat(priceAmount)%>
			</td>
			<td>$ <%=UtilMethods.toPriceFormat(lineTotal)%></td>
		</tr>
		<%}%>
		<tr>
			<td height="1" style="border-bottom: dashed 1px silver;" colspan="5" class="clear">&nbsp;</td>
		</tr>

		
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td><%= LanguageUtil.get(pageContext, "Sub-Total") %>:</td>
			<td>$ <%=UtilMethods.toPriceFormat(subTotal)%></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td nowrap><%= LanguageUtil.get(pageContext, "Shipping-and-Handling") %>:</td>
			<td>$ <%=UtilMethods.toPriceFormat(orderForm.getOrderShipping())%></td>
		</tr>
		<%if(orderForm.getShippingState().equals("FL")){ %>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td><%= LanguageUtil.get(pageContext, "Tax-6-5-FL") %>:</td>
			<td>$ <%=UtilMethods.toPriceFormat(orderForm.getOrderTax())%></td>
		</tr>
		<%} %>
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td><%= LanguageUtil.get(pageContext, "Total") %>:</td>
			<td>$ <%=UtilMethods.toPriceFormat(orderForm.getOrderTotal())%></td>
		</tr>
	</table>
	<!-- END ITEMS -->
	</td>
	</tr><!-- me -->
	<% if (orderForm.getPaymentType().equals("cc")){%>
	<tr>
		<td>
			<p><%= LanguageUtil.get(pageContext, "Your-Credit-Card-ending") %>  <%=UtilMethods.obfuscateCreditCard(orderForm.getCardNumber())%> 
			<%= LanguageUtil.get(pageContext, "was-succesfully-charged-for--") %> <%=UtilMethods.toPriceFormat(orderForm.getOrderTotal()) %></p>
		</td>
	</tr>
	<%} %>
	<% if (orderForm.getPaymentType().equals("ch")){%>
	<tr>
		<td class="mRed"><p><%= LanguageUtil.get(pageContext, "Products-are-shipped-upon-receipt-of-payment") %></p></td>
	</tr>
	<tr>
		<td>
			<p><%= LanguageUtil.get(pageContext, "Please-print-this-page-and-place-it-with-the-check-in-a-stamped-envelope") %></p>
		</td>
	</tr>
	<%} %>
	<tr>
		<td>
			<p><%= LanguageUtil.get(pageContext, "Should-special-shipping-procedures-be-required-or-if-you-have-any-questions-please-contact-us") %><br>
			<%= LanguageUtil.get(pageContext, "Thank-you-for-your-order-We-appreciate-your-business-and-look-forward-to-serving-you-in-the-future") %></p>
		</td>
	</tr>

</table>

<%shoppingCart.clear(); }%>
</form>



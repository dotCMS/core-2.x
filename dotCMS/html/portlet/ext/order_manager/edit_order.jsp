<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.cms.product.struts.ProductsForm" %>
<%@ page import="com.dotmarketing.cms.product.model.ShoppingCart" %>
<%@ page import="com.dotmarketing.cms.product.model.Holder" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductPrice" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.util.InodeUtils" %>
<%@ page import="com.liferay.portal.util.Constants" %>
<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>
<%@ page import="com.dotmarketing.portlets.order_manager.struts.OrderForm" %>
<%@ page import="com.dotmarketing.portlets.organization.model.Organization" %>


<%
	String dateFormat = "MMM / dd / yyyy hh:mm a";
	OrderForm orderForm = (OrderForm) request.getAttribute("OrderForm");

	request.setAttribute("OrderForm",orderForm);
	ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART);
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/order_manager/edit_order"});
	params.put("cmd",new String[] {Constants.EDIT});
	params.put("inode",new String[] {shoppingCart.getInode()});
	params.put("reload",new String[] { "true"});
	

	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	String shippingAmount = (String)request.getAttribute("orderShipping");
	if (shippingAmount != null){
		orderForm.setOrderShipping(Float.parseFloat(shippingAmount));
	}
	String orderTotal = (String)request.getAttribute("orderTotal");
	if (orderTotal != null){
		orderForm.setOrderTotal(Float.parseFloat(orderTotal));
	}
	String orderSubTotal = (String)request.getAttribute("orderSubTotal");
	if (orderSubTotal != null){
		orderForm.setOrderSubTotal(Float.parseFloat(orderSubTotal));
	}
	String orderTax = (String)request.getAttribute("orderTax");
	if (orderTax != null){
		orderForm.setOrderTax(Float.parseFloat(orderTax));
	}
	//referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
	
%>

<%@page import="com.dotmarketing.business.APILocator"%>

<script language="javascript">
//Layer Management Variables
styleReference = "style.";
visibleLayer = "inline";
browser = "Explorer";

//Layer Management
	function referenceLayer(layerName) 
	{ 
		if (browser == "Netscape") 
			return "document.layers['"+layerName+"']."; 
		else 
			return "document.all['"+layerName+"']."; 
	}
	
	function hideLayer(layerName)
	{ 
		eval(referenceLayer(layerName)+styleReference+ 'display="none"'); 
	}

	function showLayer(layerName) 
	{ 
		eval(referenceLayer(layerName)+styleReference+ 'display="'+visibleLayer+'"'); 
	} 
   
	function changeLayer()
	{
		var form = document.getElementById("OrderForm");
		var paymentType = form.paymentType.value;
		if (paymentType == "cc")
		{	
			showLayer("cc");
			hideLayer("check");
			hideLayer("po");
		}
		else if(paymentType == "ch")
		{
			showLayer("check");
			hideLayer("cc");
			hideLayer("po");
		}
		else if(paymentType == "po")
		{
			showLayer("po");
			hideLayer("cc");
			hideLayer("check");
		}
	}
	
	function addProducts()
	{		
		form = document.getElementById("OrderForm");
        var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    	"<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";		
		action +=    	"<portlet:param name='cmd' value='<%=Constants.SAVE%>' />";
		action +=    	"<portlet:param name='referer' value='<%=referrer%>' />";
		action +=    	"<portlet:param name='edit' value='true' />";
		action +=    "</portlet:actionURL>";
		form.action = action;
		form.submit();
	}
	
	function saveOrder()
	{		
		form = document.getElementById("OrderForm");
        var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    	"<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";		
		action +=    	"<portlet:param name='cmd' value='<%=Constants.SAVE%>' />";
		action +=    "</portlet:actionURL>";
		form.action = action;
		form.submit();
	}
	
	function Quantity()
	{		
		form = document.getElementById("OrderForm");
		form.changeQuantity.value = "true";
	}
	
	function TaxExceptionNumber()
	{		
		form = document.getElementById("OrderForm");
		form.changeTaxExceptionNumber.value = "true";
		if (form.taxExemptNumber.value != "") {
			document.getElementById('orderTax').value = 0;
			recalculateTotal();
		}
	}
	function recalculateTotal() {
		var total = parseFloat(<%=orderForm.getOrderSubTotal() %>);
		total += parseFloat(document.getElementById('orderShipping').value);
		total += parseFloat(document.getElementById('orderTax').value);
		var discount = parseFloat(<%=orderForm.getOrderDiscount()%>);
		total = total - discount;
		document.getElementById('orderTotal').value = total;
	}
	
	function BillingAddress()
	{
		form = document.getElementById("OrderForm");
		form.changeBillingAddress.value = "true";
	}
	
	function ShippingAddress()
	{
		form = document.getElementById("OrderForm");
		form.changeShippingAddress.value = "true";
	}
	
	function DiscountCodes()
	{
		form = document.getElementById("OrderForm");
		form.changeDiscount.value = "true";
	}
	
	function back()
	{
		form = document.getElementById("OrderForm");
        var action = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    	"<portlet:param name='struts_action' value='/ext/order_manager/view_orders' />";
		action +=    "</portlet:renderURL>";
		form.action = action;
		form.submit();
	}
	
	function removeItem(inode) 
	{		
		form = document.getElementById("OrderForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";
        href += "<portlet:param name='cmd' value='deleteitem' />";
        href += "<portlet:param name='referer' value='<%=referrer%>' />";
        href += "</portlet:actionURL>";
        href += "&formatInode=" + inode;
		form.action = href;
		form.submit();
	}
	
	function changeBillingStateTextLayer(){
	var form = document.getElementById("OrderForm");
	form.changeBillingAddress.value = "true";
	var state = document.getElementsByName("billingState")[0].value;
	if (state == "otherCountry")
	{
		showLayer("billingStateTextDiv");
		document.getElementsByName("billingStateOtherCountryText")[0].focus();
	}
	else 
	{
	document.getElementsByName("billingStateOtherCountryText")[0].value = "";
		hideLayer("billingStateTextDiv");
	}
}

	function changeShippingStateTextLayer(){
	var form = document.getElementById("OrderForm");
	form.changeShippingAddress.value = "true";
	var state = document.getElementsByName("shippingState")[0].value;
	if (state == "otherCountry")
	{
		showLayer("shippingStateTextDiv");	
		document.getElementsByName("shippingStateOtherCountryText")[0].focus();
	}
	else 
	{
		document.getElementsByName("shippingStateOtherCountryText")[0].value = "";
		hideLayer("shippingStateTextDiv");
	}
}
	function recalculateShipping()
	{
		updateOrder();
	}
	
	function updateOrder()
	{			
		form = document.getElementById("OrderForm");
		form.changeShippingType.value = "true";
        var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    	"<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";		
		action +=    	"<portlet:param name='cmd' value='update_shipping' />";
		action +=    "</portlet:actionURL>";
		form.action = action;
		form.submit();
	}
	
	function updateOrderDiscounts()
	{			
		form = document.getElementById("OrderForm");
		form.changeDiscount.value = "true";
        var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    	"<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";		
		action +=    	"<portlet:param name='cmd' value='update_discounts' />";
		action +=    "</portlet:actionURL>";
		form.action = action;
		form.submit();
	}
	
</script>


<html:form action="/ext/order_manager/edit_order" styleId="OrderForm" >
<%String token = (UtilMethods.isSet((String) session.getAttribute("org.apache.struts.action.TOKEN")) ? (String) session.getAttribute("org.apache.struts.action.TOKEN") : ""); %>
<input type="hidden" name="org.apache.struts.taglib.html.TOKEN" value="<%=token%>">
<input type="hidden" name="changeBillingAddress" value="false">
<input type="hidden" name="changeShippingAddress" value="false">
<input type="hidden" name="changeShippingType" value="false">

<%
	String reload = request.getParameter("reload");
	String changeQuantity = (UtilMethods.isSet(reload) ? "true" : "false");
%>

<input type="hidden" name="changeQuantity" value="<%=changeQuantity%>">
<input type="hidden" name="changeTaxExceptionNumber" value="false">
<input type="hidden" name="changeDiscount" value="false">

<input type="hidden" name="referer" value="<%=referrer%>">


<h3><%= LanguageUtil.get(pageContext, "Editing-Order--") %> <%=shoppingCart.getInode()%></h3>

<table class="listingTable">
	<tr>
		<th colspan="5"><%= LanguageUtil.get(pageContext, "General-Order-Information") %></th>
	</tr>
	<tr>		
 		<td width="10%"><%= LanguageUtil.get(pageContext, "Order-Status") %>:</td>
		<td width="40%">
			<select name="orderStatus">
			<%
			String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
			for (int i=0;i<statusesArray.length;i++) {
			String status = statusesArray[i];
			String optionName = com.dotmarketing.util.Config.getStringProperty(status + "_FN");
			String optionValue = com.dotmarketing.util.Config.getStringProperty(status);
			String selected = (orderForm.getOrderStatus()==Integer.parseInt(optionValue)) ? "selected" : "";
			%>
				<option value="<%=optionValue%>" <%=selected%>><%=optionName%></option>
			<%}%>
			</select>
		</td>
		
		<td width="10%"><%= LanguageUtil.get(pageContext, "Last-Mod-Date") %>:</td>
		<td width="40%"><%=UtilMethods.dateToHTMLDate(orderForm.getLastModDate(),dateFormat)%></td>						
	</tr>
	<tr>
		<td> <%= LanguageUtil.get(pageContext, "Payment-Status") %>:</td>
		<td>		
			<select name="paymentStatus">
			<%
			statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_PAY_STATUSES");
			for (int i=0;i<statusesArray.length;i++) {
			String status = statusesArray[i];
			String optionName = com.dotmarketing.util.Config.getStringProperty(status + "_FN");
			String optionValue = com.dotmarketing.util.Config.getStringProperty(status);
			String selected = (orderForm.getPaymentStatus()==Integer.parseInt(optionValue)) ? "selected" : "";
			%>
				<option value="<%=optionValue%>" <%=selected%>><%=optionName%></option>
			<%}%>
			</select>
		</td>
		<td><%= LanguageUtil.get(pageContext, "Order-Number") %>:</td>
		<td >
			<input type=hidden name="inode" id="inode" value='<%= orderForm.getInode() %>'>
			<% if (InodeUtils.isSet(orderForm.getInode())){ %> <%= orderForm.getInode() %><% } %>
		</td>
	</tr>
	
	<tr>
		<td><%= LanguageUtil.get(pageContext, "Order-Number") %>:</td>
		<td>
			<!--<html:hidden property="datePosted" />-->
			<%=UtilMethods.dateToHTMLDate(orderForm.getDatePosted(),dateFormat)%>
		</td>
		<td><%= LanguageUtil.get(pageContext, "Invoice-Number") %>:</td>
		<td>
			<html:text property="invoiceNumber"/>
		</td>							
	</tr>
	<tr>
		<td><%= LanguageUtil.get(pageContext, "Order-placed-in-the-backend-by") %>:</td>
		<td colspan="3">
			<%=(UtilMethods.isSet(orderForm.getBackendUserName())) ? orderForm.getBackendUserName() : ""%>
		</td>						
	</tr>
</table>

<table class="listingTable" style="margin-top:20px;">
	<tr>
		<th colspan="4"><%= LanguageUtil.get(pageContext, "Contact-Information") %>
			<div style="float:right;">
				<a href="<portlet:actionURL><portlet:param name="struts_action" value="/ext/usermanager/edit_usermanager" />
				<portlet:param name="cmd" value="edit" />
				<portlet:param name="userID" value="<%=orderForm.getUserId()%>" />
				<portlet:param name="referer" value="<%=referrer%>" /></portlet:actionURL>&referer=<%=referrer%>">
				<%= LanguageUtil.get(pageContext, "edit-user-profile-information") %></a>
			</div>
		</th>
	</tr>
	<tr>
		<td width="10%"><%= LanguageUtil.get(pageContext, "Name") %>:</td>
		<td width="40%">
			<input type="hidden" name="contactName" id="contactName" value="<%= orderForm.getContactName()%>">
			<%= orderForm.getContactName()%>
		</td>
		<td width="10%"><%= LanguageUtil.get(pageContext, "System") %>:</TD>
		<td width="40%">
			<% if (orderForm.getContactSystem() != null){ %> 
				<input type="hidden" name="contactSystem" id="contactSystem" value="<%= orderForm.getContactSystem()%>">
				<%= orderForm.getContactSystem() %>	
			<% } %> &nbsp;
		</td>
	</tr>
		
	<tr>
		<td><%= LanguageUtil.get(pageContext, "Email") %>:</td>
		<td>
			<% if (orderForm.getContactEmail() != null){ %> 
			<input type="hidden" name="contactEmail" id="contactEmail" value="<%= orderForm.getContactEmail()%>">
			<%= orderForm.getContactEmail() %>
			<% } %>
			&nbsp;
		</td>
		<td><%= LanguageUtil.get(pageContext, "Facility") %>:</td>
		<td>
			<% if (orderForm.getContactFacility() != null){ %> 
				<input type="hidden" name="contactFacility" id="contactFacility" value="<%= orderForm.getContactFacility()%>">
				<%= orderForm.getContactFacility() %>
			<% } %>
			&nbsp;
		</td>
	</tr>
</table>

<div class="yui-g" style="margin-top:20px;">
	
	<div class="yui-u first">	
		<table class="listingTable">
			<tr>
				<th colspan="5"><%= LanguageUtil.get(pageContext, "Billing-Address") %>:</th>
			</tr>
			<tr>
				<td width="30%"><span class="required"></span> <%= LanguageUtil.get(pageContext, "Address-Street-1") %>:</td>
				<td width="70%"><html:text property="billingAddress1" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Address-Street-2") %>:</td>
				<td><html:text property="billingAddress2" /></td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "City") %>:</td>
				<td><html:text property="billingCity" /></td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "State") %>:</td>
				<td>
					<select name="billingState" id="billingState" onchange="changeBillingStateTextLayer();">
						<script language="javascript">
							writeStatesOptions('<%= orderForm.getBillingState() %>');
						</script>
					</select>
					<div id="billingStateTextDiv">
						<html:text property="billingStateOtherCountryText"/>
					</div>
				</td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Country") %></td>
				<td>
					<script language="javascript">writeCountriesSelect("billingCountry", '<%= orderForm.getBillingCountry() %>');</script>
				<!-- 
					<select name="billingCountry" id="billingCountry">
					<script language="javascript">
					writeCountriesSelect('<%= orderForm.getBillingCountry() %>');
					</script>
					</select>
				 -->
					<!--<html:text property="shippingState" />-->
				</td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Zip") %>:</td>
				<td><html:text property="billingZip" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Phone") %>:</td>
				<td><html:text property="billingPhone" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Fax") %>:</td>
				<td><html:text property="billingFax" /></td>
			</tr>	
		</table>
	</div>
	
	<div class="yui-u">
		<table class="listingTable">
			<tr>
				<th colspan="2"><%= LanguageUtil.get(pageContext, "Shipping-Address") %>:</td>
			</tr>
			<tr>
				<td width="30%"><%= LanguageUtil.get(pageContext, "Shipping-Label") %>:</td>
				<td width="70%"><html:text property="shippingLabel" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Address-Street-1") %>:</td>
				<td><html:text property="shippingAddress1" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Address-Street-2") %>:</td>
				<td><html:text property="shippingAddress2" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "City") %>:</td>
				<td><html:text property="shippingCity" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "State") %>:</td>
				<td>			
					<select name="shippingState" id="shippingState" onchange="changeShippingStateTextLayer();">
						<script language="javascript">
							writeStatesOptions('<%= orderForm.getShippingState() %>');
						</script>
					</select>
					<div id="shippingStateTextDiv">
						<html:text property="shippingStateOtherCountryText"/>
					</div>
				</td>
			</tr>
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Country") %></td>
				<td>
					<script language="javascript">writeCountriesSelect("shippingCountry", '<%= orderForm.getShippingCountry() %>');</script>
				<!-- 
					<select name="shippingCountry" id="shippingCountry">
					<script language="javascript">
					writeCountriesSelect('<%= orderForm.getShippingCountry() %>');
					</script>
					</select>
				 -->
				</td>	
			</tr>
			
			<tr>
				<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Zip") %>:</td>
				<td><html:text property="shippingZip" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Phone") %>:</td>
				<td><html:text property="shippingPhone" onkeyup="ShippingAddress();" /></td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Fax") %>:</td>
				<td><html:text property="shippingFax" onkeyup="ShippingAddress();" /></td>
			</tr>
		</table>
	</div>
	
</div>


<div class="yui-g" style="margin-top:20px;">

	<div class="yui-u first">
		<table class="listingTable">
			<tr>
				<th width="30%"><%= LanguageUtil.get(pageContext, "Payment-Information") %></th>
				<th width="70%" style="text-align:right;">
					<%= LanguageUtil.get(pageContext, "Payment-Type") %>:
					<select name="paymentType" onchange="changeLayer();">				
						<%boolean set = (UtilMethods.isSet(orderForm.getPaymentType()) ? true : false);%>
						<%String selected = (set && orderForm.getPaymentType().equals("cc") ? "SELECTED" : ""); %>
						<option value="cc" <%=selected%>><%=Config.getStringProperty("ECOM_CREDIT_CARD_FN")%></option>
						<%selected = (set && orderForm.getPaymentType().equals("ch") ? "SELECTED" : ""); %>
						<option value="ch" <%=selected%>><%=Config.getStringProperty("ECOM_CHECK_FN")%></option>
						<%selected = (set && orderForm.getPaymentType().equals("po") ? "SELECTED" : ""); %>
						<option value="po" <%=selected%>><%=Config.getStringProperty("ECOM_PURCHASE_ORDER_FN")%></option>
					</select>
				</th>
			</tr>
		</table>
		
		<div id="cc">
			<table class="listingTable">
				<tr>
					<td width="30%"><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Name-on-Card") %>:</td>
					<td width="70%">
						<html:hidden property="nameOnCard" />
						<%String nameOnCard = (orderForm.getNameOnCard() != null ? orderForm.getNameOnCard() : ""); %>
						<%=nameOnCard%>
					</td>					
				</tr>
				<tr>
					<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Card-Type") %>:</td>
					<td>						
						<%String cardType = orderForm.getCardType() != null ? orderForm.getCardType() : "";%>
						<%=cardType%>
						<%
						if(UtilMethods.isSet(cardType))
						{
							if(cardType.equals("Visa"))
							{
								cardType = "vs";
							}
							else if(cardType.equals("Master Card"))
							{
								cardType = "mc";
							}									
							else if(cardType.equals("American Express"))
							{
								cardType = "ae";
							}
							else if(cardType.equals("Discover"))
							{
								cardType = "dc";
							}
							else
							{
								cardType = "";
							}
						}%>
						<input type="hidden" name="cardType" value="<%=cardType%>" >
					</td>
				</tr>
				<tr>
					<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Card-Number") %>:</td>
					<td>
						<html:hidden property="cardNumber" />
						<%String cardNumber = (orderForm.getCardNumber() != null ? orderForm.getCardNumber() : ""); %>
						<%=cardNumber%>
					</td>
				</tr>
				<tr>
					<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Expiration-Date") %>:</td>
					<td>
						<html:hidden property="cardExpMonth" />
						<html:hidden property="cardExpYear" />
						<%
						  int month = orderForm.getCardExpMonth();
						  int year = orderForm.getCardExpYear();
						 %>
						<%=UtilMethods.getMonthName(month)%>&nbsp;
							<%=year%>
					</td>
				</tr>				
				<tr>	
					<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Card-Verification-Value") %>:</td>
					<td>
						<input type="hidden" name="cardVerificationValue" value="***"/>
						<%String cardVerificationNumber = (orderForm.getCardNumber() != null ? "***" : ""); %>
						<%=cardVerificationNumber%>
					</td>
				</tr>
			</table>
		</div>				

		<div id="check">
			<table class="listingTable">
				<tr>
					<td width="30%"><span class="required"></span> <%= LanguageUtil.get(pageContext, "Check-Bank-Name") %>:</td>
					<td width="70%"><html:text property="checkBankName" /></td>
				</tr>
				<tr>
					<td><span class="required"></span> <%= LanguageUtil.get(pageContext, "Check-Number") %>:</td>
					<td><html:text property="checkNumber" /></td>
				</tr>
			</table>	
		</div>
			
		<div id="po">				
			<table class="listingTable">
				<tr>
					<td width="30%"><span class="required"></span> <%= LanguageUtil.get(pageContext, "PO-Number") %>:</td>
					<td width="70%"><html:text property="poNumber" /></td>
				</tr>
			</table>
		</div>
		
	</div>
	
	<div class="yui-u">
		<table class="listingTable">
			<tr>
				<th colspan="2"><%= LanguageUtil.get(pageContext, "Charges") %></th>
			</tr>
			<tr>
				<td width="30%"><%= LanguageUtil.get(pageContext, "SubTotal") %>:</td>
				<td width="70%">
					<html:hidden property="orderSubTotal" />
					<html:hidden property="orderSubTotalDiscount" />
					$ <%=UtilMethods.toPriceFormat(orderForm.getOrderSubTotal()) %>
				</td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Shipping-Type") %>:</td>
				<td>
					<%
					String[] shippingTypes = Config.getStringArrayProperty("SHIPPING_TYPES");
					int ground = Config.getIntProperty(shippingTypes[0]);
					int priority = Config.getIntProperty(shippingTypes[1]);
					int nextDay = Config.getIntProperty(shippingTypes[2]);
					String selectedGround = (orderForm.getOrderShipType() == ground ? "SELECTED" : "");
					String selectedPriority = (orderForm.getOrderShipType() == priority ? "SELECTED" : "");
					String selectedNextDay = (orderForm.getOrderShipType() == nextDay ? "SELECTED" : "");
					%>
					<html:select property="orderShipType" onchange="updateOrder();">
						<option value="<%=ground%>" <%=selectedGround %>><%= LanguageUtil.get(pageContext, "Ground") %></option>
						<option value="<%=priority%>" <%=selectedPriority %>><%= LanguageUtil.get(pageContext, "Second-Day") %></option>
						<option value="<%=nextDay%>" <%=selectedNextDay %>><%= LanguageUtil.get(pageContext, "Next-Day") %></option>
					</html:select>
				</td>
			</tr>
		
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Shipping-and-Handling") %>:</td>
				<td>$ <html:text property="orderShipping" styleId="orderShipping" onchange="javascript:recalculateTotal();"/></td>
			</tr>																	
		
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Tax") %>:</td>
				<td>$ <html:text property="orderTax" styleId="orderTax" onchange="javascript:recalculateTotal();"/></td>
			</tr>
			
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Tax-Exempt--") %>:</td>
				<td># <html:text property="taxExemptNumber" onkeyup="TaxExceptionNumber();"/></td>
			</tr>
			
			<tr>
				<td><%= LanguageUtil.get(pageContext, "Discount-Codes") %>:</td>
				<td>
					<html:text property="discountCodes" onkeyup="DiscountCodes();"/>
					<button dojoType="dijit.form.Button" onclick="updateOrderDiscounts();">
						<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Apply")) %>
					</button>
				</td>
			</tr>

			<tr>
				<td><%= LanguageUtil.get(pageContext, "Total") %>:</td>
				<td>$ <html:text property="orderTotal" styleId="orderTotal"/></td>
			</tr>
			<html:hidden property="orderTotalPaid" />
		</table>
	</div>
</div>

<table class="listingTable" style="margin-top:20px;">		
	<tr>
		<th width="10%"><%= LanguageUtil.get(pageContext, "Actions") %></th>
		<th width="40%"><%= LanguageUtil.get(pageContext, "Products-Ordered") %></th>
		<th width="15%"><%= LanguageUtil.get(pageContext, "Format") %></td>
		<th width="10%"><%= LanguageUtil.get(pageContext, "Quantity") %></td>					
		<th width="15%"><%= LanguageUtil.get(pageContext, "Price") %></td>
		<th width="10%" align="right">
          <button dojoType="dijit.form.Button" onclick="addProducts()">
            <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-Product")) %>
          </button>
        </td>
	</tr>
	<%
	
	List<Holder> holders = shoppingCart.getHolders();
	List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
	int k = 0;
	if (holders.size() > 0)
	{
	for(int i = 0;i < holders.size();i++)
	{	
		Holder holder = holders.get(i);
		ProductFormat format = holder.getFormat();
		Product product = format.getProduct();
		int quantity = holder.getQuantity();
		float price = UtilMethods.getItemPriceWithDiscount(format,holder.getPrice(),discounts);
		if (!InodeUtils.isSet(holder.getInode()) && price == 0)
		{
			com.liferay.portal.model.User userSession = shoppingCart.getUser();						
			String userId = userSession.getUserId();
			com.dotmarketing.beans.UserProxy up = APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false);
			Organization org = (Organization) com.dotmarketing.factories.InodeFactory.getParentOfClass(up, Organization.class);
			boolean isPartner = ((org.getPartnerKey() != null && !org.getPartnerKey().equals("") )? true : false);    	
			
			ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);			
			price = (isPartner) ? productPrice.getPartnerPriceWithDiscount() : productPrice.getRetailPriceWithDiscount();
		}
		
		
		//Select the color
		String str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
		k++;
	%>
		<tr <%=str_style %> >
			<td>
				<a href="javascript:removeItem('<%=format.getInode()%>');"><span class="deleteIcon"></span></a>					
			</td>
			<td><%=product.getTitle()%></td>
			<td><%=format.getFormatName()%></td>
			<td>
				<input type="text" value="<%=quantity%>" name="holder_<%=holder.getInode()%>" size="4" onkeyup="Quantity()" >
			</td>
			<td colspan="2" nowrap>$ <%=UtilMethods.toPriceFormat(price)%></td>
		</tr>
	<%}
	}else{%>
		<tr>
			<td colspan="6">
				<div class="noResultsMessage"><bean:message key="message.order_manager.shopping_cart_empty" /></div>
			</td>
		</tr>								
	<%}%>
</table>


<div class="buttonRow">
	<button dojoType="dijit.form.Button" onclick="back();" iconClass="cancelIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %>
    </button>
	<%if(holders.size() > 0){ %>
		<button dojoType="dijit.form.Button" onclick="saveOrder();" iconClass="saveIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Update-Order")) %>
		</button>
	<% } %>
</div>

</html:form>

<script language="javascript">
changeLayer();
changeBillingStateTextLayer();
changeShippingStateTextLayer();
</script>

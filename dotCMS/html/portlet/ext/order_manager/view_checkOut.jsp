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
<%@ page import="com.dotmarketing.portlets.order_manager.struts.OrderForm" %>

<script language="javascript">
//Layer Management Variables
styleReference = "style.";
visibleLayer = "inline";
browser = "Explorer";
var zipChanged = false;

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
		//eval(referenceLayer(layerName)+styleReference+ 'display="none"');
		document.getElementById(layerName).style.display = "none";
	}

	function showLayer(layerName) 
	{ 
		//eval(referenceLayer(layerName)+styleReference+ 'display="'+visibleLayer+'"');
		document.getElementById(layerName).style.display = visibleLayer;
	} 
   
	function changeLayer()
	{
		var form = document.getElementById("OrderForm");
		var paymentType = dijit.byId('paymentType').attr('value');
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
	
	function confirmOrder()
	{			
		form = document.getElementById("OrderForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_checkout' />";
        href += "<portlet:param name='cmd' value='<%=Constants.ADD%>' />";
        href += "</portlet:actionURL>";
		form.action = href;
		form.submit();
	}
	
	function updateOrderState()
	{
		var form = document.getElementById("OrderForm");
		var stateLength = dijit.byId('billingState').attr('value').length;
		if (stateLength == 2)
		{
			updateOrder();
		}
	}
	
	function updateOrder()
	{			
		form = document.getElementById("OrderForm");
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_checkout' />";
        href += "<portlet:param name='cmd' value='<%=Constants.UPDATE%>' />";
        href += "</portlet:actionURL>";
		form.action = href;
		form.submit();
	}
	
	function viewShoppingCart()
	{		
        href = 	"<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>' >";
        href += "<portlet:param name='struts_action' value='/ext/order_manager/view_products' />";
        href += "<portlet:param name='cmd' value='<%=Constants.VIEW%>' />";
        href += "</portlet:actionURL>";    
		document.location.href = href;
	}
	
	function changeTaxLayer()
	{
		var form = document.getElementById("OrderForm");
		var paymentType = dijit.byId('billingState').attr('value');
		if (paymentType == "FL")
		{
			showLayer("tax");
		}
		else
		{
			hideLayer("tax");
		}
	}
	
	function fillShippingAddress()
	{
		checkBox = document.getElementById("SBA");
		var form = document.getElementById("OrderForm");
		if (checkBox.checked)
		{
			dijit.byId('shippingAddress1').attr('value', dijit.byId('billingAddress1').attr('value'));
			dijit.byId('shippingAddress2').attr('value', dijit.byId('billingAddress2').attr('value'));
			dijit.byId('shippingCity').attr('value', dijit.byId('billingCity').attr('value'));
			dijit.byId('shippingState').attr('value', dijit.byId('billingState').attr('value'));
			dijit.byId('shippingStateOtherCountryText').attr('value', dijit.byId('billingStateOtherCountryText').attr('value'));
			dijit.byId('shippingCountry').attr('value', dijit.byId('billingCountry').attr('value'));
			dijit.byId('shippingZip').attr('value', dijit.byId('billingZip').attr('value'));
			dijit.byId('shippingPhone').attr('value', dijit.byId('billingPhone').attr('value'));
			dijit.byId('shippingFax').attr('value', dijit.byId('billingFax').attr('value'));
		}
		else
		{
			dijit.byId('shippingAddress1').attr('value', '');
			dijit.byId('shippingAddress2').attr('value', '');
			dijit.byId('shippingCity').attr('value', '');
			dijit.byId('shippingState').attr('value', '');
			dijit.byId('shippingCountry').attr('value', '');
			dijit.byId('shippingZip').attr('value', '');
			dijit.byId('shippingPhone').attr('value', '');
			dijit.byId('shippingFax').attr('value', '');
		}
		changeShippingStateTextLayer();
	}
	
	function zipChange()
	{
		zipChanged = true;
	}
	
	function recalculateShipping()
	{
		if(zipChanged)
		{
			updateOrder();
		}
	}
	
	function changeBillingStateTextLayer(){
	var form = document.getElementById("OrderForm");
	
	var state = dijit.byId("billingState").attr('value');
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
	changeTaxLayer();
}

	function changeShippingStateTextLayer(){
	var form = document.getElementById("OrderForm");
	
	var state = dijit.byId("shippingState").attr('value');
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

</script>
<%    
	OrderForm orderForm = (OrderForm) request.getAttribute("orderFormAux");
	request.setAttribute("OrderForm",orderForm);
	ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(com.dotmarketing.util.WebKeys.SHOPPING_CART);
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/order_manager/view_products"});
	params.put("cmd",new String[] {Constants.VIEW});
	
	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	//referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
	float subTotal = 0;
	
%>

<!--<%= LanguageUtil.get(pageContext, "New-Order") %>: <%= LanguageUtil.get(pageContext, "Checkout") %></b>-->

<html:form action="/ext/order_manager/view_checkout" styleId="OrderForm" >
<%String token = (UtilMethods.isSet((String) session.getAttribute("org.apache.struts.action.TOKEN")) ? (String) session.getAttribute("org.apache.struts.action.TOKEN") : ""); %>
<input type="hidden" name="org.apache.struts.taglib.html.TOKEN" value="<%=token%>">


<table class="listingTable" style="margin-bottom:20px;">
	<tr class="header">
		<th width="200"><%= LanguageUtil.get(pageContext, "Title") %></th>
		<th><%= LanguageUtil.get(pageContext, "Format") %></th>
		<th width="50" style="text-align:center;"><%= LanguageUtil.get(pageContext, "Quantity") %></th>					
		<th width="100" style="text-align:center;"><%= LanguageUtil.get(pageContext, "Unit-Price") %></th>
		<th width="100" style="text-align:center;"><%= LanguageUtil.get(pageContext, "Total-Price") %></th>
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
			//float price = holder.getPrice();
			float price = UtilMethods.getItemPriceWithDiscount(format,holder.getPrice(),discounts);
			float lineTotal = price * quantity;
			subTotal += lineTotal;
			//Select the color
			String str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
			k++;
		%>
			<tr <%=str_style %> >
				<td>&nbsp;<%=product.getTitle()%> </td>
				<td><%=format.getFormatName()%></td>
				<td align="center"><%=quantity%></td>					
				<td align="center"><%=UtilMethods.toPriceFormat(price)%></td>
				<td align="right"><%=UtilMethods.toPriceFormat(lineTotal)%></td>
			</tr>		
		<%}%>
	<%}else{%>
		<tr>
			<td colspan="5">
				<div class="noResultsMessage"><bean:message key="message.order_manager.shopping_cart_empty" /></div><
			</td>
		</tr>
	<%}%>
	<tr style="border-top:2px solid #ccc; border-left:none;">
		<td colspan="2" style="border:none;">&nbsp;</td>
		<th  colspan="2" style="text-align:right;border-left:1px solid #d0d0d0;"><%= LanguageUtil.get(pageContext, "SubTotal") %></th>
		<td align="right">
			<html:hidden property="orderSubTotal" />
			<html:hidden property="orderSubTotalDiscount" />
			$ <%=UtilMethods.toPriceFormat(subTotal) %>
		</td>
	</tr>
	<tr style="border:none;">
		<td colspan="2" style="border:none;">&nbsp;</td>
		<th colspan="2" style="text-align:right;border-left:1px solid #d0d0d0;"><%= LanguageUtil.get(pageContext, "Tax") %></th>
		<td align="right">
			<html:hidden property="orderTax" />
			$ <%=UtilMethods.toPriceFormat(orderForm.getOrderTax()) %>
		</td>
	</tr>
	<tr style="border:none;">
		<td colspan="2" style="border:none;">&nbsp;</td>
		<th colspan="2" style="text-align:right;border-left:1px solid #d0d0d0;"><%= LanguageUtil.get(pageContext, "Shipping") %>:</th>
		<td align="right">
			<%
			String[] shippingTypes = Config.getStringArrayProperty("SHIPPING_TYPES");
			int ground = Config.getIntProperty(shippingTypes[0]);
			int priority = Config.getIntProperty(shippingTypes[1]);
			int nextDay = Config.getIntProperty(shippingTypes[2]);
			String selectedGround = (orderForm.getOrderShipType() == ground ? "SELECTED" : "");
			String selectedPriority = (orderForm.getOrderShipType() == priority ? "SELECTED" : "");
			String selectedNextDay = (orderForm.getOrderShipType() == nextDay ? "SELECTED" : "");
			%>
			<select dojoType="dijit.form.FilteringSelect" name="orderShipType" onchange="updateOrder();">
				<option value="<%=ground%>" <%=selectedGround %>><%= LanguageUtil.get(pageContext, "Ground") %></option>
				<option value="<%=priority%>" <%=selectedPriority %>><%= LanguageUtil.get(pageContext, "Second-Day") %></option>
				<option value="<%=nextDay%>" <%=selectedNextDay %>><%= LanguageUtil.get(pageContext, "Next-Day") %></option>
			</select>
			</td>
	</tr>
	<tr style="border:none;">
		<td colspan="2" style="border:none;">&nbsp;</td>
		<th colspan="2" style="text-align:right;border-left:1px solid #d0d0d0;"><%= LanguageUtil.get(pageContext, "Shipping-and-Handling") %></th>
		<td align="right">
			<html:hidden property="orderShipping" />
			$ <%=UtilMethods.toPriceFormat(orderForm.getOrderShipping()) %>
		</td>
	</tr>
	<%if (orderForm.getIsShippingZero()){ %>
		<tr style="border:none;">
			<td colspan="4" align="right" style="border:none;">
				<span style="color:#666;font-weight:bold;"><%= LanguageUtil.get(pageContext, "We-will-apply-shipping-later-we-are-only-applying-handling-charges-now") %></span>
			</td>
			<td>&nbsp;</td>
		</tr>
	<%} %>
	<tr style="border:none;">
		<td colspan="2" style="border:none;">&nbsp;</td>
		<th colspan="2" style="text-align:right;border-left:1px solid #d0d0d0;"><%= LanguageUtil.get(pageContext, "Total") %></th>
		<td align="right">
			<html:hidden property="orderTotal" />
			<b>$ <%=UtilMethods.toPriceFormat(orderForm.getOrderTotal()) %></b>
		</td>
	</tr>
</table>

<div id="tax" style="text-align:right;margin:-10px 0 20px 0;">
	<b><%= LanguageUtil.get(pageContext, "Tax-Exempt--") %></b>
	<input type="text" dojoType="dijit.form.TextBox" name="taxExemptNumber" value="<%= UtilMethods.isSet(orderForm.getTaxExemptNumber()) ? orderForm.getTaxExemptNumber() : "" %>" />
	<button dojoType="dijit.form.Button" onClick="updateOrder();">
	   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Update-Tax")) %>
	</button>
</div>

<div class="shadowBoxLine">

	<dl>
		<dt style="color:#990000;"><%= LanguageUtil.get(pageContext, "Payment-Type") %></dt>
		<dd>
			<select dojoType="dijit.form.FilteringSelect" name="paymentType" id="paymentType" onchange="changeLayer();">
				<%boolean set = (UtilMethods.isSet(orderForm.getPaymentType()) ? true : false);%>
				<%String selected = (set && orderForm.getPaymentType().equals("cc") ? "SELECTED" : ""); %>
				<option value="cc" <%=selected%>><%=Config.getStringProperty("ECOM_CREDIT_CARD_FN")%></option>
				<%selected = (set && orderForm.getPaymentType().equals("ch") ? "SELECTED" : ""); %>
				<option value="ch" <%=selected%>><%=Config.getStringProperty("ECOM_CHECK_FN")%></option>
				<%selected = (set && orderForm.getPaymentType().equals("po") ? "SELECTED" : ""); %>
				<option value="po" <%=selected%>><%=Config.getStringProperty("ECOM_PURCHASE_ORDER_FN")%></option>
			</select>
		</dd>
	</dl>

	<dl id="cc">
		<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Name-on-Card") %></dt>
		<dd><input type="text" dojoType="dijit.form.TextBox" name="nameOnCard" style="width:165px;" value="<%= UtilMethods.isSet(orderForm.getNameOnCard()) ? orderForm.getNameOnCard() : "" %>" /></dd>
	
		<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Card-Type") %></dt>
		<dd>
			<select dojoType="dijit.form.FilteringSelect" name="cardType" style="width:165px;">
				<%set = (UtilMethods.isSet(orderForm.getCardType()) ? true : false);%>
				<%selected = (set && orderForm.getCardType().equals("vs") ? "SELECTED" : "");%>
				<option value="vs" <%=selected%>>Visa</option>
				<%selected = (set && orderForm.getCardType().equals("mc") ? "SELECTED" : "");%>
				<option value="mc" <%=selected%>>Masterd Card</option>
				<%selected = (set && orderForm.getCardType().equals("ae") ? "SELECTED" : "");%>
				<option value="ae" <%=selected%>>American Express</option>
				<%selected = (set && orderForm.getCardType().equals("dc") ? "SELECTED" : "");%>
				<option value="dc" <%=selected%>>Discover</option>
				
			</select>
		</dd>
	
		<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Card-Number") %></dt>
		<dd><input type="text" dojoType="dijit.form.TextBox" name="cardNumber"  style="width:165px;" value="<%= UtilMethods.isSet(orderForm.getCardNumber()) ? orderForm.getCardNumber() : "" %>" /> <span class="inputCaption">(<%= LanguageUtil.get(pageContext, "No-spaces-or-dashes") %>)</span></dd>
	
		<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Expiration-Date") %></dt>
		<dd>
			<%Date now = new Date();
			  Calendar calendar = Calendar.getInstance();
			  calendar.setTime(now);
			  int month = orderForm.getCardExpMonth();
			  int year = orderForm.getCardExpYear();%>
			<select dojoType="dijit.form.FilteringSelect" name="cardExpMonth">
				<%for(int i = 0; i < 12;i++){
					String monthSelected = (i == month ? "selected" : "");
				%>
					<option value="<%=i%>" <%=monthSelected%> ><%=UtilMethods.getMonthName(i)%></option>
				<%}%>
			</select>
			<select dojoType="dijit.form.FilteringSelect" name="cardExpYear">
				<%for(int i = calendar.get(Calendar.YEAR);i < calendar.get(Calendar.YEAR) + 5;i++){
					String yearSelected = (i == year ? "selected" : "");
				%>
					<option value="<%=i%>" <%=yearSelected%> ><%=i%></option>
				<%}%>
			</select>
		</dd>
	
		<dt><%= LanguageUtil.get(pageContext, "Card-Verification-Value") %></dt>
		<dd><input type="text" dojoType="dijit.form.TextBox" name="cardVerificationValue"  style="width:50px;" <%= UtilMethods.isSet(orderForm.getCardVerificationValue()) ? orderForm.getCardVerificationValue() : "" %> /></dd>
	</dl>


	<dl id="check">
		<!--
		<dt><span class="required"></span> >Check Bank Name</dt>
		<dd><input type="text" name="checkBankName" value="$!orderForm.checkBankName" ></dd>
		
		<dt><span class="required"></span> Check Number</dt>
		<dd><input type="text" name="checkNumber" value="$!orderForm.checkNumber" ></dd>
		-->
	</dl>
	
	<dl id="po">
		<dt><%= LanguageUtil.get(pageContext, "PO-Number") %></dt>
		<dd><input type="text" dojoType="dijit.form.TextBox" name="poNumber" value="<%= UtilMethods.isSet(orderForm.getPoNumber()) ? orderForm.getPoNumber() : "" %>" /></dd>
	</dl>
	
</div>



<div class="yui-g shadowBoxLine">
	<div class="yui-u first">
		<h3><%= LanguageUtil.get(pageContext, "Billing-Address") %>:</h3>
			
		<dl>
			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Address-Street-1") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingAddress1" id="billingAddress1" value="<%= UtilMethods.isSet(orderForm.getBillingAddress1()) ? orderForm.getBillingAddress1() : "" %>" /></dd>

			<dt><%= LanguageUtil.get(pageContext, "Address-Street-2") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingAddress2" id="billingAddress2" value="<%= UtilMethods.isSet(orderForm.getBillingAddress2()) ? orderForm.getBillingAddress2() : "" %>" /></dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "City") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingCity" id="billingCity" value="<%= UtilMethods.isSet(orderForm.getBillingCity()) ? orderForm.getBillingCity() : "" %>" /></dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "State") %></dt>
			<dd>
				<select dojoType="dijit.form.FilteringSelect" name="billingState" id="billingState" onchange="changeBillingStateTextLayer();">
					<script language="javascript">
						writeStatesOptions('<%= orderForm.getBillingState() %>');
					</script>
				</select>
			</dd>
		</dl>
		<dl id="billingStateTextDiv">
			<dt>&nbsp;</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingStateOtherCountryText" id="billingStateOtherCountryText" value="<%= UtilMethods.isSet(orderForm.getBillingStateOtherCountryText()) ? orderForm.getBillingStateOtherCountryText() : "" %>" /></dd>
		</dl>
		<dl>
			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Country") %></dd>
			<dd>
				<script language="javascript">writeCountriesSelect("billingCountry", '<%= UtilMethods.isSet(orderForm.getBillingCountry()) ? orderForm.getBillingCountry() : "" %>', true);</script>
				<!-- 
					<select name="billingCountry" id="billingCountry">
					<script language="javascript">
					writeCountriesSelect('<%= orderForm.getBillingCountry() %>');
					</script>
					</select>
				 -->
			</dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Zip") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingZip" id="billingZip" value="<%= UtilMethods.isSet(orderForm.getBillingZip()) ? orderForm.getBillingZip() : "" %>" /></dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Phone") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingPhone" id="billingPhone" value="<%= UtilMethods.isSet(orderForm.getBillingPhone()) ? orderForm.getBillingPhone() : "" %>" /></dd>

			<dt><%= LanguageUtil.get(pageContext, "Fax") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="billingFax" id="billingFax" value="<%= UtilMethods.isSet(orderForm.getBillingFax()) ? orderForm.getBillingFax() : "" %>" /></dd>
		</dl>
	</div>
	
	<div class="yui-u">
		
		<h3><%= LanguageUtil.get(pageContext, "Shipping-Address") %>:</h3>

		<div style="padding:5px 0;">
			<input type="checkbox" dojoType="dijit.form.CheckBox" name="SBA" id="SBA" onClick="fillShippingAddress();">
			<label for="SBA"><%= LanguageUtil.get(pageContext, "Shipping-Address-is-the-same-as-Billing-Address") %></label>
		</div>

		<dl>

			<dt><%= LanguageUtil.get(pageContext, "Shipping-Label") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingLabel" value="<%= UtilMethods.isSet(orderForm.getShippingLabel()) ? orderForm.getShippingLabel() : "" %>" /></dd>
	
			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Address-Street-1") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingAddress1" id="shippingAddress1" value="<%= UtilMethods.isSet(orderForm.getShippingAddress1()) ? orderForm.getShippingAddress1() : "" %>" /> <span class="inputCaption">(<%= LanguageUtil.get(pageContext, "No-PO-Boxes") %>)</span> </dd>
	
	
			<dt><%= LanguageUtil.get(pageContext, "Address-Street-2") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingAddress2" id="shippingAddress2" value="<%= UtilMethods.isSet(orderForm.getShippingAddress2()) ? orderForm.getShippingAddress2() : "" %>" /></dd>
	
			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "City") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingCity" id="shippingCity" value="<%= UtilMethods.isSet(orderForm.getShippingCity()) ? orderForm.getShippingCity() : "" %>" /></dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "State") %></dt>
			<dd>
				<select dojoType="dijit.form.FilteringSelect" name="shippingState" id="shippingState" onchange="changeShippingStateTextLayer();">
					<script language="javascript">
						writeStatesOptions('<%= orderForm.getShippingState() %>');
					</script>
				</select>
			</dd>
		</dl>
		
		<dl id="shippingStateTextDiv">
			<dt>&nbsp;</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingStateOtherCountryText" id="shippingStateOtherCountryText" value="<%= UtilMethods.isSet(orderForm.getShippingStateOtherCountryText()) ? orderForm.getShippingStateOtherCountryText() : "" %>" /></dd>
		</dl>
		
		<dl>
			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Country") %></dt>
			<dd>
				<script language="javascript">writeCountriesSelect("shippingCountry", '<%= UtilMethods.isSet(orderForm.getShippingCountry()) ? orderForm.getShippingCountry() : "" %>', true);</script>
				<!-- 
					<select name="shippingCountry" id="shippingCountry">
					<script language="javascript">
					writeCountriesSelect('<%= orderForm.getShippingCountry() %>');
					</script>
					</select>
				 -->
				<!--<html:text property="shippingState" />-->
			</dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Zip") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingZip" id="shippingZip" onkeyup="zipChange();" onblur="recalculateShipping();" value="<%= UtilMethods.isSet(orderForm.getShippingZip()) ? orderForm.getShippingZip() : "" %>" /></dd>

			<dt><span class="required"></span> <%= LanguageUtil.get(pageContext, "Phone") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingPhone" id="shippingPhone" value="<%= UtilMethods.isSet(orderForm.getShippingPhone()) ? orderForm.getShippingPhone() : "" %>" /></dd>

			<dt><%= LanguageUtil.get(pageContext, "Fax") %></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="shippingFax" id="shippingFax" value="<%= UtilMethods.isSet(orderForm.getShippingFax()) ? orderForm.getShippingFax() : "" %>" /></dd>
		</dl>
	</div>
</div>
	

<div class="buttonRow">
    <button dojoType="dijit.form.Button" onClick="viewShoppingCart();" iconClass="editIcon">
       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Make-Changes")) %>
    </button>
	
	<%if(holders.size() > 0){ %>
        <button dojoType="dijit.form.Button" onClick="confirmOrder();" iconClass="nextIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Place-Order-Now")) %>
        </button>
	<%} %>
</div>


</html:form>

<script language="javascript">
	dojo.addOnLoad(function () {
		changeLayer();
		changeTaxLayer();
		changeShippingStateTextLayer();
		changeBillingStateTextLayer();
	});
</script>


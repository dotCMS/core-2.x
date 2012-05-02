<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.portlets.product.factories.ProductFactory" %>
<%@ page import="com.dotmarketing.portlets.product.factories.ProductFormatFactory" %>
<%@ page import="com.dotmarketing.portlets.discountcode.struts.DiscountCodeForm" %>
<%@ page import="com.liferay.portal.util.Constants" %>
<%@ page import="com.dotmarketing.portlets.categories.model.Category" %>
<%@ page import="com.dotmarketing.util.UtilHTML" %>
<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ include file="/html/portlet/ext/discountcode/init.jsp" %>


<script language="javascript">
<%
	DiscountCode discountCode = (DiscountCode) request.getAttribute(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNTS);
	DiscountCodeForm discountCodeForm = (DiscountCodeForm) request.getAttribute("DiscountCodeForm");	
	
	int lineSize = 115;
%>
function submitForm()
{	    	
	var form = document.getElementById("discount");
	var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
	action += "<portlet:param name='struts_action' value='/ext/discountcode/edit_discountcode' />";
	action += "<portlet:param name='inode' value='<%=String.valueOf(discountCode.getInode())%>' />";
	action += "</portlet:actionURL>";
	form.action = action;
	form.submit();
}

/*function checkSelected()
{
	var form = document.getElementById("discount");
	selected = false;
	for(i = 0;i < form.products.length;i++)
	{
		if(form.products[i].selected == true)
		{
			selected = true;
			break;
		}
	}
	
	if(!selected)
	{
		for(i = form.products.length - 1;i >= 0;i--)
		{
			form.products[i].selected = true;
		}
	}	
}*/

function cancel()
{
	var href = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
	href += "<portlet:param name='struts_action' value='/ext/discountcode/view_discountcode' />";
	href += "</portlet:renderURL>";
	document.location.href = href;
}

function startDateOnClick() {
	var date = dijit.byId('startDateDate').attr('value');
	if (date != null)
		document.getElementById("startDate").value = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
	else
		document.getElementById("startDate").value = "";
}

function endDateOnClick() {
	var date = dijit.byId('endDateDate').attr('value');
	if (date != null)
		document.getElementById("endDate").value = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
	else
		document.getElementById("endDate").value = "";
}

function setCurrency(currencyString)
{
	var currency = document.getElementById("currency");
	currency.value = "(" + currencyString + ")";	
}

<!-- ### Set the number of calendars to use ### -->
<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
  <liferay:param name="calendar_num" value="2" />
</liferay:include>
<!-- ### END Set the number of calendars to use ### -->	
</script>

<!-- load the main HTMLArea file -->

<div class="shadowBoxLine">

<html:form styleId="discount" action="/ext/discountcode/edit_discountcode">  
<input type="hidden" name="<%=Constants.CMD%>" value="<%=Constants.ADD%>">
<input name="referer" type="hidden" value="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/discountcode/view_discountcode" /></portlet:renderURL>">

<dl>
	<dt><%= LanguageUtil.get(pageContext, "Coupon-ID") %>:</dt>
	<dd><input type="text" dojoType="dijit.form.TextBox" name="codeId" value="<%= UtilMethods.isSet(discountCodeForm.getCodeId()) ? discountCodeForm.getCodeId() : "" %>" /></dd>

	<dt><%= LanguageUtil.get(pageContext, "Description") %>:</dt>
	<dd><input type="text" dojoType="dijit.form.TextBox" name="codeDescription" style="width:400" value="<%= UtilMethods.isSet(discountCodeForm.getCodeDescription()) ? discountCodeForm.getCodeDescription() : "" %>" /></dd>

	<dt><%= LanguageUtil.get(pageContext, "Start-Date") %>:</dt>
	<dd>
<%
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String startDate = "";
		if (UtilMethods.isSet(discountCodeForm.getStartDate())) {
			Date sDate = sdf.parse(discountCodeForm.getStartDate());
			startDate = (sDate.getYear() + 1900) + "-" + (sDate.getMonth() < 9 ? "0" : "") + (sDate.getMonth() + 1) + "-" + (sDate.getDate() < 10 ? "0" : "") + sDate.getDate();
		}
%>
		<input type="text" dojoType="dijit.form.DateTextBox" validate='return false;' invalidMessage="" id="startDateDate" name="startDateDate" value="<%= startDate %>" onChange="startDateOnClick();" />
		<html:hidden property="startDate" styleId="startDate" />
	</dd>
	
	<dt><%= LanguageUtil.get(pageContext, "End-Date") %>:</dt>
	<dd>
<%
		String endDate = "";
		if (UtilMethods.isSet(discountCodeForm.getEndDate())) {
			Date eDate = sdf.parse(discountCodeForm.getEndDate());
			endDate = (eDate.getYear() + 1900) + "-" + (eDate.getMonth() < 9 ? "0" : "") + (eDate.getMonth() + 1) + "-" + (eDate.getDate() < 10 ? "0" : "") + eDate.getDate();
		}
%>
		<input type="text" dojoType="dijit.form.DateTextBox" validate='return false;' invalidMessage="" id="endDateDate" name="endDateDate" value="<%= endDate %>" onChange="endDateOnClick();" />
		<html:hidden property="endDate" styleId="endDate" />
	</dd>

	<dt><%= LanguageUtil.get(pageContext, "Discount-Type") %>:</dt>
	<dd>
		<input dojoType="dijit.form.RadioButton" type="radio" name="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE%>" onclick="setCurrency('%')" id="discountTypePercentage" <%= (discountCodeForm.getDiscountType() == Integer.parseInt(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE)) ? "checked" : "" %> /> <label for="discountTypePercentage"><%= LanguageUtil.get(pageContext, "Percentage") %></label>
		<input dojoType="dijit.form.RadioButton" type="radio" name="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT%>" onclick="setCurrency('$')" id="discountTypeDiscount" <%= (discountCodeForm.getDiscountType() == Integer.parseInt(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT)) ? "checked" : "" %> /> <label for="discountTypeDiscount"><%= LanguageUtil.get(pageContext, "Discount") %></label>
	</dd>

	<dt></dt>
	<dd>
		<input type="checkbox" dojoType="dijit.form.CheckBox" name="freeShipping" id="freeShippingCheckbox" <%= discountCodeForm.isFreeShipping() ? "checked" : "" %> />
		<label for="freeShippingCheckbox"><%= LanguageUtil.get(pageContext, "Free-Shipping") %></label>

		<input type="checkbox" dojoType="dijit.form.CheckBox" name="noBulkDisc" styleId="noBulkDiscCheckbox" <%= discountCodeForm.isNoBulkDisc() ? "checked" : "" %> />
		<label for="noBulkDiscCheckbox"><%= LanguageUtil.get(pageContext, "No-Bulk-Discount") %></label>
	</dd>

	<dt><%= LanguageUtil.get(pageContext, "Discount-Amount") %>:</dt>
	<dd>
		<input type="text" dojoType="dijit.form.TextBox" name="discountAmount" size="10" value="<%= discountCodeForm.getDiscountAmount() %>" />
			<%
			int discountType = discountCode.getDiscountType();
			String discountTypeString = "";
			if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE))
			{
				discountTypeString = "(%)";
			}
			else if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT))
			{
				discountTypeString = "($)";
			}				
			%>
		<input id="currency" type="text" style="border:0;width:20" readonly value="<%=discountTypeString%>" >
	</dd>


	<dt><%= LanguageUtil.get(pageContext, "Minimum-Order") %>:</dt>
	<dd><input type="text" dojoType="dijit.form.TextBox" name="minOrder" value="<%= discountCodeForm.getMinOrder() %>" /></dd>

	<dt><%= LanguageUtil.get(pageContext, "Product") %>:</dt>
	<dd>
		<script type="text/javascript">
			dojo.require("dijit.form.MultiSelect");
		</script>
		<select dojoType="dijit.form.MultiSelect" name="products" size="8" multiple="true"></dd>
			<%
			String[] selectedProducts;
			if (discountCodeForm.getProducts() != null) {
				selectedProducts = (String[]) discountCodeForm.getProducts();
			} else {
				selectedProducts = new String[0];
			}
			
			List<Product> products = ProductFactory.getAllProducts("title");
			for(Product product : products)
			{
				String productTitle = product.getTitle();
				List<ProductFormat> formats = product.getFormats();
				for(ProductFormat format : formats)
				{
					//long formatInode = format.getInode();
					String formatInode = format.getInode();
					boolean found = false;
					for(int i = 0;i < selectedProducts.length;i++)
					{
						if (selectedProducts[i].equalsIgnoreCase(formatInode))
						{
							found = true;
							break;
						}
					}
					String SELECTED = (found ? "SELECTED" : "");
					String toolTip = productTitle + " : " + format.getFormatName();
					String fullProductTitle = UtilMethods.truncFull(toolTip,lineSize);
				%>
				<option title="<%=toolTip%>" value="<%=formatInode%>" <%=SELECTED%>><%=fullProductTitle%></option>
			  <%}
			}%>
		</select>
	</dd>
	<dd class="inputCaption"><%= LanguageUtil.get(pageContext, "If-you-want-this-discount-to-be-applied-to-all-products-please-don-t-select-any-products") %></dd>
	<dd class="inputCaption"><%= LanguageUtil.get(pageContext, "To-select-multiple-products-from-this-list-click-on-the-CTRL-Key-and-select-the-products-from-the-list") %></dd>	
</dl>
</div>

<div class="buttonRow">
	<button dojoType="dijit.form.Button" onClick="submitForm();" iconClass="saveIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
	</button>
	<button dojoType="dijit.form.Button" onClick="cancel();" iconClass="cancelIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Cancel")) %>
	</button>
</div>

</html:form>


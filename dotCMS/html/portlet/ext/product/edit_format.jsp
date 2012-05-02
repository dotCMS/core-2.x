<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.portlets.product.model.*" %>
<%@ page import="com.dotmarketing.portlets.product.struts.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.beans.*" %>
<%@ page import="com.dotmarketing.factories.*" %>
<%@ include file="/html/portlet/ext/product/init.jsp" %>

<% 
	ProductFormatForm productFormatForm = (ProductFormatForm) request.getAttribute("FormatForm");
	ProductFormat productFormat = (ProductFormat) request.getAttribute(com.dotmarketing.util.WebKeys.PRODUCT_PRODUCT_FORMAT);
	productFormatForm.setInode(productFormat.getInode());

	String referrer = (request.getParameter("referrer") != null ? request.getParameter("referrer") : "");	
	String decodeReferrer = java.net.URLDecoder.decode(referrer,"UTF-8");	
	
	ProductPriceForm productPriceForm = (ProductPriceForm)request.getAttribute("productPriceForm");
	Product p = ProductFactory.getProduct(productFormatForm.getProductInode() );
%>
<%@page import="com.dotmarketing.portlets.product.factories.ProductFactory"%>
<script language="javascript">
	<%@ include file="/html/portlet/ext/product/edit_format_js_inc.jsp" %>  
</script>

	<html:form action='/ext/product/edit_format' styleId="editFormat">   
	<input type="hidden" name="dispatch" id="dispatch" value="save">
	<input type="hidden" name="cmd" id="cmd" value="save">
	<input type="hidden" name="referrer" id="referrer" value="<%=referrer%>">
	<input type="hidden" name="decodeReferrer" id="decodeReferrer" value="<%=decodeReferrer%>">
	<html:hidden property="inode" styleId="inode" />
	<html:hidden property="productInode" />




<div class="buttonBoxLeft"><h3><%= LanguageUtil.get(pageContext, "Prices") %></h3></div>

<div class="yui-g shadowBoxLine">
	<div class="yui-u first">
		<dl>
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Variant-Type") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="format" style="width:250px;" value="<%= UtilMethods.isSet(productFormatForm.getFormat()) ? productFormatForm.getFormat() : "" %>" /></dd>
			<dd class="inputCaption"><%= LanguageUtil.get(pageContext, "e-g-size-color-format-etc") %></dd>
		
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Variant-Name") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="formatName" style="width:250px;" value="<%= UtilMethods.isSet(productFormatForm.getFormatName()) ? productFormatForm.getFormatName() : "" %>" /></dd>
			<dd class="inputCaption"><%= LanguageUtil.get(pageContext, "e-g-largered-dvd-etc") %></dd>
		
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Item-----SKU") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="itemNum" style="width:250px;" value="<%= UtilMethods.isSet(productFormatForm.getItemNum()) ? productFormatForm.getItemNum() : "" %>" /></dd>
		
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Inventory-Quantity") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="inventoryQuantity" style="width:250px;" value="<%= productFormatForm.getInventoryQuantity() %>" /></dd>
		
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Reorder-Trigger") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="reorderTrigger" style="width:250px;" value="<%= productFormatForm.getReorderTrigger() %>" /></dd>
		
			<dt><%= LanguageUtil.get(pageContext, "Weight") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" name="weight" style="width:250px;" value="<%= productFormatForm.getWeight() %>" /></dd>
		</dl>
	</div>
	<div class="yui-u">
		<%if(InodeUtils.isSet(productFormat.getInode())){%>
		<% List<ProductPrice> prices = productFormatForm.getPrices(); %>

			<div class="buttonBoxRight">
				<button dojoType="dijit.form.Button" onClick="dojo.byId('addPriceButtonRow').style.display='block'; dojo.byId('addPriceButtonRowDeleteButton').style.display='none'; dojo.byId('priceInode').value = 0; dijit.byId('addPrice').show();" iconClass="plusIcon">
					<%= LanguageUtil.get(pageContext, "Add-Price-Matrix") %>
				</button>
			</div>

			<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext, "Action") %></th>
				<th><%= LanguageUtil.get(pageContext, "Min-Qty") %></th>
				<th><%= LanguageUtil.get(pageContext, "Max-Qty") %></th>
				<th><%= LanguageUtil.get(pageContext, "Retail-Price") %> <span style="font-weight:normal;"><%= LanguageUtil.get(pageContext, "non-partner") %></span></th>
				<th><%= LanguageUtil.get(pageContext, "Partner-Price") %></th>
			</tr>
			<%
			if (prices.size() > 0)
			{
				for(int i = 0;i < prices.size();i++)
				{   
				    String str_style="";
					ProductPrice price = (ProductPrice) prices.get(i);
					
					/*if(i%2==0){
					   str_style="class=\"alternate_1\"";
					}
					else{
					   str_style="class=\"alternate_2\"";
					}*/
			%>
						
					<tr>
						<td align="center" nowrap>								
							<a href="javascript:editPrice('<%=productFormat.getInode()%>','<%=price.getInode()%>','<%=price.getMinQty()%>','<%=price.getMaxQty()%>','<%=UtilMethods.dollarFormat(price.getRetailPrice())%>','<%=UtilMethods.dollarFormat(price.getPartnerPrice())%>');">
							<span class="editIcon"></span></a>
							
							<a href="javascript:deletePrice('<%=productFormat.getInode()%>','<%=price.getInode()%>')">
							<span class="deleteIcon"></span></a>
						</td>
						<td align="center"><%=price.getMinQty()%></td>
						<td align="center"><%=price.getMaxQty()%></td>
						<td align="center"><%=UtilMethods.dollarFormat(price.getRetailPrice())%></td>
						<td align="center"><%=UtilMethods.dollarFormat(price.getPartnerPrice())%></td>
					</tr>
				<%}%>
			<%}else{%>
				<tr>
					<td colspan="5">
						<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-price-for-this-format") %></div>
					</td>
				</tr>
			<%}%>
		</table>
		<%}%>
	</div>
</div>

<div class="buttonRow">
	<button dojoType="dijit.form.Button" iconClass="previousIcon" onClick="window.location.href='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/product/edit_product" /><portlet:param name="inode" value="<%=p.getInode()%>" /><portlet:param name="referrer" value="<%=referrer%>" /></portlet:actionURL>';">
		<%= LanguageUtil.get(pageContext, "Back-to") %> <%=p.getTitle() %>
	</button>
	
	<%if (InodeUtils.isSet(productFormat.getInode())){%>
		<button dojoType="dijit.form.Button" onClick="deleteFormat();" iconClass="deleteIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "delete")) %>
		</button>
	<%}%>
    <button dojoType="dijit.form.Button" onClick="saveFormat();" iconClass="saveIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>
    </button>

    <button dojoType="dijit.form.Button" onClick="cancelFormat();" iconClass="cancelIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %>
    </button>
</div>


<div id="addPrice" dojoType="dijit.Dialog" style="display: none">
	<%if(InodeUtils.isSet(productFormat.getInode())){%>
		<input type="hidden" name="priceInode" id="priceInode" value="">
		<dl style="width:600px;">
			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Min-Qty") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" id="minQty" name="minQty" value="<%=productPriceForm.getMinQty()%>" style="width:250px;"></dd>

			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Max-Qty") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" id="maxQty" name="maxQty" value="<%=productPriceForm.getMaxQty()%>" style="width:250px;"></dd>

			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Retail-Price") %>: <span style="font-weight:normal;"><%= LanguageUtil.get(pageContext, "non-partner") %></span></dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" id="retailPrice" name="retailPrice" value="<%=productPriceForm.getRetailPriceString()%>" style="width:250px;"></dd>

			<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Max-Qty") %><%= LanguageUtil.get(pageContext, "Partner-Price") %>:</dt>
			<dd><input type="text" dojoType="dijit.form.TextBox" id="partnerPrice" name="partnerPrice" value="<%=productPriceForm.getPartnerPriceString()%>" style="width:250px;"></dd>
		</dl>
		
		<div id="addPriceButtonRow" style="text-align: center;">
			<button dojoType="dijit.form.Button" onClick="savePrice();" iconClass="saveIcon">
				<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
			</button>
		</div>
		<div id="addPriceButtonRowDeleteButton" style="text-align: center;">
			<button dojoType="dijit.form.Button" onClick="deletePriceMain();" iconClass="deleteIcon">
				<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Delete")) %>
			</button>
			<button dojoType="dijit.form.Button" onClick="savePrice();" iconClass="saveIcon">
				<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
			</button>
		</div>
	<%}%>
</div>


</html:form>   




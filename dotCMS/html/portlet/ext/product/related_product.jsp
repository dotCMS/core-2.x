<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.portlets.product.model.*" %>
<%@ page import="com.dotmarketing.portlets.product.struts.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.beans.*" %>
<%@ page import="com.dotmarketing.factories.*" %>
<%@ include file="/html/portlet/ext/product/init.jsp" %>

<% 
	ProductForm productForm = (ProductForm) request.getAttribute("DotProductForm");
	Product product = (Product) request.getAttribute(com.dotmarketing.util.WebKeys.PRODUCT_PRODUCT);
	String referrer = (request.getParameter("referrer")!=null) ? request.getParameter("referrer") : "" ;
	String decodeReferrer = java.net.URLDecoder.decode(referrer,"UTF-8");
	
	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/product/edit_product"});
	params.put("inode",new String[] {product.getInode()});
	params.put("referrer",new String[] {referrer});

	String actualReferrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	/*actualReferrer = java.net.URLEncoder.encode(actualReferrer,"UTF-8");*/
%>

<script language="javascript">	
	<%@ include file="/html/portlet/ext/product/relate_product_js_inc.jsp" %>
</script>
<body>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value='<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Relate-Product" )) %>'/>

<html:form action='/ext/product/relate_product' styleId="relateProduct">   
<input type="hidden" name="cmd" id="cmd" value="save">
<input type="hidden" name="decodeReferrer" id="decodeReferrer" value="<%=decodeReferrer%>">
<html:hidden property="inode" />

	<table class="listingTable">
		<tr>
			<th nowrap><%= LanguageUtil.get(pageContext, "Relate") %></th>
			<th><%= LanguageUtil.get(pageContext, "Title") %></th>
			<th><%= LanguageUtil.get(pageContext, "Short-Description") %></th>
		</tr>	
		<%
			List<Product> products = productForm.getProducts();
				if (products.size() > 0){   
					String str_style="";
					boolean checked;
					
						for(int i = 0;i < products.size();i++){
							/*
							if(i%2==0){
								str_style="class=\"alternate_1\"";
							}else{
								str_style="class=\"alternate_2\"";
							}
							*/		
						Product relatedProduct = products.get(i);
						if(!relatedProduct.getInode().equalsIgnoreCase(product.getInode())){
							checked = false;
							for (String tempRelatedProduct: productForm.getRelatedProducts()) {
								if (relatedProduct.getInode().equals(tempRelatedProduct)) {
									checked = true;
									break;
								}
							}
		%>				
						<tr>
							<td align="center" valign="top"><input type="checkbox" dojoType="dijit.form.CheckBox" name="relatedProducts" value="<%=relatedProduct.getInode()%>" <%= checked ? "checked" : "" %> /></td>
							<td width="30%" valign="top"><%=relatedProduct.getTitle()%></td>
							<td width="70%" valign="top"><%=relatedProduct.getShortDescription()%></td>
						</td>				
					<%}%>
		   		<%}%>
			<%}else{%>
				<tr><td colspan="3"><div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-Products-to-display") %></div></td></tr>
			<%} %>
	</table>
	
	<div class="buttonRow">		            
            <button dojoType="dijit.form.Button" onClick="relateProductProduct();" iconClass="saveIcon">
               <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Relate-Products")) %> 
            </button>
            
            <button dojoType="dijit.form.Button" onClick="cancel();" iconClass="cancelIcon">
               <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Cancel" )) %>
            </button>
	</div>
	
</html:form>   
</liferay:box>
</body>
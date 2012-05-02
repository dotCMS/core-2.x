<%@ page import="java.util.*" %>
<%@ page import="com.dotmarketing.portlets.webevents.model.*" %>
<%@ page import="com.dotmarketing.portlets.webevents.struts.*" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.dotmarketing.beans.*" %>
<%@ page import="com.dotmarketing.factories.*" %>
<%@ page import="com.dotmarketing.portlets.categories.model.*" %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.portlets.files.model.*" %>
<%@ page import="com.dotmarketing.portlets.files.business.*" %>
<%@ page import="com.dotmarketing.portlets.product.struts.ProductForm" %>
<%@ page import="com.dotmarketing.portlets.product.model.Product" %>
<%@ page import="com.dotmarketing.portlets.product.model.ProductFormat" %>
<%@ page import="com.dotmarketing.business.APILocator" %>
<%@ page import="com.dotmarketing.portlets.categories.business.CategoryAPI" %>

<%@ include file="/html/portlet/ext/product/init.jsp" %>

<% 
	FileAPI fileAPI = APILocator.getFileAPI();
	ProductForm productForm = (ProductForm) request.getAttribute("DotProductForm");
	Product product = (Product) request.getAttribute(com.dotmarketing.util.WebKeys.PRODUCT_PRODUCT);
	
	String returnURL = (request.getParameter("referrer") != null ? request.getParameter("referrer") : "");

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/product/edit_product"});
	params.put("inode",new String[] {product.getInode()});

	String referrer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
	referrer = java.net.URLEncoder.encode(referrer,"UTF-8");
	
	CategoryAPI catAPI = APILocator.getCategoryAPI();
	
	int lineSize = 90;
%>

<%@page import="com.dotmarketing.portlets.product.model.ProductPrice"%>

<script>
	<%@ include file="/html/portlet/ext/product/edit_product_js_inc.jsp" %>  
</script>

<html:form action='/ext/product/edit_product' styleId="ProductForm">   
<input type="hidden" name="dispatch" id="dispatch" value='<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>'>
<input type="hidden" name="inode" value="<%=product.getInode()%>">
<input type="hidden" name="submitParent" id="submitParent" value="">
<input type="hidden" name="referrer" id="referrer" value="<%=referrer%>">
<input type="hidden" name="fileIds" id="setFileIds" value="">
<input type="hidden" id="redirect" name="<portlet:namespace />redirect" value="<portlet:renderURL>
<portlet:param name="struts_action" value="/ext/webevents/view_events" /></portlet:renderURL>">
		
<!-- START TABS -->
<div id="mainTabContainer" dojoType="dijit.layout.TabContainer" dolayout="false">
	
	<!-- START Properties Tab -->
		<div id="propertiesTab" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Properties") %>">
			<!--<div style="float:right;"">
				<span class="required"></span> <%= LanguageUtil.get(pageContext, "Required-Fields") %>
			</div>-->
			
			<dl>
				<dt><%= LanguageUtil.get(pageContext, "Title") %>:</dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="title" size="45"  id="title" value="<%= UtilMethods.isSet(productForm.getTitle()) ? productForm.getTitle() : "" %>" /></dd>

				<!-- ### PRODUCT TYPE ### -->
				<%
					com.dotmarketing.portlets.entities.model.Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_PRODUCTS_TYPE);
					java.util.List cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);
					Iterator catsIter = cats.iterator();
					while (catsIter.hasNext()) {
						Category category = (Category) catsIter.next();
						List children = InodeFactory.getChildrenClassByOrder(category, Category.class, "sort_order");
						if (children.size()>0 && catAPI.canUseCategory(category,user,false)) {
					%>
						<dt>
							<span class="required"></span>  <%= LanguageUtil.get(pageContext, "Product-Type") %>:
						</dt>			
						<dd>
							<select dojoType="dijit.form.FilteringSelect" name="productTypes" >
								<%= com.dotmarketing.util.UtilHTML.getSelectCategories(category,1, productForm.getProductTypes(), user, false) %>
							</select>
						</dd>
					<% } %>
				<% } %>
			<!-- ### END PRODUCT TYPE ### -->

				<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Short-Description") %>:<dt>
				<dd>
					<textarea dojoType="dijit.form.Textarea" name="shortDescription" id="shortDescription" style="width:450px;min-height:100px;"><%= UtilMethods.isSet(productForm.getShortDescription()) ? productForm.getShortDescription() : "" %></textarea>
				</dd>
	
				<dt><span class="required"></span>  <%= LanguageUtil.get(pageContext, "Long-Description") %>:</dt>
				<dd><textarea dojoType="dijit.form.Textarea" name="longDescription" id="longDescription" style="width:450px;min-height:200px;" ><%= UtilMethods.isSet(productForm.getLongDescription()) ? productForm.getLongDescription() : "" %></textarea></dd>
			
				<dt><%= LanguageUtil.get(pageContext, "Requires-Shipping") %>:</dt>
				<dd><input type="checkbox" dojoType="dijit.form.CheckBox" name="reqShipping" value="true" <%= productForm.isReqShipping() ? "checked" : "" %> /></dd>
		
				<dt><%= LanguageUtil.get(pageContext, "Featured-Product") %>:</dt>
				<dd><input type="checkbox" dojoType="dijit.form.CheckBox" name="featured" value="true" <%= productForm.isFeatured() ? "checked" : "" %> /></dd>
		
				<dt><%= LanguageUtil.get(pageContext, "Show-On-Web") %>:</dt>
				<dd><input type="checkbox" dojoType="dijit.form.CheckBox" name="showOnWeb" value="true" <%= productForm.isShowOnWeb() ? "checked" : "" %> /></dd>
			</dl>
			
			<%if(InodeUtils.isSet(product.getInode())){ %>

				<%entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_CATEGORIES);
					cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);
					catsIter = cats.iterator();
					while (catsIter.hasNext()) {
						Category category = (Category) catsIter.next();
						List children = InodeFactory.getChildrenClassByOrder(category, Category.class, "sort_order");
						if (children.size()>0 && catAPI.canUseCategory(category,user,false)) {
							String catOptions = com.dotmarketing.util.UtilHTML.getSelectCategories(category,1,productForm.getTopics(), user, false);
							if(catOptions.length() > 1){%>
								<dt><%=category.getCategoryName()%>:</dt>			
								<dd>
									<select dojoType="dijit.form.FilteringSelect" name="topics" style="width:300;margin:0;">
										<option value=""></option>
										<%= catOptions %>
									</select>
								</dd>
						<%}%>
					 <%}%>
				 <%}%>
				 <%if(cats.size() ==0){ %>
					<dt><%=entity.getEntityName()%>:</dt>				
					<dd><%= LanguageUtil.get(pageContext, "There-are-no-categories-associated-with-eCommerce-Products") %></dd>
				 <%}%>
			<%}%>
			
		</div>
	<!-- END Properties Tab -->
	
<%if(InodeUtils.isSet(product.getInode())){ %>

	<!-- START Images Tab -->
	<div id="imagesTab" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Images") %>">
		<dl>
			<dt><%= LanguageUtil.get(pageContext, "Main-Product-Image") %></dt>
			<dd>
				<input type="text" name="smallImage" dojoType="dotcms.dijit.form.FileSelector" fileBrowserView="thumbnails" mimeTypes="image" 
					value="<%= productForm.getSmallImage() %>" showThumbnail="true" style="width: 380px;" />			
			</dd>
			
			<dt><%= LanguageUtil.get(pageContext, "Additional-Product-Image-1") %></dt>
			<dd>
				<input type="text" name="mediumImage" dojoType="dotcms.dijit.form.FileSelector" fileBrowserView="thumbnails" mimeTypes="image" 
					value="<%= productForm.getMediumImage() %>" showThumbnail="true" style="width: 380px;" />			
			</dd>

			<dt><%= LanguageUtil.get(pageContext, "Additional-Product-Image-2") %></dt>
			<dd>
				<input type="text" name="largeImage" dojoType="dotcms.dijit.form.FileSelector" fileBrowserView="thumbnails" mimeTypes="image" 
					value="<%= productForm.getLargeImage() %>" showThumbnail="true" style="width: 380px;" />			
			</dd>
		</dl>				
	</div>
	<!-- END Images Tab -->
		
	<!-- START Files Tab -->
	<div id="filesTab" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Files") %>">	
		
		<div class="buttonRow" style="text-align:right;">
            <button dojoType="dijit.form.Button" onClick="fileBrowser.show()" iconClass="browseIcon">
              <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Browse" )) %>
            </button>
		</div>
		
		<div id="noFilesTable" style="" class="noResultsMessage">
			<%= LanguageUtil.get(pageContext, "There-are-no-Files-for-this-Product-Add-a-file-now") %>
		</div>

		<table id="filesTable" class="listingTable">
		</table>
		
					
	</div>
	<!-- END Files Tab -->
	
	<!-- START Variants Tab -->
		<div id="variantsTab" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Variants---Prices") %>">
			<div class="portlet-toolbar" style="text-align:right;">
				<button dojoType="dijit.form.Button" oncLick="addFormat();" iconClass="plusIcon">
					<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-Variant-Price")) %>
				</button>
			</div>
			<%List formats = product.getFormats();%>			
				<%if(formats.size() > 0){%>
					<table class="listingTable">
						<tr>
							<th nowrap><%= LanguageUtil.get(pageContext, "Actions") %></th>
							<th width="100%"><%= LanguageUtil.get(pageContext, "Variant-Name") %></th>
							<th nowrap><%= LanguageUtil.get(pageContext, "Quantity") %></th>
							<th nowrap><%= LanguageUtil.get(pageContext, "Min-Qty") %></th>
							<th nowrap><%= LanguageUtil.get(pageContext, "Max-Qty") %></th>
							<th nowrap><%= LanguageUtil.get(pageContext, "Retail-Price") %></th>
							<th nowrap><%= LanguageUtil.get(pageContext, "Member-Price") %></th>
						</tr>
						<%for(int i = 0; i < formats.size();i++){
							ProductFormat format = (ProductFormat) formats.get(i);
							String str_style = (i % 2 == 0 ? "alternate_1" : "alternate_2");%>
							<tr bgcolor="#ffffff" style="cursor:pointer;" onmouseover="this.style.background='#FBF7BA';" onmouseout="this.style.background='#fff';">
								<td nowrap valign="top">
									<a href="javascript:editFormat('<%=format.getInode()%>')">
										<span class="editIcon"></span>
									</a>
									<a href="javascript:deleteFormat('<%=format.getInode()%>')">
										<span class="deleteIcon"></span>
									</a>
									<a href="javascript:copyFormat('<%=format.getInode()%>')">
										<span class="copyIcon"></span>
									</a>																	
								</td>
								<td valign="top" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<span title="<%=format.getFormatName()%>"><%=UtilMethods.truncatify(format.getFormatName(),lineSize)%></span>
								</td>
								<td valign="top" align="center" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<%for(int j=0;j< format.getBulkPrices().size();j++){ %>
										<%ProductPrice pp = (ProductPrice) format.getBulkPrices().get(j);%>
										<%=pp.getMinQty() %><br>
									<%} %>
								</td>
								<td valign="top" align="center" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<%for(int j=0;j< format.getBulkPrices().size();j++){ %>
										-<br>
									<%} %>
								</td>
								<td valign="top" align="center" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<%for(int j=0;j< format.getBulkPrices().size();j++){ %>
										<%ProductPrice pp = (ProductPrice) format.getBulkPrices().get(j);%>
										<%=pp.getMaxQty() %><br>
									<%} %>
								</td>
								<td valign="top" align="right" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<%for(int j=0;j< format.getBulkPrices().size();j++){ %>
										<%ProductPrice pp = (ProductPrice) format.getBulkPrices().get(j);%>
										$ <%=UtilMethods.dollarFormat(pp.getRetailPrice()) %><br>
									<%} %>
								</td>
								<td valign="top" align="right" nowrap onclick="editFormat('<%=format.getInode()%>')">
									<%for(int j=0;j< format.getBulkPrices().size();j++){ %>
										<%ProductPrice pp = (ProductPrice) format.getBulkPrices().get(j);%>
										$ <%=UtilMethods.dollarFormat(pp.getPartnerPrice()) %><br>
									<%} %>
								</td>
							</tr>					
			  			<%}	%>
		    	<%}else{%>
					<tr>
						<td colspan="5">
							<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "This-product-does-not-yet-have-prices") %></div>
						</td>
					</tr>
				<%}%>
			</table>
			
		</div>
	<!-- END Variants Tab -->
	
	<!-- START Related Products Tab -->
		<div id="relatedTab" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Related-Products") %>">
			<div class="portlet-toolbar" style="text-align:right;">
				<button dojoType="dijit.form.Button" onClick="relateProduct();" iconClass="plusIcon">
					<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Relate-Product")) %>
				</button>
			</div>
			
			<table class="listingTable">
				<% List relatedProducts = product.getRelatedProducts();			
				if(relatedProducts.size() > 0)
				{   
				    String str_style="";
					for(int i = 0; i < relatedProducts.size();i++)
					{
						Product relatedProduct = (Product) relatedProducts.get(i);
						str_style=(i % 2 == 0 ? "class=\"alternate_1\"" : "class=\"alternate_2\"");
						%>
						<tr <%=str_style %> >
							<td nowrap>
								<a href="javascript:unrelateProduct('<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>
								<portlet:param name='struts_action' value='/ext/product/relate_product' />
								<portlet:param name='inode' value='<%=product.getInode()%>' />
								<portlet:param name='referrer' value='<%=referrer%>' />
								<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.DELETE%>' />
								<portlet:param name='relatedProductInode' value='<%=relatedProduct.getInode()%>' />
								</portlet:actionURL>')">
								<span class="deleteIcon"></span>
								</a>							
							</td>
							<td width="100%"><%=relatedProduct.getTitle()%></td>
						</tr>														
					<%}	
				}else{%>
					<tr>
						<td colspan="2">
							<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-Related-Products-to-display") %></div>
						</td>
					</tr>
				<%}%>
			</table>
			
		</div>
	<!-- END Related Products  Tab -->
<%} %>

</div>
<!-- END TABS -->
<div class="clear"></div>
<div class="buttonRow">
	<button dojoType="dijit.form.Button" onClick="saveProduct();" iconClass="saveIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save" )) %>
	</button>
	<button dojoType="dijit.form.Button" onClick="cancel();" iconClass="cancelIcon">
		<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %>
	</button>
</div>

</html:form>
			

<script language="javascript">
	//Set event files
<%
	String[] fileIdentifiers = productForm.getFileIdList();
	if (fileIdentifiers != null) {
		for (int i = 0; i < fileIdentifiers.length; i++) {
			File file = fileAPI.getWorkingFileById(fileIdentifiers[i], user, false);
			if ((file != null) && InodeUtils.isSet(file.getInode())) {
%>
			addFile ({ identifier: '<%= file.getIdentifier() %>' , fileName:'<%= file.getFileName() %>' });
<%
			}
		}
	}
%>	
</script>

<div dojoAttachPoint="fileBrowser" jsId="fileBrowser" onFileSelected="addFile" onlyFiles="true" dojoType="dotcms.dijit.FileBrowserDialog">
</div>



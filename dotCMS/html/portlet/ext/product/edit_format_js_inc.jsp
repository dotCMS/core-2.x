<%@ page import="com.dotmarketing.util.InodeUtils" %>
<%@ page import="com.liferay.portal.language.LanguageUtil" %>
function saveFormat()
{
	var form = document.getElementById("editFormat");
	href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
	href += "<portlet:param name='struts_action' value='/ext/product/edit_format' />";
	href += "<portlet:param name='inode' value='<%=productFormat.getInode()%>' />";
	href += "<portlet:param name='referrer' value='<%=referrer%>' />";
	href += "</portlet:actionURL>";
	form.action = href;
	form.submit();
}

function deleteFormat()
{	
	if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Do-you-want-to-delete-this-Price-Variant")) %>')){
		href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
		href += "<portlet:param name='struts_action' value='/ext/product/edit_format' />";
		href += "<portlet:param name='inode' value='<%=productFormat.getInode()%>' />";
		href += "<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.DELETE%>' />";
		href += "<portlet:param name='referrer' value='<%=referrer%>' />";
		href += "</portlet:actionURL>";
		document.location.href = href;		
	}
}

function cancelFormat()
{
	var decodeReferrer = document.forms[0].decodeReferrer.value;
	document.location.href = decodeReferrer;
}

function addPrice()
{
<%if(!InodeUtils.isSet(productFormat.getInode())) {%>
	
		alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "You-Must-Save-the-Format-to-Add-Prices")) %>');
	<%}else{ %>
			href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
			href += "<portlet:param name='struts_action' value='/ext/product/edit_price' />";
			href += "<portlet:param name='productFormatInode' value='<%=productFormat.getInode()%>' />";
			href += "<portlet:param name='referrer' value='<%=referrer%>' />";
			href += "</portlet:actionURL>";
			document.location.href = href;
	<%}%>
}

function editPrice(formatInode,priceInode,minQty,maxQty,retailPrice,partnerPrice)
{
	dojo.byId('addPriceButtonRow').style.display='none';
	dojo.byId('addPriceButtonRowDeleteButton').style.display='block';
	dojo.byId('priceInode').value = priceInode;
	dojo.byId('minQty').value = minQty;
	dojo.byId('maxQty').value = maxQty;
	dojo.byId('retailPrice').value = retailPrice;
	dojo.byId('partnerPrice').value = partnerPrice;
	dijit.byId('addPrice').show();
}
	
function deletePrice(formatInode,priceInode)
{
	if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-price")) %>')){
		href = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>
					<portlet:param name='struts_action' value='/ext/product/edit_format' />				
					<portlet:param name='cmd' value='deletePrice' />
					<portlet:param name='referrer' value='<%=referrer%>' />
				</portlet:actionURL>";
		href += "&inode=" + formatInode + "&priceInode=" + priceInode;			
		document.location.href = href;
	}
}

function copyPrice(href)
{
	document.location.href = href;
}


function savePrice()
{
	var form = document.getElementById("editFormat");
	href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>
				<portlet:param name='struts_action' value='/ext/product/edit_format' />
				<portlet:param name='cmd' value='savePrice' />
				<portlet:param name='referrer' value='<%=referrer%>' />
			</portlet:actionURL>";
	href += "&inode=" + dojo.byId('inode').value;
	href += "&priceInode=" + dojo.byId('priceInode').value;
	href += "&minQty=" + dojo.byId('minQty').value;
	href += "&maxQty=" + dojo.byId('maxQty').value;
	href += "&retailPrice=" + dojo.byId('retailPrice').value;
	href += "&partnerPrice=" + dojo.byId('partnerPrice').value;
	form.action = href;
	form.cmd.value = "savePrice";
	form.submit();
}

function deletePriceMain()
{	
	var formatInode = dojo.byId('inode').value;
	var priceInode = dojo.byId('priceInode').value;
	deletePrice(formatInode, priceInode);
}

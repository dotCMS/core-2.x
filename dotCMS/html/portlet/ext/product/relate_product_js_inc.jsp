
function relateProductProduct()
{	
	var form = document.getElementById("relateProduct");
	href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
	href += "<portlet:param name='struts_action' value='/ext/product/relate_product' />";
	href += "<portlet:param name='inode' value='<%=product.getInode()%>' />";
	href += "<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.SAVE%>' />";
	href += "<portlet:param name='referrer' value='<%=actualReferrer%>' />";
	href += "</portlet:actionURL>";
	form.action = href;
	form.submit();
}

function cancel()
{
	var form = document.getElementById("relateProduct");
	var decodeReferrer = form.decodeReferrer.value;
	document.location.href = decodeReferrer;	
}
		
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.liferay.portal.language.LanguageUtil" %>
function savePrice()
{
	var form = document.getElementById("editPrice");
	href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
	href += "<portlet:param name='struts_action' value='/ext/product/edit_price' />";
	href += "<portlet:param name='inode' value='<%=productPrice.getInode()%>' />";
	href += "<portlet:param name='referrer' value='<%=referrer%>' />";
	href += "</portlet:actionURL>";
	form.action = href;
	form.submit();
}

function deletePrice()
{	
	if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-price")) %>')){
		href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
		href += "<portlet:param name='struts_action' value='/ext/product/edit_price' />";
		href += "<portlet:param name='inode' value='<%=productPrice.getInode()%>' />";
		href += "<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.DELETE%>' />";
		href += "<portlet:param name='referrer' value='<%=referrer%>' />";
		href += "</portlet:actionURL>";
		document.location.href = href;		
	}
}

function cancelPrice()
{
	href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
	href += "<portlet:param name='struts_action' value='/ext/product/edit_price' />";
	href += "<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.CANCEL%>' />";
	href += "<portlet:param name='referrer' value='<%=referrer%>' />";
	href += "</portlet:actionURL>";
	document.location.href = href;	
}
		
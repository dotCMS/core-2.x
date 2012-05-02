<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>			
<%@ include file="/html/portlet/ext/discountcode/init.jsp" %>


<script language="javascript">
	function submitFormOrdered(orderBy)
	{
		var orderby = document.getElementById("orderby");
		oldOrderBy = orderby.value;
		orderby.value = orderBy;
		var direction = document.getElementById("direction");
		if (oldOrderBy == orderBy && direction.value == "ASC")
		{
			direction.value = "DESC";
		}
		else
		{
			direction.value ="ASC";
		}
		submitForm();
	}
	function submitForm()
	{
		var href = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		href += "<portlet:param name='struts_action' value='/ext/discountcode/view_discountcode' />";
		href +  "</portlet:renderURL>";
		var form = document.getElementById("discount");
		form.action = href;
		form.submit();		
	}
	
	function deleteDiscount(href)
	{
		var form = document.getElementById("discount");
		form.action = href;
		form.submit();	
	}
	
	function setCalendarDate_0(year, month, day) 
	{	  
		var date = document.getElementById("startDate");
		date.value = month + "/" + day + "/" + year;		
	}
			  
  	function setCalendarDate_1(year, month, day) 
	{	  
		var date = document.getElementById("endDate");
		date.value = month + "/" + day + "/" + year;						
	}
	
	<!-- ### Set the number of calendars to use ### -->
	<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
	  <liferay:param name="calendar_num" value="2" />
	</liferay:include>
	<!-- ### END Set the number of calendars to use ### -->	
</script>
<br/>


<table border="1" cellpadding="4" cellspacing="0" width="90%">
	<html:form action="/ext/discountcode/view_discountcode" method="post" styleId="discount">
	<input type="hidden" name="referer" value="<portlet:renderURL windowState='<%=WindowState.NORMAL.toString()%>'><portlet:param name='struts_action' value='/ext/discountcode/view_discountcode' /></portlet:renderURL>" >
	<html:hidden property="orderby" styleId="orderby" />
	<html:hidden property="direction" styleId="direction" />
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Discount-Type") %> : </b></td>
		<td colspan="4">
			<html:radio property="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE%>" styleId="discountPercent"/><b><label for="discountPercent"><%= LanguageUtil.get(pageContext, "Percentage") %></label></b>&nbsp; &nbsp; 
			<html:radio property="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT%>" styleId="discountDiscount"/><b><label for="discountDiscount"><%= LanguageUtil.get(pageContext, "Discount-Type") %><%= LanguageUtil.get(pageContext, "Discount") %></label></b>
		</td>
	</tr>
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Start-Date") %>: </b></td>
		<td nowrap>
			<html:text property="startDate" styleId="startDate" readonly="true" onclick="_EXT_DISCOUNTCODE_calendarOnClick_0();"/>
			<img align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_0_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_0();">
		</td>
		<td><b><%= LanguageUtil.get(pageContext, "End-Date") %>: </b></td>	
		<td nowrap>
			<html:text property="endDate" styleId="endDate" readonly="true" onclick="_EXT_DISCOUNTCODE_calendarOnClick_1();" />
			<img align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_1_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_1();">		
		</td>
	</tr>
	<tr>
		<td><b><%= LanguageUtil.get(pageContext, "Coupon-ID") %>: </b></td>
		<td><html:text property="codeId" /></td>
		<td><b><%= LanguageUtil.get(pageContext, "Name") %>: </b></td>	
		<td><html:text property="codeDescription" /></td>
	</tr>
	<tr>
		<td></td>
		<td>
            <button dojoType="dijit.form.Button" onCLick="submitForm();">Search</button>
        </td>
		<td></td>	
		<td></td>
	</tr>
	<!-- START LISTING -->

	<tr class="beta">
		<td><b>&nbsp</b></td>
		<td><b><a href="javascript:submitFormOrdered('code_id');" class="beta"><%= LanguageUtil.get(pageContext, "Coupon-ID") %></a></b></td>
		<td><b><a href="javascript:submitFormOrdered('code_description');" class="beta"><%= LanguageUtil.get(pageContext, "Name") %></a></b></td>
		<td><b><a href="javascript:submitFormOrdered('start_date');" class="beta"><%= LanguageUtil.get(pageContext, "Start-Date") %></a></b></td>
		<td><b><a href="javascript:submitFormOrdered('end_date');" class="beta"><%= LanguageUtil.get(pageContext, "End-Date") %></a></b></td>
		<td><b><a href="javascript:submitFormOrdered('discount_type');" class="beta"><%= LanguageUtil.get(pageContext, "Discount-Type") %></a></b></td>
	</tr>
	
	<% ArrayList discounts = (ArrayList) request.getAttribute(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNTS);
	if (discounts.size() > 0)
	{
		for(int i = 0;i < discounts.size();i++)
		{
			DiscountCode discount = (DiscountCode) discounts.get(i);
			String color = (i % 2 == 0 ? "FFFFFF" : "EEEEEE");
		%>
		<tr bgcolor="<%=color%>">
			<td><a href="<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
							<portlet:param name="struts_action" value="/ext/discountcode/edit_discountcode" />
							<portlet:param name="inode" value="<%=String.valueOf(discount.getInode())%>" />
						</portlet:actionURL>" border="0"><span class="editIcon"></span></a>
						
				<a href="javascript:deleteDiscount('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
							<portlet:param name="struts_action" value="/ext/discountcode/edit_discountcode" />
							<portlet:param name="inode" value="<%=String.valueOf(discount.getInode())%>" />
							<portlet:param name="cmd" value="<%=com.liferay.portal.util.Constants.DELETE%>" />
						</portlet:actionURL>')" border="0"><span class="deleteIcon"></span></a>
			</td>
			<td><%=discount.getCodeId()%></td>
			<td>
				<a href="<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
							<portlet:param name="struts_action" value="/ext/discountcode/edit_discountcode" />
							<portlet:param name="inode" value="<%=String.valueOf(discount.getInode())%>" />
						</portlet:actionURL>">
					<%=discount.getCodeDescription()%>
				</a>
			</td>
			<td><%=UtilMethods.dateToHTMLDate(discount.getStartDate())%></td>
			<td><%=UtilMethods.dateToHTMLDate(discount.getEndDate())%></td>
			<%
			int discountType = discount.getDiscountType();
			String discountTypeString = "";
			if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE))
			{
				discountTypeString = "Percentage";
			}
			else if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT))
			{
				discountTypeString = "Discount";
			}
			%>
			<td><%=discountTypeString.equals("Percentage")?LanguageUtil.get(pageContext, "Percentage"):LanguageUtil.get(pageContext, "Discount")%></td>
		</tr>
		<%}
		}else{%>
		<tr bgcolor="FFFFFF"><td colspan="6">&nbsp;</td></tr>
		<tr align="center" bgcolor="FFFFFF">
			<td colspan="6"><b><%= LanguageUtil.get(pageContext, "There-are-not-discounts-to-display") %></b></td>
		</tr>
		<tr bgcolor="FFFFFF"><td colspan="6">&nbsp;</td></tr>
		<%}%>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<!-- END LISTING -->	
	</html:form>
	<tr>
		<td align="right" colspan="6">
			<a class="beta" href="<portlet:renderURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
				<portlet:param name="struts_action" value="/ext/discountcode/view_discountcode" />
				</portlet:renderURL>">
				<font size="2"><%= LanguageUtil.get(pageContext, "all") %></font>
			</a>
			|
			<a class="beta" href="<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
				<portlet:param name="struts_action" value="/ext/discountcode/edit_discountcode" />
				</portlet:actionURL>">
				<font size="2"><%= LanguageUtil.get(pageContext, "new") %></font>
			</a>
		</td>
	</tr>
</table>

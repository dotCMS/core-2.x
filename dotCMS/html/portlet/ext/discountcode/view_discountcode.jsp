<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.portlet.WindowState" %>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.dotmarketing.portlets.discountcode.model.DiscountCode" %>
<%@page import="com.dotmarketing.portlets.discountcode.struts.DiscountCodeForm"%>			
<%@ include file="/html/portlet/ext/discountcode/init.jsp" %>


<script type="text/javascript">	
	dojo.require("dijit.form.FilteringSelect");
	dojo.require("dotcms.dojo.data.UsersReadStore");
	
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
	
	function addDiscountCode()
	{
		var href = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		href += "<portlet:param name='struts_action' value='/ext/discountcode/edit_discountcode' />";
		href+= "</portlet:actionURL>";
		
		document.location.href = href;
	}
	
	function deleteDiscount(href)
	{
		var form = document.getElementById("discount");
		form.action = href;
		form.submit();	
	}
	
	function _EXT_DISCOUNTCODE_setCalendarDate_0(year, month, day) 
	{	  
		var date = document.getElementById("startDate");
		date.value = month + "/" + day + "/" + year;		
	}
			  
  	function _EXT_DISCOUNTCODE_setCalendarDate_1(year, month, day) 
	{	  
		var date = document.getElementById("endDate");
		date.value = month + "/" + day + "/" + year;						
	}
	
	<!-- ### Set the number of calendars to use ### -->
	<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
	  <liferay:param name="calendar_num" value="2" />
	</liferay:include>
	<!-- ### END Set the number of calendars to use ### -->
	
	//Layout Initialization
	function  resizeBrowser(){
	    var viewport = dijit.getViewport();
	    var viewport_height = viewport.h;
	   
		var  e =  dojo.byId("borderContainer");
		dojo.style(e, "height", viewport_height -175+ "px");
		
	    var bc = dijit.byId('borderContainer');
	    if(bc != undefined){
			try{
		    	bc.resize();
			}catch(err){
				console.log(err);
			}
	    }
    	//var  e =  dojo.byId("workFlowWrapper");
       //dojo.style(e, "height", viewport_height -280+ "px");
	}
	
	dojo.addOnLoad(resizeBrowser);
	dojo.connect(window, "onresize", this, "resizeBrowser");
	
</script>

<html:form action="/ext/discountcode/view_discountcode" method="post" styleId="discount">
<input type="hidden" name="referer" value="<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'><portlet:param name='struts_action' value='/ext/discountcode/view_discountcode' /></portlet:renderURL>" >
<html:hidden property="orderby" styleId="orderby" />
<html:hidden property="direction" styleId="direction" />
<html:hidden property="startDate" styleId="startDate" />
<html:hidden property="endDate" styleId="endDate" />

<!-- START Button Row -->
	<div class="buttonBoxLeft">
		<h3><%= LanguageUtil.get(pageContext, "Filter-Discount-Codes") %></h3>
	</div>

	<div class="buttonBoxRight">
		<button dojoType="dijit.form.Button" onClick="addDiscountCode();" iconClass="plusIcon">
           <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Add-Discount-Code" )) %>
        </button>
	</div>
<!-- END Button Row -->

<!-- START Split Box -->
<div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="false" liveSplitters="true" id="borderContainer" class="shadowBox headerBox" style="height:100px;">
<%
	DiscountCodeForm discountCodeForm = (DiscountCodeForm) request.getAttribute("DiscountCodeForm");
%>
<!-- START Left Column -->	
	<div dojoType="dijit.layout.ContentPane" splitter="false" region="leading" style="width: 350px;margin-top:38px;" class="lineRight">
		
		<div style="padding:10px;">
			<dl>
				<dt><%= LanguageUtil.get(pageContext, "Discount-Type") %>:</dt>
				<dd>
					<input dojoType="dijit.form.RadioButton" type="radio" name="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE%>" id="discountPercent" <%= (discountCodeForm.getDiscountType() == Integer.parseInt(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE)) ? "checked" : "" %> />
					<label for="discountPercent"><%= LanguageUtil.get(pageContext, "Percentage") %></label>
					
					<input dojoType="dijit.form.RadioButton" type="radio" name="discountType" value="<%=com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT%>" id="discountDiscount" <%= (discountCodeForm.getDiscountType() == Integer.parseInt(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT)) ? "checked" : "" %> />
					<label for="discountDiscount"><%= LanguageUtil.get(pageContext, "Discount") %></label>
				</dd>

				<dt><%= LanguageUtil.get(pageContext, "Coupon-ID") %>:</dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="codeId" value="<%= UtilMethods.isSet(discountCodeForm.getCodeId()) ? discountCodeForm.getCodeId() : "" %>" /></dd>

				<dt><%= LanguageUtil.get(pageContext, "Name") %>:</dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="codeDescription" value="<%= UtilMethods.isSet(discountCodeForm.getCodeDescription()) ? discountCodeForm.getCodeDescription() : "" %>" /></dd>
			</dl>

			<div class="buttonRow">
				<button dojoType="dijit.form.Button" onCLick="submitForm();" iconClass="searchIcon">
		              <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Search")) %>
		        </button>
			</div>
		</div>
	</div>
<!-- END Left Column -->
	

<!-- START Right Column -->
	<div style="overflow-y:auto;margin-top:45px;" dojoType="dijit.layout.ContentPane" splitter="true" region="center">

		<!-- START Listing Table -->
		<div id="workFlowWrapper">
		
			<table class="listingTable">
				<tr>
					<th>&nbsp</th>
					<th style="white-space:nowrap;" width="25%"><a href="javascript:submitFormOrdered('code_id');"><%= LanguageUtil.get(pageContext, "Coupon-ID") %></a></th>
					<th style="white-space:nowrap;" width="50%"><a href="javascript:submitFormOrdered('code_description');"><%= LanguageUtil.get(pageContext, "Name") %></a></th>
					<th style="white-space:nowrap;text-align:center;"><a href="javascript:submitFormOrdered('start_date');"><%= LanguageUtil.get(pageContext, "Start-Date") %></a></th>
					<th style="white-space:nowrap;text-align:center;"><a href="javascript:submitFormOrdered('end_date');"><%= LanguageUtil.get(pageContext, "End-Date") %></a></th>
					<th style="white-space:nowrap;text-align:center;""><a href="javascript:submitFormOrdered('discount_amount');"><%= LanguageUtil.get(pageContext, "Discount") %></a></th>
					<th style="white-space:nowrap;text-align:center;"><a href="javascript:submitFormOrdered('discount_type');"><%= LanguageUtil.get(pageContext, "Discount-Type") %></a></th>
					<th style="white-space:nowrap;text-align:center;"><a href="javascript:submitFormOrdered('free_shipping');"><%= LanguageUtil.get(pageContext, "Free-Shipping") %></a></th>
				</tr>
					
					<% ArrayList discounts = (ArrayList) request.getAttribute(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNTS);
					if (discounts.size() > 0)
					{
						for(int i = 0;i < discounts.size();i++)
						{
							DiscountCode discount = (DiscountCode) discounts.get(i);
							String color = (i % 2 == 0 ? "FFFFFF" : "EEEEEE");
						%>
						<tr bgcolor=<%=color%>>
							<td style="white-space:nowrap;text-align:center;">
								<a border="0" href="<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>
									<portlet:param name='struts_action' value='/ext/discountcode/edit_discountcode' />
									<portlet:param name='inode' value='<%=String.valueOf(discount.getInode())%>' />
									</portlet:actionURL>" ><span class="editIcon"></span></a>
				
								<a border="0" href="javascript:deleteDiscount('<portlet:actionURL windowState="<%=WindowState.MAXIMIZED.toString()%>">
									<portlet:param name="struts_action" value="/ext/discountcode/edit_discountcode" />
									<portlet:param name="inode" value="<%=String.valueOf(discount.getInode())%>" />
									<portlet:param name="cmd" value="<%=com.liferay.portal.util.Constants.DELETE%>" />
									</portlet:actionURL>')" ><span class="deleteIcon"></span></a>
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
							<td style="white-space:nowrap;text-align:center;"><%=UtilMethods.dateToHTMLDate(discount.getStartDate())%></td>
							<td style="white-space:nowrap;text-align:center;"><%=UtilMethods.dateToHTMLDate(discount.getEndDate())%></td>
							<td style="white-space:nowrap;text-align:center;">
							<%
							int discountType = discount.getDiscountType();
							String discountTypeString = "";
							if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE))
							{
								discountTypeString = UtilMethods.toPercentageFormat(discount.getDiscountAmount()) + " %";
							}
							else if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT))
							{
								discountTypeString = "$ " + UtilMethods.toPriceFormat(discount.getDiscountAmount());
							}				
							%>
							<%= discountTypeString %>
							</td>
							<%
							discountType = discount.getDiscountType();
							discountTypeString = "";
							if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_PERCENTAGE))
							{
								discountTypeString = "Percentage";
							}
							else if (Integer.toString(discountType).equals(com.dotmarketing.util.WebKeys.DISCOUNTCODE_DISCOUNT))
							{
								discountTypeString = "Discount";
							}				
							%>
							<td style="white-space:nowrap;text-align:center;"><%=discountTypeString.equals("Percentage")?LanguageUtil.get(pageContext, "Percentage"):LanguageUtil.get(pageContext, "Discount")%></td>
							<%
							String freeShipping = discount.getFreeShipping()?"Yes":"No";
							%>
							<td style="white-space:nowrap;text-align:center;"><%=freeShipping.equals("Yes")?LanguageUtil.get(pageContext, "Yes"):LanguageUtil.get(pageContext, "No")%></td>
						</tr>
						<%}
						}else{%>
						<tr align="center" bgcolor="FFFFFF">
							<td colspan="8"><%= LanguageUtil.get(pageContext, "There-are-not-discounts-to-display") %></td>
						</tr>
						<%}%>
				</table>
			</div>
		<!-- END Listing table -->
		
	</div>
<!-- END Right Column -->

</div>
<!-- END Split Box -->

</html:form>

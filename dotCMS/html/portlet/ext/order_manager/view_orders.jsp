<%@ page import="com.dotmarketing.util.InodeUtils" %>
<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%
    java.util.List orders = (java.util.List) request.getAttribute(com.dotmarketing.util.WebKeys.ORDER_MGR_VIEW);
	//by setting the bean on context we can use the form tag instead of the struts form tag, and use the autocomplete=off on the form
	ViewOrdersForm form = (ViewOrdersForm) request.getAttribute("ViewOrdersForm");
	pageContext.setAttribute("org.apache.struts.taglib.html.BEAN",form);
%>
<!-- SCRIPT FOR AJAX MANAGEMENT -->
<script type='text/javascript' src='/dwr/interface/OrganizationAjax.js'></script>
<script type='text/javascript' src='/dwr/engine.js'></script>
<script type='text/javascript' src='/dwr/util.js'></script>
<!-- END SCRIPT FOR AJAX MANAGEMENT -->

<script language="JavaScript">
	<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
		<liferay:param name="calendar_num" value="2" />
	</liferay:include>
	
	function addOrder() {
        form = document.getElementById("fm<portlet:namespace />");
		form.action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/order_manager/add_order" /></portlet:actionURL>";
		form.<portlet:namespace />redirect.value = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/order_manager/view_orders" /></portlet:renderURL>';
		form.submit();
	}
	
	function editOrder(inode) 
	{
		var referrer = "<portlet:renderURL>";
		referrer +=    "<portlet:param name='struts_action' value='/ext/order_manager/view_orders' />";
		referrer +=    "</portlet:renderURL>";
				
		var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    "<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";		
		action +=    "<portlet:param name='cmd' value='<%=Constants.EDIT%>' />";
		action +=    "</portlet:actionURL>";
		action +=    "&inode=" + inode;
		action +=    "&referer=" + referrer;
		document.location.href = action;
	    //form = document.getElementById("fm<portlet:namespace />");
	    //form.inode.value = inode;
		//form.action = action;
		//form.referer.value = referrer;
		//form.submit();
	}
	
	function deleteOrder(inode) 
	{
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Are-you-sure-you-want-to-delete-this-order-this-cannot-be-undone")) %>'))
		{
			/*
			var referrer = "<portlet:renderURL>";
			referrer +=     "<portlet:param name='struts_action' value='/ext/order_manager/view_orders' />";
			referrer +=     "</portlet:renderURL>";
	
			var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
			action +=    "<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";
			action +=    "<portlet:param name='<%=Constants.CMD%>' value='<%=Constants.DELETE%>' />";
			action +=    "</portlet:actionURL>";
			action += "&inode=" + inode;
			*/
			var action = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
			action +=    "<portlet:param name='struts_action' value='/ext/order_manager/view_orders' />";
			action +=    "<portlet:param name='<%=Constants.CMD%>' value='<%=Constants.DELETE%>' />";
			action +=    "</portlet:renderURL>";
			action += "&inode=" + inode;
			form = document.getElementById("fm<portlet:namespace />");
			form.action = action;
			//form.referer.value = referrer;
			form.submit();
		}
	}	
	
	function reloadSearch() {
		form = document.getElementById("fm<portlet:namespace />");
		form.action = "<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/order_manager/view_orders" /></portlet:renderURL>";
		form.submit();
	}
	function resetSearch() {
		form = document.getElementById("fm<portlet:namespace />");
		form.paymentStatusArray.selectedIndex = -1;
		form.orderStatusArray.selectedIndex = -1;
		dijit.byId('startDateCal').attr('value', '');
		dijit.byId('endDateCal').attr('value', '');
		dijit.byId('firstName').attr('value', '');
		dijit.byId('lastName').attr('value', '');
		dijit.byId('email').attr('value', '');	
		dijit.byId('orderInode').attr('value', '');
		//form.system.value = "";
		//form.facilityTitle.value = "";
		//form.facility.value = "";
		dijit.byId('invoiceNumber').attr('value', '');
		dijit.byId('orderOutsideUS').attr('checked', false);
	}
	//strStartDate
	function <portlet:namespace />setCalendarDate_0 (year, month, day) {
		var textbox = document.getElementById('strStartDate');
		textbox.value = month + '/' + day + '/' + year;
	}
	//strEndDate
	function <portlet:namespace />setCalendarDate_1 (year, month, day) {
		var textbox = document.getElementById('strEndDate');
		textbox.value = month + '/' + day + '/' + year;
	}
	function filterByFacility(facName,facInode) {
		form = document.getElementById("fm<portlet:namespace />");
		form.action = "<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/order_manager/view_orders" /></portlet:renderURL>";
		form.system.value = "";
		form.facilityTitle.value = facName;
		form.facility.value = facInode;
		form.submit();
	}
	
	function downloadOrders(){
		window.location.href='<portlet:actionURL><portlet:param name="struts_action" value="/ext/order_manager/view_orders" /><portlet:param name="cmd" value="exportExcel" /></portlet:actionURL>';
	}
	
	function downloadQB(){
		window.location.href='<portlet:actionURL><portlet:param name="struts_action" value="/ext/order_manager/view_orders" /><portlet:param name="cmd" value="exportQB" /></portlet:actionURL>';
	}
	
	///// AJAX JAVASCRIPT
	function searchFacilities() {
	 	toggleBox ("facilitiesLayer", 0);		
		var keyword = $("facilityTitle").value;
		if (keyword != "") {
			OrganizationAjax.getOrganizationsByTitle(fillTable, keyword);
			toggleBox ("facilitiesLayer", 1);
		}
	}
	function getName(facility) 
	{ 
	 	return '<a href="javascript:OrganizationAjax.getOrganizationMap(facilitieselected, ' + facility["inode"] + ')">' + facility["title"] + '</a>'; 
	}
	function facilitieselected(facility) 
	{		
	  	toggleBox ("facilitiesLayer", 0);
		var facilityInode = facility["inode"];
		var facilityTitle = facility["title"];
		$("fm<portlet:namespace />").facilityTitle.value = trimString(facilityTitle);
		$("fm<portlet:namespace />").facility.value = facilityInode;
	 } 
	
	 function fillTable(facilities)
	 {  		  				  
	    DWRUtil.removeAllRows("facilitiesTable");
	    if (facilities.length > 0)
		 {		
		     DWRUtil.addRows("facilitiesTable", facilities, [ getName ]);
		 }
		 else
		 {		
		     DWRUtil.addRows("facilitiesTable", {"No facilities found" : ""}, [ direct ]);
		 }
	 }
	 function direct(data) { 
	 	return data; 
	 }
	 function empty(data) { 
	 	return ""; 
	 }
	 function toggleBox(szDivID, iState) // 1 visible, 0 hidden
	 {
	    var obj = document.layers ? document.layers[szDivID] :
	    document.getElementById ?  document.getElementById(szDivID).style :
	    document.all[szDivID].style;
	    obj.visibility = document.layers ? (iState ? "show" : "hide") :
	    (iState ? "visible" : "hidden");
	 }
	 
	 function updateOrders()
	{
		var referrer = "<portlet:renderURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		referrer +=     "<portlet:param name='struts_action' value='/ext/order_manager/view_orders' />";
		referrer +=     "</portlet:renderURL>";
		
		var action = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>";
		action +=    "<portlet:param name='struts_action' value='/ext/order_manager/edit_order' />";
		action +=    "<portlet:param name='<%=Constants.CMD%>' value='<%=Constants.UPDATE%>' />";
		action +=    "</portlet:actionURL>";
			
		var form = document.getElementById("fm<portlet:namespace />");
		form.action = action;
		form.referer.value = referrer;
		form.submit();
	}
	
	//Layout Initialization
	function  resizeBrowser(){
		var viewport = dijit.getViewport();
		var viewport_height = viewport.h;

		var  e =  dojo.byId("borderContainer");
		dojo.style(e, "height", viewport_height -175+ "px");

		var  e =  dojo.byId("filterWrapper");
		dojo.style(e, "height", viewport_height -265+ "px");

		var  e =  dojo.byId("contentWrapper");
		dojo.style(e, "height", viewport_height -270+ "px");
	}
// need the timeout for back buttons

	dojo.addOnLoad(resizeBrowser);
	dojo.connect(window, "onresize", this, "resizeBrowser");

	function updateStartDate() {
		var date = dijit.byId('startDateCal').attr('value');
		if (date != null)
			document.getElementById("strStartDate").value = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
		else
			document.getElementById("strStartDate").value = "";
	}

	function updateEndDate() {
		var date = dijit.byId('endDateCal').attr('value');
		if (date != null)
			document.getElementById("strEndDate").value = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
		else
			document.getElementById("strEndDate").value = "";
	}
</script>

<style>
.adjustColumn dt{width:38%;}
.adjustColumn dd{margin-left:38%;}
</style>

<form id="fm<portlet:namespace />" name="ViewOrdersForm" method="post" action="/ext/order_manager/view_orders" autocomplete="off">
<input type="hidden" name="<portlet:namespace />cmd" value="">
<input type="hidden" name="<portlet:namespace />redirect" value="">
<input type="hidden" name="inode" value="">
<input type="hidden" name="referer" value="">

<!-- START Button Row -->
	<div class="buttonBoxLeft">
		<h3>Filter Orders</h3>
	</div>

	<div class="buttonBoxRight">
		<button dojoType="dijit.form.Button" name="new order" iconClass="plusIcon" onClick="window.location='<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/order_manager/view_products" /><portlet:param name="cmd" value="new" /></portlet:actionURL>'">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "New-Order")) %>
		</button>
	</div>
<!-- END Button Row -->

<!-- START Split Screen -->
<div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="false" liveSplitters="true" style="height:400px;" id="borderContainer" class="shadowBox headerBox">

<!-- START Left Column -->
	<div dojoType="dijit.layout.ContentPane" splitter="false" region="leading" style="width: 325px;" class="lineRight">

		<div id="filterWrapper" style="overflow-y:auto;overflow-x:hidden;margin:38px 0 0 0;width: 325px;">
			<dl class="adjustColumn">	
				<dt><%= LanguageUtil.get(pageContext, "First-Name") %></dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="firstName" id="firstName" style="width:150px;" value="<%= UtilMethods.isSet(form.getFirstName()) ? form.getFirstName() : "" %>" /></dd>
				
				<dt><%= LanguageUtil.get(pageContext, "Last-Name") %></dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="lastName" id="lastName" style="width:150px;" value="<%= UtilMethods.isSet(form.getLastName()) ? form.getLastName() : "" %>" /></dd>
	
				<dt><%= LanguageUtil.get(pageContext, "Email") %></dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="email" id="email" style="width:150px;" value="<%= UtilMethods.isSet(form.getEmail()) ? form.getEmail() : "" %>" /></dd>
				
				<dt><%= LanguageUtil.get(pageContext, "Invoice-Number") %></dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="invoiceNumber" id="invoiceNumber" style="width:150px;" value="<%= UtilMethods.isSet(form.getInvoiceNumber()) ? form.getInvoiceNumber() : "" %>" /></dd>
				
				<dt><%= LanguageUtil.get(pageContext, "Order-Number") %></dt>
				<dd><input type="text" dojoType="dijit.form.TextBox" name="orderInode" id="orderInode" style="width:150px;" value="<%= UtilMethods.isSet(form.getOrderInode()) ? form.getOrderInode() : "" %>" /></dd>
				
				<script type="text/javascript">
					dojo.require("dijit.form.MultiSelect");
				</script>
				<dt><%= LanguageUtil.get(pageContext, "Payment-Status") %></dt>
				<dd>
					<select dojoType="dijit.form.MultiSelect" name="paymentStatusArray" multiple="true" style="width:150px;">
<%
					Iterator<Map<String,String>> paymentStatusList = (Iterator<Map<String,String>>) request.getAttribute("paymentStatusList");
					Map<String,String> paymentStatus;
					int value;
					boolean isSelected;
					while (paymentStatusList.hasNext()) {
						paymentStatus = paymentStatusList.next();
						value = Integer.parseInt(paymentStatus.get("optionValue"));
						isSelected = false;
					
						if (form.getPaymentStatusArray() != null) {
							for (int tempValue: form.getPaymentStatusArray()) {
								if (value == tempValue) {
									isSelected = true;
									break;
								}
							}
						}
%>
						<option value="<%= value %>" <%= isSelected ? "selected" : "" %> ><%= paymentStatus.get("optionName") %></option>
<%
					}
%>
					</select>
				</dd>
				
				<dt><%= LanguageUtil.get(pageContext, "Order-Status") %></dt>
				<dd>
					<select dojoType="dijit.form.MultiSelect" name="orderStatusArray" multiple="true" style="width:150px;">
<%
					Iterator<Map<String,String>> orderStatusList = (Iterator<Map<String,String>>) request.getAttribute("orderStatusList");
					Map<String,String> orderStatus;
					while (orderStatusList.hasNext()) {
						orderStatus = orderStatusList.next();
						value = Integer.parseInt(orderStatus.get("optionValue"));
						isSelected = false;
					
						if (form.getOrderStatusArray() != null) {
							for (int tempValue: form.getOrderStatusArray()) {
								if (value == tempValue) {
									isSelected = true;
									break;
								}
							}
						}
%>
						<option value="<%= value %>" <%= isSelected ? "selected" : "" %> ><%= orderStatus.get("optionName") %></option>
<%
					}
%>
					</select>
				</dd>
				
				<dt><%= LanguageUtil.get(pageContext, "Start-Date") %></dt>
				<dd>
<%
					String strDate = "";
					if (UtilMethods.isSet(form.getStartDate())) {
						strDate = (form.getStartDate().getYear() + 1900) + "-" + (form.getStartDate().getMonth() < 9 ? "0" : "") + (form.getStartDate().getMonth() + 1) + "-" + (form.getStartDate().getDate() < 10 ? "0" : "") + form.getStartDate().getDate();
					}
%>
					<input type="text" style="width:100px;" dojoType="dijit.form.DateTextBox" validate='return false;' invalidMessage="" id="startDateCal" name="startDateCal" value="<%= strDate %>" onchange="updateStartDate();" />
					<html:hidden property="strStartDate" styleId="strStartDate" />
				</dd>
				
				<dt><%= LanguageUtil.get(pageContext, "End-Date") %></dt>
				<dd>
<%
					strDate = "";
					if (UtilMethods.isSet(form.getEndDate())) {
						strDate = (form.getEndDate().getYear() + 1900) + "-" + (form.getEndDate().getMonth() < 9 ? "0" : "") + (form.getEndDate().getMonth() + 1) + "-" + (form.getEndDate().getDate() < 10 ? "0" : "") + form.getEndDate().getDate();
					}
%>
					<input type="text" style="width:100px;" dojoType="dijit.form.DateTextBox" validate='return false;' invalidMessage="" id="endDateCal" name="endDateCal" value="<%= strDate %>" onchange="updateEndDate();" />
					<html:hidden property="strEndDate" styleId="strEndDate" />
				</dd>
	
				<dt>&nbsp;</dt>
				<dd><input type="checkbox" dojoType="dijit.form.CheckBox" name="orderOutsideUS" id="orderOutsideUS" <%= form.isOrderOutsideUS() ? "checked" : "" %> /> <%= LanguageUtil.get(pageContext, "Outside-of-the-US") %></dd>
			</dl>
		</div>
		
		<div class="buttonRow" style="padding-top:10px;border-top:1px solid #ccc;">
		    <button dojoType="dijit.form.Button" name="reset"  onClick="resetSearch()" iconClass="resetIcon">
		       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Reset-Search")) %>
		    </button>
			
		    <button dojoType="dijit.form.Button" name="search" onClick="reloadSearch()" iconClass="searchIcon">
		       <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Search-Orders")) %>
		    </button>
		</div>
		
	</div>
<!-- END Left Column -->


<!-- START Right Column -->
	<div dojoType="dijit.layout.ContentPane" splitter="true" region="center">

		<div id="contentWrapper" style="overflow-y:auto; overflow-x:hidden;margin:43px 0 0 5px;">
		
			<table class="listingTable">
				<tr>
					<th><%= LanguageUtil.get(pageContext, "Actions") %></th>
					<th><%= LanguageUtil.get(pageContext, "Order--") %></th>
					<th><%= LanguageUtil.get(pageContext, "Name") %></th>
					<th><%= LanguageUtil.get(pageContext, "Facility") %></th>
					<th><%= LanguageUtil.get(pageContext, "Status") %></th>
					<th><%= LanguageUtil.get(pageContext, "Payment") %></th>
					<th><%= LanguageUtil.get(pageContext, "Shipping") %></th>
					<th><%= LanguageUtil.get(pageContext, "Tracking-Number") %></th>
					<th><%= LanguageUtil.get(pageContext, "Status") %></th>
					<th><%= LanguageUtil.get(pageContext, "Date") %></th>
				</tr>
				<% 
					//int x = 1; 
					float totalPaid = 0;
					float totalDue = 0;
					for (int k=0;k<orders.size();k++) {
						Order order = (Order) orders.get(k);
						UserProxy registrantUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(order.getUserInode(),com.dotmarketing.business.APILocator.getUserAPI().getSystemUser(), false);
						totalPaid += order.getOrderTotalPaid();
						totalDue += order.getOrderTotalDue();
						String str_style = "";
						if (order.getOrderStatus() == 4)
							str_style = "class=\"alternate_1\"";
						else
							str_style = (k%2==0) ? "class=\"alternate_1\"" : "class=\"alternate_2\"";
				%>
					<tr>
						<td nowrap align="center">
							<a  href="javascript:editOrder('<%= String.valueOf(order.getInode()) %>');">
							<span class="editIcon"></span>
							</a>
							
							<a href="javascript:deleteOrder('<%= String.valueOf(order.getInode()) %>')">
							<span class="deleteIcon"></span>
							</a>
						</td>
						<td><%= order.getInode() %></td>
						<td>
							<a class="bg" href="javascript:editOrder('<%= String.valueOf(order.getInode()) %>');">
							<%=order.getUser().getFullName()%></a> 
						</td>
						<td>
							<a class="bg" href="javascript:editOrder('<%= String.valueOf(order.getInode()) %>');">
							<% 
								String userId = registrantUser.getUserId(); 
								Organization organization = (Organization) InodeFactory.getParentOfClass(registrantUser, Organization.class);
								String organizationTitle = "";
								if (InodeUtils.isSet(organization.getInode()))
									organizationTitle = organization.getTitle();
							%>
							<%=organizationTitle%></a> 
						</td>
						<td>
							<select dojoType="dijit.form.FilteringSelect" name="orderStatus_<%=order.getInode()%>">
								<%
						    	String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
						    	for (int i=0;i<statusesArray.length;i++) {
						    		String status = statusesArray[i];
									String optionName = com.dotmarketing.util.Config.getStringProperty(status + "_FN");
									String optionValue = com.dotmarketing.util.Config.getStringProperty(status);
									String selected = (order.getOrderStatus()==Integer.parseInt(optionValue)) ? "selected" : "";
								%>
									<option value="<%=optionValue%>" <%=selected%>><%=optionName%></option>
								<%}%>
							</select>
						</td>
						<td>
							<%
								String[] paymentTypes = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
								int creditCard = Config.getIntProperty(paymentTypes[0]); 
								int check = Config.getIntProperty(paymentTypes[1]);
								int po = Config.getIntProperty(paymentTypes[2]);
								int paymentType = Integer.parseInt(order.getPaymentType().trim());
								
								String paymentTypeStr = "";
								if (paymentType == creditCard){
									paymentTypeStr = "ECOM_CREDIT_CARD";
								}
								if (paymentType == check){
									paymentTypeStr = "ECOM_CHECK";
								}			
								if (paymentType == po){
									paymentTypeStr = "ECOM_PURCHASE_ORDER";
								}	
								String paymentTypeName = "";
								if (paymentTypeStr != "")
									paymentTypeName = com.dotmarketing.util.Config.getStringProperty(paymentTypeStr + "_FN");
							%>
							<%=paymentTypeName%>
							
						</td>
						<td>
							<%=UtilMethods.getShippingTypeName(order.getOrderShipType())%>
						</td>
						<td>
							<%if (order.getTrackingNumber() != null) { %> 
								<%= order.getTrackingNumber() %>
							<%} %>
						</td>
						<td>
							<select dojoType="dijit.form.FilteringSelect" name="paymentStatus_<%=order.getInode()%>">
								<%
						    	statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_PAY_STATUSES");
						    	for (int i=0;i<statusesArray.length;i++) {
						    		String status = statusesArray[i];
									String optionName = com.dotmarketing.util.Config.getStringProperty(status + "_FN");
									String optionValue = com.dotmarketing.util.Config.getStringProperty(status);
									String selected = (order.getPaymentStatus()==Integer.parseInt(optionValue)) ? "selected" : "";
								%>
									<option value="<%=optionValue%>" <%=selected%>><%=optionName%></option>
								<%}%>
							</select>
						</td>
						<td>
							<%= UtilMethods.dateToHTMLDate(order.getDatePosted()) %> <%= UtilMethods.dateToHTMLTime(order.getDatePosted()) %>
						</td>
					</tr>
				<%}%>
			</table>
			
				<% if (orders.size()!=0) { %>
					<div class="yui-gb callOutBox" style="text-align:left;">
						<div class="yui-u first">
							<B><%= LanguageUtil.get(pageContext, "Total-Paid") %>: </B>$<%=UtilMethods.toPriceFormat(totalPaid)%>
						</div>
							
						</td>

						<div class="yui-u" style="text-align:center;">
							<B><%= LanguageUtil.get(pageContext, "Total-Due") %>: </B>
							<% if (totalDue >= 0) { %>
								$<%= UtilMethods.toPriceFormat(totalDue) %>
							<% } else { %>
								<font color="red">($<%= UtilMethods.toPriceFormat(totalDue) %>)</font>
							<% } %>						
						</div>
						
						<div class="yui-u" style="text-align:right;">
							<img src="/icon?i=test.xls" style="vertical-align:middle"> 						
							<a href="javascript:downloadOrders()"><%= LanguageUtil.get(pageContext, "Download-to-Excel") %></a>
						</div>
					</div>
				<% }else{ %>
					<div class="noResultsMessage"><%= LanguageUtil.get(pageContext, "There-are-no-Orders-to-show") %></div>
				<% } %>
			</table>
		</div>

		<div class="buttonRow" style="padding-top:10px;border-top:1px solid #ccc;">
			<button dojoType="dijit.form.Button" name="update_orders" onClick="updateOrders()">
				<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Update-Statuses")) %>
			</button>
		</div>
	</div>
<!-- ENDRESULTS -->

</div>
<!-- END Split Screen -->



</form>

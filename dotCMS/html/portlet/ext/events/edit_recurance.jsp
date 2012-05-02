<%@ page import="java.util.*,com.dotmarketing.portlets.events.struts.*,com.dotmarketing.portlets.events.model.*,com.dotmarketing.util.*,com.dotmarketing.portlets.categories.model.*,com.dotmarketing.beans.*,com.dotmarketing.factories.*,java.util.*"  %>
<%@ page import="com.dotmarketing.portlets.events.struts.RecuranceForm" %>
<%@ include file="/html/portlet/ext/events/init.jsp" %>

<%
	Recurance r = (Recurance) request.getAttribute("recurance");
	RecuranceForm rf = (RecuranceForm) request.getAttribute("recuranceForm");
	Event e = (Event) request.getAttribute("event");

	java.util.GregorianCalendar cal = new java.util.GregorianCalendar(102,1,1,0,0,0);
	boolean selected = false;

	//Getting dates to fill the form
	java.util.Date startDate = null;
	java.util.Date endDate = null;
	if (rf != null) {
		startDate = rf.getStarting();
		endDate = rf.getEnding();
	} else {
		startDate = r.getStarting();
		endDate = r.getEnding();
	}
	
	//Getting date arrays to fill the form
	int[] monthIds = CalendarUtil.getMonthIds();
	String[] months = CalendarUtil.getMonths(locale);
	String[] days = CalendarUtil.getDays(locale);
	
	Calendar startDateCal = new java.util.GregorianCalendar();
	startDateCal.setTime(startDate);

	Calendar endDateCal = new java.util.GregorianCalendar();
	endDateCal.setTime(endDate);

	boolean conflictFound = (request.getAttribute("conflict_found") != null);

%>

	
<script>

	function doShow(){
		var x = "day";
		if(document.getElementById('weekRadio').checked){
			x = "week";
		}
		else if(document.getElementById('monthRadio').checked){
			x = "month";
		}


		document.getElementById('day').style.display='none';
		document.getElementById('month').style.display='none';
		document.getElementById('week').style.display='none';
		document.getElementById('dayInterval').disabled=true;
		document.getElementById('monthInterval').disabled=true;
		document.getElementById('weekInterval').disabled=true;

		document.getElementById(x + 'Interval').disabled=false;
		var ele = document.getElementById(x);
		ele.style.display='';
	}


    function popDate(fieldId){
    //alert(fieldId);
    //alert(document.getElementById(fieldId));
        var x = document.getElementById(fieldId).value.split("/");
        calwin = window.open('/pages/admin/calendar.jsp?month=' + (x[0] -1) + '&year=' + x[2] + '&fieldName=' + fieldId, 'calwin', 'width=300,height=220');
    }
    
    function doCancel(){
    	window.location="<%=request.getParameter("redirect")%>";
    
    }
    
    
	function amPm_0(){
		var ele = document.getElementById("amPm_0");
		var val = document.getElementById("eventForm").calendar_0_hour[document.getElementById("eventForm").calendar_0_hour.selectedIndex].value ;
	
		if(val > 11){
			ele.innerHTML = "<font class=\"bg\" size=\"2\">PM</font>";
		}
		else{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">AM</font>";
		}
	}

	function amPm_1(){
		var ele = document.getElementById("amPm_1");
		var val = document.getElementById("eventForm").calendar_1_hour[document.getElementById("eventForm").calendar_1_hour.selectedIndex].value ;
	
		if(val > 11){
			ele.innerHTML = "<font class=\"bg\" size=\"2\">PM</font>";
		}
		else{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">AM</font>";
		}
	}

	function tbdChanged () {
        var form = document.getElementById("recuranceForm");
		if (document.getElementById("tbdCheckbox").checked) {
			form.calendar_0_hour.disabled = true;
			form.calendar_0_minute.disabled = true;
			form.calendar_1_hour.disabled = true;
			form.calendar_1_minute.disabled = true;
			form.calendar_0_hour.selectedIndex = 0;
			form.calendar_0_minute.selectedIndex = 0;
			form.calendar_1_hour.selectedIndex = 0;
			form.calendar_1_minute.selectedIndex = 0;
		} else {
			form.calendar_0_hour.disabled = false;
			form.calendar_0_minute.disabled = false;
			form.calendar_1_hour.disabled = false;
			form.calendar_1_minute.disabled = false;
		}
	}

    function doSubmit(){
		var form = document.getElementById('recuranceForm');
		form.action = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_recurance" /><portlet:param name="cmd" value="save" /><portlet:param name="inode" value="<%=r.getInode()%>" /><portlet:param name="parent" value="<%=e.getInode()%>" /></portlet:actionURL>';

        /* create javascript dates from the form */
        
		var sdMonth = parseFloat(form.calendar_0_month[form.calendar_0_month.selectedIndex].value) + 1;
		var sdDay = form.calendar_0_day[form.calendar_0_day.selectedIndex].value;
		var sdYear = form.calendar_0_year[form.calendar_0_year.selectedIndex].value;
		var sdHour = form.calendar_0_hour[form.calendar_0_hour.selectedIndex].value ;
		var sdMinute = form.calendar_0_minute[form.calendar_0_minute.selectedIndex].value;

		var edMonth = parseFloat(form.calendar_1_month[form.calendar_1_month.selectedIndex].value) + 1;
		var edDay = form.calendar_1_day[form.calendar_1_day.selectedIndex].value;
		var edYear = form.calendar_1_year[form.calendar_1_year.selectedIndex].value;
		var edHour = form.calendar_1_hour[form.calendar_1_hour.selectedIndex].value ;
		var edMinute = form.calendar_1_minute[form.calendar_1_minute.selectedIndex].value;
         
        /* check that end date > start date */
        sdate = new Date(sdYear, (sdMonth - 1), sdDay, sdHour, sdMinute);
        edate = new Date(edYear, (edMonth-1), edDay, edHour, edMinute);
        if(edate.valueOf() < sdate.valueOf()){
                alert("The end date is less than the start date");
                return false;
        }

        /* build our hidden startDate and endDate fields */
		form.startDateString.value = sdMonth + "/" + sdDay + "/" + sdYear + " " + sdHour + ":" + sdMinute;
		form.endDateString.value = edMonth + "/" + edDay + "/" + edYear + " " + edHour + ":" + edMinute;
		
		//submitting the form
		form.submit();
    }

    
	<liferay:include page="/html/js/calendar/calendar_js.jsp" flush="true">
		<liferay:param name="calendar_num" value="2" />
	</liferay:include>    
    
    
</script>
	
	
	
	
	
	
	
</head>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"edit-recurance\") %>" />
<table class="adminTable" cellpadding="3" cellspacing="0" border="0" align="center" style="width: 450px">
	<html:form action='/ext/events/edit_recurance' styleId="recuranceForm">
	<html:hidden property="inode" value="<%=r.getInode()%>" />
	<html:hidden property="parent" value="<%=e.getInode()%>" />
	<html:hidden property="startDateString" styleId="startDateString"/>
	<html:hidden property="endDateString" styleId="endDateString"/>
	<input type="hidden" name="continueWithConflicts" id="continueWithConflicts" value="false">
	<input type="hidden" id="redirect" name="<portlet:namespace />redirect" value="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/view_events" /><portlet:param name="category" value="none" /></portlet:renderURL>">
	<input type="hidden" name="dispatch" value="save" >
	<tr>
		<td colspan="2" height="5"></td>
	</tr>
	<tr>
		<td colspan=3 align="center" class="beta"><font class="beta" size="2"><STRONG>Recurring Event: <%=e.getTitle()%></STRONG></font></td>
	</tr>
	<tr>
		<td colspan="2" height="5"></td>
	</tr>
	<tr>
		<td colspan=3>
			<table width=100%>
				<TR style="display:none;">
					<TD nowrap>
						Starts at:
					</TD>
					<TD nowrap>
						<select name="calendar_0_hour" id="calendar_0_hour" onChange="amPm_0();">
						<%
							int sdHour = startDateCal.get(Calendar.HOUR_OF_DAY);
							for (int i = 0; i < 24; i++) {
							
								int val = i > 12 ?  i - 12: i;
								if(val ==0) val = 12;
						%>
								<option <%= (sdHour == i) ? "selected" : "" %> value="<%= i %>"><%= val %></option>
						<%
							}
									%>
						</select> : 
						<select name="calendar_0_minute" id="calendar_0_minute">
						<%
							int currentMin  = startDateCal.get(Calendar.MINUTE);
							selected = false;
							for (int i = 0; i < 60; i= (i+5)) {
								String val = (i< 10) ? "0" + i: String.valueOf(i);
						%>
								<option <%= (i >= currentMin && ! selected) ? "selected" : "" %> value="<%= val %>"><%= val %></option>
						<%
								if(i >= currentMin) selected = true;
							}
						%>
						</select>
						<span id="amPm_0"><font class="bg" size="2"><%=(sdHour > 11) ? "PM" : "AM"%></font></span>
					</TD>
					<TD nowrap>
						Ends at:
					</TD>
					<td>
						<select name="calendar_1_hour" id="calendar_1_hour" onChange="amPm_1();">
						<%
							sdHour = endDateCal.get(Calendar.HOUR_OF_DAY);
							for (int i = 0; i < 24; i++) {
							
								int val = i > 12 ?  i - 12: i;
								if(val ==0) val = 12;
						%>
								<option <%= (sdHour == i) ? "selected" : "" %> value="<%= i %>"><%= val %></option>
						<%
							}
									%>
						</select> : 
						<select name="calendar_1_minute" id="calendar_1_minute">
						<%
							currentMin  = endDateCal.get(Calendar.MINUTE);
							selected = false;
							for (int i = 0; i < 60; i= (i+5)) {
								String val = (i< 10) ? "0" + i: String.valueOf(i);
						%>
								<option <%= (i >= currentMin && ! selected) ? "selected" : "" %> value="<%= val %>"><%= val %></option>
						<%
								if(i >= currentMin) selected = true;
							}
						%>
						</select>
						<span id="amPm_1"><font class="bg" size="2"><%=(sdHour > 11) ? "PM" : "AM"%></font></span>
						<input type="checkbox" onclick="tbdChanged()" id="tbdCheckbox" <%=e.isTimeTBD()?"checked":""%>>Time TBD</input>
					</TD>
				</TR>
			</table>
		</td>
	</tr>
	<tr>
		<td valign="top" nowrap>
			<table>
				<tr>
					<td><html:radio property="occurs" value="day" styleId="dayRadio" onclick="doShow();"/></td>
					<td><label for="dayRadio">daily</label></td>
				</tr>
				<tr>
					<td><html:radio property="occurs" value="week" styleId="weekRadio" onclick="doShow();" /></td>
					<td><label for="weekRadio">weekly</label></td>
				</tr>
				<tr>
					<td><html:radio property="occurs" value="month" styleId="monthRadio" onclick="doShow();" /></td>
					<td><label for="monthRadio">monthly</label></td>
				</tr>
			</table>
			<span class="shimIcon" style="width:75px;"></span>
		</td>
		<td>
			<span class="shimIcon" style="height:75px;"></span>
		</td>
		<td valign="top" width="100%">
			<div id="day" style="display: none;">
				<table>
					<tr>
						<td>Every: <html:text property="interval" styleClass="text" style="width:30px" styleId="dayInterval" /> day(s)</td>
					</tr>
				</table>
			</div>
			<div id="week" style="display:none;">
				<table>
					<tr>
						<td colspan=4>Recur every: <html:text property="interval" styleClass="text" style="width:30px"  styleId="weekInterval" /> weeks(s) on:</td>
					</tr>
					<tr>
						<td><html:checkbox property="mon" value="true" styleId="Monday" /> <label for="Monday">Monday</label></td>
						<td><html:checkbox property="tue" value="true" styleId="Tuesday" /> <label for="Tuesday">Tuesday</label></td>
						<td><html:checkbox property="wed" value="true" styleId="Wenesday" /> <label for="Wenesday">Wednesday</label></td>
						<td><html:checkbox property="thu" value="true" styleId="Thursday" /> <label for="Thursday">Thursday</label></td>
					</tr>
					<tr>
						<td><html:checkbox property="fri" value="true" styleId="Friday" /> <label for="Friday">Friday</label></td>
						<td><html:checkbox property="sat" value="true" styleId="Saturday" /> <label for="Saturday">Saturday</label></td>
						<td colspan=2><html:checkbox property="sun" value="true" styleId="Sunday" /> <label for="Sunday">Sunday</label></td>
					</tr>
				</table>

			</div>
			<div id="month" style="display: none;">
				<table>
					<tr>
						<td>Day:&nbsp;</td>
						<td><html:text property="dayOfMonth" styleClass="text" style="width:30px" /> of the month</td>
					</tr>
					<tr>
						<td>Every:&nbsp;</td>
						<td><html:text property="interval" styleClass="text" style="width:30px" styleId="monthInterval" /> month(s)</td>
					</tr>

				</table>
			</div>
			<span class="shimIcon" style="width:100px;"></span>
		</td>
	</tr>

	<tr>
		<td colspan=3>
		
				<table>
					<tr>
						<td>Start Recurring: </td>
						<td>
							<select name="calendar_0_month" disabled="disabled">
							<%
								String sdMonth = Integer.toString(startDateCal.get(Calendar.MONTH));
								for (int i = 0; i < months.length; i++) {
							%>
								<option <%= (sdMonth.equals(Integer.toString(monthIds[i]))) ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
							<%
								}
							%>
							</select>
							<select name="calendar_0_day" disabled="disabled">
							<%
								String sdDay = Integer.toString(startDateCal.get(Calendar.DATE));
								for (int i = 1; i <= 31; i++) {
							%>
								<option <%= (sdDay.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
							<%
								}
							%>
							</select>
							<select name="calendar_0_year" disabled="disabled">
							<%
								int currentYear = startDateCal.get(Calendar.YEAR);
								String sdYear = Integer.toString(startDateCal.get(Calendar.YEAR));
								for (int i = currentYear; i <= currentYear + 10; i++) {
							%>
								<option <%= (sdYear.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
							<%
								}
							%>
							</select>
						</td>
					</tr>
					<tr>
						<td>End By</td>
						<td>
							<select name="calendar_1_month">
							<%
								String edMonth = Integer.toString(endDateCal.get(Calendar.MONTH));
								for (int i = 0; i < months.length; i++) {
							%>
								<option <%= (edMonth.equals(Integer.toString(monthIds[i]))) ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
							<%
								}
							%>
							</select>
							<select name="calendar_1_day">
							<%
								String edDay = Integer.toString(endDateCal.get(Calendar.DATE));
								for (int i = 1; i <= 31; i++) {
							%>
								<option <%= (edDay.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
							<%
								}
							%>
							</select>
							<select name="calendar_1_year">
							<%
								currentYear = endDateCal.get(Calendar.YEAR);
								String edYear = Integer.toString(endDateCal.get(Calendar.YEAR));
								for (int i = currentYear; i <= currentYear + 10; i++) {
							%>
								<option <%= (edYear.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
							<%
								}
							%>
							</select>
							<img style="cursor:hand;" align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_1_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_1('<portlet:namespace />calObj_1');">
						</td>
					</tr>

				</table>
		

		</td>
	</tr>
	<tr>
		<td colspan="2" height="15"></td>
	</tr>
	<tr>
		<td align="center" colspan="3">
        <button dojoType="dijit.form.Button" onClick="doSubmit(); return false">Save</button>
        &nbsp;
        <button dojoType="dijit.form.Button" onClick="doCancel()">Cancel</button>
		</td>
	</tr>
	<tr>
		<td colspan="2" height="15"></td>
	</tr>
	
	</html:form>
	
</table>

</liferay:box>
<script language="javascript">
	var myForm = document.getElementById('recuranceForm');
	function setDate(id, month, day, year) {
		if (id == "calendar_0") {
			myForm.calendar_0_month.selectedIndex = getIndex(myForm.calendar_0_month, month);
			myForm.calendar_0_day.selectedIndex = getIndex(myForm.calendar_0_day, day);
			myForm.calendar_0_year.selectedIndex = getIndex(myForm.calendar_0_year, year);
		}
		if (id == "calendar_1") {
			myForm.calendar_1_month.selectedIndex = getIndex(myForm.calendar_1_month, month);
			myForm.calendar_1_day.selectedIndex = getIndex(myForm.calendar_1_day, day);
			myForm.calendar_1_year.selectedIndex = getIndex(myForm.calendar_1_year, year);
		}
	}
	doShow();

	tbdChanged ();

	<% if(conflictFound) { %>
	var agree=confirm("This recurance has conflicts with other(s) approved event(s), are you sure do you want to continue?");
	if (agree) {
		document.getElementById('continueWithConflicts').value = 'true';
		doSubmit();
	}
	<% } %> 
</script>
""  
"" 

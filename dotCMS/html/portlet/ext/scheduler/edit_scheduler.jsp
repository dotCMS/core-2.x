<%@ include file="/html/portlet/ext/scheduler/init.jsp" %>

<%@ page import="com.dotmarketing.portlets.scheduler.struts.SchedulerForm" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.dotmarketing.beans.Host" %>
<%@ page import="javax.servlet.jsp.PageContext" %>
<%
	SchedulerForm schedulerForm = null;

	if (request.getAttribute("SchedulerForm") != null) {
		schedulerForm = (SchedulerForm) request.getAttribute("SchedulerForm");
	}
	
	java.util.Hashtable params = new java.util.Hashtable();
	params.put("struts_action", new String [] {"/ext/scheduler/view_schedulers"} );
	
	String referrer = com.dotmarketing.util.PortletURLUtil.getRenderURL(request, javax.portlet.WindowState.MAXIMIZED.toString(), params);
%>

<%@page import="com.dotmarketing.util.UtilMethods"%>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value='<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Edit-Job")) %>' />

<script language="Javascript">

function checkDate(element, fieldName) {
	if (element.checked) {
	  	eval("document.getElementById('" + fieldName + "Div').style.visibility = ''");
	} else {
		eval("document.getElementById('" + fieldName + "Div').style.visibility = 'hidden'");
	}
}

function <portlet:namespace />setCalendarDate_0(year, month, day) {	  
	document.forms[0].startDateYear.value = year;
	document.forms[0].startDateMonth.value = --month;
	document.forms[0].startDateDay.value = day;
	updateDate('startDate');
}

function <portlet:namespace />setCalendarDate_1(year, month, day) {	  
	document.forms[0].endDateYear.value = year;
	document.forms[0].endDateMonth.value = --month;
	document.forms[0].endDateDay.value = day;
	updateDate('endDate');
}

function <portlet:namespace />setCalendarDate_2(year, month, day) {	  
	document.forms[0].everyDateYear.value = year;
	document.forms[0].everyDateMonth.value = --month;
	document.forms[0].everyDateDay.value = day;
	updateDateOnly('everyDate');
}

function setCalendars(fieldName) {
	eval("var month = document.forms[0]." + fieldName + "Month.value");
	eval("var day =   document.forms[0]." + fieldName + "Day.value");
	eval("var year =  document.forms[0]." + fieldName + "Year.value");
	var date= month + "/" + day + "/" + year;
	eval("document.forms[0]." + fieldName + ".value = date");
}

	  function updateDate(field)
	  {
	  	eval("var year  = document.forms[0]." + field + "Year.value");
	  	eval("var month = document.forms[0]." + field + "Month.value");
	  	month = parseInt(month) + 1;
	  	eval("var day = document.forms[0]." + field + "Day.value");
	  	eval("var hour = document.forms[0]." + field + "Hour.value");
	  	eval("var minute = document.forms[0]." + field + "Minute.value");
	  	eval("var second = document.forms[0]." + field + "Second.value");
	  	
	  	var date = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
	  	eval("document.forms[0]." + field + ".value = date");
	  }
	  
	  function updateDateOnly(field)
	  {
	  	eval("var year  = document.forms[0]." + field + "Year.value");
	  	eval("var month = document.forms[0]." + field + "Month.value");
	  	month = parseInt(month) + 1;
	  	eval("var day = document.forms[0]." + field + "Day.value");
	  	
	  	var date = year + "-" + month + "-" + day;
	  	eval("document.forms[0]." + field + ".value = date");
	  }
	  
	  function updateFieldFromDate(field)
	  {		
	  	eval("var date = document.forms[0]." + field + ".value");
	  	var dateAux = date.split(" ")[0];
	  	var timeAux = date.split(" ")[1];
		
		if(dateAux != null)
		{
		  	var dateArray = dateAux.split("-");
		  	if(dateArray.length >= 3)
		  	{	  
			 	var year = dateArray[0];
			 	var month = dateArray[1];
			 	month = parseInt(trimZero(month)) - 1;
		  		var day = dateArray[2];
		  		day = parseInt(trimZero(day));
	  		  
			  	eval("document.forms[0]." + field + "Day.value = day");
			 	eval("document.forms[0]." + field + "Month.value = month");
				eval("document.forms[0]." + field + "Year.value = year");	
	  		}
	  	}
	  	
	  	if(timeAux != null)
	  	{
		  	var timeArray = timeAux.split(":");
		  	if (timeArray.length >= 2)
		  	{
		  		var hour = timeArray[0];
		  		hour = parseInt(trimZero(hour));
			  	var minute = timeArray[1];
			  	var second = timeArray[2];
	  		  
			  	eval("document.forms[0]." + field + "Hour.value = hour");
			  	eval("document.forms[0]." + field + "Minute.value = minute");
			  	eval("document.forms[0]." + field + "Second.value = second");
		  	}	  	
	  	}
	  	amPm(field);
	  }
	  
	  function amPm(fieldName)
	  {
		var ele = document.getElementById(fieldName + "PM");
		eval("var val = document.forms[0]." + fieldName + "Hour.value");

		if(val > 11)
		{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">PM</font>";
		}
		else
		{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">AM</font>";
		}
	}

<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
	<liferay:param name="calendar_num" value="<%= Integer.toString(3) %>" />
</liferay:include>

function submitfm(form) {
	if (validate()) {
		form.<portlet:namespace />cmd.value = '<%=Constants.ADD%>';
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/scheduler/edit_scheduler" /></portlet:actionURL>';
		form.<portlet:namespace />redirect.value = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/scheduler/edit_scheduler" /></portlet:renderURL>';
		form.referrer.value = '<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/scheduler/view_schedulers" /><portlet:param name="group" value="user_jobs" /></portlet:renderURL>';
		submitForm(form);
	}
	
}

function cancelEdit() {
	self.location = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/scheduler/view_schedulers" /></portlet:renderURL>';
}


function deleteSchedule(form) {
	if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.confirm.delete")) %>')){
		form.<portlet:namespace />cmd.value = '<%=Constants.DELETE%>';
		form.<portlet:namespace />redirect.value = '<%= referrer %>';
		form.referrer.value = '<%= referrer %>';
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/scheduler/edit_scheduler" /></portlet:actionURL>';
		submitForm(form);
	}
}

	function validate() {
	
		if (document.getElementById("atInfo").checked) {
			if (!document.forms[0].at[0].checked &&
				!document.forms[0].at[1].checked) {
				alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.AT.option.select")) %>');
				return false;
			}
		}
		
		if (document.getElementById("everyInfo").checked) {
			if (!document.forms[0].every[0].checked &&
				!document.forms[0].every[1].checked) {
				alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.EVERY.option.select")) %>');
				return false;
			} else if (document.forms[0].every[1].checked) {
				var selected = false;
				for (var i = 0; i < document.forms[0].everyDay.length; ++i) {
					if (document.forms[0].everyDay[i].checked) {
						selected = true;
						break;
					}
				}
				
				if (!selected) {
					alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.weekday.select")) %>');
					return false;
				}
			}
		}
		
		var atInfo = document.getElementsByName("atInfo")[0];
		if (atInfo.checked) {
			var at = document.getElementsByName("at");
			if (at[1].checked) {
				var betweenFromHourObj = document.getElementsByName("betweenFromHour")[0];
				var betweenFromHour = betweenFromHourObj[betweenFromHourObj.selectedIndex].value;
				var betweenToHourObj = document.getElementsByName("betweenToHour")[0];
				var betweenToHour = betweenToHourObj[betweenToHourObj.selectedIndex].value;

				if (parseInt(betweenToHour) < parseInt(betweenFromHour) ) {
					alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.from.lesser.than.to")) %>');
					return false;
				}
				
				if (document.getElementById("eachInfo").checked) {
					var hours = parseInt(document.getElementById("eachHours").value);
					var minutes = parseInt(document.getElementById("eachMinutes").value);
					
					if ((isNaN(hours) &&
						 isNaN(minutes)) ||
						((hours == 0) &&
						 (minutes == 0)) ||
						(isNaN(hours) &&
						 (minutes == 0)) ||
						((hours == 0) &&
						 isNaN(minutes))) {
						alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.Scheduler.specify.hours.minutes")) %>');
						return false;
					}
				}
			} else {
				document.getElementById("eachHours").value = "";
				document.getElementById("eachMinutes").value = "";
			}
		}
		
		return true;
	}
</script>

<html:form action="/ext/scheduler/edit_scheduler" styleId="fm">
	<div id="mainTabContainer" dolayout="false" dojoType="dijit.layout.TabContainer">
		<div id="main" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Main") %>">
			<input name="<portlet:namespace /><%= Constants.CMD %>" type="hidden" value="">
			<input name="<portlet:namespace />redirect" type="hidden" value="">
			<input name="referrer" type="hidden" value="">
			<%--input type="hidden" name="jobGroup" id="jobGroup" value="<%= (schedulerForm != null) && UtilMethods.isSet(schedulerForm.getJobGroup()) ? schedulerForm.getJobGroup() : "" %>"--%>
			<input type="hidden" name="jobGroup" id="jobGroup" value="User Job">
			<%--input type="hidden" name="schedulerEditable" id="schedulerEditable" value="true"--%>
			<dl>
					<dt>
						<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Job-Name") %>:</b></font>
					</dt>				
					<dd>
<%
	if (((schedulerForm.getJobGroup() == null) ||
		 (schedulerForm.getJobGroup().equals("User Job"))) &&
		(!schedulerForm.isEditMode())) {
%>
						<input class="form-text" dojoType="dijit.form.TextBox" name="jobName" id="jobName" value="<%= UtilMethods.isSet(schedulerForm.getJobName()) ? schedulerForm.getJobName() : "" %>" style="width: 300px;" type="text" >
<%
	} else {
%>
						<%= schedulerForm.getJobGroup().equals("Recurrent Campaign") ? schedulerForm.getJobDescription() : schedulerForm.getJobName() %>
						<input class="form-text" dojoType="dijit.form.TextBox" name="jobName" id="jobName" value="<%= UtilMethods.isSet(schedulerForm.getJobName()) ? schedulerForm.getJobName() : "" %>" type="hidden" >
						<input class="form-text" dojoType="dijit.form.TextBox" name="editMode" id="editMode" value="<%= schedulerForm.isEditMode()? "true" : "false" %>" type="hidden" >
<%
	}
%>
					</dd>
					<dt>
						<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Job-Description") %>:</b></font>
					</dt>				
					<dd>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
						<input class="form-text" dojoType="dijit.form.TextBox" name="jobDescription" id="jobDescription" value="<%= UtilMethods.isSet(schedulerForm.getJobDescription()) ? schedulerForm.getJobDescription() : "" %>"  style="width: 300px;" type="text" >
<%
	} else {
%>
						<%= schedulerForm.getJobGroup().equals("Recurrent Campaign") ? schedulerForm.getJobName() : schedulerForm.getJobDescription() %>
<%
	}
%>
					</dd>
					<dt>
						<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Execute") %>:</b></font>
					</dt>				
					<dd>
						<table cellpadding="0" cellspacing="0">
<%
	SimpleDateFormat sdf = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DOTSCHEDULER_DATE);

	int[] monthIds = CalendarUtil.getMonthIds();
	String[] months = CalendarUtil.getMonths(locale);

	int currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR);
	int previous = 100;

	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
							<tr>
							    <td>
							    	<%= LanguageUtil.get(pageContext, "From1") %>
							    </td>
								<td>
<%
	Calendar startDateCalendar = null;
	Date startDate;
	try {
		startDate = sdf.parse(schedulerForm.getStartDate());
	} catch (Exception e) {
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DOTSCHEDULER_DATE2);
			startDate = sdf2.parse(schedulerForm.getStartDate());
		} catch (Exception ex) {
			startDate = new Date();
		}
	}

	if (schedulerForm.isHaveStartDate() ||
		!UtilMethods.isSet(schedulerForm.getJobGroup()) ||
		(UtilMethods.isSet(schedulerForm.getJobGroup()) &&
		 !schedulerForm.isHaveStartDate())) {
		startDateCalendar = GregorianCalendar.getInstance();
		startDateCalendar.setTime(startDate);
	}
%>
									<input type="checkbox" dojoType="dijit.form.CheckBox" checked id="haveStartDate" name="haveStartDate" onclick="checkDate(this, 'startDate')"/>
								</td>
								<td>
									<div id="startDateDiv">
									<table>
										<tr>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 120px;" name="startDateMonth" onChange="updateDate('startDate');">
<%
	int startDateMonth = -1;

	if (startDateCalendar != null)
		startDateMonth = startDateCalendar.get(Calendar.MONTH);

	for (int i = 0; i < months.length; i++) {
%>
													<option <%= startDateMonth == monthIds[i] ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" style="width: 80px;" name="startDateDay" onChange="updateDate('startDate');">
<%
	int startDateDay = -1;

	if (startDateCalendar != null)
		startDateDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);

	for (int i = 1; i <= 31; i++) {
%>
													<option <%= startDateDay == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" style="width: 80px;" name="startDateYear" onChange="updateDate('startDate');">
<%
	int startDateYear = -1;

	if (startDateCalendar != null)
		startDateYear = startDateCalendar.get(Calendar.YEAR);

	for (int i = currentYear - previous; i <= currentYear + 10; i++) {
%>
													<option <%= startDateYear == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span class="calMonthIcon" id="<portlet:namespace />calendar_input_0_button" onClick="<portlet:namespace />calendarOnClick_0();"></span>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="startDateHour" onChange="amPm('startDate');updateDate('startDate');">
<%
	int startDateHour = -1;

	if (startDateCalendar != null)
		startDateHour = startDateCalendar.get(Calendar.HOUR_OF_DAY);

	for (int i = 0; i < 24; i++) 
	{
		int val = i > 12 ?  i - 12: i;
		if (val == 0)
			val = 12;
%>
													<option <%= startDateHour == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="startDateMinute" onChange="updateDate('startDate');">
<%
	int startDateMinute = -1;

	if (startDateCalendar != null)
		startDateMinute = startDateCalendar.get(Calendar.MINUTE);

	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= startDateMinute == i ? "selected" : "" %> value="<%= val %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="startDateSecond" onChange="updateDate('startDate');">
<%
	int startDateSecond = -1;

	if (startDateCalendar != null)
		startDateSecond = startDateCalendar.get(Calendar.SECOND);

	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= startDateSecond == i ? "selected" : "" %> value="<%= val %>"><%= val %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span id="startDatePM"><font class="bg" size="2">AM</font></span>
											</td>
											<td>&nbsp;
												
											</td>
										</tr>
									</table>
									</div>
								</td>
							</tr>
							<input type="hidden" name="startDate" value="" id="startDate">
							<script language="javascript">
							dojo.addOnLoad (function(){
<%
	if (!UtilMethods.isSet(schedulerForm.getJobGroup())) {
%>
								document.getElementById('haveStartDate').checked = true;
<%
	}
%>
								checkDate(document.forms[0].haveStartDate, 'startDate');
								amPm('startDate');
								updateDate('startDate');
							});
							</script>
<%
	} else {
%>
							<tr>
							    <td>
							    	<%= LanguageUtil.get(pageContext, "From1") %>
							    </td>
								<td>
<%
	if (schedulerForm.isHaveStartDate()) {
		Calendar startDateCalendar = null;
		Date startDate;
		try {
			startDate = sdf.parse(schedulerForm.getStartDate());
		} catch (Exception e) {
			try {
				SimpleDateFormat sdf2 = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DOTSCHEDULER_DATE2);
				startDate = sdf2.parse(schedulerForm.getStartDate());
			} catch (Exception ex) {
				startDate = new Date();
			}
		}
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM/dd/yyyy hh:mm:ss a");
%>
									&nbsp;&nbsp;&nbsp;<%= sdf2.format(startDate) %>
<%
	} else {
%>
									&nbsp;&nbsp;&nbsp;<%= LanguageUtil.get(pageContext, "Not-Specified") %>
<%
	}
%>
								</td>
							</tr>
<%
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
							<tr>
								<td>
									<%= LanguageUtil.get(pageContext, "To1") %>
								</td>
								<td>
<%
	Calendar endDateCalendar = null;
	Date endDate;
	try {
		endDate = sdf.parse(schedulerForm.getEndDate());
	} catch (Exception e) {
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DOTSCHEDULER_DATE2);
			endDate = sdf2.parse(schedulerForm.getEndDate());
		} catch (Exception ex) {
			endDate = new Date();
		}
	}

	if (schedulerForm.isHaveEndDate() ||
		!UtilMethods.isSet(schedulerForm.getJobGroup()) ||
		(UtilMethods.isSet(schedulerForm.getJobGroup()) &&
		 !schedulerForm.isHaveEndDate())) {
		endDateCalendar = GregorianCalendar.getInstance();
		endDateCalendar.setTime(endDate);
	}
%>
									<input type="checkbox" dojoType="dijit.form.CheckBox" checked id="haveEndDate" name="haveEndDate" onclick="checkDate(this, 'endDate')"/>
								</td>
								<td>
									<div id="endDateDiv">
									<table>
										<tr>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 120px;" name="endDateMonth" onChange="updateDate('endDate');">
<%
	int endDateMonth = -1;

	if (endDateCalendar != null)
		endDateMonth = endDateCalendar.get(Calendar.MONTH);

	for (int i = 0; i < months.length; i++) {
%>
													<option <%= endDateMonth == monthIds[i] ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="endDateDay" onChange="updateDate('endDate');">
<%
	int endDateDay = -1;

	if (endDateCalendar != null)
		endDateDay = endDateCalendar.get(Calendar.DAY_OF_MONTH);

	for (int i = 1; i <= 31; i++) {
%>
													<option <%= endDateDay == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="endDateYear" onChange="updateDate('endDate');">
<%
	int endDateYear = -1;

	if (endDateCalendar != null)
		endDateYear = endDateCalendar.get(Calendar.YEAR);

	for (int i = currentYear - previous; i <= currentYear + 10; i++) {
%>
													<option <%= endDateYear == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span class="calMonthIcon" id="<portlet:namespace />calendar_input_1_button" onClick="<portlet:namespace />calendarOnClick_1();"></span>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="endDateHour" onChange="amPm('endDate');updateDate('endDate');">
<%
	int endDateHour = -1;

	if (endDateCalendar != null)
		endDateHour = endDateCalendar.get(Calendar.HOUR_OF_DAY);

	for (int i = 0; i < 24; i++) 
	{
		int val = i > 12 ?  i - 12: i;
		if (val == 0)
			val = 12;
%>
													<option <%= endDateHour == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="endDateMinute" onChange="updateDate('endDate');">
<%
	int endDateMinute = -1;

	if (endDateCalendar != null)
		endDateMinute = endDateCalendar.get(Calendar.MINUTE);

	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= endDateMinute == i ? "selected" : "" %> value="<%= val %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="endDateSecond" onChange="updateDate('endDate');">
<%
	int endDateSecond = -1;

	if (endDateCalendar != null)
		endDateSecond = endDateCalendar.get(Calendar.SECOND);

	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= endDateSecond == i ? "selected" : "" %> value="<%= val %>"><%= val %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span id="endDatePM"><font class="bg" size="2">AM</font></span>
											</td>
											<td>&nbsp;
												
											</td>
										</tr>
									</table>
									</div>
								</td>
							</tr>
							<input type="hidden" name="endDate" value="" id="endDate">
							<script language="javascript">
							dojo.addOnLoad (function(){
<%
	if (!UtilMethods.isSet(schedulerForm.getJobGroup())) {
%>
								document.getElementById('haveEndDate').checked = true;
<%
	}
%>
								checkDate(document.forms[0].haveEndDate, 'endDate');
								amPm('endDate');
								updateDate('endDate');
							});
							</script>
<%
	} else {
%>
							<tr>
								<td>
									<%= LanguageUtil.get(pageContext, "To1") %>
								</td>
								<td>
<%
	if (schedulerForm.isHaveEndDate()) {
	Calendar endDateCalendar = null;
		Date endDate;
		try {
			endDate = sdf.parse(schedulerForm.getEndDate());
		} catch (Exception e) {
			try {
				SimpleDateFormat sdf2 = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DOTSCHEDULER_DATE2);
				endDate = sdf2.parse(schedulerForm.getEndDate());
			} catch (Exception ex) {
				endDate = new Date();
			}
		}
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM/dd/yyyy hh:mm:ss a");
%>
									&nbsp;&nbsp;&nbsp;<%= sdf2.format(endDate) %>
<%
	} else {
%>
									&nbsp;&nbsp;&nbsp;<%= LanguageUtil.get(pageContext, "Not-Specified") %>
<%
	}
%>
								</td>
							</tr>
<%
	}
%>
						</table>
					</dd>
					<dt>
						<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Class-to-be-executed") %>:</b></font>
					</dt>				
					<dd>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
						<input class="form-text" dojoType="dijit.form.TextBox" name="javaClass" id="javaClass" value="<%= UtilMethods.isSet(schedulerForm.getJavaClass()) ? schedulerForm.getJavaClass() : "" %>" style="width: 300px;" type="text" >
<%
	} else {
%>
						<%= schedulerForm.getJavaClass() %>
<%
	}
%>
					</dd>
					<dt>
						<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Execute") %>:</b></font>
					</dt>
					<dd>
						<table cellpadding="0" cellspacing="0">
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
							<tr>
								<!--td-->
								<td colspan="3">
<%
	schedulerForm.setAtInfo(true);
%>
									<div style="display: none;">
									    <input type="checkbox" dojoType="dijit.form.CheckBox" id="atInfo" name="atInfo" />
									</div>
								<!--/td>
								<td colspan="2"-->
									<font class="bg" size="2">&nbsp;&nbsp;<%= LanguageUtil.get(pageContext, "at") %></font>
								</td>
							</tr>
							<tr>
								<td>&nbsp;
									
								</td>
								<td>
									<table>
										<tr>
											<td>
												<input type="radio" name="at" id="at1" dojoType="dijit.form.RadioButton" value="isTime" <%= UtilMethods.isSet(schedulerForm.getAt()) && schedulerForm.getAt().equals("isTime") ? "checked" : "" %> >
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table>
										<tr>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="atTimeHour" onChange="amPm('atTime');">
<%
	for (int i = 0; i < 24; i++) 
	{
		int val = i > 12 ?  i - 12: i;
		if (val == 0)
			val = 12;
%>
													<option <%= schedulerForm.getAtTimeHour() == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="atTimeMinute">
<%
	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= schedulerForm.getAtTimeMinute() == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select> :
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="atTimeSecond">
<%
	for (int i = 0; i < 60; ++i) {
		String val = (i < 10) ? "0" + i: String.valueOf(i);
%>
													<option <%= schedulerForm.getAtTimeSecond() == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span id="atTimePM"><font class="bg" size="2">AM</font></span>
											</td>
										</tr>
									</table>
									<script language="javascript">
									dojo.addOnLoad (function(){
										amPm('atTime');
									});
									</script>
								</td>
							</tr>
							<tr>
								<td>&nbsp;
									
								</td>
								<td>
									<table>
										<tr>
											<td>
												<input type="radio" name="at" id="at" dojoType="dijit.form.RadioButton" value="isBetween" <%= UtilMethods.isSet(schedulerForm.getAt()) && schedulerForm.getAt().equals("isBetween") ? "checked" : "" %> >
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table>
										<tr>
											<td>
												<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "between") %></font>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="betweenFromHour" onChange="amPm('betweenFrom');">
<%
	for (int i = 0; i < 24; i++) 
	{
		int val = i > 12 ?  i - 12: i;
		if (val == 0)
			val = 12;
%>
													<option <%= schedulerForm.getBetweenFromHour() == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span id="betweenFromPM"><font class="bg" size="2">AM</font></span>
												<script language="javascript">
												dojo.addOnLoad (function(){
													amPm('betweenFrom');
												});
												</script>
											</td>
											<td>&nbsp;</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="betweenToHour" onChange="amPm('betweenTo');">
<%
	for (int i = 0; i < 24; i++) 
	{
		int val = i > 12 ?  i - 12: i;
		if (val == 0)
			val = 12;
%>
													<option <%= schedulerForm.getBetweenToHour() == i ? "selected" : "" %> value="<%= i %>"><%= val %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span id="betweenToPM"><font class="bg" size="2">AM</font></span>
												<script language="javascript">
												dojo.addOnLoad (function(){
													amPm('betweenTo');
												});
												</script>
											</td>
										</tr>
									</table>
								</td>
							</tr>
<%
	} else {
%>
							<tr>
								<td>
									<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "at") %></font>
								</td>
								<td>
									&nbsp;&nbsp;&nbsp;
								</td>
								<td>
<%
	if (schedulerForm.isAtInfo()) {
		if (UtilMethods.isSet(schedulerForm.getAt()) && schedulerForm.getAt().equals("isTime")) {
			Date atTimeDate = new Date(0,0,0, schedulerForm.getAtTimeHour(), schedulerForm.getAtTimeMinute(), schedulerForm.getAtTimeSecond());
			SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss a");
%>
									<%= sdf2.format(atTimeDate) %>
<%
		} else {
			if (UtilMethods.isSet(schedulerForm.getAt()) && schedulerForm.getAt().equals("isBetween")) {
				Date betweenFromDate = new Date(0,0,0, schedulerForm.getBetweenFromHour(), schedulerForm.getBetweenFromMinute(), schedulerForm.getBetweenFromSecond());
				Date betweenToDate = new Date(0,0,0, schedulerForm.getBetweenToHour(), schedulerForm.getBetweenToMinute(), schedulerForm.getBetweenToSecond());
				SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss a");
%>
									<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "between") %></font>&nbsp;<%= sdf2.format(betweenFromDate) %>&nbsp;<font class="bg" size="2">and</font>&nbsp;<%= sdf2.format(betweenToDate) %>
<%
			}
		}
%>
<%
	} else {
%>
									<%= LanguageUtil.get(pageContext, "Not-Specified") %>
<%
	}
%>
								</td>
							</tr>
<%
	}
%>

<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
							<tr>
								<td>
<%
	schedulerForm.setEachInfo(true);
%>
									<div style="display: none;">
									    <input type="checkbox" dojoType="dijit.form.CheckBox" id="eachInfo" name="eachInfo"/>
									</div>
								</td>
								<td>&nbsp;
									
								</td>
								<td>
									<table>
										<tr>
											<td>
												<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "each") %> <input type="text" dojoType="dijit.form.TextBox" style="width: 30px;"  class="form-text" name="eachHours" id="eachHours" maxlength="3"  <%= 0 < schedulerForm.getEachHours() ? "value=\"" + schedulerForm.getEachHours() + "\"" : "" %> > <%= LanguageUtil.get(pageContext, "hours-and") %> <input type="text" class="form-text" dojoType="dijit.form.TextBox" style="width: 30px;"  name="eachMinutes" id="eachMinutes" maxlength="3" <%= 0 < schedulerForm.getEachMinutes() ? "value=\"" + schedulerForm.getEachMinutes() + "\"" : "" %> > <%= LanguageUtil.get(pageContext, "minutes") %></font>
											</td>
										</tr>
									</table>
								</td>
							</tr>
<%
	} else {
%>
							<tr>
								<td>
									<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "each") %></font>
								</td>
								<td>
									&nbsp;&nbsp;&nbsp;
								</td>
								<td>
<%
	if (schedulerForm.isEachInfo()) {
		String output = null;
		if (0 < schedulerForm.getEachHours())
			output = "&nbsp;" + schedulerForm.getEachHours() + "&nbsp;"+LanguageUtil.get(pageContext, "hours");
			
		if (0 < schedulerForm.getEachMinutes()) {
			if (output != null)
				output = output + "&nbsp;"+LanguageUtil.get(pageContext, "and");
			else
				output = "";
			output = output + "&nbsp;" + schedulerForm.getEachMinutes() + "&nbsp;"+LanguageUtil.get(pageContext, "minutes");
		}
%>
									<font class="bg" size="2"><%= output %></font>
<%
	} else {
%>
									<%= LanguageUtil.get(pageContext, "Not-Specified") %>
<%
	}
%>
								</td>
							</tr>
<%
	}
%>

<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
		if (!schedulerForm.isEveryInfo())
			schedulerForm.setEvery("");
%>
							<tr>
								<td>
								    <input type="checkbox" dojoType="dijit.form.CheckBox" id="everyInfo" name="everyInfo" <%= schedulerForm.isEveryInfo()?"checked":"" %>/>
								</td>
								<td colspan="2">
									<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "every") %></font>
								</td>
							</tr>
							<tr>
								<td>&nbsp;
									
								</td>
								<td>
									<table>
										<tr>
											<td>
												<input type="radio" name="every" id="every" dojoType="dijit.form.RadioButton" value="isDate" <%= UtilMethods.isSet(schedulerForm.getEvery()) && schedulerForm.getEvery().equals("isDate") ? "checked" : "" %> >
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table>
										<tr>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 120px;" name="everyDateMonth" onChange="updateDateOnly('everyDate');">
													<option value="*">-</option>
<%
	for (int i = 0; i < months.length; i++) {
%>
													<option <%= (schedulerForm.getEveryDateMonth()-1) == monthIds[i] ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="everyDateDay" onChange="updateDateOnly('everyDate');">
													<option value="*">-</option>
<%
	for (int i = 1; i <= 31; i++) {
%>
													<option <%= schedulerForm.getEveryDateDay() == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<select dojoType="dijit.form.FilteringSelect" style="width: 80px;" name="everyDateYear" onChange="updateDateOnly('everyDate');">
													<option value="*">-</option>
<%
	for (int i = currentYear - previous; i <= currentYear + 10; i++) {
%>
													<option <%= schedulerForm.getEveryDateYear() == i ? "selected" : "" %> value="<%= i %>"><%= i %></option>
<%
	}
%>
												</select>
											</td>
											<td>
												<span class="calMonthIcon" id="<portlet:namespace />calendar_input_2_button" onClick="<portlet:namespace />calendarOnClick_2();"></span>
											</td>
										</tr>
									</table>
									<input type="hidden" name="everyDate" value="" id="everyDate">
									<script language="javascript">
									dojo.addOnLoad (function(){
										updateDateOnly('everyDate');
									});
									</script>
								</td>
							</tr>
							<tr>
								<td>&nbsp;
									
								</td>
								<td valign="top">
									<table>
										<tr>
											<td>
												<input type="radio" name="every" id="every1" dojoType="dijit.form.RadioButton" value="isDays" <%= UtilMethods.isSet(schedulerForm.getEvery()) && schedulerForm.getEvery().equals("isDays") ? "checked" : "" %> >
											</td>
										</tr>
									</table>
								</td>
								<td>
									<table>
										<tr>
											<td>
												<%= LanguageUtil.get(pageContext, "Mon") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Tue") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Wed") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Thu") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Fri") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Sat") %>
											</td>
											<td>
												<%= LanguageUtil.get(pageContext, "Sun") %>
											</td>
										</tr>
										<tr>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay1" value="MON" <%= schedulerForm.isMonday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay2" value="TUE" <%= schedulerForm.isTuesday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay3" value="WED" <%= schedulerForm.isWednesday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay4" value="THU" <%= schedulerForm.isThusday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay5" value="FRI" <%= schedulerForm.isFriday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay6" value="SAT" <%= schedulerForm.isSaturday() ? "checked" : "" %> >
											</td>
											<td>
												<input type="checkbox" dojoType="dijit.form.CheckBox" name="everyDay" id="everyDay7" value="SUN" <%= schedulerForm.isSunday() ? "checked" : "" %> >
											</td>
										</tr>
									</table>
								</td>
							</tr>
<%
	} else {
%>
							<tr>
								<td>
									<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "every") %></font>
								</td>
								<td>
									&nbsp;&nbsp;&nbsp;
								</td>
								<td>
<%
	if (schedulerForm.isEveryInfo()) {
		if (UtilMethods.isSet(schedulerForm.getEvery()) && schedulerForm.getEvery().equals("isDate")) {
			String everyMonth = "-";
			if (0 < schedulerForm.getEveryDateMonth())
				everyMonth = months[schedulerForm.getEveryDateMonth()-1];
			
			String everyDay = "-";
			if (0 < schedulerForm.getEveryDateDay())
				everyDay = "" + schedulerForm.getEveryDateDay();
			
			String everyYear = "-";
			if (0 < schedulerForm.getEveryDateYear())
				everyYear = "" + schedulerForm.getEveryDateYear();
%>
									<%= everyMonth %>&nbsp;/&nbsp;<%= everyDay %>&nbsp;/&nbsp;<%= everyYear %>
<%
		} else {
			if (UtilMethods.isSet(schedulerForm.getEvery()) && schedulerForm.getEvery().equals("isDays")) {
%>
									<table>
										<tr>
<%
	if (schedulerForm.isMonday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Mon") %>
											</td>
<%
	}
	if (schedulerForm.isTuesday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Tue") %>
											</td>
<%
	}
	if (schedulerForm.isWednesday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Wed") %>
											</td>
<%
	}
	if (schedulerForm.isThusday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Thu") %>
											</td>
<%
	}
	if (schedulerForm.isFriday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Fri") %>
											</td>
<%
	}
	if (schedulerForm.isSaturday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Sat") %>
											</td>
<%
	}
	if (schedulerForm.isSunday()) {
%>
											<td>
												<%= LanguageUtil.get(pageContext, "Sun") %>
											</td>
<%
	}
%>
										</tr>
									</table>
<%
			}
		}
	} else {
%>
									<%= LanguageUtil.get(pageContext, "Not-Specified") %>
<%
	}
%>
								</td>
							</tr>
<%
	}
%>
						</table>
					</dd>
			</dl>
		</div>
		<div id="properties" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Parameters") %>">
<%
	java.util.Map<String, String> properties = schedulerForm.getMap();
	Iterator<String> keys = null;
	if (properties != null)
		keys = properties.keySet().iterator();
	
	boolean parameterShowed = false;
	String key;
	String value;
	
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName0" id="propertyName0" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue0" id="propertyValue0" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>
<%
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName1" id="propertyName1" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue1" id="propertyValue1" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>
<%
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName2" id="propertyName2" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue2" id="propertyValue2" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>
<%
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName3" id="propertyName3" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue3" id="propertyValue3" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>

<%
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName4" id="propertyName4" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>:  </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue4" id="propertyValue4" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>
<%
	if ((keys != null) && keys.hasNext()) {
		key = keys.next();
		value = properties.get(key);
	} else {
		key = "";
		value = "";
	}
%>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyName5" id="propertyName5" value="<%= key %>">&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><input type="text" dojoType="dijit.form.TextBox" name="propertyValue5" id="propertyValue5" value="<%= value %>">
				<br>
				<br>
<%
	} else {
		if (UtilMethods.isSet(key)) {
			parameterShowed = true;
%>
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Name") %>: </b></font><%= key %>&nbsp;&nbsp;
				<font class="bg" size="2"><b><%= LanguageUtil.get(pageContext, "Parameter-Value") %>: </b></font><%= value %>
				<br>
				<br>
<%
		}
	}
%>
<%
	if ((schedulerForm.getJobGroup() != null) && (schedulerForm.getJobGroup().equals("Recurrent Campaign")) && !parameterShowed) {
%>
			<font class="bg" size="2"><%= LanguageUtil.get(pageContext, "There-are-no-Parameters-to-show") %></font>
<%
	}
%>
		</div>
	</div>
<%
	if ((schedulerForm.getJobGroup() == null) || (schedulerForm.getJobGroup().equals("User Job"))) {
%>
	<div class="buttonRow">
	<% if ((schedulerForm != null) && (UtilMethods.isSet(schedulerForm.getJobGroup()))) { %>
		<button dojoType="dijit.form.Button" onClick="deleteSchedule(document.getElementById('fm'))" iconClass="deleteIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Delete")) %>
		</button>
	<% } %>
		<button dojoType="dijit.form.Button"  onClick="cancelEdit();return false;" iconClass="cancelIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Cancel")) %>
		</button>
		<button dojoType="dijit.form.Button" onClick="submitfm(document.getElementById('fm'))" iconClass="saveIcon">
			<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Save")) %>
		</button>
	</div>
<%
	}
%>
</html:form>
</liferay:box>
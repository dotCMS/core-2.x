<%@ page import="java.util.*,com.dotmarketing.portlets.events.model.*,com.dotmarketing.portlets.events.struts.*,com.dotmarketing.util.*,com.dotmarketing.portlets.categories.model.*,com.dotmarketing.beans.*,com.dotmarketing.factories.*,java.util.*,com.dotmarketing.portlets.files.model.*,com.dotmarketing.portlets.files.factories.*"  %>
<%@ page import="com.dotmarketing.factories.InodeFactory" %>
<%@ page import="com.dotmarketing.portlets.facilities.model.Facility" %>
<%@ page import="com.dotmarketing.business.APILocator" %>
<%@ page import="com.dotmarketing.portlets.categories.business.CategoryAPI" %>
<%@ page import="com.dotmarketing.portlets.categories.model.Category" %>
<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="javax.servlet.jsp.PageContext" %>
<%@ include file="/html/portlet/ext/events/init.jsp" %>
<%@ page import="com.dotmarketing.util.InodeUtils" %>

<% 
	CategoryAPI catAPI = APILocator.getCategoryAPI();

	EventForm form = (EventForm) request.getAttribute("eventForm");
	String stringFormInode = form.getInode();
	Recurance r = (Recurance) request.getAttribute(com.dotmarketing.util.WebKeys.RECURANCE_EDIT);
	
	boolean selected = false;
	
	java.util.Date startDate = form.getStartDate();
	java.util.Date endDate = form.getEndDate();
	java.util.Date setupDate = form.getSetupDate();
	java.util.Date breakDate = form.getBreakDate();
	
	//Debug
	int[] monthIds = CalendarUtil.getMonthIds();
	String[] months = CalendarUtil.getMonths(locale);
	String[] days = CalendarUtil.getDays(locale);
	
	//Facilities
	List facilities = InodeFactory.getInodesOfClass(Facility.class);
	pageContext.setAttribute("facilities", facilities, PageContext.PAGE_SCOPE);
	
	boolean isCMSAdmin = ((Boolean)request.getAttribute("isCMSAdministrator")).booleanValue();
	boolean isEventAdmin = ((Boolean)request.getAttribute("isEventAdministrator")).booleanValue();
	
	Event e = (Event) InodeFactory.getInode(form.getInode(), Event.class);
	String userId = user.getUserId();
	
	boolean conflictFound = (request.getAttribute("conflict_found") != null);
	boolean savingSeries = (request.getAttribute("saving_series") != null);
%>

<script>

<%
	
	//make departament categories render
	Category depts = catAPI.findByName(Config.getStringProperty("EVENT_TYPES"),user,false);
%>
	function changeDepts() {
		var parentSelect = document.getElementById("depsSelect");	
		var selectDeptInode = parentSelect.options[parentSelect.selectedIndex].value;
		var select = document.getElementById("subDepsSelect");	
		var opts = select.options;
		opts.length = 0;
<%
        if(depts != null)
        {
			List categories = catAPI.getChildren(depts,user,false);
	    	Iterator m = categories.iterator();
	    	while (m.hasNext()) {
	            Category cat = ( Category ) m.next();
            	String currentDeptInode = cat.getInode();
				List subCategories = catAPI.getChildren(cat,user,false);
		    	Iterator sm = subCategories.iterator();
		    	while (sm.hasNext()) {
		            Category subCat = ( Category ) sm.next();
					String categoryName = " + " + subCat.getCategoryName();
					String inode = subCat.getInode();
					boolean selectedCat = false;
					
					String [] cats = form.getCategories();

					if (cats != null) {
		            	for (int i = 0; i < cats.length; i++) {
		    	            String id = cats[i];
	        	        	if (subCat.getInode().equalsIgnoreCase(id)) {
		        	        	selectedCat = true;
	                		}
		            	}
	            	}
%>
			if (selectDeptInode == "<%=currentDeptInode%>") {
				var opt = new Option ();
				opt.text = "<%= categoryName %>";
				opt.value = "<%= inode %>";
				<%=(selectedCat)?"opt.selected=true":""%>
				opts[opts.length] = opt;
			}
<%
				}
		    }
        }
%>
	}
	
	//Check registrations status
	function showOnWebCalendarChange () {
		var form = document.getElementById("eventForm");	
		if (form.showPublic.checked) {
			form.registration.disabled = false;
		} else {
			form.registration.disabled = true;
			form.registration.checked = false;
		}
	}
	
	//Event functions
	
	<% if (isEventAdmin) { %>
	function resetEventStatus () {
		if(confirm("Do you want to reset the status for this specific Event?")){
	        form = document.getElementById("eventForm");
			document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="resetStatus" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>";
			form.submit();
		}
	}
	<% } %>
	
	function deleteEvent(){
		if(confirm("Do you want to delete this specific Event?")){
	        form = document.getElementById("eventForm");
			document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="delete" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>";
			form.submit();
		}
	}
	
	function deleteSeries(){
	
		if(confirm("Do you want to delete all events in this series?")){
	        form = document.getElementById("eventForm");
			document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="deleteSeries" /><portlet:param name="r" value="<%=r.getInode()%>" /></portlet:actionURL>";
			form.submit();
			return;
		}

	}
	
    function popRecurance(){
        form = document.getElementById("eventForm");
        form.redirect.value = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="edit" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>';
		document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_recurance" /><portlet:param name="cmd" value="edit" /><portlet:param name="inode" value="<%=r.getInode()%>" /><portlet:param name="parent" value="<%=stringFormInode%>" /></portlet:actionURL>";
		form.submit();
    }

    function SavePopRecurance(){
		alert("You must save this event before you can make it recur.");
		return false;
    }

	function doSubmit(){
		document.getElementById("dispatch").value = "save";
		document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="save" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>";
		return doFinalSubmit();
	}

	function doSubmitRecuring(){
		document.getElementById("dispatch").value = "saveSeries";
		document.getElementById("eventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="saveSeries" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>";
		return doFinalSubmit();
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

	function amPm_2(){
		var ele = document.getElementById("amPm_2");
		var val = document.getElementById("eventForm").calendar_2_hour[document.getElementById("eventForm").calendar_2_hour.selectedIndex].value ;
	
		if(val > 11){
			ele.innerHTML = "<font class=\"bg\" size=\"2\">PM</font>";
		}
		else{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">AM</font>";
		}
	}

	function amPm_3(){
		var ele = document.getElementById("amPm_3");
		var val = document.getElementById("eventForm").calendar_3_hour[document.getElementById("eventForm").calendar_3_hour.selectedIndex].value ;
	
		if(val > 11){
			ele.innerHTML = "<font class=\"bg\" size=\"2\">PM</font>";
		}
		else{
			ele.innerHTML = "<font class=\"bg\" size=\"2\">AM</font>";
		}
	}

	function tbdChanged () {
        var form = document.getElementById("eventForm");
		if (document.getElementById("tbdCheckbox").checked) {
			form.calendar_2_year.value = form.calendar_0_year.value;
			form.calendar_2_month.value = form.calendar_0_month.value;
			form.calendar_2_day.value = form.calendar_0_day.value;
			form.calendar_3_year.value = form.calendar_1_year.value;
			form.calendar_3_month.value = form.calendar_1_month.value;
			form.calendar_3_day.value = form.calendar_1_day.value;
			form.calendar_2_year.disabled = true;
			form.calendar_2_month.disabled = true;
			form.calendar_2_day.disabled = true;
			form.calendar_3_year.disabled = true;
			form.calendar_3_month.disabled = true;
			form.calendar_3_day.disabled = true;
			form.calendar_0_hour.disabled = true;
			form.calendar_0_minute.disabled = true;
			form.calendar_1_hour.disabled = true;
			form.calendar_1_minute.disabled = true;
			form.calendar_2_hour.disabled = true;
			form.calendar_2_minute.disabled = true;
			form.calendar_3_hour.disabled = true;
			form.calendar_3_minute.disabled = true;
			form.calendar_0_hour.selectedIndex = 0;
			form.calendar_0_minute.selectedIndex = 0;
			form.calendar_1_hour.selectedIndex = 0;
			form.calendar_1_minute.selectedIndex = 0;
			form.calendar_2_hour.selectedIndex = 0;
			form.calendar_2_minute.selectedIndex = 0;
			form.calendar_3_hour.selectedIndex = 0;
			form.calendar_3_minute.selectedIndex = 0;
			form.allTimeCheckbox.checked = false;
			form.allTimeCheckbox.disabled = true;
			document.getElementById("<portlet:namespace />calendar_input_2_button").style.visibility = "hidden";
			document.getElementById("<portlet:namespace />calendar_input_3_button").style.visibility = "hidden";
		} else {
			form.calendar_2_year.disabled = false;
			form.calendar_2_month.disabled = false;
			form.calendar_2_day.disabled = false;
			form.calendar_3_year.disabled = false;
			form.calendar_3_month.disabled = false;
			form.calendar_3_day.disabled = false;
			form.calendar_0_hour.disabled = false;
			form.calendar_0_minute.disabled = false;
			form.calendar_1_hour.disabled = false;
			form.calendar_1_minute.disabled = false;
			form.calendar_2_hour.disabled = false;
			form.calendar_2_minute.disabled = false;
			form.calendar_3_hour.disabled = false;
			form.calendar_3_minute.disabled = false;
			form.allTimeCheckbox.disabled = false;
			document.getElementById("<portlet:namespace />calendar_input_2_button").style.visibility = "";
			document.getElementById("<portlet:namespace />calendar_input_3_button").style.visibility = "";
		}
	}

	function allTimeChanged () {
        var form = document.getElementById("eventForm");
		if (form.allTimeCheckbox.checked) {
			//Setup Time (departure)
			form.calendar_2_hour.selectedIndex = 0;
			form.calendar_2_minute.selectedIndex = 0;
			//Start Time
			form.calendar_0_hour.selectedIndex = 0;
			form.calendar_0_minute.selectedIndex = 0;
			//End Time
			form.calendar_1_hour.selectedIndex = form.calendar_1_hour.options.length - 1;
			form.calendar_1_minute.selectedIndex = form.calendar_1_minute.options.length - 1;
			//Break Time (Return)
			form.calendar_3_hour.selectedIndex = form.calendar_1_hour.options.length - 1;
			form.calendar_3_minute.selectedIndex = form.calendar_1_minute.options.length - 1;
			//Change the AM/PM information
			amPm_0();
			amPm_1();
			amPm_2();
			amPm_3();			
		} else {
			form.calendar_1_hour.selectedIndex = form.calendar_0_hour.selectedIndex;
			form.calendar_1_minute.selectedIndex = form.calendar_0_minute.selectedIndex;
			//Change the AM/PM information
			amPm_0();
			amPm_1();
			amPm_2();
			amPm_3();
		}
	}
	
	function checkAllTime () {
        var form = document.getElementById("eventForm");
        if (form.calendar_0_hour.selectedIndex == 0 && form.calendar_0_minute.selectedIndex == 0 &&
        	form.calendar_1_hour.selectedIndex ==  form.calendar_1_hour.options.length - 1 &&
        	form.calendar_1_minute.selectedIndex == form.calendar_1_minute.options.length - 1) {
        	form.allTimeCheckbox.checked = true;
        } else {
        	form.allTimeCheckbox.checked = false;
        }
	}
	
    function doFinalSubmit(){
        form = document.getElementById("eventForm");

		var ele = document.getElementById("title");

        /* create javascript dates from the form */
        
        //Start date
		var sdMonth = parseFloat(form.calendar_0_month[form.calendar_0_month.selectedIndex].value) + 1;
		var sdDay = form.calendar_0_day[form.calendar_0_day.selectedIndex].value;
		var sdYear = form.calendar_0_year[form.calendar_0_year.selectedIndex].value;
		var sdHour = form.calendar_0_hour[form.calendar_0_hour.selectedIndex].value ;
		var sdMinute = form.calendar_0_minute[form.calendar_0_minute.selectedIndex].value;

        //End date
		var edMonth = parseFloat(form.calendar_1_month[form.calendar_1_month.selectedIndex].value) + 1;
		var edDay = form.calendar_1_day[form.calendar_1_day.selectedIndex].value;
		var edYear = form.calendar_1_year[form.calendar_1_year.selectedIndex].value;
		var edHour = form.calendar_1_hour[form.calendar_1_hour.selectedIndex].value ;
		var edMinute = form.calendar_1_minute[form.calendar_1_minute.selectedIndex].value;

        //Setup date
		var setupDateMonth = parseFloat(form.calendar_2_month[form.calendar_2_month.selectedIndex].value) + 1;
		var setupDateDay = form.calendar_2_day[form.calendar_2_day.selectedIndex].value;
		var setupDateYear = form.calendar_2_year[form.calendar_2_year.selectedIndex].value;
		var setupDateHour = form.calendar_2_hour[form.calendar_2_hour.selectedIndex].value ;
		var setupDateMinute = form.calendar_2_minute[form.calendar_2_minute.selectedIndex].value;

        //Break date
		var bdMonth = parseFloat(form.calendar_3_month[form.calendar_3_month.selectedIndex].value) + 1;
		var bdDay = form.calendar_3_day[form.calendar_3_day.selectedIndex].value;
		var bdYear = form.calendar_3_year[form.calendar_3_year.selectedIndex].value;
		var bdHour = form.calendar_3_hour[form.calendar_3_hour.selectedIndex].value ;
		var bdMinute = form.calendar_3_minute[form.calendar_3_minute.selectedIndex].value;

        /* check that end date > start date */
        sdate = new Date(sdYear, (sdMonth - 1), sdDay, sdHour, sdMinute);
        edate = new Date(edYear, (edMonth-1), edDay, edHour, edMinute);
        setupDate = new Date(setupDateYear, (setupDateMonth - 1), setupDateDay, setupDateHour, setupDateMinute);
        breakDate = new Date(bdYear, (bdMonth-1), bdDay, bdHour, bdMinute);
        if(edate.valueOf() < sdate.valueOf()){
                alert("The event end date/time is earlier than the event start date/time");
                return false;
        }


		var select = document.getElementById("facilityInodeSelect");
		if (select.options[select.selectedIndex].value != "" && 
			sdate.valueOf() == setupDate.valueOf() && 
				!document.getElementById("tbdCheckbox").checked) {
                alert("The event requires a setup time because you have selected a facility");
                return false;

		        if(sdate.valueOf() < setupDate.valueOf()){
		                alert("The event setup date/time is later than the event start date/time");
		                return false;
		        }
		        
		        if(edate.valueOf() > breakDate.valueOf()){
		                alert("The event end date/time is later than the event break date/time");
		                return false;
        }
		}




		//Time to be defined
		var tbd = document.getElementById("tbdCheckbox").checked;
		
        if(!tbd && sdate.valueOf() >= edate.valueOf()){
                alert("Please choose an end date/time for the event");
                return false;
        }


        /* build our hidden startDate, endDate, breakDate and setupDate fields */
		form.startDateString.value = sdMonth + "/" + sdDay + "/" + sdYear + " " + sdHour + ":" + sdMinute;
		form.endDateString.value = edMonth + "/" + edDay + "/" + edYear + " " + edHour + ":" + edMinute;
		form.breakDateString.value = bdMonth + "/" + bdDay + "/" + bdYear + " " + bdHour + ":" + bdMinute;
		form.setupDateString.value = setupDateMonth + "/" + setupDateDay + "/" + setupDateYear + " " + setupDateHour + ":" + setupDateMinute;
		
		//Building files attached list
		form.filesInodes.value = files.join(",");

		//submitting the form
		form.submit();
    }

	//File Attachment Functions   
	var files = new Array ();
	
	function browseTree(content,popup) {
	    var content = 'files';
	    var popup = 'fileInode';
	    view = "<%= java.net.URLEncoder.encode("(working=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = " +  com.dotmarketing.db.DbConnectionFactory.getDBFalse() + ")","UTF-8") %>";
		filesWindow = window.open('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/folders/view_folders_popup" /></portlet:actionURL>&view=' + view + '&content='+content+'&popup='+popup+'&child=true', "newwin", 'width=700,height=400,scrollbars=yes,resizable=yes');
	}
	
	function submitParent () {
		addFile();
	}
	
	function addFile () {
		var inode = document.getElementById("fileInode").value;
		if (isInodeSet(inode)) {
			var fileName = document.getElementById("selectedfileInode").value;
			var table = document.getElementById('filesTable');
			var row = table.insertRow(table.rows.length);
			var fileNameTD=row.insertCell(0);
			fileNameTD.innerHTML="<img src='/icon?i=" + fileName + "' width='16' height='16' align=absmiddle border=0 vspace=1 hspace=1> " + fileName + " - <a  href='javascript:removeFile(\"" + inode + "\")' style='text-decoration:underline;cursor: hand;'>remove</a>";
			row.align="left";
			files[files.length]	= inode;
			document.getElementById("fileInode").value = "";
			document.getElementById("selectedfileInode").value = "";
			document.getElementById('noFilesTable').style.visibility = "hidden";
		}
	}
	
	function removeFile(inode)
	{
		var idx = -1;
		var del = false;
		for (var i = 0; i < files.length; i++) {
			if (files[i] == inode) {
				del = true;
				idx = i;
			}
			if (del && (i + 1) < files.length) {
				files[i] = files[i + 1];
			}
		}
		files.length = files.length - 1;
		document.getElementById('filesTable').deleteRow(idx + 1)
		if (files.length == 0) document.getElementById('noFilesTable').style.visibility = "visible";

	}
	
	function displayProperties(id) {
		if (id == "properties") {
			//display basic properties
			document.getElementById("properties").style.display = "";
			document.getElementById("advanced").style.display = "none";

			//changing class for the tabs
			document.getElementById("properties_tab").className ="alpha";
			document.getElementById("advanced_tab").className ="beta";
		}
		else  {
			//display advanced properties
			document.getElementById("properties").style.display = "none";
			document.getElementById("advanced").style.display = "";

			//changing class for the tabs
			document.getElementById("properties_tab").className ="beta";
			document.getElementById("advanced_tab").className ="alpha";
		} 
	}
	
	
	
	
	
	//End File Attachment Functions   
	<liferay:include page="/html/js/calendar/calendar_js.jsp" flush="true">
		<liferay:param name="calendar_num" value="4" />
	</liferay:include>
    
    

</script>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"edit-event\") %>" />

		<table width="652" cellspacing="0" cellpadding="0" align="center" class="portletMenu">
			<tr >
				<td><a class="alpha" href="javascript:displayProperties('properties')" id="properties_tab">Basic Properties</a>
				<a class="beta" href="javascript:displayProperties('advanced')" id="advanced_tab">Advanced Properties</a>
				</td>
			</tr>
            <tr class="blue_Border">
				<td><img border="0" height="5" hspace="0" src="<%= COMMON_IMG %>/spacer.gif" vspace="0" width="1"></td>
			</tr>

       </table>

<table>
	<tr>
		<td><img border="0" height="5" hspace="0" src="<%= COMMON_IMG %>/spacer.gif" vspace="0" width="1"></td>
	</tr>
</table>

<div id="properties" style="width: 650px;">
<table align="center" cellpadding="3" cellspacing="1" valign="middle" border="0" width="650" class="portletBox listingTable">
	<html:form action='/ext/events/edit_event' styleId="eventForm">   
		<input type="hidden" name="dispatch" id="dispatch" value="save">
	    <input type="hidden" id="redirect" name="<portlet:namespace />redirect" value="<portlet:renderURL><portlet:param name="struts_action" value="/ext/events/view_events" /></portlet:renderURL>">
		<html:hidden property="inode" />
		<html:hidden property="filesInodes" />
		<input type="hidden" name="startDateString" id="startDateString" value="">
		<input type="hidden" name="endDateString" id="endDateString" value="">
		<input type="hidden" name="setupDateString" id="setupDateString" value="">
		<input type="hidden" name="breakDateString" id="breakDateString" value="">
		<input type="hidden" name="submitParent" id="submitParent" value="">
		<input type="hidden" name="referer" id="referer" value="<%=request.getParameter("referer")%>">
		<input type="hidden" name="continueWithConflicts" id="continueWithConflicts" value="false">
		
		<tr><td colspan="2">&nbsp;</td></tr>
		
		<TR>
			<TD width="150" align=right>Title of Event:</TD>
			<TD width="500" colspan="3">
			<html:text property="title" size="45" styleClass="text" styleId="title" />
			</TD>
		</tr>
		
		<tr>

			<TD align=right>Subtitle:</TD>
			<TD colspan="3">
			<html:text property="subtitle" size="45" styleClass="text" styleId="subtitle" />
			</TD>
		</TR>

		<%
	    String[] selectedCategories = form.getCategories();
		com.dotmarketing.portlets.entities.model.Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity("Event");
		java.util.List cats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);

			Iterator catsIter = cats.iterator();
			while (catsIter.hasNext()) {
				Category category = (Category) catsIter.next();
				List<Category> children = catAPI.getChildren(category,user,false);
				if (children.size() >= 1 && catAPI.canUseCategory(category, user, false)) {
					String catOptions = com.dotmarketing.util.UtilHTML.getSelectCategories(category,1,selectedCategories, user, false);
					if(catOptions.length() > 1){
					%>
						<tr>
							<td valign="top" align="right">
								<font class="bg" size="2"><%= category.getCategoryName()%>:</font>
							</td>				
							<td>
								<html:select property="categories" styleClass="selectMulti" size='5' multiple='true'>
									<%= catOptions %>
								</html:select>
							</td>
						</tr>
					<%}
			  	}
			}%>




		<TR>
				<TD align=right><%if(!InodeUtils.isSet(r.getInode())){%>Start Date/Time:<%}else{%>Start Time:<%}%></TD>
				<TD colspan="3">
					<select name="calendar_0_month">
					<%
						Calendar startDateCal = new java.util.GregorianCalendar();
						startDateCal.setTime(startDate);
						String sdMonth = Integer.toString(startDateCal.get(Calendar.MONTH));
						for (int i = 0; i < months.length; i++) {
					%>
						<option <%= (sdMonth.equals(Integer.toString(monthIds[i]))) ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
					<%
						}
					%>
					</select>
					<select name="calendar_0_day">
					<%
						String sdDay = Integer.toString(startDateCal.get(Calendar.DATE));
						for (int i = 1; i <= 31; i++) {
					%>
						<option <%= (sdDay.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
					<%
						}
					%>
					</select>
					<select name="calendar_0_year">
					<%
						int currentYear = startDateCal.get(Calendar.YEAR);
						String sdYear = Integer.toString(startDateCal.get(Calendar.YEAR));
						for (int i = currentYear - 10; i <= currentYear + 10; i++) {
					%>
						<option <%= (sdYear.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
					<%
						}
					%>
					</select>
					<img style="cursor:hand;" align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_0_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_0('<portlet:namespace />calObj_0');"> / 
					<select name="calendar_0_hour" id="calendar_0_hour" onChange="checkAllTime(); amPm_0();">
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
					<select name="calendar_0_minute" id="calendar_0_minute" onChange="checkAllTime();">
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
					<html:checkbox property="timeTBD" onclick="tbdChanged()" styleId="tbdCheckbox">Time TBD</html:checkbox>
					</div>
				</TD>
			</TR>
			<TR>
				<TD align=right><%if(!InodeUtils.isSet(r.getInode())){%>End Date/Time:<%}else{%>End Time:<%}%></TD>
				<TD colspan="3">
					<select name="calendar_1_month">
					<%
						Calendar endDateCal = new java.util.GregorianCalendar();
						endDateCal.setTime(endDate);
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
						for (int i = currentYear - 10; i <= currentYear + 10; i++) {
					%>
						<option <%= (edYear.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
					<%
						}
					%>
					</select>
					<img style="cursor:hand;" align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_1_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_1('<portlet:namespace />calObj_1');"> / 
					<select name="calendar_1_hour" id="calendar_1_hour" onChange="amPm_1();checkAllTime();">
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
					<select name="calendar_1_minute" id="calendar_1_minute" onChange="checkAllTime();">
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
					<input type="checkbox" onclick="allTimeChanged()" id="allTimeCheckbox">All Day Event</input>
					</div>
				</TD>
			</TR>
		<TR>
			<td align=right>Event Occurs:</td>
			<td colspan="3">
				<%if(!InodeUtils.isSet(form.getInode())){%> 
					<A href="#" class="beta" onClick="SavePopRecurance();"><%=com.dotmarketing.util.UtilHTML.recuranceToString(e, r)%></a> 
				<%} else{%> 
					<A href="#" class="beta" onClick="popRecurance();"><%=com.dotmarketing.util.UtilHTML.recuranceToString(e, r)%></a> 
				<%}%>
			</td>
		</TR>
		<TR>
			<td align=right>Approval Status:</td>
			<td colspan="3">
				<%if(isEventAdmin){%>
					<select name="approvalStatus">
						<%for(int i =0;i<com.dotmarketing.util.Constants.EVENT_APPROVAL_STATUSES.length;i++){%>
							<option value="<%=com.dotmarketing.util.Constants.EVENT_APPROVAL_STATUS_VALUES[i]%>"
						<%=(form.getApprovalStatus() == com.dotmarketing.util.Constants.EVENT_APPROVAL_STATUS_VALUES[i]) ? " selected " : ""%>
							
							
							><%=com.dotmarketing.util.Constants.EVENT_APPROVAL_STATUSES[i]%></option>
						<%}%>
					</select>
				<%}else{%>
				<%=com.dotmarketing.util.Constants.EVENT_APPROVAL_STATUSES[form.getApprovalStatus()]%>
				<%}%>
			</td>
		</TR>



		<TR>
			<TD align="left"></TD>
			<td colspan="3"><input type="checkbox" name="showPublic" id="showPublic" value="true" onClick="showOnWebCalendarChange();" <%=(form.isShowPublic()) ? " checked " : ""%>>&nbsp;&nbsp;Show on Web Calendar</TD>
		</TR>
		<TR>
			<TD align="left" colspan="0"></TD>
			<td colspan="3">
				<input type="checkbox" name="registration" id="registrations" value="true" <%=(form.isRegistration()) ? " checked " : ""%>>&nbsp;&nbsp;Registration allowed
				
				<% if ((isEventAdmin || userId.equals(e.getUserId())) && InodeUtils.isSet(e.getInode())) { %>
					 &nbsp;&nbsp;&nbsp;&nbsp;(<a href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/events/edit_event" /><portlet:param name="cmd" value="showRegistrations" /><portlet:param name="inode" value="<%=stringFormInode%>" /></portlet:actionURL>" class="beta">View Registrants</a>)
				<% } %>

			</TD>
		</tr>
		<TR>
			<TD valign="top" align=right>Description:</TD>
			<TD colspan="3"><html:textarea property="description" style="width:350px;height:100px"/>
		</TR>
		<tr>
			<td colspan=4><span class="shimIcon"></span></td>
		</tr>
		<tr>
			<td class="header" colspan="4" align=center>Contact Information</td>
		</tr>
		<TR>
			<TD align=right>Contact Name:</TD>
			<TD><html:text property="contactName" styleClass="text" /></TD>
			<TD align=right>Contact Email:</TD>
			<TD><html:text property="contactEmail" styleClass="text" /></TD>
		</TR>
		<TR>
			<TD align=right>Contact Phone:</TD>
			<TD><html:text property="contactPhone" styleClass="text" /></TD>
			<TD align=right>Contact Fax:</TD>
			<TD><html:text property="contactFax" styleClass="text" /></TD>
		</TR>
		<TR>
			<TD nowrap align=right>Contact Organization:</TD>
			<TD colspan="3"><html:text property="contactCompany" styleClass="text" /></TD>
		</TR>
		<tr>
			<td colspan=4><span class="shimIcon"></span></td>
		</tr>



	</TABLE>
</div>

<%-- / basic properties --%>



<%-- advanced properties --%>
<div id="advanced" style="width: 650px; display:none; ">
	<table align="center" cellpadding="0" cellspacing="0" valign="middle" border="0" width="650" class="portletBox listingTable">
		
		<tr><td colspan="2">&nbsp;</td></tr>

		<TR>
			<TD align=right>Facility:</TD>
			<TD>
				<html:select property="facilityInode" styleId="facilityInodeSelect" styleClass="selectMulti" size="1">
					<html:option value="">None / Off-Campus</html:option>
					<html:options collection="facilities" property="inode" labelProperty="facilityName"/>
				</html:select>
			</TD>

			<TD align=right>Web Address:</TD>
			<TD><html:text property="webAddress" styleClass="text" /></TD>
		</TR>
		<tr>
			<TD align=right>Room:</TD>
			<TD><html:text property="location" styleClass="text" /></TD>
		</tr>		
		<TR>
			<TD align=right>Setup (Departure)Date/Time:</TD>
			<TD colspan="3">
				<select name="calendar_2_month">
				<%
					Calendar setupDateCal = new java.util.GregorianCalendar();
					setupDateCal.setTime(setupDate);
					String setupDateMonth = Integer.toString(setupDateCal.get(Calendar.MONTH));
					for (int i = 0; i < months.length; i++) {
				%>
					<option <%= (setupDateMonth.equals(Integer.toString(monthIds[i]))) ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
				<%
					}
				%>
				</select>
				<select name="calendar_2_day">
				<%
					String setupDateDay = Integer.toString(setupDateCal.get(Calendar.DATE));
					for (int i = 1; i <= 31; i++) {
				%>
					<option <%= (setupDateDay.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
				<%
					}
				%>
				</select>
				<select name="calendar_2_year">
				<%
					int setupDateYear = setupDateCal.get(Calendar.YEAR);
					String setupDateYearStr = Integer.toString(setupDateCal.get(Calendar.YEAR));
					for (int i = setupDateYear - 10; i <= setupDateYear + 10; i++) {
				%>
					<option <%= (setupDateYearStr.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
				<%
					}
				%>
				</select>
				<img style="cursor:hand;" align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_2_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_2('<portlet:namespace />calObj_2');"> / 
				<select name="calendar_2_hour" onChange="amPm_2();">
				<%
					int setupDateHour = setupDateCal.get(Calendar.HOUR_OF_DAY);
					for (int i = 0; i < 24; i++) {
					
						int val = i > 12 ?  i - 12: i;
						if(val ==0) val = 12;
				%>
						<option <%= (setupDateHour == i) ? "selected" : "" %> value="<%= i %>"><%= val %></option>
				<%
					}
				%>
				</select> : 
				<select name="calendar_2_minute">
				<%
					int setupDateMin  = setupDateCal.get(Calendar.MINUTE);
					selected = false;
					for (int i = 0; i < 60; i= (i+5)) {
						String val = (i< 10) ? "0" + i: String.valueOf(i);
				%>
					<option <%= (i >= setupDateMin && ! selected) ? "selected" : "" %> value="<%= val %>"><%= val %></option>
				<%
						if(i >= setupDateMin) selected = true;
					}
				%>
				</select>
				<span id="amPm_2"><font class="bg" size="2"><%=(setupDateHour > 11) ? "PM" : "AM"%></font></span>
			</TD>
		</TR>
		
		<TR>
			<TD align=right>Break (Return)Date/Time:</TD>
			<TD colspan="3">
				<select name="calendar_3_month">
				<%
					Calendar breakDateCal = new java.util.GregorianCalendar();
					breakDateCal.setTime(breakDate);
					String breakDateMonth = Integer.toString(breakDateCal.get(Calendar.MONTH));
					for (int i = 0; i < months.length; i++) {
				%>
					<option <%= (breakDateMonth.equals(Integer.toString(monthIds[i]))) ? "selected" : "" %> value="<%= monthIds[i] %>"><%= months[i] %></option>
				<%
					}
				%>
				</select>
				<select name="calendar_3_day">
				<%
					String breakDateDay = Integer.toString(breakDateCal.get(Calendar.DATE));
					for (int i = 1; i <= 31; i++) {
				%>
					<option <%= (breakDateDay.equals(Integer.toString(i))) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
				<%
					}
				%>
				</select>
				<select name="calendar_3_year">
				<%
					int breakDateYear = breakDateCal.get(Calendar.YEAR);
					String breakDateYearStr = Integer.toString(breakDateYear);
					for (int i = breakDateYear - 10; i <= breakDateYear + 10; i++) {
				%>
					<option <%= (breakDateYear == i) ? "selected" : "" %> value="<%= i %>"><%= i %></option>
				<%
					}
				%>
				</select>
				<img style="cursor:hand;" align="absmiddle" border="0" hspace="0" id="<portlet:namespace />calendar_input_3_button" src="<%= COMMON_IMG %>/calendar/calendar.gif" vspace="0" onClick="<portlet:namespace />calendarOnClick_3('<portlet:namespace />calObj_3');"> / 
				<select name="calendar_3_hour" onChange="amPm_3();">
				<%
					int breakDateHour = breakDateCal.get(Calendar.HOUR_OF_DAY);
					for (int i = 0; i < 24; i++) {
					
						int val = i > 12 ?  i - 12: i;
						if(val ==0) val = 12;
				%>
						<option <%= (breakDateHour == i) ? "selected" : "" %> value="<%= i %>"><%= val %></option>
				<%
					}
				%>
				</select> : 
				<select name="calendar_3_minute">
				<%
					int breakDateMin  = breakDateCal.get(Calendar.MINUTE);
					selected = false;
					for (int i = 0; i < 60; i= (i+5)) {
						String val = (i< 10) ? "0" + i: String.valueOf(i);
				%>
					<option <%= (i >= breakDateMin && ! selected) ? "selected" : "" %> value="<%= val %>"><%= val %></option>
				<%
						if(i >= breakDateMin) selected = true;
					}
				%>
				</select>
				<span id="amPm_3"><font class="bg" size="2"><%=(breakDateHour > 11) ? "PM" : "AM"%></font></span>
			</TD>
		</TR>
		
		<TR>
			<TD align=right>Comments / Equipment:</TD>
			<TD colspan="3"><html:textarea property="commentsEquipment" style="width:350px;height:100px"/></TD>
		</TR>
		<TR>
			<TD valign="top" align=right>Directions:</TD>
			<TD colspan="3"><html:textarea property="directions" style="width:350px;height:100px"/>
		</TR>
		
		
		<% if (isCMSAdmin) { %>
		
		
		
		<TR>
			<TD colspan="4">&nbsp;</TD>
		</TR>
		
				<input type="hidden" name="fileInode" id="fileInode">
				<input type="hidden" name="selectedfileInode" id="selectedfileInode" />
				<tr class="header">
					<td colspan="3" align="center" >File Attachments</td>
					<td  align="right">
                        <button dojoType="dijit.form.Button"  onClick="browseTree()">
                            Browse for File
                        </button>
                   </td>
				</tr>
				<tr>
					<td colspan="4">
					<table align="center" width="100%" id="filesTable" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="left">
							</td>
						</tr>
					</table>
					<table align="center" width="100%" id="noFilesTable" border="0" cellpadding="0" cellspacing="0" style="visibility:visible">
						<tr>
							<td align="center">- No Files <br/><br/></td>
						</tr>
					</table>
					</td>
				</tr>
		<% } %>
				
	</table>
</div>


<table cellspacing="1" cellpadding="4" align="center">
	<tr>
		<td>
             <button dojoType="dijit.form.Button" onClick="javascript:window.history.back(-1)">Cancel</button> 
        </td>
		<%if(InodeUtils.isSet(form.getInode())){%>
			<td>
               <button dojoType="dijit.form.Button" onClick="deleteEvent()">Delete</button> 
            </td>
		<%}%>
		<%if(InodeUtils.isSet(r.getInode())){%>
			<td>
                <button dojoType="dijit.form.Button" onClick="deleteSeries()">Delete Series</button> 
            </td>
		<%}%>
		<td>
           <button dojoType="dijit.form.Button" onClick="doSubmit()">Save</button>
        </td>
		<%if(InodeUtils.isSet(r.getInode())){%>
			<td>
              <button dojoType="dijit.form.Button" onClick="doSubmitRecuring()">Save Series</button>
            </td>
		<%}%>
	</tr>
</table>


	</html:form>

</liferay:box>
<script language="javascript">
	//Set calendar dates functions
	var myForm = document.getElementById('eventForm');
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

	//Upadate SubDepartment Combo
	//changeDepts();
	
	//Check the TBD and all time checkboxes
	tbdChanged ();
	checkAllTime ();
	
	//update registrations checkbox
	showOnWebCalendarChange();
	
	//Set event files
<%
	String[] fileInodes = form.getFilesInodes();
	if (fileInodes != null) {
		for (int i = 0; i < fileInodes.length; i++) {
			File file = (File) InodeFactory.getInode(fileInodes[i], File.class);
			String filename = file.getFileName();
			String inode = file.getInode();
%>
	document.getElementById("fileInode").value = '<%= inode %>';
	document.getElementById("selectedfileInode").value = '<%= filename %>';
	addFile ();
<%
		}
	}
%>	
	<% if(conflictFound) { %>
	var agree=confirm("This event has conflicts with other(s) approved event(s), are you sure do you want to continue?");
	if (agree) {
		document.getElementById('continueWithConflicts').value = 'true';
		<% if(savingSeries) { %>
		doSubmitRecuring();
		<% } else {%>
		doSubmit();
		<% } %> 
	}
	<% } %> 
</script>
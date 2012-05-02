<%@page import="java.lang.management.ManagementFactory"%>
<%@page import="java.lang.management.RuntimeMXBean"%>
<%@page import="com.dotmarketing.business.DotGuavaCacheAdministratorImpl"%>
<%@page import="com.dotmarketing.cache.H2CacheLoader"%>
<%@page import="com.dotmarketing.business.DotJBCacheAdministratorImpl"%>
<%@page import="com.dotmarketing.business.CacheLocator"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="javax.portlet.WindowState"%>
<%@ page import="com.dotmarketing.portlets.structure.model.Structure"%>
<%@ page import="com.dotmarketing.portlets.cmsmaintenance.struts.CmsMaintenanceForm"%>
<%@ page import="java.util.List"%>

<%@ include file="/html/portlet/ext/cmsmaintenance/init.jsp"%>


<%

DateFormat modDateFormat = java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT, locale);

java.util.Map params = new java.util.HashMap();
params.put("struts_action",	new String[] { "/ext/cmsmaintenance/view_cms_maintenance" });
String referer = java.net.URLEncoder.encode(com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.NORMAL.toString(), params), "UTF-8");

CmsMaintenanceForm CMF = (com.dotmarketing.portlets.cmsmaintenance.struts.CmsMaintenanceForm) request.getAttribute("CmsMaintenanceForm");
%>
<script type='text/javascript' src='/dwr/interface/CMSMaintenanceAjax.js'></script>
<script type="text/javascript">
<liferay:include page="/html/js/calendar/calendar_js_box_ext.jsp" flush="true">
  <liferay:param name="calendar_num" value="1" />
</liferay:include>
</script> 
<script language="Javascript">
var view = "<%= java.net.URLEncoder.encode("(working=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")","UTF-8") %>";

var isIndexTabShownOnReindexing = false;

function submitform(cacheName)
{
	form = document.getElementById('cmsMaintenanceForm');
	var action = "<portlet:actionURL>";
	action += "<portlet:param name='struts_action' value='/ext/cmsmaintenance/view_cms_maintenance' />";
	action += "<portlet:param name='cmd' value='cache' />";
	action += "</portlet:actionURL>";
	form.action = action;
	form.cmd.value="cache";
	form.cmd.value="cache";
	cacheNameID = document.getElementById('cacheName');
	cacheNameID.value = cacheName;
	form.submit();
}

function checkReindexationCallback (response) {
	var inFullReindexation = response['inFullReindexation'];
	var contentCountToIndex = response['contentCountToIndex'];
	var lastIndexationProgress = response['lastIndexationProgress'];
	var currentIndexPath = response['currentIndexPath'];
	var newIndexPath = response['newIndexPath'];
	var lastIndexationStartTime = response['lastIndexationStartTime'];
	var lastIndexationEndTime = response['lastIndexationEndTime'];

	var currentIndexDirDiv = document.getElementById("currentIndexDirDiv");
	currentIndexDirDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"Current-index-directory") %>: " + currentIndexPath;
	
	var reindexationInProgressDiv = document.getElementById("reindexationInProgressDiv");
	if (inFullReindexation) {
		if(!isIndexTabShownOnReindexing){
			dijit.byId('mainTabContainer').selectChild('index');
			isIndexTabShownOnReindexing = true;
		}
		dojo.query(".indexActionsDiv").style("display","none");


		reindexationInProgressDiv.style.display = "";
		var newIndexDirPathDiv = document.getElementById("indexationStartTimeDiv"); 
		newIndexDirPathDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"Indexation-start-time") %>: " + lastIndexationStartTime;
		var newIndexDirPathDiv = document.getElementById("newIndexDirPathDiv"); 
		newIndexDirPathDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"New-index-directory-path") %>: <b>" + newIndexPath + "</b>";
		
		var bar = dijit.byId("reindexProgressBar");
		if(bar != undefined){
		    dijit.byId("reindexProgressBar").update({
		      maximum: contentCountToIndex, 
		      progress: lastIndexationProgress
		    });
		}
		
		var indexationProgressDiv = document.getElementById("indexationProgressDiv"); 
		indexationProgressDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"Reindex-Progress") %>: " + lastIndexationProgress + " / " + contentCountToIndex + " ";
	} else {
		dojo.query(".indexActionsDiv").style("display","");
		reindexationInProgressDiv.style.display = "none";
		var lastIndexationDiv = document.getElementById("lastIndexationDiv");
		if (lastIndexationProgress >= 0) {
			lastIndexationDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"Content-indexed-last-run") %>: " + lastIndexationProgress + 
				", <%= LanguageUtil.get(pageContext,"started-at") %>: " + lastIndexationStartTime + " <%= LanguageUtil.get(pageContext,"and-ended-at") %>: " + lastIndexationEndTime;
		} else {
			lastIndexationDiv.innerHTML = "";
		}
	}
	setTimeout("checkReindexation()", 5000);
}

function checkReindexation () {
	CMSMaintenanceAjax.getReindexationProgress(checkReindexationCallback);
}

function stopReIndexing(){
	CMSMaintenanceAjax.stopReindexation(checkReindexationCallback);
	viewReindexJournalData();
}

function checkFixAssetCallback (responser) {
	$("fixAssetsButton").disabled = false;	

	var fixAssetInfoDiv = document.getElementById("fixAssetInfo");
	var fixAssetTimeDiv = document.getElementById("fixAssetTime");
	var infodiv = "";
	
	if(responser != undefined){

		for(i=0;i<responser.size();i++){
			response=responser[i];
			var total = response['total'];
			var actual = response['actual'];
			error = response['error'];
			var currentIndexPath = response['currentIndexPath'];
			var initialTime = response['initialTime'];
			var finalTime = response['finalTime'];
			var running = response['running'];
			var percentage = response['percentage'];
			var elapsed = response['elapsed'];
			var remaining = response['remaining'];
		     description = response['description'];

		     infodiv =infodiv +"<%= LanguageUtil.get(pageContext,"The-Task-perform-was") %> " + description + " .<%= LanguageUtil.get(pageContext,"The-total-of-assets-to-change-is") %> " + total + " <%= LanguageUtil.get(pageContext,"--and--") %> " + error + " <%= LanguageUtil.get(pageContext,"assets-were-succesfully-fixed") %>"+"<br />";
		     infodiv  =infodiv+"<%= LanguageUtil.get(pageContext,"The-start-time-was") %> " + initialTime + " <%= LanguageUtil.get(pageContext,"and-ended-on") %>  "+ finalTime+"<br /><br />";
			
			}
		
			fixAssetInfoDiv.innerHTML = infodiv;
			//fixAssetTimeDiv.innerHTML = timeDiv;
			document.getElementById("fixAssetsMessage").innerHTML ="";
			//$("fixAssetsButton").disabled = true;
			document.getElementById("fixAssetsButtonDiv").style.display = "";	
			
		//	setTimeout("fixAssetsCallback()", 10000000);
	}

	else{
		fixAssetInfoDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"No-Tasks-were-executed") %>"
		fixAssetTimeDiv.innerHTML = "";
		
		document.getElementById("fixAssetsButtonDiv").style.display = "";
		document.getElementById("fixAssetsMessage").innerHTML ="";
		//setTimeout("fixAssetsCallback()", 10000000);
				
	}

	setTimeout("fixAssetsCallback()", 10000000);
}
		

function checkFixAsset()
{
	CMSMaintenanceAjax.getFixAssetsProgress(checkFixAssetCallback);
}

function doReplace () {
  if (document.getElementById("searchString").value == "") {
  		alert ("<%= LanguageUtil.get(pageContext,"Please-specify-a-search-string") %>");
    	return;
  }
  if (confirm ("<%= LanguageUtil.get(pageContext,"Are-you-sure") %>")) {
  	form = document.getElementById('cmsMaintenanceForm');
	var action = "<portlet:actionURL>";
	action += "<portlet:param name='struts_action' value='/ext/cmsmaintenance/view_cms_maintenance' />";
	action += "<portlet:param name='cmd' value='searchandreplace' />";
	action += "</portlet:actionURL>";
	form.cmd.value="searchandreplace";
	form.action = action
	form.submit();
  }
}

function doCreateZip(dataOnly){
   form = document.getElementById('cmsMaintenanceForm');
	var action = "<portlet:actionURL>";
	action += "<portlet:param name='struts_action' value='/ext/cmsmaintenance/view_cms_maintenance' />";
	action += "<portlet:param name='cmd' value='createZip' />";
	action += "</portlet:actionURL>";
	form.dataOnly.value = dataOnly;
	form.cmd.value="createZip";
	form.action = action
	form.submit();
}


function doDownloadZip(dataOnly){
   form = document.getElementById('cmsMaintenanceForm');
	var action = "<portlet:actionURL>";
	action += "<portlet:param name='struts_action' value='/ext/cmsmaintenance/view_cms_maintenance' />";
	action += "<portlet:param name='cmd' value='downloadZip' />";
	action += "</portlet:actionURL>";
	form.dataOnly.value = dataOnly;
	form.cmd.value="downloadZip";
	form.action = action
	form.submit();
}

function doUpload(){
	form = document.getElementById('cmsMaintenanceForm');
	var action = "<portlet:actionURL>";
	action += "<portlet:param name='struts_action' value='/ext/cmsmaintenance/view_cms_maintenance' />";
	action += "</portlet:actionURL>";
	form.cmd.value="upload";
	form.action = action
	form.submit();
}


function doFixAssetsInconsistencies(){
   form = document.getElementById('cmsMaintenanceForm');
      
   if (confirm("<%= LanguageUtil.get(pageContext,"Do-you-want-to-fix-assets-inconsistencies") %>")) {
   		document.getElementById("fixAssetsButtonDiv").style.display = "none";
	 	$("fixAssetsButton").disabled = true;
		document.getElementById("fixAssetsMessage").innerHTML = "<font face='Arial' size='2' color='#ff0000'><b><%= LanguageUtil.get(pageContext,"Working") %></b></font>";		
		CMSMaintenanceAjax.fixAssetsInconsistencies(fixAssetsCallback);
	}
}

function fixAssetsCallback(responser)
{
	$("fixAssetsButton").disabled = false;	

	var fixAssetInfoDiv = document.getElementById("fixAssetInfo");
	var fixAssetTimeDiv = document.getElementById("fixAssetTime");
	var infodiv = "";
	if(responser!= null){

		for(i=0;i<responser.size();i++){
			response=responser[i];
			var total = response['total'];
			var actual = response['actual'];
			error = response['error'];
			var currentIndexPath = response['currentIndexPath'];
			var initialTime = response['initialTime'];
			var finalTime = response['finalTime'];
			var running = response['running'];
			var percentage = response['percentage'];
			var elapsed = response['elapsed'];
			var remaining = response['remaining'];
		     description = response['description'];

		     infodiv =infodiv +"<%= LanguageUtil.get(pageContext,"The-Task-perform-was") %> " + description + " .<%= LanguageUtil.get(pageContext,"The-total-of-assets-to-change-is") %> " + total + " <%= LanguageUtil.get(pageContext,"--and--") %> " + error + " <%= LanguageUtil.get(pageContext,"assets-were-succesfully-fixed") %>"+"<br />";
		     infodiv  =infodiv+"<%= LanguageUtil.get(pageContext,"The-start-time-was") %> " + initialTime + " <%= LanguageUtil.get(pageContext,"and-ended-on") %>  "+ finalTime+"<br /><br />";
			
			}
		
			fixAssetInfoDiv.innerHTML = infodiv;
			document.getElementById("fixAssetsMessage").innerHTML ="";
			document.getElementById("fixAssetsButtonDiv").style.display = "";	
			
	}

	else{
		fixAssetInfoDiv.innerHTML = "<%= LanguageUtil.get(pageContext,"No-Tasks-were-executed") %>"
		fixAssetTimeDiv.innerHTML = "";
		
		document.getElementById("fixAssetsButtonDiv").style.display = "";
		document.getElementById("fixAssetsMessage").innerHTML ="";			
	}
}



function doDeleteContentlets(){
	var ids= document.getElementById('contentIdsList').value;
	if(ids=="" || ids.indexOf(';') > 0 || ids.indexOf(':')>0 || ids.indexOf('.')>0 )
	{
	     alert("<%= LanguageUtil.get(pageContext,"Please-enter-a-identifiers-list") %>");
	     return false;
	}

	  if(confirm("<%= LanguageUtil.get(pageContext,"Do-you-want-to-delete-this-contentlet-s") %>")){
		 	$("deleteContentletMessage").innerHTML= '<font face="Arial" size="2" color="#ff0000><b><%= LanguageUtil.get(pageContext,"Process-in-progress-deleting-contentlets") %></b></font>';
		 	$("deleteContentletButton").disabled = true;
			CMSMaintenanceAjax.deleteContentletsFromIdList(document.getElementById('contentIdsList').value, document.getElementById('userId').value, doDeleteContentletsCallback);
		}
}

function doDeleteContentletsCallback(contentlets){
 	
    var message="";
 
 	if (contentlets[0]!="")
 	{ 
 		var contaddedsize=contentlets[0];
 	 	/*if(contentlets[0].indexOf(",")){
 	 	 	var contadded=contentlets[0].split(',')
 	 	 	contaddedsize=contadded.length;
 	 	 	}*/
 	 	message+= contaddedsize+ ' <%= LanguageUtil.get(pageContext,"contentlets-were-succesfully-deleted") %></br>';	
 	}
	if (contentlets[1]!="")
 	{ 
 	 	if(contentlets[1].indexOf(",")){
 	 	 	var contnotfound=contentlets[1].split(',')
 	 	 	message+=  '<%= LanguageUtil.get(pageContext,"The-following") %> ' + contnotfound.length + ' <%= LanguageUtil.get(pageContext,"contentlets-were-not-found") %>: '+ contentlets[1] +'</br>';	
 	 	 	}
 	 	else message+= '<%= LanguageUtil.get(pageContext,"The-following") %> ' + ' <%= LanguageUtil.get(pageContext, "contentlet-was-not-found") %>: '+ contentlets[1] +'</br>';	
 	}

	if (contentlets[2]!="")
 	{ 
 	 	if(contentlets[2].indexOf(",")){
 	 	 	var conthasreqrel=contentlets[2].split(',')
 	 	 	message+= '<%= LanguageUtil.get(pageContext,"The-following") %> ' + conthasreqrel.length + ' <%= LanguageUtil.get(pageContext,"contentlet-s-could-not-be-deleted-because-the-contentlet-is-required-by-another-piece-of-content") %>: '+ contentlets[2] +'</br>';	
 	 	 	}
 	 	else message+= '<%= LanguageUtil.get(pageContext,"The-following") %> ' + ' <%= LanguageUtil.get(pageContext, "contentlet-s-could-not-be-deleted-because-the-contentlet-is-required-by-another-piece-of-content") %>: '+ contentlets[2] +'</br>';	
 	}
	if (contentlets[3]!="")
 	{ 
 	 	if(contentlets[3].indexOf(",")){
 	 	 	var contnotfound=contentlets[3].split(',')
 	 	 	message+= '<%= LanguageUtil.get(pageContext,"The-following") %> ' + contnotfound.length + ' <%= LanguageUtil.get(pageContext,"contentlet-s-could-not-be-deleted-because-the-user-does-not-have-the-necessary-permissions") %>:'+ contentlets[3] +'</br>';	
 	 	 	}
 	 	else message+= '<%= LanguageUtil.get(pageContext,"The-following") %> ' + ' <%= LanguageUtil.get(pageContext, "contentlet-s-could-not-be-deleted-because-the-user-does-not-have-the-necessary-permissions") %>:'+ contentlets[1] +'</br>';	
 	}
 	
	document.getElementById("deleteContentletMessage").innerHTML=message;
	document.getElementById("deleteContentletButton").disabled = false;
}

function doDropAssets(){
   var form = $('cmsMaintenanceForm');
   if(!validateDate(form.removeassetsdate)){
     alert("<%= LanguageUtil.get(pageContext,"Please,-enter-a-valid-date") %>");
     return false;
   }
   
  if(confirm("<%= LanguageUtil.get(pageContext,"Do-you-want-to-drop-all-old-assets") %>")){
	 	$("dropAssetsMessage").innerHTML= '<font face="Arial" size="2" color="#ff0000><b><%= LanguageUtil.get(pageContext,"Process-in-progress") %></b></font>';
	 	$("dropAssetsButton").disabled = true;
		CMSMaintenanceAjax.removeOldVersions(form.removeassetsdate.value, doDropAssetsCallback);
	}
}

function doDropAssetsCallback(removed){
 	$("dropAssetsButton").disabled = false;
	if (removed >= 0)
	 	document.getElementById("dropAssetsMessage").innerHTML= '<font face="Arial" size="2" color="#ff0000><b>' + removed + '<%= LanguageUtil.get(pageContext,"old-asset-versions-found-and-removed-from-the-system") %></b></font>';
	else if (removed == -2)
	 	document.getElementById("dropAssetsMessage").innerHTML= '<font face="Arial" size="2" color="#ff0000><b><%= LanguageUtil.get(pageContext,"Database-inconsistencies-found.-The-process-was-cancelled") %></b></font>';
	else
	 	document.getElementById("dropAssetsMessage").innerHTML= '<font face="Arial" size="2" color="#ff0000><b><%= LanguageUtil.get(pageContext,"Remove-process-failed.-Check-the-server-log") %></b></font>';
}

function validateDate(date){

  if(date == null || date.value==''){
  	return false;
  }
  var dateStr = date.value;
  
  var month= dateStr.substring(0,2);
  if(parseInt(month) > 12){
  	return false;
  }
  
  var day= dateStr.substring(3,5);
  if(parseInt(day) > 31){
  	return false;
  }
  
  var year= dateStr.substring(6,10);
  if(parseInt(year) > 9999 || parseInt(year) < 1900  ){
  	return false;
  }
  
  return true;
}

function <portlet:namespace />setCalendarDate_0(year, month, day) {
	   date = document.getElementById("removeassetsdate");
	   var monthStr = ''+month;
	   var dayStr = ''+day;
	   if(month < 10)
	       monthStr = '0'+month;
       if(day < 10)
	       dayStr = '0'+day;
	   date.value=monthStr+"/"+dayStr+"/"+year;
	}

function indexStructureChanged(){
	if(dojo.byId('structure').value != "<%= LanguageUtil.get(pageContext,"Rebuild-Whole-Index") %>")
		dijit.byId('cleanReindexButton').attr('disabled', false);
	else
		dijit.byId('cleanReindexButton').attr('disabled', true);
}

function cleanReindexStructure(){
	var strInode = dojo.byId('structure').value;
	CMSMaintenanceAjax.cleanReindexStructure(strInode,showDotCMSSystemMessage);
}

var journalDataCellFuncs = [
                  function(data) { return data['serverId']; },
                  function(data) { return data['count']; },
                  function(data) { return data['priority']; }
                ];

var noDataCellFuncs = [];
 
function viewReindexJournalData(){
	CMSMaintenanceAjax.getReindexJournalData(viewReindexJournalDataCallback);
}
function viewReindexJournalDataCallback(data){
	if(data.length > 0){
		dwr.util.removeAllRows('reindexJournalData_tableBody');
		dwr.util.addRows( "reindexJournalData_tableBody", data , journalDataCellFuncs, { escapeHtml:false });
	}else{
		dwr.util.removeAllRows('reindexJournalData_tableBody');
		dwr.util.addRows( "reindexJournalData_tableBody", [''] , noDataCellFuncs, { 
			rowCreator:function(options) {
		    var row = document.createElement("tr");
		    row.innerHTML = "<td colspan=\"3\" style=\"text-align:center;white-space:nowrap;\"><%= LanguageUtil.get(pageContext,"No-records-to-index") %></td>";		    
		    return row;
		  },
		  escapeHtml:false });		
	}
}
function refreshCache(){
	var x = dijit.byId("cacheStatsCp");
	var y =Math.floor(Math.random()*1123213213);

	<%if(CacheLocator.getCacheAdministrator() instanceof DotGuavaCacheAdministratorImpl){%>
		if(dijit.byId("showSize").checked){
			x.attr( "href","/html/portlet/ext/cmsmaintenance/cachestats_guava.jsp?showSize=true&r=" + y  );
			
		}
		else{
			x.attr( "href","/html/portlet/ext/cmsmaintenance/cachestats_guava.jsp?r=" + y  );
			
		}
	<%}else{%>
		x.attr( "href","/html/portlet/ext/cmsmaintenance/cachestats.jsp?r=" + y  );
	<%}%>
	x.style( "height","600px"  );
	console.log(x);

}

</script>


<html:form styleId="cmsMaintenanceForm" method="POST" action="/ext/cmsmaintenance/view_cms_maintenance" enctype="multipart/form-data">
<input type="hidden" name="userId"  id="userId" value="<%=user.getUserId()%>"> 
<input type="hidden" name="referer" value="<%=referer%>"> 
<input type="hidden" name="cacheName" id="cacheName">
<input type="hidden" name="dataOnly" id="dataOnly">
<input type="hidden" name="cmd" value="">

<div id="mainTabContainer" dojoType="dijit.layout.TabContainer" dolayout="false">

	<!-- START Cache TAB -->
	<div id="cache" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Cache") %>" >

		

		
		
		
		
		<table class="listingTable shadowBox">
			<tr>
				<th colspan="2"><%= LanguageUtil.get(pageContext,"Cache") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td align="center">
			        <select name="cName" dojoType="dijit.form.ComboBox" autocomplete="true" value="<%= LanguageUtil.get(pageContext,"Flush-All-Caches") %>">
						<option selected="selected" value="all"><%= LanguageUtil.get(pageContext,"Flush-All-Caches") %></option>
						<% for(Object c : CacheLocator.getCacheIndexes()){ %>
							<option><%= c.toString() %></option>	
						<% } %>
					</select>
					<button dojoType="dijit.form.Button" onClick="submitform('flushCache');" iconClass="resetIcon">
		             <%= LanguageUtil.get(pageContext,"Flush-All-Caches") %>
		       		</button>
		        </td>
			</tr>
			<tr>
				<th colspan="2"><%= LanguageUtil.get(pageContext,"Menus-File-Store") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td colspan="2">&nbsp;</td>
				<td align="center">
		            <button dojoType="dijit.form.Button"  onClick="submitform('<%=com.dotmarketing.util.WebKeys.Cache.CACHE_MENU_FILES%>');" iconClass="deleteIcon">
		               <%= LanguageUtil.get(pageContext,"Delete-Menu-Cache") %>
		            </button>
		        </td>
			</tr>
			<tr>
				<th colspan="3"><%= LanguageUtil.get(pageContext,"Cache-Stats") %></th>
			</tr>
			<tr>
				<td colspan="3">
					<div class="buttonRow" style="text-align: right">
		            <label for="showSize">
		            <%= LanguageUtil.get(pageContext,"Show-Memory-Size") %>: <input type="checkbox" value="true" dojoType="dijit.form.CheckBox" name="showSize" id="showSize" />
		            </label>
		            <button dojoType="dijit.form.Button"  onClick="refreshCache()" iconClass="reloadIcon">
		               <%= LanguageUtil.get(pageContext,"Refresh-Stats") %>
		            </button>
					</div>
					<div id="cacheStatsCp" dojoType="dijit.layout.ContentPane" style="text-align: center;min-height: 100px;">
						
						
		<div style="padding-bottom:30px;">
	
	
			<table class="listingTable shadowBox" style="width:400px">
				<tr>
					<th><%= LanguageUtil.get(pageContext, "Total-Memory-Available") %></th>
					<td align="right"><%=UtilMethods.prettyByteify( Runtime.getRuntime().maxMemory())%> </td>
				</tr>
				<tr>
					<th><%= LanguageUtil.get(pageContext, "Memory-Allocated") %></th>
					<td align="right"><%= UtilMethods.prettyByteify( Runtime.getRuntime().totalMemory())%></td>
				</tr>
				<tr>
					<th><%= LanguageUtil.get(pageContext, "Filled-Memory") %></th>
					<td align="right"><%= UtilMethods.prettyByteify( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())%></td>
				</tr>
				<tr>
					<th><%= LanguageUtil.get(pageContext, "Free-Memory") %></th>
					<td align="right"><%= UtilMethods.prettyByteify( Runtime.getRuntime().freeMemory())%></td>
				</tr>
			</table>
			<div class="clear"></div>
		</div>
		
						
						<a href="#" onclick="refreshCache()"><%= LanguageUtil.get(pageContext,"Refresh-Stats") %></a>
					



					</div>


				</td>
			</tr>
		</table>
	</div>
	
	<!-- START Index TAB -->
	<div id="index" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Index") %>" >	
		<div style="height:20px">&nbsp;</div>
		
		<div class="indexActionsDiv">
			<table class="listingTable">
				<tr>
					<th colspan="2"><%= LanguageUtil.get(pageContext,"Content-Index-Tasks") %></th>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<div id="currentIndexDirDiv"></div>
					</td>
				</tr>
				<tr>
					<td>
						<div id="lastIndexationDiv"></div>
			
							<%= LanguageUtil.get(pageContext,"Reindex") %>:
							<select id="structure" dojoType="dijit.form.ComboBox" style="width:250px;" autocomplete="true" name="structure" onchange="indexStructureChanged();">
								<option><%= LanguageUtil.get(pageContext,"Rebuild-Whole-Index") %></option>
								<%
									List<Structure> structures = CMF.getStructures();
									for(Structure structure : structures){%>
									<option><%=structure.getVelocityVarName()%></option>
								<%}%>
							</select>
			
					</td>
					<td style="text-align:center;white-space:nowrap;" width="350">
			            <button dojoType="dijit.form.Button"  iconClass="reindexIcon" onClick="submitform('<%=com.dotmarketing.util.WebKeys.Cache.CACHE_CONTENTS_INDEX%>');return false;">
			                <%= LanguageUtil.get(pageContext,"Reindex-Structure(s)") %>
			            </button>
			            <button dojoType="dijit.form.Button"  iconClass="reindexIcon" onClick="cleanReindexStructure();return false;" id="cleanReindexButton">
			                <%= LanguageUtil.get(pageContext,"Delete-Reindex-Structure") %>
			            </button>
					</td>
				</tr>
				<tr>
					<td>
							<%= LanguageUtil.get(pageContext,"Shrink-Index") %> (<%= LanguageUtil.get(pageContext,"this-action-does-not-speed-up-searches") %> )
						<!-- ENDRe-Index Progress Display -->
					</td>
					<td align="center">
			        	<button dojoType="dijit.form.Button"  iconClass="shrinkIcon" onClick="submitform('<%=com.dotmarketing.util.WebKeys.Cache.CACHE_OPTIMIZE_INDEX%>');">
			            	<%= LanguageUtil.get(pageContext,"Shrink-Index") %>
						</button>
			    	 </td>
				</tr>
			</table>
		</div>
		
		<!-- START Re-Index Progress Display -->		
		<div id="reindexationInProgressDiv" style="display: none">
			<table class="listingTable">
				<tr>
					<th colspan="2"><%= LanguageUtil.get(pageContext,"Content-Index-Tasks") %></th>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<div>
							<%= LanguageUtil.get(pageContext,"A-reindexation-process-is-in-progress") %>
						</div>
						<div id="indexationStartTimeDiv"></div>
						<div id="newIndexDirPathDiv"></div>
						<div style="width:200px" maximum="200" id="reindexProgressBar" progress="0" dojoType="dijit.ProgressBar"></div>
						<div id="indexationProgressDiv"></div>
					</div>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<button dojoType="dijit.form.Button"  iconClass="reindexIcon" onClick="stopReIndexing();return false;">
			                <%= LanguageUtil.get(pageContext,"Stop-Reindexation") %>
			            </button>					
					</td>
				</tr>				
			</table>
		</div>
		
		<div style="height:20px">&nbsp;</div>
		<%= LanguageUtil.get(pageContext,"Reindex-Information") %>
		<div>
			<table class="listingTable" style="width:60%" id="reindexJournalData_table">
				<thead>
					<tr>
						<th><%= LanguageUtil.get(pageContext,"Server") %></th>
						<th><%= LanguageUtil.get(pageContext,"Count") %></th>
						<th><%= LanguageUtil.get(pageContext,"priority") %></th>
					</tr>
				</thead>
				<tbody id="reindexJournalData_tableBody">					
					<tr>
						<td colspan="3" align="center"><%= LanguageUtil.get(pageContext,"No-records-to-index") %></td>
					</tr>
				</tbody>
			</table>
		</div>		
		
		<div style="height:20px">&nbsp;</div>
	</div>			
	
	<!-- START Tools TAB -->
	<div id="tools" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Tools") %>" >
		<div style="height:20px">&nbsp;</div>
		<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext,"Import/Export-dotCMS-Content") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext,"Backup-to-Zip-file") %></td>
				<td style="text-align:center;white-space:nowrap;">
		            <button dojoType="dijit.form.Button" onClick="doCreateZip('true');" iconClass="backupIcon">
		               <%= LanguageUtil.get(pageContext,"Backup-Data-Only") %>
		            </button>
		            <button dojoType="dijit.form.Button" onClick="doCreateZip('false');" iconClass="backupIcon">
		              <%= LanguageUtil.get(pageContext,"Backup-Data/Assets") %>
		            </button>
				</td>
			</tr>
			<tr>
				<td><%= LanguageUtil.get(pageContext,"Download-Zip-file") %></td>
				<td style="text-align:center;white-space:nowrap;">
		            <button dojoType="dijit.form.Button" onClick="doDownloadZip('true');" iconClass="downloadIcon">
		               <%= LanguageUtil.get(pageContext,"Download-Data-Only") %>
		            </button>
		            
		            <button dojoType="dijit.form.Button" onClick="doDownloadZip('false');" iconClass="downloadIcon">
		              <%= LanguageUtil.get(pageContext,"Download-Data/Assets") %>
		            </button>
				</td>
			</tr>
		</table>
		
		<div style="height:20px">&nbsp;</div>			
		
		<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext,"Search-And-Replace-Utility") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td>
					<p><%= LanguageUtil.get(pageContext,"This-utility-will-do-a-find-and-replace") %></p>
					<%= LanguageUtil.get(pageContext,"Please-specify-the-following-parameters-and-click-replace") %>:
					<dl>
						<dt><%= LanguageUtil.get(pageContext,"String-to-find") %>:</dt>
						<dd><input type="text" name="searchString" id="searchString" size="50"></dd>
						
			    		<dt><%= LanguageUtil.get(pageContext,"Replace-with") %>:</dt>
						<dd><input type="text" name="replaceString" id="replaceString" size="50"></dd>
					</dl>
				</td>
				<td align="center" valing="middle">
		            <button dojoType="dijit.form.Button" onclick="doReplace();" iconClass="reorderIcon">
		               <%= LanguageUtil.get(pageContext,"Replace") %>
		            </button>
		        </td>
			</tr>
		</table>
		
		<div style="height:20px">&nbsp;</div>			
		
		<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext,"Fix-Assets-Inconsistencies") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td>
					<p><%= LanguageUtil.get(pageContext,"This-utility-will-fix-assets-inconsistencies") %></p>
					<p style="color:#ff0000;"><%= LanguageUtil.get(pageContext,"It's-recommended-to-have-a-fresh") %></p>
			    	<div align="center" id="fixAssetsMessage"></div>
					<%= LanguageUtil.get(pageContext,"Fix-Assets-Inconsistencies") %>
					<div align="center" id="fixAssetInfo"></div>
					<div align="center" id="fixAssetTime"></div>
				</td>
				<td align="center">
					<div id="fixAssetsButtonDiv">
		                <button dojoType="dijit.form.Button" id="fixAssetsButton"  onClick="doFixAssetsInconsistencies();" iconClass="fixIcon">
		                    <%= LanguageUtil.get(pageContext,"Execute") %>
		                </button>
					</div>
				</td>
			</tr>
		</table>
		
		<div style="height:20px">&nbsp;</div>			
		
		<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext,"Drop-Old-Assets-Versions") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td>
					<p><%= LanguageUtil.get(pageContext,"This-utility-will-remove-old-versions-of-contentlets") %></p>
					<div align="center"  id="dropAssetsMessage">&nbsp;</div>
					<dl>
						<dt><%= LanguageUtil.get(pageContext,"Remove-assets-older-than") %>:</dt>
						<dd>
							<input type="text" name="removeassetsdate" id="removeassetsdate" maxlength="10" size="8"> 
							<span class="calMonthIcon" id="<portlet:namespace />calendar_input_0_button" onClick="<portlet:namespace />calendarOnClick_0();"></span> (mm/dd/yyyy)
						</dd>
						<dd style="color:#ff0000;"><%= LanguageUtil.get(pageContext,"It's-recommended-to-have-a-fresh") %></dd>
					</dl>
			    </td>
				<td align="center">
		          <button dojoType="dijit.form.Button" onClick="doDropAssets();"  id="dropAssetsButton" iconClass="dropIcon">
		             <%= LanguageUtil.get(pageContext,"Execute") %>
		          </button>
		        </td>
			</tr>
		</table>
		
		<div style="height:20px">&nbsp;</div>			
		
		<table class="listingTable">
			<tr>
				<th><%= LanguageUtil.get(pageContext,"Delete-Contentlets") %></th>
				<th style="text-align:center;white-space:nowrap;" width="350"><%= LanguageUtil.get(pageContext,"Action") %></th>
			</tr>
			<tr>
				<td>
					<p><%= LanguageUtil.get(pageContext,"This-utility-will-remove-contentlets-from-a-list-of-comma-separated-identifier") %></p>
					<div align="center"  id="deleteContentletMessage"></div>
					<dl>
						<dt><%= LanguageUtil.get(pageContext,"Place-list-here") %>:</dt>
						<dd>
						<textarea style="width:350px" name="contentIdsList" id="contentIdsList">
						</textarea>
						</dd>
					</dl>
			    </td>
				<td align="center">
		          <button dojoType="dijit.form.Button" onClick="doDeleteContentlets();"  id="deleteContentletButton" iconClass="deleteIcon">
		             <%= LanguageUtil.get(pageContext,"Execute") %>
		          </button>
		        </td>
			</tr>
			
		</table>
	</div>
	<div id="Logging" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "Log-Files") %>" >	
		<div style="height:20px">&nbsp;</div>
		<div style="margin-bottom:10px;height:500px;border:0px solid red">
			<iframe style="margin-bottom:10px;height:500px;width:100%;border:0px;" id="_logFileInclude" src="/html/portlet/ext/cmsmaintenance/tail_log.jsp" style=""></iframe>
		</div>
	</div>
	
	<div id="systemProps" dojoType="dijit.layout.ContentPane" title="<%= LanguageUtil.get(pageContext, "System-Properties") %>" >
	
	<table class="listingTable shadowBox" style="width:600px !important;">
        <thead>
        	<th>
				<%= LanguageUtil.get(pageContext, "Env-Variable") %>
        		
        	</th>
        	<th>
        		<%= LanguageUtil.get(pageContext, "Value") %>
        	</th>
        </thead>
        
        <%Map<String,String> s = System.getenv();%>
        <%for(Object key : s.keySet()){ %>
			<tr>
				<td valign="top"><%=key %></td>
				<td style="white-space: normal;word-wrap: break-word;"><%=s.get(key) %></td>
			</tr>

		<%} %>
	</table>
		
	<table class="listingTable shadowBox" style="width:600px !important;">
        <thead>
        	<th>
				<%= LanguageUtil.get(pageContext, "System-Property") %>
        		
        	</th>
        	<th>
        		<%= LanguageUtil.get(pageContext, "Value") %>
        	</th>
        </thead>
        
        <%Properties p = System.getProperties();%>
        <% RuntimeMXBean b = ManagementFactory.getRuntimeMXBean(); %>
        <tr>
        <td valign="top" style="vertical-align: top">Startup Args</td>
		<td valign="top" style="vertical-align: top">
       		<%for(Object key : b.getInputArguments()){ %>
				<%=key %><br>
			<%} %>
        </td>
			</tr>
       
		<%for(Object key : p.keySet()){ %>
		
			<tr>
				<td><%=key %></td>
				<td style="white-space: normal;word-wrap: break-word;"><%=p.get(key) %></td>
			</tr>

		<%} %>
        </table>
        
	</div>

	</div>
	
</html:form>

<script language="Javascript">
	dojo.addOnLoad (function(){
		checkReindexation();
		checkFixAsset();
		indexStructureChanged();
		viewReindexJournalData();
		setInterval("viewReindexJournalData();",60000);
	});	
</script>

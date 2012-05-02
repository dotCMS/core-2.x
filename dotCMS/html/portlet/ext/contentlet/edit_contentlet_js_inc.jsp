<%@page import="java.util.Random"%>
<%@ page import="com.dotmarketing.util.UtilMethods" %>
<%@ page import="com.liferay.portal.language.LanguageUtil"%>
<%@page import="java.util.*"%>

<script language='javascript' type='text/javascript'>

	//http://jira.dotmarketing.net/browse/DOTCMS-2273
	var workingContentletInode = "<%= contentlet.getInode() %>";
	var currentContentletInode = "";
	var isAutoSave = false;
	var isCheckin = true;
	var isContentAutoSaving = false;
	var isContentSaving = false;
	var doesUserCancelledEdit = false;

	var tabsArray=new Array();

	dojo.require("dijit.Dialog");

	assignToDialog = new dijit.Dialog({
	      id:"assignToDialog"
	  });


	//Tabs manipulation
	function displayProperties(id) {

		for(i =0;i< tabsArray.length ; i++){
			var ele = document.getElementById(tabsArray[i] + "_tab");

			if (ele != undefined) {
				if(tabsArray[i] == id){
				  ele.className = "alpha";
		          document.getElementById(tabsArray[i]).style.display = "";
				}
				else{
                 ele.className = "beta";
				 document.getElementById(tabsArray[i]).style.display = "none";
				}
			}
		}
	}



    var myForm = document.getElementById('fm');
	var copyAsset = false;

	var inode = '<%=contentlet.getInode()%>';
	var referer = '<%=java.net.URLEncoder.encode(referer,"UTF-8")%>';

	function cancelEdit() {

		doesUserCancelledEdit = true;

		if(isContentAutoSaving){ //To avoid storage of contentlet while user cancels.
			setTimeout("cancelEdit()",300);
		}

		var ref = $('<portlet:namespace />referer').value;
		var langId = $('languageId').value;

		ContentletAjax.cancelContentEdit(workingContentletInode,currentContentletInode,ref,langId,cancelEditCallback);
	}
	function cancelEditCallback(callbackData){
		<%if(structure.getStructureType()==Structure.STRUCTURE_TYPE_FORM){%>
		callbackData=callbackData+"&structure_id=<%=structure.getInode()%>";
		<%}%>
		self.location = callbackData;
	}


	//Full delete contentlet action
	function submitfmDelete()
	{
			var href =  '<portlet:actionURL>';
			href +=		'	<portlet:param name="struts_action" value="<%= formAction %>" />';
			href +=		'	<portlet:param name="cmd" value="full_delete" />';
			href +=		'</portlet:actionURL>';

			form = document.getElementById("fm");
			form.action = href;
			form.submit();
	}

	//Versions management
    function deleteVersion(objId){
        if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.contentlet.delete.content.version")) %>')){
			window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="<%= formAction %>" /></portlet:actionURL>&cmd=deleteversion&inode=' + objId  + '&referer=' + referer;
        }
    }
	function selectVersion(objId) {
        if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.contentlet.replace.version")) %>')){
			window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="<%= formAction %>" /></portlet:actionURL>&cmd=getversionback&inode=' + objId + '&inode_version=' + objId  + '&referer=' + referer;
	    }
	}
	function editVersion(objId) {
		window.location = '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="<%= formAction %>" /></portlet:actionURL>&cmd=edit&inode=' + objId  + '&referer=' + referer;
	}



   	function openAssignTo()
   	{
		if(dojo.isFF){
			  assignToDialog._fillContent(dojo.byId('assignTaskInnerDiv'));
			  assignToDialog.show();
   	  	}
   	  	else{
   	  		var win=dijit.byId('assignTaskDiv');

			win.show();
   		}

	}

	function cancelAssignTo() {
		if(dojo.isFF){
			assignToDialog.hide();
   	   	}
   	   	else{
   	   		dijit.byId('assignTaskDiv').hide();
   	   	}
	}

	 //submit form
   	function submitfm(form, subcmd, catCounter)
   	{

   	var isAjaxFileUploading = false;
   	dojo.query(".fileAjaxUploader").forEach(function(node, index, arr){
   		FileAjax.getFileUploadStatus(node.id,{async:false, callback: function(fileStats){
   	   		if(fileStats!=null){
   	   		   isAjaxFileUploading = true;
   	   		}
   	   	}});
   	 });

   	if(isAjaxFileUploading){
   	   alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Please-wait-until-all-files-are-uploaded")) %>');
       return false;
   	}





   	if(doesUserCancelledEdit){
		return false;
   	}

   		if(subcmd == 'assignto') {
   			if (dijit.byId('taskAssignmentAux').value == '') {
   				alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Please-select-a-user-or-role-to-assign-the-task")) %>');
   				return;
   			}
   			dojo.byId('taskAssignment').value =dijit.byId('taskAssignmentAux').value ;
   			dojo.byId('taskComments').value =dojo.byId('taskCommentsAux').value ;
   		}

		// http://jira.dotmarketing.net/browse/DOTCMS-2273
		//if (subcmd != ''){
			$('subcmd').value = subcmd;
		//}
		if(isContentAutoSaving){ // To avoid concurrent auto and normal saving.
			return;
		}
		window.scrollTo(0,0);	// To show lightbox effect(IE) and save content errors.
		dijit.byId('savingContentDialog').show();
	 	var isAutoSave = false;
	 	saveContent(isAutoSave, catCounter);
	 	return;
	}



	//Structure change
  	function structureSelected()
	{
		//migrateSelectedStructure();
		var href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>";
		href += "<portlet:param name='struts_action' value='<%= formAction %>' />";
		href += "<portlet:param name='cmd' value='new' />";
		href += "<portlet:param name='referer' value='<%=referer%>' />";
		//This parameter is used to determine if the selection comes from Content Manager is EditContentletAction
		href += "<portlet:param name='selected' value='true' />";
		href += "<portlet:param name='inode' value='' />";
		href += "</portlet:actionURL>";
		document.forms[0].action = href;
		document.forms[0].submit();
	}


	//Review content changes control
	function reviewChange() {
		var obj = dijit.byId("reviewContent");
		enable = obj.checked;
		<%if (UtilMethods.isSet(structure.getReviewerRole())) {%>

			var reviewContentDate = document.getElementById("reviewContentDate");
			if(reviewContentDate == undefined){
				return;
			}
			var reviewIntervalNum = document.getElementById("reviewIntervalNumId");
			var reviewIntervalSel = document.getElementById("reviewIntervalSelectId");
			if (enable) {
				reviewContentDate.style.display='';
				reviewIntervalNum.disabled = false;
				reviewIntervalSel.disabled = false;
			} else {
				reviewContentDate.style.display='none';
				reviewIntervalNum.disabled = true;
				reviewIntervalSel.disabled = true;
			}

		<%}%>
	}

	//Copy contentlet action
	function copyContentlet()
	{
			var href =  '<portlet:actionURL>';
			href +=		'<portlet:param name="struts_action" value="<%= formAction %>" />';
			href +=		'<portlet:param name="cmd" value="copy" />';
			href +=		'</portlet:actionURL>';

			form = document.getElementById("fm");
			form.action = href;
			form.submit();
	}

	function editContentletURL (objId) {
		var loc = '';
		loc += '<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="<%= formAction %>" /><portlet:param name="cmd" value="edit" /></portlet:actionURL>&inode=' + objId;
		return loc;
	}



    function addTab(tabid){
      tabsArray.push(tabid);
	}


  	var plusSign = '/html/skin/image/01/common/06_plus.gif';
  	var minusSign = '/html/skin/image/01/common/06_minus.gif';
	function toggleSection (whichSection, expandImage) {
		if($(whichSection).style.display != "") {
	  		$(whichSection).style.display = "";
	  		$(expandImage).src = minusSign;
	  	} else {
	  		$(whichSection).style.display = "none";
	  		$(expandImage).src = plusSign;
	  	}
	}

	function submitParent(param) {
		if (copyAsset) {
			disableButtons(myForm);
			var parent = document.getElementById("parent").value;
			self.location = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/contentlet/edit_contentlet" /><portlet:param name="cmd" value="copy" /><portlet:param name="inode" value="<%=String.valueOf(contentlet.getInode())%>" /></portlet:actionURL>&parent=' + parent + '&referer=' + referer;
		}

		//WYSIWYG_CALLBACKS
		if (param == 'wysiwyg_image') {
			var imageName = document.getElementById("selectedwysiwyg_image").value;
			var imageFolder = document.getElementById("folderwysiwyg_image").value;
			var ident = document.getElementById("selectedIdentwysiwyg_image").value;
			wysiwyg_win.document.forms[0].elements[wysiwyg_field_name].value = /dotAsset/ + ident;
			wysiwyg_win.ImageDialog.showPreviewImage("/dotAsset/" + ident);
		}
		if (param == 'wysiwyg_file') {
			var fileName = document.getElementById("selectedwysiwyg_file").value;
			var ident = document.getElementById("selectedIdentwysiwyg_file").value;
			var fileFolder = document.getElementById("folderwysiwyg_file").value;
			<% String ext = com.dotmarketing.util.Config.getStringProperty("VELOCITY_PAGE_EXTENSION"); %>
			var fileExt = getFileExtension(fileName).toString();
			if(fileExt == '<%= ext %>'){
				wysiwyg_win.document.forms[0].elements[wysiwyg_field_name].value = fileFolder + fileName;
			}else{
				wysiwyg_win.document.forms[0].elements[wysiwyg_field_name].value = /dotAsset/ + ident;
			}
		}
	}

	<% if(Config.getIntProperty("CONTENT_AUTOSAVE_INTERVAL",0) > 0){%>
		// http://jira.dotmarketing.net/browse/DOTCMS-2273
		var autoSaveInterval = <%= Config.getIntProperty("CONTENT_AUTOSAVE_INTERVAL",0) %>;
		setInterval("saveContent(true)",autoSaveInterval);
	<%}%>
	function getFormData(formId,nameValueSeparator, catCounter){ // Returns form data as name value pairs with nameValueSeparator.

		var formData = new Array();

		//Taking the text from all the textareas
		var k = 0;
		$(document.getElementById(formId)).getElementsBySelector('textarea').each(
				function (textareaObj) {
					if ((textareaObj.id != "") && (codeMirrorEditors[textareaObj.id] != null)) {
						try {
							document.getElementById(textareaObj.id).value=codeMirrorEditors[textareaObj.id].getCode();
						} catch (e) {
						}
					}
				}
			);

		var formElements = document.getElementById(formId).elements;

		var formDataIndex = 0; // To collect name/values from multi-select,text-areas.

		for(var formElementsIndex = 0; formElementsIndex < formElements.length; formElementsIndex++,formDataIndex++){


			// Collecting only checked radio and checkboxes
			if((formElements[formElementsIndex].type == "radio") && (formElements[formElementsIndex].checked == false)){
				continue;
			}

			if((formElements[formElementsIndex].type == "checkbox") && (formElements[formElementsIndex].checked == false)){
				continue;
			}

			// Collecting selected values from multi select
			if(formElements[formElementsIndex].type == "select-multiple") {
				for(var multiSelectIndex = 0; multiSelectIndex < formElements[formElementsIndex].length; multiSelectIndex++){

					if(formElements[formElementsIndex].options[multiSelectIndex].selected == true){
						formData[formDataIndex] = formElements[formElementsIndex].name+nameValueSeparator+formElements[formElementsIndex].options[multiSelectIndex].value;
						formDataIndex++;
					}
				}
				continue;
			}

			// Getting values from text areas
			if(formElements[formElementsIndex].type == "textarea" && formElements[formElementsIndex].id != '') {
				if(tinyMCE.get(formElements[formElementsIndex].id) != null){
					textAreaData = tinyMCE.get(formElements[formElementsIndex].id).getContent();
					formData[formDataIndex] = formElements[formElementsIndex].name+nameValueSeparator+textAreaData;
					continue;
				}
				if(tinyMCE.get(formElements[formElementsIndex].id) == null){
						textAreaData = document.getElementById(formElements[formElementsIndex].id).value;
						formData[formDataIndex] = formElements[formElementsIndex].name+nameValueSeparator+textAreaData;
						continue;
				}
			}

			formData[formDataIndex] = formElements[formElementsIndex].name+nameValueSeparator+formElements[formElementsIndex].value;
		}

		// Categories selected in the Category Dialog

		for(var i=1; i<catCounter+1; i++) {

			if (typeof eval("addedStore"+i)!="undefined"){
				eval("addedStore"+i).fetch({onComplete:function(items) {
					for (var i = 0; i < items.length; i++){
						formData[formData.length] = "categories"+nameValueSeparator+items[i].id[0];
					   }
				}});
			}

		}


		return formData;
	}

	function saveContent(isAutoSave, catCounter){

		if(isAutoSave && isContentSaving)
			return;

		var textAreaData = "";
		var fmData = new Array();

		fmData = getFormData("fm","<%= com.dotmarketing.util.WebKeys.CONTENTLET_FORM_NAME_VALUE_SEPARATOR %>", catCounter);

		if(isInodeSet(currentContentletInode)){
			isCheckin = false;
		}

		if(isAutoSave){
			isContentAutoSaving = true;
		}

		if(!isAutoSave) {
			isContentSaving = true;
		}
		ContentletAjax.saveContent(fmData,isAutoSave,isCheckin,saveContentCallback);
	}

	function saveContentCallback(data){

		if(data["contentletInode"] != null && isInodeSet(data["contentletInode"])){
			$('contentletInode').value = data["contentletInode"];
			currentContentletInode = data["contentletInode"];
		}

		if(data["saveContentErrors"] != null ){	// To show DotContentletValidationExceptions.
			var errorDisplayElement = dijit.byId('saveContentErrors');
			var exceptionData = data["saveContentErrors"];
			var errorList = "";
				for (var i = 0; i < exceptionData.length; i++) {
					var error = exceptionData[i];
					errorList = errorList+"<li>"+error+"</li>";
				}
			dojo.byId('exceptionData').innerHTML = "<ul>"+errorList+"</ul>";
			dijit.byId('savingContentDialog').hide();
			errorDisplayElement.show();
		}else{
			if(data["referer"] != null && data["referer"] != '' && !isContentAutoSaving) {
				<%if(structure.getStructureType()==Structure.STRUCTURE_TYPE_FORM){%>
				self.location = data["referer"]+"&structure_id=<%=structure.getInode()%>&content_inode=" + data["contentletInode"];
				<%}else{%>
				self.location = data["referer"] + "&content_inode=" + data["contentletInode"];
				<%}%>
			}
		}
		isContentAutoSaving = false;
	}
	function hideEditButtonsRow() {
		dojo.style('editContentletButtonRow', { display: 'none' });
	}

	function showEditButtonsRow() {
		if( typeof changesMadeToPermissions!= "undefined"){
			if(changesMadeToPermissions == true){
				dijit.byId('applyPermissionsChangesDialog').show();
			}
		}
		dojo.style('editContentletButtonRow', { display: '' });
		changesMadeToPermissions = false;
	}
</script>
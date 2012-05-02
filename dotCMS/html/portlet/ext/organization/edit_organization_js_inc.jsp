function submitfm(form) {
	form.<portlet:namespace />cmd.value = '<%=Constants.ADD%>';
	form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/organization/edit_organization" /></portlet:actionURL>';
	form.<portlet:namespace />redirect.value = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/organization/view_organizations" /></portlet:renderURL>';
	submitForm(form);
}

function cancelEdit() {
	self.location = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/organization/view_organizations" /></portlet:renderURL>';
}

function deleteOrganization(form) {
	if(confirm("Are you sure you want to delete this organization (this cannot be undone) ?")){
		form.<portlet:namespace />cmd.value = '<%=Constants.DELETE%>';
		form.<portlet:namespace />redirect.value = '<portlet:renderURL><portlet:param name="struts_action" value="/ext/organization/view_organizations" /></portlet:renderURL>';
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/organization/edit_organization" /></portlet:actionURL>';
		submitForm(form);
	}
}
function smallPopUp(event)
{
	if (isLeftClick(event))
	{
		showMenuPopUp("logoImageDiv", event);
	}
}

function browseTreeThumbNails(content,popup) 
{
   view = "<%= java.net.URLEncoder.encode("(working=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + ")","UTF-8") %>";
}
function submitParent()
{  
	//Display small and large images  
   logoImagePath = document.getElementById("logoImage").value;
   var logoImage = document.getElementById("logoImagePreview");
   
   if (logoImagePath != null && trimAll(isInodeSet(logoImagePath)))
   {
	  	logoImage.src = "/thumbnail?inode=" + logoImagePath + "&w=100&h=100";   
   }
   else {
	  	logoImage.src = "/portal/images/shim.gif";   
   }
}
function popupFileUpload(inode) 
{
    newfilewin = window.open('<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/files/edit_file" /><portlet:param name="cmd" value="edit" /><portlet:param name="popup" value="1" /></portlet:actionURL>&inode=' + inode + '&child=true&page_width=650', "newfilewin", 'width=700,height=400,scrollbars=yes,resizable=yes');
}
function setImage(inode,name) 
{
   var form = document.forms[0];
   form.logoImage.value  = inode;
   form.selectedlogoImage.value = name;
   submitParent();	
}

function deletelogoImage()
{
	var logoImagePath = document.getElementById("logoImage")
	var logoImage = document.getElementById('logoImagePreview');
	logoImagePath.value = "";
	logoImage.src = "/portal/images/shim.gif";
}


function trimAll(sString) 
{
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	return sString;
}

/**************** AJAX FUNCTIONS ******************************/
function selectNewParent() {
	if ($("parentSystem") != null) {
		var parentSystem = DWRUtil.getValue("parentSystem");
		if (parentSystem!=0) {
			OrganizationAjax.getOrganizationMap(functRet, parentSystem);
		}
	}
}

var functRet = function selectNewParentReturn (data) {
	if (data != null) {
		var partnerURL = data["partnerUrl"];
		DWRUtil.setValue("partnerUrl",partnerURL);
		var partnerKey = data["partnerKey"];
		DWRUtil.setValue("partnerKey",partnerKey);
		var logoImage = data["logoImage"];
		DWRUtil.setValue("logoImage",logoImage);
		submitParent();
	}
	else {
		DWRUtil.setValue("partnerUrl","");
		DWRUtil.setValue("partnerKey","");
		DWRUtil.setValue("logoImage",0);
		submitParent();
	
	}
}



function generatePartnerKey() {
	var orgName = DWRUtil.getValue("title");
	if (orgName!='') {
		OrganizationAjax.generatePartnerKey (generatePartnerKeyRet, orgName);
	}
}

function generatePartnerKeyRet(data) {
	DWRUtil.setValue("partnerKey", data);
}
/**************** AJAX FUNCTIONS ******************************/

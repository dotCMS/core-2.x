<%@ include file="/html/portlet/ext/files/init.jsp" %>
<%@ page import="com.dotmarketing.util.UtilMethods"%>
<%@ page import="com.liferay.portal.language.LanguageUtil"%>
<%@ page import="com.dotmarketing.util.UtilMethods"%>
<%@ page import="com.liferay.portal.language.LanguageUtil"%>
<%@ page import="com.dotmarketing.beans.Host" %>
<%@ page import="com.dotmarketing.portlets.files.factories.FileFactory"%>
<%@ page import="com.dotmarketing.portlets.files.model.File"%>
<%@ page import="com.dotmarketing.business.APILocator"%>
<%@ page import="com.dotmarketing.business.PermissionAPI"%>
<%@ page import="com.dotmarketing.business.Role"%>
<%@page import="com.dotmarketing.portlets.files.business.FileAPI"%>
<%@page import="com.dotmarketing.portlets.folders.business.FolderAPI"%>
<%@ page import="com.dotmarketing.util.*" %>
<% 
//gets referer
String referer = (request.getParameter("referer") != null ) ? request.getParameter("referer") : "" ;

PermissionAPI perAPI = APILocator.getPermissionAPI();
FileAPI fileAPI = APILocator.getFileAPI();
FolderAPI folderAPI = APILocator.getFolderAPI();
com.dotmarketing.portlets.files.model.File file;
if (request.getAttribute(com.dotmarketing.util.WebKeys.FILE_EDIT)!=null) {
	file = (com.dotmarketing.portlets.files.model.File) request.getAttribute(com.dotmarketing.util.WebKeys.FILE_EDIT);
}
else {
	file = (com.dotmarketing.portlets.files.model.File) com.dotmarketing.factories.InodeFactory.getInode(request.getParameter("inode"), com.dotmarketing.portlets.files.model.File.class);
}
//gets parent identifier to get the categories selected for this file
com.dotmarketing.beans.Identifier identifier = com.dotmarketing.factories.IdentifierFactory.getParentIdentifier(file);

//gets parent folder
com.dotmarketing.portlets.folders.model.Folder folder = (com.dotmarketing.portlets.folders.model.Folder) folderAPI.find(file.getParent());

//The host of the file
Host host = folder != null?APILocator.getHostAPI().findParentHost(folder, APILocator.getUserAPI().getSystemUser(), false):null;

com.dotmarketing.portlets.folders.model.Folder parentFolder = (com.dotmarketing.portlets.folders.model.Folder) request.getAttribute("PARENT_FOLDER");
int countFiles = 1;
if (request.getParameter("countFiles")!=null) {
	countFiles = Integer.parseInt(request.getParameter("countFiles"));
}


boolean hasOwnerRole = com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user,com.dotmarketing.business.APILocator.getRoleAPI().loadCMSOwnerRole().getId());
boolean ownerHasPubPermission = (hasOwnerRole && perAPI.doesRoleHavePermission(file, PermissionAPI.PERMISSION_PUBLISH,com.dotmarketing.business.APILocator.getRoleAPI().loadCMSOwnerRole()));
boolean ownerHasWritePermission = (hasOwnerRole && perAPI.doesRoleHavePermission(file, PermissionAPI.PERMISSION_WRITE,com.dotmarketing.business.APILocator.getRoleAPI().loadCMSOwnerRole()));
boolean hasAdminRole = com.dotmarketing.business.APILocator.getRoleAPI().doesUserHaveRole(user,com.dotmarketing.business.APILocator.getRoleAPI().loadCMSAdminRole());
boolean canUserWriteToFile = ownerHasWritePermission || hasAdminRole || perAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_WRITE, user, false);
boolean canUserPublishFile = ownerHasPubPermission || hasAdminRole || perAPI.doesUserHavePermission(file, PermissionAPI.PERMISSION_PUBLISH, user, false) || perAPI.doesUserHavePermission(folder, PermissionAPI.PERMISSION_EDIT, user, false);

%>

<script>
	var countFiles = <%=countFiles%>;

    function doUpload(subcmd){

  

        var form = document.getElementById("fm");

		if (form.categorySelect) {
	        for (i=0;i<form.categorySelect.options.length;i++) {
	        	if (form.categorySelect.options[i].selected) {
	        		form.<portlet:namespace />categories.value += form.categorySelect.options[i].value + ",";
	        	}	
	        }
	    }
		var oneFileUploaded = false;
		for (i=0;i<countFiles;i++) {
	        x = document.getElementById("<portlet:namespace />uploadedFile"+i).value.split("\\");
	        var fileName = x[x.length -1];
	
	        while(fileName.indexOf(" ") > -1){
	            fileName = fileName.replace(" ", "");
	        }
	
	        if (fileName.length ==0 && !oneFileUploaded) {
	    		alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "message.file_asset.alert.please.upload")) %>');
	    		return false;
	        }
	        oneFileUploaded = true;
	        document.getElementById("<portlet:namespace />fileName"+i).value = fileName;
		}
		
        document.getElementById("tableDiv").style.display = "none";
        document.getElementById("messageDiv").style.display = "";

		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/files/upload_multiple" /></portlet:actionURL>';
        form.<portlet:namespace />subcmd.value = subcmd;
        form.<portlet:namespace />cmd.value="<%= Constants.ADD %>";
      	dijit.byId('saveButton').setAttribute('disabled',true);	
    	if(dijit.byId('savePublishButton')!=null){
        	dijit.byId('savePublishButton').setAttribute('disabled',true);
    	}	
        submitForm(form);
    }

	function beLazy(i){
		var ele = document.getElementById("friendlyNameField" + i);
		if(ele.value.length ==0 ){
			ele.value = document.getElementById("titleField"+ i).value;
		}
	}
	function beLazier(k){

			var ele = document.getElementById("<portlet:namespace />uploadedFile"+k).value;

			var arr = ele.split("\\");
			if(arr.length ==1){
						  var arr = ele.split("/");
			}
			val = arr[(arr.length -1)];
			var test = false;
			var newVal = "";
			for(i=0 ; i < val.length ; i++){
				var c =     val.substring(i,i+1);
				 if(c == "_") {
				 	  c = " ";
				}
				if(test == true || i == 0){
					 test = false;
				     c = c.toUpperCase();
				}
				if(c == " "){
                 	 test = true;
				}


			    if(c == ".") break;
				newVal = newVal +   c;
			}
			
			var ele = document.getElementById("titleField"+k);
			if(ele.value.length ==0 ){
				ele.value = newVal;
			}
	        beLazy(k);
	}
	function changeCountFiles(dropDown) {
		
		var form = document.getElementById("fm");
		form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/files/upload_multiple" /></portlet:actionURL>';
        form.<portlet:namespace />subcmd.value = "";
        form.<portlet:namespace />cmd.value="<%= Constants.EDIT %>";
        submitForm(form);
	
	}

</script>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"file-upload\") %>" />
   
       <body class="Body" onLoad="this.focus()" leftmargin="0" topmargin="0" marginheight="0">


					 <div id="tableDiv" style="display: ; position:relative; z-index: 100">

                                      

							 <html:form action="/ext/files/upload_multiple" method="POST"  styleId="fm" enctype="multipart/form-data" onsubmit="return false;">
                                           
                                           <fieldset>
  
							 <input type="hidden" name="<portlet:namespace />cmd" value="<%=Constants.ADD%>">
							 <input type="hidden" name="<portlet:namespace />subcmd" value="">
							 <input type="hidden" name="<portlet:namespace />redirect" value="<portlet:renderURL><portlet:param name="struts_action" value="/ext/files/view_files" /></portlet:renderURL>">
			 				 <input type="hidden" name="<portlet:namespace />categories" value="">
							 
							 <html:hidden property="maxSize" />
							 <html:hidden property="maxWidth" />
							 <html:hidden property="maxHeight" />
							 <html:hidden property="minHeight" />

							 <input type="hidden" name="userId" value="<%= user.getUserId() %>">
							 <input name="<portlet:namespace />referer" type="hidden" value="<%= referer %>">

                                           <dl>

								<dt><%= LanguageUtil.get(pageContext, "Folder") %>:</dt>
                                                <dd>
									<html:hidden property="selectedparent" styleId="selectedparent" />
									<html:text readonly="true" style="width:100%;border:0px;" styleClass="form-text" property="selectedparentPath" styleId="selectedparentPath" />
									<html:hidden styleClass="form-text" property="parent" styleId="parent" />
                                                 
                                                  </dd> 
								  
                                                 <dt><%= LanguageUtil.get(pageContext, "Number-of-files-to-upload") %>:</dt>
												
								<dd>
								   <select name="countFiles" onChange="changeCountFiles(this)">
									<% for (int k=1;k<20;k++) {%>
									<option <%=(countFiles==k)?"selected":""%> value="<%=k%>"><%=k%></option>
									<%}%>
								    </select>
                                                 </dd> 
													
							
							<% for (int i=0;i<countFiles;i++) { %>
							
								 <dt><%= LanguageUtil.get(pageContext, "Upload-New-File") %> #<%=i+1%>:</dd>
								 <dd>
                                              <input type="file" class="form-text" style="width:350" name="<portlet:namespace />uploadedFile<%=i%>" id="<portlet:namespace />uploadedFile<%=i%>" onBlur="beLazier(<%=i%>);">
							 
							<input type="hidden" style="width:70%;" class="form-text" name="title<%=i%>"  id="titleField<%=i%>" onchange="beLazy(<%=i%>);" />
							<input type="hidden" style="width:70%;" class="form-text" name="friendlyName<%=i%>" id="friendlyNameField<%=i%>" />
						    <input type="hidden" name="<portlet:namespace />fileName<%=i%>" id="<portlet:namespace />fileName<%=i%>" value="">
							<% } %>
                                               </dd>
							
						    
						     
					  <dt>&nbsp;</dt>	
	                 <%if (!InodeUtils.isSet(file.getInode()) && UtilMethods.isSet(folder)) {
		                   	if(!InodeUtils.isSet(file.getInode())) {
			                 	canUserWriteToFile = perAPI.doesUserHavePermission(folder,PermissionAPI.PERMISSION_CAN_ADD_CHILDREN,user);
			                }
	                    }
                     %>
                                <dd>		
                                <%if (canUserPublishFile) {%>
				                 <button dojoType="dijit.form.Button" onClick="doUpload('')" iconClass="saveIcon" id="saveButton">
									<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>
                                </button>
                                <button dojoType="dijit.form.Button" onClick="doUpload('publish')" iconClass="publishIcon" id="savePublishButton">
                                   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save-and-publish")) %>
                                 </button>    
			                   <%} else if (canUserWriteToFile) { %>
				                <button dojoType="dijit.form.Button" onClick="doUpload('')" iconClass="saveIcon" id="saveButton">
									<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "save")) %>
                                </button>
			                     <% } %>
                                <button dojoType="dijit.form.Button" onClick="javascript:history.back()" iconClass="cancelIcon">
                                   <%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "cancel")) %>
                                </button>
                                 </dd>
                                
                               </dl>
                               </div>

					</html:form>
					
					</div>

					<div id="messageDiv" class="messageBox shadowBox" style="display: none;">
						
						<b><%= LanguageUtil.get(pageContext, "File-Uploading") %>  . . .</b><BR>  <%= LanguageUtil.get(pageContext, "Note") %>: <%= LanguageUtil.get(pageContext, "This-window-will-redirect-you-back-when-the-file-has-been-uploaded") %>
						
					</DIV>

	</body>
</liferay:box>


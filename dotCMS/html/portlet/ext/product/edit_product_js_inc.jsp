<%@ page import="com.dotmarketing.util.*" %>
<%@ page import="com.liferay.portal.language.LanguageUtil" %>

	dojo.require('dotcms.dijit.form.FileSelector');

	function deleteProduct(){
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Do-you-want-to-delete-this-Event")) %>')){
	        form = document.getElementById("webEventForm");
			document.getElementById("webEventForm").action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/webevents/edit_event" /><portlet:param name="cmd" value="delete" /><portlet:param name="inode" value="<%=String.valueOf(productForm.getInode())%>" /></portlet:actionURL>";
			form.submit();
		}
	}
	
	function relateProduct()
	{
	<%if (!InodeUtils.isSet(product.getInode())){ %>
		alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "You-Must-Save-the-Product-to-Add-a-Related-Product")) %>');
	<%}else{%>
		href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>
					<portlet:param name='struts_action' value='/ext/product/relate_product' />
					<portlet:param name='inode' value='<%=product.getInode()%>' />
					<portlet:param name='referrer' value='<%=referrer + "#related"%>' />
				</portlet:actionURL>";
		document.location.href = href;
	<%}%>
	}
	
	function unrelateProduct(href)
	{
		document.location.href = href;
	}
	
	function addFormat()
	{
		<%if (!InodeUtils.isSet(product.getInode())){ %>
			alert('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "You-Must-Save-the-Product-to-Add-Formats")) %>');
		<%}else{%>
			href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>
						<portlet:param name='struts_action' value='/ext/product/edit_format' />
						<portlet:param name='productInode' value='<%=product.getInode()%>' />
						<portlet:param name='referrer' value='<%=referrer + "#formats"%>' />
					</portlet:actionURL>";
			document.location.href = href;
		<%}%>
	}
	
	function saveProduct()
	{
		document.getElementById("dispatch").value = "save";
		var form = document.getElementById("ProductForm");
        form.action = "<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/product/edit_product" /><portlet:param name="cmd" value="save" /><portlet:param name="inode" value="<%=String.valueOf(productForm.getInode())%>" /></portlet:actionURL>";
		form.fileIds.value = files.join(",");
		setWysiwygValues(form);
		form.submit();
	}
	
	function cancel()
	{  
		document.location.href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'><portlet:param name='struts_action' value='/ext/product/view_product' /></portlet:actionURL>";
	}
	
	
	function editFormat(inode)
	{
		href = "<portlet:actionURL windowState='<%= WindowState.MAXIMIZED.toString() %>'>
					<portlet:param name='struts_action' value='/ext/product/edit_format' />
					
				<portlet:param name='referrer' value='<%=referrer%>' /></portlet:actionURL>";
		href += "&inode=" + inode;		
		document.location.href = href;
	}
	
	function deleteFormat(inode)
	{
		if(confirm('<%= UtilMethods.escapeSingleQuotes(LanguageUtil.get(pageContext, "Do-you-want-to-delete-this-Price-Variant")) %>')){
			href = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>
						<portlet:param name='struts_action' value='/ext/product/edit_format' />
						<portlet:param name='cmd' value='<%=com.liferay.portal.util.Constants.DELETE%>' />
						<portlet:param name='referrer' value='<%=referrer%>' />
					</portlet:actionURL>";
			href += "&inode=" + inode	
			document.location.href = href;
		}
	}
	
	function copyFormat(inode)
	{
		href = "<portlet:actionURL windowState='<%=WindowState.MAXIMIZED.toString()%>'>
					<portlet:param name='struts_action' value='/ext/product/edit_format' />
					<portlet:param name='cmd' value='<%=com.dotmarketing.util.Constants.COPY%>' />
					<portlet:param name='referrer' value='<%=referrer%>' />
				</portlet:actionURL>";
		href += "&inode=" + inode;
		document.location.href = href;
	}			
		
	//File Attachment Functions   
	var files = new Array();
	
	function addFile (file) {
		var identifier = file.identifier;
		if (isInodeSet(identifier)) { 
			var fileName = file.fileName;
			var table = document.getElementById('filesTable');
			var row = table.insertRow(table.rows.length);
			var fileNameTD=row.insertCell(0);
			fileNameTD.innerHTML="<img src='/icon?i=" + fileName + "' width='16' height='16' align=absmiddle border=0 vspace=1 hspace=1> " + fileName + 
				" - <a href='javascript:removeFile(\"" + identifier + "\")'><%= LanguageUtil.get(pageContext, "remove") %></a>";
			row.align="left";
			files[files.length]	= identifier;
			document.getElementById('noFilesTable').style.display = "none";
		}
	}
	
	function removeFile(identifier)
	{
		var idx = dojo.indexOf(files, identifier);
		var del = false;
		
		if(idx >= 0) {
			files.splice(idx, 1);
			document.getElementById('filesTable').deleteRow(idx);
		}
		
		if (files.length == 0) document.getElementById('noFilesTable').style.display = "block";

	}
	//End File Attachment Functions   
	
	
	  function trimAll(sString) 
	  {
		sString = new String(sString);
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
	  
	  function trimZero(sString) 
	  {
		sString = new String(sString);
		if (sString.substring(0,1) == '0')
		{
			sString = sString.substring(1, sString.length);
		}
		return sString;
	  }
	  	  

	  function deleteFile(file)
	  {
	  	var form = document.getElementById("ProductForm");
	  	eval("form." + file + ".value = ''");
	  	eval("form." + file + "File.value = ''");
	 	eval("form.selected" + file + ".value = ''");
	  }

	  <!-- END Methods to set the images && files -->
	  
		/* WYSIWYG JAVASCRIPT */
   	  /**** WYSIWYG EDITOR / TEXTAREA JAVASCRIPT CODE BEGINS ****/
		  var shortDescription;
		  var editShortDescriptionGenerated = false;
		  var longDescription;
		  var editLongDescriptionGenerated = false;
		  var comments;
		  var editCommentsGenerated = false;		 		  
   	  /**** WYSIWYG EDITOR / TEXTAREA JAVASCRIPT CODE ENDS ****/

     function setMode(editorNum, mode) {
		  	if (editorNum==1) {
		  		if (!editShortDescriptionGenerated) {
		    	    shortDescription.registerPlugin(ContextMenu);
		  			shortDescription.generate();
			  		editShortDescriptionGenerated = true;
		  		}
		  		else {
				  	shortDescription.setMode(mode);
				  	shortDescription.updateToolbar();
				}
			}
		  	if (editorNum==2) {
		  		if (!editLongDescriptionGenerated) {
		    	    longDescription.registerPlugin(ContextMenu);
		  			longDescription.generate();
			  		editLongDescriptionGenerated = true;
		  		}
		  		else {
				  	longDescription.setMode(mode);
				  	longDescription.updateToolbar();
				}
			}
			if (editorNum==3) {
		  		if (!editCommentsGenerated) {
		    	    comments.registerPlugin(ContextMenu);
		  			comments.generate();
			  		editCommentsGenerated = true;
		  		}
		  		else {
				  	comments.setMode(mode);
				  	comments.updateToolbar();
				}
			}
			var textimg = document.getElementById("textmode" + editorNum);
			var wysiwygimg = document.getElementById("wysiwyg" + editorNum);
			if (mode == "textmode") {
				textimg.src = "/portal/images/btn_html_on.gif";
				wysiwygimg.src = "/portal/images/btn_wysiwyg_off.gif";
			}
			else {
				textimg.src = "/portal/images/btn_html_off.gif";
				wysiwygimg.src = "/portal/images/btn_wysiwyg_on.gif";
			}		
	  }
	  
	  function setWysiwygValues(form) 
	  {	  
	  	if (editShortDescriptionGenerated) 
	  	{
			form.shortDescription.value = shortDescription.getHTML();			      
		}

	  	if (editLongDescriptionGenerated) 
	  	{
        	form.longDescription.value = longDescription.getHTML();
		}
		if (editCommentsGenerated)
		{
			form.comments.value = comments.getHTML();
		}
      }

      function initDocument() 
      {	    
        shortDescription = new HTMLArea("shortDescription");
        longDescription = new HTMLArea("longDescription");
        comments = new HTMLArea("comments");
      }
	  

<%@page import="com.dotmarketing.portlets.folders.business.FolderAPI"%>
<%@page import="com.dotmarketing.portlets.folders.model.Folder"%>
<%@page import="com.dotmarketing.beans.Host"%>
<%@page import="com.dotmarketing.portlets.structure.model.Field"%>
<%/**
* To Show the contentlet info you need to include this:
*
* 1. imports in the beginning of the jsp file where you need to add the view_ contentlet_popup:
*
* @page import="java.util.List"
* @page import="com.dotmarketing.cms.factories.*"
* @page import="com.dotmarketing.util.UtilMethods"
* @page import="com.dotmarketing.portlets.links.model.Link"
* @page import="com.dotmarketing.factories.InodeFactory"
* @page import="com.dotmarketing.portlets.structure.model.Structure"
* @page import="com.dotmarketing.portlets.structure.model.Field"
* @page import="org.apache.commons.beanutils.PropertyUtils"
* @page import="com.dotmarketing.util.Parameter"
* @page import="java.util.Calendar"
* @page import="java.util.Date"
* @page import="java.util.GregorianCalendar"
* @page import="com.liferay.util.cal.CalendarUtil"
* @page import="java.util.Locale"
* @page import="java.text.SimpleDateFormat"
* @page import="com.dotmarketing.factories.InodeFactory"
* @page import="com.dotmarketing.portlets.contentlet.model.Contentlet"
* @page import="com.dotmarketing.portlets.links.model.Link"
* @page import="com.dotmarketing.portlets.structure.factories.StructureFactory"
* 
* 2. in the place where you go to put the div with the content info you need to pass the contentle an inlude this page
*
* Contentlet content = (Contentlet)asset;
* @ include file="/html/portlet/ext/contentlet/view_contentlet_popup_inc.jsp" 
*
* 3. to show the div info. include a javascript likethis
*
* a href="javascript:dijit.byId("divContent<contentInode>").show()"
*
* @author Oswaldo Gallango
*/%>

<%@page import="com.dotmarketing.portlets.categories.business.CategoryAPI"%>
<%@page import="com.dotmarketing.portlets.categories.business.CategoryAPI"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="com.dotmarketing.util.*"%>
<%@page import="com.liferay.portal.language.LanguageUtil"%>

<%@page import="com.dotmarketing.portlets.languagesmanager.business.*"%>

<%@page import="com.dotmarketing.portlets.contentlet.model.Contentlet"%>
<%@page import="com.dotmarketing.portlets.languagesmanager.model.Language"%>
<%@page import="com.dotmarketing.util.UtilMethods"%>


<%@page import="com.dotmarketing.business.web.WebAPILocator"%>


<%
Language lang = APILocator.getLanguageAPI().getLanguage(((Contentlet) content).getLanguageId()) ;
Structure structure = content.getStructure(); 
List<Field> fields = structure.getFields();

String conPath= null;
try{
	Host conHost = APILocator.getHostAPI().find(content.getHost() , user, true);
	if(!"SYSTEM_HOST".equals(conHost.getInode())){
		conPath = conHost.getHostname();
		Folder conFolder = APILocator.getFolderAPI().find(content.getFolder());
		if(!"SYSTEM_FOLDER".equals(conFolder.getInode())){
			conPath+=conFolder.getPath();
		}
	}
	
}
catch(Exception e){
	Logger.error(this.getClass(), "unable to find host for contentlet"  + content.getIdentifier());
}


%>





<style>

	.previewCon{
		align:center;
		border:1px solid silver;
	}
	
	.previewCon .tRow{
		margin-bottom:5px;
		border-bottom:1px dotted silver;
	}
	
	
	.previewCon .fColumn{
		font-weight:bold;
		padding-right:10px;
		text-align:right;
		vertical-align: top;
		border-right:1px dotted silver;
	}

	.previewCon td{
		padding:5px;
		padding-left:10px;

	}


</style>



<div id="divContent<%=content.getInode()%>_pop_up" dojoType="dijit.Dialog" style="display: none;width:800px;">
	<div><h2><%= LanguageUtil.get(pageContext, "Content-Info") %></h2></div>
	<div>
		<table class="previewCon" align="center">
			<%if (fields.size() > 0)  {%>
				<tr class="tRow">
					<td class="fColumn">
						<%= LanguageUtil.get(pageContext, "Identifier") %>
					</td>
					<td>
						<%= content.get("identifier")%>
					</td>
				</tr>
				<tr class="tRow">
					<td class="fColumn">
						<%= LanguageUtil.get(pageContext, "Viewing-Language") %>
					</td>
					<td>
						<%= lang.getCountry()%> - <%= lang.getLanguage()%>
					</td>
				</tr>
				<%if(conPath != null) {%>
					<tr class="tRow">
						<td class="fColumn">
							<%= LanguageUtil.get(pageContext, "Host-Folder") %>
						</td>
						<td>
							<%=conPath %>
						</td>
					</tr>
				<%} %>
				<tr class="tRow">
					<td class="fColumn">
						<%= LanguageUtil.get(pageContext, "Status") %>
					</td>
					<td>
						<%if(content.isArchived()){%><%= LanguageUtil.get(pageContext, "Archived") %><%}else if(content.isLive()){%><%=LanguageUtil.get(pageContext, "Live")%><%}else{%><%= LanguageUtil.get(pageContext, "Working1") %><% } %>
					</td>
				</tr>
			<% } 
			for(int i = 0; i < fields.size();i++){
				Field field = (Field) fields.get(i);
				
				
				/**************************
				*
				*  Ignore these field types
				*
				***************************/
				if(
					Field.FieldType.HOST_OR_FOLDER.toString().equals(field.getFieldType()) ||
					Field.FieldType.TAB_DIVIDER.toString().equals(field.getFieldType()) ||
					Field.FieldType.PERMISSIONS_TAB.toString().equals(field.getFieldType()) ||
					Field.FieldType.BUTTON.toString().equals(field.getFieldType()) ||
					Field.FieldType.HIDDEN.toString().equals(field.getFieldType()) ||
					Field.FieldType.CONSTANT.toString().equals(field.getFieldType()) ||
					Field.FieldType.CUSTOM_FIELD.toString().equals(field.getFieldType()) ||
					Field.FieldType.RELATIONSHIPS_TAB.toString().equals(field.getFieldType())
				){continue;}
				
				
				%>
				<tr class="tRow">
					<td class="fColumn">
							<%=field.getFieldName()%>
					</td>
					<td>
					<%if (field.getFieldType().equals(Field.FieldType.TEXT.toString())){ %>
						
							<%=(UtilMethods.isSet(APILocator.getContentletAPI().getFieldValue(content, field))
									? UtilMethods.xmlEscape(String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field)))
											: LanguageUtil.get(pageContext, "No")+" " + field.getFieldName() +" "+ LanguageUtil.get(pageContext, "configured"))%>
						
					
					
					
					
					
					
					
					
					
					
					<% }else if (field.getFieldType().equals(Field.FieldType.TEXT_AREA.toString()) || 
							field.getFieldType().equals(Field.FieldType.WYSIWYG.toString())){ %>
						
							<div style="max-height: 150px; width: 500px;font-size:12px;vertical-align: top;overflow:auto;">
						    	<%=(UtilMethods.isSet(APILocator.getContentletAPI().getFieldValue(content, field))
						    			? UtilMethods.xmlEscape(String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field))) 
						    					: LanguageUtil.get(pageContext, "No")+" " +  field.getFieldName() + " "+ LanguageUtil.get(pageContext, "configured"))%>
							</div>
						
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         
			         <% }else if (
			        		 field.getFieldType().equals(Field.FieldType.CHECKBOX.toString()) ||
			        		 field.getFieldType().equals(Field.FieldType.MULTI_SELECT.toString()) 
			        		 
			         
			         
			         
			         ){ 
													
			               String originalValue = String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field));
			               String fieldName = field.getFieldContentlet();
			               String defaultValue = field.getDefaultValue();
			               if (defaultValue != null)
			                 	defaultValue = defaultValue.trim();
			               else 
			               	defaultValue = "";
			               
			               String values = field.getValues();
			               if (values != null)
			               	values = values.trim();
			               else 
			               	values = "";
			               String[] pairs = values.split("\r\n");
			               %>
							
			               <%
			               for(int j = 0;j < pairs.length;j++) {
			                String pair = pairs[j];
			                String[] tokens = pair.split("\\|");
			                if (0 < tokens.length) {
				                String name = tokens[0];
								String value = (tokens.length > 1 ? tokens[1] : name);                                  
	
				                if (UtilMethods.isSet(originalValue)){
				                	if (originalValue.contains(value + ",")){%>
				                		<%=name%><br>
									<%}
				                } else{
				                  if (UtilMethods.isSet(defaultValue) && (defaultValue.contains("|" + value) || defaultValue.contains(value + "|") || defaultValue.equals(value))){%>
				                	  <%=name%><br>
				                  <%}
				                }

							}
						}
					%> 
						
						<% }else if (field.getFieldType().equals(Field.FieldType.DATE.toString()) || 
				 											 field.getFieldType().equals(Field.FieldType.TIME.toString()) ||
				 											 field.getFieldType().equals(Field.FieldType.DATE_TIME.toString()))
												{ %>
							<!-- DISPLAY DATE-->
							<%  java.util.Date startDate = new Date();
								
								try
								{	
									Object oDate = APILocator.getContentletAPI().getFieldValue(content, field);
						            if (oDate instanceof Date) {
						            	startDate = (Date)oDate;
						            } else {
						                String sDate = oDate.toString();
						                SimpleDateFormat dateFormatter = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.LONGDBDATE);
						                try {
						                	startDate = dateFormatter.parse(sDate);
						                } catch (Exception e) { }
						                dateFormatter = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.DBDATE);
						                try {
						                	startDate= dateFormatter.parse(sDate);
						                } catch (Exception e) { }
						                dateFormatter = new SimpleDateFormat(com.dotmarketing.util.WebKeys.DateFormats.SHORTDATE);
						                try {
						                	startDate= dateFormatter.parse(sDate);
						                } catch (Exception e) { }
						            }
								}
								catch(Exception ex)
								{			
									startDate = new Date();
								}
								
								if (field.getFieldType().equals(Field.FieldType.DATE.toString())){%>
									<%=UtilMethods.dateToHTMLDate(startDate) %>
								<%} else if (field.getFieldType().equals(Field.FieldType.DATE_TIME.toString())) {%>
									<%=UtilMethods.dateToLongPrettyHTMLDate(startDate) %>
								<%} else if (field.getFieldType().equals(Field.FieldType.TIME.toString()) ) {%>
									<%=UtilMethods.dateToHTMLTime(startDate) %>
							<%} %>				
			                












						<% }else if (field.getFieldType().equals(Field.FieldType.BINARY.toString())){ %>
							
							<%
							String x ="";
							if(UtilMethods.isSet(content.get(field.getVelocityVarName()))){
								x = String.valueOf(content.get(field.getVelocityVarName())) ;
								if(x.indexOf(java.io.File.separator) > -1){
									x=x.substring(x.lastIndexOf(java.io.File.separator)+1, x.length());	%>
									
										<%if(UtilMethods.isImage(x)){%>
											<%=x %>
											<br/>
											<a target="_blank" href="/contentAsset/raw-data/<%=content.getInode() %>/<%=field.getVelocityVarName() %>/?byInode=true">
												<img src="/contentAsset/image/<%=content.getInode() %>/<%=field.getVelocityVarName() %>?byInode=1&filter=Thumbnail&thumbnail_w=150&thumbnail_h=150&" style="border:2px dotted silver"/>
											</a>
										<%}else{ %>
									
									
									
											<a target="_blank" href="/contentAsset/raw-data/<%=content.getInode() %>/<%=field.getVelocityVarName() %>/?byInode=true"><%=x %></a>
										<%} %>
									<%}%>
								
							<%}%>
							
						








						<% }else if (field.getFieldType().equals(Field.FieldType.IMAGE.toString())){ %>
							<!-- display -->
							
							<%
								String inode = String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field));
								if(InodeUtils.isSet(inode)){
							%>
							<img id="<%=field.getFieldContentlet()%>Thumbnail" src="/thumbnail?inode=<%=inode %>" width="100" height="100" border="1">
							<%  }else{ %><%=LanguageUtil.get(pageContext, "No-Image-configured")  %><%} %>
							
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 
						 <% } else if (field.getFieldType().equals(Field.FieldType.RADIO.toString()) ||
								 field.getFieldType().equals(Field.FieldType.SELECT.toString())
						 
						 ) { 
							%>
						
							<%					
								Object originalValue = String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field));
								String defaultValue = field.getDefaultValue();
								String radio = field.getFieldContentlet();
								String values = field.getValues();
								if (values != null)
			                       values = values.trim();
			                    else 
			                       values = "";
			                    String[] pairs = values.split("\r\n");      
								for(int j = 0;j < pairs.length;j++){
									String pair = pairs[j];
									String[] tokens = pair.split("\\|");
									if (0 < tokens.length) {
										String name = tokens[0];
										Object value = (tokens.length > 1 ? tokens[1] : name);
										if(originalValue instanceof Boolean)
											value = Parameter.getBooleanFromString((String) value);
										else if (originalValue instanceof Long) 
											value = Parameter.getLong((String) value);
										else if (originalValue instanceof Double) 
											value = Parameter.getDouble((String) value);
										if ((UtilMethods.isSet(originalValue) && value.equals(originalValue)) ||
																		(UtilMethods.isSet(defaultValue) && defaultValue.equals(value))){%>
											<%=name%><br>
										<%}%> 
									<%}%> 
								<%}%> 
																							
						
	
							
							
							
							
							
							
							
							
							
							
			         
			         
			              <% } else if (field.getFieldType().equals(Field.FieldType.TAG.toString())){ %>

									<%=(UtilMethods.isSet(APILocator.getContentletAPI().getFieldValue(content, field))?String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field)):"")%>
								
						
						
						
						
								

			         
			         
			         
			         
			         
			         
			         
			         
			              <% } else if (field.getFieldType().equals(Field.FieldType.TAG.toString())){ %>

									<%=(UtilMethods.isSet(APILocator.getContentletAPI().getFieldValue(content, field))?String.valueOf(APILocator.getContentletAPI().getFieldValue(content, field)):"")%>
								
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						<%}else if (field.getFieldType().equals(Field.FieldType.CATEGORY.toString())){ %>
					       	<%
					       	CategoryAPI categoryAPI = APILocator.getCategoryAPI();
					        Set<com.dotmarketing.portlets.categories.model.Category> selectedCats = (Set<com.dotmarketing.portlets.categories.model.Category>) APILocator.getContentletAPI().getFieldValue(content, field) ;
							com.dotmarketing.portlets.categories.model.Category category = categoryAPI.find(field.getValues(), APILocator.getUserAPI().getSystemUser(), false);
							java.util.List<com.dotmarketing.portlets.categories.model.Category> children = categoryAPI.getChildren(category, user, false);
							for(com.dotmarketing.portlets.categories.model.Category child : children){%>		
								<%for(com.dotmarketing.portlets.categories.model.Category sel : selectedCats){%>		
									<%if(child.getInode().equals(sel.getInode())){ %>
										<%=child.getCategoryName() %> <br>
									<%} %>
								<%} %>
							<%} %>
	               		<% }else if(field.getFieldType().equals(Field.FieldType.FILE.toString())){%>
	               		    <!-- display -->
							
							<%
								String inode = String.valueOf(content.get(field.getVelocityVarName())) ;
								if(InodeUtils.isSet(inode)){
									com.dotmarketing.portlets.files.model.File file = APILocator.getFileAPI().getWorkingFileById(inode,APILocator.getUserAPI().getSystemUser(), false);
									if(file!=null){							
							%>
							<a target="_blank" href="<%=file.getURI()%>"><%=file.getFileName() %></a>
							<%  }
							     }%>
	               		
	               		<%} %>
	                </td>
               </tr>
	        <%} %>
		</table>
	</div>
	<div class="formRow" style="text-align:center">
		<button dojoType="dijit.form.Button"  onClick="dijit.byId('divContent<%=content.getInode()%>_pop_up').hide()"><%= LanguageUtil.get(pageContext, "close") %></button>
	</div>
	
</div>

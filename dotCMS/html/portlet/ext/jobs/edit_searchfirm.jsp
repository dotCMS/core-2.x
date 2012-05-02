<%
    java.util.List l = new java.util.ArrayList(); 
    l.add("Invoice Me");
    l.add("Visa");
    l.add("Master Card");
    l.add("American Express");
    java.util.Iterator it = l.iterator();
    com.dotmarketing.portlets.jobs.struts.SearchfirmForm s = (com.dotmarketing.portlets.jobs.struts.SearchfirmForm)request.getAttribute("SearchfirmForm");

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/jobs/view_searchfirms"});
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);

%>
<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<script>
	var form;
		function doSubmit(){
			form.cmd.value = "<%=Constants.ADD%>";
			form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" /></portlet:actionURL>';
			submitForm(form);
		}
		
		function doDelete() {
			if(confirm("Are you sure you would like to delete this listing?")) {
				window.location = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" /><portlet:param name="inode" value="<%=String.valueOf(s.getInode())%>" /><portlet:param name="<%= Constants.CMD %>" value="<%=Constants.DELETE%>" /><portlet:param name="referer" value="<%=referer%>" /></portlet:actionURL>';
			} else return false;
		}
			  
</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"add-searchfirm\") %>" />
	
	<html:form action="/ext/jobs/edit_searchfirm" styleId="fm">
	<input name="<portlet:namespace />redirect" type="hidden" value="<portlet:renderURL><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /></portlet:renderURL>">
	<input name="<%= Constants.CMD %>" type="hidden" value="<%=Constants.ADD%>">
    <html:hidden property="inode"/>

    <div align="center"><center>
    <table cellpadding="10" cellspacing="0" border="0" width="75%">
    	<tr>
    		<td align="right">
				<button dojoType="dijit.form.Button" onclick="doDelete()">Delete</button>
    		</td>
    	</tr>
    </table>
	<table cellpadding="0" cellspacing="0" border="0" width="75%">
	<tr>
		<td>
			
			<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta"  style="margin-bottom:20px;">
	            <tr>
					<td colspan="2" class="beta"><font class="beta" size="2"><strong>Executive Search Firm Information</strong></td>
				</tr>
				<tr>
					<td align="right" width=30%><font class="bg" size="2"><strong>Organization</strong></font></td>
					<td width=70% ><html:text property="organization" size="20" maxlength="150" styleClass="textbox" /></td>
				</tr>       
				<tr>
					<td align="right"><font class="bg" size="2"><strong>Name of Contact</strong></font></td>
					<td ><html:text property="name" size="20" maxlength="150" styleClass="textbox" /></td>
				</tr>
				<tr>
					<td align="right"><font class="bg" size="2"><strong>Title of Contact</strong></font></td>
					<td ><html:text property="title" size="20" maxlength="150" styleClass="textbox" /></td>
				</tr>
				<tr>
					<td align="right"><font class="bg" size="2"><strong>Address</strong></font></td>
					<td ><html:text property="streetaddress1" size="20" maxlength="250" styleClass="textbox" /></td>
				</tr>
				<tr>
					<td align="right"><font class="bg" size="2"><strong>Address</strong></font></td>
					<td ><html:text property="streetaddress2" size="20" maxlength="250" styleClass="textbox" /></td>
				</tr>            
				<tr>
					<td align="right" ><font class="bg" size="2"><strong>Phone</strong></font></td>
					<td ><html:text property="phone" size="20" maxlength="50" styleClass="textbox" /></td>
				</tr>
				<tr>
					<td align="right" ><font class="bg" size="2"><strong>Fax</strong></font></td>
					<td ><html:text property="fax" size="20" maxlength="50" styleClass="textbox" /></td>
				</tr>
				<tr>
					<td align="right" ><font class="bg" size="2"><strong>Email Address</strong></font></td>
					<td ><html:text property="email" size="20" maxlength="250" styleClass="textbox" /></td>
				</tr>
			</table>

				
			<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta"  style="margin-bottom:20px;">
				<tr>
					<td colspan="2" class="beta"><font class="beta" size="2"><strong>Online Listing</strong></font></td>
				</tr>
				<tr>
					<td colspan="2" >
						<font class="bg" size="2">
						<strong>75 word description of firm:</strong><br>
						(Please include any specializations, information on the types of positions your firm recruits for, etc.)
						</font>
					</td>
				</tr>
				<tr>
					<td colspan="2" height="54"  align=center><strong><html:textarea rows="20" property="description" cols="50" styleClass="textarea2"/></strong></font></td>
				</tr>
			</table>
				
			<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta"  style="margin-bottom:20px;">
				<tr>
					<td colspan="2" class="beta"><font class="beta" size="2"><strong>Contact Information</strong> - (If different then on the previous page)</font></td>
				</tr>
				<tr>
					<td colspan="2" height="54"  align=center><strong><html:textarea rows="20" property="contactinfo" cols="50" styleClass="textarea2"/></strong></font></td>
				</tr>
			</table>
				
		    <table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta"  style="margin-bottom:20px;">
				<tr>
					<td  width=50% align=right><font class="bg" size="2"><strong>Web Address:</strong></font></td>
					<td  width=50%><html:text property="url" size="20" maxlength="250"  styleClass="textbox"/></td>
				</tr>
				<tr>
					<td  align=right><font class="bg" size="2"><strong>I want the listing to provide a<br>link to my site ($50 extra):</strong></font></td>
					<td>
					<html:radio property="linking" value="true"/>Yes
					<html:radio property="linking" value="false"/>No</td>
				</tr>                      
				<tr>
					<td  align=right><font class="bg" size="2"><strong>Credit Card </strong></font></td>
					<td width="50%" >
						<html:select property="cctype" size="1">
							<%while(it.hasNext()) {
								String val = (String) it.next();
							%>
								<option name="<%=val%>"<%=val.equals(s.getCctype()) ? " SELECTED" : ""%>><%=val%>
							<%}%>
						</html:select>
					</td>
				</tr>
				<tr>
					<td  align=right><font class="bg" size="2"><strong>Card Number</strong></font></td>
					<td width="50%" ><html:text property="ccnum" size="20" maxlength="50"  styleClass="textbox"/></td>
				</tr>
				<tr>
					<td  align=right><font class="bg" size="2"><strong>Expiration Date</strong></font></td>
					<td width="50%" ><html:text property="ccexp" size="20" maxlength="50"  styleClass="textbox"/></td>
				</tr>
				<tr>
					<td colspan="2"  class="beta"><font class="bg" size="2"><B>For Corenet Use</B></font></td>
				</tr>					
				<tr>
					<td bgcolor="#FFFFFF" align="right"><font class="bg" size="2"><strong>Activated</strong></font></td>
					<td bgcolor="#FFFFFF">
					<html:radio property="active" value="true"/>Yes
					<html:radio property="active" value="false"/>No
					</td>
				</tr>
				<tr>
					<td bgcolor="#FFFFFF" align="right"><font class="bg" size="2"><strong>Expiration Date</strong></font></td>
					<td bgcolor="#FFFFFF"><html:text property="webexpirationdate"  styleClass="textbox"/></td>
				</tr>				
			</table>
				  
				  
			<table border="0" cellspacing="0" cellpadding="6" align=center height=30>
				<tr>
					<td>
                        <button dojoType="dijit.form.Button"  name="B1" onClick="doSubmit()">Save</button>
                    </td>
					<td>
                        <button dojoType="dijit.form.Button" name="B2" type="reset">Clear</button>
                    </td>
				</tr>
			</table>
		</td>
	</tr>
</table>

      </center></div>

    </html:form>

</liferay:box>
<script>
	form = document.getElementById("fm");
</script>

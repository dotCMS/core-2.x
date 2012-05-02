<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<%
    java.util.List l = new java.util.ArrayList(); 
    l.add("Invoice Me");
    l.add("Visa");
    l.add("Master Card");
    l.add("American Express");
    java.util.Iterator it = l.iterator();
    com.dotmarketing.portlets.jobs.struts.JobsForm j = (com.dotmarketing.portlets.jobs.struts.JobsForm)request.getAttribute("JobsForm");

	java.util.Map params = new java.util.HashMap();
	params.put("struts_action",new String[] {"/ext/jobs/view_jobs"});
	String referer = com.dotmarketing.util.PortletURLUtil.getActionURL(request,WindowState.MAXIMIZED.toString(),params);
%>
	<script>
		var form;
		function doSubmit(){
			form = document.getElementById("fm");
			form.cmd.value = "<%=Constants.ADD%>";
			form.action = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/jobs/edit_job" /></portlet:actionURL>';
			submitForm(form);
		}
		
		function doDelete() {
			if(confirm("Are you sure you would like to delete this listing?")) {
				window.location = '<portlet:actionURL><portlet:param name="struts_action" value="/ext/jobs/edit_job" /><portlet:param name="inode" value="<%=String.valueOf(j.getInode())%>" /><portlet:param name="<%= Constants.CMD %>" value="<%=Constants.DELETE%>" /><portlet:param name="referer" value="<%=referer%>" /></portlet:actionURL>';
			} else return false;
		}
		
	</script>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_title" value="<%= LanguageUtil.get(pageContext, \"add-job\") %>" />

	<html:form action="/ext/jobs/edit_job" styleId="fm" >
	<input name="<portlet:namespace />redirect" type="hidden" value="<portlet:renderURL><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /></portlet:renderURL>">
	<input name="<%= Constants.CMD %>" type="hidden" value="<%=Constants.ADD%>">
    <html:hidden property="inode"/>

    <table cellpadding="0" cellspacing="0" border="0" width="75%">
    	<tr>
    		<td>
			   <button dojoType="dijit.form.Button"  name="B1" onClick="doSubmit();">Save</button>
    		</td>
    		<td align=right>
				<button dojoType="dijit.form.Button" onclick="doDelete()">Delete</button>
    		</td>
    	</tr>
    </table>    
    <BR>
	<table cellpadding="0" cellspacing="0" border="0" width="75%">
		<tr>
			<td>	
			<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta" style="margin-bottom:20px;">
					<tr>
						<td class="beta" colspan="2"><font class="beta" size="2"><strong>Employer Information</strong></td>
					</tr>
					<tr>
						<td width="30%" align="right" >
						<font class="bg" size="2"><strong>Organization</strong></font></td>
						<td width="70%"><html:text property="organization" size="20" maxlength="150"  styleClass="textbox"/></td>
					</tr>

					<tr>
						<td align="right" nowrap><font class="bg" size="2"><strong>Name of Contact</strong></font></td>
						<td><html:text property="name" size="20" maxlength="150"  styleClass="textbox"/></td>
					</tr>

					<tr>
						<td align="right"><font class="bg" size="2"><strong>Title of Contact</strong></font></td>
						<td><html:text property="title" size="20" maxlength="150"  styleClass="textbox"/></td>
					</tr>

					<tr>
						<td align="right"><font class="bg" size="2"><strong>Address</strong></font></td>
						<td><html:text property="streetaddress1" size="20" maxlength="250"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Address</strong></font></td>
						<td><html:text property="streetaddress2" size="20" maxlength="250"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>City</strong></font></td>
						<td><html:text property="city" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>State</strong></font></td>
						<td><html:text property="state" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Zip</strong></font></td>
						<td><html:text property="zip" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Phone</strong></font></td>
						<td><html:text property="phone" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Fax</strong></font></td>
						<td><html:text property="fax" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Email Address</strong></font></td>
						<td><html:text property="email" size="20" maxlength="250"  styleClass="textbox"/></td>
					</tr>
				</table>
					
			
				
				<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta"  style="margin-bottom:20px;">
					<tr>
						<td colspan="2" class="beta"><font class="beta" size="2"><strong>Online Job Posting</strong></font></td>
					</tr>
					<tr>
						<td width="30%" align="right"><font class="bg" size="2"><strong>Job Title</strong></font></td>
						<td width="70%"><html:text property="jobtitle" size="20" maxlength="150"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Job Location</strong></font></td>
						<td><html:text property="joblocation" size="20" maxlength="100"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Salary</strong></font></td>
						<td><html:text property="salary" size="20" maxlength="100" styleClass="textbox"/></td>
					</tr>
				</table>
					
				
				<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta" style="margin-bottom:20px;">
					<tr>
						<td colspan="2" class="beta">
							<strong><font class="beta" size="2">Description of position to be filled: (Please limit to 100 words)</strong></font>
						</td>
					</tr>
					<tr>
						<td  colspan="2" height="54" align="center">
							<html:textarea rows="20" property="description" cols="50" styleClass="textarea2" />
						</td>
					</tr>
				</table>
					
				
				<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta" style="margin-bottom:20px;">
					<tr>
						<td colspan="2" class="beta"><font class="beta" size="2"><strong>Requirements/Qualifications:</strong></font></td>
					</tr>
					<tr>
						<td colspan="2" height="54" align="center">
							<html:textarea rows="20" property="requirements" cols="50" styleClass="textarea2" />
						</td>
					</tr>
				</table>
					
				
				<table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta" style="margin-bottom:20px;">
					<tr>
						<td colspan="2" class="beta"><font class="beta" size="2"><strong>Contact Information:</strong></font></td>
					</tr>
					<tr>
						<td  colspan="2" height="54" align="center">
							<html:textarea rows="20" property="contactinfo" cols="50" styleClass="textarea2"/>
						</td>
					</tr>
				</table>
					
                <table width="100%" border="0" cellspacing="1" cellpadding="4" class="beta" style="margin-bottom:20px;"> 
					<tr>
						<td align="right"><font class="bg" size="2">
						<strong>Premium ad ($50 extra)</strong></font></td>
						<td>
						<html:radio property="premiumlisting" value="true" />Yes 
						<html:radio property="premiumlisting" value="false" />No</td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Blind ad ($50 extra)</strong></font></td>
						<td>
						<html:radio property="blind" value="true"/>Yes 
						<html:radio property="blind" value="false"/>No
						</td>
					</tr>					
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Credit Card </strong></font></td>
						<td><html:select property="cctype" size="1">
							<%while(it.hasNext()) {
                				 String val = (String) it.next();
              				%>
							<option name="<%=val%>"<%=val.equals(j.getCctype())?" SELECTED":""%>><font class="bg" size="2"><%=val%> <%}%></font></option>
						</html:select>
						</td>
					</tr>
					<tr>
						<td align="right"><font class="bg" size="2"><strong>Card Number</strong></font></td>
						<td><html:text property="ccnum" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>

					<tr>
						<td align="right"><font class="bg" size="2"><strong>Expiration Date</strong></font></td>
						<td><html:text property="ccexp" size="20" maxlength="50"  styleClass="textbox"/></td>
					</tr>
					<tr>
						<td colspan="2" class="beta"><font class="bg" size="2"><B>For Corenet Use</B></font></td>
					</tr>					
					<tr>
						<td bgcolor="#FFFFFF" align="right"><font class="bg" size="2"><strong>Activated</strong></font></td>
						<td bgcolor="#FFFFFF">
						<html:radio property="active" value="true"/><font class="bg" size="2">Yes</font>
						<html:radio property="active" value="false"/><font class="bg" size="2">No</font></td>
					</tr>
					<tr>
						<td bgcolor="#FFFFFF" align="right"><font class="bg" size="2"><strong>Expiration Date</strong></font></td>
						<td bgcolor="#FFFFFF"><html:text property="webexpirationdate"  styleClass="textbox"/></td>
					</tr>
				</table>
          </div></td>

        </tr>
        <tr>
 
          <td width="100%" align=center><strong>
          <br/>
              <button dojoType="dijit.form.Button" onClick="doSubmit();">Save</button>
              <button dojoType="dijit.form.Button" onClick="doSubmit();" name="B2" type="reset">Clear</button>
          </td>
        </tr>
      </table>

      </center></div>

    </html:form>

</liferay:box>

    </td>

  </tr>
<tr>
</table>



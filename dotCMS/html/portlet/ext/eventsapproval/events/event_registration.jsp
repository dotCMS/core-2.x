<%@ page language="java" import="com.dotmarketing.portlets.events.model.*,com.dotmarketing.portlets.events.struts.*"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%
	//TODO remove that page
	Event e = (Event)request.getAttribute("event");
	EventRegistrationForm form = (EventRegistrationForm)request.getAttribute("eventRegistrationForm");
%>
<html>
  <head>
    
    <title>Event Registration</title>
    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
  	<SCRIPT type="text/javascript">
	
	function checkEmailFormat(email) {
		if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)){
			return (true)
		}
		return (false)
	}

	function IsNumeric(sText)
	
	{
	   var ValidChars = "0123456789.";
	   var IsNumber=true;
	   var Char;
	
	 
	   for (i = 0; i < sText.length && IsNumber == true; i++) 
	      { 
	      Char = sText.charAt(i); 
	      if (ValidChars.indexOf(Char) == -1) 
	         {
	         IsNumber = false;
	         }
	      }
	   return IsNumber;
   	}
   	
   	function doSubmit () {
  			var form = document.getElementById("registrationForm");
  			
  			//Validations before submit
  			if (form.parent1Name.value == "") {
  				alert ("A parent name is required");
  				form.parent1Name.focus ();
  				return false;
  			}
  			if (form.student1Name.value == "") {
  				alert ("A student name is required");
  				form.student1Name.focus ();
  				return false;
  			}
  			if (form.student1Grade.value == "") {
  				alert ("A student grade is required");
  				form.student1Grade.focus ();
  				return false;
  			}
  			if (form.email.value == "") {
  				alert ("An email is required");
  				form.email.focus ();
  				return false;
  			}
  			if (!checkEmailFormat(form.email.value)) {
  				alert ("Invalid email format");
  				form.email.focus ();
  				return false;
  			}
  			if (!IsNumeric(form.numberAttending.value)) {
  				alert ("Number Attending must be numeric");
  				form.numberAttending.focus ();
  				return false;
  			}

  			if (form.student2Name.value != "" && form.student2Grade.value == "") {
  				alert ("A student grade is required");
  				form.student2Grade.focus ();
  				return false;
  			}
  			if (form.student3Name.value != "" && form.student3Grade.value == "") {
  				alert ("A student grade is required");
  				form.student3Grade.focus ();
  				return false;
  			}
  			if (form.student4Name.value != "" && form.student4Grade.value == "") {
  				alert ("A student grade is required");
  				form.student4Grade.focus ();
  				return false;
  			}
  			if (form.student5Name.value != "" && form.student5Grade.value == "") {
  				alert ("A student grade is required");
  				form.student5Grade.focus ();
  				return false;
  			}
  			
  			//Submitting the form	
  			form.dispatch.value = "save";
  			if (confirm("All your information is correct?"))
  				form.submit ();
  			else
  				return false;
  		}
  	</SCRIPT>
    <form action="/cms/registerToEvent" method="post" id="registrationForm">
      <input type="hidden" name="dispatch" id="dispatch" value=""/>
      <input type="hidden" name="inode" id="inode" value="<%=form.getInode()%>"/>
      <input type="hidden" name="eventInode" id="eventInode" value="<%=form.getEventInode()%>"/>
      <input type="hidden" name="registationRandomId" id="registationRandomId" value="<%=form.getRegistationRandomId()%>"/>
      <table border="0" width="550" align="center" cellpadding="0" cellspacing="5">
        <tr>
          <td colspan="3" align="center"><strong>REGISTRATION FOR THE EVENT: <%=e.getTitle().toUpperCase()%></strong></td>
        </tr>
        <tr>
          <td colspan="3" height="15"></td>
        </tr>
        <tr>
          <td align="right">Parents' Names:</td>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="parent1Name" value="<%=form.getParent1Name() %>" id="parent1Name" style="width: 200px;"/>&nbsp;<FONT style="color: red;">*</FONT></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="parent2Name" value="<%=form.getParent2Name() %>" style="width: 200px;"/></td>
          <td></td>
        </tr>
        <tr>
          <td align="right">Students' Names &amp; Grades:</td>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td></td>
          <td align="center">Name</td>
          <td align="center">Grade</td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="student1Name" value="<%=form.getStudent1Name() %>" id="student1Name" style="width: 200px;"/>&nbsp;<FONT style="color: red;">*</FONT></td>
          <td><input type="text" name="student1Grade" value="<%=form.getStudent1Grade() %>" id="student1Grade" style="width: 100px;"/>&nbsp;<FONT style="color: red;">*</FONT></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="student2Name" value="<%=form.getStudent2Name() %>" style="width: 200px;"/></td>
          <td><input type="text" name="student2Grade" value="<%=form.getStudent2Grade() %>" style="width: 100px;"/></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="student3Name" value="<%=form.getStudent3Name() %>" style="width: 200px;"/></td>
          <td><input type="text" name="student3Grade" value="<%=form.getStudent3Grade() %>" style="width: 100px;"/></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="student4Name" value="<%=form.getStudent4Name() %>" style="width: 200px;"/></td>
          <td><input type="text" name="student4Grade" value="<%=form.getStudent4Grade() %>" style="width: 100px;"/></td>
        </tr>
        <tr>
          <td></td>
          <td><input type="text" name="student5Name" value="<%=form.getStudent5Name() %>" style="width: 200px;"/></td>
          <td><input type="text" name="student5Grade" value="<%=form.getStudent5Grade() %>" style="width: 100px;"/></td>
        </tr>
        <tr>
          <td align="right">Number Attending:</td>
          <td><input align="right" type="text" name="numberAttending" maxlength="2" value="<%=form.getNumberAttending() %>" style="width:20px"/></td>
          <td></td>
        </tr>
        <tr>
          <td align="right" valign="top">Comments:</td>
          <td colspan="2"><TEXTAREA name="comments" style="width: 332px;height: 80px"><%=form.getComments() %></TEXTAREA></td>
        </tr>
        <tr>
          <td align="right">Email Address:</td>
          <td><input type="text" name="email" value="<%=form.getEmail() %>" id="email" style="width: 200px;"/>&nbsp;<FONT style="color: red;">*</FONT></td>
          <td></td>
        </tr>
        <tr>
          <td colspan="2"></td>
          <td colspan="1" height="15" align="left"><FONT style="color: red;">* required fields</FONT></td>
        </tr>
        <tr>
          <td colspan="3" height="15"></td>
        </tr>
        <tr>
          <td colspan="3" align="center">
             <button dojoType="dijit.form.Button" onClick="doSubmit()">Register to Event</button> 
          </td>
        </tr>
      </table>
    </form>
  </body>
</html>

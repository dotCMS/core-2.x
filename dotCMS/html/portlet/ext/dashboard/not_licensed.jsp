<%@page import="com.liferay.portal.language.LanguageUtil"%>
<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<liferay:param name="box_title" value='<%= LanguageUtil.get(pageContext, "view-dashboard") %>' />


<script>
	dojo.addOnLoad(
		function(){
			hideEEFlyout();
		}
	);

	
	function hideEEFlyout(){

		if(dojo.style("dashboardScreencap", "opacity") < 1){
			dojo.style("dashboardScreencap", "opacity", 1);
			 var fadeArgs = {
	                   node: "eeFlyout"
	               };
	         dojo.fadeOut(fadeArgs).play();
			
		}
		else{
			dojo.style("dashboardScreencap", "opacity", .6);
			 var fadeArgs = {
	                   node: "eeFlyout"
	               };
	          dojo.fadeIn(fadeArgs).play();
			
		}
			
		
	}
</script>


<style>
.alertWindow{
	position:absolute;
	top:50%;
	left:50%;
	width:650px;
	margin:-200px 0 0 -350px;
	padding: 20px 20px 30px 40px;
	color: #fff;
	opacity:.85;
	background:#333;
	-moz-border-radius: 15px;
	-webkit-border-radius: 15px;
	-moz-box-shadow:0px 0px 15px #666;
	-webkit-box-shadow:0px 0px 15px #666;
}
.alertWindow a{color:#fff;}
</style>


	<div style="position:relative:center;width:1180px;height:1078px;margin:0 auto;background:url(/html/images/admin/dashboard_screencap.png)" id="dashboardScreencap" onclick="hideEEFlyout()">

	</div>
		<div class="alertWindow" id="eeFlyout" onclick="hideEEFlyout()">
			<div style="margin-bottom:10px;">
				<h2><%= LanguageUtil.get(pageContext, "Dashboard") %></h2>
			</div>
			
			<%= LanguageUtil.get(pageContext, "Dashboard-Not-Licensed") %>
			<div style="margin-bottom:10px;padding-top:10px;">
				<h2><%= LanguageUtil.get(pageContext, "Activity-Streams") %></h2>
			</div>
			<%= LanguageUtil.get(pageContext, "Activity-Stream-Not-Licensed") %>

		</div>
</liferay:box>

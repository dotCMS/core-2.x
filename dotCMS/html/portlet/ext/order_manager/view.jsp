<%@page import="java.util.HashMap"%>
<%@page import="com.dotmarketing.util.Config"%>
<%
//statuses  list
List<HashMap> finalPaymentStatus = new ArrayList<HashMap>();
String[] statusesArrayss = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_PAY_STATUSES");
for (int i=0;i<statusesArrayss.length;i++) {
	String status = statusesArrayss[i];
	HashMap<String,String> hs = new HashMap<String,String>();
	hs.put("optionName", Config.getStringProperty(status + "_FN"));
	hs.put("optionValue", Config.getStringProperty(status));
	finalPaymentStatus.add(hs);
}
request.setAttribute("paymentStatusList", finalPaymentStatus.iterator());



//statuses list
List<HashMap> finalStatus = new ArrayList<HashMap>();
statusesArrayss = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
for (int i=0;i<statusesArrayss.length;i++) {
	String status = statusesArrayss[i];
	HashMap<String,String> hs = new HashMap<String,String>();
	hs.put("optionName", Config.getStringProperty(status + "_FN"));
	hs.put("optionValue", Config.getStringProperty(status));
	finalStatus.add(hs);
}
request.setAttribute("orderStatusList", finalStatus.iterator());



%>



<%@ include file="/html/portlet/ext/order_manager/view_orders.jsp" %>
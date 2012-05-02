<%@ include file="/html/portlet/ext/order_manager/init.jsp" %>

<%
	RenderRequestImpl rreq = (RenderRequestImpl) pageContext.getAttribute("renderRequest");
	String portletId1 = rreq.getPortletName();
	
	if (!UtilMethods.isSet(portletId1))
		portletId1 = layouts[0].getPortletIds().get(0);
	
	Portlet portlet1 = PortletManagerUtil.getPortletById(company.getCompanyId(), portletId1);
	String strutsAction = ParamUtil.get(request, "struts_action", null);
	String cmd = ParamUtil.get(request, "cmd", "");
	String referer = UtilMethods.decodeURL(UtilMethods.decodeURL(ParamUtil.get(request, "referer", "")));
	
	if (!UtilMethods.isSet(strutsAction) || strutsAction.equals(portlet1.getInitParams().get("view-action"))) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), null));
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (strutsAction.equals("/ext/order_manager/view_products") && cmd.equals("view")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		Map params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_orders"});
		String crumbTrailReferer = PortletURLUtil.getRenderURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), crumbTrailReferer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Add-Product"), "javascript: continueShopping();"));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "View-Shopping-Cart"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (referer.contains("/ext/order_manager/edit_order")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		Map params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_orders"});
		String crumbTrailReferer = PortletURLUtil.getRenderURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), crumbTrailReferer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Edit-Order"), referer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Add-Product"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (strutsAction.equals("/ext/order_manager/view_products")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		Map params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_orders"});
		String crumbTrailReferer = PortletURLUtil.getRenderURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), crumbTrailReferer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Add-Product"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (strutsAction.equals("/ext/order_manager/view_users")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		Map params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_orders"});
		String crumbTrailReferer = PortletURLUtil.getRenderURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), crumbTrailReferer));
		
		params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_products"});
		crumbTrailReferer = PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Add-Product"), crumbTrailReferer));
		
		params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_products"});
		params.put("cmd", new String[] {"view"});
		crumbTrailReferer = PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "View-Shopping-Cart"), crumbTrailReferer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Select-User"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (strutsAction.equals("/ext/order_manager/view_checkout")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		Map params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_orders"});
		String crumbTrailReferer = PortletURLUtil.getRenderURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), crumbTrailReferer));
		
		params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_products"});
		crumbTrailReferer = PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Add-Product"), crumbTrailReferer));
		
		params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_products"});
		params.put("cmd", new String[] {"view"});
		crumbTrailReferer = PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "View-Shopping-Cart"), crumbTrailReferer));
		
		params = new HashMap();
		params.put("struts_action", new String[] {"/ext/order_manager/view_users"});
		crumbTrailReferer = PortletURLUtil.getActionURL(request, WindowState.MAXIMIZED.toString(), params);
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Select-User"), crumbTrailReferer));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Checkout"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	} else if (strutsAction.equals("/ext/order_manager/edit_order")) {
		List<CrumbTrailEntry> crumbTrailEntries = new ArrayList<CrumbTrailEntry>();
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "javax.portlet.title." + portletId1), "javascript: back();"));
		
		crumbTrailEntries.add(new CrumbTrailEntry(LanguageUtil.get(pageContext, "Edit-Order"), null));
		
		request.setAttribute(com.dotmarketing.util.WebKeys.CMS_CRUMBTRAIL_OPTIONS, crumbTrailEntries);
	}
%>

<%@ include file="/html/portlet/ext/common/sub_nav_inc.jsp" %>
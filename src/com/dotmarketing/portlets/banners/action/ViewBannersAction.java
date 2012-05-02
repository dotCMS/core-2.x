package com.dotmarketing.portlets.banners.action;

import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.portlets.banners.factories.BannerFactory;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;

public class ViewBannersAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        
        if (req.getWindowState().equals(WindowState.NORMAL)) {
        	req.setAttribute(WebKeys.BANNER_VIEW_PORTLET, BannerFactory.getActiveBanners());
            return mapping.findForward("portlet.ext.banners.view");
        } else {
            req.setAttribute(WebKeys.BANNERS_VIEW, getBanners(req));
            return mapping.findForward("portlet.ext.banners.view_banners");
        }
    }
    private List getBanners(RenderRequest req) {
    	String orderby = "title";
        if (req.getParameter("orderby")!=null && req.getParameter("orderby").length()!=0) {
        	orderby = req.getParameter("orderby");	
        }
        List l;
        if (req.getParameter("query")!=null&&req.getParameter("query").length()!=0) {
            Logger.debug(this, "Running .getBannersByOrderAndKeywords=" + req.getParameter("query"));
	     	l = BannerFactory.getBannersByOrderAndKeywords(req.getParameter("query"),orderby);
        }
        else {
            Logger.debug(this, "running getBannersByOrder" + orderby);
	        l = BannerFactory.getBannersByOrder(orderby);
        }
        return l;
    }
}

package com.dotmarketing.viewtools;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.portlets.banners.factories.BannerFactory;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

public class BannersWebAPI implements ViewTool {
	
    private HttpServletRequest request;


    public void init(Object obj) {
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();
    }
    
    public Banner getBanner(String inode) {
    	return BannerFactory.getBanner(inode);
    }

    public List getBannerList(String path, String placement, Host host) {
    	
    	HttpSession ses = request.getSession();
        User user = (User) ses.getAttribute(WebKeys.CMS_USER);
    	
    	String hostURL = host.getHostname();
    	return BannerFactory.getBannerList(hostURL + path,placement, user, true);
    }

    public Banner getRandomBanner(String path, String placement, Host host) {
    	HttpSession ses = request.getSession();
        User user = (User) ses.getAttribute(WebKeys.CMS_USER);
    	String hostURL = host.getHostname();
    	List banners = BannerFactory.getBannerList(hostURL + path,placement, user, true);
    	return (Banner) banners.get((int)Math.round(Math.random()*banners.size()-1));
    }
    
    public void updateViews(Banner banner) {
    	BannerFactory.updateBannerViews(request, banner);
    }
 
    public void updateClicks(Banner banner) {
    	BannerFactory.updateBannerClicks(request, banner);
    }
}
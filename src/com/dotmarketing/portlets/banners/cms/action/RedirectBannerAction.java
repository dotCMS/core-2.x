package com.dotmarketing.portlets.banners.cms.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.portlets.banners.factories.BannerFactory;
import com.dotmarketing.portlets.banners.model.Banner;

/**
 * @author Maru
 */
public class RedirectBannerAction  extends DispatchAction{

	public ActionForward execute(ActionMapping mapping, ActionForm jf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String inode = request.getParameter("inode");
		String redirURL = request.getParameter("redir");
		
		//to update the number of clicks and redirect to redirURL
		Banner banner = BannerFactory.getBanner(inode);
		BannerFactory.updateBannerClicks(request, banner);
		
		response.sendRedirect(redirURL);
		return null;
		
	}


}

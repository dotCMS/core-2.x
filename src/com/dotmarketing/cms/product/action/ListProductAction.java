package com.dotmarketing.cms.product.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

public class ListProductAction extends DispatchAction 
{
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//Getting the user from the session		
		//_loadProductByCategory(lf,request);
		//_loadTypeProducts(lf,request);
		
		return mapping.findForward("listProducts");
	}
}

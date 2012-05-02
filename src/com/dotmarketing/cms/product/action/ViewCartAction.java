package com.dotmarketing.cms.product.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.util.WebKeys;

public class ViewCartAction extends DispatchAction 
{
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception 
	{		
		ActionForward af = mapping.findForward("viewCart");
		return af;
	}
	
	private ShoppingCart _getShopingCart(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(WebKeys.SHOPPING_CART);
		if (shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		return shoppingCart;
	}
}

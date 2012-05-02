package com.dotmarketing.cms.product.action;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;

public class AddToCartAction extends DispatchAction 
{
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		HttpSession session = request.getSession();
		ShoppingCart shoppingCart = _getShopingCart(request);
		String formatInode = request.getParameter(WebKeys.SHOPPING_CART_FORMAT_INODE);
		int quantity = Integer.parseInt(request.getParameter(WebKeys.SHOPPING_CART_FORMAT_QUANTITY));
		shoppingCart.addItem(quantity,formatInode);
		
		String referrer = request.getParameter("referrer");
		referrer = URLDecoder.decode(referrer,"UTF-8");
				
		ActionMessages messages = new ActionMessages();
		messages.add("message" ,new ActionMessage ("message.shoppingcart.added"));
		saveMessages(session, messages);		
		ActionForward af = new ActionForward(referrer);
		af.setRedirect(true);		
		return af;
	}
	
	public ActionForward remove(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		ShoppingCart shoppingCart = _getShopingCart(request);
		String formatInode = request.getParameter("inode");
		shoppingCart.removeItem(formatInode);
		
		ActionForward af = mapping.findForward("viewCart");
		return af;
	}

	public ActionForward addDiscount(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		ShoppingCart shoppingCart = _getShopingCart(request);
		String discountString = request.getParameter("discount");
		String[] discounts = discountString.split(":");
		ActionMessages messages = new ActionMessages();
		SimpleDateFormat dateFormat = new SimpleDateFormat(WebKeys.DateFormats.EXP_IMP_DATE);
		
		for(String discount : discounts)
		{
			discount = discount.trim();
			DiscountCode discountCode = DiscountCodeFactory.getDiscountCodeById(discount);
			
			if (InodeUtils.isSet(discountCode.getInode())) {
				java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
				long now = calendar.getTimeInMillis();
				long startTime = discountCode.getStartDate().getTime();
				long endTime = discountCode.getEndDate().getTime();
				
				if ((startTime <= now) && (now <= endTime)) {
					if(InodeUtils.isSet(discountCode.getInode()))
					{
						shoppingCart.deleteDiscountCode(discount);
						shoppingCart.addDiscount(discountCode);
					}
				} else {
					
					messages.add("message" ,new ActionMessage ("message.shoppingcart.invalidDiscountDate", dateFormat.format(discountCode.getStartDate()), dateFormat.format(discountCode.getEndDate())));
				}
			}
		}
		
		saveMessages(request, messages);
		ActionForward af = mapping.findForward("viewCart");
		return af;
	}
	
	public ActionForward removeDiscount(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		ShoppingCart shoppingCart = _getShopingCart(request);
		String discountString = request.getParameter("inode");

		shoppingCart.deleteDiscountCode(discountString);
		
		ActionForward af = mapping.findForward("viewCart");
		return af;
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		ShoppingCart shoppingCart = _getShopingCart(request);
		List<Holder> holders = shoppingCart.getHolders();
		ArrayList itemsToRemove = new ArrayList();
		for(Holder holder : holders)
		{
			String formatInode = holder.getFormat().getInode();
			String  quantityString = request.getParameter(formatInode);
			if(UtilMethods.isInt(quantityString))
			{
				int quantity = Integer.parseInt(quantityString);
				holder.setQuantity(quantity);
				
				if (quantity == 0) {
					itemsToRemove.add(formatInode);
				}
			}			
		}

		for (int i=0; i<itemsToRemove.size(); i++) {
			String itemToRemove = (String) (itemsToRemove.get(i));
			shoppingCart.removeItem(itemToRemove);
		}

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

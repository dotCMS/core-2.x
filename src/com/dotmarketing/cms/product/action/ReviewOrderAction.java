package com.dotmarketing.cms.product.action;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessor;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.cms.product.model.taxes.TaxCalculationProcessor;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.viewtools.ProductWebAPI;

public class ReviewOrderAction extends DispatchAction
{
	protected ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{	
		ActionForward af = mapping.findForward("reviewOrder");
		return af;	
	}

	public ActionForward loadAmounts(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{	
		HttpSession session = request.getSession();
		//Is Partner
		boolean isPartner = false;
		if (session.getAttribute("isPartner") != null &&
				session.getAttribute("isPartner").equals("true"))
		{
			isPartner = true;
		}

		OrderForm orderForm =(OrderForm) lf;

		// if we don't have a cart
		ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute(WebKeys.SHOPPING_CART);
		if(shoppingCart ==null || shoppingCart.getHolders() ==null || shoppingCart.getHolders().size() ==0){
			ActionErrors ae = new ActionErrors();
			ae.add(Globals.ERROR_KEY,new ActionMessage("message.order_manager.shopping_cart_empty"));
			saveMessages(request, ae);
			ActionForward af = new ActionForward("/dotCMS/viewCart");
			af.setRedirect(true);
			return af;
		}
		
		
		List<Holder> holders = shoppingCart.getHolders();
		List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
		//Calculate the price each and the subTotals by holder
		float orderSubTotal = 0F;
		for(Holder holder : holders)
		{
			ProductFormat format = holder.getFormat();
			int quantity = holder.getQuantity();
			ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);
			float price = (isPartner) ? productPrice.getPartnerPrice() : productPrice.getRetailPrice();
			//float price = (isPartner) ? productPrice.getPartnerPriceWithDiscount() : productPrice.getRetailPriceWithDiscount();
			holder.setPrice(price);
			float lineTotal = quantity * price;
			orderSubTotal += lineTotal;
			holder.setLineTotal(lineTotal);
		}
		orderForm.setOrderSubTotal(orderSubTotal);
		String discountCodes = "";
		for(DiscountCode discount : discounts)
		{
			discountCodes += discount.getCodeId() + ":";			
		}
		if (UtilMethods.isSet(discountCodes))
		{
			discountCodes = discountCodes.substring(0,discountCodes.lastIndexOf(":"));
		}

		orderForm.setDiscountCodes(discountCodes);

		//Discount
		float orderDiscount = 0;
		ProductWebAPI pwa = new ProductWebAPI();
		orderDiscount = pwa.getTotalApplicableDiscount(holders,discounts,isPartner);
		orderForm.setOrderDiscount(orderDiscount);

		//SubTotal - Discount
		float orderSubTotalDiscount = orderSubTotal - orderDiscount;
		orderSubTotalDiscount = (orderSubTotalDiscount < 0 ? 0 : orderSubTotalDiscount);
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);
		
		//float orderSubTotalDiscount = orderSubTotal;
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);

		//taxes
		TaxCalculationProcessor tax = TaxCalculationProcessor.getInstance();
		float orderTax = 	tax.calculateTax(shoppingCart,  orderSubTotalDiscount, orderForm.getShippingCountry(), orderForm.getShippingState(), orderForm.getShippingZip(), orderForm.getTaxExemptNumber());
		orderForm.setOrderTax(orderTax);

		//shipping and handling
		ShippingCalculationProcessor ship = ShippingCalculationProcessor.getInstance();
		float orderShippingValue = 	ship.calculateShipping(shoppingCart, orderForm.getOrderShipType(), orderForm.getShippingCountry(), orderForm.getShippingZip());
		float handlingValue = ship.calculateHandling(shoppingCart);
		orderForm.setOrderShipping(orderShippingValue + handlingValue);


		//Total
		float orderTotal = orderSubTotalDiscount + orderTax + orderShippingValue;
 		orderForm.setOrderTotal(orderTotal);

		//Generate the Token
		generateToken(request);
		saveToken(request);

		ActionForward af = mapping.findForward("reviewOrder");
		return af;	
	}

	public ActionForward validate(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{	
		return 	loadAmounts(mapping,lf,request,response);
	}




}

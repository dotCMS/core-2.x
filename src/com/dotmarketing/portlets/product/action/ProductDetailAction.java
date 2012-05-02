package com.dotmarketing.portlets.product.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.order_manager.struts.OrderItemForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;

public class ProductDetailAction extends DispatchAction{
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		
		OrderForm form = (OrderForm) lf;
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		getOrderInfo(form, request, response);
		return mapping.findForward("productDetailPage");
	}
	
	public ActionForward back(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		
		HttpSession session = request.getSession();
		
		session.removeAttribute("webEventRegistrationForm");
		
		if (session.getAttribute(WebKeys.CMS_USER) == null) {
			return new ActionForward ("/dotCMS/login");
		}
		
		if (!InodeUtils.isSet(request.getParameter("inode"))) {
			ActionForward forward = new ActionForward ("/dotCMS/myAccount");
			return forward;
		}
		
		String btn_type = (String) request.getParameter("btn_type");
		if(UtilMethods.isSet(btn_type) && btn_type.equals("save")){
			ActionMessages messages = new ActionMessages ();
			messages.add("message" ,new ActionMessage ("error.productDetail.saved"));
			saveMessages(request, messages);
		}
		return mapping.findForward("registrationHistoryPage");
		
	}
	
	@SuppressWarnings("unchecked")
	public void getOrderInfo(OrderForm form, HttpServletRequest request, HttpServletResponse response){
		
		String inode = (String) request.getParameter("inode");
		Order order = OrderFactory.getOrderById(inode);
		
		/*Order Info*/
		form.setInode(order.getInode());
		form.setOrderStatus(order.getOrderStatus());
		form.setOrderShipping(order.getOrderShipping());
		form.setOrderTax(order.getOrderTax());
		form.setOrderSubTotal(order.getOrderSubTotal());
		form.setOrderTotal(order.getOrderTotal());
		form.setOrderDiscount(order.getOrderDiscount());
		form.setTrackingNumber(order.getTrackingNumber());
		
		/*Billing Info*/
		form.setBillingAddress1(order.getBillingAddress1());
		form.setBillingAddress2(order.getBillingAddress2());
		form.setBillingCity(order.getBillingCity());
		form.setBillingState(order.getBillingState());
		form.setBillingZip(order.getBillingZip());
		form.setBillingCountry(order.getBillingCountry());
		form.setBillingPhone(order.getBillingPhone());
		
		/*Shipping Info*/
		form.setShippingAddress1(order.getShippingAddress1());
		form.setShippingAddress2(order.getShippingAddress2());
		form.setShippingCity(order.getShippingCity());
		form.setShippingState(order.getShippingState());
		form.setShippingZip(order.getShippingZip());
		form.setShippingCountry(order.getShippingCountry());
		form.setShippingPhone(order.getShippingPhone());
		
		/*Payment Information*/
		form.setPaymentType(order.getPaymentType());
		form.setNameOnCard(order.getNameOnCard());
		form.setCardType(order.getCardType());
		form.setCardExpMonth(order.getCardExpMonth());
		form.setCardExpYear(order.getCardExpYear());
		form.setCardNumber(order.getCardNumber());
		form.setCheckBankName(order.getCheckBankName());
		form.setCheckNumber(order.getCheckNumber());
		form.setDiscountCodes(order.getDiscountCodes());
		form.setOrderSubTotalDiscount(order.getOrderSubTotal());
		
		List<OrderItemForm> currentOrderItems = new ArrayList<OrderItemForm>();
		List<OrderItem> orderItemList = OrderItemFactory.getOrderItemsByOrderId(order.getInode());
		java.util.Iterator orderItemsIter = orderItemList.iterator();
		
		while(orderItemsIter.hasNext()){
			try {
				
				OrderItem orderItem = (OrderItem) orderItemsIter.next();
				OrderItemForm orderItemForm = new OrderItemForm ();
				BeanUtils.copyProperties(orderItemForm, orderItem);
				currentOrderItems.add(orderItemForm);
				
			} catch (IllegalAccessException e) {
				Logger.error(this,e.getMessage(),e);
			} catch (InvocationTargetException e) {
				Logger.error(this,e.getMessage(),e);
			}
			
			
		}
		
		form.setOrderItemList(currentOrderItems);
		
		
	}
}

package com.dotmarketing.cms.product.action;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cms.factories.PublicAddressFactory;
import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.viewtools.ProductWebAPI;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.User;

public class CheckOutAction extends DispatchAction
{
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {		
		ActionForward af = null;
		User userSession = (User) request.getSession().getAttribute(WebKeys.CMS_USER);
		if (userSession == null) {		
			//UserProxy proxy = UserProxyFactory.getUserProxy(userSession);
			//request.setAttribute("userProxyInode", proxy.getInode());
			request.setAttribute("from", "purchaseOrder");
			String url = request.getRequestURI() + "?dispatch=afterLogin";
			request.setAttribute("referrer",url);
			request.getSession().setAttribute(WebKeys.REDIRECT_AFTER_LOGIN,url);
			af = mapping.findForward("registrantInfoPage");
			return af;
		}		
		else
		{
			
			return afterLogin(mapping,lf,request,response);
		}		
	}
	
	public ActionForward afterLogin(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		
		HttpSession session = request.getSession();
		ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(WebKeys.SHOPPING_CART);
		User userSession = (User) session.getAttribute(WebKeys.CMS_USER);
		
		if(shoppingCart ==null || userSession == null){
			ActionForward af = new ActionForward("/");
			af.setRedirect(true);
			return af;
		}
		
		OrderForm orderForm = (OrderForm) lf;
		//Getting the user from the session
		String userId = userSession.getUserId();
		String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
		
		//get liferay user
		User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
		
		orderForm.setBillingFirstName(user.getFirstName());
		orderForm.setBillingLastName(user.getLastName());
		orderForm.setBillingContactName(user.getFullName());
		orderForm.setBillingContactEmail(user.getEmailAddress());

		UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
		Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy,Organization.class);
		List addresses;
		Address address;
		boolean haveOrganization = false;
		if (InodeUtils.isSet(organization.getInode())){
			haveOrganization = true;
			//Billing Address
			orderForm.setBillingAddressType("Work");
			orderForm.setBillingAddress1(organization.getStreet1());
			orderForm.setBillingAddress2(organization.getStreet2());
			orderForm.setBillingCity(organization.getCity());
			orderForm.setBillingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
			orderForm.setBillingState(organization.getState());
			orderForm.setBillingZip(organization.getZip());
			orderForm.setBillingContactPhone(organization.getPhone());
			
			//Work Address
			orderForm.setWorkAddress1(organization.getStreet1());
			orderForm.setWorkAddress2(organization.getStreet2());
			orderForm.setWorkCity(organization.getCity());
			orderForm.setWorkCountry("United States of America");
			orderForm.setWorkState(organization.getState());
			orderForm.setWorkZip(organization.getZip());
			orderForm.setWorkFirstName(user.getFirstName());
			orderForm.setWorkLastName(user.getLastName());
			orderForm.setWorkContactName(user.getFullName());
			orderForm.setWorkContactPhone(organization.getPhone());
			orderForm.setWorkContactEmail(user.getEmailAddress());
			orderForm.setWorkLabel(organization.getTitle()!=null?organization.getTitle():"");
			
			//Shipping Address
			orderForm.setShippingAddressType("Work");
			orderForm.setShippingAddress1(organization.getStreet1());
			orderForm.setShippingAddress2(organization.getStreet2());
			orderForm.setShippingCity(organization.getCity());
			orderForm.setShippingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
			orderForm.setShippingState(organization.getState());
			orderForm.setShippingZip(organization.getZip());
			orderForm.setShippingPhone(organization.getPhone());
			orderForm.setShippingFax(organization.getFax());
			orderForm.setShippingLabel(organization.getTitle()!=null?organization.getTitle():"");
		}
		
		addresses = PublicAddressFactory.getAddressesByUserId(user.getUserId());
		int posAux = -1;
		for (int i=0; i<addresses.size();i++){
			address = (Address) addresses.get(i);
			if ((address.getDescription() != null) && (address.getDescription().toLowerCase().equals("home"))){
				orderForm.setHomeAddress1(address.getStreet1());
				orderForm.setHomeAddress2(address.getStreet2());
				orderForm.setHomeCity(address.getCity());
				orderForm.setHomeCountry(address.getCountry());
				if (address.getCountry()==null || address.getCountry().equals("United States of America")) {
					orderForm.setHomeCountry("United States of America");
					orderForm.setHomeState(address.getState());
				}	
				else{
					orderForm.setHomeState("otherCountry");
					orderForm.setHomeStateOtherCountryText(address.getState());
				}
				orderForm.setHomeZip(address.getZip());
				orderForm.setHomeFirstName(user.getFirstName());
				orderForm.setHomeLastName(user.getLastName());
				orderForm.setHomeContactName(user.getFullName());
				orderForm.setHomeContactPhone(address.getPhone());
				orderForm.setHomeContactEmail(user.getEmailAddress());
				orderForm.setHomePhone(address.getPhone());
				orderForm.setHomeFax(address.getFax());
			}
			if ((address.getDescription() != null) && (address.getDescription().toLowerCase().equals("work"))){
				posAux = i;
				if (!(haveOrganization)){
					orderForm.setWorkAddress1(address.getStreet1());
					orderForm.setWorkAddress2(address.getStreet2());
					orderForm.setWorkCity(address.getCity());
					orderForm.setWorkCountry(address.getCountry());
					if (address.getCountry()==null || address.getCountry().equals("United States of America")) {
						orderForm.setWorkCountry("United States of America");
						orderForm.setWorkState(address.getState());
					}	
					else{
						orderForm.setWorkState("otherCountry");
						orderForm.setWorkStateOtherCountryText(address.getState());
					}
					orderForm.setWorkZip(address.getZip());
					orderForm.setWorkFirstName(user.getFirstName());
					orderForm.setWorkLastName(user.getLastName());
					orderForm.setWorkContactName(user.getFullName());
					orderForm.setWorkContactPhone(address.getPhone());
					orderForm.setWorkContactEmail(user.getEmailAddress());
					orderForm.setWorkPhone(address.getPhone());
					orderForm.setWorkFax(address.getFax());
				}
			}
		}
		if (!(haveOrganization)){
			if (posAux == -1)
				posAux = 0;
			if (addresses.size() > 0) {
				address = (Address) addresses.get(posAux);
			}
			else {
				address = PublicAddressFactory.getInstance();
			}
			
			//Billing Address
			orderForm.setBillingAddressType(address.getDescription());
			orderForm.setBillingAddress1(address.getStreet1());
			orderForm.setBillingAddress2(address.getStreet2());
			orderForm.setBillingCity(address.getCity());
			orderForm.setBillingCountry(address.getCountry());
			if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))) {
				orderForm.setBillingState(address.getState());
				orderForm.setBillingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
			}
			else{
				orderForm.setBillingState("otherCountry");
				orderForm.setBillingStateOtherCountryText(address.getState());
			}
			orderForm.setBillingZip(address.getZip());
			orderForm.setBillingContactPhone(address.getPhone());
			
			//Shipping Address
			orderForm.setShippingAddressType(address.getDescription());
			orderForm.setShippingAddress1(address.getStreet1());
			orderForm.setShippingAddress2(address.getStreet2());
			orderForm.setShippingCity(address.getCity());
			orderForm.setShippingCountry(address.getCountry());
			if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))) {
				orderForm.setShippingState(address.getState());
				orderForm.setShippingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
			}
			else{
				orderForm.setShippingState("otherCountry");
				orderForm.setShippingStateOtherCountryText(address.getState());
			}
			orderForm.setShippingZip(address.getZip());
			orderForm.setShippingPhone(address.getPhone());
			orderForm.setShippingFax(address.getFax());
			orderForm.setShippingLabel("");
		}
		
		//save the order----------------------------------

		DotHibernate.startTransaction();
		Date now = new Date();
		Order order;
		List<Holder> holders;
		String newOrderInode = (String) session.getAttribute("newOrderInode");
		if (newOrderInode == null)
		{
			order = new Order();
		}
		else
		{
			order = OrderFactory.getOrderById(newOrderInode);
			List<OrderItem> orderItems = OrderItemFactory.getAllOrderItemsByParentOrder(order);
			for(OrderItem orderItem : orderItems)
			{
				OrderItemFactory.deleteOrderItem(orderItem);
			}
		}
		order.setUserInode(userProxy.getInode());
		order.setDatePosted(now);
		order.setLastModDate(now);
		//Billing
		order.setBillingAddress1(orderForm.getBillingAddress1());
		order.setBillingAddress2(orderForm.getBillingAddress2());
		order.setBillingCity(orderForm.getBillingCity());
		if (orderForm.getBillingState() != null){
		if (orderForm.getBillingState().equals("otherCountry")){
			order.setBillingState(orderForm.getBillingStateOtherCountryText());
			orderForm.setBillingState(orderForm.getBillingStateOtherCountryText());
		}
		else
			order.setBillingState(orderForm.getBillingState());
		}
		order.setBillingZip(orderForm.getBillingZip());
		order.setBillingCountry(orderForm.getBillingCountry());
		order.setBillingPhone(orderForm.getBillingPhone());
		order.setBillingFax(orderForm.getBillingFax());
		order.setBillingFirstName(orderForm.getBillingFirstName());
		order.setBillingLastName(orderForm.getBillingLastName());
		order.setBillingContactName(orderForm.getBillingContactName());
		order.setBillingContactPhone(orderForm.getBillingContactPhone());
		order.setBillingContactEmail(orderForm.getBillingContactPhone());
		//Shipping
		order.setShippingAddress1(orderForm.getShippingAddress1());
		order.setShippingAddress2(orderForm.getShippingAddress2());
		order.setShippingLabel(orderForm.getShippingLabel());
		order.setShippingCity(orderForm.getShippingCity());
		if (orderForm.getShippingState().equals("otherCountry"))
			order.setShippingState(orderForm.getShippingStateOtherCountryText());
		else
			order.setShippingState(orderForm.getShippingState());
		order.setShippingZip(orderForm.getShippingZip());
		order.setShippingCountry(orderForm.getShippingCountry());
		order.setShippingPhone(orderForm.getShippingPhone());
		order.setShippingFax(orderForm.getShippingFax());
		int orderStatus = Config.getIntProperty("ECOM_ORDER_INCOMPLETE");
		order.setOrderStatus(orderStatus);
		int paymentStatus = Config.getIntProperty("ECOM_PAY_WAITING");
		order.setPaymentStatus(paymentStatus);
		//Set Discount Codes
		List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
		String discountCodes = "";
		for(DiscountCode discount : discounts)
		{
			discountCodes += discount.getCodeId() + ":";			
		}
		if (UtilMethods.isSet(discountCodes))
		{
			discountCodes = discountCodes.substring(0,discountCodes.lastIndexOf(":"));
		}
		order.setDiscountCodes(discountCodes);
		orderForm.setDiscountCodes(discountCodes);
		
		order.setPaymentType("1");
		order.setNameOnCard("");
		order.setCardType("");				
		order.setCardNumber(""); 
		order.setCardExpMonth(0);
		order.setCardExpYear(0);
		order.setCardVerificationValue(""); 
		order.setOrderTotalPaid(0);
		order.setOrderShipType(0);
		
		//Save the Order
		OrderFactory.saveOrder(order);
		orderForm.setInode(order.getInode());
		boolean isPartner = false;
    	if (session.getAttribute("isPartner") != null &&
    		session.getAttribute("isPartner").equals("true"))
    	{
    		isPartner = true;
    	}

		
    	float orderSubTotal = 0F;
		holders = shoppingCart.getHolders();
		for(Holder holder : holders)
		{
			ProductFormat format = holder.getFormat();
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderInode(order.getInode());
			orderItem.setProductInode(format.getInode());
			orderItem.setItemQty(holder.getQuantity());
			int quantity = holder.getQuantity();
			ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);
			float price = (isPartner) ? productPrice.getPartnerPrice() : productPrice.getRetailPrice();
			orderItem.setItemPrice(price);
			float lineTotal = quantity * price;
			orderSubTotal += lineTotal;
			OrderItemFactory.saveOrderItem(orderItem);
			holder.setInode(orderItem.getInode());
		}
		//Discount
		float orderDiscount = 0;
		ProductWebAPI pwa = new ProductWebAPI();
		orderDiscount = pwa.getTotalApplicableDiscount(holders,discounts,isPartner);
		orderForm.setOrderDiscount(orderDiscount);
		order.setOrderDiscount(orderDiscount);
		//SubTotal - Discount
		float orderSubTotalDiscount = orderSubTotal - orderDiscount;
		orderSubTotalDiscount = (orderSubTotalDiscount < 0 ? 0 : orderSubTotalDiscount);
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);
		//float orderSubTotalDiscount = orderSubTotal;
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);
		order.setOrderTotalDue(orderSubTotal);
		order.setOrderSubTotal(orderSubTotal); 
		order.setOrderTotal(orderSubTotal);
		//Save the Order
		OrderFactory.saveOrder(order);
		session.setAttribute("newOrderInode",String.valueOf(order.getInode()));
		//------------------------------------------------
		DotHibernate.commitTransaction();	
		ActionForward af = mapping.findForward("checkOut");
		return af;	
	}
	
	
	public ActionForward reload(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		ActionForward af = mapping.findForward("checkOut");
		return af;
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		
		User user1 =  (User) request.getSession(false).getAttribute(WebKeys.CMS_USER);
		OrderForm orderForm = (OrderForm) lf;
		ActionErrors ae = orderForm.validate(user1);
		if(ae != null && ae.size() > 0)
		{
			saveMessages(request, ae);
			ActionForward af = mapping.getInputForward();
			return af;
		}

		ActionForward af = mapping.findForward("reviewOrder");
		return af;		
	}

	// private User _getUser(ActionRequest req) {
		// TODO Auto-generated method stub
	// return null;
	// }	
}
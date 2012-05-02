/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.order_manager.action;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessor;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.cms.product.model.taxes.TaxCalculationProcessor;
import com.dotmarketing.cms.product.model.taxes.TaxCalculationProcessorException;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.order_manager.struts.OrderItemForm;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.viewtools.ProductWebAPI;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author Salvador Di  Nardo
 *  
 */
public class EditOrderAction extends DotPortletAction {
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {
		Logger.debug(this,"START VIEW ORDERS ACTION");
		String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "";
		String referrer = req.getParameter("referrer");		
		if (referrer == null)
			referrer = req.getParameter("referer");
		if ((referrer!=null) && (referrer.length()!=0))
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");			
		}
		DotHibernate.startTransaction();

		if ((cmd != null) && cmd.equals("update_shipping")) {
			try 
			{    		  
				_updateShipping(mapping,form,config,req,res);
				setForward(req,"portlet.ext.order_manager.edit_order");
				return;
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}

		if ((cmd != null) && cmd.equals("update_discounts")) {
			try 
			{    		  
				_updateOrderDiscounts(mapping,form,config,req,res);
				setForward(req,"portlet.ext.order_manager.edit_order");
				return;
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}

		if ((cmd != null) && cmd.equals(Constants.UPDATE)) {
			try 
			{    		  
				_updateOrders(mapping,form,config,req,res);	

				_sendToReferral(req,res,referrer);
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}
		else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
			try {
				_removeOrder(mapping,form,config,req,res);
				//_refreshOrder(form);
				if (referrer != null){
					_sendToReferral(req,res,referrer);
					return;
				}
			} catch (Exception ae) {
				_handleException(ae, req);
			}			
		}	
		else if	 ((cmd != null) && cmd.equals(Constants.EDIT)) 
		{
			try {
				_editOrder(mapping,form,config,req,res);
				setForward(req,"portlet.ext.order_manager.edit_order");
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
			}			
		}		
		else if	 ((cmd != null) && cmd.equals(Constants.SAVE)) 
		{
			try {
				OrderForm orderForm = (OrderForm) form;
				User user1=com.liferay.portal.util.PortalUtil.getUser(req);
				ActionErrors ae = orderForm.validateBackEndEdit(user1);
				if (ae.size() == 0)
				{
					_saveOrder(req,res,config,form);
					SessionMessages.add(req, "message", "message.order_manager.order_updated");
					setForward(req,"portlet.ext.order_manager.edit_order");
					//_sendToReferral(req,res,referrer);
				}
				else
				{
					req.setAttribute(Globals.ERROR_KEY, ae);
					setForward(req,"portlet.ext.order_manager.edit_order");
				}
				return;

			} catch (Exception ae) {
				_handleException(ae, req);
			}			
		}
		else if	 ((cmd != null) && cmd.equals("deleteitem")) 
		{
			try {				
				_deleteItem(req,res,config,form);
				SessionMessages.add(req, "message", "message.order_manager.item_deleted");
				if (referrer != null)
					_sendToReferral(req,res,referrer);
				else
				{
					_refreshOrder(form);
					setForward(req,"portlet.ext.order_manager.edit_order");
				}
				return;

			} catch (Exception ae) {
				_handleException(ae, req);
			}			
		}
		DotHibernate.commitTransaction();    

		setForward(req,"portlet.ext.order_manager.view");
		Logger.debug(this,"END VIEW ORDERS ACTION");
	}	

	private void _updateShipping(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req, ActionResponse res) throws Exception {
		_refreshOrder(form);
		_loadAmount(req,res,config,form);	

	}

	private void _updateOrderDiscounts(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req, ActionResponse res) throws Exception {
		_refreshOrder(form);
		_loadAmount(req,res,config,form);
	}

	private ShoppingCart createShoppingCart(OrderForm orderForm) {
		ShoppingCart sp = new ShoppingCart();
		List<OrderItemForm> orderItems =  orderForm.getOrderItemList();
		OrderItemForm orderItem;
		Holder holder;
		List<Holder> holders = new ArrayList();
		for (int i=0;i<orderItems.size();i++){
			orderItem = orderItems.get(i);
			holder = new Holder();
			holder.setQuantity(orderItem.getItemQty());
			holder.setFormat(ProductFormatFactory.getProductFormat(orderItem.getProductInode()));
			holders.add(holder);
		}
		sp.setHolders(holders);
		sp.setDiscountCodes(UtilMethods.getDiscountsByOrder(orderForm));
		return sp;
	}
	private void _updateOrders(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response)
	{		
		Enumeration parameters = request.getParameterNames();
		Hashtable orders = new Hashtable<String,Order>();
		while(parameters.hasMoreElements())
		{			
			String orderInode = "";
			int value = 0;
			Order order = null;
			String parameterName = (String) parameters.nextElement();
			if (parameterName.indexOf("orderStatus_") != -1 || 
					parameterName.indexOf("paymentStatus_") != -1)
			{
				//search the order, value, etc
				orderInode = parameterName.split("_")[1];
				if (orders.containsKey(orderInode))
				{
					order = (Order) orders.get(orderInode);
				}
				else
				{
					order = OrderFactory.getOrderById(orderInode);
					orders.put(orderInode,order);
				}
				value = Integer.parseInt(request.getParameter(parameterName));
				if (parameterName.indexOf("orderStatus") != -1)
				{
					order.setOrderStatus(value);
				}						
				else if (parameterName.indexOf("paymentStatus") != -1)
				{
					order.setPaymentStatus(value);
				}				
			}			
		}
		Enumeration ordersEnum = orders.keys();
		while (ordersEnum.hasMoreElements())
		{
			Order order = (Order) orders.get(ordersEnum.nextElement()); 
			OrderFactory.saveOrder(order);
		}		
	}

	private void _removeOrder(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response)
	{		
		String orderInode = request.getParameter("inode");
		Order order = OrderFactory.getOrderById(orderInode);

		List<OrderItem> orderItems = OrderItemFactory.getAllOrderItemsByParentOrder(order);
		for(OrderItem orderItem : orderItems)
		{
			ProductFormat format = ProductFormatFactory.getProductFormat(orderItem.getProductInode());
			if (InodeUtils.isSet(format.getInode())){
				format.setInventoryQuantity(format.getInventoryQuantity() + orderItem.getItemQty());
				ProductFormatFactory.saveProductFormat(format);
			}
			OrderItemFactory.deleteOrderItem(orderItem);
		}
		OrderFactory.deleteOrder(order);
	}

	private void _editOrder(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response) throws Exception
	{		
		String orderInode = request.getParameter("inode");		
		String reload = request.getParameter("reload");


		Order order = OrderFactory.getOrderById(orderInode);
		User user = order.getUser();

		if (!UtilMethods.isSet(reload))
		{
			List<OrderItem> orderItems = OrderItemFactory.getAllOrderItemsByParentOrder(order);

			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setInode(order.getInode());


			shoppingCart.setUser(user);
			UserProxy up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			Organization org = (Organization) InodeFactory.getParentOfClass(up, Organization.class);
			boolean isPartner = ((org.getPartnerKey() != null && !org.getPartnerKey().equals("") )? true : false);    	
			List<Holder> holders = shoppingCart.getHolders();
			List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
			for(OrderItem orderItem : orderItems)
			{
				ProductFormat format = ProductFormatFactory.getProductFormat(orderItem.getProductInode());

				int quantity = orderItem.getItemQty();
				float price = orderItem.getItemPrice();
				float lineTotal = quantity * price;
				Holder holder = new Holder();
				holder.setFormat(format);
				holder.setLineTotal(lineTotal);
				holder.setPrice(price);
				holder.setQuantity(quantity);
				holder.setInode(orderItem.getInode());
				ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);
				//if (isPartner)
				//	holder.setPrice(productPrice.getPartnerPriceWithDiscount());
				//else
				//	holder.setPrice(productPrice.getRetailPriceWithDiscount());
				holders.add(holder);
			}

			String discountsString = order.getDiscountCodes();
			String[] discountsStrings = discountsString.split(":");
			for(String discountString: discountsStrings)
			{
				discountString = discountString.trim();
				if(UtilMethods.isSet(discountString))
				{
					DiscountCode discountCode = DiscountCodeFactory.getDiscountCodeById(discountString);
					discounts.add(discountCode);
				}
			}

			HttpSession session =  ((ActionRequestImpl) request).getHttpServletRequest().getSession();				
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);			
		}


		OrderForm orderForm = (OrderForm) form;
		orderForm.setUserId(user.getUserId());
		User backendUser;
		String backendUserName = "";
		try{
			backendUser = APILocator.getUserAPI().loadUserById(order.getBackendUser(),APILocator.getUserAPI().getSystemUser(),false);
			if (backendUser != null)
				backendUserName = (backendUser.getFirstName() != null ? backendUser.getFirstName() + " " : "") + (backendUser.getLastName() != null ? backendUser.getLastName() + " " : "") + (backendUser.getEmailAddress() != null ? "(" + backendUser.getEmailAddress() + ")" : ""); 
		}
		catch(Exception ex){

		}
		orderForm.setBackendUserName(backendUserName);
		/*orderForm.setInode(order.getInode());
		orderForm.setUserInode(order.getUserInode());
		orderForm.setOrderStatus(order.getOrderStatus());
		orderForm.setPaymentStatus(order.getPaymentStatus());
		orderForm.setDatePosted(order.getDatePosted());*/
		try
		{
			BeanUtils.copyProperties(orderForm,order);

			orderForm.setOrderSubTotal(orderForm.getOrderSubTotal() - orderForm.getOrderDiscount());
			//Set the paymentType
			String[] paymentTypes = Config.getStringArrayProperty("EREG_PAYMENT_TYPES");
			int creditCard = Config.getIntProperty(paymentTypes[0]); 
			int check = Config.getIntProperty(paymentTypes[1]);
			int paymentType = Integer.parseInt(orderForm.getPaymentType());
			if (paymentType == creditCard){
				orderForm.setPaymentType("cc");
			}
			else if (paymentType == check){			
				orderForm.setPaymentType("ch");
			}
			else{			
				orderForm.setPaymentType("po");
			}
			if (UtilMethods.isSet(reload))
			{
				_loadAmount(request,response,config,orderForm,true);
			}

			if (!(order.getShippingCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))))
			{
				orderForm.setShippingState("otherCountry");
				orderForm.setShippingStateOtherCountryText(order.getShippingState());
			}
			if (!(order.getBillingCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))))
			{
				orderForm.setBillingState("otherCountry");
				orderForm.setBillingStateOtherCountryText(order.getBillingState());
			}
			//add the contact information
			UserProxy orderUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(order.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
			user = APILocator.getUserAPI().loadUserById(orderUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
			orderForm.setContactName(user.getFullName()!=null?user.getFullName():"");
			orderForm.setContactEmail(user.getEmailAddress()!=null?user.getEmailAddress():"");
			if (InodeUtils.isSet(orderUser.getInode())) {
				Organization organization = (Organization) InodeFactory.getParentOfClass(orderUser,Organization.class);
				if (InodeUtils.isSet(organization.getInode())) {
					orderForm.setContactFacility(organization.getTitle().trim());
					Organization parentSystem = OrganizationFactory.getParentOrganization(organization);
					if (InodeUtils.isSet(parentSystem.getInode())) {
						orderForm.setContactSystem(parentSystem.getTitle().trim());
					}
				}
			}     

		}
		catch(Exception ex)
		{
			Logger.debug(this,ex.toString());
		}		
	}

	private void _refreshOrder(ActionForm form) throws Exception
	{		
		OrderForm orderForm = (OrderForm) form;	
		Order order = OrderFactory.getOrderById(String.valueOf(orderForm.getInode()));
		User user = order.getUser();
		orderForm.setUserId(user.getUserId());			
	}

	public void _saveOrder(ActionRequest request,ActionResponse response,PortletConfig config,ActionForm form) throws Exception 
	{
		HttpServletRequest httpServletRequest = ((ActionRequestImpl) request).getHttpServletRequest();
		HttpSession session = httpServletRequest.getSession();
		ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(WebKeys.SHOPPING_CART);
		OrderForm orderForm = (OrderForm) form;
		_refreshOrder(form);
		boolean changeTotal = _loadAmount(request,response,config,form);
		DotHibernate.startTransaction();
		try
		{	
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,null);
			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,null);					

			User user = (User) shoppingCart.getUser();
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			Date now = new Date();

			//### ORDER ###
			String orderInode = shoppingCart.getInode();
			Order order = OrderFactory.getOrderById(orderInode);
			order.setUserInode(userProxy.getInode());
			//order.setDatePosted(now);
			order.setLastModDate(now);
			order.setInvoiceNumber(orderForm.getInvoiceNumber());
			//Billing
			order.setBillingAddress1(orderForm.getBillingAddress1());
			order.setBillingAddress2(orderForm.getBillingAddress2());
			order.setBillingCity(orderForm.getBillingCity());
			if (orderForm.getBillingState().equals("otherCountry"))
				order.setBillingState(orderForm.getBillingStateOtherCountryText());
			else
				order.setBillingState(orderForm.getBillingState());
			order.setBillingZip(orderForm.getBillingZip());
			order.setBillingCountry(orderForm.getBillingCountry());
			if (orderForm.getBillingPhone() != null)
				order.setBillingPhone(orderForm.getBillingPhone());
			order.setBillingFax(orderForm.getBillingFax());
			order.setBillingContactName(orderForm.getBillingContactName());
			order.setBillingContactPhone(orderForm.getBillingContactPhone());
			order.setBillingContactEmail(orderForm.getBillingContactEmail());
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
			if (orderForm.getShippingPhone() != null)
				order.setShippingPhone(orderForm.getShippingPhone());
			order.setShippingFax(orderForm.getShippingFax());
			//Payment
			///order.setPaymentType(orderForm.getPaymentType());
			//CC
			if (orderForm.getPaymentType().equals("cc"))
			{
				order.setNameOnCard(orderForm.getNameOnCard());
				String cardType = orderForm.getCardType();				
				String[] creditCardTypes = Config.getStringArrayProperty("EREG_CREDIT_CARD_TYPES"); 
				if (cardType.equals("vs"))
				{
					cardType = creditCardTypes[0];
				}
				else if (cardType.equals("mc"))
				{
					cardType = creditCardTypes[1];
				}
				else if (cardType.equals("dc"))
				{
					cardType = creditCardTypes[2];
				}
				else if (cardType.equals("ae"))
				{
					cardType = creditCardTypes[3];
				}
				order.setCardType(cardType);
				order.setCardNumber(UtilMethods.obfuscateCreditCard(orderForm.getCardNumber())); //Obfuscate the CC Number
				order.setCardExpMonth(orderForm.getCardExpMonth());
				order.setCardExpYear(orderForm.getCardExpYear());
				order.setCardVerificationValue(""); //this value haven't to be saved
				order.setPaymentType(Config.getStringProperty("ECOM_CREDIT_CARD"));				
			}
			else if (orderForm.getPaymentType().equals("ch"))
			{	
				//Check 
				order.setCheckNumber(orderForm.getCheckNumber());
				order.setCheckBankName(orderForm.getCheckBankName());
				order.setPaymentType(Config.getStringProperty("ECOM_CHECK"));							
			}
			else if (orderForm.getPaymentType().equals("po"))
			{
				//PO
				order.setPoNumber(orderForm.getPoNumber());
				order.setPaymentType(Config.getStringProperty("ECOM_PURCHASE_ORDER"));
			}

			order.setOrderTotalPaid(orderForm.getOrderTotalPaid());
			float totalDue = orderForm.getOrderTotal() - orderForm.getOrderTotalPaid();			
			order.setOrderTotalDue(totalDue);

			int paymentStatus = 0;
			if (orderForm.getPaymentStatus() == Config.getIntProperty("ECOM_PAY_INVOICED"))
				order.setPaymentStatus(Config.getIntProperty("ECOM_PAY_INVOICED"));
			else{
				if (changeTotal)
				{				
					if (totalDue > 0)
					{
						paymentStatus = Config.getIntProperty("ECOM_PAY_WAITING");
					}
					else if (totalDue < 0)
					{
						paymentStatus = Config.getIntProperty("ECOM_PAY_REIMBURSEMENT");
					}
					else
					{
						paymentStatus = Config.getIntProperty("ECOM_PAY_PAID");
					}				
				}
				else
				{
					paymentStatus = orderForm.getPaymentStatus();
				}
				order.setPaymentStatus(paymentStatus);
			}
			int orderStatus = orderForm.getOrderStatus();
			order.setOrderStatus(orderStatus);

			//TaxExceptionNumber
			order.setTaxExemptNumber(orderForm.getTaxExemptNumber());
			//Total Amounts
			order.setOrderSubTotal(orderForm.getOrderSubTotal()); 
			order.setOrderDiscount(orderForm.getOrderDiscount());
			order.setOrderShipping(orderForm.getOrderShipping());
			order.setOrderShipType(orderForm.getOrderShipType());
			order.setOrderTax(orderForm.getOrderTax());
			order.setOrderTotal(orderForm.getOrderTotal());
			order.setModified_QB(true);
			order.setModified_FH(true);
			//Discount Codes
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

			//Save the Order
			OrderFactory.saveOrder(order);
			orderForm.setInode(order.getInode());
			//### END OORDER ###

			//### ORDER ITEM ###
			int itemQuantity;
			int inventoryQuantity;
			List<Holder> holders = shoppingCart.getHolders();
			for(Holder holder : holders)
			{
				ProductFormat format = holder.getFormat();
				OrderItem orderItem = new OrderItem();
				itemQuantity = 0;
				if (InodeUtils.isSet(holder.getInode()))
				{
					orderItem = OrderItemFactory.GetOrderItemById(holder.getInode());
					itemQuantity = orderItem.getItemQty();
				}
				orderItem.setOrderInode(order.getInode());
				orderItem.setProductInode(format.getInode());
				orderItem.setItemQty(holder.getQuantity());
				orderItem.setItemPrice(holder.getPrice());
				OrderItemFactory.saveOrderItem(orderItem);
				holder.setInode(orderItem.getInode());
				if (holder.getQuantity() != itemQuantity){
					inventoryQuantity = format.getInventoryQuantity();
					format.setInventoryQuantity(inventoryQuantity + itemQuantity - holder.getQuantity());
					ProductFormatFactory.saveProductFormat(format);
				}
			}			

			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,form);
			DotHibernate.commitTransaction();		
			//Send Email
			try
			{
				/*String to = user.getEmailAddress();			
				String from = user.getEmailAddress();
				String subject = "Thanks for ordering with Dotmarketing";
				//Body
				setForward(request,"portlet.ext.order_manager.view_checkOut");			
				String path = "/application/products/invoice_email." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
				Host host =  HostFactory.getCurrentHost(request);
				String hostName = host.getHostname();
				//hostName = "localhost:35000";
				String URL = "http://" + hostName + path + "?";
				URL += "orderInode=" + order.getInode(); 
				StringBuffer writer = UtilMethods.getURL(URL);
				String body = writer.toString().trim();			
				sendEmail(to,from,subject,body);*/			
			}
			catch(Exception ex)
			{
				Logger.debug(this,ex.toString());
			}					
			setForward(request,"portlet.ext.order_manager.view_invoice");
		}		
		catch (Exception e) 
		{
			//determine the error
			Logger.error(this,"Unknown Error placing a credit card order: "+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			ActionMessages ae = new ActionMessages();
			ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.unknown"));
			//Save the error in session
			saveErrors(httpServletRequest, ae);
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS, ae);
			//Generate a new token
			generateToken(httpServletRequest);
			saveToken(httpServletRequest);
			//forward
			setForward(request,"portlet.ext.order_manager.view_invoice");
		}
	}
	private boolean _loadAmount(ActionRequest request,ActionResponse response,PortletConfig config,ActionForm form)
	{
		String changeQualityString = request.getParameter("changeQuantity");
		boolean changeQuantity = (UtilMethods.isSet(changeQualityString) ? Boolean.parseBoolean(changeQualityString) : false); 
		return _loadAmount( request, response, config, form,changeQuantity);
	}

	private boolean _loadAmount(ActionRequest request,ActionResponse response,PortletConfig config,ActionForm form,boolean changeQuantity)
	{
		HttpSession session = ((ActionRequestImpl)request).getHttpServletRequest().getSession();
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		//Determine changes
		boolean changeTotal = false;

		String changeBillingAddressString = request.getParameter("changeBillingAddress");	
		String changeShippingAddressString = request.getParameter("changeShippingAddress");
		String changeTaxExceptionNumberString = request.getParameter("changeTaxExceptionNumber");
		String changeDiscountString = request.getParameter("changeDiscount");
		String changeShippingTypeString = request.getParameter("changeShippingType");

		boolean changeBillingAddress = (UtilMethods.isSet(changeBillingAddressString) ? Boolean.parseBoolean(changeBillingAddressString) : false);
		boolean changeShippingAddress = (UtilMethods.isSet(changeShippingAddressString) ? Boolean.parseBoolean(changeShippingAddressString) : false);
		boolean changeTaxExceptionNumber = (UtilMethods.isSet(changeTaxExceptionNumberString) ? Boolean.parseBoolean(changeTaxExceptionNumberString) : false);
		boolean changeDiscount = (UtilMethods.isSet(changeDiscountString) ? Boolean.parseBoolean(changeDiscountString) : false);
		boolean changeShippingType = (UtilMethods.isSet(changeShippingTypeString) ? Boolean.parseBoolean(changeShippingTypeString) : false);
		//Update the Holders quantity
		if(changeQuantity)
		{
			_saveShoppingCart(form,config,request,response);
		}
		//Update the Discounts		
		_saveDiscounts(form,config,request,response);

		User userSession = shoppingCart.getUser();
		OrderForm orderForm = (OrderForm) form;
		//Getting the user from the session

		String userId = userSession.getUserId();

		//Load Ammounts		
		//Is Partner		
		UserProxy up;
		try {
			up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false);
		} catch (Exception e1) {
			throw new DotRuntimeException(e1.getMessage(), e1);
		}
		Organization org = (Organization) InodeFactory.getParentOfClass(up, Organization.class);
		boolean isPartner = ((org.getPartnerKey() != null && !org.getPartnerKey().equals("") )? true : false);    	

		List<Holder> holders = shoppingCart.getHolders();
		List<DiscountCode> discounts = shoppingCart.getDiscountCodes();
		//Calculate the price each and the subTotals by holder
		float orderSubTotal = 0F;
		for(Holder holder : holders)
		{
			ProductFormat format = holder.getFormat();
			int quantity = holder.getQuantity();
			float price = 0;
			if (InodeUtils.isSet(holder.getInode()))
			{
				price = holder.getPrice();
			}
			else
			{	
				try
				{
					ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);			
					price = (isPartner) ? productPrice.getPartnerPrice() : productPrice.getRetailPrice();
				}
				catch(Exception ex)
				{
					Logger.debug(this, ex.toString());
					price = Float.MAX_VALUE;
				}
				holder.setPrice(price);
				changeQuantity = true;
			}
			float lineTotal = quantity * price;
			orderSubTotal += lineTotal;
			holder.setLineTotal(lineTotal);
		}
		orderForm.setOrderSubTotal(orderSubTotal);

		//Discount
		float orderDiscount = 0;
		ProductWebAPI pwa = new ProductWebAPI();
		orderDiscount = pwa.getTotalApplicableDiscount(holders,discounts,isPartner);
		orderForm.setOrderDiscount(orderDiscount);

		//SubTotal - Discount
		float orderSubTotalDiscount = orderSubTotal - orderDiscount;
		orderSubTotalDiscount = (orderSubTotalDiscount < 0 ? 0 : orderSubTotalDiscount);
		//float orderSubTotalDiscount = orderSubTotal;
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);
		request.setAttribute("orderSubTotal",String.valueOf(orderSubTotalDiscount));
		//Tax		
		float orderTax = orderForm.getOrderTax();
		if(changeQuantity || changeBillingAddress || changeTaxExceptionNumber)
		{			
			//taxes
			TaxCalculationProcessor tax;
			try {
				tax = TaxCalculationProcessor.getInstance();
				orderTax = 	tax.calculateTax(shoppingCart,  orderSubTotalDiscount, orderForm.getShippingCountry(), orderForm.getShippingState(), orderForm.getShippingZip(), orderForm.getTaxExemptNumber());
				orderForm.setOrderTax(orderTax);
				request.setAttribute("orderTax",String.valueOf(orderTax));
			} catch (TaxCalculationProcessorException e) {
				// TODO Auto-generated catch block
				Logger.error(this.getClass(), "Cannot get Tax Processor");
			}

		}

		//Shipping
		float orderShippingValue = orderForm.getOrderShipping();
		if(changeQuantity || changeShippingAddress || changeShippingType)
		{
			//shipping and handling
			ShippingCalculationProcessor ship;
			try {
				ship = ShippingCalculationProcessor.getInstance();
				orderShippingValue = 	ship.calculateShipping(shoppingCart, orderForm.getOrderShipType(), orderForm.getShippingCountry(), orderForm.getShippingZip());
				float handlingValue = ship.calculateHandling(shoppingCart);
				orderForm.setOrderShipping(orderShippingValue + handlingValue);
				request.setAttribute("orderShipping",String.valueOf(orderForm.getOrderShipping()));
			} catch (Exception e) {
				Logger.error(this.getClass(), "Cannot get Shipping Processor");
			}
		}

		//Total
		if (changeQuantity || changeBillingAddress || changeBillingAddress || changeTaxExceptionNumber || changeDiscount || changeShippingType)
		{
			float orderTotal = orderSubTotalDiscount + orderTax + orderShippingValue;
			orderTotal = Math.round(orderTotal * 100) / (float) 100;
			orderForm.setOrderTotal(orderTotal);
			changeTotal = true;
			request.setAttribute("orderTotal",String.valueOf(orderTotal));
		}
		return changeTotal;
	}

	private void _saveShoppingCart(ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response)
	{
		HttpSession session = ((ActionRequestImpl)request).getHttpServletRequest().getSession();
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);

		Enumeration parameters = request.getParameterNames();		
		while(parameters.hasMoreElements())
		{			
			String HolderInode = "";
			int quantity = 0;	
			String parameterName = (String) parameters.nextElement();
			if (parameterName.indexOf("holder") != -1)				
			{
				//search the order, value, etc
				try
				{
					HolderInode = (parameterName.split("_")[1]);				
					quantity = Integer.parseInt(request.getParameter(parameterName));
					shoppingCart.setQuantityItemByHolderInode(quantity,HolderInode);
				}
				catch(Exception ex)
				{
					Logger.debug(this,ex.toString());
				}
			}			
		}	
	}

	private void _saveDiscounts(ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response)
	{
		HttpSession session = ((ActionRequestImpl)request).getHttpServletRequest().getSession();
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		shoppingCart.getDiscountCodes().clear();

		OrderForm orderForm = (OrderForm) form;
		String discountsString = orderForm.getDiscountCodes();
		String[] discountIds = discountsString.split(":");
		for(int i = 0;i < discountIds.length;i++)
		{
			String discountId = discountIds[i];
			DiscountCode discountCode = DiscountCodeFactory.getDiscountCodeById(discountId);
			if(InodeUtils.isSet(discountCode.getInode()))
			{
				shoppingCart.addDiscount(discountCode);
			}
		}
	}

	private void _deleteItem(ActionRequest req,ActionResponse res,PortletConfig config,ActionForm form)
	{
		String formatInodeString = req.getParameter("formatInode");
		String formatInode = formatInodeString;		

		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);

		Holder holder = shoppingCart.findHolder(formatInode);
		ProductFormat format = holder.getFormat();
		if (InodeUtils.isSet(holder.getInode()))
		{
			OrderItem orderItem = OrderItemFactory.GetOrderItemById(holder.getInode());
			format.setInventoryQuantity(format.getInventoryQuantity() + orderItem.getItemQty());
			OrderItemFactory.deleteOrderItem(orderItem);
			ProductFormatFactory.saveProductFormat(format);
		}
		shoppingCart.removeItem(formatInode);	
	}
}

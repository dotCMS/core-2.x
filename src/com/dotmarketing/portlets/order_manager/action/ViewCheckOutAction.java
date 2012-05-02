/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.order_manager.action;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessor;
import com.dotmarketing.cms.creditcard.model.linkpoint.LinkPointCreditCardProcessorException;
import com.dotmarketing.cms.creditcard.model.linkpoint.LinkPointCreditCardProcessorResponse;
import com.dotmarketing.cms.factories.PublicAddressFactory;
import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessor;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.cms.product.model.taxes.TaxCalculationProcessor;
import com.dotmarketing.cms.product.model.taxes.TaxCalculationProcessorException;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.viewtools.ProductWebAPI;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;

/**
 * @author Salvador Di Nardo
 *  
 */
public class ViewCheckOutAction extends DotPortletAction {
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {
		Logger.debug(this,"START LOAD PRODUCTS ACTION");		
		String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "";
		String referrer = req.getParameter("referrer");	
		OrderForm orderForm = (OrderForm) form;			


		if ((referrer!=null) && (referrer.length()!=0))
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");			
		}		

		DotHibernate.startTransaction();		

		if ((cmd != null) && cmd.equals(Constants.VIEW)) {		
			try 
			{
				_retrieveCheckOut(req, res, config, form);
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}   
		}
		/*
		 * Save the format occurrence 
		 */
		else if ((cmd != null) && cmd.equals(Constants.UPDATE)) {
			try 
			{    		  
				_loadAmount(req,res,config,form);				
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}
		else if ((cmd != null) && cmd.equals(Constants.ADD)) {
			try 
			{    
				User user1=com.liferay.portal.util.PortalUtil.getUser(req);
				ActionMessages ae = orderForm.validateBackEnd(user1);
				if(ae != null && ae.size() > 0)
				{	
					HttpServletRequest httpServletRequest = ((ActionRequestImpl) req).getHttpServletRequest();
					HttpSession session = httpServletRequest.getSession();
					session.setAttribute(WebKeys.SHOPPING_CART_ERRORS, ae);
					generateToken(httpServletRequest);
					saveToken(httpServletRequest);
					saveErrors(httpServletRequest,ae);
					setForward(req,"portlet.ext.order_manager.view_checkOut");					
				}
				else
				{
					if (_saveOrder(req,res,config,form))
						return;
				}
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}

		DotHibernate.commitTransaction();    

		OrderForm orderFormAux = new OrderForm();		
		BeanUtils.copyProperties(orderFormAux,orderForm);

		req.setAttribute("orderFormAux",orderFormAux);

		setForward(req, "portlet.ext.order_manager.view_checkOut");
		Logger.debug(this,"END LOAD PRODUCTS ACTION");
	}		

	private void _retrieveCheckOut(ActionRequest request,ActionResponse res,PortletConfig config,ActionForm form)
	{
		try
		{
			HttpSession session = ((ActionRequestImpl)request).getHttpServletRequest().getSession();
			ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);		

			User userSession = shoppingCart.getUser();
			OrderForm orderForm = (OrderForm) form;
			//Getting the user from the session

			String userId = userSession.getUserId();
			String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
			//get liferay user
			User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
			List addresses = PublicAddressFactory.getAddressesByUserId(user.getUserId());
			int posAux = -1;
			for (int i=0; i<addresses.size();i++){
				Address address = (Address) addresses.get(i);
				if ((address.getDescription() != null) && (address.getDescription().toLowerCase().equals("home"))){
					orderForm.setHomeAddress1(address.getStreet1());
					orderForm.setHomeAddress2(address.getStreet2());
					orderForm.setHomeCity(address.getCity());
					orderForm.setHomeCountry(address.getCountry());
					if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))) {
						orderForm.setHomeCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
						orderForm.setHomeState(address.getState());
					}	
					else{
						orderForm.setHomeState("otherCountry");
						orderForm.setHomeStateOtherCountryText(address.getState());
					}
					orderForm.setHomeZip(address.getZip());
					orderForm.setHomeContactName(user.getFullName());
					orderForm.setHomeContactPhone(address.getPhone());
					orderForm.setHomeContactEmail(user.getEmailAddress());
					orderForm.setHomePhone(address.getPhone());
					orderForm.setHomeFax(address.getFax());
				}
				if ((address.getDescription() != null) && (address.getDescription().toLowerCase().equals("work"))){
					posAux = i;
					orderForm.setWorkAddress1(address.getStreet1());
					orderForm.setWorkAddress2(address.getStreet2());
					orderForm.setWorkCity(address.getCity());
					orderForm.setWorkCountry(address.getCountry());
					if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))) {
						orderForm.setWorkCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
						orderForm.setWorkState(address.getState());
					}	
					else{
						orderForm.setWorkState("otherCountry");
						orderForm.setWorkStateOtherCountryText(address.getState());
					}
					orderForm.setWorkZip(address.getZip());
					orderForm.setWorkContactName(user.getFullName());
					orderForm.setWorkContactPhone(address.getPhone());
					orderForm.setWorkContactEmail(user.getEmailAddress());
					orderForm.setWorkPhone(address.getPhone());
					orderForm.setWorkFax(address.getFax());
				}
			}
			Address address;
			//take the work address if it exists
			if (posAux > -1)
				address = (Address) addresses.get(posAux);
			else
				address = (Address) addresses.get(0);
			//Billing Address
			orderForm.setBillingAddressType(address.getDescription());
			orderForm.setBillingAddress1(address.getStreet1());
			orderForm.setBillingAddress2(address.getStreet2());
			orderForm.setBillingCity(address.getCity());
			orderForm.setBillingCountry(address.getCountry());
			if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))) {
				orderForm.setBillingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
				orderForm.setBillingState(address.getState());
			}	
			else{
				orderForm.setBillingState("otherCountry");
				orderForm.setBillingStateOtherCountryText(address.getState());
			}
			orderForm.setBillingZip(address.getZip());
			orderForm.setBillingContactName(user.getFullName());
			orderForm.setBillingContactPhone(address.getPhone());
			orderForm.setBillingContactEmail(user.getEmailAddress());
			orderForm.setBillingPhone(address.getPhone());
			orderForm.setBillingFax(address.getFax());

			//Shipping Address
			orderForm.setShippingAddressType(address.getDescription());
			orderForm.setShippingAddress1(address.getStreet1());
			orderForm.setShippingAddress2(address.getStreet2());
			orderForm.setShippingCity(address.getCity());
			if (address.getCountry()==null || address.getCountry().equals(Config.getStringProperty("DEFAULT_COUNTRY_CODE"))){
				orderForm.setShippingCountry(Config.getStringProperty("DEFAULT_COUNTRY_CODE"));
				orderForm.setShippingState(address.getState());
			}
			else{
				orderForm.setShippingState("otherCountry");
				orderForm.setShippingStateOtherCountryText(address.getState());
				orderForm.setShippingCountry(address.getCountry());
			}
			orderForm.setShippingZip(address.getZip());
			orderForm.setShippingPhone(address.getPhone());
			orderForm.setShippingFax(address.getFax());
			String addressType = address.getDescription();
			if (addressType != null)
				if (addressType.equals("work")){
					UserProxy up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false);
					Organization org = (Organization) InodeFactory.getParentOfClass(up, Organization.class);
					if (InodeUtils.isSet(org.getInode())){
						orderForm.setShippingLabel(org.getTitle()!=null?org.getTitle():"");
					}
				}
			_loadAmount(request,res,config,form);			
		}
		catch(Exception ex)
		{
			Logger.debug(this,ex.toString());
		}		
	}

	public boolean _saveOrder(ActionRequest request,ActionResponse response,PortletConfig config,ActionForm form) 
	{
		HttpServletRequest httpServletRequest = ((ActionRequestImpl) request).getHttpServletRequest();
		HttpSession session = httpServletRequest.getSession();
		if(!isTokenValid(httpServletRequest))
		{
			int i = 0;
			for(i = 0;i < 120;i++)
			{
				if (session.getAttribute(WebKeys.SHOPPING_CART_ERRORS) != null)
				{						
					generateToken(httpServletRequest);
					saveToken(httpServletRequest);
					saveErrors(httpServletRequest,(ActionMessages)session.getAttribute(WebKeys.SHOPPING_CART_ERRORS));

					setForward(request,"portlet.ext.order_manager.view_checkOut");
					return true;
				}
				if (session.getAttribute(WebKeys.SHOPPING_CART_ORDER_FORM) != null)
				{											
					setForward(request,"portlet.ext.order_manager.view_invoice");
					return true;
				}
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception ex)
				{
					Logger.debug(this,ex.toString());
				}
			}
			//Time Out Error
			if (i == 120)
			{
				ActionMessages ae = new ActionMessages();				
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.timeout"));
				session.setAttribute(WebKeys.SHOPPING_CART_ERRORS, ae);
				generateToken(httpServletRequest);
				saveToken(httpServletRequest);
				setForward(request,"portlet.ext.order_manager.view_checkOut");
				return true;
			}
		}
		OrderForm orderForm = (OrderForm) form;		
		//resetToken(httpServletRequest);
		DotHibernate.startTransaction();
		try
		{	
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,null);
			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,null);					
			ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(WebKeys.SHOPPING_CART);
			User user = (User) shoppingCart.getUser();
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			Date now = new Date();

			//### ORDER ###
			Order order = new Order();
			order.setUserInode(userProxy.getInode());
			order.setDatePosted(now);
			order.setLastModDate(now);
			User backendUser = _getUser(request);
			if (backendUser != null)
				order.setBackendUser(backendUser.getUserId());
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

				order.setOrderTotalPaid(orderForm.getOrderTotal());
				order.setOrderTotalDue(0);
				int paymentStatus = Config.getIntProperty("ECOM_PAY_PAID");
				order.setPaymentStatus(paymentStatus);
				order.setPaymentType(Config.getStringProperty("ECOM_CREDIT_CARD"));

				/*CreditCardProcessorResponse ccResponse = com.dotmarketing.factories.creditcard.LinkPointCreditCardProcessor.processCreditCardOrderProducts(order.getInode(), 
						null,
						(orderForm.getOrderTax() != 0 ? false : true),
						request.getRemoteUser(),
						orderForm.getOrderSubTotalDiscount(),
						orderForm.getOrderTax() !=0 ? (Float)orderForm.getOrderTax() : null, 
						orderForm.getOrderShipping(),
						orderForm.getOrderTotal(),
						orderForm.getCardNumber(),
						orderForm.getCardExpMonth() + 1, 
						orderForm.getCardExpYear(),
						orderForm.getCardVerificationValue(), 
						user.getFullName(),
						null,
						orderForm.getBillingAddress1(),
						orderForm.getBillingAddress2(),
						orderForm.getBillingCity(),
						orderForm.getBillingState(),
						orderForm.getBillingZip(), 
						Config.getStringProperty("US_COUNTRY_CODE"),
						orderForm.getBillingContactPhone(), 
						null,
						orderForm.getBillingContactEmail(),
						Config.getStringProperty("WEB_EVENT_REGISTRATION_COMMENTS"), 
						"");*/

				Date expirationDate = new Date();
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(Calendar.YEAR,orderForm.getCardExpYear());
				gc.set(Calendar.MONTH,orderForm.getCardExpMonth());
				gc.set(Calendar.DATE,gc.getActualMaximum(Calendar.DATE));
				expirationDate = gc.getTime();

				//LinkPointCreditCardProcessor lpccp = new LinkPointCreditCardProcessor();
				CreditCardProcessor lpccp = CreditCardProcessor.getInstance();
				lpccp.setOrderId(order.getInode());
				lpccp.setTaxExempt((orderForm.getOrderTax() != 0 ? false : true));
				lpccp.setClientIPAddress(request.getRemoteUser());
				lpccp.setDiscount(orderForm.getOrderDiscount());
				lpccp.setTax(orderForm.getOrderTax());
				lpccp.setShipping(orderForm.getOrderShipping());
				lpccp.setAmount(orderForm.getOrderTotalPaid());
				lpccp.setCreditCardNumber(orderForm.getCardNumber());
				lpccp.setCreditCardExpirationDate(expirationDate);
				lpccp.setCreditCardCVV(orderForm.getCardVerificationValue());
				lpccp.setBillingFirstName(orderForm.getBillingContactName());
				lpccp.setBillingStreet(orderForm.getBillingAddress1());
				lpccp.setBillingStreet2(orderForm.getBillingAddress2());
				lpccp.setBillingCity(orderForm.getBillingCity());
				lpccp.setBillingState(orderForm.getBillingState());
				lpccp.setBillingZip(orderForm.getBillingZip());
				lpccp.setBillingCountry(Config.getStringProperty("US_COUNTRY_CODE"));
				lpccp.setBillingPhone(orderForm.getBillingContactPhone());
				lpccp.setBillingEmailAdress(orderForm.getBillingContactEmail());
//				lpccp.setOrderComments(Config.getStringProperty("WEB_EVENT_REGISTRATION_COMMENTS"));
				lpccp.setOrderComments(Config.getStringProperty("WEB_ORDER_COMMENTS"));
				LinkPointCreditCardProcessorResponse ccResponse = (LinkPointCreditCardProcessorResponse) lpccp.process();

				if (!ccResponse.orderApproved()) {
					//session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,form);
					//DotHibernate.rollbackTransaction();
					ActionMessages ae = new ActionMessages();
					ae.add(Globals.ERROR_KEY, new ActionMessage(
							"error.cc_processing.card.denied", ccResponse.getError()));
					session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,ae);
					generateToken(httpServletRequest);
					saveToken(httpServletRequest);
					saveErrors(httpServletRequest, ae);
					setForward(request,"portlet.ext.order_manager.view_checkOut");
					return false;		
				}
			}
			else if (orderForm.getPaymentType().equals("ch"))
			{	
				//Check 
				order.setCheckNumber(orderForm.getCheckNumber());
				order.setCheckBankName(orderForm.getCheckBankName());

				order.setOrderTotalPaid(0);
				order.setOrderTotalDue(orderForm.getOrderTotal());		
				int paymentStatus = Config.getIntProperty("ECOM_PAY_WAITING");
				order.setPaymentStatus(paymentStatus);
				order.setPaymentType(Config.getStringProperty("ECOM_CHECK"));
			}
			else if (orderForm.getPaymentType().equals("po"))
			{
				//PO
				order.setPoNumber(orderForm.getPoNumber());

				order.setOrderTotalPaid(0);
				order.setOrderTotalDue(orderForm.getOrderTotal());		
				int paymentStatus = Config.getIntProperty("ECOM_PAY_INVOICED");
				order.setPaymentStatus(paymentStatus);
				order.setPaymentType(Config.getStringProperty("ECOM_PURCHASE_ORDER"));
			}

			int orderStatus = Config.getIntProperty("ECOM_ORDER_PLACED");
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
			List<Holder> holders = shoppingCart.getHolders();
			for(Holder holder : holders)
			{
				ProductFormat format = holder.getFormat();

				OrderItem orderItem = new OrderItem();
				orderItem.setOrderInode(order.getInode());
				orderItem.setProductInode(format.getInode());
				orderItem.setItemQty(holder.getQuantity());
				orderItem.setItemPrice(holder.getPrice());
				OrderItemFactory.saveOrderItem(orderItem);
				int inventoryQuantity = format.getInventoryQuantity();
				format.setInventoryQuantity(inventoryQuantity - holder.getQuantity());
				ProductFormatFactory.saveProductFormat(format);
			}
			//### END ORDER ITEM ###

			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,form);
			DotHibernate.commitTransaction();		
			//Send Email
			try
			{
				String to = user.getEmailAddress();			
				String from = user.getEmailAddress();
				String subject = "Thanks for ordering with Dotmarketing";
				//Body
				setForward(request,"portlet.ext.order_manager.view_checkOut");			
				String path = "/application/products/invoice_email." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION");
				Host host =  WebAPILocator.getHostWebAPI().getCurrentHost(request);
				String hostName = host.getHostname();
				//hostName = "localhost:35000";
				String URL = "http://" + hostName + path + "?";
				URL += "orderInode=" + order.getInode(); 
				StringBuffer writer = UtilMethods.getURL(URL);
				String body = writer.toString().trim();			
				sendEmail(to,from,subject,body);			
			}
			catch(Exception ex)
			{
				Logger.debug(this,ex.toString());
			}					
			setForward(request,"portlet.ext.order_manager.view_invoice");
		}
		catch (LinkPointCreditCardProcessorException e) 
		{
			//determine the error
			Logger.warn(this,"Credit card processor exception placing a credit card order: "+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			ActionMessages ae = new ActionMessages();
			switch (e.getCode()) 
			{
			case LinkPointCreditCardProcessorException.DATA_MISSING:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.invalid.card.data"));
				break;
			case LinkPointCreditCardProcessorException.COMMUNICATION_ERROR:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.communication.error"));
				break;
			default:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.unknown"));
			}
			//Save the error in session
			saveErrors(httpServletRequest, ae);
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,ae);
			//Generate a new token
			generateToken(httpServletRequest);
			saveToken(httpServletRequest);
			//forward
			setForward(request,"portlet.ext.order_manager.view_checkOut");
			return false;
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
		return true;
	}

	private void _loadAmount(ActionRequest request,ActionResponse response,PortletConfig config,ActionForm form) throws Exception
	{
		HttpSession session = ((ActionRequestImpl)request).getHttpServletRequest().getSession();
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);		

		User userSession = shoppingCart.getUser();
		OrderForm orderForm = (OrderForm) form;
		//Getting the user from the session

		String userId = userSession.getUserId();

		//Load Ammounts		
		//Is Partner		
		UserProxy up = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false);
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
			ProductPrice productPrice = format.getQuantityPrice(quantity,discounts);
			float price = (isPartner) ? productPrice.getPartnerPrice() : productPrice.getRetailPrice();
			//float price = (isPartner) ? productPrice.getPartnerPriceWithDiscount() : productPrice.getRetailPriceWithDiscount();
			holder.setPrice(price);
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
		orderForm.setOrderSubTotalDiscount(orderSubTotalDiscount);
		orderForm.setOrderSubTotalDiscount(orderSubTotal);

		//Tax
		float orderTax = 0;
		if (orderForm.getBillingState() != null){
			//taxes
			TaxCalculationProcessor tax;
			try {
				tax = TaxCalculationProcessor.getInstance();
				orderTax = 	tax.calculateTax(shoppingCart,  orderSubTotalDiscount, orderForm.getShippingCountry(), orderForm.getShippingState(), orderForm.getShippingZip(), orderForm.getTaxExemptNumber());
			} catch (TaxCalculationProcessorException e) {
				// TODO Auto-generated catch block
				Logger.error(this.getClass(), "Cannot get Tax Processor");
			}
		}
		orderForm.setOrderTax(orderTax);
		request.setAttribute("orderTax",orderTax);
		
		
		float orderShippingValue = 0;
		orderForm.setIsShippingZero(false);
		//Shipping

		//shipping and handling
		ShippingCalculationProcessor ship;
		try {
			ship = ShippingCalculationProcessor.getInstance();
			orderShippingValue = 	ship.calculateShipping(shoppingCart, orderForm.getOrderShipType(), orderForm.getShippingCountry(), orderForm.getShippingZip());
			orderShippingValue+= ship.calculateHandling(shoppingCart);
			
			
		} catch (Exception e) {
			
			Logger.error(this.getClass(), "Cannot get Shipping Processor");
		}

		orderForm.setOrderShipping(orderShippingValue);
		request.setAttribute("orderShipping",orderShippingValue);

		//Total
		float orderTotal = orderSubTotalDiscount + orderTax + orderShippingValue;
		orderForm.setOrderTotal(orderTotal);

		//Generate the Token
		HttpServletRequest httpServletRequest = ((ActionRequestImpl) request).getHttpServletRequest();
		generateToken(httpServletRequest);
		saveToken(httpServletRequest);

		setForward(request,"portlet.ext.order_manager.view_checkOut");
	}

	protected static boolean sendEmail(String to, String from, String subject, String body) 
	{
		Mailer m = new Mailer();
		m.setToEmail(to);
		m.setFromEmail(from);
		m.setSubject(subject);
		m.setHTMLBody(body);
		return m.sendMessage();
	}

	protected User _getUser(ActionRequest req) {

		//get the user
		User user = null;
		try {
			user = com.liferay.portal.util.PortalUtil.getUser(req);
		} catch (Exception e) {
			_handleException(e, req);
		}
		return user;

	}

}

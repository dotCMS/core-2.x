package com.dotmarketing.cms.product.action;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessor;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessorException;
import com.dotmarketing.cms.creditcard.model.CreditCardProcessorResponse;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Mailer;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

public class PurchaseAction extends DispatchAction
{
	
	private HostWebAPI hostWebAPI = WebAPILocator.getHostWebAPI();
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		HttpSession session = request.getSession();
		
		// if we don't have a cart
		ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute(WebKeys.SHOPPING_CART);
		if(shoppingCart ==null || shoppingCart.getHolders() ==null || shoppingCart.getHolders().size() ==0){
			ActionErrors ae = new ActionErrors();
			ae.add(Globals.ERROR_KEY,new ActionMessage("message.order_manager.shopping_cart_empty"));
			saveMessages(request, ae);
			ActionForward af = new ActionForward("/dotCMS/viewCart");
			return af;
		}
		
		
		if(!isTokenValid(request)){	
			if("127.0.0.1".equals(request.getRemoteHost())){
				generateToken(request);
				saveToken(request);
			}
			ActionMessages ae = new ActionMessages();	
			ae.add(Globals.ERROR_KEY, new ActionMessage("error.cartAlreadyProcessed"));
			saveMessages(request,ae);
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS, ae);
			ActionForward af = mapping.getInputForward();
			return af;
		}
		
		
		resetToken(request);
		DotHibernate.startTransaction();
		try
		{	
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,null);
			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,null);
			OrderForm orderForm = (OrderForm)lf;			
			User user = (User) session.getAttribute(WebKeys.CMS_USER);
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);

			
			//### ORDER ###
			Date now = new Date();
			Order order = OrderFactory.getOrderById(String.valueOf(orderForm.getInode()));
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
			
			/*
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
			orderForm.setDiscountCodes(discountCodes);
			*/
			
			//Save the Order
			OrderFactory.saveOrder(order);
			orderForm.setInode(order.getInode());
			
			
			/*
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
				format.setInventoryQuantity(format.getInventoryQuantity() -holder.getQuantity());
				ProductFormatFactory.saveProductFormat(format);
			}
			//### END ORDER ITEM ###
			 */
			 
			if (orderForm.getPaymentType().equals("cc"))
			{
				
				
				
				CreditCardProcessor ccp = CreditCardProcessor.getInstance();
				
				Date expirationDate = new Date();
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(Calendar.YEAR,orderForm.getCardExpYear());
				gc.set(Calendar.MONTH,orderForm.getCardExpMonth() -1);						
				gc.set(Calendar.DATE,gc.getActualMaximum(Calendar.DATE));
				expirationDate = gc.getTime();
				

				ccp.setOrderId(order.getInode());
				ccp.setTaxExempt((orderForm.getOrderTax() != 0 ? false : true));
				ccp.setClientIPAddress(request.getRemoteUser());
				ccp.setDiscount(orderForm.getOrderSubTotalDiscount());
				ccp.setTax(orderForm.getOrderTax());
				ccp.setShipping(orderForm.getOrderShipping());
				ccp.setAmount(orderForm.getOrderTotal());
				ccp.setCrditCardType(orderForm.getCardType());
				ccp.setCreditCardNumber(orderForm.getCardNumber());
				ccp.setCreditCardExpirationDate(expirationDate);
				ccp.setCreditCardCVV(orderForm.getCardVerificationValue());
				ccp.setClientIPAddress(request.getRemoteHost());
				
				ccp.setBillingFirstName(orderForm.getBillingFirstName());
				ccp.setBillingLastName(orderForm.getBillingLastName());
				ccp.setBillingCompany(orderForm.getBillingCompany());
				ccp.setBillingStreet(orderForm.getBillingAddress1());
				ccp.setBillingStreet2(orderForm.getBillingAddress2());
				ccp.setBillingCity(orderForm.getBillingCity());
				ccp.setBillingState(orderForm.getBillingState());
				ccp.setBillingZip(orderForm.getBillingZip());
				ccp.setBillingCountry(orderForm.getBillingCountry());
				ccp.setBillingPhone(orderForm.getBillingContactPhone());
				ccp.setBillingEmailAdress(orderForm.getBillingContactEmail());
				
				ccp.setShippingFirstName(orderForm.getBillingFirstName());
				ccp.setShippingLastName(orderForm.getBillingLastName());
				ccp.setShippingCompany(orderForm.getShippingCompany());
				ccp.setShippingStreet(orderForm.getShippingAddress1());
				ccp.setShippingStreet2(orderForm.getShippingAddress2());
				ccp.setShippingCity(orderForm.getShippingCity());
				ccp.setShippingState(orderForm.getShippingState());
				ccp.setShippingZip(orderForm.getShippingZip());
				ccp.setShippingCountry(orderForm.getShippingCountry());
				ccp.setShippingPhone(orderForm.getShippingPhone());


				
				CreditCardProcessorResponse ccResponse = (CreditCardProcessorResponse) ccp.process();
				
				
			
				
				if (!ccResponse.orderApproved()) {
					DotHibernate.rollbackTransaction();
					ActionMessages ae = new ActionMessages();
					ae.add(Globals.ERROR_KEY, new ActionMessage("error.generic.message", ccResponse.getError()));
					saveErrors(request, ae);
					generateToken(request);
					saveToken(request);
					ActionForward af = mapping.getInputForward();
					return af;
				}
			}
			
			session.setAttribute(WebKeys.SHOPPING_CART_ORDER_FORM,lf);
			DotHibernate.commitTransaction();
			session.removeAttribute("newOrderInode");
			
		//Send Email
			try
			{
				String to = user.getEmailAddress();
				String from = user.getEmailAddress();
				
				String cc = Config.getStringProperty("ORDER_MANAGER_CC_INVOICES");
				
//				String subject = "Thanks for ordering with Dotmarketing";
				Company company = PublicCompanyFactory.getDefaultCompany();
				String subject = "Thanks for ordering with " + company.getName();
				
				//Body
				String path = mapping.findForward("invoiceEmail").getPath();
				Logger.info(this, "path="+path);
				
				StringBuffer url = new StringBuffer();
				url.append("http://" + hostWebAPI.getCurrentHost(request).getHostname()) ;
				url.append(":" +request.getServerPort() ) ; 
				
				url.append(path + "?");

				url.append("orderInode=" + order.getInode()); 
				Logger.info(this, "URL="+url);
				StringBuffer writer = UtilMethods.getURL(url.toString());
				String body = writer.toString().trim();
		        Mailer m = new Mailer();
		        m.setToEmail(to);
		        m.setFromEmail(from);
		        m.setBcc(cc);
		        m.setSubject(subject);
		        m.setHTMLBody(body);

		        if(!m.sendMessage()){
		        	throw new DotDataException("Mail failed to send");
		        }
			}
			catch(Exception ex)
			{
				Logger.error(this,ex.toString());
			}
			ActionForward af = mapping.findForward("invoice");
			return af;
		}
//		catch (LinkPointCreditCardProcessorException e)
		catch (CreditCardProcessorException e)
		{
			//determine the error
			Logger.warn(this,"Credit card processor exception placing a credit card order: "+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			ActionMessages ae = new ActionMessages();
			switch (e.getCode()) 
			{
//			case LinkPointCreditCardProcessorException.DATA_MISSING:
			case CreditCardProcessorException.DATA_MISSING:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.invalid.card.data"));
				break;
//			case LinkPointCreditCardProcessorException.COMMUNICATION_ERROR:
			case CreditCardProcessorException.COMMUNICATION_ERROR:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.communication.error"));
				break;
			default:
				ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.unknown"));
			}
			//Save the error in session
			saveErrors(request, ae);
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS,ae);
			//Generate a new token
			generateToken(request);
			saveToken(request);
			//forward
			ActionForward af = mapping.getInputForward();
			return af;
		} 
		catch (Exception e) 
		{
			//determine the error
			Logger.error(this,"Unknown Error placing a credit card order: "+ e.getMessage(), e);
			DotHibernate.rollbackTransaction();
			ActionMessages ae = new ActionMessages();
			ae.add(Globals.ERROR_KEY, new ActionMessage("error.cc_processing.unknown"));
			//Save the error in session
			saveErrors(request, ae);
			session.setAttribute(WebKeys.SHOPPING_CART_ERRORS, ae);
			//Generate a new token
			generateToken(request);
			saveToken(request);
			//forward
			ActionForward af = mapping.getInputForward();
			return af;
		}
	}
	

}

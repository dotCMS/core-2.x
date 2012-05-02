/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.order_manager.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.ViewOrdersForm;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.portlet.ActionResponseImpl;
import com.liferay.portlet.RenderRequestImpl;
/**
 * @author Maria Ahues
 *  
 */
public class ViewOrdersAction extends PortletAction {
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			RenderResponse res) throws Exception {
		String cmd = req.getParameter(Constants.CMD);
		if (cmd != null){
		if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
			_removeOrder(form, req, res);
			cmd = com.liferay.portal.util.Constants.SEARCH;
		}
		}
		if (req.getWindowState().equals(WindowState.NORMAL)) {
			_viewRecentOrders(mapping, form, config, req, res);
			return mapping.findForward("portlet.ext.order_manager.view");
		} else {
			_viewOrders(mapping, form, config, req, res);
			_getOrderStatus(req);
			_getOrderPaymentStatus(req);
			return mapping.findForward("portlet.ext.order_manager.view_orders");
		}
	}
	
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {
		
		String cmd = req.getParameter(Constants.CMD);
		
		if(cmd != null && cmd.equals("exportExcel")){
			_exportExcel(req, res,config,form);
			setForward(req,"portlet.ext.order_manager.view_orders");
		}
		
		if(cmd != null && cmd.equals("exportQB")){
			_exportQB(req, res,config,form);
			setForward(req,"portlet.ext.order_manager.view_orders");
		}
		
	}
	
	
	private void _viewRecentOrders(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
			PortletResponse res) throws Exception {
		
		HttpSession session = ((RenderRequestImpl)req).getHttpServletRequest().getSession();
//		String filter = " shipping_country = '" + Config.getStringProperty("DEFAULT_COUNTRY_CODE") + "' and payment_status in ("+Config.getStringProperty("ECOM_PAY_INVOICED")+","+Config.getStringProperty("ECOM_PAY_PAID")+") and (invoice_number is NULL or invoice_number = '')";
//		List orders = OrderFactory.getTodaysOrders(filter);
		List orders = OrderFactory.getMostRecentOrders(5);
		req.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
		session.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
		
	}
	
	private void _getOrderStatus(PortletRequest req) {
		
		//statuses list
		List<HashMap> finalStatus = new ArrayList<HashMap>();
		String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
		for (int i=0;i<statusesArray.length;i++) {
			String status = statusesArray[i];
			HashMap<String,String> hs = new HashMap<String,String>();
			hs.put("optionName", Config.getStringProperty(status + "_FN"));
			hs.put("optionValue", Config.getStringProperty(status));
			finalStatus.add(hs);
		}
		req.setAttribute("orderStatusList", finalStatus.iterator());
		
		
	}
	
	private void _getOrderPaymentStatus(PortletRequest req) {
		
		//statuses  list
		List<HashMap> finalPaymentStatus = new ArrayList<HashMap>();
		String[] statusesArray = com.dotmarketing.util.Config.getStringArrayProperty("ECOM_PAY_STATUSES");
		for (int i=0;i<statusesArray.length;i++) {
			String status = statusesArray[i];
			HashMap<String,String> hs = new HashMap<String,String>();
			hs.put("optionName", Config.getStringProperty(status + "_FN"));
			hs.put("optionValue", Config.getStringProperty(status));
			finalPaymentStatus.add(hs);
		}
		req.setAttribute("paymentStatusList", finalPaymentStatus.iterator());
	}
	
	private void _viewOrders(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
			PortletResponse res) throws Exception {
		
		HttpSession session = ((RenderRequestImpl)req).getHttpServletRequest().getSession();
		
		ViewOrdersForm vForm = (ViewOrdersForm) form;
		
		String orderInode = vForm.getOrderInode();
		String systemInode = vForm.getSystem();
		String firstName = vForm.getFirstName();
		String lastName = vForm.getLastName();
		String email = vForm.getEmail();
		String facilityInode = vForm.getFacility();
		String facilityTitle = vForm.getFacilityTitle();
		String invoiceNumber = vForm.getInvoiceNumber();
		
		if (!UtilMethods.isSet(facilityTitle))
		{
			facilityInode = "0";
		}
		//int paymentStatus = vForm.getPaymentStatus();
		//int orderStatus = vForm.getOrderStatus();
		int[] orderStatusArray = vForm.getOrderStatusArray();
		int[] paymentStatusArray = vForm.getPaymentStatusArray();
    	Date startDate = vForm.getStartDate();
    	Date endDate = vForm.getEndDate();
		
		//set the systems drop down
		List systems = OrganizationFactory.getAllSystems();
		Iterator systemsIter = systems.iterator();
		req.setAttribute("systems", systemsIter);
		
		//set the facilities drop down
		List facilities = new ArrayList();
		if (InodeUtils.isSet(systemInode)) {
			Organization parentSystem = OrganizationFactory.getOrganization(systemInode);
			facilities = OrganizationFactory.getChildrenOrganizations(parentSystem,"title");
		}
		Iterator facilitiesIter = facilities.iterator();
		req.setAttribute("facilities", facilitiesIter);
		
		//long facilityInodeLong = (InodeUtils.isSet(facilityInode)) ? Long.parseLong(facilityInode) : 0;
		//long systemInodeLong = (InodeUtils.isSet(systemInode)) ? Long.parseLong(systemInode) : 0;
		
		
		//if any of the user filters are set
		if (UtilMethods.isSet(invoiceNumber) || UtilMethods.isSet(firstName) ||  UtilMethods.isSet(lastName) ||  UtilMethods.isSet(email) 
				|| InodeUtils.isSet(facilityInode) || InodeUtils.isSet(systemInode)
				|| paymentStatusArray != null || orderStatusArray != null || InodeUtils.isSet(orderInode) || UtilMethods.isSet(startDate) || UtilMethods.isSet(endDate)) {
			
			//get registrations here.
			List orders = OrderFactory.getFilteredOrders(vForm);
			
			//filter the registrations 
			if (UtilMethods.isSet(firstName) ||  UtilMethods.isSet(lastName)  ||  UtilMethods.isSet(email)  || InodeUtils.isSet(facilityInode) || InodeUtils.isSet(systemInode)) {
				
				List<Order> resultOrders = new ArrayList<Order>();
				Iterator ordersIter = orders.iterator();
				
				while (ordersIter.hasNext()) {
					Order order = (Order) ordersIter.next();
					UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(order.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
					
					String userId = userProxy.getUserId();
					if (userId != null){
					String companyId = com.dotmarketing.cms.factories.PublicCompanyFactory.getDefaultCompany().getCompanyId();
					User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
					
					if (user != null && (user.getFirstName() != null && ((user.getFirstName().toUpperCase().startsWith(firstName.toUpperCase()) || !UtilMethods.isSet(firstName)))
							&& (user.getEmailAddress() != null && (user.getEmailAddress().toUpperCase().startsWith(email.toUpperCase()) || !UtilMethods.isSet(email)))
							&& (user.getLastName() != null && (user.getLastName().toUpperCase().startsWith(lastName.toUpperCase()) || !UtilMethods.isSet(lastName))))) {
						
						if (InodeUtils.isSet(facilityInode)) {
							//if the user selected a facility then i dont need to check the system
							Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
							if (organization.getInode().equalsIgnoreCase(facilityInode)) {
								resultOrders.add(order);
							}
						}
						else {
							//if the user selected a system but not a facility i need to check the system
							if (InodeUtils.isSet(systemInode)) {
								Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
								Organization parentSystem = OrganizationFactory.getParentOrganization(organization);
								if (parentSystem.getInode().equalsIgnoreCase(systemInode)) {
									resultOrders.add(order);
								}
							}
							else {
								//the user didn't select either a facility or system.
								//if the user entered a facility name
								if (UtilMethods.isSet(facilityTitle)) {
									Organization organization = (Organization) InodeFactory.getParentOfClass(userProxy, Organization.class);
									if (organization.getTitle().contains(facilityTitle)) {
										resultOrders.add(order);
									}
								}
								else {
									//for either name, email
									resultOrders.add(order);
								}
							}
						}
					}
				}}
				///filtered by user parameters
				req.setAttribute(WebKeys.ORDER_MGR_VIEW, resultOrders);
				session.setAttribute(WebKeys.ORDER_MGR_VIEW, resultOrders);
			} else {
				///all orders filtered 
				req.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
				session.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
			}
		} else {
			
			///today's orders
//			List orders = OrderFactory.getTodaysOrders(vForm.isOrderOutsideUS());
			List orders = OrderFactory.getMostRecentOrders(20);
			req.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
			session.setAttribute(WebKeys.ORDER_MGR_VIEW, orders);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void _exportQB(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		// THE SAME METHOD USED IN QUICKBOOKS.JAVA
		String condtition = " payment_status in ("+Config.getStringProperty("ECOM_PAY_INVOICED")+","+Config.getStringProperty("ECOM_PAY_PAID")+") and (invoice_number is NULL or invoice_number = '' or modified_QB = " + DbConnectionFactory.getDBTrue() + ")";
		List<Order> orders = (List<Order>) OrderFactory.getOrdersByCondition(condtition);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"quickbooks_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			
			if(orders != null && orders.size() > 0) {
				
				out.print("Web OrderID,First Name,Last Name,Company,Email,Phone,ShipType,Payment Type,Card Type/Check Bank,Card/Check Number,Card ExpMonth,");
				out.print("Card ExpYear,Name,Bill Address1,Bill Address2,Bill City,Bill State,Bill Zip,Bill Country,Ship Name,");
				out.print("Ship Address1,Ship Address2,Ship City,Ship State,Ship Zip,Ship Country,ItemID, Price,Quantity, Shipping");
				out.print("\r\n");
				
				Iterator ordersIter = orders.iterator();
				
				while (ordersIter.hasNext()) {
					Order order = (Order) ordersIter.next();
					if(!InodeUtils.isSet(order.getUserInode()))
						continue;
					UserProxy orderUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(order.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
					User user = APILocator.getUserAPI().loadUserById(orderUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
					
					String orgName = "";
					if (InodeUtils.isSet(orderUser.getInode())) {
						Organization organization = (Organization) InodeFactory.getParentOfClass(orderUser,Organization.class);
							if ((organization != null) && (InodeUtils.isSet(organization.getInode()))){
							if (organization.getTitle()!=null) {
								orgName = organization.getTitle().trim();
							}else{
								orgName = "No Facility";
							}
						}
						else
						orgName = user.getFirstName() + " " + user.getLastName();
					}
					
					
					List<OrderItem> items = OrderItemFactory.getOrderItemsByOrderId(order.getInode());
					Iterator itemsIter = items.iterator();
					String paymentType = "";
					float itemPrice = 0;
					List<DiscountCode> discounts = UtilMethods.getDiscountsByOrder(order);
					while(itemsIter.hasNext()){
						
						OrderItem item = (OrderItem) itemsIter.next();
						
						out.print("\"" +order.getInode()+"\",");
						out.print((user.getFirstName() == null ? "," : "\"" + user.getFirstName() + "\","));
						out.print((user.getLastName() == null ? "," : "\"" + user.getLastName() + "\","));
						out.print("\"" +orgName + "\",");
						out.print((user.getEmailAddress() == null ? "," : "\"" + user.getEmailAddress() + "\","));
						out.print((order.getBillingContactPhone() == null ? "," : "\"" + order.getBillingContactPhone() + "\","));
						
						/*This need to be modify*/
						out.print("\"" + UtilMethods.getShippingTypeName(order.getOrderShipType())+"\",");
						
						/*Payment Information*/
						paymentType = order.getPaymentType().trim(); 
						out.print("\"" + UtilMethods.getPaymentTypeName(Integer.parseInt(paymentType)) +"\",");
						if (paymentType.equalsIgnoreCase("1")){
							out.print((order.getCardType() == null ? "," : "\"" + order.getCardType() + "\","));
							out.print((order.getCardNumber() == null ? "," : "\"" + order.getCardNumber() + "\","));
							out.print((order.getCardExpMonth() == 0 ? "," : "\"" + UtilMethods.getMonthName(order.getCardExpMonth()) + "\","));
							out.print((order.getCardExpYear() == 0 ? "," :"\"" + order.getCardExpYear() + "\","));
							out.print((order.getNameOnCard() == null ? "," : "\"" + order.getNameOnCard() + "\","));
						}
						else if(paymentType.equalsIgnoreCase("2")){
							out.print((order.getCheckBankName() == null ? "," : "\"" + order.getCheckBankName() + "\","));
							out.print((order.getCheckNumber() == null ? "," : "\"" + order.getCheckNumber() + "\","));
							out.print("\"\",");
							out.print("\"\",");
							out.print("\"\",");
						}  
						else
						{
							out.print("\"\",");
							out.print("\"\",");
							out.print("\"\",");
							out.print("\"\",");
							out.print("\"\",");
						}
						
						/*Billing*/
						out.print((order.getBillingAddress1() == null ? "," : "\"" + order.getBillingAddress1() + "\","));
						out.print((order.getBillingAddress2() == null ? "," : "\"" + order.getBillingAddress2() + "\","));
						out.print((order.getBillingCity() == null ? "," : "\"" + order.getBillingCity() + "\","));
						out.print((order.getBillingState() == null ? "," : "\"" + order.getBillingState() + "\","));
						out.print((order.getBillingZip() == null ? "," : "\"" + order.getBillingZip() + "\","));
						out.print((order.getBillingCountry() == null ? "," : "\"" + order.getBillingCountry() + "\","));
						
						/*Shipping*/
						out.print( "\""+user.getFullName()+"\"," );
						out.print((order.getShippingAddress1() == null ? "," : "\"" + order.getShippingAddress1() + "\","));
						out.print((order.getShippingAddress2() == null ? "," : "\"" + order.getShippingAddress2() + "\","));
						out.print((order.getShippingCity() == null ? "," : "\"" + order.getShippingCity() + "\","));
						out.print((order.getShippingState() == null ? "," : "\"" + order.getShippingState() + "\","));
						out.print((order.getShippingZip() == null ? "," : "\"" + order.getShippingZip() + "\","));
						out.print((order.getShippingCountry() == null ? "," : "\"" + order.getShippingCountry() + "\","));
						
						/*Item*/
						//out.print("\"" +item.getInode()+ "\",");
						ProductFormat productFormat = (ProductFormat) ProductFormatFactory.getProductFormat(item.getProductInode());
						out.print("\"" +productFormat.getItemNum()+ "\",");
						itemPrice = UtilMethods.getItemPriceWithDiscount(item,discounts);
						out.print("\"$ " +itemPrice+ "\",");
						out.print("\"" +item.getItemQty()+ "\",");
						out.print("\"$ " +order.getOrderShipping()+ "\",");
						out.print("\r\n");
					}
				}
			}else {
				out.print("There are no Orders to show");
				out.print("\r\n");
			}
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			
			out.print("There are no Orders to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void _exportExcel(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form) throws Exception {
		
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		
		ActionResponseImpl resImpl = (ActionResponseImpl)res;
		HttpServletResponse httpRes = resImpl.getHttpServletResponse();
		
		List<Order> orders = (List<Order>) session.getAttribute(WebKeys.ORDER_MGR_VIEW);
		
		httpRes.setContentType("application/octet-stream");
		httpRes.setHeader("Content-Disposition", "attachment; filename=\"orders_" + UtilMethods.dateToHTMLDate(new Date(),"M_d_yyyy") +".csv\"");
		
		ServletOutputStream out = httpRes.getOutputStream();
		try {
			
			if(orders != null && orders.size() > 0) {
				
				out.print("Invoice,Order Number,Invoice Date,Order Date,First name,Last name,Company,Email,Phone,");
				out.print("Shiptype,Newponumber,Bill Address1,Bill Address2,Bill City,Bill State,Bill Zip,Bill Country,");
				out.print("ShipAddress1,ShipAddress2,ShipCity,ShipState,ShipZip,ShipCountry,ItemId,Price, Discount Codes, Product Name,Quantity,");
				out.print("ShipAmt,TaxExempt,FL Tax");
				out.print("\r\n");
				
				Iterator ordersIter = orders.iterator();
				
				while (ordersIter.hasNext()) {
					Order order = (Order) ordersIter.next();
					if(!InodeUtils.isSet(order.getUserInode()))
						continue;
					if (order.getOrderStatus() == 4)
						continue;
					UserProxy orderUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(order.getUserInode(),APILocator.getUserAPI().getSystemUser(), false);
					User user = APILocator.getUserAPI().loadUserById(orderUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
					
					String orgName = "";
					if (InodeUtils.isSet(orderUser.getInode())) {
						Organization organization = (Organization) InodeFactory.getParentOfClass(orderUser,Organization.class);
						if (organization.getTitle()!=null) {
							orgName = organization.getTitle().trim();
						}else{
							orgName = "No Facility";
						}
					}
					
					List<OrderItem> items = OrderItemFactory.getOrderItemsByOrderId(order.getInode());
					Iterator itemsIter = items.iterator();
					float itemPrice = 0;
					List<DiscountCode> discounts = UtilMethods.getDiscountsByOrder(order);
					
					while(itemsIter.hasNext()){
						
						OrderItem item = (OrderItem) itemsIter.next();
						
						out.print((order.getInvoiceNumber() == null ? "," : "\"" + order.getInvoiceNumber() + "\","));
						out.print("\"" +order.getInode()+"\",");
						out.print("\"" +UtilMethods.dateToHTMLDate(order.getInvoiceDate())+"\",");
						out.print("\"" +UtilMethods.dateToHTMLDate(order.getDatePosted())+"\",");
						out.print((user.getFirstName() == null ? "," : "\"" + user.getFirstName() + "\","));
						out.print((user.getLastName() == null ? "," : "\"" + user.getLastName() + "\","));
						out.print("\"" +orgName + "\",");
						out.print((user.getEmailAddress() == null ? "," : "\"" + user.getEmailAddress() + "\","));
						out.print((order.getBillingContactPhone() == null ? "," : "\"" + order.getBillingContactPhone() + "\","));
						
						out.print("\""+ UtilMethods.getShippingTypeName(order.getOrderShipType())+"\",");
						out.print((order.getPoNumber() == null ? "," : "\"" +order.getPoNumber()+"\","));
						
						/*Billing*/
						out.print((order.getBillingAddress1() == null ? "," : "\"" + order.getBillingAddress1() + "\","));
						out.print((order.getBillingAddress2() == null ? "," : "\"" + order.getBillingAddress2() + "\","));
						out.print((order.getBillingCity() == null ? "," : "\"" + order.getBillingCity() + "\","));
						out.print((order.getBillingState() == null ? "," : "\"" + order.getBillingState() + "\","));
						out.print((order.getBillingZip() == null ? "," : "\"" + order.getBillingZip() + "\","));
						out.print((order.getBillingCountry() == null ? "," : "\"" + order.getBillingCountry() + "\","));
						
						/*Shipping*/
						out.print((order.getShippingAddress1() == null ? "," : "\"" + order.getShippingAddress1() + "\","));
						out.print((order.getShippingAddress2() == null ? "," : "\"" + order.getShippingAddress2() + "\","));
						out.print((order.getShippingCity() == null ? "," : "\"" + order.getShippingCity() + "\","));
						out.print((order.getShippingState() == null ? "," : "\"" + order.getShippingState() + "\","));
						out.print((order.getShippingZip() == null ? "," : "\"" + order.getShippingZip() + "\","));
						out.print((order.getShippingCountry() == null ? "," : "\"" + order.getShippingCountry() + "\","));
						
						/*Item*/
						//out.print("\"" +item.getInode()+ "\",");
						ProductFormat productFormat = (ProductFormat) ProductFormatFactory.getProductFormat(item.getProductInode());
						Product product = (Product) ProductFactory.getProduct(productFormat.getProductInode());
						out.print("\"" +productFormat.getItemNum()+ "\",");
						
						//out.print("\"$ " +order.getOrderDiscount()+ "\",");
						itemPrice = UtilMethods.getItemPriceWithDiscount(item,discounts);
						out.print("\"$ " +itemPrice+ "\",");
						out.print("\"" + order.getDiscountCodes()+ "\",");
						out.print("\"" + product.getTitle() + "(" + productFormat.getFormatName()+ ")\",");
						out.print("\"" +item.getItemQty()+ "\",");
						out.print("\"$ " +order.getOrderShipping()+ "\",");
						out.print((order.getTaxExemptNumber() == null ? "," : "\"" +order.getTaxExemptNumber()+ "\","));
						out.print("\"$ " +order.getOrderTax()+ "\"");
						out.print("\r\n");
					}
				}
			}else {
				out.print("There are no Orders to show");
				out.print("\r\n");
			}
			out.flush();
			out.close();
			DotHibernate.closeSession();
		}catch(Exception p){
			
			out.print("There are no Orders to show");
			out.print("\r\n");
			out.flush();
			out.close();
			DotHibernate.closeSession();
			
		}
	}
	private void _removeOrder(ActionForm form, RenderRequest request, RenderResponse response)
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
}

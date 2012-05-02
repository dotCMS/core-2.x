package com.dotmarketing.portlets.order_manager.factories;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.struts.ViewOrdersForm;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;

/**
 *
 * @author  maria
 */
public class OrderFactory {

	public static java.util.List getAllOrders() {
		DotHibernate dh = new DotHibernate(Order.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' order by last_mod_date desc");
		return dh.list();
	}
	public static java.util.List getTodaysOrders() {
		DotHibernate dh = new DotHibernate(Order.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and last_mod_date >= ? order by last_mod_date desc");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setParam(cal.getTime());
		return dh.list();
	}
	
	public static java.util.List getTodaysOrders(String filter) {
		DotHibernate dh = new DotHibernate(Order.class);
		String query = "";
		if (UtilMethods.isSet(filter))
			query = "from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and last_mod_date >= ? and " + filter + " order by last_mod_date desc";
		else
			query = "from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and last_mod_date >= ? order by last_mod_date desc";
		dh.setQuery(query);
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setParam(cal.getTime());
		return dh.list();
	}
	
	public static java.util.List getTodaysOrders(boolean outsideUS) {
		String shippingCountry = Config.getStringProperty("DEFAULT_COUNTRY_CODE");
		
		DotHibernate dh = new DotHibernate(Order.class);
		if (outsideUS)
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and last_mod_date >= ? and shipping_country <> ? order by last_mod_date desc");
		else
			dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and last_mod_date >= ? and shipping_country = ? order by last_mod_date desc");
		GregorianCalendar cal = new GregorianCalendar ();
		cal.setTime(new Date());
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		dh.setParam(cal.getTime());
		dh.setParam(shippingCountry);
		return dh.list();
	}

	public static java.util.List getAllOrders(String orderby) {
		DotHibernate dh = new DotHibernate(Order.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List getMostRecentOrders(int limit) {
		DotHibernate dh = new DotHibernate(Order.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' order by date_posted desc");
		dh.setMaxResults(limit);
		return dh.list();
	}
	
	public static List getFilteredOrders(ViewOrdersForm vForm) {
		
    	Date startDate = vForm.getStartDate();
    	Date endDate = vForm.getEndDate();
    	String orderInode = vForm.getOrderInode();
    	boolean outsideUS = vForm.isOrderOutsideUS();
		int[] paymentStatusArray = vForm.getPaymentStatusArray();
		int[] orderStatusArray = vForm.getOrderStatusArray();
    	String invoiceNumber = vForm.getInvoiceNumber();
    	
    	String condition = "";
    	
    	if (InodeUtils.isSet(orderInode)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " inode = '" + orderInode + "' ";
    	}
    	if (paymentStatusArray != null) {
    		if (condition.length()>0) condition += " and ";
    		condition += " (";
    		for (int i=0;i<paymentStatusArray.length;i++){
    			if (i > 0)
    				condition += " OR payment_status = " + paymentStatusArray[i];
    			else
    				condition += " payment_status = " + paymentStatusArray[i];
    		}
    		condition += ") ";
    	}
    	   	
    	if (orderStatusArray != null) {
    		if (condition.length()>0) condition += " and ";
    		condition += " (";
    		for (int i=0;i<orderStatusArray.length;i++){
    			if (i > 0)
    				condition += " OR order_status = " + orderStatusArray[i];
    			else
    				condition += " order_status = " + orderStatusArray[i];
    		}
    		condition += ") ";
    	}

    	if (UtilMethods.isSet(startDate)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " date_posted >= ? ";
    	}
    	if (UtilMethods.isSet(endDate)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " date_posted <= ? ";
    	}
    	if (outsideUS) {
    		if (condition.length()>0) condition += " and ";
    		condition += " shipping_country <> '" + Config.getStringProperty("DEFAULT_COUNTRY_CODE") + "' ";
    	}
    	else{
    		if (condition.length()>0) condition += " and ";
    		condition += " (shipping_country = '" + Config.getStringProperty("DEFAULT_COUNTRY_CODE") + "' or shipping_country is NULL)";
    	}
    	
    	if (UtilMethods.isSet(invoiceNumber)) {
    		if (condition.length()>0) condition += " and ";
    		condition += " invoice_number = '" + invoiceNumber + "' ";
    	}
    	
    	//to create the query and get the results
		DotHibernate dh = new DotHibernate(Order.class);
		String query = "from inode in class com.dotmarketing.portlets.order_manager.model.Order";
		if (condition.length()>0) {
			query += " where type='ecom_order' and " + condition;
		}
		query += " order by date_posted desc";
		dh.setQuery(query);

    	if (UtilMethods.isSet(startDate)) {
    		dh.setDate(startDate);
    	}
    	if (UtilMethods.isSet(endDate)) {
    		dh.setDate(endDate);
    	}
		return dh.list();

		
	}
	
	public static List getOrdersByCondition(String condition){
		DotHibernate dh = new DotHibernate(Order.class);
		String query = "from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and "+condition;
		dh.setQuery(query);
		return dh.list();
		
	}
	
	public static List getAllOrderItems(Order order) {
		return OrderItemFactory.getAllOrderItemsByParentOrder(order);
	}

	public static Order newInstance() {
		return new Order();
	}

	public static void saveOrder(Order Order) {
		InodeFactory.saveInode(Order);
	}

	public static void deleteOrder(Order Order) {
		InodeFactory.deleteInode(Order);
	}
	
	public static java.util.List getOrdersByUserInode(String inode) {
		DotHibernate dh = new DotHibernate(Order.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.Order where type='ecom_order' and user_inode=? order by last_mod_date desc");
		dh.setParam(inode);
		return dh.list();
	}
	
	public static Order getOrderById(String inode) {
		return (Order) InodeFactory.getInode(inode, Order.class);
	}
	
	
}

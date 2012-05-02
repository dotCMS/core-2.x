package com.dotmarketing.portlets.order_manager.factories;

import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
/**
 *
 * @author  maria
 */
public class OrderItemFactory {
	
	public static OrderItem GetOrderItemById(String orderItemInode)
	{
		return (OrderItem) InodeFactory.getInode(orderItemInode,OrderItem.class);
	}

	public static java.util.List getAllOrderItems() {
		DotHibernate dh = new DotHibernate(OrderItem.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.order_manager.model.OrderItem where type='ecom_order_item'");
		return dh.list();
	}
	public static java.util.List getAllOrderItems(String orderby) {
		DotHibernate dh = new DotHibernate(OrderItem.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.order_manager.model.OrderItem where type='ecom_order_item' order by " + orderby);
		return dh.list();
	}
	
	public static List getAllOrderItemsByParentOrder (Order order) {
		return InodeFactory.getInodesOfClassByCondition(OrderItem.class, "order_inode = '" + order.getInode()+"'");
	}

	public static Order getParentOrder(OrderItem orderItem) {
		return (Order) InodeFactory.getInode(orderItem.getOrderInode(), Order.class);
		
	}
	
	public static OrderItem newInstance() {
		return new OrderItem();
	}

	public static void saveOrderItem(OrderItem orderItem) {
		InodeFactory.saveInode(orderItem);
	}

	public static void deleteOrderItem(OrderItem orderItem) {
		InodeFactory.deleteInode(orderItem);
	}
	
	public static java.util.List getOrderItemsByOrderId(String orderInode) {
		DotHibernate dh = new DotHibernate(OrderItem.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.order_manager.model.OrderItem where type='ecom_order_item' and order_inode=? order by inode");
		dh.setParam(orderInode);
		return dh.list();
	}

}

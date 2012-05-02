package com.dotmarketing.portlets.order_manager.model;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;

public class OrderItem extends Inode {
	private static final long serialVersionUID = 1L;
	private String inode; //identifier
    private String orderInode; //persistent
    private String productInode; //persistent
    private int itemQty; //persistent
    private float itemPrice; //persistent

    public OrderItem() {
    	setType("ecom_order_item");
    }

    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public String getOrderInode() {
        return this.orderInode;
    }

    public void setOrderInode(String orderInode) {
        this.orderInode = orderInode;
    }
    public String getProductInode() {
        return this.productInode;
    }

    public void setProductInode(String productInode) {
        this.productInode = productInode;
    }
    public int getItemQty() {
        return this.itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }
    public float getItemPrice() {
        return this.itemPrice;
    }
    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

}

package com.dotmarketing.cms.product.model;

import com.dotmarketing.portlets.product.model.ProductFormat;

public class Holder {
	String inode = "";
	private float price;
	private float lineTotal;
	private int quantity;
	private ProductFormat formatInode;
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ProductFormat getFormat() {
		return formatInode;
	}
	public void setFormat(ProductFormat format) {
		this.formatInode = format;
	}
	public float getLineTotal() {
		return lineTotal;
	}
	public void setLineTotal(float lineTotal) {
		this.lineTotal = lineTotal;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getInode() {
		return inode;
	}
	public void setInode(String inode) {
		this.inode = inode;
	}	
}

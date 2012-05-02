package com.dotmarketing.portlets.product.model;

import java.io.Serializable;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.factories.InodeFactory;

/**
 * 
 * @author David
 *
 */
public class ProductPrice extends Inode implements Serializable {
	
	private static final long serialVersionUID = 1L;
    private String productFormatInode; //persistent
    private int minQty; //persistent
    private int maxQty; //persistent
    private float retailPrice; //persistent
    private float partnerPrice; //persistent
    private float retailPriceWithDiscount; 
    private float partnerPriceWithDiscount; 

    public ProductPrice(String productFormatInode, int minQty, int maxQty, float retailPrice, float partnerPrice) 
    {
        this.productFormatInode = productFormatInode;
        this.minQty = minQty;
        this.maxQty = maxQty;
        this.retailPrice = retailPrice;
        this.partnerPrice = partnerPrice;
    }

    public ProductPrice() {
    	setType("ecom_product_price");
    }

    public ProductPrice(String productFormatInode, int retailPrice, int partnerPrice) {
        this.productFormatInode = productFormatInode;
        this.retailPrice = retailPrice;
        this.partnerPrice = partnerPrice;
    }

    public String getProductFormatInode() {
        return this.productFormatInode;
    }

    public void setProductFormatInode(String productFormatInode) {
        this.productFormatInode = productFormatInode;
    }
    public int getMinQty() {
        return this.minQty;
    }

    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }
    public int getMaxQty() {
        return this.maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }
    public float getRetailPrice() {
        return this.retailPrice;
    }

    public void setRetailPrice(float retailPrice) {
        this.retailPrice = retailPrice;
    }
    public float getPartnerPrice() {
        return this.partnerPrice;
    }

    public void setPartnerPrice(float partnerPrice) {
        this.partnerPrice = partnerPrice;
    }
    
    public Product getProductFormat () {
    	return (Product)InodeFactory.getInode(productFormatInode, ProductFormat.class);
    }

	public float getPartnerPriceWithDiscount() {
		return partnerPriceWithDiscount;
	}

	public void setPartnerPriceWithDiscount(float partnerPriceWithDiscount) {
		this.partnerPriceWithDiscount = partnerPriceWithDiscount;
	}

	public float getRetailPriceWithDiscount() {
		return retailPriceWithDiscount;
	}

	public void setRetailPriceWithDiscount(float retailPriceWithDiscount) {
		this.retailPriceWithDiscount = retailPriceWithDiscount;
	}
    
}

package com.dotmarketing.portlets.product.model;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.product.factories.ProductPriceFactory;
import com.dotmarketing.util.Logger;

/**
 * 
 * @author David
 *
 */
public class ProductFormat extends Inode {
	
	private static final long serialVersionUID = 1L;
    private String productInode; //persistent
    private String formatName; //persistent
    private String itemNumber; //persistent
    private String format; //persistent
    private int inventoryQuantity; //persistent
    private int reorderTrigger; //persistent
    private float weight; //persistent
    private int width; //persistent
    private int height; //persistent
    private int depth; //persistent

    public ProductFormat(String productInode, String formatName, String itemNum, String format, int inventoryQuantity, int reorderTrigger, float weight, int width, int height, int depth) {
        this.productInode = productInode;
        this.formatName = formatName;
        this.itemNumber = itemNum;
        this.format = format;
        this.inventoryQuantity = inventoryQuantity;
        this.reorderTrigger = reorderTrigger;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public ProductFormat() {
    	setType("ecom_product_format");
    }

    public ProductFormat(String productInode, String formatName, String format) {
        this.productInode = productInode;
        this.formatName = formatName;
        this.format = format;
    }

    public String getProductInode() {
        return this.productInode;
    }

    public void setProductInode(String productInode) {
        this.productInode = productInode;
    }
    public String getFormatName() {
        return this.formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }
    public String getItemNum() {
        return this.itemNumber;
    }

    public void setItemNum(String itemNum) {
        this.itemNumber = itemNum;
    }
    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    public int getInventoryQuantity() {
        return this.inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }
    public int getReorderTrigger() {
        return this.reorderTrigger;
    }

    public void setReorderTrigger(int reorderTrigger) {
        this.reorderTrigger = reorderTrigger;
    }
    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public Product getProduct () {
    	return (Product)InodeFactory.getInode(productInode, Product.class);
    }
    
    public boolean getHasBulkPrices()
    {
    	boolean hasBulkPrice = false;
    	List<ProductPrice> bulkPrices = getBulkPrices();
    	if (bulkPrices.size() > 1)
    	{
    		hasBulkPrice = true;
    	}
    	return hasBulkPrice;
    }
    
    public List<ProductPrice> getBulkPrices()
    {
    	List<ProductPrice> bulkPrices = null;
    	bulkPrices = ProductPriceFactory.getAllBulkProductPricesByFormat(this);
    	return bulkPrices;
    }
      
    public ProductPrice getQuantityPrice(int quantity) 
    {
    	List<DiscountCode> discounts = new ArrayList<DiscountCode>();
    	return getQuantityPrice(quantity,discounts);
    }
    
    public ProductPrice getQuantityPrice(int quantity,List<DiscountCode> discounts)
    {
    	float totalDiscountPartner = 0;
    	float totalDiscountRetail = 0;
    	List<ProductPrice> prices = null;
    	ProductPrice productPrice;
    	for(DiscountCode discount : discounts)
    	{
    		if (discount.getMinOrder() <= quantity) {
	    		
	    		Holder holder = new Holder();
	    		holder.setQuantity(quantity);
	    		holder.setFormat(this);
	    		if (DiscountCodeFactory._potentialDiscount(holder,discount) &&	    				
	    			discount.getNoBulkDisc() == true)
	    		{
	    			quantity = 1;
	    			break;
	    		}
	    		
    		}
    	}
    	
    	prices = ProductPriceFactory.getQuantityPricePricesByFormat(this,quantity);
    	if (prices.size() == 0)
    	{
    		prices = ProductPriceFactory.getMinorQuantityPricePricesByFormat(this,quantity);
    		if (prices.size() == 0)
    		{
    			prices = ProductPriceFactory.getAllQuantityPricePricesByFormat(this,quantity);
    			if (prices.size() == 0)
    			{
    				//throw new Exception("There is no price for this quantity");
    				Logger.error(this,"There is no price for this quantity");
    				productPrice = new ProductPrice();
    				productPrice.setMinQty(1);
    				productPrice.setMaxQty(Integer.MAX_VALUE);
    				productPrice.setPartnerPrice(0);
    				productPrice.setRetailPrice(0);
    				return productPrice;
    			}
    			else
    				productPrice = prices.get(0);
    		} 
    		else
    			productPrice = prices.get(0);
    	}
    	else{
    		productPrice = prices.get(0);
    		for(DiscountCode discount : discounts)
        	{
    			if (discount.getMinOrder() <= quantity) {
	    			
	        		Holder holder = new Holder();
	        		holder.setQuantity(quantity);
	        		holder.setFormat(this);
	        		if (DiscountCodeFactory._potentialDiscount(holder,discount))
	        		{
	        			if(discount.getDiscountType() == 1)
	    				{
	        				totalDiscountPartner += (discount.getDiscountAmount()/100)*productPrice.getPartnerPrice();
	        				totalDiscountRetail += (discount.getDiscountAmount()/100)*productPrice.getRetailPrice();
	    				}
	        			else {
	        				totalDiscountPartner += discount.getDiscountAmount();
	        				totalDiscountRetail += discount.getDiscountAmount();
	        			}
	        		}
	        		
    			}
        	}
    		productPrice.setPartnerPriceWithDiscount(productPrice.getPartnerPrice() - totalDiscountPartner);
    		productPrice.setRetailPriceWithDiscount(productPrice.getRetailPrice() - totalDiscountRetail);
    	}
    	return productPrice;
    }
}

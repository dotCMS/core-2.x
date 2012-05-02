package com.dotmarketing.portlets.product.factories;

import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.product.action.EditPriceAction;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Logger;
/**
 *
 * @author  david
 */
public class ProductPriceFactory {

	public static java.util.List getAllProductPrices() {
		DotHibernate dh = new DotHibernate(ProductPrice.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.product.model.ProductPrice where type='ecom_product_price' order by product_format_inode");
		return dh.list();
	}
	public static java.util.List getAllProductPrices(String orderby) {
		DotHibernate dh = new DotHibernate(ProductPrice.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.product.model.ProductPrice where type='ecom_product_price' order by " + orderby);
		return dh.list();
	}
	
	@SuppressWarnings("unchecked")
	public static List<ProductPrice> getAllProductPricesByFormat (ProductFormat format) {
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(ProductPrice.class, "product_format_inode = '" + format.getInode()+"'", "min_qty,max_qty,retail_price,partner_price");
	}
	
	public static List<ProductPrice> getAllBulkProductPricesByFormat(ProductFormat format) {
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(ProductPrice.class, "product_format_inode = '" + format.getInode()+"'","min_qty asc, max_qty asc");
	}
	
	public static List<ProductPrice> getQuantityPricePricesByFormat(ProductFormat format,int quantity) {
		String filter = "product_format_inode = '" + format.getInode() + "' and min_qty <= " + quantity + " and " + quantity + " <= max_qty "; 
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(ProductPrice.class,filter,"retail_price");		
	}
	
	public static List<ProductPrice> getMinorQuantityPricePricesByFormat(ProductFormat format,int quantity) {
		String filter = "product_format_inode = '" + format.getInode() + "' and min_qty <= " + quantity + " and max_qty <= " + quantity; 
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(ProductPrice.class,filter,"retail_price");		
	}
	
	public static List<ProductPrice> getAllQuantityPricePricesByFormat(ProductFormat format,int quantity) {
		String filter = "product_format_inode = '" + format.getInode()+"'"; 
		return InodeFactory.getInodesOfClassByConditionAndOrderBy(ProductPrice.class,filter,"retail_price");		
	}
	

	public static ProductPrice newInstance() {
		ProductPrice price = new ProductPrice();
		return price;
	}

	public static void saveProductPrice(ProductPrice price) {
		InodeFactory.saveInode(price);
	}

	public static void deleteProductPrice(ProductPrice price) {
		InodeFactory.deleteInode(price);
	}
	
	public static void copyProductPrice(ProductPrice price) 
	{
		//Obtain the format a parent of the price
		String formatInode = price.getProductFormatInode();
		ProductFormat format = ProductFormatFactory.getProductFormat(formatInode);
		copyProductPrice(format,price);
	}
	
	public static void copyProductPrice(ProductFormat format,ProductPrice price) 
	{		
		//Obtain the new price, copy the values and save in the DB
		ProductPrice copyPrice = new ProductPrice();
		try
		{
			//BeanUtils.copyProperties(copyPrice,price);
			copyPrice.setMinQty(price.getMinQty());
			copyPrice.setMaxQty(price.getMaxQty());
			copyPrice.setRetailPrice(price.getRetailPrice());
			copyPrice.setPartnerPrice(price.getPartnerPrice());
			copyPrice.setProductFormatInode(format.getInode());
		}
		catch(Exception ex)
		{
			Logger.debug(EditPriceAction.class,ex.toString());
		}
		copyPrice.setInode(null);
		ProductPriceFactory.saveProductPrice(copyPrice);			
	}

	public static ProductPrice getProductPrice(String inode) {
		return (ProductPrice) InodeFactory.getInode(inode,ProductPrice.class);
	}

	public static ProductPrice getProductCode(String inode) {
		return (ProductPrice) InodeFactory.getInode(inode,ProductPrice.class);
	}


}

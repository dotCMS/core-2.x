package com.dotmarketing.portlets.product.factories;

import java.util.List;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Logger;
/**
 *
 * @author  david
 */
public class ProductFormatFactory {

	@SuppressWarnings("unchecked")
	public static List<ProductFormat> getAllFormatsByProduct (Product prod) {
		return InodeFactory.getInodesOfClassByCondition(ProductFormat.class, "product_inode = '" + prod.getInode()+"'");
	}

	
	public static java.util.List getAllProductFormats() {
		DotHibernate dh = new DotHibernate(ProductFormat.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.product.model.ProductFormat where type='ecom_product_format' order by product_inode, format_name");
		return dh.list();
	}
	public static java.util.List getAllProductFormats(String orderby) {
		DotHibernate dh = new DotHibernate(ProductFormat.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.product.model.ProductFormat where type='ecom_product_format' order by product_inode, format_name");
		return dh.list();
	}
	

	public static ProductFormat newInstance() {
		ProductFormat format = new ProductFormat();
		return format;
	}

	public static void saveProductFormat(ProductFormat format) {
		InodeFactory.saveInode(format);
	}

	public static void deleteProductFormat(ProductFormat format) {
		List<ProductPrice> prices = ProductPriceFactory.getAllProductPricesByFormat(format);
		for (ProductPrice price : prices) {
			ProductPriceFactory.deleteProductPrice(price);
		}
		//http://jira.dotmarketing.net/browse/DOTCMS-3678
		InodeFactory.deleteInode(format);
		/*try {
			new HibernateUtil().delete(format);
		} catch (DotHibernateException e) {

		}*/

	}
	public static void copyProductFormat(ProductFormat format) 
	{
		String productInode = format.getProductInode();
		Product product = ProductFactory.getProduct(productInode);
		copyProductFormat(product,format);		
	}
	
	public static void copyProductFormat(Product product,ProductFormat format) 
	{
		//Copy the format, populate the field and save in the DB
		ProductFormat copyFormat = new ProductFormat();
		try
		{
			//BeanUtils.copyProperties(copyFormat,format);
			copyFormat.setFormatName(format.getFormatName());
			copyFormat.setItemNum(format.getItemNum());
			copyFormat.setFormat(format.getFormat());
			copyFormat.setInventoryQuantity(format.getInventoryQuantity());
			copyFormat.setReorderTrigger(format.getReorderTrigger());
			copyFormat.setWeight(format.getWeight());
			copyFormat.setHeight(format.getHeight());
			copyFormat.setWidth(format.getWidth());
			copyFormat.setDepth(format.getDepth());		
			copyFormat.setProductInode(product.getInode());
		}
		catch(Exception ex)
		{
			Logger.debug(ProductFormatFactory.class,ex.toString());
		}
		copyFormat.setInode(null);
		String formatName = copyFormat.getFormatName();
		formatName += " COPY";
		copyFormat.setFormatName(formatName);
		ProductFormatFactory.saveProductFormat(copyFormat);
		
		//Copy the prices of the old format
		List<ProductPrice> prices = ProductPriceFactory.getAllProductPricesByFormat(format);
		for (ProductPrice price : prices) {
			ProductPriceFactory.copyProductPrice(copyFormat,price);
		}
	}

	public static ProductFormat getProductFormat(String inode) {
		return (ProductFormat) InodeFactory.getInode(inode,ProductFormat.class);
	}

	public static ProductFormat getProductFormatByItemNumber(String number) {
		return (ProductFormat) InodeFactory.getInodeOfClassByCondition(DiscountCode.class, "item_num = " + number);
	}
	

}

package com.dotmarketing.portlets.discountcode.factories;

import java.util.Date;
import java.util.List;

import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderItemForm;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
/**
 *
 * @author  david
 */
public class DiscountCodeFactory {

	public static DiscountCode newInstance() {
		DiscountCode dc = new DiscountCode();
		return dc;
	}

	public static void saveDiscountCode(DiscountCode dc) {
		InodeFactory.saveInode(dc);
	}

	public static void deleteDiscountCode(DiscountCode dc) {
		InodeFactory.deleteInode(dc);
	}

	public static DiscountCode getDiscountCode(String inode) {
		return (DiscountCode) InodeFactory.getInode(inode,DiscountCode.class);
	}

	/*public static DiscountCode getDiscountCode(long inode) {
		return (DiscountCode) InodeFactory.getInode(inode,DiscountCode.class);
	}*/

	public static DiscountCode getDiscountCodeById(String id) {
		return (DiscountCode) InodeFactory.getInodeOfClassByCondition(DiscountCode.class, "code_id = '" + id + "'");
	}
	
	public static List searchDiscountCode(int discountType, Date startDate, Date endDate, String codeId, String desc, String orderBy, String direction, int limit) {
		
		String sql = "from inode in class com.dotmarketing.portlets.discountcode.model.DiscountCode where 1 = 1 ";
		if (UtilMethods.isSet(startDate))
			sql += "and start_date >= ? ";
		if (UtilMethods.isSet(endDate))
			sql += "and end_date <= ? ";
		if (UtilMethods.isSet(codeId))
		{
			codeId = "%"+codeId+"%";
			sql += "and code_id like ? ";
		}
		if (UtilMethods.isSet(desc))
		{
			desc = "%"+desc+"%"; 
			sql += "and code_description like ? ";
		}
		if (discountType != 0)
			sql += "and discount_type = ? ";
		if (UtilMethods.isSet(orderBy))
			sql += "order by " + orderBy;
		else
			sql += "order by start_date";
		if (UtilMethods.isSet(direction))		
			sql += " " + direction;		 	

		DotHibernate dh = new DotHibernate ();
		dh.setQuery(sql);
		if (UtilMethods.isSet(startDate))
			dh.setParam(startDate);
		if (UtilMethods.isSet(endDate))
			dh.setParam(endDate);
		if (UtilMethods.isSet(codeId))
			dh.setParam(codeId);
		if (UtilMethods.isSet(desc))
			dh.setParam(desc);
		if (discountType != 0)
			dh.setParam(discountType);
		if (limit > 0)
			dh.setMaxResults(limit);	
		return dh.list();		
	}
	
	public static float getTotalApplicableDiscount(List<Holder> holders,List<DiscountCode> discounts,boolean partner)
	{
		float totalDiscount = 0;
		try
		{
		float[] amountDiscounts = getApplicableDiscount(holders,discounts,partner);				
		for(int i = 0;i < amountDiscounts.length;i++)
		{
			totalDiscount += amountDiscounts[i];
		}
		totalDiscount = ((float) Math.round(totalDiscount * 100)) / ((float) 100);
		}
		catch(Exception ex)
		{
			Logger.debug(DiscountCodeFactory.class,ex.toString());
		}
		return totalDiscount;
	}
	
	public static float[] getApplicableDiscount(List<Holder> holders,List<DiscountCode> discounts,boolean partner)
	{
		Date now = new Date();
		float[] amountDiscounts = new float[discounts.size()];
		
		for(int i = 0;i < discounts.size();i++)
		{
			int totalQuantity = 0;
			float totalPrice = 0;
			DiscountCode discount = discounts.get(i);
			for(int j = 0;j < holders.size();j++)
			{
				Holder holder = holders.get(j);
				if(_potentialDiscount(holder,discount))
				{
					int quantity = holder.getQuantity();
					ProductFormat format = holder.getFormat();
					
					totalQuantity += quantity;
					
					ProductPrice productPrice = null;
					
					if (discount.getNoBulkDisc())
					{
						productPrice = format.getQuantityPrice(1);
					}
					else
					{
						productPrice = format.getQuantityPrice(quantity);
					}
					float price = (partner ? productPrice.getPartnerPrice() : productPrice.getRetailPrice());
					
					if(InodeUtils.isSet(holder.getInode()))
					{
						price = holder.getPrice();
					}
					totalPrice += quantity * price;					
				}
			}
			if(_applyDiscount(totalQuantity,discount,now))
			{
				amountDiscounts[i] = _calculateDiscount(totalPrice,discount,holders);
			}			
		}
		return amountDiscounts;
	}
	
	public static boolean _potentialDiscount(Holder holder,DiscountCode discount)
	{
		boolean potential = false;
		ProductFormat format = holder.getFormat();
		List<ProductFormat> applicableFormat = discount.getProductFormatApplicable();
		if (!(applicableFormat.size() == 0))
		{
			for(ProductFormat auxFormat : applicableFormat)
			{
				if (auxFormat.getInode().equalsIgnoreCase(format.getInode()))
				{
					potential = true;
					break;
				}
			}
		}else
		{
			potential = true;
		}
		return potential;
	}
	
	public static boolean _potentialDiscount(ProductFormat format,DiscountCode discount)
	{
		boolean potential = false;
		List<ProductFormat> applicableFormat = discount.getProductFormatApplicable();
		if (!(applicableFormat.size() == 0))
		{
			for(ProductFormat auxFormat : applicableFormat)
			{
				if (auxFormat.getInode().equalsIgnoreCase(format.getInode()))
				{
					potential = true;
					break;
				}
			}
		}else
		{
			potential = true;
		}
		return potential;
	}
	public static boolean _potentialDiscount(OrderItem orderItem,DiscountCode discount)
	{
		boolean potential = false;
		ProductFormat format = ProductFormatFactory.getProductFormat(orderItem.getProductInode());
		List<ProductFormat> applicableFormat = discount.getProductFormatApplicable();
		if (!(applicableFormat.size() == 0))
		{
			for(ProductFormat auxFormat : applicableFormat)
			{
				if (auxFormat.getInode().equalsIgnoreCase(format.getInode()))
				{
					potential = true;
					break;
				}
			}
		}else
		{
			potential = true;
		}
		return potential;
	}
	
	public static boolean _potentialDiscount(OrderItemForm orderItemForm,DiscountCode discount)
	{
		boolean potential = false;
		ProductFormat format = ProductFormatFactory.getProductFormat(orderItemForm.getProductInode());
		List<ProductFormat> applicableFormat = discount.getProductFormatApplicable();
		if (!(applicableFormat.size() == 0))
		{
			for(ProductFormat auxFormat : applicableFormat)
			{
				if (auxFormat.getInode().equalsIgnoreCase(format.getInode()))
				{
					potential = true;
					break;
				}
			}
		}else
		{
			potential = true;
		}
		return potential;
	}
	
	private static int _totalPotentialDiscount(Holder holder,DiscountCode discount)
	{
		int potential = 0;
		ProductFormat format = holder.getFormat();
		List<ProductFormat> applicableFormat = discount.getProductFormatApplicable();
		if (!(applicableFormat.size() == 0))
		{
			for(ProductFormat auxFormat : applicableFormat)
			{
				if (auxFormat.getInode().equalsIgnoreCase(format.getInode()))
				{
					potential = holder.getQuantity();
					break;
				}
			}
		}
		return potential;
	}
	
	public static boolean _applyDiscount(int quantity,DiscountCode discount,Date now)
	{
		boolean apply = true;	
		if ((discount.getStartDate() != null && now.compareTo(discount.getStartDate()) < 0) || 
			(discount.getEndDate() != null && now.compareTo(discount.getEndDate()) > 0))
		{
			apply = false;
		}
		if(quantity < discount.getMinOrder())
		{
			apply = false;
		}
		return apply;
	}
	
	private static float _calculateDiscount(float totalPrice,DiscountCode discount,List<Holder> holders)
	{
		float discountAmount = 0;
		if (discount.getDiscountType() == Integer.parseInt(WebKeys.DISCOUNTCODE_DISCOUNT))
		{
			int totalItems = 0;
			boolean discountByItem = false;
			try
			{
				discountByItem = Config.getBooleanProperty("DISCOUNT_BY_ITEM");
			}
			catch(Exception ex)
			{
			}
			if (discountByItem)
			{
				for(Holder holder : holders)
				{
					totalItems += _totalPotentialDiscount(holder,discount);
				}
			}
			else
			{
				totalItems = 1;
			}
			discountAmount = totalItems * discount.getDiscountAmount();
		}
		else if(discount.getDiscountType() == Integer.parseInt(WebKeys.DISCOUNTCODE_PERCENTAGE))
		{
			discountAmount = (totalPrice) * discount.getDiscountAmount() / 100F;
		}
		return discountAmount;
	}
}

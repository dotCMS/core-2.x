package com.dotmarketing.cms.product.model.defaultShipping;

import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessor;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessorException;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.util.Config;

public class DefaultShippingCalculationProcessor extends ShippingCalculationProcessor {
	
	public float calculateShipping(ShoppingCart shoppingCart,int serviceType, String shippingCountry, String shippingZip) throws ShippingCalculationProcessorException {

		
		float amount = 0;
		
		float subtotalToShipping = 0;
		boolean freeShipping;
		
		for(Holder item : shoppingCart.getHolders()) {
			if (item.getFormat().getProduct().getReqShipping()) {
				if (serviceType == Config.getIntProperty("SHIPPING_GROUND")) {
					freeShipping = false;
					for (DiscountCode discount: shoppingCart.getDiscountCodes()) {
						if (DiscountCodeFactory._potentialDiscount(item, discount)) {
							if (discount.getFreeShipping()) {
								freeShipping = true;
								break;
							}
						}
					}
					
					if (!freeShipping) {
						subtotalToShipping = subtotalToShipping + (item.getQuantity() * item.getPrice());
					}
				} else {
					subtotalToShipping = subtotalToShipping + (item.getQuantity() * item.getPrice());
				}
			}
		}
		
		if ((0 < subtotalToShipping) && (subtotalToShipping < 10)) {
			amount = 0;
		} else if ((10 <= subtotalToShipping) && (subtotalToShipping < 50)) {
			amount = 4;
		} else if ((50 <= subtotalToShipping) && (subtotalToShipping < 100)) {
			amount = 6;
		} else if ((100 <= subtotalToShipping) && (subtotalToShipping < 200)) {
			amount = 8;
		} else if (200 <= subtotalToShipping) {
			amount = 10;
		}

		
		return amount;
		

	}
	
	
	/**
	 * This Method calculate the handling cost of a shopping car
	 * @param shoppingCart
	 * @return the amount cost of the handling
	 */
	public float calculateHandling(ShoppingCart shoppingCart){
		
		float amount=0;
		
		int itemsQuantity = 0;
		
		for(Holder item : shoppingCart.getHolders()){
			
			itemsQuantity = itemsQuantity + item.getQuantity();
		}
		
		amount = (Config.getFloatProperty("HANDLING_ITEM_COST") * itemsQuantity) + Config.getFloatProperty("HANDLING_BOXING_COST");
		
		return amount;
	}
	
	
	
}
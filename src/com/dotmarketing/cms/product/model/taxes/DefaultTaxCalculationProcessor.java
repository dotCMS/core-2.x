package com.dotmarketing.cms.product.model.taxes;

import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.UtilMethods;

public class DefaultTaxCalculationProcessor extends TaxCalculationProcessor {

	@Override
	public float calculateTax(ShoppingCart shoppingCart, float orderSubtotal,
			String shippingCountry, String shippingState, String shippingZip, String taxExemptId)
			throws TaxCalculationProcessorException {
		

		
		float orderTax = 0;
		
		if (UtilMethods.isSet(taxExemptId))
			return orderTax;
		
		String[] taxes = Config.getStringProperty("TAX_STATES_PERCENTAGES").split(",");
		String[] tax;
		String otherStateTax = "0";
		
		boolean calculated = false;
		
		for (int i = 0; i < taxes.length; ++i) {
			tax = taxes[i].split(":");
			
			if (tax[0].equals("OTHER")) {
				otherStateTax = tax[1];
			}
			
			if (shippingState.equals(tax[0])) {
				try {
					orderTax = ((float) Math.round(orderSubtotal * Float.parseFloat(tax[1]))) / ((float) 100);
				} catch (Exception e) {
				}
				calculated = true;
				break;
			}
		}
		
		if (!calculated) {
			try {
				orderTax = ((float) Math.round(orderSubtotal * Float.parseFloat(otherStateTax))) / ((float) 100);
			} catch (Exception e) {
			}
		}
		
		return orderTax;
	}

}

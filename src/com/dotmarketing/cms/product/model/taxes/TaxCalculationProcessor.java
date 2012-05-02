package com.dotmarketing.cms.product.model.taxes;

import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.UtilMethods;

public abstract class TaxCalculationProcessor {
	final public static TaxCalculationProcessor getInstance() throws TaxCalculationProcessorException
	{

		try {
			String taxProcessorStr = Config.getStringProperty("TaxProcessorClass");
			
			if(!UtilMethods.isSet(taxProcessorStr )){
				taxProcessorStr  = DefaultTaxCalculationProcessor.class.getName();
			}
			
			TaxCalculationProcessor taxProcessor = (TaxCalculationProcessor) Class.forName(taxProcessorStr).newInstance();
			return taxProcessor;
		} catch (Exception e) {

			throw new TaxCalculationProcessorException("There is no tax processor selected in the properties file");
		}
		
	}
	
	public abstract float calculateTax(ShoppingCart shoppingCart,float orderSubtotal,String shippingCountry, String shippingState, String shippingZip, String taxExemptId) throws TaxCalculationProcessorException;

}

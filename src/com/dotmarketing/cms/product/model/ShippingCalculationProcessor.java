package com.dotmarketing.cms.product.model;

import com.dotmarketing.cms.product.model.defaultShipping.DefaultShippingCalculationProcessor;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.UtilMethods;

public abstract class ShippingCalculationProcessor {
	final public static ShippingCalculationProcessor getInstance() throws Exception
	{
		String shippingProcessorString = Config.getStringProperty("ShippingProcessorClass");
		if(!UtilMethods.isSet(shippingProcessorString )){
			shippingProcessorString  = DefaultShippingCalculationProcessor.class.getName();
		}
		
		try {
			return (ShippingCalculationProcessor) Class.forName(shippingProcessorString).newInstance();
		
		} catch (Exception e) {

			throw new Exception("There is no shipping processor selected in the properties file");
		}
		

	}
	
	public abstract float calculateShipping(ShoppingCart shoppingCart,int serviceType, String shippingCountry, String shippingZip) throws ShippingCalculationProcessorException;

	public abstract float calculateHandling(ShoppingCart shoppingCart) throws ShippingCalculationProcessorException;

}
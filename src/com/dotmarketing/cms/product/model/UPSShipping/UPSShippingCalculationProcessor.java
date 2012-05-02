package com.dotmarketing.cms.product.model.UPSShipping;

import java.util.Vector;

import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessor;
import com.dotmarketing.cms.product.model.ShippingCalculationProcessorException;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.ups.UPSConnections;
import com.dotmarketing.util.ups.UPSResponseObject;
import com.dotmarketing.util.ups.UPSResponseParser;

public class UPSShippingCalculationProcessor extends ShippingCalculationProcessor {
	
	public float calculateShipping(ShoppingCart shoppingCart,int serviceType, String shippingCountry, String shippingZip) throws ShippingCalculationProcessorException {

		
		float amount = 0;
		
		ProductFormat format;
		float packageWeight = 0;
		boolean freeShipping;
		
		if (serviceType == Config.getIntProperty("SHIPPING_GROUND")) {
			for (Holder item : shoppingCart.getHolders()) {
				
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
					format = (ProductFormat) item.getFormat();
					packageWeight = packageWeight + (item.getQuantity() * format.getWeight());
				}
			}
		} else {
			for(Holder item : shoppingCart.getHolders()){
				format = (ProductFormat) item.getFormat();
				packageWeight = packageWeight + (item.getQuantity() * format.getWeight());
			}
		}
		
		if (packageWeight > 0) {
			try {
				
				UPSConnections conn = new UPSConnections();
				
				String serviceTypeStr = "";
				if (serviceType == Config.getIntProperty("SHIPPING_GROUND")) {
					serviceTypeStr = Config.getStringProperty("UPS_GROUND");
				} else if (serviceType == Config.getIntProperty("SHIPPING_PRIORITY")) {
					serviceTypeStr = Config.getStringProperty("UPS_PRIORITY");
				} else {
					serviceTypeStr = Config.getStringProperty("UPS_NEXT_DAY");
				}
	
				
				StringBuffer xmlRequest = generateXML(serviceTypeStr,packageWeight,shippingZip);
				
				
				
				String response = conn.contactService("Rate",xmlRequest);
				Logger.info(this, "xml Response:"+response);
				
				Vector<UPSResponseObject> values = UPSResponseParser.getObjectShippingPrices(response);
				for(UPSResponseObject obj : values){
					if(obj.getStatusCode().equals("1")){
						amount = amount + Float.parseFloat(obj.getTotalValue());
					}
				}
				
			} catch (Exception e) {
				Logger.error(this,e.getMessage(),e);
			}
		}
		

		
		return amount;
	}
	
	private static StringBuffer generateXML(String ServiceType, float packageWeight, String Ship_To_Zip) {
		
		StringBuffer xml = new StringBuffer();
		
		xml.append("<?xml version=\"1.0\"?>");
		xml.append("<RatingServiceSelectionRequest xml:lang=\"en-US\">");
		xml.append("<Request>");
		xml.append("<TransactionReference>");
		xml.append("<CustomerContext>Bare Bones Rate Request</CustomerContext>");
		xml.append("<XpciVersion>1.0001</XpciVersion>");
		xml.append("</TransactionReference>");
		xml.append("<RequestAction>Rate</RequestAction>");
		xml.append("<RequestOption>Rate</RequestOption>");
		xml.append("</Request>");
		xml.append("<PickupType>");
		xml.append("<Code>"+Config.getStringProperty("UPS_STUDER_PICKUP_TYPE")+"</Code>");
		xml.append("</PickupType>");
		xml.append("<Shipment>");
		xml.append("<Shipper>");
		xml.append("<Address>");
		xml.append("<PostalCode>"+Config.getStringProperty("UPS_STUDER_POSTAL_CODE")+"</PostalCode>");
		xml.append("<CountryCode>"+Config.getStringProperty("UPS_STUDER_COUNTRY")+"</CountryCode>");
		xml.append("</Address>");
		xml.append("</Shipper>");
		xml.append("<ShipTo>");
		xml.append("<Address>");
		xml.append("<PostalCode>"+Ship_To_Zip.trim()+"</PostalCode>");
		xml.append("<CountryCode>"+Config.getStringProperty("UPS_STUDER_COUNTRY")+"</CountryCode>");
		xml.append("</Address>");
		xml.append("</ShipTo>");
		xml.append("<ShipFrom>");
		xml.append("<Address>");
		xml.append("<PostalCode>"+Config.getStringProperty("UPS_STUDER_POSTAL_CODE")+"</PostalCode>");
		xml.append("<CountryCode>"+Config.getStringProperty("UPS_STUDER_COUNTRY")+"</CountryCode>");
		xml.append("</Address>");
		xml.append("</ShipFrom>");
		xml.append("<Service>");
		xml.append("<Code>"+ServiceType+"</Code>");
		xml.append("</Service>");
		xml.append("<Package>");
		xml.append("<PackagingType>");
		xml.append("<Code>"+Config.getStringProperty("UPS_STUDER_PACKAGE_TYPE")+"</Code>");
		xml.append("</PackagingType>");
		xml.append("<PackageWeight>");
		xml.append("<UnitOfMeasurement>");
		xml.append("<Code>"+Config.getStringProperty("UPS_STUDER_MEASURE_UNIT")+"</Code>");
		xml.append("</UnitOfMeasurement>");
		xml.append("<Weight>"+packageWeight+"</Weight>");
		xml.append("</PackageWeight>");
		xml.append("</Package>");
		xml.append("</Shipment>");
		xml.append("</RatingServiceSelectionRequest>");
		
		return xml;
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
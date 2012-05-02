package com.dotmarketing.cms.product.model;

public class ShippingCalculationProcessorException extends Exception {
	private static final long serialVersionUID = 1L;

	public ShippingCalculationProcessorException(String message) {
		super(message);		
	}

	public ShippingCalculationProcessorException(String message, Throwable ex) {
		super(message,ex);
	}
}
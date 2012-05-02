package com.dotmarketing.cms.product.model.taxes;

public class TaxCalculationProcessorException extends Exception {
	private static final long serialVersionUID = 1L;

	public TaxCalculationProcessorException(String message) {
		super(message);		
	}

	public TaxCalculationProcessorException(String message, Throwable ex) {
		super(message,ex);
	}
}
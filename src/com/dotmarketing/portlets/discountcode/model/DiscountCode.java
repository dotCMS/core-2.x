package com.dotmarketing.portlets.discountcode.model;

import java.util.List;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.WebKeys;

public class DiscountCode extends Inode  {

	public static final int percentageDiscountType = 0;
	public static final int amountDiscountType = 1;
	
	private static final long serialVersionUID = 1L;
	//private long inode; //persistent
    private int discountType; //persistent
    private java.util.Date startDate; //persistent
    private java.util.Date endDate; //persistent
    private String codeId; //persistent
    private String codeDescription; //persistent
    private boolean freeShipping; //persistent
    private boolean noBulkDisc; //persistent
    private float discountAmount; //persistent
    private int minOrder; //persistent

    public DiscountCode(int discountType, java.util.Date startDate, java.util.Date endDate, String codeId, String codeDescription, boolean freeShipping, boolean noBulkDisc, int discountAmount, int minOrder) {
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.codeId = codeId;
        this.codeDescription = codeDescription;
        this.freeShipping = freeShipping;
        this.noBulkDisc = noBulkDisc;
        this.discountAmount = discountAmount;
        this.minOrder = minOrder;
    }

    public DiscountCode() {
    }

    //public long getInode() {
    //    return this.inode;
    //}

    //public void setInode(long inode) {
    //    this.inode = inode;
    //}
    
    public int getDiscountType() {
        return this.discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }
    public java.util.Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }
    public java.util.Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }
    public String getCodeId() {
        return this.codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }
    public String getCodeDescription() {
        return this.codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }
    public boolean getFreeShipping() {
        return this.freeShipping;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }
    public boolean getNoBulkDisc() {
        return this.noBulkDisc;
    }

    public void setNoBulkDisc(boolean noBulkDisc) {
        this.noBulkDisc = noBulkDisc;
    }
    public float getDiscountAmount() {
        return this.discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }
    public int getMinOrder() {
        return this.minOrder;
    }

    public void setMinOrder(int minOrder) {
        this.minOrder = minOrder;
    }
    
    public List<ProductFormat> getProductFormatApplicable()
    {
    	return InodeFactory.getParentsOfClassByRelationType(this,ProductFormat.class,WebKeys.DISCOUNTCODE_PRODUCT_FORMAT);
    }

	@Override
	public boolean equals(Object other) 
	{
		DiscountCode otherDiscountCode = (DiscountCode) other;
		boolean returnValue = (this.inode.equalsIgnoreCase(otherDiscountCode.getInode()) ? true : false);
		return returnValue;		
	}    
}

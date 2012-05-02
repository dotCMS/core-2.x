package com.dotmarketing.portlets.product.struts;

import javax.portlet.ActionRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;

public class ProductPriceForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;

	private String inode; //identifier
    private String productFormatInode; //persistent
    private int minQty; //persistent
    private int maxQty; //persistent
    private float retailPrice; //persistent
    private float partnerPrice; //persistent
	
    public ProductPriceForm() {
    }

	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public int getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(int maxQty) {
		this.maxQty = maxQty;
	}

	public int getMinQty() {
		return minQty;
	}

	public void setMinQty(int minQty) {
		this.minQty = minQty;
	}

	
	public float getPartnerPrice() {
		return partnerPrice;
	}
    public String getPartnerPriceString() {
        return UtilMethods.dollarFormat(partnerPrice);
    }
    public String getRetailPriceString() {
        return UtilMethods.dollarFormat(retailPrice);
    }
    
	public void setPartnerPrice(float partnerPrice) {
		this.partnerPrice = partnerPrice;
	}

	public String getProductFormatInode() {
		return productFormatInode;
	}

	public void setProductFormatInode(String productFormatInode) {
		this.productFormatInode = productFormatInode;
	}

	public float getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(float retailPrice) {
		this.retailPrice = retailPrice;
	}

	public ActionErrors validate(ActionMapping mapping,ActionRequest req)
	{
		ActionErrors ae = new ActionErrors();
		if(minQty < 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Min Qty"));
		}
		if(maxQty < 0 || maxQty < minQty)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Max Qty"));
		}
		if(retailPrice <= 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Retail Price"));
		}
		if(partnerPrice <= 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Partner Price"));
		}		
		return ae;
	}    
}

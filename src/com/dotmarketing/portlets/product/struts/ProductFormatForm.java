package com.dotmarketing.portlets.product.struts;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.util.Constants;

public class ProductFormatForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;

	//Product
	private String inode; //identifier
    private String productInode; //persistent
    private String formatName; //persistent
    private String itemNum; //persistent
    private String format; //persistent
    private int inventoryQuantity; //persistent
    private int reorderTrigger; //persistent
    private float weight; //persistent
    private int width; //persistent
    private int height; //persistent
    private int depth; //persistent
    
    //Price
	private String priceInode; //identifier
    private String productFormatInode; //persistent
    private int minQty; //persistent
    private int maxQty; //persistent
    private float retailPrice; //persistent
    private float partnerPrice; //persistent
    
    private List<ProductPrice> prices;

    public ProductFormatForm() {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public int getInventoryQuantity() {
		return inventoryQuantity;
	}

	public void setInventoryQuantity(int inventoryQuantity) {
		this.inventoryQuantity = inventoryQuantity;
	}

	public String getItemNum() {
		return itemNum;
	}

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}

	public String getProductInode() {
		return productInode;
	}

	public void setProductInode(String productInode) {
		this.productInode = productInode;
	}

	public int getReorderTrigger() {
		return reorderTrigger;
	}

	public void setReorderTrigger(int reorderTrigger) {
		this.reorderTrigger = reorderTrigger;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public List<ProductPrice> getPrices() {
		return prices;
	}

	public void setPrices(List<ProductPrice> prices) {
		this.prices = prices;
	}
	
	public ActionErrors validate(ActionMapping mapping,ActionRequest req)
	{
		ActionErrors ae = new ActionErrors();
		if(!UtilMethods.isSet(formatName))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Variant Name"));
		}
		if(!UtilMethods.isSet(itemNum))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Item #"));
		}
	
		return ae;
	}
	
	//### PRICE METHODS ###
	public String getPriceInode() {
		return priceInode;
	}

	public void setPriceInode(String priceInode) {
		this.priceInode = priceInode;
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

	public ActionErrors validatePrice(ActionMapping mapping,ActionRequest req)
	{
		ActionErrors ae = new ActionErrors();
		if(minQty < 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Min Qty"));
		}
		if(maxQty <= 0 || maxQty < minQty)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Max Qty"));
		}
		if(retailPrice < 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Retail Price"));
		}
		if(partnerPrice < 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Partner Price"));
		}		

		if(partnerPrice + retailPrice <= 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format","Retail Price"));
		}	
		if(partnerPrice ==0 && retailPrice > 0){
			partnerPrice = retailPrice;
		}
		return ae;
	}  	
}

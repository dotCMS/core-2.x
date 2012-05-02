package com.dotmarketing.portlets.order_manager.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.util.Constants;

public class OrderItemForm extends ValidatorForm {
	private static final long serialVersionUID = 1L;
	private String inode; //identifier
    private String orderInode; //persistent
    private String productInode; //persistent
    private int itemQty; //persistent
    private float itemPrice; //persistent
    private String productName;
    private String formatName;

    public OrderItemForm() {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }

    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public String getOrderInode() {
        return this.orderInode;
    }

    public void setOrderInode(String orderInode) {
        this.orderInode = orderInode;
    }
    public String getProductInode() {
        return this.productInode;
    }

    public void setProductInode(String productInode) {
        this.productInode = productInode;
    }
    public int getItemQty() {
        return this.itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }
    public float getItemPrice() {
        return this.itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

}

package com.dotmarketing.portlets.discountcode.struts;

import java.text.SimpleDateFormat;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;

public class DiscountCodeForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;

	private String inode; //persistent
    private int discountType; //persistent
    private String startDate; //persistent
    private String endDate; //persistent
    private String codeId; //persistent
    private String codeDescription; //persistent
    private boolean freeShipping; //persistent
    private boolean noBulkDisc; //persistent
    private float discountAmount; //persistent
    private int minOrder; //persistent
    //private String[] categories;
    private String orderby;
    private String direction;

    /*CATEGORIES*/
    private String[] products;

    public DiscountCodeForm() {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }

	public String getCodeDescription() {
		return codeDescription;
	}

	public void setCodeDescription(String codeDescription) {
		this.codeDescription = codeDescription;
	}

	public String getCodeId() {
		return codeId;
	}

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public float getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(float discountAmount) {
		this.discountAmount = discountAmount;
	}

	public int getDiscountType() {
		return discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public boolean isFreeShipping() {
		return freeShipping;
	}

	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}

	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public int getMinOrder() {
		return minOrder;
	}

	public void setMinOrder(int minOrder) {
		this.minOrder = minOrder;
	}

	public boolean isNoBulkDisc() {
		return noBulkDisc;
	}

	public void setNoBulkDisc(boolean noBulkDisc) {
		this.noBulkDisc = noBulkDisc;
	}

	public String[] getProducts() {
		return products;
	}

	public void setProducts(String[] products) {
		this.products = products;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	
	public ActionErrors validateEdit(ActionMapping mapping,ActionRequest req, User user)
	throws Exception{
		ActionErrors ae = new ActionErrors();
    	//SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.SHORTDATE);
		SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.EXP_IMP_DATE);
    	
    	java.util.Date sDate = null;
    	java.util.Date eDate = null;
    	
    	
		if(!UtilMethods.isSet(codeId))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Discount-Code-Id")));			
		}
		if(!UtilMethods.isSet(codeDescription))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Code-Description")));
		}
		if(UtilMethods.isSet(startDate))
		{
			try
			{
				sDate = df.parse(startDate);
			}
			catch(Exception ex)
			{
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "Start-Date")));
			}
		}
		if(UtilMethods.isSet(endDate))
		{
			try
			{
				eDate = df.parse(endDate);
			}
			catch(Exception ex)
			{
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "End-Date")));
			}
		}
		
		if ((sDate != null) && (eDate != null)) {
			java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
			long now = calendar.getTimeInMillis();
			long startTime = sDate.getTime();
			long endTime = eDate.getTime();
			
			if (endTime < now) {
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.discountcode.end_date_before_now",LanguageUtil.get(user, "End-Date")));
			}
			if (endTime < startTime) {
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.discountcode.end_date_before_start_date",LanguageUtil.get(user, "End-Date")));
			}
		}
		
		//Validate Discount Type
		if (discountType == 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Discount-Type")));
		}
		
		//Discount Amount		
		try
		{
			float discountAmountAux = Float.parseFloat(req.getParameter("discountAmount"));
			if (Integer.toString(discountType).equals(WebKeys.DISCOUNTCODE_PERCENTAGE) && 
					(discountAmountAux < 0 ||
							discountAmountAux > 100))
			{
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "Discount-Amount")));
			}
		}
		catch(Exception ex)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "Discount-Amount")));
		}		
		//Minimum Order		
		try
		{
			int minOrderAux = Integer.parseInt(req.getParameter("minOrder"));
			if (minOrderAux < 0)
			{
				ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "Minimum-Order")));
			}
		}
		catch(Exception ex)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.format",LanguageUtil.get(user, "Minimum-Order")));
		}
		
		
		//Validate primary key
		DiscountCode discountCodeAux = DiscountCodeFactory.getDiscountCodeById(codeId);
		if (InodeUtils.isSet(discountCodeAux.getInode()) && (!discountCodeAux.getInode().equalsIgnoreCase(inode)))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.unique",LanguageUtil.get(user, "Discount-Code-Id")));			
		}			
		
		return ae;
	}

    
}

/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dotmarketing.portlets.discountcode.action;

import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.discountcode.struts.DiscountCodeForm;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.util.servlet.SessionMessages;

/**
 * @author David
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditDiscountCodeAction extends DotPortletAction  
{   
    public void processAction(ActionMapping mapping, ActionForm form,PortletConfig config, ActionRequest req, ActionResponse res) throws Exception 
    {    	
    	DiscountCodeForm discountCodeForm = (DiscountCodeForm) form;
        String cmd = req.getParameter(Constants.CMD);
		String referer = req.getParameter("referer");
		

		if ((referer != null) && (referer.length() != 0)) 
		{
			referer = URLDecoder.decode(referer, "UTF-8");
		}
				
		DotHibernate.startTransaction();
		_loadDiscount(req, form);

		User user = _getUser(req);			
		
		if ((cmd != null) && cmd.equals(Constants.ADD)) 
		{		   
			try 
			{	    			  
				Logger.debug(this, "Calling Add Method");
				ActionMessages ae = discountCodeForm.validateEdit(mapping,req,user);
				if (ae != null && ae.size() > 0)
				{
				    req.setAttribute(Globals.ERROR_KEY, ae);
				    setForward(req, "portlet.ext.discountcode.edit_discountcode");
				    return;
				}				
				
				_saveWebAsset(req, res, config, form, user);
				
				_sendToReferral(req, res, referer);
				
				
                return;
			} 
			catch (Exception ae) 
			{				
				_handleException(ae, req);
				return;
			} 
		}
		else if ((cmd != null) && cmd.equals(Constants.DELETE)) 
		{		   
			try 
			{	    			  								
				_deleteWebAsset(req,res,config,form,user); 
				
				
				_sendToReferral(req, res, referer);
				
				
                return;
			} 
			catch (Exception ae) 
			{				
				_handleException(ae, req);
				return;
			} 
		}		
		DotHibernate.commitTransaction();
		_loadForm(req,form);
		setForward(req, "portlet.ext.discountcode.edit_discountcode");
    }
      
    public void _saveWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
            throws Exception 
    {    	
//    	SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.SHORTDATE);
    	SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.EXP_IMP_DATE);
    	
    	DiscountCode discountCode = (DiscountCode) req.getAttribute(WebKeys.DISCOUNTCODE_DISCOUNTS);
    	
    	DiscountCodeForm discountCodeForm = (DiscountCodeForm) form;    	
    	String codeId = discountCodeForm.getCodeId();
    	String codeDesc = discountCodeForm.getCodeDescription();
    	Date startDate = null;
    	if (UtilMethods.isSet(discountCodeForm.getStartDate()))
    	{
        	startDate = df.parse(discountCodeForm.getStartDate());
    	}
    	Date endDate = null;
    	if (UtilMethods.isSet(discountCodeForm.getEndDate()))
    	{
        	endDate = df.parse(discountCodeForm.getEndDate());
    	}    	
    	int discountType = discountCodeForm.getDiscountType();
    	boolean freeShipping = discountCodeForm.isFreeShipping();
    	boolean noBulkDiscount = discountCodeForm.isNoBulkDisc();
    	float discountAmount = discountCodeForm.getDiscountAmount();
    	int minOrder = discountCodeForm.getMinOrder();
    	
    	discountCode.setCodeId(codeId);
    	discountCode.setCodeDescription(codeDesc);
    	discountCode.setStartDate(startDate);
    	discountCode.setEndDate(endDate);
    	discountCode.setDiscountType(discountType);
    	discountCode.setFreeShipping(freeShipping);
    	discountCode.setNoBulkDisc(noBulkDiscount);
    	discountCode.setDiscountAmount(discountAmount);
    	discountCode.setMinOrder(minOrder);
    	
    	DiscountCodeFactory.saveDiscountCode(discountCode);
    	
    	//Delete the old categories
    	List<ProductFormat> oldProductList = InodeFactory.getParentsOfClass(discountCode,ProductFormat.class);
    	for(ProductFormat format: oldProductList)
    	{
    		discountCode.deleteParent(format,WebKeys.DISCOUNTCODE_PRODUCT_FORMAT);
    	}
    	
    	String[] idProductList = discountCodeForm.getProducts();
    	if (idProductList != null)
    	{
    		for(int i=0;i< idProductList.length;i++) 
    		{
    			String idProduct = String.valueOf(idProductList[i]);
    			ProductFormat format = ProductFormatFactory.getProductFormat(idProduct); 
    			discountCode.addParent(format,WebKeys.DISCOUNTCODE_PRODUCT_FORMAT);
    		}
    	} 
    	else
    	{
    		List<ProductFormat> allProductFormats = ProductFormatFactory.getAllProductFormats();
    		for(ProductFormat format : allProductFormats)
    		{
    			discountCode.addParent(format,WebKeys.DISCOUNTCODE_PRODUCT_FORMAT);
    		}
    	}
    	
    	String message = "message.discountcode.save";
		SessionMessages.add(req, "message",message);	
    } 
       
    private DiscountCode _loadDiscount(ActionRequest request, ActionForm form) throws IllegalAccessException, InvocationTargetException
    {
        DiscountCode discountCode;
        String inode = request.getParameter("inode");
        /*long longInode = 0;
        if(InodeUtils.isSet(inode))
        {
        	longInode = Long.parseLong(inode);
        }*/
        discountCode = DiscountCodeFactory.getDiscountCode(inode);
        request.setAttribute(WebKeys.DISCOUNTCODE_DISCOUNTS,discountCode);        	
        return discountCode;
    } 
    
    private void _loadForm(ActionRequest request, ActionForm form)
    {
//    	SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.SHORTDATE);
    	SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.EXP_IMP_DATE);
    	
    	DiscountCodeForm discountCodeForm = (DiscountCodeForm) form;
    	DiscountCode discountCode;               
    	discountCode = (DiscountCode) request.getAttribute(WebKeys.DISCOUNTCODE_DISCOUNTS);
    	//Get the bean values
    	String codeId = discountCode.getCodeId();
    	String codeDesc = discountCode.getCodeDescription();
    	Date startDate = discountCode.getStartDate();
    	Date endDate = discountCode.getEndDate();
    	int discountType = discountCode.getDiscountType();
    	boolean freeShipping = discountCode.getFreeShipping();
    	boolean noBulkDiscount = discountCode.getNoBulkDisc();
    	float discountAmount = discountCode.getDiscountAmount();
    	int minOrder = discountCode.getMinOrder();
    	
    	//Set the values in the form
    	discountCodeForm.setCodeId(codeId);
    	discountCodeForm.setCodeDescription(codeDesc);
    	if(UtilMethods.isSet(startDate))
    	{
    		discountCodeForm.setStartDate(df.format(startDate));
    	}
    	if(UtilMethods.isSet(endDate))
    	{
    		discountCodeForm.setEndDate(df.format(endDate));
    	}
    	discountCodeForm.setDiscountType(discountType);
    	discountCodeForm.setFreeShipping(freeShipping);
    	discountCodeForm.setNoBulkDisc(noBulkDiscount);
    	discountCodeForm.setDiscountAmount(discountAmount);
    	discountCodeForm.setMinOrder(minOrder);
    	
    	List productList = InodeFactory.getParentsOfClassByRelationType(discountCode, ProductFormat.class,WebKeys.DISCOUNTCODE_PRODUCT_FORMAT);
		String[] idProductList = new String[productList.size()];
		for(int i=0; i < productList.size();i++)
		{		
			ProductFormat format = (ProductFormat) productList.get(i);
			idProductList[i] = String.valueOf(format.getInode());
		}
		discountCodeForm.setProducts(idProductList);    	
    }

	public void _deleteWebAsset(ActionRequest req, ActionResponse rest, PortletConfig config, ActionForm form, User user) throws Exception 
	{
		DiscountCode discountCode;
        String inode = req.getParameter("inode");
        /*long longInode = 0;
        if(InodeUtils.isSet(inode))
        {
        	longInode = Long.parseLong(inode);
        }*/
        discountCode = DiscountCodeFactory.getDiscountCode(inode);
        DiscountCodeFactory.deleteDiscountCode(discountCode);
        
        String message = "message.discountcode.delete";
		SessionMessages.add(req, "message",message);		
	}
}

/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.product.action;

import java.net.URLDecoder;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.factories.ProductPriceFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.model.ProductPrice;
import com.dotmarketing.portlets.product.struts.ProductFormatForm;
import com.dotmarketing.portlets.product.struts.ProductPriceForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author Salvador Di Nardo
 *
 */
public class EditFormatAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {		
		Logger.debug(this,"START EDIT FORMAT ACTION");
		ProductFormatForm productFormatForm = (ProductFormatForm) form;
		String productInode = productFormatForm.getProductInode();
		String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "";
		String referrer = req.getParameter("referrer");				

		if ((referrer!=null) && (referrer.length()!=0))
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");			
		}		

        DotHibernate.startTransaction();
		User user = _getUser(req);
		
        try {
			_retrieveFormat(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }        
        /*
         * Save the format occurrence 
         */
        if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {
            	ActionErrors ae = null;
            	ae = productFormatForm.validate(mapping,req);
				if (ae.size() == 0) 
				{
					_saveFormat(req, res, config, form, user);					
					//_sendToReferral(req,res,referrer);
				} else 
				{
					req.setAttribute(Globals.ERROR_KEY,ae);
					String input = mapping.getInput();
					setForward(req,input);
					//return;
				}
            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * If we are deleting the event,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
            try {
				_deleteFormat(req, res, config, form, user);				
				_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals(Constants.CANCEL)) {
        	_sendToReferral(req,res,referrer);
        }
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.COPY)) {
            try {
				_copyFormat(req, res, config, form, user);				
				_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals("savePrice")) {
        	ActionErrors ae = null;
        	ae = productFormatForm.validatePrice(mapping,req);
			if (ae.size() == 0)
			{
				_savePrice(req, res, config, form, user);
				//_sendToReferral(req,res,referrer);			
			} 
			else 
			{
				req.setAttribute(Globals.ERROR_KEY,ae);
				String input = mapping.getInput();
				setForward(req,input);					
			}		
        }
        else if ((cmd != null) && cmd.equals("deletePrice")) {
        	try {
				_deletePrice(req, res, config, form, user);
				//_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        DotHibernate.commitTransaction();
        _loadForm(req,res,config,form,user);
        if (InodeUtils.isSet(productInode))
        {
        	productFormatForm.setProductInode(productInode);
        }
        setForward(req, "portlet.ext.product.edit_format");
        Logger.debug(this,"END EDIT FORMAT ACTION");
    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveFormat(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode") != null) ? req.getParameter("inode") : "";
		String priceInode = (req.getParameter("priceInode") != null) ? req.getParameter("priceInode") : "";
		ProductFormat productFormat = null;
		ProductPrice productPrice = null;
		productFormat = ProductFormatFactory.getProductFormat(inode);
		productPrice = ProductPriceFactory.getProductPrice(priceInode);
		req.setAttribute(WebKeys.PRODUCT_PRODUCT_FORMAT,productFormat);
		req.setAttribute(WebKeys.PRODUCT_PRODUCT_PRICE,productPrice);
	}

	private void _saveFormat(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		ProductFormatForm productFormatForm = (ProductFormatForm) form;
        ProductFormat productFormat = (ProductFormat) req.getAttribute(WebKeys.PRODUCT_PRODUCT_FORMAT);
		BeanUtils.copyProperties(productFormat,productFormatForm);
		ProductFormatFactory.saveProductFormat(productFormat);
						
		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.format.saved");		
	}
	
	private void _deleteFormat(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
		ProductFormat productFormat = (ProductFormat) req.getAttribute(WebKeys.PRODUCT_PRODUCT_FORMAT);		
		ProductFormatFactory.deleteProductFormat(productFormat);

		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.format.deleted");
	}
	
	private void _copyFormat(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
		ProductFormat productFormat = (ProductFormat) req.getAttribute(WebKeys.PRODUCT_PRODUCT_FORMAT);		
		ProductFormatFactory.copyProductFormat(productFormat);

		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.format.copied");
	}
	
	private void _loadForm(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	{
		ProductFormatForm productFormatForm = (ProductFormatForm) form;		
		ProductFormat productFormat = (ProductFormat) req.getAttribute(WebKeys.PRODUCT_PRODUCT_FORMAT);
		ProductPrice productPrice = (ProductPrice) req.getAttribute(WebKeys.PRODUCT_PRODUCT_PRICE);
		ProductPriceForm productPriceForm = new ProductPriceForm ();
		
		try
		{
			BeanUtils.copyProperties(productFormatForm,productPrice);
			BeanUtils.copyProperties(productFormatForm,productFormat);	
			BeanUtils.copyProperties(productPriceForm,productPrice);	
		}
		catch(Exception ex)
		{
			Logger.debug(this,ex.toString());
		}
		
		List<ProductPrice> formatPrice = ProductPriceFactory.getAllProductPricesByFormat(productFormat);
		productFormatForm.setPrices(formatPrice);	
		req.setAttribute("productPriceForm", productPriceForm);
	}
	
	private void _savePrice(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		ProductFormatForm productFormatForm = (ProductFormatForm) form;
        ProductPrice productPrice = (ProductPrice) req.getAttribute(WebKeys.PRODUCT_PRODUCT_PRICE);
		//BeanUtils.copyProperties(productPrice,productFormatForm);
        productPrice.setMinQty(productFormatForm.getMinQty());
        productPrice.setMaxQty(productFormatForm.getMaxQty());
        productPrice.setRetailPrice(productFormatForm.getRetailPrice());
        productPrice.setPartnerPrice(productFormatForm.getPartnerPrice());
		productPrice.setProductFormatInode(productFormatForm.getInode());
		ProductPriceFactory.saveProductPrice(productPrice);		
		req.setAttribute(WebKeys.PRODUCT_PRODUCT_PRICE,new ProductPrice());	
		productFormatForm.setPriceInode(null);
						
		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.price.saved");		
	}
	
	private void _deletePrice(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
		ProductFormatForm productFormatForm = (ProductFormatForm) form;
		ProductPrice productPrice = (ProductPrice) req.getAttribute(WebKeys.PRODUCT_PRODUCT_PRICE);		
		ProductPriceFactory.deleteProductPrice(productPrice);
		req.setAttribute(WebKeys.PRODUCT_PRODUCT_PRICE,new ProductPrice());
		productFormatForm.setPriceInode(null);

		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.price.deleted");
	}	
}


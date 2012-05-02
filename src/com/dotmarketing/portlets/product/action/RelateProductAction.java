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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.struts.ProductForm;
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
public class RelateProductAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {
		Logger.debug(this,"START RELATED PRODUCT ACTION");
		String cmd = ((req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "");
		String referrer = req.getParameter("referrer");

		if ((referrer!=null) && (referrer.length()!=0)) 
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");
		}

        DotHibernate.startTransaction();
		User user = _getUser(req);
		
        try {
			_retrieveProduct(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }        
        /*
         * Save the format occurrence 
         */
        if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {            	            	       			
					_saveRelatedProducts(req, res, config, form, user);					
					_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * If we are deleting the event,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) 
        {
            try {
				_deleteRelatedProduct(req, res, config, form, user);
				_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals(Constants.CANCEL)) 
        {
        	_sendToReferral(req,res,referrer);
        }        
        DotHibernate.commitTransaction();
        _loadForm(req,res,config,form,user);        
        setForward(req, "portlet.ext.product.related_product");
        Logger.debug(this,"END RELATED PRODUCT ACTION");
    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode") != null) ? req.getParameter("inode") : "";
		Product product = null;
		product = ProductFactory.getProduct(inode);
		req.setAttribute(WebKeys.PRODUCT_PRODUCT,product);
	}

	private void _saveRelatedProducts(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		ProductForm productForm = (ProductForm) form;
		Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
		
		//Delete the old related entries;
		List<Product> relatedProducts = InodeFactory.getChildrenOfClassByRelationType(product,Product.class,WebKeys.PRODUCT_RELATED);
		for(Product relatedProduct:relatedProducts)
		{
			product.deleteChild(relatedProduct,WebKeys.PRODUCT_RELATED);
		}
				
		String[] relatedProductsInodes = productForm.getRelatedProducts();
		if (relatedProductsInodes != null)
		{
			for(String relatedProductInode : relatedProductsInodes)
			{
				if(InodeUtils.isSet(relatedProductInode))
				{
					Product relatedProduct = ProductFactory.getProduct(relatedProductInode);
					product.addChild(relatedProduct,WebKeys.PRODUCT_RELATED);
				}
			}
		}
       						
		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.product.related");		
	}
	
	private void _deleteRelatedProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
		Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
		String relatedProductInode = req.getParameter("relatedProductInode");
		if(InodeUtils.isSet(relatedProductInode))
		{
			Product relatedProduct = ProductFactory.getProduct(relatedProductInode);
			product.deleteChild(relatedProduct,WebKeys.PRODUCT_RELATED);
		}		

		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.product.relationdeleted");
	}
	
	private void _loadForm(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	{
		ProductForm productForm = (ProductForm) form;
		Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
		try
		{
			BeanUtils.copyProperties(productForm,product);
		}
		catch(Exception ex)
		{
			Logger.debug(this,ex.toString());
		}
		
		//Load All Products
		List<Product> listProducts = ProductFactory.getAllProducts();
		productForm.setProducts(listProducts);
		//Load the related products
		List<Product> relatedProducts = InodeFactory.getChildrenClassByRelationType(product,Product.class,WebKeys.PRODUCT_RELATED);
		String[] relatedProductsInodes = new String[relatedProducts.size()];
		for(int i = 0; i < relatedProducts.size();i++)
		{			
			relatedProductsInodes[i] = ((Product) relatedProducts.get(i)).getInode();
		}
		productForm.setRelatedProducts(relatedProductsInodes);
	}
}


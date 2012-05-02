/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.product.action;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
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

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.struts.ProductForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
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
public class EditProductAction extends DotPortletAction {
	
	public static boolean debug = false;
	private CategoryAPI catAPI = APILocator.getCategoryAPI();
	private UserAPI userAPI = APILocator.getUserAPI();
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {	
		Logger.debug(this,"START EDIT PRODUCT ACTION");
		ProductForm productForm = (ProductForm) form;
        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "";
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
         * Save the event occurrence 
         */
        if ((cmd != null) && cmd.equals(Constants.SAVE)) {
            try {
            	ActionErrors ae = null;
            	ae = productForm.validate(mapping,req,user);
            	if (ae.size() == 0)
            	{
            		_saveProduct(req, res, config, form, user);
					//_sendToReferral(req,res,referrer);
            	}
				else 
				{ 	
					req.setAttribute(Globals.ERROR_KEY, ae);				    
					String input = mapping.getInput();
					setForward(req,input);
					return;				
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
				_deleteProduct(req, res, config, form, user);
				_sendToReferral(req,res,referrer);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.COPY)) {
            try {
				_copyProduct(req, res, config, form, user);
				_sendToReferral(req,res,referrer);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals(Constants.CANCEL)) {
            try {				
				_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
        else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.REORDER)) {
            try {
            	_reorderProduct(req, res, config, form, user);
				_sendToReferral(req,res,referrer);
            } catch (ActionException ae) {
				_handleException(ae, req);
            }			
        }
		
        DotHibernate.commitTransaction();
        
        _loadForm(req,res,config,form,user); 
        setForward(req, "portlet.ext.product.edit_product");
        Logger.debug(this,"END EDIT PRODUCT ACTION");
    }


	///// ************** ALL METHODS HERE *************************** ////////
	private void _retrieveProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = ((InodeUtils.isSet(req.getParameter("inode"))) ? req.getParameter("inode") : "0");
		Product product = null;
		product = ProductFactory.getProduct(inode);
		req.setAttribute(WebKeys.PRODUCT_PRODUCT,product);
	}
	
	private void _saveProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
		
		ProductForm productForm = (ProductForm) form;
        Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
		BeanUtils.copyProperties(product,productForm);
		ProductFactory.saveProduct(product);
		
		//wipe out the old categories
		//java.util.Set _parents = product.getParents();
		List<Category> _cats = catAPI.getChildren(product, user, false);
		Iterator<Category> it = _cats.iterator();
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		
		while (it.hasNext()) {
			Category cat = it.next();
            boolean canUse = perAPI.doesUserHavePermission(cat, PERMISSION_READ, user);
            if(canUse){
            	catAPI.removeChild(product, cat, user, false);
            }
			//_parents.remove(cat);
		}
		
		//add the new Product Types
		String[] arr = productForm.getProductTypes();
		
		//Delete the old categories
		List<Category> oldCategories = catAPI.getChildren(product, WebKeys.PRODUCT_PRODUCTS_TYPE, false, null, user, false);
		
		for (int i = 0;i < oldCategories.size();i++) {
			Category cat = oldCategories.get(i);
			catAPI.removeChild(product, cat, WebKeys.PRODUCT_PRODUCTS_TYPE, user, false);
		}
		
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				Category cat = ( Category ) InodeFactory.getInode(arr[i], Category.class);
				catAPI.addChild(product, cat, WebKeys.PRODUCT_PRODUCTS_TYPE, user, false);
			}
		}
		//product.setParents(_parents);
		//add the new Topics
		arr = productForm.getTopics();
		
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				Category node = ( Category ) InodeFactory.getInode(arr[i], Category.class);
				catAPI.addChild(product, node, user, false);
//				node.addChild(product,WebKeys.PRODUCT_CATEGORIES);
				//_parents.add(node);
			}
		}
		//product.setParents(_parents);
		ProductFactory.saveProduct(product);
		
		//Save the event files
		_saveFiles(product, productForm.getFileIdList());
		
		//Save the event images
		_saveImages(product,productForm);
						
		//add message
		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
    	
		SessionMessages.add(httpReq, "message", "message.product.product.saved");		
	}
	private void _saveImages(Product product, ProductForm productForm) {

		//Delete old Images
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_SMALL_IMAGE);
		product.deleteChild(identifier,WebKeys.PRODUCT_SMALL_IMAGE);
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_MEDIUM_IMAGE);
		product.deleteChild(identifier,WebKeys.PRODUCT_MEDIUM_IMAGE);
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_LARGE_IMAGE);
		product.deleteChild(identifier,WebKeys.PRODUCT_LARGE_IMAGE);		
		
		DotHibernate.flush();

		//gets image inode and saves the identifier inode		
        String smallImage = productForm.getSmallImage();
        if (InodeUtils.isSet(smallImage)) {
        	identifier = (Identifier) InodeFactory.getInode(smallImage, Identifier.class);
        	product.addChild(identifier,WebKeys.PRODUCT_SMALL_IMAGE);
        }
                	
		//gets image inode and saves the identifier inode 
        String mediumImage = productForm.getMediumImage();
        if (InodeUtils.isSet(mediumImage)) {
        	identifier = (Identifier) InodeFactory.getInode(mediumImage, Identifier.class);
        	product.addChild(identifier,WebKeys.PRODUCT_MEDIUM_IMAGE);
        }        

		//gets image inode and saves the identifier inode 
        String largeImage = productForm.getLargeImage();
        if (InodeUtils.isSet(largeImage)) {
        	identifier = (Identifier) InodeFactory.getInode(largeImage, Identifier.class);
        	product.addChild(identifier,WebKeys.PRODUCT_LARGE_IMAGE);
        }
	}
	
	private void _saveFiles (Product product, String[] fileIds) {
		
		java.util.List _files = InodeFactory.getChildrenClassByRelationType(product, Identifier.class,WebKeys.PRODUCT_FILES);
		Iterator it = _files.iterator();
		while (it.hasNext()) {
			Identifier iden = ( Identifier ) it.next();
			product.deleteChild(iden, WebKeys.PRODUCT_FILES);
		}
		if(fileIds != null)
		{
			for (int i = 0; i < fileIds.length; i++) 
			{
				String identifiers = fileIds[i];
				if (InodeUtils.isSet(identifiers))
				{
					String[] identifierArray = identifiers.split(",");
					for(int j = 0;j < identifierArray.length;j++)
					{
						String identifierId = identifierArray[j];
						Identifier identifier = (Identifier) InodeFactory.getInode(identifierId, Identifier.class);
						product.addChild(identifier,WebKeys.PRODUCT_FILES);
					}
				}
			}
		}
	}
	
	private void _deleteProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
        Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
        //delete the product, this method delete the formats and the prices
        ProductFactory.deleteProduct(product);
		SessionMessages.add(req, "message", "message.product.product.deleted");
	}
	
	private void _copyProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
        Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
        //delete the product, this method delete the formats and the prices
        ProductFactory.copyProduct(product);
		SessionMessages.add(req, "message", "message.product.product.copied");
	}
	
	private void _reorderProduct(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception 
	{
        Enumeration enumerator = req.getParameterNames();
        while(enumerator.hasMoreElements())
        {
        	String parameterName = (String) enumerator.nextElement();
        	if (parameterName.indexOf("reorder") != -1)
        	{
        		String productInode = parameterName.split("_")[1];
        		String orderString = req.getParameter(parameterName);
        		int order = (UtilMethods.isInt(orderString) ? Integer.parseInt(orderString): 0);
        		Product product = ProductFactory.getProduct(productInode);
        		product.setSortOrder(order);        		
        		ProductFactory.saveProduct(product);
        	}
        }                        
		SessionMessages.add(req, "message", "message.product.product.reordered");
	}
	
	private void _loadForm(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user) throws Exception
	{
		Product product = (Product) req.getAttribute(WebKeys.PRODUCT_PRODUCT);
		ProductForm productForm = (ProductForm) form;
		try
		{
			BeanUtils.copyProperties(productForm,product);
		}
		catch(Exception ex)
		{
			Logger.debug(this,ex.toString());
		}
		//Copy the Images
				
		//IMAGES
		//Small Image
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_SMALL_IMAGE);
		productForm.setSmallImage(identifier.getInode());
		
		//Medium Image
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_MEDIUM_IMAGE);
		productForm.setMediumImage(identifier.getInode());
		
		//Large Image
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_LARGE_IMAGE);
		productForm.setLargeImage(identifier.getInode());
								          
		//Copy the Files
        ArrayList<String> fileIdentifiers = new ArrayList<String>();
		List identifiers = InodeFactory.getChildrenClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_FILES);
		Iterator it = identifiers.iterator();
		while (it.hasNext()) {
			identifier = (Identifier)it.next();
			fileIdentifiers.add(identifier.getInode());
		}
		String[] files = new String[fileIdentifiers.size()];
		for(int i = 0;i < files.length;i++)
		{
			files[i] = fileIdentifiers.get(i);
		}
		productForm.setFileIdList(files);

		//ProductType
        List<Category> categories = new ArrayList<Category>();
		try {
			categories = catAPI.getChildren(product, WebKeys.PRODUCT_PRODUCTS_TYPE, false, null, APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotDataException e) {
			Logger.error(this, e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(this, e.getMessage(), e);
		}
        String[] categoriesInodes = new String[categories.size()];
        for(int i = 0;i < categories.size();i++)
        {
        	categoriesInodes[i] = ((Category) categories.get(i)).getInode(); 
        }
        
        Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_CATEGORIES);
		List<Category> ecats = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntityCategories(entity);
		List<Category> _cats = catAPI.getChildren(product, userAPI.getSystemUser(), false);
		List<String> topics = new ArrayList<String>();
		for (Category c : ecats) {
			List<Category> cats;
			try {
				cats = catAPI.getChildren(c, user, false);
			} catch (Exception e) {
				Logger.error(this, e.getMessage(),e);
				continue;
			}
			for (Category category : cats) {
				for (Category _category: _cats) {
					if (category.getInode().equals(_category.getInode())) {
						topics.add(String.valueOf(category.getInode()));
						break;
					}
				}
			}
		}
		
		String[] ts = new String[topics.size()];
		for(int i = 0;i < topics.size();i++)
        {
			ts[i] = topics.get(i); 
        }
		
        productForm.setTopics(ts);
        productForm.setProductTypes(categoriesInodes);
	}
}
        
      



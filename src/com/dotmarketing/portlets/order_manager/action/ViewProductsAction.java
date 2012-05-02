/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.order_manager.action;

import java.net.URLDecoder;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.cms.product.struts.ProductsForm;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
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
public class ViewProductsAction extends DotPortletAction {
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {
		Logger.debug(this,"START LOAD PRODUCTS ACTION");		
		String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : "";
		String referrer = req.getParameter("referrer");	
		if (!UtilMethods.isSet(referrer))
		{
			referrer  = req.getParameter("referer");
		}
		ProductsForm productsForm = (ProductsForm) form;
		
		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();
		
		if ((referrer!=null) && (referrer.length()!=0))
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");			
		}		
		
		DotHibernate.startTransaction();
		User user = _getUser(req);
		
		try 
		{
			_retrieveProducts(req, res, config, form, user);
		} 
		catch (Exception ae) 
		{
			_handleException(ae, req);
		}     
		/*
		 * Save the format occurrence 
		 */
		if ((cmd != null) && cmd.equals(Constants.ADD)) {
			try 
			{    		  
				_addToCard(mapping,form,config,req,res,session);    
				_sendToReferral(req,res,referrer);
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}
		else if ((cmd != null) && cmd.equals(Constants.UPDATE)) {
			try 
			{    		  
				_updateCard(mapping,form,config,req,res,session);
				_sendToReferral(req,res,referrer);
			} 
			catch (Exception ae) 
			{
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
				_removeFromCard(mapping,form,config,req,res,session);				
				_sendToReferral(req,res,referrer);
			} catch (ActionException ae) {
				_handleException(ae, req);
			}			
		}
		else if ((cmd != null) && cmd.equals(Constants.CANCEL)) 
		{
			_sendToReferral(req,res,referrer);
		}
		else if	 ((cmd != null) && cmd.equals(Constants.VIEW)) 
		{
			setForward(req,"portlet.ext.order_manager.view_cart");
			return;
		}
		else if	 ((cmd != null) && cmd.equals("addDiscount")) 
		{
			_addDiscount(mapping,form,config,req,res,session);
			_sendToReferral(req,res,referrer);
		}
		else if	 ((cmd != null) && cmd.equals("removeDiscount")) 
		{
			_removeDiscount(mapping,form,config,req,res,session);
			_sendToReferral(req,res,referrer);
		}
		if((cmd != null) && cmd.equals("new"))
		{
			session.removeAttribute(WebKeys.SHOPPING_CART);
		}
		DotHibernate.commitTransaction();    
		
		setForward(req, "portlet.ext.order_manager.view_products");
		Logger.debug(this,"END LOAD PRODUCTS ACTION");
   }		
	
	private void _retrieveProducts(ActionRequest req,ActionResponse res,PortletConfig config,ActionForm form,User user)
	{
		_loadProductByCategory(form,req);
		_loadTypeProducts(form,req);
	}
	
	private void _loadProductByCategory(ActionForm lf,ActionRequest request)
	{
		ProductsForm productsForm = (ProductsForm) lf;
		String categoryInode = productsForm.getCategoryInode();
		String orderBy = productsForm.getOrderBy();
		orderBy = (UtilMethods.isSet(orderBy) ? orderBy : "title");
		int page = productsForm.getPage();
		int pageSize = productsForm.getPageSize();
		String direction = productsForm.getDirection();
		String filter = productsForm.getFilter();
		
		//Retrieve the products
		List<Product> listProducts = ProductFactory.getAllProductsByCategory(categoryInode,orderBy,filter,page,pageSize,direction,"");
		
		//Save the list of products
		productsForm.setListProducts(listProducts);
	}
	
	private void _loadTypeProducts(ActionForm lf,ActionRequest request)
	{
		ProductsForm productsForm = (ProductsForm) lf;
		
		String typeProductEntityName = WebKeys.PRODUCT_PRODUCTS_TYPE;
		Entity entity = EntityFactory.getEntity(typeProductEntityName);
			
		//Retrieve the products
		List<Category> listTypeProducts = InodeFactory.getParentsOfClass(entity,Category.class);
		//This is not a generic methods, it supose it has only one master category and only one level
		if(listTypeProducts.size() > 0){
			Category masterCategory = listTypeProducts.get(0);
			listTypeProducts = InodeFactory.getChildrenClass(masterCategory,Category.class);
		}
		
		//Save the list of products
		productsForm.setListTypeProducts(listTypeProducts);
	}
	
	private void _addToCard(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response,HttpSession session)
	{		
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		if (shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		
		ProductsForm productsForm = (ProductsForm) form;
		List<Product> products = productsForm.getListProducts();
		for(int i = 0;i < products.size();i++)
		{
			Product product = productsForm.getListProducts().get(i);
			List<ProductFormat> formats = product.getFormats();
			for(int j = 0;j < formats.size();j++)
			{
				ProductFormat format = formats.get(j);
				String selectedParameter = product.getInode() + "|" + format.getInode() + "|" + "ADD";
				
				if (UtilMethods.isSet(request.getParameter(selectedParameter)) &&
						request.getParameter(selectedParameter).equals("on"))
				{
					String quantityParameter = product.getInode() + "|" + format.getInode() + "|" + "QUANTITY";
					quantityParameter = request.getParameter(quantityParameter);
					int quantity = (UtilMethods.isSet(quantityParameter) && com.dotmarketing.util.UtilMethods.isInt(quantityParameter) ? Integer.parseInt(quantityParameter) : 0);
					//Add the items to the shoppingCart
					if(quantity > 0)
					{
						shoppingCart.addItem(quantity,format.getInode());
					}
				}
			}
		}
		SessionMessages.add(request, "message","message.order_manager.shopping_cart_added");
	}
	
	private void _updateCard(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response,HttpSession session)
	{		
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		if (shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		
		ProductsForm productsForm = (ProductsForm) form;
		List<Product> products = productsForm.getListProducts();
		for(int i = 0;i < products.size();i++)
		{
			Product product = productsForm.getListProducts().get(i);
			List<ProductFormat> formats = product.getFormats();
			for(int j = 0;j < formats.size();j++)
			{
				ProductFormat format = formats.get(j);
				String quantityParameter = product.getInode() + "|" + format.getInode() + "|" + "QUANTITY";
				quantityParameter = request.getParameter(quantityParameter);
				int quantity = (UtilMethods.isSet(quantityParameter) && com.dotmarketing.util.UtilMethods.isInt(quantityParameter) ? Integer.parseInt(quantityParameter) : -1);
				//Add the items to the shoppingCart
				if (quantity >= 0)
				{
					if(quantity > 0)
					{
						shoppingCart.setQuantityItem(quantity,format.getInode());
					}
					else
					{
						shoppingCart.removeItem(format.getInode());
					}
				}
			}
		}
		SessionMessages.add(request, "message","message.order_manager.shopping_cart_updated");
	}
		
	
	private void _removeFromCard(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response,HttpSession session)
	{		
		String formatInodeString = request.getParameter("formatInode");
		ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute(WebKeys.SHOPPING_CART);
		if (InodeUtils.isSet(formatInodeString))
		{
			String formatInode = formatInodeString;
			shoppingCart.removeItem(formatInode);
			SessionMessages.add(request, "message","message.order_manager.shopping_cart_deleted");
		}
	}
	
	private void _addDiscount(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response,HttpSession session)
	{		
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		if (shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		String discountString = request.getParameter("discount");
		String[] discounts = discountString.split(":");
		
		for(String discount : discounts)
		{
			discount = discount.trim();
			DiscountCode discountCode = DiscountCodeFactory.getDiscountCodeById(discount);
			if(InodeUtils.isSet(discountCode.getInode()))
			{
				shoppingCart.deleteDiscountCode(discount);
				shoppingCart.addDiscount(discountCode);
			}
		}		
		SessionMessages.add(request, "message","message.order_manager.shopping_cart_discount_added");
	}
	
	private void _removeDiscount(ActionMapping mapping, ActionForm form, PortletConfig config,ActionRequest request, ActionResponse response,HttpSession session)
	{		
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		if (shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		String discountString = request.getParameter("discountId");
				
		shoppingCart.deleteDiscountCode(discountString);
				
		SessionMessages.add(request, "message","message.order_manager.shopping_cart_discount_removed");
	}
}

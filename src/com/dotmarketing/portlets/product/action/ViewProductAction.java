/*
 * Created on 19/10/2004
 *
 */
package com.dotmarketing.portlets.product.action;

import java.util.ArrayList;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.portlets.product.struts.ProductForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.struts.PortletAction;

/**
 * @author Salvador Di Nardo
 *  
 */
public class ViewProductAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception { 
    	Logger.debug(this,"START VIEW PRODUCT ACTION");
    	_viewProducts(mapping,form,config,req,res);    	    
        if (req.getWindowState().equals(WindowState.NORMAL)) {            
            return mapping.findForward("portlet.ext.product.view");
        } else {            
            return mapping.findForward("portlet.ext.product.view_product");
        }        
    }

    private void _viewProducts(ActionMapping mapping, ActionForm form, PortletConfig config, PortletRequest req,
    		PortletResponse res) throws Exception 
    		{
    	
		Entity entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_CATEGORIES);
		if(InodeUtils.isSet(entity.getInode())){
				synchronized (this) {
					entity = com.dotmarketing.portlets.entities.factories.EntityFactory.getEntity(com.dotmarketing.util.WebKeys.PRODUCT_CATEGORIES);
					if(InodeUtils.isSet(entity.getInode())){
						entity.setEntityName(com.dotmarketing.util.WebKeys.PRODUCT_CATEGORIES);
						new HibernateUtil(entity.getClass()).save(entity);
					}
				}
		}
    	
    	
    	ProductForm productForm = (ProductForm) form;
    	String[] productsType = productForm.getProductTypes();
    	String keyword = productForm.getKeyword();
    	//productForm.setOrderBy("title");
    	String orderBy = productForm.getOrderBy();
    	String direction = productForm.getDirection();
    	
    	orderBy = (UtilMethods.isSet(orderBy) ? orderBy : "sort_order");
    	ArrayList<Product> listProducts = (ArrayList<Product>) ProductFactory.getAllProducts(productsType,keyword,orderBy,direction);
    	ArrayList<ArrayList<ProductFormat>> formatProducts = new ArrayList<ArrayList<ProductFormat>>(listProducts.size());
    	for(int i = 0; i < listProducts.size(); i++)
    	{
    		Product product = (Product) listProducts.get(i);
    		ArrayList<ProductFormat> productFormats = (ArrayList<ProductFormat>) ProductFormatFactory.getAllFormatsByProduct(product);
    		formatProducts.add(productFormats);
    	}
    	productForm.setProducts(listProducts);
    	productForm.setFormats(formatProducts);
    }
}
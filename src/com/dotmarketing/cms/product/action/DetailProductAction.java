package com.dotmarketing.cms.product.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.cms.product.struts.ProductsForm;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;

public class DetailProductAction extends DispatchAction 
{
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//Getting the user from the session		
		_loadProduct(lf,request);
		//_loadTypeProducts(lf,request);
		
		String bulk = request.getParameter("bulk");
		if (!UtilMethods.isSet(bulk) || !bulk.equals("true"))
		{
			return mapping.findForward("detailProduct");
		}
		else
		{
			return mapping.findForward("detailProductBulk");
		}	
	}	

	public ActionForm _loadProduct(ActionForm lf,HttpServletRequest request)
	{
		lf = (lf != null ? lf : new ProductsForm());
		ProductsForm productsForm = (ProductsForm) lf;
		if (InodeUtils.isSet(request.getParameter("inode")) && InodeUtils.isSet(request.getParameter("inode")))
		{
			productsForm.setInode(request.getParameter("inode"));
		}
		ProductFactory.loadProduct(lf,request);
		return lf;
	}
}

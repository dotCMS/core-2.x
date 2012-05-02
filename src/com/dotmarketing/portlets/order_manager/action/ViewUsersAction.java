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
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.NoSuchUserException;
import com.dotmarketing.cms.product.model.ShoppingCart;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.order_manager.struts.UsersForm;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


/**
 * @author Salvador Di Nardo
 *  
 */
public class ViewUsersAction extends DotPortletAction {
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			ActionRequest req, ActionResponse res)
	throws Exception {
		Logger.debug(this,"START LOAD USERS ACTION");		
		String cmd = (req.getParameter(Constants.CMD) != null) ? req.getParameter(Constants.CMD) : "";
		String referrer = req.getParameter("referer");		

		HttpSession session = ((ActionRequestImpl)req).getHttpServletRequest().getSession();

		if ((referrer!=null) && (referrer.length()!=0))
		{
			referrer = URLDecoder.decode(referrer,"UTF-8");			
		}		

		DotHibernate.startTransaction();
		User user = _getUser(req);

		try 
		{
			_retrieveUsers(req, res, config, form, user);
		} 
		catch (Exception ae) 
		{
			_handleException(ae, req);
		}     
		/*
		 * Save the format occurrence 
		 */
		if ((cmd != null) && cmd.equals("select")) {
			try 
			{   
				java.util.Map params = new java.util.HashMap();
				params.put("struts_action",new String[] {"/ext/order_manager/view_checkout"});				
				params.put("cmd",new String[] {Constants.VIEW});

				String checkOutReferrer = com.dotmarketing.util.PortletURLUtil.getActionURL(req,WindowState.MAXIMIZED.toString(),params);

				_selectUser(req,res,config,form,session);				
				_sendToReferral(req,res,checkOutReferrer);
				return;
			} 
			catch (Exception ae) 
			{
				_handleException(ae, req);
			}
		}
		else if ((cmd != null) && cmd.equals("register_user")) 
		{
			java.util.Map<String,String[]> params = new java.util.HashMap<String,String[]>();

//			params.put("struts_action",new String[] {"/ext/usermanager/register_user"});
			params.put("struts_action",new String[] {"/ext/usermanager/edit_usermanager"});

			params.put("referer",new String[] { referrer });
			params.put("cmd",new String[] {"findMeWithoutPassword"});
			//wraps request to get session object
			ActionRequestImpl reqImpl = (ActionRequestImpl)req;
			HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
			String af = com.dotmarketing.util.PortletURLUtil.getActionURL(httpReq,WindowState.MAXIMIZED.toString(),params);

			_sendToReferral(req, res, af);
			return;
		}
		else if ((cmd != null) && cmd.equals("after_register_user")) 
		{
			_selectUser(req,res,config,form,session);
			java.util.Map params = new java.util.HashMap();
			params.put("struts_action",new String[] {"/ext/order_manager/view_checkout"});				
			params.put("cmd",new String[] {Constants.VIEW});

			String checkOutReferrer = com.dotmarketing.util.PortletURLUtil.getActionURL(req,WindowState.MAXIMIZED.toString(),params);

			_sendToReferral(req,res,checkOutReferrer);
			return;
		}    		
		DotHibernate.commitTransaction();    

		setForward(req, "portlet.ext.order_manager.view_users");
		Logger.debug(this,"END LOAD USERS ACTION");
	}		

	private void _retrieveUsers(ActionRequest req,ActionResponse res,PortletConfig config,ActionForm form,User user) throws DotDataException
	{
		UsersForm usersForm = (UsersForm) form;
		String filter = usersForm.getFilter();
		int page = usersForm.getPage();
		int pageSize = usersForm.getPageSize();
		List<User> users = APILocator.getUserAPI().getUsersByNameOrEmail(filter,page,pageSize);
		usersForm.setUsers(users);
	}

	private void _selectUser(ActionRequest req,ActionResponse res,PortletConfig config,ActionForm form,HttpSession session) throws NoSuchUserException, DotDataException, DotSecurityException
	{		
		ShoppingCart shoppingCart = (ShoppingCart)session.getAttribute(WebKeys.SHOPPING_CART);
		if(shoppingCart == null)
		{
			shoppingCart = new ShoppingCart();
			session.setAttribute(WebKeys.SHOPPING_CART,shoppingCart);
		}
		//Obtain the User
		String userId = req.getParameter("userID");	
		if(!UtilMethods.isSet(userId)){
			HttpServletRequest httpReq = ((ActionRequestImpl)req).getHttpServletRequest();
			userId = (String)httpReq.getSession().getAttribute("userID");
			httpReq.getSession().removeAttribute("userID");
		}
		User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),true);
		//Add the User to the Shopping Cart
		shoppingCart.setUser(user);

		SessionMessages.add(req, "message","message.order_manager.shopping_cart_user_selected");
	}
}

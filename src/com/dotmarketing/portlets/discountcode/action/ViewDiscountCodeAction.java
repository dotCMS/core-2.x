/*
 * Created on Jun 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dotmarketing.portlets.discountcode.action;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.struts.DiscountCodeForm;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;

/**
 * @author Salvador
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ViewDiscountCodeAction extends DotPortletAction  
{
    public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig config,
			RenderRequest req, RenderResponse res)
		throws Exception {
        ArrayList discounts;
        try {
        	String orderBy = req.getParameter(WebKeys.DISCOUNTCODE_ORDER_BY);
        	String direction = req.getParameter(WebKeys.DISCOUNTCODE_DIRECTION);
        	
        	orderBy = (UtilMethods.isSet(orderBy) ? orderBy : "code_description");
        	direction = (UtilMethods.isSet(direction) ? direction : "asc");
        	
			if (req.getWindowState().equals(WindowState.NORMAL)) 
			{
		        Logger.debug(this, "Showing view answer question minimized");
		        int limit = 5;
		        discounts = _loadDiscountCode(form,orderBy,direction,limit);
		        req.setAttribute(WebKeys.DISCOUNTCODE_DISCOUNTS,discounts);
				return mapping.findForward("portlet.ext.discountcode.view");
			}
			else 
			{				
			    Logger.debug(this, "Showing view answer question maximized");
			    int limit = -1;			   			  
			    discounts = _loadDiscountCode(form,orderBy,direction,limit);
			    req.setAttribute(WebKeys.DISCOUNTCODE_DISCOUNTS,discounts);			    
				return mapping.findForward("portlet.ext.discountcode.view_discountcode");
			}			
		}
		catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);
			return mapping.findForward(com.liferay.portal.util.Constants.COMMON_ERROR);
		}
	}
    
    public void processAction(ActionMapping mapping, ActionForm form,PortletConfig config, ActionRequest req, ActionResponse res) throws Exception 
    {
        String cmd = req.getParameter(Constants.CMD);
		String referer = req.getParameter("referer");

		//wraps request to get session object
		
		if ((referer != null) && (referer.length() != 0)) {
			referer = URLDecoder.decode(referer, "UTF-8");
		}
				
		DotHibernate.startTransaction();
		
		User user = _getUser(req);
		
		if ((cmd != null) && cmd.equals(Constants.EDIT)) 
		{		   
			try 
			{
				Logger.debug(this, "Calling Edit Method");
				_editWebAsset(req, res, config, form, user);
			} 
			catch (Exception ae) 
			{				
				_handleException(ae, req);
				return;
			} 
		}

		if ((cmd != null) && cmd.equals(Constants.ADD)) 
		{
		    try {
		        Logger.debug(this, "Calling Add Method");
		        _saveWebAsset(req, res, config, form, user);
		        _sendToReferral(req, res, referer);
		    } catch (Exception ae) 
		    {
		        return;
		    }
		}
		DotHibernate.commitTransaction();
		setForward(req, "portlet.ext.askquint.edit_askquint");
    }
      
    public void _editWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
            throws Exception {
        // TODO Auto-generated method stub

    }
   
    public void _saveWebAsset(ActionRequest req, ActionResponse res, PortletConfig config, ActionForm form, User user)
            throws Exception 
    {
        try
        {
        	// TODO question???
            //AskQuint question = new AskQuint();        
            //BeanUtils.copyProperties(question,form);
            //AskQuintFactory.addAskQuintQuestion(question);            
        }
        catch(Exception ex)
        {
            
        }        
    } 
    
    private ArrayList _loadDiscountCode(ActionForm form, String orderBy, String direction, int limit) throws Exception
    {    	
    	DiscountCodeForm discountCodeForm = (DiscountCodeForm) form;    		
    	SimpleDateFormat df = new SimpleDateFormat(WebKeys.DateFormats.SHORTDATE);   
    	int discountType = discountCodeForm.getDiscountType();    		
    	Date startDate = null;
    	try
    	{
    		if (UtilMethods.isSet(discountCodeForm.getStartDate()))
    		{
    			startDate = df.parse(discountCodeForm.getStartDate());
    		}
    	}
    	catch(Exception ex)
    	{
    		Logger.debug(this, ex.toString());
    	}
    	
    	Date endDate = null;
    	try
    	{
    		if (UtilMethods.isSet(discountCodeForm.getEndDate()))
    		{
    			endDate = df.parse(discountCodeForm.getEndDate());    		
    		}
    	}
    	catch(Exception ex)
    	{
    		Logger.debug(this, ex.toString());
    	}
    	String codeId = discountCodeForm.getCodeId();    	
    	String codeDesc = discountCodeForm.getCodeDescription();
    	
    	ArrayList discounts;
    	discounts = (ArrayList) DiscountCodeFactory.searchDiscountCode(discountType,startDate,endDate,codeId,codeDesc,orderBy,direction,limit);
    	return discounts;    	    	
    }       
}
/*
 * Created on Mar 28, 2005
 */
package com.dotmarketing.portlets.jobs.cms.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.portlets.jobs.factories.EmailFactory;
import com.dotmarketing.portlets.jobs.factories.SearchfirmFactory;
import com.dotmarketing.portlets.jobs.model.Searchfirm;
import com.dotmarketing.portlets.jobs.struts.SearchfirmForm;

/**
 * @author Maru
 */
public class AddSearchFirmAction  extends DispatchAction{

	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Getting and setting request attributes
		SearchfirmForm form = (SearchfirmForm) lf;
		Searchfirm s =  SearchfirmFactory.getSearchfirm(request.getParameter("inode"));

		//copies back into the form
        BeanUtils.copyProperties(form, s);
        request.setAttribute("searchfirmForm",form);

		ActionForward af = (mapping.findForward("addSearchFirmPage"));
		return af;
	}

	public ActionForward save(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Getting and setting request attributes
		SearchfirmForm form = (SearchfirmForm) lf;
		Searchfirm s =  SearchfirmFactory.getSearchfirm(request.getParameter("inode"));
		BeanUtils.copyProperties(s, form);
		
		//Checking errors
		ActionMessages aes = form.validate(mapping, request);
		if(aes != null && aes.size() > 0){
	        request.setAttribute("searchfirmForm",form);
			saveMessages(request,aes);
			return mapping.getInputForward();
		}

        SearchfirmFactory.save(s);
        
        //copies back into the form
        BeanUtils.copyProperties(form, s);
        request.setAttribute("searchfirmForm",form);
        
        //Forwarding to the page
		ActionForward af = mapping.findForward("addSearchFirmPreviewPage");
		
		return af;
	}
	public ActionForward receipt(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Getting and setting request attributes
		SearchfirmForm form = (SearchfirmForm) lf;
		Searchfirm s =  SearchfirmFactory.getSearchfirm(request.getParameter("inode"));

		//copies back into the form
        BeanUtils.copyProperties(form, s);
        request.setAttribute("searchfirmForm",form);

        //Forwarding to the page
		ActionForward af = mapping.findForward("addSearchFirmReceiptPage");
		return af;
	}
	public ActionForward success(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		SearchfirmForm searchfirm = (SearchfirmForm) form;
		Searchfirm newSearchfirm=  SearchfirmFactory.getSearchfirm(searchfirm.getInode()+"");
		
		//send confirmation email.
		EmailFactory.sendCareerPostingReceipt("" + newSearchfirm.getInode(),newSearchfirm.getEmail(),"addSearchFirm");

		ActionForward af = (mapping.findForward("addSearchFirmPageThankYou"));
		return af;
	}
	
}

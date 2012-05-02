package com.dotmarketing.portlets.facilities.action;

import java.net.URLDecoder;

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
import com.dotmarketing.portlets.facilities.factories.FacilityFactory;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.portlets.facilities.struts.FacilityForm;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;

/**
 * @author Maria
 */

public class EditFacilityAction extends DotPortletAction {
	
	public static boolean debug = false;
	
	public void processAction(
			 ActionMapping mapping, ActionForm form, PortletConfig config,
			 ActionRequest req, ActionResponse res)
		 throws Exception {

        String cmd = (req.getParameter(Constants.CMD)!=null)? req.getParameter(Constants.CMD) : Constants.EDIT;
		String referer = req.getParameter("referer");

		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl)req;
		HttpServletRequest httpReq = reqImpl.getHttpServletRequest();

		if ((referer!=null) && (referer.length()!=0)) {
			referer = URLDecoder.decode(referer,"UTF-8");
		}

		//Logger.info("\n\n\nEditFacilityAction :: cmd=" + cmd);
		
        new DotHibernate().startTransaction();

		User user = _getUser(req);
		
        try {
			//Logger.info("Calling Retrieve method");
			_retrieveFacility(req, res, config, form, user);

        } catch (ActionException ae) {
        	_handleException(ae, req);
        }

		Facility c = (Facility) req.getAttribute(WebKeys.FACILITY_EDIT);
		 
        /*
         * We are editing the Facility
         */
        if ((cmd != null) && cmd.equals(Constants.EDIT)) {
            try {
            	//Logger.info("Calling Edit Method");
				_editFacility(req, res, config, form, user);

				//if we have a unrun Facility, show it.  Else, redirect to the report page
				setForward(req,"portlet.ext.facilities.edit_facility");

	        } catch (ActionException ae) {
				_handleException(ae, req);
	        }
        }
        
        /*
         * If we are updating the Facility, copy the information
         * from the struts bean to the hbm inode and run the
         * update action and return to the list
         */
        else if ((cmd != null) && cmd.equals(Constants.ADD)) {
            try {

				if (Validator.validate(req,form,mapping)) {
					//Logger.info("Calling Save Method");
					_saveFacility(req, res, config, form, user);
					_sendToReferral(req,res,referer);
				}
				else {
					setForward(req,"portlet.ext.facilities.edit_facility");
				}

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
        }
        /*
         * If we are deleting the Facility,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals(Constants.DELETE)) {
            try {
				//Logger.info("Calling Delete Method");
				_deleteFacility(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
        }
        /*
         * If we are deleting the selected Facilities,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals("deleteselected")) {
            try {
				//Logger.info("\n\nCalling Delete Selected Method");
				_deleteSelectedFacilities(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
        }
        /*
         * If we are deleting the selected Facilities,
         * run the delete action and return to the list
         *
         */
        else if ((cmd != null) && cmd.equals("reorder")) {
            try {
				//Logger.info("Calling Reorder Method");
				_orderSelectedFacilities(req, res, config, form, user);

            } catch (ActionException ae) {
				_handleException(ae, req);
            }
			_sendToReferral(req,res,referer);
        }
	}

	///// ************** ALL METHODS HERE *************************** ////////

	public void _retrieveFacility(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String inode = (req.getParameter("inode")!=null) ? req.getParameter("inode") : "";
		
		Facility f = FacilityFactory.getFacility(inode);
		
        req.setAttribute(WebKeys.FACILITY_EDIT, f);

	}
	public void _editFacility(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

		FacilityForm ff = (FacilityForm) form;
		Facility f = (Facility) req.getAttribute(WebKeys.FACILITY_EDIT);
		
		BeanUtils.copyProperties(form, f);

	}

	public void _saveFacility(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
		throws Exception {

		Facility c = (Facility) req.getAttribute(WebKeys.FACILITY_EDIT);
		FacilityForm cfform = (FacilityForm) form;

		BeanUtils.copyProperties(req.getAttribute(WebKeys.FACILITY_EDIT), form);

		//Logger.info("Saving Facility:" + c.getInode());

		InodeFactory.saveInode(c);

		//no sure if this is needed
		InodeFactory.saveInode(c);
		
		//add message
		SessionMessages.add(req, "message", "message.facility.saved");

	}
	
	public void _deleteFacility(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		Facility c = (Facility) req.getAttribute(WebKeys.FACILITY_EDIT);
		FacilityFactory.deleteFacility(c);
		SessionMessages.add(req, "message", "message.facility.deleted");

	}

	public void _deleteSelectedFacilities(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {

		String[] deleteInodes = req.getParameterValues("delInode");
		
		for (int i=0;i<deleteInodes.length;i++) {
			String delInode = deleteInodes[i];
			Facility f = FacilityFactory.getFacility(delInode);
			FacilityFactory.deleteFacility(f);
		}
		
		SessionMessages.add(req, "message", "message.facilities.deleted");

	}

	public void _orderSelectedFacilities(ActionRequest req, ActionResponse res,PortletConfig config,ActionForm form, User user)
	throws Exception {
	
		int count = 0;
	    try	{ 
	    	count = Integer.parseInt(req.getParameter("count"));
		    String[] order = new String[(count)];
		    int x = 9999;
		    for(int i = 0;i< order.length;i++){
		    	Facility f = FacilityFactory.getFacility(req.getParameter("sInode" + i));
		        f.setSortOrder(Integer.parseInt(req.getParameter("sortOrder" + i)  + x--));
		        InodeFactory.saveInode(f);
		    }
	    }
	    catch (Exception e) {
	    	//Logger.info("Error ordering");
	    }
	    SessionMessages.add(req, "message", "message.facilities.order");
	}
}

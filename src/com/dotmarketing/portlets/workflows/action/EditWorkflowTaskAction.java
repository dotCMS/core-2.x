package com.dotmarketing.portlets.workflows.action;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_PUBLISH;
import static com.dotmarketing.business.PermissionAPI.PERMISSION_WRITE;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.WebAsset;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.PublishFactory;
import com.dotmarketing.factories.WebAssetFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.calendar.business.EventAPI;
import com.dotmarketing.portlets.calendar.model.Event;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.workflows.factories.WorkflowsFactory;
import com.dotmarketing.portlets.workflows.model.WorkflowComment;
import com.dotmarketing.portlets.workflows.model.WorkflowHistory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.portlets.workflows.struts.WorkflowTaskForm;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.util.WebKeys.WorkflowStatuses;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.struts.ActionException;
import com.liferay.portal.util.Constants;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.portlet.ActionResponseImpl;
import com.liferay.util.servlet.SessionMessages;
/**
 * @author David
 */

public class EditWorkflowTaskAction extends DotPortletAction {

	private ContentletAPI conAPI = APILocator.getContentletAPI();
	
	public void processAction(ActionMapping mapping, ActionForm form,
			PortletConfig config, ActionRequest req, ActionResponse res)
	throws Exception {
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		String cmd = req.getParameter(Constants.CMD);
		String referer = req.getParameter("referer");
		HttpServletRequest request =  ((ActionRequestImpl)req).getHttpServletRequest();
		Logger.debug(this, "EditWorkflowTaskAction cmd=" + cmd);

		DotHibernate.startTransaction();

		User user = _getUser(req);

		try {
			Logger.debug(this, "Calling Retrieve method");
			_retrieveWorkflowTask(req, WebKeys.WORKFLOW_TASK_EDIT);
		} catch (Exception ae) {
			_handleException(ae, req);
			return;
		}

		/*
		 * We are adding a new workflow task
		 */
		if ((cmd == null) || cmd.equals(Constants.ADD)) {
			try {
				Logger.debug(this, "Calling Edit Method");
				_addWorkflowTask(req, res, config, form, user);
				setForward(req, "portlet.ext.workflows.edit_workflow_task");
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			} 
		} 
		/*
		 * We are editing the workflow task
		 */
		if ((cmd != null) && cmd.equals(Constants.EDIT)) {
			try {
				Logger.debug(this, "Calling Edit Method");
				_editWorkflowTask(req, res, config, form, user);
				setForward(req, "portlet.ext.workflows.edit_workflow_task");
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			} 
		}
		/*
		 * We are viewing the workflow task
		 */
		if ((cmd != null) && cmd.equals(Constants.VIEW)) {
			try {
				Logger.debug(this, "Calling View Method");
				_viewWorkflowTask(req, res, config, form, user);
				setForward(req, "portlet.ext.workflows.view_workflow_task");
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			} 
		}

		/*
		 * If we are updating the workflow task
		 */
		if ((cmd != null) && cmd.equals(Constants.SAVE)) {
			try {

				if (Validator.validate(req, form, mapping)) {
					Logger.debug(this, "Calling Save Method");
					_saveWorkflowTask(req, res, config, form, user);
					_sendToReferral(req, res, referer);
					return;
				}

			} catch (Exception ae) {
				_handleException(ae, req);
			}

		}/*
		 * We are deleting the workflow task
		 */
		if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.FULL_DELETE)) {
			try {
				Logger.debug(this, "Calling Delete Method");
				_deleteWorkFlowTask(req);
				_sendToReferral(req, res, referer);
				return;
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			} 
		}
		/*
		 * If we are changing the task status, run the change action and return to 
		 * task
		 *
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.CHANGE_STATUS)) {
			try {
				Logger.debug(this, "Calling Change Status Method");
				_changeWorkflowTaskStatus(req, res, config, form, user);
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		} else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.ADD_COMMENT)) {
			try {
				Logger.debug(this, "Calling Add Comment Method");
				_addWorkflowComment(req, res, config, form, user);
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		} else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.ADD_FILE)) {
			try {
				Logger.debug(this, "Calling Add File Method");
				_addFileToWorkflow(req, res, config, form, user);
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		} else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.REMOVE_FILE)) {
			try {
				Logger.debug(this, "Calling Remove File Method");
				_removeFileToWorkflow(req, res, config, form, user);
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		} else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.ASSIGN_TASK)) {
			try {
				Logger.debug(this, "Calling Assign Task Method");
				_assignWorkflowTask(req, res, config, form, user);
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
				return;
			}
		} 
		/*
		 * To Publish the webasset
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.PUBLISH)) {
			try {

				Inode asset = (Inode) InodeFactory.getInode(request.getParameter("asset_inode"), Inode.class);

				if (!perAPI.doesUserHavePermission(asset, PERMISSION_PUBLISH, user))
					throw new DotRuntimeException("The user doesn't have the required permissions.");
				
				String inode = asset.getInode();
				
				//If the contentlet is an event, it is published using EventAPI; if it is not,
				//it is published using ContentletAPI
				if(conAPI.isContentlet(inode)){			
					EventAPI eAPI = APILocator.getEventAPI();
					Structure es = eAPI.getEventStructure();
					Contentlet con = conAPI.findContentletByIdentifier(asset.getIdentifier(), false, 0, user, false);
					if(con == null || !InodeUtils.isSet(con.getInode())){
						Logger.error(this, "Unable to find contentlet inode : " + inode);
						throw new DotRuntimeException("Unable to find contentlet inode : " + inode);
					}
					if(con.getStructureInode() == es.getInode()){
						Event e = eAPI.find(con.getIdentifier(), false, user, false);
						conAPI.publish(e, user, false);
					}else{
						conAPI.publish(con, user, false);
						SessionMessages.add(request.getSession(false),"message",  "message.contentlet.published");
					}
				}else{
					PublishFactory.publishAsset(asset, request);
				}
				req.setAttribute("new_workflow_status", "RESOLVED");
				_changeWorkflowTaskStatus(req, res, config, form, user);
				_sendToReferral(req, res, referer);
				
			} catch (Exception ae) {
				_handleException(ae, req);
			}

		}/*
		 * To UnPublish the webasset
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.UNPUBLISH)) {
			try {
				Contentlet con = null;
				try{
					con = conAPI.find(request.getParameter("asset_inode"), user, false);
					
				}catch (DotDataException dde) {
					Logger.debug(this,"Not a contentlet so checking for Inode");
				}
				if(con == null){
					Inode inode = (Inode) InodeFactory.getInode(request.getParameter("asset_inode"), Inode.class);
					WebAsset asset = null;
					if (inode instanceof HTMLPage) {
						asset = (WebAsset)InodeFactory.getInode(inode.getInode(), HTMLPage.class);
					} 
					
					Folder parent = (Folder)InodeFactory.getParentOfClass(asset, Folder.class);
		        
			        if (!perAPI.doesUserHavePermission(asset, PERMISSION_PUBLISH, user))
						throw new DotRuntimeException("The user doesn't have the required permissions.");
			        
			        WebAssetFactory.unPublishAsset(asset, user.getUserId(), parent);
			        SessionMessages.add(request.getSession(false), "message","message.workflow.unpublish.succes");
					_sendToReferral(req, res, referer);
				}else{
					conAPI.unpublish(con, user, false);
				}
				SessionMessages.add(request.getSession(false), "message","message.workflow.unpublish.succes");
				_sendToReferral(req, res, referer);
			} catch (Exception ae) {
				_handleException(ae, req);
			}
		}/*
		 * To Archive the webasset
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.DELETE)) {
			try {
				Contentlet con = new Contentlet();
				try{
					con = conAPI.find(request.getParameter("asset_inode"), user, false);
					
				}catch (DotDataException dde) {
					Logger.debug(this,"Not a contentlet so checking for Inode");
				}
				if(con == null){
					Inode inode = (Inode) InodeFactory.getInode(request.getParameter("asset_inode"), Inode.class);
					WebAsset asset = null;
					if (inode instanceof HTMLPage) 
						asset = (WebAsset)InodeFactory.getInode(inode.getInode(), HTMLPage.class);
					if (!perAPI.doesUserHavePermission(asset, PERMISSION_WRITE, user))
						throw new DotRuntimeException("The user doesn't have the required permissions.");
			        WebAssetFactory.archiveAsset(asset, user.getUserId());
				}else{
					conAPI.archive(con, user, false);
					if(!conAPI.isInodeIndexed(con.getInode())){
						Logger.error(this, "Timed out while waiting for index to return");
					}	
				}
				req.setAttribute("new_workflow_status", "RESOLVED");
				_changeWorkflowTaskStatus(req, res, config, form, user);
				_sendToReferral(req, res, referer);
				SessionMessages.add(request.getSession(false),"message",  "message.workflow.archive.succes");
				_sendToReferral(req, res, referer); 
				if(con!=null){
				  conAPI.refresh(con);
			    }
			}catch (Exception ae) {
				_handleException(ae, req);
			}
		}/*
		 * To UnArchive the webasset
		 */
		else if ((cmd != null) && cmd.equals(com.dotmarketing.util.Constants.UNDELETE)) {
			try {
				Contentlet con = new Contentlet();
				try{
					con = conAPI.find(request.getParameter("asset_inode"), user, false);
					
				}catch (DotDataException dde) {
					Logger.debug(this,"Not a contentlet so checking for Inode");
				}
				if(con == null){
					Inode inode = (Inode) InodeFactory.getInode(request.getParameter("asset_inode"), Inode.class);
					WebAsset asset = null;
					if (inode instanceof HTMLPage) 
						asset = (WebAsset)InodeFactory.getInode(inode.getInode(), HTMLPage.class);				
					if (!perAPI.doesUserHavePermission(asset, PERMISSION_WRITE, user))
						throw new DotRuntimeException("The user doesn't have the required permissions.");
			        
			        WebAssetFactory.unArchiveAsset(asset);
				}else{
					conAPI.unarchive(con, user, false);
					if(!conAPI.isInodeIndexed(con.getInode())){
						Logger.error(this, "Timed out while waiting for index to return");
					}
				}
				SessionMessages.add(request.getSession(false),"message", "message.workflow.unarchive.succes");
				_sendToReferral(req, res, referer);
				if(con!=null){
				   conAPI.refresh(con);
				}
			} catch (Exception ae) {
				_handleException(ae, req);
			}
		} else

			Logger.debug(this, "Unspecified Action");

		DotHibernate.commitTransaction();

		setForward(req, "portlet.ext.workflows.edit_workflow_task");
	}

	///// ************** ALL METHODS HERE *************************** ////////



	private void _saveWorkflowTask(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		Logger.debug(this, "I'm saving the workflowtask");

		//wraps request to get session object
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();

		boolean isNew = false;
		StringBuffer changeHist = new StringBuffer(LanguageUtil.get(user, "message.workflow.Task.edited"));

		WorkflowTaskForm wtForm = (WorkflowTaskForm) form;

		//gets the current WorkflowTask being edited from the request object
		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);

		if (task.getTitle()== null || !task.getTitle().equals(wtForm.getTitle())) {
			changeHist.append(LanguageUtil.get(user, "Title")+": " + task.getTitle() + " -> " + wtForm.getTitle() + "<br>");
			task.setTitle(wtForm.getTitle());
		}

		if (task.getDescription()== null || !task.getDescription().equals(wtForm.getDescription())) {
			changeHist.append(LanguageUtil.get(user, "Description")+": " + task.getDescription() + " -> " + wtForm.getDescription() + "<br>");
			task.setDescription(wtForm.getDescription());
		}

		String taskAssignment = wtForm.getAssignedTo();
		if (taskAssignment.startsWith("role-")) {
			task.setBelongsTo(taskAssignment.substring(5, taskAssignment.length()));
			task.setAssignedTo(LanguageUtil.get(user, "Nobody"));
		} else {
			task.setAssignedTo(taskAssignment.substring(5, taskAssignment.length()));
		}

		task.setModDate(new Date());
		if (!InodeUtils.isSet(task.getInode())) {
			task.setCreationDate(new Date());
			task.setCreatedBy(user.getUserId());
			task.setStatus(WorkflowStatuses.OPEN.toString());
			isNew = true;
		}
		if (!wtForm.isNoDueDate()) {
			Date dueDate = UtilMethods.htmlToDate((Integer.parseInt(wtForm.getDueDateMonth())+1)+"/"+wtForm.getDueDateDay()+"/"+wtForm.getDueDateYear());
			if (task.getDueDate() == null || !task.getDueDate().equals(dueDate)) {
				changeHist.append(LanguageUtil.get(user, "Due-date")+": " + UtilMethods.dateToHTMLDate(task.getDueDate()) + " -> " + UtilMethods.dateToHTMLDate(dueDate) + "<br>");
				task.setDueDate(dueDate);
			}
		} else {
			if (task.getDueDate() != null) {
				changeHist.append(LanguageUtil.get(user, "Due-date")+": " + UtilMethods.dateToHTMLDate(task.getDueDate()) + " -> <br>");
				task.setDueDate(null);
			}
		}

		if (!InodeUtils.isSet(task.getInode())) 
			task.setWebasset(wtForm.getWebasset());

		InodeFactory.saveInode(task);

		if (isNew) {

			_logWorkflowTaskHistory(task, user, LanguageUtil.get(user, "Task-Creation"));
			WorkflowsFactory.sendWorkflowChangeEmails (task, "New Workflow Task Created", "New Task", user, httpReq, httpRes);        

		} else {
			WorkflowsFactory.sendWorkflowChangeEmails (task, "New Workflow Task Changed", changeHist.toString(),user, httpReq, httpRes);        
			_logWorkflowTaskHistory(task, user, changeHist.toString());
		}
		SessionMessages.add(httpReq, "message", "message.workflow.saved");

	}

	private void _addWorkflowTask(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTaskForm taskform = (WorkflowTaskForm) form;

		GregorianCalendar now = new GregorianCalendar ();
		taskform.setDueDateDay(String.valueOf(now.get(GregorianCalendar.DATE)));    
		taskform.setDueDateMonth(String.valueOf(now.get(GregorianCalendar.MONTH)));    
		taskform.setDueDateYear(String.valueOf(now.get(GregorianCalendar.YEAR)));    

	}

	private void _editWorkflowTask(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		WorkflowTaskForm taskform = (WorkflowTaskForm) form;
		BeanUtils.copyProperties(taskform, task);
		if (task.getDueDate() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(task.getDueDate());
			taskform.setDueDateDay(String.valueOf(cal.get(Calendar.DATE)));
			taskform.setDueDateMonth(String.valueOf(cal.get(Calendar.MONTH)));
			taskform.setDueDateYear(String.valueOf(cal.get(Calendar.YEAR)));
		} else {
			taskform.setNoDueDate(true);
		}
	}

	private void _viewWorkflowTask(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		WorkflowTaskForm taskform = (WorkflowTaskForm) form;
		BeanUtils.copyProperties(taskform, task);
		if (task.getDueDate() != null) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(task.getDueDate());
			taskform.setDueDateDay(String.valueOf(cal.get(Calendar.DATE)));
			taskform.setDueDateMonth(String.valueOf(cal.get(Calendar.MONTH)));
			taskform.setDueDateYear(String.valueOf(cal.get(Calendar.YEAR)));
		}
	}

	private void _changeWorkflowTaskStatus(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();
		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		String oldStatus = task.getStatus();
		String newStatus = req.getParameter ("new_status");
		if(req.getAttribute("new_workflow_status") != null)
			newStatus = (String) req.getAttribute("new_workflow_status");
		
		task.setStatus(newStatus);

		if(oldStatus.equals("OPEN")||oldStatus.equals(LanguageUtil.get(user, "OPEN")))
		{
			oldStatus=LanguageUtil.get(user, "OPEN");
		}
		else if(oldStatus.equals("RESOLVED")||oldStatus.equals(LanguageUtil.get(user, "RESOLVED")))
		{
			oldStatus=LanguageUtil.get(user, "RESOLVED");
		}
		else
		{oldStatus=LanguageUtil.get(user, "CANCELLED");}
		//newStatus=task.getStatus();
		
		if(newStatus.equals("OPEN")||newStatus.equals(LanguageUtil.get(user, "OPEN")))
		{
			newStatus=LanguageUtil.get(user, "OPEN");
		}
		else if(newStatus.equals("RESOLVED")||newStatus.equals(LanguageUtil.get(user, "RESOLVED")))
		{
			newStatus=LanguageUtil.get(user, "RESOLVED");
		}
		else
		{newStatus=LanguageUtil.get(user, "CANCELLED");}
			
		 
		
		String changeDesc = LanguageUtil.get(user, "edit_worflow.history.status_changed")+" " + oldStatus+ " -> " + newStatus;
		_logWorkflowTaskHistory(task, user, changeDesc);
		WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user, httpReq, httpRes);        


	}

	private void _addWorkflowComment(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();
		String comment = req.getParameter ("comment");
		WorkflowComment taskComment = new WorkflowComment ();
		taskComment.setComment(comment);
		taskComment.setCreationDate(new Date());
		taskComment.setPostedBy(user.getUserId());
		InodeFactory.saveInode(taskComment);
		task.addChild(taskComment);

		String changeDesc = LanguageUtil.get(user, "edit_worflow.history.comment.added") + comment;
		_logWorkflowTaskHistory(task, user, changeDesc);

		WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user, httpReq, httpRes);        

	}

	private void _addFileToWorkflow(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();
		String fileToAttachInode = req.getParameter ("file_inode");
		File fileToAttach = (File) InodeFactory.getInode(fileToAttachInode, File.class);
		if (InodeUtils.isSet(fileToAttach.getInode())) {
			task.addChild(fileToAttach);
			String changeDesc = LanguageUtil.get(user, "edit_worflow.history.file.added")+": " + fileToAttach.getFileName();
			_logWorkflowTaskHistory(task, user, changeDesc);
			WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user, httpReq, httpRes);        

		}

	}

	private void _removeFileToWorkflow(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {

		WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();
		String fileToAttachInode = req.getParameter ("file_inode");
		File fileToAttach = (File) InodeFactory.getInode(fileToAttachInode, File.class);
		if (InodeUtils.isSet(fileToAttach.getInode())) {
			task.deleteChild(fileToAttach);
			String changeDesc = LanguageUtil.get(user, "edit_worflow.history.file.removed")+": " + fileToAttach.getFileName();
			_logWorkflowTaskHistory(task, user, changeDesc);
			WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user, httpReq, httpRes);        

		}

	}

	private void _assignWorkflowTask(ActionRequest req, ActionResponse res,
			PortletConfig config, ActionForm form, User user) throws Exception {
		String newUserId = req.getParameter ("user_id");
		ActionRequestImpl reqImpl = (ActionRequestImpl) req;
		ActionResponseImpl resImpl  = (ActionResponseImpl) res;
		HttpServletRequest httpReq= reqImpl.getHttpServletRequest();
		HttpServletResponse httpRes= resImpl.getHttpServletResponse();
		if (UtilMethods.isSet(newUserId) && (5 < newUserId.length())) {
			WorkflowTask task = (WorkflowTask) req.getAttribute(WebKeys.WORKFLOW_TASK_EDIT);
			String lastUser = task.getAssignedTo();
			
			if (newUserId.startsWith("role-")) {
				task.setBelongsTo(newUserId.substring(5, newUserId.length()));
				task.setAssignedTo("Nobody");
			} else {
				task.setBelongsTo(null);
				task.setAssignedTo(newUserId.substring(5, newUserId.length()));
			}
			
			InodeFactory.saveInode(task);
			if (UtilMethods.isSet(lastUser)){
				
String changeDesc = LanguageUtil.get(user, "edit_worflow.history.task.assigned")+"  " + UtilMethods.getUserFullName(lastUser) +" " +
			LanguageUtil.get(user, "edit_worflow.history.to")+"  " + UtilMethods.getUserFullName(task.getAssignedTo());
				_logWorkflowTaskHistory(task, user, changeDesc);
				WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user, httpReq, httpRes);        
			} else {
				String changeDesc = LanguageUtil.get(user, "edit_worflow.history.task.assigned")+" " + UtilMethods.getUserFullName(task.getAssignedTo());				_logWorkflowTaskHistory(task, user, changeDesc);
				WorkflowsFactory.sendWorkflowChangeEmails (task, "Workflow Task Changed", changeDesc, user,  httpReq, httpRes);        
			}
		}
	}

	private void _retrieveWorkflowTask(ActionRequest req, String webkey) throws Exception {
		WorkflowTask webAsset = (WorkflowTask) InodeFactory.getInode(req
				.getParameter("inode"), WorkflowTask.class);
		req.setAttribute(webkey, webAsset);
	}

	private void _logWorkflowTaskHistory (WorkflowTask task, User user, String history) {
		WorkflowHistory hist = new WorkflowHistory ();
		hist.setChangeDescription(history);
		hist.setCreationDate(new Date ());
		hist.setMadeBy(user.getUserId());
		InodeFactory.saveInode(hist);
		task.addChild(hist);
	}
	
	/**
	 * Delete a workflow task,comments and history from db
	 * @param req ActionRequest
	 * @throws DotHibernateException
	 */
	private void _deleteWorkFlowTask(ActionRequest req) throws DotHibernateException{

		HttpServletRequest request =  ((ActionRequestImpl)req).getHttpServletRequest();
		HibernateUtil.startTransaction();
		try {
			WorkflowTask task = WorkflowsFactory.getWorkflowTaskByInode(request.getParameter("inode"));
			/*Clean the comments*/
			List<WorkflowComment> commentsList = WorkflowsFactory.getWorkflowCommentsOfTask(task);
			for(WorkflowComment comment : commentsList){
				WorkflowsFactory.deleteWorkflowComment(comment);	
			}
			/*Clean the history*/
			List<WorkflowHistory> historyList = WorkflowsFactory.getWorkflowHistoryOfTask(task);
			for(WorkflowHistory history : historyList){
				WorkflowsFactory.deleteWorkflowHistory(history);
			}
			/*delete the task*/
			WorkflowsFactory.deleteWorkflowTask(task);
			HibernateUtil.commitTransaction();
			SessionMessages.add(request.getSession(false), "message", "message.workflow.deleted");
		}catch(Exception e){
			Logger.debug(this, e.getMessage());
			HibernateUtil.rollbackTransaction();
		}
	}

}
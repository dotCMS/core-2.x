package com.dotmarketing.portlets.workflows.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.workflows.factories.WorkflowsFactory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.util.WebKeys.WorkflowStatuses;
import com.liferay.portal.model.User;
import com.liferay.portlet.RenderRequestImpl;

/**
 * 
 * @author David Torres
 * @version $Revision: 1.0 $ $Revision: 1.5 $
 * 
 */
public class ViewWorkflowTasksAction extends DotPortletAction {

	/*
	 * @see com.liferay.portal.struts.PortletAction#render(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm, javax.portlet.PortletConfig,
	 *      javax.portlet.RenderRequest, javax.portlet.RenderResponse)
	 */
	public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
			RenderResponse res) throws Exception {

		Logger.debug(this, "Running ViewWorkflowTasksAction!!!!=" + req.getWindowState());

		try {
			// gets the user
			User user = _getUser(req);

			_viewFilteredWorkflowTasks(req, user);
			return mapping.findForward("portlet.ext.workflows.view_workflow_tasks");

		} catch (Exception e) {
			req.setAttribute(PageContext.EXCEPTION, e);
			return mapping.findForward(com.liferay.portal.util.Constants.COMMON_ERROR);
		}
	}



	/**
	 * Gets the workflow taks related to the user roles
	 * @param req The request
	 * @param user The user
	 * @author David Torres
	 * @author Oswaldo Gallango
	 * @version 1.5
	 * @throws DotDataException 
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	private void _viewNobodyWorkflowTasks(RenderRequest req, User user) throws DotDataException {
		List<WorkflowTask> tasks = (List<WorkflowTask>)WorkflowsFactory.getWorkflowTasksOpen(user);
		List<WorkflowTask> myTasks = (List<WorkflowTask> )req.getAttribute(WebKeys.WORKFLOW_USER_TASKS_LIST);
		myTasks.removeAll(tasks);
		tasks.addAll(myTasks);
		Collections.sort(tasks, new ComparateWorkflowTask(-1));
		req.setAttribute(WebKeys.WORKFLOW_USER_TASKS_LIST, tasks);
	}

	/**
	 * Gets the user workflow tasks according to the filter options selected
	 * @param req The request
	 * @param user The user
	 * @author David Torres
	 * @author Oswaldo Gallango
	 * @version 1.5
	 * @throws DotDataException 
	 * @since 1.2
	 */
	private void _viewFilteredWorkflowTasks(RenderRequest req, User user) throws DotDataException {
		HttpServletRequest request = ((RenderRequestImpl) req).getHttpServletRequest();
		HttpSession session = request.getSession();
		
		
		
		

		int page = 1;
		String title = "";
		String description = "";
		List<String> status = new ArrayList<String>();
		String associated_type = "";

		String assignedTo = "";

		String orderBy = "mod_date desc";


		if (request.getParameter("resetFilters") != null) {
			status.add(String.valueOf(WorkflowStatuses.OPEN));
			request.getSession().setAttribute("workflow_status", status.toArray());
			request.setAttribute("workflow_status", status.toArray(new String[1]));
		}else{
			page = UtilMethods.isSet(request.getParameter("page")) ? Integer.parseInt(request.getParameter("page")): session.getAttribute("workflow_task_page")!=null ? (Integer) session.getAttribute("workflow_task_page") : 1;

			title = req.getParameter("title") != null ? req.getParameter("title") : UtilMethods.isSet((String) session.getAttribute("workflow_task_title")) ? (String) session.getAttribute("workflow_task_title") : "";

			assignedTo = request.getParameter("assignedTo") != null 
					? request.getParameter("assignedTo")
					: UtilMethods.isSet((String) session.getAttribute("workflow_task_assignedTo")) 
						? (String) session.getAttribute("workflow_task_assignedTo") : "";

			orderBy = request.getParameter("order_by") != null ? request.getParameter("order_by") : UtilMethods.isSet((String) session.getAttribute("workflow_task_orderBy")) ? (String) session.getAttribute("workflow_task_orderBy") : "mod_date desc";

			

			/*** set the user we want to show ***/
			if(!UtilMethods.isSet(assignedTo)){
				
				
			}
			

			/*** set the statuses we want to show ***/
			if(request.getParameter("status") != null){
				String[] s = request.getParameterValues("status");
				for(int i=0;i<s.length;i++){
					status.add(s[i] );
				}
				request.getSession().setAttribute("workflow_status", s);
			}
			else if(request.getSession().getAttribute("workflow_status") != null){
				try{
					String[] s = (String[]) request.getSession().getAttribute("workflow_status");
					
					for(int i=0;i<s.length;i++){
						status.add(s[i] );
					}
				}
				catch (Exception e) {
					Logger.warn(this, "unable to parse workflow status[]es from session");
				}
			}
			if(status.size() ==0){
				status.add(String.valueOf(WorkflowStatuses.OPEN));
			}
			request.setAttribute("workflow_status",status.toArray(new String[status.size()]));
			
			associated_type = request.getParameter("associated_type") != null ? request.getParameter("associated_type") : UtilMethods.isSet((String) session.getAttribute("workflow_task_associated_type")) ? (String) session.getAttribute("workflow_task_associated_type") : "";

		}

		request.setAttribute("page", page);
		session.setAttribute("workflow_task_page", page);

		request.setAttribute("title", title);
		session.setAttribute("workflow_task_title", title);
		
		request.setAttribute("status", status);
		session.setAttribute("workflow_task_status", status);

		request.setAttribute("description", description);
		session.setAttribute("workflow_task_description", description);
		
		request.setAttribute("assignedTo", assignedTo);
		session.setAttribute("workflow_task_assignedTo", assignedTo);
		
	
		//
		boolean includeReporter =(UtilMethods.isSet(request.getParameter("includeReporter")));
		if(includeReporter)
			request.setAttribute("includeReporter", includeReporter);

		
		request.setAttribute("orderBy", orderBy);
		session.setAttribute("workflow_task_orderBy", orderBy);

		request.setAttribute("associated_type", associated_type);
		session.setAttribute("workflow_task_associated_type", associated_type);

		int perPage = Config.getIntProperty("PER_PAGE");
		int offset = perPage * (page - 1);
		List<HashMap<String,Object>> tasks = new ArrayList<HashMap<String,Object>>();
		tasks = WorkflowsFactory.filterWorkflowTasks(user, title, status.toArray(new String[status.size()]), assignedTo, orderBy, offset, perPage,includeReporter);
		int count = WorkflowsFactory.countWorkFlowTasks(user, title, status.toArray(new String[status.size()]), assignedTo,includeReporter);


		
		req.setAttribute(WebKeys.WORKFLOW_FILTER_TASKS_LIST, tasks);
		req.setAttribute(WebKeys.WORKFLOW_FILTER_TASKS_COUNT, count);
	}
	
	/**
	 * Compare two WorkFlowTask for reordering
	 * @author Oswaldo Gallango
	 *
	 */
	@SuppressWarnings("unchecked")
	public class ComparateWorkflowTask implements Comparator{
		/**
		 * Evaluate the comparation of the two object
		 */
		private int direction = 1;
		public ComparateWorkflowTask(){
			
		}
		
		public ComparateWorkflowTask(int direction){
			this.direction = direction;
		}
		
		public int compare( Object object1, Object object2 ) throws ClassCastException {
			final int BEFORE = -1;
			final int EQUAL = 0;
			final int AFTER = 1;

			WorkflowTask object11 = (WorkflowTask)object1;
			WorkflowTask object22 = (WorkflowTask)object2;

			if ( object11 == object22 ) return EQUAL;

			if (object11.getModDate().before(object22.getModDate())) return BEFORE*direction;
			if (object11.getModDate().after(object22.getModDate())) return AFTER*direction;

			//if (object11.getInode() < object22.getInode()) return BEFORE*direction;
			//if (object11.getInode() > object22.getInode()) return AFTER*direction;
			
			if (object11.getCreationDate().before(object22.getCreationDate())) return BEFORE*direction;
			if (object11.getCreationDate().after(object22.getCreationDate())) return AFTER*direction;

			assert this.equals(object22) : "compareTo inconsistent with equals.";

			return EQUAL;
		}

	}
}
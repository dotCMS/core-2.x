package com.dotmarketing.portlets.workflows.business;

import java.util.List;

import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.workflows.model.WorkflowComment;
import com.dotmarketing.portlets.workflows.model.WorkflowHistory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.liferay.portal.model.User;

public interface WorkflowAPI {

	/**
	 * Finds workflows of given status assigned to user
	 * @param user
	 * @param status
	 * @return
	 * @throws DotDataException
	 */
	public java.util.List<WorkflowTask> findByAssignee(User user, String[] status) throws DotDataException;
	
	/**
	 * Finds workflows of given status created by user
	 * @param user
	 * @param status
	 * @return
	 * @throws DotDataException
	 */
	public java.util.List<WorkflowTask> findByAuthor(User user, String[] status) throws DotDataException;
	
	
	/**
	 * finds workflows of a give status by contentlet
	 * @param contentlet
	 * @param status
	 * @return
	 * @throws DotDataException
	 */
	public java.util.List<WorkflowTask> findByContentlet(Contentlet contentlet, String[] status) throws DotDataException;


	/**
	 * finds workflows of a give status by htmlPage
	 * @param contentlet
	 * @param status
	 * @return
	 * @throws DotDataException
	 */
	public java.util.List<WorkflowTask> findByHTMPage(HTMLPage page, String[] status) throws DotDataException;
	
	/**
	 * Finds a workflow by id
	 * @param id
	 * @return
	 * @throws DotDataException
	 */
	public WorkflowTask findById(String id) throws DotDataException;

	/**
	 * Finds comments on a workflow item
	 * @param task
	 * @return
	 * @throws DotDataException
	 */
	public List<WorkflowComment> fingCommentsByTask(WorkflowTask task) throws DotDataException;

	/**
	 * Saves comments on a workflow item
	 * @param comment
	 * @throws DotDataException
	 */
	public  void saveComment(WorkflowComment comment) throws DotDataException;

	/**
	 * deletes a specific comment on a workflow item
	 * @param comment
	 * @throws DotDataException
	 */
	public  void deleteComment(WorkflowComment comment) throws DotDataException;

	/**
	 * gets history of a particular workflow item
	 * @param task
	 * @return
	 * @throws DotDataException
	 */
	public  List<WorkflowHistory> findWorkflowHistory(WorkflowTask task) throws DotDataException;

	/**
	 * Saves a new history item for a workflow
	 * @param history
	 * @throws DotDataException
	 */
	public  void saveWorkflowHistory(WorkflowHistory history) throws DotDataException;

	/**
	 * deletes a history item from a workflow
	 * @param history
	 * @throws DotDataException
	 */
	public  void deleteWorkflowHistory(WorkflowHistory history) throws DotDataException;

	/**
	 * finds files associated with a workflow item
	 * @param task
	 * @return
	 * @throws DotDataException
	 */
	public  List<File> findWorkflowTaskFiles(WorkflowTask task) throws DotDataException;


	
	
	
	
}

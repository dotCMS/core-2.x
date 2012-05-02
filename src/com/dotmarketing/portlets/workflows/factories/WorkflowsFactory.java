package com.dotmarketing.portlets.workflows.factories;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.Layout;
import com.dotmarketing.business.NoSuchUserException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Role;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.factories.EmailFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.workflows.model.WorkflowComment;
import com.dotmarketing.portlets.workflows.model.WorkflowHistory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.dotmarketing.util.WebKeys.WorkflowStatuses;
import com.liferay.portal.model.User;

/**
 * 
 * @author David
 */
public class WorkflowsFactory {

	// Workflow tasks methods
	public static WorkflowTask getWorkflowTaskByInode(String inode) {
		return (WorkflowTask) InodeFactory.getInode(inode, WorkflowTask.class);
	}

	/**
	 * Gets the workflows task that belongs to the user roles, if the user is
	 * CMS ADMINISTRATOR will return all the wokflow task
	 * 
	 * @param user
	 *            The User
	 * @return List<WorkflowTask>
	 * @author David Torres
	 * @author Oswaldo Gallango
	 * @version 1.5
	 * @throws DotDataException
	 * @since 1.0
	 */
	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getWorkflowTasksOpen(User user) throws DotDataException {
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		boolean isCMSADministrator = false;
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}

			if (Config.getStringProperty("CMS_ADMINISTRATOR_ROLE").equals(role.getName())) {
				isCMSADministrator = true;
				break;
			}

			rolesString += "'" + role.getId() + "'";
		}

		if (isCMSADministrator) {
			return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, "status = '" + WorkflowStatuses.OPEN
					+ "' ", "mod_date");
		} else {
			if (rolesString.equals(""))
				return tasks;
			rolesString = "(" + rolesString + ")";
			return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, "belongs_to in " + rolesString
					+ " and status = '" + WorkflowStatuses.OPEN + "' ", "mod_date");
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getOpenWorkflows(User user) throws DotDataException {
		if(user ==null){
			throw new DotDataException("Cannot get workflow for null user");
		}
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		StringBuilder rolesString = new StringBuilder();
		for (Role role : userRoles) {
			if (rolesString.length() > 0) {
				rolesString.append(",");
			}
			rolesString.append("'" + role.getId() + "'");
		}


		if (rolesString.equals("")){
			return tasks;
		}
		return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, "(belongs_to in (" + rolesString
				+ ") or assigned_to ='" + user.getUserId()+"') and status = '" + WorkflowStatuses.OPEN + "' ", "due_date");
		
	}
	
	
	

	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getWorkflowTasksOpenWithAssignee(User user) throws DotDataException {
		List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}
			rolesString += role.getId();
		}
		if (rolesString.equals(""))
			return tasks;
		rolesString = "(" + rolesString + ")";
		return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, "belongs_to in " + rolesString
				+ " and status = " + WorkflowStatuses.OPEN.ordinal()
				+ " and (assigned_to is not null and assigned_to <> '')", "mod_date");
	}

	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getWorkflowTasksBelongsToRole(int roleId) {
		return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, "belongs_to = " + roleId, "mod_date");
	}

	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getWorkflowTasksAssociatedWithAsset(Inode asset, User user)
			throws DotDataException {
		StringBuffer condition = new StringBuffer();
		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}
			rolesString += role.getId();
		}
		condition.append("(created_by = '" + user.getUserId() + "' or assigned_to = '" + user.getUserId()
				+ "' or belongs_to in (" + rolesString + ")) and ");
		condition.append(" webasset = " + asset.getInode());

		return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, condition.toString(), "mod_date");
	}

	@SuppressWarnings("unchecked")
	public static List<WorkflowTask> getOpenedWorkflowTasksAssociatedWithAsset(Inode asset, User user)
			throws DotDataException {
		if(user == null){
			return new ArrayList<WorkflowTask>();
		}
		StringBuffer condition = new StringBuffer();
		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}
			rolesString += "'" + role.getId() + "'";
		}
		condition.append("status = '" + WebKeys.WorkflowStatuses.OPEN.toString() + "' and " + " (created_by = '"
				+ user.getUserId() + "' or assigned_to = '" + user.getUserId() + "' or belongs_to in (" + rolesString
				+ ")) and ");
		condition.append(" webasset = '" + asset.getInode() + "'");

		return InodeFactory.getInodesOfClassByCondition(WorkflowTask.class, condition.toString(), "mod_date");
	}

	
	private static String getWorkflowSqlQuery(User user, String title, String[] status,String assignedTo, String orderBy, boolean includeReporter) throws DotDataException {
		
		boolean isAdministrator = APILocator.getRoleAPI().doesUserHaveRole(user, APILocator.getRoleAPI().loadCMSAdminRole());
		
		StringBuffer condition = new StringBuffer();
		if (UtilMethods.isSet(title)) {
			condition.append(" (lower(workflow_task.title) like '%" + title.trim().toLowerCase() + "%' or ");
			condition.append(" lower(workflow_task.description) like '%" + title.trim().toLowerCase() + "%' ) and ");
		}

		if (UtilMethods.isSet(assignedTo) && assignedTo.length() > 5 && assignedTo.startsWith("role-")) {
			condition.append("workflow_task.belongs_to = '" + assignedTo.substring(5, assignedTo.length()) + "' and ");
		} else if (UtilMethods.isSet(assignedTo) && assignedTo.length() > 5 && assignedTo.startsWith("user-")) {
			condition.append("workflow_task.assigned_to = '" + assignedTo.substring(5, assignedTo.length()) + "' and ");
		}

		if (status != null && status.length > 0) {
			condition.append(" ( 0=1  ");
			for (int i = 0; i < status.length; i++) {
				condition.append(" or status = '" + status[i] + "'");
			}
			condition.append(" ) and ");
		}

		List<Role> userRoles = APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}
			rolesString += "'" + role.getId() + "'";
		}
		condition.append(" ( ");
		if(includeReporter){
			condition.append(" workflow_task.created_by = '" + user.getUserId() + "' or ");
		}
		condition.append(" workflow_task.assigned_to = '"
				+ user.getUserId() + "' or workflow_task.belongs_to in (" + rolesString + ") " + (UtilMethods.isSet(assignedTo) && isAdministrator?" or 1=1) ":") "));

		StringBuffer query = new StringBuffer("select * from ");
		query.append(" workflow_task  ");
		query.append("WHERE " + condition.toString());
		if(UtilMethods.isSet(orderBy)){
			query.append(" order by ");
			query.append(orderBy);
		}
		return query.toString();
		
	}
	
	/**
	 * Get the list of workflow tasks filter by conditions
	 * 
	 * @param user
	 * @param title
	 * @param description
	 * @param status
	 * @param createdBy
	 * @param assignedTo
	 * @param createdFrom
	 * @param createdTo
	 * @param modifiedFrom
	 * @param modifiedTo
	 * @param offset
	 * @param maxResults
	 * @param orderBy
	 * @param associated_type
	 * @return List<WorkflowTask>
	 * @author David Torres
	 * @author Oswaldo Gallango
	 * @author Roger Marin
	 * @version 1.5
	 * @throws DotDataException
	 * @since 1.0
	 */

	
	
	
	
	@SuppressWarnings("unchecked")
	public static List<HashMap<String, Object>> filterWorkflowTasks(User user, String title, String[] status,
			String assignedTo, String orderBy, int startRow, int limit, boolean includeReporter) throws DotDataException {
		PermissionAPI pAPI = APILocator.getPermissionAPI();


		String query = getWorkflowSqlQuery(user, title, status,assignedTo,  orderBy, includeReporter);

		DotConnect dc = new DotConnect();
		dc.setSQL(query);
		dc.setMaxRows(limit);
		dc.setStartRow(startRow);

		return dc.loadResults();


	}

	
	@SuppressWarnings("unchecked")
	public static int countWorkFlowTasks(User user, String title, String[] status,
			String assignedTo, boolean includeReporter) throws DotDataException {
		PermissionAPI pAPI = APILocator.getPermissionAPI();


		String query = getWorkflowSqlQuery(user, title, status,assignedTo,  null, includeReporter);
		query = query.replaceAll("select \\*", "select count(*) as mycount");
		
		DotConnect dc = new DotConnect();

		dc.setSQL(query);
		dc.loadResult();
		return dc.getInt("mycount");



	}
	
	
	@SuppressWarnings("unchecked")
	public static List<HashMap<String, Object>> filterWorkflowTasks(User user, String title, String description,
			String status, String createdBy, String assignedTo, Date createdFrom, Date createdTo, Date modifiedFrom,
			Date modifiedTo, int offset, int maxResults, String orderBy, String associated_type)
			throws DotDataException {
		boolean isCMSADministrator = false;

		StringBuffer condition = new StringBuffer();
		if (UtilMethods.isSet(title)) {
			condition.append("workflow_task.title like '%" + title + "%' and ");
		}
		if (UtilMethods.isSet(status)) {
			condition.append("workflow_task.status = '" + status + "' and ");
		}
		if (UtilMethods.isSet(description)) {
			condition.append("workflow_task.description like '%" + description + "%' and ");
		}

		if (UtilMethods.isSet(createdFrom)) {
			condition.append("workflow_task.creation_date >= '" + UtilMethods.dateToJDBC(createdFrom) + "' and ");
		}
		if (UtilMethods.isSet(createdTo)) {
			condition.append("workflow_task.creation_date <= '" + UtilMethods.dateToJDBC(createdTo) + "' and ");
		}
		if (UtilMethods.isSet(modifiedFrom)) {
			condition.append("workflow_task.mod_date >= '" + UtilMethods.dateToJDBC(modifiedFrom) + "' and ");
		}
		if (UtilMethods.isSet(modifiedTo)) {
			condition.append("workflow_task.mod_date <= '" + UtilMethods.dateToJDBC(modifiedTo) + "' and ");
		}

		if (UtilMethods.isSet(createdBy) && createdBy.length() > 5) {
			condition.append("workflow_task.created_by = '" + createdBy.substring(5, createdBy.length()) + "' and ");
		}
		if (UtilMethods.isSet(assignedTo) && assignedTo.length() > 5 && assignedTo.startsWith("role-")) {
			condition.append("workflow_task.belongs_to = '" + assignedTo.substring(5, assignedTo.length()) + "' and ");
		} else if (UtilMethods.isSet(assignedTo) && assignedTo.length() > 5 && assignedTo.startsWith("user-")) {
			condition.append("workflow_task.assigned_to = '" + assignedTo.substring(5, assignedTo.length()) + "' and ");
		}

		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}

			if (Config.getStringProperty("CMS_ADMINISTRATOR_ROLE").equals(role.getName())) {
				isCMSADministrator = true;
			}
			rolesString += "'" + role.getId() + "'";
		}

		String conditionString = "";
		if (isCMSADministrator) {
			conditionString = condition.toString();
			if (!UtilMethods.isSet(conditionString)) {
				conditionString = "1=1";
			} else if (conditionString.endsWith("and ")) {
				conditionString = conditionString.substring(0, conditionString.length() - 5);
			}
		} else {
			condition.append("(workflow_task.created_by = '" + user.getUserId() + "' or workflow_task.assigned_to = '"
					+ user.getUserId() + "' or workflow_task.belongs_to in (" + rolesString + ")) ");
			conditionString = condition.toString();
		}

		StringBuffer query = new StringBuffer("");
		String dbType = DbConnectionFactory.getDBType();
		String type = "";
		if (DbConnectionFactory.MYSQL.equals(dbType)) {
			type = "CONCAT('Content - ',structure.name)";
		} else if (DbConnectionFactory.POSTGRESQL.equals(dbType) || DbConnectionFactory.ORACLE.equals(dbType)) {
			type = "'Content - '||structure.name";
		} else if (DbConnectionFactory.MSSQL.equals(dbType)) {
			type = "'Content - '+structure.name";
		}

		if (UtilMethods.isSet(associated_type)) {

			if (associated_type.equals("0")) {

				query.append(getCustomSQL("''", dbType));
				query
						.append("FROM workflow_task WHERE (workflow_task.webasset = '0' or workflow_task.webasset is null) and "
								+ conditionString);

			} else if (associated_type.equals("htmlpage")) {

				query.append(getCustomSQL("'html page'", dbType));
				query.append("FROM workflow_task JOIN htmlpage ON workflow_task.webasset = htmlpage.inode ");
				query.append("WHERE " + conditionString);

			} else if (associated_type.equals("Other")) {

				query.append(getCustomSQL("'Other'", dbType));
				query.append("FROM workflow_task JOIN inode ON workflow_task.webasset = inode.inode ");
				query.append("WHERE inode.type <> 'htmlpage' and inode.type <>'contentlet' and " + conditionString);

			} else {

				query.append(getCustomSQL(type, dbType));
				query
						.append("FROM workflow_task JOIN contentlet ON workflow_task.webasset = contentlet.inode JOIN structure ON contentlet.structure_inode = structure.inode ");
				query.append("WHERE structure.inode= '" + associated_type + "' and " + conditionString);
			}

		} else {

			query.append("(" + getCustomSQL(type, dbType));
			query
					.append("FROM workflow_task JOIN contentlet ON workflow_task.webasset = contentlet.inode JOIN structure ON contentlet.structure_inode = structure.inode ");
			query.append("WHERE " + conditionString);
			query.append("	) union ( ");

			query.append(getCustomSQL("'html page'", dbType));
			query.append("FROM workflow_task JOIN htmlpage ON workflow_task.webasset = htmlpage.inode ");
			query.append("WHERE " + conditionString);
			query.append("	) union ( ");

			query.append(getCustomSQL("'Other'", dbType));
			query.append("FROM workflow_task JOIN inode ON workflow_task.webasset = inode.inode ");
			query.append("WHERE inode.type <> 'htmlpage' and inode.type <>'contentlet' and " + conditionString);
			query.append("	) union ( ");

			query.append(getCustomSQL("''", dbType));
			query
					.append("FROM workflow_task WHERE (workflow_task.webasset = '0' or workflow_task.webasset is null or workflow_task.webasset = '') and "
							+ conditionString);
			query.append("	) ");
		}

		query.append(" order by " + orderBy);

		List<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();
		DotConnect dc = new DotConnect();

		dc.setSQL(query.toString());
		if (maxResults != 0) {
			dc.setStartRow(offset);
			dc.setMaxRows(maxResults);
		}
		results = dc.getResults();

		return results;
	}

	/**
	 * Get the amount of workflows tasks filter by conditions
	 * 
	 * @param user
	 * @param title
	 * @param description
	 * @param status
	 * @param createdBy
	 * @param assignedTo
	 * @param createdFrom
	 * @param createdTo
	 * @param modifiedFrom
	 * @param modifiedTo
	 * @param offset
	 * @param maxResults
	 * @param orderBy
	 * @param associated_type
	 * @return List<WorkflowTask>
	 * @author David Torres
	 * @author Oswaldo Gallango
	 * @version 1.5
	 * @throws DotDataException
	 * @since 1.0
	 */
	public static int filterWorkflowTasksCount(User user, String title, String description, String status,
			String createdBy, String assignedTo, Date createdFrom, Date createdTo, Date modifiedFrom, Date modifiedTo,
			String associated_type) throws DotDataException {
		StringBuffer condition = new StringBuffer();
		boolean isCMSADministrator = false;

		if (UtilMethods.isSet(title)) {
			condition.append("workflow_task.title = '" + title + "' and ");
		}
		if (UtilMethods.isSet(status)) {
			condition.append("workflow_task.status = '" + status + "' and ");
		}
		if (UtilMethods.isSet(description)) {
			condition.append("workflow_task.description = '" + description + "' and ");
		}

		if (UtilMethods.isSet(createdFrom)) {
			condition.append("workflow_task.creation_date >= '" + UtilMethods.dateToJDBC(createdFrom) + "' and ");
		}
		if (UtilMethods.isSet(createdTo)) {
			condition.append("workflow_task.creation_date <= '" + UtilMethods.dateToJDBC(createdTo) + "' and ");
		}
		if (UtilMethods.isSet(modifiedFrom)) {
			condition.append("workflow_task.mod_date >= '" + UtilMethods.dateToJDBC(modifiedFrom) + "' and ");
		}
		if (UtilMethods.isSet(modifiedTo)) {
			condition.append("workflow_task.mod_date <= '" + UtilMethods.dateToJDBC(modifiedTo) + "' and ");
		}

		if (UtilMethods.isSet(createdBy)) {
			condition.append("workflow_task.created_by = '" + createdBy + "' and ");
		}
		if (UtilMethods.isSet(assignedTo)) {
			condition.append("workflow_task.assigned_to = '" + assignedTo + "' and ");
		}

		List<Role> userRoles = com.dotmarketing.business.APILocator.getRoleAPI().loadRolesForUser(user.getUserId());
		String rolesString = "";
		for (Role role : userRoles) {
			if (!rolesString.equals("")) {
				rolesString += ",";
			}

			if (Config.getStringProperty("CMS_ADMINISTRATOR_ROLE").equals(role.getName())) {
				isCMSADministrator = true;
			}

			rolesString += "'" + role.getId() + "'";
		}

		String conditionString = "";
		if (isCMSADministrator) {
			conditionString = condition.toString();
			if (!UtilMethods.isSet(conditionString)) {
				conditionString = "1=1";
			} else if (conditionString.endsWith("and ")) {
				conditionString = conditionString.substring(0, conditionString.length() - 5);
			}

		} else {
			condition.append("(workflow_task.created_by = '" + user.getUserId() + "' or workflow_task.assigned_to = '"
					+ user.getUserId() + "' or workflow_task.belongs_to in (" + rolesString + ")) ");
			conditionString = condition.toString();
		}

		DotConnect dc = new DotConnect();
		StringBuffer query = new StringBuffer();
		if (UtilMethods.isSet(associated_type)) {
			if (associated_type.equals("0")) {
				query.append("select count(*) as count ");
				query
						.append("FROM workflow_task where (workflow_task.webasset = '0' or workflow_task.webasset is null) and "
								+ conditionString);
			} else if (associated_type.equals("htmlpage")) {
				query.append("select count(*) as count ");
				query.append("FROM workflow_task JOIN htmlpage ON workflow_task.webasset = htmlpage.inode ");
				query.append("WHERE " + conditionString);
			} else if (associated_type.equals("Other")) {
				query.append("select count(*) as count ");
				query.append("FROM workflow_task JOIN inode ON workflow_task.webasset = inode.inode ");
				query.append("WHERE inode.type <> 'htmlpage' and inode.type <>'contentlet' and " + conditionString);
			} else {
				query.append("select count(*) as count ");
				query
						.append("FROM workflow_task JOIN contentlet ON workflow_task.webasset = contentlet.inode JOIN structure ON contentlet.structure_inode = structure.inode ");
				query.append("WHERE structure.inode= " + associated_type + " and " + conditionString);
			}

			dc.setSQL(query.toString());
		} else {
			dc.setSQL("select count(*) as count from workflow_task where " + conditionString);
		}

		return dc.getInt("count");

	}

	public static void saveWorkflowTask(WorkflowTask task) {
		InodeFactory.saveInode(task);
	}

	public static void deleteWorkflowTask(WorkflowTask task) {
		InodeFactory.deleteInode(task);
	}

	public static void sendWorkflowChangeEmails(WorkflowTask task, String subject, String change, User assignUser) {
		try {

			if (Config.getBooleanProperty("WORKFLOWS_EMAILS_ENABLED")) {
				boolean hasRecipients = false;
				User createdByUser = null;
				try {
					createdByUser = APILocator.getUserAPI().loadUserById(task.getCreatedBy(),
							APILocator.getUserAPI().getSystemUser(), false);
				} catch (NoSuchUserException e) {
				}
				User assignedToUser = null;
				try {
					assignedToUser = APILocator.getUserAPI().loadUserById(task.getAssignedTo(),
							APILocator.getUserAPI().getSystemUser(), false);
				} catch (NoSuchUserException e) {
				}
				String fromEmail = PublicCompanyFactory.getDefaultCompany().getEmailAddress();
				String toEmail = "";
				String ccEmail = "";

				if (assignedToUser != null && createdByUser != null) {
					if (assignedToUser != null) {
						toEmail = assignedToUser.getEmailAddress();
					} else if (UtilMethods.isSet(task.getBelongsTo())) {
						Role role = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
						String to = "";
						if (InodeUtils.isSet(role.getId())) {
							List<User> users = com.dotmarketing.business.APILocator.getRoleAPI().findUsersForRole(
									role.getId());
							if (users != null && users.size() > 0) {
								hasRecipients = true;
								for (User user : users) {
									if (UtilMethods.isSet(user.getEmailAddress())) {
										if(user.getEmailAddress()!=null){
											to += user.getEmailAddress() + ";";
										}
									}
								}
							}
						}
						toEmail = to;
					} else {
						toEmail = createdByUser.getEmailAddress();
					}
					if (assignedToUser != null && createdByUser != null) {
						ccEmail = createdByUser.getEmailAddress();
					}
					hasRecipients = true;
				} else if (UtilMethods.isSet(task.getBelongsTo())) {
					Role role = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
					String to = "";
					if (InodeUtils.isSet(role.getId())) {
						List<User> users = com.dotmarketing.business.APILocator.getRoleAPI().findUsersForRole(
								role.getId());
						if (users.size() > 0) {
							hasRecipients = true;
							for (User user : users) {
								if (UtilMethods.isSet(user.getEmailAddress())) {
									to += user.getEmailAddress() + ";";
								}
							}
						}
					}
					toEmail = to;
				}
				String layoutId="";
				
				if (assignedToUser != null) {
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForUser(assignedToUser);
					if(userLayouts != null && userLayouts.size() > 0){
						layoutId = userLayouts.get(0).getId();
						}			
				}
				else if (UtilMethods.isSet(task.getBelongsTo())) {
					Role layoutRole = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForRole(layoutRole);
					if(userLayouts != null && userLayouts.size() > 0){
						layoutId = userLayouts.get(0).getId();
						}
				}
				else if(createdByUser != null){
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForUser(createdByUser);
					if(userLayouts != null && userLayouts.size() > 0){
					layoutId = userLayouts.get(0).getId();
					}					
				}
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("from", fromEmail);
				parameters.put("to", toEmail);
				parameters.put("cc", ccEmail);
				parameters.put("subject", subject);
				parameters.put("task", task);
				parameters.put("taskChange", change);

				Host host = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);	
				String link = "http://" + host.getHostname() + Config.getStringProperty("WORKFLOWS_URL") + "&_EXT_21_inode="
						+ String.valueOf(task.getInode());
				if(UtilMethods.isSet(layoutId)){
					link = link +"&p_l_id="+ layoutId;	
				}
				parameters.put("taskLink", link);
				String name = createdByUser == null ? task.getCreatedBy() : createdByUser.getFullName();

				if (assignUser != null) {
					parameters.put("taskAuthor", assignUser.getFullName());
				} else {
					parameters.put("taskAuthor", createdByUser == null ? task.getCreatedBy() : createdByUser
							.getFullName());
				}
				parameters.put("taskAssignedTo", assignedToUser != null ? assignedToUser.getFullName() : "");
				parameters.put("emailTemplate", Config.getStringProperty("WORKFLOW_CHANGE_EMAIL_TEMPLATE"));

				if (hasRecipients)
					EmailFactory.sendParameterizedEmail(parameters, null, host, null);
			}
		} catch (Exception e) {
			Logger.error(WorkflowsFactory.class, "Exception ocurred trying to deliver emails for task "
					+ task.getInode() + " change", e);
		}
	}
	
	public static void sendWorkflowChangeEmails(WorkflowTask task, String subject, String change, User assignUser, HttpServletRequest request, HttpServletResponse response) {
		try {

			if (Config.getBooleanProperty("WORKFLOWS_EMAILS_ENABLED")) {
				boolean hasRecipients = false;
				User createdByUser = null;
				try {
					createdByUser = APILocator.getUserAPI().loadUserById(task.getCreatedBy(),
							APILocator.getUserAPI().getSystemUser(), false);
				} catch (NoSuchUserException e) {
				}
				User assignedToUser = null;
				try {
					assignedToUser = APILocator.getUserAPI().loadUserById(task.getAssignedTo(),
							APILocator.getUserAPI().getSystemUser(), false);
				} catch (NoSuchUserException e) {
				}
				String fromEmail = PublicCompanyFactory.getDefaultCompany().getEmailAddress();
				String toEmail = "";
				String ccEmail = "";

				if (assignedToUser != null && createdByUser != null) {
					if (assignedToUser != null) {
						toEmail = assignedToUser.getEmailAddress();
					} else if (UtilMethods.isSet(task.getBelongsTo())) {
						Role role = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
						String to = "";
						if (InodeUtils.isSet(role.getId())) {
							List<User> users = com.dotmarketing.business.APILocator.getRoleAPI().findUsersForRole(
									role.getId());
							if (users != null && users.size() > 0) {
								hasRecipients = true;
								for (User user : users) {
									if (UtilMethods.isSet(user.getEmailAddress())) {
										if(user.getEmailAddress()!=null){
											to += user.getEmailAddress() + ";";
										}
									}
								}
							}
						}
						toEmail = to;
					} else {
						toEmail = createdByUser.getEmailAddress();
					}
					if (assignedToUser != null && createdByUser != null) {
						ccEmail = createdByUser.getEmailAddress();
					}
					hasRecipients = true;
				} else if (UtilMethods.isSet(task.getBelongsTo())) {
					Role role = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
					String to = "";
					if (InodeUtils.isSet(role.getId())) {
						List<User> users = com.dotmarketing.business.APILocator.getRoleAPI().findUsersForRole(
								role.getId());
						if (users.size() > 0) {
							hasRecipients = true;
							for (User user : users) {
								if (UtilMethods.isSet(user.getEmailAddress())) {
									to += user.getEmailAddress() + ";";
								}
							}
						}
					}
					toEmail = to;
				}
				String layoutId="";
				
				if (assignedToUser != null) {
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForUser(assignedToUser);
					if(userLayouts != null && userLayouts.size() > 0){
						layoutId = userLayouts.get(0).getId();
						}			
				}
				else if (UtilMethods.isSet(task.getBelongsTo())) {
					Role layoutRole = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo());
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForRole(layoutRole);
					if(userLayouts != null && userLayouts.size() > 0){
						layoutId = userLayouts.get(0).getId();
						}
				}
				else if(createdByUser != null){
					List<Layout> userLayouts = APILocator.getLayoutAPI().loadLayoutsForUser(createdByUser);
					if(userLayouts != null && userLayouts.size() > 0){
					layoutId = userLayouts.get(0).getId();
					}					
				}
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("from", fromEmail);
				parameters.put("to", toEmail);
				parameters.put("cc", ccEmail);
				parameters.put("subject", subject);
				parameters.put("task", task);
				parameters.put("taskChange", change);
				parameters.put("request", request);
				parameters.put("response", response);

				Host host = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);	
				String link = "http://" + host.getHostname() + Config.getStringProperty("WORKFLOWS_URL") + "&_EXT_21_inode="
						+ String.valueOf(task.getInode());
				if(UtilMethods.isSet(layoutId)){
					link = link +"&p_l_id="+ layoutId;	
				}
				parameters.put("taskLink", link);
				String name = createdByUser == null ? task.getCreatedBy() : createdByUser.getFullName();

				if (assignUser != null) {
					parameters.put("taskAuthor", assignUser.getFullName());
				} else {
					parameters.put("taskAuthor", createdByUser == null ? task.getCreatedBy() : createdByUser
							.getFullName());
				}
				parameters.put("taskAssignedTo", assignedToUser != null ? assignedToUser.getFullName() : "");
				parameters.put("emailTemplate", Config.getStringProperty("WORKFLOW_CHANGE_EMAIL_TEMPLATE"));

				if (hasRecipients)
					EmailFactory.sendParameterizedEmail(parameters, null, host, null);
			}
		} catch (Exception e) {
			Logger.error(WorkflowsFactory.class, "Exception ocurred trying to deliver emails for task "
					+ task.getInode() + " change", e);
		}
	}

	// Workflow comments methods
	@SuppressWarnings("unchecked")
	public static List<WorkflowComment> getWorkflowCommentsOfTask(WorkflowTask task) {
		return InodeFactory.getChildrenClass(task, WorkflowComment.class, "creation_date");
	}

	public static void saveWorkflowComment(WorkflowComment comment) {
		InodeFactory.saveInode(comment);
	}

	public static void deleteWorkflowComment(WorkflowComment comment) {
		InodeFactory.deleteInode(comment);
	}

	// Workflow history methods
	@SuppressWarnings("unchecked")
	public static List<WorkflowHistory> getWorkflowHistoryOfTask(WorkflowTask task) {
		return InodeFactory.getChildrenClass(task, WorkflowHistory.class, "creation_date");
	}

	public static void saveWorkflowHistory(WorkflowHistory history) {
		InodeFactory.saveInode(history);
	}

	public static void deleteWorkflowHistory(WorkflowHistory history) {
		InodeFactory.deleteInode(history);
	}

	// Workflow task attached files
	@SuppressWarnings("unchecked")
	public static List<File> getWorkflowTaskFiles(WorkflowTask task) {
		return InodeFactory.getChildrenClass(task, File.class);
	}

	/**
	 * Gets the custom SQL for MSSQL Server This is to fix a bug with the
	 * generated SQL for SQL Server, where Text type fields cannot be used
	 * within a UNION clause, because a UNION effectively performs a DISTINCT on
	 * the result and you can't do DISTINCT across LOB (text, ntext, or image)
	 * fields, we need to do a 'CAST as VARCHAR' on the text type field and
	 * select every other fields explicitly instead of using .*
	 * 
	 * @param sqlVar
	 * @param dbType
	 * @return String
	 * @author Roger Marin
	 * @version 1.5
	 * @since 1.0
	 */

	public static String getCustomSQL(String sqlVar, String dbType) {

		String sqlString = "";

		if (DbConnectionFactory.MSSQL.equals(dbType)) {

			sqlString = "SELECT workflow_task.inode,workflow_task.creation_date,workflow_task.mod_date,workflow_task.due_date,"
					+ "workflow_task.created_by,workflow_task.assigned_to,workflow_task.belongs_to,workflow_task.title, "
					+ "CAST(workflow_task.description as VARCHAR(500)),workflow_task.status,workflow_task.webasset, "
					+ sqlVar + " as type ";
		} else if (DbConnectionFactory.ORACLE.equals(dbType)) {
			sqlString = "SELECT workflow_task.inode,workflow_task.creation_date,workflow_task.mod_date,workflow_task.due_date,"
					+ "workflow_task.created_by,workflow_task.assigned_to,workflow_task.belongs_to,workflow_task.title, "
					+ "TO_NCHAR(workflow_task.description) as description,workflow_task.status,workflow_task.webasset, "
					+ sqlVar + " as type ";
		} else {
			sqlString = "SELECT workflow_task.*, " + sqlVar + " AS type ";

		}

		return sqlString;

	}

}

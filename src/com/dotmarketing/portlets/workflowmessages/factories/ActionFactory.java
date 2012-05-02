package com.dotmarketing.portlets.workflowmessages.factories;

import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portlets.workflowmessages.model.Action;



/**
 *
 * @author Rocco
 */
public class ActionFactory {
    
	// public static final long ACTION_PUBLISH = 5; 
	
	public static Action getActionById(long actionId) {
		DotHibernate dh = new DotHibernate(Action.class);
		Action myAction = null;
		dh.setQuery(
			"from action in class com.dotmarketing.portlets.workflowmessages.model.Action where id = ?");
		dh.setParam(actionId);
		return (Action) dh.load();
	}

	public static java.util.List getActionsByAntiStatusId(long statusId) {
		DotHibernate dh = new DotHibernate(Action.class);
		dh.setQuery(
			"from action in class com.dotmarketing.portlets.workflowmessages.model.Action where anti_status_id = ?");
		dh.setParam(statusId);
		return dh.list();
	}

}

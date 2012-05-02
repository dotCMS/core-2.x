package com.dotmarketing.portlets.communications.factories;

import java.util.List;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.NoSuchUserException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.communications.model.Communication;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

/**
 * 
 * @author Oswaldo
 *
 */
public class CommunicationsFactory {
		
	public static List getCommunicationsByUser(User u) {
		return getCommunicationsByUser(u, "","");	
	}
	
	public static List<Communication> getCommunicationsByUser(User u, String condition, String orderby) {
		DotHibernate dh = new DotHibernate(Communication.class);
		
		if(!UtilMethods.isSet(orderby)){
			orderby = "mod_date desc";
		}
		
		String conditionQuery = "";
		if(UtilMethods.isSet(condition)){
			conditionQuery = " and "+condition;
		} 
		dh.setQuery(
				"from inode in class com.dotmarketing.portlets.communications.model.Communication where modified_by = ? "+conditionQuery+" order by "+ orderby);
		dh.setParam(u.getUserId());
		
		return dh.list(); 
		
	}
	
	public static List<Communication> getCommunications() {
		return getCommunications("","");
	}
	
	public static List<Communication> getCommunications(String condition, String orderby) {
		DotHibernate dh = new DotHibernate(Communication.class);
		if(!UtilMethods.isSet(orderby)){
			orderby = "mod_date desc";
		}
		
		String conditionQuery = "";
		if(UtilMethods.isSet(condition)){
			conditionQuery = " where "+condition;
		}
		
		dh.setQuery("from inode in class com.dotmarketing.portlets.communications.model.Communication "+conditionQuery+" order by "+orderby);
		
		return dh.list();
	}
	
	public static Communication getCommunication(String inode, String userId) {
		return getCommunication(inode,userId, "");
	}
	
	public static Communication getCommunication(String inode, String userId, String orderby) {
		DotHibernate dh = new DotHibernate(Communication.class);
		if(!UtilMethods.isSet(orderby)){
			orderby = "mod_date";
		}
		dh.setQuery(
				"from inode in class com.dotmarketing.portlets.communications.model.Communication where inode = ? and modified_by = ? order by "+orderby);
		dh.setParam(inode);
		dh.setParam(userId);
		
		return (Communication) dh.load();
	}
	
	public static Communication getCommunication(String inode) {
		DotHibernate dh = new DotHibernate(Communication.class);
		dh.setQuery(
		"from inode in class com.dotmarketing.portlets.communications.model.Communication where inode = ?");
		dh.setParam(inode);
		
		return (Communication) dh.load();
	}
	
	public static Communication newInstance() {
		Communication c = new Communication();
		c.setType("communication");
		c.setModDate(new java.util.Date());
		return c;
	}
	
	/*
	 * deletes Communications, recipients and clicks that are owned by the user
	 */
	public static void deleteCommunication(Communication c, String userId) throws DotDataException, NoSuchUserException, DotSecurityException {
		if ((userId == null) || (c.getModifiedBy() == null)) {
			return;
		}
		PermissionAPI perAPI = APILocator.getPermissionAPI();
		User user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
		if(perAPI.doesUserHavePermission(c,PermissionAPI.PERMISSION_WRITE,user)){
			perAPI.removePermissions(c);
		}
		InodeFactory.deleteInode(c);
		
	}
	
	public static void deleteCommunication(String inode) {
		Communication c = getCommunication(inode);
		DotConnect db = new DotConnect();
		db.setSQL("delete from permission where inode_id = ?");
		db.addParam(inode);
		db.getResult();
		InodeFactory.deleteInode(c);
		
	}
	
}

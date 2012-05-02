package com.dotmarketing.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.util.UtilMethods;

/**
 * Utility class to pull users and roles with permissions over a given permissionable for AJAX controls like the filtering select
 * @author Roger M
 *
 */
public class UsersRolesUtil {
	
	/**
	 * Returns a list of Users and Roles that have the given permission over the given permissionable 
	 * @param assetInode
	 * @param permission
	 * @param query
	 * @param offset
	 * @param limit
	 * @param hideSystemRoles
	 * @return
	 * @throws DotDataException
	 */
	public static List<Map<String, Object>>  getUserAndRoleListWithPermissionOnInode(String assetInode, String permission, String query, int offset, int limit, boolean hideSystemRoles) throws DotDataException{
		
		String sql = "";
		String extraSQLForOffset = "";
		Inode inodeObj = InodeFactory.getInode(assetInode, Inode.class);
		assetInode = ((Permissionable)inodeObj).getPermissionId();
		
		String name = "cms_role.role_name";
		
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){
			extraSQLForOffset = ", ROW_NUMBER() OVER(ORDER BY "+name+" ASC) LINENUM ";
		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
			extraSQLForOffset = ", ROW_NUMBER() OVER (ORDER BY "+name+" ASC) AS LINENUM ";
		}
		
		StringBuffer rolesSQL = new StringBuffer();
		rolesSQL.append("SELECT * FROM ( ");
		rolesSQL.append("SELECT distinct cms_role.id as id, 'role' as type, cms_role.role_name as name, 'role' as emailaddress ");
		rolesSQL.append( extraSQLForOffset +  "  from cms_role, permission where cms_role.id = permission.roleid ");
		if(hideSystemRoles){
			rolesSQL.append(" and cms_role.system = " + DbConnectionFactory.getDBFalse());
		}
		boolean isFilteredByName = UtilMethods.isSet(query);
		if (isFilteredByName) {
	       rolesSQL.append(" and lower(cms_role.role_name) like '%" + query.toLowerCase() + "%' ");
	    }
		if(DbConnectionFactory.isOracle()){
			rolesSQL.append(" and bitand(permission.permission, "+ permission+") >0");
		}else{
			rolesSQL.append(" and (permission.permission & "+permission+") > 0");
		}
		rolesSQL.append(" and permission.id in ( select permission.id from permission where inode_id = '"+assetInode+"' union select permission.id from permission where exists (");
		rolesSQL.append("select * from permission_reference where asset_id = '"+assetInode+"' and inode_id = reference_id and permission.permission_type = permission_reference.permission_type))");
		
		RoleAPI roleAPI = APILocator.getRoleAPI();
		
		List<Permission> allPermissions = APILocator.getPermissionAPI().getPermissions((Permissionable)inodeObj);
		List<String> roleIds = new ArrayList<String>();
		for(Permission p : allPermissions) {
			if(p.matchesPermission(Integer.valueOf(permission))) {
				roleIds.add(p.getRoleId());
			}
		}
		roleIds.add(roleAPI.loadCMSAdminRole().getId());
		
		StringBuilder roleIdsSB = new StringBuilder();
		boolean first = true;
		for(String roleId: roleIds) {
			if(!first)
				roleIdsSB.append(",");
			roleIdsSB.append("'" + roleId + "'");
			first=false;
			
		}
	
		String userFullName = DotConnect.concat( new String[]{ "firstname", "' '", "lastname" } );
		StringBuffer usersSQL = new StringBuffer();
		usersSQL.append("select distinct (user_.userid) as id, 'user' as type ");
		
		if(DbConnectionFactory.isMySql()){
			name = " concat(user_.firstName,' ',user_.lastName) "; 
			usersSQL.append(" , "+name+" as name ");
		} else if(DbConnectionFactory.isMsSql()) {
			name = " user_.firstName + ' ' + user_.lastName  ";
			usersSQL.append(" , "+name+" as name ");
		} else  {
			name = " user_.firstName || ' ' || user_.lastName  ";
			usersSQL.append(" , "+name+" as name ");
		}
		
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){
			extraSQLForOffset = " ,ROW_NUMBER() OVER(ORDER BY "+name+" ASC) LINENUM ";
		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
			extraSQLForOffset = " ,ROW_NUMBER() OVER (ORDER BY "+name+" ASC) AS LINENUM ";
		}
		
		usersSQL.append(" , user_.emailaddress as emailaddress ");
		usersSQL.append(extraSQLForOffset + " from user_, users_cms_roles where");
		usersSQL.append(" user_.companyid = '"+PublicCompanyFactory.getDefaultCompanyId()+"' and user_.userid <> 'system' ");
		usersSQL.append(" and user_.userId = users_cms_roles.user_id ");
		usersSQL.append(" and users_cms_roles.role_id in (" + roleIdsSB.toString() + ")");
		if (isFilteredByName) {
			usersSQL.append(" and lower(");
			usersSQL.append(userFullName);
			usersSQL.append(") like '%" + query.toLowerCase() + "%'");
		}
		
		
		DotConnect dc = new DotConnect();
		String limitOffsetSQL = null;
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){
			limitOffsetSQL = "WHERE LINENUM BETWEEN " + (offset<=0?offset:offset+1) + " AND " + (offset + limit);
			sql = rolesSQL.toString() + " UNION " +usersSQL.toString() + " ) " + limitOffsetSQL + " ORDER BY name ASC";
		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
			limitOffsetSQL = "AS MyDerivedTable WHERE MyDerivedTable.LINENUM BETWEEN " + (offset<=0?offset:offset+1)  + " AND " + (offset + limit);
			sql = rolesSQL.toString() + " UNION " +usersSQL.toString() + " ) "+ limitOffsetSQL + " ORDER BY name ASC";
		}else{
			limitOffsetSQL = " LIMIT " +  limit + " OFFSET " + offset;
			sql = rolesSQL.toString() + " UNION " +usersSQL.toString() +" )  as t1 ORDER BY name ASC " + limitOffsetSQL;
		}
		
		dc.setSQL(sql);
		return (ArrayList<Map<String, Object>>)dc.loadResults();
	
	}
	
	
}




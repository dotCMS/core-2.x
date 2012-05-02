package com.dotmarketing.portlets.dashboard.business;

import java.util.List;

import com.dotmarketing.db.DbConnectionFactory;

public abstract class DashboardDataGenerator {
	
	protected String getWorkstreamQuery(){   
	
	return " inode, asset_type, mod_user_id, host_id, mod_date, case when deleted = 1 then 'Deleted' else case when live = 1 then 'Published' else 'Saved' end end as action, name from( "+
	" select contentlet.inode as inode, 'contentlet' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, contentlet.live, contentlet.working, contentlet.deleted, contentlet.title as name "+
	" from contentlet join inode on inode.inode = contentlet.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select htmlpage.inode as inode, 'htmlpage' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, htmlpage.live, htmlpage.working, htmlpage.deleted, identifier.uri as name "+
	" from htmlpage join inode on inode.inode = htmlpage.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select template.inode as inode, 'template' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, template.live, template.working, template.deleted, template.title as name "+ 
	" from template join inode on inode.inode = template.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select file_asset.inode as inode, 'file_asset' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, file_asset.live, file_asset.working, file_asset.deleted, identifier.uri as name "+
	" from file_asset join inode on inode.inode = file_asset.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select containers.inode as inode, 'container' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, containers.live, containers.working, containers.deleted, containers.title as name "+
	" from containers join inode on inode.inode = containers.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select links.inode as inode, 'link' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, links.live, links.working, links.deleted, links.title as name "+
	" from links join inode on inode.inode = links.inode join identifier on identifier.inode = inode.identifier "+
	" )assets where mod_date>(select coalesce(max(mod_date),"
	+(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)?"'1970-01-01 00:00:00')"
			:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE))?"TO_TIMESTAMP('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'))"
					:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL))?"STR_TO_DATE('1970-01-01','%Y-%m-%d'))"
							:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL))?"CAST('1970-01-01' AS DATETIME))":"")+
							" from analytic_summary_workstream) order by assets.mod_date,assets.name asc ";
	}
	
	protected String getSummary404Query() {
	return "select identifier.uri as uri from htmlpage join inode on inode.inode = htmlpage.inode join identifier on identifier.inode = inode.identifier "+ 
	" where identifier.host_inode = ? "+
	" UNION ALL  "+
	" select identifier.uri as uri "+
	" from file_asset join inode on inode.inode = file_asset.inode join identifier on identifier.inode = inode.identifier "+
	" where identifier.host_inode = ? ";
	}
	protected String getPagesQuery() {
	return "select identifier.uri as uri, htmlpage.inode as inode from htmlpage join inode on inode.inode = htmlpage.inode join identifier on identifier.inode = inode.identifier "+ 
	" where identifier.host_inode = ? and live = 1 ";
	}
	
	protected String getContentQuery(){
	return "select identifier.inode as inode, contentlet.title as title "+
	" from contentlet join inode on inode.inode = contentlet.inode join identifier on identifier.inode = inode.identifier "+
	" where identifier.host_inode = ? and live = 1 ";
	}
	
	public abstract void setFlag(boolean flag);//DOTCMS-5511

	public abstract boolean isFinished();

	public abstract double getProgress();

	public abstract List<String> getErrors();

	public abstract long getRowCount();

	public abstract int getMonthFrom();

	public abstract int getYearFrom();

	public abstract int getMonthTo();

	public abstract int getYearTo();
	
	public abstract void start();

}

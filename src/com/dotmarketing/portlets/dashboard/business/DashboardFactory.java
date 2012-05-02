package com.dotmarketing.portlets.dashboard.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.cache.StructureCache;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.dashboard.model.DashboardSummary;
import com.dotmarketing.portlets.dashboard.model.DashboardSummary404;
import com.dotmarketing.portlets.dashboard.model.DashboardSummaryContent;
import com.dotmarketing.portlets.dashboard.model.DashboardSummaryPage;
import com.dotmarketing.portlets.dashboard.model.DashboardSummaryReferer;
import com.dotmarketing.portlets.dashboard.model.DashboardSummaryVisits;
import com.dotmarketing.portlets.dashboard.model.DashboardWorkStream;
import com.dotmarketing.portlets.dashboard.model.TopAsset;
import com.dotmarketing.portlets.dashboard.model.ViewType;
import com.dotmarketing.portlets.structure.factories.FieldFactory;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;



public abstract class DashboardFactory {
	
  protected String getSummaryPagesQuery(){
	return  (DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL) || DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE))?
			" select count(*) as hits, htmlpage.inode as inode,identifier.uri as uri  from clickstream_request "+
			" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
			" join htmlpage on htmlpage.inode = inode1.inode where extract(day from timestampper) = ? "+
			" and extract(month from timestampper) = ? and extract(year from timestampper) = ? "+
			" and host_id = ? group by associated_identifier, identifier.uri,htmlpage.inode "
			:DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)?
					" select count(*) as hits, htmlpage.inode as inode,identifier.uri as uri  from clickstream_request "+
					" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
					" join htmlpage on htmlpage.inode = inode1.inode where DAY(timestampper) = ? and MONTH(timestampper) = ? "+
					" and YEAR(timestampper) = ? and host_id = ? group by associated_identifier,  identifier.uri,htmlpage.inode "
					:DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)?
							" select count(*) as hits, htmlpage.inode as inode,identifier.uri as uri  from clickstream_request "+
							" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
							" join htmlpage on htmlpage.inode = inode1.inode where DATEPART(day, timestampper) = ? "+
							" and DATEPART(month, timestampper) = ? and DATEPART(year, timestampper) = ? "+
							" and host_id = ? group by associated_identifier,identifier.uri,htmlpage.inode ":"";
  }

  protected String getSummaryContentQuery(){
	return (DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL) || DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE))?
			" select count(*) as hits, identifier.uri as uri ,identifier.inode as inode, contentlet.title as title  from clickstream_request "+
			" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
			" join contentlet on contentlet.inode = inode1.inode where extract(day from timestampper) = ? and "+
			" extract(month from timestampper) = ? and extract(year from timestampper) = ? "+
			" and host_id = ? group by associated_identifier, identifier.uri,identifier.inode ,contentlet.title "
			:DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)?
					" select count(*) as hits, identifier.uri as uri ,identifier.inode  as inode, contentlet.title as title  from clickstream_request "+
					" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
					" join contentlet on contentlet.inode = inode1.inode where DAY(timestampper) = ? "+
					" and MONTH(timestampper) = ? and YEAR(timestampper) = ? "+
					" and host_id = ? group by associated_identifier, identifier.parent_path,identifier.inode ,contentlet.title "
					:DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)?
							" select count(*) as hits, identifier.uri as uri ,identifier.inode  as inode, contentlet.title as title  from clickstream_request "+
							" join identifier on identifier.inode = associated_identifier join inode inode1 on inode1.identifier = identifier.inode "+
							" join contentlet on contentlet.inode = inode1.inode where "+
							" DATEPART(day, timestampper) = ? and DATEPART(month, timestampper) = ? and "+
							" DATEPART(year, timestampper) = ? and host_id = ?"+
							" group by associated_identifier, identifier.parent_path,identifier.inode,contentlet.title ":"";
  }
	
  protected String getTopAssetsQuery() {
	return "select identifier.host_inode as host_inode,count(htmlpage.inode) as count, 'htmlpage' as asset_type from identifier "
			+ "join inode inode1 on inode1.identifier = identifier.inode "
			+ "join htmlpage on htmlpage.inode = inode1.inode "
			+ "where identifier.host_inode = ? "
			+ "and htmlpage.live = 1 "
			+ "group by identifier.host_inode "
			+ "UNION ALL "
			+ "select identifier.host_inode as host_inode,count(file_asset.inode) as count, 'file_asset' as asset_type from identifier "
			+ "join inode inode2 on inode2.identifier = identifier.inode "
			+ "join file_asset on file_asset.inode = inode2.inode "
			+ "where identifier.host_inode = ?"
			+ "and file_asset.live = 1 "
			+ "group by identifier.host_inode "
			+ "UNION ALL  "
			+ "select identifier.host_inode as host_inode,count(contentlet.inode) as count, 'contentlet' as asset_type from identifier "
			+ "join inode inode3 on inode3.identifier = identifier.inode "
			+ "join contentlet on contentlet.inode = inode3.inode "
			+ "where identifier.host_inode = ?"
			+ "and contentlet.live = 1 "
			+ "group by identifier.host_inode";
  }
	
	protected String getWorkstreamQuery(String hostId){  	
	return " inode, asset_type, mod_user_id, host_id, mod_date, case when deleted = 1 then 'Deleted' else case when live = 1 then 'Published' else 'Saved' end end as action, name from( "+
	" select contentlet.inode as inode, 'contentlet' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, contentlet.live, contentlet.working, contentlet.deleted, coalesce(contentlet.title, inode.identifier) as name "+
	" from contentlet join inode on inode.inode = contentlet.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select htmlpage.inode as inode, 'htmlpage' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, htmlpage.live, htmlpage.working, htmlpage.deleted, identifier.uri as name "+
	" from htmlpage join inode on inode.inode = htmlpage.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select template.inode as inode, 'template' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, template.live, template.working, template.deleted, coalesce(template.title, inode.identifier) as name "+ 
	" from template join inode on inode.inode = template.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select file_asset.inode as inode, 'file_asset' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, file_asset.live, file_asset.working, file_asset.deleted, identifier.uri as name "+
	" from file_asset join inode on inode.inode = file_asset.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select containers.inode as inode, 'container' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id, mod_date, containers.live, containers.working, containers.deleted, coalesce(containers.title, inode.identifier) as name "+
	" from containers join inode on inode.inode = containers.inode join identifier on identifier.inode = inode.identifier "+
	" UNION ALL "+
	" select links.inode as inode, 'link' as asset_type, mod_user as mod_user_id, identifier.host_inode as host_id,  mod_date, links.live, links.working, links.deleted, coalesce(links.title, inode.identifier) as name "+
	" from links join inode on inode.inode = links.inode join identifier on identifier.inode = inode.identifier "+
	" )assets where mod_date>(select coalesce(max(mod_date),"
	+(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)?"'1970-01-01 00:00:00')"
			:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE))?"TO_TIMESTAMP('1970-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'))"
					:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL))?"STR_TO_DATE('1970-01-01','%Y-%m-%d'))"
							:(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL))?"CAST('1970-01-01' AS DATETIME))":"")+
							" from analytic_summary_workstream) and host_id = '"+hostId+"' order by assets.mod_date,assets.name asc ";
	}
		
	protected String  getIdentifierColumn(){ 
		return "contentlet_1_.identifier";
	}
	
	protected String getWorkstreamListQuery(){
		return "select distinct {analytic_summary_workstream.*}, user_.firstname as username, contentlet.title as hostname " +
               " from analytic_summary_workstream, user_ , inode, contentlet " +
               " where user_.userid = analytic_summary_workstream.mod_user_id and contentlet.inode = inode.inode and contentlet.live = 1 " +
               " and inode.identifier = analytic_summary_workstream.host_id and analytic_summary_workstream.name is not null ";
	}
	
	protected String getWorkstreamCountQuery(){
		return "select count(distinct analytic_summary_workstream.id) as summaryCount from analytic_summary_workstream, user_, contentlet, inode where" +
        " user_.userid = analytic_summary_workstream.mod_user_id and contentlet.inode = inode.inode and contentlet.live = 1 " +
        " and inode.identifier = analytic_summary_workstream.host_id  and analytic_summary_workstream.name is not null "; 
	}

	
	protected StringBuffer getHostListQuery(boolean hasCategory, String selectedCategories, String runDashboardFieldContentlet){
		StringBuffer query = new StringBuffer();
		query.append("select "+ (DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE) || DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)?"":" distinct ")+" {contentlet.*}, " +
				"coalesce(d.page_views,0) as totalpageviews,  " +
				"CASE contentlet.live "+
                " WHEN "+ DbConnectionFactory.getDBTrue() +" THEN 'Live' "+
                " ELSE 'Stopped' "+
                "END AS status "+
				"from  contentlet, structure s,"+(hasCategory?"tree,":"") +" inode contentlet_1_  "+
				"left join " +
				"(" +
				  "select sum(page_views) as page_views, host_id from analytic_summary join "+
				  "analytic_summary_period on analytic_summary.summary_period_id = analytic_summary_period.id "+
				  "and analytic_summary_period.full_date > ? and analytic_summary_period.full_date < ? "+
				  "group by host_id" +
				") "+ (DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE) || DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)?"":" as ") +"d on d.host_id = contentlet_1_.identifier " +
				"where " +
				(hasCategory?" tree.child = contentlet.inode and tree.parent in("+selectedCategories+") and ":"") +
				"contentlet.structure_inode = s.inode and " +
				"contentlet_1_.type = 'contentlet' and contentlet.inode = contentlet_1_.inode and s.name ='Host' "+ 
		        "and contentlet.title <> 'System Host' and contentlet.working = " + DbConnectionFactory.getDBTrue() + (UtilMethods.isSet(runDashboardFieldContentlet)?" and contentlet."+runDashboardFieldContentlet+"= "+ DbConnectionFactory.getDBTrue()+"":"")+ " ");
		return query;
	}
	
	protected StringBuffer getHostListCountQuery(boolean hasCategory, String selectedCategories, String runDashboardFieldContentlet){
		StringBuffer query = new StringBuffer();
		query.append("select count(distinct contentlet.inode) as total " +
				"from  contentlet, structure s,"+(hasCategory?"tree,":"") +" inode contentlet_1_  "+
				"where "+(hasCategory?" tree.child = contentlet.inode and tree.parent in("+selectedCategories+") and ":"") +" contentlet.structure_inode = s.inode "+
				"and contentlet_1_.type = 'contentlet' and contentlet.inode = contentlet_1_.inode and s.name ='Host' "+ 
		        "and contentlet.title <> 'System Host' and contentlet.working = " + DbConnectionFactory.getDBTrue() + (UtilMethods.isSet(runDashboardFieldContentlet)?" and contentlet."+runDashboardFieldContentlet+"= "+ DbConnectionFactory.getDBTrue()+"":"")+ " ");
		return query;
	}
	
	protected String getHostQueryForClickstream(String runDashboardFieldContentlet){

		String query = " select contentlet_1_.identifier as host_id from contentlet, structure s, inode contentlet_1_ "+
		" where contentlet.structure_inode = s.inode and contentlet_1_.type = 'contentlet' and contentlet.inode = contentlet_1_.inode and s.name ='Host'"+
	    " and contentlet.title <> 'System Host' and contentlet.working = " + DbConnectionFactory.getDBTrue() + (UtilMethods.isSet(runDashboardFieldContentlet)?" and contentlet."+runDashboardFieldContentlet+"= "+ DbConnectionFactory.getDBTrue()+"":"")+
	    " group by contentlet_1_.identifier "; 
		return query;
	}
	
	/**
	 * 
	 * @param user
	 * @param includeArchived
	 * @param params
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<Host> getHostList(User user, boolean includeArchived, Map<String, Object> params, int limit, int offset, String sortBy) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param user
	 * @param includeArchived
	 * @param params
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public long getHostListCount(User user, boolean includeArchived, Map<String, Object> params) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param user
	 * @param hostId
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<DashboardWorkStream> getWorkStreamList(User user, String hostId, String userId, Date fromDate, Date toDate, int limit, int offset, String sortBy)throws DotDataException,DotHibernateException;
	
	/**
	 * 
	 * @param user
	 * @param hostId
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public long getWorkStreamListCount(User user, String hostId, String userId, Date fromDate, Date toDate)throws DotDataException,DotHibernateException;
	
	/**
	 * 
	 * @param hostId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public DashboardSummary getDashboardSummary(String hostId, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
    /**
     * 
     * @param hostId
     * @param viewType
     * @param fromDate
     * @param toDate
     * @return
     * @throws DotDataException
     * @throws DotHibernateException
     */
	abstract public List<DashboardSummaryVisits> getDashboardSummaryVisits(String hostId, ViewType viewType, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
	
	/**
	 * 
	 * @param hostId
	 * @param fromDate
	 * @param toDate
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<DashboardSummaryReferer> getTopReferers(String hostId, Date fromDate, Date toDate, int limit, int offset, String sortBy) throws DotDataException, DotHibernateException;
	
	
	/**
	 * 
	 * @param hostId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public long getTopReferersCount(String hostId, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param hostId
	 * @param fromDate
	 * @param toDate
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<DashboardSummaryPage> getTopPages(String hostId, Date fromDate, Date toDate, int limit, int offset, String sortBy) throws DotDataException, DotHibernateException;
	
    /**
     * 
     * @param hostId
     * @param fromDate
     * @param toDate
     * @return
     * @throws DotDataException
     * @throws DotHibernateException
     */
	abstract public long getTopPagesCount(String hostId, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param hostId
	 * @param fromDate
	 * @param toDate
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<DashboardSummaryContent> getTopContent(String hostId, Date fromDate, Date toDate, int limit, int offset, String sortBy) throws DotDataException, DotHibernateException;
	
    /**
     * 
     * @param hostId
     * @param fromDate
     * @param toDate
     * @return
     * @throws DotDataException
     * @throws DotHibernateException
     */
	abstract public long getTopContentCount(String hostId, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param user
	 * @param hostId
	 * @param showIgnored
	 * @param fromDate
	 * @param toDate
	 * @param limit
	 * @param offset
	 * @param sortBy
	 * @return
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public List<DashboardSummary404> get404s(String userId, String hostId, boolean showIgnored, Date fromDate, Date toDate, int limit, int offset, String sortBy) throws DotDataException, DotHibernateException;
	
	
    /**
     * 
     * @param user
     * @param hostId
     * @param showIgnored
     * @param fromDate
     * @param toDate
     * @return
     * @throws DotDataException
     * @throws DotHibernateException
     */
	abstract public long get404Count(String userId, String hostId, boolean showIgnored, Date fromDate, Date toDate) throws DotDataException, DotHibernateException;
	
	/**
	 * 
	 * @param user
	 * @param id
	 * @param ignored
	 * @throws DotDataException
	 * @throws DotHibernateException
	 */
	abstract public void setIgnored(User user, long id, boolean ignored) throws DotDataException, DotHibernateException;
	

	/**
	 * 
	 * @param user
	 * @param hostId
	 * @return
	 * @throws DotDataException
	 * @throws DotSecurityException 
	 */
	abstract public List<TopAsset> getTopAssets(User user,String hostId) throws DotDataException;
	
	/**
	 * 
	 */
	abstract public void populateAnalyticSummaryTables();
	

	
	/**
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	abstract public int checkPeriodData(int month, int year);
	
	
}

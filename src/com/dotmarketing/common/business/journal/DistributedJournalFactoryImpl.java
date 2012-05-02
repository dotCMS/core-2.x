/**
 * 
 */
package com.dotmarketing.common.business.journal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dotcms.enterprise.ClusterThreadProxy;
import com.dotmarketing.beans.Host;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author Jason Tesser
 * @since 1.6.5c
 */
public class DistributedJournalFactoryImpl<T> extends DistributedJournalFactory<T> {

	private String[] serversIds = ClusterThreadProxy.getClusteredServerIds();
	private String serverId ;
	private boolean indexationEnabled = Config.getBooleanProperty("DIST_INDEXATION_ENABLED");
	
	private String ENTRYINSERTSQL = "INSERT INTO dist_process(object_to_index, time_entered, serverid, journal_type)VALUES (?, ?, ?, ?)";
	private String GETMAXPROCESSQL = "SELECT max(id) as max FROM dist_process";
	private String PROCESSSQL = "INSERT INTO dist_journal (object_to_index, time_entered, serverid, journal_type) SELECT object_to_index, min(time_entered),  serverid, journal_type FROM dist_process p1 WHERE NOT EXISTS (SELECT p.id FROM dist_process p, dist_journal j WHERE p.object_to_index = j.object_to_index AND  p.serverid=j.serverid AND p.journal_type=j.journal_type AND p1.id = p.id) AND id <=? GROUP BY object_to_index, serverid, journal_type";
	private String DELETEPROCESSQL = "DELETE FROM dist_process WHERE id<=?";
	private String ENTRIESSELECTSQL = "SELECT object_to_index , max(id) as id from dist_journal where journal_type = ? and serverid = ? GROUP BY id, object_to_index,time_entered ORDER BY time_entered ASC";
	private String ENTRYDELETESQL = "DELETE FROM dist_journal where serverid = ? and journal_type = ? and id < ?";
											 
//	private String REINDEXENTRIESSELECTSQL = "SELECT id, inode_to_index,ident_to_index, priority,dist_action from dist_reindex_journal,inode c where dist_action = " + REINDEX_ACTION_DELETE_OBJECT + " OR (dist_reindex_journal.inode_to_index = c.inode and serverid = ? and c.type = 'contentlet' and dist_action <> " + REINDEX_ACTION_REINDEX_FULL + ") GROUP BY id, ident_to_index,priority,time_entered,dist_action, inode_to_index ORDER BY priority ASC ,time_entered ASC LIMIT 10";
	private String REINDEXENTRIESSELECTSQL = "SELECT * FROM load_records_to_index(?, ?)";
	private String ORACLEREINDEXENTRIESSELECTSQL = "SELECT * FROM table(load_records_to_index(?, ?))";
	private String MYSQLREINDEXENTRIESSELECTSQL	= "{call load_records_to_index(?,?)}";
	
	private String REINDEXENTRYDELETESQL = "DELETE FROM dist_reindex_journal where id = ?";
	private String NEEDTOBUILDNEWINDEXFORSERVER = "select count(*) as count from dist_reindex_journal where ident_to_index = ? and serverid = ?";
	private String REINDEXJOUNRNALRECORDSCOUNT = "select count(*) as count from dist_reindex_journal j where serverid = ? and inode_to_index in (select inode from inode where type = 'contentlet' and identifier = j.ident_to_index)";

	// SQL Server specific SQL
	private String MSREINDEXENTRIESSELECTSQL = "SELECT TOP 10 id, inode_to_index,ident_to_index, priority, dist_action from dist_reindex_journal, inode c where dist_action = " + REINDEX_ACTION_DELETE_OBJECT + " OR (dist_reindex_journal.inode_to_index = c.inode and type = 'contentlet' and serverid = ? and dist_action <> " + REINDEX_ACTION_REINDEX_FULL + ") GROUP BY id, ident_to_index,priority,time_entered, dist_action,inode_to_index ORDER BY priority ASC ,time_entered ASC";

	// Oracle specific SQL
	//private String ORACLEREINDEXENTRIESSELECTSQL = "SELECT id, inode_to_index,ident_to_index, priority, dist_action from dist_reindex_journal, inode c where (dist_action = " + REINDEX_ACTION_DELETE_OBJECT + " OR (dist_reindex_journal.inode_to_index = c.inode and type = 'contentlet' and serverid = ? and dist_action <> " + REINDEX_ACTION_REINDEX_FULL + ")) and rownum <=10 GROUP BY id, ident_to_index,priority,time_entered, dist_action, inode_to_index ORDER BY priority ASC ,time_entered ASC";
	
	private String TIMESTAMPSQL = "NOW()";
	
	public enum DateType {
		DAY("DAY"),
		MINUTE("MINUTE");
		
        private String value;
		
		DateType (String value) {
			this.value = value;
		}
		
		public String toString () {
			return value;
		}
			
		public static DateType getObject (String value) {
			DateType[] ojs = DateType.values();
			for (DateType oj : ojs) {
				if (oj.value.equals(value))
					return oj;
			}
			return null;
		}
	};
		
	
	public DistributedJournalFactoryImpl(T newIndexValue) {
		super(newIndexValue);

		Logger.info(this, "Server IDs configured: " + Arrays.toString(serversIds));
		
		serverId = ConfigUtils.getServerId();
		
		if (serversIds.length < 1) {
			serversIds = new String[] { serverId };
		}
		
		if (DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {
			REINDEXENTRIESSELECTSQL = MSREINDEXENTRIESSELECTSQL;
//			REINDEXDELETEPROCESSQL = MSREINDEXDELETEPROCESSQL;
			TIMESTAMPSQL = "GETDATE()";
		}  else if (DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)) {
			REINDEXENTRIESSELECTSQL = ORACLEREINDEXENTRIESSELECTSQL;
			TIMESTAMPSQL = "CAST(SYSTIMESTAMP AS TIMESTAMP)"; 
		} else if (DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)) {
			REINDEXENTRIESSELECTSQL = MYSQLREINDEXENTRIESSELECTSQL;
			
		}
	}
	
	protected boolean isIndexationEnabled() {
		return indexationEnabled;
	}

	protected void setIndexationEnabled(boolean indexationEnabled) {
		this.indexationEnabled = indexationEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dotmarketing.business.DistributedFactory#addCacheEntry(java.lang.
	 * String)
	 */
	@Override
	protected void addCacheEntry(String key, String group)
			throws DotDataException {
		Connection con = null;
		try {
			if (indexationEnabled) {
				con = DbConnectionFactory.getDataSource().getConnection();
				con.setAutoCommit(false);
				java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
				for (String serversId : serversIds) {
					if (!serverId.equals(serversId)) {
						DotConnect dc = new DotConnect();
						dc.setSQL(ENTRYINSERTSQL);
						dc.addParam(key + ":" + group);
						dc.addParam(timestamp);
						dc.addParam(serversId);
						dc.addParam(JOURNAL_TYPE_CACHE);
						try {
							dc.getResult(con);
						} catch (Exception e) {
							Logger
									.warn(this,
											"Usually not a problem but a cache entry failed to insert in the table.");
							Logger.debug(this, e.getMessage(), e);
						}
					}
				}
			}
		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				if(con!=null){
					con.commit();
				}
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					if(con!=null){
						con.close();
					}
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dotmarketing.business.DistributedFactory#findCacheEntriesToRemove()
	 */
	@Override
	protected List<String> findCacheEntriesToRemove() throws DotDataException {
		DotConnect dc = new DotConnect();
		List<String> x = new ArrayList<String>();
		Connection con = null;
		try {
			con = DbConnectionFactory.getDataSource().getConnection();
			con.setAutoCommit(false);
			dc.setSQL(ENTRIESSELECTSQL);
			dc.addParam(JOURNAL_TYPE_CACHE);
			dc.addParam(serverId);

			List<Map<String, String>> results = dc.loadResults(con);
			long id = 0;
			for (Map<String, String> r : results) {
				x.add(r.get("object_to_index"));
				id = new Long(r.get("id"));
			}
			deleteCacheEntries(serverId, id, con);
		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				con.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					con.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		return x;
	}

	private void deleteCacheEntries(String serverId, long id, Connection con)
			throws DotDataException {
		DotConnect dc = new DotConnect();
		try {
			dc.setSQL(ENTRYDELETESQL);
			dc.addParam(serverId);
			dc.addParam(JOURNAL_TYPE_CACHE);
			dc.addParam(id + 1);
			dc.loadResult(con);
		} catch (Exception e1) {
			throw new DotDataException(e1.getMessage(), e1);
		}
	}

	@Override
	protected void addContentIndexEntry(Contentlet c)
			throws DotDataException {
		
		Connection con = null;
		try {
			con = DbConnectionFactory.getDataSource().getConnection();
			con.setAutoCommit(false);
			String sql = "INSERT INTO dist_reindex_journal(inode_to_index,ident_to_index, serverid, priority,dist_action,time_entered) values (?,?,?,?,?,?)";
			DotConnect dc = new DotConnect();		
			java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
			for (String serversId : serversIds) {
				dc.setSQL(sql);
				dc.addObject(c.getInode());
				dc.addObject(c.getIdentifier());
				dc.addObject(serversId);
				int priority = REINDEX_JOURNAL_PRIORITY_CONTENT_REINDEX;
				if(c.isLowIndexPriority()){
					priority = REINDEX_JOURNAL_PRIORITY_CONTENT_CAN_WAIT_REINDEX;	
				}
				dc.addObject(priority);
				dc.addObject(REINDEX_ACTION_REINDEX_OBJECT); 
				dc.addObject(timestamp); 
				try {
					dc.loadResult(con);
				} catch (DotDataException e) {
					Logger
							.error(this,
									"A reindex entry failed to insert in the table.",e);
					
				}
			}
			

		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				con.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					con.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected void deleteContentIndexEntries(String serverId, long id)
			throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection con = null;
		try {
			con = DbConnectionFactory.getDataSource().getConnection();
			con.setAutoCommit(false);
			dc.setSQL(ENTRYDELETESQL);
			dc.addParam(serverId);
			dc.addParam(JOURNAL_TYPE_CONTENTENTINDEX);
			dc.addParam(id + 1);
			dc.loadResult(con);
		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				//con.commit();
				con.createStatement().execute("COMMIT;");
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					con.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected List<IndexJournal<T>> findContentReindexEntriesToReindex()
			throws DotDataException {
		DotConnect dc = new DotConnect();
		List<IndexJournal<T>> x = new ArrayList<IndexJournal<T>>();
		Connection con = null;
		try {
			con = DbConnectionFactory.getDataSource().getConnection();
			con.setAutoCommit(false);
			if(!DbConnectionFactory.isMsSql()){
				dc.setSQL(REINDEXENTRIESSELECTSQL);
				dc.addParam(serverId);
				dc.addParam(10);
			}else{
			  dc.setSQL("SELECT * FROM load_records_to_index('" + serverId + "',10)");
			}
			List<Map<String, Object>> results = dc.loadObjectResults(con);
			for (Map<String, Object> r : results) {
				IndexJournal<T> ij = new IndexJournal<T>();
				ij.setId(((Number)r.get("id")).longValue());
				int dist_action = ((Number)r.get("dist_action")).intValue();
	    		if(dist_action == REINDEX_ACTION_DELETE_OBJECT){
	    			ij.setDelete(true);
	    		}
				T o = (T)r.get("inode_to_index");
				T o1 = (T)r.get("ident_to_index");
				ij.setInodeToIndex(o);
				ij.setIdentToIndex(o1);
				ij.setPriority(((Number)(r.get("priority"))).intValue());
				x.add(ij);
			}
		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				con.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					con.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		return x;
	}

	@Override
	protected void processJournalEntries() throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection con = null;
		ClusterMutex mutex = null;
		try {
			con = DbConnectionFactory.getDataSource().getConnection();
			con.setAutoCommit(false);
			mutex = new ClusterMutex(con);
			mutex.lockTable();
			dc.setSQL(GETMAXPROCESSQL);
			ArrayList<Map<String, String>> ret = dc.loadResults(con);
			if (ret != null && ret.size() > 0
					&& UtilMethods.isSet(ret.get(0).get("max"))) {
				
				Long max = Long.parseLong(ret.get(0).get("max"));
			
			
				dc.setSQL(PROCESSSQL);
				dc.addParam(max);
				dc.loadResult(con);

				dc.setSQL(DELETEPROCESSQL);
				dc.addParam(max);
				dc.loadResult(con);
			}
		
		} catch (SQLException e1) {
			throw new DotDataException(e1.getMessage(), e1);
		} finally {
			try {
				con.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try{
					mutex.unlockTable();
				}catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
				try {
					con.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected void addBuildNewIndexEntries() throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
//		ReindexMutex mutex = null;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
//			mutex = new ReindexMutex(conn);
//			mutex.lockTable();
			java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
		
			for (String sid : serversIds) {
				String sql = "INSERT INTO dist_reindex_journal(inode_to_index,ident_to_index, serverid, priority, dist_action, time_entered) values(" + reindexJournalObjectToIndexNew + "," + reindexJournalObjectToIndexNew + ",'" + sid + "'," + REINDEX_JOURNAL_PRIORITY_CONTENT_REINDEX + "," + REINDEX_ACTION_REINDEX_FULL + "," + "?" + ")";
				dc.setSQL(sql);
				dc.addObject(timestamp);
				dc.getResult(conn);
				sql = "insert into dist_reindex_journal(inode_to_index,ident_to_index,serverid, priority, dist_action, time_entered) select max(inode),identifier,'" + sid + "'," + REINDEX_JOURNAL_PRIORITY_NEWINDEX +"," + REINDEX_ACTION_REINDEX_OBJECT + "," + TIMESTAMPSQL  + " from inode where type like 'contentlet'  group by identifier";
				dc.setSQL(sql);
				dc.getResult(conn);
			}
		} catch (Exception e) {
			throw new DotDataException(e.getMessage(), e);
		} finally {
//			try {
//				mutex.unlockTable();
//			} catch (SQLException ex) {
//				Logger
//						.fatal(
//								this,
//								"Error  unlocking the reindex journal table" + 	ex);
//				Logger
//				.debug(
//						this,
//						"Error  unlocking the reindex journal table" , 	ex);
//			} finally {
				try {
					conn.commit();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				} finally {
					try {
						conn.close();
					} catch (Exception e) {
						Logger.error(this, e.getMessage(), e);
					}
				}
//			}
		}
	}

	@Override
	protected void addStructureReindexEntries(T structureInode)
			throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			for (String sid : serversIds) {
				String sql = "insert into dist_reindex_journal(inode_to_index,ident_to_index,serverid, priority,dist_action, time_entered) select distinct i.inode,i.identifier,'"+sid+"',"+REINDEX_JOURNAL_PRIORITY_STRUCTURE_REINDEX+"," + REINDEX_ACTION_REINDEX_OBJECT + "," + TIMESTAMPSQL  + " from inode i, contentlet c where i.type = 'contentlet' and i.inode = c.inode and c.structure_inode = ? and (c.working = " + DbConnectionFactory.getDBTrue() + " OR c.live = " + DbConnectionFactory.getDBTrue() + " OR c.deleted = " + DbConnectionFactory.getDBTrue() + ")";
				dc.setSQL(sql);
				dc.addParam(structureInode);
				dc.loadResult(conn);
				// for (Long ident : identsToAdd) {
				// dc.setSQL(BUILDINDEXENTRIESFORSTRUCTUREINSERT);
				// dc.addParam(ident);
				// dc.addParam(sid);
				// dc.addParam(DistributedJournalAPI.REINDEX_JOURNAL_PRIORITY_STRUCTURE_REINDEX);
				// dc.loadResult(conn);
				// }
			}
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected void deleteReindexEntryForServer(IndexJournal<T> iJournal)
			throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			dc.setSQL(REINDEXENTRYDELETESQL);
			dc.addParam(iJournal.getId());
			dc.loadResult(conn);
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	protected boolean buildNewIndexForServer() throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			dc.setSQL(NEEDTOBUILDNEWINDEXFORSERVER);
			dc
					.addParam(reindexJournalObjectToIndexNew);
			dc.addParam(serverId);
			List<Map<String, String>> results = dc.loadResults(conn);
			String c = results.get(0).get("count");
			long count = 0;
			if (!UtilMethods.isSet(c)) {
				count = 0;
			} else if (c.equals("")) {
				count = 0;
			}
			count = new Long(c);
			if (count > 0) {
				dc
						.setSQL("delete from dist_reindex_journal where dist_action = " + REINDEX_ACTION_REINDEX_FULL + " and serverid like ?");
				dc.addParam(serverId);
				dc.loadResult(conn);
				return true;
			}
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		return false;
	}

	@Override
	protected void deleteLikeJournalRecords(IndexJournal<T> ijournal) throws DotDataException {
		String deleteLikeReindexRecords = "DELETE FROM dist_reindex_journal where serverid = ? AND ident_to_index = ? AND id <> ? " +
				"AND inode_to_index IN (SELECT inode FROM inode i WHERE i.type = 'contentlet' AND i.identifier = ?)";
		Connection conn = null;
		DotConnect dc = new DotConnect();
		dc.setSQL(deleteLikeReindexRecords);
		dc.addParam(serverId);
		dc.addParam(ijournal.getIdentToIndex());
		dc.addParam(ijournal.getId());
		dc.addParam(ijournal.getIdentToIndex());
		try{
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			dc.loadResult(conn);
		}catch (SQLException ex) {
			Logger.fatal(this,ex.getMessage(),ex);
			Logger.fatal(this,ex.getMessage(),ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	protected long recordsLeftToIndexForServer() throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
		long count = 0;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			
			dc.setSQL(REINDEXJOUNRNALRECORDSCOUNT);
			dc.addParam(serverId);
			List<Map<String, String>> results = results = dc.loadResults(conn);
			String c = results.get(0).get("count");
			long count1 = 0;
			if (!UtilMethods.isSet(c)) {
				count1 = 0;
			} else if (c.equals("")) {
				count1 = 0;
			}
			count1 = new Long(c);
			count = count + count1;
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		return count;
	}
	
	@Override
	protected boolean areRecordsLeftToIndex() throws DotDataException {
		DotConnect dc = new DotConnect();
		Connection conn = null;
		long count = 0;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);

			dc.setSQL(REINDEXJOUNRNALRECORDSCOUNT);
			dc.addParam(serverId);
			List<Map<String, String>> results = dc.loadResults(conn);
			String c = results.get(0).get("count");
			long count1 = 0;
			if (!UtilMethods.isSet(c)) {
				count1 = 0;
			} else if (c.equals("")) {
				count1 = 0;
			}
			count1 = new Long(c);
			count = count + count1;
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void addContentIndexEntryToDelete(T contentIdentifier)
			throws DotDataException {
		if (!UtilMethods.isSet(contentIdentifier)) {
			Logger
					.warn(this,
							"You cannot add content to index whose identifier is not valid");
			return;
		}

		Connection conn = null;
		try {
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
			for (String serversId : serversIds) {
				DotConnect dc = new DotConnect();
				String sql = "INSERT INTO dist_reindex_journal(inode_to_index,ident_to_index ,serverid, priority,dist_action, time_entered)VALUES (?, ?, ?,?,?,?)";
				dc.setSQL(sql);
				dc.addParam(contentIdentifier);
				dc.addParam(contentIdentifier);
				dc.addParam(serversId);
				dc.addParam(REINDEX_JOURNAL_PRIORITY_CONTENT_CAN_WAIT_REINDEX);
				dc.addParam(REINDEX_ACTION_DELETE_OBJECT);
				dc.addParam(timestamp);
				try {
					dc.loadResult(conn);
				} catch (DotDataException e) {
					Logger
							.error(this,
									"A reindex entry failed to insert in the table.",e);
				}
			}
		} catch (SQLException ex) {
			Logger
			.fatal(
					this,
					"Error  unlocking the reindex journal table" + 	ex);
			Logger
			.debug(
					this,
					"Error  unlocking the reindex journal table" , 	ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

//	public class ReindexMutex {
//
//		private String myLock = "lock table dist_reindex_lock write";
//		private String myCommit = "unlock tables";
//
//		private String oraLock = "LOCK TABLE DIST_REINDEX_JOURNAL IN EXCLUSIVE MODE";
//		private String pgLock = "lock table DIST_REINDEX_JOURNAL;";
//		
//		private String msLock = "SELECT * FROM dist_reindex_lock WITH (XLOCK)";
//	
//		public ReindexMutex(Connection conn) {
//			conn1 = conn;
//		}
//
//		Connection conn1 = null;
//
//		
//
//		public void lockTable() throws SQLException {
//			if (DbConnectionFactory.getDBType().equals(
//					DbConnectionFactory.MYSQL)) {
//				// We need another connection, to get around mysqls limitations
//				// with locks (the need to lock all or no tables in a query)
//				conn1 = DbConnectionFactory.getDataSource().getConnection();
//				conn1.setAutoCommit(false);
//				Statement s = conn1.createStatement();
//				s.execute(myLock);
//			}
//			if (DbConnectionFactory.getDBType().equals(
//					DbConnectionFactory.ORACLE)) {
//
//				Statement s = conn1.createStatement();
//				s.execute(oraLock);				
//			}
//			
//			if (DbConnectionFactory.getDBType().equals(
//					DbConnectionFactory.MSSQL)) {
//				conn1.setAutoCommit(false);
//				Statement s = conn1.createStatement();
//				s.execute(msLock);
//			}
//			
//			if (DbConnectionFactory.getDBType().equals(
//					DbConnectionFactory.POSTGRESQL)) {
//				conn1.setAutoCommit(false);
//				Statement s = conn1.createStatement();
//				s.execute(pgLock);
//			}
//		}
//
//		public void unlockTable() throws SQLException {
//			if (DbConnectionFactory.getDBType().equals(
//					DbConnectionFactory.MYSQL)) {
//				Statement s = conn1.createStatement();
//				s.execute(myCommit);
//				conn1.commit();
//				// We requested a new one, this is why we close only in this
//				// case
//				conn1.close();
//
//			}
//			
//			//No need to unlock for oracle pg, or sql server.  Using passed in connection, the calling method should commit (which is all that's needed to unlock the tables)
//			
//		}
//	}

	public class ClusterMutex {
		private String myLock = "lock table dist_lock write";
		private String myCommit = "unlock tables";
		
		private String msLock = "SELECT * FROM dist_lock WITH (XLOCK)";

		
		private String oraClusterLock = "LOCK TABLE DIST_JOURNAL IN EXCLUSIVE MODE";
		private String pgLock = "lock table DIST_JOURNAL;";
		
		public ClusterMutex(Connection conn) {
			conn1 = conn;
		}

		Connection conn1 = null;

		public void lockTable() throws SQLException {
			if (DbConnectionFactory.getDBType().equals(
					DbConnectionFactory.MYSQL)) {
				// We need another connection, to get around mysqls limitations
				// with locks (the need to lock all or no tables in a query)
				conn1 = DbConnectionFactory.getDataSource().getConnection();
				conn1.setAutoCommit(false);
				Statement s = conn1.createStatement();
				s.execute(myLock);
			}
			
			
			if (DbConnectionFactory.getDBType().equals(
					DbConnectionFactory.ORACLE)) {
				conn1.setAutoCommit(false);
				Statement s = conn1.createStatement();
				s.execute(oraClusterLock);
			}
			
			if (DbConnectionFactory.getDBType().equals(
					DbConnectionFactory.MSSQL)) {
				conn1.setAutoCommit(false);
				Statement s = conn1.createStatement();
				s.execute(msLock);
			}
			
			if (DbConnectionFactory.getDBType().equals(
					DbConnectionFactory.POSTGRESQL)) {
				conn1.setAutoCommit(false);
				Statement s = conn1.createStatement();
				s.execute(pgLock);
			}
			
		}

		public void unlockTable() throws SQLException {
			if (DbConnectionFactory.getDBType().equals(
					DbConnectionFactory.MYSQL)) {
				Statement s = conn1.createStatement();
				s.execute(myCommit);
				conn1.commit();
				// We requested a new one, this is why we close only in this
				// case
				conn1.close();

			}
			//No need to unlock for oracle, pg, or sql server.  Using passed in connection, the calling method should commit (which is all that's needed to unlock the tables)
		}
	}

	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	@Override
	protected void distReindexJournalCleanup(int time, boolean add, boolean includeInodeCheck, DateType type) throws DotDataException {
		

		StringBuffer reindexJournalCleanupSql = new StringBuffer();

		String sign = "+";
		if(!add){
			sign = "-";
		}

		if (DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {

			reindexJournalCleanupSql.append("DELETE FROM dist_reindex_journal WHERE time_entered < DATEADD("+ type.toString() +", "+ sign + "" + time +", GETDATE()) ");


		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){

			reindexJournalCleanupSql.append("DELETE FROM dist_reindex_journal WHERE time_entered < DATE_ADD(NOW(), INTERVAL "+ sign + "" + time +" " + type.toString()+") ");


		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){

			reindexJournalCleanupSql.append("DELETE FROM dist_reindex_journal WHERE time_entered < NOW() "+ sign + " INTERVAL '"+ time +" " + type.toString()  +"' ");


		}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){

			reindexJournalCleanupSql.append("DELETE FROM dist_reindex_journal WHERE  CAST(time_entered AS TIMESTAMP) <  CAST(SYSTIMESTAMP "+ sign + "  INTERVAL '"+time+"' "+ type.toString() + " AS TIMESTAMP)");
		}
		if(includeInodeCheck){
			reindexJournalCleanupSql.append(" AND inode_to_index NOT IN (SELECT inode FROM inode i WHERE type = 'contentlet' AND i.identifier = ident_to_index)");

		}
		reindexJournalCleanupSql.append(" AND serverid = ?");

		Connection conn = null;
		DotConnect dc = new DotConnect();
		dc.setSQL(reindexJournalCleanupSql.toString());
		dc.addParam(serverId);
		try{
			conn = DbConnectionFactory.getDataSource().getConnection();
			conn.setAutoCommit(false);
			dc.loadResult(conn);
		}catch (SQLException ex) {
			Logger.fatal(this,ex.getMessage(),ex);
		} finally {
			try {
				conn.commit();
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			} finally {
				try {
					conn.close();
				} catch (Exception e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
		
	}
	
	@Override
	protected void cleanDistReindexJournal() throws DotDataException  {
		DotConnect dc = new DotConnect();
		dc.setSQL("DELETE From dist_reindex_journal where priority =?");
		dc.addParam(REINDEX_JOURNAL_PRIORITY_NEWINDEX);
		dc.loadResult();		
	}

	@Override
	protected List<IndexJournal> viewReindexJournalData() throws DotDataException {
		DotConnect dc = new DotConnect();
		dc.setSQL("select count(*) as mycount,serverid,priority from dist_reindex_journal group by serverid,priority order by serverid, priority");
		List<IndexJournal> journalList = new ArrayList<IndexJournal>(); 
		List<Map<String, String>> results = dc.loadResults();
		for (Map<String, String> r : results) {
			IndexJournal index = new IndexJournal(r.get("serverid"),new Integer(r.get("mycount")),new Long(r.get("priority")));
			journalList.add(index);
		}
		return journalList;
	}

	@Override
	protected void refreshContentUnderHost(Host host) throws DotDataException {

		String sql = "INSERT INTO dist_reindex_journal(inode_to_index,ident_to_index, serverid, priority,dist_action) " 
						+ " SELECT distinct contentlet.inode, identifier.inode, ?, ?, ? from contentlet, inode, identifier " 
						+ " where contentlet.inode = inode.inode " 
						+ " and identifier.inode = inode.identifier "
						+ " and inode.identifier <> identifier.host_inode "
						+ " and identifier.host_inode = ? "
						+ " and ( contentlet.working = " + DbConnectionFactory.getDBTrue() 
								+ " or contentlet.live = " + DbConnectionFactory.getDBTrue()
								+ " or contentlet.deleted = " + DbConnectionFactory.getDBTrue() + ")";
		DotConnect dc = new DotConnect();		
		for (String serversId : serversIds) {
			dc.setSQL(sql);
			dc.addParam(serversId);
			int priority = REINDEX_JOURNAL_PRIORITY_CONTENT_REINDEX;				
			dc.addParam(priority);
			dc.addParam(REINDEX_ACTION_REINDEX_OBJECT); 
			dc.addParam(host.getIdentifier());
			try {
				dc.loadResult();
			} catch (DotDataException e) {
				Logger
						.error(this,
								"A reindex entry failed to insert in the table.",e);
				
			}
		}
	}	
	
	protected void refreshContentUnderFolder(Folder folder) throws DotDataException {

		String sql = "INSERT INTO dist_reindex_journal(inode_to_index,ident_to_index, serverid, priority,dist_action) " 
						+ " SELECT distinct contentlet.inode, identifier.inode, ?, ?, ? from contentlet, inode, identifier, folder " 
						+ " where contentlet.inode = inode.inode " 
						+ " and identifier.inode = inode.identifier " 
						+ " and contentlet.folder = folder.inode" 
						+ " and inode.identifier <> identifier.host_inode "
						+ " and identifier.host_inode = ? " 
						+ " and folder.path like ? "
						+ " and ( contentlet.working = " + DbConnectionFactory.getDBTrue() 
								+ " or contentlet.live = " + DbConnectionFactory.getDBTrue()
								+ " or contentlet.deleted = " + DbConnectionFactory.getDBTrue() + ")";
		
		DotConnect dc = new DotConnect();		
		for (String serversId : serversIds) {
			dc.setSQL(sql);
			dc.addParam(serversId);
			int priority = REINDEX_JOURNAL_PRIORITY_CONTENT_REINDEX;				
			dc.addParam(priority);
			dc.addParam(REINDEX_ACTION_REINDEX_OBJECT); 
			dc.addParam(folder.getHostId());
			dc.addParam(folder.getPath()+"%");
			try {
				dc.loadResult();
			} catch (DotDataException e) {
				Logger.error(this,"A reindex entry failed to insert in the table.",e);
			}
		}
	}	

}

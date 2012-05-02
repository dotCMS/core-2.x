/**
 *
 */
package com.dotmarketing.startup;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.common.util.SQLUtil;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
//import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

/**
 * @author Jason Tesser
 * @author Andres Olarte
 * @since 1.6.5a
 *
 */
public abstract class AbstractJDBCStartupTask implements StartupTask {

	/**
	 * By default statements should run in a single transaction.
	 * If you set to false every statement of the Script will be tokenized and
	 * executed within a separate transactions.
	 */
	protected boolean runInSingleTransaction = true;
	private Boolean rebuildIndices = true;
	private Boolean rebuildForeignKeys = true;
	private Boolean rebuildPrimaryKeys = true;

	public class PrimaryKey {
		String tableName;
		String keyName;
		List<String> columnNames;

	}

	public class ForeignKey {
		String PKTABLE_NAME;
		String PKCOLUMN_NAME;
		String FKTABLE_NAME;
		String FKCOLUMN_NAME;
		String FK_NAME;
		String INDEX_NAME;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ForeignKey) {
				ForeignKey k=(ForeignKey)obj;
				if (!k.PKTABLE_NAME.equalsIgnoreCase(PKTABLE_NAME)) {
					return false;
				}
				if (!k.PKCOLUMN_NAME.equalsIgnoreCase(PKCOLUMN_NAME)) {
					return false;
				}
				if (!k.FKTABLE_NAME.equalsIgnoreCase(FKTABLE_NAME)) {
					return false;
				}
				if (!k.FKCOLUMN_NAME.equalsIgnoreCase(FKCOLUMN_NAME)) {
					return false;
				}
				if (!k.FK_NAME.equalsIgnoreCase(FK_NAME)) {
					return false;
				}
				return true;
			}
			return false;
		}
	}

	public class Index {
		String tableName;
		String indexName;
		List<String> columnNames;
		Boolean unique; 
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Index) {
				Index i=(Index)obj;
				if (i.tableName.equalsIgnoreCase(tableName) && i.indexName.equalsIgnoreCase(indexName)) {
					return true;
				}
			}

			return false;
		}
		@Override
		public String toString() {
			return ((tableName!=null) ? tableName : "") + "."+  ((indexName!=null) ? indexName : "");
		}

	}



	/* (non-Javadoc)
	 * @see com.dotmarketing.startup.StartupTask#executeUpgrade()
	 */
	public void executeUpgrade() throws DotDataException, DotRuntimeException{
		DotConnect dc = new DotConnect();
		Connection conn = null;
		Connection originalCon =null;
		List<PrimaryKey> primaryKeys=null;
		List<ForeignKey> foreignKeys=null;
		List<Index> indexes=null;

		try{
			try {
				conn = DbConnectionFactory.getDataSource().getConnection();
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				throw new DotDataException(e.getMessage(), e);
			}

			List<String> tables = getTablesToDropConstraints();
			List<String> schemaList = new ArrayList<String>();
			primaryKeys=new ArrayList<PrimaryKey>();


			if(tables!=null){
				foreignKeys=getForeingKeys(conn, tables,true);
				primaryKeys=getPrimaryKey(conn, tables);
				indexes=getIndexes(conn, tables,true);
			}

			//Execute the SQL Script in accordance with the database type
			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)){
				schemaList = SQLUtil.tokenize(getPostgresScript());
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
				schemaList = SQLUtil.tokenize(getMySQLScript());
			}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){
				schemaList = SQLUtil.tokenize(getOracleScript());
			}else{
				schemaList = SQLUtil.tokenize(getMSSQLScript());
			}

			try {
				if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
					dc.executeStatement("SET storage_engine=INNODB", conn);
				}else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)){
					dc.executeStatement("SET TRANSACTION ISOLATION LEVEL READ COMMITTED;", conn);
				}
			} catch (SQLException e) {
				Logger.fatal(this, "Unable to execute SQL upgrade", e);
				throw new DotDataException(e.getMessage(), e);
			}

			if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {
				originalCon = conn;
				conn = DbConnectionFactory.getDataSource().getConnection();
			}

			for (String query : schemaList) {
				if(!runInSingleTransaction){
					try{
						HibernateUtil.startTransaction();
						dc.executeStatement(query);
					}catch (Exception e) {
						Logger.error(this, "Unable to execute query : " + query);
						HibernateUtil.rollbackTransaction();
						continue;
					}
					HibernateUtil.commitTransaction();
				}else{
					try{

						dc.executeStatement(query, conn);

					} catch (SQLException e) {
						Logger.fatal(this, "Unable to execute SQL upgrade", e);
						throw new DotDataException(e.getMessage(), e);
					}
				}
			}
			if (foreignKeys!=null && rebuildForeignKeys) {
				for (ForeignKey key:foreignKeys) {
					try {
						createConstraint(conn, key);
					} catch (SQLException e) {
						Logger.error(AbstractJDBCStartupTask.class,"SQLException: " +e.getMessage(),e);
					}
				}
			}


			if (indexes!=null && rebuildIndices) {
				for (Index index:indexes) {
					try {
						for (PrimaryKey pk:primaryKeys) {
							if(index.tableName.equalsIgnoreCase(pk.tableName) && index.indexName.equalsIgnoreCase(pk.keyName)) {
								continue;
							}
						}
						createIndex(conn, index);
					} catch (SQLException e) {
						Logger.error(AbstractJDBCStartupTask.class,"SQLException: " +e.getMessage(),e);
					}
				}
			}

			if (primaryKeys!=null && rebuildPrimaryKeys) {
				for (PrimaryKey key:primaryKeys) {
					try {
						createPrimaryKey(conn, key);
					} catch (SQLException e) {
						Logger.error(AbstractJDBCStartupTask.class,"SQLException: " +e.getMessage(),e);
					}
				}
			}

		} catch(SQLException e) {
			e.printStackTrace();
		} finally{
			if(originalCon != null){
				if(!DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {
					try {
						originalCon.commit();
					} catch (SQLException e) {
						Logger.error(AbstractJDBCStartupTask.class,e.getMessage(),e);
					}
				}
				try {
					originalCon.close();
				} catch (SQLException e) {
					Logger.error(AbstractJDBCStartupTask.class,e.getMessage(),e);
				}
			}
			if(conn != null){
				if(!DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {
					try {
						conn.commit();
					} catch (SQLException e) {
						Logger.error(AbstractJDBCStartupTask.class,e.getMessage(),e);
					}
				}
				try {
					conn.close();
				} catch (SQLException e) {
					Logger.error(AbstractJDBCStartupTask.class,e.getMessage(),e);
				}
			}
		}
	}

	protected List<String> getTablesToDropPrimaryKeys() {
		return null;
	}
	
	protected void executeDropIndex(Connection conn, String tableName, String constraintName) throws SQLException{		
		
		conn = DbConnectionFactory.getDataSource().getConnection();
		
		PreparedStatement preparedStatement = null;
		String sql="";
		if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE) || DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL)) {
			sql="drop index " + constraintName; 
		} else if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL)) {
			sql="drop index " + tableName + "." + constraintName; 
		} else {
			sql="ALTER TABLE " + tableName + " DROP INDEX " + constraintName;
		}
				
		preparedStatement = conn.prepareStatement(sql);
		Logger.info(this, "Executing : "+sql);
		preparedStatement.execute();
		preparedStatement.close();
		
		conn.close();
	}

	protected void executeDropConstraint(Connection conn, String tableName, String constraintName) throws SQLException{

		String sql="";

		conn = DbConnectionFactory.getDataSource().getConnection();

		 if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)) {
			 if(constraintName.indexOf("PRIMARY")>-1) {
				 sql="ALTER TABLE " + tableName + " DROP PRIMARY KEY ";
			 } else {
				 sql="ALTER TABLE " + tableName + " DROP INDEX " + constraintName;
			 }
		 }  else {
			 sql="ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName;
		 }

		PreparedStatement preparedStatement = conn.prepareStatement(sql);
		Logger.info(this, "Executing : " + sql);
		preparedStatement.execute();
		preparedStatement.close();

		conn.close();
	}

	protected void executeDropForeignKeyMySql(Connection conn, String tableName, String constraintName) throws SQLException{
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("ALTER TABLE " + tableName + " DROP FOREIGN KEY " + constraintName);
			Logger.info(this, "Executing : " + "ALTER TABLE " + tableName + " DROP FOREIGN KEY " + constraintName);
			preparedStatement.execute();
			preparedStatement.close();
		} catch (Exception e) {
			Logger.info(this, "Errot executing : " + "ALTER TABLE " + tableName + " DROP FOREIGN KEY " + constraintName + " - NOT A FOREIGN KEY.");
		}
	}

	protected String getColumnList(List<String> columns) {
		StringBuilder b=new StringBuilder();
		boolean first=true;
		for (String column:columns) {
			if (!first) {
				b.append(",");
			} else {
				first =false;
			}
			b.append(column);
		}
		return b.toString();
	}

	protected void createPrimaryKey(Connection conn,PrimaryKey key) throws SQLException {
		String sql="ALTER TABLE "+key.tableName+" add CONSTRAINT "+key.tableName+"_PK PRIMARY KEY ("+getColumnList(key.columnNames)+")";
		Logger.info(this, "Executing : " +sql);
		PreparedStatement stmt=conn.prepareStatement(sql);
		stmt.execute();
		stmt.close();
	}

	protected void createIndex(Connection conn,Index index) throws SQLException {
		String sql="CREATE UNIQUE INDEX "+index.indexName+" ON "+index.tableName+"_PK ("+getColumnList(index.columnNames)+")";
		PreparedStatement stmt=conn.prepareStatement(sql);
		stmt.execute();
		stmt.close();
	}

	protected List<ForeignKey> getForeingKeys(Connection conn,List<String> tables, boolean executeDrop) {
		List<ForeignKey> ret=new ArrayList<ForeignKey>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();

			for (String t : tables) {
				String schema = null;
				if (DbConnectionFactory.getDBType().equals(
						DbConnectionFactory.ORACLE)) {
					t = t.toUpperCase();
					schema = dbmd.getUserName();
				}

				ResultSet rs = dbmd.getImportedKeys(conn.getCatalog(), schema,
						t);

				// Iterates over the foreign key columns
				while (rs.next()) {
					ForeignKey key=new ForeignKey();
					key.PKTABLE_NAME=rs.getString("PKTABLE_NAME");
					key.PKCOLUMN_NAME=rs.getString("PKCOLUMN_NAME");
					key.FKTABLE_NAME=rs.getString("FKTABLE_NAME");
					key.FKCOLUMN_NAME=rs.getString("FKCOLUMN_NAME");
					key.FK_NAME=rs.getString("FK_NAME");
					if (!ret.contains(key)) {
						ret.add(key);
					}

				}
			}
			if (executeDrop) {
				for (ForeignKey key:ret) {
					if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.POSTGRESQL) ||
							DbConnectionFactory.getDBType().equals(DbConnectionFactory.MSSQL) ||
							DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){

							 executeDropConstraint(conn, key.FKTABLE_NAME, key.FK_NAME);

						} else if (DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)) {
							executeDropForeignKeyMySql(conn, key.FKTABLE_NAME, key.FK_NAME);

						}
				}
			}
		} catch (SQLException e) {
			Logger.error(this, "SQLException: " + e.getMessage(),e);
		}
		return ret;

	}

	protected List<Index> getIndexes(Connection conn,List<String> tables, boolean executeDrop) {
		List<Index> ret=new ArrayList<Index>();
		try {
			DatabaseMetaData dbmd = conn.getMetaData();

			for (String t : tables) {
				String schema = null;

				if (DbConnectionFactory.getDBType().equals(
						DbConnectionFactory.ORACLE)) {
					t = t.toUpperCase();
					schema = dbmd.getUserName();
				}

				ResultSet idxrs = dbmd.getIndexInfo(conn.getCatalog(), schema, t, false, false);
				Index i=null;
				String indexName = "";

				while (idxrs.next()) {
					if(indexName!=null && indexName.equals(idxrs.getString("INDEX_NAME"))) {
						i.columnNames.add(idxrs.getString("COLUMN_NAME"));
					} else {
						indexName = idxrs.getString("INDEX_NAME");
						i = new Index();
						i.indexName=indexName;
						i.tableName=t;
						i.columnNames=new ArrayList<String>();
						i.columnNames.add(idxrs.getString("COLUMN_NAME"));
						i.unique = !(idxrs.getBoolean("NON_UNIQUE"));
						
						if(UtilMethods.isSet(indexName)) {
							ret.add(i);
						}
					}
				}
				
			}

			if(executeDrop) {
				for (Index index:ret) {
					if(index.unique) {
						executeDropConstraint(conn, index.tableName, index.indexName); 
					} else {
						executeDropIndex(conn, index.tableName, index.indexName);
					}
				}
			}
		} catch (SQLException e) {
			Logger.error(this, "SQLException: " + e.getMessage(), e);
		}

		return ret;

	}

	protected List<PrimaryKey> getPrimaryKey(Connection conn,List<String> tablesWithKeys) {
		List<PrimaryKey> ret=new ArrayList<PrimaryKey>();
		if (tablesWithKeys!=null) {
			try {
			for (String t:tablesWithKeys) {
					DatabaseMetaData dbmd=conn.getMetaData();


					String schema=null;
					if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.ORACLE)){
						t = t.toUpperCase();
						schema=dbmd.getUserName();
					}
					ResultSet rs=dbmd.getPrimaryKeys(conn.getCatalog(), schema, t);
					PrimaryKey key=null;
					while (rs.next()) {
						if (key==null) {
							key=new PrimaryKey();
							key.keyName=rs.getString("PK_NAME");
							key.tableName=t;
							key.columnNames=new ArrayList<String>();
						}
						key.columnNames.add(rs.getString("COLUMN_NAME"));

					}
					ret.add(key);

			}

			} catch (SQLException e) {
				Logger.error(AbstractJDBCStartupTask.class,"SQLException: " +e.getMessage(),e);
			}

		}
		return ret;
	}

	protected void createConstraint(Connection conn, ForeignKey key) throws SQLException{
		String sql="ALTER TABLE " + key.FKTABLE_NAME + " ADD CONSTRAINT " + key.FK_NAME + " FOREIGN KEY(" + key.FKCOLUMN_NAME + ") REFERENCES " +key.PKTABLE_NAME+ "(" +key.PKCOLUMN_NAME + ")";
		PreparedStatement preparedStatement = conn.prepareStatement(sql );
		Logger.info(this, "Executing : " +sql );
		preparedStatement.execute();
		preparedStatement.close();
	}

	protected void setRebuildPrimaryKeys(Boolean rebuildPrimaryKeys) {
		this.rebuildPrimaryKeys = rebuildPrimaryKeys;
	}

	protected void setRebuildIndices(Boolean rebuildIndices) {
		this.rebuildIndices = rebuildIndices;
	}

	protected void setRebuildForeignKeys(Boolean rebuildForeignKeys) {
		this.rebuildForeignKeys = rebuildForeignKeys;
	}

	/**
	 * The SQL for Postgres
	 * @return
	 */
	abstract public String getPostgresScript();

	/**
	 * The SQL MySQL
	 * @return
	 */
	abstract public String getMySQLScript();

	/**
	 * The SQL for Oracle
	 * @return
	 */
	abstract public String getOracleScript();

	/**
	 * The SQL for MSSQL
	 * @return
	 */
	abstract public String getMSSQLScript();

	/**
	 * This is a list of tables which will get the constraints dropped prior to the task executing and then get recreated afer the execution of the DB Specific SQL
	 * @return
	 * @throws DotDataException
	 */
	abstract protected List<String> getTablesToDropConstraints();

}

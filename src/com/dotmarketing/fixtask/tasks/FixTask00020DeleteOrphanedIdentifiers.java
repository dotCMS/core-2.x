/**
 * 
 */
package com.dotmarketing.fixtask.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.fixtask.FixTask;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author jasontesser
 *
 */
public class FixTask00020DeleteOrphanedIdentifiers implements FixTask{

	private List <Map<String, String>>  modifiedData= new  ArrayList <Map<String,String>>();
	
	public List<Map<String, Object>> executeFix() throws DotDataException,
			DotRuntimeException {
		String inodesToDelete = "SELECT * from inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier))";
		String treesToDelete = "SELECT * from tree where child IN (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR parent IN (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier))";
		
		String permissionsToDelete = "DELETE FROM permission where inode_id in (select inode from inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier)))";
		String permissionRefsToDelete = "DELETE FROM permission_reference where asset_id in (select inode from inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier)))";
		String deleteTreesToDelete = "DELETE FROM tree where child IN (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR parent IN (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier))";
		String deleteInodesToDelete = "DELETE FROM inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (SELECT inode from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier))";
		
		DotConnect dc = new DotConnect();
		dc.setSQL(treesToDelete);
		modifiedData = dc.loadResults();
		dc.setSQL(inodesToDelete);
		modifiedData.addAll(dc.loadResults());
		if(modifiedData.size()>0){
			try{
				HibernateUtil.startTransaction();
				dc.executeStatement(deleteTreesToDelete);
				if(DbConnectionFactory.getDBType().equals(DbConnectionFactory.MYSQL)){
					deleteInodesInMySQL(dc);
				}else{
					dc.executeStatement(permissionsToDelete);
					dc.executeStatement(permissionRefsToDelete);
				    dc.executeStatement(deleteInodesToDelete);
				}
				HibernateUtil.commitTransaction();
			}catch (Exception e) {
				Logger.error(this, "Unable to clean orphaned identifiers",e);
				HibernateUtil.rollbackTransaction();
				modifiedData.clear();
			}
		}
		return (List)modifiedData;
	}

	public List<Map<String, String>> getModifiedData() {
		if (modifiedData.size() > 0) {
			XStream _xstream = new XStream(new DomDriver());
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
			String lastmoddate = sdf.format(date);
			File _writing = null;

			if (!new File(ConfigUtils.getBackupPath()+File.separator+"fixes").exists()) {
				new File(ConfigUtils.getBackupPath()+File.separator+"fixes").mkdir();
			}
			_writing = new File(ConfigUtils.getBackupPath()+File.separator+"fixes" + java.io.File.separator + lastmoddate + "_"
					+ "FixTask00020DeleteOrphanedIdentifiers" + ".xml");

			BufferedOutputStream _bout = null;
			try {
				_bout = new BufferedOutputStream(new FileOutputStream(_writing));
			} catch (FileNotFoundException e) {

			}
			_xstream.toXML(modifiedData, _bout);
		}
		return modifiedData;
	}
	private void deleteInodesInMySQL(DotConnect dc)throws DotDataException,
				DotRuntimeException {
		int count = 0;
		try {
			dc.setSQL("SELECT count(*) size from inode where type = 'identifier' and inode not in (SELECT inode FROM identifier)");
			List<HashMap<String, String>> rs = dc.loadResults();
			int size = Integer.parseInt(rs.get(0).get("size"));
			if(size > 500)
			  count = (int)Math.ceil(size/500.00);
			else
			   count=1;
			for(int i=0;i<count;i++){
				
				dc.setSQL("SELECT inode from inode where type = 'identifier' and " 
						+ " inode not in (SELECT inode FROM identifier) order by inode"
						+ " limit 500 offset " + i*500);
				
			    List<HashMap<String, String>> identifiers = dc.loadResults();
				StringBuilder identCondition = new StringBuilder(128);
			    identCondition.ensureCapacity(32);
				identCondition.append("");

				for (HashMap<String, String> inode : identifiers) {
					if (0 < identCondition.length())
						identCondition.append(",'" + inode.get("inode")+"'");
					else
						identCondition.append("'"+inode.get("inode")+"'");
				}
				if(identCondition.length()>0){
				  dc.executeStatement("DELETE FROM permission where inode_id in(select inode from inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (" +identCondition + "))");
				  dc.executeStatement("DELETE FROM permission_reference where asset_id in(select inode from inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) OR identifier in (" +identCondition + "))");
				  dc.executeStatement("DELETE FROM inode where (type = 'identifier' and inode not in (SELECT inode FROM identifier)) "
							        + "OR identifier in (" +identCondition + ")");
				}
			}
		} catch (Exception e) {
			Logger.error(this, "Unable to clean orphaned identifiers",e);
			HibernateUtil.rollbackTransaction();
			modifiedData.clear();
		} 
		
	}
	public boolean shouldRun() {
		return true;
	}

}

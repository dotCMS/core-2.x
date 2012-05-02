/**
 * 
 */
package com.dotmarketing.fixtask.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dotmarketing.common.db.DotConnect;
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
public class FixTask00030DeleteOrphanedAssets implements FixTask {
	
	private List<Map<String, String>> modifiedData = new ArrayList<Map<String,String>>();
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeFix() throws DotDataException, DotRuntimeException {
		String identifiersToDelete = "SELECT * " +
									 "FROM identifier " +
									 "WHERE host_inode NOT IN (SELECT DISTINCT identifier " +
															  "FROM inode " +
															  "WHERE inode IN (SELECT inode " +
																			  "FROM contentlet " +
																			  "WHERE structure_inode = (SELECT inode " +
																									   "FROM structure " +
																									   "WHERE velocity_var_name='Host')))";
		
		String inodesToDelete = "SELECT * FROM inode WHERE identifier = ?";
		
		String htmlPagesToDelete = "SELECT * FROM htmlpage WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String linksToDelete = "SELECT * FROM links WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String fileAssetsToDelete = "SELECT * FROM file_asset WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		
		String deleteTrees = "DELETE FROM tree WHERE child IN (SELECT inode FROM inode WHERE identifier = ?) OR parent IN (SELECT inode FROM inode WHERE identifier = ?)";
		
		String deleteInodes = "DELETE FROM inode WHERE identifier = ?";
		
		String deleteIdentifier = "DELETE FROM identifier WHERE inode = ?";
		
		String deleteHTMLPages = "DELETE FROM htmlpage WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String deleteLinks = "DELETE FROM links WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String deleteFileAssets = "DELETE FROM file_asset WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		
		DotConnect dc = new DotConnect();
		dc.setSQL(identifiersToDelete);
		List<Map<String, String>> identifiersToDeleteResult = dc.loadResults();
		
		if ((identifiersToDeleteResult != null) && (0 < identifiersToDeleteResult.size())) {
			try {
				modifiedData = identifiersToDeleteResult;
				
				List<Map<String, String>> inodesToDeleteResult;
				List<Map<String, String>> assetsToDeleteResult;
				Map<String, String> identifierToDelete;
				boolean assetDeleted;
				
				HibernateUtil.startTransaction();
				
				for (int i = 0; i < identifiersToDeleteResult.size(); ++i) {
					identifierToDelete = identifiersToDeleteResult.get(i);
					assetDeleted = false;
					dc.setSQL(inodesToDelete);
					dc.addParam(identifierToDelete.get("inode"));
					inodesToDeleteResult = dc.loadResults();
					
					if ((inodesToDeleteResult != null) && (0 < inodesToDeleteResult.size())) {
						modifiedData.addAll(inodesToDeleteResult);
						
						if (inodesToDeleteResult.get(0).get("type").equals("htmlpage")) {
							Logger.debug(this, "Deleting orphan HTMLPage with Identifier='" + identifierToDelete.get("inode") + "'");
							
							dc.setSQL(htmlPagesToDelete);
							dc.addParam(identifierToDelete.get("inode"));
							assetsToDeleteResult = dc.loadResults();
							
							if ((assetsToDeleteResult != null) && (0 < assetsToDeleteResult.size())) {
								modifiedData.addAll(assetsToDeleteResult);
								
								dc.setSQL(deleteHTMLPages);
								dc.addParam(identifierToDelete.get("inode"));
								dc.loadResult();
								
								assetDeleted = true;
							}
						} else if (inodesToDeleteResult.get(0).get("type").equals("links")) {
							Logger.debug(this, "Deleting orphan Link with Identifier='" + identifierToDelete.get("inode") + "'");
							
							dc.setSQL(linksToDelete);
							dc.addParam(identifierToDelete.get("inode"));
							assetsToDeleteResult = dc.loadResults();
							
							if ((assetsToDeleteResult != null) && (0 < assetsToDeleteResult.size())) {
								modifiedData.addAll(assetsToDeleteResult);
								
								dc.setSQL(deleteLinks);
								dc.addParam(identifierToDelete.get("inode"));
								dc.loadResult();
								
								assetDeleted = true;
							}
						} else if (inodesToDeleteResult.get(0).get("type").equals("file_asset")) {
							Logger.debug(this, "Deleting orphan File Asset with Identifier='" + identifierToDelete.get("inode") + "'");
							
							dc.setSQL(fileAssetsToDelete);
							dc.addParam(identifierToDelete.get("inode"));
							assetsToDeleteResult = dc.loadResults();
							
							if ((assetsToDeleteResult != null) && (0 < assetsToDeleteResult.size())) {
								modifiedData.addAll(assetsToDeleteResult);
								
								dc.setSQL(deleteFileAssets);
								dc.addParam(identifierToDelete.get("inode"));
								dc.loadResult();
								
								assetDeleted = true;
							}
						}
						
						if (assetDeleted) {
							dc.setSQL(deleteTrees);
							dc.addParam(identifierToDelete.get("inode"));
							dc.addParam(identifierToDelete.get("inode"));
							dc.loadResult();
							
							dc.setSQL(deleteInodes);
							dc.addParam(identifierToDelete.get("inode"));
							dc.loadResult();
						}
					}
					
					if (assetDeleted) {
						dc.setSQL(deleteIdentifier);
						dc.addParam(identifierToDelete.get("inode"));
						dc.loadResult();
						
						Logger.debug(this, "Delete completed");
					}
				}
				
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				Logger.error(this, "Unable to clean orphaned assets", e);
				HibernateUtil.rollbackTransaction();
				modifiedData.clear();
			}
		}
		
		return (List) modifiedData;
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
					+ "FixTask00030DeleteOrphanedAssets" + ".xml");

			BufferedOutputStream _bout = null;
			try {
				_bout = new BufferedOutputStream(new FileOutputStream(_writing));
			} catch (FileNotFoundException e) {

			}
			_xstream.toXML(modifiedData, _bout);
		}
		return modifiedData;
	}
	
	public boolean shouldRun() {
		return true;
	}
}
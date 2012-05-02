package com.dotmarketing.fixtask.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.fixtask.FixTask;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FixTask00021CheckOrphanedAssets implements FixTask {
	
	private List <Map<String, String>> modifiedData= new  ArrayList <Map<String, String>>();


	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<Map<String, Object>> executeFix() throws DotDataException,
			DotRuntimeException {
		DotConnect dc = new DotConnect();
		Host host = null;
		String hostId = "";
		Folder folder = null;
		FolderAPI folderAPI = APILocator.getFolderAPI();
		try {
				User user = APILocator.getUserAPI().getSystemUser();
				String query = "SELECT * FROM inode WHERE (type='file_asset' or type='htmlpage') " +
				   			   "and inode NOT IN (SELECT child FROM tree WHERE parent in (SELECT inode from folder))";
				dc.setSQL(query);
				List<HashMap<String, String>> assetIds = dc.getResults();
				for(HashMap<String, String> asset:assetIds){
					String identifier = asset.get("identifier");
					if(APILocator.getIdentifierAPI().isIdentifier(identifier)){
							Identifier ident = (Identifier) InodeFactory.getInode(identifier, Identifier.class);
							hostId = ident.getHostId();
							if(hostId == null){
								host = APILocator.getHostAPI().findDefaultHost(user,false);
								hostId = host.getInode();
							}
							String uri = ident.getURI();
							if(UtilMethods.isSet(uri)){
								int index = uri.lastIndexOf("/");
								
								if (-1 < index);
									uri = uri.substring(0, index);
							}
							folder = FolderFactory.getFolderByPath(uri, hostId);
							HibernateUtil.startTransaction();
							if(folderAPI.doesFolderExist(folder.getPath(),hostId)){
						      dc.setSQL("Insert into tree(child,parent,relation_type,tree_order) values(?,?,?,?)");
							  dc.addParam(asset.get("inode"));
							  dc.addParam(folder.getInode());
							  dc.addParam("child");
							  dc.addParam(0);
							  dc.loadResult();
							  dc.setSQL("select * from tree where child = ? and parent = ?");
							  dc.addParam(asset.get("inode"));
							  dc.addParam(folder.getInode());
							  modifiedData.addAll(dc.loadResults());
							}else{
							  deleteOrphanedAsset(asset.get("type"),asset.get("identifier"),dc);	
							  FileCache.clearCache();
							  CacheLocator.getHTMLPageCache().clearCache();
							  IdentifierCache.clearCache();
							}	
						}
				}	
				HibernateUtil.commitTransaction();
			} catch (Exception e) {
				Logger.error(this, "Unable to execute CheckOrphanedAssets Task",e);
				HibernateUtil.rollbackTransaction();
				modifiedData.clear();
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
			_writing = new File(ConfigUtils.getBackupPath()+File.separator+"fixes" + java.io.File.separator  + lastmoddate + "_"
					+ "FixTask00021CheckOrphanedAssets" + ".xml");

			BufferedOutputStream _bout = null;
			try {
				_bout = new BufferedOutputStream(new FileOutputStream(_writing));
			} catch (FileNotFoundException e) {

			}
			_xstream.toXML(modifiedData, _bout);
		}
		return modifiedData;
	}
	
	@SuppressWarnings("unchecked")
	private void deleteOrphanedAsset(String type,String identifier,DotConnect dc){
		String deleteInodes = "DELETE FROM inode WHERE identifier = ?";
		String deleteIdentifier = "DELETE FROM identifier WHERE inode = ?";
		String deleteHTMLPages = "DELETE FROM htmlpage WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String deleteFileAssets = "DELETE FROM file_asset WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String InodesToDelete = "SELECT * FROM inode WHERE identifier = ?";
		String IdentifierToDelete = "SELECT * FROM identifier WHERE inode = ?";
		String HTMLPagesToDelete = "SELECT * FROM htmlpage WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		String FileAssetsToDelete = "SELECT * FROM file_asset WHERE inode IN (SELECT inode FROM inode WHERE identifier = ?)";
		try {
			 dc.setSQL("DELETE FROM tree WHERE child IN (SELECT inode FROM inode WHERE identifier = ?)");
			 dc.addParam(identifier);
			 dc.loadResult();
			 dc.setSQL(InodesToDelete);
			 dc.addParam(identifier);
			 modifiedData = dc.loadResults();
			 dc.setSQL(IdentifierToDelete);
			 dc.addParam(identifier);
			 modifiedData = dc.loadResults();
			if(type.equalsIgnoreCase("file_asset")){
				dc.setSQL(FileAssetsToDelete);
			    dc.addParam(identifier);
			    ArrayList<HashMap<String, String>> assetList = dc.loadResults();
			    if(assetList.size()> 0){
			    	 modifiedData = dc.loadResults();
			    }else{
			    	dc.setSQL("SELECT * from identifier where inode in(select inode from inode where type='identifier') and inode = ?");
			    	dc.addParam(identifier);
			    	modifiedData.addAll(dc.loadResults());
			    }
			    if(assetList.size()>0){
			      //DELETE Orphaned File Assets	
			      dc.setSQL(deleteFileAssets);	
			      dc.addParam(identifier);
			      dc.loadResult();
			      dc.setSQL(deleteIdentifier);
			      dc.addParam(identifier);
			      dc.loadResult();
			      dc.setSQL(deleteInodes);
			      dc.addParam(identifier);
			      dc.loadResult();
			    }else{
			    	dc.setSQL("DELETE from identifier where inode in(select inode from inode where type='identifier') and inode = ?");
			    	dc.addParam(identifier);
			    	dc.loadResult();
			    }
			 }else{
				 dc.setSQL(HTMLPagesToDelete);
				 dc.addParam(identifier);
				 modifiedData = dc.loadResults();
				 //DELETE Orphaned HTMLPage Assets
				 dc.setSQL(deleteHTMLPages);	
			      dc.addParam(identifier);
			      dc.loadResult();
			      dc.setSQL(deleteIdentifier);
			      dc.addParam(identifier);
			      dc.loadResult();
			      dc.setSQL(deleteInodes);
			      dc.addParam(identifier);
			      dc.loadResult();
			 }		
		} catch (Exception e) {
			throw new DotRuntimeException("Unable to delete Orphaned Asset",e);
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean shouldRun() {
		DotConnect dc = new DotConnect();
		String query = "SELECT * FROM inode WHERE (type='file_asset' or type='htmlpage') " +
				       "and inode NOT IN (SELECT child FROM tree WHERE parent in (SELECT inode from folder))";

		dc.setSQL(query);
		List<HashMap<String, String>> assetIds = dc.getResults();
		int total = assetIds.size();
		if (total > 0)
			return true;
		else
			return false;
	}
}

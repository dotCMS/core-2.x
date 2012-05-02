package com.dotmarketing.portlets.files.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.Permissionable;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.cache.WorkingCache;
import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.business.FolderAPI;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.PaginatedArrayList;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class FileFactoryImpl implements FileFactory {
	
	protected FileCache fileCache;
	protected FolderAPI folderAPI;
	
	public FileFactoryImpl () {
		fileCache = CacheLocator.getFileCache();
		folderAPI = APILocator.getFolderAPI();
	}
	
	public void save(File file) throws DotDataException {
		HibernateUtil.save(file);
		fileCache.add(file.getInode(), file);
		WorkingCache.removeAssetFromCache(file);
		WorkingCache.addToWorkingAssetToCache(file);
		LiveCache.removeAssetFromCache(file);
		if (file.isLive()) {
			LiveCache.addToLiveAssetToCache(file);
		}
	}
	
	public void delete(File file) throws DotDataException {
		HibernateUtil.delete(file);
		fileCache.remove(file.getInode());
		WorkingCache.removeAssetFromCache(file);
		if (file.isLive()) {
			LiveCache.removeAssetFromCache(file);
		}
		IdentifierCache.removeAssetFromIdCache(file);
	}
	
	public void deleteFromCache(File file) throws DotDataException {
		fileCache.remove(file.getInode());
		WorkingCache.removeAssetFromCache(file);
		if (file.isLive()) {
			LiveCache.removeAssetFromCache(file);
		}
		IdentifierCache.removeAssetFromIdCache(file);
	}

	@SuppressWarnings("unchecked")
	public List<File> getFolderFiles(Folder folder, boolean live) throws DotDataException {
		HibernateUtil hu = new HibernateUtil(File.class);
		StringBuilder queryBuilder = new StringBuilder("select {file_asset.*} from file_asset, inode file_asset_1_, tree where " +
				"file_asset.inode = file_asset_1_.inode and " +
				"tree.parent = ? and tree.child = file_asset.inode");
		if(live)
			queryBuilder.append(" and file_asset.live = ?");
		else
			queryBuilder.append(" and file_asset.working = ?");
		
		hu.setSQLQuery(queryBuilder.toString());
		hu.setParam(folder.getInode());
		hu.setParam(true);
		
		return hu.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<File> getAllHostFiles(Host host, boolean live) throws DotDataException {
		HibernateUtil hu = new HibernateUtil(File.class);
		StringBuilder queryBuilder = new StringBuilder("select {file_asset.*} from file_asset, inode file_asset_1_, tree, folder where " +
				"file_asset.inode = file_asset_1_.inode and " +
				"tree.parent = folder.inode and tree.child = file_asset.inode and " +
				"folder.host_inode = ?");
		if(live)
			queryBuilder.append(" and file_asset.live = ?");
		else
			queryBuilder.append(" and file_asset.working = ?");
		
		hu.setSQLQuery(queryBuilder.toString());
		hu.setParam(host.getIdentifier());
		hu.setParam(true);
		
		return hu.list();
	}
	
    @SuppressWarnings("unchecked")
	public File getWorkingFileById(String identifier) throws DotDataException {
    	HibernateUtil hu = new HibernateUtil(File.class);
    	hu.setSQLQuery("select {file_asset.*} from file_asset, inode file_asset_1_ where file_asset_1_.identifier = ? and file_asset.working = ? " +
    			"and file_asset_1_.inode = file_asset.inode");
    	hu.setParam(identifier);
    	hu.setParam(true);
    	List<File> files = hu.list();
    	if(files.size() == 0)
    		return null;
    	return files.get(0);
    }	
	
	public File get(String inode) throws DotHibernateException {
		File file = fileCache.get(inode);
		
		if ((file == null) || !InodeUtils.isSet(file.getInode())) {
			file = (File) HibernateUtil.load(File.class, inode);
			
			fileCache.add(file.getInode(), file);
			WorkingCache.removeAssetFromCache(file);
			WorkingCache.addToWorkingAssetToCache(file);
			LiveCache.removeAssetFromCache(file);
			if (file.isLive()) {
				LiveCache.addToLiveAssetToCache(file);
			}
		}
		
		return file;
	}

	public Folder getFileFolder(File file) throws DotDataException {
    	DotConnect dc = new DotConnect();
    	dc.setSQL("select folder.inode as folderId from tree, folder where tree.child = ? and tree.parent = folder.inode");
    	dc.addObject(file.getInode());
    	String folderId = dc.getString("folderId");

		return folderAPI.find(folderId);
	}

	public List<File> findFiles(User user, boolean includeArchived,
			Map<String, Object> params, String hostId, String inode, String identifier, String parent,
			int offset, int limit, String orderBy) throws DotSecurityException,
			DotDataException {

		PaginatedArrayList<File> assets = new PaginatedArrayList<File>();
		List<Permissionable> toReturn = new ArrayList<Permissionable>();
		int internalLimit = 500;
		int internalOffset = 0;
		boolean done = false;

		StringBuffer conditionBuffer = new StringBuffer();
		String condition = !includeArchived?" asset.working = " + DbConnectionFactory.getDBTrue() + " and asset.deleted = " +DbConnectionFactory.getDBFalse():
			" asset.working = " + DbConnectionFactory.getDBTrue();
		conditionBuffer.append(condition);

		List<Object> paramValues =null;
		if(params!=null && params.size()>0){
			conditionBuffer.append(" and (");
			paramValues = new ArrayList<Object>();
			int counter = 0;
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if(counter==0){
					if(entry.getValue() instanceof String){
						if(entry.getKey().equalsIgnoreCase("inode")){
							conditionBuffer.append(" asset." + entry.getKey()+ " = '" + entry.getValue() + "'");
						}else{
							conditionBuffer.append(" lower(asset." + entry.getKey()+ ") like ? ");
							paramValues.add("%"+ ((String)entry.getValue()).toLowerCase()+"%");
						}
					}else{
						conditionBuffer.append(" asset." + entry.getKey()+ " = " + entry.getValue());
					}	
				}else{
					if(entry.getValue() instanceof String){
						if(entry.getKey().equalsIgnoreCase("inode")){
							conditionBuffer.append(" OR asset." + entry.getKey()+ " = '" + entry.getValue() + "'");
						}else{
							conditionBuffer.append(" OR lower(asset." + entry.getKey()+ ") like ? ");
							paramValues.add("%"+ ((String)entry.getValue()).toLowerCase()+"%");
						}
					}else{
						conditionBuffer.append(" OR asset." + entry.getKey()+ " = " + entry.getValue());
					}	
				}

				counter+=1;
			}
			conditionBuffer.append(" ) ");
		}

		StringBuffer query = new StringBuffer();
		query.append("select asset from asset in class " + File.class.getName() + ", " +
				"inode in class " + Inode.class.getName()+", ident in class " + Identifier.class.getName());
		if(UtilMethods.isSet(parent)){
			query.append(" ,tree in class " + Tree.class.getName() + " where asset.inode=inode.inode " +
					"and inode.identifier = ident.inode and tree.parent = '"+parent+"' and tree.child=asset.inode");

		}else{
			query.append(" where asset.inode=inode.inode and inode.identifier = ident.inode");
		}
		if(UtilMethods.isSet(hostId)){	
			query.append(" and ident.hostId = '"+ hostId +"'");
		}
		if(UtilMethods.isSet(inode)){	
			query.append(" and asset.inode = '"+ inode +"'");
		}
		if(UtilMethods.isSet(identifier)){	
			query.append(" and inode.identifier = '"+ identifier +"'");
		}
		if(!UtilMethods.isSet(orderBy)){
			orderBy = "modDate desc";
		}

		List<File> resultList = new ArrayList<File>();
		DotHibernate dh = new DotHibernate(File.class);
		String type;
		int countLimit = 100;
		int size = 0;
		try {
			type = ((Inode) File.class.newInstance()).getType();
			query.append(" and asset.type='"+type+ "' and " + conditionBuffer.toString() + " order by asset." + orderBy);
			dh.setQuery(query.toString());

			if(paramValues!=null && paramValues.size()>0){
				for (Object value : paramValues) {
					dh.setParam((String)value);
				}			
			}

			while(!done) { 
				dh.setFirstResult(internalOffset);
				dh.setMaxResults(internalLimit);		
				resultList = dh.list();
				PermissionAPI permAPI = APILocator.getPermissionAPI();
				toReturn.addAll(permAPI.filterCollection(resultList, PermissionAPI.PERMISSION_READ, false, user));
				if(countLimit > 0 && toReturn.size() >= countLimit + offset)
					done = true;
				else if(resultList.size() < internalLimit)
					done = true;

				internalOffset += internalLimit;
			}

			if(offset > toReturn.size()) {
				size = 0;
			} else if(countLimit > 0) {
				int toIndex = offset + countLimit > toReturn.size()?toReturn.size():offset + countLimit;
				size = toReturn.subList(offset, toIndex).size();
			} else if (offset > 0) {
				size = toReturn.subList(offset, toReturn.size()).size();
			}
			assets.setTotalResults(size);
			int from = offset<toReturn.size()?offset:0;
			int pageLimit = 0;
			for(int i=from;i<toReturn.size();i++){
				if(pageLimit<limit){
					assets.add((File) toReturn.get(i));
					pageLimit+=1;
				}else{
					break;
				}

			}

		} catch (Exception e) {

			Logger.error(FileFactoryImpl.class, "findFiles failed:" + e, e);
			throw new DotRuntimeException(e.toString());
		}

		return assets;
	}
}
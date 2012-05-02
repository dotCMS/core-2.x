package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.UtilMethods;

public class PictureGalleryWebAPI implements ViewTool {



    public void init(Object obj) {


    }

	//Pictures Gallery Methods
	public List<Hashtable<String, Object>> getPictureGalleryIndexImages (String indexFolderPath, Host host) throws DotDataException, DotSecurityException {
	    return getPictureGalleryIndexImages (indexFolderPath, host.getIdentifier());
	}
    @Deprecated
	public List<Hashtable<String, Object>> getPictureGalleryIndexImages (String indexFolderPath, long hostId) throws DotDataException, DotSecurityException {
		return getPictureGalleryIndexImages (indexFolderPath, String.valueOf(hostId));
	}
	
	public List<Hashtable<String, Object>> getPictureGalleryIndexImages (String indexFolderPath, String hostId) throws DotDataException, DotSecurityException {
		ArrayList<Hashtable<String, Object>> indexImages = new ArrayList<Hashtable<String, Object>> ();
		indexFolderPath = indexFolderPath.trim().endsWith("/")?indexFolderPath.trim():indexFolderPath.trim() + "/";
		Folder folder = FolderFactory.getFolderByPath(indexFolderPath, hostId);
		Host host = APILocator.getHostAPI().find(hostId, APILocator.getUserAPI().getSystemUser(), false);
		List<Folder> subFolders = FolderFactory.getFoldersByParent(folder.getInode());
		Iterator<Folder> sFoldersIterator = subFolders.iterator();
		while (sFoldersIterator.hasNext()) {
			Folder subFolder = (Folder) sFoldersIterator.next();
            List<File> imagesList = getPictureGalleryFolderImages(subFolder.getPath(), host);
            if (imagesList.size() > 0) {
                File indexImg = null;
                for (File img : imagesList) {
                	String ext = img.getExtension();
        			if(ext.toLowerCase().endsWith(".jpg") || ext.toLowerCase().endsWith(".gif")) {
        				if(indexImg == null)
        					indexImg = img;
	                    if (img.getFileName().toLowerCase().equals("index.jpg") || img.getFileName().toLowerCase().equals("index.gif")) {
	                        indexImg = img;
	                        break;
	                    }
        			}
                }
                if(indexImg != null) {
	                Hashtable<String, Object> folderProperties = new Hashtable<String, Object> ();
	                folderProperties.put("totalImages", imagesList.size());
	                folderProperties.put("indexImageInode", indexImg.getInode());
	                folderProperties.put("indexImageExtension", indexImg.getExtension());
	                folderProperties.put("galleryName", subFolder.getTitle());
	                folderProperties.put("galleryDate", indexImg.getFriendlyName());
	                folderProperties.put("galleryDescription", indexImg.getTitle());
	                folderProperties.put("galleryPath", subFolder.getPath());
	                indexImages.add(folderProperties);
                }
            }
		}
		return indexImages;
	}


	public List<File> getPictureGalleryFolderImages (String folderPath, Host host) {
	    return getPictureGalleryFolderImages (folderPath, host.getIdentifier());
	}
    @Deprecated
	public List<File> getPictureGalleryFolderImages (String folderPath, long hostId) {
    	return getPictureGalleryFolderImages (folderPath, String.valueOf(hostId));
	}
	public List<File> getPictureGalleryFolderImages (String folderPath, String hostId) {
		folderPath = (folderPath == null)?"":folderPath;
		folderPath = folderPath.trim().endsWith("/")?folderPath.trim():folderPath.trim() + "/";
		Folder folder = FolderFactory.getFolderByPath(folderPath, hostId);
		List<File> filesList = FileFactory.getFileChildrenByCondition(folder,"deleted=" + 
				DbConnectionFactory.getDBFalse());
		List<File> imagesList = new ArrayList<File> ();
		for(File file : filesList) {
			String ext = file.getExtension();
			if(ext.toLowerCase().endsWith(".jpg") || ext.toLowerCase().endsWith(".gif"))
				imagesList.add(file);
		}
		return imagesList;
	}

	public List<File> getShufflePictureGalleryFolderImages (String folderPath, Host host) {
		List<File> result = getPictureGalleryFolderImages (folderPath, host.getIdentifier());

		java.util.Collections.shuffle(result, new java.util.Random(new java.util.GregorianCalendar().getTimeInMillis()));

	    return result;
	}
    @Deprecated
	public String getFirstSubFolder(String fromPath, long hostId){
		return getFirstSubFolder(fromPath, String.valueOf(hostId));

	}
	
	public String getFirstSubFolder(String fromPath, String hostId){
		fromPath = fromPath.trim().endsWith("/")?fromPath.trim():fromPath.trim() + "/";
		Folder folder = FolderFactory.getFolderByPath(fromPath, hostId);
		List<Folder> subFolders = FolderFactory.getFoldersByParent(folder.getInode());
		Folder subFolder = (Folder) subFolders.get(0);
		return	subFolder.getPath();

	}

	@SuppressWarnings("unchecked")
	public List getPhotoGalleryFolderImages(String folderPath, Host host) {
		return getPhotoGalleryFolderImages(folderPath, host.getIdentifier());
	}
    @SuppressWarnings("unchecked")
	@Deprecated 
	public List getPhotoGalleryFolderImages(String folderPath, long hostId) {
		return getPhotoGalleryFolderImages(folderPath, String.valueOf(hostId));
	}
	@SuppressWarnings("unchecked")
	public List getPhotoGalleryFolderImages(String folderPath, String hostId) {
		folderPath = (folderPath == null) ? "" : folderPath;
		folderPath = folderPath.trim().endsWith("/") ? folderPath.trim() : folderPath.trim() + "/";
		Folder folder = FolderFactory.getFolderByPath(folderPath, hostId);
		List imagesList = FileFactory.getFileChildrenByConditionAndOrder(folder, "deleted=" + com.dotmarketing.db.DbConnectionFactory.getDBFalse()
            + " and  live= " + com.dotmarketing.db.DbConnectionFactory.getDBTrue(), "sort_order, file_name");
		return imagesList;
	}
	
	// Pictures Gallery Methods
    public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, Host host, String indexPicture) {
        return getPhotoGalleryIndexImages(indexFolderPath, host.getIdentifier(), indexPicture);
    }
	
	public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, Host host) {
        return getPhotoGalleryIndexImages(indexFolderPath, host.getIdentifier());
    }
	@Deprecated
	public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, long hostId) {
    	return getPhotoGalleryIndexImages(indexFolderPath, String.valueOf(hostId), null);
    }
	public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, String hostId) {
    	return getPhotoGalleryIndexImages(indexFolderPath, hostId, null);
    }
	
	@Deprecated
	public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, long hostId, String indexPicture) {
        return getPhotoGalleryIndexImages(indexFolderPath, String.valueOf(hostId), indexPicture);
    }
	
	public List<Map<String, Object>> getPhotoGalleryIndexImages(String indexFolderPath, String hostId, String indexPicture) {
    	
    	if(!UtilMethods.isSet(indexPicture)) {
    		indexPicture =  "index.jpg";
    	}
    	
        List<Map<String, Object>> indexImages = new ArrayList<Map<String, Object>>();
        indexFolderPath = indexFolderPath.trim().endsWith("/") ? indexFolderPath.trim() : indexFolderPath.trim() + "/";
        Folder folder = FolderFactory.getFolderByPath(indexFolderPath, hostId);
        List<Folder> subFolders = FolderFactory.getFoldersByParent(folder.getInode());
        Iterator<Folder> sFoldersIterator = subFolders.iterator();
        while (sFoldersIterator.hasNext()) {
            Folder subFolder = (Folder) sFoldersIterator.next();
            List<File> imagesList = FileFactory.getFileChildrenByCondition(subFolder, "deleted="
                    + com.dotmarketing.db.DbConnectionFactory.getDBFalse() + " and live=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue());
            if (imagesList.size() > 0) {
                File indexImg = imagesList.get(0);
                for (File img : imagesList) {
                    if (img.getFileName().toLowerCase().equals(indexPicture.toLowerCase())) {
                        indexImg = img;
                        break;
                    }
                }
                Map<String, Object> folderProperties = new HashMap<String, Object>();
                folderProperties.put("totalImages", imagesList.size());
                folderProperties.put("indexImageInode", indexImg.getInode());
                folderProperties.put("indexImageExtension", indexImg.getExtension());
                folderProperties.put("galleryName", subFolder.getTitle());
                folderProperties.put("galleryDate", indexImg.getFriendlyName());
                folderProperties.put("galleryDescription", indexImg.getTitle());
                folderProperties.put("galleryLongDescription", indexImg.getFriendlyName());
                folderProperties.put("galleryPath", subFolder.getPath());
                indexImages.add(folderProperties);
            }
        }
        return indexImages;
    }
	
}
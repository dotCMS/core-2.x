package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;

public class VideoGalleryWebApi implements ViewTool{
	Context ctx;
	private HttpServletRequest request;
	
	public void init(Object obj) {
		ViewContext context = (ViewContext) obj;
		ctx = context.getVelocityContext();
		this.request = context.getRequest();
	}
	
	public List<File> getVideoGalleryByPath (String folderPath, Host host) {
	    return getVideoGalleryByPath (folderPath, host.getIdentifier());
	}

	@Deprecated
	public List<File> getVideoGalleryByPath (String folderPath, long hostId) {
		return getVideoGalleryByPath (folderPath, String.valueOf(hostId));
	}
	
	public List<File> getVideoGalleryByPath (String folderPath, String hostId) {
		folderPath = (folderPath == null)?"":folderPath;
		folderPath = folderPath.trim().endsWith("/")?folderPath.trim():folderPath.trim() + "/";
		Folder folder = FolderFactory.getFolderByPath(folderPath, hostId);

        boolean ADMIN_MODE= (request.getSession().getAttribute(com.dotmarketing.util.WebKeys.ADMIN_MODE_SESSION) != null);
        boolean 	PREVIEW_MODE = ((request.getSession().getAttribute(com.dotmarketing.util.WebKeys.PREVIEW_MODE_SESSION) != null) && ADMIN_MODE);
        boolean EDIT_MODE = ((request.getSession().getAttribute(com.dotmarketing.util.WebKeys.EDIT_MODE_SESSION) != null) && ADMIN_MODE);
        StringBuffer cond = new StringBuffer("deleted=" + DbConnectionFactory.getDBFalse());
        if(PREVIEW_MODE || EDIT_MODE){
        	cond.append(" and working = " + DbConnectionFactory.getDBTrue());
        }
        else{
        	cond.append(" and live = " + DbConnectionFactory.getDBTrue());
        }
		List<File> filesList = FileFactory.getFileChildrenByCondition(folder,cond.toString());
						
        
        
		List<File> videoList = new ArrayList<File> ();
		for(File file : filesList) {
			
			String ext = file.getExtension();
			if(ext.toLowerCase().endsWith("flv"))
				videoList.add(file);
		}
		return videoList;
	}
	
	@Deprecated
	public List<File> getVideoImages (String videoURI, long hostId) {
		return getVideoImages (videoURI, String.valueOf(hostId));
	}
	
	public List<File> getVideoImages (String videoURI, String hostId) {
		//String videoURI = videoFile.getURI();
		String imageURI = videoURI.substring(0,videoURI.length()-4) + ".jpg";
		File img = FileFactory.getFileByURI(imageURI, hostId, true);
		List<File> videoList = new ArrayList<File> ();
		videoList.add(img);
		return videoList;
	}
}
